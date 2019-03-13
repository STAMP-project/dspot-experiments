/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.orphan.one2one.fk.reversed.bidirectional.multilevelcascade;


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
    public void testOrphanedWhileManaged() {
        createData();
        Session session = openSession();
        session.beginTransaction();
        List results = session.createQuery("from Tranchenmodell").list();
        Assert.assertEquals(1, results.size());
        results = session.createQuery("from Preisregelung").list();
        Assert.assertEquals(1, results.size());
        Preisregelung preisregelung = ((Preisregelung) (results.get(0)));
        Assert.assertNotNull(preisregelung.getTranchenmodell());
        preisregelung.setTranchenmodell(null);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        preisregelung = ((Preisregelung) (session.get(Preisregelung.class, preisregelung.getId())));
        Assert.assertNull(preisregelung.getTranchenmodell());
        results = session.createQuery("from Tranchenmodell").list();
        Assert.assertEquals(0, results.size());
        results = session.createQuery("from Preisregelung").list();
        Assert.assertEquals(1, results.size());
        session.getTransaction().commit();
        session.close();
        cleanupData();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9091")
    public void testReplacedWhileManaged() {
        createData();
        Session session = openSession();
        session.beginTransaction();
        List results = session.createQuery("from Tranchenmodell").list();
        Assert.assertEquals(1, results.size());
        results = session.createQuery("from Preisregelung").list();
        Assert.assertEquals(1, results.size());
        Preisregelung preisregelung = ((Preisregelung) (results.get(0)));
        Assert.assertNotNull(preisregelung.getTranchenmodell());
        // Replace with a new Tranchenmodell instance
        Tranchenmodell tranchenmodellNew = new Tranchenmodell();
        tranchenmodellNew.setId(1952L);
        preisregelung.setTranchenmodell(tranchenmodellNew);
        tranchenmodellNew.setPreisregelung(preisregelung);
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        results = session.createQuery("from Tranchenmodell").list();
        Assert.assertEquals(1, results.size());
        Tranchenmodell tranchenmodellQueried = ((Tranchenmodell) (results.get(0)));
        Assert.assertEquals(tranchenmodellNew.getId(), tranchenmodellQueried.getId());
        results = session.createQuery("from Preisregelung").list();
        Assert.assertEquals(1, results.size());
        Preisregelung preisregelung1Queried = ((Preisregelung) (results.get(0)));
        Assert.assertEquals(tranchenmodellQueried, preisregelung1Queried.getTranchenmodell());
        results = session.createQuery("from Tranche").list();
        Assert.assertEquals(0, results.size());
        session.getTransaction().commit();
        session.close();
        cleanupData();
    }
}

