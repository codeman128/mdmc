package com.pk.br.spl;

import com.ebs.jrt.communication.Channel;
import com.ebs.jrt.communication.ConnectData;
import com.pk.br.Message;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by PavelK on 4/29/2017.
 */
public class ServiceProvider {
    private final int id;
    private final ServiceProviderCluster cluster;
    private final AtomicInteger version = new AtomicInteger(0);
    private final String url;
    private final byte[] urlBytes;
    private final byte[] compId;
    private final ConnectData cData;
    private Channel channel;

    //public enum STATE {UNAVAILABLE, AVAILABLE}
    //private final AtomicReference<STATE> state = new AtomicReference();

    private ServiceProvider(){
        //state.set(STATE.UNAVAILABLE);
        id = -1;
        cluster = null;
        url = null;
        urlBytes = null;
        compId = null;
        cData = null;
    }

    public ServiceProvider(ServiceProviderCluster cluster, String host, int port){
        //state.set(STATE.UNAVAILABLE);
        this.cluster = cluster;
        id = cluster.register(this);
        url = "tcp://"+host+":"+port;
        urlBytes = url.getBytes();
        compId = (cluster.getCompIdPrefix()+id+"_"+cluster.getSPLayer().getCompIdSuffix()).getBytes();

        cData = new ConnectData(){

            @Override
            public byte[] getCompId() {
                return compId;
            }

            @Override
            public int getVersionId() {
                return cluster.getProtocolVersion();
            }

            @Override
            public byte[] getUsername() {
                return "username".getBytes();
            }

            @Override
            public byte[] getPassword() {
                return "password".getBytes();
            }
        };

    }

    final ISPLEventCollector getEventCollector(){
        return cluster.getSPLayer().getEventCollector();
    }

    private void closeConnection() {
        if (channel!=null) {
            try {
                try {
                    channel.close();
                } catch (Exception e) {
                    //do nothing.
                }
            } finally {
                channel = null;
                getEventCollector().connectionClosed(compId, urlBytes, getVersion());
            }
        }
    }

    public final boolean reconnect() {
        closeConnection();
        try {
            channel = cluster.getCommLayer().connect(url, cData, null);
        } catch (Exception e) {
            getEventCollector().connectionFailed(compId, urlBytes, getVersion(), e);
            return false;
        }
        version.incrementAndGet();
        getEventCollector().connectionEstablished(compId, urlBytes, getVersion());
        return true;
    }

    public final int getVersion() {
        return version.get() % 16;
    }

    public final boolean appHeartbeat(){
        return false;
    }

    public void sendMessage(Message msg){
        //if (spl.getVersion()==session.getSPSVersion())
    }
}
