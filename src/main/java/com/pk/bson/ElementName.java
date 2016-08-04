package com.pk.bson;


/**
 * Created by pkapovski on 8/4/2016.
 */
public class ElementName {
    private byte[] bytes;
    private int length;
    private int hash;

    public ElementName(String name){
        bytes = name.getBytes();
        length = bytes.length;
        hashCode();
    }

    public ElementName(byte[] name, int offset, int length){
        bytes = new byte[length];
        System.arraycopy(name, offset, bytes, 0, length);
        this.length = length;
        hashCode();
    }

    public int hashCode() {
        if (hash == 0 && length > 0) {
            hash = 1;
            for (int i=0; i<length; i++) {
                hash = 31*hash + bytes[i];
            }
        }
        return hash;
    }

    public String toString(){
        return new String(bytes, 0, length);
    }

}
