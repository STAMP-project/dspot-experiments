/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.storage.testing;


import com.google.api.services.storage.model.Policy;
import com.google.api.services.storage.model.Policy.Bindings;
import com.google.common.collect.ImmutableList;
import org.easymock.EasyMock;
import org.junit.Test;


public class ApiPolicyMatcherTest {
    private static interface PolicyAcceptor {
        int accept(Policy policy);
    }

    private static final String ETAG = "CAE=";

    private static final Policy API_POLICY_1 = new Policy().setBindings(ImmutableList.of(new Bindings().setMembers(ImmutableList.of("allUsers")).setRole("roles/storage.objectViewer"), new Bindings().setMembers(ImmutableList.of("user:test1@gmail.com", "user:test2@gmail.com")).setRole("roles/storage.objectAdmin"), new Bindings().setMembers(ImmutableList.of("group:test-group@gmail.com")).setRole("roles/storage.admin"))).setEtag(ApiPolicyMatcherTest.ETAG);

    private static final Policy API_POLICY_2 = new Policy().setBindings(ImmutableList.of(new Bindings().setMembers(ImmutableList.of("group:test-group@gmail.com")).setRole("roles/storage.admin"), new Bindings().setMembers(ImmutableList.of("allUsers")).setRole("roles/storage.objectViewer"), new Bindings().setMembers(ImmutableList.of("user:test2@gmail.com", "user:test1@gmail.com")).setRole("roles/storage.objectAdmin"))).setEtag(ApiPolicyMatcherTest.ETAG);

    private static final Policy API_POLICY_MISSING_BINDINGS = new Policy().setEtag(ApiPolicyMatcherTest.ETAG);

    private static final Policy API_POLICY_MISSING_ETAG = new Policy().setBindings(ImmutableList.of(new Bindings().setMembers(ImmutableList.of("group:test-group@gmail.com")).setRole("roles/storage.admin"), new Bindings().setMembers(ImmutableList.of("allUsers")).setRole("roles/storage.objectViewer"), new Bindings().setMembers(ImmutableList.of("user:test2@gmail.com", "user:test1@gmail.com")).setRole("roles/storage.objectAdmin")));

    @Test
    public void testEquivalence() {
        ApiPolicyMatcherTest.assertMatch(ApiPolicyMatcherTest.API_POLICY_1, ApiPolicyMatcherTest.API_POLICY_2);
        ApiPolicyMatcherTest.assertMatch(ApiPolicyMatcherTest.API_POLICY_2, ApiPolicyMatcherTest.API_POLICY_1);
        ApiPolicyMatcherTest.assertNoMatch(ApiPolicyMatcherTest.API_POLICY_1, ApiPolicyMatcherTest.API_POLICY_MISSING_BINDINGS);
        ApiPolicyMatcherTest.assertNoMatch(ApiPolicyMatcherTest.API_POLICY_MISSING_BINDINGS, ApiPolicyMatcherTest.API_POLICY_1);
        ApiPolicyMatcherTest.assertNoMatch(ApiPolicyMatcherTest.API_POLICY_1, ApiPolicyMatcherTest.API_POLICY_MISSING_ETAG);
        ApiPolicyMatcherTest.assertNoMatch(ApiPolicyMatcherTest.API_POLICY_MISSING_ETAG, ApiPolicyMatcherTest.API_POLICY_1);
        ApiPolicyMatcherTest.assertNoMatch(ApiPolicyMatcherTest.API_POLICY_MISSING_BINDINGS, ApiPolicyMatcherTest.API_POLICY_MISSING_ETAG);
        ApiPolicyMatcherTest.assertNoMatch(ApiPolicyMatcherTest.API_POLICY_MISSING_ETAG, ApiPolicyMatcherTest.API_POLICY_MISSING_BINDINGS);
    }

    @Test
    public void testStaticMocker() {
        ApiPolicyMatcherTest.PolicyAcceptor mock = EasyMock.createMock(ApiPolicyMatcherTest.PolicyAcceptor.class);
        EasyMock.expect(mock.accept(ApiPolicyMatcher.eqApiPolicy(ApiPolicyMatcherTest.API_POLICY_1))).andReturn(0);
        EasyMock.replay(mock);
        mock.accept(ApiPolicyMatcherTest.API_POLICY_2);
        EasyMock.verify(mock);
    }
}

