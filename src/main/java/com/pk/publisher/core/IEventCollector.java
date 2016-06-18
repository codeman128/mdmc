package com.pk.publisher.core;

import com.pk.publisher.core.ClientConnection;
import com.pk.publisher.sd.ConnectionMetadata;

import java.io.IOException;

/**
 * Created by PavelK on 5/23/2016.
 */
public interface IEventCollector {


    /** Acceptor - New connection accepted  **/
    void onConnectionAccepted(ClientConnection connection);

    void onConnectionRejected_UnknownConsumer(String address);

    /** Acceptor - Server reached maximum number of connection.
     * @param mData**/
    void onConnectionRejected_Busy(ConnectionMetadata mData);

    /** Number of simultaneous connection reached limit */
    void onConnectionRejected_LimitReached(ConnectionMetadata mData);

    /** Acceptor - Unexpected error **/
    void onUnexpectedAcceptorError(Exception e);

    /** Publisher - Bind failed, address already in use **/
    void onBindFailed(int port, IOException e);

    /** Client Connection - Connection assign error **/
    void onConnectionAssignError(ClientConnection clientConnection, ConnectionMetadata mData, IOException e);

    /** Client Connection - Connection write error, connection will be closed **/
    void onConnectionWriteError(ClientConnection clientConnection, ConnectionMetadata mData, Exception e);

    /** Monitor - Write timeout detected **/
    void onMonitorWriteTimeout(ClientConnection clientConnection, long timeNano);

    /** Monitor shutdown event **/
    void onMonitorShutdown();

    /** Monitor - Unknown Exception */
    void onMonitorException(Exception e);

}
