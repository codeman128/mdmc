package com.pk.publisher.core;

import java.nio.ByteBuffer;

/**
 * Created by PavelK on 10/23/2016.
 */
public class FeederLogEntry {

    /**
     * The Long Deflater allows to reduce number of bytes required to store long time values if they are monotonically
     * increment. If delta (between previously stored and current value) does not exceeds 4 bytes - delta will be
     * returned, if delta is > Integer.MAX_VALUE, delta will loose precision in 3 orders of magnitude and delta will be
     * returned as negative values.
     *
     * Example:
     *      We had delta 2,000,000,000 nano (which is 2 sec) it's < Integer.MAX_VALUE resolution will still be nanos.
     *      If we had delta 30,000,000,000 nano (which is 30 sec) and it's > Integer.MAX_VALUE, for periods like 30 sec
     *      nano no longer matter, we can switch to micro and the return value will be -30,000,000
     *
     * Assumptions:
     *      This is not universal solution, it's used for high-frequency/low-latency logging, so intervals are really in
     *      nanos, and not in millis, so in most of the cases precision won't be lost, and in rare cases it's ok to switch
     *      to micro if interval is to long.
     * */
    private class LongDeflater {
        private long base;

        public void reset(long base) {
            this.base = base;
        }

        public int deflate(long value){
            long delta = value-base;  //todo BUG!!! handle negative delta!!!
            base = value;
            if (delta<=Integer.MAX_VALUE) {
                return (int)delta;
            }  else {
                return - ((int)(delta/1000));
            }

        }
    }

    protected final int maxSize;
    protected final byte[] buffer;
    protected final ByteBuffer bb;
    protected int size;

    protected final LongDeflater nanoDeflater = new LongDeflater();
    protected final LongDeflater msecDeflater = new LongDeflater();



    private FeederLogEntry(){
        maxSize = 0;
        buffer = null;
        bb = null;
    }

    public FeederLogEntry(int maxSize){
        this.maxSize = maxSize;
        buffer = new byte[maxSize];
        bb = ByteBuffer.wrap(buffer, 0, maxSize);
    }

    public final void build(final Message msg, final short msgCount, final int[] pubOrder, final ClientConnection[] clients ){
        bb.position(0);
        bb.put((byte)msg.type.getId());
        bb.putLong(msg.eventTime);
        bb.put((byte) msgCount);
        size = bb.position();

        if (msgCount==0) return;

        // this was not an empty iteration
        int sizeOffset = size;
        int msgSize = msg.length-msg.offset;
        bb.putInt(0); // reserve space for size field

        // put message metadata
        bb.putLong(msg.captureNano);
        bb.putLong(msg.captureTime);
        bb.putLong(msg.publishNano);
        bb.putInt(msgSize); // message size without header


        msecDeflater.reset(msg.captureTime);
        nanoDeflater.reset(msg.publishNano);
        int ccReference;
        for (int i=0; i< pubOrder.length; i++){
            ccReference = pubOrder[i];
            ClientConnection cc = clients[ccReference];
            if (cc.getSentSize()>0) {
                bb.put((byte)ccReference); // back to int & 0xFF
                bb.putInt((int) (cc.getNextMsgSequenceId() - 1));
                bb.putInt(nanoDeflater.deflate(cc.getStartTimeNano()));
                bb.putInt(nanoDeflater.deflate(cc.getFinishTimeNano()));
                bb.putInt(msecDeflater.deflate(cc.getFinishTime()));

                if (msg.type== Message.TYPE.SNAPSHOT){
                    bb.put((byte)(cc.getMetadata().getConsumer().getNameBytes().length));
                    bb.putLong(cc.getEncodedClientAddress());
                    bb.putLong(cc.getEncodedServerAddress());
                }
            }
        }
        // Add snapshot itself, just once if at least user got it in this iteration. Size we already put earlier.
        if (msg.type== Message.TYPE.SNAPSHOT){
            bb.put(msg.getBuffer(),msg.offset, msgSize);
        }
        size = bb.position();
        bb.putInt(sizeOffset, size);
        //System.out.println("MsgType:"+msg.type+"  Size: "+size+"   entries:"+msgCount);
    }

}
