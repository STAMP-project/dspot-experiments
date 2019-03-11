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
package org.apache.hadoop.yarn.server.federation.policies.manager;


import org.apache.hadoop.yarn.server.federation.policies.exceptions.FederationPolicyInitializationException;
import org.junit.Test;


/**
 * This class provides common test methods for testing {@code FederationPolicyManager}s.
 */
public abstract class BasePolicyManagerTest {
    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected FederationPolicyManager wfp = null;

    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Class expectedPolicyManager;

    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Class expectedAMRMProxyPolicy;

    @SuppressWarnings("checkstyle:visibilitymodifier")
    protected Class expectedRouterPolicy;

    @Test
    public void testSerializeAndInstantiate() throws Exception {
        BasePolicyManagerTest.serializeAndDeserializePolicyManager(wfp, expectedPolicyManager, expectedAMRMProxyPolicy, expectedRouterPolicy);
    }

    @Test(expected = FederationPolicyInitializationException.class)
    public void testSerializeAndInstantiateBad1() throws Exception {
        BasePolicyManagerTest.serializeAndDeserializePolicyManager(wfp, String.class, expectedAMRMProxyPolicy, expectedRouterPolicy);
    }

    @Test(expected = AssertionError.class)
    public void testSerializeAndInstantiateBad2() throws Exception {
        BasePolicyManagerTest.serializeAndDeserializePolicyManager(wfp, expectedPolicyManager, String.class, expectedRouterPolicy);
    }

    @Test(expected = AssertionError.class)
    public void testSerializeAndInstantiateBad3() throws Exception {
        BasePolicyManagerTest.serializeAndDeserializePolicyManager(wfp, expectedPolicyManager, expectedAMRMProxyPolicy, String.class);
    }
}

