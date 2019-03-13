/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.drools.core.time.impl;


import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.drools.core.time.Job;
import org.drools.core.time.JobContext;
import org.drools.core.time.JobHandle;
import org.drools.core.time.Trigger;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PseudoClockSchedulerTest {
    private Job mockJob_1 = Mockito.mock(Job.class, "mockJob_1");

    private JobContext mockContext_1 = Mockito.mock(JobContext.class, "mockContext_1");

    private Trigger mockTrigger_1 = Mockito.mock(Trigger.class, "mockTrigger_1");

    private Job mockJob_2 = Mockito.mock(Job.class, "mockJob_2");

    private JobContext mockContext_2 = Mockito.mock(JobContext.class, "mockContext_2");

    private Trigger mockTrigger_2 = Mockito.mock(Trigger.class, "mockTrigger_2");

    private PseudoClockScheduler scheduler = new PseudoClockScheduler();

    @Test
    public void removeExistingJob() {
        final Date triggerTime = new Date(1000);
        Mockito.when(mockTrigger_1.hasNextFireTime()).thenReturn(triggerTime);
        JobHandle jobHandle = scheduler.scheduleJob(mockJob_1, this.mockContext_1, mockTrigger_1);
        Assert.assertThat(scheduler.getTimeToNextJob(), Is.is(triggerTime.getTime()));
        scheduler.removeJob(jobHandle);
        Assert.assertThat(scheduler.getTimeToNextJob(), Is.is((-1L)));
        Mockito.verify(mockTrigger_1, Mockito.atLeastOnce()).hasNextFireTime();
    }

    @Test
    public void removeExistingJobWhenMultipleQueued() {
        final Date triggerTime_1 = new Date(1000);
        final Date triggerTime_2 = new Date(2000);
        Mockito.when(mockTrigger_1.hasNextFireTime()).thenReturn(triggerTime_1);
        Mockito.when(mockTrigger_2.hasNextFireTime()).thenReturn(triggerTime_2);
        JobHandle jobHandle_1 = scheduler.scheduleJob(mockJob_1, this.mockContext_1, mockTrigger_1);
        JobHandle jobHandle_2 = scheduler.scheduleJob(mockJob_2, this.mockContext_2, mockTrigger_2);
        Assert.assertThat(scheduler.getTimeToNextJob(), Is.is(triggerTime_1.getTime()));
        scheduler.removeJob(jobHandle_1);
        Assert.assertThat(scheduler.getTimeToNextJob(), Is.is(triggerTime_2.getTime()));
        scheduler.removeJob(jobHandle_2);
        Assert.assertThat(scheduler.getTimeToNextJob(), Is.is((-1L)));
        Mockito.verify(mockTrigger_1, Mockito.atLeastOnce()).hasNextFireTime();
        Mockito.verify(mockTrigger_2, Mockito.atLeastOnce()).hasNextFireTime();
    }

    @Test
    public void timerIsSetToJobTriggerTimeForExecution() {
        final Date triggerTime = new Date(1000);
        Mockito.when(mockTrigger_1.hasNextFireTime()).thenReturn(triggerTime, triggerTime, triggerTime, null);
        Mockito.when(mockTrigger_1.nextFireTime()).thenReturn(triggerTime);
        Job job = new Job() {
            public void execute(JobContext ctx) {
                // Even though the clock has been advanced to 5000, the job should run
                // with the time set its trigger time.
                Assert.assertThat(scheduler.getCurrentTime(), Is.is(1000L));
            }
        };
        scheduler.scheduleJob(job, this.mockContext_1, mockTrigger_1);
        scheduler.advanceTime(5000, TimeUnit.MILLISECONDS);
        // Now, after the job has been executed the time should be what it was advanced to
        Assert.assertThat(scheduler.getCurrentTime(), Is.is(5000L));
        Mockito.verify(mockTrigger_1, Mockito.atLeast(2)).hasNextFireTime();
        Mockito.verify(mockTrigger_1, Mockito.times(1)).nextFireTime();
    }

    @Test
    public void timerIsResetWhenJobThrowsExceptions() {
        final Date triggerTime = new Date(1000);
        Mockito.when(mockTrigger_1.hasNextFireTime()).thenReturn(triggerTime, triggerTime, triggerTime, null);
        Mockito.when(mockTrigger_1.nextFireTime()).thenReturn(triggerTime);
        Job job = new Job() {
            public void execute(JobContext ctx) {
                Assert.assertThat(scheduler.getCurrentTime(), Is.is(1000L));
                throw new RuntimeException("for test");
            }
        };
        scheduler.scheduleJob(job, this.mockContext_1, mockTrigger_1);
        scheduler.advanceTime(5000, TimeUnit.MILLISECONDS);
        // The time must be advanced correctly even when the job throws an exception
        Assert.assertThat(scheduler.getCurrentTime(), Is.is(5000L));
        Mockito.verify(mockTrigger_1, Mockito.atLeast(2)).hasNextFireTime();
        Mockito.verify(mockTrigger_1, Mockito.times(1)).nextFireTime();
    }
}

