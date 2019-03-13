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
package org.apache.hadoop.mapred.jobcontrol;


import Job.SUCCESS;
import Job.WAITING;
import java.util.ArrayList;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.JobID;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * This class performs unit test for Job/JobControl classes.
 */
public class TestJobControl {
    @SuppressWarnings("deprecation")
    @Test(timeout = 30000)
    public void testJobState() throws Exception {
        Job job_1 = getCopyJob();
        JobControl jc = new JobControl("Test");
        jc.addJob(job_1);
        Assert.assertEquals(WAITING, job_1.getState());
        job_1.setState(SUCCESS);
        Assert.assertEquals(WAITING, job_1.getState());
        org.apache.hadoop.mapreduce.Job mockjob = Mockito.mock(org.apache.hadoop.mapreduce.Job.class);
        org.apache.hadoop.mapreduce.JobID jid = new org.apache.hadoop.mapreduce.JobID("test", 0);
        Mockito.when(mockjob.getJobID()).thenReturn(jid);
        job_1.setJob(mockjob);
        Assert.assertEquals("job_test_0000", job_1.getMapredJobID());
        job_1.setMapredJobID("job_test_0001");
        Assert.assertEquals("job_test_0000", job_1.getMapredJobID());
        jc.stop();
    }

    @Test(timeout = 30000)
    public void testAddingDependingJob() throws Exception {
        Job job_1 = getCopyJob();
        ArrayList<Job> dependingJobs = new ArrayList<Job>();
        JobControl jc = new JobControl("Test");
        jc.addJob(job_1);
        Assert.assertEquals(WAITING, job_1.getState());
        Assert.assertTrue(job_1.addDependingJob(new Job(job_1.getJobConf(), dependingJobs)));
    }

    @Test(timeout = 30000)
    public void testJobControl() throws Exception {
        TestJobControl.doJobControlTest();
    }

    @Test(timeout = 30000)
    public void testGetAssignedJobId() throws Exception {
        JobConf jc = new JobConf();
        Job j = new Job(jc);
        // Just make sure no exception is thrown
        Assert.assertNull(j.getAssignedJobID());
        org.apache.hadoop.mapreduce.Job mockjob = Mockito.mock(org.apache.hadoop.mapreduce.Job.class);
        org.apache.hadoop.mapreduce.JobID jid = new org.apache.hadoop.mapreduce.JobID("test", 0);
        Mockito.when(mockjob.getJobID()).thenReturn(jid);
        j.setJob(mockjob);
        org.apache.hadoop.mapred.JobID expected = new org.apache.hadoop.mapred.JobID("test", 0);
        Assert.assertEquals(expected, j.getAssignedJobID());
        Mockito.verify(mockjob).getJobID();
    }
}

