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
package org.apache.kafka.connect.runtime.errors;


import ConnectorConfig.ERRORS_LOG_ENABLE_CONFIG;
import ConnectorConfig.ERRORS_LOG_INCLUDE_MESSAGES_CONFIG;
import SinkConnectorConfig.DLQ_CONTEXT_HEADERS_ENABLE_CONFIG;
import SinkConnectorConfig.DLQ_TOPIC_NAME_CONFIG;
import SinkConnectorConfig.DLQ_TOPIC_REPLICATION_FACTOR_CONFIG;
import Stage.TRANSFORMATION;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.runtime.MockConnectMetrics;
import org.apache.kafka.connect.runtime.SinkConnectorConfig;
import org.apache.kafka.connect.runtime.isolation.Plugins;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.util.ConnectorTaskId;
import org.easymock.EasyMock;
import org.easymock.Mock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PowerMockIgnore("javax.management.*")
public class ErrorReporterTest {
    private static final String TOPIC = "test-topic";

    private static final String DLQ_TOPIC = "test-topic-errors";

    private static final ConnectorTaskId TASK_ID = new ConnectorTaskId("job", 0);

    @Mock
    KafkaProducer<byte[], byte[]> producer;

    @Mock
    Future<RecordMetadata> metadata;

    @Mock
    Plugins plugins;

    private ErrorHandlingMetrics errorHandlingMetrics;

    private MockConnectMetrics metrics;

    @Test(expected = NullPointerException.class)
    public void initializeDLQWithNullMetrics() {
        new DeadLetterQueueReporter(producer, config(Collections.emptyMap()), ErrorReporterTest.TASK_ID, null);
    }

    @Test
    public void testDLQConfigWithEmptyTopicName() {
        DeadLetterQueueReporter deadLetterQueueReporter = new DeadLetterQueueReporter(producer, config(Collections.emptyMap()), ErrorReporterTest.TASK_ID, errorHandlingMetrics);
        ProcessingContext context = processingContext();
        EasyMock.expect(producer.send(EasyMock.anyObject(), EasyMock.anyObject())).andThrow(new RuntimeException());
        replay(producer);
        // since topic name is empty, this method should be a NOOP.
        // if it attempts to log to the DLQ via the producer, the send mock will throw a RuntimeException.
        deadLetterQueueReporter.report(context);
    }

    @Test
    public void testDLQConfigWithValidTopicName() {
        DeadLetterQueueReporter deadLetterQueueReporter = new DeadLetterQueueReporter(producer, config(Collections.singletonMap(DLQ_TOPIC_NAME_CONFIG, ErrorReporterTest.DLQ_TOPIC)), ErrorReporterTest.TASK_ID, errorHandlingMetrics);
        ProcessingContext context = processingContext();
        EasyMock.expect(producer.send(EasyMock.anyObject(), EasyMock.anyObject())).andReturn(metadata);
        replay(producer);
        deadLetterQueueReporter.report(context);
        PowerMock.verifyAll();
    }

    @Test
    public void testReportDLQTwice() {
        DeadLetterQueueReporter deadLetterQueueReporter = new DeadLetterQueueReporter(producer, config(Collections.singletonMap(DLQ_TOPIC_NAME_CONFIG, ErrorReporterTest.DLQ_TOPIC)), ErrorReporterTest.TASK_ID, errorHandlingMetrics);
        ProcessingContext context = processingContext();
        EasyMock.expect(producer.send(EasyMock.anyObject(), EasyMock.anyObject())).andReturn(metadata).times(2);
        replay(producer);
        deadLetterQueueReporter.report(context);
        deadLetterQueueReporter.report(context);
        PowerMock.verifyAll();
    }

    @Test
    public void testLogOnDisabledLogReporter() {
        LogReporter logReporter = new LogReporter(ErrorReporterTest.TASK_ID, config(Collections.emptyMap()), errorHandlingMetrics);
        ProcessingContext context = processingContext();
        context.error(new RuntimeException());
        // reporting a context without an error should not cause any errors.
        logReporter.report(context);
        assertErrorHandlingMetricValue("total-errors-logged", 0.0);
    }

    @Test
    public void testLogOnEnabledLogReporter() {
        LogReporter logReporter = new LogReporter(ErrorReporterTest.TASK_ID, config(Collections.singletonMap(ERRORS_LOG_ENABLE_CONFIG, "true")), errorHandlingMetrics);
        ProcessingContext context = processingContext();
        context.error(new RuntimeException());
        // reporting a context without an error should not cause any errors.
        logReporter.report(context);
        assertErrorHandlingMetricValue("total-errors-logged", 1.0);
    }

    @Test
    public void testLogMessageWithNoRecords() {
        LogReporter logReporter = new LogReporter(ErrorReporterTest.TASK_ID, config(Collections.singletonMap(ERRORS_LOG_ENABLE_CONFIG, "true")), errorHandlingMetrics);
        ProcessingContext context = processingContext();
        String msg = logReporter.message(context);
        Assert.assertEquals(("Error encountered in task job-0. Executing stage 'KEY_CONVERTER' with class " + "'org.apache.kafka.connect.json.JsonConverter'."), msg);
    }

    @Test
    public void testLogMessageWithSinkRecords() {
        Map<String, String> props = new HashMap<>();
        props.put(ERRORS_LOG_ENABLE_CONFIG, "true");
        props.put(ERRORS_LOG_INCLUDE_MESSAGES_CONFIG, "true");
        LogReporter logReporter = new LogReporter(ErrorReporterTest.TASK_ID, config(props), errorHandlingMetrics);
        ProcessingContext context = processingContext();
        String msg = logReporter.message(context);
        Assert.assertEquals(("Error encountered in task job-0. Executing stage 'KEY_CONVERTER' with class " + ("'org.apache.kafka.connect.json.JsonConverter', where consumed record is {topic='test-topic', " + "partition=5, offset=100}.")), msg);
    }

    @Test
    public void testSetDLQConfigs() {
        SinkConnectorConfig configuration = config(Collections.singletonMap(DLQ_TOPIC_NAME_CONFIG, ErrorReporterTest.DLQ_TOPIC));
        Assert.assertEquals(configuration.dlqTopicName(), ErrorReporterTest.DLQ_TOPIC);
        configuration = config(Collections.singletonMap(DLQ_TOPIC_REPLICATION_FACTOR_CONFIG, "7"));
        Assert.assertEquals(configuration.dlqTopicReplicationFactor(), 7);
    }

    @Test
    public void testDlqHeaderConsumerRecord() {
        Map<String, String> props = new HashMap<>();
        props.put(DLQ_TOPIC_NAME_CONFIG, ErrorReporterTest.DLQ_TOPIC);
        props.put(DLQ_CONTEXT_HEADERS_ENABLE_CONFIG, "true");
        DeadLetterQueueReporter deadLetterQueueReporter = new DeadLetterQueueReporter(producer, config(props), ErrorReporterTest.TASK_ID, errorHandlingMetrics);
        ProcessingContext context = new ProcessingContext();
        context.consumerRecord(new org.apache.kafka.clients.consumer.ConsumerRecord("source-topic", 7, 10, "source-key".getBytes(), "source-value".getBytes()));
        context.currentContext(TRANSFORMATION, Transformation.class);
        context.error(new ConnectException("Test Exception"));
        ProducerRecord<byte[], byte[]> producerRecord = new ProducerRecord(ErrorReporterTest.DLQ_TOPIC, "source-key".getBytes(), "source-value".getBytes());
        deadLetterQueueReporter.populateContextHeaders(producerRecord, context);
        Assert.assertEquals("source-topic", headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_ORIG_TOPIC));
        Assert.assertEquals("7", headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_ORIG_PARTITION));
        Assert.assertEquals("10", headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_ORIG_OFFSET));
        Assert.assertEquals(ErrorReporterTest.TASK_ID.connector(), headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_CONNECTOR_NAME));
        Assert.assertEquals(String.valueOf(ErrorReporterTest.TASK_ID.task()), headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_TASK_ID));
        Assert.assertEquals(TRANSFORMATION.name(), headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_STAGE));
        Assert.assertEquals(Transformation.class.getName(), headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_EXECUTING_CLASS));
        Assert.assertEquals(ConnectException.class.getName(), headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_EXCEPTION));
        Assert.assertEquals("Test Exception", headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_EXCEPTION_MESSAGE));
        Assert.assertTrue(((headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_EXCEPTION_STACK_TRACE).length()) > 0));
        Assert.assertTrue(headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_EXCEPTION_STACK_TRACE).startsWith("org.apache.kafka.connect.errors.ConnectException: Test Exception"));
    }

    @Test
    public void testDlqHeaderOnNullExceptionMessage() {
        Map<String, String> props = new HashMap<>();
        props.put(DLQ_TOPIC_NAME_CONFIG, ErrorReporterTest.DLQ_TOPIC);
        props.put(DLQ_CONTEXT_HEADERS_ENABLE_CONFIG, "true");
        DeadLetterQueueReporter deadLetterQueueReporter = new DeadLetterQueueReporter(producer, config(props), ErrorReporterTest.TASK_ID, errorHandlingMetrics);
        ProcessingContext context = new ProcessingContext();
        context.consumerRecord(new org.apache.kafka.clients.consumer.ConsumerRecord("source-topic", 7, 10, "source-key".getBytes(), "source-value".getBytes()));
        context.currentContext(TRANSFORMATION, Transformation.class);
        context.error(new NullPointerException());
        ProducerRecord<byte[], byte[]> producerRecord = new ProducerRecord(ErrorReporterTest.DLQ_TOPIC, "source-key".getBytes(), "source-value".getBytes());
        deadLetterQueueReporter.populateContextHeaders(producerRecord, context);
        Assert.assertEquals("source-topic", headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_ORIG_TOPIC));
        Assert.assertEquals("7", headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_ORIG_PARTITION));
        Assert.assertEquals("10", headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_ORIG_OFFSET));
        Assert.assertEquals(ErrorReporterTest.TASK_ID.connector(), headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_CONNECTOR_NAME));
        Assert.assertEquals(String.valueOf(ErrorReporterTest.TASK_ID.task()), headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_TASK_ID));
        Assert.assertEquals(TRANSFORMATION.name(), headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_STAGE));
        Assert.assertEquals(Transformation.class.getName(), headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_EXECUTING_CLASS));
        Assert.assertEquals(NullPointerException.class.getName(), headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_EXCEPTION));
        Assert.assertNull(producerRecord.headers().lastHeader(DeadLetterQueueReporter.ERROR_HEADER_EXCEPTION_MESSAGE).value());
        Assert.assertTrue(((headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_EXCEPTION_STACK_TRACE).length()) > 0));
        Assert.assertTrue(headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_EXCEPTION_STACK_TRACE).startsWith("java.lang.NullPointerException"));
    }

    @Test
    public void testDlqHeaderIsAppended() {
        Map<String, String> props = new HashMap<>();
        props.put(DLQ_TOPIC_NAME_CONFIG, ErrorReporterTest.DLQ_TOPIC);
        props.put(DLQ_CONTEXT_HEADERS_ENABLE_CONFIG, "true");
        DeadLetterQueueReporter deadLetterQueueReporter = new DeadLetterQueueReporter(producer, config(props), ErrorReporterTest.TASK_ID, errorHandlingMetrics);
        ProcessingContext context = new ProcessingContext();
        context.consumerRecord(new org.apache.kafka.clients.consumer.ConsumerRecord("source-topic", 7, 10, "source-key".getBytes(), "source-value".getBytes()));
        context.currentContext(TRANSFORMATION, Transformation.class);
        context.error(new ConnectException("Test Exception"));
        ProducerRecord<byte[], byte[]> producerRecord = new ProducerRecord(ErrorReporterTest.DLQ_TOPIC, "source-key".getBytes(), "source-value".getBytes());
        producerRecord.headers().add(DeadLetterQueueReporter.ERROR_HEADER_ORIG_TOPIC, "dummy".getBytes());
        deadLetterQueueReporter.populateContextHeaders(producerRecord, context);
        int appearances = 0;
        for (Header header : producerRecord.headers()) {
            if (DeadLetterQueueReporter.ERROR_HEADER_ORIG_TOPIC.equalsIgnoreCase(header.key())) {
                appearances++;
            }
        }
        Assert.assertEquals("source-topic", headerValue(producerRecord, DeadLetterQueueReporter.ERROR_HEADER_ORIG_TOPIC));
        Assert.assertEquals(2, appearances);
    }
}

