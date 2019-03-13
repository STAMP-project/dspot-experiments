/**
 * Copyright (C) 2012 The Guava Authors
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
/**
 * import static java.lang.reflect.Modifier.isStatic;
 */
/**
 * import com.google.common.collect.ImmutableClassToInstanceMap;
 */
/**
 * import com.google.common.collect.ImmutableSet;
 */
/**
 * import com.google.common.collect.Lists;
 */
/**
 * import com.google.common.testing.NullPointerTester;
 */
/**
 * import com.google.common.testing.NullPointerTester.Visibility;
 */
/**
 * import org.easymock.EasyMock;
 */
/**
 * import org.mockito.Mockito;
 */
/**
 * import java.lang.reflect.Method;
 */
package org.apache.flume.source.shaded.guava;


import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.apache.flume.source.shaded.guava.RateLimiter.SleepingStopwatch;


// import java.util.concurrent.TimeUnit;
/**
 * Tests for RateLimiter.
 *
 * @author Dimitris Andreou
 */
public class TestRateLimiter extends TestCase {
    private static final double EPSILON = 1.0E-8;

    private final TestRateLimiter.FakeStopwatch stopwatch = new TestRateLimiter.FakeStopwatch();

    public void testSimple() {
        RateLimiter limiter = RateLimiter.create(stopwatch, 5.0);
        limiter.acquire();// R0.00, since it's the first request

        limiter.acquire();// R0.20

        limiter.acquire();// R0.20

        assertEvents("R0.00", "R0.20", "R0.20");
    }

    public void testImmediateTryAcquire() {
        RateLimiter r = RateLimiter.create(1);
        TestCase.assertTrue("Unable to acquire initial permit", r.tryAcquire());
        TestCase.assertFalse("Capable of acquiring secondary permit", r.tryAcquire());
    }

    public void testSimpleRateUpdate() {
        RateLimiter limiter = RateLimiter.create(5.0, 5, TimeUnit.SECONDS);
        TestCase.assertEquals(5.0, limiter.getRate());
        limiter.setRate(10.0);
        TestCase.assertEquals(10.0, limiter.getRate());
        try {
            limiter.setRate(0.0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            limiter.setRate((-10.0));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testAcquireParameterValidation() {
        RateLimiter limiter = RateLimiter.create(999);
        try {
            limiter.acquire(0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            limiter.acquire((-1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            limiter.tryAcquire(0);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            limiter.tryAcquire((-1));
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            limiter.tryAcquire(0, 1, TimeUnit.SECONDS);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            limiter.tryAcquire((-1), 1, TimeUnit.SECONDS);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testSimpleWithWait() {
        RateLimiter limiter = RateLimiter.create(stopwatch, 5.0);
        limiter.acquire();
        // R0.00
        stopwatch.sleepMillis(200);// U0.20, we are ready for the next request...

        limiter.acquire();
        // R0.00, ...which is granted immediately
        limiter.acquire();
        // R0.20
        assertEvents("R0.00", "U0.20", "R0.00", "R0.20");
    }

    public void testSimpleAcquireReturnValues() {
        RateLimiter limiter = RateLimiter.create(stopwatch, 5.0);
        TestCase.assertEquals(0.0, limiter.acquire(), TestRateLimiter.EPSILON);// R0.00

        stopwatch.sleepMillis(200);
        // U0.20, we are ready for the next request...
        TestCase.assertEquals(0.0, limiter.acquire(), TestRateLimiter.EPSILON);// R0.00, ...which is granted immediately

        TestCase.assertEquals(0.2, limiter.acquire(), TestRateLimiter.EPSILON);// R0.20

        assertEvents("R0.00", "U0.20", "R0.00", "R0.20");
    }

    public void testSimpleAcquireEarliestAvailableIsInPast() {
        RateLimiter limiter = RateLimiter.create(stopwatch, 5.0);
        TestCase.assertEquals(0.0, limiter.acquire(), TestRateLimiter.EPSILON);
        stopwatch.sleepMillis(400);
        TestCase.assertEquals(0.0, limiter.acquire(), TestRateLimiter.EPSILON);
        TestCase.assertEquals(0.0, limiter.acquire(), TestRateLimiter.EPSILON);
        TestCase.assertEquals(0.2, limiter.acquire(), TestRateLimiter.EPSILON);
    }

    public void testOneSecondBurst() {
        RateLimiter limiter = RateLimiter.create(stopwatch, 5.0);
        stopwatch.sleepMillis(1000);// max capacity reached

        stopwatch.sleepMillis(1000);// this makes no difference

        limiter.acquire(1);// R0.00, since it's the first request

        limiter.acquire(1);// R0.00, from capacity

        limiter.acquire(3);// R0.00, from capacity

        limiter.acquire(1);// R0.00, concluding a burst of 5 permits

        limiter.acquire();// R0.20, capacity exhausted

        // first request and burst
        assertEvents("U1.00", "U1.00", "R0.00", "R0.00", "R0.00", "R0.00", "R0.20");
    }

    public void testCreateWarmupParameterValidation() {
        RateLimiter.create(1.0, 1, TimeUnit.NANOSECONDS);
        RateLimiter.create(1.0, 0, TimeUnit.NANOSECONDS);
        try {
            RateLimiter.create(0.0, 1, TimeUnit.NANOSECONDS);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            RateLimiter.create(1.0, (-1), TimeUnit.NANOSECONDS);
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    public void testWarmUp() {
        RateLimiter limiter = RateLimiter.create(stopwatch, 2.0, 4000, TimeUnit.MILLISECONDS);
        for (int i = 0; i < 8; i++) {
            limiter.acquire();// #1

        }
        stopwatch.sleepMillis(500);// #2: to repay for the last acquire

        stopwatch.sleepMillis(4000);// #3: becomes cold again

        for (int i = 0; i < 8; i++) {
            limiter.acquire();// // #4

        }
        stopwatch.sleepMillis(500);// #5: to repay for the last acquire

        stopwatch.sleepMillis(2000);// #6: didn't get cold! It would take another 2 seconds to go cold

        for (int i = 0; i < 8; i++) {
            limiter.acquire();// #7

        }
        // #1
        // #2
        // #3
        // #4
        // #5
        // #6
        assertEvents("R0.00, R1.38, R1.13, R0.88, R0.63, R0.50, R0.50, R0.50", "U0.50", "U4.00", "R0.00, R1.38, R1.13, R0.88, R0.63, R0.50, R0.50, R0.50", "U0.50", "U2.00", "R0.00, R0.50, R0.50, R0.50, R0.50, R0.50, R0.50, R0.50");// #7

    }

    public void testWarmUpAndUpdate() {
        RateLimiter limiter = RateLimiter.create(stopwatch, 2.0, 4000, TimeUnit.MILLISECONDS);
        for (int i = 0; i < 8; i++) {
            limiter.acquire();// // #1

        }
        stopwatch.sleepMillis(4500);// #2: back to cold state (warmup period + repay last acquire)

        for (int i = 0; i < 3; i++) {
            // only three steps, we're somewhere in the warmup period
            limiter.acquire();// #3

        }
        limiter.setRate(4.0);// double the rate!

        limiter.acquire();// #4, we repay the debt of the last acquire (imposed by the old rate)

        for (int i = 0; i < 4; i++) {
            limiter.acquire();// #5

        }
        stopwatch.sleepMillis(4250);// #6, back to cold state (warmup period + repay last acquire)

        for (int i = 0; i < 11; i++) {
            limiter.acquire();// #7, showing off the warmup starting from totally cold

        }
        // make sure the areas (times) remain the same, while permits are different
        // #1
        // #2
        // #3, after that the rate changes
        // #4, this is what the throttling would be with the old rate
        // #5
        // #6
        // #7
        assertEvents("R0.00, R1.38, R1.13, R0.88, R0.63, R0.50, R0.50, R0.50", "U4.50", "R0.00, R1.38, R1.13", "R0.88", "R0.34, R0.28, R0.25, R0.25", "U4.25", "R0.00, R0.72, R0.66, R0.59, R0.53, R0.47, R0.41", "R0.34, R0.28, R0.25, R0.25");// #7 (cont.), note, this matches #5

    }

    public void testBurstyAndUpdate() {
        RateLimiter rateLimiter = RateLimiter.create(stopwatch, 1.0);
        rateLimiter.acquire(1);// no wait

        rateLimiter.acquire(1);// R1.00, to repay previous

        rateLimiter.setRate(2.0);// update the rate!

        rateLimiter.acquire(1);// R1.00, to repay previous (the previous was under the old rate!)

        rateLimiter.acquire(2);// R0.50, to repay previous (now the rate takes effect)

        rateLimiter.acquire(4);// R1.00, to repay previous

        rateLimiter.acquire(1);// R2.00, to repay previous

        assertEvents("R0.00", "R1.00", "R1.00", "R0.50", "R1.00", "R2.00");
    }

    public void testTryAcquire_noWaitAllowed() {
        RateLimiter limiter = RateLimiter.create(stopwatch, 5.0);
        TestCase.assertTrue(limiter.tryAcquire(0, TimeUnit.SECONDS));
        TestCase.assertFalse(limiter.tryAcquire(0, TimeUnit.SECONDS));
        TestCase.assertFalse(limiter.tryAcquire(0, TimeUnit.SECONDS));
        stopwatch.sleepMillis(100);
        TestCase.assertFalse(limiter.tryAcquire(0, TimeUnit.SECONDS));
    }

    public void testTryAcquire_someWaitAllowed() {
        RateLimiter limiter = RateLimiter.create(stopwatch, 5.0);
        TestCase.assertTrue(limiter.tryAcquire(0, TimeUnit.SECONDS));
        TestCase.assertTrue(limiter.tryAcquire(200, TimeUnit.MILLISECONDS));
        TestCase.assertFalse(limiter.tryAcquire(100, TimeUnit.MILLISECONDS));
        stopwatch.sleepMillis(100);
        TestCase.assertTrue(limiter.tryAcquire(100, TimeUnit.MILLISECONDS));
    }

    public void testTryAcquire_overflow() {
        RateLimiter limiter = RateLimiter.create(stopwatch, 5.0);
        TestCase.assertTrue(limiter.tryAcquire(0, TimeUnit.MICROSECONDS));
        stopwatch.sleepMillis(100);
        TestCase.assertTrue(limiter.tryAcquire(Long.MAX_VALUE, TimeUnit.MICROSECONDS));
    }

    public void testTryAcquire_negative() {
        RateLimiter limiter = RateLimiter.create(stopwatch, 5.0);
        TestCase.assertTrue(limiter.tryAcquire(5, 0, TimeUnit.SECONDS));
        stopwatch.sleepMillis(900);
        TestCase.assertFalse(limiter.tryAcquire(1, Long.MIN_VALUE, TimeUnit.SECONDS));
        stopwatch.sleepMillis(100);
        TestCase.assertTrue(limiter.tryAcquire(1, (-1), TimeUnit.SECONDS));
    }

    public void testSimpleWeights() {
        RateLimiter rateLimiter = RateLimiter.create(stopwatch, 1.0);
        rateLimiter.acquire(1);// no wait

        rateLimiter.acquire(1);// R1.00, to repay previous

        rateLimiter.acquire(2);// R1.00, to repay previous

        rateLimiter.acquire(4);// R2.00, to repay previous

        rateLimiter.acquire(8);// R4.00, to repay previous

        rateLimiter.acquire(1);// R8.00, to repay previous

        assertEvents("R0.00", "R1.00", "R1.00", "R2.00", "R4.00", "R8.00");
    }

    public void testInfinity_Bursty() {
        RateLimiter limiter = RateLimiter.create(stopwatch, Double.POSITIVE_INFINITY);
        limiter.acquire(((Integer.MAX_VALUE) / 4));
        limiter.acquire(((Integer.MAX_VALUE) / 2));
        limiter.acquire(Integer.MAX_VALUE);
        assertEvents("R0.00", "R0.00", "R0.00");// no wait, infinite rate!

        limiter.setRate(2.0);
        limiter.acquire();
        limiter.acquire();
        limiter.acquire();
        limiter.acquire();
        limiter.acquire();
        // First comes the saved-up burst, which defaults to a 1-second burst (2 requests).
        // Now comes the free request.
        // Now it's 0.5 seconds per request.
        assertEvents("R0.00", "R0.00", "R0.00", "R0.50", "R0.50");
        limiter.setRate(Double.POSITIVE_INFINITY);
        limiter.acquire();
        limiter.acquire();
        limiter.acquire();
        assertEvents("R0.50", "R0.00", "R0.00");// we repay the last request (.5sec), then back to +oo

    }

    /**
     * https://code.google.com/p/guava-libraries/issues/detail?id=1791
     */
    public void testInfinity_BustyTimeElapsed() {
        RateLimiter limiter = RateLimiter.create(stopwatch, Double.POSITIVE_INFINITY);
        stopwatch.instant += 1000000;
        limiter.setRate(2.0);
        for (int i = 0; i < 5; i++) {
            limiter.acquire();
        }
        // First comes the saved-up burst, which defaults to a 1-second burst (2 requests).
        // Now comes the free request.
        // Now it's 0.5 seconds per request.
        assertEvents("R0.00", "R0.00", "R0.00", "R0.50", "R0.50");
    }

    public void testInfinity_WarmUp() {
        RateLimiter limiter = RateLimiter.create(stopwatch, Double.POSITIVE_INFINITY, 10, TimeUnit.SECONDS);
        limiter.acquire(((Integer.MAX_VALUE) / 4));
        limiter.acquire(((Integer.MAX_VALUE) / 2));
        limiter.acquire(Integer.MAX_VALUE);
        assertEvents("R0.00", "R0.00", "R0.00");
        limiter.setRate(1.0);
        limiter.acquire();
        limiter.acquire();
        limiter.acquire();
        assertEvents("R0.00", "R1.00", "R1.00");
        limiter.setRate(Double.POSITIVE_INFINITY);
        limiter.acquire();
        limiter.acquire();
        limiter.acquire();
        assertEvents("R1.00", "R0.00", "R0.00");
    }

    public void testInfinity_WarmUpTimeElapsed() {
        RateLimiter limiter = RateLimiter.create(stopwatch, Double.POSITIVE_INFINITY, 10, TimeUnit.SECONDS);
        stopwatch.instant += 1000000;
        limiter.setRate(1.0);
        for (int i = 0; i < 5; i++) {
            limiter.acquire();
        }
        assertEvents("R0.00", "R1.00", "R1.00", "R1.00", "R1.00");
    }

    /**
     * Make sure that bursts can never go above 1-second-worth-of-work for the current
     * rate, even when we change the rate.
     */
    public void testWeNeverGetABurstMoreThanOneSec() {
        RateLimiter limiter = RateLimiter.create(stopwatch, 1.0);
        int[] rates = new int[]{ 1000, 1, 10, 1000000, 10, 1 };
        for (int rate : rates) {
            int oneSecWorthOfWork = rate;
            stopwatch.sleepMillis((rate * 1000));
            limiter.setRate(rate);
            long burst = measureTotalTimeMillis(limiter, oneSecWorthOfWork, new Random());
            // we allow one second worth of work to go in a burst (i.e. take less than a second)
            TestCase.assertTrue((burst <= 1000));
            long afterBurst = measureTotalTimeMillis(limiter, oneSecWorthOfWork, new Random());
            // but work beyond that must take at least one second
            TestCase.assertTrue((afterBurst >= 1000));
        }
    }

    /**
     * This neat test shows that no matter what weights we use in our requests, if we push X
     * amount of permits in a cool state, where X = rate * timeToCoolDown, and we have
     * specified a timeToWarmUp() period, it will cost as the prescribed amount of time. E.g.,
     * calling [acquire(5), acquire(1)] takes exactly the same time as
     * [acquire(2), acquire(3), acquire(1)].
     */
    public void testTimeToWarmUpIsHonouredEvenWithWeights() {
        Random random = new Random();
        int maxPermits = 10;
        double[] qpsToTest = new double[]{ 4.0, 2.0, 1.0, 0.5, 0.1 };
        for (int trial = 0; trial < 100; trial++) {
            for (double qps : qpsToTest) {
                // Since we know that: maxPermits = 0.5 * warmup / stableInterval;
                // then if maxPermits == 10, we have:
                // warmupSeconds = 20 / qps
                long warmupMillis = ((long) (((2 * maxPermits) / qps) * 1000.0));
                RateLimiter rateLimiter = RateLimiter.create(stopwatch, qps, warmupMillis, TimeUnit.MILLISECONDS);
                TestCase.assertEquals(warmupMillis, measureTotalTimeMillis(rateLimiter, maxPermits, random));
            }
        }
    }

    /**
     * The stopwatch gathers events and presents them as strings.
     * R0.6 means a delay of 0.6 seconds caused by the (R)ateLimiter
     * U1.0 means the (U)ser caused the stopwatch to sleep for a second.
     */
    static class FakeStopwatch extends SleepingStopwatch {
        long instant = 0L;

        final List<String> events = Lists.newArrayList();

        @Override
        public long readMicros() {
            return TimeUnit.NANOSECONDS.toMicros(instant);
        }

        void sleepMillis(int millis) {
            sleepMicros("U", TimeUnit.MILLISECONDS.toMicros(millis));
        }

        void sleepMicros(String caption, long micros) {
            instant += TimeUnit.MICROSECONDS.toNanos(micros);
            events.add((caption + (String.format("%3.2f", (micros / 1000000.0)))));
        }

        @Override
        void sleepMicrosUninterruptibly(long micros) {
            sleepMicros("R", micros);
        }

        String readEventsAndClear() {
            try {
                return events.toString();
            } finally {
                events.clear();
            }
        }

        @Override
        public String toString() {
            return events.toString();
        }
    }
}

