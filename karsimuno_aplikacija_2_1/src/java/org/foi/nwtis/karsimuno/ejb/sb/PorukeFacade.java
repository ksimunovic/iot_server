package org.foi.nwtis.karsimuno.ejb.sb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.foi.nwtis.karsimuno.ejb.eb.Poruke;

/**
 *
 * @author Administrator
 */
@Stateless
public class PorukeFacade extends AbstractFacade<Poruke> {

    @PersistenceContext(unitName = "karsimuno_aplikacija_2_1PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PorukeFacade() {
        super(Poruke.class);
    }

}
