package com.pk.publisher.core;

import com.pk.publisher.core.ClientConnection;
import com.pk.publisher.sd.ConnectionMetadata;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by PavelK on 5/23/2016.
 */
public interface IEventCollector {


    /** Listener - New connection accepted  **/
    void onConnectionAccepted(byte[] listenerName, ClientConnection connection);

    void onConnectionRejected_UnknownConsumer(byte[] listenerName, String fromAddress);

    /** Listener - Server reached maximum number of connection.
     * @param listenerName
     * @param mData**/
    void onConnectionRejected_Busy(byte[] listenerName, ConnectionMetadata mData);

    /** Number of simultaneous connection reached limit */
    void onConnectionRejected_LimitReached(byte[] listenerName, ConnectionMetadata mData);

    /** Listener - Unexpected error **/
    void onUnexpectedListenerError(byte[] listenerName, Exception e);

    /** Publisher - Bind failed, address already in use **/
    void onListenerStartFailed(byte[] name, IOException e);

    /** Listener shutdown **/
    void onListenerShutdown(byte[] name);

    /** Client Connection - Connection assign error **/
    void onConnectionAssignError(ClientConnection clientConnection, ConnectionMetadata mData, IOException e);

    /** Client Connection - Connection write error, connection will be closed **/
    void onConnectionWriteError(ClientConnection clientConnection, ConnectionMetadata mData, Exception e);

    /** Monitor - Write timeout detected **/
    void onMonitorWriteTimeout(ClientConnection clientConnection, long timeNano, long timeout, Message msg);

    /** Monitor shutdown event **/
    void onMonitorShutdown();

    /** PublisherShutdownStarted */
    void onPublisherShutdownStarted(byte[] name);

    /** Monitor - Unknown Exception */
    void onMonitorException(Exception e);

    /** Stats */
    void onPublishStats(Message message, ClientConnection cc);

}
