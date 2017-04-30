package com.pk.br.spl;


/**
 * Created by PavelK on 4/29/2017.
 */
public class ServiceProviderCluster {
    private final ServiceProviderLayer spl;
    private final SPLClusterHandler handler;

    private int spCounter = 0;
    private final ServiceProvider[] sps = new ServiceProvider[255];

    public ServiceProviderCluster(ServiceProviderLayer sLayer) {
        spl = sLayer;
        handler = new SPLClusterHandler(this);
    }

    final int register(ServiceProvider sp){
        sps[spCounter] = sp;
        return  spCounter++;
    }

    final ServiceProvider getServiceProvider(int id) {
        //todo check range
        return sps[id];
    }




}
