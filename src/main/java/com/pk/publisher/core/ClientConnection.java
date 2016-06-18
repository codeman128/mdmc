package com.pk.publisher.core;

import com.pk.publisher.sd.ConnectionMetadata;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by PavelK on 5/21/2016.
 */
//todo this is a session really, refactor latter
public class ClientConnection {
    public enum STATE {UNKNOWN, AVAILABLE, MARKED, INIT, ASSIGNED}
    private final AtomicReference<STATE> state = new AtomicReference<>(STATE.UNKNOWN);
    private final byte id;
    private final Feeder feeder;
    private  final IPublisherConfig config;
    private final Header header;
    private final IEventCollector eventEmitter;
    private Socket socket = null;
    private ConnectionMetadata mData = null;
    private OutputStream stream;
    private long msgSequenceId;
    private int heartbeatCounter;

    private ClientConnection(){
        id = -1;
        feeder = null;
        config = null;
        eventEmitter = null;
        header = null;
    }

    public ClientConnection(byte id, Feeder feeder) {
        this.id = id;
        this.feeder = feeder;
        this.config = feeder.getPublisher().getConfig();
        this.header = new Header(config);
        this.eventEmitter = feeder.getPublisher().getEventCollector();
        state.set(STATE.AVAILABLE);
    }

    public STATE getState() {
        return state.get();
    }

    public ConnectionMetadata getMetadata(){
        return mData;
    }

    public boolean assign(Socket socket, ConnectionMetadata mData){
        if (!state.compareAndSet(STATE.AVAILABLE, STATE.MARKED)) return false;
        if (!setDate(socket, mData)) return false;
        try {
            if (!mData.registerSession(this)) throw new IOException("Register session unexpected error");
            stream = socket.getOutputStream();
        } catch (IOException e) {
            eventEmitter.onConnectionAssignError(this, mData, e);
            safelyCloseConnection();
            state.compareAndSet(STATE.MARKED, STATE.AVAILABLE);
            return false;
        }
        return state.compareAndSet(STATE.MARKED, STATE.INIT);
    }

    private boolean setDate(Socket socket, ConnectionMetadata mData) {
        this.socket = socket;
        this.mData = mData;
        return true;
    }

    private boolean send(Message msg) {
        feeder.monConnection = this;
        feeder.monTime = System.nanoTime();
        try {
            header.addHeaderAndWrite(stream, msg, msgSequenceId);
            msgSequenceId++;
            return true;
        } catch (IOException e) {
            eventEmitter.onConnectionWriteError(this, mData, e);
            safelyCloseConnection();
            state.set(STATE.AVAILABLE);
            return false;
        } finally {
            feeder.monConnection = null;
            feeder.monTime = 0;
        }
    }

    public boolean sendData(Message msg) {

        switch (state.get()) {
            case ASSIGNED : {
                if (msg.type == Message.TYPE.UPDATE) {
                    heartbeatCounter = 0;
                    return send(msg);
                } else
                if (msg.type == Message.TYPE.HEARTBEAT) {
                    heartbeatCounter++;
                    if (heartbeatCounter>=mData.getHeartbeatInterval()){
                        heartbeatCounter = 0;
                        return send(msg);
                    }
                }
                break;
            }
            case INIT: {
                if (msg.type == Message.TYPE.SNAPSHOT) {
                    msgSequenceId = 1;
                    heartbeatCounter = 0;
                    if (send(msg)) {
                        state.compareAndSet(STATE.INIT, STATE.ASSIGNED);
                        return true;
                    } else return false;
                }
                break;
            }
            case AVAILABLE: {
                //todo consider put some "delay" for fairness...
                break;
            }
        }
        return true;
    }

    public synchronized void safelyCloseConnection(){
        try {
            try {
                if (socket != null) socket.close();
            } catch (IOException e) {
                // close quietly
            } finally {
                socket = null;
                if (mData != null) mData.unregisterSession(this);
            }
        } finally {
            mData = null;
            state.set(STATE.AVAILABLE); //todo add state RELEASING
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
