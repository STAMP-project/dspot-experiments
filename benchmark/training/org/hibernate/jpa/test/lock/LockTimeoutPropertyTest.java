/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.lock;


import AvailableSettings.LOCK_TIMEOUT;
import LockModeType.PESSIMISTIC_READ;
import LockModeType.PESSIMISTIC_WRITE;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.org.hibernate.query.Query;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 * no need to run this on DB matrix
 *
 * @author Strong Liu <stliu@hibernate.org>
 */
@RequiresDialect(H2Dialect.class)
public class LockTimeoutPropertyTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLockTimeoutASNamedQueryHint() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Query query = em.createNamedQuery("getAll");
        query.setLockMode(PESSIMISTIC_READ);
        int timeout = query.unwrap(Query.class).getLockOptions().getTimeOut();
        Assert.assertEquals(3000, timeout);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-6256")
    public void testTimeoutHint() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        boolean b = em.getProperties().containsKey(LOCK_TIMEOUT);
        Assert.assertTrue(b);
        int timeout = Integer.valueOf(em.getProperties().get(LOCK_TIMEOUT).toString());
        Assert.assertEquals(2000, timeout);
        org.hibernate.query.Query q = ((org.hibernate.query.Query) (em.createQuery("select u from UnversionedLock u")));
        timeout = q.getLockOptions().getTimeOut();
        Assert.assertEquals(2000, timeout);
        Query query = em.createQuery("select u from UnversionedLock u");
        query.setLockMode(PESSIMISTIC_WRITE);
        query.setHint(LOCK_TIMEOUT, 3000);
        q = ((org.hibernate.query.Query) (query));
        timeout = q.getLockOptions().getTimeOut();
        Assert.assertEquals(3000, timeout);
        em.getTransaction().rollback();
        em.close();
    }
}

