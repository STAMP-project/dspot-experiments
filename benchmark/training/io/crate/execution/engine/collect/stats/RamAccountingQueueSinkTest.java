/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.execution.engine.collect.stats;


import Plan.StatementType;
import StatementClassifier.Classification;
import io.crate.auth.user.User;
import io.crate.breaker.SizeEstimator;
import io.crate.common.collections.BlockingEvictingQueue;
import io.crate.expression.reference.sys.job.ContextLog;
import io.crate.expression.reference.sys.job.JobContextLog;
import io.crate.planner.operators.StatementClassifier;
import io.crate.test.integration.CrateUnitTest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import javax.annotation.Nullable;
import org.elasticsearch.common.unit.TimeValue;
import org.hamcrest.Matchers;
import org.junit.Test;


public class RamAccountingQueueSinkTest extends CrateUnitTest {
    private static final RamAccountingQueueSinkTest.NoopLogEstimator NOOP_ESTIMATOR = new RamAccountingQueueSinkTest.NoopLogEstimator();

    private ScheduledExecutorService scheduler;

    private QueueSink logSink;

    private static class NoopLog implements ContextLog {
        NoopLog() {
        }

        @Override
        public long ended() {
            return 0;
        }
    }

    private static class NoopLogEstimator extends SizeEstimator<RamAccountingQueueSinkTest.NoopLog> {
        @Override
        public long estimateSize(@Nullable
        RamAccountingQueueSinkTest.NoopLog value) {
            return 0L;
        }
    }

    @Test
    public void testFixedSizeRamAccountingQueueSink() throws Exception {
        BlockingEvictingQueue<RamAccountingQueueSinkTest.NoopLog> q = new BlockingEvictingQueue(15000);
        RamAccountingQueue<RamAccountingQueueSinkTest.NoopLog> ramAccountingQueue = new RamAccountingQueue(q, RamAccountingQueueSinkTest.breaker(), RamAccountingQueueSinkTest.NOOP_ESTIMATOR);
        logSink = new QueueSink(ramAccountingQueue, ramAccountingQueue::close);
        int THREADS = 50;
        final CountDownLatch latch = new CountDownLatch(THREADS);
        List<Thread> threads = new ArrayList<>(20);
        for (int i = 0; i < THREADS; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    logSink.add(new RamAccountingQueueSinkTest.NoopLog());
                }
                latch.countDown();
            });
            t.start();
            threads.add(t);
        }
        latch.await();
        assertThat(ramAccountingQueue.size(), Matchers.is(15000));
        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Test
    public void testRemoveExpiredLogs() {
        StatementClassifier.Classification classification = new StatementClassifier.Classification(StatementType.SELECT, Collections.singleton("Collect"));
        ConcurrentLinkedQueue<JobContextLog> q = new ConcurrentLinkedQueue<>();
        ScheduledFuture<?> task = TimeBasedQEviction.scheduleTruncate(1000000L, 1000000L, q, scheduler, TimeValue.timeValueSeconds(1L));
        q.add(new JobContextLog(new io.crate.expression.reference.sys.job.JobContext(UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff01"), "select 1", 1L, User.CRATE_USER, classification), null, 2000L));
        q.add(new JobContextLog(new io.crate.expression.reference.sys.job.JobContext(UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff02"), "select 1", 1L, User.CRATE_USER, classification), null, 4000L));
        q.add(new JobContextLog(new io.crate.expression.reference.sys.job.JobContext(UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff03"), "select 1", 1L, User.CRATE_USER, classification), null, 7000L));
        TimeBasedQEviction.removeExpiredLogs(q, 10000L, 5000L);
        assertThat(q.size(), Matchers.is(1));
        assertThat(q.iterator().next().id(), Matchers.is(UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff03")));
        task.cancel(true);
    }

    @Test
    public void testTimedRamAccountingQueueSink() throws Exception {
        ConcurrentLinkedQueue<RamAccountingQueueSinkTest.NoopLog> q = new ConcurrentLinkedQueue<>();
        RamAccountingQueue<RamAccountingQueueSinkTest.NoopLog> ramAccountingQueue = new RamAccountingQueue(q, RamAccountingQueueSinkTest.breaker(), RamAccountingQueueSinkTest.NOOP_ESTIMATOR);
        TimeValue timeValue = TimeValue.timeValueSeconds(1L);
        ScheduledFuture<?> task = TimeBasedQEviction.scheduleTruncate(1000L, 1000L, q, scheduler, timeValue);
        logSink = new QueueSink(ramAccountingQueue, () -> {
            task.cancel(false);
            ramAccountingQueue.close();
        });
        for (int j = 0; j < 100; j++) {
            logSink.add(new RamAccountingQueueSinkTest.NoopLog());
        }
        assertThat(ramAccountingQueue.size(), Matchers.is(100));
        Thread.sleep(2000L);
        assertThat(ramAccountingQueue.size(), Matchers.is(0));
    }
}

