
package tikape.runko.domain;


public class Avaus {
    private int id;
    private String otsikko;
    private int viesteja;
    private String viimeisin;

    public Avaus(int id, String otsikko, int viesteja, String viimeisin) {
        this.id = id;
        this.otsikko = otsikko;
        this.viesteja = viesteja;
        this.viimeisin = viimeisin;
    }

    public int getId() {
        return id;
    }

    public String getOtsikko() {
        return otsikko;
    }

    public int getViesteja() {
        return viesteja;
    }

    public String getUusinViesti() {
        return viimeisin;
    }
    
    
}
