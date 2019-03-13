/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.resource.transaction.jta;


import org.hibernate.resource.transaction.backend.jta.internal.JtaPlatformInaccessibleException;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorBuilderImpl;
import org.hibernate.resource.transaction.spi.TransactionCoordinator;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class InaccessibleJtaPlatformTests {
    private final TransactionCoordinatorOwnerTestingImpl owner = new TransactionCoordinatorOwnerTestingImpl();

    private JtaTransactionCoordinatorBuilderImpl transactionCoordinatorBuilder = new JtaTransactionCoordinatorBuilderImpl();

    @Test
    public void testInaccessibleTransactionManagerHandling() {
        // first, have JtaPlatform throw an exception
        try {
            final JtaPlatformInaccessibleImpl jtaPlatform = new JtaPlatformInaccessibleImpl(true);
            final TransactionCoordinator transactionCoordinator = new org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorImpl(transactionCoordinatorBuilder, owner, true, jtaPlatform, false, false);
            transactionCoordinator.getTransactionDriverControl().begin();
            Assert.fail("Expecting JtaPlatformInaccessibleException, but call succeeded");
        } catch (JtaPlatformInaccessibleException expected) {
            // expected condition
        } catch (Exception e) {
            Assert.fail(("Expecting JtaPlatformInaccessibleException, but got " + (e.getClass().getName())));
        }
        // then, have it return null
        try {
            final JtaPlatformInaccessibleImpl jtaPlatform = new JtaPlatformInaccessibleImpl(false);
            final TransactionCoordinator transactionCoordinator = new org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorImpl(transactionCoordinatorBuilder, owner, true, jtaPlatform, false, false);
            transactionCoordinator.getTransactionDriverControl().begin();
            Assert.fail("Expecting JtaPlatformInaccessibleException, but call succeeded");
        } catch (JtaPlatformInaccessibleException expected) {
            // expected condition
        } catch (Exception e) {
            Assert.fail(("Expecting JtaPlatformInaccessibleException, but got " + (e.getClass().getName())));
        }
    }

    @Test
    public void testInaccessibleUserTransactionHandling() {
        // first, have JtaPlatform throw an exception
        try {
            final JtaPlatformInaccessibleImpl jtaPlatform = new JtaPlatformInaccessibleImpl(true);
            final TransactionCoordinator transactionCoordinator = new org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorImpl(transactionCoordinatorBuilder, owner, true, jtaPlatform, false, false);
            transactionCoordinator.getTransactionDriverControl().begin();
            Assert.fail("Expecting JtaPlatformInaccessibleException, but call succeeded");
        } catch (JtaPlatformInaccessibleException expected) {
            // expected condition
        } catch (Exception e) {
            Assert.fail(("Expecting JtaPlatformInaccessibleException, but got " + (e.getClass().getName())));
        }
        // then, have it return null
        try {
            final JtaPlatformInaccessibleImpl jtaPlatform = new JtaPlatformInaccessibleImpl(false);
            final TransactionCoordinator transactionCoordinator = new org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorImpl(transactionCoordinatorBuilder, owner, true, jtaPlatform, false, false);
            transactionCoordinator.getTransactionDriverControl().begin();
            Assert.fail("Expecting JtaPlatformInaccessibleException, but call succeeded");
        } catch (JtaPlatformInaccessibleException expected) {
            // expected condition
        } catch (Exception e) {
            Assert.fail(("Expecting JtaPlatformInaccessibleException, but got " + (e.getClass().getName())));
        }
    }
}

