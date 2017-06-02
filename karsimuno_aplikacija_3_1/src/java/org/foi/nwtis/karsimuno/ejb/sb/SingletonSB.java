package org.foi.nwtis.karsimuno.ejb.sb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.foi.nwtis.karsimuno.JMSPorukaMqtt;
import org.foi.nwtis.karsimuno.ejb.MessageDrivenBean;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;

/**
 *
 * @author Administrator
 */
@Singleton
@LocalBean
public class SingletonSB implements Serializable {

    public static List<JMSPorukaMqtt> spremnikMqtt = new ArrayList<>();
    public static Konfiguracija konf;

    public void start() {

    }

    ;
    
    @PostConstruct
    public void initialize() {
        MessageDrivenBean mdb = new MessageDrivenBean();
    }

    @PreDestroy
    public void destroy() {

        spremiEvidenciju(konf.dajPostavku("evidDatoteka"));
    }

    void spremiEvidenciju(String datoteka) {
        datoteka = "/" + datoteka;
        String status = "OK;";
        FileOutputStream fos = null;
        ObjectOutputStream oot = null;

        try {
            File file = new File(datoteka);
            file.createNewFile();

            fos = new FileOutputStream(datoteka, false);
            oot = new ObjectOutputStream(fos);

            oot.writeObject(this);
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
    }
}
