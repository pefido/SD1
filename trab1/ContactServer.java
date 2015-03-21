package trab1;

import java.io.File;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ContactServer extends UnicastRemoteObject implements IContactServer{
	
	private Map<String, FileServerR> fileServers;
	
	public ContactServer() throws RemoteException{
		fileServers = new HashMap<String, FileServerR>();
	}
	
	public boolean addFileServer(String serverName, String serverAdress) throws RemoteException{
		if(fileServers.containsKey(serverName))
			fileServers.get(serverName).addServer(serverAdress);
		else 
			fileServers.put(serverName, new FileServerR(serverName, serverAdress));
		System.out.println(serverAdress + " adicionado como " + serverName);
		return true;
	}
	
	public String[] getFileServers() throws RemoteException{
		return fileServers.keySet().toArray(new String[0]);
	}
	
	public String[] getFileServerWSN(String serverName) throws RemoteException{
		return fileServers.get(serverName).getServersA();
	}
	
	

	public static void main(String[] args) {
		
		try {
		      String path = ".";
		      if( args.length > 0)
		        path = args[0];

		    	  File policy = new File("policy.all");
		    	  if(policy.exists())
		    		  System.getProperties().put( "java.security.policy", "policy.all");
		    	  else System.getProperties().put( "java.security.policy", "src/policy.all");

		      if( System.getSecurityManager() == null) {
		        System.setSecurityManager( new RMISecurityManager());
		      }

		      try { // start rmiregistry
		        LocateRegistry.createRegistry(1099);
		      } catch( RemoteException e) {
		        // if not start it
		        // do nothing - already started with rmiregistry
		      }

		      ContactServer server = new ContactServer();
		      Naming.rebind( "/myContactServer", server);
		      System.out.println( "ContactServer up");
		    } catch( Throwable th) {
		      th.printStackTrace();
		    }

	}

}
