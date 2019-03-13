/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.testsuite.admin;


import OperationType.DELETE;
import ResourceType.USER_LOGIN_FAILURE;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.keycloak.admin.client.resource.AttackDetectionResource;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.AdminEventPaths;
import org.keycloak.testsuite.util.OAuthClient;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class AttackDetectionResourceTest extends AbstractAdminTest {
    @ArquillianResource
    private OAuthClient oauthClient;

    @Test
    public void test() {
        AttackDetectionResource detection = adminClient.realm("test").attackDetection();
        assertBruteForce(detection.bruteForceUserStatus(findUser("test-user@localhost").getId()), 0, false, false);
        oauthClient.doLogin("test-user@localhost", "invalid");
        oauthClient.doLogin("test-user@localhost", "invalid");
        oauthClient.doLogin("test-user@localhost", "invalid");
        oauthClient.doLogin("test-user2", "invalid");
        oauthClient.doLogin("test-user2", "invalid");
        oauthClient.doLogin("nosuchuser", "invalid");
        assertBruteForce(detection.bruteForceUserStatus(findUser("test-user@localhost").getId()), 2, true, true);
        assertBruteForce(detection.bruteForceUserStatus(findUser("test-user2").getId()), 2, true, true);
        assertBruteForce(detection.bruteForceUserStatus("nosuchuser"), 0, false, false);
        detection.clearBruteForceForUser(findUser("test-user@localhost").getId());
        assertAdminEvents.assertEvent("test", DELETE, AdminEventPaths.attackDetectionClearBruteForceForUserPath(findUser("test-user@localhost").getId()), USER_LOGIN_FAILURE);
        assertBruteForce(detection.bruteForceUserStatus(findUser("test-user@localhost").getId()), 0, false, false);
        assertBruteForce(detection.bruteForceUserStatus(findUser("test-user2").getId()), 2, true, true);
        detection.clearAllBruteForce();
        assertAdminEvents.assertEvent("test", DELETE, AdminEventPaths.attackDetectionClearAllBruteForcePath(), USER_LOGIN_FAILURE);
        assertBruteForce(detection.bruteForceUserStatus(findUser("test-user@localhost").getId()), 0, false, false);
        assertBruteForce(detection.bruteForceUserStatus(findUser("test-user2").getId()), 0, false, false);
    }
}

