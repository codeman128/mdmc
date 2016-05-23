package com.pk.publisher;

import com.lmax.disruptor.EventHandler;

import java.util.Random;

/**
 * Created by PavelK on 5/21/2016.
 */
public class Feeder implements EventHandler<Message> {
    protected final Random random = new Random(System.currentTimeMillis());
    protected final IPublisherConfig config;
    protected final int[] pubOrder;
    protected final ClientConnection[] clients;
    protected final int maxConnection;


    private Feeder(){
        config = null;
        maxConnection = -1;
        pubOrder = null;
        clients = null;
    }

    public Feeder(Publisher publisher){
        config = publisher.getConfig();
        maxConnection = config.getMaxClientConnection();

        // Initialize publication order
        pubOrder = new int[maxConnection];
        for (int i=0; i<pubOrder.length; i++) {
            pubOrder[i] = i;
        }
        shuffle();

        // Initialize client connections handler
        clients = new ClientConnection[maxConnection];
        for (int i=0; i<clients.length; i++) {
            clients[i] = new ClientConnection(this);
        }
    }

    //  Durstenfeld shuffle
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
            if (clients[i].getState()== ClientConnection.STATE.AVAILABLE) counter++;
        }
        return counter;
    }

    public ClientConnection getFirstAvailable(){
        for (int i=0; i<clients.length; i++) {
            if (clients[i].getState()== ClientConnection.STATE.AVAILABLE) {
                return clients[i];
            }
        }
        return null;
    }

    @Override
    public void onEvent(Message message, long l, boolean b) throws Exception {
        for (int i=0; i<maxConnection; i++){
            clients[pubOrder[i]].sendData(message);
        }
        //debug --------------------------------------------
        long delta = System.nanoTime() - message.eventTime;
        //System.out.println((double)delta/1000000);
        //--------------------------------------------------
        shuffle();
    }
}
