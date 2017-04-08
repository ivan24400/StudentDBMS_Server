package ivn.typh.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Typh {

	public static List<String> userList;
	private static OS os;
	private static String certPath = System.getProperty("user.dir") + "\\cert\\server.pem";
	private static String certPassword = "server";

	public static void main(String[] arg) {

		if (System.getProperty("os.name").substring(0, 6).toLowerCase().matches("windows*"))
			os = OS.WINDOWS;
		else
			os = OS.LINUX;

		switch (arg[0]) {
		case "start":
			if (arg.length == 3) {
				arg[2] = arg[2].replaceAll("\\\\", "\\\\\\\\");
				userList = new ArrayList<String>();
				
				if (!isServerRunning())
					startServer(arg[2]);
				else
					System.out.println("ERROR:\t Server is already running");

			} else if(arg.length == 1){

				if (!isServerRunning())
					startServer(System.getProperty("user.dir")+"\\typh.cfg");
				else
					System.out.println("ERROR:\t Server is already running");

			}else {
				System.out.println("ERROR: Invalid number of arguments\n Use \'help\' to list all options");
			}
			break;
		case "stop":
			if (isServerRunning()) {
				stopServer();
			} else {
				System.out.println("ERROR:\t No server process found!");
			}
			break;
		case "status":
			if (!isServerRunning())
				serverStatus();
			else
				System.out.println("ERROR:\t Server is not running");
			break;
		case "help":
			int numberOfCharacters = 85;
			char[] character = new char[numberOfCharacters];
			Arrays.fill(character, '=');

			System.out.println("\n" + new String(character));
			System.out.println("\n\tTyph™ Server ");
			System.out.printf("\n[1] %-25s : %s", "start", "To start a server with config file in same directory");
			System.out.printf("\n[] %25s : %s", "start config <file>", "<file> is a configuration file. Provide full path");
			System.out.printf("\n[2] %-25s : %s", "stop-server", "stop the server");
			System.out.printf("\n[3] %-25s : %s", "status", "get information about the server");
			System.out.printf("\n[4] %-25s : %s", "restart", "restart the server");
			System.out.printf("\n[5] %-25s : %s", "help", "print this message");
			System.out.println("\n\n" + new String(character));

			break;
		case "restart":
			restartServer(arg[1]);
			break;
		default:
			System.out.println("ERROR: Invalid argument");
		}
	}

	private static void restartServer(String config) {
		stopServer();
		startServer(config);
	}

	private static void startServer(String config) {
		if (os == OS.WINDOWS)
			startWServer(config);
		else if (os == OS.LINUX)
			startLServer(config);

	}

	private static void serverStatus() {

		try {
			Process stats = Runtime.getRuntime().exec("mongo --ssl --sslPEMKeyFile " + certPath
					+ " --sslPEMKeyPassword " + certPassword + " --sslAllowInvalidHostnames --eval db.serverStatus()");
			BufferedReader bf = new BufferedReader(new InputStreamReader(stats.getInputStream()));
			String line = null;
			while ((line = bf.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void stopServer() {
		try {
			if (os == OS.WINDOWS) {
				Runtime.getRuntime().exec("sc stop typhserver");

			} else if (os == OS.LINUX) {

				// For linux
			}
			System.out.println("INFO:\t Server stopped successfully");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static boolean isServerRunning() {
		boolean exist = false;
		if (os == OS.WINDOWS) {
			try {
				String line;
				Process process = Runtime.getRuntime().exec("tasklist");
				BufferedReader bf = new BufferedReader(new InputStreamReader(process.getInputStream()));
				while ((line = bf.readLine()) != null) {
					if (line.matches("mongod.*"))
						exist = true;
				}
			} catch (IOException e) {

				e.printStackTrace();
			}

		} else if (os == OS.LINUX) {
			
			// For Linux
		}
		return exist;
	}

	private static void startLServer(String config) {
		// For Linux
	}

	private static void startWServer(String config) {
		try {

			boolean isTyphServiceCreated = false;
			Process process = Runtime.getRuntime().exec("cmd /c sc query state= all");
			BufferedReader bf = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = bf.readLine()) != null) {
				if (line.contains("Typh")) {
					isTyphServiceCreated = true;
					break;
				}
			}
			process.waitFor();

			if (!isTyphServiceCreated) {
				process = Runtime.getRuntime()
						.exec("cmd /k mongod --config \"" + config
								+ "\" --install --serviceName=typhserver --serviceDisplayName=Typh --logpath=\""
								+ System.getProperty("user.dir") + "\\logs\\mongo.log\"");

				Thread.sleep(4000);

				process = Runtime.getRuntime().exec("sc start typhserver");
			} else
				process = Runtime.getRuntime().exec("sc start typhserver");
			

			Thread user = new Thread(new HeartForUsers());
			Thread admin = new Thread(new HeartForAdmin());
			Thread check = new Thread(new HeartCheckUP());
//			user.setDaemon(true);
//			admin.setDaemon(true);

			user.start();
			admin.start();
			check.start();

			
			System.out.println("INFO:\t Server Started Successfully");

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
