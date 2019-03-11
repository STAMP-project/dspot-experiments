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
package org.keycloak.testsuite.authz;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.keycloak.representations.idm.authorization.PermissionRequest;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class RolePolicyTest extends AbstractAuthzTest {
    @Test
    public void testUserWithExpectedRole() {
        AuthzClient authzClient = getAuthzClient();
        PermissionRequest request = new PermissionRequest("Resource A");
        String ticket = authzClient.protection().permission().create(request).getTicket();
        AuthorizationResponse response = authzClient.authorization("marta", "password").authorize(new AuthorizationRequest(ticket));
        Assert.assertNotNull(response.getToken());
    }

    @Test
    public void testUserWithoutExpectedRole() {
        AuthzClient authzClient = getAuthzClient();
        PermissionRequest request = new PermissionRequest("Resource A");
        String ticket = authzClient.protection().permission().create(request).getTicket();
        try {
            authzClient.authorization("kolo", "password").authorize(new AuthorizationRequest(ticket));
            Assert.fail("Should fail because user is not granted with expected role");
        } catch (AuthorizationDeniedException ignore) {
        }
        request.setResourceId("Resource B");
        ticket = authzClient.protection().permission().create(request).getTicket();
        Assert.assertNotNull(authzClient.authorization("kolo", "password").authorize(new AuthorizationRequest(ticket)));
        UserRepresentation user = getRealm().users().search("kolo").get(0);
        RoleRepresentation roleA = getRealm().roles().get("Role A").toRepresentation();
        getRealm().users().get(user.getId()).roles().realmLevel().add(Arrays.asList(roleA));
        request.setResourceId("Resource A");
        ticket = authzClient.protection().permission().create(request).getTicket();
        Assert.assertNotNull(authzClient.authorization("kolo", "password").authorize(new AuthorizationRequest(ticket)));
    }

    @Test
    public void testUserWithGroupRole() throws InterruptedException {
        AuthzClient authzClient = getAuthzClient();
        PermissionRequest request = new PermissionRequest();
        request.setResourceId("Resource C");
        String ticket = authzClient.protection().permission().create(request).getTicket();
        Assert.assertNotNull(authzClient.authorization("alice", "password").authorize(new AuthorizationRequest(ticket)));
        UserRepresentation user = getRealm().users().search("alice").get(0);
        GroupRepresentation groupB = getRealm().groups().groups().stream().filter(( representation) -> "Group B".equals(representation.getName())).findFirst().get();
        getRealm().users().get(user.getId()).leaveGroup(groupB.getId());
        try {
            authzClient.authorization("alice", "password").authorize(new AuthorizationRequest(ticket));
            Assert.fail("Should fail because user is not granted with expected role");
        } catch (AuthorizationDeniedException ignore) {
        }
        request.setResourceId("Resource A");
        ticket = authzClient.protection().permission().create(request).getTicket();
        try {
            authzClient.authorization("alice", "password").authorize(new AuthorizationRequest(ticket));
            Assert.fail("Should fail because user is not granted with expected role");
        } catch (AuthorizationDeniedException ignore) {
        }
        GroupRepresentation groupA = getRealm().groups().groups().stream().filter(( representation) -> "Group A".equals(representation.getName())).findFirst().get();
        getRealm().users().get(user.getId()).joinGroup(groupA.getId());
        Assert.assertNotNull(authzClient.authorization("alice", "password").authorize(new AuthorizationRequest(ticket)));
    }
}

