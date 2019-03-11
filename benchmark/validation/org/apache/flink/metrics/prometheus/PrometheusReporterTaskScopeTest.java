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
package org.apache.flink.metrics.prometheus;


import CollectorRegistry.defaultRegistry;
import PrometheusReporter.HistogramSummaryProxy;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.flink.api.common.JobID;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.Histogram;
import org.apache.flink.metrics.Meter;
import org.apache.flink.metrics.SimpleCounter;
import org.apache.flink.metrics.util.TestHistogram;
import org.apache.flink.metrics.util.TestMeter;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.metrics.MetricRegistryImpl;
import org.apache.flink.runtime.metrics.groups.TaskMetricGroup;
import org.apache.flink.util.AbstractID;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link PrometheusReporter} that registers several instances of the same metric for different subtasks.
 */
public class PrometheusReporterTaskScopeTest {
    private static final String[] LABEL_NAMES = new String[]{ "job_id", "task_id", "task_attempt_id", "host", "task_name", "task_attempt_num", "job_name", "tm_id", "subtask_index" };

    private static final String TASK_MANAGER_HOST = "taskManagerHostName";

    private static final String TASK_MANAGER_ID = "taskManagerId";

    private static final String JOB_NAME = "jobName";

    private static final String TASK_NAME = "taskName";

    private static final int ATTEMPT_NUMBER = 0;

    private static final int SUBTASK_INDEX_1 = 0;

    private static final int SUBTASK_INDEX_2 = 1;

    private final JobID jobId = new JobID();

    private final JobVertexID taskId1 = new JobVertexID();

    private final AbstractID taskAttemptId1 = new AbstractID();

    private final String[] labelValues1 = new String[]{ jobId.toString(), taskId1.toString(), taskAttemptId1.toString(), PrometheusReporterTaskScopeTest.TASK_MANAGER_HOST, PrometheusReporterTaskScopeTest.TASK_NAME, "" + (PrometheusReporterTaskScopeTest.ATTEMPT_NUMBER), PrometheusReporterTaskScopeTest.JOB_NAME, PrometheusReporterTaskScopeTest.TASK_MANAGER_ID, "" + (PrometheusReporterTaskScopeTest.SUBTASK_INDEX_1) };

    private final JobVertexID taskId2 = new JobVertexID();

    private final AbstractID taskAttemptId2 = new AbstractID();

    private final String[] labelValues2 = new String[]{ jobId.toString(), taskId2.toString(), taskAttemptId2.toString(), PrometheusReporterTaskScopeTest.TASK_MANAGER_HOST, PrometheusReporterTaskScopeTest.TASK_NAME, "" + (PrometheusReporterTaskScopeTest.ATTEMPT_NUMBER), PrometheusReporterTaskScopeTest.JOB_NAME, PrometheusReporterTaskScopeTest.TASK_MANAGER_ID, "" + (PrometheusReporterTaskScopeTest.SUBTASK_INDEX_2) };

    private TaskMetricGroup taskMetricGroup1;

    private TaskMetricGroup taskMetricGroup2;

    private MetricRegistryImpl registry;

    private PrometheusReporter reporter;

    @Test
    public void countersCanBeAddedSeveralTimesIfTheyDifferInLabels() throws UnirestException {
        Counter counter1 = new SimpleCounter();
        counter1.inc(1);
        Counter counter2 = new SimpleCounter();
        counter2.inc(2);
        taskMetricGroup1.counter("my_counter", counter1);
        taskMetricGroup2.counter("my_counter", counter2);
        Assert.assertThat(defaultRegistry.getSampleValue("flink_taskmanager_job_task_my_counter", PrometheusReporterTaskScopeTest.LABEL_NAMES, labelValues1), Matchers.equalTo(1.0));
        Assert.assertThat(defaultRegistry.getSampleValue("flink_taskmanager_job_task_my_counter", PrometheusReporterTaskScopeTest.LABEL_NAMES, labelValues2), Matchers.equalTo(2.0));
    }

    @Test
    public void gaugesCanBeAddedSeveralTimesIfTheyDifferInLabels() throws UnirestException {
        Gauge<Integer> gauge1 = new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return 3;
            }
        };
        Gauge<Integer> gauge2 = new Gauge<Integer>() {
            @Override
            public Integer getValue() {
                return 4;
            }
        };
        taskMetricGroup1.gauge("my_gauge", gauge1);
        taskMetricGroup2.gauge("my_gauge", gauge2);
        Assert.assertThat(defaultRegistry.getSampleValue("flink_taskmanager_job_task_my_gauge", PrometheusReporterTaskScopeTest.LABEL_NAMES, labelValues1), Matchers.equalTo(3.0));
        Assert.assertThat(defaultRegistry.getSampleValue("flink_taskmanager_job_task_my_gauge", PrometheusReporterTaskScopeTest.LABEL_NAMES, labelValues2), Matchers.equalTo(4.0));
    }

    @Test
    public void metersCanBeAddedSeveralTimesIfTheyDifferInLabels() throws UnirestException {
        Meter meter = new TestMeter();
        taskMetricGroup1.meter("my_meter", meter);
        taskMetricGroup2.meter("my_meter", meter);
        Assert.assertThat(defaultRegistry.getSampleValue("flink_taskmanager_job_task_my_meter", PrometheusReporterTaskScopeTest.LABEL_NAMES, labelValues1), Matchers.equalTo(5.0));
        Assert.assertThat(defaultRegistry.getSampleValue("flink_taskmanager_job_task_my_meter", PrometheusReporterTaskScopeTest.LABEL_NAMES, labelValues2), Matchers.equalTo(5.0));
    }

    @Test
    public void histogramsCanBeAddedSeveralTimesIfTheyDifferInLabels() throws UnirestException {
        Histogram histogram = new TestHistogram();
        taskMetricGroup1.histogram("my_histogram", histogram);
        taskMetricGroup2.histogram("my_histogram", histogram);
        final String exportedMetrics = PrometheusReporterTest.pollMetrics(reporter.getPort()).getBody();
        Assert.assertThat(exportedMetrics, Matchers.containsString("subtask_index=\"0\",quantile=\"0.5\",} 0.5"));// histogram

        Assert.assertThat(exportedMetrics, Matchers.containsString("subtask_index=\"1\",quantile=\"0.5\",} 0.5"));// histogram

        final String[] labelNamesWithQuantile = addToArray(PrometheusReporterTaskScopeTest.LABEL_NAMES, "quantile");
        for (Double quantile : HistogramSummaryProxy.QUANTILES) {
            Assert.assertThat(defaultRegistry.getSampleValue("flink_taskmanager_job_task_my_histogram", labelNamesWithQuantile, addToArray(labelValues1, ("" + quantile))), Matchers.equalTo(quantile));
            Assert.assertThat(defaultRegistry.getSampleValue("flink_taskmanager_job_task_my_histogram", labelNamesWithQuantile, addToArray(labelValues2, ("" + quantile))), Matchers.equalTo(quantile));
        }
    }

    @Test
    public void removingSingleInstanceOfMetricDoesNotBreakOtherInstances() throws UnirestException {
        Counter counter1 = new SimpleCounter();
        counter1.inc(1);
        Counter counter2 = new SimpleCounter();
        counter2.inc(2);
        taskMetricGroup1.counter("my_counter", counter1);
        taskMetricGroup2.counter("my_counter", counter2);
        Assert.assertThat(defaultRegistry.getSampleValue("flink_taskmanager_job_task_my_counter", PrometheusReporterTaskScopeTest.LABEL_NAMES, labelValues1), Matchers.equalTo(1.0));
        Assert.assertThat(defaultRegistry.getSampleValue("flink_taskmanager_job_task_my_counter", PrometheusReporterTaskScopeTest.LABEL_NAMES, labelValues2), Matchers.equalTo(2.0));
        taskMetricGroup2.close();
        Assert.assertThat(defaultRegistry.getSampleValue("flink_taskmanager_job_task_my_counter", PrometheusReporterTaskScopeTest.LABEL_NAMES, labelValues1), Matchers.equalTo(1.0));
        taskMetricGroup1.close();
        Assert.assertThat(defaultRegistry.getSampleValue("flink_taskmanager_job_task_my_counter", PrometheusReporterTaskScopeTest.LABEL_NAMES, labelValues1), Matchers.nullValue());
    }
}

