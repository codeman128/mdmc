package com.pk.br.test;

import com.ebs.jrt.communication.BindFailedError;
import com.ebs.jrt.communication.InvalidAddressError;
import com.ebs.jrt.logging.Logger;
import com.pk.br.client.Client;
import com.pk.br.client.IClientEventCollector;

/**
 * Created by PavelK on 5/1/2017.
 */
public class CMEventCollectorStub implements IClientEventCollector{

    @Override
    public void onBindFailedUnknown(Logger logger, byte[] url, BindFailedError e) {
        System.out.println("Bind Failed Unknown ["+new String(url)+"]");
        e.printStackTrace();
    }

    @Override
    public void onBindFailedInvalidAddress(Logger logger, byte[] url, InvalidAddressError e) {
        System.out.println("Bind Failed - Invalid Address ["+new String(url)+"]");
        e.printStackTrace();
    }

    @Override
    public void onBindSuccessful(Logger logger, byte[] url) {
        System.out.println("Bind Successful ["+new String(url)+"]");
    }

    @Override
    public void onClientAssignSuccessful(Logger logger, Client client) {
        System.out.println("Client Assigned ["+client+"]");
    }
}
