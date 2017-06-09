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
@Named(value = "serverIoTPogled")
@RequestScoped
public class ServerIoTPogled {

    private Korisnik korisnik = null;
    private String prvaKomandaKraj = "";
    private String drugaKomandaKraj = "";
    private String prviOdgovor = "";
    private String drugiOdgovor = "";
    private String treciOdgovor = "";
    private Integer parametarPrvi;
    private Integer parametarDrugi;
    private String parametarTreci;
    private String parametarCetvrti;
    private KorisniciRESTResource korisniciResource;

    /**
     * Creates a new instance of ServerIoTPogled
     */
    public ServerIoTPogled() {
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
        return "USER " + korisnik.korisnickoIme + "; PASSWD " + korisnik.lozinka + "; IoT_Master ";
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
        return "USER " + korisnik.korisnickoIme + "; PASSWD " + korisnik.lozinka + "; IoT ";
    }

    public String getPrviOdgovor() {
        return prviOdgovor;
    }

    public String getDrugiOdgovor() {
        return drugiOdgovor;
    }

    public Integer getParametarPrvi() {
        return parametarPrvi;
    }

    public void setParametarPrvi(Integer parametarPrvi) {
        this.parametarPrvi = parametarPrvi;
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

        String naredba = getDrugaKomanda() + parametarPrvi + " " + drugaKomandaKraj + ";";

        ServerHelper server = new ServerHelper(Integer.parseInt(konf.dajPostavku("port")));
        drugiOdgovor = server.posaljiNaredbu(naredba);
    }

    public String getTreciOdgovor() {
        return treciOdgovor;
    }

    public void setTreciOdgovor(String treciOdgovor) {
        this.treciOdgovor = treciOdgovor;
    }

    public Integer getParametarDrugi() {
        return parametarDrugi;
    }

    public void setParametarDrugi(Integer parametarDrugi) {
        this.parametarDrugi = parametarDrugi;
    }

    public String getParametarTreci() {
        return parametarTreci;
    }

    public void setParametarTreci(String parametarTreci) {
        this.parametarTreci = parametarTreci;
    }

    public String getParametarCetvrti() {
        return parametarCetvrti;
    }

    public void setParametarCetvrti(String parametarCetvrti) {
        this.parametarCetvrti = parametarCetvrti;
    }

    
    
    public String getTrecaKomanda() {
        if (korisnik == null) {
            getKorisnik();
        }
        return "USER " + korisnik.korisnickoIme + "; PASSWD " + korisnik.lozinka + "; IoT ";
    }

    public void posaljiTrecuKomandu() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ServletContext context = (ServletContext) facesContext.getExternalContext().getContext();
        Konfiguracija konf = (Konfiguracija) context.getAttribute("Ostatak_Konf");

        String naredba = getTrecaKomanda() + parametarDrugi + " ADD \"" + parametarTreci + "\" \"" + parametarCetvrti + "\";";

        ServerHelper server = new ServerHelper(Integer.parseInt(konf.dajPostavku("port")));
        treciOdgovor = server.posaljiNaredbu(naredba);
    }

    /**
     * Obavlja se slanjem pripadajuće komande USER korisnik; PASSWD lozinka; IoT
     * d{1-6} ADD ″naziv″;
     */
}
