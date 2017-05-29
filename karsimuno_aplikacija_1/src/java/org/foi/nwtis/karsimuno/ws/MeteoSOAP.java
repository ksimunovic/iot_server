package org.foi.nwtis.karsimuno.ws;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.dkermek.ws.serveri.Lokacija;
import org.foi.nwtis.dkermek.ws.serveri.Uredjaj;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;
import org.foi.nwtis.karsimuno.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.karsimuno.podaci.MeteoPodaci;
import org.foi.nwtis.karsimuno.rest.klijenti.GMRESTHelper;
import org.foi.nwtis.karsimuno.rest.klijenti.OWMKlijent;
import org.foi.nwtis.karsimuno.slusaci.SlusacAplikacije;

/**
 *
 * @author Karlo
 */
//@WebService(serviceName = "MeteoSOAP") //FIXME: SOAP
public class MeteoSOAP {

    private ResultSet rs = null;
    private PreparedStatement stmt = null;
    private Connection conn = null;

    // adresa izabranog IoT uređaja na bazi njegove geolokacije (reverse geocoding, address lookup). Uzima se atribut "formatted_address" iz objekta koji je dobiven iz atributa „results“. Npr. http://maps.google.com/maps/api/geocode/json?latlng=46.3076267,16.3382566 gdje su upisani lokacijski podaci za FOI.
    @WebMethod(operationName = "dajAdresuUredjaja")
    public String dajAdresuUredjaja(@WebParam(name = "id") int id) {
        Uredjaj u = dohvatiUredjajIzBaze(id);
        if (u != null) {
            return dohvatiAdresuGeolokacijom(Float.parseFloat(u.getGeoloc().getLatitude()), Float.parseFloat(u.getGeoloc().getLongitude()));
        } else {
            return "";
        }
    }

    // važeći meteorološki podaci za izabrani IoT uređaj (putem openweathermap.org web servisa)
    @WebMethod(operationName = "dajVazeceMeteoPodatkeZaUredjaj")
    public MeteoPodaci dajVazeceMeteoPodatkeZaUredjaj(@WebParam(name = "id") int id) {
        MeteoPodaci mp = new MeteoPodaci();
        Konfiguracija konf = (Konfiguracija) SlusacAplikacije.getContext().getAttribute("Ostatak_Konf");

        OWMKlijent owmk = new OWMKlijent(konf.dajPostavku("apikey"));

        Uredjaj u = dohvatiUredjajIzBaze(id);
        if (u != null) {
            return owmk.getRealTimeWeather(u.getGeoloc().getLatitude(), u.getGeoloc().getLongitude());
        } else {
            return null;
        }
    }

    // posljednjih n (n se unosi) meteoroloških podataka za izabrani IoT uređaj
    @WebMethod(operationName = "dajNZadnjihMeteoPodatkaZaUredjaj")
    public List<MeteoPodaci> dajNZadnjihMeteoPodatkaZaUredjaj(@WebParam(name = "id") int id, @WebParam(name = "count") int count) {
        List<MeteoPodaci> mp = new ArrayList<>();

        try {
            spojiBazu();

            String sql = "SELECT * FROM METEO WHERE ID = ? ORDER BY \"PREUZETO\" DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            while (rs.next()) {
                mp.add(new MeteoPodaci(new Date(), new Date(), rs.getFloat("temp"), rs.getFloat("tempMin"), rs.getFloat("tempMax"),
                        "°C", rs.getFloat("vlaga"), "%", rs.getFloat("tlak"), "hPa", rs.getFloat("vjetar"), "", rs.getFloat("vjetarSmjer"),
                        "", "", 1, "", "", 0.0f, "", "", Integer.parseInt(rs.getString("vrijeme")), rs.getString("vrijemeOpis"), "",
                        rs.getDate("preuzeto")));

                if (mp.size() >= count) {
                    break;
                }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            otkvaciBazu();
        }
        return mp;
    }

    // zadnje preuzeti meteorološki podaci za izabrani IoT uređaj
    @WebMethod(operationName = "dajZadnjeMeteoPodatkeZaUredjaj")
    public MeteoPodaci dajZadnjeMeteoPodatkeZaUredjaj(@WebParam(name = "id") int id) {
        MeteoPodaci mp = new MeteoPodaci();

        try {
            spojiBazu();

            String sql = "SELECT * FROM METEO WHERE ID = ? ORDER BY \"PREUZETO\" DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                mp = new MeteoPodaci(new Date(), new Date(), rs.getFloat("temp"), rs.getFloat("tempMin"), rs.getFloat("tempMax"),
                        "°C", rs.getFloat("vlaga"), "%", rs.getFloat("tlak"), "hPa", rs.getFloat("vjetar"), "", rs.getFloat("vjetarSmjer"),
                        "", "", 1, "", "", 0.0f, "", "", Integer.parseInt(rs.getString("vrijeme")), rs.getString("vrijemeOpis"), "",
                        rs.getDate("preuzeto"));
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            otkvaciBazu();
        }
        return mp;
    }

    // meteorološki podaci za izabrani IoT uređaj u nekom vremenskom intervalu (od datuma, do datuma)
    @WebMethod(operationName = "dajSveMeteoPodatkeZaUredjaj")
    public List<MeteoPodaci> dajSveMeteoPodatkeZaUredjaj(@WebParam(name = "id") int id, @WebParam(name = "from") long from, @WebParam(name = "to") long to) {
        List<MeteoPodaci> mp = null;

        try {
            spojiBazu();

            Date dateFrom = new Date(from * 1000);
            Date dateTo = new Date(to * 1000);
            String sql = "SELECT * FROM meteo WHERE id = ? AND preuzeto >= ? AND preuzeto <= ?";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setTimestamp(2, new Timestamp(dateFrom.getTime()));
            stmt.setTimestamp(3, new Timestamp(dateTo.getTime()));
            rs = stmt.executeQuery();

            while (rs.next()) {
                if (mp == null) {
                    mp = new ArrayList<>();
                }
                mp.add(new MeteoPodaci(new Date(), new Date(), rs.getFloat("temp"), rs.getFloat("tempMin"), rs.getFloat("tempMax"),
                        "°C", rs.getFloat("vlaga"), "%", rs.getFloat("tlak"), "hPa", rs.getFloat("vjetar"), "", rs.getFloat("vjetarSmjer"),
                        "", "", 1, "", "", 0.0f, "", "", Integer.parseInt(rs.getString("vrijeme")), rs.getString("vrijemeOpis"), "",
                        rs.getDate("preuzeto")));
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            otkvaciBazu();
        }
        return mp;
    }

    private void spojiBazu() throws SQLException, ClassNotFoundException {
        BP_Konfiguracija BP_Konf = (BP_Konfiguracija) SlusacAplikacije.getContext().getAttribute("BP_Konfig");

        String database = BP_Konf.getServerDatabase() + BP_Konf.getUserDatabase();
        String user = BP_Konf.getUserUsername();
        String pass = BP_Konf.getUserPassword();

        Class.forName(BP_Konf.getDriverDatabase());
        conn = DriverManager.getConnection(database, user, pass);
    }

    private void otkvaciBazu() {
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

    private Uredjaj dohvatiUredjajIzBaze(Integer id) {
        Uredjaj uredjaj = null;

        try {
            spojiBazu();
            String sql = "SELECT * FROM uredaji WHERE id = ? ";

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                uredjaj = new Uredjaj();
                Lokacija l = new Lokacija();
                l.setLatitude(rs.getString("latitude"));
                l.setLongitude(rs.getString("longitude"));
                uredjaj.setGeoloc(l);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            otkvaciBazu();
        }

        return uredjaj;
    }

    /**
     * Za danu geo lokaciju vraća tekstualnu adresu
     *
     * @param latitude Latitude adrese
     * @param longitude Longitude adrese
     * @return Adresa u obliku teksta
     */
    private String dohvatiAdresuGeolokacijom(Float latitude, Float longitude) {
        Client client = ClientBuilder.newClient();

        String adresa = latitude.toString() + "," + longitude.toString();
        WebTarget webResource = client.target(GMRESTHelper.getGM_BASE_URI())
                .path("maps/api/geocode/json");
        try {
            webResource = webResource.queryParam("latlng",
                    URLEncoder.encode(adresa, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
        webResource = webResource.queryParam("sensor", "false");
        String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);

        JsonReader reader = Json.createReader(new StringReader(odgovor));
        JsonObject jo = reader.readObject();
        JsonObject obj = jo.getJsonArray("results").getJsonObject(0);

        return obj.getString("formatted_address");
    }
}
