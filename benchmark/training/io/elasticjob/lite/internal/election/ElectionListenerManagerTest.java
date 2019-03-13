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
package io.elasticjob.lite.internal.election;


import ServerStatus.DISABLED;
import Type.NODE_ADDED;
import Type.NODE_REMOVED;
import Type.NODE_UPDATED;
import io.elasticjob.lite.internal.schedule.JobRegistry;
import io.elasticjob.lite.internal.schedule.JobScheduleController;
import io.elasticjob.lite.internal.server.ServerService;
import io.elasticjob.lite.internal.storage.JobNodeStorage;
import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class ElectionListenerManagerTest {
    @Mock
    private CoordinatorRegistryCenter regCenter;

    @Mock
    private JobScheduleController jobScheduleController;

    @Mock
    private JobNodeStorage jobNodeStorage;

    @Mock
    private LeaderService leaderService;

    @Mock
    private ServerService serverService;

    private final ElectionListenerManager electionListenerManager = new ElectionListenerManager(null, "test_job");

    @Test
    public void assertStart() {
        electionListenerManager.start();
        Mockito.verify(jobNodeStorage, Mockito.times(2)).addDataListener(ArgumentMatchers.<ElectionListenerManager.LeaderElectionJobListener>any());
    }

    @Test
    public void assertIsNotLeaderInstancePathAndServerPath() {
        electionListenerManager.new LeaderElectionJobListener().dataChanged("/test_job/leader/election/other", NODE_REMOVED, "127.0.0.1");
        Mockito.verify(leaderService, Mockito.times(0)).electLeader();
    }

    @Test
    public void assertLeaderElectionWhenAddLeaderInstancePath() {
        electionListenerManager.new LeaderElectionJobListener().dataChanged("/test_job/leader/election/instance", NODE_ADDED, "127.0.0.1");
        Mockito.verify(leaderService, Mockito.times(0)).electLeader();
    }

    @Test
    public void assertLeaderElectionWhenRemoveLeaderInstancePathWithoutAvailableServers() {
        electionListenerManager.new LeaderElectionJobListener().dataChanged("/test_job/leader/election/instance", NODE_REMOVED, "127.0.0.1");
        Mockito.verify(leaderService, Mockito.times(0)).electLeader();
    }

    @Test
    public void assertLeaderElectionWhenRemoveLeaderInstancePathWithAvailableServerButJobInstanceIsShutdown() {
        Mockito.when(serverService.isAvailableServer("127.0.0.1")).thenReturn(true);
        electionListenerManager.new LeaderElectionJobListener().dataChanged("/test_job/leader/election/instance", NODE_REMOVED, "127.0.0.1");
        Mockito.verify(leaderService, Mockito.times(0)).electLeader();
    }

    @Test
    public void assertLeaderElectionWhenRemoveLeaderInstancePathWithAvailableServer() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        Mockito.when(serverService.isAvailableServer("127.0.0.1")).thenReturn(true);
        electionListenerManager.new LeaderElectionJobListener().dataChanged("/test_job/leader/election/instance", NODE_REMOVED, "127.0.0.1");
        Mockito.verify(leaderService).electLeader();
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertLeaderElectionWhenServerDisableWithoutLeader() {
        electionListenerManager.new LeaderElectionJobListener().dataChanged("/test_job/servers/127.0.0.1", NODE_REMOVED, DISABLED.name());
        Mockito.verify(leaderService, Mockito.times(0)).electLeader();
    }

    @Test
    public void assertLeaderElectionWhenServerEnableWithLeader() {
        Mockito.when(leaderService.hasLeader()).thenReturn(true);
        electionListenerManager.new LeaderElectionJobListener().dataChanged("/test_job/servers/127.0.0.1", NODE_UPDATED, "");
        Mockito.verify(leaderService, Mockito.times(0)).electLeader();
    }

    @Test
    public void assertLeaderElectionWhenServerEnableWithoutLeader() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        electionListenerManager.new LeaderElectionJobListener().dataChanged("/test_job/servers/127.0.0.1", NODE_UPDATED, "");
        Mockito.verify(leaderService).electLeader();
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertLeaderAbdicationWhenFollowerDisable() {
        electionListenerManager.new LeaderAbdicationJobListener().dataChanged("/test_job/servers/127.0.0.1", NODE_UPDATED, DISABLED.name());
        Mockito.verify(leaderService, Mockito.times(0)).removeLeader();
    }

    @Test
    public void assertLeaderAbdicationWhenLeaderDisable() {
        Mockito.when(leaderService.isLeader()).thenReturn(true);
        electionListenerManager.new LeaderAbdicationJobListener().dataChanged("/test_job/servers/127.0.0.1", NODE_UPDATED, DISABLED.name());
        Mockito.verify(leaderService).removeLeader();
    }
}

