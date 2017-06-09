/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.karsimuno.zrna;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import org.foi.nwtis.karsimuno.BazaHelper;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;
import org.foi.nwtis.karsimuno.podaci.Korisnik;
import org.foi.nwtis.karsimuno.slusaci.SlusacAplikacije;

/**
 *
 * @author Karlo
 */
@Named(value = "pregledKorisnika")
@RequestScoped
@ManagedBean(name = "pregledKorisnika")
public class PregledKorisnika {

    private int brojRedova;
    private BazaHelper baza;
    private int ukupnoZapisa;
    private int limitFrom = 0;
    private ResultSet rs = null;
    private Connection conn = null;
    private PreparedStatement stmt = null;

    /**
     * Creates a new instance of PregledKorisnika
     */
    public PregledKorisnika() {
    }

    public List<Korisnik> getKorisnici() {
        Konfiguracija konf = (Konfiguracija) SlusacAplikacije.getContext().getAttribute("Ostatak_Konf");
        brojRedova = Integer.parseInt(konf.dajPostavku("brojRedaka"));
        return dohvatiKorisnike();
    }

    private List<Korisnik> dohvatiKorisnike() {
        List<Korisnik> temp = new ArrayList();
        baza = new BazaHelper();

        try {
            conn = baza.spojiBazu();

            String sql = "SELECT * FROM korisnici";
            if (brojRedova != 0) {
                sql += " LIMIT " + limitFrom + ", " + brojRedova;
            }
            
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                temp.add(new Korisnik(rs.getInt("id"), rs.getString("korisnicko_ime"), rs.getString("lozinka")));
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            baza.otkvaciBazu();
        }
        return temp;
    }

    public int getBrojRedova() {
        return brojRedova;
    }

    public int getLimitFrom() {
        return limitFrom;
    }

    public void setLimitFrom(int limitFrom) {
        this.limitFrom = limitFrom;
    }

    public String prva() {
        limitFrom = 0;
        return "";
    }

    public String prethodna() {
        if ((limitFrom - brojRedova) >= 0) {
            limitFrom -= brojRedova;
        }
        return "";
    }

    public String sljedeca() {
        prebrojiZapise();
        if ((limitFrom + brojRedova) <= ukupnoZapisa) {
            limitFrom += brojRedova;
        }
        return "";
    }

    public String zadnja() {
        prebrojiZapise();
        limitFrom = ukupnoZapisa - brojRedova + (ukupnoZapisa % brojRedova);
        return "";
    }

    private void prebrojiZapise() {
        baza = new BazaHelper();
        try {
            conn = baza.spojiBazu();

            String sql = "SELECT count(*) FROM korisnici";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                ukupnoZapisa = rs.getInt(1);
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            baza.otkvaciBazu();
        }
    }
}
