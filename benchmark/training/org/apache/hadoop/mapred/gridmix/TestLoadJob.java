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


import JobCreator.LOADJOB;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import static GridmixJobSubmissionPolicy.REPLAY;
import static GridmixJobSubmissionPolicy.SERIAL;


/* Test LoadJob Gridmix sends data to job and after that */
public class TestLoadJob extends CommonJobTest {
    public static final Logger LOG = LoggerFactory.getLogger(Gridmix.class);

    static {
        GenericTestUtils.setLogLevel(LoggerFactory.getLogger("org.apache.hadoop.mapred.gridmix"), Level.DEBUG);
        GenericTestUtils.setLogLevel(LoggerFactory.getLogger(StressJobFactory.class), Level.DEBUG);
    }

    /* test serial policy  with LoadJob. Task should execute without exceptions */
    @Test(timeout = 500000)
    public void testSerialSubmit() throws Exception {
        CommonJobTest.policy = SERIAL;
        TestLoadJob.LOG.info(("Serial started at " + (System.currentTimeMillis())));
        doSubmission(LOADJOB.name(), false);
        TestLoadJob.LOG.info(("Serial ended at " + (System.currentTimeMillis())));
    }

    /* test reply policy with LoadJob */
    @Test(timeout = 500000)
    public void testReplaySubmit() throws Exception {
        CommonJobTest.policy = REPLAY;
        TestLoadJob.LOG.info((" Replay started at " + (System.currentTimeMillis())));
        doSubmission(LOADJOB.name(), false);
        TestLoadJob.LOG.info((" Replay ended at " + (System.currentTimeMillis())));
    }
}

