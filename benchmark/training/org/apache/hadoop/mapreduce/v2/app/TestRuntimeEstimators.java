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


import TaskEventType.T_ADD_SPEC_ATTEMPT;
import TaskType.MAP;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.TaskCompletionEvent;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.JobACL;
import org.apache.hadoop.mapreduce.v2.api.records.AMInfo;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.JobReport;
import org.apache.hadoop.mapreduce.v2.api.records.JobState;
import org.apache.hadoop.mapreduce.v2.api.records.Phase;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptCompletionEvent;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptReport;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptState;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskReport;
import org.apache.hadoop.mapreduce.v2.api.records.TaskState;
import org.apache.hadoop.mapreduce.v2.api.records.TaskType;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.job.Task;
import org.apache.hadoop.mapreduce.v2.app.job.TaskAttempt;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskEvent;
import org.apache.hadoop.mapreduce.v2.app.speculate.DefaultSpeculator;
import org.apache.hadoop.mapreduce.v2.app.speculate.ExponentiallySmoothedTaskRuntimeEstimator;
import org.apache.hadoop.mapreduce.v2.app.speculate.LegacyTaskRuntimeEstimator;
import org.apache.hadoop.mapreduce.v2.app.speculate.SpeculatorEvent;
import org.apache.hadoop.mapreduce.v2.app.speculate.TaskRuntimeEstimator;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authorize.AccessControlList;
import org.apache.hadoop.service.CompositeService;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.security.client.ClientToAMTokenSecretManager;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.util.ControlledClock;
import org.apache.hadoop.yarn.util.SystemClock;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class TestRuntimeEstimators {
    private static int INITIAL_NUMBER_FREE_SLOTS = 600;

    private static int MAP_SLOT_REQUIREMENT = 3;

    // this has to be at least as much as map slot requirement
    private static int REDUCE_SLOT_REQUIREMENT = 4;

    private static int MAP_TASKS = 200;

    private static int REDUCE_TASKS = 150;

    ControlledClock clock;

    Job myJob;

    AppContext myAppContext;

    private static final Logger LOG = LoggerFactory.getLogger(TestRuntimeEstimators.class);

    private final AtomicInteger slotsInUse = new AtomicInteger(0);

    AsyncDispatcher dispatcher;

    DefaultSpeculator speculator;

    TaskRuntimeEstimator estimator;

    // This is a huge kluge.  The real implementations have a decent approach
    private final AtomicInteger completedMaps = new AtomicInteger(0);

    private final AtomicInteger completedReduces = new AtomicInteger(0);

    private final AtomicInteger successfulSpeculations = new AtomicInteger(0);

    private final AtomicLong taskTimeSavedBySpeculation = new AtomicLong(0L);

    private final RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    @Test
    public void testLegacyEstimator() throws Exception {
        TaskRuntimeEstimator specificEstimator = new LegacyTaskRuntimeEstimator();
        coreTestEstimator(specificEstimator, 3);
    }

    @Test
    public void testExponentialEstimator() throws Exception {
        TaskRuntimeEstimator specificEstimator = new ExponentiallySmoothedTaskRuntimeEstimator();
        coreTestEstimator(specificEstimator, 3);
    }

    class SpeculationRequestEventHandler implements EventHandler<TaskEvent> {
        @Override
        public void handle(TaskEvent event) {
            TaskId taskID = event.getTaskID();
            Task task = myJob.getTask(taskID);
            Assert.assertEquals("Wrong type event", T_ADD_SPEC_ATTEMPT, event.getType());
            System.out.println(("SpeculationRequestEventHandler.handle adds a speculation task for " + taskID));
            addAttempt(task);
        }
    }

    class MyTaskImpl implements Task {
        private final TaskId taskID;

        private final Map<TaskAttemptId, TaskAttempt> attempts = new ConcurrentHashMap<TaskAttemptId, TaskAttempt>(4);

        MyTaskImpl(JobId jobID, int index, TaskType type) {
            taskID = recordFactory.newRecordInstance(TaskId.class);
            taskID.setId(index);
            taskID.setTaskType(type);
            taskID.setJobId(jobID);
        }

        void addAttempt() {
            TaskAttempt taskAttempt = new TestRuntimeEstimators.MyTaskAttemptImpl(taskID, attempts.size(), clock);
            TaskAttemptId taskAttemptID = taskAttempt.getID();
            attempts.put(taskAttemptID, taskAttempt);
            System.out.println(("TLTRE.MyTaskImpl.addAttempt " + (getID())));
            SpeculatorEvent event = new SpeculatorEvent(taskID, (+1));
            dispatcher.getEventHandler().handle(event);
        }

        @Override
        public TaskId getID() {
            return taskID;
        }

        @Override
        public TaskReport getReport() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Counters getCounters() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public float getProgress() {
            float result = 0.0F;
            for (TaskAttempt attempt : attempts.values()) {
                result = Math.max(result, attempt.getProgress());
            }
            return result;
        }

        @Override
        public TaskType getType() {
            return taskID.getTaskType();
        }

        @Override
        public Map<TaskAttemptId, TaskAttempt> getAttempts() {
            Map<TaskAttemptId, TaskAttempt> result = new HashMap<TaskAttemptId, TaskAttempt>(attempts.size());
            result.putAll(attempts);
            return result;
        }

        @Override
        public TaskAttempt getAttempt(TaskAttemptId attemptID) {
            return attempts.get(attemptID);
        }

        @Override
        public boolean isFinished() {
            for (TaskAttempt attempt : attempts.values()) {
                if ((attempt.getState()) == (TaskAttemptState.SUCCEEDED)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canCommit(TaskAttemptId taskAttemptID) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TaskState getState() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class MyJobImpl implements Job {
        private final JobId jobID;

        private final Map<TaskId, Task> allTasks = new HashMap<TaskId, Task>();

        private final Map<TaskId, Task> mapTasks = new HashMap<TaskId, Task>();

        private final Map<TaskId, Task> reduceTasks = new HashMap<TaskId, Task>();

        MyJobImpl(JobId jobID, int numMaps, int numReduces) {
            this.jobID = jobID;
            for (int i = 0; i < numMaps; ++i) {
                Task newTask = new TestRuntimeEstimators.MyTaskImpl(jobID, i, TaskType.MAP);
                mapTasks.put(newTask.getID(), newTask);
                allTasks.put(newTask.getID(), newTask);
            }
            for (int i = 0; i < numReduces; ++i) {
                Task newTask = new TestRuntimeEstimators.MyTaskImpl(jobID, i, TaskType.REDUCE);
                reduceTasks.put(newTask.getID(), newTask);
                allTasks.put(newTask.getID(), newTask);
            }
            // give every task an attempt
            for (Task task : allTasks.values()) {
                TestRuntimeEstimators.MyTaskImpl myTaskImpl = ((TestRuntimeEstimators.MyTaskImpl) (task));
                myTaskImpl.addAttempt();
            }
        }

        @Override
        public JobId getID() {
            return jobID;
        }

        @Override
        public JobState getState() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public JobReport getReport() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public float getProgress() {
            return 0;
        }

        @Override
        public Counters getAllCounters() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Map<TaskId, Task> getTasks() {
            return allTasks;
        }

        @Override
        public Map<TaskId, Task> getTasks(TaskType taskType) {
            return taskType == (TaskType.MAP) ? mapTasks : reduceTasks;
        }

        @Override
        public Task getTask(TaskId taskID) {
            return allTasks.get(taskID);
        }

        @Override
        public List<String> getDiagnostics() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getCompletedMaps() {
            return completedMaps.get();
        }

        @Override
        public int getCompletedReduces() {
            return completedReduces.get();
        }

        @Override
        public TaskAttemptCompletionEvent[] getTaskAttemptCompletionEvents(int fromEventId, int maxEvents) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TaskCompletionEvent[] getMapAttemptCompletionEvents(int startIndex, int maxEvents) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getQueueName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getTotalMaps() {
            return mapTasks.size();
        }

        @Override
        public int getTotalReduces() {
            return reduceTasks.size();
        }

        @Override
        public boolean isUber() {
            return false;
        }

        @Override
        public boolean checkAccess(UserGroupInformation callerUGI, JobACL jobOperation) {
            return true;
        }

        @Override
        public String getUserName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Path getConfFile() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Map<JobACL, AccessControlList> getJobACLs() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<AMInfo> getAMInfos() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Configuration loadConfFile() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setQueueName(String queueName) {
            // do nothing
        }

        @Override
        public void setJobPriority(Priority priority) {
            // do nothing
        }

        @Override
        public int getFailedMaps() {
            return 0;
        }

        @Override
        public int getFailedReduces() {
            return 0;
        }

        @Override
        public int getKilledMaps() {
            return 0;
        }

        @Override
        public int getKilledReduces() {
            return 0;
        }
    }

    /* We follow the pattern of the real XxxImpl .  We create a job and initialize
    it with a full suite of tasks which in turn have one attempt each in the
    NEW state.  Attempts transition only from NEW to RUNNING to SUCCEEDED .
     */
    class MyTaskAttemptImpl implements TaskAttempt {
        private final TaskAttemptId myAttemptID;

        long startMockTime = Long.MIN_VALUE;

        long shuffleCompletedTime = Long.MAX_VALUE;

        TaskAttemptState overridingState = TaskAttemptState.NEW;

        MyTaskAttemptImpl(TaskId taskID, int index, Clock clock) {
            myAttemptID = recordFactory.newRecordInstance(TaskAttemptId.class);
            myAttemptID.setId(index);
            myAttemptID.setTaskId(taskID);
        }

        void startUp() {
            startMockTime = clock.getTime();
            overridingState = null;
            slotsInUse.addAndGet(taskTypeSlots(myAttemptID.getTaskId().getTaskType()));
            System.out.println(("TLTRE.MyTaskAttemptImpl.startUp starting " + (getID())));
            SpeculatorEvent event = new SpeculatorEvent(getID().getTaskId(), (-1));
            dispatcher.getEventHandler().handle(event);
        }

        @Override
        public NodeId getNodeId() throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }

        @Override
        public TaskAttemptId getID() {
            return myAttemptID;
        }

        @Override
        public TaskAttemptReport getReport() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public List<String> getDiagnostics() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Counters getCounters() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public int getShufflePort() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        private float getCodeRuntime() {
            int taskIndex = myAttemptID.getTaskId().getId();
            int attemptIndex = myAttemptID.getId();
            float result = 200.0F;
            switch (taskIndex % 4) {
                case 0 :
                    if (((taskIndex % 40) == 0) && (attemptIndex == 0)) {
                        result = 600.0F;
                        break;
                    }
                    break;
                case 2 :
                    break;
                case 1 :
                    result = 150.0F;
                    break;
                case 3 :
                    result = 250.0F;
                    break;
            }
            return result;
        }

        private float getMapProgress() {
            float runtime = getCodeRuntime();
            return Math.min((((float) ((clock.getTime()) - (startMockTime))) / (runtime * 1000.0F)), 1.0F);
        }

        private float getReduceProgress() {
            Job job = myAppContext.getJob(myAttemptID.getTaskId().getJobId());
            float runtime = getCodeRuntime();
            Collection<Task> allMapTasks = job.getTasks(MAP).values();
            int numberMaps = allMapTasks.size();
            int numberDoneMaps = 0;
            for (Task mapTask : allMapTasks) {
                if (mapTask.isFinished()) {
                    ++numberDoneMaps;
                }
            }
            if (numberMaps == numberDoneMaps) {
                shuffleCompletedTime = Math.min(shuffleCompletedTime, clock.getTime());
                return Math.min(((((float) ((clock.getTime()) - (shuffleCompletedTime))) / (runtime * 2000.0F)) + 0.5F), 1.0F);
            } else {
                return (((float) (numberDoneMaps)) / numberMaps) * 0.5F;
            }
        }

        // we compute progress from time and an algorithm now
        @Override
        public float getProgress() {
            if ((overridingState) == (TaskAttemptState.NEW)) {
                return 0.0F;
            }
            return (myAttemptID.getTaskId().getTaskType()) == (TaskType.MAP) ? getMapProgress() : getReduceProgress();
        }

        @Override
        public Phase getPhase() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public TaskAttemptState getState() {
            if ((overridingState) != null) {
                return overridingState;
            }
            TaskAttemptState result = ((getProgress()) < 1.0F) ? TaskAttemptState.RUNNING : TaskAttemptState.SUCCEEDED;
            if (result == (TaskAttemptState.SUCCEEDED)) {
                overridingState = TaskAttemptState.SUCCEEDED;
                System.out.println((("MyTaskAttemptImpl.getState() -- attempt " + (myAttemptID)) + " finished."));
                slotsInUse.addAndGet((-(taskTypeSlots(myAttemptID.getTaskId().getTaskType()))));
                ((myAttemptID.getTaskId().getTaskType()) == (TaskType.MAP) ? completedMaps : completedReduces).getAndIncrement();
                // check for a spectacularly successful speculation
                TaskId taskID = myAttemptID.getTaskId();
                Task task = myJob.getTask(taskID);
                for (TaskAttempt otherAttempt : task.getAttempts().values()) {
                    if ((otherAttempt != (this)) && ((otherAttempt.getState()) == (TaskAttemptState.RUNNING))) {
                        // we had two instances running.  Try to determine how much
                        // we might have saved by speculation
                        if ((getID().getId()) > (otherAttempt.getID().getId())) {
                            // the speculation won
                            successfulSpeculations.getAndIncrement();
                            float hisProgress = otherAttempt.getProgress();
                            long hisStartTime = ((TestRuntimeEstimators.MyTaskAttemptImpl) (otherAttempt)).startMockTime;
                            System.out.println((((((((("TLTRE:A speculation finished at time " + (clock.getTime())) + ".  The stalled attempt is at ") + (hisProgress * 100.0)) + "% progress, and it started at ") + hisStartTime) + ", which is ") + ((clock.getTime()) - hisStartTime)) + " ago."));
                            long originalTaskEndEstimate = hisStartTime + (estimator.estimatedRuntime(otherAttempt.getID()));
                            System.out.println(((("TLTRE: We would have expected the original attempt to take " + (estimator.estimatedRuntime(otherAttempt.getID()))) + ", finishing at ") + originalTaskEndEstimate));
                            long estimatedSavings = originalTaskEndEstimate - (clock.getTime());
                            taskTimeSavedBySpeculation.addAndGet(estimatedSavings);
                            System.out.println(("TLTRE: The task is " + (task.getID())));
                            slotsInUse.addAndGet((-(taskTypeSlots(myAttemptID.getTaskId().getTaskType()))));
                            ((TestRuntimeEstimators.MyTaskAttemptImpl) (otherAttempt)).overridingState = TaskAttemptState.KILLED;
                        } else {
                            System.out.println(("TLTRE: The normal attempt beat the speculation in " + (task.getID())));
                        }
                    }
                }
            }
            return result;
        }

        @Override
        public boolean isFinished() {
            return (getProgress()) == 1.0F;
        }

        @Override
        public ContainerId getAssignedContainerID() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getNodeHttpAddress() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getNodeRackName() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public long getLaunchTime() {
            return startMockTime;
        }

        @Override
        public long getFinishTime() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public long getShuffleFinishTime() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public long getSortFinishTime() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getAssignedContainerMgrAddress() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class MyAppMaster extends CompositeService {
        final Clock clock;

        public MyAppMaster(Clock clock) {
            super(TestRuntimeEstimators.MyAppMaster.class.getName());
            if (clock == null) {
                clock = SystemClock.getInstance();
            }
            this.clock = clock;
            TestRuntimeEstimators.LOG.info("Created MyAppMaster");
        }
    }

    class MyAppContext implements AppContext {
        private final ApplicationAttemptId myAppAttemptID;

        private final ApplicationId myApplicationID;

        private final JobId myJobID;

        private final Map<JobId, Job> allJobs;

        MyAppContext(int numberMaps, int numberReduces) {
            myApplicationID = ApplicationId.newInstance(clock.getTime(), 1);
            myAppAttemptID = ApplicationAttemptId.newInstance(myApplicationID, 0);
            myJobID = recordFactory.newRecordInstance(JobId.class);
            myJobID.setAppId(myApplicationID);
            Job myJob = new TestRuntimeEstimators.MyJobImpl(myJobID, numberMaps, numberReduces);
            allJobs = Collections.singletonMap(myJobID, myJob);
        }

        @Override
        public ApplicationAttemptId getApplicationAttemptId() {
            return myAppAttemptID;
        }

        @Override
        public ApplicationId getApplicationID() {
            return myApplicationID;
        }

        @Override
        public Job getJob(JobId jobID) {
            return allJobs.get(jobID);
        }

        @Override
        public Map<JobId, Job> getAllJobs() {
            return allJobs;
        }

        @Override
        public EventHandler<Event> getEventHandler() {
            return dispatcher.getEventHandler();
        }

        @Override
        public CharSequence getUser() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Clock getClock() {
            return clock;
        }

        @Override
        public String getApplicationName() {
            return null;
        }

        @Override
        public long getStartTime() {
            return 0;
        }

        @Override
        public ClusterInfo getClusterInfo() {
            return new ClusterInfo();
        }

        @Override
        public Set<String> getBlacklistedNodes() {
            return null;
        }

        @Override
        public ClientToAMTokenSecretManager getClientToAMTokenSecretManager() {
            return null;
        }

        @Override
        public boolean isLastAMRetry() {
            return false;
        }

        @Override
        public boolean hasSuccessfullyUnregistered() {
            // bogus - Not Required
            return true;
        }

        @Override
        public String getNMHostname() {
            // bogus - Not Required
            return null;
        }

        @Override
        public TaskAttemptFinishingMonitor getTaskAttemptFinishingMonitor() {
            return null;
        }

        @Override
        public String getHistoryUrl() {
            return null;
        }

        @Override
        public void setHistoryUrl(String historyUrl) {
            return;
        }
    }
}

