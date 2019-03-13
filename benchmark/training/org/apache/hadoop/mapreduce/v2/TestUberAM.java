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
package org.apache.hadoop.mapreduce.v2;


import JobStatus.State.FAILED;
import TaskCompletionEvent.Status;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskCompletionEvent;
import org.apache.hadoop.mapreduce.TaskID;
import org.apache.hadoop.mapreduce.TaskType;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestUberAM extends TestMRJobs {
    private static final Logger LOG = LoggerFactory.getLogger(TestUberAM.class);

    @Override
    @Test
    public void testSleepJob() throws Exception {
        numSleepReducers = 1;
        super.testSleepJob();
    }

    @Test
    public void testSleepJobWithMultipleReducers() throws Exception {
        numSleepReducers = 3;
        super.testSleepJob();
    }

    @Override
    @Test
    public void testRandomWriter() throws IOException, ClassNotFoundException, InterruptedException {
        super.testRandomWriter();
    }

    @Override
    @Test
    public void testFailingMapper() throws IOException, ClassNotFoundException, InterruptedException {
        TestUberAM.LOG.info("\n\n\nStarting uberized testFailingMapper().");
        if (!(new File(MiniMRYarnCluster.APPJAR).exists())) {
            TestUberAM.LOG.info((("MRAppJar " + (MiniMRYarnCluster.APPJAR)) + " not found. Not running test."));
            return;
        }
        Job job = runFailingMapperJob();
        // should be able to get diags for single task attempt...
        TaskID taskID = new TaskID(job.getJobID(), TaskType.MAP, 0);
        TaskAttemptID aId = new TaskAttemptID(taskID, 0);
        System.out.println((("Diagnostics for " + aId) + " :"));
        for (String diag : job.getTaskDiagnostics(aId)) {
            System.out.println(diag);
        }
        // ...but not for second (shouldn't exist:  uber-AM overrode max attempts)
        boolean secondTaskAttemptExists = true;
        try {
            aId = new TaskAttemptID(taskID, 1);
            System.out.println((("Diagnostics for " + aId) + " :"));
            for (String diag : job.getTaskDiagnostics(aId)) {
                System.out.println(diag);
            }
        } catch (Exception e) {
            secondTaskAttemptExists = false;
        }
        Assert.assertEquals(false, secondTaskAttemptExists);
        TaskCompletionEvent[] events = job.getTaskCompletionEvents(0, 2);
        Assert.assertEquals(1, events.length);
        // TIPFAILED if it comes from the AM, FAILED if it comes from the JHS
        TaskCompletionEvent.Status status = events[0].getStatus();
        Assert.assertTrue(((status == (Status.FAILED)) || (status == (Status.TIPFAILED))));
        Assert.assertEquals(FAILED, job.getJobState());
        // Disabling till UberAM honors MRJobConfig.MAP_MAX_ATTEMPTS
        // verifyFailingMapperCounters(job);
        // TODO later:  add explicit "isUber()" checks of some sort
    }
}

