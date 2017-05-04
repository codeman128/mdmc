package com.pk.br.spl;

import com.ebs.jrt.communication.implementation.socket.SocketCommLayer;

/**
 * Created by PavelK on 4/29/2017.
 */
public class ServiceProviderCluster {
    private final int id;
    private final ServiceProviderLayer spl;
    private final String compIdPrefix;
    private final SPLClusterHandler handler;
    private final SocketCommLayer layer;

    private int spCounter = 0;
    private final ServiceProvider[] sps = new ServiceProvider[255];
    private final int protocolVersion;

    public ServiceProviderCluster(ServiceProviderLayer sLayer, String compIdPrefix, int protocolVersion) {
        spl = sLayer;
        id = spl.register(this);
        this.compIdPrefix = compIdPrefix+"_"+id+"_";
        this.protocolVersion = protocolVersion;
        handler = new SPLClusterHandler(this);
        layer = new SocketCommLayer();
        layer.init(100000, 100000, 100000, 100000, 100000, 50000, 1024, 4096);    //todo take from config
    }

    public final String getCompIdPrefix(){
        return compIdPrefix;
    }

    public final int getProtocolVersion(){
        return protocolVersion;
    }

    final int register(ServiceProvider sp){
        sps[spCounter] = sp;
        return  spCounter++;
    }

    final ServiceProviderLayer getSPLayer(){
        return spl;
    }

    final SocketCommLayer getCommLayer(){
        return layer;
    }

    final ServiceProvider getServiceProvider(int id) {
        //todo check range
        return sps[id];
    }

}
