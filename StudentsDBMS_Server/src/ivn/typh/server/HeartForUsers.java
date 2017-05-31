package ivn.typh.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/*
 * This class keeps track of all the user logged in.
 */
public class HeartForUsers implements Runnable {

	private static ServerSocket server;
	private Socket client;

	@Override
	public void run() {
		try {
			server = new ServerSocket(PortList.USER.port);
			while (Typh.isServerRunning()) {
				client = server.accept();
				HeartForAdmin.message = "__BEAT__";
				ObjectInputStream in = new ObjectInputStream(client.getInputStream());
				ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());

				String user_t = (String) in.readObject();
				synchronized (Typh.userList) {
					if (!Typh.userList.contains(user_t)){
						Typh.tlog.log(Level.INFO, user_t+" logged in.");
						Typh.userList.add(user_t);
					}else
						client.close();
				}
				ScheduledExecutorService serviceU = Executors.newSingleThreadScheduledExecutor();

				Runnable pulse = new Runnable() {
					@Override
					public void run() {
						try {

							out.writeObject(HeartForAdmin.message);
							out.flush();

						} catch (Exception e) {
							if (Typh.userList.contains(user_t)){
								Typh.userList.remove(user_t);
								Typh.tlog.log(Level.INFO, user_t + " logged out.");
							}
						}

						HeartForAdmin.message = "__BEAT__";

					}

				};

				serviceU.scheduleAtFixedRate(pulse, 0, 3, TimeUnit.SECONDS);
			}
		} catch (IOException | ClassNotFoundException e) {
		}
	}

}
