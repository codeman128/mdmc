package com.pk.publisher;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by PavelK on 5/21/2016.
 */
public class ClientConnection {
    enum STATE {UNKNOWN, MARKED, AVAILABLE, INIT, ASSIGNED}
    protected final AtomicReference<STATE> state = new AtomicReference<>(STATE.UNKNOWN);
    protected final Feeder feeder;
    protected Socket socket;
    protected OutputStream stream;

    private ClientConnection(){
        feeder = null;
    }

    public ClientConnection(Feeder feeder) {
        this.feeder = feeder;
        state.set(STATE.AVAILABLE);
    }

    public STATE getState() {
        return state.get();
    }

    public boolean assign(Socket socket){
        if (!state.compareAndSet(STATE.AVAILABLE, STATE.MARKED)) return false;
        this.socket = socket;
        try {
            stream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace(); //todo;
            safelyCloseConnection();
            state.compareAndSet(STATE.MARKED, STATE.AVAILABLE);
            return false;
        }
        if (!state.compareAndSet(STATE.MARKED, STATE.INIT)) return false;
        System.out.println("Connection assigned: "+this+" "+feeder);
        return true;
    }

    public boolean sendData(Message msg) {
        try {
            switch (state.get()) {
                case ASSIGNED : {
                    if (msg.type == Message.TYPE.UPDATE || msg.type == Message.TYPE.HEARTBEAT) {
                        stream.write(msg.getData(), 0, msg.length);
                    }
                    break;
                }
                case INIT: {
                    if (msg.type == Message.TYPE.SNAPSHOT) {
                        stream.write(msg.getData(), 0, msg.length);
                        state.compareAndSet(STATE.INIT, STATE.ASSIGNED);
                    }
                    break;
                }
                case AVAILABLE: {
                    //todo consider put some "delay" for fairness...
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            safelyCloseConnection();
            return false;
        }
        return true;
    }

    protected void safelyCloseConnection(){
        try { socket.close();} catch (IOException e) {}
        state.set(STATE.AVAILABLE);
    }

}
