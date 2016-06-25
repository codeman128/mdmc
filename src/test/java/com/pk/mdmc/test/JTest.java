package com.pk.mdmc.test;

import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * Created by PavelK on 6/20/2016.
 */
public class JTest {

    public static void main(String[] args) {
        Jedis j = new Jedis("localhost");
        Set<byte[]> keys =  j.keys("MDS.XXX.INST1.CLIENT1*".getBytes());
        for (byte[] k : keys){
            System.out.println(new String(k));
        }
        System.out.println(keys.size());
    }
}
