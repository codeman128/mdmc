package com.pk.mdmc.core;

/**
 * Created by PavelK on 4/8/2016.
 */
public class Window {
    private long sequence;
    private final IConfig cnfg;
    private final IMessageBuffer messageProducer;
    private final int maxWidth;
    private final Message[] list;
    private final boolean TRACE;

    private Window() {
        maxWidth = 0;
        cnfg = null;
        messageProducer = null;
        list = null;
        TRACE = true;
    }

    public Window(IConfig cnfg, IMessageBuffer messageProducer) {
        TRACE = cnfg.getNetTraceEnabled();
        this.messageProducer = messageProducer;
        this.cnfg = cnfg;
        sequence = Integer.MIN_VALUE;
        maxWidth = cnfg.getWindowMaxWidth();
        list = new Message[maxWidth];
        for (int i=0; i<maxWidth; i++) { list[i] = messageProducer.next(); }
    }

    public void init() {
        this.sequence = Integer.MIN_VALUE;
    }

    private void roll() {
        messageProducer.push(list[0]);
        for (int i=1; i<maxWidth; i++) { list[i-1] = list[i]; }
        //System.arraycopy(list, 1, list,  0, maxWidth-1);
        list[maxWidth-1] = messageProducer.next();
        sequence++;
        if (TRACE) System.out.println("WINDOW "+sequence+"> Rolled ");
    }

    public Packet processPacket(Packet packet) {
       final long pSequence = packet.getSequenceId();

        // Check if first packet - init sequence
        if (sequence == Integer.MIN_VALUE) {
            sequence = pSequence;
        }

        // Check if late packet, message already sent* (out of the window)
       if (pSequence < sequence) {
           if (TRACE) System.out.println("WINDOW "+sequence+"> Drop late "+ packet);
           return packet;
       }

       // Check if messages in frame still relevant and if not roll the window
       while(pSequence > (sequence+maxWidth-1)) {
           roll();
       }

       // lets add packet
       final int index = (int) (pSequence - sequence);
       final Message msg = list[index];
       final Packet p2return =  msg.addPacket(packet);

       // if message is filled roll the window (message will be sent)
       if (msg.isFilled()) {
          for (int i=0; i<=index; i++) {
              roll();
          }
       }
       return p2return;
    }

}
