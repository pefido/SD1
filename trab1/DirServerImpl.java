package trab1;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;

public class DirServerImpl
  extends UnicastRemoteObject
  implements IFileServer
{
  protected DirServerImpl() throws RemoteException {
    super();
    // TODO Auto-generated constructor stub
  }

  private static final long serialVersionUID = 1L;

  private String basePathName;
  private File basePath;

  protected DirServerImpl( String pathname) throws RemoteException {
    super();
    this.basePathName = pathname;
    basePath = new File( pathname);
  }

  @Override
  public String[] dir(String path) throws RemoteException, InfoNotFoundException {
    File f = new File( basePath, path);
    if( f.exists())
      return f.list();
    else
      throw new InfoNotFoundException( "Directory not found :" + path);
  }

  @Override
  public FileInfo getFileInfo(String path, String name) throws RemoteException, InfoNotFoundException {
    File dir = new File( basePath, path);
    if( dir.exists()) {
      File f = new File( dir, name);
      if( f.exists())
        return new FileInfo( path, f.length(), new Date(f.lastModified()), f.isFile());
      else
        throw new InfoNotFoundException( "File not found :" + name);
    } else
      throw new InfoNotFoundException( "Directory not found :" + path);
  }

  public byte[] transferFile(String path, String name) throws InfoNotFoundException, IOException{
    File dir = new File( basePath, path);
    if( dir.exists()) {
      File f = new File( dir, name);
      if( f.exists()){
        RandomAccessFile f2 = new RandomAccessFile(path + "/" + name, "r");
        byte[] b = new byte[(int) f2.length()];
        f2.readFully(b);
        return b;
      }
      else
        throw new InfoNotFoundException( "File not found :" + name);
    } else
      throw new InfoNotFoundException( "Directory not found :" + path);
  }

  public static void main( String args[]) throws Exception {
    try {
      String path = ".";
     /* if( args.length > 0)
        path = args[0];*/
      if(args.length != 2){
    	  System.out.println("Use: java DirServerImpl server_name contact_server_URL");
    	  System.exit(0);
      }
      String serverName = args[0];
      String contactServerURL = args[1];

    	  File policy = new File("policy.all");
    	  if(policy.exists())
    		  System.getProperties().put( "java.security.policy", "policy.all");
    	  else System.getProperties().put( "java.security.policy", "src/policy.all");

      if( System.getSecurityManager() == null) {
        System.setSecurityManager( new RMISecurityManager());
      }

      try { // start rmiregistry
        LocateRegistry.createRegistry( 1099);
      } catch( RemoteException e) {
        // if not start it
        // do nothing - already started with rmiregistry
      }

      DirServerImpl server = new DirServerImpl( path);
      Naming.rebind( "/serverName", server);
      System.out.println( "DirServer bound in registry");
      
      //ligar ao contactServer
      try {
			IContactServer contactServer = (IContactServer) Naming.lookup("//" + contactServerURL + "/myContactServer");
			if(contactServer.addFileServer(serverName) == true)
				System.out.println("server ligado ao contact");
		} catch( Exception e) {
			System.err.println( "Erro: " + e.getMessage());
		}
      
    } catch( Throwable th) {
      th.printStackTrace();
    }
  }


}
