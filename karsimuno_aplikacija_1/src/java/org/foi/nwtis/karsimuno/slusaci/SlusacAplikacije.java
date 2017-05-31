package org.foi.nwtis.karsimuno.slusaci;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.karsimuno.dretve.ServerDretva;
import org.foi.nwtis.karsimuno.dretve.PozadinskaDretva;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;
import org.foi.nwtis.karsimuno.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.karsimuno.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.karsimuno.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.karsimuno.konfiguracije.bp.BP_Konfiguracija;

/**
 * Web application lifecycle listener.
 *
 * @author Karlo
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    public static ServletContext context = null;
    BP_Konfiguracija BP_Konfig;
    Konfiguracija konf;
    PozadinskaDretva pd;
    ServerDretva ss;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        context = sce.getServletContext();
        ucitajKonfiguraciju();

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (pd != null) {
            pd.interrupt();
        }
        if (ss != null) {
            ss.interrupt();
        }
    }

    /**
     * Učitava konfiguracijsku datoteku u kontekst prema parametrima iz
     * konteksta
     */
    private void ucitajKonfiguraciju() {
        String path = context.getRealPath("/WEB-INF") + java.io.File.separator;
        String datoteka = context.getInitParameter("konfiguracija");

        BP_Konfig = new BP_Konfiguracija(path + datoteka);
        context.setAttribute("BP_Konfig", BP_Konfig);

        System.out.println("Učitana konfiguracija!");

        konf = null;
        try {
            konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(path + datoteka);
            context.setAttribute("Ostatak_Konf", konf);
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }

        pd = new PozadinskaDretva(konf);
//        pd.start(); //FIXME: pozadinska dretva

        ss = new ServerDretva();
        ss.start(); //FIXME: server dretva
    }

    public static ServletContext getContext() {
        return context;
    }
}
