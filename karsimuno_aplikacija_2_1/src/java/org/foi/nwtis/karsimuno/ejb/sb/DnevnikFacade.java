package org.foi.nwtis.karsimuno.ejb.sb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.foi.nwtis.karsimuno.ejb.eb.Dnevnik;

/**
 *
 * @author Administrator
 */
@Stateless
public class DnevnikFacade extends AbstractFacade<Dnevnik> {

    @PersistenceContext(unitName = "karsimuno_aplikacija_2_1PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DnevnikFacade() {
        super(Dnevnik.class);
    }

    public List<Dnevnik> findFrom(int from, int results) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Dnevnik> cq = cb.createQuery(Dnevnik.class);
        cq.select(cq.from(Dnevnik.class));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(results);
        q.setFirstResult(from);
        return q.getResultList();
    }
}
