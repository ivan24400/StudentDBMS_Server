package ivn.typh.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/*
 * This class provides command line interface to the application.
 */
public class Typh {

	public static List<String> userList;
	private static OS os;
	public static Logger tlog;
	
	public static void main(String[] arg) {

		//	Setup JVM options for trust store and log.
		
		System.setProperty("javax.net.ssl.trustStore",TrustStore.PATH.VALUE);
		System.setProperty("javax.net.ssl.trustStorePassword",TrustStore.PASSWD.VALUE);
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-9s [%2$-50s]  %5$s%6$s%n");
		
		//  Setup logger.
		
		tlog = Logger.getLogger("Typh Logger");
		tlog.setUseParentHandlers(false);
		try {
			(new File(Resources.LOGPATH.value)).getParentFile().mkdirs();
			FileHandler logfile = new FileHandler(Resources.LOGPATH.value);
			logfile.setFormatter(new SimpleFormatter());
			tlog.addHandler(logfile);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		tlog.log(Level.INFO, "Starting Typh Server ...");
		
		//  Determine the platform (although runs only on windows)
		
		if (System.getProperty("os.name").substring(0, 6).toLowerCase().matches("windows*"))
			os = OS.WINDOWS;
		else
			os = OS.LINUX;

		if (arg.length == 0 || arg.length >3){
			System.out.println("\nERROR:\tInvalid Number of Arguments !");
			displayHelp();
			System.exit(-1);
		}
		
		switch (arg[0]) {
		case "start":
			if (arg.length == 3) {
				arg[2] = arg[2].replaceAll("\\\\", "\\\\\\\\");
			
				if (!isServerRunning())
					startServer(arg[2]);
				else
					System.out.println("ERROR:\t Server is already running");

			} else if(arg.length == 1){

				if (!isServerRunning()){
					File testtyph = new File(Resources.CONFIGURATION_FILE.value);
					if(testtyph.exists() && testtyph.isFile())
						startServer(Resources.CONFIGURATION_FILE.value);
					else
						System.out.println("ERROR:\tNo configuration file found");
				}else
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
			if (isServerRunning())
				serverStatus();
			else
				System.out.println("ERROR:\t Server is not running");
			break;
		case "help":
			displayHelp();

			break;
		case "restart":
			restartServer(arg[1]);
			break;
		default:
			System.out.println("ERROR: Invalid argument");
		}
	}

	static void displayHelp() {
		int numberOfCharacters = 85;
		char[] character = new char[numberOfCharacters];
		Arrays.fill(character, '=');

		System.out.println("\n" + new String(character));
		System.out.println("\n\tTyph Server ");
		System.out.printf("\n[1] %-25s : %s", "start", "To start a server with config file in same directory");
		System.out.printf("\n    %-25s : %s", "= start config <file>", "<file> is a configuration file. Provide full path");
		System.out.printf("\n[2] %-25s : %s", "stop-server", "stop the server");
		System.out.printf("\n[3] %-25s : %s", "status", "get information about the server");
		System.out.printf("\n[4] %-25s : %s", "restart", "restart the server");
		System.out.printf("\n[5] %-25s : %s", "help", "print this message");
		System.out.println("\n\n" + new String(character));
	}

	/*
	 * This method checks whether the Database is running or not.
	 * @return A boolean corresponding to state of database process.
	 */
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

		}
		return exist;
	}

	/*
	 * This method restarts the application
	 * @param config The configuration to load the application.
	 */
	private static void restartServer(String config) {
		stopServer();
		startServer(config);
	}

	/*
	 * This method starts the database application.
	 * @param config The configuration to load the application.
	 */
	private static void startServer(String config) {
		if (os == OS.WINDOWS)
			startWServer(config);

	}

	/*
	 * This method retrieves status information of the database.
	 */
	private static void serverStatus() {

		try {
			Process stats = Runtime.getRuntime().exec("mongo --ssl --port 24000 --sslPEMKeyFile " + Resources.CERTIFICATE_PATH.value
					+ " --sslPEMKeyPassword " + Resources.CERTIFICATE_PASSWORD.value + " --sslAllowInvalidHostnames --host localhost --eval db.serverStatus()");
			BufferedReader bf = new BufferedReader(new InputStreamReader(stats.getInputStream()));
			String line = null;
			while ((line = bf.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * This method stops the database application.
	 */
	private static void stopServer() {
		try {
			if (os == OS.WINDOWS) {
				Runtime.getRuntime().exec("cmd /c sc stop typhserver");

			} 
			Typh.tlog.log(Level.INFO,"Server stopped successfully");
			System.out.println("INFO:\tServer stopped successfully.");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	/*
	 * This method is used to start db application on windows platform.
	 * @param config The configuration file.
	 */
	private static void startWServer(String config) {
		try {
			displayLoadingMessage();
			boolean isTyphServiceCreated = false;
			Process process = Runtime.getRuntime().exec("cmd /c sc query state= all | findstr Typh");
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

				process = Runtime.getRuntime().exec("cmd /k sc create typhserver  DisplayName= Typh binPath= \"mongod.exe --config \""+config+"\" --logpath=\""
								+ System.getProperty("user.dir") + "\\sys\\mongoLogs\\mongo.log\" --service\"");
				
				process.waitFor();
				
				process=Runtime.getRuntime().exec("cmd /k sc description typhserver \"Typh Server Database - MongoDB\"");
				process.waitFor();

				process = Runtime.getRuntime().exec("cmd /c sc start typhserver");
			} else
				process = Runtime.getRuntime().exec("cmd /c sc start typhserver");
					
			userList = new ArrayList<String>();
			
			Thread user = new Thread(new HeartForUsers());
			Thread admin = new Thread(new HeartForAdmin());
			Thread check = new Thread(new CheckUser());
			Thread networkTest = new Thread(new NetworkTest());
			
			check.setDaemon(true);
			networkTest.setDaemon(true);	
			
			user.start();
			admin.start();
			check.start();
			networkTest.start();
			
			
		

			Typh.tlog.log(Level.INFO,"Server started successfully.");
			System.out.println("INFO:\tServer started successfully.");
		
			Runtime.getRuntime().addShutdownHook(new Thread(){
				public void run(){
					stopServer();
					Typh.tlog.log(Level.INFO,"Server terminated.");
					System.out.println("INFO:\tServer terminated.");

				}
			});
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void displayLoadingMessage() {
		System.out.print("\nInitializing ...  ");
		for(int i=0;i<4;i++){
			try{
				System.out.printf("\b-");
				Thread.sleep(400);
				System.out.printf("\b\\");
				Thread.sleep(400);
				System.out.printf("\b|");
				Thread.sleep(400);
				System.out.printf("\b/");
				Thread.sleep(400);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		System.out.print("\n");
	}
}
