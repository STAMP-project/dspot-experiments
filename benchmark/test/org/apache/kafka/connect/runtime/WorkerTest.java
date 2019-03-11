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


import ClassLoaderUsage.CURRENT_CLASSLOADER;
import ClassLoaderUsage.PLUGINS;
import ClusterConfigState.EMPTY;
import ConfigDef.Importance.HIGH;
import ConfigDef.Type.STRING;
import ConnectorConfig.CONNECTOR_CLASS_CONFIG;
import ConnectorConfig.KEY_CONVERTER_CLASS_CONFIG;
import ConnectorConfig.NAME_CONFIG;
import ConnectorConfig.TASKS_MAX_CONFIG;
import ConnectorConfig.VALUE_CONVERTER_CLASS_CONFIG;
import SinkConnectorConfig.TOPICS_CONFIG;
import TargetState.STARTED;
import TaskConfig.TASK_CLASS_CONFIG;
import TaskStatus.Listener;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.Configurable;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.connector.ConnectorContext;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.json.JsonConverterConfig;
import org.apache.kafka.connect.runtime.distributed.ClusterConfigState;
import org.apache.kafka.connect.runtime.errors.RetryWithToleranceOperator;
import org.apache.kafka.connect.runtime.errors.RetryWithToleranceOperatorTest;
import org.apache.kafka.connect.runtime.isolation.DelegatingClassLoader;
import org.apache.kafka.connect.runtime.isolation.PluginClassLoader;
import org.apache.kafka.connect.runtime.isolation.Plugins;
import org.apache.kafka.connect.runtime.standalone.StandaloneConfig;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.storage.Converter;
import org.apache.kafka.connect.storage.HeaderConverter;
import org.apache.kafka.connect.storage.OffsetBackingStore;
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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ Worker.class, Plugins.class })
@PowerMockIgnore("javax.management.*")
public class WorkerTest extends ThreadedTest {
    private static final String CONNECTOR_ID = "test-connector";

    private static final ConnectorTaskId TASK_ID = new ConnectorTaskId("job", 0);

    private static final String WORKER_ID = "localhost:8083";

    private Map<String, String> workerProps = new HashMap<>();

    private WorkerConfig config;

    private Worker worker;

    private Map<String, String> defaultProducerConfigs = new HashMap<>();

    private Map<String, String> defaultConsumerConfigs = new HashMap<>();

    @Mock
    private Plugins plugins;

    @Mock
    private PluginClassLoader pluginLoader;

    @Mock
    private DelegatingClassLoader delegatingLoader;

    @Mock
    private OffsetBackingStore offsetBackingStore;

    @MockStrict
    private Listener taskStatusListener;

    @MockStrict
    private ConnectorStatus.Listener connectorStatusListener;

    @Mock
    private Connector connector;

    @Mock
    private ConnectorContext ctx;

    @Mock
    private WorkerTest.TestSourceTask task;

    @Mock
    private WorkerSourceTask workerTask;

    @Mock
    private Converter keyConverter;

    @Mock
    private Converter valueConverter;

    @Mock
    private Converter taskKeyConverter;

    @Mock
    private Converter taskValueConverter;

    @Mock
    private HeaderConverter taskHeaderConverter;

    @Test
    public void testStartAndStopConnector() {
        expectConverters();
        expectStartStorage();
        // Create
        EasyMock.expect(plugins.currentThreadLoader()).andReturn(delegatingLoader).times(2);
        EasyMock.expect(plugins.newConnector(WorkerTest.WorkerTestConnector.class.getName())).andReturn(connector);
        EasyMock.expect(connector.version()).andReturn("1.0");
        Map<String, String> props = new HashMap<>();
        props.put(TOPICS_CONFIG, "foo,bar");
        props.put(TASKS_MAX_CONFIG, "1");
        props.put(NAME_CONFIG, WorkerTest.CONNECTOR_ID);
        props.put(CONNECTOR_CLASS_CONFIG, WorkerTest.WorkerTestConnector.class.getName());
        EasyMock.expect(connector.version()).andReturn("1.0");
        EasyMock.expect(plugins.compareAndSwapLoaders(connector)).andReturn(delegatingLoader).times(2);
        connector.initialize(anyObject(ConnectorContext.class));
        EasyMock.expectLastCall();
        connector.start(props);
        EasyMock.expectLastCall();
        EasyMock.expect(Plugins.compareAndSwapLoaders(delegatingLoader)).andReturn(pluginLoader).times(2);
        connectorStatusListener.onStartup(WorkerTest.CONNECTOR_ID);
        EasyMock.expectLastCall();
        // Remove
        connector.stop();
        EasyMock.expectLastCall();
        connectorStatusListener.onShutdown(WorkerTest.CONNECTOR_ID);
        EasyMock.expectLastCall();
        expectStopStorage();
        PowerMock.replayAll();
        worker = new Worker(WorkerTest.WORKER_ID, new MockTime(), plugins, config, offsetBackingStore);
        worker.start();
        Assert.assertEquals(Collections.emptySet(), worker.connectorNames());
        worker.startConnector(WorkerTest.CONNECTOR_ID, props, ctx, connectorStatusListener, STARTED);
        Assert.assertEquals(new HashSet(Arrays.asList(WorkerTest.CONNECTOR_ID)), worker.connectorNames());
        try {
            worker.startConnector(WorkerTest.CONNECTOR_ID, props, ctx, connectorStatusListener, STARTED);
            Assert.fail("Should have thrown exception when trying to add connector with same name.");
        } catch (ConnectException e) {
            // expected
        }
        assertStatistics(worker, 1, 0);
        assertStartupStatistics(worker, 1, 0, 0, 0);
        worker.stopConnector(WorkerTest.CONNECTOR_ID);
        assertStatistics(worker, 0, 0);
        assertStartupStatistics(worker, 1, 0, 0, 0);
        Assert.assertEquals(Collections.emptySet(), worker.connectorNames());
        // Nothing should be left, so this should effectively be a nop
        worker.stop();
        assertStatistics(worker, 0, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testStartConnectorFailure() {
        expectConverters();
        expectStartStorage();
        Map<String, String> props = new HashMap<>();
        props.put(TOPICS_CONFIG, "foo,bar");
        props.put(TASKS_MAX_CONFIG, "1");
        props.put(NAME_CONFIG, WorkerTest.CONNECTOR_ID);
        props.put(CONNECTOR_CLASS_CONFIG, "java.util.HashMap");// Bad connector class name

        EasyMock.expect(plugins.currentThreadLoader()).andReturn(delegatingLoader);
        EasyMock.expect(plugins.newConnector(EasyMock.anyString())).andThrow(new ConnectException("Failed to find Connector"));
        EasyMock.expect(Plugins.compareAndSwapLoaders(delegatingLoader)).andReturn(pluginLoader);
        connectorStatusListener.onFailure(EasyMock.eq(WorkerTest.CONNECTOR_ID), EasyMock.<ConnectException>anyObject());
        EasyMock.expectLastCall();
        PowerMock.replayAll();
        worker = new Worker(WorkerTest.WORKER_ID, new MockTime(), plugins, config, offsetBackingStore);
        worker.start();
        assertStatistics(worker, 0, 0);
        Assert.assertFalse(worker.startConnector(WorkerTest.CONNECTOR_ID, props, ctx, connectorStatusListener, STARTED));
        assertStartupStatistics(worker, 1, 1, 0, 0);
        Assert.assertEquals(Collections.emptySet(), worker.connectorNames());
        assertStatistics(worker, 0, 0);
        assertStartupStatistics(worker, 1, 1, 0, 0);
        Assert.assertFalse(worker.stopConnector(WorkerTest.CONNECTOR_ID));
        assertStatistics(worker, 0, 0);
        assertStartupStatistics(worker, 1, 1, 0, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testAddConnectorByAlias() {
        expectConverters();
        expectStartStorage();
        EasyMock.expect(plugins.currentThreadLoader()).andReturn(delegatingLoader).times(2);
        EasyMock.expect(plugins.newConnector("WorkerTestConnector")).andReturn(connector);
        EasyMock.expect(connector.version()).andReturn("1.0");
        Map<String, String> props = new HashMap<>();
        props.put(TOPICS_CONFIG, "foo,bar");
        props.put(TASKS_MAX_CONFIG, "1");
        props.put(NAME_CONFIG, WorkerTest.CONNECTOR_ID);
        props.put(CONNECTOR_CLASS_CONFIG, "WorkerTestConnector");
        EasyMock.expect(connector.version()).andReturn("1.0");
        EasyMock.expect(plugins.compareAndSwapLoaders(connector)).andReturn(delegatingLoader).times(2);
        connector.initialize(anyObject(ConnectorContext.class));
        EasyMock.expectLastCall();
        connector.start(props);
        EasyMock.expectLastCall();
        EasyMock.expect(Plugins.compareAndSwapLoaders(delegatingLoader)).andReturn(pluginLoader).times(2);
        connectorStatusListener.onStartup(WorkerTest.CONNECTOR_ID);
        EasyMock.expectLastCall();
        // Remove
        connector.stop();
        EasyMock.expectLastCall();
        connectorStatusListener.onShutdown(WorkerTest.CONNECTOR_ID);
        EasyMock.expectLastCall();
        expectStopStorage();
        PowerMock.replayAll();
        worker = new Worker(WorkerTest.WORKER_ID, new MockTime(), plugins, config, offsetBackingStore);
        worker.start();
        assertStatistics(worker, 0, 0);
        Assert.assertEquals(Collections.emptySet(), worker.connectorNames());
        worker.startConnector(WorkerTest.CONNECTOR_ID, props, ctx, connectorStatusListener, STARTED);
        Assert.assertEquals(new HashSet(Arrays.asList(WorkerTest.CONNECTOR_ID)), worker.connectorNames());
        assertStatistics(worker, 1, 0);
        assertStartupStatistics(worker, 1, 0, 0, 0);
        worker.stopConnector(WorkerTest.CONNECTOR_ID);
        assertStatistics(worker, 0, 0);
        assertStartupStatistics(worker, 1, 0, 0, 0);
        Assert.assertEquals(Collections.emptySet(), worker.connectorNames());
        // Nothing should be left, so this should effectively be a nop
        worker.stop();
        assertStatistics(worker, 0, 0);
        assertStartupStatistics(worker, 1, 0, 0, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testAddConnectorByShortAlias() {
        expectConverters();
        expectStartStorage();
        EasyMock.expect(plugins.currentThreadLoader()).andReturn(delegatingLoader).times(2);
        EasyMock.expect(plugins.newConnector("WorkerTest")).andReturn(connector);
        EasyMock.expect(connector.version()).andReturn("1.0");
        Map<String, String> props = new HashMap<>();
        props.put(TOPICS_CONFIG, "foo,bar");
        props.put(TASKS_MAX_CONFIG, "1");
        props.put(NAME_CONFIG, WorkerTest.CONNECTOR_ID);
        props.put(CONNECTOR_CLASS_CONFIG, "WorkerTest");
        EasyMock.expect(connector.version()).andReturn("1.0");
        EasyMock.expect(plugins.compareAndSwapLoaders(connector)).andReturn(delegatingLoader).times(2);
        connector.initialize(anyObject(ConnectorContext.class));
        EasyMock.expectLastCall();
        connector.start(props);
        EasyMock.expectLastCall();
        EasyMock.expect(Plugins.compareAndSwapLoaders(delegatingLoader)).andReturn(pluginLoader).times(2);
        connectorStatusListener.onStartup(WorkerTest.CONNECTOR_ID);
        EasyMock.expectLastCall();
        // Remove
        connector.stop();
        EasyMock.expectLastCall();
        connectorStatusListener.onShutdown(WorkerTest.CONNECTOR_ID);
        EasyMock.expectLastCall();
        expectStopStorage();
        PowerMock.replayAll();
        worker = new Worker(WorkerTest.WORKER_ID, new MockTime(), plugins, config, offsetBackingStore);
        worker.start();
        assertStatistics(worker, 0, 0);
        Assert.assertEquals(Collections.emptySet(), worker.connectorNames());
        worker.startConnector(WorkerTest.CONNECTOR_ID, props, ctx, connectorStatusListener, STARTED);
        Assert.assertEquals(new HashSet(Arrays.asList(WorkerTest.CONNECTOR_ID)), worker.connectorNames());
        assertStatistics(worker, 1, 0);
        worker.stopConnector(WorkerTest.CONNECTOR_ID);
        assertStatistics(worker, 0, 0);
        Assert.assertEquals(Collections.emptySet(), worker.connectorNames());
        // Nothing should be left, so this should effectively be a nop
        worker.stop();
        assertStatistics(worker, 0, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testStopInvalidConnector() {
        expectConverters();
        expectStartStorage();
        PowerMock.replayAll();
        worker = new Worker(WorkerTest.WORKER_ID, new MockTime(), plugins, config, offsetBackingStore);
        worker.start();
        worker.stopConnector(WorkerTest.CONNECTOR_ID);
        PowerMock.verifyAll();
    }

    @Test
    public void testReconfigureConnectorTasks() {
        expectConverters();
        expectStartStorage();
        EasyMock.expect(plugins.currentThreadLoader()).andReturn(delegatingLoader).times(3);
        EasyMock.expect(plugins.newConnector(WorkerTest.WorkerTestConnector.class.getName())).andReturn(connector);
        EasyMock.expect(connector.version()).andReturn("1.0");
        Map<String, String> props = new HashMap<>();
        props.put(TOPICS_CONFIG, "foo,bar");
        props.put(TASKS_MAX_CONFIG, "1");
        props.put(NAME_CONFIG, WorkerTest.CONNECTOR_ID);
        props.put(CONNECTOR_CLASS_CONFIG, WorkerTest.WorkerTestConnector.class.getName());
        EasyMock.expect(connector.version()).andReturn("1.0");
        EasyMock.expect(plugins.compareAndSwapLoaders(connector)).andReturn(delegatingLoader).times(3);
        connector.initialize(anyObject(ConnectorContext.class));
        EasyMock.expectLastCall();
        connector.start(props);
        EasyMock.expectLastCall();
        EasyMock.expect(Plugins.compareAndSwapLoaders(delegatingLoader)).andReturn(pluginLoader).times(3);
        connectorStatusListener.onStartup(WorkerTest.CONNECTOR_ID);
        EasyMock.expectLastCall();
        // Reconfigure
        EasyMock.<Class<? extends Task>>expect(connector.taskClass()).andReturn(WorkerTest.TestSourceTask.class);
        Map<String, String> taskProps = new HashMap<>();
        taskProps.put("foo", "bar");
        EasyMock.expect(connector.taskConfigs(2)).andReturn(Arrays.asList(taskProps, taskProps));
        // Remove
        connector.stop();
        EasyMock.expectLastCall();
        connectorStatusListener.onShutdown(WorkerTest.CONNECTOR_ID);
        EasyMock.expectLastCall();
        expectStopStorage();
        PowerMock.replayAll();
        worker = new Worker(WorkerTest.WORKER_ID, new MockTime(), plugins, config, offsetBackingStore);
        worker.start();
        assertStatistics(worker, 0, 0);
        Assert.assertEquals(Collections.emptySet(), worker.connectorNames());
        worker.startConnector(WorkerTest.CONNECTOR_ID, props, ctx, connectorStatusListener, STARTED);
        assertStatistics(worker, 1, 0);
        Assert.assertEquals(new HashSet(Arrays.asList(WorkerTest.CONNECTOR_ID)), worker.connectorNames());
        try {
            worker.startConnector(WorkerTest.CONNECTOR_ID, props, ctx, connectorStatusListener, STARTED);
            Assert.fail("Should have thrown exception when trying to add connector with same name.");
        } catch (ConnectException e) {
            // expected
        }
        Map<String, String> connProps = new HashMap<>(props);
        connProps.put(TASKS_MAX_CONFIG, "2");
        ConnectorConfig connConfig = new SinkConnectorConfig(plugins, connProps);
        List<Map<String, String>> taskConfigs = worker.connectorTaskConfigs(WorkerTest.CONNECTOR_ID, connConfig);
        Map<String, String> expectedTaskProps = new HashMap<>();
        expectedTaskProps.put("foo", "bar");
        expectedTaskProps.put(TASK_CLASS_CONFIG, WorkerTest.TestSourceTask.class.getName());
        expectedTaskProps.put(SinkTask.TOPICS_CONFIG, "foo,bar");
        Assert.assertEquals(2, taskConfigs.size());
        Assert.assertEquals(expectedTaskProps, taskConfigs.get(0));
        Assert.assertEquals(expectedTaskProps, taskConfigs.get(1));
        assertStatistics(worker, 1, 0);
        assertStartupStatistics(worker, 1, 0, 0, 0);
        worker.stopConnector(WorkerTest.CONNECTOR_ID);
        assertStatistics(worker, 0, 0);
        assertStartupStatistics(worker, 1, 0, 0, 0);
        Assert.assertEquals(Collections.emptySet(), worker.connectorNames());
        // Nothing should be left, so this should effectively be a nop
        worker.stop();
        assertStatistics(worker, 0, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testAddRemoveTask() throws Exception {
        expectConverters();
        expectStartStorage();
        EasyMock.expect(workerTask.id()).andStubReturn(WorkerTest.TASK_ID);
        EasyMock.expect(plugins.currentThreadLoader()).andReturn(delegatingLoader).times(2);
        PowerMock.expectNew(WorkerSourceTask.class, EasyMock.eq(WorkerTest.TASK_ID), EasyMock.eq(task), anyObject(Listener.class), EasyMock.eq(STARTED), anyObject(JsonConverter.class), anyObject(JsonConverter.class), anyObject(JsonConverter.class), EasyMock.eq(new TransformationChain(Collections.emptyList(), RetryWithToleranceOperatorTest.NOOP_OPERATOR)), anyObject(KafkaProducer.class), anyObject(OffsetStorageReader.class), anyObject(OffsetStorageWriter.class), EasyMock.eq(config), anyObject(ClusterConfigState.class), anyObject(ConnectMetrics.class), anyObject(ClassLoader.class), anyObject(Time.class), anyObject(RetryWithToleranceOperator.class)).andReturn(workerTask);
        Map<String, String> origProps = new HashMap<>();
        origProps.put(TASK_CLASS_CONFIG, WorkerTest.TestSourceTask.class.getName());
        TaskConfig taskConfig = new TaskConfig(origProps);
        // We should expect this call, but the pluginLoader being swapped in is only mocked.
        // EasyMock.expect(pluginLoader.loadClass(TestSourceTask.class.getName()))
        // .andReturn((Class) TestSourceTask.class);
        EasyMock.expect(plugins.newTask(WorkerTest.TestSourceTask.class)).andReturn(task);
        EasyMock.expect(task.version()).andReturn("1.0");
        workerTask.initialize(taskConfig);
        EasyMock.expectLastCall();
        // Expect that the worker will create converters and will find them using the current classloader ...
        Assert.assertNotNull(taskKeyConverter);
        Assert.assertNotNull(taskValueConverter);
        Assert.assertNotNull(taskHeaderConverter);
        expectTaskKeyConverters(CURRENT_CLASSLOADER, taskKeyConverter);
        expectTaskValueConverters(CURRENT_CLASSLOADER, taskValueConverter);
        expectTaskHeaderConverter(CURRENT_CLASSLOADER, taskHeaderConverter);
        workerTask.run();
        EasyMock.expectLastCall();
        EasyMock.expect(plugins.delegatingLoader()).andReturn(delegatingLoader);
        EasyMock.expect(delegatingLoader.connectorLoader(WorkerTest.WorkerTestConnector.class.getName())).andReturn(pluginLoader);
        EasyMock.expect(Plugins.compareAndSwapLoaders(pluginLoader)).andReturn(delegatingLoader).times(2);
        EasyMock.expect(workerTask.loader()).andReturn(pluginLoader);
        EasyMock.expect(Plugins.compareAndSwapLoaders(delegatingLoader)).andReturn(pluginLoader).times(2);
        // Remove
        workerTask.stop();
        EasyMock.expectLastCall();
        EasyMock.expect(workerTask.awaitStop(EasyMock.anyLong())).andStubReturn(true);
        EasyMock.expectLastCall();
        expectStopStorage();
        PowerMock.replayAll();
        worker = new Worker(WorkerTest.WORKER_ID, new MockTime(), plugins, config, offsetBackingStore);
        worker.start();
        assertStatistics(worker, 0, 0);
        assertStartupStatistics(worker, 0, 0, 0, 0);
        Assert.assertEquals(Collections.emptySet(), worker.taskIds());
        worker.startTask(WorkerTest.TASK_ID, EMPTY, anyConnectorConfigMap(), origProps, taskStatusListener, STARTED);
        assertStatistics(worker, 0, 1);
        assertStartupStatistics(worker, 0, 0, 1, 0);
        Assert.assertEquals(new HashSet(Arrays.asList(WorkerTest.TASK_ID)), worker.taskIds());
        worker.stopAndAwaitTask(WorkerTest.TASK_ID);
        assertStatistics(worker, 0, 0);
        assertStartupStatistics(worker, 0, 0, 1, 0);
        Assert.assertEquals(Collections.emptySet(), worker.taskIds());
        // Nothing should be left, so this should effectively be a nop
        worker.stop();
        assertStatistics(worker, 0, 0);
        assertStartupStatistics(worker, 0, 0, 1, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testStartTaskFailure() {
        expectConverters();
        expectStartStorage();
        Map<String, String> origProps = new HashMap<>();
        origProps.put(TASK_CLASS_CONFIG, "missing.From.This.Workers.Classpath");
        EasyMock.expect(plugins.currentThreadLoader()).andReturn(delegatingLoader);
        EasyMock.expect(plugins.delegatingLoader()).andReturn(delegatingLoader);
        EasyMock.expect(delegatingLoader.connectorLoader(WorkerTest.WorkerTestConnector.class.getName())).andReturn(pluginLoader);
        // We would normally expect this since the plugin loader would have been swapped in. However, since we mock out
        // all classloader changes, the call actually goes to the normal default classloader. However, this works out
        // fine since we just wanted a ClassNotFoundException anyway.
        // EasyMock.expect(pluginLoader.loadClass(origProps.get(TaskConfig.TASK_CLASS_CONFIG)))
        // .andThrow(new ClassNotFoundException());
        EasyMock.expect(Plugins.compareAndSwapLoaders(pluginLoader)).andReturn(delegatingLoader);
        EasyMock.expect(Plugins.compareAndSwapLoaders(delegatingLoader)).andReturn(pluginLoader);
        taskStatusListener.onFailure(EasyMock.eq(WorkerTest.TASK_ID), EasyMock.<ConfigException>anyObject());
        EasyMock.expectLastCall();
        PowerMock.replayAll();
        worker = new Worker(WorkerTest.WORKER_ID, new MockTime(), plugins, config, offsetBackingStore);
        worker.start();
        assertStatistics(worker, 0, 0);
        assertStartupStatistics(worker, 0, 0, 0, 0);
        Assert.assertFalse(worker.startTask(WorkerTest.TASK_ID, EMPTY, anyConnectorConfigMap(), origProps, taskStatusListener, STARTED));
        assertStartupStatistics(worker, 0, 0, 1, 1);
        assertStatistics(worker, 0, 0);
        assertStartupStatistics(worker, 0, 0, 1, 1);
        Assert.assertEquals(Collections.emptySet(), worker.taskIds());
        PowerMock.verifyAll();
    }

    @Test
    public void testCleanupTasksOnStop() throws Exception {
        expectConverters();
        expectStartStorage();
        EasyMock.expect(workerTask.id()).andStubReturn(WorkerTest.TASK_ID);
        EasyMock.expect(plugins.currentThreadLoader()).andReturn(delegatingLoader).times(2);
        PowerMock.expectNew(WorkerSourceTask.class, EasyMock.eq(WorkerTest.TASK_ID), EasyMock.eq(task), anyObject(Listener.class), EasyMock.eq(STARTED), anyObject(JsonConverter.class), anyObject(JsonConverter.class), anyObject(JsonConverter.class), EasyMock.eq(new TransformationChain(Collections.emptyList(), RetryWithToleranceOperatorTest.NOOP_OPERATOR)), anyObject(KafkaProducer.class), anyObject(OffsetStorageReader.class), anyObject(OffsetStorageWriter.class), anyObject(WorkerConfig.class), anyObject(ClusterConfigState.class), anyObject(ConnectMetrics.class), EasyMock.eq(pluginLoader), anyObject(Time.class), anyObject(RetryWithToleranceOperator.class)).andReturn(workerTask);
        Map<String, String> origProps = new HashMap<>();
        origProps.put(TASK_CLASS_CONFIG, WorkerTest.TestSourceTask.class.getName());
        TaskConfig taskConfig = new TaskConfig(origProps);
        // We should expect this call, but the pluginLoader being swapped in is only mocked.
        // EasyMock.expect(pluginLoader.loadClass(TestSourceTask.class.getName()))
        // .andReturn((Class) TestSourceTask.class);
        EasyMock.expect(plugins.newTask(WorkerTest.TestSourceTask.class)).andReturn(task);
        EasyMock.expect(task.version()).andReturn("1.0");
        workerTask.initialize(taskConfig);
        EasyMock.expectLastCall();
        // Expect that the worker will create converters and will not initially find them using the current classloader ...
        Assert.assertNotNull(taskKeyConverter);
        Assert.assertNotNull(taskValueConverter);
        Assert.assertNotNull(taskHeaderConverter);
        expectTaskKeyConverters(CURRENT_CLASSLOADER, null);
        expectTaskKeyConverters(PLUGINS, taskKeyConverter);
        expectTaskValueConverters(CURRENT_CLASSLOADER, null);
        expectTaskValueConverters(PLUGINS, taskValueConverter);
        expectTaskHeaderConverter(CURRENT_CLASSLOADER, null);
        expectTaskHeaderConverter(PLUGINS, taskHeaderConverter);
        workerTask.run();
        EasyMock.expectLastCall();
        EasyMock.expect(plugins.delegatingLoader()).andReturn(delegatingLoader);
        EasyMock.expect(delegatingLoader.connectorLoader(WorkerTest.WorkerTestConnector.class.getName())).andReturn(pluginLoader);
        EasyMock.expect(Plugins.compareAndSwapLoaders(pluginLoader)).andReturn(delegatingLoader).times(2);
        EasyMock.expect(workerTask.loader()).andReturn(pluginLoader);
        EasyMock.expect(Plugins.compareAndSwapLoaders(delegatingLoader)).andReturn(pluginLoader).times(2);
        // Remove on Worker.stop()
        workerTask.stop();
        EasyMock.expectLastCall();
        EasyMock.expect(workerTask.awaitStop(EasyMock.anyLong())).andReturn(true);
        // Note that in this case we *do not* commit offsets since it's an unclean shutdown
        EasyMock.expectLastCall();
        expectStopStorage();
        PowerMock.replayAll();
        worker = new Worker(WorkerTest.WORKER_ID, new MockTime(), plugins, config, offsetBackingStore);
        worker.start();
        assertStatistics(worker, 0, 0);
        worker.startTask(WorkerTest.TASK_ID, EMPTY, anyConnectorConfigMap(), origProps, taskStatusListener, STARTED);
        assertStatistics(worker, 0, 1);
        worker.stop();
        assertStatistics(worker, 0, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testConverterOverrides() throws Exception {
        expectConverters();
        expectStartStorage();
        EasyMock.expect(workerTask.id()).andStubReturn(WorkerTest.TASK_ID);
        Capture<WorkerTest.TestConverter> keyConverter = EasyMock.newCapture();
        Capture<WorkerTest.TestConfigurableConverter> valueConverter = EasyMock.newCapture();
        Capture<HeaderConverter> headerConverter = EasyMock.newCapture();
        EasyMock.expect(plugins.currentThreadLoader()).andReturn(delegatingLoader).times(2);
        PowerMock.expectNew(WorkerSourceTask.class, EasyMock.eq(WorkerTest.TASK_ID), EasyMock.eq(task), anyObject(Listener.class), EasyMock.eq(STARTED), EasyMock.capture(keyConverter), EasyMock.capture(valueConverter), EasyMock.capture(headerConverter), EasyMock.eq(new TransformationChain(Collections.emptyList(), RetryWithToleranceOperatorTest.NOOP_OPERATOR)), anyObject(KafkaProducer.class), anyObject(OffsetStorageReader.class), anyObject(OffsetStorageWriter.class), anyObject(WorkerConfig.class), anyObject(ClusterConfigState.class), anyObject(ConnectMetrics.class), EasyMock.eq(pluginLoader), anyObject(Time.class), anyObject(RetryWithToleranceOperator.class)).andReturn(workerTask);
        Map<String, String> origProps = new HashMap<>();
        origProps.put(TASK_CLASS_CONFIG, WorkerTest.TestSourceTask.class.getName());
        TaskConfig taskConfig = new TaskConfig(origProps);
        // We should expect this call, but the pluginLoader being swapped in is only mocked.
        // EasyMock.expect(pluginLoader.loadClass(TestSourceTask.class.getName()))
        // .andReturn((Class) TestSourceTask.class);
        EasyMock.expect(plugins.newTask(WorkerTest.TestSourceTask.class)).andReturn(task);
        EasyMock.expect(task.version()).andReturn("1.0");
        workerTask.initialize(taskConfig);
        EasyMock.expectLastCall();
        // Expect that the worker will create converters and will not initially find them using the current classloader ...
        Assert.assertNotNull(taskKeyConverter);
        Assert.assertNotNull(taskValueConverter);
        Assert.assertNotNull(taskHeaderConverter);
        expectTaskKeyConverters(CURRENT_CLASSLOADER, null);
        expectTaskKeyConverters(PLUGINS, taskKeyConverter);
        expectTaskValueConverters(CURRENT_CLASSLOADER, null);
        expectTaskValueConverters(PLUGINS, taskValueConverter);
        expectTaskHeaderConverter(CURRENT_CLASSLOADER, null);
        expectTaskHeaderConverter(PLUGINS, taskHeaderConverter);
        workerTask.run();
        EasyMock.expectLastCall();
        EasyMock.expect(plugins.delegatingLoader()).andReturn(delegatingLoader);
        EasyMock.expect(delegatingLoader.connectorLoader(WorkerTest.WorkerTestConnector.class.getName())).andReturn(pluginLoader);
        EasyMock.expect(Plugins.compareAndSwapLoaders(pluginLoader)).andReturn(delegatingLoader).times(2);
        EasyMock.expect(workerTask.loader()).andReturn(pluginLoader);
        EasyMock.expect(Plugins.compareAndSwapLoaders(delegatingLoader)).andReturn(pluginLoader).times(2);
        // Remove
        workerTask.stop();
        EasyMock.expectLastCall();
        EasyMock.expect(workerTask.awaitStop(EasyMock.anyLong())).andStubReturn(true);
        EasyMock.expectLastCall();
        expectStopStorage();
        PowerMock.replayAll();
        worker = new Worker(WorkerTest.WORKER_ID, new MockTime(), plugins, config, offsetBackingStore);
        worker.start();
        assertStatistics(worker, 0, 0);
        Assert.assertEquals(Collections.emptySet(), worker.taskIds());
        Map<String, String> connProps = anyConnectorConfigMap();
        connProps.put(KEY_CONVERTER_CLASS_CONFIG, WorkerTest.TestConverter.class.getName());
        connProps.put("key.converter.extra.config", "foo");
        connProps.put(VALUE_CONVERTER_CLASS_CONFIG, WorkerTest.TestConfigurableConverter.class.getName());
        connProps.put("value.converter.extra.config", "bar");
        worker.startTask(WorkerTest.TASK_ID, EMPTY, connProps, origProps, taskStatusListener, STARTED);
        assertStatistics(worker, 0, 1);
        Assert.assertEquals(new HashSet(Arrays.asList(WorkerTest.TASK_ID)), worker.taskIds());
        worker.stopAndAwaitTask(WorkerTest.TASK_ID);
        assertStatistics(worker, 0, 0);
        Assert.assertEquals(Collections.emptySet(), worker.taskIds());
        // Nothing should be left, so this should effectively be a nop
        worker.stop();
        assertStatistics(worker, 0, 0);
        // We've mocked the Plugin.newConverter method, so we don't currently configure the converters
        PowerMock.verifyAll();
    }

    @Test
    public void testProducerConfigsWithoutOverrides() {
        Assert.assertEquals(defaultProducerConfigs, Worker.producerConfigs(config));
    }

    @Test
    public void testProducerConfigsWithOverrides() {
        Map<String, String> props = new HashMap<>(workerProps);
        props.put("producer.acks", "-1");
        props.put("producer.linger.ms", "1000");
        WorkerConfig configWithOverrides = new StandaloneConfig(props);
        Map<String, String> expectedConfigs = new HashMap<>(defaultProducerConfigs);
        expectedConfigs.put("acks", "-1");
        expectedConfigs.put("linger.ms", "1000");
        Assert.assertEquals(expectedConfigs, Worker.producerConfigs(configWithOverrides));
    }

    @Test
    public void testConsumerConfigsWithoutOverrides() {
        Map<String, String> expectedConfigs = new HashMap<>(defaultConsumerConfigs);
        expectedConfigs.put("group.id", "connect-test");
        Assert.assertEquals(expectedConfigs, Worker.consumerConfigs(new ConnectorTaskId("test", 1), config));
    }

    @Test
    public void testConsumerConfigsWithOverrides() {
        Map<String, String> props = new HashMap<>(workerProps);
        props.put("consumer.auto.offset.reset", "latest");
        props.put("consumer.max.poll.records", "1000");
        WorkerConfig configWithOverrides = new StandaloneConfig(props);
        Map<String, String> expectedConfigs = new HashMap<>(defaultConsumerConfigs);
        expectedConfigs.put("group.id", "connect-test");
        expectedConfigs.put("auto.offset.reset", "latest");
        expectedConfigs.put("max.poll.records", "1000");
        Assert.assertEquals(expectedConfigs, Worker.consumerConfigs(new ConnectorTaskId("test", 1), configWithOverrides));
    }

    /* Name here needs to be unique as we are testing the aliasing mechanism */
    public static class WorkerTestConnector extends Connector {
        private static final ConfigDef CONFIG_DEF = new ConfigDef().define("configName", STRING, HIGH, "Test configName.");

        @Override
        public String version() {
            return "1.0";
        }

        @Override
        public void start(Map<String, String> props) {
        }

        @Override
        public Class<? extends Task> taskClass() {
            return null;
        }

        @Override
        public List<Map<String, String>> taskConfigs(int maxTasks) {
            return null;
        }

        @Override
        public void stop() {
        }

        @Override
        public ConfigDef config() {
            return WorkerTest.WorkerTestConnector.CONFIG_DEF;
        }
    }

    private static class TestSourceTask extends SourceTask {
        public TestSourceTask() {
        }

        @Override
        public String version() {
            return "1.0";
        }

        @Override
        public void start(Map<String, String> props) {
        }

        @Override
        public List<SourceRecord> poll() throws InterruptedException {
            return null;
        }

        @Override
        public void stop() {
        }
    }

    public static class TestConverter implements Converter {
        public Map<String, ?> configs;

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
            this.configs = configs;
        }

        @Override
        public byte[] fromConnectData(String topic, Schema schema, Object value) {
            return new byte[0];
        }

        @Override
        public SchemaAndValue toConnectData(String topic, byte[] value) {
            return null;
        }
    }

    public static class TestConfigurableConverter implements Configurable , Converter {
        public Map<String, ?> configs;

        public ConfigDef config() {
            return JsonConverterConfig.configDef();
        }

        @Override
        public void configure(Map<String, ?> configs) {
            this.configs = configs;
            new JsonConverterConfig(configs);// requires the `converter.type` config be set

        }

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
            this.configs = configs;
        }

        @Override
        public byte[] fromConnectData(String topic, Schema schema, Object value) {
            return new byte[0];
        }

        @Override
        public SchemaAndValue toConnectData(String topic, byte[] value) {
            return null;
        }
    }
}

