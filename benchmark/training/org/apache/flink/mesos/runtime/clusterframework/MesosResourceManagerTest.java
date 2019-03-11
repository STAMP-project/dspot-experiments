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
package org.apache.flink.mesos.runtime.clusterframework;


import ApplicationStatus.SUCCEEDED;
import ConnectionMonitor.Start;
import LaunchCoordinator.Assign;
import LaunchCoordinator.Launch;
import LaunchCoordinator.Unassign;
import MesosTaskManagerParameters.ContainerType;
import MesosWorkerStore.Worker;
import Protos.FrameworkID;
import Protos.FrameworkInfo;
import Protos.FrameworkInfo.Builder;
import Protos.MasterInfo;
import Protos.Offer.Operation;
import Protos.Offer.Operation.Type.LAUNCH;
import Protos.OfferID;
import Protos.SlaveID;
import Protos.TaskID;
import Protos.TaskInfo;
import Protos.TaskState.TASK_FAILED;
import Protos.TaskState.TASK_LOST;
import Protos.TaskStatus;
import TaskMonitor.TaskGoalStateUpdated;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.TestProbe;
import com.netflix.fenzo.ConstraintEvaluator;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import junit.framework.AssertionFailedError;
import org.apache.flink.annotation.VisibleForTesting;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.mesos.runtime.clusterframework.services.MesosServices;
import org.apache.flink.mesos.runtime.clusterframework.store.MesosWorkerStore;
import org.apache.flink.mesos.scheduler.LaunchCoordinator;
import org.apache.flink.mesos.scheduler.messages.AcceptOffers;
import org.apache.flink.mesos.scheduler.messages.Disconnected;
import org.apache.flink.mesos.scheduler.messages.OfferRescinded;
import org.apache.flink.mesos.scheduler.messages.ReRegistered;
import org.apache.flink.mesos.scheduler.messages.Registered;
import org.apache.flink.mesos.scheduler.messages.ResourceOffers;
import org.apache.flink.mesos.scheduler.messages.StatusUpdate;
import org.apache.flink.mesos.util.MesosArtifactServer;
import org.apache.flink.mesos.util.MesosConfiguration;
import org.apache.flink.runtime.clusterframework.ContainerSpecification;
import org.apache.flink.runtime.clusterframework.ContaineredTaskManagerParameters;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.clusterframework.types.ResourceProfile;
import org.apache.flink.runtime.concurrent.ScheduledExecutor;
import org.apache.flink.runtime.entrypoint.ClusterInformation;
import org.apache.flink.runtime.heartbeat.HeartbeatServices;
import org.apache.flink.runtime.highavailability.HighAvailabilityServices;
import org.apache.flink.runtime.highavailability.TestingHighAvailabilityServices;
import org.apache.flink.runtime.instance.HardwareDescription;
import org.apache.flink.runtime.jobmaster.JobMasterGateway;
import org.apache.flink.runtime.jobmaster.JobMasterId;
import org.apache.flink.runtime.jobmaster.JobMasterRegistrationSuccess;
import org.apache.flink.runtime.leaderelection.TestingLeaderElectionService;
import org.apache.flink.runtime.leaderretrieval.SettableLeaderRetrievalService;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.metrics.MetricRegistry;
import org.apache.flink.runtime.metrics.MetricRegistryImpl;
import org.apache.flink.runtime.metrics.groups.JobManagerMetricGroup;
import org.apache.flink.runtime.metrics.groups.UnregisteredMetricGroups;
import org.apache.flink.runtime.registration.RegistrationResponse;
import org.apache.flink.runtime.resourcemanager.JobLeaderIdService;
import org.apache.flink.runtime.resourcemanager.ResourceManagerId;
import org.apache.flink.runtime.resourcemanager.SlotRequest;
import org.apache.flink.runtime.resourcemanager.slotmanager.ResourceActions;
import org.apache.flink.runtime.resourcemanager.slotmanager.SlotManager;
import org.apache.flink.runtime.rpc.FatalErrorHandler;
import org.apache.flink.runtime.rpc.RpcService;
import org.apache.flink.runtime.rpc.TestingRpcService;
import org.apache.flink.runtime.taskexecutor.SlotReport;
import org.apache.flink.runtime.taskexecutor.TaskExecutorGateway;
import org.apache.flink.runtime.taskexecutor.TaskExecutorRegistrationSuccess;
import org.apache.flink.runtime.util.TestingFatalErrorHandler;
import org.apache.flink.util.TestLogger;
import org.apache.mesos.Protos;
import org.apache.mesos.Scheduler;
import org.apache.mesos.SchedulerDriver;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;


/**
 * General tests for the Mesos resource manager component (v2).
 */
public class MesosResourceManagerTest extends TestLogger {
    private static final Logger LOG = LoggerFactory.getLogger(MesosResourceManagerTest.class);

    private static Configuration flinkConfig = new Configuration();

    private static ActorSystem system;

    private static final Time timeout = Time.seconds(10L);

    /**
     * The RM with some test-specific behavior.
     */
    static class TestingMesosResourceManager extends MesosResourceManager {
        public TestProbe connectionMonitor = new TestProbe(MesosResourceManagerTest.system);

        public TestProbe taskRouter = new TestProbe(MesosResourceManagerTest.system);

        public TestProbe launchCoordinator = new TestProbe(MesosResourceManagerTest.system);

        public TestProbe reconciliationCoordinator = new TestProbe(MesosResourceManagerTest.system);

        public final Set<ResourceID> closedTaskManagerConnections = new HashSet<>();

        public TestingMesosResourceManager(RpcService rpcService, String resourceManagerEndpointId, ResourceID resourceId, HighAvailabilityServices highAvailabilityServices, HeartbeatServices heartbeatServices, SlotManager slotManager, MetricRegistry metricRegistry, JobLeaderIdService jobLeaderIdService, FatalErrorHandler fatalErrorHandler, // Mesos specifics
        Configuration flinkConfig, MesosServices mesosServices, MesosConfiguration mesosConfig, MesosTaskManagerParameters taskManagerParameters, ContainerSpecification taskManagerContainerSpec, JobManagerMetricGroup jobManagerMetricGroup) {
            super(rpcService, resourceManagerEndpointId, resourceId, highAvailabilityServices, heartbeatServices, slotManager, metricRegistry, jobLeaderIdService, new ClusterInformation("localhost", 1234), fatalErrorHandler, flinkConfig, mesosServices, mesosConfig, taskManagerParameters, taskManagerContainerSpec, null, jobManagerMetricGroup);
        }

        @Override
        protected ActorRef createConnectionMonitor() {
            return connectionMonitor.ref();
        }

        @Override
        protected ActorRef createTaskMonitor(SchedulerDriver schedulerDriver) {
            return taskRouter.ref();
        }

        @Override
        protected ActorRef createLaunchCoordinator(SchedulerDriver schedulerDriver, ActorRef selfActorRef) {
            return launchCoordinator.ref();
        }

        @Override
        protected ActorRef createReconciliationCoordinator(SchedulerDriver schedulerDriver) {
            return reconciliationCoordinator.ref();
        }

        @Override
        protected void closeTaskManagerConnection(ResourceID resourceID, Exception cause) {
            super.closeTaskManagerConnection(resourceID, cause);
            closedTaskManagerConnections.add(resourceID);
        }

        @VisibleForTesting
        @Override
        public <V> CompletableFuture<V> callAsync(Callable<V> callable, Time timeout) {
            return super.callAsync(callable, timeout);
        }
    }

    /**
     * The context fixture.
     */
    static class Context implements AutoCloseable {
        // services
        TestingRpcService rpcService;

        TestingFatalErrorHandler fatalErrorHandler;

        MesosResourceManagerTest.Context.MockMesosResourceManagerRuntimeServices rmServices;

        MesosResourceManagerTest.Context.MockMesosServices mesosServices;

        // RM
        ResourceID rmResourceID;

        static final String RM_ADDRESS = "resourceManager";

        MesosResourceManagerTest.TestingMesosResourceManager resourceManager;

        // domain objects for test purposes
        final ResourceProfile resourceProfile1 = ResourceProfile.UNKNOWN;

        FrameworkID framework1 = FrameworkID.newBuilder().setValue("framework1").build();

        public SlaveID slave1 = SlaveID.newBuilder().setValue("slave1").build();

        public String slave1host = "localhost";

        public OfferID offer1 = OfferID.newBuilder().setValue("offer1").build();

        public TaskID task1 = TaskID.newBuilder().setValue("taskmanager-00001").build();

        public TaskID task2 = TaskID.newBuilder().setValue("taskmanager-00002").build();

        public TaskID task3 = TaskID.newBuilder().setValue("taskmanager-00003").build();

        // task executors
        SlotReport slotReport = new SlotReport();

        public MesosResourceManagerTest.Context.MockTaskExecutor task1Executor;

        public MesosResourceManagerTest.Context.MockTaskExecutor task2Executor;

        public MesosResourceManagerTest.Context.MockTaskExecutor task3Executor;

        // job masters
        public MesosResourceManagerTest.Context.MockJobMaster jobMaster1;

        /**
         * Create mock RM dependencies.
         */
        Context() throws Exception {
            rpcService = new TestingRpcService();
            fatalErrorHandler = new TestingFatalErrorHandler();
            rmServices = new MesosResourceManagerTest.Context.MockMesosResourceManagerRuntimeServices();
            mesosServices = new MesosResourceManagerTest.Context.MockMesosServices();
            // TaskExecutor templating
            ContainerSpecification containerSpecification = new ContainerSpecification();
            ContaineredTaskManagerParameters containeredParams = new ContaineredTaskManagerParameters(1024, 768, 256, 4, new HashMap<String, String>());
            MesosTaskManagerParameters tmParams = new MesosTaskManagerParameters(1.0, 1, ContainerType.MESOS, Option.<String>empty(), containeredParams, Collections.<Protos.Volume>emptyList(), Collections.<Protos.Parameter>emptyList(), false, Collections.<ConstraintEvaluator>emptyList(), "", Option.<String>empty(), Option.<String>empty(), Collections.<String>emptyList());
            // resource manager
            rmResourceID = ResourceID.generate();
            resourceManager = // Mesos specifics
            new MesosResourceManagerTest.TestingMesosResourceManager(rpcService, MesosResourceManagerTest.Context.RM_ADDRESS, rmResourceID, rmServices.highAvailabilityServices, rmServices.heartbeatServices, rmServices.slotManager, rmServices.metricRegistry, rmServices.jobLeaderIdService, fatalErrorHandler, MesosResourceManagerTest.flinkConfig, mesosServices, rmServices.mesosConfig, tmParams, containerSpecification, UnregisteredMetricGroups.createUnregisteredJobManagerMetricGroup());
            // TaskExecutors
            task1Executor = mockTaskExecutor(task1);
            task2Executor = mockTaskExecutor(task2);
            task3Executor = mockTaskExecutor(task3);
            // JobMaster
            jobMaster1 = mockJobMaster(rmServices, new JobID(1, 0));
        }

        /**
         * Mock services needed by the resource manager.
         */
        class MockResourceManagerRuntimeServices {
            public final ScheduledExecutor scheduledExecutor;

            public final TestingHighAvailabilityServices highAvailabilityServices;

            public final HeartbeatServices heartbeatServices;

            public final MetricRegistry metricRegistry;

            public final TestingLeaderElectionService rmLeaderElectionService;

            public final JobLeaderIdService jobLeaderIdService;

            public final SlotManager slotManager;

            public final CompletableFuture<Boolean> slotManagerStarted;

            public ResourceActions rmActions;

            public UUID rmLeaderSessionId;

            MockResourceManagerRuntimeServices() throws Exception {
                scheduledExecutor = Mockito.mock(ScheduledExecutor.class);
                highAvailabilityServices = new TestingHighAvailabilityServices();
                rmLeaderElectionService = new TestingLeaderElectionService();
                highAvailabilityServices.setResourceManagerLeaderElectionService(rmLeaderElectionService);
                heartbeatServices = new org.apache.flink.runtime.heartbeat.TestingHeartbeatServices(5L, 5L, scheduledExecutor);
                metricRegistry = Mockito.mock(MetricRegistryImpl.class);
                slotManager = Mockito.mock(SlotManager.class);
                slotManagerStarted = new CompletableFuture<>();
                jobLeaderIdService = new JobLeaderIdService(highAvailabilityServices, rpcService.getScheduledExecutor(), Time.minutes(5L));
                Mockito.doAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        rmActions = invocation.getArgument(2);
                        slotManagerStarted.complete(true);
                        return null;
                    }
                }).when(slotManager).start(ArgumentMatchers.any(ResourceManagerId.class), ArgumentMatchers.any(Executor.class), ArgumentMatchers.any(ResourceActions.class));
                Mockito.when(slotManager.registerSlotRequest(ArgumentMatchers.any(SlotRequest.class))).thenReturn(true);
            }

            public void grantLeadership() throws Exception {
                rmLeaderSessionId = UUID.randomUUID();
                rmLeaderElectionService.isLeader(rmLeaderSessionId).get(MesosResourceManagerTest.timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            }
        }

        class MockMesosResourceManagerRuntimeServices extends MesosResourceManagerTest.Context.MockResourceManagerRuntimeServices {
            public SchedulerDriver schedulerDriver;

            public MesosConfiguration mesosConfig;

            public MesosWorkerStore workerStore;

            public MesosArtifactServer artifactServer;

            MockMesosResourceManagerRuntimeServices() throws Exception {
                schedulerDriver = Mockito.mock(SchedulerDriver.class);
                mesosConfig = Mockito.mock(MesosConfiguration.class);
                Mockito.when(mesosConfig.frameworkInfo()).thenReturn(FrameworkInfo.newBuilder());
                Mockito.when(mesosConfig.withFrameworkInfo(ArgumentMatchers.any(Builder.class))).thenReturn(mesosConfig);
                Mockito.when(mesosConfig.createDriver(ArgumentMatchers.any(Scheduler.class), ArgumentMatchers.anyBoolean())).thenReturn(schedulerDriver);
                workerStore = Mockito.mock(MesosWorkerStore.class);
                Mockito.when(workerStore.getFrameworkID()).thenReturn(Option.<Protos.FrameworkID>empty());
                artifactServer = Mockito.mock(MesosArtifactServer.class);
            }
        }

        class MockMesosServices implements MesosServices {
            @Override
            public MesosWorkerStore createMesosWorkerStore(Configuration configuration, Executor executor) throws Exception {
                return rmServices.workerStore;
            }

            @Override
            public ActorSystem getLocalActorSystem() {
                return MesosResourceManagerTest.system;
            }

            @Override
            public MesosArtifactServer getArtifactServer() {
                return rmServices.artifactServer;
            }

            @Override
            public void close(boolean cleanup) throws Exception {
            }
        }

        class MockJobMaster {
            public final JobID jobID;

            public final ResourceID resourceID;

            public final String address;

            public final JobMasterGateway gateway;

            public final JobMasterId jobMasterId;

            public final SettableLeaderRetrievalService leaderRetrievalService;

            MockJobMaster(JobID jobID) {
                this.jobID = jobID;
                this.resourceID = new ResourceID(jobID.toString());
                this.address = "/" + jobID;
                this.gateway = Mockito.mock(JobMasterGateway.class);
                this.jobMasterId = JobMasterId.generate();
                this.leaderRetrievalService = new SettableLeaderRetrievalService(this.address, this.jobMasterId.toUUID());
            }
        }

        private MesosResourceManagerTest.Context.MockJobMaster mockJobMaster(MesosResourceManagerTest.Context.MockResourceManagerRuntimeServices rmServices, JobID jobID) {
            MesosResourceManagerTest.Context.MockJobMaster jm = new MesosResourceManagerTest.Context.MockJobMaster(jobID);
            rpcService.registerGateway(jm.address, jm.gateway);
            rmServices.highAvailabilityServices.setJobMasterLeaderRetriever(jm.jobID, jm.leaderRetrievalService);
            return jm;
        }

        static class MockTaskExecutor {
            public final TaskID taskID;

            public final String address;

            public final ResourceID resourceID;

            public final TaskExecutorGateway gateway;

            MockTaskExecutor(Protos.TaskID taskID) {
                this.taskID = taskID;
                this.address = "/" + taskID;
                this.gateway = Mockito.mock(TaskExecutorGateway.class);
                this.resourceID = MesosResourceManager.extractResourceID(this.taskID);
            }
        }

        private MesosResourceManagerTest.Context.MockTaskExecutor mockTaskExecutor(Protos.TaskID taskID) {
            MesosResourceManagerTest.Context.MockTaskExecutor task = new MesosResourceManagerTest.Context.MockTaskExecutor(taskID);
            rpcService.registerGateway(task.address, task.gateway);
            return task;
        }

        /**
         * Start the resource manager and grant leadership to it.
         */
        public void startResourceManager() throws Exception {
            start();
            rmServices.grantLeadership();
            // drain probe events
            start();
            resourceManager.connectionMonitor.expectMsgClass(Start.class);
        }

        /**
         * Register a job master with the RM.
         */
        public void registerJobMaster(MesosResourceManagerTest.Context.MockJobMaster jobMaster) throws Exception {
            CompletableFuture<RegistrationResponse> registration = resourceManager.registerJobManager(jobMaster.jobMasterId, jobMaster.resourceID, jobMaster.address, jobMaster.jobID, MesosResourceManagerTest.timeout);
            Assert.assertTrue(((registration.get()) instanceof JobMasterRegistrationSuccess));
        }

        /**
         * Allocate a worker using the RM.
         */
        public Worker allocateWorker(Protos.TaskID taskID, ResourceProfile resourceProfile) throws Exception {
            Mockito.when(rmServices.workerStore.newTaskID()).thenReturn(taskID);
            rmServices.slotManagerStarted.get(MesosResourceManagerTest.timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            CompletableFuture<Void> allocateResourceFuture = resourceManager.callAsync(() -> {
                rmServices.rmActions.allocateResource(resourceProfile);
                return null;
            }, MesosResourceManagerTest.timeout);
            MesosWorkerStore.Worker expected = Worker.newWorker(taskID, resourceProfile);
            // check for exceptions
            allocateResourceFuture.get(MesosResourceManagerTest.timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            // drain the probe messages
            Mockito.verify(rmServices.workerStore, Mockito.timeout(MesosResourceManagerTest.timeout.toMilliseconds())).putWorker(expected);
            Assert.assertThat(resourceManager.workersInNew, Matchers.hasEntry(MesosResourceManager.extractResourceID(taskID), expected));
            resourceManager.taskRouter.expectMsgClass(TaskGoalStateUpdated.class);
            resourceManager.launchCoordinator.expectMsgClass(Launch.class);
            return expected;
        }

        /**
         * Prepares a launch operation.
         */
        public Operation launch(Protos... taskInfo) {
            return Operation.newBuilder().setType(LAUNCH).setLaunch(Protos.Offer.Operation.Launch.newBuilder().addAllTaskInfos(Arrays.asList(taskInfo))).build();
        }

        @Override
        public void close() throws Exception {
            rpcService.stopService().get();
            fatalErrorHandler.rethrowError();
        }
    }

    @Test
    public void testInitialize() throws Exception {
        new MesosResourceManagerTest.Context() {
            {
                startResourceManager();
                MesosResourceManagerTest.LOG.info("initialized");
            }
        };
    }

    /**
     * Test recovery of persistent workers.
     */
    @Test
    public void testRecoverWorkers() throws Exception {
        new MesosResourceManagerTest.Context() {
            {
                // set the initial persistent state then initialize the RM
                MesosWorkerStore.Worker worker1 = Worker.newWorker(task1);
                MesosWorkerStore.Worker worker2 = Worker.newWorker(task2).launchWorker(slave1, slave1host);
                MesosWorkerStore.Worker worker3 = Worker.newWorker(task3).launchWorker(slave1, slave1host).releaseWorker();
                Mockito.when(rmServices.workerStore.getFrameworkID()).thenReturn(Option.apply(framework1));
                Mockito.when(rmServices.workerStore.recoverWorkers()).thenReturn(Arrays.asList(worker1, worker2, worker3));
                startResourceManager();
                // verify that the internal state was updated, the task router was notified,
                // and the launch coordinator was asked to launch a task.
                // note: "new" workers are discarded
                Assert.assertThat(resourceManager.workersInNew.entrySet(), Matchers.empty());
                Assert.assertThat(resourceManager.workersInLaunch, Matchers.hasEntry(MesosResourceManager.extractResourceID(task2), worker2));
                Assert.assertThat(resourceManager.workersBeingReturned, Matchers.hasEntry(MesosResourceManager.extractResourceID(task3), worker3));
                resourceManager.taskRouter.expectMsgClass(TaskGoalStateUpdated.class);
                LaunchCoordinator.Assign actualAssign = resourceManager.launchCoordinator.expectMsgClass(Assign.class);
                Assert.assertThat(actualAssign.tasks(), Matchers.hasSize(1));
                Assert.assertThat(actualAssign.tasks().get(0).f0.getId(), Matchers.equalTo(task2.getValue()));
                Assert.assertThat(actualAssign.tasks().get(0).f1, Matchers.equalTo(slave1host));
                resourceManager.launchCoordinator.expectNoMsg();
            }
        };
    }

    /**
     * Test request for new workers.
     */
    @Test
    public void testRequestNewWorkers() throws Exception {
        new MesosResourceManagerTest.Context() {
            {
                startResourceManager();
                // allocate a worker
                Mockito.when(rmServices.workerStore.newTaskID()).thenReturn(task1).thenThrow(new AssertionFailedError());
                rmServices.slotManagerStarted.get(MesosResourceManagerTest.timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
                CompletableFuture<Void> allocateResourceFuture = resourceManager.callAsync(() -> {
                    rmServices.rmActions.allocateResource(resourceProfile1);
                    return null;
                }, MesosResourceManagerTest.timeout);
                // check for exceptions
                allocateResourceFuture.get(MesosResourceManagerTest.timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
                // verify that a new worker was persisted, the internal state was updated, the task router was notified,
                // and the launch coordinator was asked to launch a task
                MesosWorkerStore.Worker expected = Worker.newWorker(task1, resourceProfile1);
                Mockito.verify(rmServices.workerStore, Mockito.timeout(MesosResourceManagerTest.timeout.toMilliseconds())).putWorker(expected);
                Assert.assertThat(resourceManager.workersInNew, Matchers.hasEntry(MesosResourceManager.extractResourceID(task1), expected));
                resourceManager.taskRouter.expectMsgClass(TaskGoalStateUpdated.class);
                resourceManager.launchCoordinator.expectMsgClass(Launch.class);
            }
        };
    }

    /**
     * Test offer handling.
     */
    @Test
    public void testOfferHandling() throws Exception {
        new MesosResourceManagerTest.Context() {
            {
                startResourceManager();
                // Verify that the RM forwards offers to the launch coordinator.
                resourceOffers(new ResourceOffers(Collections.<Protos.Offer>emptyList()));
                resourceManager.launchCoordinator.expectMsgClass(ResourceOffers.class);
                offerRescinded(new OfferRescinded(offer1));
                resourceManager.launchCoordinator.expectMsgClass(OfferRescinded.class);
            }
        };
    }

    /**
     * Test offer acceptance.
     */
    @Test
    public void testAcceptOffers() throws Exception {
        new MesosResourceManagerTest.Context() {
            {
                startResourceManager();
                // allocate a new worker
                MesosWorkerStore.Worker worker1 = allocateWorker(task1, resourceProfile1);
                // send an AcceptOffers message as the LaunchCoordinator would
                // to launch task1 onto slave1 with offer1
                Protos.TaskInfo task1info = TaskInfo.newBuilder().setTaskId(task1).setName("").setSlaveId(slave1).build();
                AcceptOffers msg = new AcceptOffers(slave1host, Collections.singletonList(offer1), Collections.singletonList(launch(task1info)));
                resourceManager.acceptOffers(msg);
                // verify that the worker was persisted, the internal state was updated,
                // Mesos was asked to launch task1, and the task router was notified
                MesosWorkerStore.Worker worker1launched = worker1.launchWorker(slave1, slave1host);
                Mockito.verify(rmServices.workerStore).putWorker(worker1launched);
                Assert.assertThat(resourceManager.workersInNew.entrySet(), Matchers.empty());
                Assert.assertThat(resourceManager.workersInLaunch, Matchers.hasEntry(MesosResourceManager.extractResourceID(task1), worker1launched));
                resourceManager.taskRouter.expectMsg(new org.apache.flink.mesos.scheduler.TaskMonitor.TaskGoalStateUpdated(MesosResourceManager.extractGoalState(worker1launched)));
                Mockito.verify(rmServices.schedulerDriver).acceptOffers(msg.offerIds(), msg.operations(), msg.filters());
            }
        };
    }

    /**
     * Test status handling.
     */
    @Test
    public void testStatusHandling() throws Exception {
        new MesosResourceManagerTest.Context() {
            {
                startResourceManager();
                // Verify that the RM forwards status updates to the launch coordinator and task router.
                statusUpdate(new StatusUpdate(TaskStatus.newBuilder().setTaskId(task1).setSlaveId(slave1).setState(TASK_LOST).build()));
                resourceManager.reconciliationCoordinator.expectMsgClass(StatusUpdate.class);
                resourceManager.taskRouter.expectMsgClass(StatusUpdate.class);
            }
        };
    }

    /**
     * Test worker registration after launch.
     */
    @Test
    public void testWorkerStarted() throws Exception {
        new MesosResourceManagerTest.Context() {
            {
                // set the initial state with a (recovered) launched worker
                MesosWorkerStore.Worker worker1launched = Worker.newWorker(task1).launchWorker(slave1, slave1host);
                Mockito.when(rmServices.workerStore.getFrameworkID()).thenReturn(Option.apply(framework1));
                Mockito.when(rmServices.workerStore.recoverWorkers()).thenReturn(Collections.singletonList(worker1launched));
                startResourceManager();
                Assert.assertThat(resourceManager.workersInLaunch, Matchers.hasEntry(MesosResourceManager.extractResourceID(task1), worker1launched));
                final int dataPort = 1234;
                final HardwareDescription hardwareDescription = new HardwareDescription(1, 2L, 3L, 4L);
                // send registration message
                CompletableFuture<RegistrationResponse> successfulFuture = resourceManager.registerTaskExecutor(task1Executor.address, task1Executor.resourceID, dataPort, hardwareDescription, MesosResourceManagerTest.timeout);
                RegistrationResponse response = successfulFuture.get(MesosResourceManagerTest.timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
                Assert.assertTrue((response instanceof TaskExecutorRegistrationSuccess));
                final TaskExecutorRegistrationSuccess registrationResponse = ((TaskExecutorRegistrationSuccess) (response));
                final CompletableFuture<Acknowledge> initialSlotReportFuture = resourceManager.sendSlotReport(task1Executor.resourceID, registrationResponse.getRegistrationId(), slotReport, MesosResourceManagerTest.timeout);
                // check for errors
                initialSlotReportFuture.get();
                // verify the internal state
                Assert.assertThat(resourceManager.workersInLaunch, Matchers.hasEntry(MesosResourceManager.extractResourceID(task1), worker1launched));
            }
        };
    }

    /**
     * Test unplanned task failure of a pending worker.
     */
    @Test
    public void testWorkerFailed() throws Exception {
        new MesosResourceManagerTest.Context() {
            {
                // set the initial persistent state with a launched worker
                MesosWorkerStore.Worker worker1launched = Worker.newWorker(task1).launchWorker(slave1, slave1host);
                Mockito.when(rmServices.workerStore.getFrameworkID()).thenReturn(Option.apply(framework1));
                Mockito.when(rmServices.workerStore.recoverWorkers()).thenReturn(Collections.singletonList(worker1launched));
                Mockito.when(rmServices.workerStore.newTaskID()).thenReturn(task2);
                startResourceManager();
                // tell the RM that a task failed
                Mockito.when(rmServices.workerStore.removeWorker(task1)).thenReturn(true);
                taskTerminated(new org.apache.flink.mesos.scheduler.TaskMonitor.TaskTerminated(task1, TaskStatus.newBuilder().setTaskId(task1).setSlaveId(slave1).setState(TASK_FAILED).build()));
                // verify that the instance state was updated
                Mockito.verify(rmServices.workerStore).removeWorker(task1);
                Assert.assertThat(resourceManager.workersInLaunch.entrySet(), Matchers.empty());
                Assert.assertThat(resourceManager.workersBeingReturned.entrySet(), Matchers.empty());
                Assert.assertThat(resourceManager.workersInNew, Matchers.hasKey(MesosResourceManager.extractResourceID(task2)));
                // verify that `closeTaskManagerConnection` was called
                Assert.assertThat(resourceManager.closedTaskManagerConnections, Matchers.hasItem(MesosResourceManager.extractResourceID(task1)));
            }
        };
    }

    /**
     * Test planned stop of a launched worker.
     */
    @Test
    public void testStopWorker() throws Exception {
        new MesosResourceManagerTest.Context() {
            {
                // set the initial persistent state with a launched worker
                MesosWorkerStore.Worker worker1launched = Worker.newWorker(task1).launchWorker(slave1, slave1host);
                Mockito.when(rmServices.workerStore.getFrameworkID()).thenReturn(Option.apply(framework1));
                Mockito.when(rmServices.workerStore.recoverWorkers()).thenReturn(Collections.singletonList(worker1launched));
                startResourceManager();
                // drain the assign message
                resourceManager.launchCoordinator.expectMsgClass(Assign.class);
                // tell the RM to stop the worker
                stopWorker(new RegisteredMesosWorkerNode(worker1launched));
                // verify that the instance state was updated
                MesosWorkerStore.Worker worker1Released = worker1launched.releaseWorker();
                Mockito.verify(rmServices.workerStore).putWorker(worker1Released);
                Assert.assertThat(resourceManager.workersInLaunch.entrySet(), Matchers.empty());
                Assert.assertThat(resourceManager.workersBeingReturned, Matchers.hasEntry(MesosResourceManager.extractResourceID(task1), worker1Released));
                // verify that the monitor was notified
                resourceManager.taskRouter.expectMsgClass(TaskGoalStateUpdated.class);
                resourceManager.launchCoordinator.expectMsgClass(Unassign.class);
            }
        };
    }

    /**
     * Test application shutdown handling.
     */
    @Test
    public void testShutdownApplication() throws Exception {
        new MesosResourceManagerTest.Context() {
            {
                startResourceManager();
                resourceManager.deregisterApplication(SUCCEEDED, "");
                // verify that the Mesos framework is shutdown
                Mockito.verify(rmServices.schedulerDriver).stop(false);
                Mockito.verify(rmServices.workerStore).stop(true);
            }
        };
    }

    // ------------- connectivity tests -----------------------------
    /**
     * Test Mesos registration handling.
     */
    @Test
    public void testRegistered() throws Exception {
        new MesosResourceManagerTest.Context() {
            {
                startResourceManager();
                Protos.MasterInfo masterInfo = MasterInfo.newBuilder().setId("master1").setIp(0).setPort(5050).build();
                registered(new Registered(framework1, masterInfo));
                Mockito.verify(rmServices.workerStore).setFrameworkID(Option.apply(framework1));
                resourceManager.connectionMonitor.expectMsgClass(Registered.class);
                resourceManager.reconciliationCoordinator.expectMsgClass(Registered.class);
                resourceManager.launchCoordinator.expectMsgClass(Registered.class);
                resourceManager.taskRouter.expectMsgClass(Registered.class);
            }
        };
    }

    /**
     * Test Mesos re-registration handling.
     */
    @Test
    public void testReRegistered() throws Exception {
        new MesosResourceManagerTest.Context() {
            {
                Mockito.when(rmServices.workerStore.getFrameworkID()).thenReturn(Option.apply(framework1));
                startResourceManager();
                Protos.MasterInfo masterInfo = MasterInfo.newBuilder().setId("master1").setIp(0).setPort(5050).build();
                reregistered(new ReRegistered(masterInfo));
                resourceManager.connectionMonitor.expectMsgClass(ReRegistered.class);
                resourceManager.reconciliationCoordinator.expectMsgClass(ReRegistered.class);
                resourceManager.launchCoordinator.expectMsgClass(ReRegistered.class);
                resourceManager.taskRouter.expectMsgClass(ReRegistered.class);
            }
        };
    }

    /**
     * Test Mesos re-registration handling.
     */
    @Test
    public void testDisconnected() throws Exception {
        new MesosResourceManagerTest.Context() {
            {
                Mockito.when(rmServices.workerStore.getFrameworkID()).thenReturn(Option.apply(framework1));
                startResourceManager();
                resourceManager.disconnected(new Disconnected());
                resourceManager.connectionMonitor.expectMsgClass(Disconnected.class);
                resourceManager.reconciliationCoordinator.expectMsgClass(Disconnected.class);
                resourceManager.launchCoordinator.expectMsgClass(Disconnected.class);
                resourceManager.taskRouter.expectMsgClass(Disconnected.class);
            }
        };
    }

    @Test
    public void testClearStateAfterRevokeLeadership() throws Exception {
        new MesosResourceManagerTest.Context() {
            {
                final MesosWorkerStore.Worker worker1 = Worker.newWorker(task1);
                final MesosWorkerStore.Worker worker2 = Worker.newWorker(task2).launchWorker(slave1, slave1host);
                final MesosWorkerStore.Worker worker3 = Worker.newWorker(task3).launchWorker(slave1, slave1host).releaseWorker();
                Mockito.when(rmServices.workerStore.getFrameworkID()).thenReturn(Option.apply(framework1));
                Mockito.when(rmServices.workerStore.recoverWorkers()).thenReturn(Arrays.asList(worker1, worker2, worker3)).thenReturn(Collections.emptyList());
                startResourceManager();
                rmServices.rmLeaderElectionService.notLeader();
                rmServices.grantLeadership();
                Assert.assertThat(resourceManager.workersInNew.size(), Matchers.equalTo(0));
                Assert.assertThat(resourceManager.workersInLaunch.size(), Matchers.equalTo(0));
                Assert.assertThat(resourceManager.workersBeingReturned.size(), Matchers.equalTo(0));
                Mockito.verify(rmServices.schedulerDriver).stop(true);
            }
        };
    }
}

