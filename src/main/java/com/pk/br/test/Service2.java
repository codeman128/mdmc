package com.pk.br.test;

import com.pk.br.client.ClientManager;
import com.pk.br.spl.ServiceProviderLayer;
import com.pk.br.spl.ServiceProvider;
import com.pk.br.spl.ServiceProviderCluster;

/**
 * Created by pkapovski on 4/30/2017.
 */
public class Service2 {
    private ServiceProviderLayer sLayer;


    public void init(){
        sLayer = new ServiceProviderLayer(new SPLConfig(), new SPLEventCollectorStub(), "tes123");

        ServiceProviderCluster cluster0 = new ServiceProviderCluster(sLayer, "gc.gw", 2);
        ServiceProvider sp00 = new ServiceProvider(cluster0, "localhost", 3333);
        ServiceProvider sp01 = new ServiceProvider(cluster0, "localhost", 3333);

        ServiceProviderCluster cluster1 = new ServiceProviderCluster(sLayer, "gc.mrfq", 5);
        ServiceProvider sp10 = new ServiceProvider(cluster1, "localhost", 3333);
        ServiceProvider sp11 = new ServiceProvider(cluster1, "localhost", 3333);

        ClientManager cm = new ClientManager(new CMConfigStub(), new CMEventCollectorStub(), null);
        cm.start();


        sp11.reconnect();
        sp11.reconnect();


    }

    public static void main(String[] args) {
        Service2 service = new Service2();
        service.init();


    }
}
