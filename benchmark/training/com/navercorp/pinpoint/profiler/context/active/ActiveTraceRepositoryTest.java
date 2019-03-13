/**
 * Copyright 2015 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.profiler.context.active;


import com.google.common.util.concurrent.ListenableFuture;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.profiler.context.id.TransactionCounter;
import com.navercorp.pinpoint.profiler.context.module.DefaultApplicationContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class ActiveTraceRepositoryTest {
    private static final int SAMPLING_RATE = 3;

    private TraceContext traceContext;

    private TransactionCounter transactionCounter;

    private ActiveTraceRepository activeTraceRepository;

    private DefaultApplicationContext applicationContext;

    @Test
    public void verifyActiveTraceCollectionAndTransactionCount() throws Exception {
        // Given
        final int newTransactionCount = 50;
        @SuppressWarnings("unused")
        final int expectedSampledNewCount = (newTransactionCount / (ActiveTraceRepositoryTest.SAMPLING_RATE)) + ((newTransactionCount % (ActiveTraceRepositoryTest.SAMPLING_RATE)) > 0 ? 1 : 0);
        final int expectedUnsampledNewCount = newTransactionCount - expectedSampledNewCount;
        final int expectedSampledContinuationCount = 20;
        final int expectedUnsampledContinuationCount = 30;
        final int expectedTotalTransactionCount = ((expectedSampledNewCount + expectedUnsampledNewCount) + expectedSampledContinuationCount) + expectedUnsampledContinuationCount;
        final CountDownLatch awaitLatch = new CountDownLatch(1);
        final CountDownLatch executeLatch = new CountDownLatch(expectedTotalTransactionCount);
        // When
        ExecutorService executorService = Executors.newFixedThreadPool(expectedTotalTransactionCount);
        ListenableFuture<List<ActiveTraceRepositoryTest.TraceThreadTuple>> futures = executeTransactions(executorService, awaitLatch, executeLatch, expectedSampledNewCount, expectedUnsampledNewCount, expectedSampledContinuationCount, expectedUnsampledContinuationCount);
        executeLatch.await(5, TimeUnit.SECONDS);
        List<ActiveTraceSnapshot> activeTraceInfos = this.activeTraceRepository.snapshot();
        awaitLatch.countDown();
        List<ActiveTraceRepositoryTest.TraceThreadTuple> executedTraces = futures.get(5, TimeUnit.SECONDS);
        Map<Long, ActiveTraceRepositoryTest.TraceThreadTuple> executedTraceMap = new HashMap<Long, ActiveTraceRepositoryTest.TraceThreadTuple>(executedTraces.size());
        for (ActiveTraceRepositoryTest.TraceThreadTuple tuple : executedTraces) {
            executedTraceMap.put(tuple.id, tuple);
        }
        executorService.shutdown();
        if (!(executorService.awaitTermination(5, TimeUnit.SECONDS))) {
            executorService.shutdownNow();
        }
        // Then
        Assert.assertEquals(expectedSampledNewCount, transactionCounter.getSampledNewCount());
        Assert.assertEquals(expectedUnsampledNewCount, transactionCounter.getUnSampledNewCount());
        Assert.assertEquals(expectedSampledContinuationCount, transactionCounter.getSampledContinuationCount());
        Assert.assertEquals(expectedUnsampledContinuationCount, transactionCounter.getUnSampledContinuationCount());
        Assert.assertEquals(expectedTotalTransactionCount, transactionCounter.getTotalTransactionCount());
        for (ActiveTraceSnapshot activeTraceInfo : activeTraceInfos) {
            ActiveTraceRepositoryTest.TraceThreadTuple executedTrace = executedTraceMap.get(activeTraceInfo.getLocalTransactionId());
            Assert.assertEquals(executedTrace.getId(), activeTraceInfo.getLocalTransactionId());
            Assert.assertEquals(executedTrace.getStartTime(), activeTraceInfo.getStartTime());
            Assert.assertEquals(executedTrace.getThreadId(), activeTraceInfo.getThreadId());
        }
    }

    private static class TraceThreadTuple {
        private final long id;

        private final long startTime;

        private final long threadId;

        private TraceThreadTuple(Trace trace, long threadId) {
            if (trace == null) {
                throw new NullPointerException("trace must not be null");
            }
            this.id = trace.getId();
            this.startTime = trace.getStartTime();
            this.threadId = threadId;
        }

        public long getId() {
            return id;
        }

        public long getStartTime() {
            return startTime;
        }

        public long getThreadId() {
            return threadId;
        }
    }
}

