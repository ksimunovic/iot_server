package org.foi.nwtis.karsimuno.podaci;

import java.sql.Timestamp;

/**
 *
 * @author Karlo
 */
public class Zahtjev {

    int id;
    String korisnik;
    String naredba;
    String odgovor;
    Timestamp vrijeme;

    public Zahtjev(int id, String korisnik, String naredba, String odgovor, Timestamp vrijeme) {
        this.id = id;
        this.korisnik = korisnik;
        this.naredba = naredba;
        this.odgovor = odgovor;
        this.vrijeme = vrijeme;
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

    public String getNaredba() {
        return naredba;
    }

    public void setNaredba(String naredba) {
        this.naredba = naredba;
    }

    public String getOdgovor() {
        return odgovor;
    }

    public void setOdgovor(String odgovor) {
        this.odgovor = odgovor;
    }

    public Timestamp getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(Timestamp vrijeme) {
        this.vrijeme = vrijeme;
    }

}
