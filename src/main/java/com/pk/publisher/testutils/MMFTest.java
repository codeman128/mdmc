package com.pk.publisher.testutils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by PavelK on 10/22/2016.
 */
public class MMFTest {

    public static void main(String[] args) throws IOException {
        long startT1 = System.currentTimeMillis();
        File f = new File("c://temp//temp.txt");
        f.delete();

        FileChannel fc = new RandomAccessFile(f, "rw").getChannel();

        long bufferSize=10*1024*1024;
        MappedByteBuffer mem =fc.map(FileChannel.MapMode.READ_WRITE, 0, bufferSize);

        int counter=0;
        long startT2 = System.currentTimeMillis();
        for(int i=0; i<1000000;i++){
            mem.putLong(counter++);

//            if(!mem.hasRemaining())
//            {
//                start+=mem.position();
//                mem =fc.map(FileChannel.MapMode.READ_WRITE, start, bufferSize);
//            }
//            mem.putLong(counter);
//            counter++;
//            if(counter > noOfMessage )
//                break;
        }
        long endT = System.currentTimeMillis();
        long tot = endT - startT2;
        System.out.println(String.format("No Of Message %s , Time(ms) %s Position %s\nTotal: %s",counter, tot, mem.position(), (endT-startT1))) ;

    }

}
