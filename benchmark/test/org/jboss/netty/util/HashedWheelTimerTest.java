/**
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.netty.util;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class HashedWheelTimerTest {
    @Test
    public void testScheduleTimeoutShouldNotRunBeforeDelay() throws InterruptedException {
        final Timer timer = new HashedWheelTimer();
        final CountDownLatch barrier = new CountDownLatch(1);
        final Timeout timeout = timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                Assert.fail("This should not have run");
                barrier.countDown();
            }
        }, 10, TimeUnit.SECONDS);
        Assert.assertFalse(barrier.await(3, TimeUnit.SECONDS));
        Assert.assertFalse("timer should not expire", timeout.isExpired());
        timer.stop();
    }

    @Test
    public void testScheduleTimeoutShouldRunAfterDelay() throws InterruptedException {
        final Timer timer = new HashedWheelTimer();
        final CountDownLatch barrier = new CountDownLatch(1);
        final Timeout timeout = timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                barrier.countDown();
            }
        }, 2, TimeUnit.SECONDS);
        Assert.assertTrue(barrier.await(3, TimeUnit.SECONDS));
        Assert.assertTrue("timer should expire", timeout.isExpired());
        timer.stop();
    }

    @Test
    public void testStopTimer() throws InterruptedException {
        final Timer timerProcessed = new HashedWheelTimer();
        for (int i = 0; i < 3; i++) {
            timerProcessed.newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                }
            }, 1, TimeUnit.MILLISECONDS);
        }
        Thread.sleep(1000L);// sleep for a second

        Assert.assertEquals("Number of unprocessed timeouts should be 0", 0, timerProcessed.stop().size());
        final Timer timerUnprocessed = new HashedWheelTimer();
        for (int i = 0; i < 5; i++) {
            timerUnprocessed.newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                }
            }, 5, TimeUnit.SECONDS);
        }
        Thread.sleep(1000L);// sleep for a second

        Assert.assertFalse("Number of unprocessed timeouts should be greater than 0", timerUnprocessed.stop().isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void testTimerShouldThrowExceptionAfterShutdownForNewTimeouts() throws InterruptedException {
        final Timer timer = new HashedWheelTimer();
        for (int i = 0; i < 3; i++) {
            timer.newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                }
            }, 1, TimeUnit.MILLISECONDS);
        }
        timer.stop();
        Thread.sleep(1000L);// sleep for a second

        timer.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                Assert.fail("This should not run");
            }
        }, 1, TimeUnit.SECONDS);
    }
}

