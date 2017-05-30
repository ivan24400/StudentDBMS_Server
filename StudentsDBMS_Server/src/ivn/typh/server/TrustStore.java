package ivn.typh.server;

/*
 * This enum contains trustore values
 */

public enum TrustStore {

	PATH(System.getProperty("user.dir")+"\\typh.ks"),
	PASSWD("keystore");
	
	String VALUE;
	
	TrustStore(String t){
		this.VALUE=t;
	}
}
