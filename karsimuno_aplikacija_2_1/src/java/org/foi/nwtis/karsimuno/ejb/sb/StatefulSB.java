package org.foi.nwtis.karsimuno.ejb.sb;

import com.sun.xml.ws.fault.ServerSOAPFaultException;
import java.io.StringReader;
import javax.ejb.Stateful;
import javax.ejb.LocalBean;
import javax.enterprise.context.SessionScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service;
import org.foi.nwtis.karsimuno.rest.klijenti.KorisniciRESTResource;

/**
 *
 * @author Administrator
 */
@Stateful
@LocalBean
public class StatefulSB {

    @WebServiceRef(wsdlLocation = "META-INF/wsdl/nwtis.foi.hr_8080/DZ3_Master/IoT_Master.wsdl")
    private IoTMaster_Service service;

    /**
     * Autenticiranje korisnika obavlja se u Stateful SB, a nakon uspješnog
     * autenticiranja registrira se za prijem MQTT poruka od IoT uređaja
     * korisnikove grupe putem IoT_Master-a. Primljeni podaci iz poruka upisuju
     * se u tablicu poruka u bazi podataka.
     *
     * Nakon prijema određenog broja MQTT poruka, tzv. slot, (konfiguracijom se
     * određuje broj MQTT poruka u slotu) treba poslati JMS poruku (naziv reda
     * čekanja NWTiS_{korisnicko_ime}_2) s podacima o rednom broju JMS poruke
     * koja se šalje, vremenu početka i završetka prikupljanja tog slota, broju
     * obrađenih poruka, kolekciji u koju se sprema atribut ″tekst″ primljenih
     * MQTT poruke u slotu. Poruka treba biti u obliku ObjectMessage, pri čemu
     * je naziv vlastite klase proizvoljan, a njena struktura treba sadržavati
     * potrebne podatke koji su prethodno spomenuti. Red čekanja treba ima
     * vlastiti brojač JMS poruka.
     *
     */
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

        if (autentificiran) {
            try {
                registrirajGrupuIoT(korisnickoIme, lozinka);
                aktivirajGrupuIoT(korisnickoIme, lozinka);
            } catch (ServerSOAPFaultException ex) {
            }
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

    private Boolean registrirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster port = service.getIoTMasterPort();
        return port.registrirajGrupuIoT(korisnickoIme, korisnickaLozinka);
    }

    private Boolean aktivirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster port = service.getIoTMasterPort();
        return port.aktivirajGrupuIoT(korisnickoIme, korisnickaLozinka);
    }

}
