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
package org.apache.kafka.connect.runtime.standalone;


import AbstractStatus.State;
import ConfigDef.Importance.HIGH;
import ConfigDef.Type.STRING;
import ConnectorConfig.CONNECTOR_CLASS_CONFIG;
import ConnectorConfig.NAME_CONFIG;
import Herder.Created;
import SinkConnectorConfig.TOPICS_CONFIG;
import TargetState.STARTED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigValue;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.connector.ConnectorContext;
import org.apache.kafka.connect.errors.AlreadyExistsException;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.NotFoundException;
import org.apache.kafka.connect.runtime.Herder;
import org.apache.kafka.connect.runtime.HerderConnectorContext;
import org.apache.kafka.connect.runtime.TaskStatus;
import org.apache.kafka.connect.runtime.Worker;
import org.apache.kafka.connect.runtime.WorkerConfigTransformer;
import org.apache.kafka.connect.runtime.WorkerConnector;
import org.apache.kafka.connect.runtime.distributed.ClusterConfigState;
import org.apache.kafka.connect.runtime.isolation.DelegatingClassLoader;
import org.apache.kafka.connect.runtime.isolation.PluginClassLoader;
import org.apache.kafka.connect.runtime.isolation.Plugins;
import org.apache.kafka.connect.runtime.rest.entities.ConnectorInfo;
import org.apache.kafka.connect.runtime.rest.entities.ConnectorType;
import org.apache.kafka.connect.runtime.rest.entities.TaskInfo;
import org.apache.kafka.connect.runtime.rest.errors.BadRequestException;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.storage.StatusBackingStore;
import org.apache.kafka.connect.util.Callback;
import org.apache.kafka.connect.util.ConnectorTaskId;
import org.apache.kafka.connect.util.FutureCallback;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@SuppressWarnings("unchecked")
@PrepareForTest({ StandaloneHerder.class, Plugins.class, WorkerConnector.class })
public class StandaloneHerderTest {
    private static final String CONNECTOR_NAME = "test";

    private static final List<String> TOPICS_LIST = Arrays.asList("topic1", "topic2");

    private static final String TOPICS_LIST_STR = "topic1,topic2";

    private static final int DEFAULT_MAX_TASKS = 1;

    private static final String WORKER_ID = "localhost:8083";

    private static final String KAFKA_CLUSTER_ID = "I4ZmrWqfT2e-upky_4fdPA";

    private enum SourceSink {

        SOURCE,
        SINK;}

    private StandaloneHerder herder;

    private Connector connector;

    @Mock
    protected Worker worker;

    @Mock
    protected WorkerConfigTransformer transformer;

    @Mock
    private Plugins plugins;

    @Mock
    private PluginClassLoader pluginLoader;

    @Mock
    private DelegatingClassLoader delegatingLoader;

    @Mock
    protected Callback<Herder.Created<ConnectorInfo>> createCallback;

    @Mock
    protected StatusBackingStore statusBackingStore;

    @Test
    public void testCreateSourceConnector() throws Exception {
        connector = PowerMock.createMock(StandaloneHerderTest.BogusSourceConnector.class);
        expectAdd(StandaloneHerderTest.SourceSink.SOURCE);
        Map<String, String> config = StandaloneHerderTest.connectorConfig(StandaloneHerderTest.SourceSink.SOURCE);
        Connector connectorMock = PowerMock.createMock(SourceConnector.class);
        expectConfigValidation(connectorMock, true, config);
        PowerMock.replayAll();
        herder.putConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, config, false, createCallback);
        PowerMock.verifyAll();
    }

    @Test
    public void testCreateConnectorFailedBasicValidation() {
        // Basic validation should be performed and return an error, but should still evaluate the connector's config
        connector = PowerMock.createMock(StandaloneHerderTest.BogusSourceConnector.class);
        Map<String, String> config = StandaloneHerderTest.connectorConfig(StandaloneHerderTest.SourceSink.SOURCE);
        config.remove(NAME_CONFIG);
        Connector connectorMock = PowerMock.createMock(SourceConnector.class);
        EasyMock.expect(worker.configTransformer()).andReturn(transformer).times(2);
        final Capture<Map<String, String>> configCapture = EasyMock.newCapture();
        EasyMock.expect(transformer.transform(EasyMock.capture(configCapture))).andAnswer(configCapture::getValue);
        EasyMock.expect(worker.getPlugins()).andReturn(plugins).times(3);
        EasyMock.expect(plugins.compareAndSwapLoaders(connectorMock)).andReturn(delegatingLoader);
        EasyMock.expect(plugins.newConnector(EasyMock.anyString())).andReturn(connectorMock);
        EasyMock.expect(connectorMock.config()).andStubReturn(new ConfigDef());
        ConfigValue validatedValue = new ConfigValue("foo.bar");
        EasyMock.expect(connectorMock.validate(config)).andReturn(new org.apache.kafka.common.config.Config(Collections.singletonList(validatedValue)));
        EasyMock.expect(Plugins.compareAndSwapLoaders(delegatingLoader)).andReturn(pluginLoader);
        createCallback.onCompletion(EasyMock.<BadRequestException>anyObject(), EasyMock.<Herder.Created<ConnectorInfo>>isNull());
        PowerMock.expectLastCall();
        PowerMock.replayAll();
        herder.putConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, config, false, createCallback);
        PowerMock.verifyAll();
    }

    @Test
    public void testCreateConnectorFailedCustomValidation() {
        connector = PowerMock.createMock(StandaloneHerderTest.BogusSourceConnector.class);
        Connector connectorMock = PowerMock.createMock(SourceConnector.class);
        EasyMock.expect(worker.configTransformer()).andReturn(transformer).times(2);
        final Capture<Map<String, String>> configCapture = EasyMock.newCapture();
        EasyMock.expect(transformer.transform(EasyMock.capture(configCapture))).andAnswer(configCapture::getValue);
        EasyMock.expect(worker.getPlugins()).andReturn(plugins).times(3);
        EasyMock.expect(plugins.compareAndSwapLoaders(connectorMock)).andReturn(delegatingLoader);
        EasyMock.expect(plugins.newConnector(EasyMock.anyString())).andReturn(connectorMock);
        ConfigDef configDef = new ConfigDef();
        configDef.define("foo.bar", STRING, HIGH, "foo.bar doc");
        EasyMock.expect(connectorMock.config()).andReturn(configDef);
        ConfigValue validatedValue = new ConfigValue("foo.bar");
        validatedValue.addErrorMessage("Failed foo.bar validation");
        Map<String, String> config = StandaloneHerderTest.connectorConfig(StandaloneHerderTest.SourceSink.SOURCE);
        EasyMock.expect(connectorMock.validate(config)).andReturn(new org.apache.kafka.common.config.Config(Collections.singletonList(validatedValue)));
        EasyMock.expect(Plugins.compareAndSwapLoaders(delegatingLoader)).andReturn(pluginLoader);
        createCallback.onCompletion(EasyMock.<BadRequestException>anyObject(), EasyMock.<Herder.Created<ConnectorInfo>>isNull());
        PowerMock.expectLastCall();
        PowerMock.replayAll();
        herder.putConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, config, false, createCallback);
        PowerMock.verifyAll();
    }

    @Test
    public void testCreateConnectorAlreadyExists() throws Exception {
        connector = PowerMock.createMock(StandaloneHerderTest.BogusSourceConnector.class);
        // First addition should succeed
        expectAdd(StandaloneHerderTest.SourceSink.SOURCE);
        Map<String, String> config = StandaloneHerderTest.connectorConfig(StandaloneHerderTest.SourceSink.SOURCE);
        Connector connectorMock = PowerMock.createMock(SourceConnector.class);
        expectConfigValidation(connectorMock, true, config, config);
        EasyMock.expect(worker.configTransformer()).andReturn(transformer).times(2);
        final Capture<Map<String, String>> configCapture = EasyMock.newCapture();
        EasyMock.expect(transformer.transform(EasyMock.capture(configCapture))).andAnswer(configCapture::getValue);
        EasyMock.expect(worker.getPlugins()).andReturn(plugins).times(2);
        EasyMock.expect(plugins.compareAndSwapLoaders(connectorMock)).andReturn(delegatingLoader);
        // No new connector is created
        EasyMock.expect(Plugins.compareAndSwapLoaders(delegatingLoader)).andReturn(pluginLoader);
        // Second should fail
        createCallback.onCompletion(EasyMock.<AlreadyExistsException>anyObject(), EasyMock.<Herder.Created<ConnectorInfo>>isNull());
        PowerMock.expectLastCall();
        PowerMock.replayAll();
        herder.putConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, config, false, createCallback);
        herder.putConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, config, false, createCallback);
        PowerMock.verifyAll();
    }

    @Test
    public void testCreateSinkConnector() throws Exception {
        connector = PowerMock.createMock(StandaloneHerderTest.BogusSinkConnector.class);
        expectAdd(StandaloneHerderTest.SourceSink.SINK);
        Map<String, String> config = StandaloneHerderTest.connectorConfig(StandaloneHerderTest.SourceSink.SINK);
        Connector connectorMock = PowerMock.createMock(SinkConnector.class);
        expectConfigValidation(connectorMock, true, config);
        PowerMock.replayAll();
        herder.putConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, config, false, createCallback);
        PowerMock.verifyAll();
    }

    @Test
    public void testDestroyConnector() throws Exception {
        connector = PowerMock.createMock(StandaloneHerderTest.BogusSourceConnector.class);
        expectAdd(StandaloneHerderTest.SourceSink.SOURCE);
        Map<String, String> config = StandaloneHerderTest.connectorConfig(StandaloneHerderTest.SourceSink.SOURCE);
        Connector connectorMock = PowerMock.createMock(SourceConnector.class);
        expectConfigValidation(connectorMock, true, config);
        EasyMock.expect(statusBackingStore.getAll(StandaloneHerderTest.CONNECTOR_NAME)).andReturn(Collections.<TaskStatus>emptyList());
        statusBackingStore.put(new org.apache.kafka.connect.runtime.ConnectorStatus(StandaloneHerderTest.CONNECTOR_NAME, State.DESTROYED, StandaloneHerderTest.WORKER_ID, 0));
        expectDestroy();
        PowerMock.replayAll();
        herder.putConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, config, false, createCallback);
        FutureCallback<Herder.Created<ConnectorInfo>> futureCb = new FutureCallback();
        herder.deleteConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, futureCb);
        futureCb.get(1000L, TimeUnit.MILLISECONDS);
        // Second deletion should fail since the connector is gone
        futureCb = new FutureCallback();
        herder.deleteConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, futureCb);
        try {
            futureCb.get(1000L, TimeUnit.MILLISECONDS);
            Assert.fail("Should have thrown NotFoundException");
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof NotFoundException));
        }
        PowerMock.verifyAll();
    }

    @Test
    public void testRestartConnector() throws Exception {
        expectAdd(StandaloneHerderTest.SourceSink.SOURCE);
        Map<String, String> config = StandaloneHerderTest.connectorConfig(StandaloneHerderTest.SourceSink.SOURCE);
        Connector connectorMock = PowerMock.createMock(SourceConnector.class);
        expectConfigValidation(connectorMock, true, config);
        worker.stopConnector(StandaloneHerderTest.CONNECTOR_NAME);
        EasyMock.expectLastCall().andReturn(true);
        worker.startConnector(EasyMock.eq(StandaloneHerderTest.CONNECTOR_NAME), EasyMock.eq(config), EasyMock.anyObject(HerderConnectorContext.class), EasyMock.eq(herder), EasyMock.eq(STARTED));
        EasyMock.expectLastCall().andReturn(true);
        PowerMock.replayAll();
        herder.putConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, config, false, createCallback);
        FutureCallback<Void> cb = new FutureCallback();
        herder.restartConnector(StandaloneHerderTest.CONNECTOR_NAME, cb);
        cb.get(1000L, TimeUnit.MILLISECONDS);
        PowerMock.verifyAll();
    }

    @Test
    public void testRestartConnectorFailureOnStart() throws Exception {
        expectAdd(StandaloneHerderTest.SourceSink.SOURCE);
        Map<String, String> config = StandaloneHerderTest.connectorConfig(StandaloneHerderTest.SourceSink.SOURCE);
        Connector connectorMock = PowerMock.createMock(SourceConnector.class);
        expectConfigValidation(connectorMock, true, config);
        worker.stopConnector(StandaloneHerderTest.CONNECTOR_NAME);
        EasyMock.expectLastCall().andReturn(true);
        worker.startConnector(EasyMock.eq(StandaloneHerderTest.CONNECTOR_NAME), EasyMock.eq(config), EasyMock.anyObject(HerderConnectorContext.class), EasyMock.eq(herder), EasyMock.eq(STARTED));
        EasyMock.expectLastCall().andReturn(false);
        PowerMock.replayAll();
        herder.putConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, config, false, createCallback);
        FutureCallback<Void> cb = new FutureCallback();
        herder.restartConnector(StandaloneHerderTest.CONNECTOR_NAME, cb);
        try {
            cb.get(1000L, TimeUnit.MILLISECONDS);
            Assert.fail();
        } catch (ExecutionException exception) {
            Assert.assertEquals(ConnectException.class, exception.getCause().getClass());
        }
        PowerMock.verifyAll();
    }

    @Test
    public void testRestartTask() throws Exception {
        ConnectorTaskId taskId = new ConnectorTaskId(StandaloneHerderTest.CONNECTOR_NAME, 0);
        expectAdd(StandaloneHerderTest.SourceSink.SOURCE);
        Map<String, String> connectorConfig = StandaloneHerderTest.connectorConfig(StandaloneHerderTest.SourceSink.SOURCE);
        Connector connectorMock = PowerMock.createMock(SourceConnector.class);
        expectConfigValidation(connectorMock, true, connectorConfig);
        worker.stopAndAwaitTask(taskId);
        EasyMock.expectLastCall();
        ClusterConfigState configState = new ClusterConfigState((-1), Collections.singletonMap(StandaloneHerderTest.CONNECTOR_NAME, 1), Collections.singletonMap(StandaloneHerderTest.CONNECTOR_NAME, connectorConfig), Collections.singletonMap(StandaloneHerderTest.CONNECTOR_NAME, STARTED), Collections.singletonMap(taskId, StandaloneHerderTest.taskConfig(StandaloneHerderTest.SourceSink.SOURCE)), new HashSet(), transformer);
        worker.startTask(taskId, configState, connectorConfig, StandaloneHerderTest.taskConfig(StandaloneHerderTest.SourceSink.SOURCE), herder, STARTED);
        EasyMock.expectLastCall().andReturn(true);
        PowerMock.replayAll();
        herder.putConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, connectorConfig, false, createCallback);
        FutureCallback<Void> cb = new FutureCallback();
        herder.restartTask(taskId, cb);
        cb.get(1000L, TimeUnit.MILLISECONDS);
        PowerMock.verifyAll();
    }

    @Test
    public void testRestartTaskFailureOnStart() throws Exception {
        ConnectorTaskId taskId = new ConnectorTaskId(StandaloneHerderTest.CONNECTOR_NAME, 0);
        expectAdd(StandaloneHerderTest.SourceSink.SOURCE);
        Map<String, String> connectorConfig = StandaloneHerderTest.connectorConfig(StandaloneHerderTest.SourceSink.SOURCE);
        Connector connectorMock = PowerMock.createMock(SourceConnector.class);
        expectConfigValidation(connectorMock, true, connectorConfig);
        worker.stopAndAwaitTask(taskId);
        EasyMock.expectLastCall();
        ClusterConfigState configState = new ClusterConfigState((-1), Collections.singletonMap(StandaloneHerderTest.CONNECTOR_NAME, 1), Collections.singletonMap(StandaloneHerderTest.CONNECTOR_NAME, connectorConfig), Collections.singletonMap(StandaloneHerderTest.CONNECTOR_NAME, STARTED), Collections.singletonMap(new ConnectorTaskId(StandaloneHerderTest.CONNECTOR_NAME, 0), StandaloneHerderTest.taskConfig(StandaloneHerderTest.SourceSink.SOURCE)), new HashSet(), transformer);
        worker.startTask(taskId, configState, connectorConfig, StandaloneHerderTest.taskConfig(StandaloneHerderTest.SourceSink.SOURCE), herder, STARTED);
        EasyMock.expectLastCall().andReturn(false);
        PowerMock.replayAll();
        herder.putConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, connectorConfig, false, createCallback);
        FutureCallback<Void> cb = new FutureCallback();
        herder.restartTask(taskId, cb);
        try {
            cb.get(1000L, TimeUnit.MILLISECONDS);
            Assert.fail("Expected restart callback to raise an exception");
        } catch (ExecutionException exception) {
            Assert.assertEquals(ConnectException.class, exception.getCause().getClass());
        }
        PowerMock.verifyAll();
    }

    @Test
    public void testCreateAndStop() throws Exception {
        connector = PowerMock.createMock(StandaloneHerderTest.BogusSourceConnector.class);
        expectAdd(StandaloneHerderTest.SourceSink.SOURCE);
        Map<String, String> connectorConfig = StandaloneHerderTest.connectorConfig(StandaloneHerderTest.SourceSink.SOURCE);
        Connector connectorMock = PowerMock.createMock(SourceConnector.class);
        expectConfigValidation(connectorMock, true, connectorConfig);
        // herder.stop() should stop any running connectors and tasks even if destroyConnector was not invoked
        expectStop();
        statusBackingStore.stop();
        EasyMock.expectLastCall();
        worker.stop();
        EasyMock.expectLastCall();
        PowerMock.replayAll();
        herder.putConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, connectorConfig, false, createCallback);
        herder.stop();
        PowerMock.verifyAll();
    }

    @Test
    public void testAccessors() throws Exception {
        Map<String, String> connConfig = StandaloneHerderTest.connectorConfig(StandaloneHerderTest.SourceSink.SOURCE);
        System.out.println(connConfig);
        Callback<Collection<String>> listConnectorsCb = PowerMock.createMock(Callback.class);
        Callback<ConnectorInfo> connectorInfoCb = PowerMock.createMock(Callback.class);
        Callback<Map<String, String>> connectorConfigCb = PowerMock.createMock(Callback.class);
        Callback<List<TaskInfo>> taskConfigsCb = PowerMock.createMock(Callback.class);
        // Check accessors with empty worker
        listConnectorsCb.onCompletion(null, Collections.EMPTY_SET);
        EasyMock.expectLastCall();
        connectorInfoCb.onCompletion(EasyMock.<NotFoundException>anyObject(), EasyMock.<ConnectorInfo>isNull());
        EasyMock.expectLastCall();
        connectorConfigCb.onCompletion(EasyMock.<NotFoundException>anyObject(), EasyMock.<Map<String, String>>isNull());
        EasyMock.expectLastCall();
        taskConfigsCb.onCompletion(EasyMock.<NotFoundException>anyObject(), EasyMock.<List<TaskInfo>>isNull());
        EasyMock.expectLastCall();
        // Create connector
        connector = PowerMock.createMock(StandaloneHerderTest.BogusSourceConnector.class);
        expectAdd(StandaloneHerderTest.SourceSink.SOURCE);
        expectConfigValidation(connector, true, connConfig);
        // Validate accessors with 1 connector
        listConnectorsCb.onCompletion(null, Collections.singleton(StandaloneHerderTest.CONNECTOR_NAME));
        EasyMock.expectLastCall();
        ConnectorInfo connInfo = new ConnectorInfo(StandaloneHerderTest.CONNECTOR_NAME, connConfig, Arrays.asList(new ConnectorTaskId(StandaloneHerderTest.CONNECTOR_NAME, 0)), ConnectorType.SOURCE);
        connectorInfoCb.onCompletion(null, connInfo);
        EasyMock.expectLastCall();
        connectorConfigCb.onCompletion(null, connConfig);
        EasyMock.expectLastCall();
        TaskInfo taskInfo = new TaskInfo(new ConnectorTaskId(StandaloneHerderTest.CONNECTOR_NAME, 0), StandaloneHerderTest.taskConfig(StandaloneHerderTest.SourceSink.SOURCE));
        taskConfigsCb.onCompletion(null, Arrays.asList(taskInfo));
        EasyMock.expectLastCall();
        PowerMock.replayAll();
        // All operations are synchronous for StandaloneHerder, so we don't need to actually wait after making each call
        herder.connectors(listConnectorsCb);
        herder.connectorInfo(StandaloneHerderTest.CONNECTOR_NAME, connectorInfoCb);
        herder.connectorConfig(StandaloneHerderTest.CONNECTOR_NAME, connectorConfigCb);
        herder.taskConfigs(StandaloneHerderTest.CONNECTOR_NAME, taskConfigsCb);
        herder.putConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, connConfig, false, createCallback);
        EasyMock.reset(transformer);
        EasyMock.expect(transformer.transform(EasyMock.eq(StandaloneHerderTest.CONNECTOR_NAME), EasyMock.anyObject())).andThrow(new AssertionError("Config transformation should not occur when requesting connector or task info")).anyTimes();
        EasyMock.replay(transformer);
        herder.connectors(listConnectorsCb);
        herder.connectorInfo(StandaloneHerderTest.CONNECTOR_NAME, connectorInfoCb);
        herder.connectorConfig(StandaloneHerderTest.CONNECTOR_NAME, connectorConfigCb);
        herder.taskConfigs(StandaloneHerderTest.CONNECTOR_NAME, taskConfigsCb);
        PowerMock.verifyAll();
    }

    @Test
    public void testPutConnectorConfig() throws Exception {
        Map<String, String> connConfig = StandaloneHerderTest.connectorConfig(StandaloneHerderTest.SourceSink.SOURCE);
        Map<String, String> newConnConfig = new HashMap<>(connConfig);
        newConnConfig.put("foo", "bar");
        Callback<Map<String, String>> connectorConfigCb = PowerMock.createMock(Callback.class);
        Callback<Herder.Created<ConnectorInfo>> putConnectorConfigCb = PowerMock.createMock(Callback.class);
        // Create
        connector = PowerMock.createMock(StandaloneHerderTest.BogusSourceConnector.class);
        expectAdd(StandaloneHerderTest.SourceSink.SOURCE);
        Connector connectorMock = PowerMock.createMock(SourceConnector.class);
        expectConfigValidation(connectorMock, true, connConfig);
        // Should get first config
        connectorConfigCb.onCompletion(null, connConfig);
        EasyMock.expectLastCall();
        // Update config, which requires stopping and restarting
        worker.stopConnector(StandaloneHerderTest.CONNECTOR_NAME);
        EasyMock.expectLastCall().andReturn(true);
        Capture<Map<String, String>> capturedConfig = EasyMock.newCapture();
        worker.startConnector(EasyMock.eq(StandaloneHerderTest.CONNECTOR_NAME), EasyMock.capture(capturedConfig), EasyMock.<ConnectorContext>anyObject(), EasyMock.eq(herder), EasyMock.eq(STARTED));
        EasyMock.expectLastCall().andReturn(true);
        EasyMock.expect(worker.isRunning(StandaloneHerderTest.CONNECTOR_NAME)).andReturn(true);
        // Generate same task config, which should result in no additional action to restart tasks
        EasyMock.expect(worker.connectorTaskConfigs(StandaloneHerderTest.CONNECTOR_NAME, new org.apache.kafka.connect.runtime.SourceConnectorConfig(plugins, newConnConfig))).andReturn(Collections.singletonList(StandaloneHerderTest.taskConfig(StandaloneHerderTest.SourceSink.SOURCE)));
        worker.isSinkConnector(StandaloneHerderTest.CONNECTOR_NAME);
        EasyMock.expectLastCall().andReturn(false);
        ConnectorInfo newConnInfo = new ConnectorInfo(StandaloneHerderTest.CONNECTOR_NAME, newConnConfig, Arrays.asList(new ConnectorTaskId(StandaloneHerderTest.CONNECTOR_NAME, 0)), ConnectorType.SOURCE);
        putConnectorConfigCb.onCompletion(null, new Herder.Created<>(false, newConnInfo));
        EasyMock.expectLastCall();
        // Should get new config
        expectConfigValidation(connectorMock, false, newConnConfig);
        connectorConfigCb.onCompletion(null, newConnConfig);
        EasyMock.expectLastCall();
        EasyMock.expect(worker.getPlugins()).andReturn(plugins).anyTimes();
        PowerMock.replayAll();
        herder.putConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, connConfig, false, createCallback);
        herder.connectorConfig(StandaloneHerderTest.CONNECTOR_NAME, connectorConfigCb);
        herder.putConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, newConnConfig, true, putConnectorConfigCb);
        Assert.assertEquals("bar", capturedConfig.getValue().get("foo"));
        herder.connectorConfig(StandaloneHerderTest.CONNECTOR_NAME, connectorConfigCb);
        PowerMock.verifyAll();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPutTaskConfigs() {
        Callback<Void> cb = PowerMock.createMock(Callback.class);
        PowerMock.replayAll();
        herder.putTaskConfigs(StandaloneHerderTest.CONNECTOR_NAME, Arrays.asList(Collections.singletonMap("config", "value")), cb);
        PowerMock.verifyAll();
    }

    @Test
    public void testCorruptConfig() {
        Map<String, String> config = new HashMap<>();
        config.put(NAME_CONFIG, StandaloneHerderTest.CONNECTOR_NAME);
        config.put(CONNECTOR_CLASS_CONFIG, StandaloneHerderTest.BogusSinkConnector.class.getName());
        config.put(TOPICS_CONFIG, StandaloneHerderTest.TOPICS_LIST_STR);
        Connector connectorMock = PowerMock.createMock(SinkConnector.class);
        String error = "This is an error in your config!";
        List<String> errors = new ArrayList<>(Collections.singletonList(error));
        String key = "foo.invalid.key";
        EasyMock.expect(connectorMock.validate(config)).andReturn(new org.apache.kafka.common.config.Config(Arrays.asList(new ConfigValue(key, null, Collections.emptyList(), errors))));
        ConfigDef configDef = new ConfigDef();
        configDef.define(key, STRING, HIGH, "");
        EasyMock.expect(worker.configTransformer()).andReturn(transformer).times(2);
        final Capture<Map<String, String>> configCapture = EasyMock.newCapture();
        EasyMock.expect(transformer.transform(EasyMock.capture(configCapture))).andAnswer(configCapture::getValue);
        EasyMock.expect(worker.getPlugins()).andReturn(plugins).times(3);
        EasyMock.expect(plugins.compareAndSwapLoaders(connectorMock)).andReturn(delegatingLoader);
        EasyMock.expect(worker.getPlugins()).andStubReturn(plugins);
        EasyMock.expect(plugins.newConnector(EasyMock.anyString())).andReturn(connectorMock);
        EasyMock.expect(connectorMock.config()).andStubReturn(configDef);
        EasyMock.expect(Plugins.compareAndSwapLoaders(delegatingLoader)).andReturn(pluginLoader);
        Callback<Herder.Created<ConnectorInfo>> callback = PowerMock.createMock(Callback.class);
        Capture<BadRequestException> capture = Capture.newInstance();
        callback.onCompletion(EasyMock.capture(capture), EasyMock.isNull(Created.class));
        PowerMock.replayAll();
        herder.putConnectorConfig(StandaloneHerderTest.CONNECTOR_NAME, config, true, callback);
        Assert.assertEquals(capture.getValue().getMessage(), ((("Connector configuration is invalid and contains the following 1 error(s):\n" + error) + "\n") + "You can also find the above list of errors at the endpoint `/{connectorType}/config/validate`"));
        PowerMock.verifyAll();
    }

    // We need to use a real class here due to some issue with mocking java.lang.Class
    private abstract class BogusSourceConnector extends SourceConnector {}

    private abstract class BogusSourceTask extends SourceTask {}

    private abstract class BogusSinkConnector extends SinkConnector {}

    private abstract class BogusSinkTask extends SourceTask {}
}

