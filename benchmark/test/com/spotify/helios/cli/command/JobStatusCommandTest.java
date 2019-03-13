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
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.Futures;
import com.spotify.helios.client.HeliosClient;
import com.spotify.helios.common.descriptors.Deployment;
import com.spotify.helios.common.descriptors.Job;
import com.spotify.helios.common.descriptors.JobId;
import com.spotify.helios.common.descriptors.JobStatus;
import com.spotify.helios.common.descriptors.TaskStatus;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import net.sourceforge.argparse4j.inf.Namespace;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class JobStatusCommandTest {
    private final Namespace options = Mockito.mock(Namespace.class);

    private final HeliosClient client = Mockito.mock(HeliosClient.class);

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private final PrintStream out = new PrintStream(baos);

    private JobStatusCommand command;

    @Test
    public void testFilterJob() throws Exception {
        Mockito.when(options.getString("job")).thenReturn("foo");
        final JobId jobId1 = JobId.parse("foo:bar");
        final JobId jobId2 = JobId.parse("foo:bat");
        final Job job1 = JobStatusCommandTest.jobWithId(jobId1);
        final Job job2 = JobStatusCommandTest.jobWithId(jobId2);
        final Map<JobId, Job> jobs = ImmutableMap.of(jobId1, job1, jobId2, job2);
        Mockito.when(client.jobs("foo", "")).thenReturn(Futures.immediateFuture(jobs));
        final TaskStatus taskStatus1 = TaskStatus.newBuilder().setGoal(START).setJob(job1).setState(RUNNING).build();
        final TaskStatus taskStatus2 = TaskStatus.newBuilder().setGoal(START).setJob(job2).setState(RUNNING).build();
        final Map<JobId, JobStatus> statusMap = ImmutableMap.of(jobId1, JobStatus.newBuilder().setDeployments(ImmutableMap.of("host1", Deployment.of(jobId1, START))).setJob(job1).setTaskStatuses(ImmutableMap.of("host1", taskStatus1)).build(), jobId2, JobStatus.newBuilder().setDeployments(ImmutableMap.of("host2", Deployment.of(jobId2, START))).setJob(job2).setTaskStatuses(ImmutableMap.of("host2", taskStatus2)).build());
        Mockito.when(client.jobStatuses(jobs.keySet())).thenReturn(Futures.immediateFuture(statusMap));
        final int ret = command.run(options, client, out, false, null);
        Assert.assertEquals(0, ret);
        Assert.assertThat(baos.toString().split("\n"), Matchers.arrayContaining("JOB ID             HOST      GOAL     STATE      CONTAINER ID    PORTS    ", "foo:bar:6123457    host1.    START    RUNNING                             ", "foo:bat:c8aa21a    host2.    START    RUNNING                             "));
    }

    @Test
    public void testFilterJobAndHost() throws Exception {
        Mockito.when(options.getString("job")).thenReturn("foo");
        Mockito.when(options.getString("host")).thenReturn("host");
        final JobId jobId1 = JobId.parse("foo:bar");
        final JobId jobId2 = JobId.parse("foo:bat");
        final Job job1 = JobStatusCommandTest.jobWithId(jobId1);
        final Job job2 = JobStatusCommandTest.jobWithId(jobId2);
        final Map<JobId, Job> jobs = ImmutableMap.of(jobId1, job1, jobId2, job2);
        Mockito.when(client.jobs("foo", "host")).thenReturn(Futures.immediateFuture(jobs));
        final TaskStatus taskStatus1 = TaskStatus.newBuilder().setGoal(START).setJob(job1).setState(RUNNING).build();
        final TaskStatus taskStatus2 = TaskStatus.newBuilder().setGoal(START).setJob(job2).setState(RUNNING).build();
        final Map<JobId, JobStatus> statusMap = ImmutableMap.of(jobId1, JobStatus.newBuilder().setDeployments(ImmutableMap.of("host1", Deployment.of(jobId1, START))).setJob(job1).setTaskStatuses(ImmutableMap.of("host1", taskStatus1)).build(), jobId2, JobStatus.newBuilder().setDeployments(ImmutableMap.of("host2", Deployment.of(jobId2, START))).setJob(job2).setTaskStatuses(ImmutableMap.of("host2", taskStatus2)).build());
        Mockito.when(client.jobStatuses(jobs.keySet())).thenReturn(Futures.immediateFuture(statusMap));
        final int ret = command.run(options, client, out, false, null);
        Assert.assertEquals(0, ret);
        Assert.assertThat(baos.toString().split("\n"), Matchers.arrayContaining("JOB ID             HOST      GOAL     STATE      CONTAINER ID    PORTS    ", "foo:bar:6123457    host1.    START    RUNNING                             ", "foo:bat:c8aa21a    host2.    START    RUNNING                             "));
    }
}

