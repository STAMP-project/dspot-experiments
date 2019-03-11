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


import TaskAttemptEventType.TA_RESCHEDULE;
import TaskAttemptState.COMMIT_PENDING;
import TaskAttemptState.FAILED;
import TaskAttemptState.KILLED;
import TaskAttemptState.SUCCEEDED;
import TaskCounter.CPU_MILLISECONDS;
import TaskEventType.T_ATTEMPT_COMMIT_PENDING;
import TaskEventType.T_ATTEMPT_FAILED;
import TaskEventType.T_ATTEMPT_KILLED;
import TaskEventType.T_ATTEMPT_SUCCEEDED;
import TaskState.RUNNING;
import TaskType.MAP;
import TaskType.REDUCE;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Task;
import org.apache.hadoop.mapred.TaskUmbilicalProtocol;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.security.token.JobTokenIdentifier;
import org.apache.hadoop.mapreduce.split.JobSplit.TaskSplitMetaInfo;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptState;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskType;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.TaskAttemptListener;
import org.apache.hadoop.mapreduce.v2.app.job.TaskAttempt;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEventType;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskEventType;
import org.apache.hadoop.mapreduce.v2.app.metrics.MRAppMetrics;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.event.InlineDispatcher;
import org.apache.hadoop.yarn.util.Clock;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static TaskAttemptImpl.EMPTY_COUNTERS;


@SuppressWarnings("rawtypes")
public class TestTaskImpl {
    private static final Logger LOG = LoggerFactory.getLogger(TestTaskImpl.class);

    private JobConf conf;

    private TaskAttemptListener taskAttemptListener;

    private Token<JobTokenIdentifier> jobToken;

    private JobId jobId;

    private Path remoteJobConfFile;

    private Credentials credentials;

    private Clock clock;

    private MRAppMetrics metrics;

    private TaskImpl mockTask;

    private ApplicationId appId;

    private TaskSplitMetaInfo taskSplitMetaInfo;

    private String[] dataLocations = new String[0];

    private AppContext appContext;

    private int startCount = 0;

    private int taskCounter = 0;

    private final int partition = 1;

    private InlineDispatcher dispatcher;

    private TestTaskImpl.MockTaskAttemptEventHandler taskAttemptEventHandler;

    private List<TestTaskImpl.MockTaskAttemptImpl> taskAttempts;

    private class MockTaskImpl extends TaskImpl {
        private int taskAttemptCounter = 0;

        TaskType taskType;

        public MockTaskImpl(JobId jobId, int partition, EventHandler eventHandler, Path remoteJobConfFile, JobConf conf, TaskAttemptListener taskAttemptListener, Token<JobTokenIdentifier> jobToken, Credentials credentials, Clock clock, int startCount, MRAppMetrics metrics, AppContext appContext, TaskType taskType) {
            super(jobId, taskType, partition, eventHandler, remoteJobConfFile, conf, taskAttemptListener, jobToken, credentials, clock, startCount, metrics, appContext);
            this.taskType = taskType;
        }

        @Override
        public TaskType getType() {
            return taskType;
        }

        @Override
        protected TaskAttemptImpl createAttempt() {
            TestTaskImpl.MockTaskAttemptImpl attempt = new TestTaskImpl.MockTaskAttemptImpl(getID(), (++(taskAttemptCounter)), eventHandler, taskAttemptListener, remoteJobConfFile, partition, conf, jobToken, credentials, clock, appContext, taskType);
            taskAttempts.add(attempt);
            return attempt;
        }

        @Override
        protected int getMaxAttempts() {
            return 100;
        }

        @Override
        protected void internalError(TaskEventType type) {
            super.internalError(type);
            Assert.fail(("Internal error: " + type));
        }
    }

    private class MockTaskAttemptImpl extends TaskAttemptImpl {
        private float progress = 0;

        private TaskAttemptState state = TaskAttemptState.NEW;

        boolean rescheduled = false;

        boolean containerAssigned = false;

        private TaskType taskType;

        private Counters attemptCounters = EMPTY_COUNTERS;

        public MockTaskAttemptImpl(TaskId taskId, int id, EventHandler eventHandler, TaskAttemptListener taskAttemptListener, Path jobFile, int partition, JobConf conf, Token<JobTokenIdentifier> jobToken, Credentials credentials, Clock clock, AppContext appContext, TaskType taskType) {
            super(taskId, id, eventHandler, taskAttemptListener, jobFile, partition, conf, dataLocations, jobToken, credentials, clock, appContext);
            this.taskType = taskType;
        }

        public void assignContainer() {
            containerAssigned = true;
        }

        @Override
        boolean isContainerAssigned() {
            return containerAssigned;
        }

        public TaskAttemptId getAttemptId() {
            return getID();
        }

        @Override
        protected Task createRemoteTask() {
            return new TestTaskImpl.MockTask(taskType);
        }

        public float getProgress() {
            return progress;
        }

        public void setProgress(float progress) {
            this.progress = progress;
        }

        public void setState(TaskAttemptState state) {
            this.state = state;
        }

        @Override
        public TaskAttemptState getState() {
            return state;
        }

        public boolean getRescheduled() {
            return this.rescheduled;
        }

        public void setRescheduled(boolean rescheduled) {
            this.rescheduled = rescheduled;
        }

        @Override
        public Counters getCounters() {
            return attemptCounters;
        }

        public void setCounters(Counters counters) {
            attemptCounters = counters;
        }
    }

    private class MockTask extends Task {
        private TaskType taskType;

        MockTask(TaskType taskType) {
            this.taskType = taskType;
        }

        @Override
        public void run(JobConf job, TaskUmbilicalProtocol umbilical) throws IOException, ClassNotFoundException, InterruptedException {
            return;
        }

        @Override
        public boolean isMapTask() {
            return (taskType) == (TaskType.MAP);
        }
    }

    @Test
    public void testInit() {
        TestTaskImpl.LOG.info("--- START: testInit ---");
        mockTask = createMockTask(MAP);
        assertTaskNewState();
        assert (taskAttempts.size()) == 0;
    }

    /**
     * {@link TaskState#NEW}->{@link TaskState#SCHEDULED}
     */
    @Test
    public void testScheduleTask() {
        TestTaskImpl.LOG.info("--- START: testScheduleTask ---");
        mockTask = createMockTask(MAP);
        TaskId taskId = getNewTaskID();
        scheduleTaskAttempt(taskId);
    }

    /**
     * {@link TaskState#SCHEDULED}->{@link TaskState#KILL_WAIT}
     */
    @Test
    public void testKillScheduledTask() {
        TestTaskImpl.LOG.info("--- START: testKillScheduledTask ---");
        mockTask = createMockTask(MAP);
        TaskId taskId = getNewTaskID();
        scheduleTaskAttempt(taskId);
        killTask(taskId);
    }

    /**
     * Kill attempt
     * {@link TaskState#SCHEDULED}->{@link TaskState#SCHEDULED}
     */
    @Test
    public void testKillScheduledTaskAttempt() {
        TestTaskImpl.LOG.info("--- START: testKillScheduledTaskAttempt ---");
        mockTask = createMockTask(MAP);
        TaskId taskId = getNewTaskID();
        scheduleTaskAttempt(taskId);
        killScheduledTaskAttempt(getLastAttempt().getAttemptId(), true);
        Assert.assertEquals(TA_RESCHEDULE, taskAttemptEventHandler.lastTaskAttemptEvent.getType());
    }

    /**
     * Launch attempt
     * {@link TaskState#SCHEDULED}->{@link TaskState#RUNNING}
     */
    @Test
    public void testLaunchTaskAttempt() {
        TestTaskImpl.LOG.info("--- START: testLaunchTaskAttempt ---");
        mockTask = createMockTask(MAP);
        TaskId taskId = getNewTaskID();
        scheduleTaskAttempt(taskId);
        launchTaskAttempt(getLastAttempt().getAttemptId());
    }

    /**
     * Kill running attempt
     * {@link TaskState#RUNNING}->{@link TaskState#RUNNING}
     */
    @Test
    public void testKillRunningTaskAttempt() {
        TestTaskImpl.LOG.info("--- START: testKillRunningTaskAttempt ---");
        mockTask = createMockTask(MAP);
        TaskId taskId = getNewTaskID();
        scheduleTaskAttempt(taskId);
        launchTaskAttempt(getLastAttempt().getAttemptId());
        killRunningTaskAttempt(getLastAttempt().getAttemptId(), true);
        Assert.assertEquals(TA_RESCHEDULE, taskAttemptEventHandler.lastTaskAttemptEvent.getType());
    }

    @Test
    public void testKillSuccessfulTask() {
        TestTaskImpl.LOG.info("--- START: testKillSuccesfulTask ---");
        mockTask = createMockTask(MAP);
        TaskId taskId = getNewTaskID();
        scheduleTaskAttempt(taskId);
        launchTaskAttempt(getLastAttempt().getAttemptId());
        commitTaskAttempt(getLastAttempt().getAttemptId());
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptEvent(getLastAttempt().getAttemptId(), TaskEventType.T_ATTEMPT_SUCCEEDED));
        assertTaskSucceededState();
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskEvent(taskId, TaskEventType.T_KILL));
        assertTaskSucceededState();
    }

    /**
     * Kill map attempt for succeeded map task
     * {@link TaskState#SUCCEEDED}->{@link TaskState#SCHEDULED}
     */
    @Test
    public void testKillAttemptForSuccessfulTask() {
        TestTaskImpl.LOG.info("--- START: testKillAttemptForSuccessfulTask ---");
        mockTask = createMockTask(MAP);
        TaskId taskId = getNewTaskID();
        scheduleTaskAttempt(taskId);
        launchTaskAttempt(getLastAttempt().getAttemptId());
        commitTaskAttempt(getLastAttempt().getAttemptId());
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptEvent(getLastAttempt().getAttemptId(), TaskEventType.T_ATTEMPT_SUCCEEDED));
        assertTaskSucceededState();
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptKilledEvent(getLastAttempt().getAttemptId(), true));
        Assert.assertEquals(TA_RESCHEDULE, taskAttemptEventHandler.lastTaskAttemptEvent.getType());
        assertTaskScheduledState();
    }

    @Test
    public void testTaskProgress() {
        TestTaskImpl.LOG.info("--- START: testTaskProgress ---");
        mockTask = createMockTask(MAP);
        // launch task
        TaskId taskId = getNewTaskID();
        scheduleTaskAttempt(taskId);
        float progress = 0.0F;
        assert (mockTask.getProgress()) == progress;
        launchTaskAttempt(getLastAttempt().getAttemptId());
        // update attempt1
        progress = 50.0F;
        updateLastAttemptProgress(progress);
        assert (mockTask.getProgress()) == progress;
        progress = 100.0F;
        updateLastAttemptProgress(progress);
        assert (mockTask.getProgress()) == progress;
        progress = 0.0F;
        // mark first attempt as killed
        updateLastAttemptState(KILLED);
        assert (mockTask.getProgress()) == progress;
        // kill first attempt
        // should trigger a new attempt
        // as no successful attempts
        killRunningTaskAttempt(getLastAttempt().getAttemptId());
        assert (taskAttempts.size()) == 2;
        assert (mockTask.getProgress()) == 0.0F;
        launchTaskAttempt(getLastAttempt().getAttemptId());
        progress = 50.0F;
        updateLastAttemptProgress(progress);
        assert (mockTask.getProgress()) == progress;
    }

    @Test
    public void testKillDuringTaskAttemptCommit() {
        mockTask = createMockTask(REDUCE);
        TaskId taskId = getNewTaskID();
        scheduleTaskAttempt(taskId);
        launchTaskAttempt(getLastAttempt().getAttemptId());
        updateLastAttemptState(COMMIT_PENDING);
        commitTaskAttempt(getLastAttempt().getAttemptId());
        TaskAttemptId commitAttempt = getLastAttempt().getAttemptId();
        updateLastAttemptState(KILLED);
        killRunningTaskAttempt(commitAttempt);
        Assert.assertFalse(mockTask.canCommit(commitAttempt));
    }

    @Test
    public void testFailureDuringTaskAttemptCommit() {
        mockTask = createMockTask(MAP);
        TaskId taskId = getNewTaskID();
        scheduleTaskAttempt(taskId);
        launchTaskAttempt(getLastAttempt().getAttemptId());
        updateLastAttemptState(COMMIT_PENDING);
        commitTaskAttempt(getLastAttempt().getAttemptId());
        // During the task attempt commit there is an exception which causes
        // the attempt to fail
        updateLastAttemptState(FAILED);
        failRunningTaskAttempt(getLastAttempt().getAttemptId());
        Assert.assertEquals(2, taskAttempts.size());
        updateLastAttemptState(SUCCEEDED);
        commitTaskAttempt(getLastAttempt().getAttemptId());
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptEvent(getLastAttempt().getAttemptId(), TaskEventType.T_ATTEMPT_SUCCEEDED));
        Assert.assertFalse("First attempt should not commit", mockTask.canCommit(taskAttempts.get(0).getAttemptId()));
        Assert.assertTrue("Second attempt should commit", mockTask.canCommit(getLastAttempt().getAttemptId()));
        assertTaskSucceededState();
    }

    @Test
    public void testMapSpeculativeTaskAttemptSucceedsEvenIfFirstFails() {
        mockTask = createMockTask(MAP);
        runSpeculativeTaskAttemptSucceeds(T_ATTEMPT_FAILED);
    }

    @Test
    public void testReduceSpeculativeTaskAttemptSucceedsEvenIfFirstFails() {
        mockTask = createMockTask(REDUCE);
        runSpeculativeTaskAttemptSucceeds(T_ATTEMPT_FAILED);
    }

    @Test
    public void testMapSpeculativeTaskAttemptSucceedsEvenIfFirstIsKilled() {
        mockTask = createMockTask(MAP);
        runSpeculativeTaskAttemptSucceeds(T_ATTEMPT_KILLED);
    }

    @Test
    public void testReduceSpeculativeTaskAttemptSucceedsEvenIfFirstIsKilled() {
        mockTask = createMockTask(REDUCE);
        runSpeculativeTaskAttemptSucceeds(T_ATTEMPT_KILLED);
    }

    @Test
    public void testMultipleTaskAttemptsSucceed() {
        mockTask = createMockTask(MAP);
        runSpeculativeTaskAttemptSucceeds(T_ATTEMPT_SUCCEEDED);
    }

    @Test
    public void testCommitAfterSucceeds() {
        mockTask = createMockTask(REDUCE);
        runSpeculativeTaskAttemptSucceeds(T_ATTEMPT_COMMIT_PENDING);
    }

    @Test
    public void testSpeculativeMapFetchFailure() {
        // Setup a scenario where speculative task wins, first attempt killed
        mockTask = createMockTask(MAP);
        runSpeculativeTaskAttemptSucceeds(T_ATTEMPT_KILLED);
        Assert.assertEquals(2, taskAttempts.size());
        // speculative attempt retroactively fails from fetch failures
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptFailedEvent(taskAttempts.get(1).getAttemptId()));
        assertTaskScheduledState();
        Assert.assertEquals(3, taskAttempts.size());
    }

    @Test
    public void testSpeculativeMapMultipleSucceedFetchFailure() {
        // Setup a scenario where speculative task wins, first attempt succeeds
        mockTask = createMockTask(MAP);
        runSpeculativeTaskAttemptSucceeds(T_ATTEMPT_SUCCEEDED);
        Assert.assertEquals(2, taskAttempts.size());
        // speculative attempt retroactively fails from fetch failures
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptFailedEvent(taskAttempts.get(1).getAttemptId()));
        assertTaskScheduledState();
        Assert.assertEquals(3, taskAttempts.size());
    }

    @Test
    public void testSpeculativeMapFailedFetchFailure() {
        // Setup a scenario where speculative task wins, first attempt succeeds
        mockTask = createMockTask(MAP);
        runSpeculativeTaskAttemptSucceeds(T_ATTEMPT_FAILED);
        Assert.assertEquals(2, taskAttempts.size());
        // speculative attempt retroactively fails from fetch failures
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptFailedEvent(taskAttempts.get(1).getAttemptId()));
        assertTaskScheduledState();
        Assert.assertEquals(3, taskAttempts.size());
    }

    @Test
    public void testFailedTransitions() {
        mockTask = new TestTaskImpl.MockTaskImpl(jobId, partition, dispatcher.getEventHandler(), remoteJobConfFile, conf, taskAttemptListener, jobToken, credentials, clock, startCount, metrics, appContext, TaskType.MAP) {
            @Override
            protected int getMaxAttempts() {
                return 1;
            }
        };
        TaskId taskId = getNewTaskID();
        scheduleTaskAttempt(taskId);
        launchTaskAttempt(getLastAttempt().getAttemptId());
        // add three more speculative attempts
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptEvent(getLastAttempt().getAttemptId(), TaskEventType.T_ADD_SPEC_ATTEMPT));
        launchTaskAttempt(getLastAttempt().getAttemptId());
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptEvent(getLastAttempt().getAttemptId(), TaskEventType.T_ADD_SPEC_ATTEMPT));
        launchTaskAttempt(getLastAttempt().getAttemptId());
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptEvent(getLastAttempt().getAttemptId(), TaskEventType.T_ADD_SPEC_ATTEMPT));
        launchTaskAttempt(getLastAttempt().getAttemptId());
        Assert.assertEquals(4, taskAttempts.size());
        // have the first attempt fail, verify task failed due to no retries
        TestTaskImpl.MockTaskAttemptImpl taskAttempt = taskAttempts.get(0);
        taskAttempt.setState(FAILED);
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptFailedEvent(taskAttempt.getAttemptId()));
        Assert.assertEquals(TaskState.FAILED, mockTask.getState());
        // verify task can no longer be killed
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskEvent(taskId, TaskEventType.T_KILL));
        Assert.assertEquals(TaskState.FAILED, mockTask.getState());
        // verify speculative doesn't launch new tasks
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptEvent(getLastAttempt().getAttemptId(), TaskEventType.T_ADD_SPEC_ATTEMPT));
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptEvent(getLastAttempt().getAttemptId(), TaskEventType.T_ATTEMPT_LAUNCHED));
        Assert.assertEquals(TaskState.FAILED, mockTask.getState());
        Assert.assertEquals(4, taskAttempts.size());
        // verify attempt events from active tasks don't knock task out of FAILED
        taskAttempt = taskAttempts.get(1);
        taskAttempt.setState(COMMIT_PENDING);
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptEvent(taskAttempt.getAttemptId(), TaskEventType.T_ATTEMPT_COMMIT_PENDING));
        Assert.assertEquals(TaskState.FAILED, mockTask.getState());
        taskAttempt.setState(FAILED);
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptFailedEvent(taskAttempt.getAttemptId()));
        Assert.assertEquals(TaskState.FAILED, mockTask.getState());
        taskAttempt = taskAttempts.get(2);
        taskAttempt.setState(SUCCEEDED);
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptEvent(taskAttempt.getAttemptId(), TaskEventType.T_ATTEMPT_SUCCEEDED));
        Assert.assertEquals(TaskState.FAILED, mockTask.getState());
        taskAttempt = taskAttempts.get(3);
        taskAttempt.setState(KILLED);
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptKilledEvent(taskAttempt.getAttemptId(), false));
        Assert.assertEquals(TaskState.FAILED, mockTask.getState());
    }

    private class PartialAttemptEventHandler implements EventHandler {
        @Override
        public void handle(Event event) {
            if (event instanceof TaskAttemptEvent)
                if ((event.getType()) == (TaskAttemptEventType.TA_RESCHEDULE)) {
                    TaskAttempt attempt = mockTask.getAttempt(getTaskAttemptID());
                    ((TestTaskImpl.MockTaskAttemptImpl) (attempt)).setRescheduled(true);
                }

        }
    }

    @Test
    public void testFailedTransitionWithHangingSpeculativeMap() {
        mockTask = new TestTaskImpl.MockTaskImpl(jobId, partition, new TestTaskImpl.PartialAttemptEventHandler(), remoteJobConfFile, conf, taskAttemptListener, jobToken, credentials, clock, startCount, metrics, appContext, TaskType.MAP) {
            @Override
            protected int getMaxAttempts() {
                return 4;
            }
        };
        // start a new task, schedule and launch a new attempt
        TaskId taskId = getNewTaskID();
        scheduleTaskAttempt(taskId);
        launchTaskAttempt(getLastAttempt().getAttemptId());
        // add a speculative attempt(#2), but not launch it
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptEvent(getLastAttempt().getAttemptId(), TaskEventType.T_ADD_SPEC_ATTEMPT));
        // have the first attempt(#1) fail, verify task still running since the
        // max attempts is 4
        TestTaskImpl.MockTaskAttemptImpl taskAttempt = taskAttempts.get(0);
        taskAttempt.setState(FAILED);
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptFailedEvent(taskAttempt.getAttemptId()));
        Assert.assertEquals(RUNNING, mockTask.getState());
        // verify a new attempt(#3) added because the speculative attempt(#2)
        // is hanging
        Assert.assertEquals(3, taskAttempts.size());
        // verify the speculative attempt(#2) is not a rescheduled attempt
        Assert.assertEquals(false, taskAttempts.get(1).getRescheduled());
        // verify the third attempt is a rescheduled attempt
        Assert.assertEquals(true, taskAttempts.get(2).getRescheduled());
        // now launch the latest attempt(#3) and set the internal state to running
        launchTaskAttempt(getLastAttempt().getAttemptId());
        // have the speculative attempt(#2) fail, verify task still since it
        // hasn't reach the max attempts which is 4
        TestTaskImpl.MockTaskAttemptImpl taskAttempt1 = taskAttempts.get(1);
        taskAttempt1.setState(FAILED);
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptFailedEvent(taskAttempt1.getAttemptId()));
        Assert.assertEquals(RUNNING, mockTask.getState());
        // verify there's no new attempt added because of the running attempt(#3)
        Assert.assertEquals(3, taskAttempts.size());
    }

    @Test
    public void testCountersWithSpeculation() {
        mockTask = new TestTaskImpl.MockTaskImpl(jobId, partition, dispatcher.getEventHandler(), remoteJobConfFile, conf, taskAttemptListener, jobToken, credentials, clock, startCount, metrics, appContext, TaskType.MAP) {
            @Override
            protected int getMaxAttempts() {
                return 1;
            }
        };
        TaskId taskId = getNewTaskID();
        scheduleTaskAttempt(taskId);
        launchTaskAttempt(getLastAttempt().getAttemptId());
        updateLastAttemptState(TaskAttemptState.RUNNING);
        TestTaskImpl.MockTaskAttemptImpl baseAttempt = getLastAttempt();
        // add a speculative attempt
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptEvent(getLastAttempt().getAttemptId(), TaskEventType.T_ADD_SPEC_ATTEMPT));
        launchTaskAttempt(getLastAttempt().getAttemptId());
        updateLastAttemptState(TaskAttemptState.RUNNING);
        TestTaskImpl.MockTaskAttemptImpl specAttempt = getLastAttempt();
        Assert.assertEquals(2, taskAttempts.size());
        Counters specAttemptCounters = new Counters();
        Counter cpuCounter = specAttemptCounters.findCounter(CPU_MILLISECONDS);
        cpuCounter.setValue(1000);
        specAttempt.setCounters(specAttemptCounters);
        // have the spec attempt succeed but second attempt at 1.0 progress as well
        commitTaskAttempt(specAttempt.getAttemptId());
        specAttempt.setProgress(1.0F);
        specAttempt.setState(SUCCEEDED);
        mockTask.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskTAttemptEvent(specAttempt.getAttemptId(), TaskEventType.T_ATTEMPT_SUCCEEDED));
        Assert.assertEquals(TaskState.SUCCEEDED, mockTask.getState());
        baseAttempt.setProgress(1.0F);
        Counters taskCounters = mockTask.getCounters();
        Assert.assertEquals("wrong counters for task", specAttemptCounters, taskCounters);
    }

    public static class MockTaskAttemptEventHandler implements EventHandler {
        public TaskAttemptEvent lastTaskAttemptEvent;

        @Override
        public void handle(Event event) {
            if (event instanceof TaskAttemptEvent) {
                lastTaskAttemptEvent = ((TaskAttemptEvent) (event));
            }
        }
    }
}

