package com.pk.br.test;

import com.pk.br.client.IClientManagerConfig;

/**
 * Created by PavelK on 5/1/2017.
 */
public class CMConfigStub implements IClientManagerConfig{
    @Override
    public int getNetLayerConnectTimeOut() {
        return 10000;
    }

    @Override
    public int getNetLayerReadTimeout() {
        return 5000;
    }

    @Override
    public int getNetLayerApplicationTimeOut() {
        return 10000;
    }

    @Override
    public int getNetLayerProtocolHeartbeatTimeout() {
        return 60000;
    }

    @Override
    public int getNetLayerCloseTimeout() {
        return 0;
    }

    @Override
    public int getNetLayerHeartBeatTolerance() {
        return 1000;
    }

    @Override
    public int getNetLayerQueueSize() {
        return 128;
    }

    @Override
    public int getMaxClientCount() {
        return 10;
    }

    @Override
    public String getServerHost() {
        return "tcp://localhost:3333";
    }

    @Override
    public int getServerProtocolVersionId() {
        return 2;
    }

    @Override
    public byte[] getServerCompId() {
        return "gc.server".getBytes();
    }
}
