package org.foi.nwtis.karsimuno.dretve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;
import org.foi.nwtis.karsimuno.slusaci.SlusacAplikacije;

/**
 * Main klasa kojom se pokreće server. Ona pokreće sve dretve te prihvaća i
 * raspoređuje zahtjeve prema definiranim pravilima
 *
 * @author Karlo
 */
public class ServerDretva extends Thread {

    public static ServerSocket ss;
    public static short brojDretvi = 0;
    public static Konfiguracija konf;
    public static boolean pauzirajServer = false;
    public static boolean zavrsiRadServera = false;
    public static String statusZavrsetkaServera = "";
    public static HashMap<String, Long> aktivneDretve = new HashMap<>();
    static HashMap<String, String> opcijePokretanja;

    static void ukloniAktivnuDretvu(String threadName) {
        aktivneDretve.remove(threadName);
    }

    private void inkrementirajBrojDretvi() {
        brojDretvi++;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        try {
            if (ss != null) {
                ss.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        super.run();
        pokreniServer();
    }

    @Override
    public synchronized void start() {
        super.start();
        setName("ServerDretva");
    }

    /**
     * Pokreće osluškivanja na portu/vratima koji se čita iz datoteke
     * konfiguracije i prema postavkama u njoj upravlja zahtjevima
     */
    public void pokreniServer() {
        try {
            konf = (Konfiguracija) SlusacAplikacije.getContext().getAttribute("Ostatak_Konf");
            int port = Integer.parseInt(konf.dajPostavku("port"));

            ss = new ServerSocket(port);

            while (!zavrsiRadServera) {
                System.out.println("ČEKAM KONEKCIJU");
                Socket socket = ss.accept();

                RadnaDretva radnaDretva = new RadnaDretva(socket);
                String nazivDretve = "karsimuno-" + (brojDretvi);
                inkrementirajBrojDretvi();
                
                aktivneDretve.put(nazivDretve, System.currentTimeMillis());

                radnaDretva.setName(nazivDretve);
                radnaDretva.start();

                System.out.println("Pokrećem dretvu " + nazivDretve + ", a ukupno je " + aktivneDretve.size() + " aktivnih dretvi");
            }
        } catch (IOException ex) {
            System.out.println("GASIIIIIM");
        }
    }
}
