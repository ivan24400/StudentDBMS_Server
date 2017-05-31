package ivn.typh.server;

import java.io.File;

/*
 * This enum contains trustore values
 */

public enum TrustStore {

	PATH(System.getProperty("user.dir")+File.separator+"sys"+File.separator+"typh.ks"),
	PASSWD("keystore");
	
	String VALUE;
	
	TrustStore(String t){
		this.VALUE=t;
	}
}
