package com.pk.publisher;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by PavelK on 5/21/2016.
 */
public class ClientConnection {
    enum STATE {UNKNOWN, AVAILABLE, MARKED, INIT, ASSIGNED}
    private final AtomicReference<STATE> state = new AtomicReference<>(STATE.UNKNOWN);
    private final byte id;
    private final Feeder feeder;
    private final AbstractEventEmitter eventEmitter;
    private Socket socket;
    private OutputStream stream;

    private ClientConnection(){
        id = -1;
        feeder = null;
        eventEmitter = null;
    }

    public ClientConnection(byte id, Feeder feeder) {
        this.id = id;
        this.feeder = feeder;
        this.eventEmitter = feeder.getPublisher().getEventEmitter();
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
            eventEmitter.onConnectionAssignError(this, e);
            safelyCloseConnection();
            state.compareAndSet(STATE.MARKED, STATE.AVAILABLE);
            return false;
        }
        return state.compareAndSet(STATE.MARKED, STATE.INIT);
    }

    public boolean sendData(Message msg) {
        try {
            switch (state.get()) {
                case ASSIGNED : {
                    if (msg.type == Message.TYPE.UPDATE || msg.type == Message.TYPE.HEARTBEAT) {
                        stream.write(msg.getBuffer(), 0, msg.length);
                    }
                    break;
                }
                case INIT: {
                    if (msg.type == Message.TYPE.SNAPSHOT) {
                        stream.write(msg.getBuffer(), 0, msg.length);
                        state.compareAndSet(STATE.INIT, STATE.ASSIGNED);
                    }
                    break;
                }
                case AVAILABLE: {
                    //todo consider put some "delay" for fairness...
                    break;
                }
            }

        } catch (Exception e) {
            eventEmitter.onConnectionWriteError(this, e);
            state.set(STATE.AVAILABLE);
            safelyCloseConnection();
            return false;
        }
        return true;
    }

    protected void safelyCloseConnection(){
        try {
            socket.close();
        } catch (IOException e) {
            // close quietly
        } finally {
            state.set(STATE.AVAILABLE);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public byte getId(){
        return id;
    }

    public Feeder getFeeder(){
        return feeder;
    }

}
