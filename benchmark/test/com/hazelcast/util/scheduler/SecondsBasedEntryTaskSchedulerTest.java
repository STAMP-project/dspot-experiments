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
package com.hazelcast.util.scheduler;


import com.hazelcast.spi.TaskScheduler;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Has timing sensitive tests, do not make a {@link com.hazelcast.test.annotation.ParallelTest}.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class SecondsBasedEntryTaskSchedulerTest {
    @Mock
    private TaskScheduler taskScheduler = Mockito.mock(TaskScheduler.class);

    @Mock
    @SuppressWarnings("unchecked")
    private ScheduledEntryProcessor<Integer, Integer> entryProcessor = Mockito.mock(ScheduledEntryProcessor.class);

    private SecondsBasedEntryTaskScheduler<Integer, Integer> scheduler;

    @Test
    public void test_scheduleEntry_postpone() {
        scheduler = new SecondsBasedEntryTaskScheduler<Integer, Integer>(taskScheduler, entryProcessor, ScheduleType.POSTPONE);
        Assert.assertTrue(scheduler.schedule(100, 1, 1));
        Assert.assertNotNull(scheduler.get(1));
        Assert.assertEquals(1, scheduler.size());
    }

    @Test
    public void test_rescheduleEntry_postpone() {
        scheduler = new SecondsBasedEntryTaskScheduler<Integer, Integer>(taskScheduler, entryProcessor, ScheduleType.POSTPONE);
        Assert.assertTrue(scheduler.schedule(100, 1, 1));
        Assert.assertTrue(scheduler.schedule(10000, 1, 1));
        Assert.assertNotNull(scheduler.get(1));
        Assert.assertEquals(1, scheduler.size());
    }

    @Test(timeout = 10000)
    public void test_doNotRescheduleEntryWithinSameSecond_postpone() {
        scheduler = new SecondsBasedEntryTaskScheduler<Integer, Integer>(taskScheduler, entryProcessor, ScheduleType.POSTPONE);
        final int delayMillis = 0;
        final int key = 1;
        int startSecond;
        boolean firstResult;
        boolean secondResult;
        int stopSecond = 0;
        do {
            // startSecond must be greater than stopSecond on each trial round
            // to guarantee the first schedule attempt to be successful.
            while ((startSecond = SecondsBasedEntryTaskScheduler.findRelativeSecond(delayMillis)) == stopSecond) {
                HazelcastTestSupport.sleepMillis(1);
            } 
            // we can just assert the second result if the relative second is still the same,
            // otherwise we may create a false negative failure since the second could have passed after the schedule() call.
            firstResult = scheduler.schedule(delayMillis, key, 1);
            secondResult = scheduler.schedule(delayMillis, key, 1);
            stopSecond = SecondsBasedEntryTaskScheduler.findRelativeSecond(delayMillis);
        } while (startSecond != stopSecond );
        Assert.assertTrue("First schedule() call should always be successful", firstResult);
        Assert.assertFalse("Second schedule() call should not be successful within the same second", secondResult);
        Assert.assertNotNull(scheduler.get(key));
        Assert.assertEquals(1, scheduler.size());
    }

    @Test
    public void test_cancelEntry_postpone() {
        scheduler = new SecondsBasedEntryTaskScheduler<Integer, Integer>(taskScheduler, entryProcessor, ScheduleType.POSTPONE);
        Assert.assertTrue(scheduler.schedule(100, 1, 1));
        Assert.assertEquals(1, scheduler.size());
        Assert.assertNotNull(scheduler.cancel(1));
        Assert.assertEquals(0, scheduler.size());
    }

    @Test
    public void test_scheduleEntry_foreach() {
        scheduler = new SecondsBasedEntryTaskScheduler<Integer, Integer>(taskScheduler, entryProcessor, ScheduleType.FOR_EACH);
        Assert.assertTrue(scheduler.schedule(100, 1, 1));
        Assert.assertNotNull(scheduler.get(1));
        Assert.assertEquals(1, scheduler.size());
    }

    @Test
    public void test_scheduleEntryMultipleTimes_foreach() {
        scheduler = new SecondsBasedEntryTaskScheduler<Integer, Integer>(taskScheduler, entryProcessor, ScheduleType.FOR_EACH);
        Assert.assertTrue(scheduler.schedule(100, 1, 1));
        Assert.assertTrue(scheduler.schedule(100, 1, 1));
        Assert.assertNotNull(scheduler.get(1));
        Assert.assertEquals(2, scheduler.size());
    }

    @Test
    public void test_cancelIfExists_postpone() {
        scheduler = new SecondsBasedEntryTaskScheduler<Integer, Integer>(taskScheduler, entryProcessor, ScheduleType.POSTPONE);
        Assert.assertTrue(scheduler.schedule(100, 1, 1));
        Assert.assertEquals(1, scheduler.cancelIfExists(1, 1));
    }

    @Test
    public void test_cancelIfExists_foreach() {
        scheduler = new SecondsBasedEntryTaskScheduler<Integer, Integer>(taskScheduler, entryProcessor, ScheduleType.FOR_EACH);
        Assert.assertTrue(scheduler.schedule(100, 1, 1));
        Assert.assertEquals(1, scheduler.cancelIfExists(1, 1));
    }

    @Test
    public void test_cancelIfExistsWithInvalidValue_foreach() {
        scheduler = new SecondsBasedEntryTaskScheduler<Integer, Integer>(taskScheduler, entryProcessor, ScheduleType.FOR_EACH);
        Assert.assertTrue(scheduler.schedule(100, 1, 1));
        Assert.assertEquals(0, scheduler.cancelIfExists(1, 0));
    }

    @Test
    public void test_cancelIfExistsMultiple_foreach() {
        scheduler = new SecondsBasedEntryTaskScheduler<Integer, Integer>(taskScheduler, entryProcessor, ScheduleType.FOR_EACH);
        Assert.assertTrue(scheduler.schedule(100, 1, 1));
        Assert.assertTrue(scheduler.schedule(100, 1, 2));
        Assert.assertEquals(1, scheduler.cancelIfExists(1, 1));
    }

    @Test
    public void test_cancelAll() {
        scheduler = new SecondsBasedEntryTaskScheduler<Integer, Integer>(taskScheduler, entryProcessor, ScheduleType.FOR_EACH);
        Assert.assertTrue(scheduler.schedule(100, 1, 1));
        Assert.assertTrue(scheduler.schedule(100, 1, 2));
        scheduler.cancelAll();
        Assert.assertEquals(0, scheduler.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_executeScheduledEntry() {
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.when(taskScheduler.schedule(runnableCaptor.capture(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenReturn(Mockito.mock(ScheduledFuture.class));
        scheduler = new SecondsBasedEntryTaskScheduler<Integer, Integer>(taskScheduler, entryProcessor, ScheduleType.FOR_EACH);
        Assert.assertTrue(scheduler.schedule(100, 1, 1));
        Assert.assertEquals(1, scheduler.size());
        Runnable runnable = runnableCaptor.getValue();
        Assert.assertNotNull(runnable);
        runnable.run();
        Assert.assertEquals(0, scheduler.size());
    }

    @Test
    public void test_toString() {
        scheduler = new SecondsBasedEntryTaskScheduler<Integer, Integer>(taskScheduler, entryProcessor, ScheduleType.FOR_EACH);
        Assert.assertNotNull(scheduler.toString());
    }
}

