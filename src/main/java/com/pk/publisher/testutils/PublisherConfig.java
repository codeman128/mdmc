package com.pk.publisher.testutils;

import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.pk.publisher.IPublisherConfig;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
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

    public PublisherConfig() throws Exception {
        properties = Utils.loadConfig("server");

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
    public int getDisruptorRingSize() {
        return Integer.parseInt(properties.getProperty("publisher.disruptor.ringSize", "128"));

    }

    @Override
    public int getMaxMessageSize() {
        return Integer.parseInt(properties.getProperty("publisher.message.maxSize", "10240"));
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
