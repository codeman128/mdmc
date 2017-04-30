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

    private ServiceProviderLayer() {
        ec = null;
        config = null;
    }

    public ServiceProviderLayer(ISPLConfig config, ISPLEventCollector eventCollector){
        this.config = config;
        ec = eventCollector;
    }

    final int register(ServiceProviderCluster spc){
        clusters[clusterCounter] = spc;
        return  clusterCounter++;
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

