package ivn.typh.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * This class checks if the given user is already logged in or not.
 */
public class CheckUser implements Runnable{

	@Override
	public void run() {
		try {
			ServerSocket server = new ServerSocket(PortList.CHECKUSER.port);
			while(Typh.isServerRunning()){
				Socket client = server.accept();
				PrintWriter out = new PrintWriter(client.getOutputStream());
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				
				String user = in.readLine();
				if(Typh.userList.contains(user))
					out.println("__EXIST__");
				else
					out.println("__OK__");
				out.flush();
				out.close();
				in.close();
				client.close();
			}
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
