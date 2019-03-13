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
package org.apache.flink.runtime.resourcemanager;


import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.heartbeat.HeartbeatServices;
import org.apache.flink.runtime.highavailability.TestingHighAvailabilityServices;
import org.apache.flink.runtime.instance.HardwareDescription;
import org.apache.flink.runtime.jobmaster.utils.TestingJobMasterGateway;
import org.apache.flink.runtime.jobmaster.utils.TestingJobMasterGatewayBuilder;
import org.apache.flink.runtime.leaderelection.TestingLeaderElectionService;
import org.apache.flink.runtime.leaderretrieval.LeaderRetrievalService;
import org.apache.flink.runtime.registration.RegistrationResponse;
import org.apache.flink.runtime.rest.messages.taskmanager.TaskManagerInfo;
import org.apache.flink.runtime.rpc.TestingRpcService;
import org.apache.flink.runtime.taskexecutor.TaskExecutorGateway;
import org.apache.flink.runtime.taskexecutor.TestingTaskExecutorGatewayBuilder;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.runtime.util.TestingFatalErrorHandler;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link ResourceManager}.
 */
public class ResourceManagerTest extends TestLogger {
    private static final Time TIMEOUT = Time.minutes(2L);

    private static final HeartbeatServices heartbeatServices = new HeartbeatServices(1000L, 10000L);

    private static final HeartbeatServices fastHeartbeatServices = new HeartbeatServices(1L, 1L);

    private static final HardwareDescription hardwareDescription = new HardwareDescription(42, 1337L, 1337L, 0L);

    private static final int dataPort = 1234;

    private static TestingRpcService rpcService;

    private TestingHighAvailabilityServices highAvailabilityServices;

    private TestingLeaderElectionService resourceManagerLeaderElectionService;

    private TestingFatalErrorHandler testingFatalErrorHandler;

    private ResourceID resourceManagerResourceId;

    private TestingResourceManager resourceManager;

    private ResourceManagerId resourceManagerId;

    /**
     * Tests that we can retrieve the correct {@link TaskManagerInfo} from the {@link ResourceManager}.
     */
    @Test
    public void testRequestTaskManagerInfo() throws Exception {
        final ResourceID taskManagerId = ResourceID.generate();
        final TaskExecutorGateway taskExecutorGateway = new TestingTaskExecutorGatewayBuilder().setAddress(UUID.randomUUID().toString()).createTestingTaskExecutorGateway();
        ResourceManagerTest.rpcService.registerGateway(taskExecutorGateway.getAddress(), taskExecutorGateway);
        resourceManager = createAndStartResourceManager(ResourceManagerTest.heartbeatServices);
        final ResourceManagerGateway resourceManagerGateway = getSelfGateway(ResourceManagerGateway.class);
        registerTaskExecutor(resourceManagerGateway, taskManagerId, taskExecutorGateway.getAddress());
        CompletableFuture<TaskManagerInfo> taskManagerInfoFuture = resourceManagerGateway.requestTaskManagerInfo(taskManagerId, TestingUtils.TIMEOUT());
        TaskManagerInfo taskManagerInfo = taskManagerInfoFuture.get();
        Assert.assertEquals(taskManagerId, taskManagerInfo.getResourceId());
        Assert.assertEquals(ResourceManagerTest.hardwareDescription, taskManagerInfo.getHardwareDescription());
        Assert.assertEquals(taskExecutorGateway.getAddress(), taskManagerInfo.getAddress());
        Assert.assertEquals(ResourceManagerTest.dataPort, taskManagerInfo.getDataPort());
        Assert.assertEquals(0, taskManagerInfo.getNumberSlots());
        Assert.assertEquals(0, taskManagerInfo.getNumberAvailableSlots());
    }

    @Test
    public void testHeartbeatTimeoutWithJobMaster() throws Exception {
        final CompletableFuture<ResourceID> heartbeatRequestFuture = new CompletableFuture<>();
        final CompletableFuture<ResourceManagerId> disconnectFuture = new CompletableFuture<>();
        final TestingJobMasterGateway jobMasterGateway = new TestingJobMasterGatewayBuilder().setResourceManagerHeartbeatConsumer(heartbeatRequestFuture::complete).setDisconnectResourceManagerConsumer(disconnectFuture::complete).build();
        ResourceManagerTest.rpcService.registerGateway(jobMasterGateway.getAddress(), jobMasterGateway);
        final JobID jobId = new JobID();
        final ResourceID jobMasterResourceId = ResourceID.generate();
        final LeaderRetrievalService jobMasterLeaderRetrievalService = new org.apache.flink.runtime.leaderretrieval.SettableLeaderRetrievalService(jobMasterGateway.getAddress(), jobMasterGateway.getFencingToken().toUUID());
        highAvailabilityServices.setJobMasterLeaderRetrieverFunction(( requestedJobId) -> {
            Assert.assertThat(requestedJobId, Matchers.is(Matchers.equalTo(jobId)));
            return jobMasterLeaderRetrievalService;
        });
        runHeartbeatTimeoutTest(( resourceManagerGateway) -> {
            final CompletableFuture<RegistrationResponse> registrationFuture = resourceManagerGateway.registerJobManager(jobMasterGateway.getFencingToken(), jobMasterResourceId, jobMasterGateway.getAddress(), jobId, TIMEOUT);
            assertThat(registrationFuture.get(), instanceOf(.class));
        }, ( resourceManagerResourceId) -> {
            assertThat(heartbeatRequestFuture.get(), is(equalTo(resourceManagerResourceId)));
            assertThat(disconnectFuture.get(), is(equalTo(resourceManagerId)));
        });
    }

    @Test
    public void testHeartbeatTimeoutWithTaskExecutor() throws Exception {
        final ResourceID taskExecutorId = ResourceID.generate();
        final CompletableFuture<ResourceID> heartbeatRequestFuture = new CompletableFuture<>();
        final CompletableFuture<Exception> disconnectFuture = new CompletableFuture<>();
        final TaskExecutorGateway taskExecutorGateway = new TestingTaskExecutorGatewayBuilder().setDisconnectResourceManagerConsumer(disconnectFuture::complete).setHeartbeatResourceManagerConsumer(heartbeatRequestFuture::complete).createTestingTaskExecutorGateway();
        ResourceManagerTest.rpcService.registerGateway(taskExecutorGateway.getAddress(), taskExecutorGateway);
        runHeartbeatTimeoutTest(( resourceManagerGateway) -> {
            registerTaskExecutor(resourceManagerGateway, taskExecutorId, taskExecutorGateway.getAddress());
        }, ( resourceManagerResourceId) -> {
            assertThat(heartbeatRequestFuture.get(), is(equalTo(resourceManagerResourceId)));
            assertThat(disconnectFuture.get(), instanceOf(.class));
        });
    }
}

