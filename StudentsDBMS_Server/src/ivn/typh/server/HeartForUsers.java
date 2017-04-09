package ivn.typh.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartForUsers implements Runnable {

	private static ServerSocket server;
	private Socket client;


	@Override
	public void run() {
		try {
			server = new ServerSocket(PortList.USER.port);
			while (Typh.isServerRunning()) {
				client = server.accept();
				HeartForAdmin.message="__BEAT__";
				ObjectInputStream in = new ObjectInputStream(client.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
	

				String user_t = (String) in.readObject();
				if (!Typh.userList.contains(user_t))
					Typh.userList.add(user_t);
				else
					HeartForAdmin.message="__EXISTS__";

				ScheduledExecutorService serviceU = Executors.newSingleThreadScheduledExecutor();

				Runnable pulse = new Runnable() {
					@Override
					public void run() {
						try {
						
						out.writeObject(HeartForAdmin.message);
						out.flush();

							} catch (IOException e) {
								if (Typh.userList.contains(user_t))
									Typh.userList.remove(user_t);
							}

						HeartForAdmin.message="__BEAT__";

					}

				};

				serviceU.scheduleAtFixedRate(pulse, 0, 5, TimeUnit.SECONDS);
			}
		} catch (IOException | ClassNotFoundException e) {	}
	}

}
