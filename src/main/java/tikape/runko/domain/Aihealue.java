
package tikape.runko.domain;

public class Aihealue {
    private int id;
    private String otsikko;
    private int viesteja;
    private String uusinViesti;

    public Aihealue(int id, String nimi, int viesteja, String uusinViesti){
        this.id = id;
        this.otsikko = nimi;
        this.viesteja = viesteja;
        this.uusinViesti = uusinViesti;
    }

    public String getOtsikko() {
        return otsikko;
    }

    public int getViesteja() {
        return viesteja;
    }

    @Override
    public String toString() {
        return "Aihealue{" + "id=" + id + ", otsikko=" + otsikko + ", viesteja=" + viesteja + ", uusinViesti=" + uusinViesti + '}';
    }

    public int getId() {
        return id;
    }

    public String getUusinViesti() {
        return uusinViesti;
    }
    
    
}
