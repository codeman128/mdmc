import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.pk.publisher.*;

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
            System.out.println("New connection accepted from ["+ s.getInetAddress()+":"+s.getPort());
        }

        @Override
        public void onConnectionRejected_Invalid() {

        }

        @Override
        public void onConnectionRejected_Busy() {

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


    };

    static IPublisherConfig config = new IPublisherConfig() {
        @Override
        public int getPort() {
            return 8080;
        }

        @Override
        public int getFeederCount() {
            return 2;
        }

        @Override
        public int getMaxClientConnection() {
            return 2;
        }

        @Override
        public int getAcceptorMaxRetry() {
            return 3;
        }

        @Override
        public int getDisruptorRingSize() {
           return 128;
    }

        @Override
        public int getMaxMessageSize() {
            return 2048;
        }

        @Override
        public WaitStrategy getDisruptorStrategy() {
            return new LiteBlockingWaitStrategy();
            //new BusySpinWaitStrategy();
        }
    };


        public static void main(String[] args) throws Exception {
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
