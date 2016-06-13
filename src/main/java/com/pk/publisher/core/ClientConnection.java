package com.pk.publisher.core;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by PavelK on 5/21/2016.
 */
public class ClientConnection {
    public enum STATE {UNKNOWN, AVAILABLE, MARKED, INIT, ASSIGNED}
    private final AtomicReference<STATE> state = new AtomicReference<>(STATE.UNKNOWN);
    private final byte id;
    private final Feeder feeder;
    private  final IPublisherConfig config;
    private final byte[] header;
    private final int headerSize;
    private final IEventCollector eventEmitter;
    private Socket socket;
    private OutputStream stream;
    private long msgSequenceId;

    private ClientConnection(){
        id = -1;
        feeder = null;
        config = null;
        eventEmitter = null;
        header = null;
        headerSize = 0;
    }

    public ClientConnection(byte id, Feeder feeder) {
        this.id = id;
        this.feeder = feeder;
        this.config = feeder.getPublisher().getConfig();
        this.headerSize = config.getMsgHeader().length;
        // Init local copy of the header and reserve some space for message id.
        this.header = new byte[headerSize+20];
        System.arraycopy(config.getMsgHeader(), 0, header, 0, headerSize);

        this.eventEmitter = feeder.getPublisher().getEventCollector();
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

    private boolean send(Message msg) {
        feeder.monConnection = this;
        feeder.monTime = System.nanoTime();
        try {
            int newLength = config.addMsgSeqId(header, headerSize, msgSequenceId);
            stream.write(header, 0, newLength);
            stream.write(msg.getBuffer(), 0, msg.length);
            msgSequenceId++;
            return true;
        } catch (IOException e) {
            eventEmitter.onConnectionWriteError(this, e);
            state.set(STATE.AVAILABLE);
            safelyCloseConnection();
            return false;
        } finally {
            feeder.monConnection = null;
            feeder.monTime = 0;
        }
    }

    public boolean sendData(Message msg) {

        switch (state.get()) {
            case ASSIGNED : {
                if (msg.type == Message.TYPE.UPDATE || msg.type == Message.TYPE.HEARTBEAT) {
                    return send(msg);
                }
                break;
            }
            case INIT: {
                if (msg.type == Message.TYPE.SNAPSHOT) {
                    msgSequenceId = 1;
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

    public void safelyCloseConnection(){
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
