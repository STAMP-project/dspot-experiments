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
package org.apache.hadoop.mapreduce.v2.app;


import EventType.MAP_ATTEMPT_FAILED;
import EventType.MAP_ATTEMPT_FINISHED;
import EventType.MAP_ATTEMPT_KILLED;
import EventType.MAP_ATTEMPT_STARTED;
import EventType.TASK_FAILED;
import EventType.TASK_FINISHED;
import EventType.TASK_STARTED;
import FileOutputFormat.OUTDIR;
import JobState.RUNNING;
import MRJobConfig.JOB_UBERTASK_ENABLE;
import MRJobConfig.MR_AM_JOB_RECOVERY_ENABLE;
import MRJobConfig.MR_ENCRYPTED_INTERMEDIATE_DATA;
import MRJobConfig.NUM_REDUCES;
import TaskAttemptState.FAILED;
import TaskAttemptState.KILLED;
import TaskAttemptState.NEW;
import TaskState.SUCCEEDED;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileOutputCommitter;
import org.apache.hadoop.mapred.JobContext;
import org.apache.hadoop.mapred.org.apache.hadoop.mapred.JobContext;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskID;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.jobhistory.Event;
import org.apache.hadoop.mapreduce.jobhistory.EventType;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryEvent;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryEventHandler;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryParser.TaskAttemptInfo;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryParser.TaskInfo;
import org.apache.hadoop.mapreduce.v2.api.records.AMInfo;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptState;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.job.Task;
import org.apache.hadoop.mapreduce.v2.app.job.TaskAttempt;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEventType;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskEventType;
import org.apache.hadoop.mapreduce.v2.app.job.impl.MapTaskImpl;
import org.apache.hadoop.mapreduce.v2.app.launcher.ContainerLauncher;
import org.apache.hadoop.mapreduce.v2.app.launcher.ContainerLauncherEvent;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.event.EventHandler;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class TestRecovery {
    private static final Logger LOG = LoggerFactory.getLogger(TestRecovery.class);

    private static Path outputDir = new Path((((new File("target", TestRecovery.class.getName()).getAbsolutePath()) + (Path.SEPARATOR)) + "out"));

    private static String partFile = "part-r-00000";

    private Text key1 = new Text("key1");

    private Text key2 = new Text("key2");

    private Text val1 = new Text("val1");

    private Text val2 = new Text("val2");

    /**
     * AM with 2 maps and 1 reduce. For 1st map, one attempt fails, one attempt
     * completely disappears because of failed launch, one attempt gets killed and
     * one attempt succeeds. AM crashes after the first tasks finishes and
     * recovers completely and succeeds in the second generation.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCrashed() throws Exception {
        int runCount = 0;
        long am1StartTimeEst = System.currentTimeMillis();
        MRApp app = new TestRecovery.MRAppWithHistory(2, 1, false, this.getClass().getName(), true, (++runCount));
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.setBoolean("mapred.reducer.new-api", true);
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        Job job = app.submit(conf);
        app.waitForState(job, RUNNING);
        long jobStartTime = job.getReport().getStartTime();
        // all maps would be running
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        Iterator<Task> it = job.getTasks().values().iterator();
        Task mapTask1 = it.next();
        Task mapTask2 = it.next();
        Task reduceTask = it.next();
        // all maps must be running
        app.waitForState(mapTask1, TaskState.RUNNING);
        app.waitForState(mapTask2, TaskState.RUNNING);
        TaskAttempt task1Attempt1 = mapTask1.getAttempts().values().iterator().next();
        TaskAttempt task2Attempt = mapTask2.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task1Attempt1, TaskAttemptState.RUNNING);
        app.waitForState(task2Attempt, TaskAttemptState.RUNNING);
        app.waitForState(reduceTask, TaskState.RUNNING);
        // ///////// Play some games with the TaskAttempts of the first task //////
        // send the fail signal to the 1st map task attempt
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptFailEvent(task1Attempt1.getID()));
        app.waitForState(task1Attempt1, FAILED);
        int timeOut = 0;
        while (((mapTask1.getAttempts().size()) != 2) && ((timeOut++) < 10)) {
            Thread.sleep(2000);
            TestRecovery.LOG.info("Waiting for next attempt to start");
        } 
        Assert.assertEquals(2, mapTask1.getAttempts().size());
        Iterator<TaskAttempt> itr = mapTask1.getAttempts().values().iterator();
        itr.next();
        TaskAttempt task1Attempt2 = itr.next();
        // wait for the second task attempt to be assigned.
        TestRecovery.waitForContainerAssignment(task1Attempt2);
        // This attempt will automatically fail because of the way ContainerLauncher
        // is setup
        // This attempt 'disappears' from JobHistory and so causes MAPREDUCE-3846
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(task1Attempt2.getID(), TaskAttemptEventType.TA_CONTAINER_LAUNCH_FAILED));
        app.waitForState(task1Attempt2, FAILED);
        timeOut = 0;
        while (((mapTask1.getAttempts().size()) != 3) && ((timeOut++) < 10)) {
            Thread.sleep(2000);
            TestRecovery.LOG.info("Waiting for next attempt to start");
        } 
        Assert.assertEquals(3, mapTask1.getAttempts().size());
        itr = mapTask1.getAttempts().values().iterator();
        itr.next();
        itr.next();
        TaskAttempt task1Attempt3 = itr.next();
        app.waitForState(task1Attempt3, TaskAttemptState.RUNNING);
        // send the kill signal to the 1st map 3rd attempt
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(task1Attempt3.getID(), TaskAttemptEventType.TA_KILL));
        app.waitForState(task1Attempt3, KILLED);
        timeOut = 0;
        while (((mapTask1.getAttempts().size()) != 4) && ((timeOut++) < 10)) {
            Thread.sleep(2000);
            TestRecovery.LOG.info("Waiting for next attempt to start");
        } 
        Assert.assertEquals(4, mapTask1.getAttempts().size());
        itr = mapTask1.getAttempts().values().iterator();
        itr.next();
        itr.next();
        itr.next();
        TaskAttempt task1Attempt4 = itr.next();
        app.waitForState(task1Attempt4, TaskAttemptState.RUNNING);
        // send the done signal to the 1st map 4th attempt
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(task1Attempt4.getID(), TaskAttemptEventType.TA_DONE));
        // ///////// End of games with the TaskAttempts of the first task //////
        // wait for first map task to complete
        app.waitForState(mapTask1, SUCCEEDED);
        long task1StartTime = mapTask1.getReport().getStartTime();
        long task1FinishTime = mapTask1.getReport().getFinishTime();
        // stop the app
        stop();
        // rerun
        // in rerun the 1st map will be recovered from previous run
        long am2StartTimeEst = System.currentTimeMillis();
        app = new TestRecovery.MRAppWithHistory(2, 1, false, this.getClass().getName(), false, (++runCount));
        conf = new Configuration();
        conf.setBoolean(MR_AM_JOB_RECOVERY_ENABLE, true);
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.setBoolean("mapred.reducer.new-api", true);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        job = app.submit(conf);
        app.waitForState(job, RUNNING);
        // all maps would be running
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        it = job.getTasks().values().iterator();
        mapTask1 = it.next();
        mapTask2 = it.next();
        reduceTask = it.next();
        // first map will be recovered, no need to send done
        app.waitForState(mapTask1, SUCCEEDED);
        app.waitForState(mapTask2, TaskState.RUNNING);
        task2Attempt = mapTask2.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task2Attempt, TaskAttemptState.RUNNING);
        // send the done signal to the 2nd map task
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(mapTask2.getAttempts().values().iterator().next().getID(), TaskAttemptEventType.TA_DONE));
        // wait to get it completed
        app.waitForState(mapTask2, SUCCEEDED);
        // wait for reduce to be running before sending done
        app.waitForState(reduceTask, TaskState.RUNNING);
        // send the done signal to the reduce
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(reduceTask.getAttempts().values().iterator().next().getID(), TaskAttemptEventType.TA_DONE));
        app.waitForState(job, JobState.SUCCEEDED);
        app.verifyCompleted();
        Assert.assertEquals("Job Start time not correct", jobStartTime, job.getReport().getStartTime());
        Assert.assertEquals("Task Start time not correct", task1StartTime, mapTask1.getReport().getStartTime());
        Assert.assertEquals("Task Finish time not correct", task1FinishTime, mapTask1.getReport().getFinishTime());
        Assert.assertEquals(2, job.getAMInfos().size());
        int attemptNum = 1;
        // Verify AMInfo
        for (AMInfo amInfo : job.getAMInfos()) {
            Assert.assertEquals((attemptNum++), amInfo.getAppAttemptId().getAttemptId());
            Assert.assertEquals(amInfo.getAppAttemptId(), amInfo.getContainerId().getApplicationAttemptId());
            Assert.assertEquals(MRApp.NM_HOST, amInfo.getNodeManagerHost());
            Assert.assertEquals(MRApp.NM_PORT, amInfo.getNodeManagerPort());
            Assert.assertEquals(MRApp.NM_HTTP_PORT, amInfo.getNodeManagerHttpPort());
        }
        long am1StartTimeReal = job.getAMInfos().get(0).getStartTime();
        long am2StartTimeReal = job.getAMInfos().get(1).getStartTime();
        Assert.assertTrue(((am1StartTimeReal >= am1StartTimeEst) && (am1StartTimeReal <= am2StartTimeEst)));
        Assert.assertTrue(((am2StartTimeReal >= am2StartTimeEst) && (am2StartTimeReal <= (System.currentTimeMillis()))));
        // TODO Add verification of additional data from jobHistory - whatever was
        // available in the failed attempt should be available here
    }

    /**
     * AM with 3 maps and 0 reduce. AM crashes after the first two tasks finishes
     * and recovers completely and succeeds in the second generation.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCrashOfMapsOnlyJob() throws Exception {
        int runCount = 0;
        MRApp app = new TestRecovery.MRAppWithHistory(3, 0, false, this.getClass().getName(), true, (++runCount));
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        Job job = app.submit(conf);
        app.waitForState(job, RUNNING);
        // all maps would be running
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        Iterator<Task> it = job.getTasks().values().iterator();
        Task mapTask1 = it.next();
        Task mapTask2 = it.next();
        Task mapTask3 = it.next();
        // all maps must be running
        app.waitForState(mapTask1, TaskState.RUNNING);
        app.waitForState(mapTask2, TaskState.RUNNING);
        app.waitForState(mapTask3, TaskState.RUNNING);
        TaskAttempt task1Attempt = mapTask1.getAttempts().values().iterator().next();
        TaskAttempt task2Attempt = mapTask2.getAttempts().values().iterator().next();
        TaskAttempt task3Attempt = mapTask3.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task1Attempt, TaskAttemptState.RUNNING);
        app.waitForState(task2Attempt, TaskAttemptState.RUNNING);
        app.waitForState(task3Attempt, TaskAttemptState.RUNNING);
        // send the done signal to the 1st two maps
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(task1Attempt.getID(), TaskAttemptEventType.TA_DONE));
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(task2Attempt.getID(), TaskAttemptEventType.TA_DONE));
        // wait for first two map task to complete
        app.waitForState(mapTask1, SUCCEEDED);
        app.waitForState(mapTask2, SUCCEEDED);
        // stop the app
        stop();
        // rerun
        // in rerun the 1st two map will be recovered from previous run
        app = new TestRecovery.MRAppWithHistory(2, 1, false, this.getClass().getName(), false, (++runCount));
        conf = new Configuration();
        conf.setBoolean(MR_AM_JOB_RECOVERY_ENABLE, true);
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        // Set num-reduces explicitly in conf as recovery logic depends on it.
        conf.setInt(NUM_REDUCES, 0);
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        job = app.submit(conf);
        app.waitForState(job, RUNNING);
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        it = job.getTasks().values().iterator();
        mapTask1 = it.next();
        mapTask2 = it.next();
        mapTask3 = it.next();
        // first two maps will be recovered, no need to send done
        app.waitForState(mapTask1, SUCCEEDED);
        app.waitForState(mapTask2, SUCCEEDED);
        app.waitForState(mapTask3, TaskState.RUNNING);
        task3Attempt = mapTask3.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task3Attempt, TaskAttemptState.RUNNING);
        // send the done signal to the 3rd map task
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(mapTask3.getAttempts().values().iterator().next().getID(), TaskAttemptEventType.TA_DONE));
        // wait to get it completed
        app.waitForState(mapTask3, SUCCEEDED);
        app.waitForState(job, JobState.SUCCEEDED);
        app.verifyCompleted();
    }

    /**
     * The class provides a custom implementation of output committer setupTask
     * and isRecoverySupported methods, which determines if recovery supported
     * based on config property.
     */
    public static class TestFileOutputCommitter extends org.apache.hadoop.mapred.FileOutputCommitter {
        private boolean abortJobCalled;

        @Override
        public boolean isRecoverySupported(org.apache.hadoop.mapred.JobContext jobContext) {
            boolean isRecoverySupported = false;
            if ((jobContext != null) && ((jobContext.getConfiguration()) != null)) {
                isRecoverySupported = jobContext.getConfiguration().getBoolean("want.am.recovery", false);
            }
            return isRecoverySupported;
        }

        @Override
        public void abortJob(JobContext context, int runState) throws IOException {
            super.abortJob(context, runState);
            this.abortJobCalled = true;
        }

        private boolean isAbortJobCalled() {
            return this.abortJobCalled;
        }
    }

    /**
     * This test case primarily verifies if the recovery is controlled through config
     * property. In this case, recover is turned ON. AM with 3 maps and 0 reduce.
     * AM crashes after the first two tasks finishes and recovers completely and
     * succeeds in the second generation.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRecoverySuccessUsingCustomOutputCommitter() throws Exception {
        int runCount = 0;
        MRApp app = new TestRecovery.MRAppWithHistory(3, 0, false, this.getClass().getName(), true, (++runCount));
        Configuration conf = new Configuration();
        conf.setClass("mapred.output.committer.class", TestRecovery.TestFileOutputCommitter.class, OutputCommitter.class);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        conf.setBoolean("want.am.recovery", true);
        Job job = app.submit(conf);
        app.waitForState(job, RUNNING);
        // all maps would be running
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        Iterator<Task> it = job.getTasks().values().iterator();
        Task mapTask1 = it.next();
        Task mapTask2 = it.next();
        Task mapTask3 = it.next();
        // all maps must be running
        app.waitForState(mapTask1, TaskState.RUNNING);
        app.waitForState(mapTask2, TaskState.RUNNING);
        app.waitForState(mapTask3, TaskState.RUNNING);
        TaskAttempt task1Attempt = mapTask1.getAttempts().values().iterator().next();
        TaskAttempt task2Attempt = mapTask2.getAttempts().values().iterator().next();
        TaskAttempt task3Attempt = mapTask3.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task1Attempt, TaskAttemptState.RUNNING);
        app.waitForState(task2Attempt, TaskAttemptState.RUNNING);
        app.waitForState(task3Attempt, TaskAttemptState.RUNNING);
        // send the done signal to the 1st two maps
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(task1Attempt.getID(), TaskAttemptEventType.TA_DONE));
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(task2Attempt.getID(), TaskAttemptEventType.TA_DONE));
        // wait for first two map task to complete
        app.waitForState(mapTask1, SUCCEEDED);
        app.waitForState(mapTask2, SUCCEEDED);
        // stop the app
        stop();
        // rerun
        // in rerun the 1st two map will be recovered from previous run
        app = new TestRecovery.MRAppWithHistory(2, 1, false, this.getClass().getName(), false, (++runCount));
        conf = new Configuration();
        conf.setClass("mapred.output.committer.class", TestRecovery.TestFileOutputCommitter.class, OutputCommitter.class);
        conf.setBoolean("want.am.recovery", true);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        // Set num-reduces explicitly in conf as recovery logic depends on it.
        conf.setInt(NUM_REDUCES, 0);
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        job = app.submit(conf);
        app.waitForState(job, RUNNING);
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        it = job.getTasks().values().iterator();
        mapTask1 = it.next();
        mapTask2 = it.next();
        mapTask3 = it.next();
        // first two maps will be recovered, no need to send done
        app.waitForState(mapTask1, SUCCEEDED);
        app.waitForState(mapTask2, SUCCEEDED);
        app.waitForState(mapTask3, TaskState.RUNNING);
        task3Attempt = mapTask3.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task3Attempt, TaskAttemptState.RUNNING);
        // send the done signal to the 3rd map task
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(mapTask3.getAttempts().values().iterator().next().getID(), TaskAttemptEventType.TA_DONE));
        // wait to get it completed
        app.waitForState(mapTask3, SUCCEEDED);
        app.waitForState(job, JobState.SUCCEEDED);
        app.verifyCompleted();
    }

    @Test
    public void testRecoveryWithSpillEncryption() throws Exception {
        int runCount = 0;
        MRApp app = new TestRecovery.MRAppWithHistory(1, 1, false, this.getClass().getName(), true, (++runCount)) {};
        Configuration conf = new Configuration();
        conf.setBoolean(MR_AM_JOB_RECOVERY_ENABLE, true);
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.setBoolean("mapred.reducer.new-api", true);
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        conf.setBoolean(MR_ENCRYPTED_INTERMEDIATE_DATA, true);
        // run the MR job at the first attempt
        Job jobAttempt1 = app.submit(conf);
        app.waitForState(jobAttempt1, RUNNING);
        Iterator<Task> tasks = jobAttempt1.getTasks().values().iterator();
        // finish the map task but the reduce task
        Task mapper = tasks.next();
        app.waitForState(mapper, TaskState.RUNNING);
        TaskAttempt mapAttempt = mapper.getAttempts().values().iterator().next();
        app.waitForState(mapAttempt, TaskAttemptState.RUNNING);
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(mapAttempt.getID(), TaskAttemptEventType.TA_DONE));
        app.waitForState(mapper, SUCCEEDED);
        // crash the first attempt of the MR job
        stop();
        // run the MR job again at the second attempt
        app = new TestRecovery.MRAppWithHistory(1, 1, false, this.getClass().getName(), false, (++runCount));
        Job jobAttempt2 = app.submit(conf);
        Assert.assertTrue(("Recovery from previous job attempt is processed even " + "though intermediate data encryption is enabled."), (!(recovered())));
        // The map task succeeded from previous job attempt will not be recovered
        // because the data spill encryption is enabled.
        // Let's finish the job at the second attempt and verify its completion.
        app.waitForState(jobAttempt2, RUNNING);
        tasks = jobAttempt2.getTasks().values().iterator();
        mapper = tasks.next();
        Task reducer = tasks.next();
        // finish the map task first
        app.waitForState(mapper, TaskState.RUNNING);
        mapAttempt = mapper.getAttempts().values().iterator().next();
        app.waitForState(mapAttempt, TaskAttemptState.RUNNING);
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(mapAttempt.getID(), TaskAttemptEventType.TA_DONE));
        app.waitForState(mapper, SUCCEEDED);
        // then finish the reduce task
        TaskAttempt redAttempt = reducer.getAttempts().values().iterator().next();
        app.waitForState(redAttempt, TaskAttemptState.RUNNING);
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(redAttempt.getID(), TaskAttemptEventType.TA_DONE));
        app.waitForState(reducer, SUCCEEDED);
        // verify that the job succeeds at the 2rd attempt
        app.waitForState(jobAttempt2, JobState.SUCCEEDED);
    }

    /**
     * This test case primarily verifies if the recovery is controlled through config
     * property. In this case, recover is turned OFF. AM with 3 maps and 0 reduce.
     * AM crashes after the first two tasks finishes and recovery fails and have
     * to rerun fully in the second generation and succeeds.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRecoveryFailsUsingCustomOutputCommitter() throws Exception {
        int runCount = 0;
        MRApp app = new TestRecovery.MRAppWithHistory(3, 0, false, this.getClass().getName(), true, (++runCount));
        Configuration conf = new Configuration();
        conf.setClass("mapred.output.committer.class", TestRecovery.TestFileOutputCommitter.class, OutputCommitter.class);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        conf.setBoolean("want.am.recovery", false);
        Job job = app.submit(conf);
        app.waitForState(job, RUNNING);
        // all maps would be running
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        Iterator<Task> it = job.getTasks().values().iterator();
        Task mapTask1 = it.next();
        Task mapTask2 = it.next();
        Task mapTask3 = it.next();
        // all maps must be running
        app.waitForState(mapTask1, TaskState.RUNNING);
        app.waitForState(mapTask2, TaskState.RUNNING);
        app.waitForState(mapTask3, TaskState.RUNNING);
        TaskAttempt task1Attempt = mapTask1.getAttempts().values().iterator().next();
        TaskAttempt task2Attempt = mapTask2.getAttempts().values().iterator().next();
        TaskAttempt task3Attempt = mapTask3.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task1Attempt, TaskAttemptState.RUNNING);
        app.waitForState(task2Attempt, TaskAttemptState.RUNNING);
        app.waitForState(task3Attempt, TaskAttemptState.RUNNING);
        // send the done signal to the 1st two maps
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(task1Attempt.getID(), TaskAttemptEventType.TA_DONE));
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(task2Attempt.getID(), TaskAttemptEventType.TA_DONE));
        // wait for first two map task to complete
        app.waitForState(mapTask1, SUCCEEDED);
        app.waitForState(mapTask2, SUCCEEDED);
        // stop the app
        stop();
        // rerun
        // in rerun the 1st two map will be recovered from previous run
        app = new TestRecovery.MRAppWithHistory(2, 1, false, this.getClass().getName(), false, (++runCount));
        conf = new Configuration();
        conf.setClass("mapred.output.committer.class", TestRecovery.TestFileOutputCommitter.class, OutputCommitter.class);
        conf.setBoolean("want.am.recovery", false);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        // Set num-reduces explicitly in conf as recovery logic depends on it.
        conf.setInt(NUM_REDUCES, 0);
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        job = app.submit(conf);
        app.waitForState(job, RUNNING);
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        it = job.getTasks().values().iterator();
        mapTask1 = it.next();
        mapTask2 = it.next();
        mapTask3 = it.next();
        // first two maps will NOT  be recovered, need to send done from them
        app.waitForState(mapTask1, TaskState.RUNNING);
        app.waitForState(mapTask2, TaskState.RUNNING);
        app.waitForState(mapTask3, TaskState.RUNNING);
        task3Attempt = mapTask3.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task3Attempt, TaskAttemptState.RUNNING);
        // send the done signal to all 3 tasks map task
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(mapTask1.getAttempts().values().iterator().next().getID(), TaskAttemptEventType.TA_DONE));
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(mapTask2.getAttempts().values().iterator().next().getID(), TaskAttemptEventType.TA_DONE));
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(mapTask3.getAttempts().values().iterator().next().getID(), TaskAttemptEventType.TA_DONE));
        // wait to get it completed
        app.waitForState(mapTask3, SUCCEEDED);
        app.waitForState(job, JobState.SUCCEEDED);
        app.verifyCompleted();
    }

    @Test
    public void testMultipleCrashes() throws Exception {
        int runCount = 0;
        MRApp app = new TestRecovery.MRAppWithHistory(2, 1, false, this.getClass().getName(), true, (++runCount));
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.setBoolean("mapred.reducer.new-api", true);
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        Job job = app.submit(conf);
        app.waitForState(job, RUNNING);
        // all maps would be running
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        Iterator<Task> it = job.getTasks().values().iterator();
        Task mapTask1 = it.next();
        Task mapTask2 = it.next();
        Task reduceTask = it.next();
        // all maps must be running
        app.waitForState(mapTask1, TaskState.RUNNING);
        app.waitForState(mapTask2, TaskState.RUNNING);
        TaskAttempt task1Attempt1 = mapTask1.getAttempts().values().iterator().next();
        TaskAttempt task2Attempt = mapTask2.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task1Attempt1, TaskAttemptState.RUNNING);
        app.waitForState(task2Attempt, TaskAttemptState.RUNNING);
        // reduces must be in NEW state
        Assert.assertEquals("Reduce Task state not correct", TaskState.RUNNING, reduceTask.getReport().getTaskState());
        // send the done signal to the 1st map
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(task1Attempt1.getID(), TaskAttemptEventType.TA_DONE));
        // wait for first map task to complete
        app.waitForState(mapTask1, SUCCEEDED);
        // Crash the app
        stop();
        // rerun
        // in rerun the 1st map will be recovered from previous run
        app = new TestRecovery.MRAppWithHistory(2, 1, false, this.getClass().getName(), false, (++runCount));
        conf = new Configuration();
        conf.setBoolean(MR_AM_JOB_RECOVERY_ENABLE, true);
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.setBoolean("mapred.reducer.new-api", true);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        job = app.submit(conf);
        app.waitForState(job, RUNNING);
        // all maps would be running
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        it = job.getTasks().values().iterator();
        mapTask1 = it.next();
        mapTask2 = it.next();
        reduceTask = it.next();
        // first map will be recovered, no need to send done
        app.waitForState(mapTask1, SUCCEEDED);
        app.waitForState(mapTask2, TaskState.RUNNING);
        task2Attempt = mapTask2.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task2Attempt, TaskAttemptState.RUNNING);
        // send the done signal to the 2nd map task
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(mapTask2.getAttempts().values().iterator().next().getID(), TaskAttemptEventType.TA_DONE));
        // wait to get it completed
        app.waitForState(mapTask2, SUCCEEDED);
        // Crash the app again.
        stop();
        // rerun
        // in rerun the 1st and 2nd map will be recovered from previous run
        app = new TestRecovery.MRAppWithHistory(2, 1, false, this.getClass().getName(), false, (++runCount));
        conf = new Configuration();
        conf.setBoolean(MR_AM_JOB_RECOVERY_ENABLE, true);
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.setBoolean("mapred.reducer.new-api", true);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        job = app.submit(conf);
        app.waitForState(job, RUNNING);
        // all maps would be running
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        it = job.getTasks().values().iterator();
        mapTask1 = it.next();
        mapTask2 = it.next();
        reduceTask = it.next();
        // The maps will be recovered, no need to send done
        app.waitForState(mapTask1, SUCCEEDED);
        app.waitForState(mapTask2, SUCCEEDED);
        // wait for reduce to be running before sending done
        app.waitForState(reduceTask, TaskState.RUNNING);
        // send the done signal to the reduce
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(reduceTask.getAttempts().values().iterator().next().getID(), TaskAttemptEventType.TA_DONE));
        app.waitForState(job, JobState.SUCCEEDED);
        app.verifyCompleted();
    }

    @Test
    public void testOutputRecovery() throws Exception {
        int runCount = 0;
        MRApp app = new TestRecovery.MRAppWithHistory(1, 2, false, this.getClass().getName(), true, (++runCount));
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.setBoolean("mapred.reducer.new-api", true);
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        Job job = app.submit(conf);
        app.waitForState(job, RUNNING);
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        Iterator<Task> it = job.getTasks().values().iterator();
        Task mapTask1 = it.next();
        Task reduceTask1 = it.next();
        // all maps must be running
        app.waitForState(mapTask1, TaskState.RUNNING);
        TaskAttempt task1Attempt1 = mapTask1.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task1Attempt1, TaskAttemptState.RUNNING);
        // send the done signal to the map
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(task1Attempt1.getID(), TaskAttemptEventType.TA_DONE));
        // wait for map task to complete
        app.waitForState(mapTask1, SUCCEEDED);
        // Verify the shuffle-port
        Assert.assertEquals(5467, task1Attempt1.getShufflePort());
        app.waitForState(reduceTask1, TaskState.RUNNING);
        TaskAttempt reduce1Attempt1 = reduceTask1.getAttempts().values().iterator().next();
        // write output corresponding to reduce1
        writeOutput(reduce1Attempt1, conf);
        // send the done signal to the 1st reduce
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(reduce1Attempt1.getID(), TaskAttemptEventType.TA_DONE));
        // wait for first reduce task to complete
        app.waitForState(reduceTask1, SUCCEEDED);
        // stop the app before the job completes.
        stop();
        // rerun
        // in rerun the map will be recovered from previous run
        app = new TestRecovery.MRAppWithHistory(1, 2, false, this.getClass().getName(), false, (++runCount));
        conf = new Configuration();
        conf.setBoolean(MR_AM_JOB_RECOVERY_ENABLE, true);
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.setBoolean("mapred.reducer.new-api", true);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        job = app.submit(conf);
        app.waitForState(job, RUNNING);
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        it = job.getTasks().values().iterator();
        mapTask1 = it.next();
        reduceTask1 = it.next();
        Task reduceTask2 = it.next();
        // map will be recovered, no need to send done
        app.waitForState(mapTask1, SUCCEEDED);
        // Verify the shuffle-port after recovery
        task1Attempt1 = mapTask1.getAttempts().values().iterator().next();
        Assert.assertEquals(5467, task1Attempt1.getShufflePort());
        // first reduce will be recovered, no need to send done
        app.waitForState(reduceTask1, SUCCEEDED);
        app.waitForState(reduceTask2, TaskState.RUNNING);
        TaskAttempt reduce2Attempt = reduceTask2.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(reduce2Attempt, TaskAttemptState.RUNNING);
        // send the done signal to the 2nd reduce task
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(reduce2Attempt.getID(), TaskAttemptEventType.TA_DONE));
        // wait to get it completed
        app.waitForState(reduceTask2, SUCCEEDED);
        app.waitForState(job, JobState.SUCCEEDED);
        app.verifyCompleted();
        validateOutput();
    }

    @Test
    public void testPreviousJobOutputCleanedWhenNoRecovery() throws Exception {
        int runCount = 0;
        MRApp app = new TestRecovery.MRAppWithHistory(1, 2, false, this.getClass().getName(), true, (++runCount));
        Configuration conf = new Configuration();
        conf.setBoolean(MR_AM_JOB_RECOVERY_ENABLE, false);
        conf.setClass("mapred.output.committer.class", TestRecovery.TestFileOutputCommitter.class, OutputCommitter.class);
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        Job job = app.submit(conf);
        app.waitForState(job, RUNNING);
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        // stop the app before the job completes.
        stop();
        close();
        // rerun
        app = new TestRecovery.MRAppWithHistory(1, 2, false, this.getClass().getName(), false, (++runCount));
        job = app.submit(conf);
        app.waitForState(job, RUNNING);
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        TestRecovery.TestFileOutputCommitter committer = ((TestRecovery.TestFileOutputCommitter) (getCommitter()));
        Assert.assertTrue("commiter.abortJob() has not been called", committer.isAbortJobCalled());
        close();
    }

    @Test
    public void testPreviousJobIsNotCleanedWhenRecovery() throws Exception {
        int runCount = 0;
        MRApp app = new TestRecovery.MRAppWithHistory(1, 2, false, this.getClass().getName(), true, (++runCount));
        Configuration conf = new Configuration();
        conf.setBoolean(MR_AM_JOB_RECOVERY_ENABLE, true);
        conf.setClass("mapred.output.committer.class", TestRecovery.TestFileOutputCommitter.class, OutputCommitter.class);
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        // TestFileOutputCommitter supports recovery if want.am.recovery=true
        conf.setBoolean("want.am.recovery", true);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        Job job = app.submit(conf);
        app.waitForState(job, RUNNING);
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        // stop the app before the job completes.
        stop();
        close();
        // rerun
        app = new TestRecovery.MRAppWithHistory(1, 2, false, this.getClass().getName(), false, (++runCount));
        job = app.submit(conf);
        app.waitForState(job, RUNNING);
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        TestRecovery.TestFileOutputCommitter committer = ((TestRecovery.TestFileOutputCommitter) (getCommitter()));
        Assert.assertFalse("commiter.abortJob() has been called", committer.isAbortJobCalled());
        close();
    }

    @Test
    public void testOutputRecoveryMapsOnly() throws Exception {
        int runCount = 0;
        MRApp app = new TestRecovery.MRAppWithHistory(2, 1, false, this.getClass().getName(), true, (++runCount));
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.setBoolean("mapred.reducer.new-api", true);
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        Job job = app.submit(conf);
        app.waitForState(job, RUNNING);
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        Iterator<Task> it = job.getTasks().values().iterator();
        Task mapTask1 = it.next();
        Task mapTask2 = it.next();
        Task reduceTask1 = it.next();
        // all maps must be running
        app.waitForState(mapTask1, TaskState.RUNNING);
        TaskAttempt task1Attempt1 = mapTask1.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task1Attempt1, TaskAttemptState.RUNNING);
        // write output corresponding to map1 (This is just to validate that it is
        // no included in the output)
        writeBadOutput(task1Attempt1, conf);
        // send the done signal to the map
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(task1Attempt1.getID(), TaskAttemptEventType.TA_DONE));
        // wait for map task to complete
        app.waitForState(mapTask1, SUCCEEDED);
        // Verify the shuffle-port
        Assert.assertEquals(5467, task1Attempt1.getShufflePort());
        // stop the app before the job completes.
        stop();
        // rerun
        // in rerun the map will be recovered from previous run
        app = new TestRecovery.MRAppWithHistory(2, 1, false, this.getClass().getName(), false, (++runCount));
        conf = new Configuration();
        conf.setBoolean(MR_AM_JOB_RECOVERY_ENABLE, true);
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.setBoolean("mapred.reducer.new-api", true);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        job = app.submit(conf);
        app.waitForState(job, RUNNING);
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        it = job.getTasks().values().iterator();
        mapTask1 = it.next();
        mapTask2 = it.next();
        reduceTask1 = it.next();
        // map will be recovered, no need to send done
        app.waitForState(mapTask1, SUCCEEDED);
        // Verify the shuffle-port after recovery
        task1Attempt1 = mapTask1.getAttempts().values().iterator().next();
        Assert.assertEquals(5467, task1Attempt1.getShufflePort());
        app.waitForState(mapTask2, TaskState.RUNNING);
        TaskAttempt task2Attempt1 = mapTask2.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task2Attempt1, TaskAttemptState.RUNNING);
        // send the done signal to the map
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(task2Attempt1.getID(), TaskAttemptEventType.TA_DONE));
        // wait for map task to complete
        app.waitForState(mapTask2, SUCCEEDED);
        // Verify the shuffle-port
        Assert.assertEquals(5467, task2Attempt1.getShufflePort());
        app.waitForState(reduceTask1, TaskState.RUNNING);
        TaskAttempt reduce1Attempt1 = reduceTask1.getAttempts().values().iterator().next();
        // write output corresponding to reduce1
        writeOutput(reduce1Attempt1, conf);
        // send the done signal to the 1st reduce
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(reduce1Attempt1.getID(), TaskAttemptEventType.TA_DONE));
        // wait for first reduce task to complete
        app.waitForState(reduceTask1, SUCCEEDED);
        app.waitForState(job, JobState.SUCCEEDED);
        app.verifyCompleted();
        validateOutput();
    }

    @Test
    public void testRecoveryWithOldCommiter() throws Exception {
        int runCount = 0;
        MRApp app = new TestRecovery.MRAppWithHistory(1, 2, false, this.getClass().getName(), true, (++runCount));
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.mapper.new-api", false);
        conf.setBoolean("mapred.reducer.new-api", false);
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        Job job = app.submit(conf);
        app.waitForState(job, RUNNING);
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        Iterator<Task> it = job.getTasks().values().iterator();
        Task mapTask1 = it.next();
        Task reduceTask1 = it.next();
        // all maps must be running
        app.waitForState(mapTask1, TaskState.RUNNING);
        TaskAttempt task1Attempt1 = mapTask1.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task1Attempt1, TaskAttemptState.RUNNING);
        // send the done signal to the map
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(task1Attempt1.getID(), TaskAttemptEventType.TA_DONE));
        // wait for map task to complete
        app.waitForState(mapTask1, SUCCEEDED);
        // Verify the shuffle-port
        Assert.assertEquals(5467, task1Attempt1.getShufflePort());
        app.waitForState(reduceTask1, TaskState.RUNNING);
        TaskAttempt reduce1Attempt1 = reduceTask1.getAttempts().values().iterator().next();
        // write output corresponding to reduce1
        writeOutput(reduce1Attempt1, conf);
        // send the done signal to the 1st reduce
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(reduce1Attempt1.getID(), TaskAttemptEventType.TA_DONE));
        // wait for first reduce task to complete
        app.waitForState(reduceTask1, SUCCEEDED);
        // stop the app before the job completes.
        stop();
        // rerun
        // in rerun the map will be recovered from previous run
        app = new TestRecovery.MRAppWithHistory(1, 2, false, this.getClass().getName(), false, (++runCount));
        conf = new Configuration();
        conf.setBoolean(MR_AM_JOB_RECOVERY_ENABLE, true);
        conf.setBoolean("mapred.mapper.new-api", false);
        conf.setBoolean("mapred.reducer.new-api", false);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        job = app.submit(conf);
        app.waitForState(job, RUNNING);
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        it = job.getTasks().values().iterator();
        mapTask1 = it.next();
        reduceTask1 = it.next();
        Task reduceTask2 = it.next();
        // map will be recovered, no need to send done
        app.waitForState(mapTask1, SUCCEEDED);
        // Verify the shuffle-port after recovery
        task1Attempt1 = mapTask1.getAttempts().values().iterator().next();
        Assert.assertEquals(5467, task1Attempt1.getShufflePort());
        // first reduce will be recovered, no need to send done
        app.waitForState(reduceTask1, SUCCEEDED);
        app.waitForState(reduceTask2, TaskState.RUNNING);
        TaskAttempt reduce2Attempt = reduceTask2.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(reduce2Attempt, TaskAttemptState.RUNNING);
        // send the done signal to the 2nd reduce task
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(reduce2Attempt.getID(), TaskAttemptEventType.TA_DONE));
        // wait to get it completed
        app.waitForState(reduceTask2, SUCCEEDED);
        app.waitForState(job, JobState.SUCCEEDED);
        app.verifyCompleted();
        validateOutput();
    }

    /**
     * AM with 2 maps and 1 reduce. For 1st map, one attempt fails, one attempt
     * completely disappears because of failed launch, one attempt gets killed and
     * one attempt succeeds. AM crashes after the first tasks finishes and
     * recovers completely and succeeds in the second generation.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSpeculative() throws Exception {
        int runCount = 0;
        long am1StartTimeEst = System.currentTimeMillis();
        MRApp app = new TestRecovery.MRAppWithHistory(2, 1, false, this.getClass().getName(), true, (++runCount));
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.setBoolean("mapred.reducer.new-api", true);
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        Job job = app.submit(conf);
        app.waitForState(job, RUNNING);
        long jobStartTime = job.getReport().getStartTime();
        // all maps would be running
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        Iterator<Task> it = job.getTasks().values().iterator();
        Task mapTask1 = it.next();
        Task mapTask2 = it.next();
        Task reduceTask = it.next();
        // all maps must be running
        app.waitForState(mapTask1, TaskState.RUNNING);
        app.waitForState(mapTask2, TaskState.RUNNING);
        // Launch a Speculative Task for the first Task
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskEvent(mapTask1.getID(), TaskEventType.T_ADD_SPEC_ATTEMPT));
        int timeOut = 0;
        while (((mapTask1.getAttempts().size()) != 2) && ((timeOut++) < 10)) {
            Thread.sleep(1000);
            TestRecovery.LOG.info("Waiting for next attempt to start");
        } 
        Iterator<TaskAttempt> t1it = mapTask1.getAttempts().values().iterator();
        TaskAttempt task1Attempt1 = t1it.next();
        TaskAttempt task1Attempt2 = t1it.next();
        TaskAttempt task2Attempt = mapTask2.getAttempts().values().iterator().next();
        // wait for the second task attempt to be assigned.
        TestRecovery.waitForContainerAssignment(task1Attempt2);
        ContainerId t1a2contId = task1Attempt2.getAssignedContainerID();
        TestRecovery.LOG.info(t1a2contId.toString());
        TestRecovery.LOG.info(task1Attempt1.getID().toString());
        TestRecovery.LOG.info(task1Attempt2.getID().toString());
        // Launch container for speculative attempt
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerLaunchedEvent(task1Attempt2.getID(), runCount));
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task1Attempt1, TaskAttemptState.RUNNING);
        app.waitForState(task1Attempt2, TaskAttemptState.RUNNING);
        app.waitForState(task2Attempt, TaskAttemptState.RUNNING);
        app.waitForState(reduceTask, TaskState.RUNNING);
        // send the done signal to the map 1 attempt 1
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(task1Attempt1.getID(), TaskAttemptEventType.TA_DONE));
        app.waitForState(task1Attempt1, TaskAttemptState.SUCCEEDED);
        // wait for first map task to complete
        app.waitForState(mapTask1, SUCCEEDED);
        long task1StartTime = mapTask1.getReport().getStartTime();
        long task1FinishTime = mapTask1.getReport().getFinishTime();
        // stop the app
        stop();
        // rerun
        // in rerun the 1st map will be recovered from previous run
        long am2StartTimeEst = System.currentTimeMillis();
        app = new TestRecovery.MRAppWithHistory(2, 1, false, this.getClass().getName(), false, (++runCount));
        conf = new Configuration();
        conf.setBoolean(MR_AM_JOB_RECOVERY_ENABLE, true);
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.setBoolean("mapred.reducer.new-api", true);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        job = app.submit(conf);
        app.waitForState(job, RUNNING);
        // all maps would be running
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        it = job.getTasks().values().iterator();
        mapTask1 = it.next();
        mapTask2 = it.next();
        reduceTask = it.next();
        // first map will be recovered, no need to send done
        app.waitForState(mapTask1, SUCCEEDED);
        app.waitForState(mapTask2, TaskState.RUNNING);
        task2Attempt = mapTask2.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task2Attempt, TaskAttemptState.RUNNING);
        // send the done signal to the 2nd map task
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(mapTask2.getAttempts().values().iterator().next().getID(), TaskAttemptEventType.TA_DONE));
        // wait to get it completed
        app.waitForState(mapTask2, SUCCEEDED);
        // wait for reduce to be running before sending done
        app.waitForState(reduceTask, TaskState.RUNNING);
        // send the done signal to the reduce
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(reduceTask.getAttempts().values().iterator().next().getID(), TaskAttemptEventType.TA_DONE));
        app.waitForState(job, JobState.SUCCEEDED);
        app.verifyCompleted();
        Assert.assertEquals("Job Start time not correct", jobStartTime, job.getReport().getStartTime());
        Assert.assertEquals("Task Start time not correct", task1StartTime, mapTask1.getReport().getStartTime());
        Assert.assertEquals("Task Finish time not correct", task1FinishTime, mapTask1.getReport().getFinishTime());
        Assert.assertEquals(2, job.getAMInfos().size());
        int attemptNum = 1;
        // Verify AMInfo
        for (AMInfo amInfo : job.getAMInfos()) {
            Assert.assertEquals((attemptNum++), amInfo.getAppAttemptId().getAttemptId());
            Assert.assertEquals(amInfo.getAppAttemptId(), amInfo.getContainerId().getApplicationAttemptId());
            Assert.assertEquals(MRApp.NM_HOST, amInfo.getNodeManagerHost());
            Assert.assertEquals(MRApp.NM_PORT, amInfo.getNodeManagerPort());
            Assert.assertEquals(MRApp.NM_HTTP_PORT, amInfo.getNodeManagerHttpPort());
        }
        long am1StartTimeReal = job.getAMInfos().get(0).getStartTime();
        long am2StartTimeReal = job.getAMInfos().get(1).getStartTime();
        Assert.assertTrue(((am1StartTimeReal >= am1StartTimeEst) && (am1StartTimeReal <= am2StartTimeEst)));
        Assert.assertTrue(((am2StartTimeReal >= am2StartTimeEst) && (am2StartTimeReal <= (System.currentTimeMillis()))));
    }

    @Test(timeout = 30000)
    public void testRecoveryWithoutShuffleSecret() throws Exception {
        int runCount = 0;
        MRApp app = new TestRecovery.MRAppNoShuffleSecret(2, 1, false, this.getClass().getName(), true, (++runCount));
        Configuration conf = new Configuration();
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.setBoolean("mapred.reducer.new-api", true);
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        Job job = app.submit(conf);
        app.waitForState(job, RUNNING);
        // all maps would be running
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        Iterator<Task> it = job.getTasks().values().iterator();
        Task mapTask1 = it.next();
        Task mapTask2 = it.next();
        Task reduceTask = it.next();
        // all maps must be running
        app.waitForState(mapTask1, TaskState.RUNNING);
        app.waitForState(mapTask2, TaskState.RUNNING);
        TaskAttempt task1Attempt = mapTask1.getAttempts().values().iterator().next();
        TaskAttempt task2Attempt = mapTask2.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task1Attempt, TaskAttemptState.RUNNING);
        app.waitForState(task2Attempt, TaskAttemptState.RUNNING);
        app.waitForState(reduceTask, TaskState.RUNNING);
        // send the done signal to the 1st map attempt
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(task1Attempt.getID(), TaskAttemptEventType.TA_DONE));
        // wait for first map task to complete
        app.waitForState(mapTask1, SUCCEEDED);
        // stop the app
        stop();
        // in recovery the 1st map should NOT be recovered from previous run
        // since the shuffle secret was not provided with the job credentials
        // and had to be rolled per app attempt
        app = new TestRecovery.MRAppNoShuffleSecret(2, 1, false, this.getClass().getName(), false, (++runCount));
        conf = new Configuration();
        conf.setBoolean(MR_AM_JOB_RECOVERY_ENABLE, true);
        conf.setBoolean("mapred.mapper.new-api", true);
        conf.setBoolean("mapred.reducer.new-api", true);
        conf.set(OUTDIR, TestRecovery.outputDir.toString());
        conf.setBoolean(JOB_UBERTASK_ENABLE, false);
        job = app.submit(conf);
        app.waitForState(job, RUNNING);
        // all maps would be running
        Assert.assertEquals("No of tasks not correct", 3, job.getTasks().size());
        it = job.getTasks().values().iterator();
        mapTask1 = it.next();
        mapTask2 = it.next();
        reduceTask = it.next();
        app.waitForState(mapTask1, TaskState.RUNNING);
        app.waitForState(mapTask2, TaskState.RUNNING);
        task2Attempt = mapTask2.getAttempts().values().iterator().next();
        // before sending the TA_DONE, event make sure attempt has come to
        // RUNNING state
        app.waitForState(task2Attempt, TaskAttemptState.RUNNING);
        // send the done signal to the 2nd map task
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(mapTask2.getAttempts().values().iterator().next().getID(), TaskAttemptEventType.TA_DONE));
        // wait to get it completed
        app.waitForState(mapTask2, SUCCEEDED);
        // verify first map task is still running
        app.waitForState(mapTask1, TaskState.RUNNING);
        // send the done signal to the 2nd map task
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(mapTask1.getAttempts().values().iterator().next().getID(), TaskAttemptEventType.TA_DONE));
        // wait to get it completed
        app.waitForState(mapTask1, SUCCEEDED);
        // wait for reduce to be running before sending done
        app.waitForState(reduceTask, TaskState.RUNNING);
        // send the done signal to the reduce
        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(reduceTask.getAttempts().values().iterator().next().getID(), TaskAttemptEventType.TA_DONE));
        app.waitForState(job, JobState.SUCCEEDED);
        app.verifyCompleted();
    }

    @Test
    public void testRecoverySuccessAttempt() {
        TestRecovery.LOG.info("--- START: testRecoverySuccessAttempt ---");
        long clusterTimestamp = System.currentTimeMillis();
        EventHandler mockEventHandler = Mockito.mock(EventHandler.class);
        MapTaskImpl recoverMapTask = getMockMapTask(clusterTimestamp, mockEventHandler);
        TaskId taskId = recoverMapTask.getID();
        JobID jobID = new JobID(Long.toString(clusterTimestamp), 1);
        TaskID taskID = new TaskID(jobID, TaskType.MAP, taskId.getId());
        // Mock up the TaskAttempts
        Map<TaskAttemptID, TaskAttemptInfo> mockTaskAttempts = new HashMap<TaskAttemptID, TaskAttemptInfo>();
        TaskAttemptID taId1 = new TaskAttemptID(taskID, 2);
        TaskAttemptInfo mockTAinfo1 = getMockTaskAttemptInfo(taId1, TaskAttemptState.SUCCEEDED);
        mockTaskAttempts.put(taId1, mockTAinfo1);
        TaskAttemptID taId2 = new TaskAttemptID(taskID, 1);
        TaskAttemptInfo mockTAinfo2 = getMockTaskAttemptInfo(taId2, FAILED);
        mockTaskAttempts.put(taId2, mockTAinfo2);
        OutputCommitter mockCommitter = Mockito.mock(OutputCommitter.class);
        TaskInfo mockTaskInfo = Mockito.mock(TaskInfo.class);
        Mockito.when(mockTaskInfo.getTaskStatus()).thenReturn("SUCCEEDED");
        Mockito.when(mockTaskInfo.getTaskId()).thenReturn(taskID);
        Mockito.when(mockTaskInfo.getAllTaskAttempts()).thenReturn(mockTaskAttempts);
        recoverMapTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskRecoverEvent(taskId, mockTaskInfo, mockCommitter, true));
        ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(mockEventHandler, Mockito.atLeast(1)).handle(((org.apache.hadoop.yarn.event.Event) (arg.capture())));
        Map<TaskAttemptID, TaskAttemptState> finalAttemptStates = new HashMap<TaskAttemptID, TaskAttemptState>();
        finalAttemptStates.put(taId1, TaskAttemptState.SUCCEEDED);
        finalAttemptStates.put(taId2, FAILED);
        List<EventType> jobHistoryEvents = new ArrayList<EventType>();
        jobHistoryEvents.add(TASK_STARTED);
        jobHistoryEvents.add(MAP_ATTEMPT_STARTED);
        jobHistoryEvents.add(MAP_ATTEMPT_FINISHED);
        jobHistoryEvents.add(MAP_ATTEMPT_STARTED);
        jobHistoryEvents.add(MAP_ATTEMPT_FAILED);
        jobHistoryEvents.add(TASK_FINISHED);
        recoveryChecker(recoverMapTask, SUCCEEDED, finalAttemptStates, arg, jobHistoryEvents, 2L, 1L);
    }

    @Test
    public void testRecoveryAllFailAttempts() {
        TestRecovery.LOG.info("--- START: testRecoveryAllFailAttempts ---");
        long clusterTimestamp = System.currentTimeMillis();
        EventHandler mockEventHandler = Mockito.mock(EventHandler.class);
        MapTaskImpl recoverMapTask = getMockMapTask(clusterTimestamp, mockEventHandler);
        TaskId taskId = recoverMapTask.getID();
        JobID jobID = new JobID(Long.toString(clusterTimestamp), 1);
        TaskID taskID = new TaskID(jobID, TaskType.MAP, taskId.getId());
        // Mock up the TaskAttempts
        Map<TaskAttemptID, TaskAttemptInfo> mockTaskAttempts = new HashMap<TaskAttemptID, TaskAttemptInfo>();
        TaskAttemptID taId1 = new TaskAttemptID(taskID, 2);
        TaskAttemptInfo mockTAinfo1 = getMockTaskAttemptInfo(taId1, FAILED);
        mockTaskAttempts.put(taId1, mockTAinfo1);
        TaskAttemptID taId2 = new TaskAttemptID(taskID, 1);
        TaskAttemptInfo mockTAinfo2 = getMockTaskAttemptInfo(taId2, FAILED);
        mockTaskAttempts.put(taId2, mockTAinfo2);
        OutputCommitter mockCommitter = Mockito.mock(OutputCommitter.class);
        TaskInfo mockTaskInfo = Mockito.mock(TaskInfo.class);
        Mockito.when(mockTaskInfo.getTaskStatus()).thenReturn("FAILED");
        Mockito.when(mockTaskInfo.getTaskId()).thenReturn(taskID);
        Mockito.when(mockTaskInfo.getAllTaskAttempts()).thenReturn(mockTaskAttempts);
        recoverMapTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskRecoverEvent(taskId, mockTaskInfo, mockCommitter, true));
        ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(mockEventHandler, Mockito.atLeast(1)).handle(((org.apache.hadoop.yarn.event.Event) (arg.capture())));
        Map<TaskAttemptID, TaskAttemptState> finalAttemptStates = new HashMap<TaskAttemptID, TaskAttemptState>();
        finalAttemptStates.put(taId1, FAILED);
        finalAttemptStates.put(taId2, FAILED);
        List<EventType> jobHistoryEvents = new ArrayList<EventType>();
        jobHistoryEvents.add(TASK_STARTED);
        jobHistoryEvents.add(MAP_ATTEMPT_STARTED);
        jobHistoryEvents.add(MAP_ATTEMPT_FAILED);
        jobHistoryEvents.add(MAP_ATTEMPT_STARTED);
        jobHistoryEvents.add(MAP_ATTEMPT_FAILED);
        jobHistoryEvents.add(TASK_FAILED);
        recoveryChecker(recoverMapTask, TaskState.FAILED, finalAttemptStates, arg, jobHistoryEvents, 2L, 2L);
    }

    @Test
    public void testRecoveryTaskSuccessAllAttemptsFail() {
        TestRecovery.LOG.info("--- START:  testRecoveryTaskSuccessAllAttemptsFail ---");
        long clusterTimestamp = System.currentTimeMillis();
        EventHandler mockEventHandler = Mockito.mock(EventHandler.class);
        MapTaskImpl recoverMapTask = getMockMapTask(clusterTimestamp, mockEventHandler);
        TaskId taskId = recoverMapTask.getID();
        JobID jobID = new JobID(Long.toString(clusterTimestamp), 1);
        TaskID taskID = new TaskID(jobID, TaskType.MAP, taskId.getId());
        // Mock up the TaskAttempts
        Map<TaskAttemptID, TaskAttemptInfo> mockTaskAttempts = new HashMap<TaskAttemptID, TaskAttemptInfo>();
        TaskAttemptID taId1 = new TaskAttemptID(taskID, 2);
        TaskAttemptInfo mockTAinfo1 = getMockTaskAttemptInfo(taId1, FAILED);
        mockTaskAttempts.put(taId1, mockTAinfo1);
        TaskAttemptID taId2 = new TaskAttemptID(taskID, 1);
        TaskAttemptInfo mockTAinfo2 = getMockTaskAttemptInfo(taId2, FAILED);
        mockTaskAttempts.put(taId2, mockTAinfo2);
        OutputCommitter mockCommitter = Mockito.mock(OutputCommitter.class);
        TaskInfo mockTaskInfo = Mockito.mock(TaskInfo.class);
        Mockito.when(mockTaskInfo.getTaskStatus()).thenReturn("SUCCEEDED");
        Mockito.when(mockTaskInfo.getTaskId()).thenReturn(taskID);
        Mockito.when(mockTaskInfo.getAllTaskAttempts()).thenReturn(mockTaskAttempts);
        recoverMapTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskRecoverEvent(taskId, mockTaskInfo, mockCommitter, true));
        ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(mockEventHandler, Mockito.atLeast(1)).handle(((org.apache.hadoop.yarn.event.Event) (arg.capture())));
        Map<TaskAttemptID, TaskAttemptState> finalAttemptStates = new HashMap<TaskAttemptID, TaskAttemptState>();
        finalAttemptStates.put(taId1, FAILED);
        finalAttemptStates.put(taId2, FAILED);
        // check for one new attempt launched since successful attempt not found
        TaskAttemptID taId3 = new TaskAttemptID(taskID, 2000);
        finalAttemptStates.put(taId3, NEW);
        List<EventType> jobHistoryEvents = new ArrayList<EventType>();
        jobHistoryEvents.add(TASK_STARTED);
        jobHistoryEvents.add(MAP_ATTEMPT_STARTED);
        jobHistoryEvents.add(MAP_ATTEMPT_FAILED);
        jobHistoryEvents.add(MAP_ATTEMPT_STARTED);
        jobHistoryEvents.add(MAP_ATTEMPT_FAILED);
        recoveryChecker(recoverMapTask, TaskState.RUNNING, finalAttemptStates, arg, jobHistoryEvents, 2L, 2L);
    }

    @Test
    public void testRecoveryTaskSuccessAllAttemptsSucceed() {
        TestRecovery.LOG.info("--- START:  testRecoveryTaskSuccessAllAttemptsFail ---");
        long clusterTimestamp = System.currentTimeMillis();
        EventHandler mockEventHandler = Mockito.mock(EventHandler.class);
        MapTaskImpl recoverMapTask = getMockMapTask(clusterTimestamp, mockEventHandler);
        TaskId taskId = recoverMapTask.getID();
        JobID jobID = new JobID(Long.toString(clusterTimestamp), 1);
        TaskID taskID = new TaskID(jobID, TaskType.MAP, taskId.getId());
        // Mock up the TaskAttempts
        Map<TaskAttemptID, TaskAttemptInfo> mockTaskAttempts = new HashMap<TaskAttemptID, TaskAttemptInfo>();
        TaskAttemptID taId1 = new TaskAttemptID(taskID, 2);
        TaskAttemptInfo mockTAinfo1 = getMockTaskAttemptInfo(taId1, TaskAttemptState.SUCCEEDED);
        mockTaskAttempts.put(taId1, mockTAinfo1);
        TaskAttemptID taId2 = new TaskAttemptID(taskID, 1);
        TaskAttemptInfo mockTAinfo2 = getMockTaskAttemptInfo(taId2, TaskAttemptState.SUCCEEDED);
        mockTaskAttempts.put(taId2, mockTAinfo2);
        OutputCommitter mockCommitter = Mockito.mock(OutputCommitter.class);
        TaskInfo mockTaskInfo = Mockito.mock(TaskInfo.class);
        Mockito.when(mockTaskInfo.getTaskStatus()).thenReturn("SUCCEEDED");
        Mockito.when(mockTaskInfo.getTaskId()).thenReturn(taskID);
        Mockito.when(mockTaskInfo.getAllTaskAttempts()).thenReturn(mockTaskAttempts);
        recoverMapTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskRecoverEvent(taskId, mockTaskInfo, mockCommitter, true));
        ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(mockEventHandler, Mockito.atLeast(1)).handle(((org.apache.hadoop.yarn.event.Event) (arg.capture())));
        Map<TaskAttemptID, TaskAttemptState> finalAttemptStates = new HashMap<TaskAttemptID, TaskAttemptState>();
        finalAttemptStates.put(taId1, TaskAttemptState.SUCCEEDED);
        finalAttemptStates.put(taId2, TaskAttemptState.SUCCEEDED);
        List<EventType> jobHistoryEvents = new ArrayList<EventType>();
        jobHistoryEvents.add(TASK_STARTED);
        jobHistoryEvents.add(MAP_ATTEMPT_STARTED);
        jobHistoryEvents.add(MAP_ATTEMPT_FINISHED);
        jobHistoryEvents.add(MAP_ATTEMPT_STARTED);
        jobHistoryEvents.add(MAP_ATTEMPT_FINISHED);
        jobHistoryEvents.add(TASK_FINISHED);
        recoveryChecker(recoverMapTask, SUCCEEDED, finalAttemptStates, arg, jobHistoryEvents, 2L, 0L);
    }

    @Test
    public void testRecoveryAllAttemptsKilled() {
        TestRecovery.LOG.info("--- START:  testRecoveryAllAttemptsKilled ---");
        long clusterTimestamp = System.currentTimeMillis();
        EventHandler mockEventHandler = Mockito.mock(EventHandler.class);
        MapTaskImpl recoverMapTask = getMockMapTask(clusterTimestamp, mockEventHandler);
        TaskId taskId = recoverMapTask.getID();
        JobID jobID = new JobID(Long.toString(clusterTimestamp), 1);
        TaskID taskID = new TaskID(jobID, TaskType.MAP, taskId.getId());
        // Mock up the TaskAttempts
        Map<TaskAttemptID, TaskAttemptInfo> mockTaskAttempts = new HashMap<TaskAttemptID, TaskAttemptInfo>();
        TaskAttemptID taId1 = new TaskAttemptID(taskID, 2);
        TaskAttemptInfo mockTAinfo1 = getMockTaskAttemptInfo(taId1, KILLED);
        mockTaskAttempts.put(taId1, mockTAinfo1);
        TaskAttemptID taId2 = new TaskAttemptID(taskID, 1);
        TaskAttemptInfo mockTAinfo2 = getMockTaskAttemptInfo(taId2, KILLED);
        mockTaskAttempts.put(taId2, mockTAinfo2);
        OutputCommitter mockCommitter = Mockito.mock(OutputCommitter.class);
        TaskInfo mockTaskInfo = Mockito.mock(TaskInfo.class);
        Mockito.when(mockTaskInfo.getTaskStatus()).thenReturn("KILLED");
        Mockito.when(mockTaskInfo.getTaskId()).thenReturn(taskID);
        Mockito.when(mockTaskInfo.getAllTaskAttempts()).thenReturn(mockTaskAttempts);
        recoverMapTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskRecoverEvent(taskId, mockTaskInfo, mockCommitter, true));
        ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(mockEventHandler, Mockito.atLeast(1)).handle(((org.apache.hadoop.yarn.event.Event) (arg.capture())));
        Map<TaskAttemptID, TaskAttemptState> finalAttemptStates = new HashMap<TaskAttemptID, TaskAttemptState>();
        finalAttemptStates.put(taId1, KILLED);
        finalAttemptStates.put(taId2, KILLED);
        List<EventType> jobHistoryEvents = new ArrayList<EventType>();
        jobHistoryEvents.add(TASK_STARTED);
        jobHistoryEvents.add(MAP_ATTEMPT_STARTED);
        jobHistoryEvents.add(MAP_ATTEMPT_KILLED);
        jobHistoryEvents.add(MAP_ATTEMPT_STARTED);
        jobHistoryEvents.add(MAP_ATTEMPT_KILLED);
        jobHistoryEvents.add(TASK_FAILED);
        recoveryChecker(recoverMapTask, TaskState.KILLED, finalAttemptStates, arg, jobHistoryEvents, 2L, 0L);
    }

    static class MRAppWithHistory extends MRApp {
        public MRAppWithHistory(int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart, int startCount) {
            super(maps, reduces, autoComplete, testName, cleanOnStart, startCount);
        }

        @Override
        protected ContainerLauncher createContainerLauncher(AppContext context) {
            MRApp.MockContainerLauncher launcher = new MRApp.MockContainerLauncher() {
                @Override
                public void handle(ContainerLauncherEvent event) {
                    TaskAttemptId taskAttemptID = event.getTaskAttemptID();
                    // Pass everything except the 2nd attempt of the first task.
                    if (((taskAttemptID.getId()) != 1) || ((taskAttemptID.getTaskId().getId()) != 0)) {
                        super.handle(event);
                    }
                }
            };
            launcher.shufflePort = 5467;
            return launcher;
        }

        @Override
        protected EventHandler<JobHistoryEvent> createJobHistoryHandler(AppContext context) {
            JobHistoryEventHandler eventHandler = new JobHistoryEventHandler(context, getStartCount());
            return eventHandler;
        }
    }

    static class MRAppNoShuffleSecret extends TestRecovery.MRAppWithHistory {
        public MRAppNoShuffleSecret(int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart, int startCount) {
            super(maps, reduces, autoComplete, testName, cleanOnStart, startCount);
        }

        @Override
        protected void initJobCredentialsAndUGI(Configuration conf) {
            // do NOT put a shuffle secret in the job credentials
        }
    }
}

