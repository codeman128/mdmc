package com.pk.br.test;

import com.pk.br.spl.ServiceProviderLayer;
import com.pk.br.spl.ServiceProvider;
import com.pk.br.spl.ServiceProviderCluster;

/**
 * Created by pkapovski on 4/30/2017.
 */
public class Service2 {
    private ServiceProviderLayer sLayer;


    public void init(){
        sLayer = new ServiceProviderLayer(new SPLConfig(), new SPLEventCollectorStub());

        ServiceProviderCluster cluster0 = new ServiceProviderCluster(sLayer);
        ServiceProvider sp00 = new ServiceProvider(cluster0);
        ServiceProvider sp01 = new ServiceProvider(cluster0);

        ServiceProviderCluster cluster1 = new ServiceProviderCluster(sLayer);
        ServiceProvider sp10 = new ServiceProvider(cluster1);
        ServiceProvider sp11 = new ServiceProvider(cluster1);


    }

    public static void main(String[] args) {
        Service2 service = new Service2();
        service.init();


    }
}
