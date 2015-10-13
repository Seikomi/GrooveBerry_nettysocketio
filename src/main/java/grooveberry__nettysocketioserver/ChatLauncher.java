package grooveberry__nettysocketioserver;

import com.corundumstudio.socketio.listener.*;
import com.corundumstudio.socketio.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatLauncher {
    public final static int SERVER_COMMANDE_PORT = 2009;
	public final static int SERVER_TRANSFERT_PORT = 3009;
    
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    public static void main(String[] args) throws InterruptedException {
        
        
        
        

        Configuration config = new Configuration();
        config.setHostname("192.168.0.43");
        config.setPort(9092);

        final SocketIOServer server = new SocketIOServer(config);
        server.addEventListener("chatevent", ChatObject.class, new DataListener<ChatObject>() {
            
            @Override
            public void onData(SocketIOClient client, ChatObject data, AckRequest ackRequest) {
                System.out.println(data.getMessage());
                
                try {
					out.writeObject(data.getMessage());
					out.flush();
					out.reset();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
	
                
                
                // broadcast messages to all clients
                server.getBroadcastOperations().sendEvent("chatevent", data);
            }
        });
        
        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                try {
			Socket socketCommande = new Socket(InetAddress.getLocalHost(), SERVER_COMMANDE_PORT);
			
			
			if (socketCommande.isConnected() && socketCommande.isBound()) {
				
				out = new ObjectOutputStream(socketCommande.getOutputStream());
				out.flush();
				out.reset();
                
                in = new ObjectInputStream(socketCommande.getInputStream());
                			
			}  else {
                System.out.println("ERREUR");
            }
			
			Socket socketFile = new Socket(InetAddress.getLocalHost(), SERVER_TRANSFERT_PORT);
			if (socketFile.isConnected() && socketFile.isBound()) {
				ObjectInputStream fileIn = new ObjectInputStream(socketFile.getInputStream());

				ObjectOutputStream fileOut = new ObjectOutputStream(socketFile.getOutputStream());
				fileOut.flush();
				fileOut.reset();

			} else {
                System.out.println("ERREUR");
            }
			
			
			
			
			
			//socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
            }
        });

        server.start();

        Thread.sleep(Integer.MAX_VALUE);

        server.stop();
    }

}
