package com.pk.mdmc;

import java.util.Random;

/**
 * Created by PavelK on 5/18/2016.
 */
public class ShuffleTest {
    static Random random = new Random(System.currentTimeMillis());

    public static void main(String[] args) {
        int[] solutionArray = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13 };



        long time = System.currentTimeMillis();
        int i = 0;
        for (;;){
            i++;
            shuffleArray(solutionArray);
            if (i == 1000000) {
                print(solutionArray);
                System.out.println("  "+(System.currentTimeMillis() - time));
                time = System.currentTimeMillis();
                i = 0;
            }
        }
    }

    static void print (int[] a){
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i] + "\t");
        }
    }

    //  Durstenfeld shuffle
    static void shuffleArray(int[] ar){

        for (int i = ar.length - 1; i > 0; i--){
            int index = random.nextInt(i + 1);
            // Simple swap
            int a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

}
