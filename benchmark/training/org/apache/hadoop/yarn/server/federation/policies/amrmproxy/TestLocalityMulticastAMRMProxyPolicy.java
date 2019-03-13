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
package org.apache.hadoop.yarn.server.federation.policies.amrmproxy;


import ResourceRequest.ANY;
import YarnConfiguration.FEDERATION_AMRMPROXY_SUBCLUSTER_TIMEOUT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.protocolrecords.AllocateResponse;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.federation.policies.BaseFederationPoliciesTest;
import org.apache.hadoop.yarn.server.federation.policies.exceptions.FederationPolicyInitializationException;
import org.apache.hadoop.yarn.server.federation.store.records.SubClusterId;
import org.apache.hadoop.yarn.server.federation.store.records.SubClusterInfo;
import org.apache.hadoop.yarn.server.federation.utils.FederationPoliciesTestUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple test class for the {@link LocalityMulticastAMRMProxyPolicy}.
 */
public class TestLocalityMulticastAMRMProxyPolicy extends BaseFederationPoliciesTest {
    public static final Logger LOG = LoggerFactory.getLogger(TestLocalityMulticastAMRMProxyPolicy.class);

    @Test
    public void testReinitilialize() throws YarnException {
        initializePolicy();
    }

    @Test(expected = FederationPolicyInitializationException.class)
    public void testNullWeights() throws Exception {
        getPolicyInfo().setAMRMPolicyWeights(null);
        initializePolicy();
        Assert.fail();
    }

    @Test(expected = FederationPolicyInitializationException.class)
    public void testEmptyWeights() throws Exception {
        getPolicyInfo().setAMRMPolicyWeights(new HashMap<org.apache.hadoop.yarn.server.federation.store.records.SubClusterIdInfo, Float>());
        initializePolicy();
        Assert.fail();
    }

    @Test
    public void testSplitBasedOnHeadroom() throws Exception {
        // Tests how the headroom info are used to split based on the capacity
        // each RM claims to give us.
        // Configure policy to be 100% headroom based
        getPolicyInfo().setHeadroomAlpha(1.0F);
        initializePolicy();
        List<ResourceRequest> resourceRequests = createSimpleRequest();
        prepPolicyWithHeadroom(true);
        Map<SubClusterId, List<ResourceRequest>> response = ((FederationAMRMProxyPolicy) (getPolicy())).splitResourceRequests(resourceRequests, new HashSet<SubClusterId>());
        // pretty print requests
        TestLocalityMulticastAMRMProxyPolicy.LOG.info("Initial headroom");
        prettyPrintRequests(response);
        validateSplit(response, resourceRequests);
        /* based on headroom, we expect 75 containers to got to subcluster0 (60) and
        subcluster2 (15) according to the advertised headroom (40 and 10), no
        containers for sublcuster1 as it advertise zero headroom, and 25 to
        subcluster5 which has unknown headroom, and so it gets 1/4th of the load
         */
        checkExpectedAllocation(response, "subcluster0", 1, 60);
        checkExpectedAllocation(response, "subcluster1", 1, (-1));
        checkExpectedAllocation(response, "subcluster2", 1, 15);
        checkExpectedAllocation(response, "subcluster5", 1, 25);
        checkTotalContainerAllocation(response, 100);
        // notify a change in headroom and try again
        AllocateResponse ar = getAllocateResponseWithTargetHeadroom(40);
        ((FederationAMRMProxyPolicy) (getPolicy())).notifyOfResponse(SubClusterId.newInstance("subcluster2"), ar);
        response = ((FederationAMRMProxyPolicy) (getPolicy())).splitResourceRequests(resourceRequests, new HashSet<SubClusterId>());
        TestLocalityMulticastAMRMProxyPolicy.LOG.info("After headroom update");
        prettyPrintRequests(response);
        validateSplit(response, resourceRequests);
        /* we simulated a change in headroom for subcluster2, which will now have
        the same headroom of subcluster0, so each 37.5, note that the odd one
        will be assigned to either one of the two subclusters
         */
        checkExpectedAllocation(response, "subcluster0", 1, 37);
        checkExpectedAllocation(response, "subcluster1", 1, (-1));
        checkExpectedAllocation(response, "subcluster2", 1, 37);
        checkExpectedAllocation(response, "subcluster5", 1, 25);
        checkTotalContainerAllocation(response, 100);
    }

    @Test(timeout = 5000)
    public void testStressPolicy() throws Exception {
        // Tests how the headroom info are used to split based on the capacity
        // each RM claims to give us.
        // Configure policy to be 100% headroom based
        getPolicyInfo().setHeadroomAlpha(1.0F);
        initializePolicy();
        addHomeSubClusterAsActive();
        int numRR = 1000;
        List<ResourceRequest> resourceRequests = createLargeRandomList(numRR);
        prepPolicyWithHeadroom(true);
        int numIterations = 1000;
        long tstart = System.currentTimeMillis();
        for (int i = 0; i < numIterations; i++) {
            Map<SubClusterId, List<ResourceRequest>> response = ((FederationAMRMProxyPolicy) (getPolicy())).splitResourceRequests(resourceRequests, new HashSet<SubClusterId>());
            validateSplit(response, resourceRequests);
        }
        long tend = System.currentTimeMillis();
        TestLocalityMulticastAMRMProxyPolicy.LOG.info(((((("Performed " + numIterations) + " policy invocations (and ") + "validations) in ") + (tend - tstart)) + "ms"));
    }

    @Test
    public void testFWDAllZeroANY() throws Exception {
        // Tests how the headroom info are used to split based on the capacity
        // each RM claims to give us.
        // Configure policy to be 100% headroom based
        getPolicyInfo().setHeadroomAlpha(0.5F);
        initializePolicy();
        List<ResourceRequest> resourceRequests = createZeroSizedANYRequest();
        // this receives responses from sc0,sc1,sc2
        prepPolicyWithHeadroom(true);
        Map<SubClusterId, List<ResourceRequest>> response = ((FederationAMRMProxyPolicy) (getPolicy())).splitResourceRequests(resourceRequests, new HashSet<SubClusterId>());
        // we expect all three to appear for a zero-sized ANY
        // pretty print requests
        prettyPrintRequests(response);
        validateSplit(response, resourceRequests);
        // we expect the zero size request to be sent to the first 3 rm (due to
        // the fact that we received responses only from these 3 sublcusters)
        checkExpectedAllocation(response, "subcluster0", 1, 0);
        checkExpectedAllocation(response, "subcluster1", 1, 0);
        checkExpectedAllocation(response, "subcluster2", 1, 0);
        checkExpectedAllocation(response, "subcluster3", (-1), (-1));
        checkExpectedAllocation(response, "subcluster4", (-1), (-1));
        checkExpectedAllocation(response, "subcluster5", (-1), (-1));
        checkTotalContainerAllocation(response, 0);
    }

    @Test
    public void testSplitBasedOnHeadroomAndWeights() throws Exception {
        // Tests how the headroom info are used to split based on the capacity
        // each RM claims to give us.
        // Configure policy to be 50% headroom based and 50% weight based
        getPolicyInfo().setHeadroomAlpha(0.5F);
        initializePolicy();
        List<ResourceRequest> resourceRequests = createSimpleRequest();
        prepPolicyWithHeadroom(true);
        Map<SubClusterId, List<ResourceRequest>> response = ((FederationAMRMProxyPolicy) (getPolicy())).splitResourceRequests(resourceRequests, new HashSet<SubClusterId>());
        // pretty print requests
        prettyPrintRequests(response);
        validateSplit(response, resourceRequests);
        // in this case the headroom allocates 50 containers, while weights allocate
        // the rest. due to weights we have 12.5 containers for each
        // sublcuster, the rest is due to headroom.
        checkExpectedAllocation(response, "subcluster0", 1, 42);// 30 + 12.5

        checkExpectedAllocation(response, "subcluster1", 1, 12);// 0 + 12.5

        checkExpectedAllocation(response, "subcluster2", 1, 20);// 7.5 + 12.5

        checkExpectedAllocation(response, "subcluster3", (-1), (-1));
        checkExpectedAllocation(response, "subcluster4", (-1), (-1));
        checkExpectedAllocation(response, "subcluster5", 1, 25);// 12.5 + 12.5

        checkTotalContainerAllocation(response, 100);
    }

    @Test
    public void testSplitAllocateRequest() throws Exception {
        // Test a complex List<ResourceRequest> is split correctly
        initializePolicy();
        addHomeSubClusterAsActive();
        FederationPoliciesTestUtil.initializePolicyContext(getFederationPolicyContext(), getPolicy(), getPolicyInfo(), getActiveSubclusters(), new Configuration());
        List<ResourceRequest> resourceRequests = createComplexRequest();
        Map<SubClusterId, List<ResourceRequest>> response = ((FederationAMRMProxyPolicy) (getPolicy())).splitResourceRequests(resourceRequests, new HashSet<SubClusterId>());
        validateSplit(response, resourceRequests);
        prettyPrintRequests(response);
        // we expect 7 entries for home subcluster (2 for request-id 4, 3 for
        // request-id 5, and a part of the broadcast of request-id 2
        checkExpectedAllocation(response, getHomeSubCluster().getId(), 7, 29);
        // for subcluster0 we expect 10 entries, 3 from request-id 0, and 3 from
        // request-id 3, 3 entries from request-id 5, as well as part of the
        // request-id 2 broadast
        checkExpectedAllocation(response, "subcluster0", 10, 32);
        // we expect 5 entries for subcluster1 (4 from request-id 1, and part
        // of the broadcast of request-id 2
        checkExpectedAllocation(response, "subcluster1", 5, 26);
        // sub-cluster 2 should contain 3 entries from request-id 1 and 1 from the
        // broadcast of request-id 2, and no request-id 0
        checkExpectedAllocation(response, "subcluster2", 4, 23);
        // subcluster id 3, 4 should not appear (due to weights or active/inactive)
        checkExpectedAllocation(response, "subcluster3", (-1), (-1));
        checkExpectedAllocation(response, "subcluster4", (-1), (-1));
        // subcluster5 should get only part of the request-id 2 broadcast
        checkExpectedAllocation(response, "subcluster5", 1, 20);
        // Check the total number of container asks in all RR
        checkTotalContainerAllocation(response, 130);
        // check that the allocations that show up are what expected
        for (ResourceRequest rr : response.get(getHomeSubCluster())) {
            Assert.assertTrue(((((rr.getAllocationRequestId()) == 2L) || ((rr.getAllocationRequestId()) == 4L)) || ((rr.getAllocationRequestId()) == 5L)));
        }
        List<ResourceRequest> rrs = response.get(SubClusterId.newInstance("subcluster0"));
        for (ResourceRequest rr : rrs) {
            Assert.assertTrue(((rr.getAllocationRequestId()) != 1L));
            Assert.assertTrue(((rr.getAllocationRequestId()) != 4L));
        }
        for (ResourceRequest rr : response.get(SubClusterId.newInstance("subcluster1"))) {
            Assert.assertTrue((((rr.getAllocationRequestId()) == 1L) || ((rr.getAllocationRequestId()) == 2L)));
        }
        for (ResourceRequest rr : response.get(SubClusterId.newInstance("subcluster2"))) {
            Assert.assertTrue((((rr.getAllocationRequestId()) == 1L) || ((rr.getAllocationRequestId()) == 2L)));
        }
        for (ResourceRequest rr : response.get(SubClusterId.newInstance("subcluster5"))) {
            Assert.assertTrue(((rr.getAllocationRequestId()) == 2));
            Assert.assertTrue(rr.getRelaxLocality());
        }
    }

    @Test
    public void testIntegerAssignment() throws YarnException {
        float[] weights = new float[]{ 0, 0.1F, 0.2F, 0.2F, -0.1F, 0.1F, 0.2F, 0.1F, 0.1F };
        int[] expectedMin = new int[]{ 0, 1, 3, 3, 0, 1, 3, 1, 1 };
        ArrayList<Float> weightsList = new ArrayList<>();
        for (float weight : weights) {
            weightsList.add(weight);
        }
        LocalityMulticastAMRMProxyPolicy policy = ((LocalityMulticastAMRMProxyPolicy) (getPolicy()));
        for (int i = 0; i < 500000; i++) {
            ArrayList<Integer> allocations = policy.computeIntegerAssignment(19, weightsList);
            int sum = 0;
            for (int j = 0; j < (weights.length); j++) {
                sum += allocations.get(j);
                if ((allocations.get(j)) < (expectedMin[j])) {
                    Assert.fail((((((((allocations.get(j)) + " at index ") + j) + " should be at least ") + (expectedMin[j])) + ". Allocation array: ") + (printList(allocations))));
                }
            }
            Assert.assertEquals(("Expect sum to be 19 in array: " + (printList(allocations))), 19, sum);
        }
    }

    @Test
    public void testCancelWithLocalizedResource() throws YarnException {
        // Configure policy to be 100% headroom based
        getPolicyInfo().setHeadroomAlpha(1.0F);
        initializePolicy();
        List<ResourceRequest> resourceRequests = new ArrayList<>();
        // Initialize the headroom map
        prepPolicyWithHeadroom(true);
        // Cancel at ANY level only
        resourceRequests.add(FederationPoliciesTestUtil.createResourceRequest(0L, "subcluster0-rack0-host0", 1024, 1, 1, 1, null, false));
        resourceRequests.add(FederationPoliciesTestUtil.createResourceRequest(0L, "subcluster0-rack0", 1024, 1, 1, 1, null, false));
        resourceRequests.add(FederationPoliciesTestUtil.createResourceRequest(0L, ANY, 1024, 1, 1, 0, null, false));
        Map<SubClusterId, List<ResourceRequest>> response = ((FederationAMRMProxyPolicy) (getPolicy())).splitResourceRequests(resourceRequests, new HashSet<SubClusterId>());
        checkExpectedAllocation(response, "subcluster0", 3, 1);
        checkExpectedAllocation(response, "subcluster1", 1, 0);
        checkExpectedAllocation(response, "subcluster2", 1, 0);
        checkExpectedAllocation(response, "subcluster3", (-1), (-1));
        checkExpectedAllocation(response, "subcluster4", (-1), (-1));
        checkExpectedAllocation(response, "subcluster5", (-1), (-1));
        resourceRequests.clear();
        // Cancel at node level only
        resourceRequests.add(FederationPoliciesTestUtil.createResourceRequest(0L, "subcluster0-rack0-host0", 1024, 1, 1, 0, null, false));
        resourceRequests.add(FederationPoliciesTestUtil.createResourceRequest(0L, "subcluster0-rack0", 1024, 1, 1, 0, null, false));
        resourceRequests.add(FederationPoliciesTestUtil.createResourceRequest(0L, ANY, 1024, 1, 1, 100, null, false));
        response = ((FederationAMRMProxyPolicy) (getPolicy())).splitResourceRequests(resourceRequests, new HashSet<SubClusterId>());
        /* Since node request is a cancel, it should not be considered associated
        with localized requests. Based on headroom, we expect 75 containers to
        got to subcluster0 (60) and subcluster2 (15) according to the advertised
        headroom (40 and 10), no containers for sublcuster1 as it advertise zero
        headroom, and 25 to subcluster5 which has unknown headroom, and so it
        gets 1/4th of the load
         */
        checkExpectedAllocation(response, "subcluster0", 3, 60);
        checkExpectedAllocation(response, "subcluster1", 1, (-1));
        checkExpectedAllocation(response, "subcluster2", 1, 15);
        checkExpectedAllocation(response, "subcluster5", 1, 25);
        checkTotalContainerAllocation(response, 100);
    }

    @Test
    public void testSubClusterExpiry() throws Exception {
        // Tests how the headroom info are used to split based on the capacity
        // each RM claims to give us.
        // Configure policy to be 100% headroom based
        getPolicyInfo().setHeadroomAlpha(1.0F);
        YarnConfiguration conf = new YarnConfiguration();
        // Set expiry to 500ms
        conf.setLong(FEDERATION_AMRMPROXY_SUBCLUSTER_TIMEOUT, 500);
        initializePolicy(conf);
        List<ResourceRequest> resourceRequests = createSimpleRequest();
        prepPolicyWithHeadroom(true);
        // For first time, no sub-cluster expired
        Set<SubClusterId> expiredSCList = new HashSet<>();
        Map<SubClusterId, List<ResourceRequest>> response = ((FederationAMRMProxyPolicy) (getPolicy())).splitResourceRequests(resourceRequests, expiredSCList);
        // pretty print requests
        prettyPrintRequests(response);
        validateSplit(response, resourceRequests);
        /* based on headroom, we expect 75 containers to got to subcluster0 (60) and
        subcluster2 (15) according to the advertised headroom (40 and 10), no
        containers for sublcuster1 as it advertise zero headroom, and 25 to
        subcluster5 which has unknown headroom, and so it gets 1/4th of the load
         */
        checkExpectedAllocation(response, "subcluster0", 1, 60);
        checkExpectedAllocation(response, "subcluster1", 1, (-1));
        checkExpectedAllocation(response, "subcluster2", 1, 15);
        checkExpectedAllocation(response, "subcluster5", 1, 25);
        checkTotalContainerAllocation(response, 100);
        Thread.sleep(800);
        // For the second time, sc0 and sc5 expired
        expiredSCList.add(SubClusterId.newInstance("subcluster0"));
        expiredSCList.add(SubClusterId.newInstance("subcluster5"));
        response = ((FederationAMRMProxyPolicy) (getPolicy())).splitResourceRequests(resourceRequests, expiredSCList);
        // pretty print requests
        prettyPrintRequests(response);
        validateSplit(response, resourceRequests);
        checkExpectedAllocation(response, "subcluster0", 1, (-1));
        checkExpectedAllocation(response, "subcluster1", 1, (-1));
        checkExpectedAllocation(response, "subcluster2", 1, 100);
        checkExpectedAllocation(response, "subcluster5", 1, (-1));
        checkTotalContainerAllocation(response, 100);
    }

    /**
     * A testable version of LocalityMulticastAMRMProxyPolicy that
     * deterministically falls back to home sub-cluster for unresolved requests.
     */
    private class TestableLocalityMulticastAMRMProxyPolicy extends LocalityMulticastAMRMProxyPolicy {
        @Override
        protected SubClusterId getSubClusterForUnResolvedRequest(AllocationBookkeeper bookkeeper, long allocationId) {
            SubClusterId originalResult = super.getSubClusterForUnResolvedRequest(bookkeeper, allocationId);
            Map<SubClusterId, SubClusterInfo> activeClusters = null;
            try {
                activeClusters = getActiveSubclusters();
            } catch (YarnException e) {
                throw new RuntimeException(e);
            }
            // The randomly selected sub-cluster should at least be active
            Assert.assertTrue(activeClusters.containsKey(originalResult));
            // Alwasy use home sub-cluster so that unit test is deterministic
            return getHomeSubCluster();
        }
    }
}

