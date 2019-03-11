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


import Type.NODE_ADDED;
import Type.NODE_UPDATED;
import io.elasticjob.lite.fixture.LiteJsonConstants;
import io.elasticjob.lite.internal.listener.AbstractJobListener;
import io.elasticjob.lite.internal.schedule.JobRegistry;
import io.elasticjob.lite.internal.schedule.JobScheduleController;
import io.elasticjob.lite.internal.storage.JobNodeStorage;
import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class ShardingListenerManagerTest {
    @Mock
    private CoordinatorRegistryCenter regCenter;

    @Mock
    private JobScheduleController jobScheduleController;

    @Mock
    private JobNodeStorage jobNodeStorage;

    @Mock
    private ShardingService shardingService;

    private ShardingListenerManager shardingListenerManager;

    @Test
    public void assertStart() {
        shardingListenerManager.start();
        Mockito.verify(jobNodeStorage, Mockito.times(2)).addDataListener(ArgumentMatchers.<AbstractJobListener>any());
    }

    @Test
    public void assertShardingTotalCountChangedJobListenerWhenIsNotConfigPath() {
        shardingListenerManager.new ShardingTotalCountChangedJobListener().dataChanged("/test_job/config/other", NODE_ADDED, "");
        Mockito.verify(shardingService, Mockito.times(0)).setReshardingFlag();
    }

    @Test
    public void assertShardingTotalCountChangedJobListenerWhenIsConfigPathButCurrentShardingTotalCountIsZero() {
        shardingListenerManager.new ShardingTotalCountChangedJobListener().dataChanged("/test_job/config", NODE_ADDED, LiteJsonConstants.getJobJson());
        Mockito.verify(shardingService, Mockito.times(0)).setReshardingFlag();
    }

    @Test
    public void assertShardingTotalCountChangedJobListenerWhenIsConfigPathAndCurrentShardingTotalCountIsEqualToNewShardingTotalCount() {
        JobRegistry.getInstance().setCurrentShardingTotalCount("test_job", 3);
        shardingListenerManager.new ShardingTotalCountChangedJobListener().dataChanged("/test_job/config", NODE_ADDED, LiteJsonConstants.getJobJson());
        Mockito.verify(shardingService, Mockito.times(0)).setReshardingFlag();
        JobRegistry.getInstance().setCurrentShardingTotalCount("test_job", 0);
    }

    @Test
    public void assertShardingTotalCountChangedJobListenerWhenIsConfigPathAndCurrentShardingTotalCountIsNotEqualToNewShardingTotalCount() throws NoSuchFieldException {
        JobRegistry.getInstance().setCurrentShardingTotalCount("test_job", 5);
        shardingListenerManager.new ShardingTotalCountChangedJobListener().dataChanged("/test_job/config", NODE_UPDATED, LiteJsonConstants.getJobJson());
        Mockito.verify(shardingService).setReshardingFlag();
        JobRegistry.getInstance().setCurrentShardingTotalCount("test_job", 0);
    }

    @Test
    public void assertListenServersChangedJobListenerWhenIsNotServerStatusPath() {
        shardingListenerManager.new ListenServersChangedJobListener().dataChanged("/test_job/servers/127.0.0.1/other", NODE_ADDED, "");
        Mockito.verify(shardingService, Mockito.times(0)).setReshardingFlag();
    }

    @Test
    public void assertListenServersChangedJobListenerWhenIsServerStatusPathButUpdate() {
        shardingListenerManager.new ListenServersChangedJobListener().dataChanged("/test_job/servers/127.0.0.1/status", NODE_UPDATED, "");
        Mockito.verify(shardingService, Mockito.times(0)).setReshardingFlag();
    }

    @Test
    public void assertListenServersChangedJobListenerWhenIsInstanceChangeButJobInstanceIsShutdown() {
        shardingListenerManager.new ListenServersChangedJobListener().dataChanged("/test_job/instances/xxx", NODE_ADDED, "");
        Mockito.verify(shardingService, Mockito.times(0)).setReshardingFlag();
    }

    @Test
    public void assertListenServersChangedJobListenerWhenIsInstanceChange() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        shardingListenerManager.new ListenServersChangedJobListener().dataChanged("/test_job/instances/xxx", NODE_ADDED, "");
        Mockito.verify(shardingService).setReshardingFlag();
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertListenServersChangedJobListenerWhenIsServerChange() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        shardingListenerManager.new ListenServersChangedJobListener().dataChanged("/test_job/servers/127.0.0.1", NODE_UPDATED, "");
        Mockito.verify(shardingService).setReshardingFlag();
        JobRegistry.getInstance().shutdown("test_job");
    }
}

