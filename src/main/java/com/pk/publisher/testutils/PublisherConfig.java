package com.pk.publisher.testutils;

import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.pk.publisher.core.IPublisherConfig;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by pkapovski on 5/25/2016.
 */
public class PublisherConfig implements IPublisherConfig {

    protected Properties properties;
    public byte[] snapshot;
    public byte[] update;
    public int tick;
    public int snapshotTick;

    public PublisherConfig(String configPath) throws Exception {
        properties = Utils.loadConfig("server", configPath);

        String appPath = properties.getProperty("config.path");
        snapshot = Files.readAllBytes(Paths.get(appPath + properties.get("publisher.test.snapshot")));
        System.out.println("Snapshot loaded ("+snapshot.length+ " bytes)");
        update = Files.readAllBytes(Paths.get(appPath + properties.get("publisher.test.update")));
        System.out.println("Update loaded ("+update.length+ " bytes)");
        tick = Integer.parseInt(properties.getProperty("publisher.test.tick"));
        snapshotTick =Integer.parseInt(properties.getProperty("publisher.test.snapshotIntervalInTicks"));
    }

    @Override
    public int getPort() {
        return Integer.parseInt(properties.getProperty("publisher.acceptor.port", "8080"));
    }

    @Override
    public int getFeederCount() {
        return Integer.parseInt(properties.getProperty("publisher.feeder.count", "2"));
    }

    @Override
    public int getMaxClientConnection() {
        return Integer.parseInt(properties.getProperty("publisher.feeder.connection.max", "2"));
    }

    @Override
    public int getAcceptorMaxRetry() {
        return Integer.parseInt(properties.getProperty("publisher.acceptor.maxRetry", "3"));
    }

    @Override
    public int getMonitorWriteTimeout() {
        return Integer.parseInt(properties.getProperty("publisher.monitor.WriteTimeout", "10"))*1000000;
    }

    @Override
    public int getMonitorSnapshotWriteTimeout() {
        return Integer.parseInt(properties.getProperty("publisher.monitor.SnapshotWriteTimeout", "25"))*1000000;
    }

    @Override
    public int getDisruptorRingSize() {
        return Integer.parseInt(properties.getProperty("publisher.disruptor.ringSize", "128"));

    }

    @Override
    public int getMaxMessageSize() {
        return Integer.parseInt(properties.getProperty("publisher.message.maxSize", "10240"));
    }

    @Override
    public int addMsgSeqId(byte[] buffer, int offset, long id) {
        //add call to JRT numberToString
        return offset;
    }


    @Override
    public WaitStrategy getDisruptorStrategy() {
        return new LiteBlockingWaitStrategy();
        //new BusySpinWaitStrategy();
    }

    @Override
    public InetAddress getAddress() throws UnknownHostException {
        return InetAddress.getByName(properties.getProperty("publisher.acceptor.address"));
    }
}
