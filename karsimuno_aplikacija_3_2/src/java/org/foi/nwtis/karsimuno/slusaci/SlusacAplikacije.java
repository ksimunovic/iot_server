package org.foi.nwtis.karsimuno.slusaci;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.karsimuno.ejb.sb.SingletonSB;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;
import org.foi.nwtis.karsimuno.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.karsimuno.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.karsimuno.konfiguracije.NemaKonfiguracije;

/**
 * Web application lifecycle listener.
 *
 * @author Karlo
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    SingletonSB singletonSB = lookupSingletonSBBean();

    public static ServletContext context = null;
    private Konfiguracija konf;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        context = sce.getServletContext();
        ucitajKonfiguraciju();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    /**
     * Učitava konfiguracijsku datoteku u kontekst prema parametrima iz
     * konteksta
     */
    private void ucitajKonfiguraciju() {
        String path = context.getRealPath("/WEB-INF") + java.io.File.separator;
        String datoteka = context.getInitParameter("konfiguracija");

        konf = null;
        try {
            konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(path + datoteka);
            context.setAttribute("Ostatak_Konf", konf);

            SingletonSB singletonSB = lookupSingletonSBBean();
            singletonSB.konf = konf;
            singletonSB.start();

        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Učitana konfiguracija!");
    }

    public static ServletContext getContext() {
        return context;
    }

    private SingletonSB lookupSingletonSBBean() {
        try {
            Context c = new InitialContext();
            return (SingletonSB) c.lookup("java:global/karsimuno_aplikacija_3/karsimuno_aplikacija_3_1/SingletonSB!org.foi.nwtis.karsimuno.ejb.sb.SingletonSB");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
