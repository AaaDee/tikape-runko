package tikape.runko;

import java.util.HashMap;
import spark.ModelAndView;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.*;
import tikape.runko.domain.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }
        
        String jdbcOsoite = "jdbc:sqlite:tietokanta.db";
        
        if (System.getenv("DATABASE_URL") != null) {
            jdbcOsoite = System.getenv("DATABASE_URL");
        }
        
        

        Database database = new Database(jdbcOsoite);
        
        
        database.init();

        AihealueDao aihealueDao = new AihealueDao(database);
        AvausDao avausDao = new AvausDao(database);
        ViestiDao viestiDao = new ViestiDao(database);

        List<Aihealue> aiheet = aihealueDao.aiheListaus();

        for (Aihealue aihe : aiheet) {
            System.out.println(aihe);
        }


        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("aiheet", aihealueDao.aiheListaus());

            return new ModelAndView(map, "aiheet");
        }, new ThymeleafTemplateEngine());

        get("/alue/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            int alue = Integer.parseInt(req.params(":id"));
            int sivu = 1;
            if (req.queryParams("page") != null) {
                sivu = Integer.parseInt(req.queryParams("page"));
            }
            map.put("avaukset", avausDao.listaaAvaukset(alue, sivu));
            map.put("aihealue", aihealueDao.findOne(alue));
            map.put("sivu", sivu);
            map.put("edellinen", sivu - 1);
            map.put("seuraava", sivu + 1);

            return new ModelAndView(map, "alue");
        }, new ThymeleafTemplateEngine());

        post("/", (req, res) -> {
            String aihealue = req.queryParams("otsikko");

            if (aihealue.length() > 100) {
                res.redirect("/virhe");
                return "ok";
            }

            aihealueDao.lisaa(aihealue);
            res.redirect("/");
            return "ok";

        });

        post("/alue/:id", (req, res) -> {
            String otsikko = req.queryParams("otsikko");
            String nimimerkki = req.queryParams("nimimerkki");
            String viesti = req.queryParams("viesti");
            int alue = Integer.parseInt(req.params(":id"));

            if (viesti.length() > 1000 || nimimerkki.length() > 100 || otsikko.length() > 100) {
                res.redirect("/virhe");
                return "ok";
            }

            res.redirect("/alue/" + alue);

            avausDao.lisaa(nimimerkki, otsikko, viesti, alue);
            return "ok";
        });

        get("/alue/:alueid/avaus/:avausid", (req, res) -> {
            HashMap map = new HashMap<>();
            int alue = Integer.parseInt(req.params(":alueid"));
            int avaus = Integer.parseInt(req.params(":avausid"));
            int sivu = 1;
            if (req.queryParams("page") != null) {
                sivu = Integer.parseInt(req.queryParams("page"));
            }
            map.put("avaus", avausDao.findOne(avaus));
            map.put("viestit", viestiDao.avauksenViestit(avaus, sivu));
            map.put("alue", aihealueDao.findOne(alue));
            map.put("sivu", sivu);
            map.put("edellinen", sivu - 1);
            map.put("seuraava", sivu + 1);

            return new ModelAndView(map, "viesti");
        }, new ThymeleafTemplateEngine());

        post("/alue/:alueid/avaus/:avausid", (req, res) -> {
            String nimimerkki = req.queryParams("nimimerkki");
            String viesti = req.queryParams("viesti");
            int alue = Integer.parseInt(req.params(":alueid"));
            int avaus = Integer.parseInt(req.params(":avausid"));

            if (viesti.length() > 1000 || nimimerkki.length() > 100) {
                res.redirect("/virhe");
                return "ok";
            }

            res.redirect("/alue/" + alue + "/avaus/" + avaus);

            viestiDao.lisaa(nimimerkki, viesti, avaus);
            return "ok";
        });

        get("/virhe", (req, res) -> {
            HashMap map = new HashMap<>();
            return new ModelAndView(map, "virhe");
        }, new ThymeleafTemplateEngine());

    }
}
