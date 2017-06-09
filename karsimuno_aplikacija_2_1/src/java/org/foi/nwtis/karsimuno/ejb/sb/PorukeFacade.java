package org.foi.nwtis.karsimuno.ejb.sb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.foi.nwtis.karsimuno.ejb.eb.Poruke;

/**
 *
 * @author Karlo
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

    public List<Poruke> findFrom(int limitFrom, int brojRedova) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Poruke> cq = cb.createQuery(Poruke.class);
        cq.select(cq.from(Poruke.class));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(brojRedova);
        q.setFirstResult(limitFrom);
        return q.getResultList();
    }

    public void removeAll() {
        em.createQuery("DELETE FROM Poruke").executeUpdate();
        em.clear();
    }

}
