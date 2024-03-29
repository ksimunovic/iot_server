package org.foi.nwtis.karsimuno.ejb.sb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import org.foi.nwtis.karsimuno.poruke.JMSPorukaMail;
import org.foi.nwtis.karsimuno.poruke.JMSPorukaMqtt;
import org.foi.nwtis.karsimuno.ejb.MailDrivenBean;
import org.foi.nwtis.karsimuno.ejb.MqttDrivenBean;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;
import org.foi.nwtis.karsimuno.SlusacPoruke;

/**
 *
 * @author Karlo
 */
@Singleton
@LocalBean
public class SingletonSB implements Serializable {

    public List<JMSPorukaMqtt> spremnikMqtt = null;
    public List<JMSPorukaMail> spremnikMail = null;
    public static Konfiguracija konf;
    public static String path;
    public static String evidDatoteka;
    private static List<SlusacPoruke> slusaci = null;

    
    private static SingletonSB instance = null;

    protected SingletonSB() {
    }

    public static SingletonSB getInstance() {
        if (instance == null) {
            instance = new SingletonSB();
        }
        return instance;
    }

    public void start() {
    }

    public synchronized void dodajMqtt(JMSPorukaMqtt m) {
        if (spremnikMqtt == null) {
            ucitajSpremnik();
        }
        if (spremnikMqtt == null) {
            spremnikMqtt = new ArrayList<>();
        }
        
        spremnikMqtt.add(m);
        spremiSpremnik();

        if (slusaci == null) {
            return;
        }
        for (SlusacPoruke s : slusaci) {
            if (s != null) {
                s.novaPoruka(m);
                break;
            }
        }
    }

    public synchronized void dodajMail(JMSPorukaMail m) {
        if (spremnikMail == null) {
            ucitajSpremnik();
        }
        if (spremnikMail == null) {
            spremnikMail = new ArrayList<>();
        }
        
        spremnikMail.add(m);
        spremiSpremnik();
        
        if (slusaci == null) {
            return;
        }
        for (SlusacPoruke s : slusaci) {
            if (s != null) {
                s.novaPoruka(m);
                break;
            }
        }
    }

    @PostConstruct
    public void initialize() {
        path = path.substring(0, path.lastIndexOf("3\\dist\\gfdeploy\\") + 1) + "_2/web/WEB-INF/";
        evidDatoteka = konf.dajPostavku("evidDatoteka");
        MqttDrivenBean mqtt = new MqttDrivenBean();
        MailDrivenBean mail = new MailDrivenBean();
    }

    @PreDestroy
    public void destroy() {
        spremnikMail = SingletonSB.getInstance().spremnikMail;
        spremnikMqtt = SingletonSB.getInstance().spremnikMqtt;
        spremiSpremnik();
    }

    synchronized public void spremiSpremnik() {
        if (spremnikMqtt == null) {
            spremnikMqtt = new ArrayList<>();
        }
        if (spremnikMail == null) {
            spremnikMail = new ArrayList<>();
        }

        String datoteka = path + evidDatoteka;
        FileOutputStream fos = null;
        ObjectOutputStream oot = null;

        try {
            File file = new File(datoteka);
            file.createNewFile();

            fos = new FileOutputStream(datoteka, false);
            oot = new ObjectOutputStream(fos);

            synchronized(this){
                oot.writeObject(this);
                this.notify();
            }
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (oot != null) {
                    oot.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Trenutno stanje spremnika: Mqtt - " + spremnikMqtt.size() + ", Mail - " + spremnikMail.size());
    }

    synchronized public void ucitajSpremnik() {
        String datoteka = evidDatoteka;
        if (spremnikMqtt != null && spremnikMail != null) {
            return;
        }
        if (spremnikMqtt == null) {
            spremnikMqtt = new ArrayList<>();
        }
        if (spremnikMail == null) {
            spremnikMail = new ArrayList<>();
        }

        SingletonSB sb = null;
        FileInputStream fis = null;
        datoteka = path + datoteka;
        ObjectInputStream ois = null;

        try {
            fis = new FileInputStream(datoteka);
            ois = new ObjectInputStream(fis);
            sb = (SingletonSB) ois.readObject();
        } catch (ClassNotFoundException | IOException i) {
            System.out.println("Greška kod učitavanja spremnika");
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.spremnikMqtt = sb.spremnikMqtt;
        this.spremnikMail = sb.spremnikMail;

        System.out.println("Učitan prethodni spremnik -> mqtt:" + spremnikMqtt.size() + " i mail:" + spremnikMail.size());
    }

    public void addSlusac(Object slusac) {
        if (slusaci == null) {
            slusaci = new ArrayList<>();
        }
        slusaci.add((SlusacPoruke) slusac);
    }

    public void removeSlusac(Object slusac) {
        if (slusaci == null) {
            return;
        }
        slusaci.remove((SlusacPoruke) slusac);
    }

}
