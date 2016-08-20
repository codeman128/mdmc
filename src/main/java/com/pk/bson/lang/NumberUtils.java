package com.pk.bson.lang;

import com.sun.media.sound.InvalidFormatException;

/**
 * Created by PavelK on 8/20/2016.
 */
public class NumberUtils {
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
}
