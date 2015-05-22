package trab2;

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;

public class FileServerR {

  private String name;
  private HashMap<String, FileServerA> serversA;

  public FileServerR(String name) {
    this.name = name;
    this.serversA = new HashMap<String, FileServerA>();
    //this.serversA = new LinkedList<FileServerA>();
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

  public void addServer(FileServerA server) {
    serversA.put(server.getAdress(), server);
  }

  public boolean removeServer(String adress) {
    serversA.get(adress);
    boolean exists = true;
    if (serversA.size() == 0)
      exists = false;
    return exists;
  }

}
