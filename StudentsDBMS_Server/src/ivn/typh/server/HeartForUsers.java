package ivn.typh.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartForUsers implements Runnable {

	private static ServerSocket server;
	private Socket client;

	public HeartForUsers() {
		try {
			server = new ServerSocket(61000);
		} catch (IOException e) {
			e.printStackTrace();
		}
}

	@Override
	public void run() {
		System.out.println("UserHeart");
		try {
			while (Typh.isServerRunning()) {
				client = server.accept();
				HeartForAdmin.message="__BEAT__";
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				PrintWriter out = new PrintWriter(client.getOutputStream());
	

				String user_t = in.readLine();
				if (!Typh.userList.contains(user_t))
					Typh.userList.add(user_t);

				ScheduledExecutorService serviceU = Executors.newSingleThreadScheduledExecutor();

				Runnable pulse = new Runnable() {
					@Override
					public void run() {
						out.println(HeartForAdmin.message);
						out.flush();
						//System.out.println("pulse: userHeart\t"+Typh.userList.toString());

						if (out.checkError()){
							if (Typh.userList.contains(user_t))
								Typh.userList.remove(user_t);
							try {
								client.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						HeartForAdmin.message="__BEAT__";

					}

				};

				serviceU.scheduleAtFixedRate(pulse, 0, 5, TimeUnit.SECONDS);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
