/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id$
 */
package org.hibernate.jpa.test;


import TestingJtaPlatformImpl.INSTANCE;
import javax.persistence.EntityManagerFactory;
import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
import org.junit.Assert;
import org.junit.Test;


/**
 * EntityManagerFactoryClosedTest
 *
 * @author Scott Marlow
 */
public class EntityManagerFactoryClosedTest extends BaseEntityManagerFunctionalTestCase {
    /**
     * Test that using a closed EntityManagerFactory throws an IllegalStateException
     * Also ensure that HHH-8586 doesn't regress.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWithTransactionalEnvironment() throws Exception {
        Assert.assertFalse(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        INSTANCE.getTransactionManager().begin();
        Assert.assertTrue(JtaStatusHelper.isActive(INSTANCE.getTransactionManager()));
        EntityManagerFactory entityManagerFactory = entityManagerFactory();
        entityManagerFactory.close();// close the underlying entity manager factory

        try {
            entityManagerFactory.createEntityManager();
            Assert.fail("expected IllegalStateException when calling emf.createEntityManager with closed emf");
        } catch (IllegalStateException expected) {
            // success
        }
        try {
            entityManagerFactory.getCriteriaBuilder();
            Assert.fail("expected IllegalStateException when calling emf.getCriteriaBuilder with closed emf");
        } catch (IllegalStateException expected) {
            // success
        }
        try {
            entityManagerFactory.getCache();
            Assert.fail("expected IllegalStateException when calling emf.getCache with closed emf");
        } catch (IllegalStateException expected) {
            // success
        }
        Assert.assertFalse(entityManagerFactory.isOpen());
        INSTANCE.getTransactionManager().commit();
    }
}

