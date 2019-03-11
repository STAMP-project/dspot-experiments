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
package io.elasticjob.lite.internal.failover;


import io.elasticjob.lite.internal.schedule.JobRegistry;
import io.elasticjob.lite.internal.schedule.JobScheduleController;
import io.elasticjob.lite.internal.sharding.ShardingService;
import io.elasticjob.lite.internal.storage.JobNodeStorage;
import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class FailoverServiceTest {
    @Mock
    private CoordinatorRegistryCenter regCenter;

    @Mock
    private JobScheduleController jobScheduleController;

    @Mock
    private JobNodeStorage jobNodeStorage;

    @Mock
    private ShardingService shardingService;

    private final FailoverService failoverService = new FailoverService(null, "test_job");

    @Test
    public void assertSetCrashedFailoverFlagWhenItemIsNotAssigned() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/0/failover")).thenReturn(true);
        failoverService.setCrashedFailoverFlag(0);
        Mockito.verify(jobNodeStorage).isJobNodeExisted("sharding/0/failover");
        Mockito.verify(jobNodeStorage, Mockito.times(0)).createJobNodeIfNeeded("leader/failover/items/0");
    }

    @Test
    public void assertSetCrashedFailoverFlagWhenItemIsAssigned() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/0/failover")).thenReturn(false);
        failoverService.setCrashedFailoverFlag(0);
        Mockito.verify(jobNodeStorage).isJobNodeExisted("sharding/0/failover");
        Mockito.verify(jobNodeStorage).createJobNodeIfNeeded("leader/failover/items/0");
    }

    @Test
    public void assertFailoverIfUnnecessaryWhenItemsRootNodeNotExisted() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/failover/items")).thenReturn(false);
        failoverService.failoverIfNecessary();
        Mockito.verify(jobNodeStorage).isJobNodeExisted("leader/failover/items");
        Mockito.verify(jobNodeStorage, Mockito.times(0)).executeInLeader(ArgumentMatchers.eq("leader/failover/latch"), ArgumentMatchers.<FailoverService.FailoverLeaderExecutionCallback>any());
    }

    @Test
    public void assertFailoverIfUnnecessaryWhenItemsRootNodeIsEmpty() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/failover/items")).thenReturn(true);
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("leader/failover/items")).thenReturn(Collections.<String>emptyList());
        failoverService.failoverIfNecessary();
        Mockito.verify(jobNodeStorage).isJobNodeExisted("leader/failover/items");
        Mockito.verify(jobNodeStorage).getJobNodeChildrenKeys("leader/failover/items");
        Mockito.verify(jobNodeStorage, Mockito.times(0)).executeInLeader(ArgumentMatchers.eq("leader/failover/latch"), ArgumentMatchers.<FailoverService.FailoverLeaderExecutionCallback>any());
    }

    @Test
    public void assertFailoverIfUnnecessaryWhenServerIsNotReady() {
        JobRegistry.getInstance().setJobRunning("test_job", true);
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/failover/items")).thenReturn(true);
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("leader/failover/items")).thenReturn(Arrays.asList("0", "1", "2"));
        failoverService.failoverIfNecessary();
        Mockito.verify(jobNodeStorage).isJobNodeExisted("leader/failover/items");
        Mockito.verify(jobNodeStorage).getJobNodeChildrenKeys("leader/failover/items");
        Mockito.verify(jobNodeStorage, Mockito.times(0)).executeInLeader(ArgumentMatchers.eq("leader/failover/latch"), ArgumentMatchers.<FailoverService.FailoverLeaderExecutionCallback>any());
    }

    @Test
    public void assertFailoverIfNecessary() {
        JobRegistry.getInstance().setJobRunning("test_job", false);
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/failover/items")).thenReturn(true);
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("leader/failover/items")).thenReturn(Arrays.asList("0", "1", "2"));
        failoverService.failoverIfNecessary();
        Mockito.verify(jobNodeStorage).isJobNodeExisted("leader/failover/items");
        Mockito.verify(jobNodeStorage).getJobNodeChildrenKeys("leader/failover/items");
        Mockito.verify(jobNodeStorage).executeInLeader(ArgumentMatchers.eq("leader/failover/latch"), ArgumentMatchers.<FailoverService.FailoverLeaderExecutionCallback>any());
        JobRegistry.getInstance().setJobRunning("test_job", false);
    }

    @Test
    public void assertFailoverLeaderExecutionCallbackIfNotNecessary() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        JobRegistry.getInstance().setJobRunning("test_job", false);
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/failover/items")).thenReturn(false);
        failoverService.new FailoverLeaderExecutionCallback().execute();
        Mockito.verify(jobNodeStorage).isJobNodeExisted("leader/failover/items");
        Mockito.verify(jobNodeStorage, Mockito.times(0)).getJobNodeChildrenKeys("leader/failover/items");
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertFailoverLeaderExecutionCallbackIfNecessary() {
        JobRegistry.getInstance().setJobRunning("test_job", false);
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/failover/items")).thenReturn(true);
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("leader/failover/items")).thenReturn(Arrays.asList("0", "1", "2"));
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        failoverService.new FailoverLeaderExecutionCallback().execute();
        Mockito.verify(jobNodeStorage).isJobNodeExisted("leader/failover/items");
        Mockito.verify(jobNodeStorage, Mockito.times(2)).getJobNodeChildrenKeys("leader/failover/items");
        Mockito.verify(jobNodeStorage).fillEphemeralJobNode("sharding/0/failover", "127.0.0.1@-@0");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("leader/failover/items/0");
        Mockito.verify(jobScheduleController).triggerJob();
        JobRegistry.getInstance().setJobRunning("test_job", false);
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertUpdateFailoverComplete() {
        failoverService.updateFailoverComplete(Arrays.asList(0, 1));
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/0/failover");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/1/failover");
    }

    @Test
    public void assertGetFailoverItems() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("sharding")).thenReturn(Arrays.asList("0", "1", "2"));
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/0/failover")).thenReturn(true);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/1/failover")).thenReturn(true);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/2/failover")).thenReturn(false);
        Mockito.when(jobNodeStorage.getJobNodeDataDirectly("sharding/0/failover")).thenReturn("127.0.0.1@-@0");
        Mockito.when(jobNodeStorage.getJobNodeDataDirectly("sharding/1/failover")).thenReturn("127.0.0.1@-@1");
        Assert.assertThat(failoverService.getFailoverItems("127.0.0.1@-@1"), CoreMatchers.is(Collections.singletonList(1)));
        Mockito.verify(jobNodeStorage).getJobNodeChildrenKeys("sharding");
        Mockito.verify(jobNodeStorage).isJobNodeExisted("sharding/0/failover");
        Mockito.verify(jobNodeStorage).isJobNodeExisted("sharding/1/failover");
        Mockito.verify(jobNodeStorage).getJobNodeDataDirectly("sharding/0/failover");
        Mockito.verify(jobNodeStorage).getJobNodeDataDirectly("sharding/1/failover");
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertGetLocalFailoverItemsIfShutdown() {
        Assert.assertThat(failoverService.getLocalFailoverItems(), CoreMatchers.is(Collections.<Integer>emptyList()));
        Mockito.verify(jobNodeStorage, Mockito.times(0)).getJobNodeChildrenKeys("sharding");
    }

    @Test
    public void assertGetLocalFailoverItems() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("sharding")).thenReturn(Arrays.asList("0", "1", "2"));
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/0/failover")).thenReturn(true);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/1/failover")).thenReturn(true);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/2/failover")).thenReturn(false);
        Mockito.when(jobNodeStorage.getJobNodeDataDirectly("sharding/0/failover")).thenReturn("127.0.0.1@-@0");
        Mockito.when(jobNodeStorage.getJobNodeDataDirectly("sharding/1/failover")).thenReturn("127.0.0.1@-@1");
        Assert.assertThat(failoverService.getLocalFailoverItems(), CoreMatchers.is(Collections.singletonList(0)));
        Mockito.verify(jobNodeStorage).getJobNodeChildrenKeys("sharding");
        Mockito.verify(jobNodeStorage).isJobNodeExisted("sharding/0/failover");
        Mockito.verify(jobNodeStorage).isJobNodeExisted("sharding/1/failover");
        Mockito.verify(jobNodeStorage).getJobNodeDataDirectly("sharding/0/failover");
        Mockito.verify(jobNodeStorage).getJobNodeDataDirectly("sharding/1/failover");
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertGetLocalTakeOffItems() {
        Mockito.when(shardingService.getLocalShardingItems()).thenReturn(Arrays.asList(0, 1, 2));
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/0/failover")).thenReturn(true);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/1/failover")).thenReturn(true);
        Mockito.when(jobNodeStorage.isJobNodeExisted("sharding/2/failover")).thenReturn(false);
        Assert.assertThat(failoverService.getLocalTakeOffItems(), CoreMatchers.is(Arrays.asList(0, 1)));
        Mockito.verify(shardingService).getLocalShardingItems();
        Mockito.verify(jobNodeStorage).isJobNodeExisted("sharding/0/failover");
        Mockito.verify(jobNodeStorage).isJobNodeExisted("sharding/1/failover");
        Mockito.verify(jobNodeStorage).isJobNodeExisted("sharding/2/failover");
    }

    @Test
    public void assertRemoveFailoverInfo() {
        Mockito.when(jobNodeStorage.getJobNodeChildrenKeys("sharding")).thenReturn(Arrays.asList("0", "1", "2"));
        failoverService.removeFailoverInfo();
        Mockito.verify(jobNodeStorage).getJobNodeChildrenKeys("sharding");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/0/failover");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/1/failover");
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("sharding/2/failover");
    }
}

