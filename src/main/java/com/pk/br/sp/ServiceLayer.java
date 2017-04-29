package com.pk.br.sp;


import com.pk.br.session.SessionID;

/**
 * Created by PavelK on 4/29/2017.
 */
public class ServiceLayer {
    private int clusterCounter = 0;
    private final ServiceProviderCluster[] clusters = new ServiceProviderCluster[16];
    private final IServiceLayerEventCollector ec;

    private ServiceLayer() {
        ec = null;
    }

    public ServiceLayer(IServiceLayerEventCollector eventCollector){
        ec = eventCollector;
    }

    final int register(ServiceProviderCluster spc){
        clusters[clusterCounter] = spc;
        return  clusterCounter++;
    }

    /** Returns Service Provider for session */
    public final ServiceProvider getServiceProvider(SessionID session) {        //todo add error handling
        ServiceProviderCluster cluster = clusters[session.getSPCluster()];
        return cluster.getServiceProvider(session.getSPId());
    }

}

