package com.pk.br.spl;


import com.pk.br.session.Session;
import com.pk.br.session.SessionID;

/**
 * Created by PavelK on 4/29/2017.
 */
public class ServiceProviderLayer {
    private int clusterCounter = 0;
    private final ISPLConfig config;
    private final ServiceProviderCluster[] clusters = new ServiceProviderCluster[16];
    private final ISPLEventCollector ec;
    private final String compIdSuffix;

    private ServiceProviderLayer() {
        ec = null;
        config = null;
        compIdSuffix = null;
    }

    public ServiceProviderLayer(ISPLConfig config, ISPLEventCollector eventCollector, String compIdSuffix){
        this.config = config;
        this.ec = eventCollector;
        this.compIdSuffix = compIdSuffix;
    }

    final int register(ServiceProviderCluster spc){
        clusters[clusterCounter] = spc;
        return  clusterCounter++;
    }

    final String getCompIdSuffix() {
        return compIdSuffix;
    }

    final ISPLEventCollector getEventCollector(){
        return ec;
    }

    /** Returns Service Provider for session */
    public final ServiceProvider getServiceProvider(SessionID sid) {
        ServiceProviderCluster cluster = clusters[sid.getSPCluster()];
        return cluster.getServiceProvider(sid.getSPId());
    }

    /** Will implement routing logic */
    public final ServiceProvider getServiceProvider(Session session) {
        return null;
    }


}

