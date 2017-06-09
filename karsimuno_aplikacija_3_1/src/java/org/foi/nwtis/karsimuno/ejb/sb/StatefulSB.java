package org.foi.nwtis.karsimuno.ejb.sb;

import java.io.StringReader;
import javax.ejb.Stateful;
import javax.ejb.LocalBean;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import org.foi.nwtis.karsimuno.rest.klijenti.KorisniciRESTResource;

/**
 *
 * @author Karlo
 */
@Stateful
@LocalBean
public class StatefulSB {

    public boolean provjeriKorisnika(String korisnickoIme, String lozinka) {
        boolean autentificiran = false;

        KorisniciRESTResource client = new KorisniciRESTResource(korisnickoIme);
        String response = client.getJson();
        client.close();

        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject jo = jsonReader.readObject();
        jsonReader.close();

        if (jo.containsKey("lozinka") && jo.getString("lozinka").equals(lozinka)) {
            autentificiran = true;
        }
        return autentificiran;
    }

    public boolean registrirajKorisnika(String korisnickoIme, String prezime, String lozinka, String email) {
        //korisničko ime (mora biti jedninstveno)
        KorisniciRESTResource client = new KorisniciRESTResource(korisnickoIme);
        String response = client.getJson();
        client.close();

        JsonReader jsonReader = Json.createReader(new StringReader(response));
        JsonObject jo = jsonReader.readObject();
        jsonReader.close();

        if (!jo.entrySet().isEmpty()) {
            return false;
        }

        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("korisnicko_ime", korisnickoIme);
        job.add("prezime", prezime);
        job.add("lozinka", lozinka);
        job.add("email", email);
        String payload = job.build().toString();

        client = new KorisniciRESTResource(korisnickoIme);
        response = client.postJson(payload);

        jsonReader = Json.createReader(new StringReader(response));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        return !object.entrySet().isEmpty();
    }

}
