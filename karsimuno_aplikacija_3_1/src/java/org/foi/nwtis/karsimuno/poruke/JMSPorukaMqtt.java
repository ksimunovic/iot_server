package org.foi.nwtis.karsimuno.poruke;

import java.io.Serializable;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

/**
 * s podacima o rednom broju JMS poruke koja se šalje, vremenu početka i
 * završetka rada iteracije dretve, broju pročitanih poruka, broju NWTiS poruka.
 *
 * @author Administrator
 */
public class JMSPorukaMqtt implements Serializable {

    public int redniBroj;
    public long vrijemePocetka;
    public long vrijemeZavrsetka;
    public int brojProcitanihPoruka;
    public List<String> tekstovi;

    public JMSPorukaMqtt(int redniBroj, long vrijemePocetka, long vrijemeZavrsetka, int brojProcitanihPoruka, List<String> tekstovi) {
        this.redniBroj = redniBroj;
        this.vrijemePocetka = vrijemePocetka;
        this.vrijemeZavrsetka = vrijemeZavrsetka;
        this.brojProcitanihPoruka = brojProcitanihPoruka;
        this.tekstovi = tekstovi;
    }

    public String toJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("redniBroj", redniBroj);
        job.add("vrijemePocetka", vrijemePocetka);
        job.add("vrijemeZavrsetka", vrijemeZavrsetka);
        job.add("brojProcitanihPoruka", brojProcitanihPoruka);
        
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (String tekst : tekstovi) {
            jab.add(tekst);
        }
        job.add("tekstovi", jab);
        
        return job.build().toString();
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

    public List<String> getTekstovi() {
        return tekstovi;
    }

    public void setTekstovi(List<String> tekstovi) {
        this.tekstovi = tekstovi;
    }
    
    
}
