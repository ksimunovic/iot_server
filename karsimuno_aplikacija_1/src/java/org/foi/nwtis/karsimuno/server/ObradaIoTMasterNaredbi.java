package org.foi.nwtis.karsimuno.server;

import java.util.List;
import java.net.Socket;
import java.util.HashMap;
import org.foi.nwtis.dkermek.ws.serveri.Uredjaj;
import org.foi.nwtis.dkermek.ws.serveri.StatusKorisnika;
import com.sun.xml.internal.ws.fault.ServerSOAPFaultException;

/**
 * Klasa kojom server obrađuje naredbu klijenta, kreirana radi bolje strukture
 * koda
 *
 * @author Karlo
 */
public class ObradaIoTMasterNaredbi {

    HashMap<String, String> naredbe;

    public ObradaIoTMasterNaredbi(Socket socket, HashMap<String, String> naredbe) {
        this.naredbe = naredbe;
    }

    public String izvrsiNaredbu() {

        //TODO: Autentifikacija korisnika?!
        switch (naredbe.get("naredba")) {
            case "START":
                return registrirajGrupu();
            case "STOP":
                return deregistrirajGrupu();
            case "WORK":
                return aktivirajGrupu();
            case "WAIT":
                return blokirajGrupu();
            case "LOAD":
                return ucitajUredjaje();
            case "CLEAR":
                return obrisiUredjaje();
            case "STATUS":
                return statusGrupe();
            case "LIST":
                return popisUredjaja();
        }
        throw new IllegalArgumentException();
    }

    public String registrirajGrupu() {
        String status = "";
        status = dajStatusGrupeIoT(naredbe.get("korisnik"), naredbe.get("lozinka")).name();

        if (status.equals(StatusKorisnika.BLOKIRAN.toString())) {
            boolean result = registrirajGrupuIoT(naredbe.get("korisnik"), naredbe.get("lozinka"));
            if (result) {
                return "OK 10;";
            }
        }
        return "ERR 20; Grupa je vec bila registrirana.";
    }

    private static Boolean registrirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service service = new org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service();
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster port = service.getIoTMasterPort();

        boolean result = false;
        try {
            result = port.registrirajGrupuIoT(korisnickoIme, korisnickaLozinka);
        } catch (ServerSOAPFaultException ex) {
        } finally {
            return result;
        }
    }

    private String deregistrirajGrupu() {
        String status = "";
        status = dajStatusGrupeIoT(naredbe.get("korisnik"), naredbe.get("lozinka")).name();

        if (status.equals(StatusKorisnika.AKTIVAN.toString())) {
            boolean result = deregistrirajGrupuIoT(naredbe.get("korisnik"), naredbe.get("lozinka"));
            if (result) {
                return "OK 10;";
            }
        }
        return "ERR 21; Grupa nije bila registrirana.";
    }

    private static Boolean deregistrirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service service = new org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service();
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster port = service.getIoTMasterPort();

        boolean result = false;
        try {
            result = port.deregistrirajGrupuIoT(korisnickoIme, korisnickaLozinka);
        } catch (ServerSOAPFaultException ex) {
        } finally {
            return result;
        }
    }

    private static Boolean aktivirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service service = new org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service();
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster port = service.getIoTMasterPort();
        return port.aktivirajGrupuIoT(korisnickoIme, korisnickaLozinka);
    }

    private String aktivirajGrupu() {
        String status = "";
        status = dajStatusGrupeIoT(naredbe.get("korisnik"), naredbe.get("lozinka")).name();

        if (status.equals(StatusKorisnika.BLOKIRAN.toString())) {
            boolean result = aktivirajGrupuIoT(naredbe.get("korisnik"), naredbe.get("lozinka"));
            if (result) {
                return "OK 10;";
            }
        }
        return "ERR 22; Grupa je vec bila aktivna ili nije registrirana.";
    }

    private String blokirajGrupu() {
        String status = "";
        status = dajStatusGrupeIoT(naredbe.get("korisnik"), naredbe.get("lozinka")).name();

        if (status.equals(StatusKorisnika.AKTIVAN.toString())) {
            boolean result = blokirajGrupuIoT(naredbe.get("korisnik"), naredbe.get("lozinka"));
            if (result) {
                return "OK 10;";
            }
        }
        return "ERR 23; Grupa nije bila aktivna.";
    }

    private static Boolean blokirajGrupuIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service service = new org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service();
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster port = service.getIoTMasterPort();
        return port.blokirajGrupuIoT(korisnickoIme, korisnickaLozinka);
    }

    private String ucitajUredjaje() {
        ucitajSveUredjajeGrupe(naredbe.get("korisnik"), naredbe.get("lozinka"));
        return "OK 10;";
    }

    private static boolean ucitajSveUredjajeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service service = new org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service();
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster port = service.getIoTMasterPort();
        return port.ucitajSveUredjajeGrupe(korisnickoIme, korisnickaLozinka);
    }

    private String obrisiUredjaje() {
        obrisiSveUredjajeGrupe(naredbe.get("korisnik"), naredbe.get("lozinka"));
        return "OK 10;";
    }

    private static Boolean obrisiSveUredjajeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service service = new org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service();
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster port = service.getIoTMasterPort();
        return port.obrisiSveUredjajeGrupe(korisnickoIme, korisnickaLozinka);
    }

    private String statusGrupe() {
        String odgovor = "OK ";
        StatusKorisnika sk = null;
        sk = dajStatusGrupeIoT(naredbe.get("korisnik"), naredbe.get("lozinka"));

        if (sk.equals(StatusKorisnika.BLOKIRAN)) {
            return odgovor + "24;";
        }
        if (sk.equals(StatusKorisnika.AKTIVAN)) {
            return odgovor + "25;";
        }
        return "";
    }

    private static StatusKorisnika dajStatusGrupeIoT(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service service = new org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service();
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster port = service.getIoTMasterPort();
        return port.dajStatusGrupeIoT(korisnickoIme, korisnickaLozinka);
    }

    private String popisUredjaja() {
        List<Uredjaj> uredjaji = null;
        String odgovor = "OK 10; ";
        uredjaji = dajSveUredjajeGrupe(naredbe.get("korisnik"), naredbe.get("lozinka"));
        for (int i = 0; i < uredjaji.size(); i++) {
            odgovor += "IoT " + Integer.toString(uredjaji.get(i).getId());
            if ((i + 1) < uredjaji.size()) {
                odgovor += ", ";
            } else {
                odgovor += ";";
            }
        }

        return odgovor;
    }

    private static java.util.List<org.foi.nwtis.dkermek.ws.serveri.Uredjaj> dajSveUredjajeGrupe(java.lang.String korisnickoIme, java.lang.String korisnickaLozinka) {
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service service = new org.foi.nwtis.dkermek.ws.serveri.IoTMaster_Service();
        org.foi.nwtis.dkermek.ws.serveri.IoTMaster port = service.getIoTMasterPort();
        return port.dajSveUredjajeGrupe(korisnickoIme, korisnickaLozinka);
    }
}
