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


import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.federation.policies.BaseFederationPoliciesTest;
import org.apache.hadoop.yarn.server.federation.policies.dao.WeightedPolicyInfo;
import org.apache.hadoop.yarn.server.federation.policies.exceptions.FederationPolicyException;
import org.apache.hadoop.yarn.server.federation.store.records.SubClusterId;
import org.apache.hadoop.yarn.server.federation.utils.FederationPoliciesTestUtil;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Simple test class for the {@link HomeAMRMProxyPolicy}.
 */
public class TestHomeAMRMProxyPolicy extends BaseFederationPoliciesTest {
    private static final int NUM_SUBCLUSTERS = 4;

    private static final String HOME_SC_NAME = "sc2";

    private static final SubClusterId HOME_SC_ID = SubClusterId.newInstance(TestHomeAMRMProxyPolicy.HOME_SC_NAME);

    @Test
    public void testSplitAllocateRequest() throws YarnException {
        // Verify the request only goes to the home subcluster
        String[] hosts = new String[]{ "host0", "host1", "host2", "host3" };
        List<ResourceRequest> resourceRequests = FederationPoliciesTestUtil.createResourceRequests(hosts, (2 * 1024), 2, 1, 3, null, false);
        HomeAMRMProxyPolicy federationPolicy = ((HomeAMRMProxyPolicy) (getPolicy()));
        Map<SubClusterId, List<ResourceRequest>> response = federationPolicy.splitResourceRequests(resourceRequests, new HashSet<SubClusterId>());
        Assert.assertEquals(1, response.size());
        Assert.assertNotNull(response.get(TestHomeAMRMProxyPolicy.HOME_SC_ID));
        Assert.assertEquals(9, response.get(TestHomeAMRMProxyPolicy.HOME_SC_ID).size());
    }

    @Test
    public void testHomeSubclusterNotActive() throws YarnException {
        // We setup the home subcluster to a non-existing one
        FederationPoliciesTestUtil.initializePolicyContext(getPolicy(), Mockito.mock(WeightedPolicyInfo.class), getActiveSubclusters(), "badsc");
        // Verify the request fails because the home subcluster is not available
        try {
            String[] hosts = new String[]{ "host0", "host1", "host2", "host3" };
            List<ResourceRequest> resourceRequests = FederationPoliciesTestUtil.createResourceRequests(hosts, (2 * 1024), 2, 1, 3, null, false);
            HomeAMRMProxyPolicy federationPolicy = ((HomeAMRMProxyPolicy) (getPolicy()));
            federationPolicy.splitResourceRequests(resourceRequests, new HashSet<SubClusterId>());
            Assert.fail("It should fail when the home subcluster is not active");
        } catch (FederationPolicyException e) {
            GenericTestUtils.assertExceptionContains("is not active", e);
        }
    }
}

