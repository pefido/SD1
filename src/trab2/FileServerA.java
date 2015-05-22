package trab2;

public class FileServerA {

  private String adress;
  private boolean isPrimary;

  public FileServerA(String adress, boolean isPrimary){
    this.adress = adress;
    this.isPrimary = isPrimary;
  }

  public String getAdress() {
    return adress;
  }

}
