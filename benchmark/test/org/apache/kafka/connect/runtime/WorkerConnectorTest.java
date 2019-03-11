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


import ConnectorConfig.CONNECTOR_CLASS_CONFIG;
import ConnectorConfig.NAME_CONFIG;
import ConnectorStatus.Listener;
import TargetState.PAUSED;
import TargetState.STARTED;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.connect.connector.Connector;
import org.apache.kafka.connect.connector.ConnectorContext;
import org.apache.kafka.connect.runtime.isolation.Plugins;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class WorkerConnectorTest extends EasyMockSupport {
    private static final String VERSION = "1.1";

    public static final String CONNECTOR = "connector";

    public static final Map<String, String> CONFIG = new HashMap<>();

    static {
        WorkerConnectorTest.CONFIG.put(CONNECTOR_CLASS_CONFIG, WorkerConnectorTest.TestConnector.class.getName());
        WorkerConnectorTest.CONFIG.put(NAME_CONFIG, WorkerConnectorTest.CONNECTOR);
    }

    public ConnectorConfig connectorConfig;

    public MockConnectMetrics metrics;

    @Mock
    Plugins plugins;

    @Mock
    Connector connector;

    @Mock
    ConnectorContext ctx;

    @Mock
    Listener listener;

    @Test
    public void testInitializeFailure() {
        RuntimeException exception = new RuntimeException();
        connector.version();
        expectLastCall().andReturn(WorkerConnectorTest.VERSION);
        connector.initialize(EasyMock.notNull(ConnectorContext.class));
        expectLastCall().andThrow(exception);
        listener.onFailure(WorkerConnectorTest.CONNECTOR, exception);
        expectLastCall();
        listener.onShutdown(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        replayAll();
        WorkerConnector workerConnector = new WorkerConnector(WorkerConnectorTest.CONNECTOR, connector, ctx, metrics, listener);
        workerConnector.initialize(connectorConfig);
        assertFailedMetric(workerConnector);
        workerConnector.shutdown();
        assertStoppedMetric(workerConnector);
        verifyAll();
    }

    @Test
    public void testFailureIsFinalState() {
        RuntimeException exception = new RuntimeException();
        connector.version();
        expectLastCall().andReturn(WorkerConnectorTest.VERSION);
        connector.initialize(EasyMock.notNull(ConnectorContext.class));
        expectLastCall().andThrow(exception);
        listener.onFailure(WorkerConnectorTest.CONNECTOR, exception);
        expectLastCall();
        // expect no call to onStartup() after failure
        listener.onShutdown(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        replayAll();
        WorkerConnector workerConnector = new WorkerConnector(WorkerConnectorTest.CONNECTOR, connector, ctx, metrics, listener);
        workerConnector.initialize(connectorConfig);
        assertFailedMetric(workerConnector);
        workerConnector.transitionTo(STARTED);
        assertFailedMetric(workerConnector);
        workerConnector.shutdown();
        assertStoppedMetric(workerConnector);
        verifyAll();
    }

    @Test
    public void testStartupAndShutdown() {
        connector.version();
        expectLastCall().andReturn(WorkerConnectorTest.VERSION);
        connector.initialize(EasyMock.notNull(ConnectorContext.class));
        expectLastCall();
        connector.start(WorkerConnectorTest.CONFIG);
        expectLastCall();
        listener.onStartup(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        connector.stop();
        expectLastCall();
        listener.onShutdown(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        replayAll();
        WorkerConnector workerConnector = new WorkerConnector(WorkerConnectorTest.CONNECTOR, connector, ctx, metrics, listener);
        workerConnector.initialize(connectorConfig);
        assertInitializedMetric(workerConnector);
        workerConnector.transitionTo(STARTED);
        assertRunningMetric(workerConnector);
        workerConnector.shutdown();
        assertStoppedMetric(workerConnector);
        verifyAll();
    }

    @Test
    public void testStartupAndPause() {
        connector.version();
        expectLastCall().andReturn(WorkerConnectorTest.VERSION);
        connector.initialize(EasyMock.notNull(ConnectorContext.class));
        expectLastCall();
        connector.start(WorkerConnectorTest.CONFIG);
        expectLastCall();
        listener.onStartup(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        connector.stop();
        expectLastCall();
        listener.onPause(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        listener.onShutdown(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        replayAll();
        WorkerConnector workerConnector = new WorkerConnector(WorkerConnectorTest.CONNECTOR, connector, ctx, metrics, listener);
        workerConnector.initialize(connectorConfig);
        assertInitializedMetric(workerConnector);
        workerConnector.transitionTo(STARTED);
        assertRunningMetric(workerConnector);
        workerConnector.transitionTo(PAUSED);
        assertPausedMetric(workerConnector);
        workerConnector.shutdown();
        assertStoppedMetric(workerConnector);
        verifyAll();
    }

    @Test
    public void testOnResume() {
        connector.version();
        expectLastCall().andReturn(WorkerConnectorTest.VERSION);
        connector.initialize(EasyMock.notNull(ConnectorContext.class));
        expectLastCall();
        listener.onPause(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        connector.start(WorkerConnectorTest.CONFIG);
        expectLastCall();
        listener.onResume(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        connector.stop();
        expectLastCall();
        listener.onShutdown(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        replayAll();
        WorkerConnector workerConnector = new WorkerConnector(WorkerConnectorTest.CONNECTOR, connector, ctx, metrics, listener);
        workerConnector.initialize(connectorConfig);
        assertInitializedMetric(workerConnector);
        workerConnector.transitionTo(PAUSED);
        assertPausedMetric(workerConnector);
        workerConnector.transitionTo(STARTED);
        assertRunningMetric(workerConnector);
        workerConnector.shutdown();
        assertStoppedMetric(workerConnector);
        verifyAll();
    }

    @Test
    public void testStartupPaused() {
        connector.version();
        expectLastCall().andReturn(WorkerConnectorTest.VERSION);
        connector.initialize(EasyMock.notNull(ConnectorContext.class));
        expectLastCall();
        // connector never gets started
        listener.onPause(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        listener.onShutdown(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        replayAll();
        WorkerConnector workerConnector = new WorkerConnector(WorkerConnectorTest.CONNECTOR, connector, ctx, metrics, listener);
        workerConnector.initialize(connectorConfig);
        assertInitializedMetric(workerConnector);
        workerConnector.transitionTo(PAUSED);
        assertPausedMetric(workerConnector);
        workerConnector.shutdown();
        assertStoppedMetric(workerConnector);
        verifyAll();
    }

    @Test
    public void testStartupFailure() {
        RuntimeException exception = new RuntimeException();
        connector.version();
        expectLastCall().andReturn(WorkerConnectorTest.VERSION);
        connector.initialize(EasyMock.notNull(ConnectorContext.class));
        expectLastCall();
        connector.start(WorkerConnectorTest.CONFIG);
        expectLastCall().andThrow(exception);
        listener.onFailure(WorkerConnectorTest.CONNECTOR, exception);
        expectLastCall();
        listener.onShutdown(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        replayAll();
        WorkerConnector workerConnector = new WorkerConnector(WorkerConnectorTest.CONNECTOR, connector, ctx, metrics, listener);
        workerConnector.initialize(connectorConfig);
        assertInitializedMetric(workerConnector);
        workerConnector.transitionTo(STARTED);
        assertFailedMetric(workerConnector);
        workerConnector.shutdown();
        assertStoppedMetric(workerConnector);
        verifyAll();
    }

    @Test
    public void testShutdownFailure() {
        RuntimeException exception = new RuntimeException();
        connector.version();
        expectLastCall().andReturn(WorkerConnectorTest.VERSION);
        connector.initialize(EasyMock.notNull(ConnectorContext.class));
        expectLastCall();
        connector.start(WorkerConnectorTest.CONFIG);
        expectLastCall();
        listener.onStartup(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        connector.stop();
        expectLastCall().andThrow(exception);
        listener.onFailure(WorkerConnectorTest.CONNECTOR, exception);
        expectLastCall();
        replayAll();
        WorkerConnector workerConnector = new WorkerConnector(WorkerConnectorTest.CONNECTOR, connector, ctx, metrics, listener);
        workerConnector.initialize(connectorConfig);
        assertInitializedMetric(workerConnector);
        workerConnector.transitionTo(STARTED);
        assertRunningMetric(workerConnector);
        workerConnector.shutdown();
        assertFailedMetric(workerConnector);
        verifyAll();
    }

    @Test
    public void testTransitionStartedToStarted() {
        connector.version();
        expectLastCall().andReturn(WorkerConnectorTest.VERSION);
        connector.initialize(EasyMock.notNull(ConnectorContext.class));
        expectLastCall();
        connector.start(WorkerConnectorTest.CONFIG);
        expectLastCall();
        // expect only one call to onStartup()
        listener.onStartup(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        connector.stop();
        expectLastCall();
        listener.onShutdown(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        replayAll();
        WorkerConnector workerConnector = new WorkerConnector(WorkerConnectorTest.CONNECTOR, connector, ctx, metrics, listener);
        workerConnector.initialize(connectorConfig);
        assertInitializedMetric(workerConnector);
        workerConnector.transitionTo(STARTED);
        assertRunningMetric(workerConnector);
        workerConnector.transitionTo(STARTED);
        assertRunningMetric(workerConnector);
        workerConnector.shutdown();
        assertStoppedMetric(workerConnector);
        verifyAll();
    }

    @Test
    public void testTransitionPausedToPaused() {
        connector.version();
        expectLastCall().andReturn(WorkerConnectorTest.VERSION);
        connector.initialize(EasyMock.notNull(ConnectorContext.class));
        expectLastCall();
        connector.start(WorkerConnectorTest.CONFIG);
        expectLastCall();
        listener.onStartup(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        connector.stop();
        expectLastCall();
        listener.onPause(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        listener.onShutdown(WorkerConnectorTest.CONNECTOR);
        expectLastCall();
        replayAll();
        WorkerConnector workerConnector = new WorkerConnector(WorkerConnectorTest.CONNECTOR, connector, ctx, metrics, listener);
        workerConnector.initialize(connectorConfig);
        assertInitializedMetric(workerConnector);
        workerConnector.transitionTo(STARTED);
        assertRunningMetric(workerConnector);
        workerConnector.transitionTo(PAUSED);
        assertPausedMetric(workerConnector);
        workerConnector.transitionTo(PAUSED);
        assertPausedMetric(workerConnector);
        workerConnector.shutdown();
        assertStoppedMetric(workerConnector);
        verifyAll();
    }

    private abstract static class TestConnector extends Connector {}
}

