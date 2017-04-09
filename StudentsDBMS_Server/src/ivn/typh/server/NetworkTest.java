package ivn.typh.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkTest implements Runnable{

	@Override
	public void run() {
		try {
			ServerSocket server = new ServerSocket(PortList.NETWORKTEST.port);
			while(Typh.isServerRunning()){
				Socket testClient = server.accept();
				testClient.close();
			}
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
