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
package org.apache.beam.runners.dataflow;


import Dataflow.Projects;
import Dataflow.Projects.Locations;
import Dataflow.Projects.Locations.Jobs;
import Dataflow.Projects.Locations.Jobs.Get;
import Dataflow.Projects.Locations.Jobs.Update;
import DataflowPipelineJob.MESSAGES_POLLING_INTERVAL;
import DataflowPipelineJob.MESSAGES_POLLING_RETRIES;
import DataflowPipelineJob.STATUS_BACKOFF_FACTORY;
import DataflowPipelineJob.STATUS_POLLING_INTERVAL;
import DataflowPipelineJob.STATUS_POLLING_RETRIES;
import MonitoringUtil.JobMessagesHandler;
import NanoClock.SYSTEM;
import State.CANCELLED;
import State.DONE;
import State.FAILED;
import State.RUNNING;
import State.UNKNOWN;
import State.UPDATED;
import com.google.api.client.util.NanoClock;
import com.google.api.client.util.Sleeper;
import com.google.api.services.dataflow.Dataflow;
import com.google.api.services.dataflow.Dataflow.Projects.Locations.Jobs.Messages;
import com.google.api.services.dataflow.model.Job;
import com.google.api.services.dataflow.model.JobMessage;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.NavigableMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.apache.beam.runners.dataflow.util.MonitoringUtil;
import org.apache.beam.sdk.PipelineResult.State;
import org.apache.beam.sdk.testing.ExpectedLogs;
import org.apache.beam.sdk.util.BackOffAdapter;
import org.apache.beam.sdk.util.FastNanoClockAndSleeper;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Maps;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for DataflowPipelineJob.
 */
@RunWith(JUnit4.class)
public class DataflowPipelineJobTest {
    private static final String PROJECT_ID = "some-project";

    private static final String REGION_ID = "some-region-2b";

    private static final String JOB_ID = "1234";

    private static final String REPLACEMENT_JOB_ID = "4321";

    @Mock
    private DataflowClient mockDataflowClient;

    @Mock
    private Dataflow mockWorkflowClient;

    @Mock
    private Projects mockProjects;

    @Mock
    private Locations mockLocations;

    @Mock
    private Jobs mockJobs;

    @Mock
    private JobMessagesHandler mockHandler;

    @Rule
    public FastNanoClockAndSleeper fastClock = new FastNanoClockAndSleeper();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public ExpectedLogs expectedLogs = ExpectedLogs.none(DataflowPipelineJob.class);

    private TestDataflowPipelineOptions options;

    @Test
    public void testWaitToFinishMessagesFail() throws Exception {
        Dataflow.Projects.Locations.Jobs.Get statusRequest = Mockito.mock(Get.class);
        Job statusResponse = new Job();
        statusResponse.setCurrentState(("JOB_STATE_" + (DONE.name())));
        Mockito.when(mockJobs.get(ArgumentMatchers.eq(DataflowPipelineJobTest.PROJECT_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.REGION_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.JOB_ID))).thenReturn(statusRequest);
        Mockito.when(statusRequest.execute()).thenReturn(statusResponse);
        MonitoringUtil.JobMessagesHandler jobHandler = Mockito.mock(JobMessagesHandler.class);
        Dataflow.Projects.Locations.Jobs.Messages mockMessages = Mockito.mock(Messages.class);
        Messages.List listRequest = Mockito.mock(List.class);
        Mockito.when(mockJobs.messages()).thenReturn(mockMessages);
        Mockito.when(mockMessages.list(ArgumentMatchers.eq(DataflowPipelineJobTest.PROJECT_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.REGION_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.JOB_ID))).thenReturn(listRequest);
        Mockito.when(listRequest.setPageToken(ArgumentMatchers.eq(((String) (null))))).thenReturn(listRequest);
        Mockito.when(listRequest.execute()).thenThrow(SocketTimeoutException.class);
        DataflowPipelineJob job = new DataflowPipelineJob(DataflowClient.create(options), DataflowPipelineJobTest.JOB_ID, options, ImmutableMap.of());
        State state = job.waitUntilFinish(Duration.standardMinutes(5), jobHandler, fastClock, fastClock);
        Assert.assertEquals(null, state);
    }

    /**
     * Tests that the {@link DataflowPipelineJob} understands that the {@link State#DONE DONE} state
     * is terminal.
     */
    @Test
    public void testWaitToFinishDone() throws Exception {
        Assert.assertEquals(DONE, mockWaitToFinishInState(DONE));
        expectedLogs.verifyInfo(String.format("Job %s finished with status DONE.", DataflowPipelineJobTest.JOB_ID));
    }

    /**
     * Tests that the {@link DataflowPipelineJob} understands that the {@link State#FAILED FAILED}
     * state is terminal.
     */
    @Test
    public void testWaitToFinishFailed() throws Exception {
        Assert.assertEquals(FAILED, mockWaitToFinishInState(FAILED));
        expectedLogs.verifyInfo(String.format("Job %s failed with status FAILED.", DataflowPipelineJobTest.JOB_ID));
    }

    /**
     * Tests that the {@link DataflowPipelineJob} understands that the {@link State#CANCELLED
     * CANCELLED} state is terminal.
     */
    @Test
    public void testWaitToFinishCancelled() throws Exception {
        Assert.assertEquals(CANCELLED, mockWaitToFinishInState(CANCELLED));
        expectedLogs.verifyInfo(String.format("Job %s finished with status CANCELLED", DataflowPipelineJobTest.JOB_ID));
    }

    /**
     * Tests that the {@link DataflowPipelineJob} understands that the {@link State#UPDATED UPDATED}
     * state is terminal.
     */
    @Test
    public void testWaitToFinishUpdated() throws Exception {
        Assert.assertEquals(UPDATED, mockWaitToFinishInState(UPDATED));
        expectedLogs.verifyInfo(String.format("Job %s has been updated and is running as the new job with id %s.", DataflowPipelineJobTest.JOB_ID, DataflowPipelineJobTest.REPLACEMENT_JOB_ID));
    }

    /**
     * Tests that the {@link DataflowPipelineJob} understands that the {@link State#UNKNOWN UNKNOWN}
     * state is terminal.
     */
    @Test
    public void testWaitToFinishUnknown() throws Exception {
        Assert.assertEquals(null, mockWaitToFinishInState(UNKNOWN));
        expectedLogs.verifyWarn("No terminal state was returned. State value UNKNOWN");
    }

    @Test
    public void testWaitToFinishFail() throws Exception {
        Dataflow.Projects.Locations.Jobs.Get statusRequest = Mockito.mock(Get.class);
        Mockito.when(mockJobs.get(ArgumentMatchers.eq(DataflowPipelineJobTest.PROJECT_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.REGION_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.JOB_ID))).thenReturn(statusRequest);
        Mockito.when(statusRequest.execute()).thenThrow(IOException.class);
        DataflowPipelineJob job = new DataflowPipelineJob(DataflowClient.create(options), DataflowPipelineJobTest.JOB_ID, options, ImmutableMap.of());
        long startTime = fastClock.nanoTime();
        State state = job.waitUntilFinish(Duration.standardMinutes(5), null, fastClock, fastClock);
        Assert.assertEquals(null, state);
        long timeDiff = TimeUnit.NANOSECONDS.toMillis(((fastClock.nanoTime()) - startTime));
        checkValidInterval(MESSAGES_POLLING_INTERVAL, MESSAGES_POLLING_RETRIES, timeDiff);
    }

    @Test
    public void testWaitToFinishTimeFail() throws Exception {
        Dataflow.Projects.Locations.Jobs.Get statusRequest = Mockito.mock(Get.class);
        Mockito.when(mockJobs.get(ArgumentMatchers.eq(DataflowPipelineJobTest.PROJECT_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.REGION_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.JOB_ID))).thenReturn(statusRequest);
        Mockito.when(statusRequest.execute()).thenThrow(IOException.class);
        DataflowPipelineJob job = new DataflowPipelineJob(DataflowClient.create(options), DataflowPipelineJobTest.JOB_ID, options, ImmutableMap.of());
        long startTime = fastClock.nanoTime();
        State state = job.waitUntilFinish(Duration.millis(4), null, fastClock, fastClock);
        Assert.assertEquals(null, state);
        long timeDiff = TimeUnit.NANOSECONDS.toMillis(((fastClock.nanoTime()) - startTime));
        // Should only have slept for the 4 ms allowed.
        Assert.assertEquals(4L, timeDiff);
    }

    @Test
    public void testCumulativeTimeOverflow() throws Exception {
        Dataflow.Projects.Locations.Jobs.Get statusRequest = Mockito.mock(Get.class);
        Job statusResponse = new Job();
        statusResponse.setCurrentState("JOB_STATE_RUNNING");
        Mockito.when(mockJobs.get(ArgumentMatchers.eq(DataflowPipelineJobTest.PROJECT_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.REGION_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.JOB_ID))).thenReturn(statusRequest);
        Mockito.when(statusRequest.execute()).thenReturn(statusResponse);
        DataflowPipelineJobTest.FastNanoClockAndFuzzySleeper clock = new DataflowPipelineJobTest.FastNanoClockAndFuzzySleeper();
        DataflowPipelineJob job = new DataflowPipelineJob(DataflowClient.create(options), DataflowPipelineJobTest.JOB_ID, options, ImmutableMap.of());
        long startTime = clock.nanoTime();
        State state = job.waitUntilFinish(Duration.millis(4), null, clock, clock);
        Assert.assertEquals(null, state);
        long timeDiff = TimeUnit.NANOSECONDS.toMillis(((clock.nanoTime()) - startTime));
        // Should only have slept for the 4 ms allowed.
        MatcherAssert.assertThat(timeDiff, Matchers.lessThanOrEqualTo(4L));
    }

    @Test
    public void testGetStateReturnsServiceState() throws Exception {
        Dataflow.Projects.Locations.Jobs.Get statusRequest = Mockito.mock(Get.class);
        Job statusResponse = new Job();
        statusResponse.setCurrentState(("JOB_STATE_" + (RUNNING.name())));
        Mockito.when(mockJobs.get(ArgumentMatchers.eq(DataflowPipelineJobTest.PROJECT_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.REGION_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.JOB_ID))).thenReturn(statusRequest);
        Mockito.when(statusRequest.execute()).thenReturn(statusResponse);
        DataflowPipelineJob job = new DataflowPipelineJob(DataflowClient.create(options), DataflowPipelineJobTest.JOB_ID, options, ImmutableMap.of());
        Assert.assertEquals(RUNNING, job.getStateWithRetries(BackOffAdapter.toGcpBackOff(STATUS_BACKOFF_FACTORY.backoff()), fastClock));
    }

    @Test
    public void testGetStateWithExceptionReturnsUnknown() throws Exception {
        Dataflow.Projects.Locations.Jobs.Get statusRequest = Mockito.mock(Get.class);
        Mockito.when(mockJobs.get(ArgumentMatchers.eq(DataflowPipelineJobTest.PROJECT_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.REGION_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.JOB_ID))).thenReturn(statusRequest);
        Mockito.when(statusRequest.execute()).thenThrow(IOException.class);
        DataflowPipelineJob job = new DataflowPipelineJob(DataflowClient.create(options), DataflowPipelineJobTest.JOB_ID, options, ImmutableMap.of());
        long startTime = fastClock.nanoTime();
        Assert.assertEquals(UNKNOWN, job.getStateWithRetries(BackOffAdapter.toGcpBackOff(STATUS_BACKOFF_FACTORY.backoff()), fastClock));
        long timeDiff = TimeUnit.NANOSECONDS.toMillis(((fastClock.nanoTime()) - startTime));
        checkValidInterval(STATUS_POLLING_INTERVAL, STATUS_POLLING_RETRIES, timeDiff);
    }

    private static class FastNanoClockAndFuzzySleeper implements NanoClock , Sleeper {
        private long fastNanoTime;

        public FastNanoClockAndFuzzySleeper() {
            fastNanoTime = SYSTEM.nanoTime();
        }

        @Override
        public long nanoTime() {
            return fastNanoTime;
        }

        @Override
        public void sleep(long millis) throws InterruptedException {
            fastNanoTime += (millis * 1000000L) + (ThreadLocalRandom.current().nextInt(500000));
        }
    }

    @Test
    public void testCancelUnterminatedJobThatSucceeds() throws IOException {
        Dataflow.Projects.Locations.Jobs.Update update = Mockito.mock(Update.class);
        Mockito.when(mockJobs.update(ArgumentMatchers.eq(DataflowPipelineJobTest.PROJECT_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.REGION_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.JOB_ID), ArgumentMatchers.any(Job.class))).thenReturn(update);
        Mockito.when(update.execute()).thenReturn(new Job().setCurrentState("JOB_STATE_CANCELLED"));
        DataflowPipelineJob job = new DataflowPipelineJob(DataflowClient.create(options), DataflowPipelineJobTest.JOB_ID, options, null);
        Assert.assertEquals(CANCELLED, job.cancel());
        Job content = new Job();
        content.setProjectId(DataflowPipelineJobTest.PROJECT_ID);
        content.setId(DataflowPipelineJobTest.JOB_ID);
        content.setRequestedState("JOB_STATE_CANCELLED");
        Mockito.verify(mockJobs).update(ArgumentMatchers.eq(DataflowPipelineJobTest.PROJECT_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.REGION_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.JOB_ID), ArgumentMatchers.eq(content));
        Mockito.verifyNoMoreInteractions(mockJobs);
    }

    @Test
    public void testCancelUnterminatedJobThatFails() throws IOException {
        Dataflow.Projects.Locations.Jobs.Get statusRequest = Mockito.mock(Get.class);
        Job statusResponse = new Job();
        statusResponse.setCurrentState("JOB_STATE_RUNNING");
        Mockito.when(mockJobs.get(DataflowPipelineJobTest.PROJECT_ID, DataflowPipelineJobTest.REGION_ID, DataflowPipelineJobTest.JOB_ID)).thenReturn(statusRequest);
        Mockito.when(statusRequest.execute()).thenReturn(statusResponse);
        Dataflow.Projects.Locations.Jobs.Update update = Mockito.mock(Update.class);
        Mockito.when(mockJobs.update(ArgumentMatchers.eq(DataflowPipelineJobTest.PROJECT_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.REGION_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.JOB_ID), ArgumentMatchers.any(Job.class))).thenReturn(update);
        Mockito.when(update.execute()).thenThrow(new IOException("Some random IOException"));
        DataflowPipelineJob job = new DataflowPipelineJob(DataflowClient.create(options), DataflowPipelineJobTest.JOB_ID, options, null);
        thrown.expect(IOException.class);
        thrown.expectMessage(("Failed to cancel job in state RUNNING, " + "please go to the Developers Console to cancel it manually:"));
        job.cancel();
    }

    /**
     * Test that {@link DataflowPipelineJob#cancel} doesn't throw if the Dataflow service returns
     * non-terminal state even though the cancel API call failed, which can happen in practice.
     *
     * <p>TODO: delete this code if the API calls become consistent.
     */
    @Test
    public void testCancelTerminatedJobWithStaleState() throws IOException {
        Dataflow.Projects.Locations.Jobs.Get statusRequest = Mockito.mock(Get.class);
        Job statusResponse = new Job();
        statusResponse.setCurrentState("JOB_STATE_RUNNING");
        Mockito.when(mockJobs.get(DataflowPipelineJobTest.PROJECT_ID, DataflowPipelineJobTest.REGION_ID, DataflowPipelineJobTest.JOB_ID)).thenReturn(statusRequest);
        Mockito.when(statusRequest.execute()).thenReturn(statusResponse);
        Dataflow.Projects.Locations.Jobs.Update update = Mockito.mock(Update.class);
        Mockito.when(mockJobs.update(ArgumentMatchers.eq(DataflowPipelineJobTest.PROJECT_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.REGION_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.JOB_ID), ArgumentMatchers.any(Job.class))).thenReturn(update);
        Mockito.when(update.execute()).thenThrow(new IOException("Job has terminated in state SUCCESS"));
        DataflowPipelineJob job = new DataflowPipelineJob(DataflowClient.create(options), DataflowPipelineJobTest.JOB_ID, options, null);
        State returned = job.cancel();
        MatcherAssert.assertThat(returned, Matchers.equalTo(RUNNING));
        expectedLogs.verifyWarn("Cancel failed because job is already terminated.");
    }

    @Test
    public void testCancelTerminatedJob() throws IOException {
        Dataflow.Projects.Locations.Jobs.Get statusRequest = Mockito.mock(Get.class);
        Job statusResponse = new Job();
        statusResponse.setCurrentState("JOB_STATE_FAILED");
        Mockito.when(mockJobs.get(DataflowPipelineJobTest.PROJECT_ID, DataflowPipelineJobTest.REGION_ID, DataflowPipelineJobTest.JOB_ID)).thenReturn(statusRequest);
        Mockito.when(statusRequest.execute()).thenReturn(statusResponse);
        Dataflow.Projects.Locations.Jobs.Update update = Mockito.mock(Update.class);
        Mockito.when(mockJobs.update(ArgumentMatchers.eq(DataflowPipelineJobTest.PROJECT_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.REGION_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.JOB_ID), ArgumentMatchers.any(Job.class))).thenReturn(update);
        Mockito.when(update.execute()).thenThrow(new IOException());
        DataflowPipelineJob job = new DataflowPipelineJob(DataflowClient.create(options), DataflowPipelineJobTest.JOB_ID, options, null);
        Assert.assertEquals(FAILED, job.cancel());
        Job content = new Job();
        content.setProjectId(DataflowPipelineJobTest.PROJECT_ID);
        content.setId(DataflowPipelineJobTest.JOB_ID);
        content.setRequestedState("JOB_STATE_CANCELLED");
        Mockito.verify(mockJobs).update(ArgumentMatchers.eq(DataflowPipelineJobTest.PROJECT_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.REGION_ID), ArgumentMatchers.eq(DataflowPipelineJobTest.JOB_ID), ArgumentMatchers.eq(content));
        Mockito.verify(mockJobs).get(DataflowPipelineJobTest.PROJECT_ID, DataflowPipelineJobTest.REGION_ID, DataflowPipelineJobTest.JOB_ID);
        Mockito.verifyNoMoreInteractions(mockJobs);
    }

    /**
     * Tests that a {@link DataflowPipelineJob} does not duplicate messages.
     */
    @Test
    public void testWaitUntilFinishNoRepeatedLogs() throws Exception {
        DataflowPipelineJob job = new DataflowPipelineJob(mockDataflowClient, DataflowPipelineJobTest.JOB_ID, options, null);
        Sleeper sleeper = new DataflowPipelineJobTest.ZeroSleeper();
        NanoClock nanoClock = Mockito.mock(NanoClock.class);
        Instant separatingTimestamp = new Instant(42L);
        JobMessage theMessage = DataflowPipelineJobTest.infoMessage(separatingTimestamp, "nothing");
        MonitoringUtil mockMonitor = Mockito.mock(MonitoringUtil.class);
        Mockito.when(mockMonitor.getJobMessages(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(ImmutableList.of(theMessage));
        // The Job just always reports "running" across all calls
        Job fakeJob = new Job();
        fakeJob.setCurrentState("JOB_STATE_RUNNING");
        Mockito.when(mockDataflowClient.getJob(ArgumentMatchers.anyString())).thenReturn(fakeJob);
        // After waitUntilFinish the DataflowPipelineJob should record the latest message timestamp
        Mockito.when(nanoClock.nanoTime()).thenReturn(0L).thenReturn(2000000000L);
        job.waitUntilFinish(Duration.standardSeconds(1), mockHandler, sleeper, nanoClock, mockMonitor);
        Mockito.verify(mockHandler).process(ImmutableList.of(theMessage));
        // Second waitUntilFinish should request jobs with `separatingTimestamp` so the monitor
        // will only return new messages
        Mockito.when(nanoClock.nanoTime()).thenReturn(3000000000L).thenReturn(6000000000L);
        job.waitUntilFinish(Duration.standardSeconds(1), mockHandler, sleeper, nanoClock, mockMonitor);
        Mockito.verify(mockMonitor).getJobMessages(ArgumentMatchers.anyString(), ArgumentMatchers.eq(separatingTimestamp.getMillis()));
    }

    private class FakeMonitor extends MonitoringUtil {
        // Messages in timestamp order
        private final NavigableMap<Long, JobMessage> timestampedMessages;

        public FakeMonitor(JobMessage... messages) {
            // The client should never be used; this Fake is intended to intercept relevant methods
            super(mockDataflowClient);
            NavigableMap<Long, JobMessage> timestampedMessages = Maps.newTreeMap();
            for (JobMessage message : messages) {
                timestampedMessages.put(Long.parseLong(message.getTime()), message);
            }
            this.timestampedMessages = timestampedMessages;
        }

        @Override
        public List<JobMessage> getJobMessages(String jobId, long startTimestampMs) {
            return ImmutableList.copyOf(timestampedMessages.headMap(startTimestampMs).values());
        }
    }

    private static class ZeroSleeper implements Sleeper {
        @Override
        public void sleep(long l) throws InterruptedException {
        }
    }
}

