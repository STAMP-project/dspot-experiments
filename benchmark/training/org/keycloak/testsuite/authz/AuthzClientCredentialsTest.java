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


import AccessToken.Authorization;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.resource.AuthorizationResource;
import org.keycloak.authorization.client.resource.ProtectionResource;
import org.keycloak.authorization.client.util.HttpResponseException;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.keycloak.representations.idm.authorization.Permission;
import org.keycloak.representations.idm.authorization.PermissionRequest;
import org.keycloak.representations.idm.authorization.PermissionResponse;


/**
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class AuthzClientCredentialsTest extends AbstractAuthzTest {
    @Test
    public void testSuccessfulJWTAuthentication() {
        assertAccessProtectionAPI(getAuthzClient("keycloak-with-jwt-authentication.json").protection());
    }

    @Test
    public void testSuccessfulAuthorizationRequest() throws Exception {
        AuthzClient authzClient = getAuthzClient("keycloak-with-jwt-authentication.json");
        ProtectionResource protection = authzClient.protection();
        PermissionRequest request = new PermissionRequest("Default Resource");
        PermissionResponse ticketResponse = protection.permission().create(request);
        String ticket = ticketResponse.getTicket();
        AuthorizationResponse authorizationResponse = authzClient.authorization("marta", "password").authorize(new AuthorizationRequest(ticket));
        String rpt = authorizationResponse.getToken();
        Assert.assertNotNull(rpt);
        AccessToken accessToken = new JWSInput(rpt).readJsonContent(AccessToken.class);
        AccessToken.Authorization authorization = accessToken.getAuthorization();
        Assert.assertNotNull(authorization);
        List<Permission> permissions = new java.util.ArrayList(authorization.getPermissions());
        Assert.assertFalse(permissions.isEmpty());
        Assert.assertEquals("Default Resource", permissions.get(0).getResourceName());
    }

    @Test
    public void failJWTAuthentication() {
        try {
            getAuthzClient("keycloak-with-invalid-keys-jwt-authentication.json").protection().resource().findAll();
            Assert.fail("Should fail due to invalid signature");
        } catch (Exception cause) {
            Assert.assertTrue(HttpResponseException.class.isInstance(cause.getCause().getCause()));
            Assert.assertEquals(400, HttpResponseException.class.cast(cause.getCause().getCause()).getStatusCode());
        }
    }

    @Test
    public void testSuccessfulClientSecret() {
        ProtectionResource protection = getAuthzClient("default-keycloak.json").protection();
        assertAccessProtectionAPI(protection);
    }

    @Test
    public void testReusingAccessAndRefreshTokens() throws Exception {
        ClientsResource clients = getAdminClient().realm("authz-test-session").clients();
        ClientRepresentation clientRepresentation = clients.findByClientId("resource-server-test").get(0);
        List<UserSessionRepresentation> userSessions = clients.get(clientRepresentation.getId()).getUserSessions((-1), (-1));
        Assert.assertEquals(0, userSessions.size());
        AuthzClient authzClient = getAuthzClient("default-session-keycloak.json");
        ProtectionResource protection = authzClient.protection();
        protection.resource().findByName("Default Resource");
        userSessions = clients.get(clientRepresentation.getId()).getUserSessions(null, null);
        Assert.assertEquals(1, userSessions.size());
        Thread.sleep(2000);
        protection = authzClient.protection();
        protection.resource().findByName("Default Resource");
        userSessions = clients.get(clientRepresentation.getId()).getUserSessions(null, null);
        Assert.assertEquals(1, userSessions.size());
    }

    @Test
    public void testSingleSessionPerUser() throws Exception {
        ClientsResource clients = getAdminClient().realm("authz-test-session").clients();
        ClientRepresentation clientRepresentation = clients.findByClientId("resource-server-test").get(0);
        List<UserSessionRepresentation> userSessions = clients.get(clientRepresentation.getId()).getUserSessions((-1), (-1));
        Assert.assertEquals(0, userSessions.size());
        AuthzClient authzClient = getAuthzClient("default-session-keycloak.json");
        AuthorizationResource authorization = authzClient.authorization("marta", "password");
        AuthorizationResponse response = authorization.authorize();
        AccessToken accessToken = toAccessToken(response.getToken());
        String sessionState = accessToken.getSessionState();
        Assert.assertEquals(1, accessToken.getAuthorization().getPermissions().size());
        Assert.assertEquals("Default Resource", accessToken.getAuthorization().getPermissions().iterator().next().getResourceName());
        userSessions = clients.get(clientRepresentation.getId()).getUserSessions(null, null);
        Assert.assertEquals(1, userSessions.size());
        for (int i = 0; i < 3; i++) {
            response = authorization.authorize();
            accessToken = toAccessToken(response.getToken());
            Assert.assertEquals(sessionState, accessToken.getSessionState());
            Thread.sleep(1000);
        }
        userSessions = clients.get(clientRepresentation.getId()).getUserSessions(null, null);
        Assert.assertEquals(1, userSessions.size());
    }
}

