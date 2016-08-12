package com.pk.bson.lang;

/**
 * Created by PavelK on 8/12/2016.
 */
public class ImmutableInteger {
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
}
