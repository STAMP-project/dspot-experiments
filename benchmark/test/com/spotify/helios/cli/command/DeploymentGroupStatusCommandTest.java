/**
 * -
 * -\-\-
 * Helios Tools
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
package com.spotify.helios.cli.command;


import DeploymentGroupStatusResponse.HostStatus;
import DeploymentGroupStatusResponse.Status;
import TaskStatus.State;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.spotify.helios.client.HeliosClient;
import com.spotify.helios.common.Json;
import com.spotify.helios.common.descriptors.DeploymentGroup;
import com.spotify.helios.common.descriptors.HostSelector;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.common.descriptors.RolloutOptions;
import com.spotify.helios.common.protocol.DeploymentGroupStatusResponse;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import net.sourceforge.argparse4j.inf.Namespace;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class DeploymentGroupStatusCommandTest {
    private static final JobId JOB_ID = new JobId("foo-job", "0.1.0");

    private static final String GROUP_NAME = "foo-group";

    private static final List<HostSelector> HOST_SELECTORS = ImmutableList.of(HostSelector.parse("a=b"), HostSelector.parse("foo=bar"));

    private static final RolloutOptions ROLLOUT_OPTIONS = RolloutOptions.getDefault();

    private static final DeploymentGroup DEPLOYMENT_GROUP = DeploymentGroup.newBuilder().setName(DeploymentGroupStatusCommandTest.GROUP_NAME).setHostSelectors(DeploymentGroupStatusCommandTest.HOST_SELECTORS).setJobId(DeploymentGroupStatusCommandTest.JOB_ID).setRolloutOptions(DeploymentGroupStatusCommandTest.ROLLOUT_OPTIONS).setRollingUpdateReason(MANUAL).build();

    private final Namespace options = Mockito.mock(Namespace.class);

    private final HeliosClient client = Mockito.mock(HeliosClient.class);

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private final PrintStream out = new PrintStream(baos);

    private DeploymentGroupStatusCommand command;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testDeploymentGroupStatus() throws Exception {
        final List<DeploymentGroupStatusResponse.HostStatus> hostStatuses = Lists.newArrayList();
        hostStatuses.add(new DeploymentGroupStatusResponse.HostStatus("host1", DeploymentGroupStatusCommandTest.JOB_ID, State.RUNNING));
        hostStatuses.add(new DeploymentGroupStatusResponse.HostStatus("host2", DeploymentGroupStatusCommandTest.JOB_ID, State.PULLING_IMAGE));
        hostStatuses.add(new DeploymentGroupStatusResponse.HostStatus("host3", null, null));
        final DeploymentGroupStatusResponse status = new DeploymentGroupStatusResponse(DeploymentGroupStatusCommandTest.DEPLOYMENT_GROUP, Status.ROLLING_OUT, null, hostStatuses, null);
        Mockito.when(client.deploymentGroupStatus(DeploymentGroupStatusCommandTest.GROUP_NAME)).thenReturn(Futures.immediateFuture(status));
        Mockito.when(options.getString("name")).thenReturn(DeploymentGroupStatusCommandTest.GROUP_NAME);
        final int ret = command.run(options, client, out, false, null);
        Assert.assertEquals(0, ret);
        final String output = baos.toString().replaceAll("\\s+", "");
        final String expected = String.format(("Name: %s" + (((((((("Job Id: %s" + "Status: ROLLING_OUT") + "Host selectors:") + "  a = b") + "  foo = bar") + "HOST UP-TO-DATE JOB STATE") + "host1. X %s RUNNING") + "host2. X %s PULLING_IMAGE") + "host3. - -")), DeploymentGroupStatusCommandTest.GROUP_NAME, DeploymentGroupStatusCommandTest.JOB_ID, DeploymentGroupStatusCommandTest.JOB_ID, DeploymentGroupStatusCommandTest.JOB_ID).replace(" ", "");
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testDeploymentGroupStatusBeforeRollingUpdate() throws Exception {
        final DeploymentGroup deploymentGroupWithNoJob = DeploymentGroup.newBuilder().setName(DeploymentGroupStatusCommandTest.GROUP_NAME).setHostSelectors(DeploymentGroupStatusCommandTest.HOST_SELECTORS).setRolloutOptions(DeploymentGroupStatusCommandTest.ROLLOUT_OPTIONS).build();
        final List<DeploymentGroupStatusResponse.HostStatus> hostStatuses = Lists.newArrayList();
        hostStatuses.add(new DeploymentGroupStatusResponse.HostStatus("host1", null, null));
        hostStatuses.add(new DeploymentGroupStatusResponse.HostStatus("host2", null, null));
        hostStatuses.add(new DeploymentGroupStatusResponse.HostStatus("host3", null, null));
        final DeploymentGroupStatusResponse status = new DeploymentGroupStatusResponse(deploymentGroupWithNoJob, Status.IDLE, null, hostStatuses, null);
        Mockito.when(client.deploymentGroupStatus(DeploymentGroupStatusCommandTest.GROUP_NAME)).thenReturn(Futures.immediateFuture(status));
        Mockito.when(options.getString("name")).thenReturn(DeploymentGroupStatusCommandTest.GROUP_NAME);
        final int ret = command.run(options, client, out, false, null);
        Assert.assertEquals(0, ret);
        final String output = baos.toString().replaceAll("\\s+", "");
        final String expected = String.format(("Name: %s" + (((((((("Job Id: null" + "Status: IDLE") + "Host selectors:") + "  a = b") + "  foo = bar") + "HOST UP-TO-DATE JOB STATE") + "host1. - -") + "host2. - -") + "host3. - -")), DeploymentGroupStatusCommandTest.GROUP_NAME).replace(" ", "");
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testDeploymentGroupStatusWithError() throws Exception {
        final List<DeploymentGroupStatusResponse.HostStatus> hostStatuses = Lists.newArrayList();
        hostStatuses.add(new DeploymentGroupStatusResponse.HostStatus("host1", DeploymentGroupStatusCommandTest.JOB_ID, State.RUNNING));
        hostStatuses.add(new DeploymentGroupStatusResponse.HostStatus("host2", DeploymentGroupStatusCommandTest.JOB_ID, State.PULLING_IMAGE));
        hostStatuses.add(new DeploymentGroupStatusResponse.HostStatus("host3", null, null));
        final DeploymentGroupStatusResponse status = new DeploymentGroupStatusResponse(DeploymentGroupStatusCommandTest.DEPLOYMENT_GROUP, Status.ROLLING_OUT, "Oops!", hostStatuses, null);
        Mockito.when(client.deploymentGroupStatus(DeploymentGroupStatusCommandTest.GROUP_NAME)).thenReturn(Futures.immediateFuture(status));
        Mockito.when(options.getString("name")).thenReturn(DeploymentGroupStatusCommandTest.GROUP_NAME);
        final int ret = command.run(options, client, out, false, null);
        Assert.assertEquals(0, ret);
        final String output = baos.toString().replaceAll("\\s+", "");
        final String expected = String.format(("Name: %s" + ((((((((("Job Id: %s" + "Status: ROLLING_OUT") + "Host selectors:") + "  a = b") + "  foo = bar") + "Error: Oops!") + "HOST UP-TO-DATE JOB STATE") + "host1. X %s RUNNING") + "host2. X %s PULLING_IMAGE") + "host3. - -")), DeploymentGroupStatusCommandTest.GROUP_NAME, DeploymentGroupStatusCommandTest.JOB_ID, DeploymentGroupStatusCommandTest.JOB_ID, DeploymentGroupStatusCommandTest.JOB_ID).replace(" ", "");
        Assert.assertEquals(expected, output);
    }

    @Test
    public void testDeploymentGroupNotFound() throws Exception {
        final ListenableFuture<DeploymentGroupStatusResponse> nullFuture = Futures.immediateFuture(null);
        Mockito.when(client.deploymentGroupStatus(ArgumentMatchers.anyString())).thenReturn(nullFuture);
        final String name = "non-existent-group";
        Mockito.when(options.getString("name")).thenReturn(name);
        final int ret = command.run(options, client, out, false, null);
        Assert.assertEquals(1, ret);
        final String output = baos.toString();
        MatcherAssert.assertThat(output, CoreMatchers.containsString(String.format("Unknown deployment group: %s", name)));
    }

    @Test
    public void testDeploymentGroupStatusJson() throws Exception {
        final List<DeploymentGroupStatusResponse.HostStatus> hostStatuses = Lists.newArrayList();
        hostStatuses.add(new DeploymentGroupStatusResponse.HostStatus("host1", DeploymentGroupStatusCommandTest.JOB_ID, State.RUNNING));
        hostStatuses.add(new DeploymentGroupStatusResponse.HostStatus("host2", DeploymentGroupStatusCommandTest.JOB_ID, State.RUNNING));
        hostStatuses.add(new DeploymentGroupStatusResponse.HostStatus("host3", DeploymentGroupStatusCommandTest.JOB_ID, State.PULLING_IMAGE));
        final DeploymentGroupStatusResponse status = new DeploymentGroupStatusResponse(DeploymentGroupStatusCommandTest.DEPLOYMENT_GROUP, Status.ROLLING_OUT, null, hostStatuses, null);
        Mockito.when(client.deploymentGroupStatus(DeploymentGroupStatusCommandTest.GROUP_NAME)).thenReturn(Futures.immediateFuture(status));
        Mockito.when(options.getString("name")).thenReturn(DeploymentGroupStatusCommandTest.GROUP_NAME);
        final int ret = command.run(options, client, out, true, null);
        Assert.assertEquals(0, ret);
        final DeploymentGroupStatusResponse output = Json.read(baos.toString(), DeploymentGroupStatusResponse.class);
        Assert.assertEquals(status, output);
    }

    @Test
    public void testDeploymentGroupNotFoundJson() throws Exception {
        final ListenableFuture<DeploymentGroupStatusResponse> nullFuture = Futures.immediateFuture(null);
        Mockito.when(client.deploymentGroupStatus(ArgumentMatchers.anyString())).thenReturn(nullFuture);
        final String name = "non-existent-group";
        Mockito.when(options.getString("name")).thenReturn(name);
        final int ret = command.run(options, client, out, true, null);
        Assert.assertEquals(1, ret);
        final Map<String, Object> output = Json.read(baos.toString(), new TypeReference<Map<String, Object>>() {});
        Assert.assertEquals("DEPLOYMENT_GROUP_NOT_FOUND", output.get("status"));
    }
}

