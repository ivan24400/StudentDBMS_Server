package ivn.typh.server;

/*
 * This enum consists of port numbers used by the server application.
 */

public enum PortList {

	USER(61000),
	
	ADMIN(61001),
	
	NETWORKTEST(61002),
	
	CHECKUSER(61003);
	
	int port;
	
	PortList(int p){
		this.port=p;
	}
}
