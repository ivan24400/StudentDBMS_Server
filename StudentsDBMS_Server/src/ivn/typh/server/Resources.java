package ivn.typh.server;

import java.io.File;
import java.time.LocalDateTime;

/*
 * This enum stores path of ssl and mongodb configuration files.
 */
public enum Resources {

	CERTIFICATE_PATH(System.getProperty("user.dir") + File.separator+"sys"+File.separator+"server.pem"),
	
	CERTIFICATE_PASSWORD("server"),
	
	CONFIGURATION_FILE(System.getProperty("user.dir")+ File.separator+"sys"+ File.separator+"typh.cfg"),
	
	LOGPATH(System.getProperty("user.dir")+File.separator+"sys"+File.separator+"typhLogs"+File.separator+"typh__"+LocalDateTime.now().toString().split("T")[0]+"__"+LocalDateTime.now().toString().split("T")[1].replace(':', '-')+".log");
	
	String value;
	
	Resources(String data){
		this.value = data;
	}
}
