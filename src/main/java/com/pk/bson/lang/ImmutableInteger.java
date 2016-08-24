package com.pk.bson.lang;

import com.sun.media.sound.InvalidFormatException;

/**
 * Created by PavelK on 8/12/2016.
 */
public class ImmutableInteger {

    final static byte[] MIN_INT_VAL = (""+Integer.MIN_VALUE).getBytes();

    final static byte [] DigitTens = {
            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
            '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
            '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
            '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
            '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
            '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
            '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
            '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
            '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
            '9', '9', '9', '9', '9', '9', '9', '9', '9', '9',
    } ;

    final static byte [] DigitOnes = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    } ;

    /**
     * All possible chars for representing a number as a String
     */
    final static byte[] digits = {
            '0' , '1' , '2' , '3' , '4' , '5' ,
            '6' , '7' , '8' , '9' , 'a' , 'b' ,
            'c' , 'd' , 'e' , 'f' , 'g' , 'h' ,
            'i' , 'j' , 'k' , 'l' , 'm' , 'n' ,
            'o' , 'p' , 'q' , 'r' , 's' , 't' ,
            'u' , 'v' , 'w' , 'x' , 'y' , 'z'
    };

    final static int [] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE };


    protected int value;

    public ImmutableInteger(int value){
        this.value = value;
    }

    public int get() {
        return  value;
    }

    public int hashCode() {
        return value;
    }

    @SuppressWarnings("boxing")
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        } else
        if (anObject instanceof ImmutableInteger) {
            return value == ((ImmutableInteger) anObject).value;
        } else
        if (anObject instanceof Integer) {
            return value == ((Integer) anObject).intValue();
        }
        return false;
    }

    public static int toInt(byte[] buf, int offset, int length) throws InvalidFormatException {
        int result = 0;
        for (int i=0; i<length; i++){
            byte b = buf[i+offset];
            if (48<=b && b<=57) {
                result = result * 10 + (b - 48);
            } else throw new InvalidFormatException(new String(buf, offset, length));
        }
        return result;
    }

    /**
     * Places characters representing the integer i into the
     * byte array buf.
     *
     * Will fail if i == Integer.MIN_VALUE
     */
    static void getChars(int i, int index, byte[] buf) {
        int q, r;
        int charPos = index;
        byte sign = 0;

        if (i < 0) {
            sign = '-';
            i = -i;
        }

        // Generate two digits per iteration
        while (i >= 65536) {
            q = i / 100;
            // really: r = i - (q * 100);
            r = i - ((q << 6) + (q << 5) + (q << 2));
            i = q;
            buf [--charPos] = DigitOnes[r];
            buf [--charPos] = DigitTens[r];
        }

        // Fall thru to fast mode for smaller numbers
        // assert(i <= 65536, i);
        for (;;) {
            q = (i * 52429) >>> (16+3);
            r = i - ((q << 3) + (q << 1));  // r = i-(q*10) ...
            buf [--charPos] = digits [r];
            i = q;
            if (i == 0) break;
        }
        if (sign != 0) {
            buf [--charPos] = sign;
        }
    }

    static int stringSize(int x) {
        for (int i=0; ; i++)
            if (x <= sizeTable[i])
                return i+1;
    }

    public static int intToString(byte[] buf, int offset, int value){
        if (value == Integer.MIN_VALUE) {
            System.arraycopy(MIN_INT_VAL, 0, buf, offset, MIN_INT_VAL.length);
            return MIN_INT_VAL.length;
        }
        int size = (value < 0) ? stringSize(-value) + 1 : stringSize(value);
        getChars(value, offset+size, buf);
        return size;
    }

}
