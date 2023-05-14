package fr.ensimag.deca.tools;

import fr.ensimag.deca.tree.Location;


public class DecacWarning{
  Location location;
  String message;
  String erreur;

  public DecacWarning(Location location,String message,String erreur){
    this.location = location;
    this.message = message;
    this.erreur = erreur;
  }

  public Location getLocation(){
    return location;
  }

  public String getMessage(){
    return message;
  }

  public String getErreur(){
    return erreur;
  }


  public void display() {
      Location loc = getLocation();
      String line;
      String column;
      if (loc == null) {
          line = "<unknown>";
          column = "";
      } else {
          line = Integer.toString(loc.getLine());
          column = ":" + loc.getPositionInLine();
      }
      System.out.println(location.getFilename() + ":" + line + column + ": " + "[WARNING]" + getErreur() + ": " + getMessage());
  }


}
