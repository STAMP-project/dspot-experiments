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
import org.apache.hadoop.yarn.api.records.ResourceRequest;
import org.apache.hadoop.yarn.server.federation.policies.BaseFederationPoliciesTest;
import org.apache.hadoop.yarn.server.federation.policies.exceptions.FederationPolicyException;
import org.apache.hadoop.yarn.server.federation.store.records.SubClusterId;
import org.apache.hadoop.yarn.server.federation.utils.FederationPoliciesTestUtil;
import org.junit.Test;


/**
 * Simple test class for the {@link RejectAMRMProxyPolicy}.
 */
public class TestRejectAMRMProxyPolicy extends BaseFederationPoliciesTest {
    @Test(expected = FederationPolicyException.class)
    public void testSplitAllocateRequest() throws Exception {
        // verify the request is broadcasted to all subclusters
        String[] hosts = new String[]{ "host1", "host2" };
        List<ResourceRequest> resourceRequests = FederationPoliciesTestUtil.createResourceRequests(hosts, (2 * 1024), 2, 1, 3, null, false);
        Map<SubClusterId, List<ResourceRequest>> response = ((FederationAMRMProxyPolicy) (getPolicy())).splitResourceRequests(resourceRequests, new HashSet<SubClusterId>());
    }
}

