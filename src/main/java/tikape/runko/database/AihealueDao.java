package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Aihealue;

public class AihealueDao implements Dao<Aihealue, Integer> {

    private Database database;

    public AihealueDao(Database database) {
        this.database = database;
    }

    @Override
    public Aihealue findOne(Integer key) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Aihealue WHERE id = ?");
        stmt.setInt(1, key);
        ResultSet rs = stmt.executeQuery();
        Integer id = rs.getInt("id");
        String otsikko = rs.getString("nimi");
        String viimeisin = "";
        Integer viesteja = 0;

        conn.close();
        return new Aihealue(id, otsikko, viesteja, viimeisin);
    }

    @Override
    public List<Aihealue> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void lisaa(String otsikko) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO Aihealue(nimi) "
                + "VALUES ( ? )");
        stmt.setString(1, otsikko);
        stmt.execute();

        conn.close();
    }

    public List<Aihealue> aiheListaus() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement(""
                + "SELECT a.nimi, a.id, COUNT(v.id) as viesteja, MAX(v.paivays) as paivays "
                + "FROM (Aihealue a "
                + "LEFT JOIN Keskustelunavaus k ON a.id = k.alue "
                + "LEFT JOIN Viesti v ON v.avaus = k.id) "
                + "GROUP BY a.nimi, a.id "
                + "ORDER BY a.nimi ASC;");

        ResultSet rs = stmt.executeQuery();
        List<Aihealue> aiheet = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String otsikko = rs.getString("nimi");
            String viimeisin = rs.getString("paivays");
            if (viimeisin == null) {
                viimeisin = "ei viestejä";
            }
            Integer viesteja = rs.getInt("viesteja");

            aiheet.add(new Aihealue(id, otsikko, viesteja, viimeisin));
        }

        rs.close();
        stmt.close();
        connection.close();

        return aiheet;
    }

}
