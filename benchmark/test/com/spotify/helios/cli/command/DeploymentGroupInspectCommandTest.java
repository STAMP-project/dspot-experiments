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


import DeploymentGroup.RollingUpdateReason.MANUAL;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.Futures;
import com.spotify.helios.client.HeliosClient;
import com.spotify.helios.common.Json;
import com.spotify.helios.common.descriptors.DeploymentGroup;
import com.spotify.helios.common.descriptors.HostSelector;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.common.descriptors.RolloutOptions;
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
import org.mockito.Mockito;


public class DeploymentGroupInspectCommandTest {
    private static final String NAME = "foo-group";

    private static final String NON_EXISTENT_NAME = "bar-group";

    private static final JobId JOB = new JobId("foo-job", "0.1.0");

    private static final List<HostSelector> HOST_SELECTORS = ImmutableList.of(HostSelector.parse("foo=bar"), HostSelector.parse("baz=qux"));

    private static final DeploymentGroup DEPLOYMENT_GROUP = DeploymentGroup.newBuilder().setName(DeploymentGroupInspectCommandTest.NAME).setHostSelectors(DeploymentGroupInspectCommandTest.HOST_SELECTORS).setJobId(DeploymentGroupInspectCommandTest.JOB).build();

    private static final DeploymentGroup DEPLOYMENT_GROUP_WITH_OPTIONS = DeploymentGroup.newBuilder().setName(DeploymentGroupInspectCommandTest.NAME).setHostSelectors(DeploymentGroupInspectCommandTest.HOST_SELECTORS).setJobId(DeploymentGroupInspectCommandTest.JOB).setRollingUpdateReason(MANUAL).setRolloutOptions(RolloutOptions.newBuilder().setMigrate(true).setOverlap(true).setParallelism(2).setTimeout(10L).setToken("foo").build()).build();

    private final Namespace options = Mockito.mock(Namespace.class);

    private final HeliosClient client = Mockito.mock(HeliosClient.class);

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private final PrintStream out = new PrintStream(baos);

    private DeploymentGroupInspectCommand command;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testDeploymentGroupInspectCommand() throws Exception {
        Mockito.when(options.getString("name")).thenReturn(DeploymentGroupInspectCommandTest.NAME);
        final int ret = command.run(options, client, out, false, null);
        Assert.assertEquals(0, ret);
        final String output = baos.toString();
        MatcherAssert.assertThat(output, CoreMatchers.containsString(("Name: " + (DeploymentGroupInspectCommandTest.NAME))));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("Host selectors:"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("  foo = bar"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("  baz = qux"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString(("Job: " + (DeploymentGroupInspectCommandTest.JOB.toString()))));
    }

    @Test
    public void testDeploymentGroupInspectCommandWithOptions() throws Exception {
        Mockito.when(client.deploymentGroup(DeploymentGroupInspectCommandTest.NAME)).thenReturn(Futures.immediateFuture(DeploymentGroupInspectCommandTest.DEPLOYMENT_GROUP_WITH_OPTIONS));
        Mockito.when(options.getString("name")).thenReturn(DeploymentGroupInspectCommandTest.NAME);
        final int ret = command.run(options, client, out, false, null);
        Assert.assertEquals(0, ret);
        final String output = baos.toString();
        MatcherAssert.assertThat(output, CoreMatchers.containsString(("Name: " + (DeploymentGroupInspectCommandTest.NAME))));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("Host selectors:"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("  foo = bar"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("  baz = qux"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString(("Job: " + (DeploymentGroupInspectCommandTest.JOB.toString()))));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("Rolling update reason: MANUAL"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("Rollout options:"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("Migrate: true"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("Overlap: true"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("Parallelism: 2"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("Timeout: 10"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("Token: foo"));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("Ignore failures: false"));
    }

    @Test
    public void testDeploymentGroupInspectCommandJson() throws Exception {
        Mockito.when(options.getString("name")).thenReturn(DeploymentGroupInspectCommandTest.NAME);
        final int ret = command.run(options, client, out, true, null);
        Assert.assertEquals(0, ret);
        final DeploymentGroup output = Json.read(baos.toString(), DeploymentGroup.class);
        Assert.assertEquals(DeploymentGroupInspectCommandTest.DEPLOYMENT_GROUP, output);
    }

    @Test
    public void testDeploymentGroupInspectCommandNotFound() throws Exception {
        Mockito.when(options.getString("name")).thenReturn(DeploymentGroupInspectCommandTest.NON_EXISTENT_NAME);
        final int ret = command.run(options, client, out, false, null);
        Assert.assertEquals(1, ret);
        final String output = baos.toString();
        MatcherAssert.assertThat(output, CoreMatchers.containsString(("Unknown deployment group: " + (DeploymentGroupInspectCommandTest.NON_EXISTENT_NAME))));
    }

    @Test
    public void testDeploymentGroupInspectCommandNotFoundJson() throws Exception {
        Mockito.when(options.getString("name")).thenReturn(DeploymentGroupInspectCommandTest.NON_EXISTENT_NAME);
        final int ret = command.run(options, client, out, true, null);
        Assert.assertEquals(1, ret);
        final Map<String, Object> output = Json.read(baos.toString(), new TypeReference<Map<String, Object>>() {});
        Assert.assertEquals("DEPLOYMENT_GROUP_NOT_FOUND", output.get("status"));
    }
}

