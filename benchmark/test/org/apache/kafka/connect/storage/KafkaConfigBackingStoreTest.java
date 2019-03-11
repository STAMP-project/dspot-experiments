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
package org.apache.kafka.connect.storage;


import CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import ConfigBackingStore.UpdateListener;
import ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import DistributedConfig.CONFIG_STORAGE_REPLICATION_FACTOR_CONFIG;
import DistributedConfig.CONFIG_TOPIC_CONFIG;
import DistributedConfig.GROUP_ID_CONFIG;
import DistributedConfig.INTERNAL_KEY_CONVERTER_CLASS_CONFIG;
import DistributedConfig.INTERNAL_VALUE_CONVERTER_CLASS_CONFIG;
import DistributedConfig.KEY_CONVERTER_CLASS_CONFIG;
import DistributedConfig.OFFSET_STORAGE_TOPIC_CONFIG;
import DistributedConfig.STATUS_STORAGE_TOPIC_CONFIG;
import DistributedConfig.VALUE_CONVERTER_CLASS_CONFIG;
import KafkaConfigBackingStore.CONNECTOR_CONFIGURATION_V0;
import KafkaConfigBackingStore.CONNECTOR_TASKS_COMMIT_V0;
import KafkaConfigBackingStore.TASK_CONFIGURATION_V0;
import ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import TargetState.PAUSED;
import TargetState.STARTED;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.runtime.distributed.ClusterConfigState;
import org.apache.kafka.connect.runtime.distributed.DistributedConfig;
import org.apache.kafka.connect.util.Callback;
import org.apache.kafka.connect.util.ConnectorTaskId;
import org.apache.kafka.connect.util.KafkaBasedLog;
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


@RunWith(PowerMockRunner.class)
@PrepareForTest(KafkaConfigBackingStore.class)
@PowerMockIgnore("javax.management.*")
@SuppressWarnings({ "unchecked", "deprecation" })
public class KafkaConfigBackingStoreTest {
    private static final String TOPIC = "connect-configs";

    private static final short TOPIC_REPLICATION_FACTOR = 5;

    private static final Map<String, String> DEFAULT_CONFIG_STORAGE_PROPS = new HashMap<>();

    private static final DistributedConfig DEFAULT_DISTRIBUTED_CONFIG;

    static {
        KafkaConfigBackingStoreTest.DEFAULT_CONFIG_STORAGE_PROPS.put(CONFIG_TOPIC_CONFIG, KafkaConfigBackingStoreTest.TOPIC);
        KafkaConfigBackingStoreTest.DEFAULT_CONFIG_STORAGE_PROPS.put(OFFSET_STORAGE_TOPIC_CONFIG, "connect-offsets");
        KafkaConfigBackingStoreTest.DEFAULT_CONFIG_STORAGE_PROPS.put(CONFIG_STORAGE_REPLICATION_FACTOR_CONFIG, Short.toString(KafkaConfigBackingStoreTest.TOPIC_REPLICATION_FACTOR));
        KafkaConfigBackingStoreTest.DEFAULT_CONFIG_STORAGE_PROPS.put(GROUP_ID_CONFIG, "connect");
        KafkaConfigBackingStoreTest.DEFAULT_CONFIG_STORAGE_PROPS.put(STATUS_STORAGE_TOPIC_CONFIG, "status-topic");
        KafkaConfigBackingStoreTest.DEFAULT_CONFIG_STORAGE_PROPS.put(BOOTSTRAP_SERVERS_CONFIG, "broker1:9092,broker2:9093");
        KafkaConfigBackingStoreTest.DEFAULT_CONFIG_STORAGE_PROPS.put(KEY_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
        KafkaConfigBackingStoreTest.DEFAULT_CONFIG_STORAGE_PROPS.put(VALUE_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
        KafkaConfigBackingStoreTest.DEFAULT_CONFIG_STORAGE_PROPS.put(INTERNAL_KEY_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
        KafkaConfigBackingStoreTest.DEFAULT_CONFIG_STORAGE_PROPS.put(INTERNAL_VALUE_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
        DEFAULT_DISTRIBUTED_CONFIG = new DistributedConfig(KafkaConfigBackingStoreTest.DEFAULT_CONFIG_STORAGE_PROPS);
    }

    private static final List<String> CONNECTOR_IDS = Arrays.asList("connector1", "connector2");

    private static final List<String> CONNECTOR_CONFIG_KEYS = Arrays.asList("connector-connector1", "connector-connector2");

    private static final List<String> COMMIT_TASKS_CONFIG_KEYS = Arrays.asList("commit-connector1", "commit-connector2");

    private static final List<String> TARGET_STATE_KEYS = Arrays.asList("target-state-connector1", "target-state-connector2");

    // Need a) connector with multiple tasks and b) multiple connectors
    private static final List<ConnectorTaskId> TASK_IDS = Arrays.asList(new ConnectorTaskId("connector1", 0), new ConnectorTaskId("connector1", 1), new ConnectorTaskId("connector2", 0));

    private static final List<String> TASK_CONFIG_KEYS = Arrays.asList("task-connector1-0", "task-connector1-1", "task-connector2-0");

    // Need some placeholders -- the contents don't matter here, just that they are restored properly
    private static final List<Map<String, String>> SAMPLE_CONFIGS = Arrays.asList(Collections.singletonMap("config-key-one", "config-value-one"), Collections.singletonMap("config-key-two", "config-value-two"), Collections.singletonMap("config-key-three", "config-value-three"));

    private static final List<Struct> CONNECTOR_CONFIG_STRUCTS = Arrays.asList(new Struct(KafkaConfigBackingStore.CONNECTOR_CONFIGURATION_V0).put("properties", KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0)), new Struct(KafkaConfigBackingStore.CONNECTOR_CONFIGURATION_V0).put("properties", KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(1)), new Struct(KafkaConfigBackingStore.CONNECTOR_CONFIGURATION_V0).put("properties", KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(2)));

    private static final List<Struct> TASK_CONFIG_STRUCTS = Arrays.asList(new Struct(KafkaConfigBackingStore.TASK_CONFIGURATION_V0).put("properties", KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0)), new Struct(KafkaConfigBackingStore.TASK_CONFIGURATION_V0).put("properties", KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(1)));

    private static final Struct TARGET_STATE_PAUSED = new Struct(KafkaConfigBackingStore.TARGET_STATE_V0).put("state", "PAUSED");

    private static final Struct TASKS_COMMIT_STRUCT_TWO_TASK_CONNECTOR = put("tasks", 2);

    private static final Struct TASKS_COMMIT_STRUCT_ZERO_TASK_CONNECTOR = put("tasks", 0);

    // The exact format doesn't matter here since both conversions are mocked
    private static final List<byte[]> CONFIGS_SERIALIZED = Arrays.asList("config-bytes-1".getBytes(), "config-bytes-2".getBytes(), "config-bytes-3".getBytes(), "config-bytes-4".getBytes(), "config-bytes-5".getBytes(), "config-bytes-6".getBytes(), "config-bytes-7".getBytes(), "config-bytes-8".getBytes(), "config-bytes-9".getBytes());

    @Mock
    private Converter converter;

    @Mock
    private UpdateListener configUpdateListener;

    @Mock
    KafkaBasedLog<String, byte[]> storeLog;

    private KafkaConfigBackingStore configStorage;

    private Capture<String> capturedTopic = EasyMock.newCapture();

    private Capture<Map<String, Object>> capturedProducerProps = EasyMock.newCapture();

    private Capture<Map<String, Object>> capturedConsumerProps = EasyMock.newCapture();

    private Capture<Map<String, Object>> capturedAdminProps = EasyMock.newCapture();

    private Capture<NewTopic> capturedNewTopic = EasyMock.newCapture();

    private Capture<Callback<ConsumerRecord<String, byte[]>>> capturedConsumedCallback = EasyMock.newCapture();

    private long logOffset = 0;

    @Test
    public void testStartStop() throws Exception {
        expectConfigure();
        expectStart(Collections.emptyList(), Collections.emptyMap());
        expectStop();
        PowerMock.replayAll();
        configStorage.setupAndCreateKafkaBasedLog(KafkaConfigBackingStoreTest.TOPIC, KafkaConfigBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        Assert.assertEquals(KafkaConfigBackingStoreTest.TOPIC, capturedTopic.getValue());
        Assert.assertEquals("org.apache.kafka.common.serialization.StringSerializer", capturedProducerProps.getValue().get(KEY_SERIALIZER_CLASS_CONFIG));
        Assert.assertEquals("org.apache.kafka.common.serialization.ByteArraySerializer", capturedProducerProps.getValue().get(VALUE_SERIALIZER_CLASS_CONFIG));
        Assert.assertEquals("org.apache.kafka.common.serialization.StringDeserializer", capturedConsumerProps.getValue().get(KEY_DESERIALIZER_CLASS_CONFIG));
        Assert.assertEquals("org.apache.kafka.common.serialization.ByteArrayDeserializer", capturedConsumerProps.getValue().get(VALUE_DESERIALIZER_CLASS_CONFIG));
        Assert.assertEquals(KafkaConfigBackingStoreTest.TOPIC, capturedNewTopic.getValue().name());
        Assert.assertEquals(1, capturedNewTopic.getValue().numPartitions());
        Assert.assertEquals(KafkaConfigBackingStoreTest.TOPIC_REPLICATION_FACTOR, capturedNewTopic.getValue().replicationFactor());
        configStorage.start();
        configStorage.stop();
        PowerMock.verifyAll();
    }

    @Test
    public void testPutConnectorConfig() throws Exception {
        expectConfigure();
        expectStart(Collections.emptyList(), Collections.emptyMap());
        expectConvertWriteAndRead(KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(0), CONNECTOR_CONFIGURATION_V0, KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0), "properties", KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0));
        configUpdateListener.onConnectorConfigUpdate(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0));
        EasyMock.expectLastCall();
        expectConvertWriteAndRead(KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(1), CONNECTOR_CONFIGURATION_V0, KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1), "properties", KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(1));
        configUpdateListener.onConnectorConfigUpdate(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(1));
        EasyMock.expectLastCall();
        // Config deletion
        expectConnectorRemoval(KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(1), KafkaConfigBackingStoreTest.TARGET_STATE_KEYS.get(1));
        configUpdateListener.onConnectorConfigRemove(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(1));
        EasyMock.expectLastCall();
        expectStop();
        PowerMock.replayAll();
        configStorage.setupAndCreateKafkaBasedLog(KafkaConfigBackingStoreTest.TOPIC, KafkaConfigBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        configStorage.start();
        // Null before writing
        ClusterConfigState configState = configStorage.snapshot();
        Assert.assertEquals((-1), configState.offset());
        Assert.assertNull(configState.connectorConfig(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)));
        Assert.assertNull(configState.connectorConfig(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(1)));
        // Writing should block until it is written and read back from Kafka
        configStorage.putConnectorConfig(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0), KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0));
        configState = configStorage.snapshot();
        Assert.assertEquals(1, configState.offset());
        Assert.assertEquals(KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0), configState.connectorConfig(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)));
        Assert.assertNull(configState.connectorConfig(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(1)));
        // Second should also block and all configs should still be available
        configStorage.putConnectorConfig(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(1), KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(1));
        configState = configStorage.snapshot();
        Assert.assertEquals(2, configState.offset());
        Assert.assertEquals(KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0), configState.connectorConfig(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)));
        Assert.assertEquals(KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(1), configState.connectorConfig(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(1)));
        // Deletion should remove the second one we added
        configStorage.removeConnectorConfig(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(1));
        configState = configStorage.snapshot();
        Assert.assertEquals(4, configState.offset());
        Assert.assertEquals(KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0), configState.connectorConfig(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)));
        Assert.assertNull(configState.connectorConfig(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(1)));
        Assert.assertNull(configState.targetState(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(1)));
        configStorage.stop();
        PowerMock.verifyAll();
    }

    @Test
    public void testPutTaskConfigs() throws Exception {
        expectConfigure();
        expectStart(Collections.emptyList(), Collections.emptyMap());
        // Task configs should read to end, write to the log, read to end, write root, then read to end again
        expectReadToEnd(new LinkedHashMap<>());
        expectConvertWriteRead(KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(0), TASK_CONFIGURATION_V0, KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0), "properties", KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0));
        expectConvertWriteRead(KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(1), TASK_CONFIGURATION_V0, KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1), "properties", KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(1));
        expectReadToEnd(new LinkedHashMap<String, byte[]>());
        expectConvertWriteRead(KafkaConfigBackingStoreTest.COMMIT_TASKS_CONFIG_KEYS.get(0), CONNECTOR_TASKS_COMMIT_V0, KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2), "tasks", 2);// Starts with 0 tasks, after update has 2

        // As soon as root is rewritten, we should see a callback notifying us that we reconfigured some tasks
        configUpdateListener.onTaskConfigUpdate(Arrays.asList(KafkaConfigBackingStoreTest.TASK_IDS.get(0), KafkaConfigBackingStoreTest.TASK_IDS.get(1)));
        EasyMock.expectLastCall();
        // Records to be read by consumer as it reads to the end of the log
        LinkedHashMap<String, byte[]> serializedConfigs = new LinkedHashMap<>();
        serializedConfigs.put(KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0));
        serializedConfigs.put(KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(1), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1));
        serializedConfigs.put(KafkaConfigBackingStoreTest.COMMIT_TASKS_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2));
        expectReadToEnd(serializedConfigs);
        expectStop();
        PowerMock.replayAll();
        configStorage.setupAndCreateKafkaBasedLog(KafkaConfigBackingStoreTest.TOPIC, KafkaConfigBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        configStorage.start();
        // Bootstrap as if we had already added the connector, but no tasks had been added yet
        whiteboxAddConnector(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0), KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0), Collections.emptyList());
        // Null before writing
        ClusterConfigState configState = configStorage.snapshot();
        Assert.assertEquals((-1), configState.offset());
        Assert.assertNull(configState.taskConfig(KafkaConfigBackingStoreTest.TASK_IDS.get(0)));
        Assert.assertNull(configState.taskConfig(KafkaConfigBackingStoreTest.TASK_IDS.get(1)));
        // Writing task configs should block until all the writes have been performed and the root record update
        // has completed
        List<Map<String, String>> taskConfigs = Arrays.asList(KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0), KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(1));
        configStorage.putTaskConfigs("connector1", taskConfigs);
        // Validate root config by listing all connectors and tasks
        configState = configStorage.snapshot();
        Assert.assertEquals(3, configState.offset());
        String connectorName = KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0);
        Assert.assertEquals(Arrays.asList(connectorName), new java.util.ArrayList(configState.connectors()));
        Assert.assertEquals(Arrays.asList(KafkaConfigBackingStoreTest.TASK_IDS.get(0), KafkaConfigBackingStoreTest.TASK_IDS.get(1)), configState.tasks(connectorName));
        Assert.assertEquals(KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0), configState.taskConfig(KafkaConfigBackingStoreTest.TASK_IDS.get(0)));
        Assert.assertEquals(KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(1), configState.taskConfig(KafkaConfigBackingStoreTest.TASK_IDS.get(1)));
        Assert.assertEquals(Collections.EMPTY_SET, configState.inconsistentConnectors());
        configStorage.stop();
        PowerMock.verifyAll();
    }

    @Test
    public void testPutTaskConfigsZeroTasks() throws Exception {
        expectConfigure();
        expectStart(Collections.emptyList(), Collections.emptyMap());
        // Task configs should read to end, write to the log, read to end, write root.
        expectReadToEnd(new LinkedHashMap<>());
        expectConvertWriteRead(KafkaConfigBackingStoreTest.COMMIT_TASKS_CONFIG_KEYS.get(0), CONNECTOR_TASKS_COMMIT_V0, KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0), "tasks", 0);// We have 0 tasks

        // As soon as root is rewritten, we should see a callback notifying us that we reconfigured some tasks
        configUpdateListener.onTaskConfigUpdate(Collections.<ConnectorTaskId>emptyList());
        EasyMock.expectLastCall();
        // Records to be read by consumer as it reads to the end of the log
        LinkedHashMap<String, byte[]> serializedConfigs = new LinkedHashMap<>();
        serializedConfigs.put(KafkaConfigBackingStoreTest.COMMIT_TASKS_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0));
        expectReadToEnd(serializedConfigs);
        expectStop();
        PowerMock.replayAll();
        configStorage.setupAndCreateKafkaBasedLog(KafkaConfigBackingStoreTest.TOPIC, KafkaConfigBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        configStorage.start();
        // Bootstrap as if we had already added the connector, but no tasks had been added yet
        whiteboxAddConnector(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0), KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0), Collections.emptyList());
        // Null before writing
        ClusterConfigState configState = configStorage.snapshot();
        Assert.assertEquals((-1), configState.offset());
        // Writing task configs should block until all the writes have been performed and the root record update
        // has completed
        List<Map<String, String>> taskConfigs = Collections.emptyList();
        configStorage.putTaskConfigs("connector1", taskConfigs);
        // Validate root config by listing all connectors and tasks
        configState = configStorage.snapshot();
        Assert.assertEquals(1, configState.offset());
        String connectorName = KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0);
        Assert.assertEquals(Arrays.asList(connectorName), new java.util.ArrayList(configState.connectors()));
        Assert.assertEquals(Collections.emptyList(), configState.tasks(connectorName));
        Assert.assertEquals(Collections.EMPTY_SET, configState.inconsistentConnectors());
        configStorage.stop();
        PowerMock.verifyAll();
    }

    @Test
    public void testRestoreTargetState() throws Exception {
        expectConfigure();
        List<ConsumerRecord<String, byte[]>> existingRecords = Arrays.asList(new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 2, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(1), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 3, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TARGET_STATE_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(3)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 4, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.COMMIT_TASKS_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(4)));
        LinkedHashMap<byte[], Struct> deserialized = new LinkedHashMap<>();
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0), KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(3), KafkaConfigBackingStoreTest.TARGET_STATE_PAUSED);
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(4), KafkaConfigBackingStoreTest.TASKS_COMMIT_STRUCT_TWO_TASK_CONNECTOR);
        logOffset = 5;
        expectStart(existingRecords, deserialized);
        // Shouldn't see any callbacks since this is during startup
        expectStop();
        PowerMock.replayAll();
        configStorage.setupAndCreateKafkaBasedLog(KafkaConfigBackingStoreTest.TOPIC, KafkaConfigBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        configStorage.start();
        // Should see a single connector with initial state paused
        ClusterConfigState configState = configStorage.snapshot();
        Assert.assertEquals(5, configState.offset());// Should always be next to be read, even if uncommitted

        Assert.assertEquals(Arrays.asList(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)), new java.util.ArrayList(configState.connectors()));
        Assert.assertEquals(PAUSED, configState.targetState(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)));
        configStorage.stop();
        PowerMock.verifyAll();
    }

    @Test
    public void testBackgroundUpdateTargetState() throws Exception {
        // verify that we handle target state changes correctly when they come up through the log
        expectConfigure();
        List<ConsumerRecord<String, byte[]>> existingRecords = Arrays.asList(new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 2, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(1), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 3, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.COMMIT_TASKS_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(3)));
        LinkedHashMap<byte[], Struct> deserialized = new LinkedHashMap<>();
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0), KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(3), KafkaConfigBackingStoreTest.TASKS_COMMIT_STRUCT_TWO_TASK_CONNECTOR);
        logOffset = 5;
        expectStart(existingRecords, deserialized);
        expectRead(KafkaConfigBackingStoreTest.TARGET_STATE_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0), KafkaConfigBackingStoreTest.TARGET_STATE_PAUSED);
        configUpdateListener.onConnectorTargetStateChange(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0));
        EasyMock.expectLastCall();
        expectStop();
        PowerMock.replayAll();
        configStorage.setupAndCreateKafkaBasedLog(KafkaConfigBackingStoreTest.TOPIC, KafkaConfigBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        configStorage.start();
        // Should see a single connector with initial state paused
        ClusterConfigState configState = configStorage.snapshot();
        Assert.assertEquals(STARTED, configState.targetState(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)));
        configStorage.refresh(0, TimeUnit.SECONDS);
        configStorage.stop();
        PowerMock.verifyAll();
    }

    @Test
    public void testBackgroundConnectorDeletion() throws Exception {
        // verify that we handle connector deletions correctly when they come up through the log
        expectConfigure();
        List<ConsumerRecord<String, byte[]>> existingRecords = Arrays.asList(new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 2, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(1), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 3, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.COMMIT_TASKS_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(3)));
        LinkedHashMap<byte[], Struct> deserialized = new LinkedHashMap<>();
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0), KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(3), KafkaConfigBackingStoreTest.TASKS_COMMIT_STRUCT_TWO_TASK_CONNECTOR);
        logOffset = 5;
        expectStart(existingRecords, deserialized);
        LinkedHashMap<String, byte[]> serializedData = new LinkedHashMap<>();
        serializedData.put(KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0));
        serializedData.put(KafkaConfigBackingStoreTest.TARGET_STATE_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1));
        Map<String, Struct> deserializedData = new HashMap<>();
        deserializedData.put(KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(0), null);
        deserializedData.put(KafkaConfigBackingStoreTest.TARGET_STATE_KEYS.get(0), null);
        expectRead(serializedData, deserializedData);
        configUpdateListener.onConnectorConfigRemove(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0));
        EasyMock.expectLastCall();
        expectStop();
        PowerMock.replayAll();
        configStorage.setupAndCreateKafkaBasedLog(KafkaConfigBackingStoreTest.TOPIC, KafkaConfigBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        configStorage.start();
        // Should see a single connector with initial state paused
        ClusterConfigState configState = configStorage.snapshot();
        Assert.assertEquals(STARTED, configState.targetState(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)));
        configStorage.refresh(0, TimeUnit.SECONDS);
        configStorage.stop();
        PowerMock.verifyAll();
    }

    @Test
    public void testRestoreTargetStateUnexpectedDeletion() throws Exception {
        expectConfigure();
        List<ConsumerRecord<String, byte[]>> existingRecords = Arrays.asList(new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 2, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(1), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 3, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TARGET_STATE_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(3)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 4, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.COMMIT_TASKS_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(4)));
        LinkedHashMap<byte[], Struct> deserialized = new LinkedHashMap<>();
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0), KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(3), null);
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(4), KafkaConfigBackingStoreTest.TASKS_COMMIT_STRUCT_TWO_TASK_CONNECTOR);
        logOffset = 5;
        expectStart(existingRecords, deserialized);
        // Shouldn't see any callbacks since this is during startup
        expectStop();
        PowerMock.replayAll();
        configStorage.setupAndCreateKafkaBasedLog(KafkaConfigBackingStoreTest.TOPIC, KafkaConfigBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        configStorage.start();
        // The target state deletion should reset the state to STARTED
        ClusterConfigState configState = configStorage.snapshot();
        Assert.assertEquals(5, configState.offset());// Should always be next to be read, even if uncommitted

        Assert.assertEquals(Arrays.asList(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)), new java.util.ArrayList(configState.connectors()));
        Assert.assertEquals(STARTED, configState.targetState(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)));
        configStorage.stop();
        PowerMock.verifyAll();
    }

    @Test
    public void testRestore() throws Exception {
        // Restoring data should notify only of the latest values after loading is complete. This also validates
        // that inconsistent state is ignored.
        expectConfigure();
        // Overwrite each type at least once to ensure we see the latest data after loading
        List<ConsumerRecord<String, byte[]>> existingRecords = // Connector after root update should make it through, task update shouldn't
        Arrays.asList(new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 2, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(1), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 3, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(3)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 4, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.COMMIT_TASKS_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(4)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 5, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(5)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 6, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(6)));
        LinkedHashMap<byte[], Struct> deserialized = new LinkedHashMap<>();
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0), KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(3), KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_STRUCTS.get(1));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(4), KafkaConfigBackingStoreTest.TASKS_COMMIT_STRUCT_TWO_TASK_CONNECTOR);
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(5), KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_STRUCTS.get(2));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(6), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(1));
        logOffset = 7;
        expectStart(existingRecords, deserialized);
        // Shouldn't see any callbacks since this is during startup
        expectStop();
        PowerMock.replayAll();
        configStorage.setupAndCreateKafkaBasedLog(KafkaConfigBackingStoreTest.TOPIC, KafkaConfigBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        configStorage.start();
        // Should see a single connector and its config should be the last one seen anywhere in the log
        ClusterConfigState configState = configStorage.snapshot();
        Assert.assertEquals(7, configState.offset());// Should always be next to be read, even if uncommitted

        Assert.assertEquals(Arrays.asList(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)), new java.util.ArrayList(configState.connectors()));
        Assert.assertEquals(STARTED, configState.targetState(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)));
        // CONNECTOR_CONFIG_STRUCTS[2] -> SAMPLE_CONFIGS[2]
        Assert.assertEquals(KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(2), configState.connectorConfig(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)));
        // Should see 2 tasks for that connector. Only config updates before the root key update should be reflected
        Assert.assertEquals(Arrays.asList(KafkaConfigBackingStoreTest.TASK_IDS.get(0), KafkaConfigBackingStoreTest.TASK_IDS.get(1)), configState.tasks(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)));
        // Both TASK_CONFIG_STRUCTS[0] -> SAMPLE_CONFIGS[0]
        Assert.assertEquals(KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0), configState.taskConfig(KafkaConfigBackingStoreTest.TASK_IDS.get(0)));
        Assert.assertEquals(KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0), configState.taskConfig(KafkaConfigBackingStoreTest.TASK_IDS.get(1)));
        Assert.assertEquals(Collections.EMPTY_SET, configState.inconsistentConnectors());
        configStorage.stop();
        PowerMock.verifyAll();
    }

    @Test
    public void testRestoreConnectorDeletion() throws Exception {
        // Restoring data should notify only of the latest values after loading is complete. This also validates
        // that inconsistent state is ignored.
        expectConfigure();
        // Overwrite each type at least once to ensure we see the latest data after loading
        List<ConsumerRecord<String, byte[]>> existingRecords = Arrays.asList(new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 2, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(1), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 3, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(3)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 4, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TARGET_STATE_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(4)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 5, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.COMMIT_TASKS_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(5)));
        LinkedHashMap<byte[], Struct> deserialized = new LinkedHashMap<>();
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0), KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(3), null);
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(4), null);
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(5), KafkaConfigBackingStoreTest.TASKS_COMMIT_STRUCT_TWO_TASK_CONNECTOR);
        logOffset = 6;
        expectStart(existingRecords, deserialized);
        // Shouldn't see any callbacks since this is during startup
        expectStop();
        PowerMock.replayAll();
        configStorage.setupAndCreateKafkaBasedLog(KafkaConfigBackingStoreTest.TOPIC, KafkaConfigBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        configStorage.start();
        // Should see a single connector and its config should be the last one seen anywhere in the log
        ClusterConfigState configState = configStorage.snapshot();
        Assert.assertEquals(6, configState.offset());// Should always be next to be read, even if uncommitted

        Assert.assertTrue(configState.connectors().isEmpty());
        configStorage.stop();
        PowerMock.verifyAll();
    }

    @Test
    public void testRestoreZeroTasks() throws Exception {
        // Restoring data should notify only of the latest values after loading is complete. This also validates
        // that inconsistent state is ignored.
        expectConfigure();
        // Overwrite each type at least once to ensure we see the latest data after loading
        List<ConsumerRecord<String, byte[]>> existingRecords = // Connector after root update should make it through, task update shouldn't
        Arrays.asList(new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 2, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(1), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 3, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(3)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 4, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.COMMIT_TASKS_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(4)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 5, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(5)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 6, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(6)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 7, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.COMMIT_TASKS_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(7)));
        LinkedHashMap<byte[], Struct> deserialized = new LinkedHashMap<>();
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0), KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(1), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(3), KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_STRUCTS.get(1));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(4), KafkaConfigBackingStoreTest.TASKS_COMMIT_STRUCT_TWO_TASK_CONNECTOR);
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(5), KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_STRUCTS.get(2));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(6), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(1));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(7), KafkaConfigBackingStoreTest.TASKS_COMMIT_STRUCT_ZERO_TASK_CONNECTOR);
        logOffset = 8;
        expectStart(existingRecords, deserialized);
        // Shouldn't see any callbacks since this is during startup
        expectStop();
        PowerMock.replayAll();
        configStorage.setupAndCreateKafkaBasedLog(KafkaConfigBackingStoreTest.TOPIC, KafkaConfigBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        configStorage.start();
        // Should see a single connector and its config should be the last one seen anywhere in the log
        ClusterConfigState configState = configStorage.snapshot();
        Assert.assertEquals(8, configState.offset());// Should always be next to be read, even if uncommitted

        Assert.assertEquals(Arrays.asList(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)), new java.util.ArrayList(configState.connectors()));
        // CONNECTOR_CONFIG_STRUCTS[2] -> SAMPLE_CONFIGS[2]
        Assert.assertEquals(KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(2), configState.connectorConfig(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)));
        // Should see 0 tasks for that connector.
        Assert.assertEquals(Collections.emptyList(), configState.tasks(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)));
        // Both TASK_CONFIG_STRUCTS[0] -> SAMPLE_CONFIGS[0]
        Assert.assertEquals(Collections.EMPTY_SET, configState.inconsistentConnectors());
        configStorage.stop();
        PowerMock.verifyAll();
    }

    @Test
    public void testPutTaskConfigsDoesNotResolveAllInconsistencies() throws Exception {
        // Test a case where a failure and compaction has left us in an inconsistent state when reading the log.
        // We start out by loading an initial configuration where we started to write a task update, and then
        // compaction cleaned up the earlier record.
        expectConfigure();
        List<ConsumerRecord<String, byte[]>> existingRecords = // This is the record that has been compacted:
        // new ConsumerRecord<>(TOPIC, 0, 1, TASK_CONFIG_KEYS.get(0), CONFIGS_SERIALIZED.get(1)),
        Arrays.asList(new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 2, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(1), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 4, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.COMMIT_TASKS_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(4)), new ConsumerRecord(KafkaConfigBackingStoreTest.TOPIC, 0, 5, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(5)));
        LinkedHashMap<byte[], Struct> deserialized = new LinkedHashMap<>();
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0), KafkaConfigBackingStoreTest.CONNECTOR_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(0));
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(4), KafkaConfigBackingStoreTest.TASKS_COMMIT_STRUCT_TWO_TASK_CONNECTOR);
        deserialized.put(KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(5), KafkaConfigBackingStoreTest.TASK_CONFIG_STRUCTS.get(1));
        logOffset = 6;
        expectStart(existingRecords, deserialized);
        // Successful attempt to write new task config
        expectReadToEnd(new LinkedHashMap<String, byte[]>());
        expectConvertWriteRead(KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(0), TASK_CONFIGURATION_V0, KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0), "properties", KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0));
        expectReadToEnd(new LinkedHashMap<String, byte[]>());
        expectConvertWriteRead(KafkaConfigBackingStoreTest.COMMIT_TASKS_CONFIG_KEYS.get(0), CONNECTOR_TASKS_COMMIT_V0, KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2), "tasks", 1);// Updated to just 1 task

        // As soon as root is rewritten, we should see a callback notifying us that we reconfigured some tasks
        configUpdateListener.onTaskConfigUpdate(Arrays.asList(KafkaConfigBackingStoreTest.TASK_IDS.get(0)));
        EasyMock.expectLastCall();
        // Records to be read by consumer as it reads to the end of the log
        LinkedHashMap<String, byte[]> serializedConfigs = new LinkedHashMap<>();
        serializedConfigs.put(KafkaConfigBackingStoreTest.TASK_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(0));
        serializedConfigs.put(KafkaConfigBackingStoreTest.COMMIT_TASKS_CONFIG_KEYS.get(0), KafkaConfigBackingStoreTest.CONFIGS_SERIALIZED.get(2));
        expectReadToEnd(serializedConfigs);
        expectStop();
        PowerMock.replayAll();
        configStorage.setupAndCreateKafkaBasedLog(KafkaConfigBackingStoreTest.TOPIC, KafkaConfigBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        configStorage.start();
        // After reading the log, it should have been in an inconsistent state
        ClusterConfigState configState = configStorage.snapshot();
        Assert.assertEquals(6, configState.offset());// Should always be next to be read, not last committed

        Assert.assertEquals(Arrays.asList(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)), new java.util.ArrayList(configState.connectors()));
        // Inconsistent data should leave us with no tasks listed for the connector and an entry in the inconsistent list
        Assert.assertEquals(Collections.emptyList(), configState.tasks(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)));
        // Both TASK_CONFIG_STRUCTS[0] -> SAMPLE_CONFIGS[0]
        Assert.assertNull(configState.taskConfig(KafkaConfigBackingStoreTest.TASK_IDS.get(0)));
        Assert.assertNull(configState.taskConfig(KafkaConfigBackingStoreTest.TASK_IDS.get(1)));
        Assert.assertEquals(Collections.singleton(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)), configState.inconsistentConnectors());
        // Next, issue a write that has everything that is needed and it should be accepted. Note that in this case
        // we are going to shrink the number of tasks to 1
        configStorage.putTaskConfigs("connector1", Collections.singletonList(KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0)));
        // Validate updated config
        configState = configStorage.snapshot();
        // This is only two more ahead of the last one because multiple calls fail, and so their configs are not written
        // to the topic. Only the last call with 1 task config + 1 commit actually gets written.
        Assert.assertEquals(8, configState.offset());
        Assert.assertEquals(Arrays.asList(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)), new java.util.ArrayList(configState.connectors()));
        Assert.assertEquals(Arrays.asList(KafkaConfigBackingStoreTest.TASK_IDS.get(0)), configState.tasks(KafkaConfigBackingStoreTest.CONNECTOR_IDS.get(0)));
        Assert.assertEquals(KafkaConfigBackingStoreTest.SAMPLE_CONFIGS.get(0), configState.taskConfig(KafkaConfigBackingStoreTest.TASK_IDS.get(0)));
        Assert.assertEquals(Collections.EMPTY_SET, configState.inconsistentConnectors());
        configStorage.stop();
        PowerMock.verifyAll();
    }
}

