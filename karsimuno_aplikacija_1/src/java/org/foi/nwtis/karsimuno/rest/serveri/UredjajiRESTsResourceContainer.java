/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.foi.nwtis.karsimuno.BazaHelper;

// dodavanje jednog IoT uređaja (vraća 0 ako već postoji, 1 ako ne postoji te je dodan)
// ažuriranje jednog IoT uređaja (vraća 0 ako ne postoji, 1 ako postoji te je ažuriran)
// preuzimanje jednog IoT uređaja (vraća JSON IoT uređaja)
// preuzimanje svih IoT uređaja (vraća niz JSON IoT uređaja).
/**
 * REST Web Service
 *
 * @author Administrator
 */
@Path("/uredjaji")
public class UredjajiRESTsResourceContainer {

    @Context
    private UriInfo context;

    private ResultSet rs = null;
    private Connection conn = null;
    private PreparedStatement stmt = null;
    private final BazaHelper baza = new BazaHelper();

    /**
     * Creates a new instance of UredjajiRESTsResourceContainer
     */
    public UredjajiRESTsResourceContainer() {
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.karsimuno.rest.serveri.UredjajiRESTsResourceContainer
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        try {
            conn = baza.spojiBazu();

            String sql = "SELECT * FROM uredaji";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                JsonObjectBuilder job = Json.createObjectBuilder();
                job.add("id", rs.getInt("id"));
                job.add("naziv", rs.getString("naziv"));
                job.add("latitude", rs.getFloat("latitude"));
                job.add("longitude", rs.getFloat("longitude"));
                job.add("status", rs.getInt("status"));
                job.add("vrijeme_promjene", rs.getTimestamp("vrijeme_promjene").getTime());
                job.add("vrijeme_kreiranja", rs.getTimestamp("vrijeme_kreiranja").getTime());
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
     * POST method for creating an instance of UredjajiRESTResource
     *
     * @param content representation for the new resource
     * @return an HTTP response with content of the created resource
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postJson(String content) {
        //TODO
        return Response.created(context.getAbsolutePath()).build();
    }

    /**
     * Sub-resource locator method for {id}
     */
    @Path("{id}")
    public UredjajiRESTResource getUredjajiRESTResource(@PathParam("id") String id) {
        return UredjajiRESTResource.getInstance(id);
    }
}
