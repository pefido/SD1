package trab1;

import java.io.RandomAccessFile;
import java.rmi.Naming;

public class Client {

	public static void main(String[] args) {
		if( args.length != 1) {
        	System.out.println( "Use: java Client contact_server_URL");
        	System.exit(0);
        }
        String contactServerName = args[0];
    	
		try {
			IContactServer contactServer = (IContactServer) Naming.lookup("//" + contactServerName);
			String[] servers = (String[])contactServer.getFileServers();
			System.out.println("servers:" + servers.toString());
		} catch( Exception e) {
			System.err.println( "Erro: " + e.getMessage());
		}

	}

}
