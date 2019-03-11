/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.transaction;


import javax.persistence.EntityManager;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class JtaGetTransactionThrowsExceptionTest extends BaseEntityManagerFunctionalTestCase {
    @Test(expected = IllegalStateException.class)
    @TestForIssue(jiraKey = "HHH-12487")
    public void onCloseEntityManagerTest() {
        EntityManager em = createEntityManager();
        em.close();
        em.getTransaction();
        Assert.fail("Calling getTransaction on a JTA entity manager should throw an IllegalStateException");
    }

    @Test(expected = IllegalStateException.class)
    @TestForIssue(jiraKey = "HHH-12487")
    public void onOpenEntityManagerTest() {
        EntityManager em = createEntityManager();
        try {
            em.getTransaction();
            Assert.fail("Calling getTransaction on a JTA entity manager should throw an IllegalStateException");
        } finally {
            em.close();
        }
    }
}

