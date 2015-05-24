package trab2;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
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
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Random;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;

public class ContactServer extends UnicastRemoteObject implements IContactServer {

  private static Map<String, FileServerR> fileServers;

  public ContactServer() throws RemoteException {
    fileServers = new HashMap<String, FileServerR>();
  }

  public boolean addFileServer(String hostName, String serverName, String serverAdress) throws RemoteException , MalformedURLException , NotBoundException, InfoNotFoundException, IOException {
    if (fileServers.containsKey(serverName)) {
      FileServerA ns = new FileServerA(hostName + "/" + serverAdress, false);
      fileServers.get(serverName).addServer(ns);
      System.out.println(serverAdress + " adicionado como " + serverName + " (secundário)");
      sync(ns.getAdress(), fileServers.get(serverName).getPrimary());
      return false;
    }
    else {
      FileServerA ns = new FileServerA(hostName + "/" + serverAdress, true);
      FileServerR r = new FileServerR(serverName);
      r.addServer(ns);
      fileServers.put(serverName, r);
      System.out.println(serverAdress + " adicionado como " + serverName + " (primário)");
      return true;
    }
  }

  public void sync(String secondary, String primary) throws RemoteException, MalformedURLException, NotBoundException, InfoNotFoundException , IOException {
    System.out.println("Sincronizar o  " + primary + " com o " + secondary);
    IFileServer pserver = (IFileServer) Naming.lookup("//" + primary);
    IFileServer sserver = (IFileServer) Naming.lookup("//" + secondary);
    String[] tmp = pserver.dir(".");
    String result = "";
    for (String a : tmp) {
      //sserver.cpTo(".", a, pserver.cpFromSync(a));
      result += a + "\n";
    }
    System.out.println(result);
  }
  
  public void propagate(String serverName, String path, String operation) throws NotBoundException, InfoNotFoundException, IOException{
    IFileServer pserver = (IFileServer) Naming.lookup("//" + fileServers.get(serverName).getPrimary());
    //progagar operacao sobre o ficheiro path para todos os secundários
    String [] tmpServers = fileServers.get(serverName).getServersA();
    for(String a: tmpServers){
      if(!a.equals(fileServers.get(serverName).getPrimary())){
        IFileServer sserver = (IFileServer) Naming.lookup("//" + a);
        if(operation.equals("cpTo")){
          //System.out.println("aqui tao cenas: " + path);
          sserver.cpTo(".", path, pserver.cpFromSync("/" + path));
        }
        else if(operation.equals("rm")){
          sserver.rm(path);
        }
      }
    }
  }

  public String[] getFileServers() throws RemoteException {
    return fileServers.keySet().toArray(new String[0]);
  }

  public String[] getFileServerWSN(String serverName) throws RemoteException {
    return fileServers.get(serverName).getServersA();
  }

  public String getFileServerURLRandom(String name) throws RemoteException {
    Random rand = new Random();
    int tmp = rand.nextInt(fileServers.get(name).getnServers());
    return fileServers.get(name).getServersA()[tmp];
  }

  public String getFileServerPrimary(String name) throws RemoteException {
    return fileServers.get(name).getPrimary();
  }

  public static void main(String[] args) {

    try {
      String path = ".";
      if (args.length > 0)
        path = args[0];

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

      ContactServer server = new ContactServer();
      Naming.rebind("/myContactServer", server);
      System.out.println("ContactServer up in " + hostname);

      Thread keepAlive = new Thread(){
        public void run() {
          while (true){
            for(FileServerR a: fileServers.values()){
              for(String i: a.getServersA()){
                try{
                  IFileServer fileServer = (IFileServer) Naming.lookup("//" + i);
                  fileServer.isAlive();
                }catch(Exception death){
                  System.out.println(i + " is down");
                  if(a.removeServer(i) == false) {
                    System.out.println(a.getServerName() + " has no servers left");
                    fileServers.remove(a.getServerName());
                  }
                  else {
                    if(a.getPrimary().equals(i)) {
                      System.out.println(a.newPrimary() + " is the new primary "+a.getServerName()+ " server");
                    }
                  }
                }
                try {
                  sleep(1000);
                } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
              if(a.getnServers() == 0) {
                fileServers.remove(a.getServerName());
              }
            }
            try {
              sleep(5000);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          }
      }
      };
      keepAlive.start();

    } catch (Throwable th) {
      th.printStackTrace();
    }

  }

}
