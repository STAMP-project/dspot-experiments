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
package org.apache.flink.runtime.resourcemanager;


import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.concurrent.ScheduledExecutor;
import org.apache.flink.runtime.highavailability.TestingHighAvailabilityServices;
import org.apache.flink.runtime.jobmaster.JobMasterId;
import org.apache.flink.runtime.leaderretrieval.SettableLeaderRetrievalService;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class JobLeaderIdServiceTest extends TestLogger {
    /**
     * Tests adding a job and finding out its leader id
     */
    @Test(timeout = 10000)
    public void testAddingJob() throws Exception {
        final JobID jobId = new JobID();
        final String address = "foobar";
        final JobMasterId leaderId = JobMasterId.generate();
        TestingHighAvailabilityServices highAvailabilityServices = new TestingHighAvailabilityServices();
        SettableLeaderRetrievalService leaderRetrievalService = new SettableLeaderRetrievalService(null, null);
        highAvailabilityServices.setJobMasterLeaderRetriever(jobId, leaderRetrievalService);
        ScheduledExecutor scheduledExecutor = Mockito.mock(ScheduledExecutor.class);
        Time timeout = Time.milliseconds(5000L);
        JobLeaderIdActions jobLeaderIdActions = Mockito.mock(JobLeaderIdActions.class);
        JobLeaderIdService jobLeaderIdService = new JobLeaderIdService(highAvailabilityServices, scheduledExecutor, timeout);
        jobLeaderIdService.start(jobLeaderIdActions);
        jobLeaderIdService.addJob(jobId);
        CompletableFuture<JobMasterId> leaderIdFuture = jobLeaderIdService.getLeaderId(jobId);
        // notify the leader id service about the new leader
        leaderRetrievalService.notifyListener(address, leaderId.toUUID());
        Assert.assertEquals(leaderId, leaderIdFuture.get());
        Assert.assertTrue(jobLeaderIdService.containsJob(jobId));
    }

    /**
     * Tests that removing a job completes the job leader id future exceptionally
     */
    @Test(timeout = 10000)
    public void testRemovingJob() throws Exception {
        final JobID jobId = new JobID();
        TestingHighAvailabilityServices highAvailabilityServices = new TestingHighAvailabilityServices();
        SettableLeaderRetrievalService leaderRetrievalService = new SettableLeaderRetrievalService(null, null);
        highAvailabilityServices.setJobMasterLeaderRetriever(jobId, leaderRetrievalService);
        ScheduledExecutor scheduledExecutor = Mockito.mock(ScheduledExecutor.class);
        Time timeout = Time.milliseconds(5000L);
        JobLeaderIdActions jobLeaderIdActions = Mockito.mock(JobLeaderIdActions.class);
        JobLeaderIdService jobLeaderIdService = new JobLeaderIdService(highAvailabilityServices, scheduledExecutor, timeout);
        jobLeaderIdService.start(jobLeaderIdActions);
        jobLeaderIdService.addJob(jobId);
        CompletableFuture<JobMasterId> leaderIdFuture = jobLeaderIdService.getLeaderId(jobId);
        // remove the job before we could find a leader
        jobLeaderIdService.removeJob(jobId);
        Assert.assertFalse(jobLeaderIdService.containsJob(jobId));
        try {
            leaderIdFuture.get();
            Assert.fail("The leader id future should be completed exceptionally.");
        } catch (ExecutionException ignored) {
            // expected exception
        }
    }

    /**
     * Tests that the initial job registration registers a timeout which will call
     * {@link JobLeaderIdActions#notifyJobTimeout(JobID, UUID)} when executed.
     */
    @Test
    public void testInitialJobTimeout() throws Exception {
        final JobID jobId = new JobID();
        TestingHighAvailabilityServices highAvailabilityServices = new TestingHighAvailabilityServices();
        SettableLeaderRetrievalService leaderRetrievalService = new SettableLeaderRetrievalService(null, null);
        highAvailabilityServices.setJobMasterLeaderRetriever(jobId, leaderRetrievalService);
        ScheduledExecutor scheduledExecutor = Mockito.mock(ScheduledExecutor.class);
        Time timeout = Time.milliseconds(5000L);
        JobLeaderIdActions jobLeaderIdActions = Mockito.mock(JobLeaderIdActions.class);
        JobLeaderIdService jobLeaderIdService = new JobLeaderIdService(highAvailabilityServices, scheduledExecutor, timeout);
        jobLeaderIdService.start(jobLeaderIdActions);
        jobLeaderIdService.addJob(jobId);
        Assert.assertTrue(jobLeaderIdService.containsJob(jobId));
        ArgumentCaptor<Runnable> runnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
        Mockito.verify(scheduledExecutor).schedule(runnableArgumentCaptor.capture(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        Runnable timeoutRunnable = runnableArgumentCaptor.getValue();
        timeoutRunnable.run();
        ArgumentCaptor<UUID> timeoutIdArgumentCaptor = ArgumentCaptor.forClass(UUID.class);
        Mockito.verify(jobLeaderIdActions, Mockito.times(1)).notifyJobTimeout(ArgumentMatchers.eq(jobId), timeoutIdArgumentCaptor.capture());
        Assert.assertTrue(jobLeaderIdService.isValidTimeout(jobId, timeoutIdArgumentCaptor.getValue()));
    }

    /**
     * Tests that a timeout get cancelled once a job leader has been found. Furthermore, it tests
     * that a new timeout is registered after the jobmanager has lost leadership.
     */
    @Test(timeout = 10000)
    public void jobTimeoutAfterLostLeadership() throws Exception {
        final JobID jobId = new JobID();
        final String address = "foobar";
        final JobMasterId leaderId = JobMasterId.generate();
        TestingHighAvailabilityServices highAvailabilityServices = new TestingHighAvailabilityServices();
        SettableLeaderRetrievalService leaderRetrievalService = new SettableLeaderRetrievalService(null, null);
        highAvailabilityServices.setJobMasterLeaderRetriever(jobId, leaderRetrievalService);
        ScheduledFuture<?> timeout1 = Mockito.mock(ScheduledFuture.class);
        ScheduledFuture<?> timeout2 = Mockito.mock(ScheduledFuture.class);
        final Queue<ScheduledFuture<?>> timeoutQueue = new ArrayDeque<>(Arrays.asList(timeout1, timeout2));
        ScheduledExecutor scheduledExecutor = Mockito.mock(ScheduledExecutor.class);
        final AtomicReference<Runnable> lastRunnable = new AtomicReference<>();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                lastRunnable.set(((Runnable) (invocation.getArguments()[0])));
                return timeoutQueue.poll();
            }
        }).when(scheduledExecutor).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        Time timeout = Time.milliseconds(5000L);
        JobLeaderIdActions jobLeaderIdActions = Mockito.mock(JobLeaderIdActions.class);
        final AtomicReference<UUID> lastTimeoutId = new AtomicReference<>();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                lastTimeoutId.set(((UUID) (invocation.getArguments()[1])));
                return null;
            }
        }).when(jobLeaderIdActions).notifyJobTimeout(ArgumentMatchers.eq(jobId), ArgumentMatchers.any(UUID.class));
        JobLeaderIdService jobLeaderIdService = new JobLeaderIdService(highAvailabilityServices, scheduledExecutor, timeout);
        jobLeaderIdService.start(jobLeaderIdActions);
        jobLeaderIdService.addJob(jobId);
        CompletableFuture<JobMasterId> leaderIdFuture = jobLeaderIdService.getLeaderId(jobId);
        // notify the leader id service about the new leader
        leaderRetrievalService.notifyListener(address, leaderId.toUUID());
        Assert.assertEquals(leaderId, leaderIdFuture.get());
        Assert.assertTrue(jobLeaderIdService.containsJob(jobId));
        // check that the first timeout got cancelled
        Mockito.verify(timeout1, Mockito.times(1)).cancel(ArgumentMatchers.anyBoolean());
        Mockito.verify(scheduledExecutor, Mockito.times(1)).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        // initial timeout runnable which should no longer have an effect
        Runnable runnable = lastRunnable.get();
        Assert.assertNotNull(runnable);
        runnable.run();
        Mockito.verify(jobLeaderIdActions, Mockito.times(1)).notifyJobTimeout(ArgumentMatchers.eq(jobId), ArgumentMatchers.any(UUID.class));
        // the timeout should no longer be valid
        Assert.assertFalse(jobLeaderIdService.isValidTimeout(jobId, lastTimeoutId.get()));
        // lose leadership
        leaderRetrievalService.notifyListener("", null);
        Mockito.verify(scheduledExecutor, Mockito.times(2)).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
        // the second runnable should be the new timeout
        runnable = lastRunnable.get();
        Assert.assertNotNull(runnable);
        runnable.run();
        Mockito.verify(jobLeaderIdActions, Mockito.times(2)).notifyJobTimeout(ArgumentMatchers.eq(jobId), ArgumentMatchers.any(UUID.class));
        // the new timeout should be valid
        Assert.assertTrue(jobLeaderIdService.isValidTimeout(jobId, lastTimeoutId.get()));
    }
}

