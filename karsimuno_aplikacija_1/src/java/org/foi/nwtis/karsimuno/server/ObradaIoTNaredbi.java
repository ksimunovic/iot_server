package org.foi.nwtis.karsimuno.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.dkermek.ws.serveri.Lokacija;
import org.foi.nwtis.dkermek.ws.serveri.StatusUredjaja;
import org.foi.nwtis.dkermek.ws.serveri.Uredjaj;
import org.foi.nwtis.karsimuno.dretve.PozadinskaDretva;
import org.foi.nwtis.karsimuno.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.karsimuno.slusaci.SlusacAplikacije;

/**
 *
 * @author Karlo
 */
public class ObradaIoTNaredbi {

    HashMap<String, String> naredbe;

    public ObradaIoTNaredbi(HashMap<String, String> naredbe) {
        this.naredbe = naredbe;
    }

    public String izvrsiNaredbu() {

        //TODO: Autentifikacija korisnika?!
        switch (naredbe.get("naredba")) {
            case "ADD":
                return dodajUredjaj();
            case "WORK":
                return aktivirajUredjaj();
            case "WAIT":
                return blokirajUredjaj();
            case "REMOVE":
                return brisiUredjaj();
            case "STATUS":
                return statusUredjaja();

        }
        throw new IllegalArgumentException();
    }

    private Uredjaj nadjiUredjaj(String id, String naziv) {
        BP_Konfiguracija BP_Konf = (BP_Konfiguracija) SlusacAplikacije.getContext().getAttribute("BP_Konfig");

        Uredjaj uredjaj = null;
        String sql;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            Class.forName(BP_Konf.getDriverDatabase());
            conn = DriverManager.getConnection(BP_Konf.getServerDatabase() + BP_Konf.getUserDatabase(), BP_Konf.getUserUsername(), BP_Konf.getUserPassword());

            if (naziv.isEmpty()) {
                sql = "SELECT * FROM uredaji WHERE id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, naredbe.get("iot"));

            } else {
                sql = "SELECT * FROM uredaji WHERE naziv = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, naredbe.get("naziv"));
            }

            rs = stmt.executeQuery();

            if (rs.next()) {
                uredjaj = new Uredjaj();
                uredjaj.setId(rs.getInt("id"));
                uredjaj.setNaziv(rs.getString("naziv"));

                Lokacija l = new Lokacija();
                l.setLatitude(rs.getString("latitude"));
                l.setLongitude(rs.getString("longitude"));
                uredjaj.setGeoloc(l);
            } else {
                uredjaj = null;
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

        return uredjaj;
    }

    private String dodajUredjaj() {
        Uredjaj u = nadjiUredjaj(null, naredbe.get("naziv"));
        if (u == null) {
            return "ERR 30; Uredjaj ne postoji u bazi.";
        }

        //TODO: Statusom provjerit postoji li uređaj u grupi ili ostaje ovako?
        if (dodajUredjajGrupi(naredbe.get("korisnik"), naredbe.get("lozinka"), u)) {
            return "OK 10;";
        } else {
            return "ERR 30; Uredjaj vec postoji u grupi.";
        }
    }

    private static Boolean dodajUredjajGrupi(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, org.foi.nwtis.dkermek.ws.serveri.Uredjaj iotUredjaj) {
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service service = new org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service();
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster port = service.getIoTMasterPort();
        return port.dodajUredjajGrupi(korisnickoIme, korisnickaLozinka, iotUredjaj);
    }

    /**
     * IoT d{1-6} WORK; – aktivira IoT uređaj. Vraća OK 10; ako nije bio
     * aktivan, odnosno ERR 31; ako je bio aktivan.
     */
    private String aktivirajUredjaj() {
        StatusUredjaja su = dajStatusUredjajaGrupe(naredbe.get("korisnik"), naredbe.get("lozinka"), Integer.parseInt(naredbe.get("iot")));

        if (su != null && su.equals(StatusUredjaja.AKTIVAN)) {
            return "ERR 31; Uredjaj je vec bio aktivan.";
        } else {
            aktivirajUredjajGrupe(naredbe.get("korisnik"), naredbe.get("lozinka"), Integer.parseInt(naredbe.get("iot")));
            return "OK 10;";
        }
    }

    private static boolean aktivirajUredjajGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int idUredjaj) {
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service service = new org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service();
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster port = service.getIoTMasterPort();
        return port.aktivirajUredjajGrupe(korisnickoIme, korisnickaLozinka, idUredjaj);
    }

    private String blokirajUredjaj() {
        StatusUredjaja su = dajStatusUredjajaGrupe(naredbe.get("korisnik"), naredbe.get("lozinka"), Integer.parseInt(naredbe.get("iot")));

        //TODO(nedefinirano) ako je status NEPOSTOJI šta onda?
        if (su != null && su.equals(StatusUredjaja.BLOKIRAN)) {
            return "ERR 32; Uredjaj je vec bio blokiran.";
        } else {
            blokirajUredjajGrupe(naredbe.get("korisnik"), naredbe.get("lozinka"), Integer.parseInt(naredbe.get("iot")));
            return "OK 10;";
        }
    }

    private static boolean blokirajUredjajGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int idUredjaj) {
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service service = new org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service();
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster port = service.getIoTMasterPort();
        return port.blokirajUredjajGrupe(korisnickoIme, korisnickaLozinka, idUredjaj);
    }

    /**
     * err 33 ako ne postoji
     */
    private String brisiUredjaj() {
        StatusUredjaja su = dajStatusUredjajaGrupe(naredbe.get("korisnik"), naredbe.get("lozinka"), Integer.parseInt(naredbe.get("iot")));

        if (su != null && su.equals(StatusUredjaja.NEPOSTOJI)) {
            return "ERR 33; Uredjaj ne postoji.";
        } else {
            obrisiUredjajGrupe(naredbe.get("korisnik"), naredbe.get("lozinka"), Integer.parseInt(naredbe.get("iot")));
            return "OK 10;";
        }
    }

    private static StatusUredjaja dajStatusUredjajaGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int idUredjaj) {
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service service = new org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service();
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster port = service.getIoTMasterPort();
        return port.dajStatusUredjajaGrupe(korisnickoIme, korisnickaLozinka, idUredjaj);
    }

    private String statusUredjaja() {
        StatusUredjaja su = dajStatusUredjajaGrupe(naredbe.get("korisnik"), naredbe.get("lozinka"), Integer.parseInt(naredbe.get("iot")));

        if (su != null && su.equals(StatusUredjaja.AKTIVAN)) {
            return "OK; 35;";
        } else {
            return "OK; 34;";
        }
    }

    private static boolean obrisiUredjajGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka, int idUredjaj) {
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service service = new org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service();
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster port = service.getIoTMasterPort();
        return port.obrisiUredjajGrupe(korisnickoIme, korisnickaLozinka, idUredjaj);
    }

}
