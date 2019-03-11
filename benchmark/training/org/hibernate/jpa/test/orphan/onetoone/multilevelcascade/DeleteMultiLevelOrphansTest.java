/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.orphan.onetoone.multilevelcascade;


import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 * @author Gail Badner
 */
public class DeleteMultiLevelOrphansTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9091")
    public void testDirectAssociationOrphanedWhileManaged() {
        createData();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        List results = em.createQuery("from Tranchenmodell").getResultList();
        Assert.assertEquals(1, results.size());
        results = em.createQuery("from Preisregelung").getResultList();
        Assert.assertEquals(1, results.size());
        Preisregelung preisregelung = ((Preisregelung) (results.get(0)));
        Tranchenmodell tranchenmodell = preisregelung.getTranchenmodell();
        Assert.assertNotNull(tranchenmodell);
        Assert.assertNotNull(tranchenmodell.getX());
        Assert.assertEquals(2, tranchenmodell.getTranchen().size());
        Assert.assertNotNull(tranchenmodell.getTranchen().get(0).getY());
        preisregelung.setTranchenmodell(null);
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        preisregelung = ((Preisregelung) (em.find(Preisregelung.class, preisregelung.getId())));
        Assert.assertNull(preisregelung.getTranchenmodell());
        results = em.createQuery("from Tranchenmodell").getResultList();
        Assert.assertEquals(0, results.size());
        results = em.createQuery("from Tranche").getResultList();
        Assert.assertEquals(0, results.size());
        results = em.createQuery("from X").getResultList();
        Assert.assertEquals(0, results.size());
        results = em.createQuery("from Y").getResultList();
        Assert.assertEquals(0, results.size());
        results = em.createQuery("from Preisregelung").getResultList();
        Assert.assertEquals(1, results.size());
        em.getTransaction().commit();
        em.close();
        cleanupData();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9091")
    public void testReplacedDirectAssociationWhileManaged() {
        createData();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        List results = em.createQuery("from Tranchenmodell").getResultList();
        Assert.assertEquals(1, results.size());
        results = em.createQuery("from Preisregelung").getResultList();
        Assert.assertEquals(1, results.size());
        Preisregelung preisregelung = ((Preisregelung) (results.get(0)));
        Tranchenmodell tranchenmodell = preisregelung.getTranchenmodell();
        Assert.assertNotNull(tranchenmodell);
        Assert.assertNotNull(tranchenmodell.getX());
        Assert.assertEquals(2, tranchenmodell.getTranchen().size());
        Assert.assertNotNull(tranchenmodell.getTranchen().get(0).getY());
        // Create a new Tranchenmodell with new direct and nested associations
        Tranchenmodell tranchenmodellNew = new Tranchenmodell();
        X xNew = new X();
        tranchenmodellNew.setX(xNew);
        xNew.setTranchenmodell(tranchenmodellNew);
        Tranche trancheNew = new Tranche();
        tranchenmodellNew.getTranchen().add(trancheNew);
        trancheNew.setTranchenmodell(tranchenmodellNew);
        Y yNew = new Y();
        trancheNew.setY(yNew);
        yNew.setTranche(trancheNew);
        // Replace with a new Tranchenmodell instance containing new direct and nested associations
        preisregelung.setTranchenmodell(tranchenmodellNew);
        tranchenmodellNew.setPreisregelung(preisregelung);
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        results = em.createQuery("from Tranche").getResultList();
        Assert.assertEquals(1, results.size());
        results = em.createQuery("from Tranchenmodell").getResultList();
        Assert.assertEquals(1, results.size());
        results = em.createQuery("from X").getResultList();
        Assert.assertEquals(1, results.size());
        results = em.createQuery("from Y").getResultList();
        Assert.assertEquals(1, results.size());
        results = em.createQuery("from Preisregelung").getResultList();
        Assert.assertEquals(1, results.size());
        preisregelung = ((Preisregelung) (results.get(0)));
        tranchenmodell = preisregelung.getTranchenmodell();
        Assert.assertNotNull(tranchenmodell);
        Assert.assertEquals(tranchenmodellNew.getId(), tranchenmodell.getId());
        Assert.assertNotNull(tranchenmodell.getX());
        Assert.assertEquals(xNew.getId(), tranchenmodell.getX().getId());
        Assert.assertEquals(1, tranchenmodell.getTranchen().size());
        Assert.assertEquals(trancheNew.getId(), tranchenmodell.getTranchen().get(0).getId());
        Assert.assertEquals(yNew.getId(), tranchenmodell.getTranchen().get(0).getY().getId());
        // Replace with a new Tranchenmodell instance with no associations
        tranchenmodellNew = new Tranchenmodell();
        preisregelung.setTranchenmodell(tranchenmodellNew);
        tranchenmodellNew.setPreisregelung(preisregelung);
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        results = em.createQuery("from Tranchenmodell").getResultList();
        Assert.assertEquals(1, results.size());
        tranchenmodell = ((Tranchenmodell) (results.get(0)));
        Assert.assertEquals(tranchenmodellNew.getId(), tranchenmodell.getId());
        results = em.createQuery("from Preisregelung").getResultList();
        Assert.assertEquals(1, results.size());
        preisregelung = ((Preisregelung) (results.get(0)));
        Assert.assertEquals(tranchenmodell, preisregelung.getTranchenmodell());
        results = em.createQuery("from Tranche").getResultList();
        Assert.assertEquals(0, results.size());
        results = em.createQuery("from X").getResultList();
        Assert.assertEquals(0, results.size());
        results = em.createQuery("from Y").getResultList();
        Assert.assertEquals(0, results.size());
        em.getTransaction().commit();
        em.close();
        cleanupData();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9091")
    public void testDirectAndNestedAssociationsOrphanedWhileManaged() {
        createData();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        List results = em.createQuery("from Tranchenmodell").getResultList();
        Assert.assertEquals(1, results.size());
        results = em.createQuery("from Preisregelung").getResultList();
        Assert.assertEquals(1, results.size());
        Preisregelung preisregelung = ((Preisregelung) (results.get(0)));
        Tranchenmodell tranchenmodell = preisregelung.getTranchenmodell();
        Assert.assertNotNull(tranchenmodell);
        Assert.assertNotNull(tranchenmodell.getX());
        Assert.assertEquals(2, tranchenmodell.getTranchen().size());
        Assert.assertNotNull(tranchenmodell.getTranchen().get(0).getY());
        preisregelung.setTranchenmodell(null);
        tranchenmodell.setX(null);
        tranchenmodell.getTranchen().get(0).setY(null);
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        preisregelung = ((Preisregelung) (em.find(Preisregelung.class, preisregelung.getId())));
        Assert.assertNull(preisregelung.getTranchenmodell());
        results = em.createQuery("from Tranchenmodell").getResultList();
        Assert.assertEquals(0, results.size());
        results = em.createQuery("from Tranche").getResultList();
        Assert.assertEquals(0, results.size());
        results = em.createQuery("from X").getResultList();
        Assert.assertEquals(0, results.size());
        results = em.createQuery("from Y").getResultList();
        Assert.assertEquals(0, results.size());
        results = em.createQuery("from Preisregelung").getResultList();
        Assert.assertEquals(1, results.size());
        em.getTransaction().commit();
        em.close();
        cleanupData();
    }
}

