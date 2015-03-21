package trab1;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IContactServer extends Remote{
	
	public boolean addFileServer(String serverName) throws RemoteException;
	
	public String[] getFileServers() throws RemoteException;
	
	

}
