/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.karsimuno.rest.klijenti;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

/**
 * Jersey REST client generated for REST
 * resource:KorisniciRESTsResourceContainer [/korisnici]<br>
 * USAGE:
 * <pre>
 *        KorisniciRESTsResourceContainer client = new KorisniciRESTsResourceContainer();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author Karlo
 */
public class KorisniciRESTsResourceContainer {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/karsimuno_aplikacija_1/webresources";

    public KorisniciRESTsResourceContainer() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("korisnici");
    }

    public String getJson() throws ClientErrorException {
        WebTarget resource = webTarget;
        return resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).get(String.class);
    }

    public void close() {
        client.close();
    }

}
