/**
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.exoplayer2.testutil;


import RobolectricUtil.CustomLooper;
import RobolectricUtil.CustomMessageQueue;
import android.os.HandlerThread;
import com.google.android.exoplayer2.util.Clock;
import com.google.android.exoplayer2.util.HandlerWrapper;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Unit test for {@link FakeClock}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(shadows = { CustomLooper.class, CustomMessageQueue.class })
public final class FakeClockTest {
    private static final long TIMEOUT_MS = 10000;

    @Test
    public void testAdvanceTime() {
        FakeClock fakeClock = new FakeClock(2000);
        assertThat(fakeClock.elapsedRealtime()).isEqualTo(2000);
        fakeClock.advanceTime(500);
        assertThat(fakeClock.elapsedRealtime()).isEqualTo(2500);
        fakeClock.advanceTime(0);
        assertThat(fakeClock.elapsedRealtime()).isEqualTo(2500);
    }

    @Test
    public void testSleep() throws InterruptedException {
        FakeClock fakeClock = new FakeClock(0);
        FakeClockTest.SleeperThread sleeperThread = new FakeClockTest.SleeperThread(fakeClock, 1000);
        sleeperThread.start();
        assertThat(sleeperThread.waitUntilAsleep(FakeClockTest.TIMEOUT_MS)).isTrue();
        assertThat(sleeperThread.isSleeping()).isTrue();
        fakeClock.advanceTime(1000);
        sleeperThread.join(FakeClockTest.TIMEOUT_MS);
        assertThat(sleeperThread.isSleeping()).isFalse();
        sleeperThread = new FakeClockTest.SleeperThread(fakeClock, 0);
        sleeperThread.start();
        sleeperThread.join();
        assertThat(sleeperThread.isSleeping()).isFalse();
        FakeClockTest.SleeperThread[] sleeperThreads = new FakeClockTest.SleeperThread[5];
        sleeperThreads[0] = new FakeClockTest.SleeperThread(fakeClock, 1000);
        sleeperThreads[1] = new FakeClockTest.SleeperThread(fakeClock, 1000);
        sleeperThreads[2] = new FakeClockTest.SleeperThread(fakeClock, 2000);
        sleeperThreads[3] = new FakeClockTest.SleeperThread(fakeClock, 3000);
        sleeperThreads[4] = new FakeClockTest.SleeperThread(fakeClock, 4000);
        for (FakeClockTest.SleeperThread thread : sleeperThreads) {
            thread.start();
            assertThat(thread.waitUntilAsleep(FakeClockTest.TIMEOUT_MS)).isTrue();
        }
        FakeClockTest.assertSleepingStates(new boolean[]{ true, true, true, true, true }, sleeperThreads);
        fakeClock.advanceTime(1500);
        assertThat(sleeperThreads[0].waitUntilAwake(FakeClockTest.TIMEOUT_MS)).isTrue();
        assertThat(sleeperThreads[1].waitUntilAwake(FakeClockTest.TIMEOUT_MS)).isTrue();
        FakeClockTest.assertSleepingStates(new boolean[]{ false, false, true, true, true }, sleeperThreads);
        fakeClock.advanceTime(2000);
        assertThat(sleeperThreads[2].waitUntilAwake(FakeClockTest.TIMEOUT_MS)).isTrue();
        assertThat(sleeperThreads[3].waitUntilAwake(FakeClockTest.TIMEOUT_MS)).isTrue();
        FakeClockTest.assertSleepingStates(new boolean[]{ false, false, false, false, true }, sleeperThreads);
        fakeClock.advanceTime(2000);
        for (FakeClockTest.SleeperThread thread : sleeperThreads) {
            thread.join(FakeClockTest.TIMEOUT_MS);
        }
        FakeClockTest.assertSleepingStates(new boolean[]{ false, false, false, false, false }, sleeperThreads);
    }

    @Test
    public void testPostDelayed() {
        HandlerThread handlerThread = new HandlerThread("FakeClockTest thread");
        handlerThread.start();
        FakeClock fakeClock = new FakeClock(0);
        HandlerWrapper handler = /* callback= */
        fakeClock.createHandler(handlerThread.getLooper(), null);
        FakeClockTest.TestRunnable[] testRunnables = new FakeClockTest.TestRunnable[]{ new FakeClockTest.TestRunnable(), new FakeClockTest.TestRunnable(), new FakeClockTest.TestRunnable(), new FakeClockTest.TestRunnable(), new FakeClockTest.TestRunnable() };
        handler.postDelayed(testRunnables[0], 0);
        handler.postDelayed(testRunnables[1], 100);
        handler.postDelayed(testRunnables[2], 200);
        FakeClockTest.waitForHandler(handler);
        FakeClockTest.assertTestRunnableStates(new boolean[]{ true, false, false, false, false }, testRunnables);
        fakeClock.advanceTime(150);
        handler.postDelayed(testRunnables[3], 50);
        handler.postDelayed(testRunnables[4], 100);
        FakeClockTest.waitForHandler(handler);
        FakeClockTest.assertTestRunnableStates(new boolean[]{ true, true, false, false, false }, testRunnables);
        fakeClock.advanceTime(50);
        FakeClockTest.waitForHandler(handler);
        FakeClockTest.assertTestRunnableStates(new boolean[]{ true, true, true, true, false }, testRunnables);
        fakeClock.advanceTime(1000);
        FakeClockTest.waitForHandler(handler);
        FakeClockTest.assertTestRunnableStates(new boolean[]{ true, true, true, true, true }, testRunnables);
    }

    private static final class SleeperThread extends Thread {
        private final Clock clock;

        private final long sleepDurationMs;

        private final CountDownLatch fallAsleepCountDownLatch;

        private final CountDownLatch wakeUpCountDownLatch;

        private volatile boolean isSleeping;

        public SleeperThread(Clock clock, long sleepDurationMs) {
            this.clock = clock;
            this.sleepDurationMs = sleepDurationMs;
            this.fallAsleepCountDownLatch = new CountDownLatch(1);
            this.wakeUpCountDownLatch = new CountDownLatch(1);
        }

        public boolean waitUntilAsleep(long timeoutMs) throws InterruptedException {
            return fallAsleepCountDownLatch.await(timeoutMs, TimeUnit.MILLISECONDS);
        }

        public boolean waitUntilAwake(long timeoutMs) throws InterruptedException {
            return wakeUpCountDownLatch.await(timeoutMs, TimeUnit.MILLISECONDS);
        }

        public boolean isSleeping() {
            return isSleeping;
        }

        @Override
        public void run() {
            // This relies on the FakeClock's methods synchronizing on its own monitor to ensure that
            // any interactions with it occur only after sleep() has called wait() or returned.
            synchronized(clock) {
                isSleeping = true;
                fallAsleepCountDownLatch.countDown();
                clock.sleep(sleepDurationMs);
                isSleeping = false;
                wakeUpCountDownLatch.countDown();
            }
        }
    }

    private static final class TestRunnable implements Runnable {
        public boolean hasRun;

        @Override
        public void run() {
            hasRun = true;
        }
    }
}

