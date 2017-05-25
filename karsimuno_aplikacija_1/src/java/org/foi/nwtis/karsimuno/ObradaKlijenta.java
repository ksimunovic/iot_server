package org.foi.nwtis.karsimuno;

import org.foi.nwtis.karsimuno.dretve.ServerDretva;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.HashMap;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;

/**
 * Klasa kojom server obrađuje naredbu klijenta, kreirana radi bolje strukture
 * koda
 *
 * @author Karlo
 */
public class ObradaKlijenta {

    Socket socket;
    Konfiguracija konf;
//    Evidencija evidencija;
    HashMap<String, String> naredbe;
    HashMap<String, String> administratori = null;

    public ObradaKlijenta(Socket socket, HashMap<String, String> naredbe) {
        this.socket = socket;
        this.naredbe = naredbe;
        this.konf = ServerDretva.konf;
//        evidencija = Evidencija.getInstance();
    }

    /**
     * Izvršava klijentsku naredbu prema dobivenoj naredbi
     *
     * @return Vraća status obavljanja naredbe koji se onda šalje korisniku
     */
    public String izvrsiNaredbu() {

        switch (naredbe.get("naredba")) {
//            case "ADD":
//                return ProvjeraAdresa.dodajAdresuZaProvjeru(naredbe.get("parametar"));
//            case "TEST":
//                return ProvjeraAdresa.vratiStatusAdrese(naredbe.get("parametar"));
            case "WAIT":
                return cekaj(Integer.parseInt(naredbe.get("parametar")));
        }
        throw new IllegalArgumentException();
    }

    /**
     * Pauzira obradu naredbe klijenta dani broj milisekundi, u slučaju prekida
     * vraća grešku
     *
     * @param milisekunde Broj sekundi koliko će server čekati/spavati
     * @return Odgovor sa statusom je li čekanje uspješno izvršeno
     */
    public String cekaj(int milisekunde) {
        try {
            sleep(milisekunde);
        } catch (InterruptedException ex) {
            return "ERROR 13; Radna dretva nije uspjesno odradila cekanje";
        }
        return "OK;";
    }
}
