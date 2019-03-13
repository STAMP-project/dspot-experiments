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
package org.apache.hadoop.mapreduce;


import JobPriority.NORMAL;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.ClusterMapReduceTestCase;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.tools.CLI;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * test CLI class. CLI class implemented  the Tool interface.
 * Here test that CLI sends correct command with options and parameters.
 */
public class TestMRJobClient extends ClusterMapReduceTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(TestMRJobClient.class);

    private static class BadOutputFormat extends TextOutputFormat<Object, Object> {
        @Override
        public void checkOutputSpecs(JobContext job) throws IOException {
            throw new IOException();
        }
    }

    @Test
    public void testJobSubmissionSpecsAndFiles() throws Exception {
        Configuration conf = createJobConf();
        Job job = MapReduceTestUtil.createJob(conf, getInputDir(), getOutputDir(), 1, 1);
        job.setOutputFormatClass(TestMRJobClient.BadOutputFormat.class);
        try {
            job.submit();
            Assert.fail("Should've thrown an exception while checking output specs.");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof IOException));
        }
        Cluster cluster = new Cluster(conf);
        Path jobStagingArea = JobSubmissionFiles.getStagingDir(cluster, job.getConfiguration());
        Path submitJobDir = new Path(jobStagingArea, "JobId");
        Path submitJobFile = JobSubmissionFiles.getJobConfPath(submitJobDir);
        Assert.assertFalse("Shouldn't have created a job file if job specs failed.", FileSystem.get(conf).exists(submitJobFile));
    }

    /**
     * main test method
     */
    @Test
    public void testJobClient() throws Exception {
        Configuration conf = createJobConf();
        Job job = runJob(conf);
        String jobId = job.getJobID().toString();
        // test all jobs list
        testAllJobList(jobId, conf);
        // test only submitted jobs list
        testSubmittedJobList(conf);
        // test job counter
        testGetCounter(jobId, conf);
        // status
        testJobStatus(jobId, conf);
        // test list of events
        testJobEvents(jobId, conf);
        // test job history
        testJobHistory(jobId, conf);
        // test tracker list
        testListTrackers(conf);
        // attempts list
        testListAttemptIds(jobId, conf);
        // black list
        testListBlackList(conf);
        // test method main and help screen
        startStop();
        // test a change job priority .
        testChangingJobPriority(jobId, conf);
        // submit job from file
        testSubmit(conf);
        // kill a task
        testKillTask(conf);
        // fail a task
        testfailTask(conf);
        // kill job
        testKillJob(conf);
        // download job config
        testConfig(jobId, conf);
    }

    /**
     * Test -list option displays job name.
     * The name is capped to 20 characters for display.
     */
    @Test
    public void testJobName() throws Exception {
        Configuration conf = createJobConf();
        CLI jc = createJobClient();
        Job job = MapReduceTestUtil.createJob(conf, getInputDir(), getOutputDir(), 1, 1, "short_name");
        job.setJobName("mapreduce");
        job.setPriority(NORMAL);
        job.waitForCompletion(true);
        String jobId = job.getJobID().toString();
        verifyJobName(jobId, "mapreduce", conf, jc);
        Job job2 = MapReduceTestUtil.createJob(conf, getInputDir(), getOutputDir(), 1, 1, "long_name");
        job2.setJobName("mapreduce_job_with_long_name");
        job2.setPriority(NORMAL);
        job2.waitForCompletion(true);
        jobId = job2.getJobID().toString();
        verifyJobName(jobId, "mapreduce_job_with_l", conf, jc);
    }
}

