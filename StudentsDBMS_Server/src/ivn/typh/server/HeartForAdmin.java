package ivn.typh.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartForAdmin implements Runnable{

	private static ServerSocket server;
	private static Socket socket;
	
	public static String message;
	
	public HeartForAdmin(){
		try {
			server=new ServerSocket(61001);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while(Typh.isServerRunning()){
		try {
			socket = server.accept();
			System.out.println(socket.getRemoteSocketAddress());
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

			Runnable users = new Runnable(){

				@Override
				public void run() {
					System.out.println("pulse: adminHeart\t"+Typh.userList);

					try {
						out.writeObject(Typh.userList);
						out.flush();
						message = (String) in.readObject();
					} catch (IOException | ClassNotFoundException e) {
						try {
							socket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						service.shutdownNow();
						e.printStackTrace();
					}
					
				}
				
			};
			
			service.scheduleAtFixedRate(users,0,5,TimeUnit.SECONDS);

		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		
	}
}
