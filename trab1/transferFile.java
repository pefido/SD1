package trab1;

import java.io.File;
import java.io.RandomAccessFile;
import java.rmi.Naming;

public class transferFile {

	public static void main(String[] args) {
		if( args.length != 3) {
        	System.out.println( "Use: java GetFileInfo server_host path name");
        	System.exit(0);
        }
        String serverHost = args[0];
        String path = args[1];
        String name = args[2];
    	
		try {
			IFileServer server = (IFileServer) Naming.lookup("//" + serverHost + "/myFileServer");
			byte[] ficheiro = server.transferFile(path, name);
			RandomAccessFile f3 = new RandomAccessFile(name, "rw");
			f3.write(ficheiro);
			f3.close();
			System.out.println("ficheiro transferido!!!");
		} catch( Exception e) {
			System.err.println( "Erro: " + e.getMessage());
		}

	}

}
