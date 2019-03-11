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
package io.elasticjob.lite.internal.schedule;


import io.elasticjob.lite.config.JobCoreConfiguration;
import io.elasticjob.lite.config.LiteJobConfiguration;
import io.elasticjob.lite.fixture.TestDataflowJob;
import io.elasticjob.lite.fixture.util.JobConfigurationUtil;
import io.elasticjob.lite.internal.config.ConfigurationService;
import io.elasticjob.lite.internal.election.LeaderService;
import io.elasticjob.lite.internal.instance.InstanceService;
import io.elasticjob.lite.internal.listener.ListenerManager;
import io.elasticjob.lite.internal.monitor.MonitorService;
import io.elasticjob.lite.internal.reconcile.ReconcileService;
import io.elasticjob.lite.internal.server.ServerService;
import io.elasticjob.lite.internal.sharding.ShardingService;
import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class SchedulerFacadeTest {
    @Mock
    private CoordinatorRegistryCenter regCenter;

    @Mock
    private JobScheduleController jobScheduleController;

    @Mock
    private ConfigurationService configService;

    @Mock
    private LeaderService leaderService;

    @Mock
    private ServerService serverService;

    @Mock
    private InstanceService instanceService;

    @Mock
    private ShardingService shardingService;

    @Mock
    private MonitorService monitorService;

    @Mock
    private ReconcileService reconcileService;

    @Mock
    private ListenerManager listenerManager;

    private final LiteJobConfiguration liteJobConfig = JobConfigurationUtil.createDataflowLiteJobConfiguration();

    private SchedulerFacade schedulerFacade;

    @Test
    public void assertUpdateJobConfiguration() {
        LiteJobConfiguration jobConfig = LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.dataflow.DataflowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestDataflowJob.class.getCanonicalName(), false)).build();
        Mockito.when(configService.load(false)).thenReturn(jobConfig);
        MatcherAssert.assertThat(schedulerFacade.updateJobConfiguration(jobConfig), Is.is(jobConfig));
        Mockito.verify(configService).persist(jobConfig);
    }

    @Test
    public void assertRegisterStartUpInfo() {
        Mockito.when(configService.load(false)).thenReturn(LiteJobConfiguration.newBuilder(new io.elasticjob.lite.config.dataflow.DataflowJobConfiguration(JobCoreConfiguration.newBuilder("test_job", "0/1 * * * * ?", 3).build(), TestDataflowJob.class.getCanonicalName(), false)).build());
        schedulerFacade.registerStartUpInfo(true);
        Mockito.verify(listenerManager).startAllListeners();
        Mockito.verify(leaderService).electLeader();
        Mockito.verify(serverService).persistOnline(true);
        Mockito.verify(shardingService).setReshardingFlag();
        Mockito.verify(monitorService).listen();
    }

    @Test
    public void assertShutdownInstanceIfNotLeaderAndReconcileServiceIsNotRunning() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        schedulerFacade.shutdownInstance();
        Mockito.verify(leaderService, Mockito.times(0)).removeLeader();
        Mockito.verify(monitorService).close();
        Mockito.verify(reconcileService, Mockito.times(0)).stopAsync();
        Mockito.verify(jobScheduleController).shutdown();
    }

    @Test
    public void assertShutdownInstanceIfLeaderAndReconcileServiceIsRunning() {
        Mockito.when(leaderService.isLeader()).thenReturn(true);
        Mockito.when(reconcileService.isRunning()).thenReturn(true);
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        schedulerFacade.shutdownInstance();
        Mockito.verify(leaderService).removeLeader();
        Mockito.verify(monitorService).close();
        Mockito.verify(reconcileService).stopAsync();
        Mockito.verify(jobScheduleController).shutdown();
    }
}

