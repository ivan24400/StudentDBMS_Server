package ivn.typh.server;

import java.io.File;

/*
 * This enum stores path of ssl and mongodb configuration files.
 */
public enum Credential {

	CERTIFICATE_PATH(System.getProperty("user.dir") + File.separator+"sys"+File.separator+"server.pem"),
	
	CERTIFICATE_PASSWORD("server"),
	
	CONFIGURATION_FILE(System.getProperty("user.dir")+ File.separator+"sys"+ File.separator+"typh.cfg");
	
	String value;
	
	Credential(String data){
		this.value = data;
	}
}
