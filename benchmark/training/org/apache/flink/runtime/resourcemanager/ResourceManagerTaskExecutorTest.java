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


import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.instance.HardwareDescription;
import org.apache.flink.runtime.instance.InstanceID;
import org.apache.flink.runtime.registration.RegistrationResponse;
import org.apache.flink.runtime.rest.messages.taskmanager.TaskManagerInfo;
import org.apache.flink.runtime.rpc.TestingRpcService;
import org.apache.flink.runtime.rpc.exceptions.FencingTokenException;
import org.apache.flink.runtime.taskexecutor.SlotReport;
import org.apache.flink.runtime.taskexecutor.SlotStatus;
import org.apache.flink.runtime.taskexecutor.TaskExecutorRegistrationSuccess;
import org.apache.flink.runtime.taskexecutor.TestingTaskExecutorGateway;
import org.apache.flink.runtime.util.TestingFatalErrorHandler;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link ResourceManager} and {@link TaskExecutor} interaction.
 */
public class ResourceManagerTaskExecutorTest extends TestLogger {
    private static final Time TIMEOUT = Time.seconds(10L);

    private static TestingRpcService rpcService;

    private TestingTaskExecutorGateway taskExecutorGateway;

    private int dataPort = 1234;

    private HardwareDescription hardwareDescription = new HardwareDescription(1, 2L, 3L, 4L);

    private ResourceID taskExecutorResourceID;

    private ResourceID resourceManagerResourceID;

    private StandaloneResourceManager resourceManager;

    private ResourceManagerGateway rmGateway;

    private ResourceManagerGateway wronglyFencedGateway;

    private TestingFatalErrorHandler testingFatalErrorHandler;

    /**
     * Test receive normal registration from task executor and receive duplicate registration
     * from task executor.
     */
    @Test
    public void testRegisterTaskExecutor() throws Exception {
        // test response successful
        CompletableFuture<RegistrationResponse> successfulFuture = registerTaskExecutor(rmGateway, taskExecutorGateway.getAddress());
        RegistrationResponse response = successfulFuture.get(ResourceManagerTaskExecutorTest.TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS);
        Assert.assertTrue((response instanceof TaskExecutorRegistrationSuccess));
        final TaskManagerInfo taskManagerInfo = rmGateway.requestTaskManagerInfo(taskExecutorResourceID, ResourceManagerTaskExecutorTest.TIMEOUT).get();
        Assert.assertThat(taskManagerInfo.getResourceId(), Matchers.equalTo(taskExecutorResourceID));
        // test response successful with instanceID not equal to previous when receive duplicate registration from taskExecutor
        CompletableFuture<RegistrationResponse> duplicateFuture = registerTaskExecutor(rmGateway, taskExecutorGateway.getAddress());
        RegistrationResponse duplicateResponse = duplicateFuture.get();
        Assert.assertTrue((duplicateResponse instanceof TaskExecutorRegistrationSuccess));
        Assert.assertNotEquals(getRegistrationId(), getRegistrationId());
        Assert.assertThat(rmGateway.requestResourceOverview(ResourceManagerTaskExecutorTest.TIMEOUT).get().getNumberTaskManagers(), Matchers.is(1));
    }

    /**
     * Tests that a TaskExecutor can disconnect from the {@link ResourceManager}.
     */
    @Test
    public void testDisconnectTaskExecutor() throws Exception {
        final RegistrationResponse registrationResponse = registerTaskExecutor(rmGateway, taskExecutorGateway.getAddress()).get();
        Assert.assertThat(registrationResponse, Matchers.instanceOf(TaskExecutorRegistrationSuccess.class));
        final InstanceID registrationId = ((TaskExecutorRegistrationSuccess) (registrationResponse)).getRegistrationId();
        final int numberSlots = 10;
        final Collection<SlotStatus> slots = createSlots(numberSlots);
        final SlotReport slotReport = new SlotReport(slots);
        rmGateway.sendSlotReport(taskExecutorResourceID, registrationId, slotReport, ResourceManagerTaskExecutorTest.TIMEOUT).get();
        final ResourceOverview resourceOverview = rmGateway.requestResourceOverview(ResourceManagerTaskExecutorTest.TIMEOUT).get();
        Assert.assertThat(resourceOverview.getNumberTaskManagers(), Matchers.is(1));
        Assert.assertThat(resourceOverview.getNumberRegisteredSlots(), Matchers.is(numberSlots));
        rmGateway.disconnectTaskManager(taskExecutorResourceID, new FlinkException("testDisconnectTaskExecutor"));
        final ResourceOverview afterDisconnectResourceOverview = rmGateway.requestResourceOverview(ResourceManagerTaskExecutorTest.TIMEOUT).get();
        Assert.assertThat(afterDisconnectResourceOverview.getNumberTaskManagers(), Matchers.is(0));
        Assert.assertThat(afterDisconnectResourceOverview.getNumberRegisteredSlots(), Matchers.is(0));
    }

    /**
     * Test receive registration with unmatched leadershipId from task executor.
     */
    @Test
    public void testRegisterTaskExecutorWithUnmatchedLeaderSessionId() throws Exception {
        // test throw exception when receive a registration from taskExecutor which takes unmatched leaderSessionId
        CompletableFuture<RegistrationResponse> unMatchedLeaderFuture = registerTaskExecutor(wronglyFencedGateway, taskExecutorGateway.getAddress());
        try {
            unMatchedLeaderFuture.get(ResourceManagerTaskExecutorTest.TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS);
            Assert.fail("Should have failed because we are using a wrongly fenced ResourceManagerGateway.");
        } catch (ExecutionException e) {
            Assert.assertTrue(((ExceptionUtils.stripExecutionException(e)) instanceof FencingTokenException));
        }
    }

    /**
     * Test receive registration with invalid address from task executor.
     */
    @Test
    public void testRegisterTaskExecutorFromInvalidAddress() throws Exception {
        // test throw exception when receive a registration from taskExecutor which takes invalid address
        String invalidAddress = "/taskExecutor2";
        CompletableFuture<RegistrationResponse> invalidAddressFuture = registerTaskExecutor(rmGateway, invalidAddress);
        Assert.assertTrue(((invalidAddressFuture.get(ResourceManagerTaskExecutorTest.TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS)) instanceof RegistrationResponse.Decline));
    }
}

