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
package org.apache.hadoop.yarn.server.nodemanager.amrmproxy;


import FinalApplicationStatus.SUCCEEDED;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.hadoop.registry.client.api.RegistryOperations;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.NMToken;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.PreemptionMessage;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.UpdateContainerError;
import org.apache.hadoop.yarn.api.records.UpdatedContainer;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.MockResourceManagerFacade;
import org.apache.hadoop.yarn.server.federation.store.impl.MemoryFederationStateStore;
import org.apache.hadoop.yarn.server.federation.store.records.SubClusterId;
import org.apache.hadoop.yarn.server.nodemanager.Context;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService;
import org.apache.hadoop.yarn.util.Records;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Extends the TestAMRMProxyService and overrides methods in order to use the
 * AMRMProxyService's pipeline test cases for testing the FederationInterceptor
 * class. The tests for AMRMProxyService has been written cleverly so that it
 * can be reused to validate different request intercepter chains.
 */
public class TestFederationInterceptor extends BaseAMRMProxyTest {
    private static final Logger LOG = LoggerFactory.getLogger(TestFederationInterceptor.class);

    public static final String HOME_SC_ID = "SC-home";

    private TestableFederationInterceptor interceptor;

    private MemoryFederationStateStore stateStore;

    private NMStateStoreService nmStateStore;

    private RegistryOperations registry;

    private Context nmContext;

    private int testAppId;

    private ApplicationAttemptId attemptId;

    private volatile int lastResponseId;

    @Test
    public void testMultipleSubClusters() throws Exception {
        UserGroupInformation ugi = interceptor.getUGIWithToken(getAttemptId());
        ugi.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                // Register the application
                RegisterApplicationMasterRequest registerReq = Records.newRecord(RegisterApplicationMasterRequest.class);
                registerReq.setHost(Integer.toString(testAppId));
                registerReq.setRpcPort(0);
                registerReq.setTrackingUrl("");
                RegisterApplicationMasterResponse registerResponse = interceptor.registerApplicationMaster(registerReq);
                Assert.assertNotNull(registerResponse);
                lastResponseId = 0;
                Assert.assertEquals(0, getUnmanagedAMPoolSize());
                // Allocate the first batch of containers, with sc1 and sc2 active
                registerSubCluster(SubClusterId.newInstance("SC-1"));
                registerSubCluster(SubClusterId.newInstance("SC-2"));
                int numberOfContainers = 3;
                List<Container> containers = getContainersAndAssert(numberOfContainers, (numberOfContainers * 2));
                Assert.assertEquals(2, getUnmanagedAMPoolSize());
                // Allocate the second batch of containers, with sc1 and sc3 active
                deRegisterSubCluster(SubClusterId.newInstance("SC-2"));
                registerSubCluster(SubClusterId.newInstance("SC-3"));
                numberOfContainers = 1;
                containers.addAll(getContainersAndAssert(numberOfContainers, (numberOfContainers * 2)));
                Assert.assertEquals(3, getUnmanagedAMPoolSize());
                // Allocate the third batch of containers with only in home sub-cluster
                // active
                deRegisterSubCluster(SubClusterId.newInstance("SC-1"));
                deRegisterSubCluster(SubClusterId.newInstance("SC-3"));
                registerSubCluster(SubClusterId.newInstance(TestFederationInterceptor.HOME_SC_ID));
                numberOfContainers = 2;
                containers.addAll(getContainersAndAssert(numberOfContainers, (numberOfContainers * 1)));
                Assert.assertEquals(3, getUnmanagedAMPoolSize());
                // Release all containers
                releaseContainersAndAssert(containers);
                // Finish the application
                FinishApplicationMasterRequest finishReq = Records.newRecord(FinishApplicationMasterRequest.class);
                finishReq.setDiagnostics("");
                finishReq.setTrackingUrl("");
                finishReq.setFinalApplicationStatus(SUCCEEDED);
                FinishApplicationMasterResponse finshResponse = interceptor.finishApplicationMaster(finishReq);
                Assert.assertNotNull(finshResponse);
                Assert.assertEquals(true, finshResponse.getIsUnregistered());
                return null;
            }
        });
    }

    /* Test re-register when RM fails over. */
    @Test
    public void testReregister() throws Exception {
        UserGroupInformation ugi = interceptor.getUGIWithToken(getAttemptId());
        ugi.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                // Register the application
                RegisterApplicationMasterRequest registerReq = Records.newRecord(RegisterApplicationMasterRequest.class);
                registerReq.setHost(Integer.toString(testAppId));
                registerReq.setRpcPort(0);
                registerReq.setTrackingUrl("");
                RegisterApplicationMasterResponse registerResponse = interceptor.registerApplicationMaster(registerReq);
                Assert.assertNotNull(registerResponse);
                lastResponseId = 0;
                Assert.assertEquals(0, getUnmanagedAMPoolSize());
                // Allocate the first batch of containers
                registerSubCluster(SubClusterId.newInstance("SC-1"));
                registerSubCluster(SubClusterId.newInstance(TestFederationInterceptor.HOME_SC_ID));
                interceptor.setShouldReRegisterNext();
                int numberOfContainers = 3;
                List<Container> containers = getContainersAndAssert(numberOfContainers, (numberOfContainers * 2));
                Assert.assertEquals(1, getUnmanagedAMPoolSize());
                interceptor.setShouldReRegisterNext();
                // Release all containers
                releaseContainersAndAssert(containers);
                interceptor.setShouldReRegisterNext();
                // Finish the application
                FinishApplicationMasterRequest finishReq = Records.newRecord(FinishApplicationMasterRequest.class);
                finishReq.setDiagnostics("");
                finishReq.setTrackingUrl("");
                finishReq.setFinalApplicationStatus(SUCCEEDED);
                FinishApplicationMasterResponse finshResponse = interceptor.finishApplicationMaster(finishReq);
                Assert.assertNotNull(finshResponse);
                Assert.assertEquals(true, finshResponse.getIsUnregistered());
                return null;
            }
        });
    }

    /* Test concurrent register threads. This is possible because the timeout
    between AM and AMRMProxy is shorter than the timeout + failOver between
    FederationInterceptor (AMRMProxy) and RM. When first call is blocked due to
    RM failover and AM timeout, it will call us resulting in a second register
    thread.
     */
    @Test(timeout = 5000)
    public void testConcurrentRegister() throws InterruptedException, ExecutionException {
        ExecutorService threadpool = Executors.newCachedThreadPool();
        ExecutorCompletionService<RegisterApplicationMasterResponse> compSvc = new ExecutorCompletionService<>(threadpool);
        Object syncObj = MockResourceManagerFacade.getRegisterSyncObj();
        // Two register threads
        synchronized(syncObj) {
            // Make sure first thread will block within RM, before the second thread
            // starts
            TestFederationInterceptor.LOG.info("Starting first register thread");
            compSvc.submit(new TestFederationInterceptor.ConcurrentRegisterAMCallable());
            try {
                TestFederationInterceptor.LOG.info("Test main starts waiting for the first thread to block");
                syncObj.wait();
                TestFederationInterceptor.LOG.info("Test main wait finished");
            } catch (Exception e) {
                TestFederationInterceptor.LOG.info("Test main wait interrupted", e);
            }
        }
        // The second thread will get already registered exception from RM.
        TestFederationInterceptor.LOG.info("Starting second register thread");
        compSvc.submit(new TestFederationInterceptor.ConcurrentRegisterAMCallable());
        // Notify the first register thread to return
        TestFederationInterceptor.LOG.info("Let first blocked register thread move on");
        synchronized(syncObj) {
            syncObj.notifyAll();
        }
        // Both thread should return without exception
        RegisterApplicationMasterResponse response = compSvc.take().get();
        Assert.assertNotNull(response);
        response = compSvc.take().get();
        Assert.assertNotNull(response);
        threadpool.shutdown();
    }

    /**
     * A callable that calls registerAM to RM with blocking.
     */
    public class ConcurrentRegisterAMCallable implements Callable<RegisterApplicationMasterResponse> {
        @Override
        public RegisterApplicationMasterResponse call() throws Exception {
            RegisterApplicationMasterResponse response = null;
            try {
                // Use port number 1001 to let mock RM block in the register call
                response = interceptor.registerApplicationMaster(RegisterApplicationMasterRequest.newInstance(null, 1001, null));
                lastResponseId = 0;
            } catch (Exception e) {
                TestFederationInterceptor.LOG.info("Register thread exception", e);
                response = null;
            }
            return response;
        }
    }

    @Test
    public void testRecoverWithAMRMProxyHA() throws Exception {
        testRecover(registry);
    }

    @Test
    public void testRecoverWithoutAMRMProxyHA() throws Exception {
        testRecover(null);
    }

    @Test
    public void testRequestInterceptorChainCreation() throws Exception {
        RequestInterceptor root = createRequestInterceptorChain();
        int index = 0;
        while (root != null) {
            switch (index) {
                case 0 :
                case 1 :
                    Assert.assertEquals(PassThroughRequestInterceptor.class.getName(), root.getClass().getName());
                    break;
                case 2 :
                    Assert.assertEquals(TestableFederationInterceptor.class.getName(), root.getClass().getName());
                    break;
                default :
                    Assert.fail();
            }
            root = root.getNextInterceptor();
            index++;
        } 
        Assert.assertEquals("The number of interceptors in chain does not match", Integer.toString(3), Integer.toString(index));
    }

    /**
     * Between AM and AMRMProxy, FederationInterceptor modifies the RM behavior,
     * so that when AM registers more than once, it returns the same register
     * success response instead of throwing
     * {@link InvalidApplicationMasterRequestException}
     *
     * We did this because FederationInterceptor can receive concurrent register
     * requests from AM because of timeout between AM and AMRMProxy. This can
     * possible since the timeout between FederationInterceptor and RM longer
     * because of performFailover + timeout.
     */
    @Test
    public void testTwoIdenticalRegisterRequest() throws Exception {
        // Register the application twice
        RegisterApplicationMasterRequest registerReq = Records.newRecord(RegisterApplicationMasterRequest.class);
        registerReq.setHost(Integer.toString(testAppId));
        registerReq.setRpcPort(0);
        registerReq.setTrackingUrl("");
        for (int i = 0; i < 2; i++) {
            RegisterApplicationMasterResponse registerResponse = interceptor.registerApplicationMaster(registerReq);
            Assert.assertNotNull(registerResponse);
            lastResponseId = 0;
        }
    }

    @Test
    public void testTwoDifferentRegisterRequest() throws Exception {
        // Register the application first time
        RegisterApplicationMasterRequest registerReq = Records.newRecord(RegisterApplicationMasterRequest.class);
        registerReq.setHost(Integer.toString(testAppId));
        registerReq.setRpcPort(0);
        registerReq.setTrackingUrl("");
        RegisterApplicationMasterResponse registerResponse = interceptor.registerApplicationMaster(registerReq);
        Assert.assertNotNull(registerResponse);
        lastResponseId = 0;
        // Register the application second time with a different request obj
        registerReq = Records.newRecord(RegisterApplicationMasterRequest.class);
        registerReq.setHost(Integer.toString(testAppId));
        registerReq.setRpcPort(0);
        registerReq.setTrackingUrl("different");
        try {
            registerResponse = interceptor.registerApplicationMaster(registerReq);
            lastResponseId = 0;
            Assert.fail("Should throw if a different request obj is used");
        } catch (YarnException e) {
        }
    }

    @Test
    public void testAllocateResponse() throws Exception {
        interceptor.registerApplicationMaster(RegisterApplicationMasterRequest.newInstance(null, 0, null));
        AllocateRequest allocateRequest = Records.newRecord(AllocateRequest.class);
        Map<SubClusterId, List<AllocateResponse>> asyncResponseSink = getAsyncResponseSink();
        ContainerId cid = ContainerId.newContainerId(attemptId, 0);
        ContainerStatus cStatus = Records.newRecord(ContainerStatus.class);
        cStatus.setContainerId(cid);
        Container container = Container.newInstance(cid, null, null, null, null, null);
        AllocateResponse response = Records.newRecord(AllocateResponse.class);
        response.setAllocatedContainers(Collections.singletonList(container));
        response.setCompletedContainersStatuses(Collections.singletonList(cStatus));
        response.setUpdatedNodes(Collections.singletonList(Records.newRecord(NodeReport.class)));
        response.setNMTokens(Collections.singletonList(Records.newRecord(NMToken.class)));
        response.setUpdatedContainers(Collections.singletonList(Records.newRecord(UpdatedContainer.class)));
        response.setUpdateErrors(Collections.singletonList(Records.newRecord(UpdateContainerError.class)));
        response.setAvailableResources(Records.newRecord(Resource.class));
        response.setPreemptionMessage(Records.newRecord(PreemptionMessage.class));
        List<AllocateResponse> list = new ArrayList<>();
        list.add(response);
        asyncResponseSink.put(SubClusterId.newInstance("SC-1"), list);
        response = interceptor.allocate(allocateRequest);
        Assert.assertEquals(1, response.getAllocatedContainers().size());
        Assert.assertNotNull(response.getAvailableResources());
        Assert.assertEquals(1, response.getCompletedContainersStatuses().size());
        Assert.assertEquals(1, response.getUpdatedNodes().size());
        Assert.assertNotNull(response.getPreemptionMessage());
        Assert.assertEquals(1, response.getNMTokens().size());
        Assert.assertEquals(1, response.getUpdatedContainers().size());
        Assert.assertEquals(1, response.getUpdateErrors().size());
    }

    @Test
    public void testSubClusterTimeOut() throws Exception {
        UserGroupInformation ugi = interceptor.getUGIWithToken(getAttemptId());
        ugi.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                // Register the application first time
                RegisterApplicationMasterRequest registerReq = Records.newRecord(RegisterApplicationMasterRequest.class);
                registerReq.setHost(Integer.toString(testAppId));
                registerReq.setRpcPort(0);
                registerReq.setTrackingUrl("");
                RegisterApplicationMasterResponse registerResponse = interceptor.registerApplicationMaster(registerReq);
                Assert.assertNotNull(registerResponse);
                lastResponseId = 0;
                registerSubCluster(SubClusterId.newInstance("SC-1"));
                getContainersAndAssert(1, 1);
                AllocateResponse allocateResponse = generateBaseAllocationResponse();
                Assert.assertEquals(2, allocateResponse.getNumClusterNodes());
                Assert.assertEquals(0, getTimedOutSCs(true).size());
                // Let all SC timeout (home and SC-1), without an allocate from AM
                Thread.sleep(800);
                // Should not be considered timeout, because there's no recent AM
                // heartbeat
                allocateResponse = interceptor.generateBaseAllocationResponse();
                Assert.assertEquals(2, allocateResponse.getNumClusterNodes());
                Assert.assertEquals(0, getTimedOutSCs(true).size());
                // Generate a duplicate heartbeat from AM, so that it won't really
                // trigger an heartbeat to all SC
                AllocateRequest allocateRequest = Records.newRecord(AllocateRequest.class);
                // Set to lastResponseId - 1 so that it will be considered a duplicate
                // heartbeat and thus not forwarded to all SCs
                allocateRequest.setResponseId(((lastResponseId) - 1));
                interceptor.allocate(allocateRequest);
                // Should be considered timeout
                allocateResponse = interceptor.generateBaseAllocationResponse();
                Assert.assertEquals(0, allocateResponse.getNumClusterNodes());
                Assert.assertEquals(2, getTimedOutSCs(true).size());
                return null;
            }
        });
    }

    @Test
    public void testSecondAttempt() throws Exception {
        final RegisterApplicationMasterRequest registerReq = Records.newRecord(RegisterApplicationMasterRequest.class);
        registerReq.setHost(Integer.toString(testAppId));
        registerReq.setRpcPort(testAppId);
        registerReq.setTrackingUrl("");
        UserGroupInformation ugi = interceptor.getUGIWithToken(getAttemptId());
        ugi.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                // Register the application
                RegisterApplicationMasterResponse registerResponse = interceptor.registerApplicationMaster(registerReq);
                Assert.assertNotNull(registerResponse);
                lastResponseId = 0;
                Assert.assertEquals(0, getUnmanagedAMPoolSize());
                // Allocate one batch of containers
                registerSubCluster(SubClusterId.newInstance("SC-1"));
                registerSubCluster(SubClusterId.newInstance(TestFederationInterceptor.HOME_SC_ID));
                int numberOfContainers = 3;
                List<Container> containers = getContainersAndAssert(numberOfContainers, (numberOfContainers * 2));
                for (Container c : containers) {
                    TestFederationInterceptor.LOG.info(("Allocated container " + (c.getId())));
                }
                Assert.assertEquals(1, getUnmanagedAMPoolSize());
                // Make sure all async hb threads are done
                interceptor.drainAllAsyncQueue(true);
                // Preserve the mock RM instances for secondaries
                ConcurrentHashMap<String, MockResourceManagerFacade> secondaries = interceptor.getSecondaryRMs();
                // Increase the attemptId and create a new intercepter instance for it
                attemptId = ApplicationAttemptId.newInstance(attemptId.getApplicationId(), ((attemptId.getAttemptId()) + 1));
                interceptor = new TestableFederationInterceptor(null, secondaries);
                init(new AMRMProxyApplicationContextImpl(nmContext, getConf(), attemptId, "test-user", null, null, null, registry));
                return null;
            }
        });
        // Update the ugi with new attemptId
        ugi = interceptor.getUGIWithToken(getAttemptId());
        ugi.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                RegisterApplicationMasterResponse registerResponse = interceptor.registerApplicationMaster(registerReq);
                lastResponseId = 0;
                int numberOfContainers = 3;
                // Should re-attach secondaries and get the three running containers
                Assert.assertEquals(1, getUnmanagedAMPoolSize());
                // SC1 should be initialized to be timed out
                Assert.assertEquals(1, getTimedOutSCs(true).size());
                Assert.assertEquals(numberOfContainers, registerResponse.getContainersFromPreviousAttempts().size());
                // Release all containers
                releaseContainersAndAssert(registerResponse.getContainersFromPreviousAttempts());
                // Finish the application
                FinishApplicationMasterRequest finishReq = Records.newRecord(FinishApplicationMasterRequest.class);
                finishReq.setDiagnostics("");
                finishReq.setTrackingUrl("");
                finishReq.setFinalApplicationStatus(SUCCEEDED);
                FinishApplicationMasterResponse finshResponse = interceptor.finishApplicationMaster(finishReq);
                Assert.assertNotNull(finshResponse);
                Assert.assertEquals(true, finshResponse.getIsUnregistered());
                // After the application succeeds, the registry entry should be deleted
                if ((getRegistryClient()) != null) {
                    Assert.assertEquals(0, getRegistryClient().getAllApplications().size());
                }
                return null;
            }
        });
    }

    @Test
    public void testMergeAllocateResponse() {
        ContainerId cid = ContainerId.newContainerId(attemptId, 0);
        ContainerStatus cStatus = Records.newRecord(ContainerStatus.class);
        cStatus.setContainerId(cid);
        Container container = Container.newInstance(cid, null, null, null, null, null);
        AllocateResponse homeResponse = Records.newRecord(AllocateResponse.class);
        homeResponse.setAllocatedContainers(Collections.singletonList(container));
        homeResponse.setCompletedContainersStatuses(Collections.singletonList(cStatus));
        homeResponse.setUpdatedNodes(Collections.singletonList(Records.newRecord(NodeReport.class)));
        homeResponse.setNMTokens(Collections.singletonList(Records.newRecord(NMToken.class)));
        homeResponse.setUpdatedContainers(Collections.singletonList(Records.newRecord(UpdatedContainer.class)));
        homeResponse.setUpdateErrors(Collections.singletonList(Records.newRecord(UpdateContainerError.class)));
        homeResponse.setAvailableResources(Records.newRecord(Resource.class));
        homeResponse.setPreemptionMessage(createDummyPreemptionMessage(ContainerId.newContainerId(attemptId, 0)));
        AllocateResponse response = Records.newRecord(AllocateResponse.class);
        response.setAllocatedContainers(Collections.singletonList(container));
        response.setCompletedContainersStatuses(Collections.singletonList(cStatus));
        response.setUpdatedNodes(Collections.singletonList(Records.newRecord(NodeReport.class)));
        response.setNMTokens(Collections.singletonList(Records.newRecord(NMToken.class)));
        response.setUpdatedContainers(Collections.singletonList(Records.newRecord(UpdatedContainer.class)));
        response.setUpdateErrors(Collections.singletonList(Records.newRecord(UpdateContainerError.class)));
        response.setAvailableResources(Records.newRecord(Resource.class));
        response.setPreemptionMessage(createDummyPreemptionMessage(ContainerId.newContainerId(attemptId, 1)));
        interceptor.mergeAllocateResponse(homeResponse, response, SubClusterId.newInstance("SC-1"));
        Assert.assertEquals(2, homeResponse.getPreemptionMessage().getContract().getContainers().size());
        Assert.assertEquals(2, homeResponse.getAllocatedContainers().size());
        Assert.assertEquals(2, homeResponse.getUpdatedNodes().size());
        Assert.assertEquals(2, homeResponse.getCompletedContainersStatuses().size());
    }
}

