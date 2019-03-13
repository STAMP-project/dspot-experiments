/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.client.semaphore;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientSemaphoreThreadedTest {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance client;

    @Test
    public void concurrent_trySemaphoreTest() {
        concurrent_trySemaphoreTest(false);
    }

    @Test
    public void concurrent_trySemaphoreWithTimeOutTest() {
        concurrent_trySemaphoreTest(true);
    }

    static class TrySemaphoreThread extends ClientSemaphoreThreadedTest.SemaphoreTestThread {
        public TrySemaphoreThread(ISemaphore semaphore, AtomicInteger upTotal, AtomicInteger downTotal) {
            super(semaphore, upTotal, downTotal);
        }

        public void iterativelyRun() throws Exception {
            if (semaphore.tryAcquire()) {
                work();
                semaphore.release();
            }
        }
    }

    static class TrySemaphoreTimeOutThread extends ClientSemaphoreThreadedTest.SemaphoreTestThread {
        public TrySemaphoreTimeOutThread(ISemaphore semaphore, AtomicInteger upTotal, AtomicInteger downTotal) {
            super(semaphore, upTotal, downTotal);
        }

        public void iterativelyRun() throws Exception {
            if (semaphore.tryAcquire(1, TimeUnit.MILLISECONDS)) {
                work();
                semaphore.release();
            }
        }
    }

    abstract static class SemaphoreTestThread extends Thread {
        private static final int MAX_ITTERATIONS = 1000 * 10;

        public volatile Throwable error;

        protected final ISemaphore semaphore;

        protected final AtomicInteger upTotal;

        protected final AtomicInteger downTotal;

        private final Random random = new Random();

        public SemaphoreTestThread(ISemaphore semaphore, AtomicInteger upTotal, AtomicInteger downTotal) {
            this.semaphore = semaphore;
            this.upTotal = upTotal;
            this.downTotal = downTotal;
        }

        public final void run() {
            try {
                for (int i = 0; i < (ClientSemaphoreThreadedTest.SemaphoreTestThread.MAX_ITTERATIONS); i++) {
                    iterativelyRun();
                }
            } catch (Throwable e) {
                error = e;
            }
        }

        abstract void iterativelyRun() throws Exception;

        protected void work() {
            final int delta = random.nextInt(1000);
            upTotal.addAndGet(delta);
            downTotal.addAndGet((-delta));
        }
    }
}

