package org.foi.nwtis.karsimuno.zrna;

import javax.inject.Named;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.karsimuno.podaci.Uredjaj;
import org.foi.nwtis.karsimuno.rest.klijenti.UredjajiRESTResource;
import org.foi.nwtis.karsimuno.rest.klijenti.UredjajiRESTsResourceContainer;
import org.foi.nwtis.karsimuno.ws.MeteoPodaci;
import org.foi.nwtis.karsimuno.ws.MeteoSOAP_Service;

/**
 *
 * @author Karlo
 */
@Named(value = "uredjajiPogled")
@RequestScoped
public class UredjajiPogled {

    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8084/karsimuno_aplikacija_1/MeteoSOAP.wsdl")
    private MeteoSOAP_Service service;

    private int errorCode = -1;
    private String adresa = "";
    private boolean ponovoUcitaj = false;
    private List<Uredjaj> uredjaji = null;
    private MeteoPodaci meteoPodaci = null;
    private Uredjaj uredjaj = new Uredjaj();
    private UredjajiRESTResource uredjajiResource;
    private String displayUpdateContainer = "display: none";
    private UredjajiRESTsResourceContainer uredjajiContainer;

    /**
     * Creates a new instance of OdabirIoTPrognoza
     */
    public UredjajiPogled() {
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Uredjaj getUredjaj() {
        return uredjaj;
    }

    public void setUredjaj(Uredjaj uredjaj) {
        this.uredjaj = uredjaj;
    }

    public String getDisplayUpdateContainer() {
        return displayUpdateContainer;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public List<Uredjaj> getUredjaji() {
        if (uredjaji == null || uredjaji.isEmpty() || ponovoUcitaj) {
            uredjaji = new ArrayList<>();
            uredjajiContainer = new UredjajiRESTsResourceContainer();
            String odgovor = uredjajiContainer.getJson();

            JsonReader reader = Json.createReader(new StringReader(odgovor));
            JsonArray ja = reader.readArray();

            for (JsonValue jsonValue : ja) {
                Uredjaj u = new Uredjaj();
                u.fromJson(jsonValue.toString());
                uredjaji.add(u);
            }
        }
        return uredjaji;
    }

    public void setUredjaji(List<Uredjaj> uredjaji) {
        this.uredjaji = uredjaji;
    }

    public void ucitajUredjaj(Uredjaj uredjaj) {
        displayUpdateContainer = "display:block";
        this.uredjaj = uredjaj;
    }

    public MeteoPodaci getMeteoPodaci() {
        return meteoPodaci;
    }

    public void setMeteoPodaci(MeteoPodaci meteoPodaci) {
        this.meteoPodaci = meteoPodaci;
    }

    public void update() {
        errorCode = -1;
        if (uredjaj.naziv.isEmpty() || uredjaj.longitude.isNaN() || uredjaj.latitude.isNaN()) {
            errorCode = 0;
            return;
        }

        String json = uredjaj.toJson();
        uredjajiResource = new UredjajiRESTResource(Integer.toString(uredjaj.id));
        if (uredjaj.id == 0) {
            uredjajiResource.postJson(json);
        } else {
            uredjajiResource.putJson(json);
        }
        ponovoUcitaj = true;
        uredjaj = new Uredjaj();
    }

    public void create(){
        uredjaj = new Uredjaj();
        displayUpdateContainer = "display: none";
    }
    
    public void dohvatiAdresu(Uredjaj u) {
        adresa = "alert('" + dajAdresuUredjaja(u.id) + "');";
    }

    private String dajAdresuUredjaja(int id) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.karsimuno.ws.MeteoSOAP port = service.getMeteoSOAPPort();
        return port.dajAdresuUredjaja(id);
    }

    public void dohvatiVazece(Uredjaj uredjaj) {
        meteoPodaci = dajVazeceMeteoPodatkeZaUredjaj(uredjaj.id);
    }

    private MeteoPodaci dajVazeceMeteoPodatkeZaUredjaj(int id) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.karsimuno.ws.MeteoSOAP port = service.getMeteoSOAPPort();
        return port.dajVazeceMeteoPodatkeZaUredjaj(id);
    }

    public void dohvatiZadnje(Uredjaj uredjaj) {
        //TODO: Testirati kad budem imao meteo podatke u bazi
        meteoPodaci = dajZadnjeMeteoPodatkeZaUredjaj(uredjaj.id);
    }

    private MeteoPodaci dajZadnjeMeteoPodatkeZaUredjaj(int id) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.karsimuno.ws.MeteoSOAP port = service.getMeteoSOAPPort();
        return port.dajZadnjeMeteoPodatkeZaUredjaj(id);
    }

}
