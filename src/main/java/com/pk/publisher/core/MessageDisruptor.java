package com.pk.publisher.core;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.pk.publisher.Publisher;
import com.pk.publisher.core.IPublisherConfig;
import com.pk.publisher.core.Message;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by PavelK on 5/22/2016.
 */
public class MessageDisruptor {
    private final IPublisherConfig config;
    private final Disruptor<Message> disruptor;
    private final RingBuffer<Message> ringBuffer;
    private final ExecutorService executor;
    private volatile long lastAllocSequence = -1;

    private final EventFactory<Message> factory = new EventFactory<Message>() {
        @Override
        public Message newInstance() {
            return new Message(config.getMaxMessageSize());
        }
    };

    private MessageDisruptor() {
        config = null;
        disruptor = null;
        ringBuffer = null;
        executor = null;
    }

    public MessageDisruptor(Publisher publisher) {
        this.config = publisher.getConfig();
        executor = Executors.newCachedThreadPool();
        disruptor = new Disruptor<>(factory, config.getDisruptorRingSize(), executor,
                ProducerType.SINGLE, config.getDisruptorStrategy());

        disruptor.handleEventsWith(publisher.getFeeders());
        disruptor.start();
        this.ringBuffer = disruptor.getRingBuffer();
    }

    public Message next() {
        final long sequence = ringBuffer.next();
        final Message msg = ringBuffer.get(sequence);
        msg.init(sequence);
        lastAllocSequence = sequence;
        return msg;
    }

    public void push(Message msg) {
        final long rbSequence = msg.getDSequence();
        msg.publishNano = System.nanoTime();
        ringBuffer.publish(rbSequence);
    }

    public void shutdown() {
        //todo review https://groups.google.com/forum/#!topic/lmax-disruptor/URytxjgyYKo
        executor.shutdown();
    }

    public long getLastAllocSequence() {
        return lastAllocSequence;
    }
}
