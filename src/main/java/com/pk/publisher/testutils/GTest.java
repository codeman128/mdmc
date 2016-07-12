package com.pk.publisher.testutils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by pkapovski on 7/12/2016.
 */
public class GTest {

    public static void main(String[] args) {
        AtomicLong al = new AtomicLong(0);

        for (int i=1; i==20000; i++) {
            al.incrementAndGet();
        }


        long start = System.nanoTime();
        for (int i=1; i==10000000 && al.incrementAndGet()<200000000; i++) {
            al.incrementAndGet();
        }
        long finish = System.nanoTime();
        System.out.println(finish-start);
    }
}
