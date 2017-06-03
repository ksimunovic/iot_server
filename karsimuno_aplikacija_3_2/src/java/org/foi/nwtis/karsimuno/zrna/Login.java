package org.foi.nwtis.karsimuno.zrna;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.karsimuno.ejb.sb.StatefulSB;

/**
 *
 * @author Administrator
 */
@Named(value = "login")
@RequestScoped
@ManagedBean(name = "login")
public class Login {

    @EJB
    private StatefulSB statefulSB;

    private String korisnickoIme;
    private String lozinka;
    private boolean showError;

    /**
     * Creates a new instance of Prijava
     */
    public Login() {
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public boolean getError() {
        return showError;
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

    public void login() {

        showError = true;
        if (korisnickoIme == null || korisnickoIme.isEmpty() || lozinka == null || lozinka.isEmpty()) {
            return;
        }
        showError = !statefulSB.provjeriKorisnika(korisnickoIme, lozinka);

        if (!showError) {
            try {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
                session.setAttribute("korisnik", korisnickoIme);

                HttpServletRequest origRequest = (HttpServletRequest) facesContext.getExternalContext().getRequest();
                facesContext.getExternalContext().redirect(origRequest.getContextPath() + "/");
            } catch (IOException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
