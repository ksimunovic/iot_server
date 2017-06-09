package org.foi.nwtis.karsimuno.zrna;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.foi.nwtis.karsimuno.poruke.JMSPorukaMqtt;
import org.foi.nwtis.karsimuno.SlusacPoruke;
import org.foi.nwtis.karsimuno.ejb.sb.SingletonSB;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;
import org.foi.nwtis.karsimuno.podaci.Korisnik;
import org.foi.nwtis.karsimuno.poruke.JMSPorukaMail;
import org.foi.nwtis.karsimuno.rest.klijenti.KorisniciRESTResource;
import org.foi.nwtis.karsimuno.ServerHelper;

/**
 *
 * @author Karlo
 */
@Named(value = "jmsPogled")
@RequestScoped
@ServerEndpoint("/websocket")
public class JmsPogled implements SlusacPoruke {

    private static final Set<Session> SESSIONS = ConcurrentHashMap.newKeySet();
    private String drugaKomandaKraj = "";
    private String drugiOdgovor = "";
    private Korisnik korisnik = null;
    private KorisniciRESTResource korisniciResource;

    /**
     * Creates a new instance of MqttPogled
     */
    public JmsPogled() {
    }

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
        SingletonSB.getInstance().addSlusac(this);
    }

    @Override
    public void novaPoruka(Object message) {
//        JMSPorukaMqtt jmsPoruka = (JMSPorukaMqtt) message;
        sendAll("a");
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session);
        SingletonSB.getInstance().removeSlusac(this);
    }

    public static void sendAll(String text) {
        synchronized (SESSIONS) {
            for (Session session : SESSIONS) {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(text);
                }
            }
        }
    }

    public List<JMSPorukaMqtt> getMqttPoruke() {
        List<JMSPorukaMqtt> orig = SingletonSB.getInstance().spremnikMqtt;
        List<JMSPorukaMqtt> temp = new ArrayList<>();

        if (orig != null) {
            for (JMSPorukaMqtt jMSPorukaMqtt : orig) {
                if (jMSPorukaMqtt != null) {
                    temp.add(jMSPorukaMqtt);
                }
            }
            Collections.reverse(temp);
        }
        return temp;
    }

    public List<JMSPorukaMail> getMailPoruke() {
        List<JMSPorukaMail> orig = SingletonSB.getInstance().spremnikMail;
        List<JMSPorukaMail> temp = new ArrayList<>();

        if (orig != null) {
            for (JMSPorukaMail jMSPorukaMail : orig) {
                if (jMSPorukaMail != null) {
                    temp.add(jMSPorukaMail);
                }
            }
            Collections.reverse(temp);
        }
        return temp;
    }

    public void updateView() {
    }

    public void clearMessages(String tip) {
        if (tip.equals("mqtt")) {
            SingletonSB.getInstance().spremnikMqtt = new ArrayList<>();
        }
        if (tip.equals("mail")) {
            SingletonSB.getInstance().spremnikMail = new ArrayList<>();
        }
        SingletonSB.getInstance().spremiSpremnik();
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
        return "USER " + korisnik.korisnickoIme + "; PASSWD " + korisnik.lozinka + "; IoT_Master ";
    }

    public String getDrugiOdgovor() {
        return drugiOdgovor;
    }

    public void posaljiDruguKomandu() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ServletContext context = (ServletContext) facesContext.getExternalContext().getContext();
        Konfiguracija konf = (Konfiguracija) context.getAttribute("Ostatak_Konf");

        String naredba = getDrugaKomanda() + drugaKomandaKraj + ";";

        ServerHelper server = new ServerHelper(Integer.parseInt(konf.dajPostavku("port")));
        drugiOdgovor = server.posaljiNaredbu(naredba);
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
}
