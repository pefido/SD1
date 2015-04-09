package trab1;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IContactServer extends Remote {

  public boolean addFileServer(String hostName, String serverName, String serverAdress) throws RemoteException;

  public String[] getFileServers() throws RemoteException;

  public String[] getFileServerWSN(String serverName) throws RemoteException;

  public String getFileServerURL(String name) throws RemoteException;

}
