package ivn.typh.server;

public enum Credential {

	CERTIFICATE_PATH(System.getProperty("user.dir") + "\\cert\\server.pem"),
	
	CERTIFICATE_PASSWORD("server"),
	
	CONFIGURATION_FILE(System.getProperty("user.dir")+"\\typh.cfg");
	
	String value;
	
	Credential(String data){
		this.value = data;
	}
}
