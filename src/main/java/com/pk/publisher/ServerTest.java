package com.pk.publisher;

import java.io.IOException;
import java.net.Socket;



/**
 * Created by PavelK on 5/21/2016.
 */
public class ServerTest {



    static AbstractEventEmitter eventEmitter = new AbstractEventEmitter() {

        @Override
        public void onConnectionAccepted(ClientConnection connection) {
            Socket s = connection.getSocket();
            System.out.println("New connection accepted from ["+ s.getInetAddress()+":"+s.getPort()+"] assigned to C["
            +connection.getId()+"] F["+connection.getFeeder().getId()+"]");
        }

        @Override
        public void onConnectionRejected_Invalid() {
            System.out.println("Connection Rejected - Invalid");
        }

        @Override
        public void onConnectionRejected_Busy() {
            System.out.println("Connection Rejected - Busy");

        }

        @Override
        public void onUnexpectedAcceptorError(Exception e) {
            System.out.println("onUnexpectedAcceptorError\n");
            e.printStackTrace();
        }

        @Override
        public void onBindFailed(int port, IOException e) {
            System.out.println("Bind to port "+port+" failed.");
            e.printStackTrace();
        }

        @Override
        public void onConnectionAssignError(ClientConnection clientConnection, IOException e) {
            System.out.println("Connection Assign Error");
            e.printStackTrace();
        }

        @Override
        public void onConnectionWriteError(ClientConnection clientConnection, Exception e) {
            System.out.println("Connection Write Error, connection closed and released");
            e.printStackTrace();
        }


    };




        public static void main(String[] args) throws Exception {
            String configPath = null;
            try {
                configPath = System.getProperty("user.dir").replace("\\", "/");
                configPath = configPath + "/publisher.cnfg";
                System.out.println("Load configuration: " + configPath);

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }



            IPublisherConfig config = new PublisherConfig(configPath);

        Publisher publisher = new Publisher("L1 PUBLISHER".getBytes(), config, eventEmitter);

        long time;
        Message msg;

        while (true){
            time = System.currentTimeMillis();
            String update_str = "UPDATE "+time+"\n";
            String snapshot_str = "SNAPSHOT "+time+"\n";


            msg = publisher.getNext();
            System.arraycopy(update_str.getBytes(), 0, msg.getBuffer(), 0, update_str.length());
            msg.length = update_str.length();
            msg.type = Message.TYPE.UPDATE;
            msg.eventTime = System.nanoTime();
            publisher.publish(msg);

            msg = publisher.getNext();
            System.arraycopy(snapshot_str.getBytes(), 0, msg.getBuffer(), 0, snapshot_str.length());
            msg.length = snapshot_str.length();
            msg.type = Message.TYPE.SNAPSHOT;
            msg.eventTime = System.nanoTime();
            publisher.publish(msg);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

//        ServerSocket server = new ServerSocket(8080);
//        Socket client = server.accept();
//        OutputStream os = client.getOutputStream();
//        os.write("Welcome :-)".getBytes());
//        os.flush();
//        //int a= System.in.read();
//        os.close();
//        client.close();
//        server.close();
    }
}
