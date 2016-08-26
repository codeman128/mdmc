package com.pk.bson.core;

import com.pk.lang.ImmutableString;

/**
 * Created by pkapovski on 8/16/2016.
 */
public interface ICollection {

    /**
     * Remove element by key
     **/
    boolean remove(ImmutableString key);

    /**
     * Remove element by key id
     **/
    boolean remove(int key);

    void recycle();
}
