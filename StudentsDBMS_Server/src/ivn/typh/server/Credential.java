package ivn.typh.server;

/*
 * This enum stores path of ssl and mongodb configuration files.
 */
public enum Credential {

	CERTIFICATE_PATH(System.getProperty("user.dir") + "\\cert\\server.pem"),
	
	CERTIFICATE_PASSWORD("server"),
	
	CONFIGURATION_FILE(System.getProperty("user.dir")+"\\typh.cfg");
	
	String value;
	
	Credential(String data){
		this.value = data;
	}
}
