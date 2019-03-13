/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.runtime;


import TargetState.PAUSED;
import TaskConfig.TASK_CLASS_CONFIG;
import TaskStatus.Listener;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.record.InvalidRecordException;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.runtime.WorkerSourceTask.SourceTaskMetricsGroup;
import org.apache.kafka.connect.runtime.distributed.ClusterConfigState;
import org.apache.kafka.connect.runtime.isolation.Plugins;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.source.SourceTaskContext;
import org.apache.kafka.connect.storage.Converter;
import org.apache.kafka.connect.storage.HeaderConverter;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.apache.kafka.connect.storage.OffsetStorageWriter;
import org.apache.kafka.connect.util.ConnectorTaskId;
import org.apache.kafka.connect.util.ThreadedTest;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


@PowerMockIgnore({ "javax.management.*", "org.apache.kafka.connect.runtime.isolation.*" })
@RunWith(PowerMockRunner.class)
public class WorkerSourceTaskTest extends ThreadedTest {
    private static final String TOPIC = "topic";

    private static final Map<String, byte[]> PARTITION = Collections.singletonMap("key", "partition".getBytes());

    private static final Map<String, Integer> OFFSET = Collections.singletonMap("key", 12);

    // Connect-format data
    private static final Schema KEY_SCHEMA = Schema.INT32_SCHEMA;

    private static final Integer KEY = -1;

    private static final Schema RECORD_SCHEMA = Schema.INT64_SCHEMA;

    private static final Long RECORD = 12L;

    // Serialized data. The actual format of this data doesn't matter -- we just want to see that the right version
    // is used in the right place.
    private static final byte[] SERIALIZED_KEY = "converted-key".getBytes();

    private static final byte[] SERIALIZED_RECORD = "converted-record".getBytes();

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private ConnectorTaskId taskId = new ConnectorTaskId("job", 0);

    private ConnectorTaskId taskId1 = new ConnectorTaskId("job", 1);

    private WorkerConfig config;

    private Plugins plugins;

    private MockConnectMetrics metrics;

    @Mock
    private SourceTask sourceTask;

    @Mock
    private Converter keyConverter;

    @Mock
    private Converter valueConverter;

    @Mock
    private HeaderConverter headerConverter;

    @Mock
    private TransformationChain<SourceRecord> transformationChain;

    @Mock
    private KafkaProducer<byte[], byte[]> producer;

    @Mock
    private OffsetStorageReader offsetReader;

    @Mock
    private OffsetStorageWriter offsetWriter;

    @Mock
    private ClusterConfigState clusterConfigState;

    private WorkerSourceTask workerTask;

    @Mock
    private Future<RecordMetadata> sendFuture;

    @MockStrict
    private Listener statusListener;

    private Capture<Callback> producerCallbacks;

    private static final Map<String, String> TASK_PROPS = new HashMap<>();

    static {
        WorkerSourceTaskTest.TASK_PROPS.put(TASK_CLASS_CONFIG, WorkerSourceTaskTest.TestSourceTask.class.getName());
    }

    private static final TaskConfig TASK_CONFIG = new TaskConfig(WorkerSourceTaskTest.TASK_PROPS);

    private static final List<SourceRecord> RECORDS = Arrays.asList(new SourceRecord(WorkerSourceTaskTest.PARTITION, WorkerSourceTaskTest.OFFSET, "topic", null, WorkerSourceTaskTest.KEY_SCHEMA, WorkerSourceTaskTest.KEY, WorkerSourceTaskTest.RECORD_SCHEMA, WorkerSourceTaskTest.RECORD));

    @Test
    public void testStartPaused() throws Exception {
        final CountDownLatch pauseLatch = new CountDownLatch(1);
        createWorkerTask(PAUSED);
        statusListener.onPause(taskId);
        EasyMock.expectLastCall().andAnswer(new org.easymock.IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                pauseLatch.countDown();
                return null;
            }
        });
        producer.close(EasyMock.anyObject(Duration.class));
        EasyMock.expectLastCall();
        transformationChain.close();
        EasyMock.expectLastCall();
        statusListener.onShutdown(taskId);
        EasyMock.expectLastCall();
        PowerMock.replayAll();
        workerTask.initialize(WorkerSourceTaskTest.TASK_CONFIG);
        Future<?> taskFuture = executor.submit(workerTask);
        Assert.assertTrue(pauseLatch.await(5, TimeUnit.SECONDS));
        workerTask.stop();
        Assert.assertTrue(workerTask.awaitStop(1000));
        taskFuture.get();
        PowerMock.verifyAll();
    }

    @Test
    public void testPause() throws Exception {
        createWorkerTask();
        sourceTask.initialize(EasyMock.anyObject(SourceTaskContext.class));
        EasyMock.expectLastCall();
        sourceTask.start(WorkerSourceTaskTest.TASK_PROPS);
        EasyMock.expectLastCall();
        statusListener.onStartup(taskId);
        EasyMock.expectLastCall();
        AtomicInteger count = new AtomicInteger(0);
        CountDownLatch pollLatch = expectPolls(10, count);
        // In this test, we don't flush, so nothing goes any further than the offset writer
        statusListener.onPause(taskId);
        EasyMock.expectLastCall();
        sourceTask.stop();
        EasyMock.expectLastCall();
        expectOffsetFlush(true);
        statusListener.onShutdown(taskId);
        EasyMock.expectLastCall();
        producer.close(EasyMock.anyObject(Duration.class));
        EasyMock.expectLastCall();
        transformationChain.close();
        EasyMock.expectLastCall();
        PowerMock.replayAll();
        workerTask.initialize(WorkerSourceTaskTest.TASK_CONFIG);
        Future<?> taskFuture = executor.submit(workerTask);
        Assert.assertTrue(awaitLatch(pollLatch));
        workerTask.transitionTo(PAUSED);
        int priorCount = count.get();
        Thread.sleep(100);
        // since the transition is observed asynchronously, the count could be off by one loop iteration
        Assert.assertTrue((((count.get()) - priorCount) <= 1));
        workerTask.stop();
        Assert.assertTrue(workerTask.awaitStop(1000));
        taskFuture.get();
        PowerMock.verifyAll();
    }

    @Test
    public void testPollsInBackground() throws Exception {
        createWorkerTask();
        sourceTask.initialize(EasyMock.anyObject(SourceTaskContext.class));
        EasyMock.expectLastCall();
        sourceTask.start(WorkerSourceTaskTest.TASK_PROPS);
        EasyMock.expectLastCall();
        statusListener.onStartup(taskId);
        EasyMock.expectLastCall();
        final CountDownLatch pollLatch = expectPolls(10);
        // In this test, we don't flush, so nothing goes any further than the offset writer
        sourceTask.stop();
        EasyMock.expectLastCall();
        expectOffsetFlush(true);
        statusListener.onShutdown(taskId);
        EasyMock.expectLastCall();
        producer.close(EasyMock.anyObject(Duration.class));
        EasyMock.expectLastCall();
        transformationChain.close();
        EasyMock.expectLastCall();
        PowerMock.replayAll();
        workerTask.initialize(WorkerSourceTaskTest.TASK_CONFIG);
        Future<?> taskFuture = executor.submit(workerTask);
        Assert.assertTrue(awaitLatch(pollLatch));
        workerTask.stop();
        Assert.assertTrue(workerTask.awaitStop(1000));
        taskFuture.get();
        assertPollMetrics(10);
        PowerMock.verifyAll();
    }

    @Test
    public void testFailureInPoll() throws Exception {
        createWorkerTask();
        sourceTask.initialize(EasyMock.anyObject(SourceTaskContext.class));
        EasyMock.expectLastCall();
        sourceTask.start(WorkerSourceTaskTest.TASK_PROPS);
        EasyMock.expectLastCall();
        statusListener.onStartup(taskId);
        EasyMock.expectLastCall();
        final CountDownLatch pollLatch = new CountDownLatch(1);
        final RuntimeException exception = new RuntimeException();
        EasyMock.expect(sourceTask.poll()).andAnswer(new org.easymock.IAnswer<List<SourceRecord>>() {
            @Override
            public List<SourceRecord> answer() throws Throwable {
                pollLatch.countDown();
                throw exception;
            }
        });
        statusListener.onFailure(taskId, exception);
        EasyMock.expectLastCall();
        sourceTask.stop();
        EasyMock.expectLastCall();
        expectOffsetFlush(true);
        producer.close(EasyMock.anyObject(Duration.class));
        EasyMock.expectLastCall();
        transformationChain.close();
        EasyMock.expectLastCall();
        PowerMock.replayAll();
        workerTask.initialize(WorkerSourceTaskTest.TASK_CONFIG);
        Future<?> taskFuture = executor.submit(workerTask);
        Assert.assertTrue(awaitLatch(pollLatch));
        workerTask.stop();
        Assert.assertTrue(workerTask.awaitStop(1000));
        taskFuture.get();
        assertPollMetrics(0);
        PowerMock.verifyAll();
    }

    @Test
    public void testCommit() throws Exception {
        // Test that the task commits properly when prompted
        createWorkerTask();
        sourceTask.initialize(EasyMock.anyObject(SourceTaskContext.class));
        EasyMock.expectLastCall();
        sourceTask.start(WorkerSourceTaskTest.TASK_PROPS);
        EasyMock.expectLastCall();
        statusListener.onStartup(taskId);
        EasyMock.expectLastCall();
        // We'll wait for some data, then trigger a flush
        final CountDownLatch pollLatch = expectPolls(1);
        expectOffsetFlush(true);
        sourceTask.stop();
        EasyMock.expectLastCall();
        expectOffsetFlush(true);
        statusListener.onShutdown(taskId);
        EasyMock.expectLastCall();
        producer.close(EasyMock.anyObject(Duration.class));
        EasyMock.expectLastCall();
        transformationChain.close();
        EasyMock.expectLastCall();
        PowerMock.replayAll();
        workerTask.initialize(WorkerSourceTaskTest.TASK_CONFIG);
        Future<?> taskFuture = executor.submit(workerTask);
        Assert.assertTrue(awaitLatch(pollLatch));
        Assert.assertTrue(workerTask.commitOffsets());
        workerTask.stop();
        Assert.assertTrue(workerTask.awaitStop(1000));
        taskFuture.get();
        assertPollMetrics(1);
        PowerMock.verifyAll();
    }

    @Test
    public void testCommitFailure() throws Exception {
        // Test that the task commits properly when prompted
        createWorkerTask();
        sourceTask.initialize(EasyMock.anyObject(SourceTaskContext.class));
        EasyMock.expectLastCall();
        sourceTask.start(WorkerSourceTaskTest.TASK_PROPS);
        EasyMock.expectLastCall();
        statusListener.onStartup(taskId);
        EasyMock.expectLastCall();
        // We'll wait for some data, then trigger a flush
        final CountDownLatch pollLatch = expectPolls(1);
        expectOffsetFlush(true);
        sourceTask.stop();
        EasyMock.expectLastCall();
        expectOffsetFlush(false);
        statusListener.onShutdown(taskId);
        EasyMock.expectLastCall();
        producer.close(EasyMock.anyObject(Duration.class));
        EasyMock.expectLastCall();
        transformationChain.close();
        EasyMock.expectLastCall();
        PowerMock.replayAll();
        workerTask.initialize(WorkerSourceTaskTest.TASK_CONFIG);
        Future<?> taskFuture = executor.submit(workerTask);
        Assert.assertTrue(awaitLatch(pollLatch));
        Assert.assertTrue(workerTask.commitOffsets());
        workerTask.stop();
        Assert.assertTrue(workerTask.awaitStop(1000));
        taskFuture.get();
        assertPollMetrics(1);
        PowerMock.verifyAll();
    }

    @Test
    public void testSendRecordsConvertsData() throws Exception {
        createWorkerTask();
        List<SourceRecord> records = new ArrayList<>();
        // Can just use the same record for key and value
        records.add(new SourceRecord(WorkerSourceTaskTest.PARTITION, WorkerSourceTaskTest.OFFSET, "topic", null, WorkerSourceTaskTest.KEY_SCHEMA, WorkerSourceTaskTest.KEY, WorkerSourceTaskTest.RECORD_SCHEMA, WorkerSourceTaskTest.RECORD));
        Capture<ProducerRecord<byte[], byte[]>> sent = expectSendRecordAnyTimes();
        PowerMock.replayAll();
        Whitebox.setInternalState(workerTask, "toSend", records);
        Whitebox.invokeMethod(workerTask, "sendRecords");
        Assert.assertEquals(WorkerSourceTaskTest.SERIALIZED_KEY, sent.getValue().key());
        Assert.assertEquals(WorkerSourceTaskTest.SERIALIZED_RECORD, sent.getValue().value());
        PowerMock.verifyAll();
    }

    @Test
    public void testSendRecordsPropagatesTimestamp() throws Exception {
        final Long timestamp = System.currentTimeMillis();
        createWorkerTask();
        List<SourceRecord> records = Collections.singletonList(new SourceRecord(WorkerSourceTaskTest.PARTITION, WorkerSourceTaskTest.OFFSET, "topic", null, WorkerSourceTaskTest.KEY_SCHEMA, WorkerSourceTaskTest.KEY, WorkerSourceTaskTest.RECORD_SCHEMA, WorkerSourceTaskTest.RECORD, timestamp));
        Capture<ProducerRecord<byte[], byte[]>> sent = expectSendRecordAnyTimes();
        PowerMock.replayAll();
        Whitebox.setInternalState(workerTask, "toSend", records);
        Whitebox.invokeMethod(workerTask, "sendRecords");
        Assert.assertEquals(timestamp, sent.getValue().timestamp());
        PowerMock.verifyAll();
    }

    @Test(expected = InvalidRecordException.class)
    public void testSendRecordsCorruptTimestamp() throws Exception {
        final Long timestamp = -3L;
        createWorkerTask();
        List<SourceRecord> records = Collections.singletonList(new SourceRecord(WorkerSourceTaskTest.PARTITION, WorkerSourceTaskTest.OFFSET, "topic", null, WorkerSourceTaskTest.KEY_SCHEMA, WorkerSourceTaskTest.KEY, WorkerSourceTaskTest.RECORD_SCHEMA, WorkerSourceTaskTest.RECORD, timestamp));
        Capture<ProducerRecord<byte[], byte[]>> sent = expectSendRecordAnyTimes();
        PowerMock.replayAll();
        Whitebox.setInternalState(workerTask, "toSend", records);
        Whitebox.invokeMethod(workerTask, "sendRecords");
        Assert.assertEquals(null, sent.getValue().timestamp());
        PowerMock.verifyAll();
    }

    @Test
    public void testSendRecordsNoTimestamp() throws Exception {
        final Long timestamp = -1L;
        createWorkerTask();
        List<SourceRecord> records = Collections.singletonList(new SourceRecord(WorkerSourceTaskTest.PARTITION, WorkerSourceTaskTest.OFFSET, "topic", null, WorkerSourceTaskTest.KEY_SCHEMA, WorkerSourceTaskTest.KEY, WorkerSourceTaskTest.RECORD_SCHEMA, WorkerSourceTaskTest.RECORD, timestamp));
        Capture<ProducerRecord<byte[], byte[]>> sent = expectSendRecordAnyTimes();
        PowerMock.replayAll();
        Whitebox.setInternalState(workerTask, "toSend", records);
        Whitebox.invokeMethod(workerTask, "sendRecords");
        Assert.assertEquals(null, sent.getValue().timestamp());
        PowerMock.verifyAll();
    }

    @Test
    public void testSendRecordsRetries() throws Exception {
        createWorkerTask();
        // Differentiate only by Kafka partition so we can reuse conversion expectations
        SourceRecord record1 = new SourceRecord(WorkerSourceTaskTest.PARTITION, WorkerSourceTaskTest.OFFSET, "topic", 1, WorkerSourceTaskTest.KEY_SCHEMA, WorkerSourceTaskTest.KEY, WorkerSourceTaskTest.RECORD_SCHEMA, WorkerSourceTaskTest.RECORD);
        SourceRecord record2 = new SourceRecord(WorkerSourceTaskTest.PARTITION, WorkerSourceTaskTest.OFFSET, "topic", 2, WorkerSourceTaskTest.KEY_SCHEMA, WorkerSourceTaskTest.KEY, WorkerSourceTaskTest.RECORD_SCHEMA, WorkerSourceTaskTest.RECORD);
        SourceRecord record3 = new SourceRecord(WorkerSourceTaskTest.PARTITION, WorkerSourceTaskTest.OFFSET, "topic", 3, WorkerSourceTaskTest.KEY_SCHEMA, WorkerSourceTaskTest.KEY, WorkerSourceTaskTest.RECORD_SCHEMA, WorkerSourceTaskTest.RECORD);
        // First round
        expectSendRecordOnce(false);
        // Any Producer retriable exception should work here
        expectSendRecordSyncFailure(new TimeoutException("retriable sync failure"));
        // Second round
        expectSendRecordOnce(true);
        expectSendRecordOnce(false);
        PowerMock.replayAll();
        // Try to send 3, make first pass, second fail. Should save last two
        Whitebox.setInternalState(workerTask, "toSend", Arrays.asList(record1, record2, record3));
        Whitebox.invokeMethod(workerTask, "sendRecords");
        Assert.assertEquals(true, Whitebox.getInternalState(workerTask, "lastSendFailed"));
        Assert.assertEquals(Arrays.asList(record2, record3), Whitebox.getInternalState(workerTask, "toSend"));
        // Next they all succeed
        Whitebox.invokeMethod(workerTask, "sendRecords");
        Assert.assertEquals(false, Whitebox.getInternalState(workerTask, "lastSendFailed"));
        Assert.assertNull(Whitebox.getInternalState(workerTask, "toSend"));
        PowerMock.verifyAll();
    }

    @Test
    public void testSendRecordsTaskCommitRecordFail() throws Exception {
        createWorkerTask();
        // Differentiate only by Kafka partition so we can reuse conversion expectations
        SourceRecord record1 = new SourceRecord(WorkerSourceTaskTest.PARTITION, WorkerSourceTaskTest.OFFSET, "topic", 1, WorkerSourceTaskTest.KEY_SCHEMA, WorkerSourceTaskTest.KEY, WorkerSourceTaskTest.RECORD_SCHEMA, WorkerSourceTaskTest.RECORD);
        SourceRecord record2 = new SourceRecord(WorkerSourceTaskTest.PARTITION, WorkerSourceTaskTest.OFFSET, "topic", 2, WorkerSourceTaskTest.KEY_SCHEMA, WorkerSourceTaskTest.KEY, WorkerSourceTaskTest.RECORD_SCHEMA, WorkerSourceTaskTest.RECORD);
        SourceRecord record3 = new SourceRecord(WorkerSourceTaskTest.PARTITION, WorkerSourceTaskTest.OFFSET, "topic", 3, WorkerSourceTaskTest.KEY_SCHEMA, WorkerSourceTaskTest.KEY, WorkerSourceTaskTest.RECORD_SCHEMA, WorkerSourceTaskTest.RECORD);
        // Source task commit record failure will not cause the task to abort
        expectSendRecordOnce(false);
        expectSendRecordTaskCommitRecordFail(false, false);
        expectSendRecordOnce(false);
        PowerMock.replayAll();
        Whitebox.setInternalState(workerTask, "toSend", Arrays.asList(record1, record2, record3));
        Whitebox.invokeMethod(workerTask, "sendRecords");
        Assert.assertEquals(false, Whitebox.getInternalState(workerTask, "lastSendFailed"));
        Assert.assertNull(Whitebox.getInternalState(workerTask, "toSend"));
        PowerMock.verifyAll();
    }

    @Test
    public void testSlowTaskStart() throws Exception {
        final CountDownLatch startupLatch = new CountDownLatch(1);
        final CountDownLatch finishStartupLatch = new CountDownLatch(1);
        createWorkerTask();
        sourceTask.initialize(EasyMock.anyObject(SourceTaskContext.class));
        EasyMock.expectLastCall();
        sourceTask.start(WorkerSourceTaskTest.TASK_PROPS);
        EasyMock.expectLastCall().andAnswer(new org.easymock.IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                startupLatch.countDown();
                Assert.assertTrue(awaitLatch(finishStartupLatch));
                return null;
            }
        });
        statusListener.onStartup(taskId);
        EasyMock.expectLastCall();
        sourceTask.stop();
        EasyMock.expectLastCall();
        expectOffsetFlush(true);
        statusListener.onShutdown(taskId);
        EasyMock.expectLastCall();
        producer.close(EasyMock.anyObject(Duration.class));
        EasyMock.expectLastCall();
        transformationChain.close();
        EasyMock.expectLastCall();
        PowerMock.replayAll();
        workerTask.initialize(WorkerSourceTaskTest.TASK_CONFIG);
        Future<?> workerTaskFuture = executor.submit(workerTask);
        // Stopping immediately while the other thread has work to do should result in no polling, no offset commits,
        // exiting the work thread immediately, and the stop() method will be invoked in the background thread since it
        // cannot be invoked immediately in the thread trying to stop the task.
        Assert.assertTrue(awaitLatch(startupLatch));
        workerTask.stop();
        finishStartupLatch.countDown();
        Assert.assertTrue(workerTask.awaitStop(1000));
        workerTaskFuture.get();
        PowerMock.verifyAll();
    }

    @Test
    public void testMetricsGroup() {
        SourceTaskMetricsGroup group = new SourceTaskMetricsGroup(taskId, metrics);
        SourceTaskMetricsGroup group1 = new SourceTaskMetricsGroup(taskId1, metrics);
        for (int i = 0; i != 10; ++i) {
            group.recordPoll(100, (1000 + (i * 100)));
            group.recordWrite(10);
        }
        for (int i = 0; i != 20; ++i) {
            group1.recordPoll(100, (1000 + (i * 100)));
            group1.recordWrite(10);
        }
        Assert.assertEquals(1900.0, metrics.currentMetricValueAsDouble(group.metricGroup(), "poll-batch-max-time-ms"), 0.001);
        Assert.assertEquals(1450.0, metrics.currentMetricValueAsDouble(group.metricGroup(), "poll-batch-avg-time-ms"), 0.001);
        Assert.assertEquals(33.333, metrics.currentMetricValueAsDouble(group.metricGroup(), "source-record-poll-rate"), 0.001);
        Assert.assertEquals(1000, metrics.currentMetricValueAsDouble(group.metricGroup(), "source-record-poll-total"), 0.001);
        Assert.assertEquals(3.3333, metrics.currentMetricValueAsDouble(group.metricGroup(), "source-record-write-rate"), 0.001);
        Assert.assertEquals(100, metrics.currentMetricValueAsDouble(group.metricGroup(), "source-record-write-total"), 0.001);
        Assert.assertEquals(900.0, metrics.currentMetricValueAsDouble(group.metricGroup(), "source-record-active-count"), 0.001);
        // Close the group
        group.close();
        for (MetricName metricName : group.metricGroup().metrics().metrics().keySet()) {
            // Metrics for this group should no longer exist
            Assert.assertFalse(group.metricGroup().groupId().includes(metricName));
        }
        // Sensors for this group should no longer exist
        Assert.assertNull(group.metricGroup().metrics().getSensor("sink-record-read"));
        Assert.assertNull(group.metricGroup().metrics().getSensor("sink-record-send"));
        Assert.assertNull(group.metricGroup().metrics().getSensor("sink-record-active-count"));
        Assert.assertNull(group.metricGroup().metrics().getSensor("partition-count"));
        Assert.assertNull(group.metricGroup().metrics().getSensor("offset-seq-number"));
        Assert.assertNull(group.metricGroup().metrics().getSensor("offset-commit-completion"));
        Assert.assertNull(group.metricGroup().metrics().getSensor("offset-commit-completion-skip"));
        Assert.assertNull(group.metricGroup().metrics().getSensor("put-batch-time"));
        Assert.assertEquals(2900.0, metrics.currentMetricValueAsDouble(group1.metricGroup(), "poll-batch-max-time-ms"), 0.001);
        Assert.assertEquals(1950.0, metrics.currentMetricValueAsDouble(group1.metricGroup(), "poll-batch-avg-time-ms"), 0.001);
        Assert.assertEquals(66.667, metrics.currentMetricValueAsDouble(group1.metricGroup(), "source-record-poll-rate"), 0.001);
        Assert.assertEquals(2000, metrics.currentMetricValueAsDouble(group1.metricGroup(), "source-record-poll-total"), 0.001);
        Assert.assertEquals(6.667, metrics.currentMetricValueAsDouble(group1.metricGroup(), "source-record-write-rate"), 0.001);
        Assert.assertEquals(200, metrics.currentMetricValueAsDouble(group1.metricGroup(), "source-record-write-total"), 0.001);
        Assert.assertEquals(1800.0, metrics.currentMetricValueAsDouble(group1.metricGroup(), "source-record-active-count"), 0.001);
    }

    private abstract static class TestSourceTask extends SourceTask {}
}

