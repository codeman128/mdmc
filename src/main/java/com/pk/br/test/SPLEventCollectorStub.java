package com.pk.br.test;

import com.pk.br.spl.ISPLEventCollector;

/**
 * Created by pkapovski on 4/30/2017.
 */
public class SPLEventCollectorStub implements ISPLEventCollector {
    @Override
    public void connectionClosed(byte[] fromCompId, byte[] urlBytes, int version) {
        System.out.println("Connection Closed      from ["+new String(fromCompId)+"] to ["+new String(urlBytes)+"] version ["+version+"].");
    }

    @Override
    public void connectionFailed(byte[] fromCompId, byte[] urlBytes, int version, Exception e) {
        System.out.println("Connection Failed from ["+new String(fromCompId)+"] to ["+new String(urlBytes)+"] version ["+version+"].");
        e.printStackTrace();
        System.out.println();
    }

    @Override
    public void connectionEstablished(byte[] fromCompId, byte[] urlBytes, int version) {
        System.out.println("Connection Established from ["+new String(fromCompId)+"] to ["+new String(urlBytes)+"] version ["+version+"].");
    }
}
