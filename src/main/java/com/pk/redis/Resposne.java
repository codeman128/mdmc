package com.pk.redis;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by PavelK on 8/26/2016.
 */
public class Resposne {
    private final byte[] buffer = new byte[1024*10];

    public void read(InputStream in) throws IOException {
        int len = in.read(buffer, 0, buffer.length);
        switch (buffer[0]) {
            case '+': {

            }
        }

    }

}
