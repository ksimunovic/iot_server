package org.foi.nwtis.karsimuno.rest.serveri;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.karsimuno.BazaHelper;

/**
 * REST Web Service
 *
 * @author Administrator
 */
public class KorisniciRESTResource {

    private ResultSet rs = null;
    private Connection conn = null;
    private final String korisnickoIme;
    private PreparedStatement stmt = null;
    private final BazaHelper baza = new BazaHelper();

    /**
     * Creates a new instance of KorisniciRESTResource
     */
    private KorisniciRESTResource(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    /**
     * Get instance of the KorisniciRESTResource
     */
    public static KorisniciRESTResource getInstance(String korisnickoIme) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of KorisniciRESTResource class.
        return new KorisniciRESTResource(korisnickoIme);
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.karsimuno.rest.serveri.KorisniciRESTResource
     *
     * @return an instance of java.lang.String
     */
    // preuzimanje jednog korisnika (vraća JSON korisnika, uključujući korisničku lozinku)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();

        try {
            conn = baza.spojiBazu();

            String sql = "SELECT * FROM korisnici WHERE korisnicko_ime = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, korisnickoIme);

            rs = stmt.executeQuery();
            if (rs.next()) {
                job.add("id", rs.getInt("id"));
                job.add("korisnicko_ime", rs.getString("korisnicko_ime"));
                job.add("lozinka", rs.getString("lozinka"));
                job.add("prezime", rs.getString("prezime"));
                job.add("email", rs.getString("email"));
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            baza.otkvaciBazu();
        }
        return job.build().toString();
    }

    // dodavanje jednog korisnika (vraća 0 ako već postoji, 1 ako ne postoji te je dodan)
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postJson(String content) {
        Integer status = 0;

        if (postojiKorisnik(korisnickoIme) || content.isEmpty()) {
            return status.toString();
        }

        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject jo = reader.readObject();

        try {
            conn = baza.spojiBazu();

            String sql = "INSERT INTO korisnici (korisnicko_ime, lozinka, prezime, email) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, jo.getString("korisnicko_ime"));
            stmt.setString(2, jo.getString("lozinka"));
            stmt.setString(3, jo.getString("prezime"));
            stmt.setString(4, jo.getString("email"));

            if (stmt.executeUpdate() == 1) {
                status = 1;

                sql = "INSERT INTO user_roles (korisnicko_ime, role_name) VALUE (?, 'client')";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, jo.getString("korisnicko_ime"));
                stmt.executeUpdate();
            }

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            baza.otkvaciBazu();
        }
        return status.toString();
    }

    /**
     * PUT method for updating or creating an instance of KorisniciRESTResource
     *
     * @param content representation for the resource
     * @return
     */
    // ažuriranje jednog korisnika (vraća 0 ako ne postoji, 1 ako postoji te je ažuriran)
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putJson(String content) {

        Integer status = 0;

        if (!postojiKorisnik(korisnickoIme) || content.isEmpty()) {
            return status.toString();
        }

        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject jo = reader.readObject();

        try {
            conn = baza.spojiBazu();

            String sql = "UPDATE korisnici SET lozinka = ?, korisnicko_ime = ?, prezime = ?, email = ? WHERE korisnicko_ime = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, jo.getString("lozinka"));
            stmt.setString(2, jo.getString("korisnicko_ime"));
            stmt.setString(3, jo.getString("prezime"));
            stmt.setString(4, jo.getString("email"));
            stmt.setString(5, korisnickoIme);

            if (stmt.executeUpdate() == 1) {
                status = 1;
                
                sql = "UPDATE user_roles SET korisnicko_ime = ? WHERE korisnicko_ime = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, jo.getString("korisnicko_ime"));
                stmt.setString(2, korisnickoIme);
                stmt.executeUpdate();
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            baza.otkvaciBazu();
        }
        return status.toString();
    }

    private boolean postojiKorisnik(String korisnickoIme) {
        boolean postoji = false;
        try {
            conn = baza.spojiBazu();

            String sql = "SELECT * FROM korisnici WHERE korisnicko_ime = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, korisnickoIme);
            rs = stmt.executeQuery();
            if (rs.next()) {
                postoji = true;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            baza.otkvaciBazu();
        }
        return postoji;
    }
}
