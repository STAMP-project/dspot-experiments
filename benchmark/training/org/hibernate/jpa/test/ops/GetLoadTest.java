/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.ops;


import DialectChecks.SupportsNoColumnInsert;
import javax.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 * @author Hardy Ferentschik
 */
@RequiresDialectFeature(SupportsNoColumnInsert.class)
public class GetLoadTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testGet() {
        clearCounts();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Session s = ((Session) (em.getDelegate()));
        Employer emp = new Employer();
        s.persist(emp);
        Node node = new Node("foo");
        Node parent = new Node("bar");
        parent.addChild(node);
        s.persist(parent);
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        s = ((Session) (em.getDelegate()));
        emp = ((Employer) (s.get(Employer.class, emp.getId())));
        Assert.assertTrue(Hibernate.isInitialized(emp));
        Assert.assertFalse(Hibernate.isInitialized(emp.getEmployees()));
        node = ((Node) (s.get(Node.class, node.getName())));
        Assert.assertTrue(Hibernate.isInitialized(node));
        Assert.assertFalse(Hibernate.isInitialized(node.getChildren()));
        Assert.assertFalse(Hibernate.isInitialized(node.getParent()));
        Assert.assertNull(s.get(Node.class, "xyz"));
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        s = ((Session) (em.getDelegate()));
        emp = ((Employer) (s.get(Employer.class.getName(), emp.getId())));
        Assert.assertTrue(Hibernate.isInitialized(emp));
        node = ((Node) (s.get(Node.class.getName(), node.getName())));
        Assert.assertTrue(Hibernate.isInitialized(node));
        em.getTransaction().commit();
        em.close();
        assertFetchCount(0);
    }

    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testLoad() {
        clearCounts();
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Session s = ((Session) (em.getDelegate()));
        Employer emp = new Employer();
        s.persist(emp);
        Node node = new Node("foo");
        Node parent = new Node("bar");
        parent.addChild(node);
        s.persist(parent);
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        s = ((Session) (em.getDelegate()));
        emp = ((Employer) (s.load(Employer.class, emp.getId())));
        emp.getId();
        Assert.assertFalse(Hibernate.isInitialized(emp));
        node = ((Node) (s.load(Node.class, node.getName())));
        Assert.assertEquals(node.getName(), "foo");
        Assert.assertFalse(Hibernate.isInitialized(node));
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        s = ((Session) (em.getDelegate()));
        emp = ((Employer) (s.load(Employer.class.getName(), emp.getId())));
        emp.getId();
        Assert.assertFalse(Hibernate.isInitialized(emp));
        node = ((Node) (s.load(Node.class.getName(), node.getName())));
        Assert.assertEquals(node.getName(), "foo");
        Assert.assertFalse(Hibernate.isInitialized(node));
        em.getTransaction().commit();
        em.close();
        assertFetchCount(0);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9856")
    public void testNonEntity() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        try {
            em.getReference(String.class, 1);
            Assert.fail("Expecting a failure");
        } catch (IllegalArgumentException ignore) {
            // expected
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11838")
    public void testLoadGetId() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Session s = ((Session) (em.getDelegate()));
        Workload workload = new Workload();
        s.persist(workload);
        em.getTransaction().commit();
        em.close();
        em = getOrCreateEntityManager();
        em.getTransaction().begin();
        s = ((Session) (em.getDelegate()));
        Workload proxy = s.load(Workload.class, workload.id);
        proxy.getId();
        Assert.assertFalse(Hibernate.isInitialized(proxy));
        proxy.getName();
        Assert.assertTrue(Hibernate.isInitialized(proxy));
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12034")
    public void testLoadIdNotFound_FieldBasedAccess() {
        EntityManager em = getOrCreateEntityManager();
        try {
            em.getTransaction().begin();
            Session s = ((Session) (em.getDelegate()));
            Assert.assertNull(s.get(Workload.class, 999));
            Workload proxy = s.load(Workload.class, 999);
            Assert.assertFalse(Hibernate.isInitialized(proxy));
            proxy.getId();
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12034")
    public void testReferenceIdNotFound_FieldBasedAccess() {
        EntityManager em = getOrCreateEntityManager();
        try {
            em.getTransaction().begin();
            Assert.assertNull(em.find(Workload.class, 999));
            Workload proxy = em.getReference(Workload.class, 999);
            Assert.assertFalse(Hibernate.isInitialized(proxy));
            proxy.getId();
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12034")
    public void testLoadIdNotFound_PropertyBasedAccess() {
        EntityManager em = getOrCreateEntityManager();
        try {
            em.getTransaction().begin();
            Session s = ((Session) (em.getDelegate()));
            Assert.assertNull(s.get(Employee.class, 999));
            Employee proxy = s.load(Employee.class, 999);
            Assert.assertFalse(Hibernate.isInitialized(proxy));
            proxy.getId();
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12034")
    public void testReferenceIdNotFound_PropertyBasedAccess() {
        EntityManager em = getOrCreateEntityManager();
        try {
            em.getTransaction().begin();
            Assert.assertNull(em.find(Employee.class, 999));
            Employee proxy = em.getReference(Employee.class, 999);
            Assert.assertFalse(Hibernate.isInitialized(proxy));
            proxy.getId();
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
    }
}

