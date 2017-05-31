/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.karsimuno.zrna;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.karsimuno.BazaHelper;

/**
 *
 * @author Administrator
 */
@Named(value = "login")
@RequestScoped
@ManagedBean(name = "login")
public class Login_BRISI {

    private String korisnickoIme;
    private String lozinka;
    private String error;

    /**
     * Creates a new instance of Testni
     */
    public Login_BRISI() {
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String login() {

        if (provjeriKorisnika(korisnickoIme, lozinka)) {
            error = "super";
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
                session.setAttribute("korisnik", korisnickoIme);
                
                HttpServletRequest origRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
                facesContext.getExternalContext().redirect(origRequest.getContextPath() + "/");
            } catch (IOException ex) {
                Logger.getLogger(Login_BRISI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            error = "greska";
        }
        return "";
    }

    private boolean provjeriKorisnika(String korisnickoIme, String lozinka) {
        BazaHelper baza = new BazaHelper();
        ResultSet rs = null;
        PreparedStatement stmt = null;
        boolean autentificiran = false;

        if ((korisnickoIme == null || lozinka == null || korisnickoIme.length() == 0 || lozinka.length() == 0)) {
            return false;
        }

        try {
            Connection conn = baza.spojiBazu();

            String sql = "SELECT * FROM korisnici WHERE korisnicko_ime = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, korisnickoIme);

            rs = stmt.executeQuery();
            if (rs.next()) {
                if (rs.getString("lozinka").equals(lozinka)) {
                    autentificiran = true;
                }
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            baza.otkvaciBazu();
        }

        return autentificiran;
    }
}
