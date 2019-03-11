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
package org.apache.flink.runtime.jobmaster;


import CheckpointRetentionPolicy.NEVER_RETAIN_AFTER_TERMINATION;
import ConfigConstants.RESTART_STRATEGY_FIXED_DELAY_ATTEMPTS;
import ConfigConstants.RESTART_STRATEGY_FIXED_DELAY_DELAY;
import ExecutionState.FINISHED;
import ExecutionState.SCHEDULED;
import JobManagerOptions.SLOT_REQUEST_TIMEOUT;
import JobStatus.FAILED;
import RpcUtils.INF_TIMEOUT;
import akka.actor.ActorSystem;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.io.DefaultInputSplitAssigner;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.java.ClosureCleaner;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.io.InputSplit;
import org.apache.flink.core.io.InputSplitAssigner;
import org.apache.flink.core.io.InputSplitSource;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.queryablestate.KvStateID;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.checkpoint.CheckpointProperties;
import org.apache.flink.runtime.checkpoint.CompletedCheckpoint;
import org.apache.flink.runtime.checkpoint.StandaloneCheckpointIDCounter;
import org.apache.flink.runtime.checkpoint.StandaloneCompletedCheckpointStore;
import org.apache.flink.runtime.checkpoint.TestingCheckpointRecoveryFactory;
import org.apache.flink.runtime.clusterframework.types.AllocationID;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.clusterframework.types.ResourceProfile;
import org.apache.flink.runtime.deployment.ResultPartitionDeploymentDescriptor;
import org.apache.flink.runtime.deployment.TaskDeploymentDescriptor;
import org.apache.flink.runtime.execution.ExecutionState;
import org.apache.flink.runtime.executiongraph.ArchivedExecutionGraph;
import org.apache.flink.runtime.executiongraph.ExecutionAttemptID;
import org.apache.flink.runtime.executiongraph.ExecutionGraph;
import org.apache.flink.runtime.executiongraph.ExecutionGraphTestUtils;
import org.apache.flink.runtime.executiongraph.ExecutionVertex;
import org.apache.flink.runtime.executiongraph.restart.FixedDelayRestartStrategy;
import org.apache.flink.runtime.executiongraph.restart.RestartStrategy;
import org.apache.flink.runtime.executiongraph.restart.RestartStrategyFactory;
import org.apache.flink.runtime.heartbeat.HeartbeatServices;
import org.apache.flink.runtime.highavailability.TestingHighAvailabilityServices;
import org.apache.flink.runtime.io.network.partition.ResultPartitionID;
import org.apache.flink.runtime.jobgraph.IntermediateDataSetID;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobgraph.JobVertexID;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.jobmanager.OnCompletionActions;
import org.apache.flink.runtime.jobmanager.PartitionProducerDisposedException;
import org.apache.flink.runtime.jobmaster.factories.UnregisteredJobManagerJobMetricGroupFactory;
import org.apache.flink.runtime.jobmaster.slotpool.DefaultSchedulerFactory;
import org.apache.flink.runtime.jobmaster.slotpool.DefaultSlotPoolFactory;
import org.apache.flink.runtime.leaderretrieval.SettableLeaderRetrievalService;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.messages.FlinkJobNotFoundException;
import org.apache.flink.runtime.messages.checkpoint.DeclineCheckpoint;
import org.apache.flink.runtime.query.KvStateLocation;
import org.apache.flink.runtime.query.UnknownKvStateLocation;
import org.apache.flink.runtime.registration.RegistrationResponse;
import org.apache.flink.runtime.resourcemanager.ResourceManagerId;
import org.apache.flink.runtime.resourcemanager.SlotRequest;
import org.apache.flink.runtime.resourcemanager.utils.TestingResourceManagerGateway;
import org.apache.flink.runtime.resourcemanager.utils.Tuple2;
import org.apache.flink.runtime.resourcemanager.utils.Tuple4;
import org.apache.flink.runtime.rpc.RpcService;
import org.apache.flink.runtime.rpc.RpcUtils;
import org.apache.flink.runtime.rpc.TestingRpcService;
import org.apache.flink.runtime.rpc.akka.AkkaRpcServiceConfiguration;
import org.apache.flink.runtime.state.CompletedCheckpointStorageLocation;
import org.apache.flink.runtime.state.KeyGroupRange;
import org.apache.flink.runtime.state.StreamStateHandle;
import org.apache.flink.runtime.taskexecutor.AccumulatorReport;
import org.apache.flink.runtime.taskexecutor.TestingTaskExecutorGateway;
import org.apache.flink.runtime.taskexecutor.TestingTaskExecutorGatewayBuilder;
import org.apache.flink.runtime.taskexecutor.rpc.RpcCheckpointResponder;
import org.apache.flink.runtime.taskexecutor.slot.SlotOffer;
import org.apache.flink.runtime.taskmanager.LocalTaskManagerLocation;
import org.apache.flink.runtime.taskmanager.TaskManagerLocation;
import org.apache.flink.runtime.testtasks.NoOpInvokable;
import org.apache.flink.runtime.util.TestingFatalErrorHandler;
import org.apache.flink.testutils.ClassLoaderUtils;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.InstantiationUtil;
import org.apache.flink.util.SerializedThrowable;
import org.apache.flink.util.TestLogger;
import org.apache.flink.util.function.SupplierWithException;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for {@link JobMaster}.
 */
public class JobMasterTest extends TestLogger {
    private static final JobMasterTest.TestingInputSplit[] EMPTY_TESTING_INPUT_SPLITS = new JobMasterTest.TestingInputSplit[0];

    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final Time testingTimeout = Time.seconds(10L);

    private static final long fastHeartbeatInterval = 1L;

    private static final long fastHeartbeatTimeout = 5L;

    private static final long heartbeatInterval = 1000L;

    private static final long heartbeatTimeout = 5000000L;

    private static final JobGraph jobGraph = new JobGraph();

    private static TestingRpcService rpcService;

    private static HeartbeatServices fastHeartbeatServices;

    private static HeartbeatServices heartbeatServices;

    private Configuration configuration;

    private ResourceID jmResourceId;

    private JobMasterId jobMasterId;

    private TestingHighAvailabilityServices haServices;

    private SettableLeaderRetrievalService rmLeaderRetrievalService;

    private TestingFatalErrorHandler testingFatalErrorHandler;

    @Test
    public void testDeclineCheckpointInvocationWithUserException() throws Exception {
        RpcService rpcService1 = null;
        RpcService rpcService2 = null;
        try {
            final ActorSystem actorSystem1 = AkkaUtils.createDefaultActorSystem();
            final ActorSystem actorSystem2 = AkkaUtils.createDefaultActorSystem();
            AkkaRpcServiceConfiguration akkaRpcServiceConfig = AkkaRpcServiceConfiguration.fromConfiguration(configuration);
            rpcService1 = new org.apache.flink.runtime.rpc.akka.AkkaRpcService(actorSystem1, akkaRpcServiceConfig);
            rpcService2 = new org.apache.flink.runtime.rpc.akka.AkkaRpcService(actorSystem2, akkaRpcServiceConfig);
            final CompletableFuture<Throwable> declineCheckpointMessageFuture = new CompletableFuture<>();
            final JobManagerSharedServices jobManagerSharedServices = new TestingJobManagerSharedServicesBuilder().build();
            final JobMasterConfiguration jobMasterConfiguration = JobMasterConfiguration.fromConfiguration(configuration);
            final JobMaster jobMaster = new JobMaster(rpcService1, jobMasterConfiguration, jmResourceId, JobMasterTest.jobGraph, haServices, DefaultSlotPoolFactory.fromConfiguration(configuration), DefaultSchedulerFactory.fromConfiguration(configuration), jobManagerSharedServices, JobMasterTest.heartbeatServices, UnregisteredJobManagerJobMetricGroupFactory.INSTANCE, new JobMasterTest.TestingOnCompletionActions(), testingFatalErrorHandler, JobMasterTest.class.getClassLoader()) {
                @Override
                public void declineCheckpoint(DeclineCheckpoint declineCheckpoint) {
                    declineCheckpointMessageFuture.complete(declineCheckpoint.getReason());
                }
            };
            jobMaster.start(jobMasterId).get();
            final String className = "UserException";
            final URLClassLoader userClassLoader = ClassLoaderUtils.compileAndLoadJava(JobMasterTest.temporaryFolder.newFolder(), (className + ".java"), String.format("public class %s extends RuntimeException { public %s() {super(\"UserMessage\");} }", className, className));
            Throwable userException = ((Throwable) (Class.forName(className, false, userClassLoader).newInstance()));
            JobMasterGateway jobMasterGateway = rpcService2.connect(jobMaster.getAddress(), jobMaster.getFencingToken(), JobMasterGateway.class).get();
            RpcCheckpointResponder rpcCheckpointResponder = new RpcCheckpointResponder(jobMasterGateway);
            rpcCheckpointResponder.declineCheckpoint(JobMasterTest.jobGraph.getJobID(), new ExecutionAttemptID(1, 1), 1, userException);
            Throwable throwable = declineCheckpointMessageFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            MatcherAssert.assertThat(throwable, Matchers.instanceOf(SerializedThrowable.class));
            MatcherAssert.assertThat(throwable.getMessage(), Matchers.equalTo(userException.getMessage()));
        } finally {
            RpcUtils.terminateRpcServices(JobMasterTest.testingTimeout, rpcService1, rpcService2);
        }
    }

    @Test
    public void testHeartbeatTimeoutWithTaskManager() throws Exception {
        final CompletableFuture<ResourceID> heartbeatResourceIdFuture = new CompletableFuture<>();
        final CompletableFuture<JobID> disconnectedJobManagerFuture = new CompletableFuture<>();
        final TaskManagerLocation taskManagerLocation = new LocalTaskManagerLocation();
        final TestingTaskExecutorGateway taskExecutorGateway = new TestingTaskExecutorGatewayBuilder().setHeartbeatJobManagerConsumer(heartbeatResourceIdFuture::complete).setDisconnectJobManagerConsumer(( jobId, throwable) -> disconnectedJobManagerFuture.complete(jobId)).createTestingTaskExecutorGateway();
        JobMasterTest.rpcService.registerGateway(taskExecutorGateway.getAddress(), taskExecutorGateway);
        final JobManagerSharedServices jobManagerSharedServices = new TestingJobManagerSharedServicesBuilder().build();
        final JobMaster jobMaster = createJobMaster(configuration, JobMasterTest.jobGraph, haServices, jobManagerSharedServices);
        CompletableFuture<Acknowledge> startFuture = jobMaster.start(jobMasterId);
        try {
            // wait for the start to complete
            startFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            final JobMasterGateway jobMasterGateway = jobMaster.getSelfGateway(JobMasterGateway.class);
            // register task manager will trigger monitor heartbeat target, schedule heartbeat request at interval time
            CompletableFuture<RegistrationResponse> registrationResponse = jobMasterGateway.registerTaskManager(taskExecutorGateway.getAddress(), taskManagerLocation, JobMasterTest.testingTimeout);
            // wait for the completion of the registration
            registrationResponse.get();
            final ResourceID heartbeatResourceId = heartbeatResourceIdFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            MatcherAssert.assertThat(heartbeatResourceId, Matchers.equalTo(jmResourceId));
            final JobID disconnectedJobManager = disconnectedJobManagerFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            MatcherAssert.assertThat(disconnectedJobManager, Matchers.equalTo(JobMasterTest.jobGraph.getJobID()));
        } finally {
            jobManagerSharedServices.shutdown();
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    @Test
    public void testHeartbeatTimeoutWithResourceManager() throws Exception {
        final String resourceManagerAddress = "rm";
        final ResourceManagerId resourceManagerId = ResourceManagerId.generate();
        final ResourceID rmResourceId = new ResourceID(resourceManagerAddress);
        final TestingResourceManagerGateway resourceManagerGateway = new TestingResourceManagerGateway(resourceManagerId, rmResourceId, resourceManagerAddress, "localhost");
        final CompletableFuture<Tuple3<JobMasterId, ResourceID, JobID>> jobManagerRegistrationFuture = new CompletableFuture<>();
        final CompletableFuture<JobID> disconnectedJobManagerFuture = new CompletableFuture<>();
        final CountDownLatch registrationAttempts = new CountDownLatch(2);
        resourceManagerGateway.setRegisterJobManagerConsumer(( tuple) -> {
            jobManagerRegistrationFuture.complete(Tuple3.of(tuple.f0, tuple.f1, tuple.f3));
            registrationAttempts.countDown();
        });
        resourceManagerGateway.setDisconnectJobManagerConsumer(( tuple) -> disconnectedJobManagerFuture.complete(tuple.f0));
        JobMasterTest.rpcService.registerGateway(resourceManagerAddress, resourceManagerGateway);
        final JobManagerSharedServices jobManagerSharedServices = new TestingJobManagerSharedServicesBuilder().build();
        final JobMaster jobMaster = createJobMaster(configuration, JobMasterTest.jobGraph, haServices, jobManagerSharedServices);
        CompletableFuture<Acknowledge> startFuture = jobMaster.start(jobMasterId);
        try {
            // wait for the start operation to complete
            startFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            // define a leader and see that a registration happens
            rmLeaderRetrievalService.notifyListener(resourceManagerAddress, resourceManagerId.toUUID());
            // register job manager success will trigger monitor heartbeat target between jm and rm
            final Tuple3<JobMasterId, ResourceID, JobID> registrationInformation = jobManagerRegistrationFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            MatcherAssert.assertThat(registrationInformation.f0, Matchers.equalTo(jobMasterId));
            MatcherAssert.assertThat(registrationInformation.f1, Matchers.equalTo(jmResourceId));
            MatcherAssert.assertThat(registrationInformation.f2, Matchers.equalTo(JobMasterTest.jobGraph.getJobID()));
            final JobID disconnectedJobManager = disconnectedJobManagerFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            // heartbeat timeout should trigger disconnect JobManager from ResourceManager
            MatcherAssert.assertThat(disconnectedJobManager, Matchers.equalTo(JobMasterTest.jobGraph.getJobID()));
            // the JobMaster should try to reconnect to the RM
            registrationAttempts.await();
        } finally {
            jobManagerSharedServices.shutdown();
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    /**
     * Tests that a JobMaster will restore the given JobGraph from its savepoint upon
     * initial submission.
     */
    @Test
    public void testRestoringFromSavepoint() throws Exception {
        // create savepoint data
        final long savepointId = 42L;
        final File savepointFile = createSavepoint(savepointId);
        // set savepoint settings
        final SavepointRestoreSettings savepointRestoreSettings = SavepointRestoreSettings.forPath(savepointFile.getAbsolutePath(), true);
        final JobGraph jobGraph = createJobGraphWithCheckpointing(savepointRestoreSettings);
        final StandaloneCompletedCheckpointStore completedCheckpointStore = new StandaloneCompletedCheckpointStore(1);
        final TestingCheckpointRecoveryFactory testingCheckpointRecoveryFactory = new TestingCheckpointRecoveryFactory(completedCheckpointStore, new StandaloneCheckpointIDCounter());
        haServices.setCheckpointRecoveryFactory(testingCheckpointRecoveryFactory);
        final JobMaster jobMaster = createJobMaster(configuration, jobGraph, haServices, new TestingJobManagerSharedServicesBuilder().build());
        try {
            // starting the JobMaster should have read the savepoint
            final CompletedCheckpoint savepointCheckpoint = completedCheckpointStore.getLatestCheckpoint();
            MatcherAssert.assertThat(savepointCheckpoint, Matchers.notNullValue());
            MatcherAssert.assertThat(savepointCheckpoint.getCheckpointID(), Matchers.is(savepointId));
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    /**
     * Tests that a JobMaster will only restore a modified JobGraph if non
     * restored state is allowed.
     */
    @Test
    public void testRestoringModifiedJobFromSavepoint() throws Exception {
        // create savepoint data
        final long savepointId = 42L;
        final OperatorID operatorID = new OperatorID();
        final File savepointFile = createSavepointWithOperatorState(savepointId, operatorID);
        // set savepoint settings which don't allow non restored state
        final SavepointRestoreSettings savepointRestoreSettings = SavepointRestoreSettings.forPath(savepointFile.getAbsolutePath(), false);
        // create a new operator
        final JobVertex jobVertex = new JobVertex("New operator");
        jobVertex.setInvokableClass(NoOpInvokable.class);
        final JobGraph jobGraphWithNewOperator = createJobGraphFromJobVerticesWithCheckpointing(savepointRestoreSettings, jobVertex);
        final StandaloneCompletedCheckpointStore completedCheckpointStore = new StandaloneCompletedCheckpointStore(1);
        final TestingCheckpointRecoveryFactory testingCheckpointRecoveryFactory = new TestingCheckpointRecoveryFactory(completedCheckpointStore, new StandaloneCheckpointIDCounter());
        haServices.setCheckpointRecoveryFactory(testingCheckpointRecoveryFactory);
        try {
            createJobMaster(configuration, jobGraphWithNewOperator, haServices, new TestingJobManagerSharedServicesBuilder().build());
            Assert.fail("Should fail because we cannot resume the changed JobGraph from the savepoint.");
        } catch (IllegalStateException expected) {
            // that was expected :-)
        }
        // allow for non restored state
        jobGraphWithNewOperator.setSavepointRestoreSettings(SavepointRestoreSettings.forPath(savepointFile.getAbsolutePath(), true));
        final JobMaster jobMaster = createJobMaster(configuration, jobGraphWithNewOperator, haServices, new TestingJobManagerSharedServicesBuilder().build());
        try {
            // starting the JobMaster should have read the savepoint
            final CompletedCheckpoint savepointCheckpoint = completedCheckpointStore.getLatestCheckpoint();
            MatcherAssert.assertThat(savepointCheckpoint, Matchers.notNullValue());
            MatcherAssert.assertThat(savepointCheckpoint.getCheckpointID(), Matchers.is(savepointId));
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    /**
     * Tests that in a streaming use case where checkpointing is enabled, a
     * fixed delay with Integer.MAX_VALUE retries is instantiated if no other restart
     * strategy has been specified.
     */
    @Test
    public void testAutomaticRestartingWhenCheckpointing() throws Exception {
        // create savepoint data
        final long savepointId = 42L;
        final File savepointFile = createSavepoint(savepointId);
        // set savepoint settings
        final SavepointRestoreSettings savepointRestoreSettings = SavepointRestoreSettings.forPath(savepointFile.getAbsolutePath(), true);
        final JobGraph jobGraph = createJobGraphWithCheckpointing(savepointRestoreSettings);
        final StandaloneCompletedCheckpointStore completedCheckpointStore = new StandaloneCompletedCheckpointStore(1);
        final TestingCheckpointRecoveryFactory testingCheckpointRecoveryFactory = new TestingCheckpointRecoveryFactory(completedCheckpointStore, new StandaloneCheckpointIDCounter());
        haServices.setCheckpointRecoveryFactory(testingCheckpointRecoveryFactory);
        final JobMaster jobMaster = createJobMaster(new Configuration(), jobGraph, haServices, new TestingJobManagerSharedServicesBuilder().setRestartStrategyFactory(RestartStrategyFactory.createRestartStrategyFactory(configuration)).build());
        RestartStrategy restartStrategy = jobMaster.getRestartStrategy();
        Assert.assertNotNull(restartStrategy);
        Assert.assertTrue((restartStrategy instanceof FixedDelayRestartStrategy));
    }

    /**
     * Tests that an existing checkpoint will have precedence over an savepoint.
     */
    @Test
    public void testCheckpointPrecedesSavepointRecovery() throws Exception {
        // create savepoint data
        final long savepointId = 42L;
        final File savepointFile = createSavepoint(savepointId);
        // set savepoint settings
        final SavepointRestoreSettings savepointRestoreSettings = SavepointRestoreSettings.forPath(("" + (savepointFile.getAbsolutePath())), true);
        final JobGraph jobGraph = createJobGraphWithCheckpointing(savepointRestoreSettings);
        final long checkpointId = 1L;
        final CompletedCheckpoint completedCheckpoint = new CompletedCheckpoint(jobGraph.getJobID(), checkpointId, 1L, 1L, Collections.emptyMap(), null, CheckpointProperties.forCheckpoint(NEVER_RETAIN_AFTER_TERMINATION), new JobMasterTest.DummyCheckpointStorageLocation());
        final StandaloneCompletedCheckpointStore completedCheckpointStore = new StandaloneCompletedCheckpointStore(1);
        completedCheckpointStore.addCheckpoint(completedCheckpoint);
        final TestingCheckpointRecoveryFactory testingCheckpointRecoveryFactory = new TestingCheckpointRecoveryFactory(completedCheckpointStore, new StandaloneCheckpointIDCounter());
        haServices.setCheckpointRecoveryFactory(testingCheckpointRecoveryFactory);
        final JobMaster jobMaster = createJobMaster(configuration, jobGraph, haServices, new TestingJobManagerSharedServicesBuilder().build());
        try {
            // starting the JobMaster should have read the savepoint
            final CompletedCheckpoint savepointCheckpoint = completedCheckpointStore.getLatestCheckpoint();
            MatcherAssert.assertThat(savepointCheckpoint, Matchers.notNullValue());
            MatcherAssert.assertThat(savepointCheckpoint.getCheckpointID(), Matchers.is(checkpointId));
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    /**
     * Tests that the JobMaster retries the scheduling of a job
     * in case of a missing slot offering from a registered TaskExecutor.
     */
    @Test
    public void testSlotRequestTimeoutWhenNoSlotOffering() throws Exception {
        final JobGraph restartingJobGraph = createSingleVertexJobWithRestartStrategy();
        final long slotRequestTimeout = 10L;
        configuration.setLong(SLOT_REQUEST_TIMEOUT, slotRequestTimeout);
        final JobMaster jobMaster = createJobMaster(configuration, restartingJobGraph, haServices, new TestingJobManagerSharedServicesBuilder().build(), JobMasterTest.heartbeatServices);
        final JobMasterGateway jobMasterGateway = jobMaster.getSelfGateway(JobMasterGateway.class);
        try {
            final long start = System.nanoTime();
            jobMaster.start(JobMasterId.generate()).get();
            final TestingResourceManagerGateway resourceManagerGateway = createAndRegisterTestingResourceManagerGateway();
            final ArrayBlockingQueue<SlotRequest> blockingQueue = new ArrayBlockingQueue<>(2);
            resourceManagerGateway.setRequestSlotConsumer(blockingQueue::offer);
            notifyResourceManagerLeaderListeners(resourceManagerGateway);
            // wait for the first slot request
            blockingQueue.take();
            final CompletableFuture<TaskDeploymentDescriptor> submittedTaskFuture = new CompletableFuture<>();
            final LocalTaskManagerLocation taskManagerLocation = new LocalTaskManagerLocation();
            final TestingTaskExecutorGateway taskExecutorGateway = new TestingTaskExecutorGatewayBuilder().setSubmitTaskConsumer(( tdd, ignored) -> {
                submittedTaskFuture.complete(tdd);
                return CompletableFuture.completedFuture(Acknowledge.get());
            }).createTestingTaskExecutorGateway();
            JobMasterTest.rpcService.registerGateway(taskExecutorGateway.getAddress(), taskExecutorGateway);
            jobMasterGateway.registerTaskManager(taskExecutorGateway.getAddress(), taskManagerLocation, JobMasterTest.testingTimeout).get();
            // wait for the slot request timeout
            final SlotRequest slotRequest = blockingQueue.take();
            final long end = System.nanoTime();
            // we rely on the slot request timeout to fail a stuck scheduling operation
            MatcherAssert.assertThat(((end - start) / 1000000L), Matchers.greaterThanOrEqualTo(slotRequestTimeout));
            MatcherAssert.assertThat(submittedTaskFuture.isDone(), Matchers.is(false));
            final SlotOffer slotOffer = new SlotOffer(slotRequest.getAllocationId(), 0, ResourceProfile.UNKNOWN);
            final CompletableFuture<Collection<SlotOffer>> acceptedSlotsFuture = jobMasterGateway.offerSlots(getResourceID(), Collections.singleton(slotOffer), JobMasterTest.testingTimeout);
            final Collection<SlotOffer> acceptedSlots = acceptedSlotsFuture.get();
            MatcherAssert.assertThat(acceptedSlots, Matchers.hasSize(1));
            final SlotOffer acceptedSlot = acceptedSlots.iterator().next();
            MatcherAssert.assertThat(acceptedSlot.getAllocationId(), Matchers.equalTo(slotRequest.getAllocationId()));
            // wait for the deployed task
            final TaskDeploymentDescriptor taskDeploymentDescriptor = submittedTaskFuture.get();
            MatcherAssert.assertThat(taskDeploymentDescriptor.getAllocationId(), Matchers.equalTo(slotRequest.getAllocationId()));
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    /**
     * Tests that we can close an unestablished ResourceManager connection.
     */
    @Test
    public void testCloseUnestablishedResourceManagerConnection() throws Exception {
        final JobMaster jobMaster = createJobMaster(configuration, JobMasterTest.jobGraph, haServices, new TestingJobManagerSharedServicesBuilder().build());
        try {
            jobMaster.start(JobMasterId.generate()).get();
            final TestingResourceManagerGateway firstResourceManagerGateway = createAndRegisterTestingResourceManagerGateway();
            final TestingResourceManagerGateway secondResourceManagerGateway = createAndRegisterTestingResourceManagerGateway();
            final OneShotLatch firstJobManagerRegistration = new OneShotLatch();
            final OneShotLatch secondJobManagerRegistration = new OneShotLatch();
            firstResourceManagerGateway.setRegisterJobManagerConsumer(( jobMasterIdResourceIDStringJobIDTuple4) -> firstJobManagerRegistration.trigger());
            secondResourceManagerGateway.setRegisterJobManagerConsumer(( jobMasterIdResourceIDStringJobIDTuple4) -> secondJobManagerRegistration.trigger());
            notifyResourceManagerLeaderListeners(firstResourceManagerGateway);
            // wait until we have seen the first registration attempt
            firstJobManagerRegistration.await();
            // this should stop the connection attempts towards the first RM
            notifyResourceManagerLeaderListeners(secondResourceManagerGateway);
            // check that we start registering at the second RM
            secondJobManagerRegistration.await();
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    /**
     * Tests that we continue reconnecting to the latest known RM after a disconnection
     * message.
     */
    @Test
    public void testReconnectionAfterDisconnect() throws Exception {
        final JobMaster jobMaster = createJobMaster(configuration, JobMasterTest.jobGraph, haServices, new TestingJobManagerSharedServicesBuilder().build());
        final JobMasterGateway jobMasterGateway = jobMaster.getSelfGateway(JobMasterGateway.class);
        CompletableFuture<Acknowledge> startFuture = jobMaster.start(jobMasterId);
        try {
            // wait for the start to complete
            startFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            final TestingResourceManagerGateway testingResourceManagerGateway = createAndRegisterTestingResourceManagerGateway();
            final BlockingQueue<JobMasterId> registrationsQueue = new ArrayBlockingQueue<>(1);
            testingResourceManagerGateway.setRegisterJobManagerConsumer(( jobMasterIdResourceIDStringJobIDTuple4) -> registrationsQueue.offer(jobMasterIdResourceIDStringJobIDTuple4.f0));
            final ResourceManagerId resourceManagerId = testingResourceManagerGateway.getFencingToken();
            notifyResourceManagerLeaderListeners(testingResourceManagerGateway);
            // wait for first registration attempt
            final JobMasterId firstRegistrationAttempt = registrationsQueue.take();
            MatcherAssert.assertThat(firstRegistrationAttempt, Matchers.equalTo(jobMasterId));
            MatcherAssert.assertThat(registrationsQueue.isEmpty(), Matchers.is(true));
            jobMasterGateway.disconnectResourceManager(resourceManagerId, new FlinkException("Test exception"));
            // wait for the second registration attempt after the disconnect call
            MatcherAssert.assertThat(registrationsQueue.take(), Matchers.equalTo(jobMasterId));
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    /**
     * Tests that the a JM connects to the leading RM after regaining leadership.
     */
    @Test
    public void testResourceManagerConnectionAfterRegainingLeadership() throws Exception {
        final JobMaster jobMaster = createJobMaster(configuration, JobMasterTest.jobGraph, haServices, new TestingJobManagerSharedServicesBuilder().build());
        CompletableFuture<Acknowledge> startFuture = jobMaster.start(jobMasterId);
        try {
            // wait for the start to complete
            startFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            final TestingResourceManagerGateway testingResourceManagerGateway = createAndRegisterTestingResourceManagerGateway();
            final BlockingQueue<JobMasterId> registrationQueue = new ArrayBlockingQueue<>(1);
            testingResourceManagerGateway.setRegisterJobManagerConsumer(( jobMasterIdResourceIDStringJobIDTuple4) -> registrationQueue.offer(jobMasterIdResourceIDStringJobIDTuple4.f0));
            notifyResourceManagerLeaderListeners(testingResourceManagerGateway);
            final JobMasterId firstRegistrationAttempt = registrationQueue.take();
            MatcherAssert.assertThat(firstRegistrationAttempt, Matchers.equalTo(jobMasterId));
            jobMaster.suspend(new FlinkException("Test exception.")).get();
            final JobMasterId jobMasterId2 = JobMasterId.generate();
            jobMaster.start(jobMasterId2).get();
            final JobMasterId secondRegistrationAttempt = registrationQueue.take();
            MatcherAssert.assertThat(secondRegistrationAttempt, Matchers.equalTo(jobMasterId2));
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    @Test
    public void testRequestNextInputSplit() throws Exception {
        final List<JobMasterTest.TestingInputSplit> expectedInputSplits = Arrays.asList(new JobMasterTest.TestingInputSplit(1), new JobMasterTest.TestingInputSplit(42), new JobMasterTest.TestingInputSplit(1337));
        // build one node JobGraph
        InputSplitSource<JobMasterTest.TestingInputSplit> inputSplitSource = new JobMasterTest.TestingInputSplitSource(expectedInputSplits);
        JobVertex source = new JobVertex("vertex1");
        source.setParallelism(1);
        source.setInputSplitSource(inputSplitSource);
        source.setInvokableClass(AbstractInvokable.class);
        final JobGraph testJobGraph = new JobGraph(source);
        testJobGraph.setAllowQueuedScheduling(true);
        configuration.setLong(RESTART_STRATEGY_FIXED_DELAY_ATTEMPTS, 1);
        configuration.setString(RESTART_STRATEGY_FIXED_DELAY_DELAY, "0 s");
        final JobManagerSharedServices jobManagerSharedServices = new TestingJobManagerSharedServicesBuilder().setRestartStrategyFactory(RestartStrategyFactory.createRestartStrategyFactory(configuration)).build();
        final JobMaster jobMaster = createJobMaster(configuration, testJobGraph, haServices, jobManagerSharedServices);
        CompletableFuture<Acknowledge> startFuture = jobMaster.start(jobMasterId);
        try {
            // wait for the start to complete
            startFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            final JobMasterGateway jobMasterGateway = jobMaster.getSelfGateway(JobMasterGateway.class);
            ExecutionGraph eg = jobMaster.getExecutionGraph();
            ExecutionVertex ev = eg.getAllExecutionVertices().iterator().next();
            final SupplierWithException<SerializedInputSplit, Exception> inputSplitSupplier = () -> jobMasterGateway.requestNextInputSplit(source.getID(), ev.getCurrentExecutionAttempt().getAttemptId()).get();
            List<InputSplit> actualInputSplits = JobMasterTest.getInputSplits(expectedInputSplits.size(), inputSplitSupplier);
            final Matcher<Iterable<? extends InputSplit>> expectedInputSplitsMatcher = Matchers.containsInAnyOrder(expectedInputSplits.toArray(JobMasterTest.EMPTY_TESTING_INPUT_SPLITS));
            MatcherAssert.assertThat(actualInputSplits, expectedInputSplitsMatcher);
            final long maxWaitMillis = 2000L;
            ExecutionGraphTestUtils.waitUntilExecutionVertexState(ev, SCHEDULED, maxWaitMillis);
            CompletableFuture.runAsync(() -> eg.failGlobal(new Exception("Testing exception")), eg.getJobMasterMainThreadExecutor()).get();
            ExecutionGraphTestUtils.waitUntilExecutionVertexState(ev, SCHEDULED, maxWaitMillis);
            actualInputSplits = JobMasterTest.getInputSplits(expectedInputSplits.size(), inputSplitSupplier);
            MatcherAssert.assertThat(actualInputSplits, expectedInputSplitsMatcher);
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    private static final class TestingInputSplitSource implements InputSplitSource<JobMasterTest.TestingInputSplit> {
        private static final long serialVersionUID = -2344684048759139086L;

        private final List<JobMasterTest.TestingInputSplit> inputSplits;

        private TestingInputSplitSource(List<JobMasterTest.TestingInputSplit> inputSplits) {
            this.inputSplits = inputSplits;
        }

        @Override
        public JobMasterTest.TestingInputSplit[] createInputSplits(int minNumSplits) {
            return inputSplits.toArray(JobMasterTest.EMPTY_TESTING_INPUT_SPLITS);
        }

        @Override
        public InputSplitAssigner getInputSplitAssigner(JobMasterTest.TestingInputSplit[] inputSplits) {
            return new DefaultInputSplitAssigner(inputSplits);
        }
    }

    private static final class TestingInputSplit implements InputSplit {
        private static final long serialVersionUID = -5404803705463116083L;

        private final int splitNumber;

        TestingInputSplit(int number) {
            this.splitNumber = number;
        }

        public int getSplitNumber() {
            return splitNumber;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            JobMasterTest.TestingInputSplit that = ((JobMasterTest.TestingInputSplit) (o));
            return (splitNumber) == (that.splitNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(splitNumber);
        }
    }

    @Test
    public void testRequestKvStateWithoutRegistration() throws Exception {
        final JobGraph graph = createKvJobGraph();
        final JobMaster jobMaster = createJobMaster(configuration, graph, haServices, new TestingJobManagerSharedServicesBuilder().build(), JobMasterTest.heartbeatServices);
        CompletableFuture<Acknowledge> startFuture = jobMaster.start(jobMasterId);
        final JobMasterGateway jobMasterGateway = jobMaster.getSelfGateway(JobMasterGateway.class);
        try {
            // wait for the start to complete
            startFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            // lookup location
            try {
                jobMasterGateway.requestKvStateLocation(graph.getJobID(), "unknown").get();
                Assert.fail("Expected to fail with UnknownKvStateLocation");
            } catch (Exception e) {
                Assert.assertTrue(ExceptionUtils.findThrowable(e, UnknownKvStateLocation.class).isPresent());
            }
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    @Test
    public void testRequestKvStateOfWrongJob() throws Exception {
        final JobGraph graph = createKvJobGraph();
        final JobMaster jobMaster = createJobMaster(configuration, graph, haServices, new TestingJobManagerSharedServicesBuilder().build(), JobMasterTest.heartbeatServices);
        CompletableFuture<Acknowledge> startFuture = jobMaster.start(jobMasterId);
        final JobMasterGateway jobMasterGateway = jobMaster.getSelfGateway(JobMasterGateway.class);
        try {
            // wait for the start to complete
            startFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            // lookup location
            try {
                jobMasterGateway.requestKvStateLocation(new JobID(), "unknown").get();
                Assert.fail("Expected to fail with FlinkJobNotFoundException");
            } catch (Exception e) {
                Assert.assertTrue(ExceptionUtils.findThrowable(e, FlinkJobNotFoundException.class).isPresent());
            }
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    @Test
    public void testRequestKvStateWithIrrelevantRegistration() throws Exception {
        final JobGraph graph = createKvJobGraph();
        final JobMaster jobMaster = createJobMaster(configuration, graph, haServices, new TestingJobManagerSharedServicesBuilder().build(), JobMasterTest.heartbeatServices);
        CompletableFuture<Acknowledge> startFuture = jobMaster.start(jobMasterId);
        final JobMasterGateway jobMasterGateway = jobMaster.getSelfGateway(JobMasterGateway.class);
        try {
            // wait for the start to complete
            startFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            // register an irrelevant KvState
            try {
                jobMasterGateway.notifyKvStateRegistered(new JobID(), new JobVertexID(), new KeyGroupRange(0, 0), "any-name", new KvStateID(), new InetSocketAddress(InetAddress.getLocalHost(), 1233)).get();
                Assert.fail("Expected to fail with FlinkJobNotFoundException.");
            } catch (Exception e) {
                Assert.assertTrue(ExceptionUtils.findThrowable(e, FlinkJobNotFoundException.class).isPresent());
            }
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    @Test
    public void testRegisterAndUnregisterKvState() throws Exception {
        final JobGraph graph = createKvJobGraph();
        final List<JobVertex> jobVertices = graph.getVerticesSortedTopologicallyFromSources();
        final JobVertex vertex1 = jobVertices.get(0);
        final JobMaster jobMaster = createJobMaster(configuration, graph, haServices, new TestingJobManagerSharedServicesBuilder().build(), JobMasterTest.heartbeatServices);
        CompletableFuture<Acknowledge> startFuture = jobMaster.start(jobMasterId);
        final JobMasterGateway jobMasterGateway = jobMaster.getSelfGateway(JobMasterGateway.class);
        try {
            // wait for the start to complete
            startFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            // register a KvState
            final String registrationName = "register-me";
            final KvStateID kvStateID = new KvStateID();
            final KeyGroupRange keyGroupRange = new KeyGroupRange(0, 0);
            final InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), 1029);
            jobMasterGateway.notifyKvStateRegistered(graph.getJobID(), vertex1.getID(), keyGroupRange, registrationName, kvStateID, address).get();
            final KvStateLocation location = jobMasterGateway.requestKvStateLocation(graph.getJobID(), registrationName).get();
            Assert.assertEquals(graph.getJobID(), location.getJobId());
            Assert.assertEquals(vertex1.getID(), location.getJobVertexId());
            Assert.assertEquals(vertex1.getMaxParallelism(), location.getNumKeyGroups());
            Assert.assertEquals(1, location.getNumRegisteredKeyGroups());
            Assert.assertEquals(1, keyGroupRange.getNumberOfKeyGroups());
            Assert.assertEquals(kvStateID, location.getKvStateID(keyGroupRange.getStartKeyGroup()));
            Assert.assertEquals(address, location.getKvStateServerAddress(keyGroupRange.getStartKeyGroup()));
            // unregister the KvState
            jobMasterGateway.notifyKvStateUnregistered(graph.getJobID(), vertex1.getID(), keyGroupRange, registrationName).get();
            try {
                jobMasterGateway.requestKvStateLocation(graph.getJobID(), registrationName).get();
                Assert.fail("Expected to fail with an UnknownKvStateLocation.");
            } catch (Exception e) {
                Assert.assertTrue(ExceptionUtils.findThrowable(e, UnknownKvStateLocation.class).isPresent());
            }
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    @Test
    public void testDuplicatedKvStateRegistrationsFailTask() throws Exception {
        final JobGraph graph = createKvJobGraph();
        final List<JobVertex> jobVertices = graph.getVerticesSortedTopologicallyFromSources();
        final JobVertex vertex1 = jobVertices.get(0);
        final JobVertex vertex2 = jobVertices.get(1);
        final JobMaster jobMaster = createJobMaster(configuration, graph, haServices, new TestingJobManagerSharedServicesBuilder().build(), JobMasterTest.heartbeatServices);
        CompletableFuture<Acknowledge> startFuture = jobMaster.start(jobMasterId);
        final JobMasterGateway jobMasterGateway = jobMaster.getSelfGateway(JobMasterGateway.class);
        try {
            // wait for the start to complete
            startFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            // duplicate registration fails task
            // register a KvState
            final String registrationName = "duplicate-me";
            final KvStateID kvStateID = new KvStateID();
            final KeyGroupRange keyGroupRange = new KeyGroupRange(0, 0);
            final InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost(), 4396);
            jobMasterGateway.notifyKvStateRegistered(graph.getJobID(), vertex1.getID(), keyGroupRange, registrationName, kvStateID, address).get();
            try {
                // <--- different operator, but...
                // ...same name
                jobMasterGateway.notifyKvStateRegistered(graph.getJobID(), vertex2.getID(), keyGroupRange, registrationName, kvStateID, address).get();
                Assert.fail("Expected to fail because of clashing registration message.");
            } catch (Exception e) {
                Assert.assertTrue(ExceptionUtils.findThrowableWithMessage(e, "Registration name clash").isPresent());
                Assert.assertEquals(FAILED, jobMaster.getExecutionGraph().getState());
            }
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    /**
     * Tests the {@link JobMaster#requestPartitionState(IntermediateDataSetID, ResultPartitionID)}
     * call for a finished result partition.
     */
    @Test
    public void testRequestPartitionState() throws Exception {
        final JobGraph producerConsumerJobGraph = producerConsumerJobGraph();
        final JobMaster jobMaster = createJobMaster(configuration, producerConsumerJobGraph, haServices, new TestingJobManagerSharedServicesBuilder().build(), JobMasterTest.heartbeatServices);
        CompletableFuture<Acknowledge> startFuture = jobMaster.start(jobMasterId);
        try {
            // wait for the start to complete
            startFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            final CompletableFuture<TaskDeploymentDescriptor> tddFuture = new CompletableFuture<>();
            final TestingTaskExecutorGateway testingTaskExecutorGateway = new TestingTaskExecutorGatewayBuilder().setSubmitTaskConsumer(( taskDeploymentDescriptor, jobMasterId) -> {
                tddFuture.complete(taskDeploymentDescriptor);
                return CompletableFuture.completedFuture(Acknowledge.get());
            }).createTestingTaskExecutorGateway();
            final JobMasterGateway jobMasterGateway = jobMaster.getSelfGateway(JobMasterGateway.class);
            final Collection<SlotOffer> slotOffers = registerSlotsAtJobMaster(1, jobMasterGateway, testingTaskExecutorGateway);
            MatcherAssert.assertThat(slotOffers, Matchers.hasSize(1));
            // obtain tdd for the result partition ids
            final TaskDeploymentDescriptor tdd = tddFuture.get();
            MatcherAssert.assertThat(tdd.getProducedPartitions(), Matchers.hasSize(1));
            final ResultPartitionDeploymentDescriptor partition = tdd.getProducedPartitions().iterator().next();
            final ExecutionAttemptID executionAttemptId = tdd.getExecutionAttemptId();
            final ExecutionAttemptID copiedExecutionAttemptId = new ExecutionAttemptID(executionAttemptId.getLowerPart(), executionAttemptId.getUpperPart());
            // finish the producer task
            jobMasterGateway.updateTaskExecutionState(new org.apache.flink.runtime.taskmanager.TaskExecutionState(producerConsumerJobGraph.getJobID(), executionAttemptId, ExecutionState.FINISHED)).get();
            // request the state of the result partition of the producer
            final ResultPartitionID partitionId = new ResultPartitionID(partition.getPartitionId(), copiedExecutionAttemptId);
            CompletableFuture<ExecutionState> partitionStateFuture = jobMasterGateway.requestPartitionState(partition.getResultId(), partitionId);
            MatcherAssert.assertThat(partitionStateFuture.get(), Matchers.equalTo(FINISHED));
            // ask for unknown result partition
            partitionStateFuture = jobMasterGateway.requestPartitionState(partition.getResultId(), new ResultPartitionID());
            try {
                partitionStateFuture.get();
                Assert.fail("Expected failure.");
            } catch (ExecutionException e) {
                MatcherAssert.assertThat(ExceptionUtils.findThrowable(e, IllegalArgumentException.class).isPresent(), Matchers.is(true));
            }
            // ask for wrong intermediate data set id
            partitionStateFuture = jobMasterGateway.requestPartitionState(new IntermediateDataSetID(), partitionId);
            try {
                partitionStateFuture.get();
                Assert.fail("Expected failure.");
            } catch (ExecutionException e) {
                MatcherAssert.assertThat(ExceptionUtils.findThrowable(e, IllegalArgumentException.class).isPresent(), Matchers.is(true));
            }
            // ask for "old" execution
            partitionStateFuture = jobMasterGateway.requestPartitionState(partition.getResultId(), new ResultPartitionID(partition.getPartitionId(), new ExecutionAttemptID()));
            try {
                partitionStateFuture.get();
                Assert.fail("Expected failure.");
            } catch (ExecutionException e) {
                MatcherAssert.assertThat(ExceptionUtils.findThrowable(e, PartitionProducerDisposedException.class).isPresent(), Matchers.is(true));
            }
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    /**
     * Tests that the timeout in {@link JobMasterGateway#triggerSavepoint(String, boolean, Time)}
     * is respected.
     */
    @Test
    public void testTriggerSavepointTimeout() throws Exception {
        final JobMaster jobMaster = new JobMaster(JobMasterTest.rpcService, JobMasterConfiguration.fromConfiguration(configuration), jmResourceId, JobMasterTest.jobGraph, haServices, DefaultSlotPoolFactory.fromConfiguration(configuration), DefaultSchedulerFactory.fromConfiguration(configuration), new TestingJobManagerSharedServicesBuilder().build(), JobMasterTest.heartbeatServices, UnregisteredJobManagerJobMetricGroupFactory.INSTANCE, new JobMasterTest.TestingOnCompletionActions(), testingFatalErrorHandler, JobMasterTest.class.getClassLoader()) {
            @Override
            public CompletableFuture<String> triggerSavepoint(@Nullable
            final String targetDirectory, final boolean cancelJob, final Time timeout) {
                return new CompletableFuture<>();
            }
        };
        try {
            final CompletableFuture<Acknowledge> startFuture = jobMaster.start(jobMasterId);
            startFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            final JobMasterGateway jobMasterGateway = jobMaster.getSelfGateway(JobMasterGateway.class);
            final CompletableFuture<String> savepointFutureLowTimeout = jobMasterGateway.triggerSavepoint("/tmp", false, Time.milliseconds(1));
            final CompletableFuture<String> savepointFutureHighTimeout = jobMasterGateway.triggerSavepoint("/tmp", false, INF_TIMEOUT);
            try {
                savepointFutureLowTimeout.get(JobMasterTest.testingTimeout.getSize(), JobMasterTest.testingTimeout.getUnit());
                Assert.fail();
            } catch (final ExecutionException e) {
                final Throwable cause = ExceptionUtils.stripExecutionException(e);
                MatcherAssert.assertThat(cause, Matchers.instanceOf(TimeoutException.class));
            }
            MatcherAssert.assertThat(savepointFutureHighTimeout.isDone(), Matchers.is(Matchers.equalTo(false)));
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    /**
     * Tests that the TaskExecutor is released if all of its slots have been freed.
     */
    @Test
    public void testReleasingTaskExecutorIfNoMoreSlotsRegistered() throws Exception {
        final JobManagerSharedServices jobManagerSharedServices = new TestingJobManagerSharedServicesBuilder().build();
        final JobGraph jobGraph = createSingleVertexJobWithRestartStrategy();
        final JobMaster jobMaster = createJobMaster(configuration, jobGraph, haServices, jobManagerSharedServices, JobMasterTest.heartbeatServices);
        final CompletableFuture<JobID> disconnectTaskExecutorFuture = new CompletableFuture<>();
        final CompletableFuture<AllocationID> freedSlotFuture = new CompletableFuture<>();
        final TestingTaskExecutorGateway testingTaskExecutorGateway = new TestingTaskExecutorGatewayBuilder().setFreeSlotFunction(( allocationID, throwable) -> {
            freedSlotFuture.complete(allocationID);
            return CompletableFuture.completedFuture(Acknowledge.get());
        }).setDisconnectJobManagerConsumer(( jobID, throwable) -> disconnectTaskExecutorFuture.complete(jobID)).createTestingTaskExecutorGateway();
        try {
            jobMaster.start(jobMasterId).get();
            final JobMasterGateway jobMasterGateway = jobMaster.getSelfGateway(JobMasterGateway.class);
            final Collection<SlotOffer> slotOffers = registerSlotsAtJobMaster(1, jobMasterGateway, testingTaskExecutorGateway);
            // check that we accepted the offered slot
            MatcherAssert.assertThat(slotOffers, Matchers.hasSize(1));
            final AllocationID allocationId = slotOffers.iterator().next().getAllocationId();
            // now fail the allocation and check that we close the connection to the TaskExecutor
            jobMasterGateway.notifyAllocationFailure(allocationId, new FlinkException("Fail alloction test exception"));
            // we should free the slot and then disconnect from the TaskExecutor because we use no longer slots from it
            MatcherAssert.assertThat(freedSlotFuture.get(), Matchers.equalTo(allocationId));
            MatcherAssert.assertThat(disconnectTaskExecutorFuture.get(), Matchers.equalTo(jobGraph.getJobID()));
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    /**
     * Tests the updateGlobalAggregate functionality
     */
    @Test
    public void testJobMasterAggregatesValuesCorrectly() throws Exception {
        final JobMaster jobMaster = createJobMaster(configuration, JobMasterTest.jobGraph, haServices, new TestingJobManagerSharedServicesBuilder().build(), JobMasterTest.heartbeatServices);
        CompletableFuture<Acknowledge> startFuture = jobMaster.start(jobMasterId);
        final JobMasterGateway jobMasterGateway = jobMaster.getSelfGateway(JobMasterGateway.class);
        try {
            // wait for the start to complete
            startFuture.get(JobMasterTest.testingTimeout.toMilliseconds(), TimeUnit.MILLISECONDS);
            CompletableFuture<Object> updateAggregateFuture;
            AggregateFunction<Integer, Integer, Integer> aggregateFunction = createAggregateFunction();
            ClosureCleaner.clean(aggregateFunction, true);
            byte[] serializedAggregateFunction = InstantiationUtil.serializeObject(aggregateFunction);
            updateAggregateFuture = jobMasterGateway.updateGlobalAggregate("agg1", 1, serializedAggregateFunction);
            MatcherAssert.assertThat(updateAggregateFuture.get(), Matchers.equalTo(1));
            updateAggregateFuture = jobMasterGateway.updateGlobalAggregate("agg1", 2, serializedAggregateFunction);
            MatcherAssert.assertThat(updateAggregateFuture.get(), Matchers.equalTo(3));
            updateAggregateFuture = jobMasterGateway.updateGlobalAggregate("agg1", 3, serializedAggregateFunction);
            MatcherAssert.assertThat(updateAggregateFuture.get(), Matchers.equalTo(6));
            updateAggregateFuture = jobMasterGateway.updateGlobalAggregate("agg1", 4, serializedAggregateFunction);
            MatcherAssert.assertThat(updateAggregateFuture.get(), Matchers.equalTo(10));
            updateAggregateFuture = jobMasterGateway.updateGlobalAggregate("agg2", 10, serializedAggregateFunction);
            MatcherAssert.assertThat(updateAggregateFuture.get(), Matchers.equalTo(10));
            updateAggregateFuture = jobMasterGateway.updateGlobalAggregate("agg2", 23, serializedAggregateFunction);
            MatcherAssert.assertThat(updateAggregateFuture.get(), Matchers.equalTo(33));
        } finally {
            RpcUtils.terminateRpcEndpoint(jobMaster, JobMasterTest.testingTimeout);
        }
    }

    /**
     * Tests that the job execution is failed if the TaskExecutor disconnects from the
     * JobMaster.
     */
    @Test
    public void testJobFailureWhenGracefulTaskExecutorTermination() throws Exception {
        runJobFailureWhenTaskExecutorTerminatesTest(JobMasterTest.heartbeatServices, ( localTaskManagerLocation, jobMasterGateway) -> jobMasterGateway.disconnectTaskManager(localTaskManagerLocation.getResourceID(), new FlinkException("Test disconnectTaskManager exception.")), ( jobMasterGateway, resourceID) -> ( ignored) -> {
        });
    }

    @Test
    public void testJobFailureWhenTaskExecutorHeartbeatTimeout() throws Exception {
        final AtomicBoolean respondToHeartbeats = new AtomicBoolean(true);
        runJobFailureWhenTaskExecutorTerminatesTest(JobMasterTest.fastHeartbeatServices, ( localTaskManagerLocation, jobMasterGateway) -> respondToHeartbeats.set(false), ( jobMasterGateway, taskManagerResourceId) -> ( resourceId) -> {
            if (respondToHeartbeats.get()) {
                jobMasterGateway.heartbeatFromTaskManager(taskManagerResourceId, new AccumulatorReport(Collections.emptyList()));
            }
        });
    }

    private static final class TestingOnCompletionActions implements OnCompletionActions {
        private final CompletableFuture<ArchivedExecutionGraph> jobReachedGloballyTerminalStateFuture = new CompletableFuture<>();

        private final CompletableFuture<Void> jobFinishedByOtherFuture = new CompletableFuture<>();

        private final CompletableFuture<Throwable> jobMasterFailedFuture = new CompletableFuture<>();

        @Override
        public void jobReachedGloballyTerminalState(ArchivedExecutionGraph executionGraph) {
            jobReachedGloballyTerminalStateFuture.complete(executionGraph);
        }

        @Override
        public void jobFinishedByOther() {
            jobFinishedByOtherFuture.complete(null);
        }

        @Override
        public void jobMasterFailed(Throwable cause) {
            jobMasterFailedFuture.complete(cause);
        }

        public CompletableFuture<ArchivedExecutionGraph> getJobReachedGloballyTerminalStateFuture() {
            return jobReachedGloballyTerminalStateFuture;
        }
    }

    private static final class AllocationIdsResourceManagerGateway extends TestingResourceManagerGateway {
        private final BlockingQueue<AllocationID> allocationIds;

        private AllocationIdsResourceManagerGateway() {
            this.allocationIds = new ArrayBlockingQueue(10);
            setRequestSlotConsumer(( slotRequest) -> allocationIds.offer(slotRequest.getAllocationId()));
        }

        AllocationID takeAllocationId() {
            try {
                return allocationIds.take();
            } catch (InterruptedException e) {
                ExceptionUtils.rethrow(e);
                return null;
            }
        }
    }

    private static final class DummyCheckpointStorageLocation implements CompletedCheckpointStorageLocation {
        private static final long serialVersionUID = 164095949572620688L;

        @Override
        public String getExternalPointer() {
            return null;
        }

        @Override
        public StreamStateHandle getMetadataHandle() {
            return null;
        }

        @Override
        public void disposeStorageLocation() throws IOException {
        }
    }
}

