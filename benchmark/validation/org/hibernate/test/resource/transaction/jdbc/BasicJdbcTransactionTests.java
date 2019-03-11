/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.resource.transaction.jdbc;


import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.resource.transaction.spi.TransactionCoordinator;
import org.hibernate.test.resource.common.SynchronizationCollectorImpl;
import org.hibernate.test.resource.common.SynchronizationErrorImpl;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.transaction.TransactionUtil2;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class BasicJdbcTransactionTests extends BaseUnitTestCase {
    @Test
    public void basicUsageTest() {
        try (final SessionFactoryImplementor sf = generateSessionFactory()) {
            TransactionUtil2.inSession(sf, ( session) -> {
                final TransactionCoordinator coordinator = session.getTransactionCoordinator();
                final SynchronizationCollectorImpl sync = new SynchronizationCollectorImpl();
                coordinator.getLocalSynchronizations().registerSynchronization(sync);
                coordinator.getTransactionDriverControl().begin();
                assertEquals(0, sync.getBeforeCompletionCount());
                assertEquals(0, sync.getSuccessfulCompletionCount());
                assertEquals(0, sync.getFailedCompletionCount());
                coordinator.getTransactionDriverControl().commit();
                assertEquals(1, sync.getBeforeCompletionCount());
                assertEquals(1, sync.getSuccessfulCompletionCount());
                assertEquals(0, sync.getFailedCompletionCount());
            });
        }
    }

    @Test
    @SuppressWarnings("EmptyCatchBlock")
    public void testMarkRollbackOnly() {
        try (final SessionFactoryImplementor sf = generateSessionFactory()) {
            TransactionUtil2.inSession(sf, ( session) -> {
                final TransactionCoordinator coordinator = session.getTransactionCoordinator();
                assertEquals(TransactionStatus.NOT_ACTIVE, coordinator.getTransactionDriverControl().getStatus());
                session.getTransaction().begin();
                assertEquals(TransactionStatus.ACTIVE, coordinator.getTransactionDriverControl().getStatus());
                session.getTransaction().markRollbackOnly();
                assertEquals(TransactionStatus.MARKED_ROLLBACK, coordinator.getTransactionDriverControl().getStatus());
                try {
                    session.getTransaction().commit();
                } catch ( expected) {
                } finally {
                    assertThat(coordinator.getTransactionDriverControl().getStatus(), anyOf(is(TransactionStatus.NOT_ACTIVE), is(TransactionStatus.ROLLED_BACK)));
                }
            });
        }
    }

    @Test
    @SuppressWarnings("EmptyCatchBlock")
    public void testSynchronizationFailure() {
        try (final SessionFactoryImplementor sf = generateSessionFactory()) {
            TransactionUtil2.inSession(sf, ( session) -> {
                final TransactionCoordinator coordinator = session.getTransactionCoordinator();
                assertEquals(TransactionStatus.NOT_ACTIVE, coordinator.getTransactionDriverControl().getStatus());
                coordinator.getLocalSynchronizations().registerSynchronization(SynchronizationErrorImpl.forBefore());
                coordinator.getTransactionDriverControl().begin();
                assertEquals(TransactionStatus.ACTIVE, coordinator.getTransactionDriverControl().getStatus());
                try {
                    coordinator.getTransactionDriverControl().commit();
                } catch ( expected) {
                } finally {
                    assertThat(coordinator.getTransactionDriverControl().getStatus(), anyOf(is(TransactionStatus.NOT_ACTIVE), is(TransactionStatus.ROLLED_BACK)));
                }
            });
        }
    }
}

