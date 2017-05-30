package org.foi.nwtis.karsimuno.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import org.foi.nwtis.karsimuno.kontrole.Izbornik;

/**
 *
 * @author Karlo
 */
@Named(value = "lokalizacija")
@SessionScoped
public class Lokalizacija implements Serializable {

    private static final ArrayList<Izbornik> izbornikJezika = new ArrayList<>();
    private String odabraniJezik = "hr";

    static {
        izbornikJezika.add(new Izbornik("hrvatski", "hr"));
        izbornikJezika.add(new Izbornik("engleski", "en"));
        izbornikJezika.add(new Izbornik("njemački", "de"));
    }

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

    public ArrayList<Izbornik> getIzbornikJezika() {
        return izbornikJezika;
    }

    public Object odaberiJezik() {
        setOdabraniJezik(odabraniJezik);
        return "";
    }

    public Object saljiPoruku() {
        return "SaljiPoruku";
    }

    public Object pregledPoruka() {
        return "PregledPoruka";
    }
}
