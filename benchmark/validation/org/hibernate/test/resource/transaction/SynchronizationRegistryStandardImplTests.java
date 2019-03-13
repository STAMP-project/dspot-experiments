/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.resource.transaction;


import Status.STATUS_COMMITTED;
import Status.STATUS_ROLLEDBACK;
import javax.transaction.Synchronization;
import org.hibernate.resource.transaction.LocalSynchronizationException;
import org.hibernate.resource.transaction.NullSynchronizationException;
import org.hibernate.resource.transaction.internal.SynchronizationRegistryStandardImpl;
import org.hibernate.test.resource.common.SynchronizationCollectorImpl;
import org.hibernate.test.resource.common.SynchronizationErrorImpl;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for SynchronizationRegistryStandardImpl.
 *
 * @author Steve Ebersole
 */
public class SynchronizationRegistryStandardImplTests {
    @Test
    public void basicUsageTests() {
        final SynchronizationRegistryStandardImpl registry = new SynchronizationRegistryStandardImpl();
        try {
            registry.registerSynchronization(null);
            Assert.fail("Was expecting NullSynchronizationException, but call succeeded");
        } catch (NullSynchronizationException expected) {
            // expected behavior
        } catch (Exception e) {
            Assert.fail(("Was expecting NullSynchronizationException, but got " + (e.getClass().getName())));
        }
        final SynchronizationCollectorImpl synchronization = new SynchronizationCollectorImpl();
        Assert.assertEquals(0, registry.getNumberOfRegisteredSynchronizations());
        registry.registerSynchronization(synchronization);
        Assert.assertEquals(1, registry.getNumberOfRegisteredSynchronizations());
        registry.registerSynchronization(synchronization);
        Assert.assertEquals(1, registry.getNumberOfRegisteredSynchronizations());
        Assert.assertEquals(0, synchronization.getBeforeCompletionCount());
        Assert.assertEquals(0, synchronization.getSuccessfulCompletionCount());
        Assert.assertEquals(0, synchronization.getFailedCompletionCount());
        {
            registry.notifySynchronizationsBeforeTransactionCompletion();
            Assert.assertEquals(1, synchronization.getBeforeCompletionCount());
            Assert.assertEquals(0, synchronization.getSuccessfulCompletionCount());
            Assert.assertEquals(0, synchronization.getFailedCompletionCount());
            registry.notifySynchronizationsAfterTransactionCompletion(STATUS_COMMITTED);
            Assert.assertEquals(1, synchronization.getBeforeCompletionCount());
            Assert.assertEquals(1, synchronization.getSuccessfulCompletionCount());
            Assert.assertEquals(0, synchronization.getFailedCompletionCount());
        }
        // after completion should clear registered synchronizations
        Assert.assertEquals(0, registry.getNumberOfRegisteredSynchronizations());
        // reset the sync
        synchronization.reset();
        Assert.assertEquals(0, synchronization.getBeforeCompletionCount());
        Assert.assertEquals(0, synchronization.getSuccessfulCompletionCount());
        Assert.assertEquals(0, synchronization.getFailedCompletionCount());
        // re-register it
        registry.registerSynchronization(synchronization);
        Assert.assertEquals(1, registry.getNumberOfRegisteredSynchronizations());
        {
            registry.notifySynchronizationsAfterTransactionCompletion(STATUS_ROLLEDBACK);
            Assert.assertEquals(0, synchronization.getBeforeCompletionCount());
            Assert.assertEquals(0, synchronization.getSuccessfulCompletionCount());
            Assert.assertEquals(1, synchronization.getFailedCompletionCount());
            // after completion should clear registered synchronizations
            Assert.assertEquals(0, registry.getNumberOfRegisteredSynchronizations());
        }
    }

    @Test
    public void testUserSynchronizationExceptions() {
        // exception in beforeCompletion
        SynchronizationRegistryStandardImpl registry = new SynchronizationRegistryStandardImpl();
        Synchronization synchronization = SynchronizationErrorImpl.forBefore();
        registry.registerSynchronization(synchronization);
        try {
            registry.notifySynchronizationsBeforeTransactionCompletion();
            Assert.fail("Expecting LocalSynchronizationException, but call succeeded");
        } catch (LocalSynchronizationException expected) {
            // expected
        } catch (Exception e) {
            Assert.fail(("Was expecting LocalSynchronizationException, but got " + (e.getClass().getName())));
        }
        // exception in beforeCompletion
        registry.clearSynchronizations();
        registry = new SynchronizationRegistryStandardImpl();
        synchronization = SynchronizationErrorImpl.forAfter();
        registry.registerSynchronization(synchronization);
        try {
            registry.notifySynchronizationsAfterTransactionCompletion(STATUS_COMMITTED);
            Assert.fail("Expecting LocalSynchronizationException, but call succeeded");
        } catch (LocalSynchronizationException expected) {
            // expected
        } catch (Exception e) {
            Assert.fail(("Was expecting LocalSynchronizationException, but got " + (e.getClass().getName())));
        }
    }
}

