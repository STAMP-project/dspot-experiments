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


import State.DONE;
import State.FAILED;
import State.RUNNING;
import com.google.api.services.dataflow.model.JobMessage;
import com.google.api.services.dataflow.model.JobMetrics;
import java.io.IOException;
import java.util.Arrays;
import org.apache.beam.runners.dataflow.util.MonitoringUtil.JobMessagesHandler;
import org.apache.beam.runners.dataflow.util.TimeUtil;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.SerializableMatcher;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.TestPipelineOptions;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Optional;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
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
 * Tests for {@link TestDataflowRunner}.
 */
@RunWith(JUnit4.class)
public class TestDataflowRunnerTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private DataflowClient mockClient;

    private TestDataflowPipelineOptions options;

    @Test
    public void testToString() {
        Assert.assertEquals("TestDataflowRunner#TestAppName", TestDataflowRunner.fromOptions(options).toString());
    }

    @Test
    public void testRunBatchJobThatSucceeds() throws Exception {
        Pipeline p = Pipeline.create(options);
        PCollection<Integer> pc = p.apply(Create.of(1, 2, 3));
        PAssert.that(pc).containsInAnyOrder(1, 2, 3);
        DataflowPipelineJob mockJob = Mockito.mock(DataflowPipelineJob.class);
        Mockito.when(mockJob.getState()).thenReturn(DONE);
        Mockito.when(mockJob.getProjectId()).thenReturn("test-project");
        Mockito.when(mockJob.getJobId()).thenReturn("test-job");
        DataflowRunner mockRunner = Mockito.mock(DataflowRunner.class);
        Mockito.when(mockRunner.run(ArgumentMatchers.any(Pipeline.class))).thenReturn(mockJob);
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenReturn(/* success */
        /* tentative */
        generateMockMetricResponse(true, true));
        Assert.assertEquals(mockJob, runner.run(p, mockRunner));
    }

    /**
     * Job success on Dataflow means that it handled transient errors (if any) successfully by
     * retrying failed bundles.
     */
    @Test
    public void testRunBatchJobThatSucceedsDespiteTransientErrors() throws Exception {
        Pipeline p = Pipeline.create(options);
        PCollection<Integer> pc = p.apply(Create.of(1, 2, 3));
        PAssert.that(pc).containsInAnyOrder(1, 2, 3);
        DataflowPipelineJob mockJob = Mockito.mock(DataflowPipelineJob.class);
        Mockito.when(mockJob.getState()).thenReturn(DONE);
        Mockito.when(mockJob.getProjectId()).thenReturn("test-project");
        Mockito.when(mockJob.getJobId()).thenReturn("test-job");
        Mockito.when(mockJob.waitUntilFinish(ArgumentMatchers.any(Duration.class), ArgumentMatchers.any(JobMessagesHandler.class))).thenAnswer(( invocation) -> {
            JobMessage message = new JobMessage();
            message.setMessageText("TransientError");
            message.setTime(TimeUtil.toCloudTime(Instant.now()));
            message.setMessageImportance("JOB_MESSAGE_ERROR");
            ((JobMessagesHandler) (invocation.getArguments()[1])).process(Arrays.asList(message));
            return State.DONE;
        });
        DataflowRunner mockRunner = Mockito.mock(DataflowRunner.class);
        Mockito.when(mockRunner.run(ArgumentMatchers.any(Pipeline.class))).thenReturn(mockJob);
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenReturn(/* success */
        /* tentative */
        generateMockMetricResponse(true, true));
        Assert.assertEquals(mockJob, runner.run(p, mockRunner));
    }

    /**
     * Tests that when a batch job terminates in a failure state even if all assertions passed, it
     * throws an error to that effect.
     */
    @Test
    public void testRunBatchJobThatFails() throws Exception {
        Pipeline p = TestPipeline.create(options);
        PCollection<Integer> pc = p.apply(Create.of(1, 2, 3));
        PAssert.that(pc).containsInAnyOrder(1, 2, 3);
        DataflowPipelineJob mockJob = Mockito.mock(DataflowPipelineJob.class);
        Mockito.when(mockJob.getState()).thenReturn(FAILED);
        Mockito.when(mockJob.getProjectId()).thenReturn("test-project");
        Mockito.when(mockJob.getJobId()).thenReturn("test-job");
        DataflowRunner mockRunner = Mockito.mock(DataflowRunner.class);
        Mockito.when(mockRunner.run(ArgumentMatchers.any(Pipeline.class))).thenReturn(mockJob);
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenReturn(/* success */
        /* tentative */
        generateMockMetricResponse(true, false));
        expectedException.expect(RuntimeException.class);
        runner.run(p, mockRunner);
        // Note that fail throws an AssertionError which is why it is placed out here
        // instead of inside the try-catch block.
        Assert.fail("AssertionError expected");
    }

    @Test
    public void testBatchPipelineFailsIfException() throws Exception {
        Pipeline p = TestPipeline.create(options);
        PCollection<Integer> pc = p.apply(Create.of(1, 2, 3));
        PAssert.that(pc).containsInAnyOrder(1, 2, 3);
        DataflowPipelineJob mockJob = Mockito.mock(DataflowPipelineJob.class);
        Mockito.when(mockJob.getState()).thenReturn(RUNNING);
        Mockito.when(mockJob.getProjectId()).thenReturn("test-project");
        Mockito.when(mockJob.getJobId()).thenReturn("test-job");
        Mockito.when(mockJob.waitUntilFinish(ArgumentMatchers.any(Duration.class), ArgumentMatchers.any(JobMessagesHandler.class))).thenAnswer(( invocation) -> {
            JobMessage message = new JobMessage();
            message.setMessageText("FooException");
            message.setTime(TimeUtil.toCloudTime(Instant.now()));
            message.setMessageImportance("JOB_MESSAGE_ERROR");
            ((JobMessagesHandler) (invocation.getArguments()[1])).process(Arrays.asList(message));
            return State.CANCELLED;
        });
        DataflowRunner mockRunner = Mockito.mock(DataflowRunner.class);
        Mockito.when(mockRunner.run(ArgumentMatchers.any(Pipeline.class))).thenReturn(mockJob);
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenReturn(/* success */
        /* tentative */
        generateMockMetricResponse(false, true));
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        try {
            runner.run(p, mockRunner);
        } catch (AssertionError expected) {
            Assert.assertThat(expected.getMessage(), Matchers.containsString("FooException"));
            Mockito.verify(mockJob, Mockito.never()).cancel();
            return;
        }
        // Note that fail throws an AssertionError which is why it is placed out here
        // instead of inside the try-catch block.
        Assert.fail("AssertionError expected");
    }

    /**
     * A streaming job that terminates with no error messages is a success.
     */
    @Test
    public void testRunStreamingJobUsingPAssertThatSucceeds() throws Exception {
        options.setStreaming(true);
        Pipeline p = TestPipeline.create(options);
        PCollection<Integer> pc = p.apply(Create.of(1, 2, 3));
        PAssert.that(pc).containsInAnyOrder(1, 2, 3);
        DataflowPipelineJob mockJob = Mockito.mock(DataflowPipelineJob.class);
        Mockito.when(mockJob.getState()).thenReturn(DONE);
        Mockito.when(mockJob.waitUntilFinish(ArgumentMatchers.any(Duration.class), ArgumentMatchers.any(JobMessagesHandler.class))).thenReturn(DONE);
        Mockito.when(mockJob.getProjectId()).thenReturn("test-project");
        Mockito.when(mockJob.getJobId()).thenReturn("test-job");
        DataflowRunner mockRunner = Mockito.mock(DataflowRunner.class);
        Mockito.when(mockRunner.run(ArgumentMatchers.any(Pipeline.class))).thenReturn(mockJob);
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenReturn(/* success */
        /* tentative */
        generateMockMetricResponse(true, true));
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        runner.run(p, mockRunner);
    }

    @Test
    public void testRunStreamingJobNotUsingPAssertThatSucceeds() throws Exception {
        options.setStreaming(true);
        Pipeline p = TestPipeline.create(options);
        p.apply(Create.of(1, 2, 3));
        DataflowPipelineJob mockJob = Mockito.mock(DataflowPipelineJob.class);
        Mockito.when(mockJob.getState()).thenReturn(DONE);
        Mockito.when(mockJob.waitUntilFinish(ArgumentMatchers.any(Duration.class), ArgumentMatchers.any(JobMessagesHandler.class))).thenReturn(DONE);
        Mockito.when(mockJob.getProjectId()).thenReturn("test-project");
        Mockito.when(mockJob.getJobId()).thenReturn("test-job");
        DataflowRunner mockRunner = Mockito.mock(DataflowRunner.class);
        Mockito.when(mockRunner.run(ArgumentMatchers.any(Pipeline.class))).thenReturn(mockJob);
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenReturn(generateMockStreamingMetricResponse(ImmutableMap.of()));
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        runner.run(p, mockRunner);
    }

    /**
     * Tests that a streaming job with a false {@link PAssert} fails.
     *
     * <p>Currently, this failure is indistinguishable from a non-{@link PAssert} failure, because it
     * is detected only by failure job messages. With fuller metric support, this can detect a PAssert
     * failure via metrics and raise an {@link AssertionError} in just that case.
     */
    @Test
    public void testRunStreamingJobThatFails() throws Exception {
        testStreamingPipelineFailsIfException();
    }

    /**
     * Tests that a tentative {@code true} from metrics indicates that every {@link PAssert} has
     * succeeded.
     */
    @Test
    public void testCheckingForSuccessWhenPAssertSucceeds() throws Exception {
        DataflowPipelineJob job = Mockito.spy(new DataflowPipelineJob(mockClient, "test-job", options, null));
        Pipeline p = TestPipeline.create(options);
        PCollection<Integer> pc = p.apply(Create.of(1, 2, 3));
        PAssert.that(pc).containsInAnyOrder(1, 2, 3);
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenReturn(buildJobMetrics(/* success */
        /* tentative */
        generateMockMetrics(true, true)));
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        Mockito.doReturn(DONE).when(job).getState();
        Assert.assertThat(runner.checkForPAssertSuccess(job), Matchers.equalTo(Optional.of(true)));
    }

    /**
     * Tests that when we just see a tentative failure for a {@link PAssert} it is considered a
     * conclusive failure.
     */
    @Test
    public void testCheckingForSuccessWhenPAssertFails() throws Exception {
        DataflowPipelineJob job = Mockito.spy(new DataflowPipelineJob(mockClient, "test-job", options, null));
        Pipeline p = TestPipeline.create(options);
        PCollection<Integer> pc = p.apply(Create.of(1, 2, 3));
        PAssert.that(pc).containsInAnyOrder(1, 2, 3);
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenReturn(buildJobMetrics(/* success */
        /* tentative */
        generateMockMetrics(false, true)));
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        Mockito.doReturn(DONE).when(job).getState();
        Assert.assertThat(runner.checkForPAssertSuccess(job), Matchers.equalTo(Optional.of(false)));
    }

    @Test
    public void testCheckingForSuccessSkipsNonTentativeMetrics() throws Exception {
        DataflowPipelineJob job = Mockito.spy(new DataflowPipelineJob(mockClient, "test-job", options, null));
        Pipeline p = TestPipeline.create(options);
        PCollection<Integer> pc = p.apply(Create.of(1, 2, 3));
        PAssert.that(pc).containsInAnyOrder(1, 2, 3);
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenReturn(buildJobMetrics(/* success */
        /* tentative */
        generateMockMetrics(true, false)));
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        runner.updatePAssertCount(p);
        Mockito.doReturn(RUNNING).when(job).getState();
        Assert.assertThat(runner.checkForPAssertSuccess(job), Matchers.equalTo(Optional.<Boolean>absent()));
    }

    /**
     * Tests that if a streaming pipeline crash loops for a non-assertion reason that the test run
     * throws an {@link AssertionError}.
     *
     * <p>This is a known limitation/bug of the runner that it does not distinguish the two modes of
     * failure.
     */
    @Test
    public void testStreamingPipelineFailsIfException() throws Exception {
        options.setStreaming(true);
        Pipeline pipeline = TestPipeline.create(options);
        PCollection<Integer> pc = pipeline.apply(Create.of(1, 2, 3));
        PAssert.that(pc).containsInAnyOrder(1, 2, 3);
        DataflowPipelineJob mockJob = Mockito.mock(DataflowPipelineJob.class);
        Mockito.when(mockJob.getState()).thenReturn(RUNNING);
        Mockito.when(mockJob.getProjectId()).thenReturn("test-project");
        Mockito.when(mockJob.getJobId()).thenReturn("test-job");
        Mockito.when(mockJob.waitUntilFinish(ArgumentMatchers.any(Duration.class), ArgumentMatchers.any(JobMessagesHandler.class))).thenAnswer(( invocation) -> {
            JobMessage message = new JobMessage();
            message.setMessageText("FooException");
            message.setTime(TimeUtil.toCloudTime(Instant.now()));
            message.setMessageImportance("JOB_MESSAGE_ERROR");
            ((JobMessagesHandler) (invocation.getArguments()[1])).process(Arrays.asList(message));
            return State.CANCELLED;
        });
        DataflowRunner mockRunner = Mockito.mock(DataflowRunner.class);
        Mockito.when(mockRunner.run(ArgumentMatchers.any(Pipeline.class))).thenReturn(mockJob);
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenReturn(/* success */
        /* tentative */
        generateMockMetricResponse(false, true));
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        expectedException.expect(RuntimeException.class);
        runner.run(pipeline, mockRunner);
    }

    @Test
    public void testGetJobMetricsThatSucceeds() throws Exception {
        DataflowPipelineJob job = Mockito.spy(new DataflowPipelineJob(mockClient, "test-job", options, null));
        Pipeline p = TestPipeline.create(options);
        p.apply(Create.of(1, 2, 3));
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenReturn(/* success */
        /* tentative */
        generateMockMetricResponse(true, true));
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        JobMetrics metrics = runner.getJobMetrics(job);
        Assert.assertEquals(1, metrics.getMetrics().size());
        Assert.assertEquals(/* success */
        /* tentative */
        generateMockMetrics(true, true), metrics.getMetrics());
    }

    @Test
    public void testGetJobMetricsThatFailsForException() throws Exception {
        DataflowPipelineJob job = Mockito.spy(new DataflowPipelineJob(mockClient, "test-job", options, null));
        Pipeline p = TestPipeline.create(options);
        p.apply(Create.of(1, 2, 3));
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenThrow(new IOException());
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        Assert.assertNull(runner.getJobMetrics(job));
    }

    @Test
    public void testBatchOnCreateMatcher() throws Exception {
        Pipeline p = TestPipeline.create(options);
        PCollection<Integer> pc = p.apply(Create.of(1, 2, 3));
        PAssert.that(pc).containsInAnyOrder(1, 2, 3);
        final DataflowPipelineJob mockJob = Mockito.mock(DataflowPipelineJob.class);
        Mockito.when(mockJob.getState()).thenReturn(DONE);
        Mockito.when(mockJob.getProjectId()).thenReturn("test-project");
        Mockito.when(mockJob.getJobId()).thenReturn("test-job");
        DataflowRunner mockRunner = Mockito.mock(DataflowRunner.class);
        Mockito.when(mockRunner.run(ArgumentMatchers.any(Pipeline.class))).thenReturn(mockJob);
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        options.as(TestPipelineOptions.class).setOnCreateMatcher(new TestDataflowRunnerTest.TestSuccessMatcher(mockJob, 0));
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenReturn(/* success */
        /* tentative */
        generateMockMetricResponse(true, true));
        runner.run(p, mockRunner);
    }

    @Test
    public void testStreamingOnCreateMatcher() throws Exception {
        options.setStreaming(true);
        Pipeline p = TestPipeline.create(options);
        PCollection<Integer> pc = p.apply(Create.of(1, 2, 3));
        PAssert.that(pc).containsInAnyOrder(1, 2, 3);
        final DataflowPipelineJob mockJob = Mockito.mock(DataflowPipelineJob.class);
        Mockito.when(mockJob.getState()).thenReturn(DONE);
        Mockito.when(mockJob.getProjectId()).thenReturn("test-project");
        Mockito.when(mockJob.getJobId()).thenReturn("test-job");
        DataflowRunner mockRunner = Mockito.mock(DataflowRunner.class);
        Mockito.when(mockRunner.run(ArgumentMatchers.any(Pipeline.class))).thenReturn(mockJob);
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        options.as(TestPipelineOptions.class).setOnCreateMatcher(new TestDataflowRunnerTest.TestSuccessMatcher(mockJob, 0));
        Mockito.when(mockJob.waitUntilFinish(ArgumentMatchers.any(Duration.class), ArgumentMatchers.any(JobMessagesHandler.class))).thenReturn(DONE);
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenReturn(/* success */
        /* tentative */
        generateMockMetricResponse(true, true));
        runner.run(p, mockRunner);
    }

    @Test
    public void testBatchOnSuccessMatcherWhenPipelineSucceeds() throws Exception {
        Pipeline p = TestPipeline.create(options);
        PCollection<Integer> pc = p.apply(Create.of(1, 2, 3));
        PAssert.that(pc).containsInAnyOrder(1, 2, 3);
        final DataflowPipelineJob mockJob = Mockito.mock(DataflowPipelineJob.class);
        Mockito.when(mockJob.getState()).thenReturn(DONE);
        Mockito.when(mockJob.getProjectId()).thenReturn("test-project");
        Mockito.when(mockJob.getJobId()).thenReturn("test-job");
        DataflowRunner mockRunner = Mockito.mock(DataflowRunner.class);
        Mockito.when(mockRunner.run(ArgumentMatchers.any(Pipeline.class))).thenReturn(mockJob);
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        options.as(TestPipelineOptions.class).setOnSuccessMatcher(new TestDataflowRunnerTest.TestSuccessMatcher(mockJob, 1));
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenReturn(/* success */
        /* tentative */
        generateMockMetricResponse(true, true));
        runner.run(p, mockRunner);
    }

    /**
     * Tests that when a streaming pipeline terminates and doesn't fail due to {@link PAssert} that
     * the {@link TestPipelineOptions#setOnSuccessMatcher(SerializableMatcher) on success matcher} is
     * invoked.
     */
    @Test
    public void testStreamingOnSuccessMatcherWhenPipelineSucceeds() throws Exception {
        options.setStreaming(true);
        Pipeline p = TestPipeline.create(options);
        PCollection<Integer> pc = p.apply(Create.of(1, 2, 3));
        PAssert.that(pc).containsInAnyOrder(1, 2, 3);
        final DataflowPipelineJob mockJob = Mockito.mock(DataflowPipelineJob.class);
        Mockito.when(mockJob.getState()).thenReturn(DONE);
        Mockito.when(mockJob.getProjectId()).thenReturn("test-project");
        Mockito.when(mockJob.getJobId()).thenReturn("test-job");
        DataflowRunner mockRunner = Mockito.mock(DataflowRunner.class);
        Mockito.when(mockRunner.run(ArgumentMatchers.any(Pipeline.class))).thenReturn(mockJob);
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        options.as(TestPipelineOptions.class).setOnSuccessMatcher(new TestDataflowRunnerTest.TestSuccessMatcher(mockJob, 1));
        Mockito.when(mockJob.waitUntilFinish(ArgumentMatchers.any(Duration.class), ArgumentMatchers.any(JobMessagesHandler.class))).thenReturn(DONE);
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenReturn(/* success */
        /* tentative */
        generateMockMetricResponse(true, true));
        runner.run(p, mockRunner);
    }

    @Test
    public void testBatchOnSuccessMatcherWhenPipelineFails() throws Exception {
        Pipeline p = TestPipeline.create(options);
        PCollection<Integer> pc = p.apply(Create.of(1, 2, 3));
        PAssert.that(pc).containsInAnyOrder(1, 2, 3);
        final DataflowPipelineJob mockJob = Mockito.mock(DataflowPipelineJob.class);
        Mockito.when(mockJob.getState()).thenReturn(FAILED);
        Mockito.when(mockJob.getProjectId()).thenReturn("test-project");
        Mockito.when(mockJob.getJobId()).thenReturn("test-job");
        DataflowRunner mockRunner = Mockito.mock(DataflowRunner.class);
        Mockito.when(mockRunner.run(ArgumentMatchers.any(Pipeline.class))).thenReturn(mockJob);
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        options.as(TestPipelineOptions.class).setOnSuccessMatcher(new TestDataflowRunnerTest.TestFailureMatcher());
        Mockito.when(mockClient.getJobMetrics(ArgumentMatchers.anyString())).thenReturn(/* success */
        /* tentative */
        generateMockMetricResponse(false, true));
        try {
            runner.run(p, mockRunner);
        } catch (AssertionError expected) {
            Mockito.verify(mockJob, Mockito.times(1)).waitUntilFinish(ArgumentMatchers.any(Duration.class), ArgumentMatchers.any(JobMessagesHandler.class));
            return;
        }
        Assert.fail("Expected an exception on pipeline failure.");
    }

    /**
     * Tests that when a streaming pipeline terminates in FAIL that the {@link TestPipelineOptions#setOnSuccessMatcher(SerializableMatcher) on success matcher} is not
     * invoked.
     */
    @Test
    public void testStreamingOnSuccessMatcherWhenPipelineFails() throws Exception {
        options.setStreaming(true);
        Pipeline p = TestPipeline.create(options);
        PCollection<Integer> pc = p.apply(Create.of(1, 2, 3));
        PAssert.that(pc).containsInAnyOrder(1, 2, 3);
        final DataflowPipelineJob mockJob = Mockito.mock(DataflowPipelineJob.class);
        Mockito.when(mockJob.getState()).thenReturn(FAILED);
        Mockito.when(mockJob.getProjectId()).thenReturn("test-project");
        Mockito.when(mockJob.getJobId()).thenReturn("test-job");
        DataflowRunner mockRunner = Mockito.mock(DataflowRunner.class);
        Mockito.when(mockRunner.run(ArgumentMatchers.any(Pipeline.class))).thenReturn(mockJob);
        TestDataflowRunner runner = TestDataflowRunner.fromOptionsAndClient(options, mockClient);
        options.as(TestPipelineOptions.class).setOnSuccessMatcher(new TestDataflowRunnerTest.TestFailureMatcher());
        Mockito.when(mockJob.waitUntilFinish(ArgumentMatchers.any(Duration.class), ArgumentMatchers.any(JobMessagesHandler.class))).thenReturn(FAILED);
        expectedException.expect(RuntimeException.class);
        runner.run(p, mockRunner);
        // If the onSuccessMatcher were invoked, it would have crashed here with AssertionError
    }

    static class TestSuccessMatcher extends BaseMatcher<PipelineResult> implements SerializableMatcher<PipelineResult> {
        private final transient DataflowPipelineJob mockJob;

        private final int called;

        public TestSuccessMatcher(DataflowPipelineJob job, int times) {
            this.mockJob = job;
            this.called = times;
        }

        @Override
        public boolean matches(Object o) {
            if (!(o instanceof PipelineResult)) {
                Assert.fail(String.format("Expected PipelineResult but received %s", o));
            }
            try {
                Mockito.verify(mockJob, Mockito.times(called)).waitUntilFinish(ArgumentMatchers.any(Duration.class), ArgumentMatchers.any(JobMessagesHandler.class));
            } catch (IOException | InterruptedException e) {
                throw new AssertionError(e);
            }
            Assert.assertSame(mockJob, o);
            return true;
        }

        @Override
        public void describeTo(Description description) {
        }
    }

    static class TestFailureMatcher extends BaseMatcher<PipelineResult> implements SerializableMatcher<PipelineResult> {
        @Override
        public boolean matches(Object o) {
            Assert.fail("OnSuccessMatcher should not be called on pipeline failure.");
            return false;
        }

        @Override
        public void describeTo(Description description) {
        }
    }
}

