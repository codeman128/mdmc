package com.pk.bson.lang;

/**
 * Created by PavelK on 11/13/2014.
 */
public class ObjectCache<T> {

    private int count = 0;
    private Object[] list;
    private Object[] available;
    private int availableCount = 0;

    private ObjectCache(){}

    public ObjectCache(int maxSize){
        list = new Object[maxSize];
        available = new Object[maxSize];
    }

    /** This method used for iteration */
    @SuppressWarnings("unchecked")
    public T get(int index){
        return (T)list[index];
    }

    public int getCount(){
        return count;
    }

    public int getAvailableCount(){
        return availableCount;
    }

    public int add(T obj) {
        if (count<(list.length) && availableCount <(list.length)){
            list[count++] = obj;
            available[availableCount++]=obj;
            return availableCount-1;
        } else return -1;
    }

    /** Acquire Available Object. Null if no available  */
    @SuppressWarnings("unchecked")
    public T acquire(){
        if (availableCount>0) {
            return (T)available[--availableCount];
        } return null;
    }

    /***/
    public void release(T obj){
        if (obj!=null && availableCount<count) {
            available[availableCount++] = obj;
        }
    }

    public void debugFindLeak() {
        System.out.println("count:"+count+" available:"+getAvailableCount());
        System.out.println("--------------------------------------------------");
        for (Object i : list) {
            boolean found = false;
            for (int j=0; j<availableCount; j++) {
                if (i==available[j]) {
                    found = true;
                }
            }
            if (!found) {
                System.out.println(">> "+i);
            }
        }
        System.out.println("--------------------------------------------------");
    }
}
