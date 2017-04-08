package ivn.typh.server;

import java.io.IOException;
import java.net.ServerSocket;

public class HeartCheckUP implements Runnable{

	@Override
	public void run() {
		try {
			ServerSocket server = new ServerSocket(61002);
			while(Typh.isServerRunning()){
				server.accept();
				server.close();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}
