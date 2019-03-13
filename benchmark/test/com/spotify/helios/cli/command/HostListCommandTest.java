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


import Goal.START;
import TaskStatus.State.RUNNING;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Futures;
import com.spotify.helios.cli.TestUtils;
import com.spotify.helios.client.HeliosClient;
import com.spotify.helios.common.descriptors.Deployment;
import com.spotify.helios.common.descriptors.HostStatus;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.common.descriptors.TaskStatus;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class HostListCommandTest {
    private final HeliosClient client = Mockito.mock(HeliosClient.class);

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private final PrintStream out = new PrintStream(baos);

    private static final String JOB_NAME = "job";

    private static final String JOB_VERSION1 = "1-aaa";

    private static final String JOB_VERSION2 = "3-ccc";

    private static final String JOB_VERSION3 = "2-bbb";

    private static final JobId JOB_ID1 = new JobId(HostListCommandTest.JOB_NAME, HostListCommandTest.JOB_VERSION1);

    private static final JobId JOB_ID2 = new JobId(HostListCommandTest.JOB_NAME, HostListCommandTest.JOB_VERSION2);

    private static final JobId JOB_ID3 = new JobId(HostListCommandTest.JOB_NAME, HostListCommandTest.JOB_VERSION3);

    private static final Job JOB1 = Job.newBuilder().setName(HostListCommandTest.JOB_NAME).setVersion(HostListCommandTest.JOB_VERSION1).build();

    private static final Job JOB2 = Job.newBuilder().setName(HostListCommandTest.JOB_NAME).setVersion(HostListCommandTest.JOB_VERSION2).build();

    private static final Job JOB3 = Job.newBuilder().setName(HostListCommandTest.JOB_NAME).setVersion(HostListCommandTest.JOB_VERSION3).build();

    private static final Map<JobId, Deployment> JOBS = ImmutableMap.of(HostListCommandTest.JOB_ID1, Deployment.newBuilder().build(), HostListCommandTest.JOB_ID2, Deployment.newBuilder().build(), HostListCommandTest.JOB_ID3, Deployment.newBuilder().build());

    private static final Map<JobId, TaskStatus> JOB_STATUSES = ImmutableMap.of(HostListCommandTest.JOB_ID1, TaskStatus.newBuilder().setJob(HostListCommandTest.JOB1).setGoal(START).setState(RUNNING).build(), HostListCommandTest.JOB_ID2, TaskStatus.newBuilder().setJob(HostListCommandTest.JOB2).setGoal(START).setState(RUNNING).build(), HostListCommandTest.JOB_ID3, TaskStatus.newBuilder().setJob(HostListCommandTest.JOB3).setGoal(START).setState(RUNNING).build());

    private static final Map<String, String> LABELS = ImmutableMap.of("foo", "bar", "baz", "qux");

    private static final List<String> EXPECTED_ORDER = ImmutableList.of("host1.", "host2.", "host3.");

    private HostStatus upStatus;

    private HostStatus downStatus;

    @Test
    public void testCommand() throws Exception {
        final int ret = runCommand();
        final String output = baos.toString();
        Assert.assertEquals(0, ret);
        Assert.assertThat(output, Matchers.containsString(("HOST      STATUS        DEPLOYED    RUNNING    CPUS    MEM     LOAD AVG    MEM USAGE    " + "OS              HELIOS     DOCKER          LABELS")));
        Assert.assertThat(output, Matchers.containsString(("host1.    Up 2 days     3           3          4       1 gb    0.10        0.53         " + "OS foo 0.1.0    0.8.420    1.7.0 (1.18)    foo=bar, baz=qux")));
        Assert.assertThat(output, Matchers.containsString(("host2.    Up 2 days     3           3          4       1 gb    0.10        0.53         " + "OS foo 0.1.0    0.8.420    1.7.0 (1.18)    foo=bar, baz=qux")));
        Assert.assertThat(output, Matchers.containsString(("host3.    Down 1 day    3           3          4       1 gb    0.10        0.53         " + "OS foo 0.1.0    0.8.420    1.7.0 (1.18)    foo=bar, baz=qux")));
    }

    @Test
    public void testQuietOutputIsSorted() throws Exception {
        final int ret = runCommand("-q");
        Assert.assertEquals(0, ret);
        Assert.assertEquals(HostListCommandTest.EXPECTED_ORDER, TestUtils.readFirstColumnFromOutput(baos.toString(), false));
    }

    @Test
    public void testNonQuietOutputIsSorted() throws Exception {
        final int ret = runCommand();
        Assert.assertEquals(0, ret);
        Assert.assertEquals(HostListCommandTest.EXPECTED_ORDER, TestUtils.readFirstColumnFromOutput(baos.toString(), true));
    }

    @Test(expected = ArgumentParserException.class)
    public void testInvalidStatusThrowsError() throws Exception {
        runCommand("--status", "DWN");
    }

    @Test
    public void testPatternFilter() throws Exception {
        final String hostname = "host1.example.com";
        final List<String> hosts = ImmutableList.of(hostname);
        Mockito.when(client.listHosts("host1")).thenReturn(Futures.immediateFuture(hosts));
        final Map<String, HostStatus> statusResponse = ImmutableMap.of(hostname, upStatus);
        Mockito.when(client.hostStatuses(ArgumentMatchers.eq(hosts), ArgumentMatchers.anyMapOf(String.class, String.class))).thenReturn(Futures.immediateFuture(statusResponse));
        final int ret = runCommand("host1");
        Assert.assertEquals(0, ret);
        Assert.assertEquals(ImmutableList.of("HOST", (hostname + ".")), TestUtils.readFirstColumnFromOutput(baos.toString(), false));
    }

    @Test
    public void testSelectorFilter() throws Exception {
        final String hostname = "foo1.example.com";
        final List<String> hosts = ImmutableList.of(hostname);
        Mockito.when(client.listHosts(ImmutableSet.of("foo=bar"))).thenReturn(Futures.immediateFuture(hosts));
        final Map<String, HostStatus> statusResponse = ImmutableMap.of(hostname, upStatus);
        Mockito.when(client.hostStatuses(ArgumentMatchers.eq(hosts), ArgumentMatchers.anyMapOf(String.class, String.class))).thenReturn(Futures.immediateFuture(statusResponse));
        final int ret = runCommand("--selector", "foo=bar");
        Assert.assertEquals(0, ret);
        Assert.assertEquals(ImmutableList.of("HOST", (hostname + ".")), TestUtils.readFirstColumnFromOutput(baos.toString(), false));
    }

    /**
     * Verify that the configuration of the '--selector' argument does not cause positional arguments
     * specified after the optional argument to be greedily parsed. This verifies a fix for a bug
     * where `nargs("+")` was used where it was not necessary, which causes a command like `--selector
     * foo=bar pattern` to be interpreted as two selectors of `foo=bar` and `pattern`.
     */
    @Test
    public void testSelectorSlurping() throws Exception {
        final List<String> hosts = ImmutableList.of("host-1");
        Mockito.when(client.hostStatuses(ArgumentMatchers.eq(hosts), ArgumentMatchers.anyMapOf(String.class, String.class))).thenReturn(Futures.immediateFuture(Collections.<String, HostStatus>emptyMap()));
        Mockito.when(client.listHosts("blah", ImmutableSet.of("foo=bar"))).thenReturn(Futures.immediateFuture(hosts));
        Assert.assertThat(runCommand("-s", "foo=bar", "blah"), Matchers.equalTo(0));
        Assert.assertThat(runCommand("blah", "-s", "foo=bar"), Matchers.equalTo(0));
        Mockito.when(client.listHosts("blarp", ImmutableSet.of("a=b", "z=1"))).thenReturn(Futures.immediateFuture(hosts));
        Assert.assertThat(runCommand("-s", "a=b", "-s", "z=1", "blarp"), Matchers.equalTo(0));
        Assert.assertThat(runCommand("blarp", "--selector", "a=b", "--selector", "z=1"), Matchers.equalTo(0));
    }
}

