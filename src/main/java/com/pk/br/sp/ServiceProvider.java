package com.pk.br.sp;

import com.pk.br.Message;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by PavelK on 4/29/2017.
 */
public class ServiceProvider {
    final int id;
    private final ServiceProviderCluster cluster;
    private final AtomicInteger version = new AtomicInteger(0);
    private final AtomicInteger sessionCount = new AtomicInteger(0);


    private ServiceProvider(){
        id = -1;
        cluster = null;
    }

    public ServiceProvider(ServiceProviderCluster cluster){
        this.cluster = cluster;
        id = cluster.register(this);
    }

    public final int getVersion() {
        return version.get() % 16;
    }

    public void sendMessage(Message msg){
        //if (sp.getVersion()==session.getSPSVersion())
    }
}
