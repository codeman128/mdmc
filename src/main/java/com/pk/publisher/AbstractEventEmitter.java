package com.pk.publisher;

import java.io.IOException;

/**
 * Created by PavelK on 5/23/2016.
 */
public abstract class AbstractEventEmitter {


    /** New connection accepted  **/
    abstract public void onConnectionAccepted(ClientConnection connection);

    abstract public void onConnectionRejected_Invalid();

    /** Server reached maximum number of connection. **/
    abstract public void onConnectionRejected_Busy();

    /** Bind failed, address already in use **/
    public abstract void onBindFailed(int port, IOException e);
}
