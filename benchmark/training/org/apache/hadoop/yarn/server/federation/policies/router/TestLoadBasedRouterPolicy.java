/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.yarn.server.federation.policies.router;


import SubClusterState.SC_RUNNING;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.federation.policies.dao.WeightedPolicyInfo;
import org.apache.hadoop.yarn.server.federation.store.records.SubClusterId;
import org.apache.hadoop.yarn.server.federation.store.records.SubClusterIdInfo;
import org.apache.hadoop.yarn.server.federation.store.records.SubClusterInfo;
import org.apache.hadoop.yarn.server.federation.utils.FederationPoliciesTestUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * Simple test class for the {@link LoadBasedRouterPolicy}. Test that the load
 * is properly considered for allocation.
 */
public class TestLoadBasedRouterPolicy extends BaseRouterPoliciesTest {
    @Test
    public void testLoadIsRespected() throws YarnException {
        SubClusterId chosen = ((FederationRouterPolicy) (getPolicy())).getHomeSubcluster(getApplicationSubmissionContext(), null);
        // check the "planted" best cluster is chosen
        Assert.assertEquals("sc05", chosen.getId());
    }

    @Test
    public void testIfNoSubclustersWithWeightOne() {
        setPolicy(new LoadBasedRouterPolicy());
        setPolicyInfo(new WeightedPolicyInfo());
        Map<SubClusterIdInfo, Float> routerWeights = new HashMap<>();
        Map<SubClusterIdInfo, Float> amrmWeights = new HashMap<>();
        // update subcluster with weight 0
        SubClusterIdInfo sc = new SubClusterIdInfo(String.format("sc%02d", 0));
        SubClusterInfo federationSubClusterInfo = SubClusterInfo.newInstance(sc.toId(), null, null, null, null, (-1), SC_RUNNING, (-1), generateClusterMetricsInfo(0));
        getActiveSubclusters().clear();
        getActiveSubclusters().put(sc.toId(), federationSubClusterInfo);
        routerWeights.put(sc, 0.0F);
        amrmWeights.put(sc, 0.0F);
        getPolicyInfo().setRouterPolicyWeights(routerWeights);
        getPolicyInfo().setAMRMPolicyWeights(amrmWeights);
        try {
            FederationPoliciesTestUtil.initializePolicyContext(getPolicy(), getPolicyInfo(), getActiveSubclusters());
            ((FederationRouterPolicy) (getPolicy())).getHomeSubcluster(getApplicationSubmissionContext(), null);
            Assert.fail();
        } catch (YarnException ex) {
            Assert.assertTrue(ex.getMessage().contains("Zero Active Subcluster with weight 1"));
        }
    }
}

