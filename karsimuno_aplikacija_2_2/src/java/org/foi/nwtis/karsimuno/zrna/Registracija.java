/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.karsimuno.zrna;

import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import org.foi.nwtis.karsimuno.ejb.sb.StatefulSB;

/**
 *
 * @author Karlo
 */
@Named(value = "registracija")
@RequestScoped
public class Registracija {

    @EJB
    private StatefulSB statefulSB;
    
    private String korisnickoIme;
    private String prezime;
    private String lozinka;
    private String ponovljenaLozinka;
    private String email;
    private int errorCode = -1;
    
    /**
     * Creates a new instance of Registracija
     */
    public Registracija() {
    }

    public String getKoriscnikoIme() {
        return korisnickoIme;
    }

    public void setKoriscnikoIme(String koriscnikoIme) {
        this.korisnickoIme = koriscnikoIme;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getPonovljenaLozinka() {
        return ponovljenaLozinka;
    }

    public void setPonovljenaLozinka(String ponovljenaLozinka) {
        this.ponovljenaLozinka = ponovljenaLozinka;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    
    public void register(){
        errorCode = -1;
        if(korisnickoIme.isEmpty() || prezime.isEmpty() || lozinka.isEmpty() || email.isEmpty()){
            errorCode = 0;
            return;
        }
        if(!lozinka.equals(ponovljenaLozinka)){
            errorCode = 1;
            return;
        }
        if(!statefulSB.registrirajKorisnika(korisnickoIme, prezime, lozinka, email)){
            errorCode = 2;
            return;
        }
    }
    
}
