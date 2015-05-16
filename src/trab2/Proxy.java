package trab2;

import java.net.InetAddress;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.util.*;
import java.io.*;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.*;
import org.scribe.builder.*;
import org.scribe.builder.api.*;
import org.scribe.model.*;
import org.scribe.oauth.*;

public class Proxy extends UnicastRemoteObject implements IFileServer {

  private static final long serialVersionUID = 1L;

  private String basePathName;
  private File basePath;
  private static OAuthService service;
  private static Token accessToken;

  // Informação para ligar à API da drop
  private static final String API_KEY = "vmprbxiq1dy4gef";
  private static final String API_SECRET = "nmbdy6fttf7nkaz";
  private static final String SCOPE = "dropbox";
  private static final String AUTHORIZE_URL = "https://www.dropbox.com/1/oauth/authorize?oauth_token=";

  protected Proxy() throws RemoteException {
    super();
  }

  protected Proxy(String pathname) throws RemoteException {
    super();
    this.basePathName = pathname;
    basePath = new File(pathname);
  }

  public String[] dir(String path) throws RemoteException, InfoNotFoundException {
    ArrayList<String> tmp = new ArrayList<String>();
    String[] cenas = null;
    try {
      String tmpS;
      if(path.equals("."))
        tmpS = "https://api.dropbox.com/1/metadata/dropbox/?list=true";
      else tmpS = "https://api.dropbox.com/1/metadata/dropbox/" + path + "/?list=true";
      OAuthRequest request = new OAuthRequest(Verb.GET, tmpS);
      service.signRequest(accessToken, request);
      Response response = request.send();

      if (response.getCode() != 200)
        throw new RuntimeException("Metadata response code:" + response.getCode());

      JSONParser parser = new JSONParser();
      JSONObject res = (JSONObject) parser.parse(response.getBody());

      JSONArray items = (JSONArray) res.get("contents");
      Iterator it = items.iterator();
      while (it.hasNext()) {
        JSONObject file = (JSONObject) it.next();
        tmp.add((String)file.get("path"));
        System.out.println(file.get("path"));
      }
      cenas = new String[tmp.size()];
    } catch (Exception e) {
      e.printStackTrace();
    }
    return tmp.toArray(cenas);
  }

  public byte[] cpFrom(String path, String name) throws InfoNotFoundException, IOException{
    return null;

  }

  public void cpTo(String path, String name, byte[] cpFile) throws InfoNotFoundException, IOException{

  }

  public void rm(String path) throws InfoNotFoundException, IOException{
    try{
      OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.dropbox.com/1/fileops/delete");
      request.addBodyParameter("root", "auto");
      request.addBodyParameter("path", path);
      service.signRequest(accessToken, request);
      Response response = request.send();
      if (response.getCode() != 200)
        throw new RuntimeException("Metadata response code:" + response.getCode());
    }catch (Exception e) {
      e.printStackTrace();
    }
    return "file " + path + " removed";
  }

  public void makeDir(String path) throws SecurityException, RemoteException {
    try{
      OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.dropbox.com/1/fileops/create_folder");
      request.addBodyParameter("root", "auto");
      request.addBodyParameter("path", path);
      service.signRequest(accessToken, request);
      Response response = request.send();
      if (response.getCode() != 200)
        throw new RuntimeException("Metadata response code:" + response.getCode());
    }catch (Exception e) {
      e.printStackTrace();
    }

  }

  public String removeDir(String path) throws SecurityException, RemoteException {
    try{
      OAuthRequest request = new OAuthRequest(Verb.POST, "https://api.dropbox.com/1/fileops/delete");
      request.addBodyParameter("root", "auto");
      request.addBodyParameter("path", path);
      service.signRequest(accessToken, request);
      Response response = request.send();
      if (response.getCode() != 200)
        throw new RuntimeException("Metadata response code:" + response.getCode());
    }catch (Exception e) {
      e.printStackTrace();
    }
    return "dir " + path + " removed";

  }

  public String[] getAttr(String path) throws RemoteException, InfoNotFoundException {
    String[] info = new String[4];
    try {
      OAuthRequest request = new OAuthRequest(Verb.GET, "https://api.dropbox.com/1/metadata/dropbox/" + path + "/?list=true");
      service.signRequest(accessToken, request);
      Response response = request.send();

      if (response.getCode() != 200)
        throw new RuntimeException("Metadata response code:" + response.getCode());

      JSONParser parser = new JSONParser();
      JSONObject res = (JSONObject) parser.parse(response.getBody());
      info[0] = (String)res.get("path");
      info[1] = (String)res.get("size");
      if(res.get("is_dir").toString().equals("false"))
        info[2] = "true";
      else
        info[2] = "false";
      info[3] = (String)res.get("modified");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return info;

  }

  public String isAlive() throws RemoteException {
    return "potato";
  }

  public static void main(String args[]) throws Exception {
    try {
      String path = ".";
      if (args.length != 2) {
        System.out.println("Use: java Proxy server_name contact_server_URL");
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

      Proxy server = new Proxy(path);
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

      // LIGAR À LÀ DROP, YO!
      service = new ServiceBuilder().provider(DropBoxApi.class).apiKey(API_KEY)
        .apiSecret(API_SECRET).scope(SCOPE).build();
      Scanner in = new Scanner(System.in);

      // Obter Request token
      Token requestToken = service.getRequestToken();

      System.out.println("Tem de obter autorizacao para a aplicacao continuar acedendo ao link:");
      System.out.println(AUTHORIZE_URL + requestToken.getToken());
      System.out.println("E carregar em enter quando der autorizacao");
      System.out.print(">>");
      Verifier verifier = new Verifier(in.nextLine());

      // O Dropbox usa como verifier o mesmo segredo do request token, ao
      // contrario de outros
      // sistemas, que usam um codigo fornecido na pagina web
      // Com esses sistemas a linha abaixo esta a mais
      verifier = new Verifier(requestToken.getSecret());
      // Obter access token
      accessToken = service.getAccessToken(requestToken, verifier);

    } catch (Throwable th) {
      th.printStackTrace();
    }
  }

}
