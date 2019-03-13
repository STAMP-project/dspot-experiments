/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.orphan.one2one.fk.bidirectional.multilevelcascade;


import java.util.List;
import org.hibernate.Session;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 * @author Gail Badner
 */
public class DeleteMultiLevelOrphansTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9091")
    public void testDirectAssociationOrphanedWhileManaged() {
        createData();
        Session session = openSession();
        session.beginTransaction();
        List results = session.createQuery("from Tranchenmodell").list();
        Assert.assertEquals(1, results.size());
        results = session.createQuery("from Preisregelung").list();
        Assert.assertEquals(1, results.size());
        Preisregelung preisregelung = ((Preisregelung) (results.get(0)));
        Tranchenmodell tranchenmodell = preisregelung.getTranchenmodell();
        Assert.assertNotNull(tranchenmodell);
        Assert.assertNotNull(tranchenmodell.getX());
        Assert.assertEquals(2, tranchenmodell.getTranchen().size());
        Assert.assertNotNull(tranchenmodell.getTranchen().get(0).getY());
        preisregelung.setTranchenmodell(null);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        preisregelung = ((Preisregelung) (session.get(Preisregelung.class, preisregelung.getId())));
        Assert.assertNull(preisregelung.getTranchenmodell());
        results = session.createQuery("from Tranchenmodell").list();
        Assert.assertEquals(0, results.size());
        results = session.createQuery("from Tranche").list();
        Assert.assertEquals(0, results.size());
        results = session.createQuery("from X").list();
        Assert.assertEquals(0, results.size());
        results = session.createQuery("from Y").list();
        Assert.assertEquals(0, results.size());
        results = session.createQuery("from Preisregelung").list();
        Assert.assertEquals(1, results.size());
        session.getTransaction().commit();
        session.close();
        cleanupData();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9091")
    public void testReplacedDirectAssociationWhileManaged() {
        createData();
        Session session = openSession();
        session.beginTransaction();
        List results = session.createQuery("from Tranchenmodell").list();
        Assert.assertEquals(1, results.size());
        results = session.createQuery("from Preisregelung").list();
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
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.getTransaction().begin();
        results = session.createQuery("from Tranche").list();
        Assert.assertEquals(1, results.size());
        results = session.createQuery("from Tranchenmodell").list();
        Assert.assertEquals(1, results.size());
        results = session.createQuery("from X").list();
        Assert.assertEquals(1, results.size());
        results = session.createQuery("from Y").list();
        Assert.assertEquals(1, results.size());
        results = session.createQuery("from Preisregelung").list();
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
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        results = session.createQuery("from Tranchenmodell").list();
        Assert.assertEquals(1, results.size());
        tranchenmodell = ((Tranchenmodell) (results.get(0)));
        Assert.assertEquals(tranchenmodellNew.getId(), tranchenmodell.getId());
        results = session.createQuery("from Preisregelung").list();
        Assert.assertEquals(1, results.size());
        preisregelung = ((Preisregelung) (results.get(0)));
        Assert.assertEquals(tranchenmodell, preisregelung.getTranchenmodell());
        results = session.createQuery("from Tranche").list();
        Assert.assertEquals(0, results.size());
        results = session.createQuery("from X").list();
        Assert.assertEquals(0, results.size());
        results = session.createQuery("from Y").list();
        Assert.assertEquals(0, results.size());
        session.getTransaction().commit();
        session.close();
        cleanupData();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9091")
    public void testDirectAndNestedAssociationsOrphanedWhileManaged() {
        createData();
        Session session = openSession();
        session.beginTransaction();
        List results = session.createQuery("from Tranchenmodell").list();
        Assert.assertEquals(1, results.size());
        results = session.createQuery("from Preisregelung").list();
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
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        preisregelung = ((Preisregelung) (session.get(Preisregelung.class, preisregelung.getId())));
        Assert.assertNull(preisregelung.getTranchenmodell());
        results = session.createQuery("from Tranchenmodell").list();
        Assert.assertEquals(0, results.size());
        results = session.createQuery("from Tranche").list();
        Assert.assertEquals(0, results.size());
        results = session.createQuery("from X").list();
        Assert.assertEquals(0, results.size());
        results = session.createQuery("from Y").list();
        Assert.assertEquals(0, results.size());
        results = session.createQuery("from Preisregelung").list();
        Assert.assertEquals(1, results.size());
        session.getTransaction().commit();
        session.close();
        cleanupData();
    }
}

