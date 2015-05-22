package trab2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.*;
import java.util.*;

public interface IFileServer extends Remote {
  /**
   * Lista nome de ficheiros num dado directorio
   */
  public String[] dir(String path) throws RemoteException, InfoNotFoundException;

  public void makeDir(String name) throws SecurityException, RemoteException;
  
  public String removeDir(String name) throws SecurityException, RemoteException;
  
  public byte[] cpFrom(String path, String name) throws InfoNotFoundException, IOException;
  
  public void cpTo(String path, String name, byte[] cpFile) throws InfoNotFoundException, IOException;
  
  public String rm(String path) throws InfoNotFoundException, IOException;
  
  public String[] getAttr(String path) throws RemoteException, InfoNotFoundException;
  
  public String isAlive() throws RemoteException;

}
