package com.pk.publisher.core;

import com.pk.publisher.sd.ConnectionMetadata;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by PavelK on 5/21/2016.
 */
//todo this is a session really, refactor(rename) latter
public class ClientConnection {
    public enum STATE {UNKNOWN, AVAILABLE, MARKED, INIT, ASSIGNED, TERMINATED}
    private final AtomicReference<STATE> state = new AtomicReference<>(STATE.UNKNOWN);
    private final byte id;
    private final Feeder feeder;
    private final IPublisherConfig config;
    private final Header header;
    private final IEventCollector eventEmitter;
    private final boolean shouldAddHeader;
    private Socket socket = null;
    private ConnectionMetadata mData = null;
    private OutputStream stream;
    private long msgSequenceId;
    private int heartbeatCounter;
    private volatile boolean closedByMonitor = false;

    long startTimeNano;
    long finishTimeNano;
    long finishTime;
    int sentSize;
    long totalSent;

    private ClientConnection(){
        id = -1;
        feeder = null;
        config = null;
        eventEmitter = null;
        header = null;
        shouldAddHeader = false;
    }

    public ClientConnection(byte id, Feeder feeder) {
        this.id = id;
        this.feeder = feeder;
        this.config = feeder.getPublisher().getConfig();
        this.header = new Header(config);
        this.eventEmitter = feeder.getPublisher().getEventCollector();
        this.shouldAddHeader = config.shouldAddHeader();
        state.set(STATE.AVAILABLE);
    }

    public final STATE getState() {
        return state.get();
    }

    public final ConnectionMetadata getMetadata(){
        return mData;
    }

    public final boolean assign(Socket socket, ConnectionMetadata mData){
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
        this.closedByMonitor = false;
        this.socket = socket;
        this.mData = mData;
        return true;
    }

    private boolean send(final Message msg, final byte[] msgBuffer) {
        startTimeNano = System.nanoTime();
        try {
            feeder.monConnection.lazySet(this);
            try {
                if (shouldAddHeader) {
                    sentSize = header.addHeaderAndWrite(stream, msg, msgSequenceId);
                } else {
                    sentSize = msg.length-msg.offset;
                    stream.write(msg.buffer, msg.offset, sentSize);
                }
            } finally {
                feeder.monConnection.lazySet(null);
            }
            totalSent += sentSize;
            finishTimeNano = System.nanoTime();
            finishTime = System.currentTimeMillis();
            msgSequenceId++;
            return true;
        } catch (IOException e) {
            try {
                if (!closedByMonitor) eventEmitter.onConnectionWriteError(this, mData, e);
            } finally {
                safelyCloseConnection();
                state.set(STATE.AVAILABLE);
                closedByMonitor = false;
            }
            return false;
        }
    }

    public boolean sendData(Message msg, byte[] msgBuffer) {
        sentSize = 0;
        switch (state.get()) {
            case ASSIGNED : {
                if (msg.type == Message.TYPE.UPDATE) {
                    heartbeatCounter = 0;
                    return send(msg, msgBuffer);
                } else
                if (msg.type == Message.TYPE.HEARTBEAT) {
                    heartbeatCounter++;
                    if (heartbeatCounter>=mData.getHeartbeatInterval()){
                        heartbeatCounter = 0;
                        return send(msg, msgBuffer);
                    }
                }
                break;
            }
            case INIT: {
                if (msg.type == Message.TYPE.SNAPSHOT) {
                    msgSequenceId = 1;
                    heartbeatCounter = 0;
                    totalSent = 0;
                    if (send(msg, msgBuffer)) {
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
        return false;
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

    public void closeByMonitor(){
        safelyCloseConnection();
        closedByMonitor = true;
    }

    public void shutdown(){
        safelyCloseConnection();
        state.set(STATE.TERMINATED);
    }

    public final Socket getSocket() {
        return socket;
    }

    public final byte getId(){
        return id;
    }

    public final Feeder getFeeder(){
        return feeder;
    }

    public final long getNextMsgSequenceId() {
        return msgSequenceId;
    }

    public final long getStartTimeNano() {
        return startTimeNano;
    }

    public final void setStartTimeNano(long startTimeNano) {
        this.startTimeNano = startTimeNano;
    }

    public final long getFinishTimeNano() {
        return finishTimeNano;
    }

    public final long getFinishTime() {
        return finishTime;
    }

    /**
     * @return number of bytes sent in last message
     **/
    public final int getSentSize() {
        return sentSize;
    }

    /**
     * @return number of bytes sent in current session
     **/
    public final long getTotalSent(){
        return totalSent;
    }
}
