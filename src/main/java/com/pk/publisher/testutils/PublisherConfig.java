package com.pk.publisher.testutils;

import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.pk.publisher.IPublisherConfig;

import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by pkapovski on 5/25/2016.
 */
public class PublisherConfig implements IPublisherConfig {
    protected Properties properties;

    public PublisherConfig() throws Exception {
        String path = null;
        try {
            path = System.getProperty("user.dir").replace("\\", "/");
            path = path + "/server.cnfg";
            System.out.println("Load configuration: " + path);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
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
}
