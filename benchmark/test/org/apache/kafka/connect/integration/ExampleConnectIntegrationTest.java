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
import org.apache.kafka.connect.runtime.ConnectorConfig;
import org.apache.kafka.connect.runtime.SinkConnectorConfig;
import org.apache.kafka.connect.storage.StringConverter;
import org.apache.kafka.connect.util.clusters.EmbeddedConnectCluster;
import org.apache.kafka.test.IntegrationTest;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * An example integration test that demonstrates how to setup an integration test for Connect.
 * <p></p>
 * The following test configures and executes up a sink connector pipeline in a worker, produces messages into
 * the source topic-partitions, and demonstrates how to check the overall behavior of the pipeline.
 */
@Category(IntegrationTest.class)
public class ExampleConnectIntegrationTest {
    private static final Logger log = LoggerFactory.getLogger(ExampleConnectIntegrationTest.class);

    private static final int NUM_RECORDS_PRODUCED = 2000;

    private static final int NUM_TOPIC_PARTITIONS = 3;

    private static final int CONSUME_MAX_DURATION_MS = 5000;

    private static final int CONNECTOR_SETUP_DURATION_MS = 15000;

    private static final int NUM_TASKS = 3;

    private static final String CONNECTOR_NAME = "simple-conn";

    private EmbeddedConnectCluster connect;

    private ConnectorHandle connectorHandle;

    /**
     * Simple test case to configure and execute an embedded Connect cluster. The test will produce and consume
     * records, and start up a sink connector which will consume these records.
     */
    @Test
    public void testProduceConsumeConnector() throws Exception {
        // create test topic
        connect.kafka().createTopic("test-topic", ExampleConnectIntegrationTest.NUM_TOPIC_PARTITIONS);
        // setup up props for the sink connector
        Map<String, String> props = new HashMap<>();
        props.put(ConnectorConfig.CONNECTOR_CLASS_CONFIG, "MonitorableSink");
        props.put(ConnectorConfig.TASKS_MAX_CONFIG, String.valueOf(ExampleConnectIntegrationTest.NUM_TASKS));
        props.put(SinkConnectorConfig.TOPICS_CONFIG, "test-topic");
        props.put(ConnectorConfig.KEY_CONVERTER_CLASS_CONFIG, StringConverter.class.getName());
        props.put(ConnectorConfig.VALUE_CONVERTER_CLASS_CONFIG, StringConverter.class.getName());
        // expect all records to be consumed by the connector
        connectorHandle.expectedRecords(ExampleConnectIntegrationTest.NUM_RECORDS_PRODUCED);
        // start a sink connector
        connect.configureConnector(ExampleConnectIntegrationTest.CONNECTOR_NAME, props);
        TestUtils.waitForCondition(this::checkForPartitionAssignment, ExampleConnectIntegrationTest.CONNECTOR_SETUP_DURATION_MS, "Connector tasks were not assigned a partition each.");
        // produce some messages into source topic partitions
        for (int i = 0; i < (ExampleConnectIntegrationTest.NUM_RECORDS_PRODUCED); i++) {
            connect.kafka().produce("test-topic", (i % (ExampleConnectIntegrationTest.NUM_TOPIC_PARTITIONS)), "key", ("simple-message-value-" + i));
        }
        // consume all records from the source topic or fail, to ensure that they were correctly produced.
        Assert.assertEquals("Unexpected number of records consumed", ExampleConnectIntegrationTest.NUM_RECORDS_PRODUCED, connect.kafka().consume(ExampleConnectIntegrationTest.NUM_RECORDS_PRODUCED, ExampleConnectIntegrationTest.CONSUME_MAX_DURATION_MS, "test-topic").count());
        // wait for the connector tasks to consume all records.
        connectorHandle.awaitRecords(ExampleConnectIntegrationTest.CONSUME_MAX_DURATION_MS);
        // delete connector
        connect.deleteConnector(ExampleConnectIntegrationTest.CONNECTOR_NAME);
    }
}

