/**
 * -
 * -\-\-
 * Helios Services
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.master;


import HostStatus.Status.UP;
import com.google.common.collect.ImmutableList;
import com.spotify.helios.common.Json;
import com.spotify.helios.common.descriptors.DeploymentGroup;
import com.spotify.helios.common.descriptors.DeploymentGroupStatus;
import com.spotify.helios.common.descriptors.HostSelector;
import com.spotify.helios.common.descriptors.HostStatus;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.RolloutOptions;
import com.spotify.helios.servicescommon.coordination.Paths;
import com.spotify.helios.servicescommon.coordination.ZooKeeperClient;
import com.spotify.helios.servicescommon.coordination.ZooKeeperOperation;
import com.spotify.helios.servicescommon.coordination.ZooKeeperOperations;
import java.util.List;
import org.apache.curator.test.TestingServer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;


@RunWith(Theories.class)
public class DeploymentGroupTest {
    private static final String GROUP_NAME = "my_group";

    private TestingServer zkServer;

    private ZooKeeperClient client;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Captor
    private ArgumentCaptor<List<ZooKeeperOperation>> opCaptor;

    @Test
    public void testUpdateDeploymentGroupHostsPartialRolloutOptions() throws Exception {
        testUpdateDeploymentGroupHosts(RolloutOptions.newBuilder().setOverlap(true).build());
    }

    // A test that ensures healthy deployment groups will perform a rolling update when their hosts
    // change.
    @Test
    public void testUpdateDeploymentGroupHostsFullRolloutOptions() throws Exception {
        testUpdateDeploymentGroupHosts(RolloutOptions.getDefault());
    }

    // A test that ensures deployment groups that failed during a rolling update triggered by
    // changing hosts will perform a new rolling update if the hosts change again.
    @Test
    public void testUpdateFailedHostsChangedDeploymentGroupHosts() throws Exception {
        final ZooKeeperClient client = Mockito.spy(this.client);
        final ZooKeeperMasterModel masterModel = Mockito.spy(newMasterModel(client));
        // Return a job so we can add a real deployment group.
        final Job job = Job.newBuilder().setCommand(ImmutableList.of("COMMAND")).setImage("IMAGE").setName("JOB_NAME").setVersion("VERSION").build();
        Mockito.doReturn(job).when(masterModel).getJob(job.getId());
        // Add a real deployment group.
        final DeploymentGroup dg = DeploymentGroup.newBuilder().setName(DeploymentGroupTest.GROUP_NAME).setHostSelectors(ImmutableList.of(HostSelector.parse("role=melmac"))).setJobId(job.getId()).setRolloutOptions(RolloutOptions.getDefault()).setRollingUpdateReason(HOSTS_CHANGED).build();
        masterModel.addDeploymentGroup(dg);
        // Give the deployment group a host.
        client.setData(Paths.statusDeploymentGroupHosts(dg.getName()), Json.asBytes(ImmutableList.of("host1")));
        // And a status...
        client.setData(Paths.statusDeploymentGroup(dg.getName()), DeploymentGroupStatus.newBuilder().setState(FAILED).build().toJsonBytes());
        // Pretend our new host is UP.
        final HostStatus statusUp = Mockito.mock(HostStatus.class);
        Mockito.doReturn(UP).when(statusUp).getStatus();
        Mockito.doReturn(statusUp).when(masterModel).getHostStatus("host2");
        // Switch out our host!
        masterModel.updateDeploymentGroupHosts(dg.getName(), ImmutableList.of("host2"));
        // Ensure we write the same DG status again.
        // This is a no-op, but it means we triggered a rolling update.
        final ZooKeeperOperation setDeploymentGroup = ZooKeeperOperations.set(Paths.configDeploymentGroup(dg.getName()), dg);
        Mockito.verify(client, Mockito.times(2)).transaction(opCaptor.capture());
        Assert.assertThat(opCaptor.getValue(), Matchers.hasItem(setDeploymentGroup));
    }

    // A test that ensures deployment groups that failed during a manual rolling update will not
    // perform a new rolling update if the hosts change.
    @Test
    public void testUpdateFailedManualDeploymentGroupHosts() throws Exception {
        final ZooKeeperClient client = Mockito.spy(this.client);
        final ZooKeeperMasterModel masterModel = Mockito.spy(newMasterModel(client));
        // Return a job so we can add a real deployment group.
        final Job job = Job.newBuilder().setCommand(ImmutableList.of("COMMAND")).setImage("IMAGE").setName("JOB_NAME").setVersion("VERSION").build();
        Mockito.doReturn(job).when(masterModel).getJob(job.getId());
        // Add a real deployment group.
        final DeploymentGroup dg = DeploymentGroup.newBuilder().setName(DeploymentGroupTest.GROUP_NAME).setHostSelectors(ImmutableList.of(HostSelector.parse("role=melmac"))).setJobId(job.getId()).setRolloutOptions(RolloutOptions.getDefault()).setRollingUpdateReason(MANUAL).build();
        masterModel.addDeploymentGroup(dg);
        // Give the deployment group a host.
        client.setData(Paths.statusDeploymentGroupHosts(dg.getName()), Json.asBytes(ImmutableList.of("host1")));
        // And a status...
        client.setData(Paths.statusDeploymentGroup(dg.getName()), DeploymentGroupStatus.newBuilder().setState(FAILED).build().toJsonBytes());
        // Pretend our new host is UP.
        final HostStatus statusUp = Mockito.mock(HostStatus.class);
        Mockito.doReturn(UP).when(statusUp).getStatus();
        Mockito.doReturn(statusUp).when(masterModel).getHostStatus("host2");
        // Switch out our host!
        masterModel.updateDeploymentGroupHosts(dg.getName(), ImmutableList.of("host2"));
        // Ensure we do not set the DG status to HOSTS_CHANGED.
        // We don't want to trigger a rolling update because the last one was manual, and failed.
        final ZooKeeperOperation setDeploymentGroupHostChanged = ZooKeeperOperations.set(Paths.configDeploymentGroup(dg.getName()), dg.toBuilder().setRollingUpdateReason(HOSTS_CHANGED).build());
        Mockito.verify(client, Mockito.times(2)).transaction(opCaptor.capture());
        Assert.assertThat(opCaptor.getValue(), CoreMatchers.not(Matchers.hasItem(setDeploymentGroupHostChanged)));
    }
}

