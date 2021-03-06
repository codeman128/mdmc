package com.pk.publisher.testutils;

import com.pk.publisher.core.ClientConnection;
import com.pk.publisher.core.FeederLogEntry;
import com.pk.publisher.core.IEventCollector;
import com.pk.publisher.core.Message;
import com.pk.publisher.sd.ConnectionMetadata;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

/**
 * Created by pkapovski on 5/26/2016.
 */
public class EventCollectorStub implements IEventCollector<Object> {

    @Override
    public  Object createProductLogger(String productName) {
        return null;
    }

    @Override
    public Object createFeederLogger(String feederName) {
        return null;
    }

    @Override
    public void closeLogger(Object logger) {

    }

    @Override
    public void logProductDelta(Object logger, long arbTime, byte[] buffer, int offset, int size) {

    }

    @Override
    public void logFeederData(Object logger, FeederLogEntry fle) {

    }


    @Override
    public void onConnectionAccepted(byte[] listenerName, ClientConnection connection) {
        Socket s = connection.getSocket();
        byte[] publisherName = connection.getFeeder().getPublisher().getName();
        System.out.println("New connection accepted by ["+new String(listenerName)+"] from ["+ s.getInetAddress()+":"+s.getPort()+
                "] assigned to P["+new String(publisherName)+"] F["+connection.getFeeder().getId()+"] C["+connection.getId()+"]");
    }

    @Override
    public void onConnectionRejected_UnknownConsumer(byte[] listenerName, String fromAddress) {
        System.out.println("Connection Rejected - Unknown from address: "+ fromAddress);
    }

    @Override
    public void onConnectionRejected_Busy(byte[] listenerName, ConnectionMetadata mData) {
        System.out.println("Connection from "+mData+" Rejected - Busy");

    }

    @Override
    public void onConnectionRejected_LimitReached(byte[] listenerName, ConnectionMetadata mData) {
        System.out.println("Connection from "+mData +" rejected, simulations connection limit reached "+mData.getConsumer().getSimConnLimit());
    }

    @Override
    public void onUnexpectedListenerError(byte[] listenerName, Exception e) {
        System.out.println("onUnexpectedListenerError\n");
        e.printStackTrace();
    }

    @Override
    public void onListenerStartFailed(byte[] name, IOException e) {
        System.out.println("Listener "+new String(name)+" bind failed.");
        e.printStackTrace();
    }

    @Override
    public void onListenerShutdown(byte[] name) {
        System.out.println("Listener shutdown " + new String(name));
    }

    @Override
    public void onConnectionAssignError(ClientConnection clientConnection, ConnectionMetadata mData, IOException e) {
        System.out.println("Connection Assign Error");
        e.printStackTrace();
    }

    @Override
    public void onConnectionWriteError(ClientConnection clientConnection, ConnectionMetadata mData, Exception e) {
        System.out.println("Connection Write Error, connection closed and released " + mData);
    }

    @Override
    public void onMonitorWriteTimeout(ClientConnection clientConnection, long timeNano, long timeout, Message msg) {
//        String msgName = "NULL";
//        if (msg.type!=null) msgName = msg.type.name();
//        System.out.println("Monitor - write timeout detected, stale for "+timeNano+" expected "+timeout+" when publishing "+msg.type+
//                "\n Connection closed from: "+clientConnection.getMetadata() );

        ConnectionMetadata mData = clientConnection.getMetadata();
        System.out.println("Connection from consumer "+mData.getConsumer().getName() +"ip "+mData.getIpString() +" closed by latency monitor when publishing "+msg.type+
        ". Took "+timeNano+" threshold "+timeout+" .");

    }

    @Override
    public void onMonitorShutdown() {
        System.out.println("Monitor shutdown");
    }

    @Override
    public void onPublisherShutdownStarted(byte[] name) {
        System.out.println("Publisher "+new String(name)+" shutdown started");
    }

    @Override
    public void onMonitorException(Exception e) {
        System.out.println("Monitor - Exception "+e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void onPublishStats(Message message, ClientConnection cc) {
        ConnectionMetadata cmd = cc.getMetadata();
        //if (cmd == null) return;
        /**/
        System.out.println((new Date(message.eventTime)).toString()+ ", " +                            // arb time with ms
                           cc.getFeeder().getId()+"_"+cc.getId()+", " +                                 // feeder + connection id
                           cmd.getConsumer().getName()+ ", " +                                         // consumer name
                           cmd.getIpString()+ ", " +                                                   // consumer ip
                           (cc.getNextMsgSequenceId()-1)+", " +                                        // message sequence id
                           message.type +", " +   // don't use until we support 0 alloc                // message type
                           cc.getSentSize()+", " +                                                     // published length

                           (cc.getFinishTime()-message.eventTime)  +", " +                             // total Time     (in msec)
                           (message.captureTime - message.eventTime)  +", " +                          // total MSL time (in msec)
                           (double)(cc.getFinishTimeNano()-message.captureNano)/1000000 +", " +        // total MDS time (in msec)
                           (double)(cc.getFinishTimeNano()-message.publishNano)/1000000 +", " +        // from publish time
                           (double)(cc.getFinishTimeNano()-cc.getStartTimeNano())/1000000              // write time

        );/**/



    }
}
