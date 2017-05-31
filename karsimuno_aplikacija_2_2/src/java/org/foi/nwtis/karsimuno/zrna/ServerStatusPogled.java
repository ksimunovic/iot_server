/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.karsimuno.zrna;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.karsimuno.ServerHelper;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;
import org.foi.nwtis.karsimuno.podaci.Korisnik;
import org.foi.nwtis.karsimuno.rest.klijenti.KorisniciRESTResource;

/**
 *
 * @author Karlo
 */
@Named(value = "serverStatusPogled")
@RequestScoped
public class ServerStatusPogled {

    private Korisnik korisnik = null;
    private String prvaKomandaKraj = "";
    private String drugaKomandaKraj = "";
    private String prviOdgovor = "";
    private String drugiOdgovor = "";
    private KorisniciRESTResource korisniciResource;

    /**
     * Creates a new instance of ServerStatusPogled
     */
    public ServerStatusPogled() {
    }

    public Korisnik getKorisnik() {
        if (korisnik == null) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
            String ulogiraniKorisnik = (String) session.getAttribute("korisnik");

            korisniciResource = new KorisniciRESTResource(ulogiraniKorisnik);
            korisnik = new Korisnik();
            korisnik.fromJson(korisniciResource.getJson());
        }
        return korisnik;
    }

    public void setKorisnik(Korisnik k) {
        this.korisnik = k;
    }

    public String getPrvaKomanda() {
        if (korisnik == null) {
            getKorisnik();
        }
        return "USER " + korisnik.korisnickoIme + "; PASSWD " + korisnik.lozinka + "; ";
    }

    public String getPrvaKomandaKraj() {
        return prvaKomandaKraj;
    }

    public void setPrvaKomandaKraj(String prvaKomandaKraj) {
        this.prvaKomandaKraj = prvaKomandaKraj;
    }

    public String getDrugaKomandaKraj() {
        return drugaKomandaKraj;
    }

    public void setDrugaKomandaKraj(String drugaKomandaKraj) {
        this.drugaKomandaKraj = drugaKomandaKraj;
    }

    public String getDrugaKomanda() {
        if (korisnik == null) {
            getKorisnik();
        }
        return "USER " + korisnik.korisnickoIme + "; PASSWD " + korisnik.lozinka + "; IoT_Master ";
    }

    public String getPrviOdgovor() {
        return prviOdgovor;
    }

    public String getDrugiOdgovor() {
        return drugiOdgovor;
    }

    public void posaljiPrvuKomandu() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ServletContext context = (ServletContext) facesContext.getExternalContext().getContext();
        Konfiguracija konf = (Konfiguracija) context.getAttribute("Ostatak_Konf");

        String naredba = getPrvaKomanda() + prvaKomandaKraj + ";";

        ServerHelper server = new ServerHelper(Integer.parseInt(konf.dajPostavku("port")));
        prviOdgovor = server.posaljiNaredbu(naredba);
    }

    public void posaljiDruguKomandu() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ServletContext context = (ServletContext) facesContext.getExternalContext().getContext();
        Konfiguracija konf = (Konfiguracija) context.getAttribute("Ostatak_Konf");

        String naredba = getDrugaKomanda() + drugaKomandaKraj + ";";

        ServerHelper server = new ServerHelper(Integer.parseInt(konf.dajPostavku("port")));
        drugiOdgovor = server.posaljiNaredbu(naredba);
    }

}
