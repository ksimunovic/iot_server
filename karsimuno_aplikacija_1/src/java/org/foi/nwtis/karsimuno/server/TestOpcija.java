package org.foi.nwtis.karsimuno.server;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Glavna klasa za provjeru parametara za pokretanja i zaprimljenih naredbi
 *
 * @author Karlo
 */
public class TestOpcija {

    Pattern pattern;
    Matcher m;

    /**
     * Provjerava je li primljena naredba administratorska te je li ispravna
     *
     * @param args Naredba primljena od korisnika sustava
     * @return HashMapa koja sadrži informacije naredbe ako su one pravilne u
     * suprotnome null
     */
    public HashMap<String, String> serverSocketNaredbe(String[] args) {
        HashMap<String, String> opcijeKorisnika = new HashMap<>();

        StringBuilder sb = new StringBuilder();
        sb.append(String.join(" ", args));

        String sintaksa = "^USER ([^\\s]+); PASSWD ([^\\s]+); (PAUSE|STOP|START|STATUS);$";

        pattern = Pattern.compile(sintaksa);
        m = pattern.matcher(sb);
        if (m.matches()) {
            opcijeKorisnika.put("korisnik", m.group(1));
            opcijeKorisnika.put("lozinka", m.group(2));
            opcijeKorisnika.put("naredba", m.group(3));
            return opcijeKorisnika;
        }

        return null;
    }

    /**
     * Provjerava je li klijent pokrenut s korisnik parametrima pomoću
     * regularnih izraza i onda vraća te opcije u obliku za daljnje korištenje
     *
     * @param args Argumenti pokretanja klase KorisnikSustava
     * @return HashMapa koja sadrži opcije pokretanja ako su one pravilne u
     * suprotnome null
     */
    public HashMap<String, String> IoTMasterNaredbe(String[] args) {
        HashMap<String, String> opcijeKorisnika = new HashMap<>();

        StringBuilder sb = new StringBuilder();
        sb.append(String.join(" ", args));

        String sintaksa1 = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT_Master (START|STOP|WORK|WAIT|LOAD|CLEAR|STATUS|LIST);$";

        pattern = Pattern.compile(sintaksa1);
        m = pattern.matcher(sb);
        if (m.matches()) {
            opcijeKorisnika.put("korisnik", m.group(1));
            opcijeKorisnika.put("lozinka", m.group(2));
            opcijeKorisnika.put("naredba", m.group(3));
            return opcijeKorisnika;
        }
        return null;
    }

    /**
     * Provjerava je li klijent pokrenut s korisnik parametrima pomoću
     * regularnih izraza i onda vraća te opcije u obliku za daljnje korištenje
     *
     * @param args Argumenti pokretanja klase KorisnikSustava
     * @return HashMapa koja sadrži opcije pokretanja ako su one pravilne u
     * suprotnome null
     */
    public HashMap<String, String> IoTNaredbe(String[] args) {
        HashMap<String, String> opcijeKorisnika = new HashMap<>();

        StringBuilder sb = new StringBuilder();
        sb.append(String.join(" ", args));

        String sintaksa1 = "^USER ([^\\s]+); PASSWD ([^\\s]+); IoT (\\d{1,6}) ((ADD) \"([^\\s]+)\"|WORK|WAIT|REMOVE|STATUS);$";

        pattern = Pattern.compile(sintaksa1);
        m = pattern.matcher(sb);
        if (m.matches()) {
            opcijeKorisnika.put("korisnik", m.group(1));
            opcijeKorisnika.put("lozinka", m.group(2));
            opcijeKorisnika.put("iot", m.group(3));
            opcijeKorisnika.put("naredba", m.group(4));
            if (m.group(5) != null) {
                opcijeKorisnika.put("naredba", m.group(5));
                opcijeKorisnika.put("naziv", m.group(6));
                opcijeKorisnika.put("adresa", m.group(7));
            }
            return opcijeKorisnika;
        }
        return null;
    }
}
