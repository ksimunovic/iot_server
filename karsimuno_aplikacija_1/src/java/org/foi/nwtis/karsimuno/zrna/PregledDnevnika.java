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
import org.foi.nwtis.karsimuno.podaci.Dnevnik;
import org.foi.nwtis.karsimuno.slusaci.SlusacAplikacije;

/**
 *
 * @author Karlo
 */
@Named(value = "pregledDnevnika")
@RequestScoped
@ManagedBean(name = "pregledDnevnika")
public class PregledDnevnika {

    private static int brojRedova = 0;
    private BazaHelper baza;
    private int limitFrom = 0;
    private int ukupnoZapisa;
    private ResultSet rs = null;
    private Connection conn = null;
    private PreparedStatement stmt = null;

    /**
     * Creates a new instance of PregledDnevnika
     */
    public PregledDnevnika() {
    }

    public List<Dnevnik> getDnevnickiZapisi() {
        if (brojRedova == 0) {
            Konfiguracija konf = (Konfiguracija) SlusacAplikacije.getContext().getAttribute("Ostatak_Konf");
            brojRedova = Integer.parseInt(konf.dajPostavku("brojRedaka"));
        }
        return dohvatiDnevnik();
    }

    private List<Dnevnik> dohvatiDnevnik() {
        List<Dnevnik> temp = new ArrayList();
        baza = new BazaHelper();
        try {
            conn = baza.spojiBazu();

            String sql = "SELECT * FROM dnevnik";

            if (brojRedova != 0) {
                sql += " LIMIT " + limitFrom + ", " + brojRedova;
            }

            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                temp.add(new Dnevnik(rs.getInt("id"), rs.getString("korisnik"), rs.getString("url"), rs.getString("ipadresa"), rs.getTimestamp("vrijeme"), rs.getInt("trajanje"), rs.getInt("status")));
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

            String sql = "SELECT count(*) FROM dnevnik";
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
