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
package org.apache.hadoop.mapreduce.lib.jobcontrol;


import ControlledJob.State.DEPENDENT_FAILED;
import ControlledJob.State.FAILED;
import ControlledJob.State.SUCCESS;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests the JobControl API using mock and stub Job instances.
 */
public class TestMapReduceJobControlWithMocks {
    @Test
    public void testSuccessfulJobs() throws Exception {
        JobControl jobControl = new JobControl("Test");
        ControlledJob job1 = createSuccessfulControlledJob(jobControl);
        ControlledJob job2 = createSuccessfulControlledJob(jobControl);
        ControlledJob job3 = createSuccessfulControlledJob(jobControl, job1, job2);
        ControlledJob job4 = createSuccessfulControlledJob(jobControl, job3);
        runJobControl(jobControl);
        Assert.assertEquals("Success list", 4, jobControl.getSuccessfulJobList().size());
        Assert.assertEquals("Failed list", 0, jobControl.getFailedJobList().size());
        Assert.assertEquals(SUCCESS, job1.getJobState());
        Assert.assertEquals(SUCCESS, job2.getJobState());
        Assert.assertEquals(SUCCESS, job3.getJobState());
        Assert.assertEquals(SUCCESS, job4.getJobState());
        jobControl.stop();
    }

    @Test
    public void testFailedJob() throws Exception {
        JobControl jobControl = new JobControl("Test");
        ControlledJob job1 = createFailedControlledJob(jobControl);
        ControlledJob job2 = createSuccessfulControlledJob(jobControl);
        ControlledJob job3 = createSuccessfulControlledJob(jobControl, job1, job2);
        ControlledJob job4 = createSuccessfulControlledJob(jobControl, job3);
        runJobControl(jobControl);
        Assert.assertEquals("Success list", 1, jobControl.getSuccessfulJobList().size());
        Assert.assertEquals("Failed list", 3, jobControl.getFailedJobList().size());
        Assert.assertEquals(FAILED, job1.getJobState());
        Assert.assertEquals(SUCCESS, job2.getJobState());
        Assert.assertEquals(DEPENDENT_FAILED, job3.getJobState());
        Assert.assertEquals(DEPENDENT_FAILED, job4.getJobState());
        jobControl.stop();
    }

    @Test
    public void testErrorWhileSubmitting() throws Exception {
        JobControl jobControl = new JobControl("Test");
        Job mockJob = Mockito.mock(Job.class);
        ControlledJob job1 = new ControlledJob(mockJob, null);
        Mockito.when(mockJob.getConfiguration()).thenReturn(new Configuration());
        Mockito.doThrow(new IncompatibleClassChangeError("This is a test")).when(mockJob).submit();
        jobControl.addJob(job1);
        runJobControl(jobControl);
        try {
            Assert.assertEquals("Success list", 0, jobControl.getSuccessfulJobList().size());
            Assert.assertEquals("Failed list", 1, jobControl.getFailedJobList().size());
            Assert.assertEquals(FAILED, job1.getJobState());
        } finally {
            jobControl.stop();
        }
    }

    @Test
    public void testKillJob() throws Exception {
        JobControl jobControl = new JobControl("Test");
        ControlledJob job = createFailedControlledJob(jobControl);
        job.killJob();
        // Verify that killJob() was called on the mock Job
        Mockito.verify(job.getJob()).killJob();
    }
}

