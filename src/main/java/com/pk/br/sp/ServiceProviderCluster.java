package com.pk.br.sp;


/**
 * Created by PavelK on 4/29/2017.
 */
public class ServiceProviderCluster {
    private int spCounter = 0;
    private final ServiceProvider[] sps = new ServiceProvider[255];

    final int register(ServiceProvider sp){
        sps[spCounter] = sp;
        return  spCounter++;
    }

    final ServiceProvider getServiceProvider(int id) {
        //todo check range
        return sps[id];
    }




}