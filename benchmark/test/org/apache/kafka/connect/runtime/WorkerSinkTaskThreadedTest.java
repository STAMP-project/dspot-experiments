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


import SinkConnector.TOPICS_CONFIG;
import TaskConfig.TASK_CLASS_CONFIG;
import TaskStatus.Listener;
import WorkerConfig.OFFSET_COMMIT_INTERVAL_MS_DEFAULT;
import WorkerConfig.OFFSET_COMMIT_TIMEOUT_MS_DEFAULT;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.runtime.isolation.PluginClassLoader;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.kafka.connect.storage.Converter;
import org.apache.kafka.connect.storage.HeaderConverter;
import org.apache.kafka.connect.util.ConnectorTaskId;
import org.apache.kafka.connect.util.ThreadedTest;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static TargetState.STARTED;
import static WorkerConfig.OFFSET_COMMIT_INTERVAL_MS_DEFAULT;


@RunWith(PowerMockRunner.class)
@PrepareForTest(WorkerSinkTask.class)
@PowerMockIgnore("javax.management.*")
public class WorkerSinkTaskThreadedTest extends ThreadedTest {
    // These are fixed to keep this code simpler. In this example we assume byte[] raw values
    // with mix of integer/string in Connect
    private static final String TOPIC = "test";

    private static final int PARTITION = 12;

    private static final int PARTITION2 = 13;

    private static final int PARTITION3 = 14;

    private static final long FIRST_OFFSET = 45;

    private static final Schema KEY_SCHEMA = Schema.INT32_SCHEMA;

    private static final int KEY = 12;

    private static final Schema VALUE_SCHEMA = Schema.STRING_SCHEMA;

    private static final String VALUE = "VALUE";

    private static final byte[] RAW_KEY = "key".getBytes();

    private static final byte[] RAW_VALUE = "value".getBytes();

    private static final TopicPartition TOPIC_PARTITION = new TopicPartition(WorkerSinkTaskThreadedTest.TOPIC, WorkerSinkTaskThreadedTest.PARTITION);

    private static final TopicPartition TOPIC_PARTITION2 = new TopicPartition(WorkerSinkTaskThreadedTest.TOPIC, WorkerSinkTaskThreadedTest.PARTITION2);

    private static final TopicPartition TOPIC_PARTITION3 = new TopicPartition(WorkerSinkTaskThreadedTest.TOPIC, WorkerSinkTaskThreadedTest.PARTITION3);

    private static final TopicPartition UNASSIGNED_TOPIC_PARTITION = new TopicPartition(WorkerSinkTaskThreadedTest.TOPIC, 200);

    private static final Map<String, String> TASK_PROPS = new HashMap<>();

    private static final long TIMESTAMP = 42L;

    private static final TimestampType TIMESTAMP_TYPE = TimestampType.CREATE_TIME;

    static {
        WorkerSinkTaskThreadedTest.TASK_PROPS.put(TOPICS_CONFIG, WorkerSinkTaskThreadedTest.TOPIC);
        WorkerSinkTaskThreadedTest.TASK_PROPS.put(TASK_CLASS_CONFIG, WorkerSinkTaskThreadedTest.TestSinkTask.class.getName());
    }

    private static final TaskConfig TASK_CONFIG = new TaskConfig(WorkerSinkTaskThreadedTest.TASK_PROPS);

    private ConnectorTaskId taskId = new ConnectorTaskId("job", 0);

    private TargetState initialState = STARTED;

    private Time time;

    private ConnectMetrics metrics;

    @Mock
    private SinkTask sinkTask;

    private Capture<WorkerSinkTaskContext> sinkTaskContext = EasyMock.newCapture();

    private WorkerConfig workerConfig;

    @Mock
    private PluginClassLoader pluginLoader;

    @Mock
    private Converter keyConverter;

    @Mock
    private Converter valueConverter;

    @Mock
    private HeaderConverter headerConverter;

    @Mock
    private TransformationChain<SinkRecord> transformationChain;

    private WorkerSinkTask workerTask;

    @Mock
    private KafkaConsumer<byte[], byte[]> consumer;

    private Capture<ConsumerRebalanceListener> rebalanceListener = EasyMock.newCapture();

    @Mock
    private Listener statusListener;

    private long recordsReturned;

    @Test
    public void testPollsInBackground() throws Exception {
        expectInitializeTask();
        expectPollInitialAssignment();
        Capture<Collection<SinkRecord>> capturedRecords = expectPolls(1L);
        expectStopTask();
        PowerMock.replayAll();
        workerTask.initialize(WorkerSinkTaskThreadedTest.TASK_CONFIG);
        workerTask.initializeAndStart();
        // First iteration initializes partition assignment
        workerTask.iteration();
        // Then we iterate to fetch data
        for (int i = 0; i < 10; i++) {
            workerTask.iteration();
        }
        workerTask.stop();
        workerTask.close();
        // Verify contents match expected values, i.e. that they were translated properly. With max
        // batch size 1 and poll returns 1 message at a time, we should have a matching # of batches
        Assert.assertEquals(10, capturedRecords.getValues().size());
        int offset = 0;
        for (Collection<SinkRecord> recs : capturedRecords.getValues()) {
            Assert.assertEquals(1, recs.size());
            for (SinkRecord rec : recs) {
                SinkRecord referenceSinkRecord = new SinkRecord(WorkerSinkTaskThreadedTest.TOPIC, WorkerSinkTaskThreadedTest.PARTITION, WorkerSinkTaskThreadedTest.KEY_SCHEMA, WorkerSinkTaskThreadedTest.KEY, WorkerSinkTaskThreadedTest.VALUE_SCHEMA, WorkerSinkTaskThreadedTest.VALUE, ((WorkerSinkTaskThreadedTest.FIRST_OFFSET) + offset), WorkerSinkTaskThreadedTest.TIMESTAMP, WorkerSinkTaskThreadedTest.TIMESTAMP_TYPE);
                Assert.assertEquals(referenceSinkRecord, rec);
                offset++;
            }
        }
        PowerMock.verifyAll();
    }

    @Test
    public void testCommit() throws Exception {
        expectInitializeTask();
        expectPollInitialAssignment();
        // Make each poll() take the offset commit interval
        Capture<Collection<SinkRecord>> capturedRecords = expectPolls(OFFSET_COMMIT_INTERVAL_MS_DEFAULT);
        expectOffsetCommit(1L, null, null, 0, true);
        expectStopTask();
        PowerMock.replayAll();
        workerTask.initialize(WorkerSinkTaskThreadedTest.TASK_CONFIG);
        workerTask.initializeAndStart();
        // Initialize partition assignment
        workerTask.iteration();
        // Fetch one record
        workerTask.iteration();
        // Trigger the commit
        workerTask.iteration();
        // Commit finishes synchronously for testing so we can check this immediately
        Assert.assertEquals(0, workerTask.commitFailures());
        workerTask.stop();
        workerTask.close();
        Assert.assertEquals(2, capturedRecords.getValues().size());
        PowerMock.verifyAll();
    }

    @Test
    public void testCommitFailure() throws Exception {
        expectInitializeTask();
        expectPollInitialAssignment();
        Capture<Collection<SinkRecord>> capturedRecords = expectPolls(OFFSET_COMMIT_INTERVAL_MS_DEFAULT);
        expectOffsetCommit(1L, new RuntimeException(), null, 0, true);
        // Should rewind to last known good positions, which in this case will be the offsets loaded during initialization
        // for all topic partitions
        consumer.seek(WorkerSinkTaskThreadedTest.TOPIC_PARTITION, WorkerSinkTaskThreadedTest.FIRST_OFFSET);
        PowerMock.expectLastCall();
        consumer.seek(WorkerSinkTaskThreadedTest.TOPIC_PARTITION2, WorkerSinkTaskThreadedTest.FIRST_OFFSET);
        PowerMock.expectLastCall();
        consumer.seek(WorkerSinkTaskThreadedTest.TOPIC_PARTITION3, WorkerSinkTaskThreadedTest.FIRST_OFFSET);
        PowerMock.expectLastCall();
        expectStopTask();
        PowerMock.replayAll();
        workerTask.initialize(WorkerSinkTaskThreadedTest.TASK_CONFIG);
        workerTask.initializeAndStart();
        // Initialize partition assignment
        workerTask.iteration();
        // Fetch some data
        workerTask.iteration();
        // Trigger the commit
        workerTask.iteration();
        Assert.assertEquals(1, workerTask.commitFailures());
        Assert.assertEquals(false, Whitebox.getInternalState(workerTask, "committing"));
        workerTask.stop();
        workerTask.close();
        PowerMock.verifyAll();
    }

    @Test
    public void testCommitSuccessFollowedByFailure() throws Exception {
        // Validate that we rewind to the correct offsets if a task's preCommit() method throws an exception
        expectInitializeTask();
        expectPollInitialAssignment();
        Capture<Collection<SinkRecord>> capturedRecords = expectPolls(OFFSET_COMMIT_INTERVAL_MS_DEFAULT);
        expectOffsetCommit(1L, null, null, 0, true);
        expectOffsetCommit(2L, new RuntimeException(), null, 0, true);
        // Should rewind to last known committed positions
        consumer.seek(WorkerSinkTaskThreadedTest.TOPIC_PARTITION, ((WorkerSinkTaskThreadedTest.FIRST_OFFSET) + 1));
        PowerMock.expectLastCall();
        consumer.seek(WorkerSinkTaskThreadedTest.TOPIC_PARTITION2, WorkerSinkTaskThreadedTest.FIRST_OFFSET);
        PowerMock.expectLastCall();
        consumer.seek(WorkerSinkTaskThreadedTest.TOPIC_PARTITION3, WorkerSinkTaskThreadedTest.FIRST_OFFSET);
        PowerMock.expectLastCall();
        expectStopTask();
        PowerMock.replayAll();
        workerTask.initialize(WorkerSinkTaskThreadedTest.TASK_CONFIG);
        workerTask.initializeAndStart();
        // Initialize partition assignment
        workerTask.iteration();
        // Fetch some data
        workerTask.iteration();
        // Trigger first commit,
        workerTask.iteration();
        // Trigger second (failing) commit
        workerTask.iteration();
        Assert.assertEquals(1, workerTask.commitFailures());
        Assert.assertEquals(false, Whitebox.getInternalState(workerTask, "committing"));
        workerTask.stop();
        workerTask.close();
        PowerMock.verifyAll();
    }

    @Test
    public void testCommitConsumerFailure() throws Exception {
        expectInitializeTask();
        expectPollInitialAssignment();
        Capture<Collection<SinkRecord>> capturedRecords = expectPolls(OFFSET_COMMIT_INTERVAL_MS_DEFAULT);
        expectOffsetCommit(1L, null, new Exception(), 0, true);
        expectStopTask();
        PowerMock.replayAll();
        workerTask.initialize(WorkerSinkTaskThreadedTest.TASK_CONFIG);
        workerTask.initializeAndStart();
        // Initialize partition assignment
        workerTask.iteration();
        // Fetch some data
        workerTask.iteration();
        // Trigger commit
        workerTask.iteration();
        // TODO Response to consistent failures?
        Assert.assertEquals(1, workerTask.commitFailures());
        Assert.assertEquals(false, Whitebox.getInternalState(workerTask, "committing"));
        workerTask.stop();
        workerTask.close();
        PowerMock.verifyAll();
    }

    @Test
    public void testCommitTimeout() throws Exception {
        expectInitializeTask();
        expectPollInitialAssignment();
        // Cut down amount of time to pass in each poll so we trigger exactly 1 offset commit
        Capture<Collection<SinkRecord>> capturedRecords = expectPolls(((OFFSET_COMMIT_INTERVAL_MS_DEFAULT) / 2));
        expectOffsetCommit(2L, null, null, OFFSET_COMMIT_TIMEOUT_MS_DEFAULT, false);
        expectStopTask();
        PowerMock.replayAll();
        workerTask.initialize(WorkerSinkTaskThreadedTest.TASK_CONFIG);
        workerTask.initializeAndStart();
        // Initialize partition assignment
        workerTask.iteration();
        // Fetch some data
        workerTask.iteration();
        workerTask.iteration();
        // Trigger the commit
        workerTask.iteration();
        // Trigger the timeout without another commit
        workerTask.iteration();
        // TODO Response to consistent failures?
        Assert.assertEquals(1, workerTask.commitFailures());
        Assert.assertEquals(false, Whitebox.getInternalState(workerTask, "committing"));
        workerTask.stop();
        workerTask.close();
        PowerMock.verifyAll();
    }

    @Test
    public void testAssignmentPauseResume() throws Exception {
        // Just validate that the calls are passed through to the consumer, and that where appropriate errors are
        // converted
        expectInitializeTask();
        expectPollInitialAssignment();
        expectOnePoll().andAnswer(new org.easymock.IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                Assert.assertEquals(new java.util.HashSet(Arrays.asList(WorkerSinkTaskThreadedTest.TOPIC_PARTITION, WorkerSinkTaskThreadedTest.TOPIC_PARTITION2, WorkerSinkTaskThreadedTest.TOPIC_PARTITION3)), sinkTaskContext.getValue().assignment());
                return null;
            }
        });
        EasyMock.expect(consumer.assignment()).andReturn(new java.util.HashSet(Arrays.asList(WorkerSinkTaskThreadedTest.TOPIC_PARTITION, WorkerSinkTaskThreadedTest.TOPIC_PARTITION2, WorkerSinkTaskThreadedTest.TOPIC_PARTITION3)));
        expectOnePoll().andAnswer(new org.easymock.IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                try {
                    sinkTaskContext.getValue().pause(WorkerSinkTaskThreadedTest.UNASSIGNED_TOPIC_PARTITION);
                    Assert.fail("Trying to pause unassigned partition should have thrown an Connect exception");
                } catch (ConnectException e) {
                    // expected
                }
                sinkTaskContext.getValue().pause(WorkerSinkTaskThreadedTest.TOPIC_PARTITION, WorkerSinkTaskThreadedTest.TOPIC_PARTITION2);
                return null;
            }
        });
        consumer.pause(Arrays.asList(WorkerSinkTaskThreadedTest.UNASSIGNED_TOPIC_PARTITION));
        PowerMock.expectLastCall().andThrow(new IllegalStateException("unassigned topic partition"));
        consumer.pause(Arrays.asList(WorkerSinkTaskThreadedTest.TOPIC_PARTITION, WorkerSinkTaskThreadedTest.TOPIC_PARTITION2));
        PowerMock.expectLastCall();
        expectOnePoll().andAnswer(new org.easymock.IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                try {
                    sinkTaskContext.getValue().resume(WorkerSinkTaskThreadedTest.UNASSIGNED_TOPIC_PARTITION);
                    Assert.fail("Trying to resume unassigned partition should have thrown an Connect exception");
                } catch (ConnectException e) {
                    // expected
                }
                sinkTaskContext.getValue().resume(WorkerSinkTaskThreadedTest.TOPIC_PARTITION, WorkerSinkTaskThreadedTest.TOPIC_PARTITION2);
                return null;
            }
        });
        consumer.resume(Arrays.asList(WorkerSinkTaskThreadedTest.UNASSIGNED_TOPIC_PARTITION));
        PowerMock.expectLastCall().andThrow(new IllegalStateException("unassigned topic partition"));
        consumer.resume(Arrays.asList(WorkerSinkTaskThreadedTest.TOPIC_PARTITION, WorkerSinkTaskThreadedTest.TOPIC_PARTITION2));
        PowerMock.expectLastCall();
        expectStopTask();
        PowerMock.replayAll();
        workerTask.initialize(WorkerSinkTaskThreadedTest.TASK_CONFIG);
        workerTask.initializeAndStart();
        workerTask.iteration();
        workerTask.iteration();
        workerTask.iteration();
        workerTask.iteration();
        workerTask.stop();
        workerTask.close();
        PowerMock.verifyAll();
    }

    @Test
    public void testRewind() throws Exception {
        expectInitializeTask();
        expectPollInitialAssignment();
        final long startOffset = 40L;
        final Map<TopicPartition, Long> offsets = new HashMap<>();
        expectOnePoll().andAnswer(new org.easymock.IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                offsets.put(WorkerSinkTaskThreadedTest.TOPIC_PARTITION, startOffset);
                sinkTaskContext.getValue().offset(offsets);
                return null;
            }
        });
        consumer.seek(WorkerSinkTaskThreadedTest.TOPIC_PARTITION, startOffset);
        EasyMock.expectLastCall();
        expectOnePoll().andAnswer(new org.easymock.IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                Map<TopicPartition, Long> offsets = sinkTaskContext.getValue().offsets();
                Assert.assertEquals(0, offsets.size());
                return null;
            }
        });
        expectStopTask();
        PowerMock.replayAll();
        workerTask.initialize(WorkerSinkTaskThreadedTest.TASK_CONFIG);
        workerTask.initializeAndStart();
        workerTask.iteration();
        workerTask.iteration();
        workerTask.iteration();
        workerTask.stop();
        workerTask.close();
        PowerMock.verifyAll();
    }

    @Test
    public void testRewindOnRebalanceDuringPoll() throws Exception {
        expectInitializeTask();
        expectPollInitialAssignment();
        expectRebalanceDuringPoll().andAnswer(new org.easymock.IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                Map<TopicPartition, Long> offsets = sinkTaskContext.getValue().offsets();
                Assert.assertEquals(0, offsets.size());
                return null;
            }
        });
        expectStopTask();
        PowerMock.replayAll();
        workerTask.initialize(WorkerSinkTaskThreadedTest.TASK_CONFIG);
        workerTask.initializeAndStart();
        workerTask.iteration();
        workerTask.iteration();
        workerTask.stop();
        workerTask.close();
        PowerMock.verifyAll();
    }

    private abstract static class TestSinkTask extends SinkTask {}
}

