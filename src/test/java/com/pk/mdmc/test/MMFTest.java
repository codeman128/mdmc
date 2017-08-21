package com.pk.mdmc.test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Random;

/**
 * Created by PavelK on 8/21/2017.
 */
public class MMFTest {

    static int length = 1*1024*1024;



    public static void main(String[] args) throws IOException {
        int[] address = new int[length];
        Random random = new Random(System.currentTimeMillis());

        for (int i=0; i<length; i++){

            address[i] = (int) (Math.random() * (length - 8));

        }


        MappedByteBuffer out = new RandomAccessFile("C:\\temp\\howtodoinjava.dat", "rw")
                .getChannel().map(FileChannel.MapMode.READ_WRITE, 0, length);

        //System.out.println(out.getClass().getName());
        for (int i = 0; i < length; i++)
        {
            out.put((byte) 'x');
        }
        System.out.println("Finished writing");


    for (int k=0; k<10; k++) {
        long start = System.nanoTime();
        for (int l=0; l<400; l++) {
            for (int i = 0; i < length; i++) {
                int j = address[i];
                out.putInt(j, j);
            }
        }
        long stop = System.nanoTime();

        System.out.println((stop - start)/1000000);
    }


    }
}
