package org.foi.nwtis.karsimuno.poruke;

import java.io.Serializable;

/**
 * s podacima o rednom broju JMS poruke koja se šalje, vremenu početka i
 * završetka rada iteracije dretve, broju pročitanih poruka, broju NWTiS poruka.
 *
 * @author Karlo
 */
public class JMSPorukaMail implements Serializable {

    public int redniBroj;
    public long vrijemePocetka;
    public long vrijemeZavrsetka;
    public int brojProcitanihPoruka;
    public int brojNWTiSPoruka;

    public JMSPorukaMail(int redniBroj, long vrijemePocetka, long vrijemeZavrsetka, int brojProcitanihPoruka, int brojNWTiSPoruka) {
        this.redniBroj = redniBroj;
        this.vrijemePocetka = vrijemePocetka;
        this.vrijemeZavrsetka = vrijemeZavrsetka;
        this.brojProcitanihPoruka = brojProcitanihPoruka;
        this.brojNWTiSPoruka = brojNWTiSPoruka;
    } 

    public int getRedniBroj() {
        return redniBroj;
    }

    public void setRedniBroj(int redniBroj) {
        this.redniBroj = redniBroj;
    }

    public long getVrijemePocetka() {
        return vrijemePocetka;
    }

    public void setVrijemePocetka(long vrijemePocetka) {
        this.vrijemePocetka = vrijemePocetka;
    }

    public long getVrijemeZavrsetka() {
        return vrijemeZavrsetka;
    }

    public void setVrijemeZavrsetka(long vrijemeZavrsetka) {
        this.vrijemeZavrsetka = vrijemeZavrsetka;
    }

    public int getBrojProcitanihPoruka() {
        return brojProcitanihPoruka;
    }

    public void setBrojProcitanihPoruka(int brojProcitanihPoruka) {
        this.brojProcitanihPoruka = brojProcitanihPoruka;
    }

    public int getBrojNWTiSPoruka() {
        return brojNWTiSPoruka;
    }

    public void setBrojNWTiSPoruka(int brojNWTiSPoruka) {
        this.brojNWTiSPoruka = brojNWTiSPoruka;
    }
    
}
