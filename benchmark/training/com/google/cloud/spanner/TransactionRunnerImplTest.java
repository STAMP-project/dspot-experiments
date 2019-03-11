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
import SpannerImpl.SessionImpl;
import SpannerImpl.TransactionContextImpl;
import SpannerImpl.TransactionRunnerImpl;
import SpannerImpl.TransactionRunnerImpl.Sleeper;
import Status.Code.RESOURCE_EXHAUSTED;
import com.google.api.client.util.BackOff;
import com.google.cloud.spanner.spi.v1.SpannerRpc;
import io.grpc.Context;
import io.grpc.Status;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit test for {@link com.google.cloud.spanner.SpannerImpl.TransactionRunnerImpl}
 */
@RunWith(JUnit4.class)
public class TransactionRunnerImplTest {
    @Mock
    private SpannerRpc rpc;

    @Mock
    private SessionImpl session;

    @Mock
    private Sleeper sleeper;

    @Mock
    private TransactionContextImpl txn;

    private TransactionRunnerImpl transactionRunner;

    private boolean firstRun;

    @Test
    public void commitSucceeds() {
        final AtomicInteger numCalls = new AtomicInteger(0);
        transactionRunner.run(new com.google.cloud.spanner.TransactionRunner.TransactionCallable<Void>() {
            @Override
            public Void run(TransactionContext transaction) throws Exception {
                numCalls.incrementAndGet();
                return null;
            }
        });
        assertThat(numCalls.get()).isEqualTo(1);
        Mockito.verify(txn).ensureTxn();
        Mockito.verify(txn).commit();
    }

    @Test
    public void runAbort() {
        Mockito.when(txn.isAborted()).thenReturn(true);
        long backoffMillis = 100L;
        Mockito.when(txn.getRetryDelayInMillis(ArgumentMatchers.any(BackOff.class))).thenReturn(backoffMillis);
        runTransaction(SpannerExceptionFactory.newSpannerException(ABORTED, ""));
        Mockito.verify(sleeper, Mockito.times(1)).backoffSleep(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq(backoffMillis));
    }

    @Test
    public void commitAbort() {
        final SpannerException error = SpannerExceptionFactory.newSpannerException(SpannerExceptionFactory.newSpannerException(ABORTED, ""));
        Mockito.doThrow(error).doNothing().when(txn).commit();
        long backoffMillis = 100L;
        Mockito.when(txn.getRetryDelayInMillis(ArgumentMatchers.any(BackOff.class))).thenReturn(backoffMillis);
        final AtomicInteger numCalls = new AtomicInteger(0);
        transactionRunner.run(new com.google.cloud.spanner.TransactionRunner.TransactionCallable<Void>() {
            @Override
            public Void run(TransactionContext transaction) throws Exception {
                numCalls.incrementAndGet();
                return null;
            }
        });
        assertThat(numCalls.get()).isEqualTo(2);
        Mockito.verify(sleeper, Mockito.times(1)).backoffSleep(ArgumentMatchers.any(Context.class), ArgumentMatchers.eq(backoffMillis));
        Mockito.verify(txn, Mockito.times(2)).ensureTxn();
    }

    @Test
    public void commitFailsWithNonAbort() {
        final SpannerException error = SpannerExceptionFactory.newSpannerException(SpannerExceptionFactory.newSpannerException(UNKNOWN, ""));
        Mockito.doThrow(error).when(txn).commit();
        final AtomicInteger numCalls = new AtomicInteger(0);
        try {
            transactionRunner.run(new com.google.cloud.spanner.TransactionRunner.TransactionCallable<Void>() {
                @Override
                public Void run(TransactionContext transaction) throws Exception {
                    numCalls.incrementAndGet();
                    return null;
                }
            });
            Assert.fail("Expected exception");
        } catch (SpannerException e) {
            assertThat(e.getErrorCode()).isEqualTo(UNKNOWN);
        }
        assertThat(numCalls.get()).isEqualTo(1);
        Mockito.verify(txn, Mockito.times(1)).ensureTxn();
        Mockito.verify(txn, Mockito.times(1)).commit();
    }

    @Test
    public void runResourceExhaustedNoRetry() throws Exception {
        try {
            runTransaction(new io.grpc.StatusRuntimeException(Status.fromCodeValue(RESOURCE_EXHAUSTED.value())));
            Assert.fail("Expected exception");
        } catch (SpannerException e) {
            // expected.
        }
        Mockito.verify(txn).rollback();
    }
}

