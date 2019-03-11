/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.wildfly.integrationtest;


import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.engine.transaction.spi.TransactionImplementor;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Andrea Boriero
 */
@RunWith(Arquillian.class)
public class TransactionRollbackTest {
    private static final String ORM_VERSION = Session.class.getPackage().getImplementationVersion();

    private static final String ORM_MINOR_VERSION = TransactionRollbackTest.ORM_VERSION.substring(0, TransactionRollbackTest.ORM_VERSION.indexOf(".", ((TransactionRollbackTest.ORM_VERSION.indexOf(".")) + 1)));

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void testMarkRollbackOnlyAnUnactiveTransaction() {
        EntityTransaction transaction = entityManager.getTransaction();
        final TransactionImplementor hibernateTransaction = ((TransactionImplementor) (transaction));
        hibernateTransaction.markRollbackOnly();
        transaction.rollback();
        Assert.assertFalse(transaction.isActive());
    }

    @Test
    public void testMarkRollbackOnlyAnActiveTransaction() {
        EntityTransaction transaction = entityManager.getTransaction();
        final TransactionImplementor hibernateTransaction = ((TransactionImplementor) (transaction));
        transaction.begin();
        hibernateTransaction.markRollbackOnly();
        transaction.rollback();
        Assert.assertFalse(transaction.isActive());
    }
}

