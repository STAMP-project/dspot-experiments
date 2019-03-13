/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.spanner;


import ErrorCode.ABORTED;
import ErrorCode.UNKNOWN;
import SpannerImpl.TransactionContextImpl;
import TransactionState.COMMITTED;
import TransactionState.STARTED;
import com.google.cloud.Timestamp;
import com.google.cloud.spanner.SpannerImpl.SessionImpl;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class TransactionManagerImplTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private SessionImpl session;

    @Mock
    TransactionContextImpl txn;

    private TransactionManagerImpl manager;

    @Test
    public void beginCalledTwiceFails() {
        Mockito.when(session.newTransaction()).thenReturn(txn);
        assertThat(manager.begin()).isEqualTo(txn);
        assertThat(manager.getState()).isEqualTo(STARTED);
        exception.expect(IllegalStateException.class);
        manager.begin();
    }

    @Test
    public void commitBeforeBeginFails() {
        exception.expect(IllegalStateException.class);
        manager.commit();
    }

    @Test
    public void rollbackBeforeBeginFails() {
        exception.expect(IllegalStateException.class);
        manager.rollback();
    }

    @Test
    public void resetBeforeBeginFails() {
        exception.expect(IllegalStateException.class);
        manager.resetForRetry();
    }

    @Test
    public void transactionRolledBackOnClose() {
        Mockito.when(session.newTransaction()).thenReturn(txn);
        Mockito.when(txn.isAborted()).thenReturn(false);
        manager.begin();
        manager.close();
        Mockito.verify(txn).rollback();
    }

    @Test
    public void commitSucceeds() {
        Mockito.when(session.newTransaction()).thenReturn(txn);
        Timestamp commitTimestamp = Timestamp.ofTimeMicroseconds(1);
        Mockito.when(txn.commitTimestamp()).thenReturn(commitTimestamp);
        manager.begin();
        manager.commit();
        assertThat(manager.getState()).isEqualTo(COMMITTED);
        assertThat(manager.getCommitTimestamp()).isEqualTo(commitTimestamp);
    }

    @Test
    public void resetAfterSuccessfulCommitFails() {
        Mockito.when(session.newTransaction()).thenReturn(txn);
        manager.begin();
        manager.commit();
        exception.expect(IllegalStateException.class);
        manager.resetForRetry();
    }

    @Test
    public void resetAfterAbortSucceeds() {
        Mockito.when(session.newTransaction()).thenReturn(txn);
        manager.begin();
        Mockito.doThrow(SpannerExceptionFactory.newSpannerException(ABORTED, "")).when(txn).commit();
        try {
            manager.commit();
            Assert.fail("Expected AbortedException");
        } catch (AbortedException e) {
            assertThat(manager.getState()).isEqualTo(TransactionState.ABORTED);
        }
        txn = Mockito.mock(TransactionContextImpl.class);
        Mockito.when(session.newTransaction()).thenReturn(txn);
        assertThat(manager.resetForRetry()).isEqualTo(txn);
        assertThat(manager.getState()).isEqualTo(STARTED);
    }

    @Test
    public void resetAfterErrorFails() {
        Mockito.when(session.newTransaction()).thenReturn(txn);
        manager.begin();
        Mockito.doThrow(SpannerExceptionFactory.newSpannerException(UNKNOWN, "")).when(txn).commit();
        try {
            manager.commit();
            Assert.fail("Expected AbortedException");
        } catch (SpannerException e) {
            assertThat(e.getErrorCode()).isEqualTo(UNKNOWN);
        }
        exception.expect(IllegalStateException.class);
        manager.resetForRetry();
    }

    @Test
    public void rollbackAfterCommitFails() {
        Mockito.when(session.newTransaction()).thenReturn(txn);
        manager.begin();
        manager.commit();
        exception.expect(IllegalStateException.class);
        manager.rollback();
    }

    @Test
    public void commitAfterRollbackFails() {
        Mockito.when(session.newTransaction()).thenReturn(txn);
        manager.begin();
        manager.rollback();
        exception.expect(IllegalStateException.class);
        manager.commit();
    }
}

