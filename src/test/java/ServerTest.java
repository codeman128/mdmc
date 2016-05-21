import com.pk.publisher.IPublisherConfig;
import com.pk.publisher.Message;
import com.pk.publisher.Publisher;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by PavelK on 5/21/2016.
 */
public class ServerTest {
    static IPublisherConfig config = new IPublisherConfig() {
        @Override
        public int getPort() {
            return 8080;
        }

        @Override
        public int getFeederCount() {
            return 1;
        }

        @Override
        public int getMaxClientConnection() {
            return 5;
        }

        @Override
        public int getAcceptorMaxRetry() {
            return 3;
        }
    };


    public static void main(String[] args) throws IOException {
        Publisher publisher = new Publisher(config);

        long time;
        Message msg = new Message(1024*5);

        while (true){
            time = System.currentTimeMillis();
            String update_str = "UPDATE "+time+"\n";
            String snapshot_str = "SNAPSHOT "+time+"\n";


            System.arraycopy(update_str.getBytes(), 0, msg.getData(), 0, update_str.length());
            msg.length = update_str.length();
            msg.type = Message.TYPE.UPDATE;
            publisher.feeders[0].publish(msg);

            System.arraycopy(snapshot_str.getBytes(), 0, msg.getData(), 0, snapshot_str.length());
            msg.length = snapshot_str.length();
            msg.type = Message.TYPE.SNAPSHOT;
            publisher.feeders[0].publish(msg);

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
