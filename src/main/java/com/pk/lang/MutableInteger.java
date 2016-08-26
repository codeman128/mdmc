package com.pk.lang;

/**
 * Created by PavelK on 8/12/2016.
 */
public class MutableInteger extends ImmutableInteger{

    public MutableInteger(int value) {
        super(value);
    }

    public void set(int value) {
        this.value = value;
    }


}
