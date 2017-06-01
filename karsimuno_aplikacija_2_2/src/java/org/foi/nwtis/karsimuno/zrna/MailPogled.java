/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.karsimuno.zrna;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;
import org.foi.nwtis.karsimuno.kontrole.Izbornik;
import org.foi.nwtis.karsimuno.kontrole.Poruka;
import org.foi.nwtis.karsimuno.slusaci.SlusacAplikacije;

/**
 *
 * @author Karlo
 */
@Named(value = "mailPogled")
@RequestScoped
public class MailPogled {

    private Session session;
    int pozicijaOdPoruke = 1;
    private String traziPoruke;
    int brojPorukaZaPrikaz = 5;
    private final String lozinka;
    private final String korisnik;
    private final String posluzitelj;
    private final Konfiguracija konf;
    Integer brojPrikazanihPoruka = 0;
    private int ukupnoPorukaMapa = 0;
    static int pozicijaDoPoruke = -1;
    boolean onemoguciSljedeca = false;
    boolean onemoguciPrethodna = false;
    private static String odabranaMapa;
    private final Properties properties;
    private static ArrayList<Poruka> poruke = null;
    private ArrayList<Izbornik> mape = new ArrayList<>();

    public MailPogled() {
        konf = (Konfiguracija) SlusacAplikacije.getContext().getAttribute("Ostatak_Konf");
        posluzitelj = konf.dajPostavku("mail.server");
        korisnik = konf.dajPostavku("mail.usernameThread");
        lozinka = konf.dajPostavku("mail.passwordThread");
        brojPorukaZaPrikaz = Integer.parseInt(konf.dajPostavku("mail.numMessages")) - 1;

        properties = System.getProperties();
        properties.put("mail.smtp.host", posluzitelj);
        session = Session.getInstance(properties, null);

        preuzmiMape();
        if (poruke == null || odabranaMapa == null) {
            poruke = new ArrayList<Poruka>();
            preuzmiPoruke();
        }

        if (poruke.isEmpty()) {
            onemoguciPrethodna = onemoguciSljedeca = true;
        }
    }

    /**
     * Spaja se na mail server te preuzima sve mape kako bi se korisniku dalo na
     * odabir pregled poruka iz druge mape
     */
    void preuzmiMape() {
        ukupnoPorukaMapa = 0;
        session = Session.getInstance(properties, null);
        try {
            Store store = session.getStore("imap");
            store.connect(posluzitelj, korisnik, lozinka);

            Folder[] f = store.getDefaultFolder().list();
            for (Folder fd : f) {
                mape.add(new Izbornik(fd.getName() + " - " + fd.getMessageCount(), fd.getName()));
                ukupnoPorukaMapa += fd.getMessageCount();
            }

            store.close();
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(MailPogled.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(MailPogled.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metoda se spaja na mail server i ako u zadanoj mapi postoje poruke
     * učitava ih. Nakon učitavanja paginacijom određenog dijela, sortira poruke
     * po datumu slanja(najstarije prvo) i filtirira ih ukoliko je uključena
     * pretraga
     */
    void preuzmiPoruke() {
        poruke.clear();
        if (odabranaMapa == null || odabranaMapa.isEmpty()) {
            odabranaMapa = "INBOX"; // Defaultni folder
        }
        onemoguciPrethodna = onemoguciSljedeca = false;
        session = Session.getInstance(properties, null);
        try {
            Store store = session.getStore("imap");
            store.connect(posluzitelj, korisnik, lozinka);

            Folder folder = store.getFolder(odabranaMapa);
            folder.open(Folder.READ_ONLY);

            int maxPoruka = folder.getMessageCount();
            if (maxPoruka != 0) {

                if (pozicijaDoPoruke == -1 || pozicijaDoPoruke > maxPoruka) {
                    pozicijaDoPoruke = maxPoruka;
                }

                pozicijaOdPoruke = pozicijaDoPoruke - brojPorukaZaPrikaz;
                if (maxPoruka < pozicijaOdPoruke) {
                    pozicijaOdPoruke = maxPoruka;
                }

                if (pozicijaOdPoruke < 1) {
                    pozicijaOdPoruke = 1;
                    onemoguciSljedeca = true;
                }

                if (pozicijaDoPoruke == maxPoruka) {
                    onemoguciPrethodna = true;
                }

                Message[] messages = folder.getMessages(pozicijaOdPoruke, pozicijaDoPoruke);

                Arrays.sort(messages, new Comparator<Message>() {
                    @Override
                    public int compare(Message m1, Message m2) {
                        try {
                            return m1.getSentDate().compareTo(m2.getSentDate());
                        } catch (MessagingException ex) {
                            Logger.getLogger(MailPogled.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        return 0;
                    }
                });

                Collections.reverse(Arrays.asList(messages));

                for (Message m : messages) {
                    poruke.add(new Poruka(Integer.toString(m.getMessageNumber()), m.getSentDate(), m.getReceivedDate(), m.getFrom()[0].toString(), m.getSubject(), m.getContent().toString(), "0"));
                }
            } else {
                onemoguciPrethodna = onemoguciSljedeca = true;
            }
            store.close();
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(MailPogled.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException | IOException ex) {
            Logger.getLogger(MailPogled.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.filtrirajPoruke();
    }

    /**
     * Mijenja trenutnu mapu i postavlja paginator na početak
     *
     * @return
     */
    public String promjenaMape() {
        pozicijaOdPoruke = 1;
        pozicijaDoPoruke = -1;
        this.traziPoruke = "";
        this.preuzmiPoruke();
        return "";
    }

    /**
     * Filtrira trenutno učitane poruke i osvježava njihov prikaz
     *
     * @return
     */
    public String traziPoruke() {
        this.preuzmiPoruke();
        this.filtrirajPoruke();
        return "";
    }

    /**
     * Briše poruke iz trenutnog prikaza ukoliko u svom sadržaju nemaju traženi
     * pojam
     */
    public void filtrirajPoruke() {
        Iterator<Poruka> i = poruke.iterator();
        while (i.hasNext()) {
            Poruka p = i.next();
            if (traziPoruke != null && !traziPoruke.isEmpty() && !p.getSadrzaj().contains(traziPoruke)) {
                i.remove();
            }
        }
    }

    public String prethodnePoruke() {
        pozicijaDoPoruke = pozicijaDoPoruke + brojPorukaZaPrikaz + 1;
        this.preuzmiPoruke();
        return "PrethodnePoruke";
    }

    /**
     * Pomiće paginator na sljedeću stranicu i ponovo preuzima poruke
     *
     * @return
     */
    public String sljedecePoruke() {
        pozicijaDoPoruke = pozicijaDoPoruke - brojPorukaZaPrikaz - 1;
        if (pozicijaDoPoruke < 1) {
            pozicijaDoPoruke = 1;
        }

        this.preuzmiPoruke();
        return "SljedecePoruke";
    }

    public void ocistiMapu() {
        if (odabranaMapa == null || odabranaMapa.isEmpty()) {
            odabranaMapa = "INBOX"; // Defaultni folder
        }
        session = Session.getInstance(properties, null);
        try {
            Store store = session.getStore("imap");
            store.connect(posluzitelj, korisnik, lozinka);

            Folder folder = store.getFolder(odabranaMapa);
            folder.open(Folder.READ_WRITE);

            if (folder.getMessageCount() != 0) {
                Message[] messages = folder.getMessages();
                for (Message message : messages) {
                    message.setFlag(Flags.Flag.DELETED, true);
                }
            }
            folder.close(true);
            store.close();
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(MailPogled.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(MailPogled.class.getName()).log(Level.SEVERE, null, ex);
        }
        preuzmiMape();
        preuzmiPoruke();
    }

    public String promjenaJezika() {
        return "PromjenaJezika";
    }

    public String saljiPoruku() {
        return "SaljiPoruku";
    }

    public ArrayList<Izbornik> getMape() {
        return mape;
    }

    public ArrayList<Poruka> getPoruke() {
        return poruke;
    }

    public String getTraziPoruke() {
        return traziPoruke;
    }

    public void setTraziPoruke(String traziPoruke) {
        this.traziPoruke = traziPoruke;
    }

    public String getOdabranaMapa() {
        return odabranaMapa;
    }

    public void setOdabranaMapa(String odabranaMapa) {
        this.odabranaMapa = odabranaMapa;
    }

    public int getUkupnoPorukaMapa() {
        return ukupnoPorukaMapa;
    }

    public void setUkupnoPorukaMapa(int ukupnoPorukaMapa) {
        this.ukupnoPorukaMapa = ukupnoPorukaMapa;
    }

    public boolean isOnemoguciSljedeca() {
        return onemoguciSljedeca;
    }

    public boolean isOnemoguciPrethodna() {
        return onemoguciPrethodna;
    }
}
