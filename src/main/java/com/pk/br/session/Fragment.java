package com.pk.br.session;

/**
 * Created by PavelK on 4/8/2017.
 */
public class Fragment {
    private final long mask;
    private final int offset;
    private final int valueMask;
    private final String name;

    public Fragment(long mask, String name){
        this.mask = mask;
        this.name = name;
        int size = 0;
        int tmpOffset = 0;

        boolean found = false;
        for (int i=0; i<=64; i+=4){
            tmpOffset=i;
            byte b = (byte)((mask>>i)&0xFL);
            if (b==0xF) {
                if (found) break;
            } else
            if (b==0x0){
                found = true;
                size++;
            } else throw new IllegalArgumentException(Long.toHexString(mask)+" only 0x0 or 0xF allowed!");
        }
        offset = tmpOffset-(size)*4;
        valueMask = (int)Math.pow(2,size*4)-1;

    }

    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("mask: ").append(Long.toHexString(mask));
        //sb.append("\nsize: ").append(size);
        sb.append("\noffset: ").append(offset);
        sb.append("\nvalueMask: ").append(Integer.toHexString(valueMask));
        return sb.toString();
    }

    public String toString(long data){
        StringBuilder sb = new StringBuilder(100);
        sb.append("[").
                append(String.format("%016X", mask)).
                append("] ").append(name).append(" - ").append(get(data)).append("\n");
        return sb.toString();
    }

    public final int get(long data){
        return ((int)(data>>offset) & valueMask);
    }


    public final long set(long data, int value) {
        long tmp = (long)(value & valueMask);
        data = data & mask;
        return data | (tmp<<offset);
    }


}
