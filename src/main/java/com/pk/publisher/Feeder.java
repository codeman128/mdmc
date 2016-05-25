package com.pk.publisher;

import com.lmax.disruptor.EventHandler;

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


    private Feeder(){
        id = -1;
        publisher = null;
        config = null;
        maxConnCount = -1;
        pubOrder = null;
        clients = null;
    }

    public Feeder(byte id, Publisher publisher){
        this.id = id;
        this.publisher = publisher;
        config = publisher.getConfig();
        maxConnCount = config.getMaxClientConnection();

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
            clients[pubOrder[i]].sendData(message);
        }
        //debug --------------------------------------------
        long delta = System.nanoTime() - message.eventTime;
        //System.out.println((double)delta/1000000);
        //--------------------------------------------------
        shuffle();
    }

    public Publisher getPublisher(){
        return publisher;
    }

    public byte getId() {
        return id;
    }
}
