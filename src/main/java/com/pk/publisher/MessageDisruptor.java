package com.pk.publisher;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by PavelK on 5/22/2016.
 */
public class MessageDisruptor {
    private final IPublisherConfig config;
    private final Disruptor<Message> disruptor;
    private final RingBuffer<Message> ringBuffer;

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
    }

    public MessageDisruptor(Publisher publisher) {
        this.config = publisher.getConfig();
        Executor executor = Executors.newCachedThreadPool();
        disruptor = new Disruptor<>(factory, config.getDisruptorRingSize(), executor,
                ProducerType.SINGLE, config.getDisruptorStrategy());

        disruptor.handleEventsWith(publisher.feeders);
        disruptor.start();
        this.ringBuffer = disruptor.getRingBuffer();
    }

    public Message next() {
        final long sequence = ringBuffer.next();
        final Message msg = ringBuffer.get(sequence);
        msg.init(sequence);
        return msg;
    }

    public void push(Message msg) {
        final long rbSequence = msg.getDSequence();
        ringBuffer.publish(rbSequence);
    }

}
