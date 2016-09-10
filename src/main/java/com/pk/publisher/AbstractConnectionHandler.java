package com.pk.publisher;

import com.pk.publisher.core.ClientConnection;
import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.sd.ConnectionLookup;
import com.pk.publisher.sd.ConnectionMetadata;
import com.pk.publisher.sd.ConsumerManager;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by pkapovski on 9/8/2016.
 */
public abstract class AbstractConnectionHandler {
    private final IEventCollector eventCollector;
    private final ConsumerManager consumerManager;
    private final ConnectionLookup lookup = new ConnectionLookup();
    private final int maxRetry;

    private AbstractConnectionHandler(){
        consumerManager = null;
        eventCollector = null;
        maxRetry = 0;
    }

    public AbstractConnectionHandler(ConsumerManager consumerManager, IEventCollector eventCollector, int maxRetry){
        this.consumerManager = consumerManager;
        this.eventCollector = eventCollector;
        this.maxRetry = maxRetry;
    }

    public synchronized ConnectionMetadata handleConnection(Listener listener, Socket clientSocket){
        ConnectionMetadata mData = validateNewConnection(listener, clientSocket);
        if (mData == null) {
            closeQuietly(clientSocket);
        } else {
            Publisher publisher = getPublisher(mData);
            //todo check if publisher null +add event

            // let's try to find free slot
            int retry = 1;
            ClientConnection connection;
            while (retry < maxRetry) {
                retry++;
                connection = publisher.getAvailableConnection();
                if (connection != null && connection.assign(clientSocket, mData)) {
                    eventCollector.onConnectionAccepted(listener.getName(), connection);
                    break;
                }
            }

            // handle no available spot found
            if (retry >= maxRetry) {
                eventCollector.onConnectionRejected_Busy(listener.getName(), mData);
                closeQuietly(clientSocket);
            }
        }
        return mData;
    }

    protected abstract Publisher getPublisher(ConnectionMetadata mData);

    protected ConnectionMetadata validateNewConnection(Listener listener,  Socket socket) {
        lookup.setIpBytes(socket.getInetAddress().getAddress());
        ConnectionMetadata mData = consumerManager.getConnection(lookup);

        if (mData==null) {
            eventCollector.onConnectionRejected_UnknownConsumer(listener.getName(), socket.getInetAddress().toString());
            return null;
        }

        if (mData.getConsumer().getConnCount()==mData.getConsumer().getSimConnLimit()) {
            eventCollector.onConnectionRejected_LimitReached(listener.getName(), mData);
            return null;
        }
        return mData;
    }

    private void closeQuietly(Socket socket){
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

}
