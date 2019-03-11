/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.clustering.ee.infinispan;


import Batch.State.ACTIVE;
import Batch.State.CLOSED;
import Status.STATUS_ACTIVE;
import Status.STATUS_COMMITTED;
import Status.STATUS_ROLLEDBACK;
import javax.transaction.Synchronization;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.wildfly.clustering.ee.BatchContext;
import org.wildfly.clustering.ee.Batcher;


/**
 * Unit test for {@link InfinispanBatcher}.
 *
 * @author Paul Ferraro
 */
public class InfinispanBatcherTestCase {
    private final TransactionManager tm = Mockito.mock(TransactionManager.class);

    private final Batcher<TransactionBatch> batcher = new InfinispanBatcher(this.tm);

    @Test
    public void createExistingActiveBatch() throws Exception {
        TransactionBatch existingBatch = Mockito.mock(TransactionBatch.class);
        InfinispanBatcher.setCurrentBatch(existingBatch);
        Mockito.when(existingBatch.getState()).thenReturn(ACTIVE);
        Mockito.when(existingBatch.interpose()).thenReturn(existingBatch);
        TransactionBatch result = this.batcher.createBatch();
        Mockito.verify(existingBatch).interpose();
        Mockito.verifyZeroInteractions(this.tm);
        Assert.assertSame(existingBatch, result);
    }

    @Test
    public void createExistingClosedBatch() throws Exception {
        TransactionBatch existingBatch = Mockito.mock(TransactionBatch.class);
        Transaction tx = Mockito.mock(Transaction.class);
        ArgumentCaptor<Synchronization> capturedSync = ArgumentCaptor.forClass(Synchronization.class);
        InfinispanBatcher.setCurrentBatch(existingBatch);
        Mockito.when(existingBatch.getState()).thenReturn(CLOSED);
        Mockito.when(this.tm.getTransaction()).thenReturn(tx);
        try (TransactionBatch batch = this.batcher.createBatch()) {
            Mockito.verify(this.tm).begin();
            Mockito.verify(tx).registerSynchronization(capturedSync.capture());
            Assert.assertSame(tx, batch.getTransaction());
            Assert.assertSame(batch, InfinispanBatcher.getCurrentBatch());
        } finally {
            capturedSync.getValue().afterCompletion(STATUS_COMMITTED);
        }
        Mockito.verify(tx).commit();
        Assert.assertNull(InfinispanBatcher.getCurrentBatch());
    }

    @Test
    public void createBatchClose() throws Exception {
        Transaction tx = Mockito.mock(Transaction.class);
        ArgumentCaptor<Synchronization> capturedSync = ArgumentCaptor.forClass(Synchronization.class);
        Mockito.when(this.tm.getTransaction()).thenReturn(tx);
        try (TransactionBatch batch = this.batcher.createBatch()) {
            Mockito.verify(this.tm).begin();
            Mockito.verify(tx).registerSynchronization(capturedSync.capture());
            Assert.assertSame(tx, batch.getTransaction());
        } finally {
            capturedSync.getValue().afterCompletion(STATUS_COMMITTED);
        }
        Mockito.verify(tx).commit();
        Assert.assertNull(InfinispanBatcher.getCurrentBatch());
    }

    @Test
    public void createBatchDiscard() throws Exception {
        Transaction tx = Mockito.mock(Transaction.class);
        ArgumentCaptor<Synchronization> capturedSync = ArgumentCaptor.forClass(Synchronization.class);
        Mockito.when(this.tm.getTransaction()).thenReturn(tx);
        try (TransactionBatch batch = this.batcher.createBatch()) {
            Mockito.verify(this.tm).begin();
            Mockito.verify(tx).registerSynchronization(capturedSync.capture());
            Assert.assertSame(tx, batch.getTransaction());
            batch.discard();
        } finally {
            capturedSync.getValue().afterCompletion(STATUS_ROLLEDBACK);
        }
        Mockito.verify(tx, Mockito.never()).commit();
        Mockito.verify(tx).rollback();
        Assert.assertNull(InfinispanBatcher.getCurrentBatch());
    }

    @Test
    public void createNestedBatchClose() throws Exception {
        Transaction tx = Mockito.mock(Transaction.class);
        ArgumentCaptor<Synchronization> capturedSync = ArgumentCaptor.forClass(Synchronization.class);
        Mockito.when(this.tm.getTransaction()).thenReturn(tx);
        try (TransactionBatch outerBatch = this.batcher.createBatch()) {
            Mockito.verify(this.tm).begin();
            Mockito.verify(tx).registerSynchronization(capturedSync.capture());
            Mockito.reset(this.tm);
            Assert.assertSame(tx, outerBatch.getTransaction());
            Mockito.when(this.tm.getTransaction()).thenReturn(tx);
            try (TransactionBatch innerBatch = this.batcher.createBatch()) {
                Mockito.verify(this.tm, Mockito.never()).begin();
                Mockito.verify(this.tm, Mockito.never()).suspend();
            }
            Mockito.verify(tx, Mockito.never()).rollback();
            Mockito.verify(tx, Mockito.never()).commit();
        } finally {
            capturedSync.getValue().afterCompletion(STATUS_COMMITTED);
        }
        Mockito.verify(tx, Mockito.never()).rollback();
        Mockito.verify(tx).commit();
        Assert.assertNull(InfinispanBatcher.getCurrentBatch());
    }

    @Test
    public void createNestedBatchDiscard() throws Exception {
        Transaction tx = Mockito.mock(Transaction.class);
        ArgumentCaptor<Synchronization> capturedSync = ArgumentCaptor.forClass(Synchronization.class);
        Mockito.when(this.tm.getTransaction()).thenReturn(tx);
        try (TransactionBatch outerBatch = this.batcher.createBatch()) {
            Mockito.verify(this.tm).begin();
            Mockito.verify(tx).registerSynchronization(capturedSync.capture());
            Mockito.reset(this.tm);
            Assert.assertSame(tx, outerBatch.getTransaction());
            Mockito.when(tx.getStatus()).thenReturn(STATUS_ACTIVE);
            Mockito.when(this.tm.getTransaction()).thenReturn(tx);
            try (TransactionBatch innerBatch = this.batcher.createBatch()) {
                Mockito.verify(this.tm, Mockito.never()).begin();
                innerBatch.discard();
            }
            Mockito.verify(tx, Mockito.never()).commit();
            Mockito.verify(tx, Mockito.never()).rollback();
        } finally {
            capturedSync.getValue().afterCompletion(STATUS_ROLLEDBACK);
        }
        Mockito.verify(tx).rollback();
        Mockito.verify(tx, Mockito.never()).commit();
        Assert.assertNull(InfinispanBatcher.getCurrentBatch());
    }

    @SuppressWarnings("resource")
    @Test
    public void createOverlappingBatchClose() throws Exception {
        Transaction tx = Mockito.mock(Transaction.class);
        ArgumentCaptor<Synchronization> capturedSync = ArgumentCaptor.forClass(Synchronization.class);
        Mockito.when(this.tm.getTransaction()).thenReturn(tx);
        TransactionBatch batch = this.batcher.createBatch();
        Mockito.verify(this.tm).begin();
        Mockito.verify(tx).registerSynchronization(capturedSync.capture());
        Mockito.reset(this.tm);
        try {
            Assert.assertSame(tx, batch.getTransaction());
            Mockito.when(this.tm.getTransaction()).thenReturn(tx);
            Mockito.when(tx.getStatus()).thenReturn(STATUS_ACTIVE);
            try (TransactionBatch innerBatch = this.batcher.createBatch()) {
                Mockito.verify(this.tm, Mockito.never()).begin();
                batch.close();
                Mockito.verify(tx, Mockito.never()).rollback();
                Mockito.verify(tx, Mockito.never()).commit();
            }
        } finally {
            capturedSync.getValue().afterCompletion(STATUS_COMMITTED);
        }
        Mockito.verify(tx, Mockito.never()).rollback();
        Mockito.verify(tx).commit();
        Assert.assertNull(InfinispanBatcher.getCurrentBatch());
    }

    @SuppressWarnings("resource")
    @Test
    public void createOverlappingBatchDiscard() throws Exception {
        Transaction tx = Mockito.mock(Transaction.class);
        ArgumentCaptor<Synchronization> capturedSync = ArgumentCaptor.forClass(Synchronization.class);
        Mockito.when(this.tm.getTransaction()).thenReturn(tx);
        TransactionBatch batch = this.batcher.createBatch();
        Mockito.verify(this.tm).begin();
        Mockito.verify(tx).registerSynchronization(capturedSync.capture());
        Mockito.reset(this.tm);
        try {
            Assert.assertSame(tx, batch.getTransaction());
            Mockito.when(this.tm.getTransaction()).thenReturn(tx);
            Mockito.when(tx.getStatus()).thenReturn(STATUS_ACTIVE);
            try (TransactionBatch innerBatch = this.batcher.createBatch()) {
                Mockito.verify(this.tm, Mockito.never()).begin();
                innerBatch.discard();
                batch.close();
                Mockito.verify(tx, Mockito.never()).commit();
                Mockito.verify(tx, Mockito.never()).rollback();
            }
        } finally {
            capturedSync.getValue().afterCompletion(STATUS_ROLLEDBACK);
        }
        Mockito.verify(tx).rollback();
        Mockito.verify(tx, Mockito.never()).commit();
        Assert.assertNull(InfinispanBatcher.getCurrentBatch());
    }

    @Test
    public void resumeNullBatch() throws Exception {
        TransactionBatch batch = Mockito.mock(TransactionBatch.class);
        InfinispanBatcher.setCurrentBatch(batch);
        try (BatchContext context = this.batcher.resumeBatch(null)) {
            Mockito.verifyZeroInteractions(this.tm);
            Assert.assertNull(InfinispanBatcher.getCurrentBatch());
        }
        Mockito.verifyZeroInteractions(this.tm);
        Assert.assertSame(batch, InfinispanBatcher.getCurrentBatch());
    }

    @Test
    public void resumeNonTxBatch() throws Exception {
        TransactionBatch existingBatch = Mockito.mock(TransactionBatch.class);
        InfinispanBatcher.setCurrentBatch(existingBatch);
        TransactionBatch batch = Mockito.mock(TransactionBatch.class);
        try (BatchContext context = this.batcher.resumeBatch(batch)) {
            Mockito.verifyZeroInteractions(this.tm);
            Assert.assertSame(batch, InfinispanBatcher.getCurrentBatch());
        }
        Mockito.verifyZeroInteractions(this.tm);
        Assert.assertSame(existingBatch, InfinispanBatcher.getCurrentBatch());
    }

    @Test
    public void resumeBatch() throws Exception {
        TransactionBatch batch = Mockito.mock(TransactionBatch.class);
        Transaction tx = Mockito.mock(Transaction.class);
        Mockito.when(batch.getTransaction()).thenReturn(tx);
        try (BatchContext context = this.batcher.resumeBatch(batch)) {
            Mockito.verify(this.tm, Mockito.never()).suspend();
            Mockito.verify(this.tm).resume(tx);
            Mockito.reset(this.tm);
            Assert.assertSame(batch, InfinispanBatcher.getCurrentBatch());
        }
        Mockito.verify(this.tm).suspend();
        Mockito.verify(this.tm, Mockito.never()).resume(ArgumentMatchers.any());
        Assert.assertNull(InfinispanBatcher.getCurrentBatch());
    }

    @Test
    public void resumeBatchExisting() throws Exception {
        TransactionBatch existingBatch = Mockito.mock(TransactionBatch.class);
        Transaction existingTx = Mockito.mock(Transaction.class);
        InfinispanBatcher.setCurrentBatch(existingBatch);
        TransactionBatch batch = Mockito.mock(TransactionBatch.class);
        Transaction tx = Mockito.mock(Transaction.class);
        Mockito.when(existingBatch.getTransaction()).thenReturn(existingTx);
        Mockito.when(batch.getTransaction()).thenReturn(tx);
        Mockito.when(this.tm.suspend()).thenReturn(existingTx);
        try (BatchContext context = this.batcher.resumeBatch(batch)) {
            Mockito.verify(this.tm).resume(tx);
            Mockito.reset(this.tm);
            Assert.assertSame(batch, InfinispanBatcher.getCurrentBatch());
            Mockito.when(this.tm.suspend()).thenReturn(tx);
        }
        Mockito.verify(this.tm).resume(existingTx);
        Assert.assertSame(existingBatch, InfinispanBatcher.getCurrentBatch());
    }

    @Test
    public void suspendBatch() throws Exception {
        TransactionBatch batch = Mockito.mock(TransactionBatch.class);
        InfinispanBatcher.setCurrentBatch(batch);
        TransactionBatch result = this.batcher.suspendBatch();
        Mockito.verify(this.tm).suspend();
        Assert.assertSame(batch, result);
        Assert.assertNull(InfinispanBatcher.getCurrentBatch());
    }

    @Test
    public void suspendNoBatch() throws Exception {
        TransactionBatch result = this.batcher.suspendBatch();
        Mockito.verify(this.tm, Mockito.never()).suspend();
        Assert.assertNull(result);
    }
}

