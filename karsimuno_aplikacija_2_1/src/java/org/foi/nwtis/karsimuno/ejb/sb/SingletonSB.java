package org.foi.nwtis.karsimuno.ejb.sb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import org.foi.nwtis.karsimuno.dretve.ObradaMaila;
import org.foi.nwtis.karsimuno.konfiguracije.Konfiguracija;
import org.foi.nwtis.karsimuno.konfiguracije.bp.BP_Konfiguracija;

/**
 * Kreiranje Singleton SB pokreće dretvu (konfiguracijom se određuje pravilni
 * vremenski interval rada (jedinica je sekunda), npr. 5 sec, 20 sec, 100 sec,
 * ...) koja provjerava u poštanskom sandučiću (adresa poslužitelja, korisničko
 * ime i lozinka definiraju se u konfiguracijskoj datoteci) pristiglu poštu.
 * Brisanje Singleton SB prekida dretvu i zaustavlja ju.
 *
 * @author Karlo
 */
@Singleton
@LocalBean
public class SingletonSB {

    public static Konfiguracija konf;
    public static BP_Konfiguracija BP_Konfig;
    private ObradaMaila mail;

    public void start() {
    }

    @PostConstruct
    public void initialize() {
        System.out.println("POKREĆEM SINGLETON SB!");
        mail = new ObradaMaila();
        mail.start(); //FIXME: mailObrada Dretva
    }

    @PreDestroy
    public void destroy() {
        System.out.println("GASIM SINGLETON SB!");
        mail.interrupt();
    }
}
