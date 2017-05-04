package com.pk.br.spl;

/**
 * Created by PavelK on 4/29/2017.
 */
public interface ISPLEventCollector {
    void connectionClosed(byte[] fromCompId, byte[] urlBytes, int version);
    void connectionFailed(byte[] fromCompId, byte[] urlBytes, int version, Exception e);
    void connectionEstablished(byte[] fromCompId, byte[] urlBytes, int version);
}
