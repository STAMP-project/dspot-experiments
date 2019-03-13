package io.elasticjob.lite.internal.listener;


import ConnectionState.CONNECTED;
import ConnectionState.LOST;
import ConnectionState.RECONNECTED;
import io.elasticjob.lite.internal.instance.InstanceService;
import io.elasticjob.lite.internal.schedule.JobRegistry;
import io.elasticjob.lite.internal.schedule.JobScheduleController;
import io.elasticjob.lite.internal.server.ServerService;
import io.elasticjob.lite.internal.sharding.ExecutionService;
import io.elasticjob.lite.internal.sharding.ShardingService;
import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import java.util.Arrays;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class RegistryCenterConnectionStateListenerTest {
    @Mock
    private CoordinatorRegistryCenter regCenter;

    @Mock
    private ServerService serverService;

    @Mock
    private InstanceService instanceService;

    @Mock
    private ShardingService shardingService;

    @Mock
    private ExecutionService executionService;

    @Mock
    private JobScheduleController jobScheduleController;

    private RegistryCenterConnectionStateListener regCenterConnectionStateListener;

    @Test
    public void assertConnectionLostListenerWhenConnectionStateIsLost() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        regCenterConnectionStateListener.stateChanged(null, LOST);
        Mockito.verify(jobScheduleController).pauseJob();
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertConnectionLostListenerWhenConnectionStateIsLostButIsShutdown() {
        regCenterConnectionStateListener.stateChanged(null, LOST);
        Mockito.verify(jobScheduleController, Mockito.times(0)).pauseJob();
        Mockito.verify(jobScheduleController, Mockito.times(0)).resumeJob();
    }

    @Test
    public void assertConnectionLostListenerWhenConnectionStateIsReconnected() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        Mockito.when(shardingService.getLocalShardingItems()).thenReturn(Arrays.asList(0, 1));
        Mockito.when(serverService.isEnableServer("127.0.0.1")).thenReturn(true);
        regCenterConnectionStateListener.stateChanged(null, RECONNECTED);
        Mockito.verify(serverService).persistOnline(true);
        Mockito.verify(executionService).clearRunningInfo(Arrays.asList(0, 1));
        Mockito.verify(jobScheduleController).resumeJob();
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertConnectionLostListenerWhenConnectionStateIsReconnectedButIsShutdown() {
        Mockito.when(shardingService.getLocalShardingItems()).thenReturn(Arrays.asList(0, 1));
        Mockito.when(serverService.isEnableServer("127.0.0.1")).thenReturn(true);
        regCenterConnectionStateListener.stateChanged(null, RECONNECTED);
        Mockito.verify(jobScheduleController, Mockito.times(0)).pauseJob();
        Mockito.verify(jobScheduleController, Mockito.times(0)).resumeJob();
    }

    @Test
    public void assertConnectionLostListenerWhenConnectionStateIsOther() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        regCenterConnectionStateListener.stateChanged(null, CONNECTED);
        Mockito.verify(jobScheduleController, Mockito.times(0)).pauseJob();
        Mockito.verify(jobScheduleController, Mockito.times(0)).resumeJob();
        JobRegistry.getInstance().shutdown("test_job");
    }
}

