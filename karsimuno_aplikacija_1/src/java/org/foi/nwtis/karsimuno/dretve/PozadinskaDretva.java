package org.foi.nwtis.karsimuno.dretve;

import static java.lang.Thread.sleep;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.dkermek.ws.serveri.Lokacija;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;
import org.foi.nwtis.karsimuno.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.karsimuno.podaci.MeteoPodaci;
import org.foi.nwtis.karsimuno.rest.klijenti.OWMKlijent;
import org.foi.nwtis.karsimuno.slusaci.SlusacAplikacije;

/**
 ** pokreće dretvu (konfiguracijom se određuje pravilni vremenski interval
 * (jedinica je sekunda) preuzimanja podataka, npr. 30 sec, 100 sec, 2 min, 10
 * min, 30 min, 60 min, ...) koja preuzima važeće meteorološke podatke od
 * openweathermap.org web servisa (u prilogu se nalazi opis postupka) za
 * izabrani skup IoT uređaja na bazi njihovih lokacijskih podataka
 *
 * @author Administrator
 */
public class PozadinskaDretva extends Thread {

    OWMKlijent owmk;
    private final String apikey;
    private final Konfiguracija konf;
    private final BP_Konfiguracija BP_Konf;
    private final int intervalDretveZaMeteoPodatke;

    public PozadinskaDretva(Konfiguracija konf) {
        this.konf = konf;
        intervalDretveZaMeteoPodatke = Integer.parseInt(konf.dajPostavku("intervalDretveZaMeteoPodatke")) * 1000;
        this.apikey = konf.dajPostavku("apikey");
        BP_Konf = (BP_Konfiguracija) SlusacAplikacije.getContext().getAttribute("BP_Konfig");
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public void run() {
        super.run();

        owmk = new OWMKlijent(apikey);

        int iter = 0;//XXX: temp
        while (iter < 10) {//XXX: temp
            long trenutnoVrijeme = System.currentTimeMillis();
            System.out.println("Pozdrav iz pozadinske dretvee!");

            preuzmiPodatkeZaUredjaje();

            try {
                long vrijemeZavršetka = System.currentTimeMillis();
                long sleepTime = intervalDretveZaMeteoPodatke - (vrijemeZavršetka - trenutnoVrijeme);
                sleepTime = sleepTime <= 0 ? 1 : sleepTime;
                sleep(sleepTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(PozadinskaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }
            iter++; //XXX: temp
        }
    }

    /**
     * Preuzima uređaje iz baze podataka i za svaki uređaj dohvaća trenutne
     * meteo podatke te ih sprema u bazu
     */
    private void preuzmiPodatkeZaUredjaje() {
        HashMap<Lokacija, MeteoPodaci> cache = new HashMap<>();

        String database = BP_Konf.getServerDatabase() + BP_Konf.getUserDatabase();
        String user = BP_Konf.getUserUsername();
        String pass = BP_Konf.getUserPassword();

        String sql;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        Connection conn = null;
        try {
            Class.forName(BP_Konf.getDriverDatabase());
            conn = DriverManager.getConnection(database, user, pass);

            sql = "SELECT * FROM uredaji";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {

                Lokacija l = new Lokacija();
                l.setLatitude(rs.getString("latitude"));
                l.setLongitude(rs.getString("longitude"));

                MeteoPodaci mp = null;
                for (Map.Entry<Lokacija, MeteoPodaci> entry : cache.entrySet()) {
                    Lokacija key = entry.getKey();
                    MeteoPodaci value = entry.getValue();
                    if (key.getLatitude().equals(l.getLatitude())
                            && key.getLongitude().equals(l.getLongitude())) {
                        System.out.print("OPTIMIZIRANO! ");
                        mp = value;
                        break;
                    }
                }

                if (mp == null) {
                    mp = owmk.getRealTimeWeather(rs.getString("latitude"), rs.getString("longitude"));
                    cache.put(l, mp);
                }

                sql = "INSERT INTO meteo (id, adresastanice, latitude, longitude, vrijeme, vrijemeOpis, temp, tempMin, tempMax, vlaga, tlak, vjetar, vjetarSmjer)"
                        + "VALUES( ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?,  ?, ?)";

                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, rs.getInt("id"));
                stmt.setString(2, rs.getString("latitude") + ";" + rs.getString("longitude"));
                stmt.setFloat(3, rs.getFloat("latitude"));
                stmt.setFloat(4, rs.getFloat("longitude"));
                stmt.setString(5, Integer.toString(mp.getWeatherNumber()));
                stmt.setString(6, mp.getWeatherValue());
                stmt.setFloat(7, mp.getTemperatureValue());
                stmt.setFloat(8, mp.getTemperatureMin());
                stmt.setFloat(9, mp.getTemperatureMax());
                stmt.setFloat(10, mp.getHumidityValue());
                stmt.setFloat(11, mp.getPressureValue());
                stmt.setFloat(12, mp.getWindSpeedValue());
                stmt.setFloat(13, mp.getWindDirectionValue());

                if (stmt.executeUpdate() == 1) {
                    System.out.println("Meteo podaci uređaja '" + rs.getString("naziv") + "' uspješno upisani. ");
                } else {
                    System.out.println("Pogreška kod dodavanja uređaja '" + rs.getString("naziv") + "' u bazu.");
                }
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
    }
}
