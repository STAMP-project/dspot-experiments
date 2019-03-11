/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.android.desugar.runtime;


import com.google.common.testing.GcFinalization;
import com.google.devtools.build.android.desugar.runtime.ThrowableExtension.ConcurrentWeakIdentityHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test for {@link ConcurrentWeakIdentityHashMap}. This test uses multi-threading, and needs GC
 * sometime to assert weak references, so it could take long.
 */
@RunWith(JUnit4.class)
public class ConcurrentWeakIdentityHashMapTest {
    private final Random random = new Random();

    @Test
    public void testSingleThreadedUse() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(5);
        ConcurrentWeakIdentityHashMap map = ConcurrentWeakIdentityHashMapTest.testConcurrentWeakIdentityHashMapSingleThreadedHelper(latch);
        for (int i = 0; i < 5; i++) {
            map.deleteEmptyKeys();
            GcFinalization.awaitFullGc();
        }
        latch.await();// wait for e1 to be garbage collected.

        map.deleteEmptyKeys();
        assertThat(map.size()).isEqualTo(0);
    }

    @Test
    public void testMultiThreadedUseMedium() throws InterruptedException {
        for (int i = 0; i < 10; ++i) {
            testMultiThreadedUse(50, 100);
        }
    }

    @Test
    public void testMultiThreadedUseLarge() throws InterruptedException {
        for (int i = 0; i < 5; ++i) {
            testMultiThreadedUse(100, 100);
        }
    }

    @Test
    public void testMultiThreadedUseSmall() throws InterruptedException {
        for (int i = 0; i < 10; ++i) {
            testMultiThreadedUse(20, 100);
        }
    }

    private static class ExceptionWithLatch extends Exception {
        private final CountDownLatch latch;

        private ExceptionWithLatch(String message, CountDownLatch latch) {
            super(message);
            this.latch = latch;
        }

        @Override
        public String toString() {
            return this.getMessage();
        }

        @Override
        protected void finalize() throws Throwable {
            latch.countDown();
        }
    }

    private static class Pair {
        final Throwable throwable;

        final Throwable suppressed;

        public Pair(Throwable throwable, Throwable suppressed) {
            this.throwable = throwable;
            this.suppressed = suppressed;
        }
    }

    private class Worker implements Runnable {
        private final ConcurrentWeakIdentityHashMap map;

        private final List<ConcurrentWeakIdentityHashMapTest.Pair> exceptionList = new ArrayList<>();

        private final String name;

        private Worker(String name, ConcurrentWeakIdentityHashMap map) {
            this.name = name;
            this.map = map;
        }

        public String getName() {
            return name;
        }

        @Override
        public void run() {
            Iterator<ConcurrentWeakIdentityHashMapTest.Pair> iterator = exceptionList.iterator();
            while (iterator.hasNext()) {
                int timeToSleep = random.nextInt(3);
                if ((random.nextBoolean()) && (timeToSleep > 0)) {
                    try {
                        Thread.sleep(timeToSleep);// add randomness to the scheduler.

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                ConcurrentWeakIdentityHashMapTest.Pair pair = iterator.next();
                List<Throwable> suppressed = map.get(pair.throwable, true);
                System.out.printf("add suppressed %s to %s\n", pair.suppressed, pair.throwable);
                suppressed.add(pair.suppressed);
            } 
        }
    }
}

