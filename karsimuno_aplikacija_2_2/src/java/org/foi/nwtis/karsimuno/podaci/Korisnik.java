package org.foi.nwtis.karsimuno.podaci;

import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

/**
 *
 * @author Karlo
 */
public class Korisnik {

    public int id;
    public String korisnickoIme = null;
    public String lozinka = null;
    public String prezime = null;
    public String email = null;

    public Korisnik() {
    }

    public Korisnik(int id, String korisnickoIme, String lozinka, String prezime, String email) {
        this.id = id;
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
        this.prezime = prezime;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void fromJson(String korisnikJson) {
        JsonReader jsonReader = Json.createReader(new StringReader(korisnikJson));
        JsonObject object = jsonReader.readObject();

        this.id = object.getInt("id");
        this.korisnickoIme = object.getString("korisnicko_ime");
        if (object.containsKey("lozinka")) {
            this.lozinka = object.getString("lozinka");
        }
        this.prezime = object.getString("prezime");
        this.email = object.getString("email");
    }

    public String toJson() {
        JsonObjectBuilder job = Json.createObjectBuilder();
        if (id != 0) {
            job.add("id", id);
        }
        job.add("korisnicko_ime", korisnickoIme);
        if (lozinka != null) {
            job.add("lozinka", lozinka);
        }
        if (prezime != null) {
            job.add("prezime", prezime);
        }
        if (email != null) {
            job.add("email", email);
        }

        return job.build().toString();
    }
}
