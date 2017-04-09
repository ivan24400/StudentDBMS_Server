package ivn.typh.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartForAdmin implements Runnable {

	private static ServerSocket server;
	private static Socket socket;

	public static String message;

	@Override
	public void run() {
		try {
			server = new ServerSocket(PortList.ADMIN.port);

			while (Typh.isServerRunning()) {

				socket = server.accept();
				System.out.println(socket.getRemoteSocketAddress());
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

				Runnable users = new Runnable() {

					@Override
					public void run() {

						try {
							out.writeObject(Typh.userList);
							out.flush();
							message = (String) in.readObject();
						} catch (IOException | ClassNotFoundException e) {
							try {
								socket.close();
							} catch (IOException e1) {

							}
							service.shutdownNow();
						}

					}

				};

				service.scheduleAtFixedRate(users, 0, 5, TimeUnit.SECONDS);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
