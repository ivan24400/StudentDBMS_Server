package ivn.typh.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HeartCheckUP implements Runnable{

	@Override
	public void run() {
		try {
			ServerSocket server = new ServerSocket(61002);
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
