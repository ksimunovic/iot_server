package org.foi.nwtis.karsimuno.podaci;

import java.io.StringReader;
import java.sql.Timestamp;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

/**
 *
 * @author Administrator
 */
public class Uredjaj {

    public int id;
    public String naziv;
    public Float latitude;
    public Float longitude;
    public String adresa;
    public int status;
    public Timestamp vrijemePromjene;
    public Timestamp vrijemeKreiranja;

    public Uredjaj() {
    }

    public Uredjaj(int id, String naziv, Float latitude, Float longitude, String adresa, int status, Timestamp vrijemePromjene, Timestamp vrijemeKreiranja) {
        this.id = id;
        this.naziv = naziv;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adresa = adresa;
        this.status = status;
        this.vrijemePromjene = vrijemePromjene;
        this.vrijemeKreiranja = vrijemeKreiranja;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Timestamp getVrijemePromjene() {
        return vrijemePromjene;
    }

    public void setVrijemePromjene(Timestamp vrijemePromjene) {
        this.vrijemePromjene = vrijemePromjene;
    }

    public Timestamp getVrijemeKreiranja() {
        return vrijemeKreiranja;
    }

    public void setVrijemeKreiranja(Timestamp vrijemeKreiranja) {
        this.vrijemeKreiranja = vrijemeKreiranja;
    }

    public void fromJson(String json) {
        JsonReader jsonReader = Json.createReader(new StringReader(json));
        JsonObject object = jsonReader.readObject();

        this.id = object.getInt("id");
        this.naziv = object.getString("naziv");
        this.latitude = object.getJsonNumber("latitude").bigDecimalValue().floatValue();
        this.longitude = object.getJsonNumber("longitude").bigDecimalValue().floatValue();
        this.status = object.getInt("status");
        this.vrijemePromjene = new Timestamp(object.getJsonNumber("vrijeme_promjene").longValue());
        this.vrijemeKreiranja = new Timestamp(object.getJsonNumber("vrijeme_kreiranja").longValue());
    }

    public String toJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        if (id != 0) {
            job.add("id", id);
        }
        job.add("naziv", naziv);
        job.add("latitude", latitude.toString());
        job.add("longitude", longitude.toString());
        job.add("status", status);
        if (vrijemePromjene != null) {
            job.add("vrijeme_promjene", vrijemePromjene.getTime());
        }
        if (vrijemeKreiranja != null) {
            job.add("vrijeme_kreiranja", vrijemeKreiranja.getTime());
        }

        return job.build().toString();
    }
}
