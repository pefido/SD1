package trab2;

import java.net.InetAddress;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;

public class DirServerImpl extends UnicastRemoteObject implements IFileServer {
  protected DirServerImpl() throws RemoteException {
    super();
    // TODO Auto-generated constructor stub
  }

  private static final long serialVersionUID = 1L;

  private String basePathName;
  private File basePath;

  protected DirServerImpl(String pathname) throws RemoteException {
    super();
    this.basePathName = pathname;
    basePath = new File(pathname);
  }

  @Override
  public String[] dir(String path) throws RemoteException, InfoNotFoundException {
    File f = new File(basePath, path);
    if (f.exists())
      return f.list();
    else
      throw new InfoNotFoundException("Directory not found :" + path);
  }

  public byte[] cpFrom(String path, String name) throws InfoNotFoundException, IOException{
    File dir = new File(basePath, path);
    if (dir.exists()) {
      File f = new File(dir, name);
      if (f.exists()) {
        RandomAccessFile f2 = new RandomAccessFile(path + "/" + name, "r");
        byte[] b = new byte[(int) f2.length()];
        f2.readFully(b);
        f2.close();
        return b;
      } else
        throw new InfoNotFoundException("File not found :" + name);
    } else
      throw new InfoNotFoundException("Directory not found :" + path);
  }

  public void cpTo(String path, String name, byte[] cpFile) throws InfoNotFoundException, IOException{
    File dir = new File(basePath, path);
    if(dir.exists()){
      File f = new File(dir, name);
      if(!f.exists()){
        RandomAccessFile f2 = new RandomAccessFile(path + "/" + name, "rw");
        f2.write(cpFile);
        f2.close();
      }
      else throw new InfoNotFoundException("File " + name + " alredy exists!");
    }
    else throw new InfoNotFoundException("Directory not found :" + path);
  }

  public String rm(String path) throws InfoNotFoundException, IOException{
    File f = new File(basePath, path);
    if(f.exists()){
      f.delete();
    }
    else throw new InfoNotFoundException("File " + path + " does not exists!");
    return "";
  }

  public void makeDir(String name) throws SecurityException, RemoteException {
    File dir = new File(name);
    if (!dir.exists()) {
      dir.mkdir();
      System.out.println("created directory: " + name);
    } else
      System.out.println("directory " + name + " alredy exists!");
  }

  public String removeDir(String name) throws SecurityException, RemoteException {
    String result = "";
    File dir = new File(name);
    if (dir.exists()) {
      if (dir.list().length > 0) {
        result = "the directory is not empty!";
        System.out.println(result);
      } else {
        dir.delete();
        result = "directory " + name + " has been removed";
        System.out.println(result);
      }
    } else {
      result = "directory " + name + " does not exist!";
      System.out.println(result);
    }
    return result;
  }

  public String[] getAttr(String path) throws RemoteException, InfoNotFoundException {
      File f = new File(path);
      if (f.exists())
        //return new FileInfo(path, f.length(), new Date(f.lastModified()), f.isFile());
        return null;
      else
        throw new InfoNotFoundException("File not found :" + path);
  }

  public String isAlive() throws RemoteException{
    return "potato";
  }

  public static void main(String args[]) throws Exception {
    try {
      String path = "./local";
      if (args.length != 2) {
        System.out.println("Use: java DirServerImpl server_name contact_server_URL");
        System.exit(0);
      }
      String serverName = args[0];
      String contactServerURL = args[1];

      File policy = new File("policy.all");
      if (policy.exists())
        System.getProperties().put("java.security.policy", "policy.all");
      else
        System.getProperties().put("java.security.policy", "src/policy.all");

      if (System.getSecurityManager() == null) {
        System.setSecurityManager(new RMISecurityManager());
      }

      String hostname = InetAddress.getLocalHost().getCanonicalHostName();
      try { // start rmiregistry
        System.setProperty("java.rmi.server.hostname", hostname);
        LocateRegistry.createRegistry(1099);
      } catch (RemoteException e) {
        // if not start it
        // do nothing - already started with rmiregistry
      }

      DirServerImpl server = new DirServerImpl(path);
      String adress = serverName + System.currentTimeMillis();
      Naming.rebind(adress, server);
      System.out.println("DirServer bound in registry");

      // ligar ao contactServer
      try {
        IContactServer contactServer = (IContactServer) Naming.lookup("//" + contactServerURL + "/myContactServer");
        if (contactServer.addFileServer(hostname, serverName, adress) == true)
          System.out.println("server ligado ao contact");
      } catch (Exception e) {
        System.err.println("Erro: " + e.getMessage());
      }

    } catch (Throwable th) {
      th.printStackTrace();
    }
  }

}
