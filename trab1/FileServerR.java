package trab1;

import java.util.LinkedList;
import java.util.List;

public class FileServerR {

  private String name;
  private List<String> serversA;

  public FileServerR(String name, String serverA) {
    this.name = name;
    this.serversA = new LinkedList<String>();
    this.serversA.add(serverA);
  }

  public String[] getServersA() {
    return serversA.toArray(new String[0]);
  }

  public String getServerName() {
    return name;
  }

  public int getnServers() {
    return serversA.size();
  }

  public void addServer(String adress) {
    serversA.add(adress);
  }

  public boolean removeServer(String adress) {
    for (String a : serversA) {
      if (a.equals(adress))
        serversA.remove(a);
    }
    boolean exists = true;
    if (serversA.size() == 0)
      exists = false;
    return exists;
  }

}
