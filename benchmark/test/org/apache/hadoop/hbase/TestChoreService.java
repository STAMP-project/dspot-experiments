/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase;


import ChoreService.MIN_CORE_POOL_SIZE;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ChoreService.MIN_CORE_POOL_SIZE;


@Category(SmallTests.class)
public class TestChoreService {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestChoreService.class);

    public static final Logger log = LoggerFactory.getLogger(TestChoreService.class);

    /**
     * A few ScheduledChore samples that are useful for testing with ChoreService
     */
    public static class ScheduledChoreSamples {
        /**
         * Straight forward stopper implementation that is used by default when one is not provided
         */
        public static class SampleStopper implements Stoppable {
            private boolean stopped = false;

            @Override
            public void stop(String why) {
                stopped = true;
            }

            @Override
            public boolean isStopped() {
                return stopped;
            }
        }

        /**
         * Sleeps for longer than the scheduled period. This chore always misses its scheduled periodic
         * executions
         */
        public static class SlowChore extends ScheduledChore {
            public SlowChore(String name, int period) {
                this(name, new TestChoreService.ScheduledChoreSamples.SampleStopper(), period);
            }

            public SlowChore(String name, Stoppable stopper, int period) {
                super(name, stopper, period);
            }

            @Override
            protected boolean initialChore() {
                try {
                    Thread.sleep(((getPeriod()) * 2));
                } catch (InterruptedException e) {
                    TestChoreService.log.warn("", e);
                }
                return true;
            }

            @Override
            protected void chore() {
                try {
                    Thread.sleep(((getPeriod()) * 2));
                } catch (InterruptedException e) {
                    TestChoreService.log.warn("", e);
                }
            }
        }

        /**
         * Lightweight ScheduledChore used primarily to fill the scheduling queue in tests
         */
        public static class DoNothingChore extends ScheduledChore {
            public DoNothingChore(String name, int period) {
                super(name, new TestChoreService.ScheduledChoreSamples.SampleStopper(), period);
            }

            public DoNothingChore(String name, Stoppable stopper, int period) {
                super(name, stopper, period);
            }

            @Override
            protected void chore() {
                // DO NOTHING
            }
        }

        public static class SleepingChore extends ScheduledChore {
            private int sleepTime;

            public SleepingChore(String name, int chorePeriod, int sleepTime) {
                this(name, new TestChoreService.ScheduledChoreSamples.SampleStopper(), chorePeriod, sleepTime);
            }

            public SleepingChore(String name, Stoppable stopper, int period, int sleepTime) {
                super(name, stopper, period);
                this.sleepTime = sleepTime;
            }

            @Override
            protected boolean initialChore() {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    TestChoreService.log.warn("", e);
                }
                return true;
            }

            @Override
            protected void chore() {
                try {
                    Thread.sleep(sleepTime);
                } catch (Exception e) {
                    TestChoreService.log.warn("", e);
                }
            }
        }

        public static class CountingChore extends ScheduledChore {
            private int countOfChoreCalls;

            private boolean outputOnTicks = false;

            public CountingChore(String name, int period) {
                this(name, new TestChoreService.ScheduledChoreSamples.SampleStopper(), period);
            }

            public CountingChore(String name, Stoppable stopper, int period) {
                this(name, stopper, period, false);
            }

            public CountingChore(String name, Stoppable stopper, int period, final boolean outputOnTicks) {
                super(name, stopper, period);
                this.countOfChoreCalls = 0;
                this.outputOnTicks = outputOnTicks;
            }

            @Override
            protected boolean initialChore() {
                (countOfChoreCalls)++;
                if (outputOnTicks) {
                    outputTickCount();
                }
                return true;
            }

            @Override
            protected void chore() {
                (countOfChoreCalls)++;
                if (outputOnTicks) {
                    outputTickCount();
                }
            }

            private void outputTickCount() {
                TestChoreService.log.info(((("Chore: " + (getName())) + ". Count of chore calls: ") + (countOfChoreCalls)));
            }

            public int getCountOfChoreCalls() {
                return countOfChoreCalls;
            }

            public boolean isOutputtingOnTicks() {
                return outputOnTicks;
            }

            public void setOutputOnTicks(boolean o) {
                outputOnTicks = o;
            }
        }

        /**
         * A Chore that will try to execute the initial chore a few times before succeeding. Once the
         * initial chore is complete the chore cancels itself
         */
        public static class FailInitialChore extends ScheduledChore {
            private int numberOfFailures;

            private int failureThreshold;

            /**
             *
             *
             * @param failThreshold
             * 		Number of times the Chore fails when trying to execute initialChore
             * 		before succeeding.
             */
            public FailInitialChore(String name, int period, int failThreshold) {
                this(name, new TestChoreService.ScheduledChoreSamples.SampleStopper(), period, failThreshold);
            }

            public FailInitialChore(String name, Stoppable stopper, int period, int failThreshold) {
                super(name, stopper, period);
                numberOfFailures = 0;
                failureThreshold = failThreshold;
            }

            @Override
            protected boolean initialChore() {
                if ((numberOfFailures) < (failureThreshold)) {
                    (numberOfFailures)++;
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            protected void chore() {
                Assert.assertTrue(((numberOfFailures) == (failureThreshold)));
                cancel(false);
            }
        }
    }

    @Test
    public void testInitialChorePrecedence() throws InterruptedException {
        ChoreService service = new ChoreService("testInitialChorePrecedence");
        final int period = 100;
        final int failureThreshold = 5;
        try {
            ScheduledChore chore = new TestChoreService.ScheduledChoreSamples.FailInitialChore("chore", period, failureThreshold);
            service.scheduleChore(chore);
            int loopCount = 0;
            boolean brokeOutOfLoop = false;
            while ((!(chore.isInitialChoreComplete())) && (chore.isScheduled())) {
                Thread.sleep((failureThreshold * period));
                loopCount++;
                if (loopCount > 3) {
                    brokeOutOfLoop = true;
                    break;
                }
            } 
            Assert.assertFalse(brokeOutOfLoop);
        } finally {
            shutdownService(service);
        }
    }

    @Test
    public void testCancelChore() throws InterruptedException {
        final int period = 100;
        ScheduledChore chore1 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("chore1", period);
        ChoreService service = new ChoreService("testCancelChore");
        try {
            service.scheduleChore(chore1);
            Assert.assertTrue(chore1.isScheduled());
            chore1.cancel(true);
            Assert.assertFalse(chore1.isScheduled());
            Assert.assertTrue(((service.getNumberOfScheduledChores()) == 0));
        } finally {
            shutdownService(service);
        }
    }

    @Test
    public void testScheduledChoreConstruction() {
        final String NAME = "chore";
        final int PERIOD = 100;
        final long VALID_DELAY = 0;
        final long INVALID_DELAY = -100;
        final TimeUnit UNIT = TimeUnit.NANOSECONDS;
        ScheduledChore chore1 = new ScheduledChore(NAME, new TestChoreService.ScheduledChoreSamples.SampleStopper(), PERIOD, VALID_DELAY, UNIT) {
            @Override
            protected void chore() {
                // DO NOTHING
            }
        };
        Assert.assertEquals("Name construction failed", NAME, chore1.getName());
        Assert.assertEquals("Period construction failed", PERIOD, chore1.getPeriod());
        Assert.assertEquals("Initial Delay construction failed", VALID_DELAY, chore1.getInitialDelay());
        Assert.assertEquals("TimeUnit construction failed", UNIT, chore1.getTimeUnit());
        ScheduledChore invalidDelayChore = new ScheduledChore(NAME, new TestChoreService.ScheduledChoreSamples.SampleStopper(), PERIOD, INVALID_DELAY, UNIT) {
            @Override
            protected void chore() {
                // DO NOTHING
            }
        };
        Assert.assertEquals("Initial Delay should be set to 0 when invalid", 0, invalidDelayChore.getInitialDelay());
    }

    @Test
    public void testChoreServiceConstruction() throws InterruptedException {
        final int corePoolSize = 10;
        final int defaultCorePoolSize = MIN_CORE_POOL_SIZE;
        ChoreService customInit = new ChoreService("testChoreServiceConstruction_custom", corePoolSize, false);
        try {
            Assert.assertEquals(corePoolSize, customInit.getCorePoolSize());
        } finally {
            shutdownService(customInit);
        }
        ChoreService defaultInit = new ChoreService("testChoreServiceConstruction_default");
        try {
            Assert.assertEquals(defaultCorePoolSize, defaultInit.getCorePoolSize());
        } finally {
            shutdownService(defaultInit);
        }
        ChoreService invalidInit = new ChoreService("testChoreServiceConstruction_invalid", (-10), false);
        try {
            Assert.assertEquals(defaultCorePoolSize, invalidInit.getCorePoolSize());
        } finally {
            shutdownService(invalidInit);
        }
    }

    @Test
    public void testFrequencyOfChores() throws InterruptedException {
        final int period = 100;
        // Small delta that acts as time buffer (allowing chores to complete if running slowly)
        final int delta = 5;
        ChoreService service = new ChoreService("testFrequencyOfChores");
        TestChoreService.ScheduledChoreSamples.CountingChore chore = new TestChoreService.ScheduledChoreSamples.CountingChore("countingChore", period);
        try {
            service.scheduleChore(chore);
            Thread.sleep(((10 * period) + delta));
            Assert.assertTrue(((chore.getCountOfChoreCalls()) == 11));
            Thread.sleep((10 * period));
            Assert.assertTrue(((chore.getCountOfChoreCalls()) == 21));
        } finally {
            shutdownService(service);
        }
    }

    @Test
    public void testForceTrigger() throws InterruptedException {
        final int period = 100;
        final int delta = 10;
        ChoreService service = new ChoreService("testForceTrigger");
        final TestChoreService.ScheduledChoreSamples.CountingChore chore = new TestChoreService.ScheduledChoreSamples.CountingChore("countingChore", period);
        try {
            service.scheduleChore(chore);
            Thread.sleep(((10 * period) + delta));
            Assert.assertTrue(((chore.getCountOfChoreCalls()) == 11));
            // Force five runs of the chore to occur, sleeping between triggers to ensure the
            // chore has time to run
            triggerNow();
            Thread.sleep(delta);
            triggerNow();
            Thread.sleep(delta);
            triggerNow();
            Thread.sleep(delta);
            triggerNow();
            Thread.sleep(delta);
            triggerNow();
            Thread.sleep(delta);
            Assert.assertTrue(("" + (chore.getCountOfChoreCalls())), ((chore.getCountOfChoreCalls()) == 16));
            Thread.sleep(((10 * period) + delta));
            // Be loosey-goosey. It used to be '26' but it was a big flakey relying on timing.
            Assert.assertTrue(("" + (chore.getCountOfChoreCalls())), ((chore.getCountOfChoreCalls()) > 16));
        } finally {
            shutdownService(service);
        }
    }

    @Test
    public void testCorePoolIncrease() throws InterruptedException {
        final int initialCorePoolSize = 3;
        ChoreService service = new ChoreService("testCorePoolIncrease", initialCorePoolSize, false);
        try {
            Assert.assertEquals(("Should have a core pool of size: " + initialCorePoolSize), initialCorePoolSize, service.getCorePoolSize());
            final int slowChorePeriod = 100;
            TestChoreService.ScheduledChoreSamples.SlowChore slowChore1 = new TestChoreService.ScheduledChoreSamples.SlowChore("slowChore1", slowChorePeriod);
            TestChoreService.ScheduledChoreSamples.SlowChore slowChore2 = new TestChoreService.ScheduledChoreSamples.SlowChore("slowChore2", slowChorePeriod);
            TestChoreService.ScheduledChoreSamples.SlowChore slowChore3 = new TestChoreService.ScheduledChoreSamples.SlowChore("slowChore3", slowChorePeriod);
            service.scheduleChore(slowChore1);
            service.scheduleChore(slowChore2);
            service.scheduleChore(slowChore3);
            Thread.sleep((slowChorePeriod * 10));
            Assert.assertEquals("Should not create more pools than scheduled chores", 3, service.getCorePoolSize());
            TestChoreService.ScheduledChoreSamples.SlowChore slowChore4 = new TestChoreService.ScheduledChoreSamples.SlowChore("slowChore4", slowChorePeriod);
            service.scheduleChore(slowChore4);
            Thread.sleep((slowChorePeriod * 10));
            Assert.assertEquals("Chores are missing their start time. Should expand core pool size", 4, service.getCorePoolSize());
            TestChoreService.ScheduledChoreSamples.SlowChore slowChore5 = new TestChoreService.ScheduledChoreSamples.SlowChore("slowChore5", slowChorePeriod);
            service.scheduleChore(slowChore5);
            Thread.sleep((slowChorePeriod * 10));
            Assert.assertEquals("Chores are missing their start time. Should expand core pool size", 5, service.getCorePoolSize());
        } finally {
            shutdownService(service);
        }
    }

    @Test
    public void testCorePoolDecrease() throws InterruptedException {
        final int initialCorePoolSize = 3;
        ChoreService service = new ChoreService("testCorePoolDecrease", initialCorePoolSize, false);
        final int chorePeriod = 100;
        try {
            // Slow chores always miss their start time and thus the core pool size should be at least as
            // large as the number of running slow chores
            TestChoreService.ScheduledChoreSamples.SlowChore slowChore1 = new TestChoreService.ScheduledChoreSamples.SlowChore("slowChore1", chorePeriod);
            TestChoreService.ScheduledChoreSamples.SlowChore slowChore2 = new TestChoreService.ScheduledChoreSamples.SlowChore("slowChore2", chorePeriod);
            TestChoreService.ScheduledChoreSamples.SlowChore slowChore3 = new TestChoreService.ScheduledChoreSamples.SlowChore("slowChore3", chorePeriod);
            service.scheduleChore(slowChore1);
            service.scheduleChore(slowChore2);
            service.scheduleChore(slowChore3);
            Thread.sleep((chorePeriod * 10));
            Assert.assertEquals("Should not create more pools than scheduled chores", service.getNumberOfScheduledChores(), service.getCorePoolSize());
            TestChoreService.ScheduledChoreSamples.SlowChore slowChore4 = new TestChoreService.ScheduledChoreSamples.SlowChore("slowChore4", chorePeriod);
            service.scheduleChore(slowChore4);
            Thread.sleep((chorePeriod * 10));
            Assert.assertEquals("Chores are missing their start time. Should expand core pool size", service.getNumberOfScheduledChores(), service.getCorePoolSize());
            TestChoreService.ScheduledChoreSamples.SlowChore slowChore5 = new TestChoreService.ScheduledChoreSamples.SlowChore("slowChore5", chorePeriod);
            service.scheduleChore(slowChore5);
            Thread.sleep((chorePeriod * 10));
            Assert.assertEquals("Chores are missing their start time. Should expand core pool size", service.getNumberOfScheduledChores(), service.getCorePoolSize());
            Assert.assertEquals(5, service.getNumberOfChoresMissingStartTime());
            // Now we begin to cancel the chores that caused an increase in the core thread pool of the
            // ChoreService. These cancellations should cause a decrease in the core thread pool.
            cancel();
            Thread.sleep((chorePeriod * 10));
            Assert.assertEquals(Math.max(MIN_CORE_POOL_SIZE, service.getNumberOfScheduledChores()), service.getCorePoolSize());
            Assert.assertEquals(4, service.getNumberOfChoresMissingStartTime());
            cancel();
            Thread.sleep((chorePeriod * 10));
            Assert.assertEquals(Math.max(MIN_CORE_POOL_SIZE, service.getNumberOfScheduledChores()), service.getCorePoolSize());
            Assert.assertEquals(3, service.getNumberOfChoresMissingStartTime());
            cancel();
            Thread.sleep((chorePeriod * 10));
            Assert.assertEquals(Math.max(MIN_CORE_POOL_SIZE, service.getNumberOfScheduledChores()), service.getCorePoolSize());
            Assert.assertEquals(2, service.getNumberOfChoresMissingStartTime());
            cancel();
            Thread.sleep((chorePeriod * 10));
            Assert.assertEquals(Math.max(MIN_CORE_POOL_SIZE, service.getNumberOfScheduledChores()), service.getCorePoolSize());
            Assert.assertEquals(1, service.getNumberOfChoresMissingStartTime());
            cancel();
            Thread.sleep((chorePeriod * 10));
            Assert.assertEquals(Math.max(MIN_CORE_POOL_SIZE, service.getNumberOfScheduledChores()), service.getCorePoolSize());
            Assert.assertEquals(0, service.getNumberOfChoresMissingStartTime());
        } finally {
            shutdownService(service);
        }
    }

    @Test
    public void testNumberOfRunningChores() throws InterruptedException {
        ChoreService service = new ChoreService("testNumberOfRunningChores");
        final int period = 100;
        final int sleepTime = 5;
        try {
            TestChoreService.ScheduledChoreSamples.DoNothingChore dn1 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("dn1", period);
            TestChoreService.ScheduledChoreSamples.DoNothingChore dn2 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("dn2", period);
            TestChoreService.ScheduledChoreSamples.DoNothingChore dn3 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("dn3", period);
            TestChoreService.ScheduledChoreSamples.DoNothingChore dn4 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("dn4", period);
            TestChoreService.ScheduledChoreSamples.DoNothingChore dn5 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("dn5", period);
            service.scheduleChore(dn1);
            service.scheduleChore(dn2);
            service.scheduleChore(dn3);
            service.scheduleChore(dn4);
            service.scheduleChore(dn5);
            Thread.sleep(sleepTime);
            Assert.assertEquals("Scheduled chore mismatch", 5, service.getNumberOfScheduledChores());
            cancel();
            Thread.sleep(sleepTime);
            Assert.assertEquals("Scheduled chore mismatch", 4, service.getNumberOfScheduledChores());
            cancel();
            cancel();
            cancel();
            Thread.sleep(sleepTime);
            Assert.assertEquals("Scheduled chore mismatch", 1, service.getNumberOfScheduledChores());
            cancel();
            Thread.sleep(sleepTime);
            Assert.assertEquals("Scheduled chore mismatch", 0, service.getNumberOfScheduledChores());
        } finally {
            shutdownService(service);
        }
    }

    @Test
    public void testNumberOfChoresMissingStartTime() throws InterruptedException {
        ChoreService service = new ChoreService("testNumberOfChoresMissingStartTime");
        final int period = 100;
        final int sleepTime = 5 * period;
        try {
            // Slow chores sleep for a length of time LONGER than their period. Thus, SlowChores
            // ALWAYS miss their start time since their execution takes longer than their period
            TestChoreService.ScheduledChoreSamples.SlowChore sc1 = new TestChoreService.ScheduledChoreSamples.SlowChore("sc1", period);
            TestChoreService.ScheduledChoreSamples.SlowChore sc2 = new TestChoreService.ScheduledChoreSamples.SlowChore("sc2", period);
            TestChoreService.ScheduledChoreSamples.SlowChore sc3 = new TestChoreService.ScheduledChoreSamples.SlowChore("sc3", period);
            TestChoreService.ScheduledChoreSamples.SlowChore sc4 = new TestChoreService.ScheduledChoreSamples.SlowChore("sc4", period);
            TestChoreService.ScheduledChoreSamples.SlowChore sc5 = new TestChoreService.ScheduledChoreSamples.SlowChore("sc5", period);
            service.scheduleChore(sc1);
            service.scheduleChore(sc2);
            service.scheduleChore(sc3);
            service.scheduleChore(sc4);
            service.scheduleChore(sc5);
            Thread.sleep(sleepTime);
            Assert.assertEquals(5, service.getNumberOfChoresMissingStartTime());
            cancel();
            Thread.sleep(sleepTime);
            Assert.assertEquals(4, service.getNumberOfChoresMissingStartTime());
            cancel();
            cancel();
            cancel();
            Thread.sleep(sleepTime);
            Assert.assertEquals(1, service.getNumberOfChoresMissingStartTime());
            cancel();
            Thread.sleep(sleepTime);
            Assert.assertEquals(0, service.getNumberOfChoresMissingStartTime());
        } finally {
            shutdownService(service);
        }
    }

    /**
     * ChoreServices should never have a core pool size that exceeds the number of chores that have
     * been scheduled with the service. For example, if 4 ScheduledChores are scheduled with a
     * ChoreService, the number of threads in the ChoreService's core pool should never exceed 4
     */
    @Test
    public void testMaximumChoreServiceThreads() throws InterruptedException {
        ChoreService service = new ChoreService("testMaximumChoreServiceThreads");
        final int period = 100;
        final int sleepTime = 5 * period;
        try {
            // Slow chores sleep for a length of time LONGER than their period. Thus, SlowChores
            // ALWAYS miss their start time since their execution takes longer than their period.
            // Chores that miss their start time will trigger the onChoreMissedStartTime callback
            // in the ChoreService. This callback will try to increase the number of core pool
            // threads.
            TestChoreService.ScheduledChoreSamples.SlowChore sc1 = new TestChoreService.ScheduledChoreSamples.SlowChore("sc1", period);
            TestChoreService.ScheduledChoreSamples.SlowChore sc2 = new TestChoreService.ScheduledChoreSamples.SlowChore("sc2", period);
            TestChoreService.ScheduledChoreSamples.SlowChore sc3 = new TestChoreService.ScheduledChoreSamples.SlowChore("sc3", period);
            TestChoreService.ScheduledChoreSamples.SlowChore sc4 = new TestChoreService.ScheduledChoreSamples.SlowChore("sc4", period);
            TestChoreService.ScheduledChoreSamples.SlowChore sc5 = new TestChoreService.ScheduledChoreSamples.SlowChore("sc5", period);
            service.scheduleChore(sc1);
            service.scheduleChore(sc2);
            service.scheduleChore(sc3);
            service.scheduleChore(sc4);
            service.scheduleChore(sc5);
            Thread.sleep(sleepTime);
            Assert.assertTrue(((service.getCorePoolSize()) <= (service.getNumberOfScheduledChores())));
            TestChoreService.ScheduledChoreSamples.SlowChore sc6 = new TestChoreService.ScheduledChoreSamples.SlowChore("sc6", period);
            TestChoreService.ScheduledChoreSamples.SlowChore sc7 = new TestChoreService.ScheduledChoreSamples.SlowChore("sc7", period);
            TestChoreService.ScheduledChoreSamples.SlowChore sc8 = new TestChoreService.ScheduledChoreSamples.SlowChore("sc8", period);
            TestChoreService.ScheduledChoreSamples.SlowChore sc9 = new TestChoreService.ScheduledChoreSamples.SlowChore("sc9", period);
            TestChoreService.ScheduledChoreSamples.SlowChore sc10 = new TestChoreService.ScheduledChoreSamples.SlowChore("sc10", period);
            service.scheduleChore(sc6);
            service.scheduleChore(sc7);
            service.scheduleChore(sc8);
            service.scheduleChore(sc9);
            service.scheduleChore(sc10);
            Thread.sleep(sleepTime);
            Assert.assertTrue(((service.getCorePoolSize()) <= (service.getNumberOfScheduledChores())));
        } finally {
            shutdownService(service);
        }
    }

    @Test
    public void testChangingChoreServices() throws InterruptedException {
        final int period = 100;
        final int sleepTime = 10;
        ChoreService service1 = new ChoreService("testChangingChoreServices_1");
        ChoreService service2 = new ChoreService("testChangingChoreServices_2");
        ScheduledChore chore = new TestChoreService.ScheduledChoreSamples.DoNothingChore("sample", period);
        try {
            Assert.assertFalse(chore.isScheduled());
            Assert.assertFalse(service1.isChoreScheduled(chore));
            Assert.assertFalse(service2.isChoreScheduled(chore));
            Assert.assertTrue(((chore.getChoreServicer()) == null));
            service1.scheduleChore(chore);
            Thread.sleep(sleepTime);
            Assert.assertTrue(chore.isScheduled());
            Assert.assertTrue(service1.isChoreScheduled(chore));
            Assert.assertFalse(service2.isChoreScheduled(chore));
            Assert.assertFalse(((chore.getChoreServicer()) == null));
            service2.scheduleChore(chore);
            Thread.sleep(sleepTime);
            Assert.assertTrue(chore.isScheduled());
            Assert.assertFalse(service1.isChoreScheduled(chore));
            Assert.assertTrue(service2.isChoreScheduled(chore));
            Assert.assertFalse(((chore.getChoreServicer()) == null));
            chore.cancel();
            Assert.assertFalse(chore.isScheduled());
            Assert.assertFalse(service1.isChoreScheduled(chore));
            Assert.assertFalse(service2.isChoreScheduled(chore));
            Assert.assertTrue(((chore.getChoreServicer()) == null));
        } finally {
            shutdownService(service1);
            shutdownService(service2);
        }
    }

    @Test
    public void testStopperForScheduledChores() throws InterruptedException {
        ChoreService service = new ChoreService("testStopperForScheduledChores");
        Stoppable stopperForGroup1 = new TestChoreService.ScheduledChoreSamples.SampleStopper();
        Stoppable stopperForGroup2 = new TestChoreService.ScheduledChoreSamples.SampleStopper();
        final int period = 100;
        final int delta = 10;
        try {
            ScheduledChore chore1_group1 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("c1g1", stopperForGroup1, period);
            ScheduledChore chore2_group1 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("c2g1", stopperForGroup1, period);
            ScheduledChore chore3_group1 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("c3g1", stopperForGroup1, period);
            ScheduledChore chore1_group2 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("c1g2", stopperForGroup2, period);
            ScheduledChore chore2_group2 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("c2g2", stopperForGroup2, period);
            ScheduledChore chore3_group2 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("c3g2", stopperForGroup2, period);
            service.scheduleChore(chore1_group1);
            service.scheduleChore(chore2_group1);
            service.scheduleChore(chore3_group1);
            service.scheduleChore(chore1_group2);
            service.scheduleChore(chore2_group2);
            service.scheduleChore(chore3_group2);
            Thread.sleep(delta);
            Thread.sleep((10 * period));
            Assert.assertTrue(chore1_group1.isScheduled());
            Assert.assertTrue(chore2_group1.isScheduled());
            Assert.assertTrue(chore3_group1.isScheduled());
            Assert.assertTrue(chore1_group2.isScheduled());
            Assert.assertTrue(chore2_group2.isScheduled());
            Assert.assertTrue(chore3_group2.isScheduled());
            stopperForGroup1.stop("test stopping group 1");
            Thread.sleep(period);
            Assert.assertFalse(chore1_group1.isScheduled());
            Assert.assertFalse(chore2_group1.isScheduled());
            Assert.assertFalse(chore3_group1.isScheduled());
            Assert.assertTrue(chore1_group2.isScheduled());
            Assert.assertTrue(chore2_group2.isScheduled());
            Assert.assertTrue(chore3_group2.isScheduled());
            stopperForGroup2.stop("test stopping group 2");
            Thread.sleep(period);
            Assert.assertFalse(chore1_group1.isScheduled());
            Assert.assertFalse(chore2_group1.isScheduled());
            Assert.assertFalse(chore3_group1.isScheduled());
            Assert.assertFalse(chore1_group2.isScheduled());
            Assert.assertFalse(chore2_group2.isScheduled());
            Assert.assertFalse(chore3_group2.isScheduled());
        } finally {
            shutdownService(service);
        }
    }

    @Test
    public void testShutdownCancelsScheduledChores() throws InterruptedException {
        final int period = 100;
        ChoreService service = new ChoreService("testShutdownCancelsScheduledChores");
        ScheduledChore successChore1 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("sc1", period);
        ScheduledChore successChore2 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("sc2", period);
        ScheduledChore successChore3 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("sc3", period);
        try {
            Assert.assertTrue(service.scheduleChore(successChore1));
            Assert.assertTrue(successChore1.isScheduled());
            Assert.assertTrue(service.scheduleChore(successChore2));
            Assert.assertTrue(successChore2.isScheduled());
            Assert.assertTrue(service.scheduleChore(successChore3));
            Assert.assertTrue(successChore3.isScheduled());
        } finally {
            shutdownService(service);
        }
        Assert.assertFalse(successChore1.isScheduled());
        Assert.assertFalse(successChore2.isScheduled());
        Assert.assertFalse(successChore3.isScheduled());
    }

    @Test
    public void testShutdownWorksWhileChoresAreExecuting() throws InterruptedException {
        final int period = 100;
        final int sleep = 5 * period;
        ChoreService service = new ChoreService("testShutdownWorksWhileChoresAreExecuting");
        ScheduledChore slowChore1 = new TestChoreService.ScheduledChoreSamples.SleepingChore("sc1", period, sleep);
        ScheduledChore slowChore2 = new TestChoreService.ScheduledChoreSamples.SleepingChore("sc2", period, sleep);
        ScheduledChore slowChore3 = new TestChoreService.ScheduledChoreSamples.SleepingChore("sc3", period, sleep);
        try {
            Assert.assertTrue(service.scheduleChore(slowChore1));
            Assert.assertTrue(service.scheduleChore(slowChore2));
            Assert.assertTrue(service.scheduleChore(slowChore3));
            Thread.sleep((sleep / 2));
            shutdownService(service);
            Assert.assertFalse(slowChore1.isScheduled());
            Assert.assertFalse(slowChore2.isScheduled());
            Assert.assertFalse(slowChore3.isScheduled());
            Assert.assertTrue(service.isShutdown());
            Thread.sleep(5);
            Assert.assertTrue(service.isTerminated());
        } finally {
            shutdownService(service);
        }
    }

    @Test
    public void testShutdownRejectsNewSchedules() throws InterruptedException {
        final int period = 100;
        ChoreService service = new ChoreService("testShutdownRejectsNewSchedules");
        ScheduledChore successChore1 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("sc1", period);
        ScheduledChore successChore2 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("sc2", period);
        ScheduledChore successChore3 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("sc3", period);
        ScheduledChore failChore1 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("fc1", period);
        ScheduledChore failChore2 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("fc2", period);
        ScheduledChore failChore3 = new TestChoreService.ScheduledChoreSamples.DoNothingChore("fc3", period);
        try {
            Assert.assertTrue(service.scheduleChore(successChore1));
            Assert.assertTrue(successChore1.isScheduled());
            Assert.assertTrue(service.scheduleChore(successChore2));
            Assert.assertTrue(successChore2.isScheduled());
            Assert.assertTrue(service.scheduleChore(successChore3));
            Assert.assertTrue(successChore3.isScheduled());
        } finally {
            shutdownService(service);
        }
        Assert.assertFalse(service.scheduleChore(failChore1));
        Assert.assertFalse(failChore1.isScheduled());
        Assert.assertFalse(service.scheduleChore(failChore2));
        Assert.assertFalse(failChore2.isScheduled());
        Assert.assertFalse(service.scheduleChore(failChore3));
        Assert.assertFalse(failChore3.isScheduled());
    }
}

