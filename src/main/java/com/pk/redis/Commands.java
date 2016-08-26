package com.pk.redis;

import com.pk.lang.ImmutableString;

/**
 * Created by PavelK on 8/26/2016.
 */
public enum Commands {
    SET("SET"),
    GET("GET");

    private ImmutableString string;

    Commands(String string){
        this.string = new ImmutableString(string);
    }

    public ImmutableString getString(){
        return string;
    }

}
