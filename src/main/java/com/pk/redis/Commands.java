package com.pk.redis;

import com.pk.lang.ImmutableString;

/**
 * Created by PavelK on 8/26/2016.
 */
public enum Commands {
    PING,
    SET,
    GET;

    private ImmutableString string;

    Commands(){
        this.string = new ImmutableString(name());
    }

    public ImmutableString getString(){
        return string;
    }

}
