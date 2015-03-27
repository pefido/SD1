package trab1;

import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client {

  public static void getServers(IContactServer contactServer) throws RemoteException{
    String[] servers = contactServer.getFileServers();
    String result = "servers online:";
    for(String a: servers)
      result += " " + a + " ";
    System.out.println(result);
  }

  public static void getServersWSN(IContactServer contactServer, String serverName) throws RemoteException{
    String[] serversWSN = contactServer.getFileServerWSN(serverName);
    String result = "servers named " + serverName + ":";
    for(String a: serversWSN)
      result += " " + a + " ";
    System.out.println(result);
  }

  public static void mkdir(IContactServer contactServer, String serverName, String dirName) throws RemoteException, MalformedURLException, NotBoundException{
    String fileServerURL = contactServer.getFileServerURL(serverName);
    IFileServer fileServer = (IFileServer)Naming.lookup("//localhost" + fileServerURL);
    fileServer.makeDir(dirName);
    System.out.println("directory " + dirName + " created in the " + serverName + " server");
  }

  public static void rmdir(IContactServer contactServer, String serverName, String dirName) throws RemoteException, MalformedURLException, NotBoundException{
    String fileServerURL = contactServer.getFileServerURL(serverName);
    IFileServer fileServer = (IFileServer)Naming.lookup("//localhost" + fileServerURL);
    System.out.println(fileServer.removeDir(dirName));
  }

  public static void ls(IContactServer contactServer, String serverName, String dirName) throws RemoteException, MalformedURLException, NotBoundException, InfoNotFoundException{
    String fileServerURL = contactServer.getFileServerURL(serverName);
    IFileServer fileServer = (IFileServer)Naming.lookup("//localhost" + fileServerURL);
    String[] tmp = fileServer.dir(dirName);
    String result = "";
    for(String a: tmp){
      result += a + " ";
    }
    System.out.println(result);
  }

  public static void main(String[] args) {
    if( args.length != 1) {
      System.out.println( "Use: java Client contact_server_URL");
      System.exit(0);
    }
    String contactServerName = args[0];

    try {
      IContactServer contactServer = (IContactServer) Naming.lookup("//" + contactServerName);

      Scanner sc = new Scanner(System.in);
      String command = "";
      boolean running = true;
      while(running){
        System.out.print("> ");
        command = sc.nextLine();
        switch (command){
          case "servers" :
            getServers(contactServer);
            break;
          case "exit": running = false;
                       break;
          default: ;
                   if(command.contains("servers"))
                     getServersWSN(contactServer, command.substring(8));
                   else if(command.contains("mkdir")){
                     command = command.substring(6);
                     String[] tmp = command.split("@");
                     mkdir(contactServer, tmp[0], tmp[1]);
                   }
                   else if(command.contains("rmdir")){
                     command = command.substring(6);
                     String[] tmp = command.split("@");
                     rmdir(contactServer, tmp[0], tmp[1]);
                   }
                   else if(command.contains("ls")){
                     command = command.substring(3);
                     String[] tmp = command.split("@");
                     ls(contactServer, tmp[0], tmp[1]);
                   }
                   else
                     System.out.println("invalid command");
                   break;
        }


      }

    } catch( Exception e) {
      System.err.println( "Erro: " + e.getMessage());
    }

  }

}
