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


import JobPriority.HIGH;
import JobTrackerStatus.RUNNING;
import TaskStatusFilter.ALL;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.ClusterStatus.BlackListInfo;
import org.apache.hadoop.mapred.JobClient.NetworkedJob;
import org.apache.hadoop.mapred.lib.IdentityMapper;
import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestNetworkedJob {
    private static String TEST_ROOT_DIR = new File(System.getProperty("test.build.data", "/tmp")).toURI().toString().replace(' ', '+');

    private static Path testDir = new Path(((TestNetworkedJob.TEST_ROOT_DIR) + "/test_mini_mr_local"));

    private static Path inFile = new Path(TestNetworkedJob.testDir, "in");

    private static Path outDir = new Path(TestNetworkedJob.testDir, "out");

    @Test(timeout = 5000)
    public void testGetNullCounters() throws Exception {
        // mock creation
        Job mockJob = Mockito.mock(Job.class);
        RunningJob underTest = new JobClient.NetworkedJob(mockJob);
        Mockito.when(mockJob.getCounters()).thenReturn(null);
        Assert.assertNull(underTest.getCounters());
        // verification
        Mockito.verify(mockJob).getCounters();
    }

    @Test(timeout = 500000)
    public void testGetJobStatus() throws IOException, ClassNotFoundException, InterruptedException {
        MiniMRClientCluster mr = null;
        FileSystem fileSys = null;
        try {
            mr = createMiniClusterWithCapacityScheduler();
            JobConf job = new JobConf(mr.getConfig());
            fileSys = FileSystem.get(job);
            fileSys.delete(TestNetworkedJob.testDir, true);
            FSDataOutputStream out = fileSys.create(TestNetworkedJob.inFile, true);
            out.writeBytes("This is a test file");
            out.close();
            FileInputFormat.setInputPaths(job, TestNetworkedJob.inFile);
            FileOutputFormat.setOutputPath(job, TestNetworkedJob.outDir);
            job.setInputFormat(TextInputFormat.class);
            job.setOutputFormat(TextOutputFormat.class);
            job.setMapperClass(IdentityMapper.class);
            job.setReducerClass(IdentityReducer.class);
            job.setNumReduceTasks(0);
            JobClient client = new JobClient(mr.getConfig());
            RunningJob rj = client.submitJob(job);
            JobID jobId = rj.getID();
            // The following asserts read JobStatus twice and ensure the returned
            // JobStatus objects correspond to the same Job.
            Assert.assertEquals("Expected matching JobIDs", jobId, client.getJob(jobId).getJobStatus().getJobID());
            Assert.assertEquals("Expected matching startTimes", rj.getJobStatus().getStartTime(), client.getJob(jobId).getJobStatus().getStartTime());
        } finally {
            if (fileSys != null) {
                fileSys.delete(TestNetworkedJob.testDir, true);
            }
            if (mr != null) {
                mr.stop();
            }
        }
    }

    /**
     * test JobConf
     *
     * @throws Exception
     * 		
     */
    @SuppressWarnings("deprecation")
    @Test(timeout = 500000)
    public void testNetworkedJob() throws Exception {
        // mock creation
        MiniMRClientCluster mr = null;
        FileSystem fileSys = null;
        try {
            mr = createMiniClusterWithCapacityScheduler();
            JobConf job = new JobConf(mr.getConfig());
            fileSys = FileSystem.get(job);
            fileSys.delete(TestNetworkedJob.testDir, true);
            FSDataOutputStream out = fileSys.create(TestNetworkedJob.inFile, true);
            out.writeBytes("This is a test file");
            out.close();
            FileInputFormat.setInputPaths(job, TestNetworkedJob.inFile);
            FileOutputFormat.setOutputPath(job, TestNetworkedJob.outDir);
            job.setInputFormat(TextInputFormat.class);
            job.setOutputFormat(TextOutputFormat.class);
            job.setMapperClass(IdentityMapper.class);
            job.setReducerClass(IdentityReducer.class);
            job.setNumReduceTasks(0);
            JobClient client = new JobClient(mr.getConfig());
            RunningJob rj = client.submitJob(job);
            JobID jobId = rj.getID();
            NetworkedJob runningJob = ((NetworkedJob) (client.getJob(jobId)));
            runningJob.setJobPriority(HIGH.name());
            // test getters
            Assert.assertTrue(runningJob.getConfiguration().toString().endsWith("0001/job.xml"));
            Assert.assertEquals(jobId, runningJob.getID());
            Assert.assertEquals(jobId.toString(), runningJob.getJobID());
            Assert.assertEquals("N/A", runningJob.getJobName());
            Assert.assertTrue(runningJob.getJobFile().endsWith(((".staging/" + (runningJob.getJobID())) + "/job.xml")));
            Assert.assertTrue(((runningJob.getTrackingURL().length()) > 0));
            Assert.assertTrue(((runningJob.mapProgress()) == 0.0F));
            Assert.assertTrue(((runningJob.reduceProgress()) == 0.0F));
            Assert.assertTrue(((runningJob.cleanupProgress()) == 0.0F));
            Assert.assertTrue(((runningJob.setupProgress()) == 0.0F));
            TaskCompletionEvent[] tce = runningJob.getTaskCompletionEvents(0);
            Assert.assertEquals(tce.length, 0);
            Assert.assertEquals("", runningJob.getHistoryUrl());
            Assert.assertFalse(runningJob.isRetired());
            Assert.assertEquals("", runningJob.getFailureInfo());
            Assert.assertEquals("N/A", runningJob.getJobStatus().getJobName());
            Assert.assertEquals(0, client.getMapTaskReports(jobId).length);
            try {
                client.getSetupTaskReports(jobId);
            } catch (YarnRuntimeException e) {
                Assert.assertEquals("Unrecognized task type: JOB_SETUP", e.getMessage());
            }
            try {
                client.getCleanupTaskReports(jobId);
            } catch (YarnRuntimeException e) {
                Assert.assertEquals("Unrecognized task type: JOB_CLEANUP", e.getMessage());
            }
            Assert.assertEquals(0, client.getReduceTaskReports(jobId).length);
            // test ClusterStatus
            ClusterStatus status = client.getClusterStatus(true);
            Assert.assertEquals(2, status.getActiveTrackerNames().size());
            // it method does not implemented and always return empty array or null;
            Assert.assertEquals(0, status.getBlacklistedTrackers());
            Assert.assertEquals(0, status.getBlacklistedTrackerNames().size());
            Assert.assertEquals(0, status.getBlackListedTrackersInfo().size());
            Assert.assertEquals(RUNNING, status.getJobTrackerStatus());
            Assert.assertEquals(1, status.getMapTasks());
            Assert.assertEquals(20, status.getMaxMapTasks());
            Assert.assertEquals(4, status.getMaxReduceTasks());
            Assert.assertEquals(0, status.getNumExcludedNodes());
            Assert.assertEquals(1, status.getReduceTasks());
            Assert.assertEquals(2, status.getTaskTrackers());
            Assert.assertEquals(0, status.getTTExpiryInterval());
            Assert.assertEquals(RUNNING, status.getJobTrackerStatus());
            Assert.assertEquals(0, status.getGraylistedTrackers());
            // test read and write
            ByteArrayOutputStream dataOut = new ByteArrayOutputStream();
            status.write(new DataOutputStream(dataOut));
            ClusterStatus status2 = new ClusterStatus();
            status2.readFields(new DataInputStream(new ByteArrayInputStream(dataOut.toByteArray())));
            Assert.assertEquals(status.getActiveTrackerNames(), status2.getActiveTrackerNames());
            Assert.assertEquals(status.getBlackListedTrackersInfo(), status2.getBlackListedTrackersInfo());
            Assert.assertEquals(status.getMapTasks(), status2.getMapTasks());
            // test taskStatusfilter
            JobClient.setTaskOutputFilter(job, ALL);
            Assert.assertEquals(ALL, JobClient.getTaskOutputFilter(job));
            // runningJob.setJobPriority(JobPriority.HIGH.name());
            // test default map
            Assert.assertEquals(20, client.getDefaultMaps());
            Assert.assertEquals(4, client.getDefaultReduces());
            Assert.assertEquals("jobSubmitDir", client.getSystemDir().getName());
            // test queue information
            JobQueueInfo[] rootQueueInfo = client.getRootQueues();
            Assert.assertEquals(1, rootQueueInfo.length);
            Assert.assertEquals("default", rootQueueInfo[0].getQueueName());
            JobQueueInfo[] qinfo = client.getQueues();
            Assert.assertEquals(1, qinfo.length);
            Assert.assertEquals("default", qinfo[0].getQueueName());
            Assert.assertEquals(0, client.getChildQueues("default").length);
            Assert.assertEquals(1, client.getJobsFromQueue("default").length);
            Assert.assertTrue(client.getJobsFromQueue("default")[0].getJobFile().endsWith("/job.xml"));
            JobQueueInfo qi = client.getQueueInfo("default");
            Assert.assertEquals("default", qi.getQueueName());
            Assert.assertEquals("running", qi.getQueueState());
            QueueAclsInfo[] aai = client.getQueueAclsForCurrentUser();
            Assert.assertEquals(2, aai.length);
            Assert.assertEquals("root", aai[0].getQueueName());
            Assert.assertEquals("default", aai[1].getQueueName());
            // test JobClient
            // The following asserts read JobStatus twice and ensure the returned
            // JobStatus objects correspond to the same Job.
            Assert.assertEquals("Expected matching JobIDs", jobId, client.getJob(jobId).getJobStatus().getJobID());
            Assert.assertEquals("Expected matching startTimes", rj.getJobStatus().getStartTime(), client.getJob(jobId).getJobStatus().getStartTime());
        } finally {
            if (fileSys != null) {
                fileSys.delete(TestNetworkedJob.testDir, true);
            }
            if (mr != null) {
                mr.stop();
            }
        }
    }

    /**
     * test BlackListInfo class
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 5000)
    public void testBlackListInfo() throws IOException {
        BlackListInfo info = new BlackListInfo();
        info.setBlackListReport("blackListInfo");
        info.setReasonForBlackListing("reasonForBlackListing");
        info.setTrackerName("trackerName");
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        DataOutput out = new DataOutputStream(byteOut);
        info.write(out);
        BlackListInfo info2 = new BlackListInfo();
        info2.readFields(new DataInputStream(new ByteArrayInputStream(byteOut.toByteArray())));
        Assert.assertEquals(info, info2);
        Assert.assertEquals(info.toString(), info2.toString());
        Assert.assertEquals("trackerName", info2.getTrackerName());
        Assert.assertEquals("reasonForBlackListing", info2.getReasonForBlackListing());
        Assert.assertEquals("blackListInfo", info2.getBlackListReport());
    }

    /**
     * test run from command line JobQueueClient
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 500000)
    public void testJobQueueClient() throws Exception {
        MiniMRClientCluster mr = null;
        FileSystem fileSys = null;
        PrintStream oldOut = System.out;
        try {
            mr = createMiniClusterWithCapacityScheduler();
            JobConf job = new JobConf(mr.getConfig());
            fileSys = FileSystem.get(job);
            fileSys.delete(TestNetworkedJob.testDir, true);
            FSDataOutputStream out = fileSys.create(TestNetworkedJob.inFile, true);
            out.writeBytes("This is a test file");
            out.close();
            FileInputFormat.setInputPaths(job, TestNetworkedJob.inFile);
            FileOutputFormat.setOutputPath(job, TestNetworkedJob.outDir);
            job.setInputFormat(TextInputFormat.class);
            job.setOutputFormat(TextOutputFormat.class);
            job.setMapperClass(IdentityMapper.class);
            job.setReducerClass(IdentityReducer.class);
            job.setNumReduceTasks(0);
            JobClient client = new JobClient(mr.getConfig());
            client.submitJob(job);
            JobQueueClient jobClient = new JobQueueClient(job);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            System.setOut(new PrintStream(bytes));
            String[] arg = new String[]{ "-list" };
            jobClient.run(arg);
            Assert.assertTrue(bytes.toString().contains("Queue Name : default"));
            Assert.assertTrue(bytes.toString().contains("Queue State : running"));
            bytes = new ByteArrayOutputStream();
            System.setOut(new PrintStream(bytes));
            String[] arg1 = new String[]{ "-showacls" };
            jobClient.run(arg1);
            Assert.assertTrue(bytes.toString().contains("Queue acls for user :"));
            Assert.assertTrue(bytes.toString().contains("root  ADMINISTER_QUEUE,SUBMIT_APPLICATIONS"));
            Assert.assertTrue(bytes.toString().contains("default  ADMINISTER_QUEUE,SUBMIT_APPLICATIONS"));
            // test for info and default queue
            bytes = new ByteArrayOutputStream();
            System.setOut(new PrintStream(bytes));
            String[] arg2 = new String[]{ "-info", "default" };
            jobClient.run(arg2);
            Assert.assertTrue(bytes.toString().contains("Queue Name : default"));
            Assert.assertTrue(bytes.toString().contains("Queue State : running"));
            Assert.assertTrue(bytes.toString().contains("Scheduling Info"));
            // test for info , default queue and jobs
            bytes = new ByteArrayOutputStream();
            System.setOut(new PrintStream(bytes));
            String[] arg3 = new String[]{ "-info", "default", "-showJobs" };
            jobClient.run(arg3);
            Assert.assertTrue(bytes.toString().contains("Queue Name : default"));
            Assert.assertTrue(bytes.toString().contains("Queue State : running"));
            Assert.assertTrue(bytes.toString().contains("Scheduling Info"));
            Assert.assertTrue(bytes.toString().contains("job_1"));
            String[] arg4 = new String[]{  };
            jobClient.run(arg4);
        } finally {
            System.setOut(oldOut);
            if (fileSys != null) {
                fileSys.delete(TestNetworkedJob.testDir, true);
            }
            if (mr != null) {
                mr.stop();
            }
        }
    }
}

