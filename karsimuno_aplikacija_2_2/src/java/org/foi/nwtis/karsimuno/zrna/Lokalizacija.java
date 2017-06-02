package org.foi.nwtis.karsimuno.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Locale;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 *
 * @author Karlo
 */
@Named(value = "lokalizacija")
@SessionScoped
public class Lokalizacija implements Serializable {

    private String odabraniJezik = "hr";

    /**
     * Creates a new instance of Lokalizacija
     */
    public Lokalizacija() {
    }

    /**
     * Dohvaća trenutno postavljeni jezik
     *
     * @return postavljeni jezik
     */
    public String getOdabraniJezik() {
        UIViewRoot UVIR = FacesContext.getCurrentInstance().getViewRoot();
        if (UVIR != null) {
            Locale lokalniJezik = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            odabraniJezik = lokalniJezik.getLanguage();
        }
        return odabraniJezik;
    }

    /**
     * Postavlja jezik servisa
     *
     * @param odabraniJezik jezik koji se želi postaviti
     */
    public void setOdabraniJezik(String odabraniJezik) {
        this.odabraniJezik = odabraniJezik;
        Locale lokalniJezik = new Locale(odabraniJezik);
        FacesContext.getCurrentInstance().getViewRoot().setLocale(lokalniJezik);
    }

    public Object odaberiJezik() {
        setOdabraniJezik(odabraniJezik);
        return "";
    }

}
