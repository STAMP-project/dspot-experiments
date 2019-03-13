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
package org.apache.druid.indexing.overlord.hrtr;


import NodeType.MIDDLE_MANAGER;
import TaskState.SUCCESS;
import WorkerHolder.Listener;
import WorkerNodeService.DISCOVERY_SERVICE_KEY;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.curator.framework.CuratorFramework;
import org.apache.druid.common.guava.DSuppliers;
import org.apache.druid.discovery.DiscoveryDruidNode;
import org.apache.druid.discovery.DruidNodeDiscovery;
import org.apache.druid.discovery.DruidNodeDiscoveryProvider;
import org.apache.druid.discovery.NodeType;
import org.apache.druid.discovery.WorkerNodeService;
import org.apache.druid.indexer.TaskLocation;
import org.apache.druid.indexer.TaskStatus;
import org.apache.druid.indexing.common.task.NoopTask;
import org.apache.druid.indexing.common.task.Task;
import org.apache.druid.indexing.overlord.TaskRunnerWorkItem;
import org.apache.druid.indexing.overlord.TaskStorage;
import org.apache.druid.indexing.overlord.config.HttpRemoteTaskRunnerConfig;
import org.apache.druid.indexing.overlord.setup.DefaultWorkerBehaviorConfig;
import org.apache.druid.indexing.worker.TaskAnnouncement;
import org.apache.druid.indexing.worker.Worker;
import org.apache.druid.java.util.http.client.HttpClient;
import org.apache.druid.segment.TestHelper;
import org.apache.druid.server.DruidNode;
import org.apache.druid.server.initialization.ZkPathsConfig;
import org.easymock.EasyMock;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class HttpRemoteTaskRunnerTest {
    /* Simulates startup of Overlord and Workers being discovered with no previously known tasks. Fresh tasks are given
    and expected to be completed.
     */
    @Test(timeout = 60000L)
    public void testFreshStart() throws Exception {
        HttpRemoteTaskRunnerTest.TestDruidNodeDiscovery druidNodeDiscovery = new HttpRemoteTaskRunnerTest.TestDruidNodeDiscovery();
        DruidNodeDiscoveryProvider druidNodeDiscoveryProvider = EasyMock.createMock(DruidNodeDiscoveryProvider.class);
        EasyMock.expect(druidNodeDiscoveryProvider.getForNodeType(MIDDLE_MANAGER)).andReturn(druidNodeDiscovery);
        EasyMock.replay(druidNodeDiscoveryProvider);
        HttpRemoteTaskRunner taskRunner = new HttpRemoteTaskRunner(TestHelper.makeJsonMapper(), new HttpRemoteTaskRunnerConfig() {
            @Override
            public int getPendingTasksRunnerNumThreads() {
                return 3;
            }
        }, EasyMock.createNiceMock(HttpClient.class), DSuppliers.of(new java.util.concurrent.atomic.AtomicReference(DefaultWorkerBehaviorConfig.defaultConfig())), new org.apache.druid.indexing.overlord.autoscaling.NoopProvisioningStrategy(), druidNodeDiscoveryProvider, EasyMock.createNiceMock(TaskStorage.class), EasyMock.createNiceMock(CuratorFramework.class), new org.apache.druid.server.initialization.IndexerZkConfig(new ZkPathsConfig(), null, null, null, null)) {
            @Override
            protected WorkerHolder createWorkerHolder(ObjectMapper smileMapper, HttpClient httpClient, HttpRemoteTaskRunnerConfig config, ScheduledExecutorService workersSyncExec, WorkerHolder.Listener listener, Worker worker) {
                return HttpRemoteTaskRunnerTest.createWorkerHolder(smileMapper, httpClient, config, workersSyncExec, listener, worker, ImmutableList.of(), ImmutableMap.of(), new AtomicInteger(), ImmutableSet.of());
            }
        };
        taskRunner.start();
        DiscoveryDruidNode druidNode1 = new DiscoveryDruidNode(new DruidNode("service", "host1", false, 8080, null, true, false), NodeType.MIDDLE_MANAGER, ImmutableMap.of(DISCOVERY_SERVICE_KEY, new WorkerNodeService("ip1", 2, "0")));
        DiscoveryDruidNode druidNode2 = new DiscoveryDruidNode(new DruidNode("service", "host2", false, 8080, null, true, false), NodeType.MIDDLE_MANAGER, ImmutableMap.of(DISCOVERY_SERVICE_KEY, new WorkerNodeService("ip2", 2, "0")));
        druidNodeDiscovery.listener.nodesAdded(ImmutableList.of(druidNode1, druidNode2));
        int numTasks = 8;
        List<Future<TaskStatus>> futures = new ArrayList<>();
        for (int i = 0; i < numTasks; i++) {
            futures.add(taskRunner.run(NoopTask.create(("task-id-" + i), 0)));
        }
        for (Future<TaskStatus> future : futures) {
            Assert.assertTrue(future.get().isSuccess());
        }
        Assert.assertEquals(numTasks, taskRunner.getKnownTasks().size());
        Assert.assertEquals(numTasks, taskRunner.getCompletedTasks().size());
    }

    /* Simulates one task not getting acknowledged to be running after assigning it to a worker. But, other tasks are
    successfully assigned to other worker and get completed.
     */
    @Test(timeout = 60000L)
    public void testOneStuckTaskAssignmentDoesntBlockOthers() throws Exception {
        HttpRemoteTaskRunnerTest.TestDruidNodeDiscovery druidNodeDiscovery = new HttpRemoteTaskRunnerTest.TestDruidNodeDiscovery();
        DruidNodeDiscoveryProvider druidNodeDiscoveryProvider = EasyMock.createMock(DruidNodeDiscoveryProvider.class);
        EasyMock.expect(druidNodeDiscoveryProvider.getForNodeType(MIDDLE_MANAGER)).andReturn(druidNodeDiscovery);
        EasyMock.replay(druidNodeDiscoveryProvider);
        Task task1 = NoopTask.create("task-id-1", 0);
        Task task2 = NoopTask.create("task-id-2", 0);
        Task task3 = NoopTask.create("task-id-3", 0);
        HttpRemoteTaskRunner taskRunner = new HttpRemoteTaskRunner(TestHelper.makeJsonMapper(), new HttpRemoteTaskRunnerConfig() {
            @Override
            public int getPendingTasksRunnerNumThreads() {
                return 3;
            }
        }, EasyMock.createNiceMock(HttpClient.class), DSuppliers.of(new java.util.concurrent.atomic.AtomicReference(DefaultWorkerBehaviorConfig.defaultConfig())), new org.apache.druid.indexing.overlord.autoscaling.NoopProvisioningStrategy(), druidNodeDiscoveryProvider, EasyMock.createNiceMock(TaskStorage.class), EasyMock.createNiceMock(CuratorFramework.class), new org.apache.druid.server.initialization.IndexerZkConfig(new ZkPathsConfig(), null, null, null, null)) {
            @Override
            protected WorkerHolder createWorkerHolder(ObjectMapper smileMapper, HttpClient httpClient, HttpRemoteTaskRunnerConfig config, ScheduledExecutorService workersSyncExec, WorkerHolder.Listener listener, Worker worker) {
                return // no announcements would be received for task1
                HttpRemoteTaskRunnerTest.createWorkerHolder(smileMapper, httpClient, config, workersSyncExec, listener, worker, ImmutableList.of(), ImmutableMap.of(task1, ImmutableList.of()), new AtomicInteger(), ImmutableSet.of());
            }
        };
        taskRunner.start();
        DiscoveryDruidNode druidNode1 = new DiscoveryDruidNode(new DruidNode("service", "host1", false, 8080, null, true, false), NodeType.MIDDLE_MANAGER, ImmutableMap.of(DISCOVERY_SERVICE_KEY, new WorkerNodeService("ip1", 2, "0")));
        DiscoveryDruidNode druidNode2 = new DiscoveryDruidNode(new DruidNode("service", "host2", false, 8080, null, true, false), NodeType.MIDDLE_MANAGER, ImmutableMap.of(DISCOVERY_SERVICE_KEY, new WorkerNodeService("ip2", 2, "0")));
        druidNodeDiscovery.listener.nodesAdded(ImmutableList.of(druidNode1, druidNode2));
        taskRunner.run(task1);
        Future<TaskStatus> future2 = taskRunner.run(task2);
        Future<TaskStatus> future3 = taskRunner.run(task3);
        Assert.assertTrue(future2.get().isSuccess());
        Assert.assertTrue(future3.get().isSuccess());
        Assert.assertEquals(task1.getId(), Iterables.getOnlyElement(taskRunner.getPendingTasks()).getTaskId());
    }

    /* Simulates restart of the Overlord where taskRunner, on start, discovers workers with prexisting tasks. */
    @Test(timeout = 60000L)
    public void testTaskRunnerRestart() throws Exception {
        HttpRemoteTaskRunnerTest.TestDruidNodeDiscovery druidNodeDiscovery = new HttpRemoteTaskRunnerTest.TestDruidNodeDiscovery();
        DruidNodeDiscoveryProvider druidNodeDiscoveryProvider = EasyMock.createMock(DruidNodeDiscoveryProvider.class);
        EasyMock.expect(druidNodeDiscoveryProvider.getForNodeType(MIDDLE_MANAGER)).andReturn(druidNodeDiscovery);
        EasyMock.replay(druidNodeDiscoveryProvider);
        ConcurrentMap<String, HttpRemoteTaskRunnerTest.CustomFunction> workerHolders = new ConcurrentHashMap<>();
        Task task1 = NoopTask.create("task-id-1", 0);
        Task task2 = NoopTask.create("task-id-2", 0);
        Task task3 = NoopTask.create("task-id-3", 0);
        Task task4 = NoopTask.create("task-id-4", 0);
        Task task5 = NoopTask.create("task-id-5", 0);
        TaskStorage taskStorageMock = EasyMock.createStrictMock(TaskStorage.class);
        EasyMock.expect(taskStorageMock.getStatus(task1.getId())).andReturn(Optional.absent());
        EasyMock.expect(taskStorageMock.getStatus(task2.getId())).andReturn(Optional.absent()).times(2);
        EasyMock.expect(taskStorageMock.getStatus(task3.getId())).andReturn(Optional.of(TaskStatus.running(task3.getId())));
        EasyMock.expect(taskStorageMock.getStatus(task4.getId())).andReturn(Optional.of(TaskStatus.running(task4.getId())));
        EasyMock.expect(taskStorageMock.getStatus(task5.getId())).andReturn(Optional.of(TaskStatus.success(task5.getId())));
        EasyMock.replay(taskStorageMock);
        HttpRemoteTaskRunner taskRunner = new HttpRemoteTaskRunner(TestHelper.makeJsonMapper(), new HttpRemoteTaskRunnerConfig() {
            @Override
            public int getPendingTasksRunnerNumThreads() {
                return 3;
            }
        }, EasyMock.createNiceMock(HttpClient.class), DSuppliers.of(new java.util.concurrent.atomic.AtomicReference(DefaultWorkerBehaviorConfig.defaultConfig())), new org.apache.druid.indexing.overlord.autoscaling.NoopProvisioningStrategy(), druidNodeDiscoveryProvider, taskStorageMock, EasyMock.createNiceMock(CuratorFramework.class), new org.apache.druid.server.initialization.IndexerZkConfig(new ZkPathsConfig(), null, null, null, null)) {
            @Override
            protected WorkerHolder createWorkerHolder(ObjectMapper smileMapper, HttpClient httpClient, HttpRemoteTaskRunnerConfig config, ScheduledExecutorService workersSyncExec, WorkerHolder.Listener listener, Worker worker) {
                if (workerHolders.containsKey(worker.getHost())) {
                    return workerHolders.get(worker.getHost()).apply(smileMapper, httpClient, config, workersSyncExec, listener, worker);
                } else {
                    throw new org.apache.druid.java.util.common.ISE("No WorkerHolder for [%s].", worker.getHost());
                }
            }
        };
        taskRunner.start();
        DiscoveryDruidNode druidNode = new DiscoveryDruidNode(new DruidNode("service", "host", false, 1234, null, true, false), NodeType.MIDDLE_MANAGER, ImmutableMap.of(DISCOVERY_SERVICE_KEY, new WorkerNodeService("ip1", 2, "0")));
        AtomicInteger ticks = new AtomicInteger();
        Set<String> taskShutdowns = new HashSet<>();
        workerHolders.put("host:1234", ( mapper, httpClient, config, exec, listener, worker) -> HttpRemoteTaskRunnerTest.createWorkerHolder(mapper, httpClient, config, exec, listener, worker, ImmutableList.of(TaskAnnouncement.create(task1, TaskStatus.success(task1.getId()), TaskLocation.create("host", 1234, 1235)), TaskAnnouncement.create(task2, TaskStatus.running(task2.getId()), TaskLocation.create("host", 1234, 1235)), TaskAnnouncement.create(task2, TaskStatus.success(task2.getId()), TaskLocation.create("host", 1234, 1235)), TaskAnnouncement.create(task3, TaskStatus.success(task3.getId()), TaskLocation.create("host", 1234, 1235)), TaskAnnouncement.create(task4, TaskStatus.running(task4.getId()), TaskLocation.create("host", 1234, 1235)), TaskAnnouncement.create(task5, TaskStatus.running(task5.getId()), TaskLocation.create("host", 1234, 1235))), ImmutableMap.of(), ticks, taskShutdowns));
        druidNodeDiscovery.listener.nodesAdded(ImmutableList.of(druidNode));
        while ((ticks.get()) < 1) {
            Thread.sleep(100);
        } 
        EasyMock.verify(taskStorageMock);
        Assert.assertEquals(ImmutableSet.of(task2.getId(), task5.getId()), taskShutdowns);
        Assert.assertTrue(taskRunner.getPendingTasks().isEmpty());
        TaskRunnerWorkItem item = Iterables.getOnlyElement(taskRunner.getRunningTasks());
        Assert.assertEquals(task4.getId(), item.getTaskId());
        Assert.assertTrue(taskRunner.run(task3).get().isSuccess());
        Assert.assertEquals(2, taskRunner.getKnownTasks().size());
    }

    @Test(timeout = 60000L)
    public void testWorkerDisapperAndReappearBeforeItsCleanup() throws Exception {
        HttpRemoteTaskRunnerTest.TestDruidNodeDiscovery druidNodeDiscovery = new HttpRemoteTaskRunnerTest.TestDruidNodeDiscovery();
        DruidNodeDiscoveryProvider druidNodeDiscoveryProvider = EasyMock.createMock(DruidNodeDiscoveryProvider.class);
        EasyMock.expect(druidNodeDiscoveryProvider.getForNodeType(MIDDLE_MANAGER)).andReturn(druidNodeDiscovery);
        EasyMock.replay(druidNodeDiscoveryProvider);
        ConcurrentMap<String, HttpRemoteTaskRunnerTest.CustomFunction> workerHolders = new ConcurrentHashMap<>();
        HttpRemoteTaskRunner taskRunner = new HttpRemoteTaskRunner(TestHelper.makeJsonMapper(), new HttpRemoteTaskRunnerConfig() {
            @Override
            public int getPendingTasksRunnerNumThreads() {
                return 3;
            }
        }, EasyMock.createNiceMock(HttpClient.class), DSuppliers.of(new java.util.concurrent.atomic.AtomicReference(DefaultWorkerBehaviorConfig.defaultConfig())), new org.apache.druid.indexing.overlord.autoscaling.NoopProvisioningStrategy(), druidNodeDiscoveryProvider, EasyMock.createNiceMock(TaskStorage.class), EasyMock.createNiceMock(CuratorFramework.class), new org.apache.druid.server.initialization.IndexerZkConfig(new ZkPathsConfig(), null, null, null, null)) {
            @Override
            protected WorkerHolder createWorkerHolder(ObjectMapper smileMapper, HttpClient httpClient, HttpRemoteTaskRunnerConfig config, ScheduledExecutorService workersSyncExec, WorkerHolder.Listener listener, Worker worker) {
                if (workerHolders.containsKey(worker.getHost())) {
                    return workerHolders.get(worker.getHost()).apply(smileMapper, httpClient, config, workersSyncExec, listener, worker);
                } else {
                    throw new org.apache.druid.java.util.common.ISE("No WorkerHolder for [%s].", worker.getHost());
                }
            }
        };
        taskRunner.start();
        Task task1 = NoopTask.create("task-id-1", 0);
        Task task2 = NoopTask.create("task-id-2", 0);
        DiscoveryDruidNode druidNode = new DiscoveryDruidNode(new DruidNode("service", "host", false, 1234, null, true, false), NodeType.MIDDLE_MANAGER, ImmutableMap.of(DISCOVERY_SERVICE_KEY, new WorkerNodeService("ip1", 2, "0")));
        workerHolders.put("host:1234", ( mapper, httpClient, config, exec, listener, worker) -> HttpRemoteTaskRunnerTest.createWorkerHolder(mapper, httpClient, config, exec, listener, worker, ImmutableList.of(), ImmutableMap.of(task1, ImmutableList.of(TaskAnnouncement.create(task1, TaskStatus.running(task1.getId()), TaskLocation.unknown()), TaskAnnouncement.create(task1, TaskStatus.running(task1.getId()), TaskLocation.create("host", 1234, 1235)), TaskAnnouncement.create(task1, TaskStatus.success(task1.getId()), TaskLocation.create("host", 1234, 1235))), task2, ImmutableList.of(TaskAnnouncement.create(task2, TaskStatus.running(task2.getId()), TaskLocation.unknown()), TaskAnnouncement.create(task2, TaskStatus.running(task2.getId()), TaskLocation.create("host", 1234, 1235)))), new AtomicInteger(), ImmutableSet.of()));
        druidNodeDiscovery.listener.nodesAdded(ImmutableList.of(druidNode));
        Future<TaskStatus> future1 = taskRunner.run(task1);
        Future<TaskStatus> future2 = taskRunner.run(task2);
        while ((taskRunner.getPendingTasks().size()) > 0) {
            Thread.sleep(100);
        } 
        druidNodeDiscovery.listener.nodesRemoved(ImmutableList.of(druidNode));
        workerHolders.put("host:1234", ( mapper, httpClient, config, exec, listener, worker) -> HttpRemoteTaskRunnerTest.createWorkerHolder(mapper, httpClient, config, exec, listener, worker, ImmutableList.of(TaskAnnouncement.create(task1, TaskStatus.success(task1.getId()), TaskLocation.create("host", 1234, 1235)), TaskAnnouncement.create(task2, TaskStatus.running(task2.getId()), TaskLocation.create("host", 1234, 1235)), TaskAnnouncement.create(task2, TaskStatus.success(task2.getId()), TaskLocation.create("host", 1234, 1235))), ImmutableMap.of(), new AtomicInteger(), ImmutableSet.of()));
        druidNodeDiscovery.listener.nodesAdded(ImmutableList.of(druidNode));
        Assert.assertTrue(future1.get().isSuccess());
        Assert.assertTrue(future2.get().isSuccess());
    }

    @Test(timeout = 60000L)
    public void testWorkerDisapperAndReappearAfterItsCleanup() throws Exception {
        HttpRemoteTaskRunnerTest.TestDruidNodeDiscovery druidNodeDiscovery = new HttpRemoteTaskRunnerTest.TestDruidNodeDiscovery();
        DruidNodeDiscoveryProvider druidNodeDiscoveryProvider = EasyMock.createMock(DruidNodeDiscoveryProvider.class);
        EasyMock.expect(druidNodeDiscoveryProvider.getForNodeType(MIDDLE_MANAGER)).andReturn(druidNodeDiscovery);
        EasyMock.replay(druidNodeDiscoveryProvider);
        ConcurrentMap<String, HttpRemoteTaskRunnerTest.CustomFunction> workerHolders = new ConcurrentHashMap<>();
        HttpRemoteTaskRunner taskRunner = new HttpRemoteTaskRunner(TestHelper.makeJsonMapper(), new HttpRemoteTaskRunnerConfig() {
            @Override
            public Period getTaskCleanupTimeout() {
                return Period.millis(1);
            }
        }, EasyMock.createNiceMock(HttpClient.class), DSuppliers.of(new java.util.concurrent.atomic.AtomicReference(DefaultWorkerBehaviorConfig.defaultConfig())), new org.apache.druid.indexing.overlord.autoscaling.NoopProvisioningStrategy(), druidNodeDiscoveryProvider, EasyMock.createNiceMock(TaskStorage.class), EasyMock.createNiceMock(CuratorFramework.class), new org.apache.druid.server.initialization.IndexerZkConfig(new ZkPathsConfig(), null, null, null, null)) {
            @Override
            protected WorkerHolder createWorkerHolder(ObjectMapper smileMapper, HttpClient httpClient, HttpRemoteTaskRunnerConfig config, ScheduledExecutorService workersSyncExec, WorkerHolder.Listener listener, Worker worker) {
                if (workerHolders.containsKey(worker.getHost())) {
                    return workerHolders.get(worker.getHost()).apply(smileMapper, httpClient, config, workersSyncExec, listener, worker);
                } else {
                    throw new org.apache.druid.java.util.common.ISE("No WorkerHolder for [%s].", worker.getHost());
                }
            }
        };
        taskRunner.start();
        Task task1 = NoopTask.create("task-id-1", 0);
        Task task2 = NoopTask.create("task-id-2", 0);
        DiscoveryDruidNode druidNode = new DiscoveryDruidNode(new DruidNode("service", "host", false, 1234, null, true, false), NodeType.MIDDLE_MANAGER, ImmutableMap.of(DISCOVERY_SERVICE_KEY, new WorkerNodeService("ip1", 2, "0")));
        workerHolders.put("host:1234", ( mapper, httpClient, config, exec, listener, worker) -> HttpRemoteTaskRunnerTest.createWorkerHolder(mapper, httpClient, config, exec, listener, worker, ImmutableList.of(), ImmutableMap.of(task1, ImmutableList.of(TaskAnnouncement.create(task1, TaskStatus.running(task1.getId()), TaskLocation.unknown()), TaskAnnouncement.create(task1, TaskStatus.running(task1.getId()), TaskLocation.create("host", 1234, 1235))), task2, ImmutableList.of(TaskAnnouncement.create(task2, TaskStatus.running(task2.getId()), TaskLocation.unknown()), TaskAnnouncement.create(task2, TaskStatus.running(task2.getId()), TaskLocation.create("host", 1234, 1235)))), new AtomicInteger(), ImmutableSet.of()));
        druidNodeDiscovery.listener.nodesAdded(ImmutableList.of(druidNode));
        Future<TaskStatus> future1 = taskRunner.run(task1);
        Future<TaskStatus> future2 = taskRunner.run(task2);
        while ((taskRunner.getPendingTasks().size()) > 0) {
            Thread.sleep(100);
        } 
        druidNodeDiscovery.listener.nodesRemoved(ImmutableList.of(druidNode));
        Assert.assertTrue(future1.get().isFailure());
        Assert.assertTrue(future2.get().isFailure());
        AtomicInteger ticks = new AtomicInteger();
        Set<String> actualShutdowns = new org.eclipse.jetty.util.ConcurrentHashSet();
        workerHolders.put("host:1234", ( mapper, httpClient, config, exec, listener, worker) -> HttpRemoteTaskRunnerTest.createWorkerHolder(mapper, httpClient, config, exec, listener, worker, ImmutableList.of(TaskAnnouncement.create(task1, TaskStatus.success(task1.getId()), TaskLocation.create("host", 1234, 1235)), TaskAnnouncement.create(task2, TaskStatus.running(task2.getId()), TaskLocation.create("host", 1234, 1235))), ImmutableMap.of(), ticks, actualShutdowns));
        druidNodeDiscovery.listener.nodesAdded(ImmutableList.of(druidNode));
        while ((ticks.get()) < 1) {
            Thread.sleep(100);
        } 
        Assert.assertEquals(ImmutableSet.of(task2.getId()), actualShutdowns);
        Assert.assertTrue(taskRunner.run(task1).get().isFailure());
        Assert.assertTrue(taskRunner.run(task2).get().isFailure());
    }

    @Test(timeout = 60000L)
    public void testMarkWorkersLazy() throws Exception {
        HttpRemoteTaskRunnerTest.TestDruidNodeDiscovery druidNodeDiscovery = new HttpRemoteTaskRunnerTest.TestDruidNodeDiscovery();
        DruidNodeDiscoveryProvider druidNodeDiscoveryProvider = EasyMock.createMock(DruidNodeDiscoveryProvider.class);
        EasyMock.expect(druidNodeDiscoveryProvider.getForNodeType(MIDDLE_MANAGER)).andReturn(druidNodeDiscovery);
        EasyMock.replay(druidNodeDiscoveryProvider);
        Task task1 = NoopTask.create("task-id-1", 0);
        Task task2 = NoopTask.create("task-id-2", 0);
        ConcurrentMap<String, HttpRemoteTaskRunnerTest.CustomFunction> workerHolders = new ConcurrentHashMap<>();
        HttpRemoteTaskRunner taskRunner = new HttpRemoteTaskRunner(TestHelper.makeJsonMapper(), new HttpRemoteTaskRunnerConfig() {
            @Override
            public int getPendingTasksRunnerNumThreads() {
                return 3;
            }
        }, EasyMock.createNiceMock(HttpClient.class), DSuppliers.of(new java.util.concurrent.atomic.AtomicReference(DefaultWorkerBehaviorConfig.defaultConfig())), new org.apache.druid.indexing.overlord.autoscaling.NoopProvisioningStrategy(), druidNodeDiscoveryProvider, EasyMock.createNiceMock(TaskStorage.class), EasyMock.createNiceMock(CuratorFramework.class), new org.apache.druid.server.initialization.IndexerZkConfig(new ZkPathsConfig(), null, null, null, null)) {
            @Override
            protected WorkerHolder createWorkerHolder(ObjectMapper smileMapper, HttpClient httpClient, HttpRemoteTaskRunnerConfig config, ScheduledExecutorService workersSyncExec, WorkerHolder.Listener listener, Worker worker) {
                if (workerHolders.containsKey(worker.getHost())) {
                    return workerHolders.get(worker.getHost()).apply(smileMapper, httpClient, config, workersSyncExec, listener, worker);
                } else {
                    throw new org.apache.druid.java.util.common.ISE("No WorkerHolder for [%s].", worker.getHost());
                }
            }
        };
        taskRunner.start();
        AtomicInteger ticks = new AtomicInteger();
        DiscoveryDruidNode druidNode1 = new DiscoveryDruidNode(new DruidNode("service", "host1", false, 8080, null, true, false), NodeType.MIDDLE_MANAGER, ImmutableMap.of(DISCOVERY_SERVICE_KEY, new WorkerNodeService("ip1", 1, "0")));
        workerHolders.put("host1:8080", ( mapper, httpClient, config, exec, listener, worker) -> HttpRemoteTaskRunnerTest.createWorkerHolder(mapper, httpClient, config, exec, listener, worker, ImmutableList.of(), ImmutableMap.of(task1, ImmutableList.of(TaskAnnouncement.create(task1, TaskStatus.running(task1.getId()), TaskLocation.unknown()), TaskAnnouncement.create(task1, TaskStatus.running(task1.getId()), TaskLocation.create("host1", 8080, (-1))))), ticks, ImmutableSet.of()));
        druidNodeDiscovery.listener.nodesAdded(ImmutableList.of(druidNode1));
        taskRunner.run(task1);
        while ((ticks.get()) < 1) {
            Thread.sleep(100);
        } 
        DiscoveryDruidNode druidNode2 = new DiscoveryDruidNode(new DruidNode("service", "host2", false, 8080, null, true, false), NodeType.MIDDLE_MANAGER, ImmutableMap.of(DISCOVERY_SERVICE_KEY, new WorkerNodeService("ip2", 1, "0")));
        workerHolders.put("host2:8080", ( mapper, httpClient, config, exec, listener, worker) -> HttpRemoteTaskRunnerTest.createWorkerHolder(mapper, httpClient, config, exec, listener, worker, ImmutableList.of(), ImmutableMap.of(task2, ImmutableList.of()), ticks, ImmutableSet.of()));
        druidNodeDiscovery.listener.nodesAdded(ImmutableList.of(druidNode2));
        taskRunner.run(task2);
        while ((ticks.get()) < 2) {
            Thread.sleep(100);
        } 
        DiscoveryDruidNode druidNode3 = new DiscoveryDruidNode(new DruidNode("service", "host3", false, 8080, null, true, false), NodeType.MIDDLE_MANAGER, ImmutableMap.of(DISCOVERY_SERVICE_KEY, new WorkerNodeService("ip2", 1, "0")));
        workerHolders.put("host3:8080", ( mapper, httpClient, config, exec, listener, worker) -> HttpRemoteTaskRunnerTest.createWorkerHolder(mapper, httpClient, config, exec, listener, worker, ImmutableList.of(), ImmutableMap.of(), new AtomicInteger(), ImmutableSet.of()));
        druidNodeDiscovery.listener.nodesAdded(ImmutableList.of(druidNode3));
        Assert.assertEquals(task1.getId(), Iterables.getOnlyElement(taskRunner.getRunningTasks()).getTaskId());
        Assert.assertEquals(task2.getId(), Iterables.getOnlyElement(taskRunner.getPendingTasks()).getTaskId());
        Assert.assertEquals("host3:8080", Iterables.getOnlyElement(taskRunner.markWorkersLazy(Predicates.alwaysTrue(), Integer.MAX_VALUE)).getHost());
    }

    /* Task goes PENDING -> RUNNING -> SUCCESS and few more useless notifications in between. */
    @Test
    public void testTaskAddedOrUpdated1() throws Exception {
        Task task = NoopTask.create("task");
        List<Object> listenerNotificationsAccumulator = new ArrayList<>();
        HttpRemoteTaskRunner taskRunner = createTaskRunnerForTestTaskAddedOrUpdated(EasyMock.createStrictMock(TaskStorage.class), listenerNotificationsAccumulator);
        WorkerHolder workerHolder = EasyMock.createMock(WorkerHolder.class);
        EasyMock.expect(workerHolder.getWorker()).andReturn(new Worker("http", "worker", "127.0.0.1", 1, "v1")).anyTimes();
        workerHolder.setLastCompletedTaskTime(EasyMock.anyObject());
        workerHolder.resetContinuouslyFailedTasksCount();
        EasyMock.expect(workerHolder.getContinuouslyFailedTasksCount()).andReturn(0);
        EasyMock.replay(workerHolder);
        Future<TaskStatus> future = taskRunner.run(task);
        Assert.assertEquals(task.getId(), Iterables.getOnlyElement(taskRunner.getPendingTasks()).getTaskId());
        // RUNNING notification from worker
        taskRunner.taskAddedOrUpdated(TaskAnnouncement.create(task, TaskStatus.running(task.getId()), TaskLocation.create("worker", 1000, 1001)), workerHolder);
        Assert.assertEquals(task.getId(), Iterables.getOnlyElement(taskRunner.getRunningTasks()).getTaskId());
        // Another RUNNING notification from worker, notifying change in location
        taskRunner.taskAddedOrUpdated(TaskAnnouncement.create(task, TaskStatus.running(task.getId()), TaskLocation.create("worker", 1, 2)), workerHolder);
        Assert.assertEquals(task.getId(), Iterables.getOnlyElement(taskRunner.getRunningTasks()).getTaskId());
        // Redundant RUNNING notification from worker, ignored
        taskRunner.taskAddedOrUpdated(TaskAnnouncement.create(task, TaskStatus.running(task.getId()), TaskLocation.create("worker", 1, 2)), workerHolder);
        Assert.assertEquals(task.getId(), Iterables.getOnlyElement(taskRunner.getRunningTasks()).getTaskId());
        // Another "rogue-worker" reports running it, and gets asked to shutdown the task
        WorkerHolder rogueWorkerHolder = EasyMock.createMock(WorkerHolder.class);
        EasyMock.expect(rogueWorkerHolder.getWorker()).andReturn(new Worker("http", "rogue-worker", "127.0.0.1", 5, "v1")).anyTimes();
        rogueWorkerHolder.shutdownTask(task.getId());
        EasyMock.replay(rogueWorkerHolder);
        taskRunner.taskAddedOrUpdated(TaskAnnouncement.create(task, TaskStatus.running(task.getId()), TaskLocation.create("rogue-worker", 1, 2)), rogueWorkerHolder);
        Assert.assertEquals(task.getId(), Iterables.getOnlyElement(taskRunner.getRunningTasks()).getTaskId());
        EasyMock.verify(rogueWorkerHolder);
        // "rogue-worker" reports FAILURE for the task, ignored
        rogueWorkerHolder = EasyMock.createMock(WorkerHolder.class);
        EasyMock.expect(rogueWorkerHolder.getWorker()).andReturn(new Worker("http", "rogue-worker", "127.0.0.1", 5, "v1")).anyTimes();
        EasyMock.replay(rogueWorkerHolder);
        taskRunner.taskAddedOrUpdated(TaskAnnouncement.create(task, TaskStatus.failure(task.getId()), TaskLocation.create("rogue-worker", 1, 2)), rogueWorkerHolder);
        Assert.assertEquals(task.getId(), Iterables.getOnlyElement(taskRunner.getRunningTasks()).getTaskId());
        EasyMock.verify(rogueWorkerHolder);
        // workers sends SUCCESS notification, task is marked SUCCESS now.
        taskRunner.taskAddedOrUpdated(TaskAnnouncement.create(task, TaskStatus.success(task.getId()), TaskLocation.create("worker", 1, 2)), workerHolder);
        Assert.assertEquals(task.getId(), Iterables.getOnlyElement(taskRunner.getCompletedTasks()).getTaskId());
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        // "rogue-worker" reports running it, and gets asked to shutdown the task
        rogueWorkerHolder = EasyMock.createMock(WorkerHolder.class);
        EasyMock.expect(rogueWorkerHolder.getWorker()).andReturn(new Worker("http", "rogue-worker", "127.0.0.1", 5, "v1")).anyTimes();
        rogueWorkerHolder.shutdownTask(task.getId());
        EasyMock.replay(rogueWorkerHolder);
        taskRunner.taskAddedOrUpdated(TaskAnnouncement.create(task, TaskStatus.running(task.getId()), TaskLocation.create("rogue-worker", 1, 2)), rogueWorkerHolder);
        Assert.assertEquals(task.getId(), Iterables.getOnlyElement(taskRunner.getCompletedTasks()).getTaskId());
        EasyMock.verify(rogueWorkerHolder);
        // "rogue-worker" reports FAILURE for the tasks, ignored
        rogueWorkerHolder = EasyMock.createMock(WorkerHolder.class);
        EasyMock.expect(rogueWorkerHolder.getWorker()).andReturn(new Worker("http", "rogue-worker", "127.0.0.1", 5, "v1")).anyTimes();
        EasyMock.replay(rogueWorkerHolder);
        taskRunner.taskAddedOrUpdated(TaskAnnouncement.create(task, TaskStatus.failure(task.getId()), TaskLocation.create("rogue-worker", 1, 2)), rogueWorkerHolder);
        Assert.assertEquals(task.getId(), Iterables.getOnlyElement(taskRunner.getCompletedTasks()).getTaskId());
        EasyMock.verify(rogueWorkerHolder);
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        EasyMock.verify(workerHolder);
        Assert.assertEquals(listenerNotificationsAccumulator, ImmutableList.of(ImmutableList.of(task.getId(), TaskLocation.create("worker", 1000, 1001)), ImmutableList.of(task.getId(), TaskLocation.create("worker", 1, 2)), ImmutableList.of(task.getId(), TaskStatus.success(task.getId()))));
    }

    /* Task goes from PENDING -> SUCCESS . Happens when TaskRunner is given task but a worker reported it being already
    completed with SUCCESS.
     */
    @Test
    public void testTaskAddedOrUpdated2() throws Exception {
        Task task = NoopTask.create("task");
        List<Object> listenerNotificationsAccumulator = new ArrayList<>();
        HttpRemoteTaskRunner taskRunner = createTaskRunnerForTestTaskAddedOrUpdated(EasyMock.createStrictMock(TaskStorage.class), listenerNotificationsAccumulator);
        Worker worker = new Worker("http", "localhost", "127.0.0.1", 1, "v1");
        WorkerHolder workerHolder = EasyMock.createMock(WorkerHolder.class);
        EasyMock.expect(workerHolder.getWorker()).andReturn(worker).anyTimes();
        workerHolder.setLastCompletedTaskTime(EasyMock.anyObject());
        workerHolder.resetContinuouslyFailedTasksCount();
        EasyMock.expect(workerHolder.getContinuouslyFailedTasksCount()).andReturn(0);
        EasyMock.replay(workerHolder);
        Future<TaskStatus> future = taskRunner.run(task);
        Assert.assertEquals(task.getId(), Iterables.getOnlyElement(taskRunner.getPendingTasks()).getTaskId());
        taskRunner.taskAddedOrUpdated(TaskAnnouncement.create(task, TaskStatus.success(task.getId()), TaskLocation.create("worker", 1, 2)), workerHolder);
        Assert.assertEquals(task.getId(), Iterables.getOnlyElement(taskRunner.getCompletedTasks()).getTaskId());
        Assert.assertEquals(SUCCESS, future.get().getStatusCode());
        EasyMock.verify(workerHolder);
        Assert.assertEquals(listenerNotificationsAccumulator, ImmutableList.of(ImmutableList.of(task.getId(), TaskLocation.create("worker", 1, 2)), ImmutableList.of(task.getId(), TaskStatus.success(task.getId()))));
    }

    /* Notifications received for tasks not known to TaskRunner maybe known to TaskStorage.
    This could happen when TaskRunner starts and workers reports running/completed tasks on them.
     */
    @Test
    public void testTaskAddedOrUpdated3() {
        Task task1 = NoopTask.create("task1");
        Task task2 = NoopTask.create("task2");
        Task task3 = NoopTask.create("task3");
        Task task4 = NoopTask.create("task4");
        Task task5 = NoopTask.create("task5");
        Task task6 = NoopTask.create("task6");
        TaskStorage taskStorage = EasyMock.createMock(TaskStorage.class);
        EasyMock.expect(taskStorage.getStatus(task1.getId())).andReturn(Optional.of(TaskStatus.running(task1.getId())));
        EasyMock.expect(taskStorage.getStatus(task2.getId())).andReturn(Optional.of(TaskStatus.running(task2.getId())));
        EasyMock.expect(taskStorage.getStatus(task3.getId())).andReturn(Optional.of(TaskStatus.success(task3.getId())));
        EasyMock.expect(taskStorage.getStatus(task4.getId())).andReturn(Optional.of(TaskStatus.success(task4.getId())));
        EasyMock.expect(taskStorage.getStatus(task5.getId())).andReturn(Optional.absent());
        EasyMock.expect(taskStorage.getStatus(task6.getId())).andReturn(Optional.absent());
        EasyMock.replay(taskStorage);
        List<Object> listenerNotificationsAccumulator = new ArrayList<>();
        HttpRemoteTaskRunner taskRunner = createTaskRunnerForTestTaskAddedOrUpdated(taskStorage, listenerNotificationsAccumulator);
        Worker worker = new Worker("http", "localhost", "127.0.0.1", 1, "v1");
        WorkerHolder workerHolder = EasyMock.createMock(WorkerHolder.class);
        EasyMock.expect(workerHolder.getWorker()).andReturn(worker).anyTimes();
        workerHolder.setLastCompletedTaskTime(EasyMock.anyObject());
        workerHolder.resetContinuouslyFailedTasksCount();
        EasyMock.expect(workerHolder.getContinuouslyFailedTasksCount()).andReturn(0);
        workerHolder.shutdownTask(task3.getId());
        workerHolder.shutdownTask(task5.getId());
        EasyMock.replay(workerHolder);
        Assert.assertEquals(0, taskRunner.getKnownTasks().size());
        taskRunner.taskAddedOrUpdated(TaskAnnouncement.create(task1, TaskStatus.running(task1.getId()), TaskLocation.create("worker", 1, 2)), workerHolder);
        taskRunner.taskAddedOrUpdated(TaskAnnouncement.create(task2, TaskStatus.success(task2.getId()), TaskLocation.create("worker", 3, 4)), workerHolder);
        taskRunner.taskAddedOrUpdated(TaskAnnouncement.create(task3, TaskStatus.running(task3.getId()), TaskLocation.create("worker", 5, 6)), workerHolder);
        taskRunner.taskAddedOrUpdated(TaskAnnouncement.create(task4, TaskStatus.success(task4.getId()), TaskLocation.create("worker", 7, 8)), workerHolder);
        taskRunner.taskAddedOrUpdated(TaskAnnouncement.create(task5, TaskStatus.running(task5.getId()), TaskLocation.create("worker", 9, 10)), workerHolder);
        taskRunner.taskAddedOrUpdated(TaskAnnouncement.create(task6, TaskStatus.success(task6.getId()), TaskLocation.create("worker", 11, 12)), workerHolder);
        EasyMock.verify(workerHolder, taskStorage);
        Assert.assertEquals(listenerNotificationsAccumulator, ImmutableList.of(ImmutableList.of(task1.getId(), TaskLocation.create("worker", 1, 2)), ImmutableList.of(task2.getId(), TaskLocation.create("worker", 3, 4)), ImmutableList.of(task2.getId(), TaskStatus.success(task2.getId()))));
    }

    private static class TestDruidNodeDiscovery implements DruidNodeDiscovery {
        Listener listener;

        @Override
        public Collection<DiscoveryDruidNode> getAllNodes() {
            throw new UnsupportedOperationException("Not Implemented.");
        }

        @Override
        public void registerListener(Listener listener) {
            listener.nodesAdded(ImmutableList.of());
            listener.nodeViewInitialized();
            this.listener = listener;
        }
    }

    private interface CustomFunction {
        WorkerHolder apply(ObjectMapper smileMapper, HttpClient httpClient, HttpRemoteTaskRunnerConfig config, ScheduledExecutorService workersSyncExec, WorkerHolder.Listener listener, Worker worker);
    }
}

