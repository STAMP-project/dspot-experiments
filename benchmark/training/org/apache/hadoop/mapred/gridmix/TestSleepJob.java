/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapred.gridmix;


import JobCreator.SLEEPJOB;
import SleepJob.SLEEPJOB_MAPTASK_ONLY;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.tools.rumen.JobStory;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import static GridmixJobSubmissionPolicy.REPLAY;
import static GridmixJobSubmissionPolicy.SERIAL;
import static GridmixJobSubmissionPolicy.STRESS;


public class TestSleepJob extends CommonJobTest {
    public static final Logger LOG = LoggerFactory.getLogger(Gridmix.class);

    static {
        GenericTestUtils.setLogLevel(LoggerFactory.getLogger("org.apache.hadoop.mapred.gridmix"), Level.DEBUG);
    }

    static GridmixJobSubmissionPolicy policy = REPLAY;

    @Test
    public void testMapTasksOnlySleepJobs() throws Exception {
        Configuration configuration = GridmixTestUtils.mrvl.getConfig();
        DebugJobProducer jobProducer = new DebugJobProducer(5, configuration);
        configuration.setBoolean(SLEEPJOB_MAPTASK_ONLY, true);
        UserGroupInformation ugi = UserGroupInformation.getLoginUser();
        JobStory story;
        int seq = 1;
        while ((story = jobProducer.getNextJob()) != null) {
            GridmixJob gridmixJob = SLEEPJOB.createGridmixJob(configuration, 0, story, new Path("ignored"), ugi, (seq++));
            gridmixJob.buildSplits(null);
            Job job = gridmixJob.call();
            Assert.assertEquals(0, job.getNumReduceTasks());
        } 
        jobProducer.close();
        Assert.assertEquals(6, seq);
    }

    /* test RandomLocation */
    @Test
    public void testRandomLocation() throws Exception {
        UserGroupInformation ugi = UserGroupInformation.getLoginUser();
        testRandomLocation(1, 10, ugi);
        testRandomLocation(2, 10, ugi);
    }

    // test Serial submit
    @Test
    public void testSerialSubmit() throws Exception {
        // set policy
        TestSleepJob.policy = SERIAL;
        TestSleepJob.LOG.info(("Serial started at " + (System.currentTimeMillis())));
        doSubmission(SLEEPJOB.name(), false);
        TestSleepJob.LOG.info(("Serial ended at " + (System.currentTimeMillis())));
    }

    @Test
    public void testReplaySubmit() throws Exception {
        TestSleepJob.policy = REPLAY;
        TestSleepJob.LOG.info((" Replay started at " + (System.currentTimeMillis())));
        doSubmission(SLEEPJOB.name(), false);
        TestSleepJob.LOG.info((" Replay ended at " + (System.currentTimeMillis())));
    }

    @Test
    public void testStressSubmit() throws Exception {
        TestSleepJob.policy = STRESS;
        TestSleepJob.LOG.info((" Replay started at " + (System.currentTimeMillis())));
        doSubmission(SLEEPJOB.name(), false);
        TestSleepJob.LOG.info((" Replay ended at " + (System.currentTimeMillis())));
    }
}

