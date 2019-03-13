/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapreduce.tools;


import MRJobConfig.MR_CLIENT_JOB_MAX_RETRIES;
import State.FAILED;
import State.KILLED;
import State.PREP;
import State.RUNNING;
import State.SUCCEEDED;
import TaskType.MAP;
import TaskType.REDUCE;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Cluster;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobID;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestCLI {
    private static String jobIdStr = "job_1015298225799_0015";

    @Test
    public void testListAttemptIdsWithValidInput() throws Exception {
        JobID jobId = JobID.forName(TestCLI.jobIdStr);
        Cluster mockCluster = Mockito.mock(Cluster.class);
        Job job = Mockito.mock(Job.class);
        CLI cli = Mockito.spy(new CLI(new Configuration()));
        Mockito.doReturn(mockCluster).when(cli).createCluster();
        Mockito.when(job.getTaskReports(MAP)).thenReturn(getTaskReports(jobId, MAP));
        Mockito.when(job.getTaskReports(REDUCE)).thenReturn(getTaskReports(jobId, REDUCE));
        Mockito.when(mockCluster.getJob(jobId)).thenReturn(job);
        int retCode_MAP = cli.run(new String[]{ "-list-attempt-ids", TestCLI.jobIdStr, "MAP", "running" });
        // testing case insensitive behavior
        int retCode_map = cli.run(new String[]{ "-list-attempt-ids", TestCLI.jobIdStr, "map", "running" });
        int retCode_REDUCE = cli.run(new String[]{ "-list-attempt-ids", TestCLI.jobIdStr, "REDUCE", "running" });
        int retCode_completed = cli.run(new String[]{ "-list-attempt-ids", TestCLI.jobIdStr, "REDUCE", "completed" });
        Assert.assertEquals("MAP is a valid input,exit code should be 0", 0, retCode_MAP);
        Assert.assertEquals("map is a valid input,exit code should be 0", 0, retCode_map);
        Assert.assertEquals("REDUCE is a valid input,exit code should be 0", 0, retCode_REDUCE);
        Assert.assertEquals("REDUCE and completed are a valid inputs to -list-attempt-ids,exit code should be 0", 0, retCode_completed);
        Mockito.verify(job, Mockito.times(2)).getTaskReports(MAP);
        Mockito.verify(job, Mockito.times(2)).getTaskReports(REDUCE);
    }

    @Test
    public void testListAttemptIdsWithInvalidInputs() throws Exception {
        JobID jobId = JobID.forName(TestCLI.jobIdStr);
        Cluster mockCluster = Mockito.mock(Cluster.class);
        Job job = Mockito.mock(Job.class);
        CLI cli = Mockito.spy(new CLI(new Configuration()));
        Mockito.doReturn(mockCluster).when(cli).createCluster();
        Mockito.when(mockCluster.getJob(jobId)).thenReturn(job);
        int retCode_JOB_SETUP = cli.run(new String[]{ "-list-attempt-ids", TestCLI.jobIdStr, "JOB_SETUP", "running" });
        int retCode_JOB_CLEANUP = cli.run(new String[]{ "-list-attempt-ids", TestCLI.jobIdStr, "JOB_CLEANUP", "running" });
        int retCode_invalidTaskState = cli.run(new String[]{ "-list-attempt-ids", TestCLI.jobIdStr, "REDUCE", "complete" });
        String jobIdStr2 = "job_1015298225799_0016";
        int retCode_invalidJobId = cli.run(new String[]{ "-list-attempt-ids", jobIdStr2, "MAP", "running" });
        Assert.assertEquals("JOB_SETUP is an invalid input,exit code should be -1", (-1), retCode_JOB_SETUP);
        Assert.assertEquals("JOB_CLEANUP is an invalid input,exit code should be -1", (-1), retCode_JOB_CLEANUP);
        Assert.assertEquals("complete is an invalid input,exit code should be -1", (-1), retCode_invalidTaskState);
        Assert.assertEquals("Non existing job id should be skippted with -1", (-1), retCode_invalidJobId);
    }

    @Test
    public void testJobKIll() throws Exception {
        Cluster mockCluster = Mockito.mock(Cluster.class);
        CLI cli = Mockito.spy(new CLI(new Configuration()));
        Mockito.doReturn(mockCluster).when(cli).createCluster();
        String jobId1 = "job_1234654654_001";
        String jobId2 = "job_1234654654_002";
        String jobId3 = "job_1234654654_003";
        String jobId4 = "job_1234654654_004";
        Job mockJob1 = mockJob(mockCluster, jobId1, RUNNING);
        Job mockJob2 = mockJob(mockCluster, jobId2, KILLED);
        Job mockJob3 = mockJob(mockCluster, jobId3, FAILED);
        Job mockJob4 = mockJob(mockCluster, jobId4, PREP);
        int exitCode1 = cli.run(new String[]{ "-kill", jobId1 });
        Assert.assertEquals(0, exitCode1);
        Mockito.verify(mockJob1, Mockito.times(1)).killJob();
        int exitCode2 = cli.run(new String[]{ "-kill", jobId2 });
        Assert.assertEquals((-1), exitCode2);
        Mockito.verify(mockJob2, Mockito.times(0)).killJob();
        int exitCode3 = cli.run(new String[]{ "-kill", jobId3 });
        Assert.assertEquals((-1), exitCode3);
        Mockito.verify(mockJob3, Mockito.times(0)).killJob();
        int exitCode4 = cli.run(new String[]{ "-kill", jobId4 });
        Assert.assertEquals(0, exitCode4);
        Mockito.verify(mockJob4, Mockito.times(1)).killJob();
    }

    @Test
    public void testGetJobWithoutRetry() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(MR_CLIENT_JOB_MAX_RETRIES, 0);
        final Cluster mockCluster = Mockito.mock(Cluster.class);
        Mockito.when(mockCluster.getJob(ArgumentMatchers.any(JobID.class))).thenReturn(null);
        CLI cli = new CLI(conf);
        cli.cluster = mockCluster;
        Job job = cli.getJob(JobID.forName("job_1234654654_001"));
        Assert.assertTrue("job is not null", (job == null));
    }

    @Test
    public void testGetJobWithRetry() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(MR_CLIENT_JOB_MAX_RETRIES, 1);
        final Cluster mockCluster = Mockito.mock(Cluster.class);
        final Job mockJob = Job.getInstance(conf);
        Mockito.when(mockCluster.getJob(ArgumentMatchers.any(JobID.class))).thenReturn(null).thenReturn(mockJob);
        CLI cli = new CLI(conf);
        cli.cluster = mockCluster;
        Job job = cli.getJob(JobID.forName("job_1234654654_001"));
        Assert.assertTrue("job is null", (job != null));
    }

    @Test
    public void testListEvents() throws Exception {
        Cluster mockCluster = Mockito.mock(Cluster.class);
        CLI cli = Mockito.spy(new CLI(new Configuration()));
        Mockito.doReturn(mockCluster).when(cli).createCluster();
        String jobId1 = "job_1234654654_001";
        String jobId2 = "job_1234654656_002";
        Job mockJob1 = mockJob(mockCluster, jobId1, RUNNING);
        // Check exiting with non existing job
        int exitCode = cli.run(new String[]{ "-events", jobId2, "0", "10" });
        Assert.assertEquals((-1), exitCode);
    }

    @Test
    public void testLogs() throws Exception {
        Cluster mockCluster = Mockito.mock(Cluster.class);
        CLI cli = Mockito.spy(new CLI(new Configuration()));
        Mockito.doReturn(mockCluster).when(cli).createCluster();
        String jobId1 = "job_1234654654_001";
        String jobId2 = "job_1234654656_002";
        Job mockJob1 = mockJob(mockCluster, jobId1, SUCCEEDED);
        // Check exiting with non existing job
        int exitCode = cli.run(new String[]{ "-logs", jobId2 });
        Assert.assertEquals((-1), exitCode);
    }
}

