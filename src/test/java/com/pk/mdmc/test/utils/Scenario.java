package com.pk.mdmc.test.utils;

import com.pk.mdmc.core.IConfig;
import com.pk.mdmc.core.Packet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PavelK on 4/16/2016.
 */
public class Scenario {
    private final List<Packet> list = new ArrayList<Packet>();
    private IConfig cnfg;

    private Scenario() {}

    public Scenario(IConfig cnfg){
        this.cnfg = cnfg;
    }

    public Packet add(long seqId, int segId, int segCount, int size) {
        Packet p = new Packet(cnfg);
        p.setSequenceId(seqId);
        p.setSegmentId((short) segId);
        p.setSegmentCount((short) segCount);
        p.setPayloadSize((short)size);
        list.add(p);
        return p;
    }

    public List<Packet> getPacketList(){
        return list;
    }
}
