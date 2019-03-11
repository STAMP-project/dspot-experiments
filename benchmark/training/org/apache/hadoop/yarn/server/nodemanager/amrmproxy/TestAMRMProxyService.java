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


import FinalApplicationStatus.FAILED;
import FinalApplicationStatus.SUCCEEDED;
import YarnConfiguration.AMRM_PROXY_INTERCEPTOR_CLASS_PIPELINE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateRequest;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterResponse;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.MockResourceManagerFacade;
import org.apache.hadoop.yarn.server.nodemanager.amrmproxy.AMRMProxyService.RequestInterceptorChainWrapper;
import org.apache.hadoop.yarn.server.nodemanager.recovery.NMStateStoreService.RecoveredAMRMProxyState;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestAMRMProxyService extends BaseAMRMProxyTest {
    private static final Logger LOG = LoggerFactory.getLogger(TestAMRMProxyService.class);

    private static MockResourceManagerFacade mockRM;

    /**
     * Test if the pipeline is created properly.
     */
    @Test
    public void testRequestInterceptorChainCreation() throws Exception {
        RequestInterceptor root = createRequestInterceptorChain();
        int index = 0;
        while (root != null) {
            switch (index) {
                case 0 :
                case 1 :
                case 2 :
                    Assert.assertEquals(PassThroughRequestInterceptor.class.getName(), root.getClass().getName());
                    break;
                case 3 :
                    Assert.assertEquals(MockRequestInterceptor.class.getName(), root.getClass().getName());
                    break;
            }
            root = root.getNextInterceptor();
            index++;
        } 
        Assert.assertEquals("The number of interceptors in chain does not match", Integer.toString(4), Integer.toString(index));
    }

    /**
     * Tests registration of a single application master.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRegisterOneApplicationMaster() throws Exception {
        // The testAppId identifier is used as host name and the mock resource
        // manager return it as the queue name. Assert that we received the queue
        // name
        int testAppId = 1;
        RegisterApplicationMasterResponse response1 = registerApplicationMaster(testAppId);
        Assert.assertNotNull(response1);
        Assert.assertEquals(Integer.toString(testAppId), response1.getQueue());
    }

    /**
     * Tests the case when interceptor pipeline initialization fails.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testInterceptorInitFailure() throws IOException {
        Configuration conf = this.getConf();
        // Override with a bad interceptor configuration
        conf.set(AMRM_PROXY_INTERCEPTOR_CLASS_PIPELINE, "class.that.does.not.exist");
        // Reinitialize instance with the new config
        createAndStartAMRMProxyService(conf);
        int testAppId = 1;
        try {
            registerApplicationMaster(testAppId);
            Assert.fail("Should not reach here. Expecting an exception thrown");
        } catch (Exception e) {
            Map<ApplicationId, RequestInterceptorChainWrapper> pipelines = getPipelines();
            ApplicationId id = getApplicationId(testAppId);
            Assert.assertTrue("The interceptor pipeline should be removed if initializtion fails", ((pipelines.get(id)) == null));
        }
    }

    /**
     * Tests the registration of multiple application master serially one at a
     * time.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRegisterMulitpleApplicationMasters() throws Exception {
        for (int testAppId = 0; testAppId < 3; testAppId++) {
            RegisterApplicationMasterResponse response = registerApplicationMaster(testAppId);
            Assert.assertNotNull(response);
            Assert.assertEquals(Integer.toString(testAppId), response.getQueue());
        }
    }

    /**
     * Tests the registration of multiple application masters using multiple
     * threads in parallel.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRegisterMulitpleApplicationMastersInParallel() throws Exception {
        int numberOfRequests = 5;
        ArrayList<String> testContexts = CreateTestRequestIdentifiers(numberOfRequests);
        super.registerApplicationMastersInParallel(testContexts);
    }

    @Test
    public void testFinishOneApplicationMasterWithSuccess() throws Exception {
        int testAppId = 1;
        RegisterApplicationMasterResponse registerResponse = registerApplicationMaster(testAppId);
        Assert.assertNotNull(registerResponse);
        Assert.assertEquals(Integer.toString(testAppId), registerResponse.getQueue());
        FinishApplicationMasterResponse finshResponse = finishApplicationMaster(testAppId, SUCCEEDED);
        Assert.assertNotNull(finshResponse);
        Assert.assertEquals(true, finshResponse.getIsUnregistered());
    }

    @Test
    public void testFinishOneApplicationMasterWithFailure() throws Exception {
        int testAppId = 1;
        RegisterApplicationMasterResponse registerResponse = registerApplicationMaster(testAppId);
        Assert.assertNotNull(registerResponse);
        Assert.assertEquals(Integer.toString(testAppId), registerResponse.getQueue());
        FinishApplicationMasterResponse finshResponse = finishApplicationMaster(testAppId, FAILED);
        Assert.assertNotNull(finshResponse);
        try {
            // Try to finish an application master that is already finished.
            finishApplicationMaster(testAppId, SUCCEEDED);
            Assert.fail("The request to finish application master should have failed");
        } catch (Throwable ex) {
            // This is expected. So nothing required here.
            TestAMRMProxyService.LOG.info("Finish registration failed as expected because it was not registered");
        }
    }

    @Test
    public void testFinishInvalidApplicationMaster() throws Exception {
        try {
            // Try to finish an application master that was not registered.
            finishApplicationMaster(4, SUCCEEDED);
            Assert.fail("The request to finish application master should have failed");
        } catch (Throwable ex) {
            // This is expected. So nothing required here.
            TestAMRMProxyService.LOG.info("Finish registration failed as expected because it was not registered");
        }
    }

    @Test
    public void testFinishMulitpleApplicationMasters() throws Exception {
        int numberOfRequests = 3;
        for (int index = 0; index < numberOfRequests; index++) {
            RegisterApplicationMasterResponse registerResponse = registerApplicationMaster(index);
            Assert.assertNotNull(registerResponse);
            Assert.assertEquals(Integer.toString(index), registerResponse.getQueue());
        }
        // Finish in reverse sequence
        for (int index = numberOfRequests - 1; index >= 0; index--) {
            FinishApplicationMasterResponse finshResponse = finishApplicationMaster(index, SUCCEEDED);
            Assert.assertNotNull(finshResponse);
            Assert.assertEquals(true, finshResponse.getIsUnregistered());
            // Assert that the application has been removed from the collection
            Assert.assertTrue(((this.getAMRMProxyService().getPipelines().size()) == index));
        }
        try {
            // Try to finish an application master that is already finished.
            finishApplicationMaster(1, SUCCEEDED);
            Assert.fail("The request to finish application master should have failed");
        } catch (Throwable ex) {
            // This is expected. So nothing required here.
            TestAMRMProxyService.LOG.info("Finish registration failed as expected because it was not registered");
        }
        try {
            // Try to finish an application master that was not registered.
            finishApplicationMaster(4, SUCCEEDED);
            Assert.fail("The request to finish application master should have failed");
        } catch (Throwable ex) {
            // This is expected. So nothing required here.
            TestAMRMProxyService.LOG.info("Finish registration failed as expected because it was not registered");
        }
    }

    @Test
    public void testFinishMulitpleApplicationMastersInParallel() throws Exception {
        int numberOfRequests = 5;
        ArrayList<String> testContexts = new ArrayList<String>();
        TestAMRMProxyService.LOG.info((("Creating " + numberOfRequests) + " contexts for testing"));
        for (int i = 0; i < numberOfRequests; i++) {
            testContexts.add(("test-endpoint-" + (Integer.toString(i))));
            TestAMRMProxyService.LOG.info(("Created test context: " + (testContexts.get(i))));
            RegisterApplicationMasterResponse registerResponse = registerApplicationMaster(i);
            Assert.assertNotNull(registerResponse);
            Assert.assertEquals(Integer.toString(i), registerResponse.getQueue());
        }
        finishApplicationMastersInParallel(testContexts);
    }

    @Test
    public void testAllocateRequestWithNullValues() throws Exception {
        int testAppId = 1;
        RegisterApplicationMasterResponse registerResponse = registerApplicationMaster(testAppId);
        Assert.assertNotNull(registerResponse);
        Assert.assertEquals(Integer.toString(testAppId), registerResponse.getQueue());
        AllocateResponse allocateResponse = allocate(testAppId);
        Assert.assertNotNull(allocateResponse);
        FinishApplicationMasterResponse finshResponse = finishApplicationMaster(testAppId, SUCCEEDED);
        Assert.assertNotNull(finshResponse);
        Assert.assertEquals(true, finshResponse.getIsUnregistered());
    }

    @Test
    public void testAllocateRequestWithoutRegistering() throws Exception {
        try {
            // Try to allocate an application master without registering.
            allocate(1);
            Assert.fail("The request to allocate application master should have failed");
        } catch (Throwable ex) {
            // This is expected. So nothing required here.
            TestAMRMProxyService.LOG.info("AllocateRequest failed as expected because AM was not registered");
        }
    }

    @Test
    public void testAllocateWithOneResourceRequest() throws Exception {
        int testAppId = 1;
        RegisterApplicationMasterResponse registerResponse = registerApplicationMaster(testAppId);
        Assert.assertNotNull(registerResponse);
        getContainersAndAssert(testAppId, 1);
        finishApplicationMaster(testAppId, SUCCEEDED);
    }

    @Test
    public void testAllocateWithMultipleResourceRequest() throws Exception {
        int testAppId = 1;
        RegisterApplicationMasterResponse registerResponse = registerApplicationMaster(testAppId);
        Assert.assertNotNull(registerResponse);
        getContainersAndAssert(testAppId, 10);
        finishApplicationMaster(testAppId, SUCCEEDED);
    }

    @Test
    public void testAllocateAndReleaseContainers() throws Exception {
        int testAppId = 1;
        RegisterApplicationMasterResponse registerResponse = registerApplicationMaster(testAppId);
        Assert.assertNotNull(registerResponse);
        List<Container> containers = getContainersAndAssert(testAppId, 10);
        releaseContainersAndAssert(testAppId, containers);
        finishApplicationMaster(testAppId, SUCCEEDED);
    }

    @Test
    public void testAllocateAndReleaseContainersForMultipleAM() throws Exception {
        int numberOfApps = 5;
        for (int testAppId = 0; testAppId < numberOfApps; testAppId++) {
            RegisterApplicationMasterResponse registerResponse = registerApplicationMaster(testAppId);
            Assert.assertNotNull(registerResponse);
            List<Container> containers = getContainersAndAssert(testAppId, 10);
            releaseContainersAndAssert(testAppId, containers);
        }
        for (int testAppId = 0; testAppId < numberOfApps; testAppId++) {
            finishApplicationMaster(testAppId, SUCCEEDED);
        }
    }

    @Test
    public void testAllocateAndReleaseContainersForMultipleAMInParallel() throws Exception {
        int numberOfApps = 6;
        ArrayList<Integer> tempAppIds = new ArrayList<Integer>();
        for (int i = 0; i < numberOfApps; i++) {
            tempAppIds.add(new Integer(i));
        }
        final ArrayList<Integer> appIds = tempAppIds;
        List<Integer> responses = runInParallel(appIds, new BaseAMRMProxyTest.Function<Integer, Integer>() {
            @Override
            public Integer invoke(Integer testAppId) {
                try {
                    RegisterApplicationMasterResponse registerResponse = registerApplicationMaster(testAppId);
                    Assert.assertNotNull("response is null", registerResponse);
                    List<Container> containers = getContainersAndAssert(testAppId, 10);
                    releaseContainersAndAssert(testAppId, containers);
                    TestAMRMProxyService.LOG.info(("Sucessfully registered application master with appId: " + testAppId));
                } catch (Throwable ex) {
                    TestAMRMProxyService.LOG.error(("Failed to register application master with appId: " + testAppId), ex);
                    testAppId = null;
                }
                return testAppId;
            }
        });
        Assert.assertEquals("Number of responses received does not match with request", appIds.size(), responses.size());
        for (Integer testAppId : responses) {
            Assert.assertNotNull(testAppId);
            finishApplicationMaster(testAppId.intValue(), SUCCEEDED);
        }
    }

    @Test
    public void testMultipleAttemptsSameNode() throws IOException, Exception, YarnException {
        String user = "hadoop";
        ApplicationId appId = ApplicationId.newInstance(1, 1);
        ApplicationAttemptId applicationAttemptId;
        // First Attempt
        RegisterApplicationMasterResponse response1 = registerApplicationMaster(appId.getId());
        Assert.assertNotNull(response1);
        AllocateResponse allocateResponse = allocate(appId.getId());
        Assert.assertNotNull(allocateResponse);
        // Second Attempt
        applicationAttemptId = ApplicationAttemptId.newInstance(appId, 2);
        getAMRMProxyService().initializePipeline(applicationAttemptId, user, new org.apache.hadoop.security.token.Token<org.apache.hadoop.yarn.security.AMRMTokenIdentifier>(), null, null, false, null);
        RequestInterceptorChainWrapper chain2 = getAMRMProxyService().getPipelines().get(appId);
        Assert.assertEquals(applicationAttemptId, chain2.getApplicationAttemptId());
        allocateResponse = allocate(appId.getId());
        Assert.assertNotNull(allocateResponse);
    }

    /**
     * Test AMRMProxy restart with recovery.
     */
    @Test
    public void testRecovery() throws Exception, YarnException {
        Configuration conf = createConfiguration();
        // Use the MockRequestInterceptorAcrossRestart instead for the chain
        conf.set(AMRM_PROXY_INTERCEPTOR_CLASS_PIPELINE, TestAMRMProxyService.MockRequestInterceptorAcrossRestart.class.getName());
        TestAMRMProxyService.mockRM = new MockResourceManagerFacade(new org.apache.hadoop.yarn.conf.YarnConfiguration(conf), 0);
        createAndStartAMRMProxyService(conf);
        int testAppId1 = 1;
        RegisterApplicationMasterResponse registerResponse = registerApplicationMaster(testAppId1);
        Assert.assertNotNull(registerResponse);
        Assert.assertEquals(Integer.toString(testAppId1), registerResponse.getQueue());
        int testAppId2 = 2;
        registerResponse = registerApplicationMaster(testAppId2);
        Assert.assertNotNull(registerResponse);
        Assert.assertEquals(Integer.toString(testAppId2), registerResponse.getQueue());
        AllocateResponse allocateResponse = allocate(testAppId2);
        Assert.assertNotNull(allocateResponse);
        // At the time of kill, app1 just registerAM, app2 already did one allocate.
        // Both application should be recovered
        createAndStartAMRMProxyService(conf);
        Assert.assertTrue(((getAMRMProxyService().getPipelines().size()) == 2));
        allocateResponse = allocate(testAppId1);
        Assert.assertNotNull(allocateResponse);
        FinishApplicationMasterResponse finshResponse = finishApplicationMaster(testAppId1, SUCCEEDED);
        Assert.assertNotNull(finshResponse);
        Assert.assertEquals(true, finshResponse.getIsUnregistered());
        allocateResponse = allocate(testAppId2);
        Assert.assertNotNull(allocateResponse);
        finshResponse = finishApplicationMaster(testAppId2, SUCCEEDED);
        Assert.assertNotNull(finshResponse);
        Assert.assertEquals(true, finshResponse.getIsUnregistered());
        int testAppId3 = 3;
        try {
            // Try to finish an application master that is not registered.
            finishApplicationMaster(testAppId3, SUCCEEDED);
            Assert.fail("The Mock RM should complain about not knowing the third app");
        } catch (Throwable ex) {
        }
        TestAMRMProxyService.mockRM = null;
    }

    /**
     * Test AMRMProxy restart with application recovery failure.
     */
    @Test
    public void testAppRecoveryFailure() throws Exception, YarnException {
        Configuration conf = createConfiguration();
        // Use the MockRequestInterceptorAcrossRestart instead for the chain
        conf.set(AMRM_PROXY_INTERCEPTOR_CLASS_PIPELINE, TestAMRMProxyService.BadRequestInterceptorAcrossRestart.class.getName());
        TestAMRMProxyService.mockRM = new MockResourceManagerFacade(new org.apache.hadoop.yarn.conf.YarnConfiguration(conf), 0);
        createAndStartAMRMProxyService(conf);
        // Create an app entry in NMSS
        registerApplicationMaster(1);
        RecoveredAMRMProxyState state = getNMContext().getNMStateStore().loadAMRMProxyState();
        Assert.assertEquals(1, state.getAppContexts().size());
        // AMRMProxy restarts and recover
        createAndStartAMRMProxyService(conf);
        state = getNMContext().getNMStateStore().loadAMRMProxyState();
        // The app that failed to recover should have been removed from NMSS
        Assert.assertEquals(0, state.getAppContexts().size());
    }

    /**
     * A mock intercepter implementation that uses the same mockRM instance across
     * restart.
     */
    public static class MockRequestInterceptorAcrossRestart extends AbstractRequestInterceptor {
        public MockRequestInterceptorAcrossRestart() {
        }

        @Override
        public void init(AMRMProxyApplicationContext appContext) {
            super.init(appContext);
            if ((TestAMRMProxyService.mockRM) == null) {
                throw new RuntimeException("mockRM not initialized yet");
            }
        }

        @Override
        public RegisterApplicationMasterResponse registerApplicationMaster(RegisterApplicationMasterRequest request) throws IOException, YarnException {
            return TestAMRMProxyService.mockRM.registerApplicationMaster(request);
        }

        @Override
        public FinishApplicationMasterResponse finishApplicationMaster(FinishApplicationMasterRequest request) throws IOException, YarnException {
            return TestAMRMProxyService.mockRM.finishApplicationMaster(request);
        }

        @Override
        public AllocateResponse allocate(AllocateRequest request) throws IOException, YarnException {
            return TestAMRMProxyService.mockRM.allocate(request);
        }
    }

    /**
     * A mock intercepter implementation that throws when recovering.
     */
    public static class BadRequestInterceptorAcrossRestart extends TestAMRMProxyService.MockRequestInterceptorAcrossRestart {
        @Override
        public void recover(Map<String, byte[]> recoveredDataMap) {
            throw new RuntimeException("Kaboom");
        }
    }
}

