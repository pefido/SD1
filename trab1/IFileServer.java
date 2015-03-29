package trab1;

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

  /**
   * Devolve informacao sobre ficheiro.
   */
  public FileInfo getFileInfo(String path, String name) throws RemoteException, InfoNotFoundException;

  /**
   * transferir um ficheiro do server
   */
  public byte[] transferFile(String path, String name) throws RemoteException, InfoNotFoundException, IOException;

  public void makeDir(String name) throws SecurityException, RemoteException;

  public String removeDir(String name) throws SecurityException, RemoteException;

}
