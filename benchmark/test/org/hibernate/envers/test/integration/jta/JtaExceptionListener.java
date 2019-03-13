/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.jta;


import TestingJtaPlatformImpl.INSTANCE;
import javax.persistence.EntityManager;
import javax.transaction.RollbackException;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.StrTestEntity;
import org.hibernate.testing.jta.TestingJtaPlatformImpl;
import org.junit.Assert;
import org.junit.Test;


/**
 * Same as {@link org.hibernate.envers.test.integration.reventity.ExceptionListener}, but in a JTA environment.
 *
 * @author Adam Warski (adam at warski dot org)
 */
public class JtaExceptionListener extends BaseEnversJPAFunctionalTestCase {
    // must run before testDataNotPersisted()
    @Test(expected = RollbackException.class)
    @Priority(5)
    public void testTransactionRollback() throws Exception {
        INSTANCE.getTransactionManager().begin();
        try {
            EntityManager em = getEntityManager();
            // Trying to persist an entity - however the listener should throw an exception, so the entity
            // shouldn't be persisted
            StrTestEntity te = new StrTestEntity("x");
            em.persist(te);
        } finally {
            TestingJtaPlatformImpl.tryCommit();
        }
    }

    @Test
    public void testDataNotPersisted() throws Exception {
        INSTANCE.getTransactionManager().begin();
        try {
            // Checking if the entity became persisted
            EntityManager em = getEntityManager();
            long count = em.createQuery("from StrTestEntity s where s.str = 'x'").getResultList().size();
            Assert.assertEquals(0, count);
        } finally {
            TestingJtaPlatformImpl.tryCommit();
        }
    }
}

