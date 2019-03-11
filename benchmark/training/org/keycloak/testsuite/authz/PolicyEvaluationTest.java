/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.authz;


import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class PolicyEvaluationTest extends AbstractAuthzTest {
    @Test
    public void testCheckDateAndTime() {
        testingClient.server().run(PolicyEvaluationTest::testCheckDateAndTime);
    }

    @Test
    public void testCheckUserInGroup() {
        testingClient.server().run(PolicyEvaluationTest::testCheckUserInGroup);
    }

    @Test
    public void testCheckUserInRole() {
        testingClient.server().run(PolicyEvaluationTest::testCheckUserInRole);
    }

    @Test
    public void testCheckUserInClientRole() {
        testingClient.server().run(PolicyEvaluationTest::testCheckUserInClientRole);
    }

    @Test
    public void testCheckGroupInRole() {
        testingClient.server().run(PolicyEvaluationTest::testCheckGroupInRole);
    }

    @Test
    public void testCheckUserRealmRoles() {
        testingClient.server().run(PolicyEvaluationTest::testCheckUserRealmRoles);
    }

    @Test
    public void testCheckUserClientRoles() {
        testingClient.server().run(PolicyEvaluationTest::testCheckUserClientRoles);
    }

    @Test
    public void testCheckUserGroups() {
        testingClient.server().run(PolicyEvaluationTest::testCheckUserGroups);
    }

    @Test
    public void testCheckUserAttributes() {
        testingClient.server().run(PolicyEvaluationTest::testCheckUserAttributes);
    }

    @Test
    public void testCheckResourceAttributes() {
        testingClient.server().run(PolicyEvaluationTest::testCheckResourceAttributes);
    }

    @Test
    public void testCheckReadOnlyInstances() {
        testingClient.server().run(PolicyEvaluationTest::testCheckReadOnlyInstances);
    }

    @Test
    public void testCachedDecisionsWithNegativePolicies() {
        testingClient.server().run(PolicyEvaluationTest::testCachedDecisionsWithNegativePolicies);
    }
}

