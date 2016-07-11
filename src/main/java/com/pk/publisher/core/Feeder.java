package com.pk.publisher.core;

import com.lmax.disruptor.EventHandler;
import com.pk.publisher.Publisher;

import java.util.Random;

/**
 * Created by PavelK on 5/21/2016.
 */
public class Feeder implements EventHandler<Message> {
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

    ClientConnection monConnection;
    Message.TYPE monMessageType;

    private long statSigma;
    private long statCounter;
    private long statMin;
    private long statMax;


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
    }

    public Feeder(byte id, Publisher publisher){
        this.id = id;
        this.publisher = publisher;
        config = publisher.getConfig();
        maxConnCount = config.getMaxClientConnection();
        writeTimeout = config.getMonitorWriteTimeout();
        snapshotWriteTimeout = config.getMonitorSnapshotWriteTimeout();
        eventCollector = publisher.getEventCollector();

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

    //  Durstenfeld shuffle algo
    protected void shuffle(){
        for (int i = pubOrder.length - 1; i > 0; i--){
            int index = random.nextInt(i + 1);
            int a = pubOrder[index];
            pubOrder[index] = pubOrder[i];
            pubOrder[i] = a;
        }
    }

    public int countAvailable(){
        int counter = 0;
        for (int i=0; i<clients.length; i++) {
            if (clients[i].getState() == ClientConnection.STATE.AVAILABLE) {
                counter++;
            }
        }
        return counter;
    }

    public ClientConnection getFirstAvailable(){
        for (int i=0; i<clients.length; i++) {
            if (clients[i].getState() == ClientConnection.STATE.AVAILABLE) {
                return clients[i];
            }
        }
        return null;
    }

    @Override
    public void onEvent(Message message, long l, boolean b) throws Exception {
        for (int i=0; i< maxConnCount; i++){
            monConnection = clients[pubOrder[i]]; //todo to move into send
            monConnection.sendData(message);
        }
        monConnection = null;

        for (int i=0; i< maxConnCount; i++){
            ClientConnection cc = clients[pubOrder[i]];
            if (cc.getSentSize()>0) {
                eventCollector.onPublishStats(message, cc);
            }
        }

        //debug --------------------------------------------
        /*
        long delta = System.nanoTime() - message.eventTime;
        statMin = Math.min(statMin, delta);
        statMax = Math.max(statMax, delta);
        statSigma += delta;
        statCounter++;
        if (statCounter>30) {
            System.out.println("Ave: ["+(double)statSigma/(statCounter*1000000)+"] Min: ["+(double)statMin/1000000+"] Max: ["+(double)statMax/1000000+"]");
            statMax = 0;
            statMin = 0;
            statCounter = 0;
            statSigma = 0;
        }
           */
        //--------------------------------------------------
        shuffle();
    }

    public Publisher getPublisher(){
        return publisher;
    }

    public final byte getId() {
        return id;
    }

    public ClientConnection getMonConnection() {
        return monConnection;
    }

    public final Message.TYPE getMonMessageType(){
        return monMessageType;
    }

    public final long getMonitorWriteTimeout(){
        try {
            //System.out.println(monMessageType.name()+ " "+snapshotWriteTimeout);
            if (monMessageType != null && monMessageType == Message.TYPE.SNAPSHOT) {
                return snapshotWriteTimeout;
            } else return writeTimeout;
        } catch (Exception e) {
        }
        return writeTimeout;
    }
}
