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


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.clusterframework.types.ResourceID;
import org.apache.flink.runtime.highavailability.HighAvailabilityServices;
import org.apache.flink.runtime.highavailability.TestingHighAvailabilityServices;
import org.apache.flink.runtime.jobmaster.JobMasterId;
import org.apache.flink.runtime.jobmaster.JobMasterRegistrationSuccess;
import org.apache.flink.runtime.jobmaster.utils.TestingJobMasterGateway;
import org.apache.flink.runtime.leaderelection.TestingLeaderElectionService;
import org.apache.flink.runtime.leaderretrieval.SettableLeaderRetrievalService;
import org.apache.flink.runtime.registration.RegistrationResponse;
import org.apache.flink.runtime.resourcemanager.exceptions.ResourceManagerException;
import org.apache.flink.runtime.rpc.TestingRpcService;
import org.apache.flink.runtime.rpc.exceptions.FencingTokenException;
import org.apache.flink.runtime.util.TestingFatalErrorHandler;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the interaction between the {@link ResourceManager} and the
 * {@link JobMaster}.
 */
public class ResourceManagerJobMasterTest extends TestLogger {
    private static final Time TIMEOUT = Time.seconds(10L);

    private TestingRpcService rpcService;

    private JobID jobId;

    private TestingJobMasterGateway jobMasterGateway;

    private ResourceID jobMasterResourceId;

    private SettableLeaderRetrievalService jobMasterLeaderRetrievalService;

    private TestingLeaderElectionService resourceManagerLeaderElectionService;

    private TestingHighAvailabilityServices haServices;

    private TestingFatalErrorHandler testingFatalErrorHandler;

    private ResourceManager<?> resourceManager;

    private ResourceManagerGateway resourceManagerGateway;

    /**
     * Test receive normal registration from job master and receive duplicate
     * registration from job master.
     */
    @Test
    public void testRegisterJobMaster() throws Exception {
        // test response successful
        CompletableFuture<RegistrationResponse> successfulFuture = resourceManagerGateway.registerJobManager(jobMasterGateway.getFencingToken(), jobMasterResourceId, jobMasterGateway.getAddress(), jobId, ResourceManagerJobMasterTest.TIMEOUT);
        RegistrationResponse response = successfulFuture.get(ResourceManagerJobMasterTest.TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS);
        Assert.assertTrue((response instanceof JobMasterRegistrationSuccess));
    }

    /**
     * Test receive registration with unmatched leadershipId from job master.
     */
    @Test
    public void testRegisterJobMasterWithUnmatchedLeaderSessionId1() throws Exception {
        final ResourceManagerGateway wronglyFencedGateway = rpcService.connect(resourceManager.getAddress(), ResourceManagerId.generate(), ResourceManagerGateway.class).get(ResourceManagerJobMasterTest.TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS);
        // test throw exception when receive a registration from job master which takes unmatched leaderSessionId
        CompletableFuture<RegistrationResponse> unMatchedLeaderFuture = wronglyFencedGateway.registerJobManager(jobMasterGateway.getFencingToken(), jobMasterResourceId, jobMasterGateway.getAddress(), jobId, ResourceManagerJobMasterTest.TIMEOUT);
        try {
            unMatchedLeaderFuture.get(5L, TimeUnit.SECONDS);
            Assert.fail("Should fail because we are using the wrong fencing token.");
        } catch (ExecutionException e) {
            Assert.assertTrue(((ExceptionUtils.stripExecutionException(e)) instanceof FencingTokenException));
        }
    }

    /**
     * Test receive registration with unmatched leadershipId from job master.
     */
    @Test
    public void testRegisterJobMasterWithUnmatchedLeaderSessionId2() throws Exception {
        // test throw exception when receive a registration from job master which takes unmatched leaderSessionId
        JobMasterId differentJobMasterId = JobMasterId.generate();
        CompletableFuture<RegistrationResponse> unMatchedLeaderFuture = resourceManagerGateway.registerJobManager(differentJobMasterId, jobMasterResourceId, jobMasterGateway.getAddress(), jobId, ResourceManagerJobMasterTest.TIMEOUT);
        Assert.assertTrue(((unMatchedLeaderFuture.get()) instanceof RegistrationResponse.Decline));
    }

    /**
     * Test receive registration with invalid address from job master.
     */
    @Test
    public void testRegisterJobMasterFromInvalidAddress() throws Exception {
        // test throw exception when receive a registration from job master which takes invalid address
        String invalidAddress = "/jobMasterAddress2";
        CompletableFuture<RegistrationResponse> invalidAddressFuture = resourceManagerGateway.registerJobManager(new JobMasterId(HighAvailabilityServices.DEFAULT_LEADER_ID), jobMasterResourceId, invalidAddress, jobId, ResourceManagerJobMasterTest.TIMEOUT);
        Assert.assertTrue(((invalidAddressFuture.get(5, TimeUnit.SECONDS)) instanceof RegistrationResponse.Decline));
    }

    /**
     * Check and verify return RegistrationResponse. Decline when failed to start a
     * job master Leader retrieval listener.
     */
    @Test
    public void testRegisterJobMasterWithFailureLeaderListener() throws Exception {
        JobID unknownJobIDToHAServices = new JobID();
        // this should fail because we try to register a job leader listener for an unknown job id
        CompletableFuture<RegistrationResponse> registrationFuture = resourceManagerGateway.registerJobManager(jobMasterGateway.getFencingToken(), jobMasterResourceId, jobMasterGateway.getAddress(), unknownJobIDToHAServices, ResourceManagerJobMasterTest.TIMEOUT);
        try {
            registrationFuture.get(ResourceManagerJobMasterTest.TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS);
            Assert.fail("Expected to fail with a ResourceManagerException.");
        } catch (ExecutionException e) {
            Assert.assertTrue(((ExceptionUtils.stripExecutionException(e)) instanceof ResourceManagerException));
        }
        // ignore the reported error
        testingFatalErrorHandler.clearError();
    }
}

