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
package org.apache.flink.runtime.taskmanager;


import ConfigConstants.TASK_MANAGER_LOG_PATH_KEY;
import ExecutionState.CANCELED;
import ExecutionState.FAILED;
import ExecutionState.FINISHED;
import ExecutionState.RUNNING;
import HighAvailabilityServices.DEFAULT_JOB_ID;
import HighAvailabilityServices.DEFAULT_LEADER_ID;
import RegistrationMessages.RegisterTaskManager;
import TaskManagerOptions.DATA_PORT;
import TaskManagerOptions.MEMORY_SEGMENT_SIZE;
import TaskManagerOptions.NETWORK_BUFFERS_PER_CHANNEL;
import TaskManagerOptions.NETWORK_EXTRA_BUFFERS_PER_GATE;
import TaskManagerOptions.NETWORK_REQUEST_BACKOFF_INITIAL;
import TaskManagerOptions.NETWORK_REQUEST_BACKOFF_MAX;
import TaskMessages.UpdateTaskExecutionState;
import Tasks.AgnosticReceiver;
import Tasks.BlockingReceiver;
import TestingTaskManagerMessages.ResponseRunningTasks;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Kill;
import akka.actor.Props;
import akka.actor.Status;
import akka.japi.Creator;
import akka.testkit.JavaTestKit;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.akka.FlinkUntypedActor;
import org.apache.flink.runtime.deployment.InputChannelDeploymentDescriptor;
import org.apache.flink.runtime.deployment.InputGateDeploymentDescriptor;
import org.apache.flink.runtime.deployment.ResultPartitionDeploymentDescriptor;
import org.apache.flink.runtime.deployment.ResultPartitionLocation;
import org.apache.flink.runtime.deployment.TaskDeploymentDescriptor;
import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.execution.ExecutionState;
import org.apache.flink.runtime.executiongraph.ExecutionAttemptID;
import org.apache.flink.runtime.highavailability.HighAvailabilityServices;
import org.apache.flink.runtime.highavailability.TestingHighAvailabilityServices;
import org.apache.flink.runtime.instance.ActorGateway;
import org.apache.flink.runtime.instance.InstanceID;
import org.apache.flink.runtime.io.network.ConnectionID;
import org.apache.flink.runtime.io.network.api.writer.RecordWriter;
import org.apache.flink.runtime.io.network.partition.PartitionNotFoundException;
import org.apache.flink.runtime.io.network.partition.ResultPartitionID;
import org.apache.flink.runtime.io.network.partition.ResultPartitionType;
import org.apache.flink.runtime.jobgraph.IntermediateDataSetID;
import org.apache.flink.runtime.jobgraph.IntermediateResultPartitionID;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.jobmaster.TestingAbstractInvokables;
import org.apache.flink.runtime.leaderretrieval.SettableLeaderRetrievalService;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.messages.JobManagerMessages;
import org.apache.flink.runtime.messages.RegistrationMessages;
import org.apache.flink.runtime.messages.StackTraceSampleResponse;
import org.apache.flink.runtime.messages.TaskManagerMessages;
import org.apache.flink.runtime.messages.TaskManagerMessages.FatalError;
import org.apache.flink.runtime.messages.TaskMessages;
import org.apache.flink.runtime.taskexecutor.TaskManagerServicesConfiguration;
import org.apache.flink.runtime.testingUtils.TestingTaskManagerMessages;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.runtime.testtasks.BlockingNoOpInvokable;
import org.apache.flink.runtime.testutils.StoppableInvokable;
import org.apache.flink.types.IntValue;
import org.apache.flink.util.NetUtils;
import org.apache.flink.util.SerializedValue;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import scala.util.Failure;


/**
 * Tests for the legacy {@link TaskManager}.
 */
@SuppressWarnings("serial")
public class TaskManagerTest extends TestLogger {
    private static final Logger LOG = LoggerFactory.getLogger(TaskManagerTest.class);

    private static final FiniteDuration timeout = new FiniteDuration(1, TimeUnit.MINUTES);

    private static final FiniteDuration d = new FiniteDuration(60, TimeUnit.SECONDS);

    private static final Time timeD = Time.seconds(60L);

    private static ActorSystem system;

    static final UUID LEADER_SESSION_ID = UUID.randomUUID();

    private TestingHighAvailabilityServices highAvailabilityServices;

    @Test
    public void testSubmitAndExecuteTask() throws IOException {
        new JavaTestKit(TaskManagerTest.system) {
            {
                ActorGateway taskManager = null;
                final ActorGateway jobManager = TestingUtils.createForwardingActor(TaskManagerTest.system, getTestActor(), DEFAULT_LEADER_ID, Option.<String>empty());
                highAvailabilityServices.setJobMasterLeaderRetriever(DEFAULT_JOB_ID, new org.apache.flink.runtime.leaderretrieval.StandaloneLeaderRetrievalService(jobManager.path(), jobManager.leaderSessionID()));
                try {
                    taskManager = TestingUtils.createTaskManager(TaskManagerTest.system, highAvailabilityServices, new Configuration(), true, false);
                    final ActorGateway tm = taskManager;
                    // handle the registration
                    new Within(TaskManagerTest.d) {
                        @Override
                        protected void run() {
                            expectMsgClass(RegisterTaskManager.class);
                            final InstanceID iid = new InstanceID();
                            Assert.assertEquals(tm.actor(), getLastSender());
                            tm.tell(new RegistrationMessages.AcknowledgeRegistration(iid, 12345), jobManager);
                        }
                    };
                    final JobID jid = new JobID();
                    final JobVertexID vid = new JobVertexID();
                    final ExecutionAttemptID eid = new ExecutionAttemptID();
                    final SerializedValue<ExecutionConfig> executionConfig = new SerializedValue(new ExecutionConfig());
                    final TaskDeploymentDescriptor tdd = TaskManagerTest.createTaskDeploymentDescriptor(jid, "TestJob", vid, eid, executionConfig, "TestTask", 7, 2, 7, 0, new Configuration(), new Configuration(), TaskManagerTest.TestInvokableCorrect.class.getName(), Collections.<ResultPartitionDeploymentDescriptor>emptyList(), Collections.<InputGateDeploymentDescriptor>emptyList(), new ArrayList<org.apache.flink.runtime.blob.PermanentBlobKey>(), Collections.emptyList(), 0);
                    new Within(TaskManagerTest.d) {
                        @Override
                        protected void run() {
                            tm.tell(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd), jobManager);
                            // TaskManager should acknowledge the submission
                            // heartbeats may be interleaved
                            long deadline = (System.currentTimeMillis()) + 10000;
                            do {
                                Object message = receiveOne(TaskManagerTest.d);
                                if (message.equals(Acknowledge.get())) {
                                    break;
                                }
                            } while ((System.currentTimeMillis()) < deadline );
                            // task should have switched to running
                            Object toRunning = new TaskMessages.UpdateTaskExecutionState(new TaskExecutionState(jid, eid, ExecutionState.RUNNING));
                            // task should have switched to finished
                            Object toFinished = new TaskMessages.UpdateTaskExecutionState(new TaskExecutionState(jid, eid, ExecutionState.FINISHED));
                            deadline = (System.currentTimeMillis()) + 10000;
                            do {
                                Object message = receiveOne(TaskManagerTest.d);
                                if (message.equals(toRunning)) {
                                    break;
                                } else
                                    if (!(message instanceof TaskManagerMessages.Heartbeat)) {
                                        Assert.fail(("Unexpected message: " + message));
                                    }

                            } while ((System.currentTimeMillis()) < deadline );
                            deadline = (System.currentTimeMillis()) + 10000;
                            do {
                                Object message = receiveOne(TaskManagerTest.d);
                                if (message.equals(toFinished)) {
                                    break;
                                } else
                                    if (!(message instanceof TaskManagerMessages.Heartbeat)) {
                                        Assert.fail(("Unexpected message: " + message));
                                    }

                            } while ((System.currentTimeMillis()) < deadline );
                        }
                    };
                } finally {
                    // shut down the actors
                    TestingUtils.stopActor(taskManager);
                    TestingUtils.stopActor(jobManager);
                }
            }
        };
    }

    @Test
    public void testJobSubmissionAndCanceling() {
        new JavaTestKit(TaskManagerTest.system) {
            {
                ActorGateway jobManager = null;
                ActorGateway taskManager = null;
                final ActorGateway testActorGateway = new org.apache.flink.runtime.instance.AkkaActorGateway(getTestActor(), TaskManagerTest.LEADER_SESSION_ID);
                try {
                    ActorRef jm = TaskManagerTest.system.actorOf(Props.create(TaskManagerTest.SimpleJobManager.class, TaskManagerTest.LEADER_SESSION_ID));
                    jobManager = new org.apache.flink.runtime.instance.AkkaActorGateway(jm, TaskManagerTest.LEADER_SESSION_ID);
                    highAvailabilityServices.setJobMasterLeaderRetriever(DEFAULT_JOB_ID, new org.apache.flink.runtime.leaderretrieval.StandaloneLeaderRetrievalService(jobManager.path(), jobManager.leaderSessionID()));
                    taskManager = TestingUtils.createTaskManager(TaskManagerTest.system, highAvailabilityServices, new Configuration(), true, true);
                    final JobID jid1 = new JobID();
                    final JobID jid2 = new JobID();
                    JobVertexID vid1 = new JobVertexID();
                    JobVertexID vid2 = new JobVertexID();
                    final ExecutionAttemptID eid1 = new ExecutionAttemptID();
                    final ExecutionAttemptID eid2 = new ExecutionAttemptID();
                    final TaskDeploymentDescriptor tdd1 = TaskManagerTest.createTaskDeploymentDescriptor(jid1, "TestJob1", vid1, eid1, new SerializedValue(new ExecutionConfig()), "TestTask1", 5, 1, 5, 0, new Configuration(), new Configuration(), TaskManagerTest.TestInvokableBlockingCancelable.class.getName(), Collections.<ResultPartitionDeploymentDescriptor>emptyList(), Collections.<InputGateDeploymentDescriptor>emptyList(), new ArrayList(), Collections.<URL>emptyList(), 0);
                    final TaskDeploymentDescriptor tdd2 = TaskManagerTest.createTaskDeploymentDescriptor(jid2, "TestJob2", vid2, eid2, new SerializedValue(new ExecutionConfig()), "TestTask2", 7, 2, 7, 0, new Configuration(), new Configuration(), TaskManagerTest.TestInvokableBlockingCancelable.class.getName(), Collections.<ResultPartitionDeploymentDescriptor>emptyList(), Collections.<InputGateDeploymentDescriptor>emptyList(), new ArrayList(), Collections.emptyList(), 0);
                    final ActorGateway tm = taskManager;
                    new Within(TaskManagerTest.d) {
                        @Override
                        protected void run() {
                            try {
                                Future<Object> t1Running = tm.ask(new TestingTaskManagerMessages.NotifyWhenTaskIsRunning(eid1), TaskManagerTest.timeout);
                                Future<Object> t2Running = tm.ask(new TestingTaskManagerMessages.NotifyWhenTaskIsRunning(eid2), TaskManagerTest.timeout);
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd1), testActorGateway);
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd2), testActorGateway);
                                expectMsgEquals(Acknowledge.get());
                                expectMsgEquals(Acknowledge.get());
                                Await.ready(t1Running, TaskManagerTest.d);
                                Await.ready(t2Running, TaskManagerTest.d);
                                tm.tell(TestingTaskManagerMessages.getRequestRunningTasksMessage(), testActorGateway);
                                Map<ExecutionAttemptID, Task> runningTasks = expectMsgClass(ResponseRunningTasks.class).asJava();
                                Assert.assertEquals(2, runningTasks.size());
                                Task t1 = runningTasks.get(eid1);
                                Task t2 = runningTasks.get(eid2);
                                Assert.assertNotNull(t1);
                                Assert.assertNotNull(t2);
                                Assert.assertEquals(RUNNING, t1.getExecutionState());
                                Assert.assertEquals(RUNNING, t2.getExecutionState());
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.CancelTask(eid1), testActorGateway);
                                expectMsgEquals(Acknowledge.get());
                                Future<Object> response = tm.ask(new TestingTaskManagerMessages.NotifyWhenTaskRemoved(eid1), TaskManagerTest.timeout);
                                Await.ready(response, TaskManagerTest.d);
                                Assert.assertEquals(CANCELED, t1.getExecutionState());
                                tm.tell(TestingTaskManagerMessages.getRequestRunningTasksMessage(), testActorGateway);
                                runningTasks = expectMsgClass(ResponseRunningTasks.class).asJava();
                                Assert.assertEquals(1, runningTasks.size());
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.CancelTask(eid1), testActorGateway);
                                expectMsgEquals(Acknowledge.get());
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.CancelTask(eid2), testActorGateway);
                                expectMsgEquals(Acknowledge.get());
                                response = tm.ask(new TestingTaskManagerMessages.NotifyWhenTaskRemoved(eid2), TaskManagerTest.timeout);
                                Await.ready(response, TaskManagerTest.d);
                                Assert.assertEquals(CANCELED, t2.getExecutionState());
                                tm.tell(TestingTaskManagerMessages.getRequestRunningTasksMessage(), testActorGateway);
                                runningTasks = expectMsgClass(ResponseRunningTasks.class).asJava();
                                Assert.assertEquals(0, runningTasks.size());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Assert.fail(e.getMessage());
                            }
                        }
                    };
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail(e.getMessage());
                } finally {
                    TestingUtils.stopActor(taskManager);
                    TestingUtils.stopActor(jobManager);
                }
            }
        };
    }

    @Test
    public void testJobSubmissionAndStop() throws Exception {
        new JavaTestKit(TaskManagerTest.system) {
            {
                ActorGateway jobManager = null;
                ActorGateway taskManager = null;
                final ActorGateway testActorGateway = new org.apache.flink.runtime.instance.AkkaActorGateway(getTestActor(), TaskManagerTest.LEADER_SESSION_ID);
                try {
                    ActorRef jm = TaskManagerTest.system.actorOf(Props.create(TaskManagerTest.SimpleJobManager.class, TaskManagerTest.LEADER_SESSION_ID));
                    jobManager = new org.apache.flink.runtime.instance.AkkaActorGateway(jm, TaskManagerTest.LEADER_SESSION_ID);
                    highAvailabilityServices.setJobMasterLeaderRetriever(DEFAULT_JOB_ID, new org.apache.flink.runtime.leaderretrieval.StandaloneLeaderRetrievalService(jobManager.path(), jobManager.leaderSessionID()));
                    taskManager = TestingUtils.createTaskManager(TaskManagerTest.system, highAvailabilityServices, new Configuration(), true, true);
                    final JobID jid1 = new JobID();
                    final JobID jid2 = new JobID();
                    JobVertexID vid1 = new JobVertexID();
                    JobVertexID vid2 = new JobVertexID();
                    final ExecutionAttemptID eid1 = new ExecutionAttemptID();
                    final ExecutionAttemptID eid2 = new ExecutionAttemptID();
                    final SerializedValue<ExecutionConfig> executionConfig = new SerializedValue(new ExecutionConfig());
                    final TaskDeploymentDescriptor tdd1 = TaskManagerTest.createTaskDeploymentDescriptor(jid1, "TestJob", vid1, eid1, executionConfig, "TestTask1", 5, 1, 5, 0, new Configuration(), new Configuration(), StoppableInvokable.class.getName(), Collections.<ResultPartitionDeploymentDescriptor>emptyList(), Collections.<InputGateDeploymentDescriptor>emptyList(), new ArrayList(), Collections.emptyList(), 0);
                    final TaskDeploymentDescriptor tdd2 = TaskManagerTest.createTaskDeploymentDescriptor(jid2, "TestJob", vid2, eid2, executionConfig, "TestTask2", 7, 2, 7, 0, new Configuration(), new Configuration(), TaskManagerTest.TestInvokableBlockingCancelable.class.getName(), Collections.<ResultPartitionDeploymentDescriptor>emptyList(), Collections.<InputGateDeploymentDescriptor>emptyList(), new ArrayList(), Collections.emptyList(), 0);
                    final ActorGateway tm = taskManager;
                    new Within(TaskManagerTest.d) {
                        @Override
                        protected void run() {
                            try {
                                Future<Object> t1Running = tm.ask(new TestingTaskManagerMessages.NotifyWhenTaskIsRunning(eid1), TaskManagerTest.timeout);
                                Future<Object> t2Running = tm.ask(new TestingTaskManagerMessages.NotifyWhenTaskIsRunning(eid2), TaskManagerTest.timeout);
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd1), testActorGateway);
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd2), testActorGateway);
                                expectMsgEquals(Acknowledge.get());
                                expectMsgEquals(Acknowledge.get());
                                Await.ready(t1Running, TaskManagerTest.d);
                                Await.ready(t2Running, TaskManagerTest.d);
                                tm.tell(TestingTaskManagerMessages.getRequestRunningTasksMessage(), testActorGateway);
                                Map<ExecutionAttemptID, Task> runningTasks = expectMsgClass(ResponseRunningTasks.class).asJava();
                                Assert.assertEquals(2, runningTasks.size());
                                Task t1 = runningTasks.get(eid1);
                                Task t2 = runningTasks.get(eid2);
                                Assert.assertNotNull(t1);
                                Assert.assertNotNull(t2);
                                Assert.assertEquals(RUNNING, t1.getExecutionState());
                                Assert.assertEquals(RUNNING, t2.getExecutionState());
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.StopTask(eid1), testActorGateway);
                                expectMsgEquals(Acknowledge.get());
                                Future<Object> response = tm.ask(new TestingTaskManagerMessages.NotifyWhenTaskRemoved(eid1), TaskManagerTest.timeout);
                                Await.ready(response, TaskManagerTest.d);
                                Assert.assertEquals(FINISHED, t1.getExecutionState());
                                tm.tell(TestingTaskManagerMessages.getRequestRunningTasksMessage(), testActorGateway);
                                runningTasks = expectMsgClass(ResponseRunningTasks.class).asJava();
                                Assert.assertEquals(1, runningTasks.size());
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.StopTask(eid1), testActorGateway);
                                expectMsgEquals(Acknowledge.get());
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.StopTask(eid2), testActorGateway);
                                expectMsgClass(Failure.class);
                                Assert.assertEquals(RUNNING, t2.getExecutionState());
                                tm.tell(TestingTaskManagerMessages.getRequestRunningTasksMessage(), testActorGateway);
                                runningTasks = expectMsgClass(ResponseRunningTasks.class).asJava();
                                Assert.assertEquals(1, runningTasks.size());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Assert.fail(e.getMessage());
                            }
                        }
                    };
                } finally {
                    TestingUtils.stopActor(taskManager);
                    TestingUtils.stopActor(jobManager);
                }
            }
        };
    }

    @Test
    public void testGateChannelEdgeMismatch() {
        new JavaTestKit(TaskManagerTest.system) {
            {
                ActorGateway jobManager = null;
                ActorGateway taskManager = null;
                final ActorGateway testActorGateway = new org.apache.flink.runtime.instance.AkkaActorGateway(getTestActor(), TaskManagerTest.LEADER_SESSION_ID);
                try {
                    ActorRef jm = TaskManagerTest.system.actorOf(Props.create(TaskManagerTest.SimpleJobManager.class, TaskManagerTest.LEADER_SESSION_ID));
                    jobManager = new org.apache.flink.runtime.instance.AkkaActorGateway(jm, TaskManagerTest.LEADER_SESSION_ID);
                    highAvailabilityServices.setJobMasterLeaderRetriever(DEFAULT_JOB_ID, new org.apache.flink.runtime.leaderretrieval.StandaloneLeaderRetrievalService(jobManager.path(), jobManager.leaderSessionID()));
                    taskManager = TestingUtils.createTaskManager(TaskManagerTest.system, highAvailabilityServices, new Configuration(), true, true);
                    final ActorGateway tm = taskManager;
                    final JobID jid = new JobID();
                    JobVertexID vid1 = new JobVertexID();
                    JobVertexID vid2 = new JobVertexID();
                    final ExecutionAttemptID eid1 = new ExecutionAttemptID();
                    final ExecutionAttemptID eid2 = new ExecutionAttemptID();
                    final TaskDeploymentDescriptor tdd1 = TaskManagerTest.createTaskDeploymentDescriptor(jid, "TestJob", vid1, eid1, new SerializedValue(new ExecutionConfig()), "Sender", 1, 0, 1, 0, new Configuration(), new Configuration(), TestingAbstractInvokables.Sender.class.getName(), Collections.<ResultPartitionDeploymentDescriptor>emptyList(), Collections.<InputGateDeploymentDescriptor>emptyList(), new ArrayList(), Collections.emptyList(), 0);
                    final TaskDeploymentDescriptor tdd2 = TaskManagerTest.createTaskDeploymentDescriptor(jid, "TestJob", vid2, eid2, new SerializedValue(new ExecutionConfig()), "Receiver", 7, 2, 7, 0, new Configuration(), new Configuration(), TestingAbstractInvokables.Receiver.class.getName(), Collections.<ResultPartitionDeploymentDescriptor>emptyList(), Collections.<InputGateDeploymentDescriptor>emptyList(), new ArrayList(), Collections.emptyList(), 0);
                    new Within(TaskManagerTest.d) {
                        @Override
                        protected void run() {
                            try {
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd1), testActorGateway);
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd2), testActorGateway);
                                expectMsgEquals(Acknowledge.get());
                                expectMsgEquals(Acknowledge.get());
                                tm.tell(new TestingTaskManagerMessages.NotifyWhenTaskRemoved(eid1), testActorGateway);
                                tm.tell(new TestingTaskManagerMessages.NotifyWhenTaskRemoved(eid2), testActorGateway);
                                expectMsgEquals(true);
                                expectMsgEquals(true);
                                tm.tell(TestingTaskManagerMessages.getRequestRunningTasksMessage(), testActorGateway);
                                Map<ExecutionAttemptID, Task> tasks = expectMsgClass(ResponseRunningTasks.class).asJava();
                                Assert.assertEquals(0, tasks.size());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Assert.fail(e.getMessage());
                            }
                        }
                    };
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail(e.getMessage());
                } finally {
                    // shut down the actors
                    TestingUtils.stopActor(taskManager);
                    TestingUtils.stopActor(jobManager);
                }
            }
        };
    }

    @Test
    public void testRunJobWithForwardChannel() {
        new JavaTestKit(TaskManagerTest.system) {
            {
                ActorGateway jobManager = null;
                ActorGateway taskManager = null;
                final ActorGateway testActorGateway = new org.apache.flink.runtime.instance.AkkaActorGateway(getTestActor(), TaskManagerTest.LEADER_SESSION_ID);
                try {
                    final JobID jid = new JobID();
                    JobVertexID vid1 = new JobVertexID();
                    JobVertexID vid2 = new JobVertexID();
                    final ExecutionAttemptID eid1 = new ExecutionAttemptID();
                    final ExecutionAttemptID eid2 = new ExecutionAttemptID();
                    ActorRef jm = TaskManagerTest.system.actorOf(Props.create(new TaskManagerTest.SimpleLookupJobManagerCreator(TaskManagerTest.LEADER_SESSION_ID)));
                    jobManager = new org.apache.flink.runtime.instance.AkkaActorGateway(jm, TaskManagerTest.LEADER_SESSION_ID);
                    highAvailabilityServices.setJobMasterLeaderRetriever(DEFAULT_JOB_ID, new org.apache.flink.runtime.leaderretrieval.StandaloneLeaderRetrievalService(jobManager.path(), jobManager.leaderSessionID()));
                    taskManager = TestingUtils.createTaskManager(TaskManagerTest.system, highAvailabilityServices, new Configuration(), true, true);
                    final ActorGateway tm = taskManager;
                    IntermediateResultPartitionID partitionId = new IntermediateResultPartitionID();
                    List<ResultPartitionDeploymentDescriptor> irpdd = new ArrayList<ResultPartitionDeploymentDescriptor>();
                    irpdd.add(new ResultPartitionDeploymentDescriptor(new IntermediateDataSetID(), partitionId, ResultPartitionType.PIPELINED, 1, 1, true));
                    InputGateDeploymentDescriptor ircdd = new InputGateDeploymentDescriptor(new IntermediateDataSetID(), ResultPartitionType.PIPELINED, 0, new InputChannelDeploymentDescriptor[]{ new InputChannelDeploymentDescriptor(new ResultPartitionID(partitionId, eid1), ResultPartitionLocation.createLocal()) });
                    final TaskDeploymentDescriptor tdd1 = TaskManagerTest.createTaskDeploymentDescriptor(jid, "TestJob", vid1, eid1, new SerializedValue(new ExecutionConfig()), "Sender", 1, 0, 1, 0, new Configuration(), new Configuration(), TestingAbstractInvokables.Sender.class.getName(), irpdd, Collections.<InputGateDeploymentDescriptor>emptyList(), new ArrayList(), Collections.emptyList(), 0);
                    final TaskDeploymentDescriptor tdd2 = TaskManagerTest.createTaskDeploymentDescriptor(jid, "TestJob", vid2, eid2, new SerializedValue(new ExecutionConfig()), "Receiver", 7, 2, 7, 0, new Configuration(), new Configuration(), TestingAbstractInvokables.Receiver.class.getName(), Collections.<ResultPartitionDeploymentDescriptor>emptyList(), Collections.singletonList(ircdd), new ArrayList(), Collections.emptyList(), 0);
                    new Within(TaskManagerTest.d) {
                        @Override
                        protected void run() {
                            try {
                                Future<Object> t1Running = tm.ask(new TestingTaskManagerMessages.NotifyWhenTaskIsRunning(eid1), TaskManagerTest.timeout);
                                Future<Object> t2Running = tm.ask(new TestingTaskManagerMessages.NotifyWhenTaskIsRunning(eid2), TaskManagerTest.timeout);
                                // submit the sender task
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd1), testActorGateway);
                                expectMsgEquals(Acknowledge.get());
                                // wait until the sender task is running
                                Await.ready(t1Running, TaskManagerTest.d);
                                // only now (after the sender is running), submit the receiver task
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd2), testActorGateway);
                                expectMsgEquals(Acknowledge.get());
                                // wait until the receiver task is running
                                Await.ready(t2Running, TaskManagerTest.d);
                                tm.tell(TestingTaskManagerMessages.getRequestRunningTasksMessage(), testActorGateway);
                                Map<ExecutionAttemptID, Task> tasks = expectMsgClass(ResponseRunningTasks.class).asJava();
                                Task t1 = tasks.get(eid1);
                                Task t2 = tasks.get(eid2);
                                // wait until the tasks are done. thread races may cause the tasks to be done before
                                // we get to the check, so we need to guard the check
                                if (t1 != null) {
                                    Future<Object> response = tm.ask(new TestingTaskManagerMessages.NotifyWhenTaskRemoved(eid1), TaskManagerTest.timeout);
                                    Await.ready(response, TaskManagerTest.d);
                                }
                                if (t2 != null) {
                                    Future<Object> response = tm.ask(new TestingTaskManagerMessages.NotifyWhenTaskRemoved(eid2), TaskManagerTest.timeout);
                                    Await.ready(response, TaskManagerTest.d);
                                    Assert.assertEquals(FINISHED, t2.getExecutionState());
                                }
                                tm.tell(TestingTaskManagerMessages.getRequestRunningTasksMessage(), testActorGateway);
                                tasks = expectMsgClass(ResponseRunningTasks.class).asJava();
                                Assert.assertEquals(0, tasks.size());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Assert.fail(e.getMessage());
                            }
                        }
                    };
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail(e.getMessage());
                } finally {
                    // shut down the actors
                    TestingUtils.stopActor(taskManager);
                    TestingUtils.stopActor(jobManager);
                }
            }
        };
    }

    @Test
    public void testCancellingDependentAndStateUpdateFails() {
        // this tests creates two tasks. the sender sends data, and fails to send the
        // state update back to the job manager
        // the second one blocks to be canceled
        new JavaTestKit(TaskManagerTest.system) {
            {
                ActorGateway jobManager = null;
                ActorGateway taskManager = null;
                final ActorGateway testActorGateway = new org.apache.flink.runtime.instance.AkkaActorGateway(getTestActor(), TaskManagerTest.LEADER_SESSION_ID);
                try {
                    final JobID jid = new JobID();
                    JobVertexID vid1 = new JobVertexID();
                    JobVertexID vid2 = new JobVertexID();
                    final ExecutionAttemptID eid1 = new ExecutionAttemptID();
                    final ExecutionAttemptID eid2 = new ExecutionAttemptID();
                    ActorRef jm = TaskManagerTest.system.actorOf(Props.create(new TaskManagerTest.SimpleLookupFailingUpdateJobManagerCreator(TaskManagerTest.LEADER_SESSION_ID, eid2)));
                    jobManager = new org.apache.flink.runtime.instance.AkkaActorGateway(jm, TaskManagerTest.LEADER_SESSION_ID);
                    highAvailabilityServices.setJobMasterLeaderRetriever(DEFAULT_JOB_ID, new org.apache.flink.runtime.leaderretrieval.StandaloneLeaderRetrievalService(jobManager.path(), jobManager.leaderSessionID()));
                    taskManager = TestingUtils.createTaskManager(TaskManagerTest.system, highAvailabilityServices, new Configuration(), true, true);
                    final ActorGateway tm = taskManager;
                    IntermediateResultPartitionID partitionId = new IntermediateResultPartitionID();
                    List<ResultPartitionDeploymentDescriptor> irpdd = new ArrayList<ResultPartitionDeploymentDescriptor>();
                    irpdd.add(new ResultPartitionDeploymentDescriptor(new IntermediateDataSetID(), partitionId, ResultPartitionType.PIPELINED, 1, 1, true));
                    InputGateDeploymentDescriptor ircdd = new InputGateDeploymentDescriptor(new IntermediateDataSetID(), ResultPartitionType.PIPELINED, 0, new InputChannelDeploymentDescriptor[]{ new InputChannelDeploymentDescriptor(new ResultPartitionID(partitionId, eid1), ResultPartitionLocation.createLocal()) });
                    final TaskDeploymentDescriptor tdd1 = TaskManagerTest.createTaskDeploymentDescriptor(jid, "TestJob", vid1, eid1, new SerializedValue(new ExecutionConfig()), "Sender", 1, 0, 1, 0, new Configuration(), new Configuration(), TestingAbstractInvokables.Sender.class.getName(), irpdd, Collections.<InputGateDeploymentDescriptor>emptyList(), new ArrayList(), Collections.emptyList(), 0);
                    final TaskDeploymentDescriptor tdd2 = TaskManagerTest.createTaskDeploymentDescriptor(jid, "TestJob", vid2, eid2, new SerializedValue(new ExecutionConfig()), "Receiver", 7, 2, 7, 0, new Configuration(), new Configuration(), BlockingReceiver.class.getName(), Collections.<ResultPartitionDeploymentDescriptor>emptyList(), Collections.singletonList(ircdd), new ArrayList(), Collections.emptyList(), 0);
                    new Within(TaskManagerTest.d) {
                        @Override
                        protected void run() {
                            try {
                                Future<Object> t1Running = tm.ask(new TestingTaskManagerMessages.NotifyWhenTaskIsRunning(eid1), TaskManagerTest.timeout);
                                Future<Object> t2Running = tm.ask(new TestingTaskManagerMessages.NotifyWhenTaskIsRunning(eid2), TaskManagerTest.timeout);
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd2), testActorGateway);
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd1), testActorGateway);
                                expectMsgEquals(Acknowledge.get());
                                expectMsgEquals(Acknowledge.get());
                                Await.ready(t1Running, TaskManagerTest.d);
                                Await.ready(t2Running, TaskManagerTest.d);
                                tm.tell(TestingTaskManagerMessages.getRequestRunningTasksMessage(), testActorGateway);
                                Map<ExecutionAttemptID, Task> tasks = expectMsgClass(ResponseRunningTasks.class).asJava();
                                Task t1 = tasks.get(eid1);
                                Task t2 = tasks.get(eid2);
                                tm.tell(new org.apache.flink.runtime.messages.TaskMessages.CancelTask(eid2), testActorGateway);
                                expectMsgEquals(Acknowledge.get());
                                if (t2 != null) {
                                    Future<Object> response = tm.ask(new TestingTaskManagerMessages.NotifyWhenTaskRemoved(eid2), TaskManagerTest.timeout);
                                    Await.ready(response, TaskManagerTest.d);
                                }
                                if (t1 != null) {
                                    if ((t1.getExecutionState()) == (ExecutionState.RUNNING)) {
                                        tm.tell(new org.apache.flink.runtime.messages.TaskMessages.CancelTask(eid1), testActorGateway);
                                        expectMsgEquals(Acknowledge.get());
                                    }
                                    Future<Object> response = tm.ask(new TestingTaskManagerMessages.NotifyWhenTaskRemoved(eid1), TaskManagerTest.timeout);
                                    Await.ready(response, TaskManagerTest.d);
                                }
                                tm.tell(TestingTaskManagerMessages.getRequestRunningTasksMessage(), testActorGateway);
                                tasks = expectMsgClass(ResponseRunningTasks.class).asJava();
                                Assert.assertEquals(0, tasks.size());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Assert.fail(e.getMessage());
                            }
                        }
                    };
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail(e.getMessage());
                } finally {
                    // shut down the actors
                    TestingUtils.stopActor(taskManager);
                    TestingUtils.stopActor(jobManager);
                }
            }
        };
    }

    /**
     * Tests that repeated remote {@link PartitionNotFoundException}s ultimately fail the receiver.
     */
    @Test
    public void testRemotePartitionNotFound() throws Exception {
        new JavaTestKit(TaskManagerTest.system) {
            {
                ActorGateway jobManager = null;
                ActorGateway taskManager = null;
                final ActorGateway testActorGateway = new org.apache.flink.runtime.instance.AkkaActorGateway(getTestActor(), TaskManagerTest.LEADER_SESSION_ID);
                try {
                    final IntermediateDataSetID resultId = new IntermediateDataSetID();
                    // Create the JM
                    ActorRef jm = TaskManagerTest.system.actorOf(Props.create(new TaskManagerTest.SimplePartitionStateLookupJobManagerCreator(TaskManagerTest.LEADER_SESSION_ID, getTestActor())));
                    jobManager = new org.apache.flink.runtime.instance.AkkaActorGateway(jm, TaskManagerTest.LEADER_SESSION_ID);
                    highAvailabilityServices.setJobMasterLeaderRetriever(DEFAULT_JOB_ID, new org.apache.flink.runtime.leaderretrieval.StandaloneLeaderRetrievalService(jobManager.path(), jobManager.leaderSessionID()));
                    final int dataPort = NetUtils.getAvailablePort();
                    Configuration config = new Configuration();
                    config.setInteger(DATA_PORT, dataPort);
                    config.setInteger(NETWORK_REQUEST_BACKOFF_INITIAL, 100);
                    config.setInteger(NETWORK_REQUEST_BACKOFF_MAX, 200);
                    taskManager = TestingUtils.createTaskManager(TaskManagerTest.system, highAvailabilityServices, config, false, true);
                    // ---------------------------------------------------------------------------------
                    final ActorGateway tm = taskManager;
                    final JobID jid = new JobID();
                    final JobVertexID vid = new JobVertexID();
                    final ExecutionAttemptID eid = new ExecutionAttemptID();
                    final ResultPartitionID partitionId = new ResultPartitionID();
                    // Remote location (on the same TM though) for the partition
                    final ResultPartitionLocation loc = ResultPartitionLocation.createRemote(new ConnectionID(new InetSocketAddress("localhost", dataPort), 0));
                    final InputChannelDeploymentDescriptor[] icdd = new InputChannelDeploymentDescriptor[]{ new InputChannelDeploymentDescriptor(partitionId, loc) };
                    final InputGateDeploymentDescriptor igdd = new InputGateDeploymentDescriptor(resultId, ResultPartitionType.PIPELINED, 0, icdd);
                    final TaskDeploymentDescriptor tdd = TaskManagerTest.createTaskDeploymentDescriptor(jid, "TestJob", vid, eid, new SerializedValue(new ExecutionConfig()), "Receiver", 1, 0, 1, 0, new Configuration(), new Configuration(), AgnosticReceiver.class.getName(), Collections.<ResultPartitionDeploymentDescriptor>emptyList(), Collections.singletonList(igdd), Collections.emptyList(), Collections.emptyList(), 0);
                    new Within(TaskManagerTest.d) {
                        @Override
                        protected void run() {
                            // Submit the task
                            tm.tell(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd), testActorGateway);
                            expectMsgClass(Acknowledge.get().getClass());
                            // Wait to be notified about the final execution state by the mock JM
                            TaskExecutionState msg = expectMsgClass(TaskExecutionState.class);
                            // The task should fail after repeated requests
                            Assert.assertEquals(FAILED, msg.getExecutionState());
                            Throwable t = msg.getError(ClassLoader.getSystemClassLoader());
                            Assert.assertEquals(("Thrown exception was not a PartitionNotFoundException: " + (t.getMessage())), PartitionNotFoundException.class, t.getClass());
                        }
                    };
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail(e.getMessage());
                } finally {
                    TestingUtils.stopActor(taskManager);
                    TestingUtils.stopActor(jobManager);
                }
            }
        };
    }

    @Test
    public void testTaskManagerServicesConfiguration() throws Exception {
        // set some non-default values
        final Configuration config = new Configuration();
        config.setInteger(NETWORK_REQUEST_BACKOFF_INITIAL, 100);
        config.setInteger(NETWORK_REQUEST_BACKOFF_MAX, 200);
        config.setInteger(NETWORK_BUFFERS_PER_CHANNEL, 10);
        config.setInteger(NETWORK_EXTRA_BUFFERS_PER_GATE, 100);
        TaskManagerServicesConfiguration tmConfig = TaskManagerServicesConfiguration.fromConfiguration(config, InetAddress.getLoopbackAddress(), true);
        Assert.assertEquals(tmConfig.getNetworkConfig().partitionRequestInitialBackoff(), 100);
        Assert.assertEquals(tmConfig.getNetworkConfig().partitionRequestMaxBackoff(), 200);
        Assert.assertEquals(tmConfig.getNetworkConfig().networkBuffersPerChannel(), 10);
        Assert.assertEquals(tmConfig.getNetworkConfig().floatingNetworkBuffersPerGate(), 100);
    }

    /**
     * Tests that repeated local {@link PartitionNotFoundException}s ultimately fail the receiver.
     */
    @Test
    public void testLocalPartitionNotFound() throws Exception {
        new JavaTestKit(TaskManagerTest.system) {
            {
                ActorGateway jobManager = null;
                ActorGateway taskManager = null;
                final ActorGateway testActorGateway = new org.apache.flink.runtime.instance.AkkaActorGateway(getTestActor(), TaskManagerTest.LEADER_SESSION_ID);
                try {
                    final IntermediateDataSetID resultId = new IntermediateDataSetID();
                    // Create the JM
                    ActorRef jm = TaskManagerTest.system.actorOf(Props.create(new TaskManagerTest.SimplePartitionStateLookupJobManagerCreator(TaskManagerTest.LEADER_SESSION_ID, getTestActor())));
                    jobManager = new org.apache.flink.runtime.instance.AkkaActorGateway(jm, TaskManagerTest.LEADER_SESSION_ID);
                    highAvailabilityServices.setJobMasterLeaderRetriever(DEFAULT_JOB_ID, new org.apache.flink.runtime.leaderretrieval.StandaloneLeaderRetrievalService(jobManager.path(), jobManager.leaderSessionID()));
                    final Configuration config = new Configuration();
                    config.setInteger(NETWORK_REQUEST_BACKOFF_INITIAL, 100);
                    config.setInteger(NETWORK_REQUEST_BACKOFF_MAX, 200);
                    taskManager = TestingUtils.createTaskManager(TaskManagerTest.system, highAvailabilityServices, config, true, true);
                    // ---------------------------------------------------------------------------------
                    final ActorGateway tm = taskManager;
                    final JobID jid = new JobID();
                    final JobVertexID vid = new JobVertexID();
                    final ExecutionAttemptID eid = new ExecutionAttemptID();
                    final ResultPartitionID partitionId = new ResultPartitionID();
                    // Local location (on the same TM though) for the partition
                    final ResultPartitionLocation loc = ResultPartitionLocation.createLocal();
                    final InputChannelDeploymentDescriptor[] icdd = new InputChannelDeploymentDescriptor[]{ new InputChannelDeploymentDescriptor(partitionId, loc) };
                    final InputGateDeploymentDescriptor igdd = new InputGateDeploymentDescriptor(resultId, ResultPartitionType.PIPELINED, 0, icdd);
                    final TaskDeploymentDescriptor tdd = TaskManagerTest.createTaskDeploymentDescriptor(jid, "TestJob", vid, eid, new SerializedValue(new ExecutionConfig()), "Receiver", 1, 0, 1, 0, new Configuration(), new Configuration(), AgnosticReceiver.class.getName(), Collections.<ResultPartitionDeploymentDescriptor>emptyList(), Collections.singletonList(igdd), Collections.emptyList(), Collections.emptyList(), 0);
                    new Within(new FiniteDuration(120, TimeUnit.SECONDS)) {
                        @Override
                        protected void run() {
                            // Submit the task
                            tm.tell(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd), testActorGateway);
                            expectMsgClass(Acknowledge.get().getClass());
                            // Wait to be notified about the final execution state by the mock JM
                            TaskExecutionState msg = expectMsgClass(TaskExecutionState.class);
                            // The task should fail after repeated requests
                            Assert.assertEquals(msg.getExecutionState(), FAILED);
                            Throwable error = msg.getError(getClass().getClassLoader());
                            if ((error.getClass()) != (PartitionNotFoundException.class)) {
                                error.printStackTrace();
                                Assert.fail(("Wrong exception: " + (error.getMessage())));
                            }
                        }
                    };
                } catch (Exception e) {
                    e.printStackTrace();
                    Assert.fail(e.getMessage());
                } finally {
                    TestingUtils.stopActor(taskManager);
                    TestingUtils.stopActor(jobManager);
                }
            }
        };
    }

    @Test
    public void testLogNotFoundHandling() throws Exception {
        new JavaTestKit(TaskManagerTest.system) {
            {
                // we require a JobManager so that the BlobService is also started
                ActorGateway jobManager = null;
                ActorGateway taskManager = null;
                try {
                    // Create the JM
                    ActorRef jm = TaskManagerTest.system.actorOf(Props.create(new TaskManagerTest.SimplePartitionStateLookupJobManagerCreator(TaskManagerTest.LEADER_SESSION_ID, getTestActor())));
                    jobManager = new org.apache.flink.runtime.instance.AkkaActorGateway(jm, TaskManagerTest.LEADER_SESSION_ID);
                    final int dataPort = NetUtils.getAvailablePort();
                    Configuration config = new Configuration();
                    config.setInteger(DATA_PORT, dataPort);
                    config.setInteger(NETWORK_REQUEST_BACKOFF_INITIAL, 100);
                    config.setInteger(NETWORK_REQUEST_BACKOFF_MAX, 200);
                    config.setString(TASK_MANAGER_LOG_PATH_KEY, "/i/dont/exist");
                    highAvailabilityServices.setJobMasterLeaderRetriever(DEFAULT_JOB_ID, new org.apache.flink.runtime.leaderretrieval.StandaloneLeaderRetrievalService(jobManager.path(), jobManager.leaderSessionID()));
                    taskManager = TestingUtils.createTaskManager(TaskManagerTest.system, highAvailabilityServices, config, false, true);
                    // ---------------------------------------------------------------------------------
                    final ActorGateway tm = taskManager;
                    new Within(TaskManagerTest.d) {
                        @Override
                        protected void run() {
                            Future<Object> logFuture = tm.ask(TaskManagerMessages.getRequestTaskManagerLog(), TaskManagerTest.timeout);
                            try {
                                Await.result(logFuture, TaskManagerTest.timeout);
                                Assert.fail();
                            } catch (Exception e) {
                                Assert.assertTrue(e.getMessage().startsWith("TaskManager log files are unavailable. Log file could not be found at"));
                            }
                        }
                    };
                } finally {
                    TestingUtils.stopActor(taskManager);
                    TestingUtils.stopActor(jobManager);
                }
            }
        };
    }

    // ------------------------------------------------------------------------
    // Stack trace sample
    // ------------------------------------------------------------------------
    /**
     * Tests sampling of task stack traces.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testTriggerStackTraceSampleMessage() throws Exception {
        new JavaTestKit(TaskManagerTest.system) {
            {
                ActorGateway taskManagerActorGateway = null;
                // We need this to be a JM that answers to update messages for
                // robustness on Travis (if jobs need to be resubmitted in (4)).
                ActorRef jm = TaskManagerTest.system.actorOf(Props.create(new TaskManagerTest.SimpleLookupJobManagerCreator(HighAvailabilityServices.DEFAULT_LEADER_ID)));
                ActorGateway jobManagerActorGateway = new org.apache.flink.runtime.instance.AkkaActorGateway(jm, HighAvailabilityServices.DEFAULT_LEADER_ID);
                final ActorGateway testActorGateway = new org.apache.flink.runtime.instance.AkkaActorGateway(getTestActor(), HighAvailabilityServices.DEFAULT_LEADER_ID);
                try {
                    final ActorGateway jobManager = jobManagerActorGateway;
                    highAvailabilityServices.setJobMasterLeaderRetriever(DEFAULT_JOB_ID, new org.apache.flink.runtime.leaderretrieval.StandaloneLeaderRetrievalService(jobManager.path(), jobManager.leaderSessionID()));
                    final ActorGateway taskManager = TestingUtils.createTaskManager(TaskManagerTest.system, highAvailabilityServices, new Configuration(), true, false);
                    final JobID jobId = new JobID();
                    // Single blocking task
                    final TaskDeploymentDescriptor tdd = TaskManagerTest.createTaskDeploymentDescriptor(jobId, "Job", new JobVertexID(), new ExecutionAttemptID(), new SerializedValue(new ExecutionConfig()), "Task", 1, 0, 1, 0, new Configuration(), new Configuration(), BlockingNoOpInvokable.class.getName(), Collections.<ResultPartitionDeploymentDescriptor>emptyList(), Collections.<InputGateDeploymentDescriptor>emptyList(), Collections.emptyList(), Collections.emptyList(), 0);
                    // Submit the task
                    new Within(TaskManagerTest.d) {
                        @Override
                        protected void run() {
                            try {
                                // Make sure to register
                                Future<?> connectFuture = taskManager.ask(new TestingTaskManagerMessages.NotifyWhenRegisteredAtJobManager(jobManager.actor()), remaining());
                                Await.ready(connectFuture, remaining());
                                Future<Object> taskRunningFuture = taskManager.ask(new TestingTaskManagerMessages.NotifyWhenTaskIsRunning(tdd.getExecutionAttemptId()), TaskManagerTest.timeout);
                                taskManager.tell(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd));
                                Await.ready(taskRunningFuture, TaskManagerTest.d);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Assert.fail(e.getMessage());
                            }
                        }
                    };
                    // 
                    // 1) Trigger sample for non-existing task
                    // 
                    new Within(TaskManagerTest.d) {
                        @Override
                        protected void run() {
                            try {
                                ExecutionAttemptID taskId = new ExecutionAttemptID();
                                taskManager.tell(new org.apache.flink.runtime.messages.StackTraceSampleMessages.TriggerStackTraceSample(112223, taskId, 100, TaskManagerTest.timeD, 0), testActorGateway);
                                // Receive the expected message (heartbeat races possible)
                                Object[] msg = receiveN(1);
                                while (!((msg[0]) instanceof Status.Failure)) {
                                    msg = receiveN(1);
                                } 
                                Status.Failure response = ((Status.Failure) (msg[0]));
                                Assert.assertEquals(IllegalStateException.class, response.cause().getClass());
                            } catch (Exception e) {
                                e.printStackTrace();
                                Assert.fail(e.getMessage());
                            }
                        }
                    };
                    // 
                    // 2) Trigger sample for the blocking task
                    // 
                    new Within(TaskManagerTest.d) {
                        @Override
                        protected void run() {
                            boolean success = false;
                            Throwable lastError = null;
                            for (int i = 0; (i < 100) && (!success); i++) {
                                try {
                                    int numSamples = 5;
                                    taskManager.tell(new org.apache.flink.runtime.messages.StackTraceSampleMessages.TriggerStackTraceSample(19230, tdd.getExecutionAttemptId(), numSamples, Time.milliseconds(100L), 0), testActorGateway);
                                    // Receive the expected message (heartbeat races possible)
                                    Object[] msg = receiveN(1);
                                    while (!((msg[0]) instanceof StackTraceSampleResponse)) {
                                        msg = receiveN(1);
                                    } 
                                    StackTraceSampleResponse response = ((StackTraceSampleResponse) (msg[0]));
                                    // ---- Verify response ----
                                    Assert.assertEquals(19230, response.getSampleId());
                                    Assert.assertEquals(tdd.getExecutionAttemptId(), response.getExecutionAttemptID());
                                    List<StackTraceElement[]> traces = response.getSamples();
                                    Assert.assertEquals("Number of samples", numSamples, traces.size());
                                    for (StackTraceElement[] trace : traces) {
                                        // Look for BlockingNoOpInvokable#invoke
                                        for (StackTraceElement elem : trace) {
                                            if (elem.getClassName().equals(BlockingNoOpInvokable.class.getName())) {
                                                Assert.assertEquals("invoke", elem.getMethodName());
                                                success = true;
                                                break;
                                            }
                                        }
                                        Assert.assertTrue(("Unexpected stack trace: " + (Arrays.toString(trace))), success);
                                    }
                                } catch (Throwable t) {
                                    lastError = t;
                                    TaskManagerTest.LOG.warn("Failed to find invokable.", t);
                                }
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    TaskManagerTest.LOG.error("Interrupted while sleeping before retry.", e);
                                    break;
                                }
                            }
                            if (!success) {
                                if (lastError == null) {
                                    Assert.fail("Failed to find invokable");
                                } else {
                                    Assert.fail(lastError.getMessage());
                                }
                            }
                        }
                    };
                    // 
                    // 3) Trigger sample for the blocking task with max depth
                    // 
                    new Within(TaskManagerTest.d) {
                        @Override
                        protected void run() {
                            try {
                                int numSamples = 5;
                                int maxDepth = 2;
                                taskManager.tell(new org.apache.flink.runtime.messages.StackTraceSampleMessages.TriggerStackTraceSample(1337, tdd.getExecutionAttemptId(), numSamples, Time.milliseconds(100L), maxDepth), testActorGateway);
                                // Receive the expected message (heartbeat races possible)
                                Object[] msg = receiveN(1);
                                while (!((msg[0]) instanceof StackTraceSampleResponse)) {
                                    msg = receiveN(1);
                                } 
                                StackTraceSampleResponse response = ((StackTraceSampleResponse) (msg[0]));
                                // ---- Verify response ----
                                Assert.assertEquals(1337, response.getSampleId());
                                Assert.assertEquals(tdd.getExecutionAttemptId(), response.getExecutionAttemptID());
                                List<StackTraceElement[]> traces = response.getSamples();
                                Assert.assertEquals("Number of samples", numSamples, traces.size());
                                for (StackTraceElement[] trace : traces) {
                                    Assert.assertEquals("Max depth", maxDepth, trace.length);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Assert.fail(e.getMessage());
                            }
                        }
                    };
                    // 
                    // 4) Trigger sample for the blocking task, but cancel it during sampling
                    // 
                    new Within(TaskManagerTest.d) {
                        @Override
                        protected void run() {
                            try {
                                int maxAttempts = 10;
                                int sleepTime = 100;
                                for (int i = 0; i < maxAttempts; i++ , sleepTime *= 2) {
                                    // Trigger many samples in order to cancel the task
                                    // during a sample
                                    taskManager.tell(new org.apache.flink.runtime.messages.StackTraceSampleMessages.TriggerStackTraceSample(44, tdd.getExecutionAttemptId(), Integer.MAX_VALUE, Time.milliseconds(10L), 0), testActorGateway);
                                    Thread.sleep(sleepTime);
                                    Future<?> removeFuture = taskManager.ask(new TestingTaskManagerMessages.NotifyWhenJobRemoved(jobId), remaining());
                                    // Cancel the task
                                    taskManager.tell(new org.apache.flink.runtime.messages.TaskMessages.CancelTask(tdd.getExecutionAttemptId()));
                                    // Receive the expected message (heartbeat races possible)
                                    while (true) {
                                        Object[] msg = receiveN(1);
                                        if ((msg[0]) instanceof StackTraceSampleResponse) {
                                            StackTraceSampleResponse response = ((StackTraceSampleResponse) (msg[0]));
                                            Assert.assertEquals(tdd.getExecutionAttemptId(), response.getExecutionAttemptID());
                                            Assert.assertEquals(44, response.getSampleId());
                                            // Done
                                            return;
                                        } else
                                            if ((msg[0]) instanceof Failure) {
                                                // Wait for removal before resubmitting
                                                Await.ready(removeFuture, remaining());
                                                Future<?> taskRunningFuture = taskManager.ask(new TestingTaskManagerMessages.NotifyWhenTaskIsRunning(tdd.getExecutionAttemptId()), TaskManagerTest.timeout);
                                                // Resubmit
                                                taskManager.tell(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd));
                                                Await.ready(taskRunningFuture, remaining());
                                                // Retry the sample message
                                                break;
                                            } else {
                                                // Different message
                                                continue;
                                            }

                                    } 
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Assert.fail(e.getMessage());
                            }
                        }
                    };
                } finally {
                    TestingUtils.stopActor(taskManagerActorGateway);
                    TestingUtils.stopActor(jobManagerActorGateway);
                }
            }
        };
    }

    @Test
    public void testTerminationOnFatalError() {
        highAvailabilityServices.setJobMasterLeaderRetriever(DEFAULT_JOB_ID, new SettableLeaderRetrievalService());
        new JavaTestKit(TaskManagerTest.system) {
            {
                final ActorGateway taskManager = // no jobmanager
                TestingUtils.createTaskManager(TaskManagerTest.system, highAvailabilityServices, new Configuration(), true, false);
                try {
                    watch(taskManager.actor());
                    taskManager.tell(new FatalError("test fatal error", new Exception("something super bad")));
                    expectTerminated(TaskManagerTest.d, taskManager.actor());
                } finally {
                    taskManager.tell(Kill.getInstance());
                }
            }
        };
    }

    /**
     * Test that a failing schedule or update consumers call leads to the failing of the respective
     * task.
     *
     * <p>IMPORTANT: We have to make sure that the invokable's cancel method is called, because only
     * then the future is completed. We do this by not eagerly deploy consumer tasks and requiring
     * the invokable to fill one memory segment. The completed memory segment will trigger the
     * scheduling of the downstream operator since it is in pipeline mode. After we've filled the
     * memory segment, we'll block the invokable and wait for the task failure due to the failed
     * schedule or update consumers call.
     */
    @Test(timeout = 10000L)
    public void testFailingScheduleOrUpdateConsumersMessage() throws Exception {
        new JavaTestKit(TaskManagerTest.system) {
            {
                final Configuration configuration = new Configuration();
                // set the memory segment to the smallest size possible, because we have to fill one
                // memory buffer to trigger the schedule or update consumers message to the downstream
                // operators
                configuration.setString(MEMORY_SEGMENT_SIZE, "4096");
                final JobID jid = new JobID();
                final JobVertexID vid = new JobVertexID();
                final ExecutionAttemptID eid = new ExecutionAttemptID();
                final SerializedValue<ExecutionConfig> executionConfig = new SerializedValue(new ExecutionConfig());
                final ResultPartitionDeploymentDescriptor resultPartitionDeploymentDescriptor = new ResultPartitionDeploymentDescriptor(new IntermediateDataSetID(), new IntermediateResultPartitionID(), ResultPartitionType.PIPELINED, 1, 1, true);
                final TaskDeploymentDescriptor tdd = TaskManagerTest.createTaskDeploymentDescriptor(jid, "TestJob", vid, eid, executionConfig, "TestTask", 1, 0, 1, 0, new Configuration(), new Configuration(), TaskManagerTest.TestInvokableRecordCancel.class.getName(), Collections.singletonList(resultPartitionDeploymentDescriptor), Collections.<InputGateDeploymentDescriptor>emptyList(), new ArrayList(), Collections.emptyList(), 0);
                ActorRef jmActorRef = TaskManagerTest.system.actorOf(Props.create(TaskManagerTest.FailingScheduleOrUpdateConsumersJobManager.class, TaskManagerTest.LEADER_SESSION_ID), "jobmanager");
                ActorGateway jobManager = new org.apache.flink.runtime.instance.AkkaActorGateway(jmActorRef, TaskManagerTest.LEADER_SESSION_ID);
                highAvailabilityServices.setJobMasterLeaderRetriever(DEFAULT_JOB_ID, new org.apache.flink.runtime.leaderretrieval.StandaloneLeaderRetrievalService(jobManager.path(), jobManager.leaderSessionID()));
                final ActorGateway taskManager = TestingUtils.createTaskManager(TaskManagerTest.system, highAvailabilityServices, configuration, true, true);
                try {
                    TaskManagerTest.TestInvokableRecordCancel.resetGotCanceledFuture();
                    Future<Object> result = taskManager.ask(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd), TaskManagerTest.timeout);
                    Await.result(result, TaskManagerTest.timeout);
                    CompletableFuture<Boolean> cancelFuture = TaskManagerTest.TestInvokableRecordCancel.gotCanceled();
                    Assert.assertEquals(true, cancelFuture.get());
                } finally {
                    TestingUtils.stopActor(taskManager);
                    TestingUtils.stopActor(jobManager);
                }
            }
        };
    }

    /**
     * Tests that the TaskManager sends a proper exception back to the sender if the submit task
     * message fails.
     */
    @Test
    public void testSubmitTaskFailure() throws Exception {
        ActorGateway jobManager = null;
        ActorGateway taskManager = null;
        try {
            ActorRef jm = TaskManagerTest.system.actorOf(Props.create(TaskManagerTest.SimpleJobManager.class, TaskManagerTest.LEADER_SESSION_ID));
            jobManager = new org.apache.flink.runtime.instance.AkkaActorGateway(jm, TaskManagerTest.LEADER_SESSION_ID);
            highAvailabilityServices.setJobMasterLeaderRetriever(DEFAULT_JOB_ID, new org.apache.flink.runtime.leaderretrieval.StandaloneLeaderRetrievalService(jobManager.path(), jobManager.leaderSessionID()));
            taskManager = TestingUtils.createTaskManager(TaskManagerTest.system, highAvailabilityServices, new Configuration(), true, true);
            TaskDeploymentDescriptor tdd = // this will make the submission fail because the number of key groups must be >= 1
            TaskManagerTest.createTaskDeploymentDescriptor(new JobID(), "test job", new JobVertexID(), new ExecutionAttemptID(), new SerializedValue(new ExecutionConfig()), "test task", 0, 0, 1, 0, new Configuration(), new Configuration(), "Foobar", Collections.<ResultPartitionDeploymentDescriptor>emptyList(), Collections.<InputGateDeploymentDescriptor>emptyList(), Collections.emptyList(), Collections.emptyList(), 0);
            Future<Object> submitResponse = taskManager.ask(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd), TaskManagerTest.timeout);
            try {
                Await.result(submitResponse, TaskManagerTest.timeout);
                Assert.fail("The submit task message should have failed.");
            } catch (IllegalArgumentException e) {
                // expected
            }
        } finally {
            TestingUtils.stopActor(jobManager);
            TestingUtils.stopActor(taskManager);
        }
    }

    /**
     * Tests that the TaskManager sends a proper exception back to the sender if the stop task
     * message fails.
     */
    @Test
    public void testStopTaskFailure() throws Exception {
        ActorGateway jobManager = null;
        ActorGateway taskManager = null;
        try {
            final ExecutionAttemptID executionAttemptId = new ExecutionAttemptID();
            ActorRef jm = TaskManagerTest.system.actorOf(Props.create(TaskManagerTest.SimpleJobManager.class, TaskManagerTest.LEADER_SESSION_ID));
            jobManager = new org.apache.flink.runtime.instance.AkkaActorGateway(jm, TaskManagerTest.LEADER_SESSION_ID);
            highAvailabilityServices.setJobMasterLeaderRetriever(DEFAULT_JOB_ID, new org.apache.flink.runtime.leaderretrieval.StandaloneLeaderRetrievalService(jobManager.path(), jobManager.leaderSessionID()));
            taskManager = TestingUtils.createTaskManager(TaskManagerTest.system, highAvailabilityServices, new Configuration(), true, true);
            TaskDeploymentDescriptor tdd = TaskManagerTest.createTaskDeploymentDescriptor(new JobID(), "test job", new JobVertexID(), executionAttemptId, new SerializedValue(new ExecutionConfig()), "test task", 1, 0, 1, 0, new Configuration(), new Configuration(), BlockingNoOpInvokable.class.getName(), Collections.<ResultPartitionDeploymentDescriptor>emptyList(), Collections.<InputGateDeploymentDescriptor>emptyList(), Collections.emptyList(), Collections.emptyList(), 0);
            Future<Object> submitResponse = taskManager.ask(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd), TaskManagerTest.timeout);
            Await.result(submitResponse, TaskManagerTest.timeout);
            final Future<Object> taskRunning = taskManager.ask(new TestingTaskManagerMessages.NotifyWhenTaskIsRunning(executionAttemptId), TaskManagerTest.timeout);
            Await.result(taskRunning, TaskManagerTest.timeout);
            Future<Object> stopResponse = taskManager.ask(new org.apache.flink.runtime.messages.TaskMessages.StopTask(executionAttemptId), TaskManagerTest.timeout);
            try {
                Await.result(stopResponse, TaskManagerTest.timeout);
                Assert.fail("The stop task message should have failed.");
            } catch (UnsupportedOperationException e) {
                // expected
            }
        } finally {
            TestingUtils.stopActor(jobManager);
            TestingUtils.stopActor(taskManager);
        }
    }

    /**
     * Tests that the TaskManager sends a proper exception back to the sender if the trigger stack
     * trace message fails.
     */
    @Test
    public void testStackTraceSampleFailure() throws Exception {
        ActorGateway jobManager = null;
        ActorGateway taskManager = null;
        try {
            ActorRef jm = TaskManagerTest.system.actorOf(Props.create(TaskManagerTest.SimpleJobManager.class, TaskManagerTest.LEADER_SESSION_ID));
            jobManager = new org.apache.flink.runtime.instance.AkkaActorGateway(jm, TaskManagerTest.LEADER_SESSION_ID);
            highAvailabilityServices.setJobMasterLeaderRetriever(DEFAULT_JOB_ID, new org.apache.flink.runtime.leaderretrieval.StandaloneLeaderRetrievalService(jobManager.path(), jobManager.leaderSessionID()));
            taskManager = TestingUtils.createTaskManager(TaskManagerTest.system, highAvailabilityServices, new Configuration(), true, true);
            Future<Object> stackTraceResponse = taskManager.ask(new org.apache.flink.runtime.messages.StackTraceSampleMessages.TriggerStackTraceSample(0, new ExecutionAttemptID(), 0, Time.milliseconds(1L), 0), TaskManagerTest.timeout);
            try {
                Await.result(stackTraceResponse, TaskManagerTest.timeout);
                Assert.fail("The trigger stack trace message should have failed.");
            } catch (IllegalStateException e) {
                // expected
            }
        } finally {
            TestingUtils.stopActor(jobManager);
            TestingUtils.stopActor(taskManager);
        }
    }

    /**
     * Tests that the TaskManager sends a proper exception back to the sender if the trigger stack
     * trace message fails.
     */
    @Test
    public void testUpdateTaskInputPartitionsFailure() throws Exception {
        ActorGateway jobManager = null;
        ActorGateway taskManager = null;
        try {
            final ExecutionAttemptID executionAttemptId = new ExecutionAttemptID();
            ActorRef jm = TaskManagerTest.system.actorOf(Props.create(TaskManagerTest.SimpleJobManager.class, TaskManagerTest.LEADER_SESSION_ID));
            jobManager = new org.apache.flink.runtime.instance.AkkaActorGateway(jm, TaskManagerTest.LEADER_SESSION_ID);
            highAvailabilityServices.setJobMasterLeaderRetriever(DEFAULT_JOB_ID, new org.apache.flink.runtime.leaderretrieval.StandaloneLeaderRetrievalService(jobManager.path(), jobManager.leaderSessionID()));
            taskManager = TestingUtils.createTaskManager(TaskManagerTest.system, highAvailabilityServices, new Configuration(), true, true);
            TaskDeploymentDescriptor tdd = TaskManagerTest.createTaskDeploymentDescriptor(new JobID(), "test job", new JobVertexID(), executionAttemptId, new SerializedValue(new ExecutionConfig()), "test task", 1, 0, 1, 0, new Configuration(), new Configuration(), BlockingNoOpInvokable.class.getName(), Collections.<ResultPartitionDeploymentDescriptor>emptyList(), Collections.<InputGateDeploymentDescriptor>emptyList(), Collections.emptyList(), Collections.emptyList(), 0);
            Future<Object> submitResponse = taskManager.ask(new org.apache.flink.runtime.messages.TaskMessages.SubmitTask(tdd), TaskManagerTest.timeout);
            Await.result(submitResponse, TaskManagerTest.timeout);
            Future<Object> partitionUpdateResponse = taskManager.ask(new TaskMessages.UpdateTaskSinglePartitionInfo(executionAttemptId, new IntermediateDataSetID(), new InputChannelDeploymentDescriptor(new ResultPartitionID(), ResultPartitionLocation.createLocal())), TaskManagerTest.timeout);
            try {
                Await.result(partitionUpdateResponse, TaskManagerTest.timeout);
                Assert.fail("The update task input partitions message should have failed.");
            } catch (Exception e) {
                // expected
            }
        } finally {
            TestingUtils.stopActor(jobManager);
            TestingUtils.stopActor(taskManager);
        }
    }

    // --------------------------------------------------------------------------------------------
    public static class SimpleJobManager extends FlinkUntypedActor {
        private final UUID leaderSessionID;

        public SimpleJobManager(UUID leaderSessionID) {
            this.leaderSessionID = leaderSessionID;
        }

        @Override
        public void handleMessage(Object message) throws Exception {
            if (message instanceof RegistrationMessages.RegisterTaskManager) {
                final InstanceID iid = new InstanceID();
                final ActorRef self = getSelf();
                getSender().tell(decorateMessage(new RegistrationMessages.AcknowledgeRegistration(iid, 12345)), self);
            } else
                if (message instanceof TaskMessages.UpdateTaskExecutionState) {
                    getSender().tell(true, getSelf());
                }

        }

        @Override
        protected UUID getLeaderSessionID() {
            return leaderSessionID;
        }
    }

    public static class FailingScheduleOrUpdateConsumersJobManager extends TaskManagerTest.SimpleJobManager {
        public FailingScheduleOrUpdateConsumersJobManager(UUID leaderSessionId) {
            super(leaderSessionId);
        }

        @Override
        public void handleMessage(Object message) throws Exception {
            if (message instanceof JobManagerMessages.ScheduleOrUpdateConsumers) {
                getSender().tell(decorateMessage(new Status.Failure(new Exception("Could not schedule or update consumers."))), getSelf());
            } else {
                super.handleMessage(message);
            }
        }
    }

    public static class SimpleLookupJobManager extends TaskManagerTest.SimpleJobManager {
        public SimpleLookupJobManager(UUID leaderSessionID) {
            super(leaderSessionID);
        }

        @Override
        public void handleMessage(Object message) throws Exception {
            if (message instanceof JobManagerMessages.ScheduleOrUpdateConsumers) {
                getSender().tell(decorateMessage(Acknowledge.get()), getSelf());
            } else {
                super.handleMessage(message);
            }
        }
    }

    public static class SimpleLookupFailingUpdateJobManager extends TaskManagerTest.SimpleLookupJobManager {
        private final Set<ExecutionAttemptID> validIDs;

        public SimpleLookupFailingUpdateJobManager(UUID leaderSessionID, Set<ExecutionAttemptID> ids) {
            super(leaderSessionID);
            this.validIDs = new HashSet(ids);
        }

        @Override
        public void handleMessage(Object message) throws Exception {
            if (message instanceof TaskMessages.UpdateTaskExecutionState) {
                TaskMessages.UpdateTaskExecutionState updateMsg = ((TaskMessages.UpdateTaskExecutionState) (message));
                if (validIDs.contains(updateMsg.taskExecutionState().getID())) {
                    getSender().tell(true, getSelf());
                } else {
                    getSender().tell(false, getSelf());
                }
            } else {
                super.handleMessage(message);
            }
        }
    }

    public static class SimplePartitionStateLookupJobManager extends TaskManagerTest.SimpleJobManager {
        private final ActorRef testActor;

        public SimplePartitionStateLookupJobManager(UUID leaderSessionID, ActorRef testActor) {
            super(leaderSessionID);
            this.testActor = testActor;
        }

        @Override
        public void handleMessage(Object message) throws Exception {
            if (message instanceof JobManagerMessages.RequestPartitionProducerState) {
                getSender().tell(decorateMessage(RUNNING), getSelf());
            } else
                if (message instanceof TaskMessages.UpdateTaskExecutionState) {
                    final TaskExecutionState msg = taskExecutionState();
                    if (msg.getExecutionState().isTerminal()) {
                        testActor.tell(msg, self());
                    }
                } else {
                    super.handleMessage(message);
                }

        }
    }

    public static class SimpleLookupJobManagerCreator implements Creator<TaskManagerTest.SimpleLookupJobManager> {
        private final UUID leaderSessionID;

        public SimpleLookupJobManagerCreator(UUID leaderSessionID) {
            this.leaderSessionID = leaderSessionID;
        }

        @Override
        public TaskManagerTest.SimpleLookupJobManager create() throws Exception {
            return new TaskManagerTest.SimpleLookupJobManager(leaderSessionID);
        }
    }

    public static class SimpleLookupFailingUpdateJobManagerCreator implements Creator<TaskManagerTest.SimpleLookupFailingUpdateJobManager> {
        private final UUID leaderSessionID;

        private final Set<ExecutionAttemptID> validIDs;

        public SimpleLookupFailingUpdateJobManagerCreator(UUID leaderSessionID, ExecutionAttemptID... ids) {
            this.leaderSessionID = leaderSessionID;
            validIDs = new HashSet<ExecutionAttemptID>();
            for (ExecutionAttemptID id : ids) {
                this.validIDs.add(id);
            }
        }

        @Override
        public TaskManagerTest.SimpleLookupFailingUpdateJobManager create() throws Exception {
            return new TaskManagerTest.SimpleLookupFailingUpdateJobManager(leaderSessionID, validIDs);
        }
    }

    public static class SimplePartitionStateLookupJobManagerCreator implements Creator<TaskManagerTest.SimplePartitionStateLookupJobManager> {
        private final UUID leaderSessionID;

        private final ActorRef testActor;

        public SimplePartitionStateLookupJobManagerCreator(UUID leaderSessionID, ActorRef testActor) {
            this.leaderSessionID = leaderSessionID;
            this.testActor = testActor;
        }

        @Override
        public TaskManagerTest.SimplePartitionStateLookupJobManager create() throws Exception {
            return new TaskManagerTest.SimplePartitionStateLookupJobManager(leaderSessionID, testActor);
        }
    }

    // --------------------------------------------------------------------------------------------
    public static final class TestInvokableCorrect extends AbstractInvokable {
        public TestInvokableCorrect(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() {
        }
    }

    public static class TestInvokableBlockingCancelable extends AbstractInvokable {
        public TestInvokableBlockingCancelable(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() throws Exception {
            final Object o = new Object();
            // noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized(o) {
                // noinspection InfiniteLoopStatement
                while (true) {
                    o.wait();
                } 
            }
        }
    }

    public static final class TestInvokableRecordCancel extends AbstractInvokable {
        private static final Object lock = new Object();

        private static CompletableFuture<Boolean> gotCanceledFuture = new CompletableFuture<>();

        public TestInvokableRecordCancel(Environment environment) {
            super(environment);
        }

        @Override
        public void invoke() throws Exception {
            final Object o = new Object();
            RecordWriter<IntValue> recordWriter = new RecordWriter(getEnvironment().getWriter(0));
            for (int i = 0; i < 1024; i++) {
                recordWriter.emit(new IntValue(42));
            }
            synchronized(o) {
                // noinspection InfiniteLoopStatement
                while (true) {
                    o.wait();
                } 
            }
        }

        @Override
        public void cancel() {
            synchronized(TaskManagerTest.TestInvokableRecordCancel.lock) {
                TaskManagerTest.TestInvokableRecordCancel.gotCanceledFuture.complete(true);
            }
        }

        public static void resetGotCanceledFuture() {
            synchronized(TaskManagerTest.TestInvokableRecordCancel.lock) {
                TaskManagerTest.TestInvokableRecordCancel.gotCanceledFuture = new CompletableFuture<>();
            }
        }

        public static CompletableFuture<Boolean> gotCanceled() {
            synchronized(TaskManagerTest.TestInvokableRecordCancel.lock) {
                return TaskManagerTest.TestInvokableRecordCancel.gotCanceledFuture;
            }
        }
    }
}

