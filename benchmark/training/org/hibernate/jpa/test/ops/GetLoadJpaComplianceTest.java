/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.ops;


import DialectChecks.SupportsNoColumnInsert;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialectFeature;
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
public class GetLoadJpaComplianceTest extends BaseEntityManagerFunctionalTestCase {
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
            Assert.fail("Should have failed because there is no Employee Entity with id == 999");
        } catch (EntityNotFoundException ex) {
            // expected
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
            Assert.fail("Should have failed because there is no Workload Entity with id == 999");
        } catch (EntityNotFoundException ex) {
            // expected
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
            Assert.fail("Should have failed because there is no Employee Entity with id == 999");
        } catch (EntityNotFoundException ex) {
            // expected
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
            Assert.fail("Should have failed because there is no Employee Entity with id == 999");
        } catch (EntityNotFoundException ex) {
            // expected
        } finally {
            em.getTransaction().rollback();
            em.close();
        }
    }
}

