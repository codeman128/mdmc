package com.pk.mdmc.client;

import com.pk.mdmc.IConfig;
import com.pk.mdmc.Message;
import com.pk.mdmc.Packet;

/**
 * Created by PavelK on 4/8/2016.
 */
public class Window {
    private final IConfig config;
    private final IMessageRingBuffer messageProducer;
    private final int maxWidth;
    private final Message[] list;
    private final boolean TRACE;
    private long sequence;

    private Window() {
        maxWidth = 0;
        config = null;
        messageProducer = null;
        list = null;
        TRACE = true;
    }

    public Window(IConfig config, IMessageRingBuffer messageProducer) {
        TRACE = config.getNetTraceEnabled();
        this.messageProducer = messageProducer;
        this.config = config;
        sequence = Integer.MIN_VALUE;
        maxWidth = config.getWindowMaxWidth();
        list = new Message[maxWidth];
        for (int i = 0; i < maxWidth; i++) {
            list[i] = messageProducer.next();
        }
    }

    public void init() {
        this.sequence = Integer.MIN_VALUE;
    }

    private void roll() {
        messageProducer.push(list[0]);
        for (int i = 1; i < maxWidth; i++) {
            list[i - 1] = list[i];
        }
        //System.arraycopy(list, 1, list,  0, maxWidth-1);
        list[maxWidth - 1] = messageProducer.next();
        sequence++;
        if (TRACE) System.out.println("WINDOW " + sequence + "> Rolled ");
    }

    public Packet processPacket(Packet packet) {
        final long pSequence = packet.getSequenceId();

        // Check if first packet - init sequence
        if (sequence == Integer.MIN_VALUE) {
            sequence = pSequence;
        }

        // Check if "late" packet, message already sent (i.e. "out" of the window)
        if (pSequence < sequence) {
            if (TRACE) System.out.println("WINDOW " + sequence + "> Drop late " + packet);
            return packet;
        }

        // Check if messages in window still relevant and if not roll the window
        while (pSequence > (sequence + maxWidth - 1)) {
            roll();
        }

        // lets add packet
        final int index = (int) (pSequence - sequence);
        final Message msg = list[index];
        final Packet p2return = msg.addPacket(packet);

        // if message is filled, roll the window (message will be sent)
        if (msg.isFilled()) {
            for (int i = 0; i <= index; i++) {
                roll();
            }
        }
        return p2return;
    }

}
