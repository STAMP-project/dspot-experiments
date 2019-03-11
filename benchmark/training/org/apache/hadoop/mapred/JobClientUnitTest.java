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
package org.apache.hadoop.mapred;


import JobPriority.NORMAL;
import JobStatus.State.RUNNING;
import MRJobConfig.DEFAULT_MR_CLIENT_JOB_MAX_RETRIES;
import MRJobConfig.MR_CLIENT_JOB_MAX_RETRIES;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.hadoop.mapreduce.Cluster;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobStatus;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.TaskReport;
import org.apache.hadoop.mapreduce.TaskType;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@SuppressWarnings("deprecation")
public class JobClientUnitTest {
    public class TestJobClient extends JobClient {
        TestJobClient(JobConf jobConf) throws IOException {
            super(jobConf);
        }

        void setCluster(Cluster cluster) {
            this.cluster = cluster;
        }
    }

    public class TestJobClientGetJob extends JobClientUnitTest.TestJobClient {
        int lastGetJobRetriesCounter = 0;

        int getJobRetriesCounter = 0;

        int getJobRetries = 0;

        RunningJob runningJob;

        TestJobClientGetJob(JobConf jobConf) throws IOException {
            super(jobConf);
        }

        public int getLastGetJobRetriesCounter() {
            return lastGetJobRetriesCounter;
        }

        public void setGetJobRetries(int getJobRetries) {
            this.getJobRetries = getJobRetries;
        }

        public void setRunningJob(RunningJob runningJob) {
            this.runningJob = runningJob;
        }

        protected RunningJob getJobInner(final JobID jobid) throws IOException {
            if ((getJobRetriesCounter) >= (getJobRetries)) {
                lastGetJobRetriesCounter = getJobRetriesCounter;
                getJobRetriesCounter = 0;
                return runningJob;
            }
            (getJobRetriesCounter)++;
            return null;
        }
    }

    @Test
    public void testMapTaskReportsWithNullJob() throws Exception {
        JobClientUnitTest.TestJobClient client = new JobClientUnitTest.TestJobClient(new JobConf());
        Cluster mockCluster = Mockito.mock(Cluster.class);
        client.setCluster(mockCluster);
        JobID id = new JobID("test", 0);
        Mockito.when(mockCluster.getJob(id)).thenReturn(null);
        TaskReport[] result = client.getMapTaskReports(id);
        Assert.assertEquals(0, result.length);
        Mockito.verify(mockCluster).getJob(id);
    }

    @Test
    public void testReduceTaskReportsWithNullJob() throws Exception {
        JobClientUnitTest.TestJobClient client = new JobClientUnitTest.TestJobClient(new JobConf());
        Cluster mockCluster = Mockito.mock(Cluster.class);
        client.setCluster(mockCluster);
        JobID id = new JobID("test", 0);
        Mockito.when(mockCluster.getJob(id)).thenReturn(null);
        TaskReport[] result = client.getReduceTaskReports(id);
        Assert.assertEquals(0, result.length);
        Mockito.verify(mockCluster).getJob(id);
    }

    @Test
    public void testSetupTaskReportsWithNullJob() throws Exception {
        JobClientUnitTest.TestJobClient client = new JobClientUnitTest.TestJobClient(new JobConf());
        Cluster mockCluster = Mockito.mock(Cluster.class);
        client.setCluster(mockCluster);
        JobID id = new JobID("test", 0);
        Mockito.when(mockCluster.getJob(id)).thenReturn(null);
        TaskReport[] result = client.getSetupTaskReports(id);
        Assert.assertEquals(0, result.length);
        Mockito.verify(mockCluster).getJob(id);
    }

    @Test
    public void testCleanupTaskReportsWithNullJob() throws Exception {
        JobClientUnitTest.TestJobClient client = new JobClientUnitTest.TestJobClient(new JobConf());
        Cluster mockCluster = Mockito.mock(Cluster.class);
        client.setCluster(mockCluster);
        JobID id = new JobID("test", 0);
        Mockito.when(mockCluster.getJob(id)).thenReturn(null);
        TaskReport[] result = client.getCleanupTaskReports(id);
        Assert.assertEquals(0, result.length);
        Mockito.verify(mockCluster).getJob(id);
    }

    @Test
    public void testShowJob() throws Exception {
        JobClientUnitTest.TestJobClient client = new JobClientUnitTest.TestJobClient(new JobConf());
        long startTime = System.currentTimeMillis();
        JobID jobID = new JobID(String.valueOf(startTime), 12345);
        JobStatus mockJobStatus = Mockito.mock(JobStatus.class);
        Mockito.when(mockJobStatus.getJobID()).thenReturn(jobID);
        Mockito.when(mockJobStatus.getJobName()).thenReturn(jobID.toString());
        Mockito.when(mockJobStatus.getState()).thenReturn(RUNNING);
        Mockito.when(mockJobStatus.getStartTime()).thenReturn(startTime);
        Mockito.when(mockJobStatus.getUsername()).thenReturn("mockuser");
        Mockito.when(mockJobStatus.getQueue()).thenReturn("mockqueue");
        Mockito.when(mockJobStatus.getPriority()).thenReturn(NORMAL);
        Mockito.when(mockJobStatus.getNumUsedSlots()).thenReturn(1);
        Mockito.when(mockJobStatus.getNumReservedSlots()).thenReturn(1);
        Mockito.when(mockJobStatus.getUsedMem()).thenReturn(1024);
        Mockito.when(mockJobStatus.getReservedMem()).thenReturn(512);
        Mockito.when(mockJobStatus.getNeededMem()).thenReturn(2048);
        Mockito.when(mockJobStatus.getSchedulingInfo()).thenReturn("NA");
        Job mockJob = Mockito.mock(Job.class);
        Mockito.when(mockJob.getTaskReports(ArgumentMatchers.isA(TaskType.class))).thenReturn(new TaskReport[5]);
        Cluster mockCluster = Mockito.mock(Cluster.class);
        Mockito.when(mockCluster.getJob(jobID)).thenReturn(mockJob);
        client.setCluster(mockCluster);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        client.displayJobList(new JobStatus[]{ mockJobStatus }, new PrintWriter(out));
        String commandLineOutput = out.toString();
        System.out.println(commandLineOutput);
        Assert.assertTrue(commandLineOutput.contains("Total jobs:1"));
        Mockito.verify(mockJobStatus, Mockito.atLeastOnce()).getJobID();
        Mockito.verify(mockJobStatus).getState();
        Mockito.verify(mockJobStatus).getStartTime();
        Mockito.verify(mockJobStatus).getUsername();
        Mockito.verify(mockJobStatus).getQueue();
        Mockito.verify(mockJobStatus).getPriority();
        Mockito.verify(mockJobStatus).getNumUsedSlots();
        Mockito.verify(mockJobStatus).getNumReservedSlots();
        Mockito.verify(mockJobStatus).getUsedMem();
        Mockito.verify(mockJobStatus).getReservedMem();
        Mockito.verify(mockJobStatus).getNeededMem();
        Mockito.verify(mockJobStatus).getSchedulingInfo();
        // This call should not go to each AM.
        Mockito.verify(mockCluster, Mockito.never()).getJob(jobID);
        Mockito.verify(mockJob, Mockito.never()).getTaskReports(ArgumentMatchers.isA(TaskType.class));
    }

    @Test
    public void testGetJobWithUnknownJob() throws Exception {
        JobClientUnitTest.TestJobClient client = new JobClientUnitTest.TestJobClient(new JobConf());
        Cluster mockCluster = Mockito.mock(Cluster.class);
        client.setCluster(mockCluster);
        JobID id = new JobID("unknown", 0);
        Mockito.when(mockCluster.getJob(id)).thenReturn(null);
        Assert.assertNull(client.getJob(id));
    }

    @Test
    public void testGetJobRetry() throws Exception {
        // To prevent the test from running for a very long time, lower the retry
        JobConf conf = new JobConf();
        conf.setInt(MR_CLIENT_JOB_MAX_RETRIES, 2);
        JobClientUnitTest.TestJobClientGetJob client = new JobClientUnitTest.TestJobClientGetJob(conf);
        JobID id = new JobID("ajob", 1);
        RunningJob rj = Mockito.mock(RunningJob.class);
        client.setRunningJob(rj);
        // no retry
        Assert.assertNotNull(client.getJob(id));
        Assert.assertEquals(client.getLastGetJobRetriesCounter(), 0);
        // 2 retries
        client.setGetJobRetries(2);
        Assert.assertNotNull(client.getJob(id));
        Assert.assertEquals(client.getLastGetJobRetriesCounter(), 2);
        // beyond yarn.app.mapreduce.client.job.max-retries, will get null
        client.setGetJobRetries(3);
        Assert.assertNull(client.getJob(id));
    }

    @Test
    public void testGetJobRetryDefault() throws Exception {
        // To prevent the test from running for a very long time, lower the retry
        JobConf conf = new JobConf();
        JobClientUnitTest.TestJobClientGetJob client = new JobClientUnitTest.TestJobClientGetJob(conf);
        JobID id = new JobID("ajob", 1);
        RunningJob rj = Mockito.mock(RunningJob.class);
        client.setRunningJob(rj);
        // 3 retries (default)
        client.setGetJobRetries(DEFAULT_MR_CLIENT_JOB_MAX_RETRIES);
        Assert.assertNotNull(client.getJob(id));
        Assert.assertEquals(client.getLastGetJobRetriesCounter(), DEFAULT_MR_CLIENT_JOB_MAX_RETRIES);
        // beyond yarn.app.mapreduce.client.job.max-retries, will get null
        client.setGetJobRetries(((MRJobConfig.DEFAULT_MR_CLIENT_JOB_MAX_RETRIES) + 1));
        Assert.assertNull(client.getJob(id));
    }
}

