/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.karsimuno.zrna;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.karsimuno.podaci.Korisnik;
import org.foi.nwtis.karsimuno.rest.klijenti.KorisniciRESTResource;
import org.foi.nwtis.karsimuno.rest.klijenti.KorisniciRESTsResourceContainer;

/**
 *
 * @author Karlo
 */
@Named(value = "korisniciPogled")
@RequestScoped
public class KorisniciPogled {

    private Korisnik korisnik;
    private int errorCode = -1;
    private String ponovljenaLozinka;
    private String staroKorisnickoIme;
    private List<Korisnik> korisnici = null;
    private KorisniciRESTResource korisniciResource;
    private KorisniciRESTsResourceContainer korisniciContainer;

    /**
     * Creates a new instance of KorisniciPogled
     */
    public KorisniciPogled() {
        getKorisnici();
    }

    public List<Korisnik> getKorisnici() {
        if (korisnici == null) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
            String ulogiraniKorisnik = (String) session.getAttribute("korisnik");

            korisnici = new ArrayList<>();
            korisniciContainer = new KorisniciRESTsResourceContainer();
            String odgovor = korisniciContainer.getJson();

            JsonReader reader = Json.createReader(new StringReader(odgovor));
            JsonArray ja = reader.readArray();

            for (JsonValue jsonValue : ja) {
                Korisnik k = new Korisnik();
                k.fromJson(jsonValue.toString());
                korisnici.add(k);
                if (k.korisnickoIme.equals(ulogiraniKorisnik)) {
                    korisnik = k;
                    staroKorisnickoIme = k.korisnickoIme;
                }
            }
        }
        return korisnici;
    }

    public void setKorisnici(List<Korisnik> korisnici) {
        this.korisnici = korisnici;
    }

    public Korisnik getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(Korisnik korisnik) {
        this.korisnik = korisnik;
    }

    public String getPonovljenaLozinka() {
        return ponovljenaLozinka;
    }

    public void setPonovljenaLozinka(String ponovljenaLozinka) {
        this.ponovljenaLozinka = ponovljenaLozinka;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getStaroKorisnickoIme() {
        return staroKorisnickoIme;
    }

    public void setStaroKorisnickoIme(String staroKorisnickoIme) {
        this.staroKorisnickoIme = staroKorisnickoIme;
    }

    public void update() {
        errorCode = -1;
        if (korisnik.korisnickoIme.isEmpty() || korisnik.prezime.isEmpty() || korisnik.email.isEmpty()) {
            errorCode = 0;
            return;
        }
        if (!korisnik.lozinka.equals(ponovljenaLozinka)) {
            errorCode = 1;
            return;
        }

        String korisnikJson = korisnik.toJson();
        korisniciResource = new KorisniciRESTResource(staroKorisnickoIme);
        korisniciResource.putJson(korisnikJson);

        if (!staroKorisnickoIme.equals(korisnik.korisnickoIme)) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
            session.setAttribute("korisnik", korisnik.korisnickoIme);
        }
    }
}
