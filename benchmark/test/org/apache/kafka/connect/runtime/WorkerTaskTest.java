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


import TargetState.STARTED;
import TaskConfig.TASK_CLASS_CONFIG;
import TaskStatus.Listener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.runtime.WorkerTask.TaskMetricsGroup;
import org.apache.kafka.connect.runtime.errors.RetryWithToleranceOperator;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.kafka.connect.util.ConnectorTaskId;
import org.easymock.EasyMock;
import org.easymock.Mock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ WorkerTask.class })
@PowerMockIgnore("javax.management.*")
public class WorkerTaskTest {
    private static final Map<String, String> TASK_PROPS = new HashMap<>();

    static {
        WorkerTaskTest.TASK_PROPS.put(TASK_CLASS_CONFIG, WorkerTaskTest.TestSinkTask.class.getName());
    }

    private static final TaskConfig TASK_CONFIG = new TaskConfig(WorkerTaskTest.TASK_PROPS);

    private ConnectMetrics metrics;

    @Mock
    private Listener statusListener;

    @Mock
    private ClassLoader loader;

    RetryWithToleranceOperator retryWithToleranceOperator;

    @Test
    public void standardStartup() {
        ConnectorTaskId taskId = new ConnectorTaskId("foo", 0);
        WorkerTask workerTask = partialMockBuilder(WorkerTask.class).withConstructor(ConnectorTaskId.class, Listener.class, TargetState.class, ClassLoader.class, ConnectMetrics.class, RetryWithToleranceOperator.class).withArgs(taskId, statusListener, STARTED, loader, metrics, retryWithToleranceOperator).addMockedMethod("initialize").addMockedMethod("execute").addMockedMethod("close").createStrictMock();
        workerTask.initialize(WorkerTaskTest.TASK_CONFIG);
        expectLastCall();
        workerTask.execute();
        expectLastCall();
        statusListener.onStartup(taskId);
        expectLastCall();
        workerTask.close();
        expectLastCall();
        workerTask.releaseResources();
        EasyMock.expectLastCall();
        statusListener.onShutdown(taskId);
        expectLastCall();
        replay(workerTask);
        workerTask.initialize(WorkerTaskTest.TASK_CONFIG);
        workerTask.run();
        workerTask.stop();
        workerTask.awaitStop(1000L);
        verify(workerTask);
    }

    @Test
    public void stopBeforeStarting() {
        ConnectorTaskId taskId = new ConnectorTaskId("foo", 0);
        WorkerTask workerTask = partialMockBuilder(WorkerTask.class).withConstructor(ConnectorTaskId.class, Listener.class, TargetState.class, ClassLoader.class, ConnectMetrics.class, RetryWithToleranceOperator.class).withArgs(taskId, statusListener, STARTED, loader, metrics, retryWithToleranceOperator).addMockedMethod("initialize").addMockedMethod("execute").addMockedMethod("close").createStrictMock();
        workerTask.initialize(WorkerTaskTest.TASK_CONFIG);
        EasyMock.expectLastCall();
        workerTask.close();
        EasyMock.expectLastCall();
        workerTask.releaseResources();
        EasyMock.expectLastCall();
        replay(workerTask);
        workerTask.initialize(WorkerTaskTest.TASK_CONFIG);
        workerTask.stop();
        workerTask.awaitStop(1000L);
        // now run should not do anything
        workerTask.run();
        verify(workerTask);
    }

    @Test
    public void cancelBeforeStopping() throws Exception {
        ConnectorTaskId taskId = new ConnectorTaskId("foo", 0);
        WorkerTask workerTask = partialMockBuilder(WorkerTask.class).withConstructor(ConnectorTaskId.class, Listener.class, TargetState.class, ClassLoader.class, ConnectMetrics.class, RetryWithToleranceOperator.class).withArgs(taskId, statusListener, STARTED, loader, metrics, retryWithToleranceOperator).addMockedMethod("initialize").addMockedMethod("execute").addMockedMethod("close").createStrictMock();
        final CountDownLatch stopped = new CountDownLatch(1);
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    stopped.await();
                } catch (Exception e) {
                }
            }
        };
        workerTask.initialize(WorkerTaskTest.TASK_CONFIG);
        EasyMock.expectLastCall();
        workerTask.execute();
        expectLastCall().andAnswer(new org.easymock.IAnswer<Void>() {
            @Override
            public Void answer() throws Throwable {
                thread.start();
                return null;
            }
        });
        statusListener.onStartup(taskId);
        expectLastCall();
        workerTask.close();
        expectLastCall();
        workerTask.releaseResources();
        EasyMock.expectLastCall();
        // there should be no call to onShutdown()
        replay(workerTask);
        workerTask.initialize(WorkerTaskTest.TASK_CONFIG);
        workerTask.run();
        workerTask.stop();
        workerTask.cancel();
        stopped.countDown();
        thread.join();
        verify(workerTask);
    }

    @Test
    public void updateMetricsOnListenerEventsForStartupPauseResumeAndShutdown() {
        ConnectorTaskId taskId = new ConnectorTaskId("foo", 0);
        ConnectMetrics metrics = new MockConnectMetrics();
        TaskMetricsGroup group = new TaskMetricsGroup(taskId, metrics, statusListener);
        statusListener.onStartup(taskId);
        expectLastCall();
        statusListener.onPause(taskId);
        expectLastCall();
        statusListener.onResume(taskId);
        expectLastCall();
        statusListener.onShutdown(taskId);
        expectLastCall();
        replay(statusListener);
        group.onStartup(taskId);
        assertRunningMetric(group);
        group.onPause(taskId);
        assertPausedMetric(group);
        group.onResume(taskId);
        assertRunningMetric(group);
        group.onShutdown(taskId);
        assertStoppedMetric(group);
        verify(statusListener);
    }

    @Test
    public void updateMetricsOnListenerEventsForStartupPauseResumeAndFailure() {
        ConnectorTaskId taskId = new ConnectorTaskId("foo", 0);
        MockConnectMetrics metrics = new MockConnectMetrics();
        MockTime time = metrics.time();
        ConnectException error = new ConnectException("error");
        TaskMetricsGroup group = new TaskMetricsGroup(taskId, metrics, statusListener);
        statusListener.onStartup(taskId);
        expectLastCall();
        statusListener.onPause(taskId);
        expectLastCall();
        statusListener.onResume(taskId);
        expectLastCall();
        statusListener.onPause(taskId);
        expectLastCall();
        statusListener.onResume(taskId);
        expectLastCall();
        statusListener.onFailure(taskId, error);
        expectLastCall();
        statusListener.onShutdown(taskId);
        expectLastCall();
        replay(statusListener);
        time.sleep(1000L);
        group.onStartup(taskId);
        assertRunningMetric(group);
        time.sleep(2000L);
        group.onPause(taskId);
        assertPausedMetric(group);
        time.sleep(3000L);
        group.onResume(taskId);
        assertRunningMetric(group);
        time.sleep(4000L);
        group.onPause(taskId);
        assertPausedMetric(group);
        time.sleep(5000L);
        group.onResume(taskId);
        assertRunningMetric(group);
        time.sleep(6000L);
        group.onFailure(taskId, error);
        assertFailedMetric(group);
        time.sleep(7000L);
        group.onShutdown(taskId);
        assertStoppedMetric(group);
        verify(statusListener);
        long totalTime = 27000L;
        double pauseTimeRatio = ((double) (3000L + 5000L)) / totalTime;
        double runningTimeRatio = ((double) ((2000L + 4000L) + 6000L)) / totalTime;
        Assert.assertEquals(pauseTimeRatio, metrics.currentMetricValueAsDouble(group.metricGroup(), "pause-ratio"), 1.0E-6);
        Assert.assertEquals(runningTimeRatio, metrics.currentMetricValueAsDouble(group.metricGroup(), "running-ratio"), 1.0E-6);
    }

    private abstract static class TestSinkTask extends SinkTask {}
}

