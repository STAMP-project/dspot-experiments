/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package io.elasticjob.lite.internal.sharding;


import ShardingNode.PROCESSING;
import ShardingNode.ROOT;
import io.elasticjob.lite.api.strategy.JobInstance;
import io.elasticjob.lite.config.JobCoreConfiguration;
import io.elasticjob.lite.config.LiteJobConfiguration;
import io.elasticjob.lite.fixture.TestSimpleJob;
import io.elasticjob.lite.internal.config.ConfigurationService;
import io.elasticjob.lite.internal.election.LeaderService;
import io.elasticjob.lite.internal.instance.InstanceService;
import io.elasticjob.lite.internal.schedule.JobRegistry;
import io.elasticjob.lite.internal.schedule.JobScheduleController;
import io.elasticjob.lite.internal.server.ServerService;
import io.elasticjob.lite.internal.storage.JobNodeStorage;
import io.elasticjob.lite.internal.storage.TransactionExecutionCallback;
import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.curator.framework.api.transaction.CuratorTransactionBridge;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;
import org.apache.curator.framework.api.transaction.TransactionCreateBuilder;
import org.apache.curator.framework.api.transaction.TransactionDeleteBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class ShardingServiceTest {
    @Mock
    private CoordinatorRegistryCenter regCenter;

    @Mock
    private JobScheduleController jobScheduleController;

    @Mock
    private JobNodeStorage jobNodeStorage;

    @Mock
    private LeaderService leaderService;

    @Mock
    private ConfigurationService configService;

    @Mock
    private ExecutionService executionService;

    @Mock
    private ServerService serverService;

    @Mock
    private InstanceService instanceService;

    private final ShardingService shardingService = new ShardingService(null, "test_job");

    @Test
    public void assertSetReshardingFlag() {
        shardingService.setReshardingFlag();
        Mockito.verify(jobNodeStorage).createJobNodeIfNeeded("leader/sharding/necessary");
    }

    @Test
    public void assertIsNeedSharding() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/sharding/necessary")).thenReturn(true);
        Assert.assertTrue(shardingService.isNeedSharding());
    }

    @Test
    public void assertShardingWhenUnnecessary() {
        shardingService.shardingIfNecessary();
        Mockito.verify(jobNodeStorage, Mockito.times(0)).fillEphemeralJobNode(PROCESSING, "");
    }

    @Test
    public void assertShardingWithoutAvailableJobInstances() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/sharding/necessary")).thenReturn(true);
        shardingService.shardingIfNecessary();
        Mockito.verify(jobNodeStorage, Mockito.times(0)).fillEphemeralJobNode(PROCESSING, "");
    }

    @Test
    public void assertShardingWhenIsNotLeader() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/sharding/necessary")).thenReturn(true, false);
        Mockito.when(instanceService.getAvailableJobInstances()).thenReturn(Collections.singletonList(new JobInstance("127.0.0.1@-@0")));
        Mockito.when(leaderService.isLeaderUntilBlock()).thenReturn(false);
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/sharding/processing")).thenReturn(true, false);
        shardingService.shardingIfNecessary();
        Mockito.verify(jobNodeStorage, Mockito.times(0)).fillEphemeralJobNode(PROCESSING, "");
    }

    @Test
    public void assertShardingNecessaryWhenMonitorExecutionEnabledAndIncreaseShardingTotalCount() {
        Mockito.when(instanceService.getAvailableJobInstances()).thenReturn(Collections.singletonList(new JobInstance("127.0.0.1@-@0")));
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/sharding/necessary")).thenReturn(true);
        Mockito.when(leaderService.isLeaderUntilBlock()).thenReturn(true);
        Mockito.when(configService.load(false)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(true).build());
        Mockito.when(executionService.hasRunningItems()).thenReturn(true, false);
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys(ROOT)).thenReturn(Arrays.asList("0", "1"));
        shardingService.shardingIfNecessary();
        Mockito.verify(executionService, Mockito.times(2)).hasRunningItems();
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/0/instance");
        Mockito.verify(jobNodeStorage).createJobNodeIfNeeded("sharding/0");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/1/instance");
        Mockito.verify(jobNodeStorage).createJobNodeIfNeeded("sharding/1");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/2/instance");
        Mockito.verify(jobNodeStorage).createJobNodeIfNeeded("sharding/2");
        Mockito.verify(jobNodeStorage).fillEphemeralJobNode("leader/sharding/processing", "");
        Mockito.verify(jobNodeStorage).executeInTransaction(ArgumentMatchers.any(TransactionExecutionCallback.class));
    }

    @Test
    public void assertShardingNecessaryWhenMonitorExecutionDisabledAndDecreaseShardingTotalCount() {
        Mockito.when(instanceService.getAvailableJobInstances()).thenReturn(Collections.singletonList(new JobInstance("127.0.0.1@-@0")));
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/sharding/necessary")).thenReturn(true);
        Mockito.when(leaderService.isLeaderUntilBlock()).thenReturn(true);
        Mockito.when(configService.load(false)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).monitorExecution(false).build());
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys(ROOT)).thenReturn(Arrays.asList("0", "1", "2", "3"));
        shardingService.shardingIfNecessary();
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/0/instance");
        Mockito.verify(jobNodeStorage).createJobNodeIfNeeded("sharding/0");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/1/instance");
        Mockito.verify(jobNodeStorage).createJobNodeIfNeeded("sharding/1");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/2/instance");
        Mockito.verify(jobNodeStorage).createJobNodeIfNeeded("sharding/2");
        Mockito.verify(jobNodeStorage, Mockito.times(0)).removeJobNodeIfExisted("execution/2");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/3");
        Mockito.verify(jobNodeStorage).fillEphemeralJobNode("leader/sharding/processing", "");
        Mockito.verify(jobNodeStorage).executeInTransaction(ArgumentMatchers.any(TransactionExecutionCallback.class));
    }

    @Test
    public void assertGetShardingItemsWithNotAvailableServer() {
        Assert.assertThat(shardingService.getShardingItems("127.0.0.1@-@0"), CoreMatchers.is(Collections.<Integer>emptyList()));
    }

    @Test
    public void assertGetShardingItemsWithEnabledServer() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        Mockito.when(serverService.isAvailableServer("127.0.0.1")).thenReturn(true);
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).build());
        Mockito.when(jobNodeStorage.getJobNodeData("sharding/0/instance")).thenReturn("127.0.0.1@-@0");
        Mockito.when(jobNodeStorage.getJobNodeData("sharding/1/instance")).thenReturn("127.0.0.1@-@1");
        Mockito.when(jobNodeStorage.getJobNodeData("sharding/2/instance")).thenReturn("127.0.0.1@-@0");
        Assert.assertThat(shardingService.getShardingItems("127.0.0.1@-@0"), CoreMatchers.is(Arrays.asList(0, 2)));
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertGetLocalShardingItemsWithInstanceShutdown() {
        Assert.assertThat(shardingService.getLocalShardingItems(), CoreMatchers.is(Collections.<Integer>emptyList()));
    }

    @Test
    public void assertGetLocalShardingItemsWithDisabledServer() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        Assert.assertThat(shardingService.getLocalShardingItems(), CoreMatchers.is(Collections.<Integer>emptyList()));
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertGetLocalShardingItemsWithEnabledServer() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        Mockito.when(serverService.isAvailableServer("127.0.0.1")).thenReturn(true);
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).build());
        Mockito.when(jobNodeStorage.getJobNodeData("sharding/0/instance")).thenReturn("127.0.0.1@-@0");
        Mockito.when(jobNodeStorage.getJobNodeData("sharding/1/instance")).thenReturn("127.0.0.1@-@1");
        Mockito.when(jobNodeStorage.getJobNodeData("sharding/2/instance")).thenReturn("127.0.0.1@-@0");
        Assert.assertThat(shardingService.getLocalShardingItems(), CoreMatchers.is(Arrays.asList(0, 2)));
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertHasShardingInfoInOfflineServers() {
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys(InstanceNode.ROOT)).thenReturn(Arrays.asList("host0@-@0", "host0@-@1"));
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).build());
        Mockito.when(jobNodeStorage.getJobNodeData(ShardingNode.getInstanceNode(0))).thenReturn("host0@-@0");
        Mockito.when(jobNodeStorage.getJobNodeData(ShardingNode.getInstanceNode(1))).thenReturn("host0@-@1");
        Mockito.when(jobNodeStorage.getJobNodeData(ShardingNode.getInstanceNode(2))).thenReturn("host0@-@2");
        Assert.assertTrue(shardingService.hasShardingInfoInOfflineServers());
    }

    @Test
    public void assertHasNotShardingInfoInOfflineServers() {
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys(InstanceNode.ROOT)).thenReturn(Arrays.asList("host0@-@0", "host0@-@1"));
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).build());
        Mockito.when(jobNodeStorage.getJobNodeData(ShardingNode.getInstanceNode(0))).thenReturn("host0@-@0");
        Mockito.when(jobNodeStorage.getJobNodeData(ShardingNode.getInstanceNode(1))).thenReturn("host0@-@1");
        Mockito.when(jobNodeStorage.getJobNodeData(ShardingNode.getInstanceNode(2))).thenReturn("host0@-@0");
        Assert.assertFalse(shardingService.hasShardingInfoInOfflineServers());
    }

    @Test
    public void assertPersistShardingInfoTransactionExecutionCallback() throws Exception {
        CuratorTransactionFinal curatorTransactionFinal = Mockito.mock(CuratorTransactionFinal.class);
        TransactionCreateBuilder transactionCreateBuilder = Mockito.mock(TransactionCreateBuilder.class);
        TransactionDeleteBuilder transactionDeleteBuilder = Mockito.mock(TransactionDeleteBuilder.class);
        CuratorTransactionBridge curatorTransactionBridge = Mockito.mock(CuratorTransactionBridge.class);
        Mockito.when(curatorTransactionFinal.create()).thenReturn(transactionCreateBuilder);
        Mockito.when(configService.load(true)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.simple.SimpleJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestSimpleJob.class.getCanonicalName())).build());
        Mockito.when(transactionCreateBuilder.forPath("/test_job/sharding/0/instance", "host0@-@0".getBytes())).thenReturn(curatorTransactionBridge);
        Mockito.when(transactionCreateBuilder.forPath("/test_job/sharding/1/instance", "host0@-@0".getBytes())).thenReturn(curatorTransactionBridge);
        Mockito.when(transactionCreateBuilder.forPath("/test_job/sharding/2/instance", "host0@-@0".getBytes())).thenReturn(curatorTransactionBridge);
        Mockito.when(curatorTransactionBridge.and()).thenReturn(curatorTransactionFinal);
        Mockito.when(curatorTransactionFinal.delete()).thenReturn(transactionDeleteBuilder);
        Mockito.when(transactionDeleteBuilder.forPath("/test_job/leader/sharding/necessary")).thenReturn(curatorTransactionBridge);
        Mockito.when(curatorTransactionBridge.and()).thenReturn(curatorTransactionFinal);
        Mockito.when(curatorTransactionFinal.delete()).thenReturn(transactionDeleteBuilder);
        Mockito.when(transactionDeleteBuilder.forPath("/test_job/leader/sharding/processing")).thenReturn(curatorTransactionBridge);
        Mockito.when(curatorTransactionBridge.and()).thenReturn(curatorTransactionFinal);
        Map<JobInstance, List<Integer>> shardingResult = new HashMap<>();
        shardingResult.put(new JobInstance("host0@-@0"), Arrays.asList(0, 1, 2));
        ShardingService.PersistShardingInfoTransactionExecutionCallback actual = shardingService.new PersistShardingInfoTransactionExecutionCallback(shardingResult);
        actual.execute(curatorTransactionFinal);
        Mockito.verify(curatorTransactionFinal, Mockito.times(3)).create();
        Mockito.verify(curatorTransactionFinal, Mockito.times(2)).delete();
        Mockito.verify(transactionDeleteBuilder).forPath("/test_job/leader/sharding/necessary");
        Mockito.verify(transactionDeleteBuilder).forPath("/test_job/leader/sharding/processing");
        Mockito.verify(curatorTransactionBridge, Mockito.times(5)).and();
    }
}

