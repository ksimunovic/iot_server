/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.karsimuno.dretve;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.karsimuno.ObradaSocketNaredbi;
import org.foi.nwtis.karsimuno.ObradaKlijenta;
import org.foi.nwtis.karsimuno.TestOpcija;

/**
 *
 * @author Administrator
 */
public class RadnaDretva extends Thread {

    Socket socket;
    long startTime = 0;
    InputStream is = null;
    String odgovor = null;
    OutputStream os = null;
    String adresaZahtjeva = "";
    HashMap<String, String> naredbe;
    Boolean cekajKrajServera = false;

    public RadnaDretva(Socket socket) {
        this.socket = socket;
        this.setName("RadnaDretva");
        adresaZahtjeva = socket.getRemoteSocketAddress().toString();
        startTime = System.currentTimeMillis();
    }

    @Override
    public void interrupt() {
        super.interrupt();
        System.out.println("Radna dretva " + this.getName() + " je prekinuta!");
    }

    @Override
    public void run() {
        super.run();
        startTime = System.currentTimeMillis();
        System.out.println("Dretva: " + this.getName());

        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();

            StringBuffer sb = new StringBuffer();

            while (true) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }
            System.err.println("Primljena naredba: " + sb + " s adrese: " + adresaZahtjeva);

            obradiNaredbu(sb);
            odgovor = "AAAAAAAAAA RADIIM!";

        } catch (IOException ex) {
//            ServerSustava.ukloniAktivnuDretvu(this.getName());
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            zatvoriVezuSaServeromIEvidentiraj();
        }
    }

    /**
     * Provjerava ispravnost naredbe te prema vrsti naredbe pokreće njenu obradu
     *
     * @param sb StringBuffer u kojem se nalaze podaci(naredba) primljeni od
     * strane korisnika sustava
     */
    void obradiNaredbu(StringBuffer sb) {
        TestOpcija provjeriNaredbe = new TestOpcija();
        String[] args = new String[]{sb.toString()};

        odgovor = "";
        naredbe = provjeriNaredbe.serverSocketNaredbe(args);
        if (naredbe != null) {
            ObradaSocketNaredbi obradaSocketNaredbi = new ObradaSocketNaredbi(socket, naredbe);
            odgovor = obradaSocketNaredbi.izvrsiNaredbu();

            if ("STOP".equals(odgovor)) {
                cekajKrajServera = true;
                odgovor = "";
            }
        }

        naredbe = provjeriNaredbe.IoTMasterNaredbe(args);
        if (naredbe != null && !ServerDretva.pauzirajServer) {
            ObradaKlijenta obradaKlijenta = new ObradaKlijenta(socket, naredbe);
            odgovor = obradaKlijenta.izvrsiNaredbu();
        }

        naredbe = provjeriNaredbe.IoTNaredbe(args);
        if (naredbe != null && !ServerDretva.pauzirajServer) {
            ObradaKlijenta obradaKlijenta = new ObradaKlijenta(socket, naredbe);
            odgovor = obradaKlijenta.izvrsiNaredbu();
        }
        
        if (odgovor.isEmpty()) {
            odgovor = "ERROR 01; Server je u stanju pauze, ne prihvaca naredbe.";
        } else {
//            evidencija.postaviZadnjuDretvu(ServerSustava.brojDretvi);
        }
    }

    /**
     * Ako je ovo dretva koja je primila naredbu zaustavljanja servera onda čeka
     * da se server zaustavi pa tek onda zatvara socket i pripadajuće tokove
     */
    private void zatvoriVezuSaServeromIEvidentiraj() {

//        if (cekajKrajServera) {
//            while (ServerSustava.statusZavrsetkaServera.isEmpty() || odgovor.isEmpty()) {
//                try {
//                    sleep(1);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                odgovor = ServerSustava.statusZavrsetkaServera;
//            }
//        }
        try {
            if (os != null && socket != null && !socket.isClosed()) {
                os.write(odgovor.getBytes());
                os.flush();
            }
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
            if (socket != null) {
                socket.close();
            }

//            evidencija.dodajUkupnoVrijeme(System.currentTimeMillis() - startTime);
//            ServerSustava.ukloniAktivnuDretvu(this.getName());
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public synchronized void start() {
        super.start();
    }
}
