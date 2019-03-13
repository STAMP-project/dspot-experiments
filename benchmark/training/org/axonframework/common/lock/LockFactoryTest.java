/**
 * Copyright (c) 2010-2018. Axon Framework
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
package org.axonframework.common.lock;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test that discovers <a href="https://github.com/AxonFramework/AxonFramework/issues/32">issue #32</a>.
 */
public class LockFactoryTest {
    private static final int THREAD_COUNT = 4;

    private static final int ATTEMPTS = 3000;

    private LockFactory lockFactory;

    private String aggregateIdentifier;

    @Test
    public void testObtainLock() {
        ExecutorService service = Executors.newFixedThreadPool(LockFactoryTest.THREAD_COUNT);
        LockFactoryTest.LockUnlock[] attempts = new LockFactoryTest.LockUnlock[LockFactoryTest.ATTEMPTS];
        for (int t = 0; t < (LockFactoryTest.ATTEMPTS); t++) {
            attempts[t] = new LockFactoryTest.LockUnlock(t);
        }
        long startTime = System.currentTimeMillis();
        for (LockFactoryTest.LockUnlock attempt : attempts) {
            service.submit(attempt);
        }
        service.shutdown();
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Assert.fail("Interrupted");
        }
        int failedAttempts = 0;
        for (LockFactoryTest.LockUnlock attempt : attempts) {
            if (!(attempt.success)) {
                failedAttempts++;
            }
        }
        Assert.assertEquals("Failed LockUnlock count", 0, failedAttempts);
    }

    private class LockUnlock implements Runnable {
        private int instanceIndex;

        private boolean success;

        public LockUnlock(int instanceIndex) {
            this.instanceIndex = instanceIndex;
        }

        @Override
        public void run() {
            int locksAcquired = 0;
            int locksReleased = 0;
            try {
                Lock lock = lockFactory.obtainLock(aggregateIdentifier);
                locksAcquired++;
                lock.release();
                locksReleased++;
                success = true;
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                sw.append("Failed ").append(Integer.toString(instanceIndex)).append(" aquired=").append(Integer.toString(locksAcquired)).append(" release=").append(Integer.toString(locksReleased)).append(" Exception:");
                PrintWriter writer = new PrintWriter(sw);
                e.printStackTrace(writer);
                System.out.println(sw.toString());
            }
        }
    }
}

