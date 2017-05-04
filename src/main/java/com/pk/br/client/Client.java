package com.pk.br.client;

import com.ebs.jrt.communication.*;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by PavelK on 5/1/2017.
 */
public class Client {
    public enum State { INITIALISING, READY, ASSIGNED,  SHUTDOWN, TERMINATED};
    private final AtomicReference<State> state = new AtomicReference<>(State.READY);
    private final int id;
    private final ClientManager cm;
    private Channel channel;

    private Client(){
        id = -1;

        cm = null;
    }

    public Client(ClientManager cm){
        this.cm = cm;
    }

    public boolean assign(Channel channel) {
        if (state.compareAndSet(State.READY, State.ASSIGNED)) {
            this.channel = channel;
        } else return false;
        return true;
    }

    public final State getState(){
        return state.get();
    }

    public final boolean checkState(State s){
        return (state.get()==s);
    }


    public final void close() {
        if (channel!=null) {
            try {
                channel.close();
            } catch (Exception e) {

            }
            state.set(State.READY);
        }

    }

    public final void shutDown() {
        close();

    }
}
