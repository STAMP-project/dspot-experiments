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


import JobConf.MAPRED_MAP_TASK_ENV;
import Locality.NODE_LOCAL;
import Locality.OFF_SWITCH;
import Locality.RACK_LOCAL;
import MRJobConfig.APPLICATION_ATTEMPT_ID;
import MRJobConfig.DEFAULT_REDUCE_CPU_VCORES;
import MRJobConfig.DEFAULT_REDUCE_MEMORY_MB;
import MRJobConfig.REDUCE_CPU_VCORES;
import MRJobConfig.REDUCE_MEMORY_MB;
import MRJobConfig.RESOURCE_TYPE_ALTERNATIVE_NAME_MEMORY;
import MRJobConfig.RESOURCE_TYPE_NAME_MEMORY;
import TaskAttemptImpl.RequestContainerTransition;
import TaskAttemptState.COMMIT_PENDING;
import TaskAttemptState.FAILED;
import TaskAttemptState.KILLED;
import TaskAttemptState.RUNNING;
import TaskAttemptState.SUCCEEDED;
import TaskAttemptStateInternal.ASSIGNED;
import TaskAttemptStateInternal.FAIL_CONTAINER_CLEANUP;
import TaskAttemptStateInternal.FAIL_FINISHING_CONTAINER;
import TaskAttemptStateInternal.FAIL_TASK_CLEANUP;
import TaskAttemptStateInternal.KILL_CONTAINER_CLEANUP;
import TaskAttemptStateInternal.KILL_TASK_CLEANUP;
import TaskAttemptStateInternal.SUCCESS_CONTAINER_CLEANUP;
import TaskAttemptStateInternal.SUCCESS_FINISHING_CONTAINER;
import TaskEventType.T_ATTEMPT_KILLED;
import TaskEventType.T_ATTEMPT_SUCCEEDED;
import TaskType.MAP;
import TaskType.REDUCE;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RawLocalFileSystem;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.jobhistory.EventType;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryEvent;
import org.apache.hadoop.mapreduce.jobhistory.TaskAttemptUnsuccessfulCompletion;
import org.apache.hadoop.mapreduce.split.JobSplit.TaskSplitMetaInfo;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.ClusterInfo;
import org.apache.hadoop.mapreduce.v2.app.MRApp;
import org.apache.hadoop.mapreduce.v2.app.TaskAttemptListener;
import org.apache.hadoop.mapreduce.v2.app.job.event.JobEvent;
import org.apache.hadoop.mapreduce.v2.app.job.event.JobEventType;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEventType;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskEvent;
import org.apache.hadoop.mapreduce.v2.app.rm.ContainerRequestEvent;
import org.apache.hadoop.mapreduce.v2.util.MRBuilderUtils;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.ResourceInformation;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.util.SystemClock;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


@SuppressWarnings({ "unchecked", "rawtypes" })
public class TestTaskAttempt {
    private static final String CUSTOM_RESOURCE_NAME = "a-custom-resource";

    public static class StubbedFS extends RawLocalFileSystem {
        @Override
        public FileStatus getFileStatus(Path f) throws IOException {
            return new FileStatus(1, false, 1, 1, 1, f);
        }
    }

    private static class TestAppender extends AppenderSkeleton {
        private final List<LoggingEvent> logEvents = new CopyOnWriteArrayList<>();

        @Override
        public boolean requiresLayout() {
            return false;
        }

        @Override
        public void close() {
        }

        @Override
        protected void append(LoggingEvent arg0) {
            logEvents.add(arg0);
        }

        private List<LoggingEvent> getLogEvents() {
            return logEvents;
        }
    }

    @Test
    public void testMRAppHistoryForMap() throws Exception {
        MRApp app = null;
        try {
            app = new TestTaskAttempt.FailingAttemptsMRApp(1, 0);
            testMRAppHistory(app);
        } finally {
            close();
        }
    }

    @Test
    public void testMRAppHistoryForReduce() throws Exception {
        MRApp app = null;
        try {
            app = new TestTaskAttempt.FailingAttemptsMRApp(0, 1);
            testMRAppHistory(app);
        } finally {
            close();
        }
    }

    @Test
    public void testMRAppHistoryForTAFailedInAssigned() throws Exception {
        // test TA_CONTAINER_LAUNCH_FAILED for map
        TestTaskAttempt.FailingAttemptsDuringAssignedMRApp app = null;
        try {
            app = new TestTaskAttempt.FailingAttemptsDuringAssignedMRApp(1, 0, TaskAttemptEventType.TA_CONTAINER_LAUNCH_FAILED);
            testTaskAttemptAssignedFailHistory(app);
            close();
            // test TA_CONTAINER_LAUNCH_FAILED for reduce
            app = new TestTaskAttempt.FailingAttemptsDuringAssignedMRApp(0, 1, TaskAttemptEventType.TA_CONTAINER_LAUNCH_FAILED);
            testTaskAttemptAssignedFailHistory(app);
            close();
            // test TA_CONTAINER_COMPLETED for map
            app = new TestTaskAttempt.FailingAttemptsDuringAssignedMRApp(1, 0, TaskAttemptEventType.TA_CONTAINER_COMPLETED);
            testTaskAttemptAssignedFailHistory(app);
            close();
            // test TA_CONTAINER_COMPLETED for reduce
            app = new TestTaskAttempt.FailingAttemptsDuringAssignedMRApp(0, 1, TaskAttemptEventType.TA_CONTAINER_COMPLETED);
            testTaskAttemptAssignedFailHistory(app);
            close();
            // test TA_FAILMSG for map
            app = new TestTaskAttempt.FailingAttemptsDuringAssignedMRApp(1, 0, TaskAttemptEventType.TA_FAILMSG);
            testTaskAttemptAssignedFailHistory(app);
            close();
            // test TA_FAILMSG for reduce
            app = new TestTaskAttempt.FailingAttemptsDuringAssignedMRApp(0, 1, TaskAttemptEventType.TA_FAILMSG);
            testTaskAttemptAssignedFailHistory(app);
            close();
            // test TA_FAILMSG_BY_CLIENT for map
            app = new TestTaskAttempt.FailingAttemptsDuringAssignedMRApp(1, 0, TaskAttemptEventType.TA_FAILMSG_BY_CLIENT);
            testTaskAttemptAssignedFailHistory(app);
            close();
            // test TA_FAILMSG_BY_CLIENT for reduce
            app = new TestTaskAttempt.FailingAttemptsDuringAssignedMRApp(0, 1, TaskAttemptEventType.TA_FAILMSG_BY_CLIENT);
            testTaskAttemptAssignedFailHistory(app);
            close();
            // test TA_KILL for map
            app = new TestTaskAttempt.FailingAttemptsDuringAssignedMRApp(1, 0, TaskAttemptEventType.TA_KILL);
            testTaskAttemptAssignedKilledHistory(app);
            close();
            // test TA_KILL for reduce
            app = new TestTaskAttempt.FailingAttemptsDuringAssignedMRApp(0, 1, TaskAttemptEventType.TA_KILL);
            testTaskAttemptAssignedKilledHistory(app);
            close();
        } finally {
            close();
        }
    }

    @Test
    public void testSingleRackRequest() throws Exception {
        TaskAttemptImpl.RequestContainerTransition rct = new TaskAttemptImpl.RequestContainerTransition(false);
        EventHandler eventHandler = Mockito.mock(EventHandler.class);
        String[] hosts = new String[3];
        hosts[0] = "host1";
        hosts[1] = "host2";
        hosts[2] = "host3";
        TaskSplitMetaInfo splitInfo = new TaskSplitMetaInfo(hosts, 0, ((128 * 1024) * 1024L));
        TaskAttemptImpl mockTaskAttempt = createMapTaskAttemptImplForTest(eventHandler, splitInfo);
        TaskAttemptEvent mockTAEvent = Mockito.mock(TaskAttemptEvent.class);
        rct.transition(mockTaskAttempt, mockTAEvent);
        ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(eventHandler, Mockito.times(2)).handle(arg.capture());
        if (!((arg.getAllValues().get(1)) instanceof ContainerRequestEvent)) {
            Assert.fail("Second Event not of type ContainerRequestEvent");
        }
        ContainerRequestEvent cre = ((ContainerRequestEvent) (arg.getAllValues().get(1)));
        String[] requestedRacks = cre.getRacks();
        // Only a single occurrence of /DefaultRack
        Assert.assertEquals(1, requestedRacks.length);
    }

    @Test
    public void testHostResolveAttempt() throws Exception {
        TaskAttemptImpl.RequestContainerTransition rct = new TaskAttemptImpl.RequestContainerTransition(false);
        EventHandler eventHandler = Mockito.mock(EventHandler.class);
        String[] hosts = new String[3];
        hosts[0] = "192.168.1.1";
        hosts[1] = "host2";
        hosts[2] = "host3";
        TaskSplitMetaInfo splitInfo = new TaskSplitMetaInfo(hosts, 0, ((128 * 1024) * 1024L));
        TaskAttemptImpl mockTaskAttempt = createMapTaskAttemptImplForTest(eventHandler, splitInfo);
        TaskAttemptImpl spyTa = Mockito.spy(mockTaskAttempt);
        Mockito.when(spyTa.resolveHost(hosts[0])).thenReturn("host1");
        spyTa.dataLocalHosts = spyTa.resolveHosts(splitInfo.getLocations());
        TaskAttemptEvent mockTAEvent = Mockito.mock(TaskAttemptEvent.class);
        rct.transition(spyTa, mockTAEvent);
        Mockito.verify(spyTa).resolveHost(hosts[0]);
        ArgumentCaptor<Event> arg = ArgumentCaptor.forClass(Event.class);
        Mockito.verify(eventHandler, Mockito.times(2)).handle(arg.capture());
        if (!((arg.getAllValues().get(1)) instanceof ContainerRequestEvent)) {
            Assert.fail("Second Event not of type ContainerRequestEvent");
        }
        Map<String, Boolean> expected = new HashMap<String, Boolean>();
        expected.put("host1", true);
        expected.put("host2", true);
        expected.put("host3", true);
        ContainerRequestEvent cre = ((ContainerRequestEvent) (arg.getAllValues().get(1)));
        String[] requestedHosts = cre.getHosts();
        for (String h : requestedHosts) {
            expected.remove(h);
        }
        Assert.assertEquals(0, expected.size());
    }

    @Test
    public void testMillisCountersUpdate() throws Exception {
        verifyMillisCounters(Resource.newInstance(1024, 1), 512);
        verifyMillisCounters(Resource.newInstance(2048, 4), 1024);
        verifyMillisCounters(Resource.newInstance(10240, 8), 2048);
    }

    static class FailingAttemptsMRApp extends MRApp {
        FailingAttemptsMRApp(int maps, int reduces) {
            super(maps, reduces, true, "FailingAttemptsMRApp", true);
        }

        @Override
        protected void attemptLaunched(TaskAttemptId attemptID) {
            getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptDiagnosticsUpdateEvent(attemptID, "Test Diagnostic Event"));
            getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptFailEvent(attemptID));
        }

        protected EventHandler<JobHistoryEvent> createJobHistoryHandler(AppContext context) {
            return new EventHandler<JobHistoryEvent>() {
                @Override
                public void handle(JobHistoryEvent event) {
                    if ((event.getType()) == (EventType.MAP_ATTEMPT_FAILED)) {
                        TaskAttemptUnsuccessfulCompletion datum = ((TaskAttemptUnsuccessfulCompletion) (event.getHistoryEvent().getDatum()));
                        Assert.assertEquals("Diagnostic Information is not Correct", "Test Diagnostic Event", datum.get(8).toString());
                    }
                }
            };
        }
    }

    static class FailingAttemptsDuringAssignedMRApp extends MRApp {
        FailingAttemptsDuringAssignedMRApp(int maps, int reduces, TaskAttemptEventType event) {
            super(maps, reduces, true, "FailingAttemptsMRApp", true);
            sendFailEvent = event;
        }

        TaskAttemptEventType sendFailEvent;

        @Override
        protected void containerLaunched(TaskAttemptId attemptID, int shufflePort) {
            // do nothing, not send TA_CONTAINER_LAUNCHED event
        }

        @Override
        protected void attemptLaunched(TaskAttemptId attemptID) {
            getContext().getEventHandler().handle(new TaskAttemptEvent(attemptID, sendFailEvent));
        }

        private boolean receiveTaStartJHEvent = false;

        private boolean receiveTaFailedJHEvent = false;

        private boolean receiveTaKilledJHEvent = false;

        public boolean getTaStartJHEvent() {
            return receiveTaStartJHEvent;
        }

        public boolean getTaFailedJHEvent() {
            return receiveTaFailedJHEvent;
        }

        public boolean getTaKilledJHEvent() {
            return receiveTaKilledJHEvent;
        }

        protected EventHandler<JobHistoryEvent> createJobHistoryHandler(AppContext context) {
            return new EventHandler<JobHistoryEvent>() {
                @Override
                public void handle(JobHistoryEvent event) {
                    if ((event.getType()) == (EventType.MAP_ATTEMPT_FAILED)) {
                        receiveTaFailedJHEvent = true;
                    } else
                        if ((event.getType()) == (EventType.MAP_ATTEMPT_KILLED)) {
                            receiveTaKilledJHEvent = true;
                        } else
                            if ((event.getType()) == (EventType.MAP_ATTEMPT_STARTED)) {
                                receiveTaStartJHEvent = true;
                            } else
                                if ((event.getType()) == (EventType.REDUCE_ATTEMPT_FAILED)) {
                                    receiveTaFailedJHEvent = true;
                                } else
                                    if ((event.getType()) == (EventType.REDUCE_ATTEMPT_KILLED)) {
                                        receiveTaKilledJHEvent = true;
                                    } else
                                        if ((event.getType()) == (EventType.REDUCE_ATTEMPT_STARTED)) {
                                            receiveTaStartJHEvent = true;
                                        }





                }
            };
        }
    }

    @Test
    public void testLaunchFailedWhileKilling() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(1, 2);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 0);
        JobId jobId = MRBuilderUtils.newJobId(appId, 1);
        TaskId taskId = MRBuilderUtils.newTaskId(jobId, 1, MAP);
        TaskAttemptId attemptId = MRBuilderUtils.newTaskAttemptId(taskId, 0);
        Path jobFile = Mockito.mock(Path.class);
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptListener taListener = Mockito.mock(TaskAttemptListener.class);
        Mockito.when(taListener.getAddress()).thenReturn(new InetSocketAddress("localhost", 0));
        JobConf jobConf = new JobConf();
        jobConf.setClass("fs.file.impl", TestTaskAttempt.StubbedFS.class, FileSystem.class);
        jobConf.setBoolean("fs.file.impl.disable.cache", true);
        jobConf.set(MAPRED_MAP_TASK_ENV, "");
        jobConf.set(APPLICATION_ATTEMPT_ID, "10");
        TaskSplitMetaInfo splits = Mockito.mock(TaskSplitMetaInfo.class);
        Mockito.when(splits.getLocations()).thenReturn(new String[]{ "127.0.0.1" });
        TaskAttemptImpl taImpl = new org.apache.hadoop.mapred.MapTaskAttemptImpl(taskId, 1, eventHandler, jobFile, 1, splits, jobConf, taListener, new Token(), new Credentials(), SystemClock.getInstance(), null);
        NodeId nid = NodeId.newInstance("127.0.0.1", 0);
        ContainerId contId = ContainerId.newContainerId(appAttemptId, 3);
        Container container = Mockito.mock(Container.class);
        Mockito.when(container.getId()).thenReturn(contId);
        Mockito.when(container.getNodeId()).thenReturn(nid);
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_SCHEDULE));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerAssignedEvent(attemptId, container, Mockito.mock(Map.class)));
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_KILL));
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_CONTAINER_CLEANED));
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_CONTAINER_LAUNCH_FAILED));
        Assert.assertFalse(eventHandler.internalError);
        Assert.assertEquals("Task attempt is not assigned on the local node", NODE_LOCAL, taImpl.getLocality());
    }

    @Test
    public void testContainerCleanedWhileRunning() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(1, 2);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 0);
        JobId jobId = MRBuilderUtils.newJobId(appId, 1);
        TaskId taskId = MRBuilderUtils.newTaskId(jobId, 1, MAP);
        TaskAttemptId attemptId = MRBuilderUtils.newTaskAttemptId(taskId, 0);
        Path jobFile = Mockito.mock(Path.class);
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptListener taListener = Mockito.mock(TaskAttemptListener.class);
        Mockito.when(taListener.getAddress()).thenReturn(new InetSocketAddress("localhost", 0));
        JobConf jobConf = new JobConf();
        jobConf.setClass("fs.file.impl", TestTaskAttempt.StubbedFS.class, FileSystem.class);
        jobConf.setBoolean("fs.file.impl.disable.cache", true);
        jobConf.set(MAPRED_MAP_TASK_ENV, "");
        jobConf.set(APPLICATION_ATTEMPT_ID, "10");
        TaskSplitMetaInfo splits = Mockito.mock(TaskSplitMetaInfo.class);
        Mockito.when(splits.getLocations()).thenReturn(new String[]{ "127.0.0.1" });
        AppContext appCtx = Mockito.mock(AppContext.class);
        ClusterInfo clusterInfo = Mockito.mock(ClusterInfo.class);
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(appCtx.getClusterInfo()).thenReturn(clusterInfo);
        Mockito.when(resource.getMemorySize()).thenReturn(1024L);
        setupTaskAttemptFinishingMonitor(eventHandler, jobConf, appCtx);
        TaskAttemptImpl taImpl = new org.apache.hadoop.mapred.MapTaskAttemptImpl(taskId, 1, eventHandler, jobFile, 1, splits, jobConf, taListener, new Token(), new Credentials(), SystemClock.getInstance(), appCtx);
        NodeId nid = NodeId.newInstance("127.0.0.2", 0);
        ContainerId contId = ContainerId.newContainerId(appAttemptId, 3);
        Container container = Mockito.mock(Container.class);
        Mockito.when(container.getId()).thenReturn(contId);
        Mockito.when(container.getNodeId()).thenReturn(nid);
        Mockito.when(container.getNodeHttpAddress()).thenReturn("localhost:0");
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_SCHEDULE));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerAssignedEvent(attemptId, container, Mockito.mock(Map.class)));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerLaunchedEvent(attemptId, 0));
        Assert.assertEquals("Task attempt is not in running state", taImpl.getState(), RUNNING);
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_CONTAINER_CLEANED));
        Assert.assertFalse("InternalError occurred trying to handle TA_CONTAINER_CLEANED", eventHandler.internalError);
        Assert.assertEquals("Task attempt is not assigned on the local rack", RACK_LOCAL, taImpl.getLocality());
    }

    @Test
    public void testContainerCleanedWhileCommitting() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(1, 2);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 0);
        JobId jobId = MRBuilderUtils.newJobId(appId, 1);
        TaskId taskId = MRBuilderUtils.newTaskId(jobId, 1, MAP);
        TaskAttemptId attemptId = MRBuilderUtils.newTaskAttemptId(taskId, 0);
        Path jobFile = Mockito.mock(Path.class);
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptListener taListener = Mockito.mock(TaskAttemptListener.class);
        Mockito.when(taListener.getAddress()).thenReturn(new InetSocketAddress("localhost", 0));
        JobConf jobConf = new JobConf();
        jobConf.setClass("fs.file.impl", TestTaskAttempt.StubbedFS.class, FileSystem.class);
        jobConf.setBoolean("fs.file.impl.disable.cache", true);
        jobConf.set(MAPRED_MAP_TASK_ENV, "");
        jobConf.set(APPLICATION_ATTEMPT_ID, "10");
        TaskSplitMetaInfo splits = Mockito.mock(TaskSplitMetaInfo.class);
        Mockito.when(splits.getLocations()).thenReturn(new String[]{  });
        AppContext appCtx = Mockito.mock(AppContext.class);
        ClusterInfo clusterInfo = Mockito.mock(ClusterInfo.class);
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(appCtx.getClusterInfo()).thenReturn(clusterInfo);
        Mockito.when(resource.getMemorySize()).thenReturn(1024L);
        setupTaskAttemptFinishingMonitor(eventHandler, jobConf, appCtx);
        TaskAttemptImpl taImpl = new org.apache.hadoop.mapred.MapTaskAttemptImpl(taskId, 1, eventHandler, jobFile, 1, splits, jobConf, taListener, new Token(), new Credentials(), SystemClock.getInstance(), appCtx);
        NodeId nid = NodeId.newInstance("127.0.0.1", 0);
        ContainerId contId = ContainerId.newContainerId(appAttemptId, 3);
        Container container = Mockito.mock(Container.class);
        Mockito.when(container.getId()).thenReturn(contId);
        Mockito.when(container.getNodeId()).thenReturn(nid);
        Mockito.when(container.getNodeHttpAddress()).thenReturn("localhost:0");
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_SCHEDULE));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerAssignedEvent(attemptId, container, Mockito.mock(Map.class)));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerLaunchedEvent(attemptId, 0));
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_COMMIT_PENDING));
        Assert.assertEquals("Task attempt is not in commit pending state", taImpl.getState(), COMMIT_PENDING);
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_CONTAINER_CLEANED));
        Assert.assertFalse("InternalError occurred trying to handle TA_CONTAINER_CLEANED", eventHandler.internalError);
        Assert.assertEquals("Task attempt is assigned locally", OFF_SWITCH, taImpl.getLocality());
    }

    @Test
    public void testDoubleTooManyFetchFailure() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(1, 2);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 0);
        JobId jobId = MRBuilderUtils.newJobId(appId, 1);
        TaskId taskId = MRBuilderUtils.newTaskId(jobId, 1, MAP);
        TaskAttemptId attemptId = MRBuilderUtils.newTaskAttemptId(taskId, 0);
        TaskId reduceTaskId = MRBuilderUtils.newTaskId(jobId, 1, REDUCE);
        TaskAttemptId reduceTAId = MRBuilderUtils.newTaskAttemptId(reduceTaskId, 0);
        Path jobFile = Mockito.mock(Path.class);
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptListener taListener = Mockito.mock(TaskAttemptListener.class);
        Mockito.when(taListener.getAddress()).thenReturn(new InetSocketAddress("localhost", 0));
        JobConf jobConf = new JobConf();
        jobConf.setClass("fs.file.impl", TestTaskAttempt.StubbedFS.class, FileSystem.class);
        jobConf.setBoolean("fs.file.impl.disable.cache", true);
        jobConf.set(MAPRED_MAP_TASK_ENV, "");
        jobConf.set(APPLICATION_ATTEMPT_ID, "10");
        TaskSplitMetaInfo splits = Mockito.mock(TaskSplitMetaInfo.class);
        Mockito.when(splits.getLocations()).thenReturn(new String[]{ "127.0.0.1" });
        AppContext appCtx = Mockito.mock(AppContext.class);
        ClusterInfo clusterInfo = Mockito.mock(ClusterInfo.class);
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(appCtx.getClusterInfo()).thenReturn(clusterInfo);
        Mockito.when(resource.getMemorySize()).thenReturn(1024L);
        setupTaskAttemptFinishingMonitor(eventHandler, jobConf, appCtx);
        TaskAttemptImpl taImpl = new org.apache.hadoop.mapred.MapTaskAttemptImpl(taskId, 1, eventHandler, jobFile, 1, splits, jobConf, taListener, new Token(), new Credentials(), SystemClock.getInstance(), appCtx);
        NodeId nid = NodeId.newInstance("127.0.0.1", 0);
        ContainerId contId = ContainerId.newContainerId(appAttemptId, 3);
        Container container = Mockito.mock(Container.class);
        Mockito.when(container.getId()).thenReturn(contId);
        Mockito.when(container.getNodeId()).thenReturn(nid);
        Mockito.when(container.getNodeHttpAddress()).thenReturn("localhost:0");
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_SCHEDULE));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerAssignedEvent(attemptId, container, Mockito.mock(Map.class)));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerLaunchedEvent(attemptId, 0));
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_DONE));
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_CONTAINER_COMPLETED));
        Assert.assertEquals("Task attempt is not in succeeded state", taImpl.getState(), SUCCEEDED);
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptTooManyFetchFailureEvent(attemptId, reduceTAId, "Host"));
        Assert.assertEquals("Task attempt is not in FAILED state", taImpl.getState(), FAILED);
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_TOO_MANY_FETCH_FAILURE));
        Assert.assertEquals("Task attempt is not in FAILED state, still", taImpl.getState(), FAILED);
        Assert.assertFalse("InternalError occurred trying to handle TA_CONTAINER_CLEANED", eventHandler.internalError);
    }

    @Test
    public void testAppDiagnosticEventOnUnassignedTask() {
        ApplicationId appId = ApplicationId.newInstance(1, 2);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 0);
        JobId jobId = MRBuilderUtils.newJobId(appId, 1);
        TaskId taskId = MRBuilderUtils.newTaskId(jobId, 1, MAP);
        TaskAttemptId attemptId = MRBuilderUtils.newTaskAttemptId(taskId, 0);
        Path jobFile = Mockito.mock(Path.class);
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptListener taListener = Mockito.mock(TaskAttemptListener.class);
        Mockito.when(taListener.getAddress()).thenReturn(new InetSocketAddress("localhost", 0));
        JobConf jobConf = new JobConf();
        jobConf.setClass("fs.file.impl", TestTaskAttempt.StubbedFS.class, FileSystem.class);
        jobConf.setBoolean("fs.file.impl.disable.cache", true);
        jobConf.set(MAPRED_MAP_TASK_ENV, "");
        jobConf.set(APPLICATION_ATTEMPT_ID, "10");
        TaskSplitMetaInfo splits = Mockito.mock(TaskSplitMetaInfo.class);
        Mockito.when(splits.getLocations()).thenReturn(new String[]{ "127.0.0.1" });
        AppContext appCtx = Mockito.mock(AppContext.class);
        ClusterInfo clusterInfo = Mockito.mock(ClusterInfo.class);
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(appCtx.getClusterInfo()).thenReturn(clusterInfo);
        Mockito.when(resource.getMemorySize()).thenReturn(1024L);
        setupTaskAttemptFinishingMonitor(eventHandler, jobConf, appCtx);
        TaskAttemptImpl taImpl = new org.apache.hadoop.mapred.MapTaskAttemptImpl(taskId, 1, eventHandler, jobFile, 1, splits, jobConf, taListener, new Token(), new Credentials(), SystemClock.getInstance(), appCtx);
        NodeId nid = NodeId.newInstance("127.0.0.1", 0);
        ContainerId contId = ContainerId.newContainerId(appAttemptId, 3);
        Container container = Mockito.mock(Container.class);
        Mockito.when(container.getId()).thenReturn(contId);
        Mockito.when(container.getNodeId()).thenReturn(nid);
        Mockito.when(container.getNodeHttpAddress()).thenReturn("localhost:0");
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_SCHEDULE));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptDiagnosticsUpdateEvent(attemptId, "Task got killed"));
        Assert.assertFalse("InternalError occurred trying to handle TA_DIAGNOSTICS_UPDATE on assigned task", eventHandler.internalError);
        try {
            taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_KILL));
            Assert.assertTrue("No exception on UNASSIGNED STATE KILL event", true);
        } catch (Exception e) {
            Assert.assertFalse("Exception not expected for UNASSIGNED STATE KILL event", true);
        }
    }

    @Test
    public void testTooManyFetchFailureAfterKill() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(1, 2);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 0);
        JobId jobId = MRBuilderUtils.newJobId(appId, 1);
        TaskId taskId = MRBuilderUtils.newTaskId(jobId, 1, MAP);
        TaskAttemptId attemptId = MRBuilderUtils.newTaskAttemptId(taskId, 0);
        Path jobFile = Mockito.mock(Path.class);
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptListener taListener = Mockito.mock(TaskAttemptListener.class);
        Mockito.when(taListener.getAddress()).thenReturn(new InetSocketAddress("localhost", 0));
        JobConf jobConf = new JobConf();
        jobConf.setClass("fs.file.impl", TestTaskAttempt.StubbedFS.class, FileSystem.class);
        jobConf.setBoolean("fs.file.impl.disable.cache", true);
        jobConf.set(MAPRED_MAP_TASK_ENV, "");
        jobConf.set(APPLICATION_ATTEMPT_ID, "10");
        TaskSplitMetaInfo splits = Mockito.mock(TaskSplitMetaInfo.class);
        Mockito.when(splits.getLocations()).thenReturn(new String[]{ "127.0.0.1" });
        AppContext appCtx = Mockito.mock(AppContext.class);
        ClusterInfo clusterInfo = Mockito.mock(ClusterInfo.class);
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(appCtx.getClusterInfo()).thenReturn(clusterInfo);
        Mockito.when(resource.getMemorySize()).thenReturn(1024L);
        setupTaskAttemptFinishingMonitor(eventHandler, jobConf, appCtx);
        TaskAttemptImpl taImpl = new org.apache.hadoop.mapred.MapTaskAttemptImpl(taskId, 1, eventHandler, jobFile, 1, splits, jobConf, taListener, Mockito.mock(Token.class), new Credentials(), SystemClock.getInstance(), appCtx);
        NodeId nid = NodeId.newInstance("127.0.0.1", 0);
        ContainerId contId = ContainerId.newContainerId(appAttemptId, 3);
        Container container = Mockito.mock(Container.class);
        Mockito.when(container.getId()).thenReturn(contId);
        Mockito.when(container.getNodeId()).thenReturn(nid);
        Mockito.when(container.getNodeHttpAddress()).thenReturn("localhost:0");
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_SCHEDULE));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerAssignedEvent(attemptId, container, Mockito.mock(Map.class)));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerLaunchedEvent(attemptId, 0));
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_DONE));
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_CONTAINER_COMPLETED));
        Assert.assertEquals("Task attempt is not in succeeded state", taImpl.getState(), SUCCEEDED);
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_KILL));
        Assert.assertEquals("Task attempt is not in KILLED state", taImpl.getState(), KILLED);
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_TOO_MANY_FETCH_FAILURE));
        Assert.assertEquals("Task attempt is not in KILLED state, still", taImpl.getState(), KILLED);
        Assert.assertFalse("InternalError occurred trying to handle TA_CONTAINER_CLEANED", eventHandler.internalError);
    }

    @Test
    public void testAppDiagnosticEventOnNewTask() {
        ApplicationId appId = ApplicationId.newInstance(1, 2);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 0);
        JobId jobId = MRBuilderUtils.newJobId(appId, 1);
        TaskId taskId = MRBuilderUtils.newTaskId(jobId, 1, MAP);
        TaskAttemptId attemptId = MRBuilderUtils.newTaskAttemptId(taskId, 0);
        Path jobFile = Mockito.mock(Path.class);
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptListener taListener = Mockito.mock(TaskAttemptListener.class);
        Mockito.when(taListener.getAddress()).thenReturn(new InetSocketAddress("localhost", 0));
        JobConf jobConf = new JobConf();
        jobConf.setClass("fs.file.impl", TestTaskAttempt.StubbedFS.class, FileSystem.class);
        jobConf.setBoolean("fs.file.impl.disable.cache", true);
        jobConf.set(MAPRED_MAP_TASK_ENV, "");
        jobConf.set(APPLICATION_ATTEMPT_ID, "10");
        TaskSplitMetaInfo splits = Mockito.mock(TaskSplitMetaInfo.class);
        Mockito.when(splits.getLocations()).thenReturn(new String[]{ "127.0.0.1" });
        AppContext appCtx = Mockito.mock(AppContext.class);
        ClusterInfo clusterInfo = Mockito.mock(ClusterInfo.class);
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(appCtx.getClusterInfo()).thenReturn(clusterInfo);
        Mockito.when(resource.getMemorySize()).thenReturn(1024L);
        setupTaskAttemptFinishingMonitor(eventHandler, jobConf, appCtx);
        TaskAttemptImpl taImpl = new org.apache.hadoop.mapred.MapTaskAttemptImpl(taskId, 1, eventHandler, jobFile, 1, splits, jobConf, taListener, new Token(), new Credentials(), SystemClock.getInstance(), appCtx);
        NodeId nid = NodeId.newInstance("127.0.0.1", 0);
        ContainerId contId = ContainerId.newContainerId(appAttemptId, 3);
        Container container = Mockito.mock(Container.class);
        Mockito.when(container.getId()).thenReturn(contId);
        Mockito.when(container.getNodeId()).thenReturn(nid);
        Mockito.when(container.getNodeHttpAddress()).thenReturn("localhost:0");
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptDiagnosticsUpdateEvent(attemptId, "Task got killed"));
        Assert.assertFalse("InternalError occurred trying to handle TA_DIAGNOSTICS_UPDATE on assigned task", eventHandler.internalError);
    }

    @Test
    public void testFetchFailureAttemptFinishTime() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(1, 2);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 0);
        JobId jobId = MRBuilderUtils.newJobId(appId, 1);
        TaskId taskId = MRBuilderUtils.newTaskId(jobId, 1, MAP);
        TaskAttemptId attemptId = MRBuilderUtils.newTaskAttemptId(taskId, 0);
        TaskId reducetaskId = MRBuilderUtils.newTaskId(jobId, 1, REDUCE);
        TaskAttemptId reduceTAId = MRBuilderUtils.newTaskAttemptId(reducetaskId, 0);
        Path jobFile = Mockito.mock(Path.class);
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptListener taListener = Mockito.mock(TaskAttemptListener.class);
        Mockito.when(taListener.getAddress()).thenReturn(new InetSocketAddress("localhost", 0));
        JobConf jobConf = new JobConf();
        jobConf.setClass("fs.file.impl", TestTaskAttempt.StubbedFS.class, FileSystem.class);
        jobConf.setBoolean("fs.file.impl.disable.cache", true);
        jobConf.set(MAPRED_MAP_TASK_ENV, "");
        jobConf.set(APPLICATION_ATTEMPT_ID, "10");
        TaskSplitMetaInfo splits = Mockito.mock(TaskSplitMetaInfo.class);
        Mockito.when(splits.getLocations()).thenReturn(new String[]{ "127.0.0.1" });
        AppContext appCtx = Mockito.mock(AppContext.class);
        ClusterInfo clusterInfo = Mockito.mock(ClusterInfo.class);
        Mockito.when(appCtx.getClusterInfo()).thenReturn(clusterInfo);
        setupTaskAttemptFinishingMonitor(eventHandler, jobConf, appCtx);
        TaskAttemptImpl taImpl = new org.apache.hadoop.mapred.MapTaskAttemptImpl(taskId, 1, eventHandler, jobFile, 1, splits, jobConf, taListener, Mockito.mock(Token.class), new Credentials(), SystemClock.getInstance(), appCtx);
        NodeId nid = NodeId.newInstance("127.0.0.1", 0);
        ContainerId contId = ContainerId.newContainerId(appAttemptId, 3);
        Container container = Mockito.mock(Container.class);
        Mockito.when(container.getId()).thenReturn(contId);
        Mockito.when(container.getNodeId()).thenReturn(nid);
        Mockito.when(container.getNodeHttpAddress()).thenReturn("localhost:0");
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_SCHEDULE));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerAssignedEvent(attemptId, container, Mockito.mock(Map.class)));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerLaunchedEvent(attemptId, 0));
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_DONE));
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_CONTAINER_COMPLETED));
        Assert.assertEquals("Task attempt is not in succeeded state", taImpl.getState(), SUCCEEDED);
        Assert.assertTrue("Task Attempt finish time is not greater than 0", ((taImpl.getFinishTime()) > 0));
        Long finishTime = taImpl.getFinishTime();
        Thread.sleep(5);
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptTooManyFetchFailureEvent(attemptId, reduceTAId, "Host"));
        Assert.assertEquals("Task attempt is not in Too Many Fetch Failure state", taImpl.getState(), FAILED);
        Assert.assertEquals(("After TA_TOO_MANY_FETCH_FAILURE," + " Task attempt finish time is not the same "), finishTime, Long.valueOf(taImpl.getFinishTime()));
    }

    @Test
    public void testContainerKillOnNew() throws Exception {
        containerKillBeforeAssignment(false);
    }

    @Test
    public void testContainerKillOnUnassigned() throws Exception {
        containerKillBeforeAssignment(true);
    }

    @Test
    public void testContainerKillAfterAssigned() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(1, 2);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 0);
        JobId jobId = MRBuilderUtils.newJobId(appId, 1);
        TaskId taskId = MRBuilderUtils.newTaskId(jobId, 1, MAP);
        TaskAttemptId attemptId = MRBuilderUtils.newTaskAttemptId(taskId, 0);
        Path jobFile = Mockito.mock(Path.class);
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptListener taListener = Mockito.mock(TaskAttemptListener.class);
        Mockito.when(taListener.getAddress()).thenReturn(new InetSocketAddress("localhost", 0));
        JobConf jobConf = new JobConf();
        jobConf.setClass("fs.file.impl", TestTaskAttempt.StubbedFS.class, FileSystem.class);
        jobConf.setBoolean("fs.file.impl.disable.cache", true);
        jobConf.set(MAPRED_MAP_TASK_ENV, "");
        jobConf.set(APPLICATION_ATTEMPT_ID, "10");
        TaskSplitMetaInfo splits = Mockito.mock(TaskSplitMetaInfo.class);
        Mockito.when(splits.getLocations()).thenReturn(new String[]{ "127.0.0.1" });
        AppContext appCtx = Mockito.mock(AppContext.class);
        ClusterInfo clusterInfo = Mockito.mock(ClusterInfo.class);
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(appCtx.getClusterInfo()).thenReturn(clusterInfo);
        Mockito.when(resource.getMemorySize()).thenReturn(1024L);
        TaskAttemptImpl taImpl = new org.apache.hadoop.mapred.MapTaskAttemptImpl(taskId, 1, eventHandler, jobFile, 1, splits, jobConf, taListener, new Token(), new Credentials(), SystemClock.getInstance(), appCtx);
        NodeId nid = NodeId.newInstance("127.0.0.2", 0);
        ContainerId contId = ContainerId.newContainerId(appAttemptId, 3);
        Container container = Mockito.mock(Container.class);
        Mockito.when(container.getId()).thenReturn(contId);
        Mockito.when(container.getNodeId()).thenReturn(nid);
        Mockito.when(container.getNodeHttpAddress()).thenReturn("localhost:0");
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_SCHEDULE));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerAssignedEvent(attemptId, container, Mockito.mock(Map.class)));
        Assert.assertEquals("Task attempt is not in assinged state", taImpl.getInternalState(), ASSIGNED);
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_KILL));
        Assert.assertEquals("Task should be in KILL_CONTAINER_CLEANUP state", KILL_CONTAINER_CLEANUP, taImpl.getInternalState());
    }

    @Test
    public void testContainerKillWhileRunning() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(1, 2);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 0);
        JobId jobId = MRBuilderUtils.newJobId(appId, 1);
        TaskId taskId = MRBuilderUtils.newTaskId(jobId, 1, MAP);
        TaskAttemptId attemptId = MRBuilderUtils.newTaskAttemptId(taskId, 0);
        Path jobFile = Mockito.mock(Path.class);
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptListener taListener = Mockito.mock(TaskAttemptListener.class);
        Mockito.when(taListener.getAddress()).thenReturn(new InetSocketAddress("localhost", 0));
        JobConf jobConf = new JobConf();
        jobConf.setClass("fs.file.impl", TestTaskAttempt.StubbedFS.class, FileSystem.class);
        jobConf.setBoolean("fs.file.impl.disable.cache", true);
        jobConf.set(MAPRED_MAP_TASK_ENV, "");
        jobConf.set(APPLICATION_ATTEMPT_ID, "10");
        TaskSplitMetaInfo splits = Mockito.mock(TaskSplitMetaInfo.class);
        Mockito.when(splits.getLocations()).thenReturn(new String[]{ "127.0.0.1" });
        AppContext appCtx = Mockito.mock(AppContext.class);
        ClusterInfo clusterInfo = Mockito.mock(ClusterInfo.class);
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(appCtx.getClusterInfo()).thenReturn(clusterInfo);
        Mockito.when(resource.getMemorySize()).thenReturn(1024L);
        TaskAttemptImpl taImpl = new org.apache.hadoop.mapred.MapTaskAttemptImpl(taskId, 1, eventHandler, jobFile, 1, splits, jobConf, taListener, new Token(), new Credentials(), SystemClock.getInstance(), appCtx);
        NodeId nid = NodeId.newInstance("127.0.0.2", 0);
        ContainerId contId = ContainerId.newContainerId(appAttemptId, 3);
        Container container = Mockito.mock(Container.class);
        Mockito.when(container.getId()).thenReturn(contId);
        Mockito.when(container.getNodeId()).thenReturn(nid);
        Mockito.when(container.getNodeHttpAddress()).thenReturn("localhost:0");
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_SCHEDULE));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerAssignedEvent(attemptId, container, Mockito.mock(Map.class)));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerLaunchedEvent(attemptId, 0));
        Assert.assertEquals("Task attempt is not in running state", taImpl.getState(), RUNNING);
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_KILL));
        Assert.assertFalse("InternalError occurred trying to handle TA_KILL", eventHandler.internalError);
        Assert.assertEquals("Task should be in KILL_CONTAINER_CLEANUP state", KILL_CONTAINER_CLEANUP, taImpl.getInternalState());
    }

    @Test
    public void testContainerKillWhileCommitPending() throws Exception {
        ApplicationId appId = ApplicationId.newInstance(1, 2);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 0);
        JobId jobId = MRBuilderUtils.newJobId(appId, 1);
        TaskId taskId = MRBuilderUtils.newTaskId(jobId, 1, MAP);
        TaskAttemptId attemptId = MRBuilderUtils.newTaskAttemptId(taskId, 0);
        Path jobFile = Mockito.mock(Path.class);
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptListener taListener = Mockito.mock(TaskAttemptListener.class);
        Mockito.when(taListener.getAddress()).thenReturn(new InetSocketAddress("localhost", 0));
        JobConf jobConf = new JobConf();
        jobConf.setClass("fs.file.impl", TestTaskAttempt.StubbedFS.class, FileSystem.class);
        jobConf.setBoolean("fs.file.impl.disable.cache", true);
        jobConf.set(MAPRED_MAP_TASK_ENV, "");
        jobConf.set(APPLICATION_ATTEMPT_ID, "10");
        TaskSplitMetaInfo splits = Mockito.mock(TaskSplitMetaInfo.class);
        Mockito.when(splits.getLocations()).thenReturn(new String[]{ "127.0.0.1" });
        AppContext appCtx = Mockito.mock(AppContext.class);
        ClusterInfo clusterInfo = Mockito.mock(ClusterInfo.class);
        Resource resource = Mockito.mock(Resource.class);
        Mockito.when(appCtx.getClusterInfo()).thenReturn(clusterInfo);
        Mockito.when(resource.getMemorySize()).thenReturn(1024L);
        TaskAttemptImpl taImpl = new org.apache.hadoop.mapred.MapTaskAttemptImpl(taskId, 1, eventHandler, jobFile, 1, splits, jobConf, taListener, new Token(), new Credentials(), SystemClock.getInstance(), appCtx);
        NodeId nid = NodeId.newInstance("127.0.0.2", 0);
        ContainerId contId = ContainerId.newContainerId(appAttemptId, 3);
        Container container = Mockito.mock(Container.class);
        Mockito.when(container.getId()).thenReturn(contId);
        Mockito.when(container.getNodeId()).thenReturn(nid);
        Mockito.when(container.getNodeHttpAddress()).thenReturn("localhost:0");
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_SCHEDULE));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerAssignedEvent(attemptId, container, Mockito.mock(Map.class)));
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptContainerLaunchedEvent(attemptId, 0));
        Assert.assertEquals("Task attempt is not in running state", taImpl.getState(), RUNNING);
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_COMMIT_PENDING));
        Assert.assertEquals("Task should be in COMMIT_PENDING state", TaskAttemptStateInternal.COMMIT_PENDING, taImpl.getInternalState());
        taImpl.handle(new TaskAttemptEvent(attemptId, TaskAttemptEventType.TA_KILL));
        Assert.assertFalse("InternalError occurred trying to handle TA_KILL", eventHandler.internalError);
        Assert.assertEquals("Task should be in KILL_CONTAINER_CLEANUP state", KILL_CONTAINER_CLEANUP, taImpl.getInternalState());
    }

    @Test
    public void testKillMapTaskWhileSuccessFinishing() throws Exception {
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptImpl taImpl = createTaskAttemptImpl(eventHandler);
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_DONE));
        Assert.assertEquals("Task attempt is not in SUCCEEDED state", taImpl.getState(), SUCCEEDED);
        Assert.assertEquals(("Task attempt's internal state is not " + "SUCCESS_FINISHING_CONTAINER"), taImpl.getInternalState(), SUCCESS_FINISHING_CONTAINER);
        // If the map task is killed when it is in SUCCESS_FINISHING_CONTAINER
        // state, the state will move to KILL_CONTAINER_CLEANUP
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_KILL));
        Assert.assertEquals("Task attempt is not in KILLED state", taImpl.getState(), KILLED);
        Assert.assertEquals("Task attempt's internal state is not KILL_CONTAINER_CLEANUP", taImpl.getInternalState(), KILL_CONTAINER_CLEANUP);
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_CONTAINER_CLEANED));
        Assert.assertEquals("Task attempt's internal state is not KILL_TASK_CLEANUP", taImpl.getInternalState(), KILL_TASK_CLEANUP);
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_CLEANUP_DONE));
        Assert.assertEquals("Task attempt is not in KILLED state", taImpl.getState(), KILLED);
        Assert.assertFalse("InternalError occurred", eventHandler.internalError);
    }

    @Test
    public void testKillMapOnlyTaskWhileSuccessFinishing() throws Exception {
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptImpl taImpl = createMapOnlyTaskAttemptImpl(eventHandler);
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_DONE));
        Assert.assertEquals("Task attempt is not in SUCCEEDED state", SUCCEEDED, taImpl.getState());
        Assert.assertEquals(("Task attempt's internal state is not " + "SUCCESS_FINISHING_CONTAINER"), SUCCESS_FINISHING_CONTAINER, taImpl.getInternalState());
        // If the map only task is killed when it is in SUCCESS_FINISHING_CONTAINER
        // state, the state will move to SUCCESS_CONTAINER_CLEANUP
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_KILL));
        Assert.assertEquals("Task attempt is not in SUCCEEDED state", SUCCEEDED, taImpl.getState());
        Assert.assertEquals(("Task attempt's internal state is not " + "SUCCESS_CONTAINER_CLEANUP"), SUCCESS_CONTAINER_CLEANUP, taImpl.getInternalState());
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_CONTAINER_CLEANED));
        Assert.assertEquals("Task attempt is not in SUCCEEDED state", SUCCEEDED, taImpl.getState());
        Assert.assertEquals("Task attempt's internal state is not SUCCEEDED state", TaskAttemptStateInternal.SUCCEEDED, taImpl.getInternalState());
        Assert.assertFalse("InternalError occurred", eventHandler.internalError);
    }

    @Test
    public void testKillMapTaskAfterSuccess() throws Exception {
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptImpl taImpl = createTaskAttemptImpl(eventHandler);
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_DONE));
        Assert.assertEquals("Task attempt is not in SUCCEEDED state", taImpl.getState(), SUCCEEDED);
        Assert.assertEquals(("Task attempt's internal state is not " + "SUCCESS_FINISHING_CONTAINER"), taImpl.getInternalState(), SUCCESS_FINISHING_CONTAINER);
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_CONTAINER_CLEANED));
        // Send a map task attempt kill event indicating next map attempt has to be
        // reschedule
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptKillEvent(taImpl.getID(), "", true));
        Assert.assertEquals("Task attempt is not in KILLED state", taImpl.getState(), KILLED);
        Assert.assertEquals("Task attempt's internal state is not KILLED", taImpl.getInternalState(), TaskAttemptStateInternal.KILLED);
        Assert.assertFalse("InternalError occurred", eventHandler.internalError);
        TaskEvent event = eventHandler.lastTaskEvent;
        Assert.assertEquals(T_ATTEMPT_KILLED, event.getType());
        // Send an attempt killed event to TaskImpl forwarding the same reschedule
        // flag we received in task attempt kill event.
        Assert.assertTrue(getRescheduleAttempt());
    }

    @Test
    public void testKillMapOnlyTaskAfterSuccess() throws Exception {
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptImpl taImpl = createMapOnlyTaskAttemptImpl(eventHandler);
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_DONE));
        Assert.assertEquals("Task attempt is not in SUCCEEDED state", SUCCEEDED, taImpl.getState());
        Assert.assertEquals(("Task attempt's internal state is not " + "SUCCESS_FINISHING_CONTAINER"), SUCCESS_FINISHING_CONTAINER, taImpl.getInternalState());
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_CONTAINER_CLEANED));
        // Succeeded
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptKillEvent(taImpl.getID(), "", true));
        Assert.assertEquals("Task attempt is not in SUCCEEDED state", SUCCEEDED, taImpl.getState());
        Assert.assertEquals("Task attempt's internal state is not SUCCEEDED", TaskAttemptStateInternal.SUCCEEDED, taImpl.getInternalState());
        Assert.assertFalse("InternalError occurred", eventHandler.internalError);
        TaskEvent event = eventHandler.lastTaskEvent;
        Assert.assertEquals(T_ATTEMPT_SUCCEEDED, event.getType());
    }

    @Test
    public void testKillMapTaskWhileFailFinishing() throws Exception {
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptImpl taImpl = createTaskAttemptImpl(eventHandler);
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptFailEvent(taImpl.getID()));
        Assert.assertEquals("Task attempt is not in FAILED state", taImpl.getState(), FAILED);
        Assert.assertEquals(("Task attempt's internal state is not " + "FAIL_FINISHING_CONTAINER"), taImpl.getInternalState(), FAIL_FINISHING_CONTAINER);
        // If the map task is killed when it is in FAIL_FINISHING_CONTAINER state,
        // the state will stay in FAIL_FINISHING_CONTAINER.
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_KILL));
        Assert.assertEquals("Task attempt is not in RUNNING state", taImpl.getState(), FAILED);
        Assert.assertEquals(("Task attempt's internal state is not " + "FAIL_FINISHING_CONTAINER"), taImpl.getInternalState(), FAIL_FINISHING_CONTAINER);
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_TIMED_OUT));
        Assert.assertEquals("Task attempt's internal state is not FAIL_CONTAINER_CLEANUP", taImpl.getInternalState(), FAIL_CONTAINER_CLEANUP);
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_CONTAINER_CLEANED));
        Assert.assertEquals("Task attempt's internal state is not FAIL_TASK_CLEANUP", taImpl.getInternalState(), FAIL_TASK_CLEANUP);
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_CLEANUP_DONE));
        Assert.assertEquals("Task attempt is not in KILLED state", taImpl.getState(), FAILED);
        Assert.assertFalse("InternalError occurred", eventHandler.internalError);
    }

    @Test
    public void testFailMapTaskByClient() throws Exception {
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptImpl taImpl = createTaskAttemptImpl(eventHandler);
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_FAILMSG_BY_CLIENT));
        Assert.assertEquals("Task attempt is not in RUNNING state", taImpl.getState(), FAILED);
        Assert.assertEquals(("Task attempt's internal state is not " + "FAIL_CONTAINER_CLEANUP"), taImpl.getInternalState(), FAIL_CONTAINER_CLEANUP);
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_CONTAINER_CLEANED));
        Assert.assertEquals("Task attempt's internal state is not FAIL_TASK_CLEANUP", taImpl.getInternalState(), FAIL_TASK_CLEANUP);
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_CLEANUP_DONE));
        Assert.assertEquals("Task attempt is not in KILLED state", taImpl.getState(), FAILED);
        Assert.assertFalse("InternalError occurred", eventHandler.internalError);
    }

    @Test
    public void testTaskAttemptDiagnosticEventOnFinishing() throws Exception {
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptImpl taImpl = createTaskAttemptImpl(eventHandler);
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_DONE));
        Assert.assertEquals("Task attempt is not in RUNNING state", taImpl.getState(), SUCCEEDED);
        Assert.assertEquals(("Task attempt's internal state is not " + "SUCCESS_FINISHING_CONTAINER"), taImpl.getInternalState(), SUCCESS_FINISHING_CONTAINER);
        // TA_DIAGNOSTICS_UPDATE doesn't change state
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptDiagnosticsUpdateEvent(taImpl.getID(), "Task got updated"));
        Assert.assertEquals("Task attempt is not in RUNNING state", taImpl.getState(), SUCCEEDED);
        Assert.assertEquals(("Task attempt's internal state is not " + "SUCCESS_FINISHING_CONTAINER"), taImpl.getInternalState(), SUCCESS_FINISHING_CONTAINER);
        Assert.assertFalse("InternalError occurred", eventHandler.internalError);
    }

    @Test
    public void testTimeoutWhileSuccessFinishing() throws Exception {
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptImpl taImpl = createTaskAttemptImpl(eventHandler);
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_DONE));
        Assert.assertEquals("Task attempt is not in RUNNING state", taImpl.getState(), SUCCEEDED);
        Assert.assertEquals(("Task attempt's internal state is not " + "SUCCESS_FINISHING_CONTAINER"), taImpl.getInternalState(), SUCCESS_FINISHING_CONTAINER);
        // If the task stays in SUCCESS_FINISHING_CONTAINER for too long,
        // TaskAttemptListenerImpl will time out the attempt.
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_TIMED_OUT));
        Assert.assertEquals("Task attempt is not in RUNNING state", taImpl.getState(), SUCCEEDED);
        Assert.assertEquals(("Task attempt's internal state is not " + "SUCCESS_CONTAINER_CLEANUP"), taImpl.getInternalState(), SUCCESS_CONTAINER_CLEANUP);
        Assert.assertFalse("InternalError occurred", eventHandler.internalError);
    }

    @Test
    public void testTimeoutWhileFailFinishing() throws Exception {
        TestTaskAttempt.MockEventHandler eventHandler = new TestTaskAttempt.MockEventHandler();
        TaskAttemptImpl taImpl = createTaskAttemptImpl(eventHandler);
        taImpl.handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptFailEvent(taImpl.getID()));
        Assert.assertEquals("Task attempt is not in RUNNING state", taImpl.getState(), FAILED);
        Assert.assertEquals(("Task attempt's internal state is not " + "FAIL_FINISHING_CONTAINER"), taImpl.getInternalState(), FAIL_FINISHING_CONTAINER);
        // If the task stays in FAIL_FINISHING_CONTAINER for too long,
        // TaskAttemptListenerImpl will time out the attempt.
        taImpl.handle(new TaskAttemptEvent(taImpl.getID(), TaskAttemptEventType.TA_TIMED_OUT));
        Assert.assertEquals("Task attempt's internal state is not FAIL_CONTAINER_CLEANUP", taImpl.getInternalState(), FAIL_CONTAINER_CLEANUP);
        Assert.assertFalse("InternalError occurred", eventHandler.internalError);
    }

    @Test
    public void testMapperCustomResourceTypes() {
        initResourceTypes();
        EventHandler eventHandler = Mockito.mock(EventHandler.class);
        TaskSplitMetaInfo taskSplitMetaInfo = new TaskSplitMetaInfo();
        Clock clock = SystemClock.getInstance();
        JobConf jobConf = new JobConf();
        jobConf.setLong(((MRJobConfig.MAP_RESOURCE_TYPE_PREFIX) + (TestTaskAttempt.CUSTOM_RESOURCE_NAME)), 7L);
        TaskAttemptImpl taImpl = createMapTaskAttemptImplForTest(eventHandler, taskSplitMetaInfo, clock, jobConf);
        ResourceInformation resourceInfo = getResourceInfoFromContainerRequest(taImpl, eventHandler).getResourceInformation(TestTaskAttempt.CUSTOM_RESOURCE_NAME);
        Assert.assertEquals("Expecting the default unit (G)", "G", resourceInfo.getUnits());
        Assert.assertEquals(7L, resourceInfo.getValue());
    }

    @Test
    public void testReducerCustomResourceTypes() {
        initResourceTypes();
        EventHandler eventHandler = Mockito.mock(EventHandler.class);
        Clock clock = SystemClock.getInstance();
        JobConf jobConf = new JobConf();
        jobConf.set(((MRJobConfig.REDUCE_RESOURCE_TYPE_PREFIX) + (TestTaskAttempt.CUSTOM_RESOURCE_NAME)), "3m");
        TaskAttemptImpl taImpl = createReduceTaskAttemptImplForTest(eventHandler, clock, jobConf);
        ResourceInformation resourceInfo = getResourceInfoFromContainerRequest(taImpl, eventHandler).getResourceInformation(TestTaskAttempt.CUSTOM_RESOURCE_NAME);
        Assert.assertEquals("Expecting the specified unit (m)", "m", resourceInfo.getUnits());
        Assert.assertEquals(3L, resourceInfo.getValue());
    }

    @Test
    public void testReducerMemoryRequestViaMapreduceReduceMemoryMb() {
        EventHandler eventHandler = Mockito.mock(EventHandler.class);
        Clock clock = SystemClock.getInstance();
        JobConf jobConf = new JobConf();
        jobConf.setInt(REDUCE_MEMORY_MB, 2048);
        TaskAttemptImpl taImpl = createReduceTaskAttemptImplForTest(eventHandler, clock, jobConf);
        long memorySize = getResourceInfoFromContainerRequest(taImpl, eventHandler).getMemorySize();
        Assert.assertEquals(2048, memorySize);
    }

    @Test
    public void testReducerMemoryRequestViaMapreduceReduceResourceMemory() {
        EventHandler eventHandler = Mockito.mock(EventHandler.class);
        Clock clock = SystemClock.getInstance();
        JobConf jobConf = new JobConf();
        jobConf.set(((MRJobConfig.REDUCE_RESOURCE_TYPE_PREFIX) + (MRJobConfig.RESOURCE_TYPE_NAME_MEMORY)), "2 Gi");
        TaskAttemptImpl taImpl = createReduceTaskAttemptImplForTest(eventHandler, clock, jobConf);
        long memorySize = getResourceInfoFromContainerRequest(taImpl, eventHandler).getMemorySize();
        Assert.assertEquals(2048, memorySize);
    }

    @Test
    public void testReducerMemoryRequestDefaultMemory() {
        EventHandler eventHandler = Mockito.mock(EventHandler.class);
        Clock clock = SystemClock.getInstance();
        TaskAttemptImpl taImpl = createReduceTaskAttemptImplForTest(eventHandler, clock, new JobConf());
        long memorySize = getResourceInfoFromContainerRequest(taImpl, eventHandler).getMemorySize();
        Assert.assertEquals(DEFAULT_REDUCE_MEMORY_MB, memorySize);
    }

    @Test
    public void testReducerMemoryRequestWithoutUnits() {
        Clock clock = SystemClock.getInstance();
        for (String memoryResourceName : ImmutableList.of(RESOURCE_TYPE_NAME_MEMORY, RESOURCE_TYPE_ALTERNATIVE_NAME_MEMORY)) {
            EventHandler eventHandler = Mockito.mock(EventHandler.class);
            JobConf jobConf = new JobConf();
            jobConf.setInt(((MRJobConfig.REDUCE_RESOURCE_TYPE_PREFIX) + memoryResourceName), 2048);
            TaskAttemptImpl taImpl = createReduceTaskAttemptImplForTest(eventHandler, clock, jobConf);
            long memorySize = getResourceInfoFromContainerRequest(taImpl, eventHandler).getMemorySize();
            Assert.assertEquals(2048, memorySize);
        }
    }

    @Test
    public void testReducerMemoryRequestOverriding() {
        for (String memoryName : ImmutableList.of(RESOURCE_TYPE_NAME_MEMORY, RESOURCE_TYPE_ALTERNATIVE_NAME_MEMORY)) {
            TestTaskAttempt.TestAppender testAppender = new TestTaskAttempt.TestAppender();
            final Logger logger = Logger.getLogger(TaskAttemptImpl.class);
            try {
                logger.addAppender(testAppender);
                EventHandler eventHandler = Mockito.mock(EventHandler.class);
                Clock clock = SystemClock.getInstance();
                JobConf jobConf = new JobConf();
                jobConf.set(((MRJobConfig.REDUCE_RESOURCE_TYPE_PREFIX) + memoryName), "3Gi");
                jobConf.setInt(REDUCE_MEMORY_MB, 2048);
                TaskAttemptImpl taImpl = createReduceTaskAttemptImplForTest(eventHandler, clock, jobConf);
                long memorySize = getResourceInfoFromContainerRequest(taImpl, eventHandler).getMemorySize();
                Assert.assertEquals(3072, memorySize);
                Assert.assertTrue(testAppender.getLogEvents().stream().anyMatch(( e) -> ((e.getLevel()) == (Level.WARN)) && ((((("Configuration " + "mapreduce.reduce.resource.") + memoryName) + "=3Gi is ") + "overriding the mapreduce.reduce.memory.mb=2048 configuration").equals(e.getMessage()))));
            } finally {
                logger.removeAppender(testAppender);
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReducerMemoryRequestMultipleName() {
        EventHandler eventHandler = Mockito.mock(EventHandler.class);
        Clock clock = SystemClock.getInstance();
        JobConf jobConf = new JobConf();
        for (String memoryName : ImmutableList.of(RESOURCE_TYPE_NAME_MEMORY, RESOURCE_TYPE_ALTERNATIVE_NAME_MEMORY)) {
            jobConf.set(((MRJobConfig.REDUCE_RESOURCE_TYPE_PREFIX) + memoryName), "3Gi");
        }
        createReduceTaskAttemptImplForTest(eventHandler, clock, jobConf);
    }

    @Test
    public void testReducerCpuRequestViaMapreduceReduceCpuVcores() {
        EventHandler eventHandler = Mockito.mock(EventHandler.class);
        Clock clock = SystemClock.getInstance();
        JobConf jobConf = new JobConf();
        jobConf.setInt(REDUCE_CPU_VCORES, 3);
        TaskAttemptImpl taImpl = createReduceTaskAttemptImplForTest(eventHandler, clock, jobConf);
        int vCores = getResourceInfoFromContainerRequest(taImpl, eventHandler).getVirtualCores();
        Assert.assertEquals(3, vCores);
    }

    @Test
    public void testReducerCpuRequestViaMapreduceReduceResourceVcores() {
        EventHandler eventHandler = Mockito.mock(EventHandler.class);
        Clock clock = SystemClock.getInstance();
        JobConf jobConf = new JobConf();
        jobConf.set(((MRJobConfig.REDUCE_RESOURCE_TYPE_PREFIX) + (MRJobConfig.RESOURCE_TYPE_NAME_VCORE)), "5");
        TaskAttemptImpl taImpl = createReduceTaskAttemptImplForTest(eventHandler, clock, jobConf);
        int vCores = getResourceInfoFromContainerRequest(taImpl, eventHandler).getVirtualCores();
        Assert.assertEquals(5, vCores);
    }

    @Test
    public void testReducerCpuRequestDefaultMemory() {
        EventHandler eventHandler = Mockito.mock(EventHandler.class);
        Clock clock = SystemClock.getInstance();
        TaskAttemptImpl taImpl = createReduceTaskAttemptImplForTest(eventHandler, clock, new JobConf());
        int vCores = getResourceInfoFromContainerRequest(taImpl, eventHandler).getVirtualCores();
        Assert.assertEquals(DEFAULT_REDUCE_CPU_VCORES, vCores);
    }

    @Test
    public void testReducerCpuRequestOverriding() {
        TestTaskAttempt.TestAppender testAppender = new TestTaskAttempt.TestAppender();
        final Logger logger = Logger.getLogger(TaskAttemptImpl.class);
        try {
            logger.addAppender(testAppender);
            EventHandler eventHandler = Mockito.mock(EventHandler.class);
            Clock clock = SystemClock.getInstance();
            JobConf jobConf = new JobConf();
            jobConf.set(((MRJobConfig.REDUCE_RESOURCE_TYPE_PREFIX) + (MRJobConfig.RESOURCE_TYPE_NAME_VCORE)), "7");
            jobConf.setInt(REDUCE_CPU_VCORES, 9);
            TaskAttemptImpl taImpl = createReduceTaskAttemptImplForTest(eventHandler, clock, jobConf);
            long vCores = getResourceInfoFromContainerRequest(taImpl, eventHandler).getVirtualCores();
            Assert.assertEquals(7, vCores);
            Assert.assertTrue(testAppender.getLogEvents().stream().anyMatch(( e) -> ((e.getLevel()) == (Level.WARN)) && (("Configuration " + ("mapreduce.reduce.resource.vcores=7 is overriding the " + "mapreduce.reduce.cpu.vcores=9 configuration")).equals(e.getMessage()))));
        } finally {
            logger.removeAppender(testAppender);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReducerCustomResourceTypeWithInvalidUnit() {
        initResourceTypes();
        EventHandler eventHandler = Mockito.mock(EventHandler.class);
        Clock clock = SystemClock.getInstance();
        JobConf jobConf = new JobConf();
        jobConf.set(((MRJobConfig.REDUCE_RESOURCE_TYPE_PREFIX) + (TestTaskAttempt.CUSTOM_RESOURCE_NAME)), "3z");
        createReduceTaskAttemptImplForTest(eventHandler, clock, jobConf);
    }

    public static class MockEventHandler implements EventHandler {
        public boolean internalError;

        public TaskEvent lastTaskEvent;

        @Override
        public void handle(Event event) {
            if (event instanceof TaskEvent) {
                lastTaskEvent = ((TaskEvent) (event));
            }
            if (event instanceof JobEvent) {
                JobEvent je = ((JobEvent) (event));
                if ((JobEventType.INTERNAL_ERROR) == (je.getType())) {
                    internalError = true;
                }
            }
        }
    }
}

