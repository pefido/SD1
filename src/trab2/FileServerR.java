package trab2;

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

public class FileServerR {

  private String name;
  private FileServerA prim;
  private HashMap<String, FileServerA> serversA;

  public FileServerR(String name) {
    this.name = name;
    this.serversA = new HashMap<String, FileServerA>();
  }

  public String[] getServersA() {
    return serversA.keySet().toArray(new String[0]);
  }

  public String getServerName() {
    return name;
  }

  public int getnServers() {
    return serversA.size();
  }

  public String getPrimary() {
    return prim.getAdress();
  }

  public Iterator teste() {
    return serversA.entrySet().iterator();
  }

  public String newPrimary() {
    Iterator it = serversA.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pair = (Map.Entry)it.next();
      if (! pair.getKey().equals(prim.getAdress())) {
        prim = (FileServerA)pair.getValue();
        return prim.getAdress();
      }
    }
    return null;
  }

  public void addServer(FileServerA server) {
    if (server.isprim())
      prim = server;
    serversA.put(server.getAdress(), server);
  }

  public boolean removeServer(String adress) {
    //serversA.get(adress);
    serversA.remove(adress);
    boolean exists = true;
    if (serversA.size() == 0)
      exists = false;
    return exists;
  }

}
