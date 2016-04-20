package com.pk.mdmc.impl;

/**
 * Created by PavelK on 3/6/2016.
 */



import com.pk.mdmc.core.IConfig;
import com.pk.mdmc.core.Packet;

import java.io.IOException;

import java.net.MulticastSocket;
import java.net.UnknownHostException;


public class MDMCClient {

    public static void main(String[] args) throws UnknownHostException {
        IConfig cnfg = new MDMCConfig();
        Packet packet = new Packet(cnfg);

        try (MulticastSocket clientSocket = new MulticastSocket(cnfg.getPort())){
            //Joint the Multicast group.
            clientSocket.joinGroup(cnfg.getAddress());

            while (true) {
                //clientSocket.receive(packet.getDatagram());
                System.out.println("Socket received: "+packet);


                //packet = window.processPacket(packet);

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
