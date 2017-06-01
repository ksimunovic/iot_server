/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.karsimuno.zrna;

import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import org.foi.nwtis.karsimuno.ejb.eb.Dnevnik;
import org.foi.nwtis.karsimuno.ejb.sb.DnevnikFacade;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;
import org.foi.nwtis.karsimuno.slusaci.SlusacAplikacije;

/**
 *
 * @author Administrator
 */
@Named(value = "dnevnikPogled")
@RequestScoped
public class DnevnikPogled {

    @EJB
    private DnevnikFacade dnevnikFacade;

    private static int brojRedova = 0;
    private int limitFrom = 0;
    private int ukupnoZapisa;
    
    /**
     * Creates a new instance of PregledDnevnika
     */
    public DnevnikPogled() {
    }

    public List<Dnevnik> getDnevnickiZapisi() {
        if (brojRedova == 0) {
            Konfiguracija konf = (Konfiguracija) SlusacAplikacije.getContext().getAttribute("Ostatak_Konf");
            brojRedova = Integer.parseInt(konf.dajPostavku("brojRedaka"));
        }
        return dohvatiDnevnik();
    }

    private List<Dnevnik> dohvatiDnevnik() {
        return dnevnikFacade.findFrom(limitFrom, brojRedova);
    }

    public int getBrojRedova() {
        return brojRedova;
    }

    public int getLimitFrom() {
        return limitFrom;
    }

    public void setLimitFrom(int limitFrom) {
        this.limitFrom = limitFrom;
    }

    public String prva() {
        limitFrom = 0;
        return "";
    }

    public String prethodna() {
        if ((limitFrom - brojRedova) >= 0) {
            limitFrom -= brojRedova;
        }
        return "";
    }

    public String sljedeca() {
        prebrojiZapise();
        if ((limitFrom + brojRedova) <= ukupnoZapisa) {
            limitFrom += brojRedova;
        }
        return "";
    }

    public String zadnja() {
        prebrojiZapise();
        limitFrom = ukupnoZapisa - brojRedova + (ukupnoZapisa % brojRedova);
        return "";
    }

    private void prebrojiZapise() {
        ukupnoZapisa = dnevnikFacade.count();
    }
}
