package org.foi.nwtis.karsimuno.poruke;

import java.io.Serializable;

/**
 * s podacima o rednom broju JMS poruke koja se šalje, vremenu početka i
 * završetka rada iteracije dretve, broju pročitanih poruka, broju NWTiS poruka.
 *
 * @author Administrator
 */
public class JMSPorukaMail implements Serializable {

    int redniBroj;
    long vrijemePocetka;
    long vrijemeZavrsetka;
    int brojProcitanihPoruka;
    int brojNWTiSPoruka;

    public JMSPorukaMail(int redniBroj, long vrijemePocetka, long vrijemeZavrsetka, int brojProcitanihPoruka, int brojNWTiSPoruka) {
        this.redniBroj = redniBroj;
        this.vrijemePocetka = vrijemePocetka;
        this.vrijemeZavrsetka = vrijemeZavrsetka;
        this.brojProcitanihPoruka = brojProcitanihPoruka;
        this.brojNWTiSPoruka = brojNWTiSPoruka;
    }
    
    
}
