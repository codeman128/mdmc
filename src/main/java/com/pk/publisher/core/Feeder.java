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
    private final long writeTimeout;
    private final long snapshotWriteTimeout;
    private final IEventCollector eventCollector;
    private final byte[] msgBuffer;

    // monitor related values
    private long monTimeout;
    private Message monMessage;
    final AtomicReference<ClientConnection> monConnection = new AtomicReference<>();

    private Feeder(){
        id = -1;
        publisher = null;
        config = null;
        maxConnCount = -1;
        pubOrder = null;
        clients = null;
        writeTimeout = 0;
        snapshotWriteTimeout = 0;
        eventCollector = null;
        msgBuffer = null;
    }

    public Feeder(byte id, Publisher publisher){
        this.id = id;
        this.publisher = publisher;
        config = publisher.getConfig();
        maxConnCount = config.getMaxClientConnection();
        writeTimeout = config.getMonitorWriteTimeout();
        snapshotWriteTimeout = config.getMonitorSnapshotWriteTimeout();
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
    public final void onEvent(Message msg, long l, boolean b) throws Exception {
        // keep local copy of the message content because prefix going to be added by each feeder
        System.arraycopy(msg.buffer, msg.offset, msgBuffer, Message.HEADER_OFFSET, msg.length);

        monMessage = msg;
        if (msg.type== Message.TYPE.SNAPSHOT) {
            monTimeout = snapshotWriteTimeout;
        } else {
            monTimeout = writeTimeout;
        }

        for (int i=0; i< maxConnCount; i++){
            clients[pubOrder[i]].sendData(msg, msgBuffer);
        }

        // todo prepare next message header

        for (int i=0; i< maxConnCount; i++){
            ClientConnection cc = clients[pubOrder[i]];
            if (cc.getSentSize()>0) {
                eventCollector.onPublishStats(msg, cc);
            }
        }

        shuffle();
        monMessage = null;
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

    public final long getMonitorWriteTimeout(){
        return monTimeout;
    }

    public void shutdown() {
        for (byte i=0; i<clients.length; i++) {
            clients[i].shutdown();
        }
    }
}
