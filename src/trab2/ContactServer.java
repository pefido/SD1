package trab2;

import java.io.File;
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
import java.util.Random;

public class ContactServer extends UnicastRemoteObject implements IContactServer {

  private static Map<String, FileServerR> fileServers;

  public ContactServer() throws RemoteException {
    fileServers = new HashMap<String, FileServerR>();
  }

  public boolean addFileServer(String hostName, String serverName, String serverAdress) throws RemoteException {
    if (fileServers.containsKey(serverName))
      fileServers.get(serverName).addServer(hostName + "/" + serverAdress);
    else
      fileServers.put(serverName, new FileServerR(serverName, hostName + "/" + serverAdress));
    System.out.println(serverAdress + " adicionado como " + serverName);
    return true;
  }

  public String[] getFileServers() throws RemoteException {
    return fileServers.keySet().toArray(new String[0]);
  }

  public String[] getFileServerWSN(String serverName) throws RemoteException {
    return fileServers.get(serverName).getServersA();
  }

  public String getFileServerURL(String name) throws RemoteException {
    Random rand = new Random();
    int tmp = rand.nextInt(fileServers.get(name).getnServers());
    return fileServers.get(name).getServersA()[tmp];
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
                  if(a.removeServer(i) == false)
                    fileServers.remove(a.getServerName());
                  System.out.println(i + " is dead");
                }
                try {
                  sleep(1000);
                } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
              if(a.getnServers() == 0)
                fileServers.remove(a.getServerName());
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
