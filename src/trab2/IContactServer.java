package trab2;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.net.MalformedURLException;
import java.io.*;

public interface IContactServer extends Remote {

  public boolean addFileServer(String hostName, String serverName, String serverAdress) throws RemoteException, MalformedURLException, NotBoundException, InfoNotFoundException, IOException;

  public String[] getFileServers() throws RemoteException;

  public String[] getFileServerWSN(String serverName) throws RemoteException;

  public String getFileServerURLRandom(String name) throws RemoteException;

  public String getFileServerPrimary(String name) throws RemoteException;

}
