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


import ControlledJob.State;
import JobControl.ThreadState;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.HadoopTestCase;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.MapReduceTestUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class performs unit test for Job/JobControl classes.
 */
public class TestMapReduceJobControl extends HadoopTestCase {
    public static final Logger LOG = LoggerFactory.getLogger(TestMapReduceJobControl.class);

    static Path rootDataDir = new Path(System.getProperty("test.build.data", "."), "TestData");

    static Path indir = new Path(TestMapReduceJobControl.rootDataDir, "indir");

    static Path outdir_1 = new Path(TestMapReduceJobControl.rootDataDir, "outdir_1");

    static Path outdir_2 = new Path(TestMapReduceJobControl.rootDataDir, "outdir_2");

    static Path outdir_3 = new Path(TestMapReduceJobControl.rootDataDir, "outdir_3");

    static Path outdir_4 = new Path(TestMapReduceJobControl.rootDataDir, "outdir_4");

    static ControlledJob cjob1 = null;

    static ControlledJob cjob2 = null;

    static ControlledJob cjob3 = null;

    static ControlledJob cjob4 = null;

    public TestMapReduceJobControl() throws IOException {
        super(HadoopTestCase.LOCAL_MR, HadoopTestCase.LOCAL_FS, 2, 2);
    }

    @Test
    public void testJobControlWithFailJob() throws Exception {
        TestMapReduceJobControl.LOG.info("Starting testJobControlWithFailJob");
        Configuration conf = createJobConf();
        cleanupData(conf);
        // create a Fail job
        Job job1 = MapReduceTestUtil.createFailJob(conf, TestMapReduceJobControl.outdir_1, TestMapReduceJobControl.indir);
        // create job dependencies
        JobControl theControl = createDependencies(conf, job1);
        // wait till all the jobs complete
        waitTillAllFinished(theControl);
        Assert.assertTrue(((TestMapReduceJobControl.cjob1.getJobState()) == (State.FAILED)));
        Assert.assertTrue(((TestMapReduceJobControl.cjob2.getJobState()) == (State.SUCCESS)));
        Assert.assertTrue(((TestMapReduceJobControl.cjob3.getJobState()) == (State.DEPENDENT_FAILED)));
        Assert.assertTrue(((TestMapReduceJobControl.cjob4.getJobState()) == (State.DEPENDENT_FAILED)));
        theControl.stop();
    }

    @Test
    public void testJobControlWithKillJob() throws Exception {
        TestMapReduceJobControl.LOG.info("Starting testJobControlWithKillJob");
        Configuration conf = createJobConf();
        cleanupData(conf);
        Job job1 = MapReduceTestUtil.createKillJob(conf, TestMapReduceJobControl.outdir_1, TestMapReduceJobControl.indir);
        JobControl theControl = createDependencies(conf, job1);
        while ((TestMapReduceJobControl.cjob1.getJobState()) != (State.RUNNING)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        } 
        // verify adding dependingJo to RUNNING job fails.
        Assert.assertFalse(TestMapReduceJobControl.cjob1.addDependingJob(TestMapReduceJobControl.cjob2));
        // suspend jobcontrol and resume it again
        theControl.suspend();
        Assert.assertTrue(((theControl.getThreadState()) == (ThreadState.SUSPENDED)));
        theControl.resume();
        // kill the first job.
        TestMapReduceJobControl.cjob1.killJob();
        // wait till all the jobs complete
        waitTillAllFinished(theControl);
        Assert.assertTrue(((TestMapReduceJobControl.cjob1.getJobState()) == (State.FAILED)));
        Assert.assertTrue(((TestMapReduceJobControl.cjob2.getJobState()) == (State.SUCCESS)));
        Assert.assertTrue(((TestMapReduceJobControl.cjob3.getJobState()) == (State.DEPENDENT_FAILED)));
        Assert.assertTrue(((TestMapReduceJobControl.cjob4.getJobState()) == (State.DEPENDENT_FAILED)));
        theControl.stop();
    }

    @Test
    public void testJobControl() throws Exception {
        TestMapReduceJobControl.LOG.info("Starting testJobControl");
        Configuration conf = createJobConf();
        cleanupData(conf);
        Job job1 = MapReduceTestUtil.createCopyJob(conf, TestMapReduceJobControl.outdir_1, TestMapReduceJobControl.indir);
        JobControl theControl = createDependencies(conf, job1);
        // wait till all the jobs complete
        waitTillAllFinished(theControl);
        Assert.assertEquals("Some jobs failed", 0, theControl.getFailedJobList().size());
        theControl.stop();
    }

    @Test(timeout = 30000)
    public void testControlledJob() throws Exception {
        TestMapReduceJobControl.LOG.info("Starting testControlledJob");
        Configuration conf = createJobConf();
        cleanupData(conf);
        Job job1 = MapReduceTestUtil.createCopyJob(conf, TestMapReduceJobControl.outdir_1, TestMapReduceJobControl.indir);
        JobControl theControl = createDependencies(conf, job1);
        while ((TestMapReduceJobControl.cjob1.getJobState()) != (State.RUNNING)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        } 
        Assert.assertNotNull(TestMapReduceJobControl.cjob1.getMapredJobId());
        // wait till all the jobs complete
        waitTillAllFinished(theControl);
        Assert.assertEquals("Some jobs failed", 0, theControl.getFailedJobList().size());
        theControl.stop();
    }
}

