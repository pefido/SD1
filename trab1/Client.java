package trab1;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Client {
  private static String basePath = "./client";

  public static void getServers(IContactServer contactServer) throws RemoteException {
    String[] servers = contactServer.getFileServers();
    String result = "servers online:";
    for (String a : servers)
      result += " " + a + " ";
    System.out.println(result);
  }

  public static void getServersWSN(IContactServer contactServer, String serverName) throws RemoteException {
    String[] serversWSN = contactServer.getFileServerWSN(serverName);
    String result = "servers named " + serverName + ":";
    for (String a : serversWSN)
      result += " " + a + " ";
    System.out.println(result);
  }

  public static void mkdir(IContactServer contactServer, String serverName, String dirName) throws RemoteException, MalformedURLException, NotBoundException {
    String fileServerURL = contactServer.getFileServerURL(serverName);
    IFileServer fileServer = (IFileServer) Naming.lookup("//localhost" + fileServerURL);
    fileServer.makeDir(dirName);
    System.out.println("directory " + dirName + " created in the " + serverName + " server");
  }

  public static void rmdir(IContactServer contactServer, String serverName, String dirName) throws RemoteException, MalformedURLException, NotBoundException {
    String fileServerURL = contactServer.getFileServerURL(serverName);
    IFileServer fileServer = (IFileServer) Naming.lookup("//localhost" + fileServerURL);
    System.out.println(fileServer.removeDir(dirName));
  }

  public static void ls(IContactServer contactServer, String serverName, String dirName) throws RemoteException, MalformedURLException, NotBoundException, InfoNotFoundException {
    String fileServerURL = contactServer.getFileServerURL(serverName);
    IFileServer fileServer = (IFileServer) Naming.lookup("//localhost" + fileServerURL);
    String[] tmp = fileServer.dir(dirName);
    String result = "";
    for (String a : tmp) {
      result += a + " ";
    }
    System.out.println(result);
  }
  
  public static void cpFrom(IContactServer contactServer, String serverName, String pathFrom, String pathTo, String fileName) throws RemoteException, MalformedURLException, NotBoundException, InfoNotFoundException{
    String fileServerURL = contactServer.getFileServerURL(serverName);
    IFileServer fileServer = (IFileServer) Naming.lookup("//localhost" + fileServerURL);
    try {
      byte[] tmp = fileServer.cpFrom(pathFrom, fileName);
      File dir = new File(basePath, pathTo);
      if(!dir.exists())
        dir.mkdir();
      File f = new File(dir, fileName);
      if(!f.exists()){
        f.createNewFile();
        RandomAccessFile f2 = new RandomAccessFile(basePath + "/" + pathTo + "/" + fileName, "rw");
        f2.write(tmp);
        f2.close();
        System.out.println("ficheiro " + fileName + " transferido para " + pathTo);
      }
      else throw new InfoNotFoundException("File " + fileName + " alredy exists!");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public static void rm(IContactServer contactServer, String serverName, String path) throws NotBoundException, InfoNotFoundException, IOException{
    String fileServerURL = contactServer.getFileServerURL(serverName);
    IFileServer fileServer = (IFileServer) Naming.lookup("//localhost" + fileServerURL);
    fileServer.rm(path);
    System.out.println(path + " removido!");
  }
  
  public static void rmLocal(String path) throws InfoNotFoundException{
    File f = new File(basePath, path);
    if(f.exists()){
      f.delete();
      System.out.println(path + " removido!");
    }
    else throw new InfoNotFoundException("File " + path + " does not exists!");
  }
  
  public static void cpTo(IContactServer contactServer, String serverName, String pathFrom, String pathTo, String fileName) throws NotBoundException, InfoNotFoundException, IOException{
    String fileServerURL = contactServer.getFileServerURL(serverName);
    IFileServer fileServer = (IFileServer) Naming.lookup("//localhost" + fileServerURL);
    
    File dir = new File(basePath, pathFrom);
    if (dir.exists()) {
      File f = new File(dir, fileName);
      if (f.exists()) {
        RandomAccessFile f2 = new RandomAccessFile(basePath + "/" + pathFrom + "/" + fileName, "r");
        byte[] b = new byte[(int) f2.length()];
        f2.readFully(b);
        f2.close();
        fileServer.cpTo(pathTo, fileName, b);
        System.out.println("ficheiro " + fileName + " enviado para " + serverName + "@" + pathTo);
      } else
        throw new InfoNotFoundException("File not found :" + fileName);
    } else
      throw new InfoNotFoundException("Directory not found :" + pathFrom);
  }

  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Use: java Client contact_server_URL");
      System.exit(0);
    }
    String contactServerName = args[0];

    try {
      IContactServer contactServer = (IContactServer) Naming.lookup("//" + contactServerName);

      Scanner sc = new Scanner(System.in);
      String command = "";
      boolean running = true;
      while (running) {
        System.out.print("> ");
        command = sc.nextLine();
        if (command.equals("servers"))
          getServers(contactServer);
        else if(command.equals("exit"))
          running = false;
        else if (command.contains("servers"))
          getServersWSN(contactServer, command.substring(8));
        else if (command.contains("mkdir")) {
          command = command.substring(6);
          String[] tmp = command.split("@");
          mkdir(contactServer, tmp[0], tmp[1]);
        } else if (command.contains("rmdir")) {
          command = command.substring(6);
          String[] tmp = command.split("@");
          rmdir(contactServer, tmp[0], tmp[1]);
        } else if (command.contains("ls")) {
          command = command.substring(3);
          String[] tmp = command.split("@");
          ls(contactServer, tmp[0], tmp[1]);
        } else if(command.contains("cp")){
          command = command.substring(3);
          String[] path = command.split(" ");
          if(path[0].contains("@")){
            String[] serverPath = path[0].split("@");
            String[] filePath = serverPath[1].split("/");
            cpFrom(contactServer, serverPath[0], filePath[0], path[1], filePath[1]);
          }
          else{
            String[] serverPath = path[1].split("@");
            String[] filePath = path[0].split("/");
            cpTo(contactServer, serverPath[0], filePath[0], serverPath[1], filePath[1]);
          }                      /*server      from          to            nome*/
        } else if(command.contains("rm")){
          command = command.substring(3);
          if(command.contains("@")){
            String[] tmp = command.split("@");
            rm(contactServer, tmp[0], tmp[1]);
          }
          else{
            rmLocal(command);
          }
        }
        else System.out.println("invalid command");

      }

    } catch (Exception e) {
      System.err.println("Erro: " + e.getMessage());
    }

  }

}
