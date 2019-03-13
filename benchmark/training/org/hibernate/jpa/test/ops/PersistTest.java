/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id$
 */
package org.hibernate.jpa.test.ops;


import DialectChecks.SupportsNoColumnInsert;
import java.util.ArrayList;
import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 * @author Hardy Ferentschik
 */
@RequiresDialectFeature(SupportsNoColumnInsert.class)
public class PersistTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testCreateTree() {
        clearCounts();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Node root = new Node("root");
        Node child = new Node("child");
        root.addChild(child);
        em.persist(root);
        em.getTransaction().commit();
        em.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        root = em.find(Node.class, "root");
        Node child2 = new Node("child2");
        root.addChild(child2);
        em.getTransaction().commit();
        em.close();
        assertInsertCount(3);
        assertUpdateCount(0);
    }

    @Test
    public void testCreateTreeWithGeneratedId() {
        clearCounts();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        NumberedNode root = new NumberedNode("root");
        NumberedNode child = new NumberedNode("child");
        root.addChild(child);
        em.persist(root);
        em.getTransaction().commit();
        em.close();
        assertInsertCount(2);
        assertUpdateCount(0);
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        root = em.find(NumberedNode.class, root.getId());
        NumberedNode child2 = new NumberedNode("child2");
        root.addChild(child2);
        em.getTransaction().commit();
        em.close();
        assertInsertCount(3);
        assertUpdateCount(0);
    }

    @Test
    public void testCreateException() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Node dupe = new Node("dupe");
        em.persist(dupe);
        em.persist(dupe);
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(dupe);
        try {
            em.getTransaction().commit();
            Assert.fail("Cannot persist() twice the same entity");
        } catch (Exception cve) {
            // verify that an exception is thrown!
        }
        em.close();
        Node nondupe = new Node("nondupe");
        nondupe.addChild(dupe);
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(nondupe);
        try {
            em.getTransaction().commit();
            Assert.assertFalse(true);
        } catch (RollbackException e) {
            // verify that an exception is thrown!
        }
        em.close();
    }

    @Test
    public void testCreateExceptionWithGeneratedId() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        NumberedNode dupe = new NumberedNode("dupe");
        em.persist(dupe);
        em.persist(dupe);
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(dupe);
            Assert.fail();
        } catch (PersistenceException poe) {
            // verify that an exception is thrown!
        }
        em.getTransaction().rollback();
        em.close();
        NumberedNode nondupe = new NumberedNode("nondupe");
        nondupe.addChild(dupe);
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(nondupe);
            Assert.fail();
        } catch (PersistenceException poe) {
            // verify that an exception is thrown!
        }
        em.getTransaction().rollback();
        em.close();
    }

    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testBasic() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Employer er = new Employer();
        Employee ee = new Employee();
        em.persist(ee);
        Collection<Employee> erColl = new ArrayList<Employee>();
        Collection<Employer> eeColl = new ArrayList<Employer>();
        erColl.add(ee);
        eeColl.add(er);
        er.setEmployees(erColl);
        ee.setEmployers(eeColl);
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        er = em.find(Employer.class, er.getId());
        Assert.assertNotNull(er);
        Assert.assertNotNull(er.getEmployees());
        Assert.assertEquals(1, er.getEmployees().size());
        Employee eeFromDb = ((Employee) (er.getEmployees().iterator().next()));
        Assert.assertEquals(ee.getId(), eeFromDb.getId());
        em.getTransaction().commit();
        em.close();
    }
}

