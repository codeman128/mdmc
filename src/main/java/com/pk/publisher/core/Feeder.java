package com.pk.publisher.core;

import com.lmax.disruptor.EventHandler;
import com.pk.publisher.Publisher;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by PavelK on 5/21/2016.
 */
public final class Feeder implements EventHandler<Message> {
    private final byte id;
    private final Publisher publisher;
    private final Random random = new Random(System.currentTimeMillis());
    private final IPublisherConfig config;
    private final int[] pubOrder;
    private final ClientConnection[] clients;
    private final int maxConnCount;
    private final IEventCollector eventCollector;
    private final byte[] msgBuffer;
    private volatile long lastProcessedSequence = -1;

    private final FeederLogEntry fle = new FeederLogEntry(100+256*100);

    // slow consumer monitor related members
    private Message monMessage;
    final AtomicReference<ClientConnection> monConnection = new AtomicReference<>();

    private Feeder(){
        id = -1;
        publisher = null;
        config = null;
        maxConnCount = -1;
        pubOrder = null;
        clients = null;
        eventCollector = null;
        msgBuffer = null;
    }

    public Feeder(byte id, Publisher publisher){
        this.id = id;
        this.publisher = publisher;
        config = publisher.getConfig();
        maxConnCount = config.getMaxClientConnection();
        eventCollector = publisher.getEventCollector();
        msgBuffer = new byte[config.getMaxMessageSize()];

        // Initialize publication order
        pubOrder = new int[maxConnCount];
        for (int i=0; i<pubOrder.length; i++) {
            pubOrder[i] = i;
        }
        shuffle();

        // Initialize client connections handler
        clients = new ClientConnection[maxConnCount];
        for (byte i=0; i<clients.length; i++) {
            clients[i] = new ClientConnection(i, this);
        }
    }

    /**
     * The Fisher�Yates shuffle, as implemented by Durstenfeld, is an in-place shuffle.
     * (see <a href="https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle">The "inside-out" algorithm</a>)
     **/
    protected final void shuffle(){
        for (int i = pubOrder.length - 1; i > 0; i--){
            int index = random.nextInt(i + 1);
            int a = pubOrder[index];
            pubOrder[index] = pubOrder[i];
            pubOrder[i] = a;
        }
    }

    /** @return number of available session slots feeder can currently support */
    public final int countAvailable(){
        int counter = 0;
        for (int i=0; i<clients.length; i++) {
            if (clients[i].getState() == ClientConnection.STATE.AVAILABLE) {
                counter++;
            }
        }
        return counter;
    }

    /** @return first available session slots */
    public final ClientConnection getFirstAvailable(){
        for (int i=0; i<clients.length; i++) {
            if (clients[i].getState() == ClientConnection.STATE.AVAILABLE) {
                return clients[i];
            }
        }
        return null;
    }

    /**
     * Disruptor Event Handler
     * <ul>
     * <li>Set monitor time out relevant to the message type
     * <li>Publish data to the clients
     * <li>Logs performance related data for client connections that actually sent data
     * <li>Shuffle sent order for next iteration (for fairness)
     * </ul>
     **/
    @Override
    public final void onEvent(final Message msg, final long l, final boolean b) throws Exception {
        // keep local copy of the message content because header prefix going to be added by each feeder thread
        System.arraycopy(msg.buffer, msg.offset, msgBuffer, Message.HEADER_OFFSET, msg.length);

        // slow consumer monitoring: lets use proper timeout (based on message type) for current iteration
        monMessage = msg;

        // send messages in publishing order
        short msgSentCount = 0; // represents number of messages (or clients that was served) in this iteration
        for (int i=0; i< maxConnCount; i++){
            if (clients[pubOrder[i]].sendData(msg, msgBuffer)) msgSentCount++;
        }

        fle.build(msg, msgSentCount, pubOrder, clients);
        //todo persist to binary perf mon log, should replace the below

        // update monitor log
        for (int i=0; i< maxConnCount; i++){
            ClientConnection cc = clients[pubOrder[i]];
            if (cc.getSentSize()>0) {
                eventCollector.onPublishStats(msg, cc);
            }
        }

        // lets prepare to next cycle
        monMessage = null;
        shuffle();
        lastProcessedSequence = msg.dSequence;
        // todo prepare next message header

    }

    public final Publisher getPublisher(){
        return publisher;
    }

    public final byte getId() {
        return id;
    }

    public final ClientConnection getMonConnection() {
        return monConnection.get();
    }

    public final Message getMonMessage(){
        return monMessage;
    }

    public void shutdown() {
        for (byte i=0; i<clients.length; i++) {
            clients[i].shutdown();
        }
    }

    public long getLastProcessedSequence() {
        return lastProcessedSequence;
    }
}
