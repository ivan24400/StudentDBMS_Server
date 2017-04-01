package ivn.typh.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Typh {

	public static void main(String[] arg){
		
		int osFlag=9;
		
		//		Linux or Windows
		
		System.out.println(Arrays.toString(arg));
		if(System.getProperty("os.name").substring(0, 6).toLowerCase().matches("windows*"))
			osFlag=1;
		else
			osFlag=0;
		
		if(arg[0].equals("config") && arg.length==2){
			if(!isServerRunning(osFlag)){
			if(osFlag == 1)
				startWServer(arg[1]);
			else if(osFlag == 0)
				startLServer(arg[1]);
			}else{
				System.out.println("ERROR:\t Server is already running");
			}
			
		}else if(arg.length==1){
	
			switch(arg[0]){
			case "stop-server":
				if(isServerRunning(osFlag)){
					stopServer(osFlag);
				}else{
					System.out.println("ERROR:\t No server process found!");
				}
				break;
			case "status":
				serverStatus();
				break;
			case "help":
				int numberOfCharacters = 85;
				char[] character = new char[numberOfCharacters];
				Arrays.fill(character, '=');
				
				System.out.println("\n"+new String(character));
				System.out.println("\n\tTyph™ Server ");
				System.out.printf("\n[1] %-25s : %s","config <file>","<file> is a configuration file. Provide full path"); 
				System.out.printf("\n[2] %-25s : %s","stop-server","stop the server"); 
				System.out.printf("\n[3] %-25s : %s","status","get information about the server"); 
				System.out.printf("\n[4] %-25s : %s","restart","restart the server"); 
				System.out.printf("\n[5] %-25s : %s","help","print this message"); 
				System.out.println("\n\n"+new String(character));

				break;
			case "restart":
				restartServer();
				break;
			default:
				System.out.println("ERROR: Invalid argument");
			}
	}else{
		System.out.println("ERROR: Invalid number of arguments\n Use \'help\' to list all options");
	}
		
	}

	private static void restartServer() {
		// TODO Auto-generated method stub
		
	}

	private static void serverStatus() {
		// TODO Auto-generated method stub
		
	}

	private static void stopServer(int os) {
		try {
			if(os == 1){
				Runtime.getRuntime().exec("cmd /c taskkill /f /im mongod.exe");
			
		}else{
			
			// For linux
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static boolean isServerRunning(int flag) {
		boolean exist=false;
		if(flag == 1){
			try {
				String line;
				Process process = Runtime.getRuntime().exec("tasklist");
				BufferedReader bf = new BufferedReader(new InputStreamReader(process.getInputStream()));
				while((line=bf.readLine())!=null){
					if(line.matches("mongod*"))
						exist=true;
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
			
		}else{
			// For Linux
		}
		System.out.println("in server");
		return exist;
	}

	private static void startLServer(String config) {
		//	For Linux
	}

	private static void startWServer(String config) {
		try {
			System.out.println("windows server\t"+config+"\t"+System.getProperty("user.dir"));
			String[] arg = {"cmd.exe","/k", "mongod --config ",config};
			Runtime.getRuntime().exec("cmd /c start mongod --config "+System.getProperty("user.dir")+"\\"+config);
//			List<String> args = Arrays.asList("cmd","/k","start","mongod.bat");
//			ProcessBuilder pb = new ProcessBuilder(args);
//			pb.directory(new File(System.getProperty("user.dir")));
//			pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
