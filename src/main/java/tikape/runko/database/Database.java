package tikape.runko.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.net.*;

public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;
    }

    public Connection getConnection() throws SQLException {
        
        if (this.databaseAddress.contains("postgres")) {
            try {
                URI dbUri = new URI(databaseAddress);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

                return DriverManager.getConnection(dbUrl, username, password);
            } catch (Throwable t) {
                System.out.println("Error: " + t.getMessage());
                t.printStackTrace();
            }
        }

        return DriverManager.getConnection(databaseAddress);
    }
    

    public void init() {
        List<String> lauseet = null;
        if (this.databaseAddress.contains("postgres")) {
            lauseet = postgreLauseet();
        } else {
            lauseet = sqliteLauseet();
        }

        // "try with resources" sulkee resurssin automaattisesti lopuksi
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
            // jos tietokantataulu on jo olemassa, ei komentoja suoriteta
            System.out.println("Error >> " + t.getMessage());
        }
    }

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();
        lista.add("CREATE TABLE Aihealue "
                + "(id integer PRIMARY KEY, "
                + "nimi varchar(200) "
                + ");");
        lista.add("CREATE TABLE Keskustelunavaus (\n"
                + "id integer PRIMARY KEY,\n"
                + "otsikko varchar(200),\n"
                + "alue integer,\n"
                + "FOREIGN KEY(alue) REFERENCES Aihealue(id)\n"
                + ");");
        lista.add("CREATE TABLE Viesti (\n"
                + "id integer PRIMARY KEY,\n"
                + "kirjoittaja varchar(200),\n"
                + "sisalto varchar(10000),\n"
                + "paivays timestamp,\n"
                + "avaus integer,\n"
                + "FOREIGN KEY(avaus) REFERENCES Keskustelunavaus(id)\n"
                + ");");
        return lista;
    }

    private List<String> postgreLauseet() {
        ArrayList<String> lista = new ArrayList<>();
        lista.add("CREATE TABLE Aihealue "
                + "(id serial PRIMARY KEY, "
                + "nimi varchar(200) "
                + ");");
        lista.add("CREATE TABLE Keskustelunavaus (\n"
                + "id serial PRIMARY KEY,\n"
                + "otsikko varchar(200),\n"
                + "alue integer,\n"
                + "FOREIGN KEY(alue) REFERENCES Aihealue(id)\n"
                + ");");
        lista.add("CREATE TABLE Viesti (\n"
                + "id serial PRIMARY KEY,\n"
                + "kirjoittaja varchar(200),\n"
                + "sisalto varchar(10000),\n"
                + "paivays timestamp,\n"
                + "avaus integer,\n"
                + "FOREIGN KEY(avaus) REFERENCES Keskustelunavaus(id)\n"
                + ");");
        return lista;

    }
}
