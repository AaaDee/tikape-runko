package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.*;

public class ViestiDao {

    private Database database;

    public ViestiDao(Database database) {
        this.database = database;
    }

    public List<Viesti> avauksenViestit(int avaus, int sivu) throws SQLException {
        Connection connection = database.getConnection();

        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti "
                + "WHERE avaus = ? "
                + "ORDER BY paivays ASC "
                + "LIMIT 20 OFFSET ?");

        stmt.setInt(1, avaus);
        stmt.setInt(2, (sivu - 1) * 10);
        ResultSet rs = stmt.executeQuery();
        List<Viesti> viestit = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("id");
            String kirjoittaja = rs.getString("kirjoittaja");
            String viesti = rs.getString("sisalto");
            String paivays = rs.getString("paivays");

            viestit.add(new Viesti(id, kirjoittaja, viesti, paivays));
        }

        rs.close();
        stmt.close();
        connection.close();

        return viestit;
    }

    public void lisaa(String nimimerkki, String vastaus, int avaus) throws SQLException {
        Connection conn = database.getConnection();
        PreparedStatement stmt = conn.prepareStatement("Insert into Viesti "
                + "(kirjoittaja, sisalto, paivays, avaus) Values"
                + "(?, ?, current_timestamp,?);");
        stmt.setString(1, nimimerkki);
        stmt.setString(2, vastaus);
        stmt.setInt(3, avaus);
        stmt.execute();

        conn.close();
    }
}
