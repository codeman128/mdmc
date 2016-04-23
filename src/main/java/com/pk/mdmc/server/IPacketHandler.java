package com.pk.mdmc.server;

import com.lmax.disruptor.EventHandler;
import com.pk.mdmc.Packet;

/**
 * Created by PavelK on 4/23/2016.
 */

public interface IPacketHandler extends EventHandler<Packet> {
}
