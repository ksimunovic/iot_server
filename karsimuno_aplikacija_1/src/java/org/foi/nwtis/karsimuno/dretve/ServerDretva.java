package org.foi.nwtis.karsimuno.dretve;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;
import org.foi.nwtis.karsimuno.slusaci.SlusacAplikacije;

/**
 * Main klasa kojom se pokreće server. Ona pokreće sve dretve te prihvaća i
 * raspoređuje zahtjeve prema definiranim pravilima
 *
 * @author Karlo
 */
public class ServerDretva extends Thread {

    RezervnaDretva rd;
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
    }

    @Override
    public void run() {
        super.run();
        pokreniServer();
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    /**
     * Pokreće osluškivanja na portu/vratima koji se čita iz datoteke
     * konfiguracije i prema postavkama u njoj upravlja zahtjevima
     */
    public void pokreniServer() {
        try {
            konf = (Konfiguracija) SlusacAplikacije.getContext().getAttribute("Ostatak_Konf");

            int port = Integer.parseInt(konf.dajPostavku("port"));
            int maxDretvi = Integer.parseInt(konf.dajPostavku("maksBrojRadnihDretvi"));

            ss = new ServerSocket(port);

            while (!zavrsiRadServera) {
                System.out.println("CEKAM KONEKCIJU");
                Socket socket = ss.accept();

                RadnaDretva radnaDretva = new RadnaDretva(socket);
                String nazivDretve = "karsimuno-" + (brojDretvi);
                inkrementirajBrojDretvi();

                if (aktivneDretve.size() >= maxDretvi) {
                    System.out.println("Nemam mjesta za novu radnu dretvu, pokrećem rezervnu!");
                    synchronized (rd) {
                        rd.postaviSocket(socket);
                        rd.notify();
                    }
                    continue;
                }

                aktivneDretve.put(nazivDretve, System.currentTimeMillis());

                radnaDretva.setName(nazivDretve);
                radnaDretva.start();

                System.out.println("Pokrećem dretvu " + nazivDretve + ", a ukupno je " + aktivneDretve.size() + " aktivnih dretvi");
            }
        } catch (IOException ex) {

            // javi RezervnojDretvi da završi rad
//            synchronized (rd) {
//                rd.postaviSocket(null);
//                rd.notify();
//            }
        }
    }
}
