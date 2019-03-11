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


import ConfigDef.Importance.MEDIUM;
import ConfigDef.Type.INT;
import ConnectorConfig.ERRORS_LOG_ENABLE_CONFIG;
import ConnectorConfig.ERRORS_LOG_INCLUDE_MESSAGES_CONFIG;
import Schema.INT32_SCHEMA;
import SinkConnector.TOPICS_CONFIG;
import TaskConfig.TASK_CLASS_CONFIG;
import TaskStatus.Listener;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.RetriableException;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.runtime.errors.ErrorHandlingMetrics;
import org.apache.kafka.connect.runtime.errors.LogReporter;
import org.apache.kafka.connect.runtime.errors.RetryWithToleranceOperator;
import org.apache.kafka.connect.runtime.errors.ToleranceType;
import org.apache.kafka.connect.runtime.isolation.PluginClassLoader;
import org.apache.kafka.connect.runtime.isolation.Plugins;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.storage.HeaderConverter;
import org.apache.kafka.connect.storage.OffsetStorageReader;
import org.apache.kafka.connect.storage.OffsetStorageWriter;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.SimpleConfig;
import org.apache.kafka.connect.util.ConnectorTaskId;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static TargetState.STARTED;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ WorkerSinkTask.class, WorkerSourceTask.class })
@PowerMockIgnore("javax.management.*")
public class ErrorHandlingTaskTest {
    private static final String TOPIC = "test";

    private static final int PARTITION1 = 12;

    private static final int PARTITION2 = 13;

    private static final long FIRST_OFFSET = 45;

    @Mock
    Plugins plugins;

    private static final Map<String, String> TASK_PROPS = new HashMap<>();

    static {
        ErrorHandlingTaskTest.TASK_PROPS.put(TOPICS_CONFIG, ErrorHandlingTaskTest.TOPIC);
        ErrorHandlingTaskTest.TASK_PROPS.put(TASK_CLASS_CONFIG, ErrorHandlingTaskTest.TestSinkTask.class.getName());
    }

    public static final long OPERATOR_RETRY_TIMEOUT_MILLIS = 60000;

    public static final long OPERATOR_RETRY_MAX_DELAY_MILLIS = 5000;

    public static final ToleranceType OPERATOR_TOLERANCE_TYPE = ToleranceType.ALL;

    private static final TaskConfig TASK_CONFIG = new TaskConfig(ErrorHandlingTaskTest.TASK_PROPS);

    private ConnectorTaskId taskId = new ConnectorTaskId("job", 0);

    private TargetState initialState = STARTED;

    private Time time;

    private MockConnectMetrics metrics;

    @SuppressWarnings("unused")
    @Mock
    private SinkTask sinkTask;

    @SuppressWarnings("unused")
    @Mock
    private SourceTask sourceTask;

    private Capture<WorkerSinkTaskContext> sinkTaskContext = EasyMock.newCapture();

    private WorkerConfig workerConfig;

    @Mock
    private PluginClassLoader pluginLoader;

    @SuppressWarnings("unused")
    @Mock
    private HeaderConverter headerConverter;

    private WorkerSinkTask workerSinkTask;

    private WorkerSourceTask workerSourceTask;

    @SuppressWarnings("unused")
    @Mock
    private KafkaConsumer<byte[], byte[]> consumer;

    @SuppressWarnings("unused")
    @Mock
    private KafkaProducer<byte[], byte[]> producer;

    @Mock
    OffsetStorageReader offsetReader;

    @Mock
    OffsetStorageWriter offsetWriter;

    private Capture<ConsumerRebalanceListener> rebalanceListener = EasyMock.newCapture();

    @SuppressWarnings("unused")
    @Mock
    private Listener statusListener;

    private ErrorHandlingMetrics errorHandlingMetrics;

    @Test
    public void testErrorHandlingInSinkTasks() throws Exception {
        Map<String, String> reportProps = new HashMap<>();
        reportProps.put(ERRORS_LOG_ENABLE_CONFIG, "true");
        reportProps.put(ERRORS_LOG_INCLUDE_MESSAGES_CONFIG, "true");
        LogReporter reporter = new LogReporter(taskId, connConfig(reportProps), errorHandlingMetrics);
        RetryWithToleranceOperator retryWithToleranceOperator = operator();
        retryWithToleranceOperator.metrics(errorHandlingMetrics);
        retryWithToleranceOperator.reporters(Collections.singletonList(reporter));
        createSinkTask(initialState, retryWithToleranceOperator);
        expectInitializeTask();
        // valid json
        ConsumerRecord<byte[], byte[]> record1 = new ConsumerRecord(ErrorHandlingTaskTest.TOPIC, ErrorHandlingTaskTest.PARTITION1, ErrorHandlingTaskTest.FIRST_OFFSET, null, "{\"a\": 10}".getBytes());
        // bad json
        ConsumerRecord<byte[], byte[]> record2 = new ConsumerRecord(ErrorHandlingTaskTest.TOPIC, ErrorHandlingTaskTest.PARTITION2, ErrorHandlingTaskTest.FIRST_OFFSET, null, "{\"a\" 10}".getBytes());
        EasyMock.expect(consumer.poll(Duration.ofMillis(EasyMock.anyLong()))).andReturn(records(record1));
        EasyMock.expect(consumer.poll(Duration.ofMillis(EasyMock.anyLong()))).andReturn(records(record2));
        sinkTask.put(EasyMock.anyObject());
        EasyMock.expectLastCall().times(2);
        PowerMock.replayAll();
        workerSinkTask.initialize(ErrorHandlingTaskTest.TASK_CONFIG);
        workerSinkTask.initializeAndStart();
        workerSinkTask.iteration();
        workerSinkTask.iteration();
        // two records were consumed from Kafka
        assertSinkMetricValue("sink-record-read-total", 2.0);
        // only one was written to the task
        assertSinkMetricValue("sink-record-send-total", 1.0);
        // one record completely failed (converter issues)
        assertErrorHandlingMetricValue("total-record-errors", 1.0);
        // 2 failures in the transformation, and 1 in the converter
        assertErrorHandlingMetricValue("total-record-failures", 3.0);
        // one record completely failed (converter issues), and thus was skipped
        assertErrorHandlingMetricValue("total-records-skipped", 1.0);
        PowerMock.verifyAll();
    }

    @Test
    public void testErrorHandlingInSourceTasks() throws Exception {
        Map<String, String> reportProps = new HashMap<>();
        reportProps.put(ERRORS_LOG_ENABLE_CONFIG, "true");
        reportProps.put(ERRORS_LOG_INCLUDE_MESSAGES_CONFIG, "true");
        LogReporter reporter = new LogReporter(taskId, connConfig(reportProps), errorHandlingMetrics);
        RetryWithToleranceOperator retryWithToleranceOperator = operator();
        retryWithToleranceOperator.metrics(errorHandlingMetrics);
        retryWithToleranceOperator.reporters(Collections.singletonList(reporter));
        createSourceTask(initialState, retryWithToleranceOperator);
        // valid json
        Schema valSchema = SchemaBuilder.struct().field("val", INT32_SCHEMA).build();
        Struct struct1 = put("val", 1234);
        SourceRecord record1 = new SourceRecord(Collections.emptyMap(), Collections.emptyMap(), ErrorHandlingTaskTest.TOPIC, ErrorHandlingTaskTest.PARTITION1, valSchema, struct1);
        Struct struct2 = put("val", 6789);
        SourceRecord record2 = new SourceRecord(Collections.emptyMap(), Collections.emptyMap(), ErrorHandlingTaskTest.TOPIC, ErrorHandlingTaskTest.PARTITION1, valSchema, struct2);
        EasyMock.expect(workerSourceTask.isStopping()).andReturn(false);
        EasyMock.expect(workerSourceTask.isStopping()).andReturn(false);
        EasyMock.expect(workerSourceTask.isStopping()).andReturn(true);
        EasyMock.expect(workerSourceTask.commitOffsets()).andReturn(true);
        offsetWriter.offset(EasyMock.anyObject(), EasyMock.anyObject());
        EasyMock.expectLastCall().times(2);
        sourceTask.initialize(EasyMock.anyObject());
        EasyMock.expectLastCall();
        sourceTask.start(EasyMock.anyObject());
        EasyMock.expectLastCall();
        EasyMock.expect(sourceTask.poll()).andReturn(Collections.singletonList(record1));
        EasyMock.expect(sourceTask.poll()).andReturn(Collections.singletonList(record2));
        EasyMock.expect(producer.send(EasyMock.anyObject(), EasyMock.anyObject())).andReturn(null).times(2);
        PowerMock.replayAll();
        workerSourceTask.initialize(ErrorHandlingTaskTest.TASK_CONFIG);
        workerSourceTask.execute();
        // two records were consumed from Kafka
        assertSourceMetricValue("source-record-poll-total", 2.0);
        // only one was written to the task
        assertSourceMetricValue("source-record-write-total", 0.0);
        // one record completely failed (converter issues)
        assertErrorHandlingMetricValue("total-record-errors", 0.0);
        // 2 failures in the transformation, and 1 in the converter
        assertErrorHandlingMetricValue("total-record-failures", 4.0);
        // one record completely failed (converter issues), and thus was skipped
        assertErrorHandlingMetricValue("total-records-skipped", 0.0);
        PowerMock.verifyAll();
    }

    @Test
    public void testErrorHandlingInSourceTasksWthBadConverter() throws Exception {
        Map<String, String> reportProps = new HashMap<>();
        reportProps.put(ERRORS_LOG_ENABLE_CONFIG, "true");
        reportProps.put(ERRORS_LOG_INCLUDE_MESSAGES_CONFIG, "true");
        LogReporter reporter = new LogReporter(taskId, connConfig(reportProps), errorHandlingMetrics);
        RetryWithToleranceOperator retryWithToleranceOperator = operator();
        retryWithToleranceOperator.metrics(errorHandlingMetrics);
        retryWithToleranceOperator.reporters(Collections.singletonList(reporter));
        createSourceTask(initialState, retryWithToleranceOperator, badConverter());
        // valid json
        Schema valSchema = SchemaBuilder.struct().field("val", INT32_SCHEMA).build();
        Struct struct1 = put("val", 1234);
        SourceRecord record1 = new SourceRecord(Collections.emptyMap(), Collections.emptyMap(), ErrorHandlingTaskTest.TOPIC, ErrorHandlingTaskTest.PARTITION1, valSchema, struct1);
        Struct struct2 = put("val", 6789);
        SourceRecord record2 = new SourceRecord(Collections.emptyMap(), Collections.emptyMap(), ErrorHandlingTaskTest.TOPIC, ErrorHandlingTaskTest.PARTITION1, valSchema, struct2);
        EasyMock.expect(workerSourceTask.isStopping()).andReturn(false);
        EasyMock.expect(workerSourceTask.isStopping()).andReturn(false);
        EasyMock.expect(workerSourceTask.isStopping()).andReturn(true);
        EasyMock.expect(workerSourceTask.commitOffsets()).andReturn(true);
        offsetWriter.offset(EasyMock.anyObject(), EasyMock.anyObject());
        EasyMock.expectLastCall().times(2);
        sourceTask.initialize(EasyMock.anyObject());
        EasyMock.expectLastCall();
        sourceTask.start(EasyMock.anyObject());
        EasyMock.expectLastCall();
        EasyMock.expect(sourceTask.poll()).andReturn(Collections.singletonList(record1));
        EasyMock.expect(sourceTask.poll()).andReturn(Collections.singletonList(record2));
        EasyMock.expect(producer.send(EasyMock.anyObject(), EasyMock.anyObject())).andReturn(null).times(2);
        PowerMock.replayAll();
        workerSourceTask.initialize(ErrorHandlingTaskTest.TASK_CONFIG);
        workerSourceTask.execute();
        // two records were consumed from Kafka
        assertSourceMetricValue("source-record-poll-total", 2.0);
        // only one was written to the task
        assertSourceMetricValue("source-record-write-total", 0.0);
        // one record completely failed (converter issues)
        assertErrorHandlingMetricValue("total-record-errors", 0.0);
        // 2 failures in the transformation, and 1 in the converter
        assertErrorHandlingMetricValue("total-record-failures", 8.0);
        // one record completely failed (converter issues), and thus was skipped
        assertErrorHandlingMetricValue("total-records-skipped", 0.0);
        PowerMock.verifyAll();
    }

    private abstract static class TestSinkTask extends SinkTask {}

    static class FaultyConverter extends JsonConverter {
        private static final Logger log = LoggerFactory.getLogger(ErrorHandlingTaskTest.FaultyConverter.class);

        private int invocations = 0;

        public byte[] fromConnectData(String topic, Schema schema, Object value) {
            if (value == null) {
                return super.fromConnectData(topic, schema, null);
            }
            (invocations)++;
            if (((invocations) % 3) == 0) {
                ErrorHandlingTaskTest.FaultyConverter.log.debug("Succeeding record: {} where invocations={}", value, invocations);
                return super.fromConnectData(topic, schema, value);
            } else {
                ErrorHandlingTaskTest.FaultyConverter.log.debug("Failing record: {} at invocations={}", value, invocations);
                throw new RetriableException((("Bad invocations " + (invocations)) + " for mod 3"));
            }
        }
    }

    static class FaultyPassthrough<R extends ConnectRecord<R>> implements Transformation<R> {
        private static final Logger log = LoggerFactory.getLogger(ErrorHandlingTaskTest.FaultyPassthrough.class);

        private static final String MOD_CONFIG = "mod";

        private static final int MOD_CONFIG_DEFAULT = 3;

        public static final ConfigDef CONFIG_DEF = new ConfigDef().define(ErrorHandlingTaskTest.FaultyPassthrough.MOD_CONFIG, INT, ErrorHandlingTaskTest.FaultyPassthrough.MOD_CONFIG_DEFAULT, MEDIUM, "Pass records without failure only if timestamp % mod == 0");

        private int mod = ErrorHandlingTaskTest.FaultyPassthrough.MOD_CONFIG_DEFAULT;

        private int invocations = 0;

        @Override
        public R apply(R record) {
            (invocations)++;
            if (((invocations) % (mod)) == 0) {
                ErrorHandlingTaskTest.FaultyPassthrough.log.debug("Succeeding record: {} where invocations={}", record, invocations);
                return record;
            } else {
                ErrorHandlingTaskTest.FaultyPassthrough.log.debug("Failing record: {} at invocations={}", record, invocations);
                throw new RetriableException(((("Bad invocations " + (invocations)) + " for mod ") + (mod)));
            }
        }

        @Override
        public ConfigDef config() {
            return ErrorHandlingTaskTest.FaultyPassthrough.CONFIG_DEF;
        }

        @Override
        public void close() {
            ErrorHandlingTaskTest.FaultyPassthrough.log.info("Shutting down transform");
        }

        @Override
        public void configure(Map<String, ?> configs) {
            final SimpleConfig config = new SimpleConfig(ErrorHandlingTaskTest.FaultyPassthrough.CONFIG_DEF, configs);
            mod = Math.max(config.getInt(ErrorHandlingTaskTest.FaultyPassthrough.MOD_CONFIG), 2);
            ErrorHandlingTaskTest.FaultyPassthrough.log.info("Configuring {}. Setting mod to {}", this.getClass(), mod);
        }
    }
}

