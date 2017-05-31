/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.karsimuno.rest.serveri;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import org.foi.nwtis.karsimuno.podaci.Lokacija;
import org.foi.nwtis.karsimuno.rest.klijenti.GMKlijent;

/**
 * REST Web Service
 *
 * @author Karlo
 */
public class UredjajiRESTResource {

    private String id;
    private ResultSet rs = null;
    private Connection conn = null;
    private PreparedStatement stmt = null;
    private final BazaHelper baza = new BazaHelper();

    /**
     * Creates a new instance of UredjajiRESTResource
     */
    private UredjajiRESTResource(String id) {
        this.id = id;
    }

    /**
     * Get instance of the UredjajiRESTResource
     */
    public static UredjajiRESTResource getInstance(String id) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of UredjajiRESTResource class.
        return new UredjajiRESTResource(id);
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.karsimuno.rest.serveri.UredjajiRESTResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        try {
            conn = baza.spojiBazu();

            String sql = "SELECT * FROM uredaji WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);

            rs = stmt.executeQuery();
            if (rs.next()) {
                job.add("id", rs.getInt("id"));
                job.add("naziv", rs.getString("naziv"));
                job.add("latitude", rs.getFloat("latitude"));
                job.add("longitude", rs.getFloat("longitude"));
                job.add("status", rs.getInt("status"));
                job.add("vrijeme_promjene", rs.getTimestamp("vrijeme_promjene").getTime());
                job.add("vrijeme_kreiranja", rs.getTimestamp("vrijeme_kreiranja").getTime());
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            baza.otkvaciBazu();
        }
        return job.build().toString();
    }

    // {"naziv":"testniUređaj","adresa":"mikuševa 10, Sesvete"}
    // {"naziv":"testniUređaj","longitude":"45.0","latitude":"16.0"}
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postJson(String content) {
        Integer status = 0;

        if (postojiUredjaj(id) || content.isEmpty()) {
            return status.toString();
        }

        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject jo = reader.readObject();

        String naziv = jo.getString("naziv");

        Lokacija loc = new Lokacija();
        if (jo.containsKey("adresa")) {
            GMKlijent gmk = new GMKlijent();
            loc = gmk.getGeoLocation(jo.getString("adresa"));
        } else {
            loc.setLatitude(jo.getString("latitude"));
            loc.setLongitude(jo.getString("longitude"));
        }

        try {
            conn = baza.spojiBazu();
            String sql = "INSERT INTO uredaji (id, naziv, latitude, longitude, vrijeme_promjene, vrijeme_kreiranja) VALUES (?, ?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, Integer.parseInt(id));
            stmt.setString(2, naziv);
            stmt.setString(3, loc.getLatitude());
            stmt.setString(4, loc.getLongitude());
            stmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

            if (stmt.executeUpdate() == 1) {
                status = 1;
            }

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            baza.otkvaciBazu();
        }
        return status.toString();
    }

    /**
     * PUT method for updating or creating an instance of UredjajiRESTResource
     *
     * @param content representation for the resource
     * @return
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String putJson(String content) {
Integer status = 0;

        if (!postojiUredjaj(id) || content.isEmpty()) {
            return status.toString();
        }

        JsonReader reader = Json.createReader(new StringReader(content));
        JsonObject jo = reader.readObject();

        Lokacija loc = new Lokacija();
        if (jo.containsKey("adresa")) {
            GMKlijent gmk = new GMKlijent();
            loc = gmk.getGeoLocation(jo.getString("adresa"));
        } else {
            loc.setLatitude(jo.getString("latitude"));
            loc.setLongitude(jo.getString("longitude"));
        }

        try {
            conn = baza.spojiBazu();
            String sql = "UPDATE uredaji SET naziv = ?, latitude = ?, longitude = ?, vrijeme_promjene = ? WHERE id = ?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, jo.getString("naziv"));
            stmt.setString(2, loc.getLatitude());
            stmt.setString(3, loc.getLongitude());
            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.setString(5, id);

            if (stmt.executeUpdate() == 1) {
                status = 1;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            baza.otkvaciBazu();
        }
        return status.toString();
    }

    private boolean postojiUredjaj(String id) {
        boolean postoji = false;
        try {
            conn = baza.spojiBazu();

            String sql = "SELECT * FROM uredaji WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
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
