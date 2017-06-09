package org.foi.nwtis.karsimuno.dretve;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.foi.nwtis.karsimuno.BazaHelper;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;
import org.foi.nwtis.karsimuno.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.karsimuno.server.ObradaSocketNaredbi;
import org.foi.nwtis.karsimuno.server.ObradaIoTMasterNaredbi;
import org.foi.nwtis.karsimuno.server.ObradaIoTNaredbi;
import org.foi.nwtis.karsimuno.server.TestOpcija;
import org.foi.nwtis.karsimuno.slusaci.SlusacAplikacije;

/**
 *
 * @author Karlo
 */
class RadnaDretva extends Thread {

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

        } catch (IOException ex) {
            ServerDretva.ukloniAktivnuDretvu(this.getName());
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            zatvoriVezuSaServerom();
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
        String korisnik = "nepoznat";
        Boolean mailNaredba = false;

        odgovor = "";
        naredbe = provjeriNaredbe.serverSocketNaredbe(args);
        if (naredbe != null) {
            korisnik = naredbe.get("korisnik");
            String statusPrava = provjeriKorisnika();
            if (!"OK;".equals(statusPrava)) {
                odgovor = statusPrava;
                return;
            }

            ObradaSocketNaredbi obradaSocketNaredbi = new ObradaSocketNaredbi(socket, naredbe);
            odgovor = obradaSocketNaredbi.izvrsiNaredbu();

            if ("STOP".equals(odgovor)) {
                cekajKrajServera = true;
                odgovor = "OK 10;";
            }
            mailNaredba = true;
        }

        naredbe = provjeriNaredbe.IoTMasterNaredbe(args);
        if (naredbe != null && !ServerDretva.pauzirajServer) {
            korisnik = naredbe.get("korisnik");
            String statusPrava = provjeriKorisnika();
            if (!"OK;".equals(statusPrava)) {
                odgovor = statusPrava;
                return;
            }

            ObradaIoTMasterNaredbi obradaIoTMasterNaredbi = new ObradaIoTMasterNaredbi(socket, naredbe);
            odgovor = obradaIoTMasterNaredbi.izvrsiNaredbu();
        }

        naredbe = provjeriNaredbe.IoTNaredbe(args);
        if (naredbe != null && !ServerDretva.pauzirajServer) {
            korisnik = naredbe.get("korisnik");
            String statusPrava = provjeriKorisnika();
            if (!"OK;".equals(statusPrava)) {
                odgovor = statusPrava;
                return;
            }

            ObradaIoTNaredbi obradaIoTNaredbi = new ObradaIoTNaredbi(naredbe);
            odgovor = obradaIoTNaredbi.izvrsiNaredbu();
        }

        if (odgovor.isEmpty() && ServerDretva.zavrsiRadServera) {
            odgovor = "ERR 12; Server je u postupku prekida.";
        } else if (odgovor.isEmpty()) {
            odgovor = "ERR 1000: Pogresna komanda"; //TODO: staviti pravi broj errora
        }

        if (!odgovor.contains("ERR") && mailNaredba) {
            posaljiMail();
        }

        zabiljeziZahtjev(korisnik, sb.toString(), odgovor);
    }

    /**
     * Ako je ovo dretva koja je primila naredbu zaustavljanja servera onda čeka
     * da se server zaustavi pa tek onda zatvara socket i pripadajuće tokove
     */
    private void zatvoriVezuSaServerom() {
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

            ServerDretva.ukloniAktivnuDretvu(this.getName());
        } catch (IOException ex) {
            Logger.getLogger(RadnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    /**
     * U slučaju uspješne komande vrste Server socket potrebno je poslati email
     * poruku (adresa primatelja, adresa pošiljatelja i predmet poruke određuju
     * se postavkama) u MIME tipu „text/plain“ s informacijama o zahtjevu (u
     * prvi redak kopira se sadržaj komande, a zatim u sljedećem retku slijede
     * podaci o vremenu primanja zahtjeva u obliku: dd.MM.yyyy hh.mm.ss.zzz).
     */
    private void posaljiMail() {
        Konfiguracija konf = (Konfiguracija) SlusacAplikacije.getContext().getAttribute("Ostatak_Konf");
        String to = konf.dajPostavku("mail.receiver");
        String from = konf.dajPostavku("mail.sender");

        java.util.Properties properties = System.getProperties();
        properties.put("mail.smtp.host", konf.dajPostavku("mail.server"));

        try {
            Session session = Session.getInstance(properties, null);
            MimeMessage message = new MimeMessage(session);

            Address fromAddress = new InternetAddress(from);
            message.setFrom(fromAddress);

            Address[] toAddresses = InternetAddress.parse(to);
            message.setRecipients(Message.RecipientType.TO, toAddresses);

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh.mm.ss.zzz");
            String datum = sdf.format(new Date());

            message.setSubject(konf.dajPostavku("mail.subject"));
            message.setText(odgovor + "\r\n" + datum);
            message.setSentDate(new Date());

            Transport.send(message);
            System.out.println("Mail uspješno poslan...");
        } catch (MessagingException ex) {
        }
    }

    /**
     * Kreirati tablicu korisnici (id, korisnik, lozinka) i prema njoj
     * provjeriti korisnika
     */
    String provjeriKorisnika() {

        BP_Konfiguracija BP_Konf = (BP_Konfiguracija) SlusacAplikacije.getContext().getAttribute("BP_Konfig");

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
                prava = "ERR 10; Ne postoji takav korisnik.";
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

    private void zabiljeziZahtjev(String korisnik, String naredba, String odgovor) {
        BazaHelper baza = new BazaHelper();
        PreparedStatement stmt;
        Connection conn;

        try {
            conn = baza.spojiBazu();

            String sql = "INSERT INTO zahtjevi (korisnik, naredba, odgovor) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, korisnik);
            stmt.setString(2, naredba);
            stmt.setString(3, odgovor);

            stmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            baza.otkvaciBazu();
        }
    }
}
