package com.pk.mdmc.server;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.pk.mdmc.IConfig;
import com.pk.mdmc.Packet;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by PavelK on 4/23/2016.
 */
public class PacketDisruptor implements IPacketBuffer{
    private final IConfig config;
    private final Disruptor<Packet> disruptor;
    private final RingBuffer<Packet> ringBuffer;
    private long currentSequence = Long.MIN_VALUE;

    private final EventFactory<Packet> factory = new EventFactory<Packet>() {
        @Override
        public Packet newInstance() {
            return new Packet(config);
        }
    };

    private PacketDisruptor() {
        config = null;
        disruptor = null;
        ringBuffer = null;
    }

    public PacketDisruptor(IConfig config, IPacketHandler handler) {
        this.config = config;
        Executor executor = Executors.newCachedThreadPool();
        disruptor = new Disruptor<>(factory, config.getDisruptorRingSize(), executor,
                ProducerType.SINGLE, new BusySpinWaitStrategy());

        disruptor.handleEventsWith(handler);
        disruptor.start();
        this.ringBuffer = disruptor.getRingBuffer();
    }

    public Packet next() {
        if (currentSequence == Long.MIN_VALUE) {
            currentSequence = ringBuffer.next();
            return ringBuffer.get(currentSequence);
        } else {
            throw new IllegalStateException();
        }
    }

    public void publish() {
        if (currentSequence == Long.MIN_VALUE) {
            throw new IllegalStateException();
        } else {
            ringBuffer.publish(currentSequence);
            currentSequence = Long.MIN_VALUE;
        }
    }
}
