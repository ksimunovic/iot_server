package org.foi.nwtis.karsimuno.podaci;

import java.sql.Timestamp;

/**
 *
 * @author Karlo
 */
public class Dnevnik {

    private int id;
    private String korisnik;
    private String url;
    private String ipadresa;
    private Timestamp vrijeme;
    private int trajanje;
    private int status;

    public Dnevnik(int id, String korisnik, String url, String ipadresa, Timestamp vrijeme, int trajanje, int status) {
        this.id = id;
        this.korisnik = korisnik;
        this.url = url;
        this.ipadresa = ipadresa;
        this.vrijeme = vrijeme;
        this.trajanje = trajanje;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIpadresa() {
        return ipadresa;
    }

    public void setIpadresa(String ipadresa) {
        this.ipadresa = ipadresa;
    }

    public Timestamp getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(Timestamp vrijeme) {
        this.vrijeme = vrijeme;
    }

    public int getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(int trajanje) {
        this.trajanje = trajanje;
    }
    
    
}
