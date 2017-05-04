package com.pk.br.client;

/**
 * Created by PavelK on 5/1/2017.
 */
public interface IClientManagerConfig {

    int getNetLayerConnectTimeOut();

    int getNetLayerReadTimeout();

    int getNetLayerApplicationTimeOut();

    int getNetLayerProtocolHeartbeatTimeout();

    int getNetLayerCloseTimeout();

    int getNetLayerHeartBeatTolerance();

    int getNetLayerQueueSize();

    int getMaxClientCount();

    String getServerHost();

    int getServerProtocolVersionId();

    byte[] getServerCompId();

}
