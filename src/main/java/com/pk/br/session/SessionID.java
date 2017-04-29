package com.pk.br.session;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by PavelK on 4/8/2017.
 */


public class SessionID {
    private static AtomicInteger counter = new AtomicInteger(0);
    public static Fragment INSTANCE_ID     = new Fragment(0x0FFFFFFFFFFFFFFFL, "INSTANCE_ID    ");
    public static Fragment CYCLIC_COUNTER  = new Fragment(0xF0000FFFFFFFFFFFL, "CYCLIC_COUNTER ");
    public static Fragment SUBSCRIPTION_ID = new Fragment(0xFFFFF0000FFFFFFFL, "SUBSCRIPTION_ID");
    public static Fragment CL_SLOT_VERSION = new Fragment(0xFFFFFFFFF0FFFFFFL, "CL_SLOT_VERSION");
    public static Fragment CL_SLOT_ID      = new Fragment(0xFFFFFFFFFF00FFFFL, "CL_SLOT_ID     ");
    public static Fragment SP_CLUSTER      = new Fragment(0xFFFFFFFFFFFF0FFFL, "SP_CLUSTER");
    public static Fragment SP_VERSION = new Fragment(0xFFFFFFFFFFFFF0FFL, "SP_VERSION");
    public static Fragment SP_ID      = new Fragment(0xFFFFFFFFFFFFFF00L, "SP_ID     ");

    private long sid;

    public final long getId(){
        return  sid;
    }

    public final void setId(long anSId){
        sid = anSId;
    }

    public static int getNextCyclicId(){
        return counter.incrementAndGet();
    }

    public String toString(){
        StringBuilder sb = new StringBuilder(300);
        sb.append("[").append(String.format("%016X", sid)).append("] SID             - ").append(sid).append("\n").
                append(INSTANCE_ID.toString(sid)).
                append(CYCLIC_COUNTER.toString(sid)).
                append(SUBSCRIPTION_ID.toString(sid)).
                append(CL_SLOT_VERSION.toString(sid)).
                append(CL_SLOT_ID.toString(sid)).
                append(SP_CLUSTER.toString(sid)).
                append(SP_VERSION.toString(sid)).
                append(SP_ID.toString(sid));
        return sb.toString();
    }

    public final void setInstanceId(int value) {
        sid = INSTANCE_ID.set(sid, value);
    }

    public final void setCyclicCounter(int value) {
        sid = CYCLIC_COUNTER.set(sid, value);
    }

    public final int getCyclicCounter() {
        return CYCLIC_COUNTER.get(sid);
    }

    public final void setSubscriptionId(int value) {
        sid = SUBSCRIPTION_ID.set(sid, value);
    }

    public final int getSubscriptionId() {
        return SUBSCRIPTION_ID.get(sid);
    }

    public final void setCLSlotVersion(int value) {
        sid = CL_SLOT_VERSION.set(sid, value);
    }

    public final int getCLSlotVersion() {
        return CL_SLOT_VERSION.get(sid);
    }

    public final void setCLSlotId(int value) {
        sid = CL_SLOT_ID.set(sid, value);
    }

    public final int getCLSlotId(){
        return CL_SLOT_ID.get(sid);
    }

    public final void setSPCluster(int value) {
        sid = SP_CLUSTER.set(sid, value);
    }

    public final int getSPCluster(){
        return SP_CLUSTER.get(sid);
    }

    public final void setSPVersion(int value) {
        sid = SP_VERSION.set(sid, value);
    }

    public final int getSPSVersion(){
        return SP_VERSION.get(sid);
    }

    public final void setSPId(int value) {
        sid = SP_ID.set(sid, value);
    }

    public final int getSPId() {
        return SP_ID.get(sid);
    }

        public static void main(String[] args) {
        SessionID s = new SessionID();
        s.setInstanceId(0x1);
        s.setCyclicCounter(0x2345);
        s.setSubscriptionId(0x6789);

        s.setCLSlotVersion(0x0);
        s.setCLSlotId(0xAB);

        s.setSPCluster(0xC);
        s.setSPVersion(0x000AD);
        s.setSPId(0x000EF);
        System.out.println(s);

    }



}

