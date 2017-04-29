package com.pk.br.session;

/**
 * Created by PavelK on 11/13/2014.
 */
public class SubList<T> {

    private int size = 0;
    private Object[] list;
    private Object[] available;
    private int availableCount = 0;

    private SubList(){}

    public SubList(int maxSize){
        list = new Object[maxSize];
        available = new Object[maxSize];
    }

    /** This method used for iteration */
    @SuppressWarnings("unchecked")
    public T get(int index){
        return (T)list[index];
    }

    /** Total number of element in collection */
    public int getSize(){
        return size;
    }

    /** Get currently available count */
    public int getAvailableCount(){
        return availableCount;
    }

    /** Used for initialization */
    public int add(T sub) {
        if (size<(list.length) && availableCount <(list.length)){
            list[size++] = sub;
            available[availableCount++]=sub;
            return availableCount-1;
        } else return -1;
    }

    /** Acquire Available Subscription. Null if no subscriptions are available  */
    @SuppressWarnings("unchecked")
    public T popAvailable(){
        if (availableCount>0) {
            return (T)available[--availableCount];
        } return null;
    }

    /** Return element to the collection */
    public void pushAvailable(T sub){
        if (sub!=null && availableCount<size) {
            available[availableCount++] = sub;
        }
    }
}
