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


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Futures;
import com.spotify.helios.client.HeliosClient;
import com.spotify.helios.common.Json;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.common.descriptors.PortMapping;
import com.spotify.helios.common.descriptors.RolloutOptions;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Map;
import net.sourceforge.argparse4j.inf.Namespace;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class JobInspectCommandTest {
    private static final String JOB_NAME = "foo";

    private static final String JOB_VERSION = "2-bbb";

    private static final String JOB_NAME_VERSION = ((JobInspectCommandTest.JOB_NAME) + ":") + (JobInspectCommandTest.JOB_VERSION);

    private static final RolloutOptions ROLLOUT_OPTIONS = RolloutOptions.newBuilder().setIgnoreFailures(true).setMigrate(true).setOverlap(true).setParallelism(2).setTimeout(250L).setToken("foobar").build();

    private static final Job JOB = Job.newBuilder().setName(JobInspectCommandTest.JOB_NAME).setVersion(JobInspectCommandTest.JOB_VERSION).setCreated(((long) (0))).setExpires(new Date(0)).setSecondsToWaitBeforeKill(10).setAddCapabilities(ImmutableSet.of("cap1", "cap2")).setDropCapabilities(ImmutableSet.of("cap3", "cap4")).setPorts(ImmutableMap.of("foo", PortMapping.builder().ip("127.0.0.1").internalPort(80).externalPort(8080).protocol(UDP).build(), "bar", PortMapping.builder().ip("0.0.0.0").internalPort(123).externalPort(456).build())).setRuntime("nvidia").setRolloutOptions(JobInspectCommandTest.ROLLOUT_OPTIONS).build();

    private final Namespace options = Mockito.mock(Namespace.class);

    private final HeliosClient client = Mockito.mock(HeliosClient.class);

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private final PrintStream out = new PrintStream(baos);

    private JobInspectCommand command;

    private final Map<JobId, Job> jobs = ImmutableMap.of(new JobId(JobInspectCommandTest.JOB_NAME, JobInspectCommandTest.JOB_VERSION), JobInspectCommandTest.JOB);

    @Test
    public void test() throws Exception {
        Mockito.when(options.getString("job")).thenReturn(JobInspectCommandTest.JOB_NAME_VERSION);
        final int ret = command.run(options, client, out, false, null);
        Assert.assertEquals(0, ret);
        final String output = baos.toString();
        Assert.assertThat(output, Matchers.containsString("Created: Thu, 1 Jan 1970 00:00:00 +0000"));
        Assert.assertThat(output, Matchers.containsString("Expires: Thu, 1 Jan 1970 00:00:00 +0000"));
        Assert.assertThat(output, Matchers.containsString("Time to wait before kill (seconds): 10"));
        Assert.assertThat(output, Matchers.containsString("Add capabilities: cap1, cap2"));
        Assert.assertThat(output, Matchers.containsString("Drop capabilities: cap3, cap4"));
        Assert.assertThat(output, Matchers.containsString("Ports: bar=0.0.0.0:123:456/tcp"));
        Assert.assertThat(output, Matchers.containsString("foo=127.0.0.1:80:8080/udp"));
        Assert.assertThat(output, Matchers.containsString("Runtime: nvidia"));
        Assert.assertThat(output, Matchers.containsString(("Rollout options (null options will fallback to defaults " + (("at rolling-update time): timeout: 250, parallelism: 2, " + "migrate: true, overlap: true, token: foobar, ") + "ignoreFailures: true"))));
    }

    @Test
    public void testRolloutOptionsNull() throws Exception {
        final String jobVersion = "no-rollout-options";
        final String jobNameVersion = ((JobInspectCommandTest.JOB_NAME) + ":") + jobVersion;
        final Map<JobId, Job> jobs = ImmutableMap.of(new JobId(JobInspectCommandTest.JOB_NAME, jobVersion), JobInspectCommandTest.JOB.toBuilder().setRolloutOptions(null).build());
        Mockito.when(client.jobs(jobNameVersion)).thenReturn(Futures.immediateFuture(jobs));
        Mockito.when(options.getString("job")).thenReturn(jobNameVersion);
        final int ret = command.run(options, client, out, false, null);
        Assert.assertEquals(0, ret);
        final String output = baos.toString();
        Assert.assertThat(output, Matchers.not(Matchers.containsString("timeout: ")));
        Assert.assertThat(output, Matchers.not(Matchers.containsString("parallelism: ")));
        Assert.assertThat(output, Matchers.not(Matchers.containsString("migrate: ")));
        Assert.assertThat(output, Matchers.not(Matchers.containsString("overlap: ")));
        Assert.assertThat(output, Matchers.not(Matchers.containsString("token: ")));
        Assert.assertThat(output, Matchers.not(Matchers.containsString("ignoreFailures: ")));
    }

    @Test
    public void testJson() throws Exception {
        Mockito.when(options.getString("job")).thenReturn(JobInspectCommandTest.JOB_NAME_VERSION);
        final int ret = command.run(options, client, out, true, null);
        Assert.assertEquals(0, ret);
        final String output = baos.toString();
        final Job job = Json.read(output, Job.class);
        Assert.assertEquals(JobInspectCommandTest.JOB, job);
    }
}

