
package tikape.runko.domain;


public class Viesti {
   private int id;
   private String kirjoittaja;
   private String viesti;
   private String paivays;
 

    public Viesti(int id, String kirjoittaja, String viesti, String paivays) {
        this.id = id;
        this.kirjoittaja = kirjoittaja;
        this.viesti = viesti;
        this.paivays = paivays;
    }

    public int getId() {
        return id;
    }

    public String getKirjoittaja() {
        return kirjoittaja;
    }

    public String getViesti() {
        return viesti;
    }

    public String getPaivays() {
        return paivays;
    }   
}
