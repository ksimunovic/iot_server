package org.foi.nwtis.karsimuno.rest.serveri;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.karsimuno.BazaHelper;

/**
 * REST Web Service
 *
 * @author Karlo
 */
@Path("/korisnici") //FIXME: korisniciREST
public class KorisniciRESTsResourceContainer {

    @Context
    private UriInfo context;

    private ResultSet rs = null;
    private Connection conn = null;
    private PreparedStatement stmt = null;
    private final BazaHelper baza = new BazaHelper();

    /**
     * Creates a new instance of KorisniciRESTsResourceContainer
     */
    public KorisniciRESTsResourceContainer() {
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.karsimuno.rest.serveri.KorisniciRESTsResourceContainer
     *
     * @return an instance of java.lang.String
     */
    // preuzimanje svih korisnika (vraća niz JSON korisnika, bez korisničke lozinke).
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        try {
            conn = baza.spojiBazu();

            String sql = "SELECT * FROM korisnici";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                JsonObjectBuilder job = Json.createObjectBuilder();
                job.add("id", rs.getInt("id"));
                job.add("korisnicko_ime", rs.getString("korisnicko_ime"));
                job.add("prezime", rs.getString("prezime"));
                job.add("email", rs.getString("email"));
                jab.add(job);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            baza.otkvaciBazu();
        }
        return jab.build().toString();
    }

    /**
     * Sub-resource locator method for {korisnickoIme}
     */
    @Path("{korisnickoIme}")
    public KorisniciRESTResource getKorisniciRESTResource(@PathParam("korisnickoIme") String korisnickoIme) {
        return KorisniciRESTResource.getInstance(korisnickoIme);
    }
}
