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


import AbstractStatus.State;
import ConfigDef.Importance.LOW;
import ConfigDef.Type.STRING;
import ConnectorConfig.COMMON_GROUP;
import ConnectorConfig.CONNECTOR_CLASS_CONFIG;
import ConnectorConfig.ERROR_GROUP;
import ConnectorConfig.NAME_CONFIG;
import ConnectorConfig.TASKS_MAX_CONFIG;
import ConnectorConfig.TRANSFORMS_CONFIG;
import ConnectorConfig.TRANSFORMS_GROUP;
import ConnectorStateInfo.TaskState;
import ConnectorType.SOURCE;
import SinkConnectorConfig.TOPICS_CONFIG;
import SinkConnectorConfig.TOPICS_REGEX_CONFIG;
import TargetState.STARTED;
import TaskConfig.TASK_CLASS_CONFIG;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.runtime.distributed.ClusterConfigState;
import org.apache.kafka.connect.runtime.isolation.PluginDesc;
import org.apache.kafka.connect.runtime.isolation.Plugins;
import org.apache.kafka.connect.runtime.rest.entities.ConfigInfos;
import org.apache.kafka.connect.runtime.rest.entities.ConnectorStateInfo;
import org.apache.kafka.connect.runtime.rest.errors.BadRequestException;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.storage.ConfigBackingStore;
import org.apache.kafka.connect.storage.StatusBackingStore;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.util.ConnectorTaskId;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.annotation.MockStrict;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static ConnectorConfig.TRANSFORMS_CONFIG;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractHerder.class })
public class AbstractHerderTest {
    private static final String CONN1 = "sourceA";

    private static final ConnectorTaskId TASK0 = new ConnectorTaskId(AbstractHerderTest.CONN1, 0);

    private static final ConnectorTaskId TASK1 = new ConnectorTaskId(AbstractHerderTest.CONN1, 1);

    private static final ConnectorTaskId TASK2 = new ConnectorTaskId(AbstractHerderTest.CONN1, 2);

    private static final Integer MAX_TASKS = 3;

    private static final Map<String, String> CONN1_CONFIG = new HashMap<>();

    private static final String TEST_KEY = "testKey";

    private static final String TEST_KEY2 = "testKey2";

    private static final String TEST_KEY3 = "testKey3";

    private static final String TEST_VAL = "testVal";

    private static final String TEST_VAL2 = "testVal2";

    private static final String TEST_REF = "${file:/tmp/somefile.txt:somevar}";

    private static final String TEST_REF2 = "${file:/tmp/somefile2.txt:somevar2}";

    private static final String TEST_REF3 = "${file:/tmp/somefile3.txt:somevar3}";

    static {
        AbstractHerderTest.CONN1_CONFIG.put(NAME_CONFIG, AbstractHerderTest.CONN1);
        AbstractHerderTest.CONN1_CONFIG.put(TASKS_MAX_CONFIG, AbstractHerderTest.MAX_TASKS.toString());
        AbstractHerderTest.CONN1_CONFIG.put(TOPICS_CONFIG, "foo,bar");
        AbstractHerderTest.CONN1_CONFIG.put(CONNECTOR_CLASS_CONFIG, AbstractHerderTest.BogusSourceConnector.class.getName());
        AbstractHerderTest.CONN1_CONFIG.put(AbstractHerderTest.TEST_KEY, AbstractHerderTest.TEST_REF);
        AbstractHerderTest.CONN1_CONFIG.put(AbstractHerderTest.TEST_KEY2, AbstractHerderTest.TEST_REF2);
        AbstractHerderTest.CONN1_CONFIG.put(AbstractHerderTest.TEST_KEY3, AbstractHerderTest.TEST_REF3);
    }

    private static final Map<String, String> TASK_CONFIG = new HashMap<>();

    static {
        AbstractHerderTest.TASK_CONFIG.put(TASK_CLASS_CONFIG, AbstractHerderTest.BogusSourceTask.class.getName());
        AbstractHerderTest.TASK_CONFIG.put(AbstractHerderTest.TEST_KEY, AbstractHerderTest.TEST_REF);
    }

    private static final List<Map<String, String>> TASK_CONFIGS = new ArrayList<>();

    static {
        AbstractHerderTest.TASK_CONFIGS.add(AbstractHerderTest.TASK_CONFIG);
        AbstractHerderTest.TASK_CONFIGS.add(AbstractHerderTest.TASK_CONFIG);
        AbstractHerderTest.TASK_CONFIGS.add(AbstractHerderTest.TASK_CONFIG);
    }

    private static final HashMap<ConnectorTaskId, Map<String, String>> TASK_CONFIGS_MAP = new HashMap<>();

    static {
        AbstractHerderTest.TASK_CONFIGS_MAP.put(AbstractHerderTest.TASK0, AbstractHerderTest.TASK_CONFIG);
        AbstractHerderTest.TASK_CONFIGS_MAP.put(AbstractHerderTest.TASK1, AbstractHerderTest.TASK_CONFIG);
        AbstractHerderTest.TASK_CONFIGS_MAP.put(AbstractHerderTest.TASK2, AbstractHerderTest.TASK_CONFIG);
    }

    private static final ClusterConfigState SNAPSHOT = new ClusterConfigState(1, Collections.singletonMap(AbstractHerderTest.CONN1, 3), Collections.singletonMap(AbstractHerderTest.CONN1, AbstractHerderTest.CONN1_CONFIG), Collections.singletonMap(AbstractHerderTest.CONN1, STARTED), AbstractHerderTest.TASK_CONFIGS_MAP, Collections.<String>emptySet());

    private static final ClusterConfigState SNAPSHOT_NO_TASKS = new ClusterConfigState(1, Collections.singletonMap(AbstractHerderTest.CONN1, 3), Collections.singletonMap(AbstractHerderTest.CONN1, AbstractHerderTest.CONN1_CONFIG), Collections.singletonMap(AbstractHerderTest.CONN1, STARTED), Collections.emptyMap(), Collections.<String>emptySet());

    private final String workerId = "workerId";

    private final String kafkaClusterId = "I4ZmrWqfT2e-upky_4fdPA";

    private final int generation = 5;

    private final String connector = "connector";

    @MockStrict
    private Worker worker;

    @MockStrict
    private WorkerConfigTransformer transformer;

    @MockStrict
    private Plugins plugins;

    @MockStrict
    private ClassLoader classLoader;

    @MockStrict
    private ConfigBackingStore configStore;

    @MockStrict
    private StatusBackingStore statusStore;

    @Test
    public void connectorStatus() {
        ConnectorTaskId taskId = new ConnectorTaskId(connector, 0);
        AbstractHerder herder = partialMockBuilder(AbstractHerder.class).withConstructor(Worker.class, String.class, String.class, StatusBackingStore.class, ConfigBackingStore.class).withArgs(worker, workerId, kafkaClusterId, statusStore, configStore).addMockedMethod("generation").createMock();
        EasyMock.expect(herder.generation()).andStubReturn(generation);
        EasyMock.expect(herder.config(connector)).andReturn(null);
        EasyMock.expect(statusStore.get(connector)).andReturn(new ConnectorStatus(connector, State.RUNNING, workerId, generation));
        EasyMock.expect(statusStore.getAll(connector)).andReturn(Collections.singletonList(new TaskStatus(taskId, State.UNASSIGNED, workerId, generation)));
        EasyMock.expect(worker.getPlugins()).andStubReturn(plugins);
        replayAll();
        ConnectorStateInfo state = herder.connectorStatus(connector);
        Assert.assertEquals(connector, state.name());
        Assert.assertEquals("RUNNING", state.connector().state());
        Assert.assertEquals(1, state.tasks().size());
        Assert.assertEquals(workerId, state.connector().workerId());
        ConnectorStateInfo.TaskState taskState = state.tasks().get(0);
        Assert.assertEquals(0, taskState.id());
        Assert.assertEquals("UNASSIGNED", taskState.state());
        Assert.assertEquals(workerId, taskState.workerId());
        PowerMock.verifyAll();
    }

    @Test
    public void taskStatus() {
        ConnectorTaskId taskId = new ConnectorTaskId("connector", 0);
        String workerId = "workerId";
        AbstractHerder herder = partialMockBuilder(AbstractHerder.class).withConstructor(Worker.class, String.class, String.class, StatusBackingStore.class, ConfigBackingStore.class).withArgs(worker, workerId, kafkaClusterId, statusStore, configStore).addMockedMethod("generation").createMock();
        EasyMock.expect(herder.generation()).andStubReturn(5);
        final Capture<TaskStatus> statusCapture = EasyMock.newCapture();
        statusStore.putSafe(EasyMock.capture(statusCapture));
        EasyMock.expectLastCall();
        EasyMock.expect(statusStore.get(taskId)).andAnswer(new org.easymock.IAnswer<TaskStatus>() {
            @Override
            public TaskStatus answer() throws Throwable {
                return statusCapture.getValue();
            }
        });
        replayAll();
        herder.onFailure(taskId, new RuntimeException());
        ConnectorStateInfo.TaskState taskState = herder.taskStatus(taskId);
        Assert.assertEquals(workerId, taskState.workerId());
        Assert.assertEquals("FAILED", taskState.state());
        Assert.assertEquals(0, taskState.id());
        Assert.assertNotNull(taskState.trace());
        verifyAll();
    }

    @Test(expected = BadRequestException.class)
    public void testConfigValidationEmptyConfig() {
        AbstractHerder herder = createConfigValidationHerder(TestSourceConnector.class);
        replayAll();
        herder.validateConnectorConfig(new HashMap<String, String>());
        verifyAll();
    }

    @Test
    public void testConfigValidationMissingName() {
        AbstractHerder herder = createConfigValidationHerder(TestSourceConnector.class);
        replayAll();
        Map<String, String> config = Collections.singletonMap(CONNECTOR_CLASS_CONFIG, TestSourceConnector.class.getName());
        ConfigInfos result = herder.validateConnectorConfig(config);
        // We expect there to be errors due to the missing name and .... Note that these assertions depend heavily on
        // the config fields for SourceConnectorConfig, but we expect these to change rarely.
        Assert.assertEquals(TestSourceConnector.class.getName(), result.name());
        Assert.assertEquals(Arrays.asList(COMMON_GROUP, TRANSFORMS_GROUP, ERROR_GROUP), result.groups());
        Assert.assertEquals(2, result.errorCount());
        // Base connector config has 13 fields, connector's configs add 2
        Assert.assertEquals(15, result.values().size());
        // Missing name should generate an error
        Assert.assertEquals(NAME_CONFIG, result.values().get(0).configValue().name());
        Assert.assertEquals(1, result.values().get(0).configValue().errors().size());
        // "required" config from connector should generate an error
        Assert.assertEquals("required", result.values().get(13).configValue().name());
        Assert.assertEquals(1, result.values().get(13).configValue().errors().size());
        verifyAll();
    }

    @Test(expected = ConfigException.class)
    public void testConfigValidationInvalidTopics() {
        AbstractHerder herder = createConfigValidationHerder(TestSinkConnector.class);
        replayAll();
        Map<String, String> config = new HashMap<>();
        config.put(CONNECTOR_CLASS_CONFIG, TestSinkConnector.class.getName());
        config.put(TOPICS_CONFIG, "topic1,topic2");
        config.put(TOPICS_REGEX_CONFIG, "topic.*");
        herder.validateConnectorConfig(config);
        verifyAll();
    }

    @Test
    public void testConfigValidationTransformsExtendResults() {
        AbstractHerder herder = createConfigValidationHerder(TestSourceConnector.class);
        // 2 transform aliases defined -> 2 plugin lookups
        Set<PluginDesc<Transformation>> transformations = new HashSet<>();
        transformations.add(new PluginDesc<Transformation>(AbstractHerderTest.SampleTransformation.class, "1.0", classLoader));
        EasyMock.expect(plugins.transformations()).andReturn(transformations).times(2);
        replayAll();
        // Define 2 transformations. One has a class defined and so can get embedded configs, the other is missing
        // class info that should generate an error.
        Map<String, String> config = new HashMap<>();
        config.put(CONNECTOR_CLASS_CONFIG, TestSourceConnector.class.getName());
        config.put(NAME_CONFIG, "connector-name");
        config.put(TRANSFORMS_CONFIG, "xformA,xformB");
        config.put(((TRANSFORMS_CONFIG) + ".xformA.type"), AbstractHerderTest.SampleTransformation.class.getName());
        config.put("required", "value");// connector required config

        ConfigInfos result = herder.validateConnectorConfig(config);
        Assert.assertEquals(herder.connectorTypeForClass(config.get(CONNECTOR_CLASS_CONFIG)), SOURCE);
        // We expect there to be errors due to the missing name and .... Note that these assertions depend heavily on
        // the config fields for SourceConnectorConfig, but we expect these to change rarely.
        Assert.assertEquals(TestSourceConnector.class.getName(), result.name());
        // Each transform also gets its own group
        List<String> expectedGroups = Arrays.asList(COMMON_GROUP, TRANSFORMS_GROUP, ERROR_GROUP, "Transforms: xformA", "Transforms: xformB");
        Assert.assertEquals(expectedGroups, result.groups());
        Assert.assertEquals(2, result.errorCount());
        // Base connector config has 13 fields, connector's configs add 2, 2 type fields from the transforms, and
        // 1 from the valid transformation's config
        Assert.assertEquals(18, result.values().size());
        // Should get 2 type fields from the transforms, first adds its own config since it has a valid class
        Assert.assertEquals("transforms.xformA.type", result.values().get(13).configValue().name());
        Assert.assertTrue(result.values().get(13).configValue().errors().isEmpty());
        Assert.assertEquals("transforms.xformA.subconfig", result.values().get(14).configValue().name());
        Assert.assertEquals("transforms.xformB.type", result.values().get(15).configValue().name());
        Assert.assertFalse(result.values().get(15).configValue().errors().isEmpty());
        verifyAll();
    }

    @Test
    public void testReverseTransformConfigs() {
        // Construct a task config with constant values for TEST_KEY and TEST_KEY2
        Map<String, String> newTaskConfig = new HashMap<>();
        newTaskConfig.put(TASK_CLASS_CONFIG, AbstractHerderTest.BogusSourceTask.class.getName());
        newTaskConfig.put(AbstractHerderTest.TEST_KEY, AbstractHerderTest.TEST_VAL);
        newTaskConfig.put(AbstractHerderTest.TEST_KEY2, AbstractHerderTest.TEST_VAL2);
        List<Map<String, String>> newTaskConfigs = new ArrayList<>();
        newTaskConfigs.add(newTaskConfig);
        // The SNAPSHOT has a task config with TEST_KEY and TEST_REF
        List<Map<String, String>> reverseTransformed = AbstractHerder.reverseTransform(AbstractHerderTest.CONN1, AbstractHerderTest.SNAPSHOT, newTaskConfigs);
        Assert.assertEquals(AbstractHerderTest.TEST_REF, reverseTransformed.get(0).get(AbstractHerderTest.TEST_KEY));
        // The SNAPSHOT has no task configs but does have a connector config with TEST_KEY2 and TEST_REF2
        reverseTransformed = AbstractHerder.reverseTransform(AbstractHerderTest.CONN1, AbstractHerderTest.SNAPSHOT_NO_TASKS, newTaskConfigs);
        Assert.assertEquals(AbstractHerderTest.TEST_REF2, reverseTransformed.get(0).get(AbstractHerderTest.TEST_KEY2));
        // The reverseTransformed result should not have TEST_KEY3 since newTaskConfigs does not have TEST_KEY3
        reverseTransformed = AbstractHerder.reverseTransform(AbstractHerderTest.CONN1, AbstractHerderTest.SNAPSHOT_NO_TASKS, newTaskConfigs);
        Assert.assertFalse(reverseTransformed.get(0).containsKey(AbstractHerderTest.TEST_KEY3));
    }

    public static class SampleTransformation<R extends ConnectRecord<R>> implements Transformation<R> {
        @Override
        public void configure(Map<String, ?> configs) {
        }

        @Override
        public R apply(R record) {
            return record;
        }

        @Override
        public ConfigDef config() {
            return new ConfigDef().define("subconfig", STRING, "default", LOW, "docs");
        }

        @Override
        public void close() {
        }
    }

    // We need to use a real class here due to some issue with mocking java.lang.Class
    private abstract class BogusSourceConnector extends SourceConnector {}

    private abstract class BogusSourceTask extends SourceTask {}
}

