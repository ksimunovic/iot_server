package org.foi.nwtis.karsimuno.server;

import org.foi.nwtis.karsimuno.dretve.ServerDretva;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.karsimuno.dretve.PozadinskaDretva;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;

/**
 * Klasa kojom server obrađuje naredbu administratora, kreirana radi bolje
 * strukture koda
 *
 * @author Karlo
 */
public class ObradaSocketNaredbi {

    Socket socket;
    Konfiguracija konf;
    HashMap<String, String> naredbe;
    HashMap<String, String> administratori = null;

    public ObradaSocketNaredbi(Socket socket, HashMap<String, String> naredbe) {
        this.socket = socket;
        this.naredbe = naredbe;
        this.konf = ServerDretva.konf;
    }

    /**
     * Provjerava admin prava pa izvršava admin naredbu prema dobivenoj naredbi
     *
     * @return Vraća status obavljanja naredbe koji se onda šalje korisniku
     */
    public String izvrsiNaredbu() {

        switch (naredbe.get("naredba")) {
            case "PAUSE":
                return pauzirajServer();
            case "START":
                return pokreniServer();
            case "STOP":
                return ugasiServer();
            case "STATUS":
                return vratiStatus();
            default:
                return "";
        }
    }

    synchronized String ugasiServer() {

        if (!ServerDretva.zavrsiRadServera) {
            ServerDretva.zavrsiRadServera = true;

            try {
                ServerDretva.ss.close();
            } catch (IOException ex) {
                Logger.getLogger(ObradaSocketNaredbi.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "STOP";
        } else {
            return "ERR 12; Server je vec u postuku prekida";
        }
    }

    synchronized String pauzirajServer() {
        if (PozadinskaDretva.preskociCiklus == false) {
            PozadinskaDretva.preskociCiklus = true;
            return "OK 10;";
        }
        return "ERR 10; Server je vec u stanju pause.";
    }

    synchronized String pokreniServer() {
        if (PozadinskaDretva.preskociCiklus == true) {
            PozadinskaDretva.preskociCiklus = false;
            return "OK 10;";
        }
        return "ERR 11; Server je vec pokrenut.";
    }

    private String vratiStatus() {
        String dd = "14";

        if (PozadinskaDretva.preskociCiklus == true) {
            dd = "13";
        }
        if (ServerDretva.zavrsiRadServera == true) {
            dd = "15";
        }
        return "OK " + dd + ";";
    }
}
