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
package org.apache.hadoop.mapred;


import EnumCounter.CHECKPOINT_BYTES;
import EnumCounter.CHECKPOINT_MS;
import MRJobConfig.TASK_EXIT_TIMEOUT;
import MRJobConfig.TASK_EXIT_TIMEOUT_DEFAULT;
import MRJobConfig.TASK_PREEMPTION;
import MRJobConfig.TASK_TIMEOUT_CHECK_INTERVAL_MS;
import Phase.REDUCE;
import Phase.SHUFFLE;
import Phase.SORT;
import TaskAttemptCompletionEventStatus.FAILED;
import TaskAttemptCompletionEventStatus.OBSOLETE;
import TaskAttemptCompletionEventStatus.SUCCEEDED;
import TaskStatus.State;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.Counters.Counter;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.checkpoint.CheckpointID;
import org.apache.hadoop.mapreduce.checkpoint.TaskCheckpointID;
import org.apache.hadoop.mapreduce.security.token.JobTokenSecretManager;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.Phase;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptCompletionEvent;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.TaskHeartbeatHandler;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptStatusUpdateEvent;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptStatusUpdateEvent.TaskAttemptStatus;
import org.apache.hadoop.mapreduce.v2.app.rm.RMHeartbeatHandler;
import org.apache.hadoop.mapreduce.v2.app.rm.preemption.AMPreemptionPolicy;
import org.apache.hadoop.mapreduce.v2.app.rm.preemption.CheckpointAMPreemptionPolicy;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.util.ControlledClock;
import org.apache.hadoop.yarn.util.SystemClock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests the behavior of TaskAttemptListenerImpl.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestTaskAttemptListenerImpl {
    private static final String ATTEMPT1_ID = "attempt_123456789012_0001_m_000001_0";

    private static final String ATTEMPT2_ID = "attempt_123456789012_0001_m_000002_0";

    private static final TaskAttemptId TASKATTEMPTID1 = TypeConverter.toYarn(TaskAttemptID.forName(TestTaskAttemptListenerImpl.ATTEMPT1_ID));

    private static final TaskAttemptId TASKATTEMPTID2 = TypeConverter.toYarn(TaskAttemptID.forName(TestTaskAttemptListenerImpl.ATTEMPT2_ID));

    @Mock
    private AppContext appCtx;

    @Mock
    private JobTokenSecretManager secret;

    @Mock
    private RMHeartbeatHandler rmHeartbeatHandler;

    @Mock
    private TaskHeartbeatHandler hbHandler;

    @Mock
    private Dispatcher dispatcher;

    @Mock
    private Task task;

    @SuppressWarnings("rawtypes")
    @Mock
    private EventHandler<Event> ea;

    @SuppressWarnings("rawtypes")
    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    private CheckpointAMPreemptionPolicy policy;

    private JVMId id;

    private WrappedJvmID wid;

    private TaskAttemptID attemptID;

    private TaskAttemptId attemptId;

    private ReduceTaskStatus firstReduceStatus;

    private ReduceTaskStatus secondReduceStatus;

    private ReduceTaskStatus thirdReduceStatus;

    private TestTaskAttemptListenerImpl.MockTaskAttemptListenerImpl listener;

    public static class MockTaskAttemptListenerImpl extends TaskAttemptListenerImpl {
        public MockTaskAttemptListenerImpl(AppContext context, JobTokenSecretManager jobTokenSecretManager, RMHeartbeatHandler rmHeartbeatHandler, AMPreemptionPolicy policy) {
            super(context, jobTokenSecretManager, rmHeartbeatHandler, policy);
        }

        public MockTaskAttemptListenerImpl(AppContext context, JobTokenSecretManager jobTokenSecretManager, RMHeartbeatHandler rmHeartbeatHandler, TaskHeartbeatHandler hbHandler, AMPreemptionPolicy policy) {
            super(context, jobTokenSecretManager, rmHeartbeatHandler, policy);
            this.taskHeartbeatHandler = hbHandler;
        }

        @Override
        protected void registerHeartbeatHandler(Configuration conf) {
            // Empty
        }

        @Override
        protected void startRpcServer() {
            // Empty
        }

        @Override
        protected void stopRpcServer() {
            // Empty
        }
    }

    @Test(timeout = 5000)
    public void testGetTask() throws IOException {
        configureMocks();
        startListener(false);
        // Verify ask before registration.
        // The JVM ID has not been registered yet so we should kill it.
        JvmContext context = new JvmContext();
        context.jvmId = id;
        JvmTask result = listener.getTask(context);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.shouldDie);
        // Verify ask after registration but before launch.
        // Don't kill, should be null.
        // Now put a task with the ID
        listener.registerPendingTask(task, wid);
        result = listener.getTask(context);
        Assert.assertNull(result);
        // Unregister for more testing.
        listener.unregister(attemptId, wid);
        // Verify ask after registration and launch
        // Now put a task with the ID
        listener.registerPendingTask(task, wid);
        listener.registerLaunchedTask(attemptId, wid);
        Mockito.verify(hbHandler).register(attemptId);
        result = listener.getTask(context);
        Assert.assertNotNull(result);
        Assert.assertFalse(result.shouldDie);
        // Don't unregister yet for more testing.
        // Verify that if we call it again a second time we are told to die.
        result = listener.getTask(context);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.shouldDie);
        listener.unregister(attemptId, wid);
        // Verify after unregistration.
        result = listener.getTask(context);
        Assert.assertNotNull(result);
        Assert.assertTrue(result.shouldDie);
        // test JVMID
        JVMId jvmid = JVMId.forName("jvm_001_002_m_004");
        Assert.assertNotNull(jvmid);
        try {
            JVMId.forName("jvm_001_002_m_004_006");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertEquals(e.getMessage(), "TaskId string : jvm_001_002_m_004_006 is not properly formed");
        }
    }

    @Test(timeout = 5000)
    public void testJVMId() {
        JVMId jvmid = new JVMId("test", 1, true, 2);
        JVMId jvmid1 = JVMId.forName("jvm_test_0001_m_000002");
        // test compare methot should be the same
        Assert.assertEquals(0, jvmid.compareTo(jvmid1));
    }

    @Test(timeout = 10000)
    public void testGetMapCompletionEvents() throws IOException {
        TaskAttemptCompletionEvent[] empty = new TaskAttemptCompletionEvent[]{  };
        TaskAttemptCompletionEvent[] taskEvents = new TaskAttemptCompletionEvent[]{ TestTaskAttemptListenerImpl.createTce(0, true, OBSOLETE), TestTaskAttemptListenerImpl.createTce(1, false, FAILED), TestTaskAttemptListenerImpl.createTce(2, true, SUCCEEDED), TestTaskAttemptListenerImpl.createTce(3, false, FAILED) };
        TaskAttemptCompletionEvent[] mapEvents = new TaskAttemptCompletionEvent[]{ taskEvents[0], taskEvents[2] };
        Job mockJob = Mockito.mock(Job.class);
        Mockito.when(mockJob.getTaskAttemptCompletionEvents(0, 100)).thenReturn(taskEvents);
        Mockito.when(mockJob.getTaskAttemptCompletionEvents(0, 2)).thenReturn(Arrays.copyOfRange(taskEvents, 0, 2));
        Mockito.when(mockJob.getTaskAttemptCompletionEvents(2, 100)).thenReturn(Arrays.copyOfRange(taskEvents, 2, 4));
        Mockito.when(mockJob.getMapAttemptCompletionEvents(0, 100)).thenReturn(TypeConverter.fromYarn(mapEvents));
        Mockito.when(mockJob.getMapAttemptCompletionEvents(0, 2)).thenReturn(TypeConverter.fromYarn(mapEvents));
        Mockito.when(mockJob.getMapAttemptCompletionEvents(2, 100)).thenReturn(TypeConverter.fromYarn(empty));
        configureMocks();
        Mockito.when(appCtx.getJob(ArgumentMatchers.any(JobId.class))).thenReturn(mockJob);
        listener = new TestTaskAttemptListenerImpl.MockTaskAttemptListenerImpl(appCtx, secret, rmHeartbeatHandler, policy) {
            @Override
            protected void registerHeartbeatHandler(Configuration conf) {
                taskHeartbeatHandler = hbHandler;
            }
        };
        Configuration conf = new Configuration();
        listener.init(conf);
        start();
        JobID jid = new JobID("12345", 1);
        TaskAttemptID tid = new TaskAttemptID("12345", 1, TaskType.REDUCE, 1, 0);
        MapTaskCompletionEventsUpdate update = listener.getMapCompletionEvents(jid, 0, 100, tid);
        Assert.assertEquals(2, update.events.length);
        update = listener.getMapCompletionEvents(jid, 0, 2, tid);
        Assert.assertEquals(2, update.events.length);
        update = listener.getMapCompletionEvents(jid, 2, 100, tid);
        Assert.assertEquals(0, update.events.length);
    }

    @Test(timeout = 10000)
    public void testCommitWindow() throws IOException {
        SystemClock clock = SystemClock.getInstance();
        configureMocks();
        org.apache.hadoop.mapreduce.v2.app.job.Task mockTask = Mockito.mock(org.apache.hadoop.mapreduce.v2.app.job.Task.class);
        Mockito.when(mockTask.canCommit(ArgumentMatchers.any(TaskAttemptId.class))).thenReturn(true);
        Job mockJob = Mockito.mock(Job.class);
        Mockito.when(mockJob.getTask(ArgumentMatchers.any(TaskId.class))).thenReturn(mockTask);
        Mockito.when(appCtx.getJob(ArgumentMatchers.any(JobId.class))).thenReturn(mockJob);
        Mockito.when(appCtx.getClock()).thenReturn(clock);
        listener = new TestTaskAttemptListenerImpl.MockTaskAttemptListenerImpl(appCtx, secret, rmHeartbeatHandler, policy) {
            @Override
            protected void registerHeartbeatHandler(Configuration conf) {
                taskHeartbeatHandler = hbHandler;
            }
        };
        Configuration conf = new Configuration();
        listener.init(conf);
        start();
        // verify commit not allowed when RM heartbeat has not occurred recently
        TaskAttemptID tid = new TaskAttemptID("12345", 1, TaskType.REDUCE, 1, 0);
        boolean canCommit = listener.canCommit(tid);
        Assert.assertFalse(canCommit);
        Mockito.verify(mockTask, Mockito.never()).canCommit(ArgumentMatchers.any(TaskAttemptId.class));
        // verify commit allowed when RM heartbeat is recent
        Mockito.when(rmHeartbeatHandler.getLastHeartbeatTime()).thenReturn(clock.getTime());
        canCommit = listener.canCommit(tid);
        Assert.assertTrue(canCommit);
        Mockito.verify(mockTask, Mockito.times(1)).canCommit(ArgumentMatchers.any(TaskAttemptId.class));
    }

    @Test
    public void testCheckpointIDTracking() throws IOException, InterruptedException {
        configureMocks();
        listener = new TestTaskAttemptListenerImpl.MockTaskAttemptListenerImpl(appCtx, secret, rmHeartbeatHandler, policy) {
            @Override
            protected void registerHeartbeatHandler(Configuration conf) {
                taskHeartbeatHandler = hbHandler;
            }
        };
        Configuration conf = new Configuration();
        conf.setBoolean(TASK_PREEMPTION, true);
        // conf.setBoolean("preemption.reduce", true);
        listener.init(conf);
        start();
        TaskAttemptID tid = new TaskAttemptID("12345", 1, TaskType.REDUCE, 1, 0);
        List<Path> partialOut = new ArrayList<Path>();
        partialOut.add(new Path("/prev1"));
        partialOut.add(new Path("/prev2"));
        Counters counters = Mockito.mock(Counters.class);
        final long CBYTES = (64L * 1024) * 1024;
        final long CTIME = 4344L;
        final Path CLOC = new Path("/test/1");
        Counter cbytes = Mockito.mock(Counter.class);
        Mockito.when(cbytes.getValue()).thenReturn(CBYTES);
        Counter ctime = Mockito.mock(Counter.class);
        Mockito.when(ctime.getValue()).thenReturn(CTIME);
        Mockito.when(counters.findCounter(ArgumentMatchers.eq(CHECKPOINT_BYTES))).thenReturn(cbytes);
        Mockito.when(counters.findCounter(ArgumentMatchers.eq(CHECKPOINT_MS))).thenReturn(ctime);
        // propagating a taskstatus that contains a checkpoint id
        TaskCheckpointID incid = new TaskCheckpointID(new org.apache.hadoop.mapreduce.checkpoint.FSCheckpointID(CLOC), partialOut, counters);
        listener.setCheckpointID(org.apache.hadoop.mapred.TaskID.downgrade(tid.getTaskID()), incid);
        // and try to get it back
        CheckpointID outcid = listener.getCheckpointID(tid.getTaskID());
        TaskCheckpointID tcid = ((TaskCheckpointID) (outcid));
        Assert.assertEquals(CBYTES, tcid.getCheckpointBytes());
        Assert.assertEquals(CTIME, tcid.getCheckpointTime());
        Assert.assertTrue(partialOut.containsAll(tcid.getPartialCommittedOutput()));
        Assert.assertTrue(tcid.getPartialCommittedOutput().containsAll(partialOut));
        // assert it worked
        assert outcid == incid;
    }

    @Test
    public void testStatusUpdateProgress() throws IOException, InterruptedException {
        configureMocks();
        startListener(true);
        Mockito.verify(hbHandler).register(attemptId);
        // make sure a ping doesn't report progress
        AMFeedback feedback = listener.statusUpdate(attemptID, null);
        Assert.assertTrue(feedback.getTaskFound());
        Mockito.verify(hbHandler, Mockito.never()).progressing(ArgumentMatchers.eq(attemptId));
        // make sure a status update does report progress
        MapTaskStatus mockStatus = new MapTaskStatus(attemptID, 0.0F, 1, State.RUNNING, "", "RUNNING", "", Phase.MAP, new Counters());
        feedback = listener.statusUpdate(attemptID, mockStatus);
        Assert.assertTrue(feedback.getTaskFound());
        Mockito.verify(hbHandler).progressing(ArgumentMatchers.eq(attemptId));
    }

    @Test
    public void testSingleStatusUpdate() throws IOException, InterruptedException {
        configureMocks();
        startListener(true);
        listener.statusUpdate(attemptID, firstReduceStatus);
        Mockito.verify(ea).handle(eventCaptor.capture());
        TaskAttemptStatusUpdateEvent updateEvent = ((TaskAttemptStatusUpdateEvent) (eventCaptor.getValue()));
        TaskAttemptStatus status = updateEvent.getTaskAttemptStatusRef().get();
        Assert.assertTrue(status.fetchFailedMaps.contains(TestTaskAttemptListenerImpl.TASKATTEMPTID1));
        Assert.assertEquals(1, status.fetchFailedMaps.size());
        Assert.assertEquals(SHUFFLE, status.phase);
    }

    @Test
    public void testStatusUpdateEventCoalescing() throws IOException, InterruptedException {
        configureMocks();
        startListener(true);
        listener.statusUpdate(attemptID, firstReduceStatus);
        listener.statusUpdate(attemptID, secondReduceStatus);
        Mockito.verify(ea).handle(ArgumentMatchers.any(Event.class));
        ConcurrentMap<TaskAttemptId, AtomicReference<TaskAttemptStatus>> attemptIdToStatus = getAttemptIdToStatus();
        TaskAttemptStatus status = attemptIdToStatus.get(attemptId).get();
        Assert.assertTrue(status.fetchFailedMaps.contains(TestTaskAttemptListenerImpl.TASKATTEMPTID1));
        Assert.assertTrue(status.fetchFailedMaps.contains(TestTaskAttemptListenerImpl.TASKATTEMPTID2));
        Assert.assertEquals(2, status.fetchFailedMaps.size());
        Assert.assertEquals(SORT, status.phase);
    }

    @Test
    public void testCoalescedStatusUpdatesCleared() throws IOException, InterruptedException {
        // First two events are coalesced, the third is not
        configureMocks();
        startListener(true);
        listener.statusUpdate(attemptID, firstReduceStatus);
        listener.statusUpdate(attemptID, secondReduceStatus);
        ConcurrentMap<TaskAttemptId, AtomicReference<TaskAttemptStatus>> attemptIdToStatus = getAttemptIdToStatus();
        attemptIdToStatus.get(attemptId).set(null);
        listener.statusUpdate(attemptID, thirdReduceStatus);
        Mockito.verify(ea, Mockito.times(2)).handle(eventCaptor.capture());
        TaskAttemptStatusUpdateEvent updateEvent = ((TaskAttemptStatusUpdateEvent) (eventCaptor.getValue()));
        TaskAttemptStatus status = updateEvent.getTaskAttemptStatusRef().get();
        Assert.assertNull(status.fetchFailedMaps);
        Assert.assertEquals(REDUCE, status.phase);
    }

    @Test
    public void testStatusUpdateFromUnregisteredTask() throws Exception {
        configureMocks();
        ControlledClock clock = new ControlledClock();
        clock.setTime(0);
        Mockito.doReturn(clock).when(appCtx).getClock();
        final TaskAttemptListenerImpl tal = new TaskAttemptListenerImpl(appCtx, secret, rmHeartbeatHandler, policy) {
            @Override
            protected void startRpcServer() {
                // Empty
            }

            @Override
            protected void stopRpcServer() {
                // Empty
            }
        };
        Configuration conf = new Configuration();
        conf.setLong(TASK_TIMEOUT_CHECK_INTERVAL_MS, 1);
        tal.init(conf);
        tal.start();
        AMFeedback feedback = tal.statusUpdate(attemptID, firstReduceStatus);
        Assert.assertFalse(feedback.getTaskFound());
        tal.registerPendingTask(task, wid);
        tal.registerLaunchedTask(attemptId, wid);
        feedback = tal.statusUpdate(attemptID, firstReduceStatus);
        Assert.assertTrue(feedback.getTaskFound());
        // verify attempt is still reported as found if recently unregistered
        tal.unregister(attemptId, wid);
        feedback = tal.statusUpdate(attemptID, firstReduceStatus);
        Assert.assertTrue(feedback.getTaskFound());
        // verify attempt is not found if not recently unregistered
        long unregisterTimeout = conf.getLong(TASK_EXIT_TIMEOUT, TASK_EXIT_TIMEOUT_DEFAULT);
        clock.setTime((unregisterTimeout + 1));
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                try {
                    AMFeedback response = tal.statusUpdate(attemptID, firstReduceStatus);
                    return !(response.getTaskFound());
                } catch (Exception e) {
                    throw new RuntimeException("status update failed", e);
                }
            }
        }, 10, 10000);
    }
}

