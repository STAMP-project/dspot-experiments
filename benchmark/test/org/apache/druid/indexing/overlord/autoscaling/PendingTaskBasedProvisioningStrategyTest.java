/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.indexing.overlord.autoscaling;


import ScalingStats.EVENT;
import ScalingStats.ScalingEvent;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.druid.indexer.TaskLocation;
import org.apache.druid.indexer.TaskStatus;
import org.apache.druid.indexing.common.task.NoopTask;
import org.apache.druid.indexing.common.task.Task;
import org.apache.druid.indexing.overlord.RemoteTaskRunner;
import org.apache.druid.indexing.overlord.ZkWorker;
import org.apache.druid.indexing.overlord.config.RemoteTaskRunnerConfig;
import org.apache.druid.indexing.overlord.setup.WorkerBehaviorConfig;
import org.apache.druid.indexing.worker.TaskAnnouncement;
import org.apache.druid.indexing.worker.Worker;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.concurrent.Execs;
import org.apache.druid.java.util.emitter.EmittingLogger;
import org.apache.druid.java.util.emitter.service.ServiceEmitter;
import org.apache.druid.java.util.emitter.service.ServiceEventBuilder;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class PendingTaskBasedProvisioningStrategyTest {
    private AutoScaler autoScaler;

    private Task testTask;

    private PendingTaskBasedWorkerProvisioningStrategy strategy;

    private AtomicReference<WorkerBehaviorConfig> workerConfig;

    private ScheduledExecutorService executorService = Execs.scheduledSingleThreaded("test service");

    private static final String MIN_VERSION = "2014-01-00T00:01:00Z";

    private static final String INVALID_VERSION = "0";

    @Test
    public void testSuccessfulInitialMinWorkersProvision() {
        EasyMock.expect(autoScaler.getMinNumWorkers()).andReturn(3);
        EasyMock.expect(autoScaler.getMaxNumWorkers()).andReturn(5);
        EasyMock.expect(autoScaler.ipToIdLookup(EasyMock.anyObject())).andReturn(new ArrayList<String>());
        RemoteTaskRunner runner = EasyMock.createMock(RemoteTaskRunner.class);
        // No pending tasks
        EasyMock.expect(runner.getPendingTaskPayloads()).andReturn(new ArrayList());
        EasyMock.expect(runner.getWorkers()).andReturn(Collections.emptyList());
        EasyMock.expect(runner.getConfig()).andReturn(new RemoteTaskRunnerConfig());
        EasyMock.expect(autoScaler.provision()).andReturn(new AutoScalingData(Collections.singletonList("aNode"))).times(3);
        EasyMock.replay(runner, autoScaler);
        Provisioner provisioner = strategy.makeProvisioner(runner);
        boolean provisionedSomething = provisioner.doProvision();
        Assert.assertTrue(provisionedSomething);
        Assert.assertTrue(((provisioner.getStats().toList().size()) == 3));
        for (ScalingStats.ScalingEvent event : provisioner.getStats().toList()) {
            Assert.assertTrue(((event.getEvent()) == (EVENT.PROVISION)));
        }
    }

    @Test
    public void testSuccessfulMinWorkersProvision() {
        EasyMock.expect(autoScaler.getMinNumWorkers()).andReturn(3);
        EasyMock.expect(autoScaler.getMaxNumWorkers()).andReturn(5);
        EasyMock.expect(autoScaler.ipToIdLookup(EasyMock.anyObject())).andReturn(new ArrayList<String>());
        RemoteTaskRunner runner = EasyMock.createMock(RemoteTaskRunner.class);
        // No pending tasks
        EasyMock.expect(runner.getPendingTaskPayloads()).andReturn(new ArrayList());
        // 1 node already running, only provision 2 more.
        EasyMock.expect(runner.getWorkers()).andReturn(Collections.singletonList(toImmutable()));
        EasyMock.expect(runner.getConfig()).andReturn(new RemoteTaskRunnerConfig());
        EasyMock.expect(autoScaler.provision()).andReturn(new AutoScalingData(Collections.singletonList("aNode"))).times(2);
        EasyMock.replay(runner, autoScaler);
        Provisioner provisioner = strategy.makeProvisioner(runner);
        boolean provisionedSomething = provisioner.doProvision();
        Assert.assertTrue(provisionedSomething);
        Assert.assertTrue(((provisioner.getStats().toList().size()) == 2));
        for (ScalingStats.ScalingEvent event : provisioner.getStats().toList()) {
            Assert.assertTrue(((event.getEvent()) == (EVENT.PROVISION)));
        }
    }

    @Test
    public void testSuccessfulMinWorkersProvisionWithOldVersionNodeRunning() {
        EasyMock.expect(autoScaler.getMinNumWorkers()).andReturn(3);
        EasyMock.expect(autoScaler.getMaxNumWorkers()).andReturn(5);
        EasyMock.expect(autoScaler.ipToIdLookup(EasyMock.anyObject())).andReturn(new ArrayList<String>());
        RemoteTaskRunner runner = EasyMock.createMock(RemoteTaskRunner.class);
        // No pending tasks
        EasyMock.expect(runner.getPendingTaskPayloads()).andReturn(new ArrayList());
        // 1 node already running, only provision 2 more.
        EasyMock.expect(runner.getWorkers()).andReturn(// Invalid version node
        Arrays.asList(toImmutable(), toImmutable()));
        EasyMock.expect(runner.getConfig()).andReturn(new RemoteTaskRunnerConfig());
        EasyMock.expect(autoScaler.provision()).andReturn(new AutoScalingData(Collections.singletonList("aNode"))).times(2);
        EasyMock.replay(runner, autoScaler);
        Provisioner provisioner = strategy.makeProvisioner(runner);
        boolean provisionedSomething = provisioner.doProvision();
        Assert.assertTrue(provisionedSomething);
        Assert.assertTrue(((provisioner.getStats().toList().size()) == 2));
        for (ScalingStats.ScalingEvent event : provisioner.getStats().toList()) {
            Assert.assertTrue(((event.getEvent()) == (EVENT.PROVISION)));
        }
    }

    @Test
    public void testSomethingProvisioning() {
        EasyMock.expect(autoScaler.getMinNumWorkers()).andReturn(0).times(1);
        EasyMock.expect(autoScaler.getMaxNumWorkers()).andReturn(2).times(1);
        EasyMock.expect(autoScaler.ipToIdLookup(EasyMock.anyObject())).andReturn(new ArrayList<String>()).times(2);
        EasyMock.expect(autoScaler.provision()).andReturn(new AutoScalingData(Collections.singletonList("fake")));
        RemoteTaskRunner runner = EasyMock.createMock(RemoteTaskRunner.class);
        EasyMock.expect(runner.getPendingTaskPayloads()).andReturn(Collections.singletonList(NoopTask.create())).times(2);
        EasyMock.expect(runner.getWorkers()).andReturn(// Invalid version node
        Arrays.asList(toImmutable(), toImmutable())).times(2);
        EasyMock.expect(runner.getConfig()).andReturn(new RemoteTaskRunnerConfig()).times(1);
        EasyMock.replay(runner);
        EasyMock.replay(autoScaler);
        Provisioner provisioner = strategy.makeProvisioner(runner);
        boolean provisionedSomething = provisioner.doProvision();
        Assert.assertTrue(provisionedSomething);
        Assert.assertTrue(((provisioner.getStats().toList().size()) == 1));
        DateTime createdTime = provisioner.getStats().toList().get(0).getTimestamp();
        Assert.assertTrue(((provisioner.getStats().toList().get(0).getEvent()) == (EVENT.PROVISION)));
        provisionedSomething = provisioner.doProvision();
        Assert.assertFalse(provisionedSomething);
        Assert.assertTrue(((provisioner.getStats().toList().get(0).getEvent()) == (EVENT.PROVISION)));
        DateTime anotherCreatedTime = provisioner.getStats().toList().get(0).getTimestamp();
        Assert.assertTrue(createdTime.equals(anotherCreatedTime));
        EasyMock.verify(autoScaler);
        EasyMock.verify(runner);
    }

    @Test
    public void testProvisionAlert() throws Exception {
        ServiceEmitter emitter = EasyMock.createMock(ServiceEmitter.class);
        EmittingLogger.registerEmitter(emitter);
        emitter.emit(EasyMock.<ServiceEventBuilder>anyObject());
        EasyMock.expectLastCall();
        EasyMock.replay(emitter);
        EasyMock.expect(autoScaler.getMinNumWorkers()).andReturn(0).times(1);
        EasyMock.expect(autoScaler.getMaxNumWorkers()).andReturn(2).times(1);
        EasyMock.expect(autoScaler.ipToIdLookup(EasyMock.anyObject())).andReturn(new ArrayList<String>()).times(2);
        EasyMock.expect(autoScaler.terminateWithIds(EasyMock.anyObject())).andReturn(null);
        EasyMock.expect(autoScaler.provision()).andReturn(new AutoScalingData(Collections.singletonList("fake")));
        EasyMock.replay(autoScaler);
        RemoteTaskRunner runner = EasyMock.createMock(RemoteTaskRunner.class);
        EasyMock.expect(runner.getPendingTaskPayloads()).andReturn(Collections.singletonList(NoopTask.create())).times(2);
        EasyMock.expect(runner.getWorkers()).andReturn(// Invalid version node
        // Invalid version node
        Arrays.asList(toImmutable(), toImmutable(), toImmutable())).times(2);
        EasyMock.expect(runner.getConfig()).andReturn(new RemoteTaskRunnerConfig());
        EasyMock.replay(runner);
        Provisioner provisioner = strategy.makeProvisioner(runner);
        boolean provisionedSomething = provisioner.doProvision();
        Assert.assertTrue(provisionedSomething);
        Assert.assertTrue(((provisioner.getStats().toList().size()) == 1));
        DateTime createdTime = provisioner.getStats().toList().get(0).getTimestamp();
        Assert.assertTrue(((provisioner.getStats().toList().get(0).getEvent()) == (EVENT.PROVISION)));
        Thread.sleep(2000);
        provisionedSomething = provisioner.doProvision();
        Assert.assertFalse(provisionedSomething);
        Assert.assertTrue(((provisioner.getStats().toList().get(0).getEvent()) == (EVENT.PROVISION)));
        DateTime anotherCreatedTime = provisioner.getStats().toList().get(0).getTimestamp();
        Assert.assertTrue(createdTime.equals(anotherCreatedTime));
        EasyMock.verify(autoScaler);
        EasyMock.verify(emitter);
        EasyMock.verify(runner);
    }

    @Test
    public void testDoSuccessfulTerminate() {
        EasyMock.expect(autoScaler.getMinNumWorkers()).andReturn(0);
        EasyMock.expect(autoScaler.ipToIdLookup(EasyMock.anyObject())).andReturn(new ArrayList<String>());
        EasyMock.expect(autoScaler.terminate(EasyMock.anyObject())).andReturn(new AutoScalingData(new ArrayList()));
        EasyMock.replay(autoScaler);
        RemoteTaskRunner runner = EasyMock.createMock(RemoteTaskRunner.class);
        EasyMock.expect(runner.getPendingTasks()).andReturn(Collections.singletonList(new org.apache.druid.indexing.overlord.RemoteTaskRunnerWorkItem(testTask.getId(), testTask.getType(), null, TaskLocation.unknown(), testTask.getDataSource()).withQueueInsertionTime(DateTimes.nowUtc()))).times(2);
        EasyMock.expect(runner.getWorkers()).andReturn(Collections.singletonList(toImmutable())).times(2);
        EasyMock.expect(runner.markWorkersLazy(EasyMock.anyObject(), EasyMock.anyInt())).andReturn(Collections.singletonList(getWorker()));
        EasyMock.expect(runner.getLazyWorkers()).andReturn(new ArrayList());
        EasyMock.replay(runner);
        Provisioner provisioner = strategy.makeProvisioner(runner);
        boolean terminatedSomething = provisioner.doTerminate();
        Assert.assertTrue(terminatedSomething);
        Assert.assertTrue(((provisioner.getStats().toList().size()) == 1));
        Assert.assertTrue(((provisioner.getStats().toList().get(0).getEvent()) == (EVENT.TERMINATE)));
        EasyMock.verify(autoScaler);
    }

    @Test
    public void testSomethingTerminating() {
        EasyMock.expect(autoScaler.getMinNumWorkers()).andReturn(0).times(1);
        EasyMock.expect(autoScaler.ipToIdLookup(EasyMock.anyObject())).andReturn(Collections.singletonList("ip")).times(2);
        EasyMock.expect(autoScaler.terminate(EasyMock.anyObject())).andReturn(new AutoScalingData(Collections.singletonList("ip")));
        EasyMock.replay(autoScaler);
        RemoteTaskRunner runner = EasyMock.createMock(RemoteTaskRunner.class);
        EasyMock.expect(runner.getWorkers()).andReturn(Collections.singletonList(toImmutable())).times(2);
        EasyMock.expect(runner.getLazyWorkers()).andReturn(new ArrayList()).times(2);
        EasyMock.expect(runner.markWorkersLazy(EasyMock.anyObject(), EasyMock.anyInt())).andReturn(Collections.singletonList(getWorker()));
        EasyMock.replay(runner);
        Provisioner provisioner = strategy.makeProvisioner(runner);
        boolean terminatedSomething = provisioner.doTerminate();
        Assert.assertTrue(terminatedSomething);
        Assert.assertTrue(((provisioner.getStats().toList().size()) == 1));
        Assert.assertTrue(((provisioner.getStats().toList().get(0).getEvent()) == (EVENT.TERMINATE)));
        terminatedSomething = provisioner.doTerminate();
        Assert.assertFalse(terminatedSomething);
        Assert.assertTrue(((provisioner.getStats().toList().size()) == 1));
        Assert.assertTrue(((provisioner.getStats().toList().get(0).getEvent()) == (EVENT.TERMINATE)));
        EasyMock.verify(autoScaler);
        EasyMock.verify(runner);
    }

    @Test
    public void testNoActionNeeded() {
        EasyMock.reset(autoScaler);
        EasyMock.expect(autoScaler.getMinNumWorkers()).andReturn(0);
        EasyMock.expect(autoScaler.ipToIdLookup(EasyMock.anyObject())).andReturn(Collections.singletonList("ip"));
        EasyMock.replay(autoScaler);
        RemoteTaskRunner runner = EasyMock.createMock(RemoteTaskRunner.class);
        EasyMock.expect(runner.getPendingTaskPayloads()).andReturn(Collections.singletonList(((Task) (NoopTask.create())))).times(1);
        EasyMock.expect(runner.getWorkers()).andReturn(Arrays.asList(toImmutable(), toImmutable())).times(2);
        EasyMock.expect(runner.getConfig()).andReturn(new RemoteTaskRunnerConfig());
        EasyMock.expect(runner.getLazyWorkers()).andReturn(new ArrayList());
        EasyMock.expect(runner.markWorkersLazy(EasyMock.anyObject(), EasyMock.anyInt())).andReturn(Collections.emptyList());
        EasyMock.replay(runner);
        Provisioner provisioner = strategy.makeProvisioner(runner);
        boolean terminatedSomething = provisioner.doTerminate();
        Assert.assertFalse(terminatedSomething);
        EasyMock.verify(autoScaler);
        EasyMock.reset(autoScaler);
        EasyMock.expect(autoScaler.getMinNumWorkers()).andReturn(0);
        EasyMock.expect(autoScaler.getMaxNumWorkers()).andReturn(2);
        EasyMock.expect(autoScaler.ipToIdLookup(EasyMock.anyObject())).andReturn(Collections.singletonList("ip"));
        EasyMock.replay(autoScaler);
        boolean provisionedSomething = provisioner.doProvision();
        Assert.assertFalse(provisionedSomething);
        EasyMock.verify(autoScaler);
        EasyMock.verify(runner);
    }

    @Test
    public void testMinCountIncrease() {
        // Don't terminate anything
        EasyMock.reset(autoScaler);
        EasyMock.expect(autoScaler.getMinNumWorkers()).andReturn(0);
        EasyMock.expect(autoScaler.ipToIdLookup(EasyMock.anyObject())).andReturn(Collections.singletonList("ip"));
        EasyMock.replay(autoScaler);
        RemoteTaskRunner runner = EasyMock.createMock(RemoteTaskRunner.class);
        EasyMock.expect(runner.getPendingTaskPayloads()).andReturn(Collections.emptyList()).times(2);
        EasyMock.expect(runner.getWorkers()).andReturn(Collections.singletonList(toImmutable())).times(3);
        EasyMock.expect(runner.getConfig()).andReturn(new RemoteTaskRunnerConfig()).times(2);
        EasyMock.expect(runner.getLazyWorkers()).andReturn(new ArrayList());
        EasyMock.expect(runner.markWorkersLazy(EasyMock.anyObject(), EasyMock.anyInt())).andReturn(Collections.emptyList());
        EasyMock.replay(runner);
        Provisioner provisioner = strategy.makeProvisioner(runner);
        boolean terminatedSomething = provisioner.doTerminate();
        Assert.assertFalse(terminatedSomething);
        EasyMock.verify(autoScaler);
        // Don't provision anything
        EasyMock.reset(autoScaler);
        EasyMock.expect(autoScaler.getMinNumWorkers()).andReturn(0);
        EasyMock.expect(autoScaler.getMaxNumWorkers()).andReturn(2);
        EasyMock.expect(autoScaler.ipToIdLookup(EasyMock.anyObject())).andReturn(Collections.singletonList("ip"));
        EasyMock.replay(autoScaler);
        boolean provisionedSomething = provisioner.doProvision();
        Assert.assertFalse(provisionedSomething);
        EasyMock.verify(autoScaler);
        EasyMock.reset(autoScaler);
        // Increase minNumWorkers
        EasyMock.expect(autoScaler.getMinNumWorkers()).andReturn(3);
        EasyMock.expect(autoScaler.getMaxNumWorkers()).andReturn(5);
        EasyMock.expect(autoScaler.ipToIdLookup(EasyMock.anyObject())).andReturn(Collections.singletonList("ip"));
        EasyMock.expect(autoScaler.provision()).andReturn(new AutoScalingData(Collections.singletonList("h3")));
        // Should provision two new workers
        EasyMock.expect(autoScaler.provision()).andReturn(new AutoScalingData(Collections.singletonList("h4")));
        EasyMock.replay(autoScaler);
        provisionedSomething = provisioner.doProvision();
        Assert.assertTrue(provisionedSomething);
        EasyMock.verify(autoScaler);
        EasyMock.verify(runner);
    }

    @Test
    public void testNullWorkerConfig() {
        workerConfig.set(null);
        EasyMock.replay(autoScaler);
        RemoteTaskRunner runner = EasyMock.createMock(RemoteTaskRunner.class);
        EasyMock.expect(runner.getPendingTaskPayloads()).andReturn(Collections.singletonList(NoopTask.create())).times(1);
        EasyMock.expect(runner.getWorkers()).andReturn(Collections.singletonList(toImmutable())).times(2);
        EasyMock.replay(runner);
        Provisioner provisioner = strategy.makeProvisioner(runner);
        boolean terminatedSomething = provisioner.doTerminate();
        boolean provisionedSomething = provisioner.doProvision();
        Assert.assertFalse(terminatedSomething);
        Assert.assertFalse(provisionedSomething);
        EasyMock.verify(autoScaler);
        EasyMock.verify(runner);
    }

    private static class TestZkWorker extends ZkWorker {
        private final Task testTask;

        public TestZkWorker(Task testTask) {
            this(testTask, "http", "host", "ip", PendingTaskBasedProvisioningStrategyTest.MIN_VERSION);
        }

        public TestZkWorker(Task testTask, String scheme, String host, String ip, String version) {
            this(testTask, scheme, host, ip, version, 1);
        }

        public TestZkWorker(Task testTask, String scheme, String host, String ip, String version, int capacity) {
            super(new Worker(scheme, host, ip, capacity, version), null, new DefaultObjectMapper());
            this.testTask = testTask;
        }

        @Override
        public Map<String, TaskAnnouncement> getRunningTasks() {
            if ((testTask) == null) {
                return new HashMap<>();
            }
            return ImmutableMap.of(testTask.getId(), TaskAnnouncement.create(testTask, TaskStatus.running(testTask.getId()), TaskLocation.unknown()));
        }
    }
}

