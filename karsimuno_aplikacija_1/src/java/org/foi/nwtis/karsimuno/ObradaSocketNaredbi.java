package org.foi.nwtis.karsimuno;

import org.foi.nwtis.karsimuno.dretve.ServerDretva;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
        ucitajAdminDatoteku();
        if (administratori == null) {
            return "ERROR 00; Problem kod ucitavanja admin datoteke.";
        }

        String statusPrava = provjeriAdminPrava();
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
     * Učitava datoteku s podacima o login podacima administratorima
     */
    void ucitajAdminDatoteku() {
        String line;
        String fileName = konf.dajPostavku("adminDatoteka");

        try {
            FileReader fileReader = new FileReader(fileName);

            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                administratori = new HashMap<>();
                while ((line = bufferedReader.readLine()) != null) {
                    String[] dio = line.split(";");
                    administratori.put(dio[0], dio[1]);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Ne mogu otvoriti datoteku '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Greška kod čitanja datoteke '" + fileName + "'");
        }
    }

    /**
     * TODO: Kreirati tablicu korisnici (id, korisnik, lozinka) i prema njoj
     * provjeriti korisnika
     */
    String provjeriAdminPrava() {

        BP_Konfiguracija BP_Konf = null;
        BP_Konf = (BP_Konfiguracija) SlusacAplikacije.getContext().getAttribute("BP_Konf");
        
        String prava = "";
        String sql;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            Class.forName(BP_Konf.getDriverDatabase());
            conn = DriverManager.getConnection(BP_Konf.getUserDatabase(), BP_Konf.getUserUsername(), BP_Konf.getUserPassword());

            sql = "SELECT count(*) FROM korisnici WHERE korisnicko_ime = ? AND lozinka = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(0, naredbe.get("korisnik"));
            stmt.setString(1, naredbe.get("lozinka"));
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

    /**
     * TODO: – potpuno prekida preuzimanje meteoroloških podataka i preuzimanje
     * komandi. I završava rad. Vraća OK 10; ako nije bio u postupku prekida,
     * odnosno ERR 12; ako je bio u postupku prekida.
     */
    synchronized String ugasiServer() {
        ServerDretva.zavrsiRadServera = true;
        try {
            ServerDretva.ss.close();
        } catch (IOException ex) {
            Logger.getLogger(ObradaSocketNaredbi.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "STOP";
    }

    /**
     * TODO: – privremeno prekida preuzimanje meteoroloških sljedećeg ciklusa (i
     * dalje može preuzimati komande). Vraća OK 10; ako nije bio u pauzi,
     * odnosno ERR 10; ako je bio u pauzi
     *
     * @return
     */
    synchronized String pauzirajServer() {
        if (ServerDretva.pauzirajServer == false) {
            ServerDretva.pauzirajServer = true;
            return "OK;";
        }
        return "ERROR 01; Server je vec u stanju pause.";
    }

    /**
     * TODO: – nastavlja s preuzimanjem meteoroloških podataka od sljedećeg
     * ciklusa. Vraća OK 10; ako je bio u pauzi, odnosno ERR 11; ako nije bio u
     * pauzi.
     */
    synchronized String pokreniServer() {
        if (ServerDretva.pauzirajServer == true) {
            ServerDretva.pauzirajServer = false;
            return "OK;";
        }
        return "ERROR 02; Server je vec pokrenut.";
    }

    private String vratiStatus() {
        return "";
    }
}
