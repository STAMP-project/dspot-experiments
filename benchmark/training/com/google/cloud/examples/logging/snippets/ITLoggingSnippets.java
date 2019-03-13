/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.examples.logging.snippets;


import com.google.cloud.MonitoredResourceDescriptor;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.Metric;
import com.google.cloud.logging.Sink;
import com.google.cloud.logging.testing.RemoteLoggingHelper;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;


public class ITLoggingSnippets {
    private static final String DATASET = "dataset";

    private static Logging logging;

    private static LoggingSnippets loggingSnippets;

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSink() throws InterruptedException, ExecutionException {
        String sinkName1 = RemoteLoggingHelper.formatForTest("sink_name1");
        String sinkName2 = RemoteLoggingHelper.formatForTest("sink_name2");
        Sink sink1 = ITLoggingSnippets.loggingSnippets.createSink(sinkName1, ITLoggingSnippets.DATASET);
        Sink sink2 = ITLoggingSnippets.loggingSnippets.createSinkAsync(sinkName2, ITLoggingSnippets.DATASET);
        Assert.assertNotNull(sink1);
        Assert.assertNotNull(sink2);
        sink1 = ITLoggingSnippets.loggingSnippets.getSink(sinkName1);
        sink2 = ITLoggingSnippets.loggingSnippets.getSinkAsync(sinkName2);
        Assert.assertNotNull(sink1);
        Assert.assertNotNull(sink2);
        sink1 = ITLoggingSnippets.loggingSnippets.updateSink(sinkName1, ITLoggingSnippets.DATASET);
        sink2 = ITLoggingSnippets.loggingSnippets.updateSinkAsync(sinkName2, ITLoggingSnippets.DATASET);
        Set<Sink> sinks = Sets.newHashSet(ITLoggingSnippets.loggingSnippets.listSinks().iterateAll());
        while ((!(sinks.contains(sink1))) || (!(sinks.contains(sink2)))) {
            Thread.sleep(500);
            sinks = Sets.newHashSet(ITLoggingSnippets.loggingSnippets.listSinks().iterateAll());
        } 
        sinks = Sets.newHashSet(ITLoggingSnippets.loggingSnippets.listSinksAsync().iterateAll());
        while ((!(sinks.contains(sink1))) || (!(sinks.contains(sink2)))) {
            Thread.sleep(500);
            sinks = Sets.newHashSet(ITLoggingSnippets.loggingSnippets.listSinksAsync().iterateAll());
        } 
        Assert.assertTrue(ITLoggingSnippets.loggingSnippets.deleteSink(sinkName1));
        Assert.assertTrue(ITLoggingSnippets.loggingSnippets.deleteSinkAsync(sinkName2));
    }

    @Test
    public void testMetric() throws InterruptedException, ExecutionException {
        String metricName1 = RemoteLoggingHelper.formatForTest("metric_name1");
        String metricName2 = RemoteLoggingHelper.formatForTest("metric_name2");
        Metric metric1 = ITLoggingSnippets.loggingSnippets.createMetric(metricName1);
        Metric metric2 = ITLoggingSnippets.loggingSnippets.createMetricAsync(metricName2);
        Assert.assertNotNull(metric1);
        Assert.assertNotNull(metric2);
        metric1 = ITLoggingSnippets.loggingSnippets.getMetric(metricName1);
        metric2 = ITLoggingSnippets.loggingSnippets.getMetricAsync(metricName2);
        Assert.assertNotNull(metric1);
        Assert.assertNotNull(metric2);
        metric1 = ITLoggingSnippets.loggingSnippets.updateMetric(metricName1);
        metric2 = ITLoggingSnippets.loggingSnippets.updateMetricAsync(metricName2);
        Set<Metric> metrics = Sets.newHashSet(ITLoggingSnippets.loggingSnippets.listMetrics().iterateAll());
        while ((!(metrics.contains(metric1))) || (!(metrics.contains(metric2)))) {
            Thread.sleep(500);
            metrics = Sets.newHashSet(ITLoggingSnippets.loggingSnippets.listMetrics().iterateAll());
        } 
        metrics = Sets.newHashSet(ITLoggingSnippets.loggingSnippets.listMetricsAsync().iterateAll());
        while ((!(metrics.contains(metric1))) || (!(metrics.contains(metric2)))) {
            Thread.sleep(500);
            metrics = Sets.newHashSet(ITLoggingSnippets.loggingSnippets.listMetricsAsync().iterateAll());
        } 
        Assert.assertTrue(ITLoggingSnippets.loggingSnippets.deleteMetric(metricName1));
        Assert.assertTrue(ITLoggingSnippets.loggingSnippets.deleteMetricAsync(metricName2));
    }

    @Test
    public void testMonitoredResourceDescriptor() throws InterruptedException, ExecutionException {
        Iterator<MonitoredResourceDescriptor> iterator = ITLoggingSnippets.loggingSnippets.listMonitoredResourceDescriptors().iterateAll().iterator();
        int count = 0;
        while (iterator.hasNext()) {
            Assert.assertNotNull(iterator.next().getType());
            count += 1;
        } 
        Assert.assertTrue((count > 0));
        iterator = ITLoggingSnippets.loggingSnippets.listMonitoredResourceDescriptorsAsync().iterateAll().iterator();
        count = 0;
        while (iterator.hasNext()) {
            Assert.assertNotNull(iterator.next().getType());
            count += 1;
        } 
        Assert.assertTrue((count > 0));
    }

    @Test
    public void testWriteAndListLogEntries() throws InterruptedException {
        String logName = RemoteLoggingHelper.formatForTest("log_name");
        String filter = (("logName=projects/" + (ITLoggingSnippets.logging.getOptions().getProjectId())) + "/logs/") + logName;
        ITLoggingSnippets.loggingSnippets.write(logName);
        Iterator<LogEntry> iterator = ITLoggingSnippets.loggingSnippets.listLogEntries(filter).iterateAll().iterator();
        while ((Iterators.size(iterator)) < 2) {
            Thread.sleep(500);
            iterator = ITLoggingSnippets.loggingSnippets.listLogEntries(filter).iterateAll().iterator();
        } 
        Assert.assertTrue(ITLoggingSnippets.loggingSnippets.deleteLog(logName));
    }

    @Test
    public void testWriteAndListLogEntriesAsync() throws InterruptedException, ExecutionException {
        String logName = RemoteLoggingHelper.formatForTest("log_name");
        String filter = (("logName=projects/" + (ITLoggingSnippets.logging.getOptions().getProjectId())) + "/logs/") + logName;
        ITLoggingSnippets.loggingSnippets.write(logName);
        // flush all pending asynchronous writes
        ITLoggingSnippets.logging.flush();
        Iterator<LogEntry> iterator = ITLoggingSnippets.loggingSnippets.listLogEntriesAsync(filter).iterateAll().iterator();
        while ((Iterators.size(iterator)) < 2) {
            Thread.sleep(500);
            iterator = ITLoggingSnippets.loggingSnippets.listLogEntriesAsync(filter).iterateAll().iterator();
        } 
        Assert.assertTrue(ITLoggingSnippets.loggingSnippets.deleteLogAsync(logName));
    }
}

