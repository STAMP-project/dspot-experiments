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
package org.apache.hadoop.mapreduce.v2.app.job.impl;


import JobACL.VIEW_JOB;
import JobEventType.JOB_INIT;
import JobState.ERROR;
import JobState.RUNNING;
import JobStateInternal.COMMITTING;
import JobStateInternal.FAILED;
import JobStateInternal.FAIL_ABORT;
import JobStateInternal.FAIL_WAIT;
import JobStateInternal.INITED;
import JobStateInternal.KILLED;
import JobStateInternal.KILL_ABORT;
import JobStateInternal.NEW;
import JobStateInternal.REBOOT;
import JobStateInternal.SETUP;
import JobStateInternal.SUCCEEDED;
import MRConfig.MR_ACLS_ENABLED;
import MRJobConfig.JOB_ACL_VIEW_JOB;
import MRJobConfig.JOB_UBERTASK_ENABLE;
import MRJobConfig.JOB_UBERTASK_MAXMAPS;
import MRJobConfig.JOB_UBERTASK_MAXREDUCES;
import MRJobConfig.MAP_MAX_ATTEMPTS;
import MRJobConfig.MR_AM_COMMITTER_CANCEL_TIMEOUT_MS;
import MRJobConfig.MR_AM_MAX_ATTEMPTS;
import MRJobConfig.MR_AM_STAGING_DIR;
import MRJobConfig.NUM_REDUCES;
import MRJobConfig.REDUCE_CPU_VCORES;
import MRJobConfig.REDUCE_MEMORY_MB;
import MRJobConfig.WORKFLOW_ID;
import MRJobConfig.WORKFLOW_NAME;
import MRJobConfig.WORKFLOW_NODE_NAME;
import MRJobConfig.WORKFLOW_TAGS;
import NodeState.UNHEALTHY;
import TaskType.MAP;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.JobStatus.State;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.jobhistory.EventType;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryEvent;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryParser.TaskInfo;
import org.apache.hadoop.mapreduce.jobhistory.JobSubmittedEvent;
import org.apache.hadoop.mapreduce.security.token.JobTokenSecretManager;
import org.apache.hadoop.mapreduce.split.JobSplit.TaskSplitMetaInfo;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptCompletionEvent;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskState;
import org.apache.hadoop.mapreduce.v2.api.records.TaskType;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.commit.CommitterEventHandler;
import org.apache.hadoop.mapreduce.v2.app.job.JobStateInternal;
import org.apache.hadoop.mapreduce.v2.app.job.Task;
import org.apache.hadoop.mapreduce.v2.app.job.TaskAttempt;
import org.apache.hadoop.mapreduce.v2.app.job.event.JobDiagnosticsUpdateEvent;
import org.apache.hadoop.mapreduce.v2.app.job.event.JobEvent;
import org.apache.hadoop.mapreduce.v2.app.job.event.JobEventType;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEventType;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskEventType;
import org.apache.hadoop.mapreduce.v2.app.job.impl.JobImpl.InitTransition;
import org.apache.hadoop.mapreduce.v2.app.metrics.MRAppMetrics;
import org.apache.hadoop.mapreduce.v2.util.MRBuilderUtils;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import org.apache.hadoop.yarn.event.DrainDispatcher;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.event.InlineDispatcher;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.state.StateMachine;
import org.apache.hadoop.yarn.state.StateMachineFactory;
import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.yarn.util.SystemClock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests various functions of the JobImpl class
 */
@SuppressWarnings({ "rawtypes" })
public class TestJobImpl {
    static String stagingDir = "target/test-staging/";

    @Test
    public void testJobNoTasks() {
        Configuration conf = new Configuration();
        conf.setInt(NUM_REDUCES, 0);
        conf.set(MR_AM_STAGING_DIR, TestJobImpl.stagingDir);
        conf.set(WORKFLOW_ID, "testId");
        conf.set(WORKFLOW_NAME, "testName");
        conf.set(WORKFLOW_NODE_NAME, "testNodeName");
        conf.set(((MRJobConfig.WORKFLOW_ADJACENCY_PREFIX_STRING) + "key1"), "value1");
        conf.set(((MRJobConfig.WORKFLOW_ADJACENCY_PREFIX_STRING) + "key2"), "value2");
        conf.set(WORKFLOW_TAGS, "tag1,tag2");
        AsyncDispatcher dispatcher = new AsyncDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        OutputCommitter committer = Mockito.mock(OutputCommitter.class);
        CommitterEventHandler commitHandler = TestJobImpl.createCommitterEventHandler(dispatcher, committer);
        commitHandler.init(conf);
        commitHandler.start();
        TestJobImpl.JobSubmittedEventHandler jseHandler = new TestJobImpl.JobSubmittedEventHandler("testId", "testName", "testNodeName", "\"key2\"=\"value2\" \"key1\"=\"value1\" ", "tag1,tag2");
        dispatcher.register(EventType.class, jseHandler);
        JobImpl job = TestJobImpl.createStubbedJob(conf, dispatcher, 0, null);
        job.handle(new JobEvent(job.getID(), JobEventType.JOB_INIT));
        TestJobImpl.assertJobState(job, INITED);
        job.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobStartEvent(job.getID()));
        TestJobImpl.assertJobState(job, SUCCEEDED);
        dispatcher.stop();
        commitHandler.stop();
        try {
            Assert.assertTrue(jseHandler.getAssertValue());
        } catch (InterruptedException e) {
            Assert.fail("Workflow related attributes are not tested properly");
        }
    }

    @Test(timeout = 20000)
    public void testCommitJobFailsJob() throws Exception {
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, TestJobImpl.stagingDir);
        AsyncDispatcher dispatcher = new AsyncDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        CyclicBarrier syncBarrier = new CyclicBarrier(2);
        OutputCommitter committer = new TestJobImpl.TestingOutputCommitter(syncBarrier, false);
        CommitterEventHandler commitHandler = TestJobImpl.createCommitterEventHandler(dispatcher, committer);
        commitHandler.init(conf);
        commitHandler.start();
        JobImpl job = TestJobImpl.createRunningStubbedJob(conf, dispatcher, 2, null);
        TestJobImpl.completeJobTasks(job);
        TestJobImpl.assertJobState(job, COMMITTING);
        // let the committer fail and verify the job fails
        syncBarrier.await();
        TestJobImpl.assertJobState(job, FAILED);
        dispatcher.stop();
        commitHandler.stop();
    }

    @Test(timeout = 20000)
    public void testCheckJobCompleteSuccess() throws Exception {
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, TestJobImpl.stagingDir);
        AsyncDispatcher dispatcher = new AsyncDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        CyclicBarrier syncBarrier = new CyclicBarrier(2);
        OutputCommitter committer = new TestJobImpl.TestingOutputCommitter(syncBarrier, true);
        CommitterEventHandler commitHandler = TestJobImpl.createCommitterEventHandler(dispatcher, committer);
        commitHandler.init(conf);
        commitHandler.start();
        JobImpl job = TestJobImpl.createRunningStubbedJob(conf, dispatcher, 2, null);
        TestJobImpl.completeJobTasks(job);
        TestJobImpl.assertJobState(job, COMMITTING);
        job.handle(new JobEvent(job.getID(), JobEventType.JOB_TASK_ATTEMPT_COMPLETED));
        TestJobImpl.assertJobState(job, COMMITTING);
        job.handle(new JobEvent(job.getID(), JobEventType.JOB_MAP_TASK_RESCHEDULED));
        TestJobImpl.assertJobState(job, COMMITTING);
        // let the committer complete and verify the job succeeds
        syncBarrier.await();
        TestJobImpl.assertJobState(job, SUCCEEDED);
        job.handle(new JobEvent(job.getID(), JobEventType.JOB_TASK_ATTEMPT_COMPLETED));
        TestJobImpl.assertJobState(job, SUCCEEDED);
        job.handle(new JobEvent(job.getID(), JobEventType.JOB_MAP_TASK_RESCHEDULED));
        TestJobImpl.assertJobState(job, SUCCEEDED);
        dispatcher.stop();
        commitHandler.stop();
    }

    @Test(timeout = 20000)
    public void testRebootedDuringSetup() throws Exception {
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, TestJobImpl.stagingDir);
        AsyncDispatcher dispatcher = new AsyncDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        OutputCommitter committer = new TestJobImpl.StubbedOutputCommitter() {
            @Override
            public synchronized void setupJob(JobContext jobContext) throws IOException {
                while (!(Thread.interrupted())) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                } 
            }
        };
        CommitterEventHandler commitHandler = TestJobImpl.createCommitterEventHandler(dispatcher, committer);
        commitHandler.init(conf);
        commitHandler.start();
        AppContext mockContext = Mockito.mock(AppContext.class);
        Mockito.when(mockContext.isLastAMRetry()).thenReturn(false);
        JobImpl job = TestJobImpl.createStubbedJob(conf, dispatcher, 2, mockContext);
        JobId jobId = job.getID();
        job.handle(new JobEvent(jobId, JobEventType.JOB_INIT));
        TestJobImpl.assertJobState(job, INITED);
        job.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobStartEvent(jobId));
        TestJobImpl.assertJobState(job, SETUP);
        job.handle(new JobEvent(job.getID(), JobEventType.JOB_AM_REBOOT));
        TestJobImpl.assertJobState(job, REBOOT);
        // return the external state as RUNNING since otherwise JobClient will
        // exit when it polls the AM for job state
        Assert.assertEquals(RUNNING, job.getState());
        dispatcher.stop();
        commitHandler.stop();
    }

    @Test(timeout = 20000)
    public void testRebootedDuringCommit() throws Exception {
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, TestJobImpl.stagingDir);
        conf.setInt(MR_AM_MAX_ATTEMPTS, 2);
        AsyncDispatcher dispatcher = new AsyncDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        CyclicBarrier syncBarrier = new CyclicBarrier(2);
        OutputCommitter committer = new TestJobImpl.WaitingOutputCommitter(syncBarrier, true);
        CommitterEventHandler commitHandler = TestJobImpl.createCommitterEventHandler(dispatcher, committer);
        commitHandler.init(conf);
        commitHandler.start();
        AppContext mockContext = Mockito.mock(AppContext.class);
        Mockito.when(mockContext.isLastAMRetry()).thenReturn(true);
        Mockito.when(mockContext.hasSuccessfullyUnregistered()).thenReturn(false);
        JobImpl job = TestJobImpl.createRunningStubbedJob(conf, dispatcher, 2, mockContext);
        TestJobImpl.completeJobTasks(job);
        TestJobImpl.assertJobState(job, COMMITTING);
        syncBarrier.await();
        job.handle(new JobEvent(job.getID(), JobEventType.JOB_AM_REBOOT));
        TestJobImpl.assertJobState(job, REBOOT);
        // return the external state as ERROR since this is last retry.
        Assert.assertEquals(RUNNING, job.getState());
        Mockito.when(mockContext.hasSuccessfullyUnregistered()).thenReturn(true);
        Assert.assertEquals(ERROR, job.getState());
        dispatcher.stop();
        commitHandler.stop();
    }

    @Test(timeout = 20000)
    public void testKilledDuringSetup() throws Exception {
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, TestJobImpl.stagingDir);
        AsyncDispatcher dispatcher = new AsyncDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        OutputCommitter committer = new TestJobImpl.StubbedOutputCommitter() {
            @Override
            public synchronized void setupJob(JobContext jobContext) throws IOException {
                while (!(Thread.interrupted())) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                } 
            }
        };
        CommitterEventHandler commitHandler = TestJobImpl.createCommitterEventHandler(dispatcher, committer);
        commitHandler.init(conf);
        commitHandler.start();
        JobImpl job = TestJobImpl.createStubbedJob(conf, dispatcher, 2, null);
        JobId jobId = job.getID();
        job.handle(new JobEvent(jobId, JobEventType.JOB_INIT));
        TestJobImpl.assertJobState(job, INITED);
        job.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobStartEvent(jobId));
        TestJobImpl.assertJobState(job, SETUP);
        job.handle(new JobEvent(job.getID(), JobEventType.JOB_KILL));
        TestJobImpl.assertJobState(job, KILLED);
        dispatcher.stop();
        commitHandler.stop();
    }

    @Test(timeout = 20000)
    public void testKilledDuringCommit() throws Exception {
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, TestJobImpl.stagingDir);
        AsyncDispatcher dispatcher = new AsyncDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        CyclicBarrier syncBarrier = new CyclicBarrier(2);
        OutputCommitter committer = new TestJobImpl.WaitingOutputCommitter(syncBarrier, true);
        CommitterEventHandler commitHandler = TestJobImpl.createCommitterEventHandler(dispatcher, committer);
        commitHandler.init(conf);
        commitHandler.start();
        JobImpl job = TestJobImpl.createRunningStubbedJob(conf, dispatcher, 2, null);
        TestJobImpl.completeJobTasks(job);
        TestJobImpl.assertJobState(job, COMMITTING);
        syncBarrier.await();
        job.handle(new JobEvent(job.getID(), JobEventType.JOB_KILL));
        TestJobImpl.assertJobState(job, KILLED);
        dispatcher.stop();
        commitHandler.stop();
    }

    @Test
    public void testAbortJobCalledAfterKillingTasks() throws IOException {
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, TestJobImpl.stagingDir);
        conf.set(MR_AM_COMMITTER_CANCEL_TIMEOUT_MS, "1000");
        InlineDispatcher dispatcher = new InlineDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        OutputCommitter committer = Mockito.mock(OutputCommitter.class);
        CommitterEventHandler commitHandler = TestJobImpl.createCommitterEventHandler(dispatcher, committer);
        commitHandler.init(conf);
        commitHandler.start();
        JobImpl job = TestJobImpl.createRunningStubbedJob(conf, dispatcher, 2, null);
        // Fail one task. This should land the JobImpl in the FAIL_WAIT state
        job.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobTaskEvent(MRBuilderUtils.newTaskId(job.getID(), 1, MAP), TaskState.FAILED));
        // Verify abort job hasn't been called
        Mockito.verify(committer, Mockito.never()).abortJob(((JobContext) (Mockito.any())), ((State) (Mockito.any())));
        TestJobImpl.assertJobState(job, FAIL_WAIT);
        // Verify abortJob is called once and the job failed
        Mockito.verify(committer, Mockito.timeout(2000).times(1)).abortJob(((JobContext) (Mockito.any())), ((State) (Mockito.any())));
        TestJobImpl.assertJobState(job, FAILED);
        dispatcher.stop();
    }

    @Test(timeout = 10000)
    public void testFailAbortDoesntHang() throws IOException {
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, TestJobImpl.stagingDir);
        conf.set(MR_AM_COMMITTER_CANCEL_TIMEOUT_MS, "1000");
        DrainDispatcher dispatcher = new DrainDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        OutputCommitter committer = Mockito.mock(OutputCommitter.class);
        CommitterEventHandler commitHandler = TestJobImpl.createCommitterEventHandler(dispatcher, committer);
        commitHandler.init(conf);
        commitHandler.start();
        // Job has only 1 mapper task. No reducers
        conf.setInt(NUM_REDUCES, 0);
        conf.setInt(MAP_MAX_ATTEMPTS, 1);
        JobImpl job = TestJobImpl.createRunningStubbedJob(conf, dispatcher, 1, null);
        // Fail / finish all the tasks. This should land the JobImpl directly in the
        // FAIL_ABORT state
        for (Task t : job.tasks.values()) {
            TaskImpl task = ((TaskImpl) (t));
            task.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskEvent(task.getID(), TaskEventType.T_SCHEDULE));
            for (TaskAttempt ta : task.getAttempts().values()) {
                task.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptFailedEvent(ta.getID()));
            }
        }
        dispatcher.await();
        // Verify abortJob is called once and the job failed
        Mockito.verify(committer, Mockito.timeout(2000).times(1)).abortJob(((JobContext) (Mockito.any())), ((State) (Mockito.any())));
        TestJobImpl.assertJobState(job, FAILED);
        dispatcher.stop();
    }

    @Test(timeout = 20000)
    public void testKilledDuringFailAbort() throws Exception {
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, TestJobImpl.stagingDir);
        AsyncDispatcher dispatcher = new AsyncDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        OutputCommitter committer = new TestJobImpl.StubbedOutputCommitter() {
            @Override
            public void setupJob(JobContext jobContext) throws IOException {
                throw new IOException("forced failure");
            }

            @Override
            public synchronized void abortJob(JobContext jobContext, State state) throws IOException {
                while (!(Thread.interrupted())) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                } 
            }
        };
        CommitterEventHandler commitHandler = TestJobImpl.createCommitterEventHandler(dispatcher, committer);
        commitHandler.init(conf);
        commitHandler.start();
        JobImpl job = TestJobImpl.createStubbedJob(conf, dispatcher, 2, null);
        JobId jobId = job.getID();
        job.handle(new JobEvent(jobId, JobEventType.JOB_INIT));
        TestJobImpl.assertJobState(job, INITED);
        job.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobStartEvent(jobId));
        TestJobImpl.assertJobState(job, FAIL_ABORT);
        job.handle(new JobEvent(jobId, JobEventType.JOB_KILL));
        TestJobImpl.assertJobState(job, KILLED);
        dispatcher.stop();
        commitHandler.stop();
    }

    @Test(timeout = 20000)
    public void testKilledDuringKillAbort() throws Exception {
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, TestJobImpl.stagingDir);
        // not initializing dispatcher to avoid potential race condition between
        // the dispatcher thread & test thread - see MAPREDUCE-6831
        AsyncDispatcher dispatcher = new AsyncDispatcher();
        OutputCommitter committer = new TestJobImpl.StubbedOutputCommitter() {
            @Override
            public synchronized void abortJob(JobContext jobContext, State state) throws IOException {
                while (!(Thread.interrupted())) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                } 
            }
        };
        CommitterEventHandler commitHandler = TestJobImpl.createCommitterEventHandler(dispatcher, committer);
        commitHandler.init(conf);
        commitHandler.start();
        JobImpl job = TestJobImpl.createStubbedJob(conf, dispatcher, 2, null);
        JobId jobId = job.getID();
        job.handle(new JobEvent(jobId, JobEventType.JOB_INIT));
        TestJobImpl.assertJobState(job, INITED);
        job.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobStartEvent(jobId));
        TestJobImpl.assertJobState(job, SETUP);
        job.handle(new JobEvent(jobId, JobEventType.JOB_KILL));
        TestJobImpl.assertJobState(job, KILL_ABORT);
        job.handle(new JobEvent(jobId, JobEventType.JOB_KILL));
        TestJobImpl.assertJobState(job, KILLED);
        dispatcher.stop();
        commitHandler.stop();
    }

    @Test(timeout = 20000)
    public void testUnusableNodeTransition() throws Exception {
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, TestJobImpl.stagingDir);
        conf.setInt(NUM_REDUCES, 1);
        DrainDispatcher dispatcher = new DrainDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        CyclicBarrier syncBarrier = new CyclicBarrier(2);
        OutputCommitter committer = new TestJobImpl.TestingOutputCommitter(syncBarrier, true);
        CommitterEventHandler commitHandler = TestJobImpl.createCommitterEventHandler(dispatcher, committer);
        commitHandler.init(conf);
        commitHandler.start();
        final JobImpl job = TestJobImpl.createRunningStubbedJob(conf, dispatcher, 2, null);
        // add a special task event handler to put the task back to running in case
        // of task rescheduling/killing
        EventHandler<TaskAttemptEvent> taskAttemptEventHandler = new EventHandler<TaskAttemptEvent>() {
            @Override
            public void handle(TaskAttemptEvent event) {
                if ((event.getType()) == (TaskAttemptEventType.TA_KILL)) {
                    job.decrementSucceededMapperCount();
                }
            }
        };
        dispatcher.register(TaskAttemptEventType.class, taskAttemptEventHandler);
        // replace the tasks with spied versions to return the right attempts
        Map<TaskId, Task> spiedTasks = new HashMap<>();
        List<NodeReport> nodeReports = new ArrayList<>();
        Map<NodeReport, TaskId> nodeReportsToTaskIds = new HashMap<>();
        createSpiedMapTasks(nodeReportsToTaskIds, spiedTasks, job, UNHEALTHY, nodeReports);
        // replace the tasks with the spied tasks
        job.tasks.putAll(spiedTasks);
        // complete all mappers first
        for (TaskId taskId : job.tasks.keySet()) {
            if ((taskId.getTaskType()) == (TaskType.MAP)) {
                // generate a task attempt completed event first to populate the
                // nodes-to-succeeded-attempts map
                TaskAttemptCompletionEvent tce = Records.newRecord(TaskAttemptCompletionEvent.class);
                TaskAttemptId attemptId = MRBuilderUtils.newTaskAttemptId(taskId, 0);
                tce.setAttemptId(attemptId);
                tce.setStatus(TaskAttemptCompletionEventStatus.SUCCEEDED);
                job.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobTaskAttemptCompletedEvent(tce));
                // complete the task itself
                job.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobTaskEvent(taskId, TaskState.SUCCEEDED));
                Assert.assertEquals(RUNNING, job.getState());
            }
        }
        // add an event for a node transition
        NodeReport firstMapperNodeReport = nodeReports.get(0);
        NodeReport secondMapperNodeReport = nodeReports.get(1);
        job.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobUpdatedNodesEvent(job.getID(), Collections.singletonList(firstMapperNodeReport)));
        dispatcher.await();
        // complete the reducer
        for (TaskId taskId : job.tasks.keySet()) {
            if ((taskId.getTaskType()) == (TaskType.REDUCE)) {
                job.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobTaskEvent(taskId, TaskState.SUCCEEDED));
            }
        }
        // add another event for a node transition for the other mapper
        // this should not trigger rescheduling
        job.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobUpdatedNodesEvent(job.getID(), Collections.singletonList(secondMapperNodeReport)));
        // complete the first mapper that was rescheduled
        TaskId firstMapper = nodeReportsToTaskIds.get(firstMapperNodeReport);
        job.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobTaskEvent(firstMapper, TaskState.SUCCEEDED));
        // verify the state is moving to committing
        TestJobImpl.assertJobState(job, COMMITTING);
        // let the committer complete and verify the job succeeds
        syncBarrier.await();
        TestJobImpl.assertJobState(job, SUCCEEDED);
        dispatcher.stop();
        commitHandler.stop();
    }

    @Test
    public void testJobNCompletedWhenAllReducersAreFinished() throws Exception {
        testJobCompletionWhenReducersAreFinished(true);
    }

    @Test
    public void testJobNotCompletedWhenAllReducersAreFinished() throws Exception {
        testJobCompletionWhenReducersAreFinished(false);
    }

    @Test
    public void testCheckAccess() {
        // Create two unique users
        String user1 = System.getProperty("user.name");
        String user2 = user1 + "1234";
        UserGroupInformation ugi1 = UserGroupInformation.createRemoteUser(user1);
        UserGroupInformation ugi2 = UserGroupInformation.createRemoteUser(user2);
        // Create the job
        JobID jobID = JobID.forName("job_1234567890000_0001");
        JobId jobId = TypeConverter.toYarn(jobID);
        // Setup configuration access only to user1 (owner)
        Configuration conf1 = new Configuration();
        conf1.setBoolean(MR_ACLS_ENABLED, true);
        conf1.set(JOB_ACL_VIEW_JOB, "");
        // Verify access
        JobImpl job1 = new JobImpl(jobId, null, conf1, null, null, null, null, null, null, null, null, true, user1, 0, null, null, null, null);
        Assert.assertTrue(job1.checkAccess(ugi1, VIEW_JOB));
        Assert.assertFalse(job1.checkAccess(ugi2, VIEW_JOB));
        // Setup configuration access to the user1 (owner) and user2
        Configuration conf2 = new Configuration();
        conf2.setBoolean(MR_ACLS_ENABLED, true);
        conf2.set(JOB_ACL_VIEW_JOB, user2);
        // Verify access
        JobImpl job2 = new JobImpl(jobId, null, conf2, null, null, null, null, null, null, null, null, true, user1, 0, null, null, null, null);
        Assert.assertTrue(job2.checkAccess(ugi1, VIEW_JOB));
        Assert.assertTrue(job2.checkAccess(ugi2, VIEW_JOB));
        // Setup configuration access with security enabled and access to all
        Configuration conf3 = new Configuration();
        conf3.setBoolean(MR_ACLS_ENABLED, true);
        conf3.set(JOB_ACL_VIEW_JOB, "*");
        // Verify access
        JobImpl job3 = new JobImpl(jobId, null, conf3, null, null, null, null, null, null, null, null, true, user1, 0, null, null, null, null);
        Assert.assertTrue(job3.checkAccess(ugi1, VIEW_JOB));
        Assert.assertTrue(job3.checkAccess(ugi2, VIEW_JOB));
        // Setup configuration access without security enabled
        Configuration conf4 = new Configuration();
        conf4.setBoolean(MR_ACLS_ENABLED, false);
        conf4.set(JOB_ACL_VIEW_JOB, "");
        // Verify access
        JobImpl job4 = new JobImpl(jobId, null, conf4, null, null, null, null, null, null, null, null, true, user1, 0, null, null, null, null);
        Assert.assertTrue(job4.checkAccess(ugi1, VIEW_JOB));
        Assert.assertTrue(job4.checkAccess(ugi2, VIEW_JOB));
        // Setup configuration access without security enabled
        Configuration conf5 = new Configuration();
        conf5.setBoolean(MR_ACLS_ENABLED, true);
        conf5.set(JOB_ACL_VIEW_JOB, "");
        // Verify access
        JobImpl job5 = new JobImpl(jobId, null, conf5, null, null, null, null, null, null, null, null, true, user1, 0, null, null, null, null);
        Assert.assertTrue(job5.checkAccess(ugi1, null));
        Assert.assertTrue(job5.checkAccess(ugi2, null));
    }

    @Test
    public void testReportDiagnostics() throws Exception {
        JobID jobID = JobID.forName("job_1234567890000_0001");
        JobId jobId = TypeConverter.toYarn(jobID);
        final String diagMsg = "some diagnostic message";
        final JobDiagnosticsUpdateEvent diagUpdateEvent = new JobDiagnosticsUpdateEvent(jobId, diagMsg);
        MRAppMetrics mrAppMetrics = MRAppMetrics.create();
        AppContext mockContext = Mockito.mock(AppContext.class);
        Mockito.when(mockContext.hasSuccessfullyUnregistered()).thenReturn(true);
        JobImpl job = new JobImpl(jobId, Records.newRecord(ApplicationAttemptId.class), new Configuration(), Mockito.mock(EventHandler.class), null, Mockito.mock(JobTokenSecretManager.class), null, SystemClock.getInstance(), null, mrAppMetrics, null, true, null, 0, null, mockContext, null, null);
        job.handle(diagUpdateEvent);
        String diagnostics = job.getReport().getDiagnostics();
        Assert.assertNotNull(diagnostics);
        Assert.assertTrue(diagnostics.contains(diagMsg));
        job = new JobImpl(jobId, Records.newRecord(ApplicationAttemptId.class), new Configuration(), Mockito.mock(EventHandler.class), null, Mockito.mock(JobTokenSecretManager.class), null, SystemClock.getInstance(), null, mrAppMetrics, null, true, null, 0, null, mockContext, null, null);
        job.handle(new JobEvent(jobId, JobEventType.JOB_KILL));
        job.handle(diagUpdateEvent);
        diagnostics = job.getReport().getDiagnostics();
        Assert.assertNotNull(diagnostics);
        Assert.assertTrue(diagnostics.contains(diagMsg));
    }

    @Test
    public void testUberDecision() throws Exception {
        // with default values, no of maps is 2
        Configuration conf = new Configuration();
        boolean isUber = testUberDecision(conf);
        Assert.assertFalse(isUber);
        // enable uber mode, no of maps is 2
        conf = new Configuration();
        conf.setBoolean(JOB_UBERTASK_ENABLE, true);
        isUber = testUberDecision(conf);
        Assert.assertTrue(isUber);
        // enable uber mode, no of maps is 2, no of reduces is 1 and uber task max
        // reduces is 0
        conf = new Configuration();
        conf.setBoolean(JOB_UBERTASK_ENABLE, true);
        conf.setInt(JOB_UBERTASK_MAXREDUCES, 0);
        conf.setInt(NUM_REDUCES, 1);
        isUber = testUberDecision(conf);
        Assert.assertFalse(isUber);
        // enable uber mode, no of maps is 2, no of reduces is 1 and uber task max
        // reduces is 1
        conf = new Configuration();
        conf.setBoolean(JOB_UBERTASK_ENABLE, true);
        conf.setInt(JOB_UBERTASK_MAXREDUCES, 1);
        conf.setInt(NUM_REDUCES, 1);
        isUber = testUberDecision(conf);
        Assert.assertTrue(isUber);
        // enable uber mode, no of maps is 2 and uber task max maps is 0
        conf = new Configuration();
        conf.setBoolean(JOB_UBERTASK_ENABLE, true);
        conf.setInt(JOB_UBERTASK_MAXMAPS, 1);
        isUber = testUberDecision(conf);
        Assert.assertFalse(isUber);
        // enable uber mode of 0 reducer no matter how much memory assigned to reducer
        conf = new Configuration();
        conf.setBoolean(JOB_UBERTASK_ENABLE, true);
        conf.setInt(NUM_REDUCES, 0);
        conf.setInt(REDUCE_MEMORY_MB, 2048);
        conf.setInt(REDUCE_CPU_VCORES, 10);
        isUber = testUberDecision(conf);
        Assert.assertTrue(isUber);
    }

    @Test
    public void testTransitionsAtFailed() throws IOException {
        Configuration conf = new Configuration();
        AsyncDispatcher dispatcher = new AsyncDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        OutputCommitter committer = Mockito.mock(OutputCommitter.class);
        Mockito.doThrow(new IOException("forcefail")).when(committer).setupJob(ArgumentMatchers.any(JobContext.class));
        CommitterEventHandler commitHandler = TestJobImpl.createCommitterEventHandler(dispatcher, committer);
        commitHandler.init(conf);
        commitHandler.start();
        AppContext mockContext = Mockito.mock(AppContext.class);
        Mockito.when(mockContext.hasSuccessfullyUnregistered()).thenReturn(false);
        JobImpl job = TestJobImpl.createStubbedJob(conf, dispatcher, 2, mockContext);
        JobId jobId = job.getID();
        job.handle(new JobEvent(jobId, JobEventType.JOB_INIT));
        TestJobImpl.assertJobState(job, INITED);
        job.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobStartEvent(jobId));
        TestJobImpl.assertJobState(job, FAILED);
        job.handle(new JobEvent(jobId, JobEventType.JOB_TASK_COMPLETED));
        TestJobImpl.assertJobState(job, FAILED);
        job.handle(new JobEvent(jobId, JobEventType.JOB_TASK_ATTEMPT_COMPLETED));
        TestJobImpl.assertJobState(job, FAILED);
        job.handle(new JobEvent(jobId, JobEventType.JOB_MAP_TASK_RESCHEDULED));
        TestJobImpl.assertJobState(job, FAILED);
        job.handle(new JobEvent(jobId, JobEventType.JOB_TASK_ATTEMPT_FETCH_FAILURE));
        TestJobImpl.assertJobState(job, FAILED);
        Assert.assertEquals(RUNNING, job.getState());
        Mockito.when(mockContext.hasSuccessfullyUnregistered()).thenReturn(true);
        Assert.assertEquals(JobState.FAILED, job.getState());
        dispatcher.stop();
        commitHandler.stop();
    }

    static final String EXCEPTIONMSG = "Splits max exceeded";

    @Test
    public void testMetaInfoSizeOverMax() throws Exception {
        Configuration conf = new Configuration();
        JobID jobID = JobID.forName("job_1234567890000_0001");
        JobId jobId = TypeConverter.toYarn(jobID);
        MRAppMetrics mrAppMetrics = MRAppMetrics.create();
        JobImpl job = new JobImpl(jobId, ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 0), 0), conf, Mockito.mock(EventHandler.class), null, new JobTokenSecretManager(), new Credentials(), null, null, mrAppMetrics, null, true, null, 0, null, null, null, null);
        InitTransition initTransition = new InitTransition() {
            @Override
            protected TaskSplitMetaInfo[] createSplits(JobImpl job, JobId jobId) {
                throw new YarnRuntimeException(TestJobImpl.EXCEPTIONMSG);
            }
        };
        JobEvent mockJobEvent = Mockito.mock(JobEvent.class);
        JobStateInternal jobSI = initTransition.transition(job, mockJobEvent);
        Assert.assertTrue("When init fails, return value from InitTransition.transition should equal NEW.", jobSI.equals(NEW));
        Assert.assertTrue("Job diagnostics should contain YarnRuntimeException", job.getDiagnostics().toString().contains("YarnRuntimeException"));
        Assert.assertTrue(("Job diagnostics should contain " + (TestJobImpl.EXCEPTIONMSG)), job.getDiagnostics().toString().contains(TestJobImpl.EXCEPTIONMSG));
    }

    @Test
    public void testJobPriorityUpdate() throws Exception {
        Configuration conf = new Configuration();
        AsyncDispatcher dispatcher = new AsyncDispatcher();
        Priority submittedPriority = Priority.newInstance(5);
        AppContext mockContext = Mockito.mock(AppContext.class);
        Mockito.when(mockContext.hasSuccessfullyUnregistered()).thenReturn(false);
        JobImpl job = TestJobImpl.createStubbedJob(conf, dispatcher, 2, mockContext);
        JobId jobId = job.getID();
        job.handle(new JobEvent(jobId, JobEventType.JOB_INIT));
        TestJobImpl.assertJobState(job, INITED);
        job.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobStartEvent(jobId));
        TestJobImpl.assertJobState(job, SETUP);
        // Update priority of job to 5, and it will be updated
        job.setJobPriority(submittedPriority);
        Assert.assertEquals(submittedPriority, job.getReport().getJobPriority());
        job.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobSetupCompletedEvent(jobId));
        TestJobImpl.assertJobState(job, JobStateInternal.RUNNING);
        // Update priority of job to 8, and see whether its updated
        Priority updatedPriority = Priority.newInstance(8);
        job.setJobPriority(updatedPriority);
        TestJobImpl.assertJobState(job, JobStateInternal.RUNNING);
        Priority jobPriority = job.getReport().getJobPriority();
        Assert.assertNotNull(jobPriority);
        // Verify whether changed priority is same as what is set in Job.
        Assert.assertEquals(updatedPriority, jobPriority);
    }

    private static class JobSubmittedEventHandler implements EventHandler<JobHistoryEvent> {
        private String workflowId;

        private String workflowName;

        private String workflowNodeName;

        private String workflowAdjacencies;

        private String workflowTags;

        private Boolean assertBoolean;

        public JobSubmittedEventHandler(String workflowId, String workflowName, String workflowNodeName, String workflowAdjacencies, String workflowTags) {
            this.workflowId = workflowId;
            this.workflowName = workflowName;
            this.workflowNodeName = workflowNodeName;
            this.workflowAdjacencies = workflowAdjacencies;
            this.workflowTags = workflowTags;
            assertBoolean = null;
        }

        @Override
        public void handle(JobHistoryEvent jhEvent) {
            if ((jhEvent.getType()) != (EventType.JOB_SUBMITTED)) {
                return;
            }
            JobSubmittedEvent jsEvent = ((JobSubmittedEvent) (jhEvent.getHistoryEvent()));
            if (!(workflowId.equals(jsEvent.getWorkflowId()))) {
                setAssertValue(false);
                return;
            }
            if (!(workflowName.equals(jsEvent.getWorkflowName()))) {
                setAssertValue(false);
                return;
            }
            if (!(workflowNodeName.equals(jsEvent.getWorkflowNodeName()))) {
                setAssertValue(false);
                return;
            }
            String[] wrkflowAdj = workflowAdjacencies.split(" ");
            String[] jswrkflowAdj = jsEvent.getWorkflowAdjacencies().split(" ");
            Arrays.sort(wrkflowAdj);
            Arrays.sort(jswrkflowAdj);
            if (!(Arrays.equals(wrkflowAdj, jswrkflowAdj))) {
                setAssertValue(false);
                return;
            }
            if (!(workflowTags.equals(jsEvent.getWorkflowTags()))) {
                setAssertValue(false);
                return;
            }
            setAssertValue(true);
        }

        private synchronized void setAssertValue(Boolean bool) {
            assertBoolean = bool;
            notify();
        }

        public synchronized boolean getAssertValue() throws InterruptedException {
            while ((assertBoolean) == null) {
                wait();
            } 
            return assertBoolean;
        }
    }

    private static class StubbedJob extends JobImpl {
        // override the init transition
        private final InitTransition initTransition;

        StateMachineFactory<JobImpl, JobStateInternal, JobEventType, JobEvent> localFactory;

        private final StateMachine<JobStateInternal, JobEventType, JobEvent> localStateMachine;

        @Override
        protected StateMachine<JobStateInternal, JobEventType, JobEvent> getStateMachine() {
            return localStateMachine;
        }

        public StubbedJob(JobId jobId, ApplicationAttemptId applicationAttemptId, Configuration conf, EventHandler eventHandler, boolean newApiCommitter, String user, int numSplits, AppContext appContext) {
            super(jobId, applicationAttemptId, conf, eventHandler, null, new JobTokenSecretManager(), new Credentials(), SystemClock.getInstance(), Collections.<TaskId, TaskInfo>emptyMap(), MRAppMetrics.create(), null, newApiCommitter, user, System.currentTimeMillis(), null, appContext, null, null);
            initTransition = TestJobImpl.getInitTransition(numSplits);
            localFactory = // This is abusive.
            stateMachineFactory.addTransition(NEW, EnumSet.of(INITED, FAILED), JOB_INIT, initTransition);
            // This "this leak" is okay because the retained pointer is in an
            // instance variable.
            localStateMachine = localFactory.make(this);
        }
    }

    private static class StubbedOutputCommitter extends OutputCommitter {
        public StubbedOutputCommitter() {
            super();
        }

        @Override
        public void setupJob(JobContext jobContext) throws IOException {
        }

        @Override
        public void setupTask(TaskAttemptContext taskContext) throws IOException {
        }

        @Override
        public boolean needsTaskCommit(TaskAttemptContext taskContext) throws IOException {
            return false;
        }

        @Override
        public void commitTask(TaskAttemptContext taskContext) throws IOException {
        }

        @Override
        public void abortTask(TaskAttemptContext taskContext) throws IOException {
        }
    }

    private static class TestingOutputCommitter extends TestJobImpl.StubbedOutputCommitter {
        CyclicBarrier syncBarrier;

        boolean shouldSucceed;

        public TestingOutputCommitter(CyclicBarrier syncBarrier, boolean shouldSucceed) {
            super();
            this.syncBarrier = syncBarrier;
            this.shouldSucceed = shouldSucceed;
        }

        @Override
        public void commitJob(JobContext jobContext) throws IOException {
            try {
                syncBarrier.await();
            } catch (BrokenBarrierException e) {
            } catch (InterruptedException e) {
            }
            if (!(shouldSucceed)) {
                throw new IOException("forced failure");
            }
        }
    }

    private static class WaitingOutputCommitter extends TestJobImpl.TestingOutputCommitter {
        public WaitingOutputCommitter(CyclicBarrier syncBarrier, boolean shouldSucceed) {
            super(syncBarrier, shouldSucceed);
        }

        @Override
        public void commitJob(JobContext jobContext) throws IOException {
            try {
                syncBarrier.await();
            } catch (BrokenBarrierException e) {
            } catch (InterruptedException e) {
            }
            while (!(Thread.interrupted())) {
                try {
                    synchronized(this) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    break;
                }
            } 
        }
    }
}

