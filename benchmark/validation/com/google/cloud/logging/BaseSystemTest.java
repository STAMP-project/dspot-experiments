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
package com.google.cloud.logging;


import Logging.ListOption;
import Severity.DEFAULT;
import Severity.INFO;
import Severity.WARNING;
import SinkInfo.VersionFormat.V2;
import SortingField.TIMESTAMP;
import SortingOrder.DESCENDING;
import Synchronicity.SYNC;
import com.google.api.gax.paging.Page;
import com.google.cloud.MonitoredResource;
import com.google.cloud.MonitoredResourceDescriptor;
import com.google.cloud.logging.Logging.EntryListOption;
import com.google.cloud.logging.Payload.JsonPayload;
import com.google.cloud.logging.Payload.StringPayload;
import com.google.cloud.logging.SinkInfo.Destination.DatasetDestination;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.logging.v2.LogName;
import com.google.logging.v2.ProjectLogName;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;


/**
 * A base class for system tests. This class can be extended to run system tests in different
 * environments (e.g. local emulator or remote Logging service).
 */
public abstract class BaseSystemTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public Timeout globalTimeout = Timeout.seconds(300);

    @Test
    public void testCreateGetUpdateAndDeleteSink() {
        String name = formatForTest("test-create-get-update-sink");
        SinkInfo sinkInfo = SinkInfo.newBuilder(name, DatasetDestination.of("dataset")).setFilter("severity>=ERROR").setVersionFormat(V2).build();
        Sink sink = logging().create(sinkInfo);
        Assert.assertEquals(name, sink.getName());
        Assert.assertEquals(V2, sink.getVersionFormat());
        Assert.assertEquals("severity>=ERROR", sink.getFilter());
        DatasetDestination datasetDestination = sink.getDestination();
        Assert.assertEquals(logging().getOptions().getProjectId(), datasetDestination.getProject());
        Assert.assertEquals("dataset", datasetDestination.getDataset());
        Assert.assertEquals(sink, logging().getSink(name));
        sink = sink.toBuilder().setFilter("severity<=ERROR").build().update();
        Assert.assertEquals(name, sink.getName());
        Assert.assertEquals(V2, sink.getVersionFormat());
        Assert.assertEquals("severity<=ERROR", sink.getFilter());
        Assert.assertTrue(sink.delete());
        Assert.assertFalse(sink.delete());
    }

    @Test
    public void testUpdateNonExistingSink() {
        String name = formatForTest("test-update-non-existing-sink");
        SinkInfo sinkInfo = SinkInfo.newBuilder(name, DatasetDestination.of("dataset")).setFilter("severity>=ERROR").setVersionFormat(V2).build();
        Assert.assertNull(logging().getSink(name));
        thrown.expect(LoggingException.class);
        thrown.expectMessage("NOT_FOUND");
        logging().update(sinkInfo);
    }

    @Test
    public void testListSinks() throws InterruptedException {
        String firstName = formatForTest("test-list-sinks-1");
        String secondName = formatForTest("test-list-sinks-2");
        Sink firstSink = logging().create(SinkInfo.of(firstName, DatasetDestination.of("dataset")));
        Sink secondSink = logging().create(SinkInfo.of(secondName, DatasetDestination.of("dataset")));
        Logging[] options = new ListOption[]{ ListOption.pageSize(1) };
        Page<Sink> sinkPage = logging().listSinks(options);
        Set<Sink> sinks = Sets.newHashSet(sinkPage.iterateAll());
        while ((!(sinks.contains(firstSink))) || (!(sinks.contains(secondSink)))) {
            Thread.sleep(500);
            sinks = Sets.newHashSet(logging().listSinks(options).iterateAll());
        } 
        firstSink.delete();
        secondSink.delete();
    }

    @Test
    public void testListMonitoredResourceDescriptors() {
        Iterator<MonitoredResourceDescriptor> iterator = logging().listMonitoredResourceDescriptors(ListOption.pageSize(100)).iterateAll().iterator();
        int count = 0;
        while (iterator.hasNext()) {
            Assert.assertNotNull(iterator.next().getType());
            count += 1;
        } 
        Assert.assertTrue((count > 0));
    }

    @Test
    public void testCreateGetUpdateAndDeleteMetric() {
        String name = formatForTest("test-create-get-update-metric");
        MetricInfo metricInfo = MetricInfo.newBuilder(name, "severity>=ERROR").setDescription("description").build();
        Metric metric = logging().create(metricInfo);
        Assert.assertEquals(name, metric.getName());
        Assert.assertEquals("severity>=ERROR", metric.getFilter());
        Assert.assertEquals("description", metric.getDescription());
        Assert.assertEquals(metric, logging().getMetric(name));
        metric = metric.toBuilder().setDescription("newDescription").setFilter("severity>=WARNING").build().update();
        Assert.assertEquals(name, metric.getName());
        Assert.assertEquals("severity>=WARNING", metric.getFilter());
        Assert.assertEquals("newDescription", metric.getDescription());
        Assert.assertTrue(metric.delete());
        Assert.assertFalse(metric.delete());
    }

    @Test
    public void testUpdateNonExistingMetric() {
        String name = formatForTest("test-update-non-existing-metric");
        MetricInfo metricInfo = MetricInfo.newBuilder(name, "severity>=ERROR").setDescription("description").build();
        Assert.assertNull(logging().getMetric(name));
        Metric metric = logging().update(metricInfo);
        Assert.assertEquals(name, metric.getName());
        Assert.assertEquals("severity>=ERROR", metric.getFilter());
        Assert.assertEquals("description", metric.getDescription());
        Assert.assertTrue(metric.delete());
    }

    @Test
    public void testListMetrics() throws InterruptedException {
        String firstName = formatForTest("test-list-metrics-1");
        String secondName = formatForTest("test-list-metrics-2");
        Metric firstMetric = logging().create(MetricInfo.of(firstName, "severity>=ERROR"));
        Metric secondMetric = logging().create(MetricInfo.of(secondName, "severity>=ERROR"));
        Logging[] options = new ListOption[]{ ListOption.pageSize(1) };
        Page<Metric> metricPage = logging().listMetrics(options);
        Set<Metric> metrics = Sets.newHashSet(metricPage.iterateAll());
        while ((!(metrics.contains(firstMetric))) || (!(metrics.contains(secondMetric)))) {
            Thread.sleep(500);
            metrics = Sets.newHashSet(logging().listMetrics(options).iterateAll());
        } 
        firstMetric.delete();
        secondMetric.delete();
    }

    @Test
    public void testWriteAndListLogEntries() throws InterruptedException {
        String logId = formatForTest("test-write-log-entries-log");
        LoggingOptions loggingOptions = logging().getOptions();
        LogName logName = ProjectLogName.of(loggingOptions.getProjectId(), logId);
        StringPayload firstPayload = StringPayload.of("stringPayload");
        LogEntry firstEntry = LogEntry.newBuilder(firstPayload).addLabel("key1", "value1").setLogName(logId).setHttpRequest(HttpRequest.newBuilder().setStatus(500).build()).setResource(MonitoredResource.newBuilder("global").build()).build();
        JsonPayload secondPayload = JsonPayload.of(ImmutableMap.<String, Object>of("jsonKey", "jsonValue"));
        LogEntry secondEntry = LogEntry.newBuilder(secondPayload).addLabel("key2", "value2").setLogName(logId).setOperation(Operation.of("operationId", "operationProducer")).setResource(MonitoredResource.newBuilder("cloudsql_database").build()).build();
        logging().write(ImmutableList.of(firstEntry));
        logging().write(ImmutableList.of(secondEntry));
        logging().flush();
        String filter = BaseSystemTest.createEqualityFilter("logName", logName);
        EntryListOption[] options = new EntryListOption[]{ EntryListOption.filter(filter), EntryListOption.pageSize(1) };
        Page<LogEntry> page = logging().listLogEntries(options);
        while ((Iterators.size(page.iterateAll().iterator())) < 2) {
            Thread.sleep(500);
            page = logging().listLogEntries(options);
        } 
        Iterator<LogEntry> iterator = page.iterateAll().iterator();
        Assert.assertTrue(iterator.hasNext());
        LogEntry entry = iterator.next();
        Assert.assertEquals(firstPayload, entry.getPayload());
        Assert.assertEquals(logId, entry.getLogName());
        Assert.assertEquals(ImmutableMap.of("key1", "value1"), entry.getLabels());
        Assert.assertEquals("global", entry.getResource().getType());
        Assert.assertEquals(HttpRequest.newBuilder().setStatus(500).build(), entry.getHttpRequest());
        Assert.assertEquals(DEFAULT, entry.getSeverity());
        Assert.assertNull(entry.getOperation());
        Assert.assertNotNull(entry.getInsertId());
        Assert.assertNotNull(entry.getTimestamp());
        Assert.assertTrue(iterator.hasNext());
        entry = iterator.next();
        Assert.assertEquals(secondPayload, entry.getPayload());
        Assert.assertEquals(logId, entry.getLogName());
        Assert.assertEquals(ImmutableMap.of("key2", "value2"), entry.getLabels());
        Assert.assertEquals("cloudsql_database", entry.getResource().getType());
        Assert.assertEquals(Operation.of("operationId", "operationProducer"), entry.getOperation());
        Assert.assertEquals(DEFAULT, entry.getSeverity());
        Assert.assertNull(entry.getHttpRequest());
        Assert.assertNotNull(entry.getInsertId());
        Assert.assertNotNull(entry.getTimestamp());
        options = new EntryListOption[]{ EntryListOption.filter(filter), EntryListOption.sortOrder(TIMESTAMP, DESCENDING) };
        page = logging().listLogEntries(options);
        while ((Iterators.size(page.iterateAll().iterator())) < 2) {
            Thread.sleep(500);
            page = logging().listLogEntries(options);
        } 
        iterator = page.iterateAll().iterator();
        Long lastTimestamp = iterator.next().getTimestamp();
        while (iterator.hasNext()) {
            Assert.assertTrue(((iterator.next().getTimestamp()) <= lastTimestamp));
        } 
        int deleteAttempts = 0;
        int allowedDeleteAttempts = 5;
        boolean deleted = false;
        while ((!deleted) && (deleteAttempts < allowedDeleteAttempts)) {
            Thread.sleep(1000);
            deleted = logging().deleteLog(logId);
            deleteAttempts++;
        } 
        Assert.assertTrue(deleted);
    }

    @Test
    public void testDeleteNonExistingLog() {
        String logId = formatForTest("test-delete-non-existing-log");
        Assert.assertFalse(logging().deleteLog(logId));
    }

    @Test
    public void testLoggingHandler() throws InterruptedException {
        String logId = formatForTest("test-logging-handler");
        LoggingOptions options = logging().getOptions();
        LogName logName = ProjectLogName.of(options.getProjectId(), logId);
        LoggingHandler handler = new LoggingHandler(logId, options);
        handler.setLevel(Level.INFO);
        Logger logger = Logger.getLogger(getClass().getName());
        logger.addHandler(handler);
        logger.setLevel(Level.INFO);
        logger.info("Message");
        String filter = BaseSystemTest.createEqualityFilter("logName", logName);
        Iterator<LogEntry> iterator = logging().listLogEntries(EntryListOption.filter(filter)).iterateAll().iterator();
        while (!(iterator.hasNext())) {
            Thread.sleep(500L);
            iterator = logging().listLogEntries(EntryListOption.filter(filter)).iterateAll().iterator();
        } 
        assertThat(iterator.hasNext()).isTrue();
        LogEntry entry = iterator.next();
        assertThat(((entry.getPayload()) instanceof StringPayload)).isTrue();
        assertThat(entry.<StringPayload>getPayload().getData()).contains("Message");
        assertThat(entry.getLogName()).isEqualTo(logId);
        assertThat(entry.getLabels()).containsExactly("levelName", "INFO", "levelValue", String.valueOf(Level.INFO.intValue()));
        MonitoredResource monitoredResource = new LoggingConfig(handler.getClass().getName()).getMonitoredResource(options.getProjectId());
        assertThat(entry.getResource().getType()).isEqualTo(monitoredResource.getType());
        assertThat(entry.getResource().getLabels()).containsEntry("project_id", options.getProjectId());
        assertThat(entry.getHttpRequest()).isNull();
        assertThat(entry.getSeverity()).isEqualTo(INFO);
        assertThat(entry.getOperation()).isNull();
        assertThat(entry.getInsertId()).isNotNull();
        assertThat(entry.getTimestamp()).isNotNull();
        assertThat(iterator.hasNext()).isFalse();
        logger.removeHandler(handler);
        logging().deleteLog(logId);
    }

    @Test
    public void testSyncLoggingHandler() throws InterruptedException {
        String logId = formatForTest("test-sync-logging-handler");
        LoggingOptions options = logging().getOptions();
        LogName logName = ProjectLogName.of(options.getProjectId(), logId);
        MonitoredResource resource = MonitoredResource.of("gce_instance", ImmutableMap.of("project_id", options.getProjectId(), "instance_id", "instance", "zone", "us-central1-a"));
        LoggingHandler handler = new LoggingHandler(logId, options, resource);
        handler.setLevel(Level.WARNING);
        handler.setSynchronicity(SYNC);
        Logger logger = Logger.getLogger(getClass().getName());
        logger.addHandler(handler);
        logger.setLevel(Level.WARNING);
        logger.warning("Message");
        String filter = BaseSystemTest.createEqualityFilter("logName", logName);
        Iterator<LogEntry> iterator = logging().listLogEntries(EntryListOption.filter(filter)).iterateAll().iterator();
        while (!(iterator.hasNext())) {
            Thread.sleep(500L);
            iterator = logging().listLogEntries(EntryListOption.filter(filter)).iterateAll().iterator();
        } 
        Assert.assertTrue(iterator.hasNext());
        LogEntry entry = iterator.next();
        Assert.assertTrue(((entry.getPayload()) instanceof StringPayload));
        Assert.assertTrue(entry.<StringPayload>getPayload().getData().contains("Message"));
        Assert.assertEquals(logId, entry.getLogName());
        Assert.assertEquals(ImmutableMap.of("levelName", "WARNING", "levelValue", String.valueOf(Level.WARNING.intValue())), entry.getLabels());
        Assert.assertEquals(resource, entry.getResource());
        Assert.assertNull(entry.getHttpRequest());
        Assert.assertEquals(WARNING, entry.getSeverity());
        Assert.assertNull(entry.getOperation());
        Assert.assertNotNull(entry.getInsertId());
        Assert.assertNotNull(entry.getTimestamp());
        Assert.assertFalse(iterator.hasNext());
        logger.removeHandler(handler);
        logging().deleteLog(logId);
    }
}

