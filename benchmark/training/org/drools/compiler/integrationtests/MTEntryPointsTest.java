/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.time.SessionPseudoClock;


/**
 * Tests inserting events into KIE Session from multiple threads using one and
 * two entry points.
 *
 * BZ-967599
 */
public class MTEntryPointsTest {
    private KieSession kieSession;

    /**
     * Inserts events using multiple threads into one EntryPoint. The insert
     * operation is synchronized on corresponding SessionEntryPoint instance.
     */
    @Test
    public void testOneEntryPoint() throws Exception {
        final EntryPoint firstThreadEntryPoint = kieSession.getEntryPoint("FirstStream");
        final int numInsertersInEachEntryPoint = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(numInsertersInEachEntryPoint);
        try {
            final List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < numInsertersInEachEntryPoint; i++) {
                // future for exception watching
                final Future<?> futureForFirstThread = executorService.submit(new MTEntryPointsTest.TestInserter(kieSession, firstThreadEntryPoint));
                futures.add(futureForFirstThread);
            }
            for (final Future<?> f : futures) {
                f.get(30, TimeUnit.SECONDS);
            }
        } finally {
            executorService.shutdownNow();
        }
    }

    /**
     * Inserts events using multiple threads into two EntryPoints. The insert
     * operation is synchronized on corresponding SessionEntryPoint instance.
     */
    @Test
    public void testTwoEntryPoints() throws Exception {
        final EntryPoint firstThreadEntryPoint = kieSession.getEntryPoint("FirstStream");
        final EntryPoint secondThreadEntryPoint = kieSession.getEntryPoint("SecondStream");
        final int numInsertersInEachEntryPoint = 10;
        final int numThreadPoolCapacity = numInsertersInEachEntryPoint * 2;
        final ExecutorService executorService = Executors.newFixedThreadPool(numThreadPoolCapacity);
        try {
            final List<Future<?>> futures = new ArrayList<>();
            for (int i = 0; i < numInsertersInEachEntryPoint; i++) {
                // working only with first stream, future for exception watching
                final Future<?> futureForFirstThread = executorService.submit(new MTEntryPointsTest.TestInserter(kieSession, firstThreadEntryPoint));
                futures.add(futureForFirstThread);
                // working only with second stream, future for exception watching
                final Future<?> futureForSecondThread = executorService.submit(new MTEntryPointsTest.TestInserter(kieSession, secondThreadEntryPoint));
                futures.add(futureForSecondThread);
            }
            for (final Future<?> f : futures) {
                f.get(30, TimeUnit.SECONDS);
            }
        } finally {
            executorService.shutdownNow();
        }
    }

    /**
     * Inserts 10 test events into specified EntryPoint and advances pseudo-clock
     * time by a fixed amount.
     *
     * Insert operation is synchronized on given SessionEntryPoint instance.
     */
    public static class TestInserter implements Runnable {
        private final EntryPoint entryPoint;

        private final KieSession kieSession;

        public TestInserter(final KieSession kieSession, final EntryPoint entryPoint) {
            this.kieSession = kieSession;
            this.entryPoint = entryPoint;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                synchronized(entryPoint) {
                    entryPoint.insert(new MTEntryPointsTest.MessageEvent(i));
                }
                advanceTime(100);
            }
        }

        private void advanceTime(long millis) {
            SessionPseudoClock pseudoClock = kieSession.getSessionClock();
            pseudoClock.advanceTime(millis, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Immutable event used in the test.
     */
    public static class MessageEvent {
        private int value;

        public MessageEvent(final int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
}

