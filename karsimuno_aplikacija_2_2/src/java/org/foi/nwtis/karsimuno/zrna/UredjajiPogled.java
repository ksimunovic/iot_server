/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.karsimuno.zrna;

import javax.inject.Named;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.karsimuno.podaci.MeteoPodaci;
import org.foi.nwtis.karsimuno.podaci.Uredjaj;
import org.foi.nwtis.karsimuno.rest.klijenti.GMRESTHelper;
import org.foi.nwtis.karsimuno.rest.klijenti.OWMKlijent;
import org.foi.nwtis.karsimuno.rest.klijenti.UredjajiRESTResource;
import org.foi.nwtis.karsimuno.rest.klijenti.UredjajiRESTsResourceContainer;

/**
 *
 * @author Karlo
 */
@Named(value = "uredjajiPogled")
@RequestScoped
@ManagedBean(name = "uredjajiPogled")
public class UredjajiPogled implements Serializable {

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
        uredjajiResource.putJson(json);
        ponovoUcitaj = true;
    }

    public void dohvatiAdresu(Uredjaj u){
        adresa = "alert('" + dohvatiAdresuGeolokacijom(u.latitude, u.longitude) + "');";
        System.out.println("-----------" + adresa);

    }

    public String dohvatiAdresuGeolokacijom(Float latitude, Float longitude) {
        Client client = ClientBuilder.newClient();

        String adresa = latitude.toString() + "," + longitude.toString();
        WebTarget webResource = client.target(GMRESTHelper.getGM_BASE_URI())
                .path("maps/api/geocode/json");
        try {
            webResource = webResource.queryParam("latlng",
                    URLEncoder.encode(adresa, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
        webResource = webResource.queryParam("sensor", "false");
        String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);

        JsonReader reader = Json.createReader(new StringReader(odgovor));
        JsonObject jo = reader.readObject();
        JsonObject obj = jo.getJsonArray("results").getJsonObject(0);

        return obj.getString("formatted_address");
    }
    
    
    
    public void dohvatiVazece(Uredjaj uredjaj){
//        MeteoPodaci mp = null;
        OWMKlijent owmk = new OWMKlijent("5d9999aedf0f764ceb1fb1f291bbf093"); //TODO složit učitavanje iz konfiga

        meteoPodaci = owmk.getRealTimeWeather(uredjaj.latitude.toString(), uredjaj.longitude.toString());
        
//        int i = 0;
//        System.out.println("test");
    }

    
    
}
