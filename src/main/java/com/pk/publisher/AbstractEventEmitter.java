package com.pk.publisher;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by PavelK on 5/23/2016.
 */
public abstract class AbstractEventEmitter {


    /** Acceptor - New connection accepted  **/
    abstract public void onConnectionAccepted(ClientConnection connection);

    abstract public void onConnectionRejected_Invalid();

    /** Acceptor - Server reached maximum number of connection. **/
    abstract public void onConnectionRejected_Busy();

    /** Acceptor - Unexpected error **/
    abstract public void onUnexpectedAcceptorError(Exception e);

    /** Publisher - Bind failed, address already in use **/
    public abstract void onBindFailed(int port, IOException e);

   }
