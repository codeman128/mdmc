package com.pk.publisher;

import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by pkapovski on 5/25/2016.
 */
public class PublisherConfig implements IPublisherConfig{
    protected Properties properties;

    public PublisherConfig(String path) throws Exception {
        FileInputStream file = new FileInputStream(path);
        properties = new Properties();
        properties.load(file);
        file.close();

        System.out.println("-------------------------------------------------------------------------");
        Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            String value = (String)properties.get(key);
            System.out.println(key + ": " + value);
        }
        System.out.println("-------------------------------------------------------------------------");
    }

    @Override
    public int getPort() {
        return Integer.parseInt(properties.getProperty("publisher.acceptor.port", "8080"));
    }

    @Override
    public int getFeederCount() {
        return 2;
    }

    @Override
    public int getMaxClientConnection() {
        return 2;
    }

    @Override
    public int getAcceptorMaxRetry() {
        return 3;
    }

    @Override
    public int getDisruptorRingSize() {
        return 128;
    }

    @Override
    public int getMaxMessageSize() {
        return 1024;
    }

    @Override
    public WaitStrategy getDisruptorStrategy() {
        return new LiteBlockingWaitStrategy();
        //new BusySpinWaitStrategy();
    }
}
