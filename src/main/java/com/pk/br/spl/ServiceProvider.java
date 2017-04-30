package com.pk.br.spl;

import com.pk.br.Message;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by PavelK on 4/29/2017.
 */
public class ServiceProvider {
    public enum STATE {UNAVAILABLE, AVAILABLE}
    final int id;
    private final ServiceProviderCluster cluster;
    private final AtomicInteger version = new AtomicInteger(0);
    private final AtomicReference<STATE> state = new AtomicReference();


    private ServiceProvider(){
        state.set(STATE.UNAVAILABLE);
        id = -1;
        cluster = null;
    }

    public ServiceProvider(ServiceProviderCluster cluster){
        state.set(STATE.UNAVAILABLE);
        this.cluster = cluster;
        id = cluster.register(this);
    }

    public final int getVersion() {
        return version.get() % 16;
    }

    public boolean reconnect(){
        return false;

    }

    public final boolean appHeartbeat(){
        return false;
    }

    public void sendMessage(Message msg){
        //if (spl.getVersion()==session.getSPSVersion())
    }
}
