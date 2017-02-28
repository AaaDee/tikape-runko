package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.*;

public class AvausDao implements Dao<Avaus,Integer>{

    private Database database;

    public AvausDao(Database database)  {
        this.database = database;
    }

    public List<Avaus> listaaAvaukset(int aihe, int sivu) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement(""
                + "SELECT k.otsikko, k.id, COUNT(*) as viesteja, MAX(v.paivays) as paivays\n"
                + "FROM Keskustelunavaus k, Viesti v\n"
                + "WHERE v.avaus = k.id\n"
                + "AND k.alue = ?"
                + "GROUP BY k.otsikko, k.id\n"
                + "ORDER BY paivays DESC\n"
                + "LIMIT 10 OFFSET ?");

        stmt.setInt(1, aihe);
        stmt.setInt(2, (sivu - 1) * 10);
        ResultSet rs = stmt.executeQuery();
        List<Avaus> avaukset = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String otsikko = rs.getString("otsikko");
            String viimeisin = rs.getString("paivays");
            Integer viesteja = rs.getInt("viesteja");

            avaukset.add(new Avaus(id, otsikko, viesteja, viimeisin));
        }

        rs.close();
        stmt.close();
        connection.close();

        return avaukset;
    }

    public void lisaa(String nimimerkki, String otsikko, String viesti, int alue) throws SQLException {
        Connection connection = database.getConnection();

        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Keskustelunavaus"
                + "(otsikko, alue) Values "
                + "(?, ?);");

        stmt.setString(1, otsikko);
        stmt.setInt(2, alue);
        
        stmt.execute();
        stmt.close();
        
        //tässä kysely missä haetaan maxid, jotta tiedetään että mihin seuraavassa viitataan
        PreparedStatement stmt2 = connection.prepareStatement("SELECT MAX(id) as id FROM Keskustelunavaus");
        int id = Integer.parseInt(stmt2.executeQuery().getString("id"));
        stmt2.close();
        
                //tässä jatkuu

        PreparedStatement stmt3 = connection.prepareStatement("Insert into Viesti"
                + " (kirjoittaja, sisalto, paivays, avaus) Values "
                + "(?, ?, current_timestamp,?);");
        
        stmt3.setString(1, nimimerkki);
        stmt3.setString(2, viesti);
        stmt3.setInt(3, id);
        
        stmt3.execute();
        
        stmt3.close();
        connection.close();
    }

    @Override
    public Avaus findOne(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Keskustelunavaus WHERE id = ?");
        stmt.setInt(1, key);
        ResultSet rs = stmt.executeQuery();
        Integer id = rs.getInt("id");
        String otsikko = rs.getString("otsikko");
        String viimeisin = "";
        Integer viesteja = 0;

        conn.close();
        return new Avaus(id,otsikko,0,"");
    }

    @Override
    public List<Avaus> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
