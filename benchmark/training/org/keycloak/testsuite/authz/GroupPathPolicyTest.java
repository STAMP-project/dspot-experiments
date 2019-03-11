/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
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


import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.keycloak.representations.idm.authorization.PermissionRequest;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class GroupPathPolicyTest extends AbstractAuthzTest {
    @Test
    public void testAllowParentAndChildren() {
        AuthzClient authzClient = getAuthzClient();
        PermissionRequest request = new PermissionRequest("Resource A");
        String ticket = authzClient.protection().permission().create(request).getTicket();
        AuthorizationResponse response = authzClient.authorization("marta", "password").authorize(new AuthorizationRequest(ticket));
        Assert.assertNotNull(response.getToken());
        RealmResource realm = getRealm();
        GroupRepresentation group = getGroup("/Group A/Group B/Group C");
        UserRepresentation user = realm.users().search("kolo").get(0);
        realm.users().get(user.getId()).joinGroup(group.getId());
        ticket = authzClient.protection().permission().create(request).getTicket();
        response = authzClient.authorization("kolo", "password").authorize(new AuthorizationRequest(ticket));
        Assert.assertNotNull(response.getToken());
    }

    @Test
    public void testOnlyChildrenPolicy() throws Exception {
        RealmResource realm = getRealm();
        AuthzClient authzClient = getAuthzClient();
        PermissionRequest request = new PermissionRequest("Resource B");
        String ticket = authzClient.protection().permission().create(request).getTicket();
        try {
            authzClient.authorization("kolo", "password").authorize(new AuthorizationRequest(ticket));
            Assert.fail("Should fail because user is not granted with expected role");
        } catch (AuthorizationDeniedException ignore) {
        }
        GroupRepresentation group = getGroup("/Group A/Group B/Group C");
        UserRepresentation user = realm.users().search("kolo").get(0);
        realm.users().get(user.getId()).joinGroup(group.getId());
        AuthorizationResponse response = authzClient.authorization("kolo", "password").authorize(new AuthorizationRequest(ticket));
        Assert.assertNotNull(response.getToken());
        try {
            authzClient.authorization("marta", "password").authorize(new AuthorizationRequest(ticket));
            Assert.fail("Should fail because user is not granted with expected role");
        } catch (AuthorizationDeniedException ignore) {
        }
    }
}

