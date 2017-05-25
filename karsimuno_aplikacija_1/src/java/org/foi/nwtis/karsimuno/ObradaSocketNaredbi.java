package org.foi.nwtis.karsimuno;

import org.foi.nwtis.karsimuno.dretve.ServerDretva;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.karsimuno.dretve.PozadinskaDretva;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;
import org.foi.nwtis.karsimuno.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.karsimuno.slusaci.SlusacAplikacije;

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

        String statusPrava = provjeriKorisnika();
        if (!"OK;".equals(statusPrava)) {
            return statusPrava;
        }

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

    /**
     * TODO: Kreirati tablicu korisnici (id, korisnik, lozinka) i prema njoj
     * provjeriti korisnika
     */
    String provjeriKorisnika() {

        BP_Konfiguracija BP_Konf = null;
        BP_Konf = (BP_Konfiguracija) SlusacAplikacije.getContext().getAttribute("BP_Konfig");

        String prava = "";
        String sql;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            Class.forName(BP_Konf.getDriverDatabase());
            conn = DriverManager.getConnection(BP_Konf.getServerDatabase() + BP_Konf.getUserDatabase(), BP_Konf.getUserUsername(), BP_Konf.getUserPassword());

            sql = "SELECT * FROM korisnici WHERE korisnicko_ime = ? AND lozinka = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, naredbe.get("korisnik"));
            stmt.setString(2, naredbe.get("lozinka"));
            rs = stmt.executeQuery();

            if (rs.next()) {
                prava = "OK;";
            } else {
                prava = "ERROR 10; Ne postoji takav korisnik.";
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(PozadinskaDretva.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
            }
        }
        return prava;
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
        return "ERROR 10; Server je vec u stanju pause.";
    }

    synchronized String pokreniServer() {
        if (PozadinskaDretva.preskociCiklus == true) {
            PozadinskaDretva.preskociCiklus = false;
            return "OK 10;";
        }
        return "ERROR 11; Server je vec pokrenut.";
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
