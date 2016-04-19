package com.pk.mdmc.core;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by PavelK on 4/13/2016.
 */
public class MessageDisruptor implements IMessageProducer {
    private final boolean TRACE = true;
    private final Config cnfg;
    private final Disruptor<Message> disruptor;
    private final RingBuffer<Message> ringBuffer;

    private final EventFactory<Message> factory = new EventFactory<Message>() {
        @Override
        public Message newInstance() {
            return new Message(cnfg);
        }
    };

    public MessageDisruptor(Config cnfg, EventHandler<Message> handler) {
        this.cnfg = cnfg;
        Executor executor = Executors.newCachedThreadPool();

        disruptor = new Disruptor<>(factory, cnfg.getDisruptorRingSize(), executor,
                ProducerType.SINGLE, new BusySpinWaitStrategy());

        disruptor.handleEventsWith(handler);

        disruptor.start();

        this.ringBuffer = disruptor.getRingBuffer();
    }

    @Override
    public Message next() {
        final long sequence = ringBuffer.next();
        final Message msg = ringBuffer.get(sequence);
        msg.init(sequence);
        return msg;
    }

    @Override
    public void push(Message msg) {
        final long rbSequence = msg.getRbSequence();
        if (msg.isFilled()) {
            if (TRACE) System.out.println("RB ["+rbSequence+"] --->>>  push "+msg);
        } else {
            if (TRACE) System.out.println("RB ["+rbSequence+"] --->>>  drop "+msg);
        }
        ringBuffer.publish(rbSequence);
    }
}


