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
package org.apache.kafka.connect.integration;


import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.errors.RetriableException;
import org.apache.kafka.connect.runtime.ConnectorConfig;
import org.apache.kafka.connect.runtime.SinkConnectorConfig;
import org.apache.kafka.connect.runtime.errors.DeadLetterQueueReporter;
import org.apache.kafka.connect.storage.StringConverter;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.util.clusters.EmbeddedConnectCluster;
import org.apache.kafka.test.IntegrationTest;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Integration test for the different error handling policies in Connect (namely, retry policies, skipping bad records,
 * and dead letter queues).
 */
@Category(IntegrationTest.class)
public class ErrorHandlingIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandlingIntegrationTest.class);

    private static final String DLQ_TOPIC = "my-connector-errors";

    private static final String CONNECTOR_NAME = "error-conn";

    private static final String TASK_ID = "error-conn-0";

    private static final int NUM_RECORDS_PRODUCED = 20;

    private static final int EXPECTED_CORRECT_RECORDS = 19;

    private static final int EXPECTED_INCORRECT_RECORDS = 1;

    private static final int NUM_TASKS = 1;

    private static final int CONNECTOR_SETUP_DURATION_MS = 5000;

    private static final int CONSUME_MAX_DURATION_MS = 5000;

    private EmbeddedConnectCluster connect;

    private ConnectorHandle connectorHandle;

    @Test
    public void testSkipRetryAndDLQWithHeaders() throws Exception {
        // create test topic
        connect.kafka().createTopic("test-topic");
        // setup connector config
        Map<String, String> props = new HashMap<>();
        props.put(ConnectorConfig.CONNECTOR_CLASS_CONFIG, "MonitorableSink");
        props.put(ConnectorConfig.TASKS_MAX_CONFIG, String.valueOf(ErrorHandlingIntegrationTest.NUM_TASKS));
        props.put(SinkConnectorConfig.TOPICS_CONFIG, "test-topic");
        props.put(ConnectorConfig.KEY_CONVERTER_CLASS_CONFIG, StringConverter.class.getName());
        props.put(ConnectorConfig.VALUE_CONVERTER_CLASS_CONFIG, StringConverter.class.getName());
        props.put(ConnectorConfig.TRANSFORMS_CONFIG, "failing_transform");
        props.put("transforms.failing_transform.type", ErrorHandlingIntegrationTest.FaultyPassthrough.class.getName());
        // log all errors, along with message metadata
        props.put(ConnectorConfig.ERRORS_LOG_ENABLE_CONFIG, "true");
        props.put(ConnectorConfig.ERRORS_LOG_INCLUDE_MESSAGES_CONFIG, "true");
        // produce bad messages into dead letter queue
        props.put(SinkConnectorConfig.DLQ_TOPIC_NAME_CONFIG, ErrorHandlingIntegrationTest.DLQ_TOPIC);
        props.put(SinkConnectorConfig.DLQ_CONTEXT_HEADERS_ENABLE_CONFIG, "true");
        props.put(SinkConnectorConfig.DLQ_TOPIC_REPLICATION_FACTOR_CONFIG, "1");
        // tolerate all erros
        props.put(ConnectorConfig.ERRORS_TOLERANCE_CONFIG, "all");
        // retry for up to one second
        props.put(ConnectorConfig.ERRORS_RETRY_TIMEOUT_CONFIG, "1000");
        // set expected records to successfully reach the task
        connectorHandle.taskHandle(ErrorHandlingIntegrationTest.TASK_ID).expectedRecords(ErrorHandlingIntegrationTest.EXPECTED_CORRECT_RECORDS);
        connect.configureConnector(ErrorHandlingIntegrationTest.CONNECTOR_NAME, props);
        TestUtils.waitForCondition(this::checkForPartitionAssignment, ErrorHandlingIntegrationTest.CONNECTOR_SETUP_DURATION_MS, "Connector task was not assigned a partition.");
        // produce some strings into test topic
        for (int i = 0; i < (ErrorHandlingIntegrationTest.NUM_RECORDS_PRODUCED); i++) {
            connect.kafka().produce("test-topic", ("key-" + i), ("value-" + i));
        }
        // consume all records from test topic
        ErrorHandlingIntegrationTest.log.info("Consuming records from test topic");
        int i = 0;
        for (ConsumerRecord<byte[], byte[]> rec : connect.kafka().consume(ErrorHandlingIntegrationTest.NUM_RECORDS_PRODUCED, ErrorHandlingIntegrationTest.CONSUME_MAX_DURATION_MS, "test-topic")) {
            String k = new String(rec.key());
            String v = new String(rec.value());
            ErrorHandlingIntegrationTest.log.debug("Consumed record (key='{}', value='{}') from topic {}", k, v, rec.topic());
            Assert.assertEquals("Unexpected key", k, ("key-" + i));
            Assert.assertEquals("Unexpected value", v, ("value-" + i));
            i++;
        }
        // wait for records to reach the task
        connectorHandle.taskHandle(ErrorHandlingIntegrationTest.TASK_ID).awaitRecords(ErrorHandlingIntegrationTest.CONSUME_MAX_DURATION_MS);
        // consume failed records from dead letter queue topic
        ErrorHandlingIntegrationTest.log.info("Consuming records from test topic");
        ConsumerRecords<byte[], byte[]> messages = connect.kafka().consume(ErrorHandlingIntegrationTest.EXPECTED_INCORRECT_RECORDS, ErrorHandlingIntegrationTest.CONSUME_MAX_DURATION_MS, ErrorHandlingIntegrationTest.DLQ_TOPIC);
        for (ConsumerRecord<byte[], byte[]> recs : messages) {
            ErrorHandlingIntegrationTest.log.debug("Consumed record (key={}, value={}) from dead letter queue topic {}", new String(recs.key()), new String(recs.value()), ErrorHandlingIntegrationTest.DLQ_TOPIC);
            Assert.assertTrue(((recs.headers().toArray().length) > 0));
            assertValue("test-topic", recs.headers(), DeadLetterQueueReporter.ERROR_HEADER_ORIG_TOPIC);
            assertValue(RetriableException.class.getName(), recs.headers(), DeadLetterQueueReporter.ERROR_HEADER_EXCEPTION);
            assertValue("Error when value='value-7'", recs.headers(), DeadLetterQueueReporter.ERROR_HEADER_EXCEPTION_MESSAGE);
        }
        connect.deleteConnector(ErrorHandlingIntegrationTest.CONNECTOR_NAME);
    }

    public static class FaultyPassthrough<R extends ConnectRecord<R>> implements Transformation<R> {
        static final ConfigDef CONFIG_DEF = new ConfigDef();

        /**
         * An arbitrary id which causes this transformation to fail with a {@link RetriableException}, but succeeds
         * on subsequent attempt.
         */
        static final int BAD_RECORD_VAL_RETRIABLE = 4;

        /**
         * An arbitrary id which causes this transformation to fail with a {@link RetriableException}.
         */
        static final int BAD_RECORD_VAL = 7;

        private boolean shouldFail = true;

        @Override
        public R apply(R record) {
            String badValRetriable = "value-" + (ErrorHandlingIntegrationTest.FaultyPassthrough.BAD_RECORD_VAL_RETRIABLE);
            if ((badValRetriable.equals(value())) && (shouldFail)) {
                shouldFail = false;
                throw new RetriableException((("Error when value='" + badValRetriable) + "'. A reattempt with this record will succeed."));
            }
            String badVal = "value-" + (ErrorHandlingIntegrationTest.FaultyPassthrough.BAD_RECORD_VAL);
            if (badVal.equals(value())) {
                throw new RetriableException((("Error when value='" + badVal) + "'"));
            }
            return record;
        }

        @Override
        public ConfigDef config() {
            return ErrorHandlingIntegrationTest.FaultyPassthrough.CONFIG_DEF;
        }

        @Override
        public void close() {
        }

        @Override
        public void configure(Map<String, ?> configs) {
        }
    }
}

