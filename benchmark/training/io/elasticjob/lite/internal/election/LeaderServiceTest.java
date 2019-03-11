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


import io.elasticjob.lite.internal.election.LeaderService.LeaderElectionExecutionCallback;
import io.elasticjob.lite.internal.schedule.JobRegistry;
import io.elasticjob.lite.internal.schedule.JobScheduleController;
import io.elasticjob.lite.internal.server.ServerService;
import io.elasticjob.lite.internal.storage.JobNodeStorage;
import io.elasticjob.lite.reg.base.CoordinatorRegistryCenter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class LeaderServiceTest {
    @Mock
    private CoordinatorRegistryCenter regCenter;

    @Mock
    private JobScheduleController jobScheduleController;

    @Mock
    private JobNodeStorage jobNodeStorage;

    @Mock
    private ServerService serverService;

    private LeaderService leaderService;

    @Test
    public void assertElectLeader() {
        leaderService.electLeader();
        Mockito.verify(jobNodeStorage).executeInLeader(ArgumentMatchers.eq("leader/election/latch"), ArgumentMatchers.<LeaderElectionExecutionCallback>any());
    }

    @Test
    public void assertIsLeaderUntilBlockWithLeader() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/election/instance")).thenReturn(true);
        Mockito.when(jobNodeStorage.getJobNodeData("leader/election/instance")).thenReturn("127.0.0.1@-@0");
        Assert.assertTrue(leaderService.isLeaderUntilBlock());
        Mockito.verify(jobNodeStorage, Mockito.times(0)).executeInLeader(ArgumentMatchers.eq("leader/election/latch"), ArgumentMatchers.<LeaderElectionExecutionCallback>any());
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertIsLeaderUntilBlockWithoutLeaderAndAvailableServers() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/election/instance")).thenReturn(false);
        Assert.assertFalse(leaderService.isLeaderUntilBlock());
        Mockito.verify(jobNodeStorage, Mockito.times(0)).executeInLeader(ArgumentMatchers.eq("leader/election/latch"), ArgumentMatchers.<LeaderElectionExecutionCallback>any());
    }

    @Test
    public void assertIsLeaderUntilBlockWithoutLeaderWithAvailableServers() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/election/instance")).thenReturn(false, true);
        Assert.assertFalse(leaderService.isLeaderUntilBlock());
        Mockito.verify(jobNodeStorage, Mockito.times(0)).executeInLeader(ArgumentMatchers.eq("leader/election/latch"), ArgumentMatchers.<LeaderElectionExecutionCallback>any());
    }

    @Test
    public void assertIsLeaderUntilBlockWhenHasLeader() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/election/instance")).thenReturn(false, true);
        Mockito.when(serverService.hasAvailableServers()).thenReturn(true);
        Mockito.when(serverService.isAvailableServer("127.0.0.1")).thenReturn(true);
        Mockito.when(jobNodeStorage.getJobNodeData("leader/election/instance")).thenReturn("127.0.0.1@-@0");
        Assert.assertTrue(leaderService.isLeaderUntilBlock());
        Mockito.verify(jobNodeStorage).executeInLeader(ArgumentMatchers.eq("leader/election/latch"), ArgumentMatchers.<LeaderElectionExecutionCallback>any());
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertIsLeader() {
        JobRegistry.getInstance().registerJob("test_job", jobScheduleController, regCenter);
        Mockito.when(jobNodeStorage.getJobNodeData("leader/election/instance")).thenReturn("127.0.0.1@-@0");
        Assert.assertTrue(leaderService.isLeader());
        JobRegistry.getInstance().shutdown("test_job");
    }

    @Test
    public void assertHasLeader() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/election/instance")).thenReturn(true);
        Assert.assertTrue(leaderService.hasLeader());
    }

    @Test
    public void assertRemoveLeader() {
        leaderService.removeLeader();
        Mockito.verify(jobNodeStorage).removeJobNodeIfExisted("leader/election/instance");
    }

    @Test
    public void assertElectLeaderExecutionCallbackWithLeader() {
        Mockito.when(jobNodeStorage.isJobNodeExisted("leader/election/instance")).thenReturn(true);
        leaderService.new LeaderElectionExecutionCallback().execute();
        Mockito.verify(jobNodeStorage, Mockito.times(0)).fillEphemeralJobNode("leader/election/instance", "127.0.0.1@-@0");
    }

    @Test
    public void assertElectLeaderExecutionCallbackWithoutLeader() {
        leaderService.new LeaderElectionExecutionCallback().execute();
        Mockito.verify(jobNodeStorage).fillEphemeralJobNode("leader/election/instance", "127.0.0.1@-@0");
    }
}

