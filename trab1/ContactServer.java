package trab1;

import java.io.File;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;

public class ContactServer extends UnicastRemoteObject implements IContactServer{
	
	private List<String> fileServers;
	
	public ContactServer() throws RemoteException{
		fileServers = new LinkedList<String>();
	}
	
	public boolean addFileServer(String serverName) throws RemoteException{
		fileServers.add(serverName);
		System.out.println("adicionado " + serverName);
		return true;
	}
	
	public Object[] getFileServers() throws RemoteException{
		return fileServers.toArray();
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
