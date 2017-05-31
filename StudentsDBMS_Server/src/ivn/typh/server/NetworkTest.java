package ivn.typh.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

/*
 * This class provides simple connection for any client to first check if the server
 * application is running or not.
 */
public class NetworkTest implements Runnable{

	@Override
	public void run() {
		try {
			ServerSocket server = new ServerSocket(PortList.NETWORKTEST.port);
			while(Typh.isServerRunning()){
				Socket testClient = server.accept();
				Typh.tlog.log(Level.WARNING,testClient.getRemoteSocketAddress()+" is trying to connect !");
				testClient.close();
			}
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
