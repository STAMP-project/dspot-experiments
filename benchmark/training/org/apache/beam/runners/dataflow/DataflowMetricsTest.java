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
import State.DONE;
import State.RUNNING;
import com.google.api.services.dataflow.Dataflow;
import com.google.api.services.dataflow.model.Job;
import com.google.api.services.dataflow.model.JobMetrics;
import com.google.api.services.dataflow.model.MetricUpdate;
import java.io.IOException;
import java.math.BigDecimal;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.metrics.DistributionResult;
import org.apache.beam.sdk.metrics.MetricQueryResults;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.HashBiMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link DataflowMetrics}.
 */
@RunWith(JUnit4.class)
public class DataflowMetricsTest {
    private static final String PROJECT_ID = "some-project";

    private static final String JOB_ID = "1234";

    @Mock
    private Dataflow mockWorkflowClient;

    @Mock
    private Projects mockProjects;

    @Mock
    private Locations mockLocations;

    @Mock
    private Jobs mockJobs;

    private TestDataflowPipelineOptions options;

    @Test
    public void testEmptyMetricUpdates() throws IOException {
        Job modelJob = new Job();
        modelJob.setCurrentState(RUNNING.toString());
        DataflowPipelineJob job = Mockito.mock(DataflowPipelineJob.class);
        DataflowPipelineOptions options = Mockito.mock(DataflowPipelineOptions.class);
        Mockito.when(options.isStreaming()).thenReturn(false);
        Mockito.when(job.getDataflowOptions()).thenReturn(options);
        Mockito.when(job.getState()).thenReturn(RUNNING);
        job.jobId = DataflowMetricsTest.JOB_ID;
        JobMetrics jobMetrics = new JobMetrics();
        /* this is how the APIs represent empty metrics */
        jobMetrics.setMetrics(null);
        DataflowClient dataflowClient = Mockito.mock(DataflowClient.class);
        Mockito.when(dataflowClient.getJobMetrics(DataflowMetricsTest.JOB_ID)).thenReturn(jobMetrics);
        DataflowMetrics dataflowMetrics = new DataflowMetrics(job, dataflowClient);
        MetricQueryResults result = dataflowMetrics.allMetrics();
        MatcherAssert.assertThat(ImmutableList.copyOf(result.getCounters()), Matchers.is(Matchers.empty()));
        MatcherAssert.assertThat(ImmutableList.copyOf(result.getDistributions()), Matchers.is(Matchers.empty()));
    }

    @Test
    public void testCachingMetricUpdates() throws IOException {
        Job modelJob = new Job();
        modelJob.setCurrentState(RUNNING.toString());
        DataflowPipelineJob job = Mockito.mock(DataflowPipelineJob.class);
        DataflowPipelineOptions options = Mockito.mock(DataflowPipelineOptions.class);
        Mockito.when(options.isStreaming()).thenReturn(false);
        Mockito.when(job.getDataflowOptions()).thenReturn(options);
        Mockito.when(job.getState()).thenReturn(DONE);
        job.jobId = DataflowMetricsTest.JOB_ID;
        JobMetrics jobMetrics = new JobMetrics();
        jobMetrics.setMetrics(ImmutableList.of());
        DataflowClient dataflowClient = Mockito.mock(DataflowClient.class);
        Mockito.when(dataflowClient.getJobMetrics(DataflowMetricsTest.JOB_ID)).thenReturn(jobMetrics);
        DataflowMetrics dataflowMetrics = new DataflowMetrics(job, dataflowClient);
        Mockito.verify(dataflowClient, Mockito.times(0)).getJobMetrics(DataflowMetricsTest.JOB_ID);
        dataflowMetrics.allMetrics();
        Mockito.verify(dataflowClient, Mockito.times(1)).getJobMetrics(DataflowMetricsTest.JOB_ID);
        dataflowMetrics.allMetrics();
        Mockito.verify(dataflowClient, Mockito.times(1)).getJobMetrics(DataflowMetricsTest.JOB_ID);
    }

    @Test
    public void testSingleCounterUpdates() throws IOException {
        JobMetrics jobMetrics = new JobMetrics();
        DataflowPipelineJob job = Mockito.mock(DataflowPipelineJob.class);
        DataflowPipelineOptions options = Mockito.mock(DataflowPipelineOptions.class);
        Mockito.when(options.isStreaming()).thenReturn(false);
        Mockito.when(job.getDataflowOptions()).thenReturn(options);
        Mockito.when(job.getState()).thenReturn(RUNNING);
        job.jobId = DataflowMetricsTest.JOB_ID;
        AppliedPTransform<?, ?, ?> myStep = Mockito.mock(AppliedPTransform.class);
        Mockito.when(myStep.getFullName()).thenReturn("myStepName");
        job.transformStepNames = HashBiMap.create();
        job.transformStepNames.put(myStep, "s2");
        MetricUpdate update = new MetricUpdate();
        long stepValue = 1234L;
        update.setScalar(new BigDecimal(stepValue));
        // The parser relies on the fact that one tentative and one committed metric update exist in
        // the job metrics results.
        MetricUpdate mu1 = makeCounterMetricUpdate("counterName", "counterNamespace", "s2", 1234L, false);
        MetricUpdate mu1Tentative = makeCounterMetricUpdate("counterName", "counterNamespace", "s2", 1233L, true);
        jobMetrics.setMetrics(ImmutableList.of(mu1, mu1Tentative));
        DataflowClient dataflowClient = Mockito.mock(DataflowClient.class);
        Mockito.when(dataflowClient.getJobMetrics(DataflowMetricsTest.JOB_ID)).thenReturn(jobMetrics);
        DataflowMetrics dataflowMetrics = new DataflowMetrics(job, dataflowClient);
        MetricQueryResults result = dataflowMetrics.allMetrics();
        MatcherAssert.assertThat(result.getCounters(), Matchers.containsInAnyOrder(attemptedMetricsResult("counterNamespace", "counterName", "myStepName", 1234L)));
        MatcherAssert.assertThat(result.getCounters(), Matchers.containsInAnyOrder(committedMetricsResult("counterNamespace", "counterName", "myStepName", 1234L)));
    }

    @Test
    public void testIgnoreDistributionButGetCounterUpdates() throws IOException {
        JobMetrics jobMetrics = new JobMetrics();
        DataflowClient dataflowClient = Mockito.mock(DataflowClient.class);
        Mockito.when(dataflowClient.getJobMetrics(DataflowMetricsTest.JOB_ID)).thenReturn(jobMetrics);
        DataflowPipelineJob job = Mockito.mock(DataflowPipelineJob.class);
        DataflowPipelineOptions options = Mockito.mock(DataflowPipelineOptions.class);
        Mockito.when(options.isStreaming()).thenReturn(false);
        Mockito.when(job.getDataflowOptions()).thenReturn(options);
        Mockito.when(job.getState()).thenReturn(RUNNING);
        job.jobId = DataflowMetricsTest.JOB_ID;
        AppliedPTransform<?, ?, ?> myStep = Mockito.mock(AppliedPTransform.class);
        Mockito.when(myStep.getFullName()).thenReturn("myStepName");
        job.transformStepNames = HashBiMap.create();
        job.transformStepNames.put(myStep, "s2");
        // The parser relies on the fact that one tentative and one committed metric update exist in
        // the job metrics results.
        jobMetrics.setMetrics(ImmutableList.of(makeCounterMetricUpdate("counterName", "counterNamespace", "s2", 1233L, false), makeCounterMetricUpdate("counterName", "counterNamespace", "s2", 1233L, true), makeCounterMetricUpdate("otherCounter[MIN]", "otherNamespace", "s2", 0L, false), makeCounterMetricUpdate("otherCounter[MIN]", "otherNamespace", "s2", 0L, true)));
        DataflowMetrics dataflowMetrics = new DataflowMetrics(job, dataflowClient);
        MetricQueryResults result = dataflowMetrics.allMetrics();
        MatcherAssert.assertThat(result.getCounters(), Matchers.containsInAnyOrder(attemptedMetricsResult("counterNamespace", "counterName", "myStepName", 1233L)));
        MatcherAssert.assertThat(result.getCounters(), Matchers.containsInAnyOrder(committedMetricsResult("counterNamespace", "counterName", "myStepName", 1233L)));
    }

    @Test
    public void testDistributionUpdates() throws IOException {
        JobMetrics jobMetrics = new JobMetrics();
        DataflowClient dataflowClient = Mockito.mock(DataflowClient.class);
        Mockito.when(dataflowClient.getJobMetrics(DataflowMetricsTest.JOB_ID)).thenReturn(jobMetrics);
        DataflowPipelineJob job = Mockito.mock(DataflowPipelineJob.class);
        DataflowPipelineOptions options = Mockito.mock(DataflowPipelineOptions.class);
        Mockito.when(options.isStreaming()).thenReturn(false);
        Mockito.when(job.getDataflowOptions()).thenReturn(options);
        Mockito.when(job.getState()).thenReturn(RUNNING);
        job.jobId = DataflowMetricsTest.JOB_ID;
        AppliedPTransform<?, ?, ?> myStep2 = Mockito.mock(AppliedPTransform.class);
        Mockito.when(myStep2.getFullName()).thenReturn("myStepName");
        job.transformStepNames = HashBiMap.create();
        job.transformStepNames.put(myStep2, "s2");
        // The parser relies on the fact that one tentative and one committed metric update exist in
        // the job metrics results.
        jobMetrics.setMetrics(ImmutableList.of(makeDistributionMetricUpdate("distributionName", "distributionNamespace", "s2", 18L, 2L, 2L, 16L, false), makeDistributionMetricUpdate("distributionName", "distributionNamespace", "s2", 18L, 2L, 2L, 16L, true)));
        DataflowMetrics dataflowMetrics = new DataflowMetrics(job, dataflowClient);
        MetricQueryResults result = dataflowMetrics.allMetrics();
        MatcherAssert.assertThat(result.getDistributions(), Matchers.contains(attemptedMetricsResult("distributionNamespace", "distributionName", "myStepName", DistributionResult.create(18, 2, 2, 16))));
        MatcherAssert.assertThat(result.getDistributions(), Matchers.contains(committedMetricsResult("distributionNamespace", "distributionName", "myStepName", DistributionResult.create(18, 2, 2, 16))));
    }

    @Test
    public void testDistributionUpdatesStreaming() throws IOException {
        JobMetrics jobMetrics = new JobMetrics();
        DataflowClient dataflowClient = Mockito.mock(DataflowClient.class);
        Mockito.when(dataflowClient.getJobMetrics(DataflowMetricsTest.JOB_ID)).thenReturn(jobMetrics);
        DataflowPipelineJob job = Mockito.mock(DataflowPipelineJob.class);
        DataflowPipelineOptions options = Mockito.mock(DataflowPipelineOptions.class);
        Mockito.when(options.isStreaming()).thenReturn(true);
        Mockito.when(job.getDataflowOptions()).thenReturn(options);
        Mockito.when(job.getState()).thenReturn(RUNNING);
        job.jobId = DataflowMetricsTest.JOB_ID;
        AppliedPTransform<?, ?, ?> myStep2 = Mockito.mock(AppliedPTransform.class);
        Mockito.when(myStep2.getFullName()).thenReturn("myStepName");
        job.transformStepNames = HashBiMap.create();
        job.transformStepNames.put(myStep2, "s2");
        // The parser relies on the fact that one tentative and one committed metric update exist in
        // the job metrics results.
        jobMetrics.setMetrics(ImmutableList.of(makeDistributionMetricUpdate("distributionName", "distributionNamespace", "s2", 18L, 2L, 2L, 16L, false), makeDistributionMetricUpdate("distributionName", "distributionNamespace", "s2", 18L, 2L, 2L, 16L, true)));
        DataflowMetrics dataflowMetrics = new DataflowMetrics(job, dataflowClient);
        MetricQueryResults result = dataflowMetrics.allMetrics();
        try {
            result.getDistributions().iterator().next().getCommitted();
            Assert.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString(("This runner does not currently support committed" + " metrics results. Please use 'attempted' instead.")));
        }
        MatcherAssert.assertThat(result.getDistributions(), Matchers.contains(attemptedMetricsResult("distributionNamespace", "distributionName", "myStepName", DistributionResult.create(18, 2, 2, 16))));
    }

    @Test
    public void testMultipleCounterUpdates() throws IOException {
        JobMetrics jobMetrics = new JobMetrics();
        DataflowClient dataflowClient = Mockito.mock(DataflowClient.class);
        Mockito.when(dataflowClient.getJobMetrics(DataflowMetricsTest.JOB_ID)).thenReturn(jobMetrics);
        DataflowPipelineJob job = Mockito.mock(DataflowPipelineJob.class);
        DataflowPipelineOptions options = Mockito.mock(DataflowPipelineOptions.class);
        Mockito.when(options.isStreaming()).thenReturn(false);
        Mockito.when(job.getDataflowOptions()).thenReturn(options);
        Mockito.when(job.getState()).thenReturn(RUNNING);
        job.jobId = DataflowMetricsTest.JOB_ID;
        AppliedPTransform<?, ?, ?> myStep2 = Mockito.mock(AppliedPTransform.class);
        Mockito.when(myStep2.getFullName()).thenReturn("myStepName");
        job.transformStepNames = HashBiMap.create();
        job.transformStepNames.put(myStep2, "s2");
        AppliedPTransform<?, ?, ?> myStep3 = Mockito.mock(AppliedPTransform.class);
        Mockito.when(myStep3.getFullName()).thenReturn("myStepName3");
        job.transformStepNames.put(myStep3, "s3");
        AppliedPTransform<?, ?, ?> myStep4 = Mockito.mock(AppliedPTransform.class);
        Mockito.when(myStep4.getFullName()).thenReturn("myStepName4");
        job.transformStepNames.put(myStep4, "s4");
        // The parser relies on the fact that one tentative and one committed metric update exist in
        // the job metrics results.
        jobMetrics.setMetrics(// The following counter can not have its name translated thus it won't appear.
        ImmutableList.of(makeCounterMetricUpdate("counterName", "counterNamespace", "s2", 1233L, false), makeCounterMetricUpdate("counterName", "counterNamespace", "s2", 1234L, true), makeCounterMetricUpdate("otherCounter", "otherNamespace", "s3", 12L, false), makeCounterMetricUpdate("otherCounter", "otherNamespace", "s3", 12L, true), makeCounterMetricUpdate("counterName", "otherNamespace", "s4", 1200L, false), makeCounterMetricUpdate("counterName", "otherNamespace", "s4", 1233L, true), makeCounterMetricUpdate("lostName", "otherNamespace", "s5", 1200L, false), makeCounterMetricUpdate("lostName", "otherNamespace", "s5", 1200L, true)));
        DataflowMetrics dataflowMetrics = new DataflowMetrics(job, dataflowClient);
        MetricQueryResults result = dataflowMetrics.allMetrics();
        MatcherAssert.assertThat(result.getCounters(), Matchers.containsInAnyOrder(attemptedMetricsResult("counterNamespace", "counterName", "myStepName", 1233L), attemptedMetricsResult("otherNamespace", "otherCounter", "myStepName3", 12L), attemptedMetricsResult("otherNamespace", "counterName", "myStepName4", 1200L)));
        MatcherAssert.assertThat(result.getCounters(), Matchers.containsInAnyOrder(committedMetricsResult("counterNamespace", "counterName", "myStepName", 1233L), committedMetricsResult("otherNamespace", "otherCounter", "myStepName3", 12L), committedMetricsResult("otherNamespace", "counterName", "myStepName4", 1200L)));
    }

    @Test
    public void testMultipleCounterUpdatesStreaming() throws IOException {
        JobMetrics jobMetrics = new JobMetrics();
        DataflowClient dataflowClient = Mockito.mock(DataflowClient.class);
        Mockito.when(dataflowClient.getJobMetrics(DataflowMetricsTest.JOB_ID)).thenReturn(jobMetrics);
        DataflowPipelineJob job = Mockito.mock(DataflowPipelineJob.class);
        DataflowPipelineOptions options = Mockito.mock(DataflowPipelineOptions.class);
        Mockito.when(options.isStreaming()).thenReturn(true);
        Mockito.when(job.getDataflowOptions()).thenReturn(options);
        Mockito.when(job.getState()).thenReturn(RUNNING);
        job.jobId = DataflowMetricsTest.JOB_ID;
        AppliedPTransform<?, ?, ?> myStep2 = Mockito.mock(AppliedPTransform.class);
        Mockito.when(myStep2.getFullName()).thenReturn("myStepName");
        job.transformStepNames = HashBiMap.create();
        job.transformStepNames.put(myStep2, "s2");
        AppliedPTransform<?, ?, ?> myStep3 = Mockito.mock(AppliedPTransform.class);
        Mockito.when(myStep3.getFullName()).thenReturn("myStepName3");
        job.transformStepNames.put(myStep3, "s3");
        AppliedPTransform<?, ?, ?> myStep4 = Mockito.mock(AppliedPTransform.class);
        Mockito.when(myStep4.getFullName()).thenReturn("myStepName4");
        job.transformStepNames.put(myStep4, "s4");
        // The parser relies on the fact that one tentative and one committed metric update exist in
        // the job metrics results.
        jobMetrics.setMetrics(ImmutableList.of(makeCounterMetricUpdate("counterName", "counterNamespace", "s2", 1233L, false), makeCounterMetricUpdate("counterName", "counterNamespace", "s2", 1234L, true), makeCounterMetricUpdate("otherCounter", "otherNamespace", "s3", 12L, false), makeCounterMetricUpdate("otherCounter", "otherNamespace", "s3", 12L, true), makeCounterMetricUpdate("counterName", "otherNamespace", "s4", 1200L, false), makeCounterMetricUpdate("counterName", "otherNamespace", "s4", 1233L, true)));
        DataflowMetrics dataflowMetrics = new DataflowMetrics(job, dataflowClient);
        MetricQueryResults result = dataflowMetrics.allMetrics();
        try {
            result.getCounters().iterator().next().getCommitted();
            Assert.fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException expected) {
            MatcherAssert.assertThat(expected.getMessage(), Matchers.containsString(("This runner does not currently support committed" + " metrics results. Please use 'attempted' instead.")));
        }
        MatcherAssert.assertThat(result.getCounters(), Matchers.containsInAnyOrder(attemptedMetricsResult("counterNamespace", "counterName", "myStepName", 1233L), attemptedMetricsResult("otherNamespace", "otherCounter", "myStepName3", 12L), attemptedMetricsResult("otherNamespace", "counterName", "myStepName4", 1200L)));
    }
}

