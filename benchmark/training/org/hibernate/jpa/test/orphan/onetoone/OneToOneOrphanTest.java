/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.orphan.onetoone;


import javax.persistence.EntityManager;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Martin Simka
 * @author Gail Badner
 */
public class OneToOneOrphanTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9568")
    public void testFlushTransientOneToOneNoCascade() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        B b = new B();
        A a = new A();
        a.setB(b);
        try {
            em.persist(a);
            em.flush();
            em.getTransaction().commit();
            Assert.fail("should have raised an IllegalStateException");
        } catch (IllegalStateException ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            // IllegalStateException caught as expected
        } finally {
            em.close();
        }
    }
}

