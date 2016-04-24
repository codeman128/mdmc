package com.pk.mdmc.server;

import com.pk.mdmc.Packet;

/**
 * Created by PavelK on 4/23/2016.
 */
public interface IPacketRingBuffer {
    Packet next();

    void publish();
}
