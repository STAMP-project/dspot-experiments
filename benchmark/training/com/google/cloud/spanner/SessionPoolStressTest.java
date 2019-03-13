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


import SessionPoolOptions.Builder;
import com.google.common.util.concurrent.Uninterruptibles;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static ErrorCode.RESOURCE_EXHAUSTED;


/**
 * Stress test for {@code SessionPool} which does multiple operations on the pool, making some of
 * them fail and asserts that all the invariants are maintained.
 */
@RunWith(Parameterized.class)
public class SessionPoolStressTest extends BaseSessionPoolTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Parameterized.Parameter(0)
    public double writeSessionsFraction;

    @Parameterized.Parameter(1)
    public boolean shouldBlock;

    DatabaseId db = DatabaseId.of("projects/p/instances/i/databases/unused");

    SessionPool pool;

    SessionPoolOptions options;

    Object lock = new Object();

    Random random = new Random();

    BaseSessionPoolTest.FakeClock clock = new BaseSessionPoolTest.FakeClock();

    Map<String, Boolean> sessions = new HashMap<>();

    // Exception keeps track of where the session was closed at.
    Map<String, Exception> closedSessions = new HashMap<>();

    Set<String> expiredSessions = new HashSet<>();

    SpannerImpl mockSpanner;

    int maxAliveSessions;

    int minSessionsWhenSessionClosed = Integer.MAX_VALUE;

    Exception e;

    @Test
    public void stressTest() throws Exception {
        int concurrentThreads = 10;
        final int numOperationsPerThread = 1000;
        final CountDownLatch releaseThreads = new CountDownLatch(1);
        final CountDownLatch threadsDone = new CountDownLatch(concurrentThreads);
        final int writeOperationFraction = 5;
        setupSpanner(db);
        int minSessions = 2;
        int maxSessions = concurrentThreads / 2;
        float writeSessionsFraction = 0.5F;
        SessionPoolOptions.Builder builder = SessionPoolOptions.newBuilder().setMinSessions(minSessions).setMaxSessions(maxSessions).setWriteSessionsFraction(writeSessionsFraction);
        if (shouldBlock) {
            builder.setBlockIfPoolExhausted();
        } else {
            builder.setFailIfPoolExhausted();
        }
        pool = SessionPool.createPool(builder.build(), new BaseSessionPoolTest.TestExecutorFactory(), db, mockSpanner, clock);
        for (int i = 0; i < concurrentThreads; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Uninterruptibles.awaitUninterruptibly(releaseThreads);
                    for (int j = 0; j < numOperationsPerThread; j++) {
                        try {
                            Session session = null;
                            if ((random.nextInt(10)) < writeOperationFraction) {
                                session = pool.getReadWriteSession();
                                assertWritePrepared(session);
                            } else {
                                session = pool.getReadSession();
                            }
                            Uninterruptibles.sleepUninterruptibly(random.nextInt(5), TimeUnit.MILLISECONDS);
                            resetTransaction(session);
                            session.close();
                        } catch (SpannerException e) {
                            if (((e.getErrorCode()) != (RESOURCE_EXHAUSTED)) || (shouldBlock)) {
                                setFailed(e);
                            }
                        } catch (Exception e) {
                            setFailed(e);
                        }
                    }
                    threadsDone.countDown();
                }
            }).start();
        }
        // Start maintenance threads in tight loop
        final AtomicBoolean stopMaintenance = new AtomicBoolean(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!(stopMaintenance.get())) {
                    runMaintainanceLoop(clock, pool, 1);
                } 
            }
        }).start();
        releaseThreads.countDown();
        threadsDone.await();
        synchronized(lock) {
            assertThat(maxAliveSessions).isAtMost(maxSessions);
        }
        pool.closeAsync().get();
        Exception e = getFailedError();
        if (e != null) {
            throw e;
        }
    }
}

