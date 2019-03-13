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
package org.keycloak.testsuite.admin.client.authorization;


import CorsHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import CorsHeaders.ORIGIN;
import HttpHeaders.WWW_AUTHENTICATE;
import HttpMethod.OPTIONS;
import OAuth2Constants.CODE;
import OAuthClient.AccessTokenResponse;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.AuthorizationContext;
import org.keycloak.adapters.AuthenticatedActionsHandler;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.OIDCHttpFacade;
import org.keycloak.adapters.authorization.PolicyEnforcer;
import org.keycloak.adapters.spi.HttpFacade.Response;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.PermissionsResource;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.keycloak.representations.idm.authorization.Permission;
import org.keycloak.representations.idm.authorization.ResourcePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.OAuthClient;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class PolicyEnforcerTest extends AbstractKeycloakTest {
    private static final String RESOURCE_SERVER_CLIENT_ID = "resource-server-test";

    private static final String REALM_NAME = "authz-test";

    @Test
    public void testBearerOnlyClientResponse() {
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getAdapterConfiguration("enforcer-bearer-only.json"));
        PolicyEnforcer policyEnforcer = deployment.getPolicyEnforcer();
        OIDCHttpFacade httpFacade = createHttpFacade("/api/resourcea");
        AuthorizationContext context = policyEnforcer.enforce(httpFacade);
        Assert.assertFalse(context.isGranted());
        Assert.assertEquals(403, PolicyEnforcerTest.TestResponse.class.cast(httpFacade.getResponse()).getStatus());
        oauth.realm(PolicyEnforcerTest.REALM_NAME);
        oauth.clientId("public-client-test");
        oauth.doLogin("marta", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, null);
        String token = response.getAccessToken();
        httpFacade = createHttpFacade("/api/resourcea", token);
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertTrue(context.isGranted());
        httpFacade = createHttpFacade("/api/resourceb");
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertFalse(context.isGranted());
        Assert.assertEquals(403, PolicyEnforcerTest.TestResponse.class.cast(httpFacade.getResponse()).getStatus());
    }

    @Test
    public void testResolvingClaimsOnce() {
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getAdapterConfiguration("enforcer-bearer-only-with-cip.json"));
        PolicyEnforcer policyEnforcer = deployment.getPolicyEnforcer();
        oauth.realm(PolicyEnforcerTest.REALM_NAME);
        oauth.clientId("public-client-test");
        oauth.doLogin("marta", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, null);
        String token = response.getAccessToken();
        OIDCHttpFacade httpFacade = createHttpFacade("/api/resourcea", token, new Function<String, String>() {
            AtomicBoolean resolved = new AtomicBoolean();

            @Override
            public String apply(String s) {
                Assert.assertTrue(resolved.compareAndSet(false, true));
                return "value-" + s;
            }
        });
        AuthorizationContext context = policyEnforcer.enforce(httpFacade);
        Permission permission = context.getPermissions().get(0);
        Map<String, Set<String>> claims = permission.getClaims();
        Assert.assertTrue(context.isGranted());
        Assert.assertEquals("value-claim-a", claims.get("claim-a").iterator().next());
        Assert.assertEquals("claim-b", claims.get("claim-b").iterator().next());
    }

    @Test
    public void testCustomClaimProvider() {
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getAdapterConfiguration("enforcer-bearer-only-with-cip.json"));
        PolicyEnforcer policyEnforcer = deployment.getPolicyEnforcer();
        oauth.realm(PolicyEnforcerTest.REALM_NAME);
        oauth.clientId("public-client-test");
        oauth.doLogin("marta", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, null);
        String token = response.getAccessToken();
        OIDCHttpFacade httpFacade = createHttpFacade("/api/resourcea", token);
        AuthorizationContext context = policyEnforcer.enforce(httpFacade);
        Permission permission = context.getPermissions().get(0);
        Map<String, Set<String>> claims = permission.getClaims();
        Assert.assertTrue(context.isGranted());
        Assert.assertEquals("test", claims.get("resolved-claim").iterator().next());
    }

    @Test
    public void testOnDenyRedirectTo() {
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getAdapterConfiguration("enforcer-on-deny-redirect.json"));
        PolicyEnforcer policyEnforcer = deployment.getPolicyEnforcer();
        OIDCHttpFacade httpFacade = createHttpFacade("/api/resourcea");
        AuthorizationContext context = policyEnforcer.enforce(httpFacade);
        Assert.assertFalse(context.isGranted());
        PolicyEnforcerTest.TestResponse response = PolicyEnforcerTest.TestResponse.class.cast(httpFacade.getResponse());
        Assert.assertEquals(302, response.getStatus());
        List<String> location = response.getHeaders().getOrDefault("Location", Collections.emptyList());
        Assert.assertFalse(location.isEmpty());
        Assert.assertEquals("/accessDenied", location.get(0));
    }

    @Test
    public void testNotAuthenticatedDenyUnmapedPath() {
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getAdapterConfiguration("enforcer-bearer-only.json"));
        PolicyEnforcer policyEnforcer = deployment.getPolicyEnforcer();
        OIDCHttpFacade httpFacade = createHttpFacade("/api/unmmaped");
        AuthorizationContext context = policyEnforcer.enforce(httpFacade);
        Assert.assertFalse(context.isGranted());
        PolicyEnforcerTest.TestResponse response = PolicyEnforcerTest.TestResponse.class.cast(httpFacade.getResponse());
        Assert.assertEquals(403, response.getStatus());
    }

    @Test
    public void testPublicEndpointNoBearerAbortRequest() {
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getAdapterConfiguration("enforcer-bearer-only.json"));
        OIDCHttpFacade httpFacade = createHttpFacade("/api/public");
        AuthenticatedActionsHandler handler = new AuthenticatedActionsHandler(deployment, httpFacade);
        Assert.assertTrue(handler.handledRequest());
        oauth.realm(PolicyEnforcerTest.REALM_NAME);
        oauth.clientId("public-client-test");
        oauth.doLogin("marta", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, null);
        String token = response.getAccessToken();
        httpFacade = createHttpFacade("/api/resourcea", token);
        handler = new AuthenticatedActionsHandler(deployment, httpFacade);
        Assert.assertFalse(handler.handledRequest());
    }

    @Test
    public void testMappedPathEnforcementModeDisabled() {
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getAdapterConfiguration("enforcer-disabled-enforce-mode-path.json"));
        PolicyEnforcer policyEnforcer = deployment.getPolicyEnforcer();
        OIDCHttpFacade httpFacade = createHttpFacade("/api/resource/public");
        AuthorizationContext context = policyEnforcer.enforce(httpFacade);
        Assert.assertTrue(context.isGranted());
        httpFacade = createHttpFacade("/api/resourceb");
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertFalse(context.isGranted());
        PolicyEnforcerTest.TestResponse response = PolicyEnforcerTest.TestResponse.class.cast(httpFacade.getResponse());
        Assert.assertEquals(403, response.getStatus());
        oauth.realm(PolicyEnforcerTest.REALM_NAME);
        oauth.clientId("public-client-test");
        oauth.doLogin("marta", "password");
        String token = oauth.doAccessTokenRequest(oauth.getCurrentQuery().get(CODE), null).getAccessToken();
        httpFacade = createHttpFacade("/api/resourcea", token);
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertTrue(context.isGranted());
        httpFacade = createHttpFacade("/api/resourceb", token);
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertFalse(context.isGranted());
        response = PolicyEnforcerTest.TestResponse.class.cast(httpFacade.getResponse());
        Assert.assertEquals(403, response.getStatus());
        httpFacade = createHttpFacade("/api/resource/public", token);
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertTrue(context.isGranted());
    }

    @Test
    public void testDefaultWWWAuthenticateCorsHeader() {
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getAdapterConfiguration("enforcer-disabled-enforce-mode-path.json"));
        deployment.setCors(true);
        Map<String, List<String>> headers = new HashMap<>();
        headers.put(ORIGIN, Arrays.asList("http://localhost:8180"));
        oauth.realm(PolicyEnforcerTest.REALM_NAME);
        oauth.clientId("public-client-test");
        oauth.doLogin("marta", "password");
        String token = oauth.doAccessTokenRequest(oauth.getCurrentQuery().get(CODE), null).getAccessToken();
        OIDCHttpFacade httpFacade = createHttpFacade("http://server/api/resource/public", OPTIONS, token, headers, Collections.emptyMap(), null, deployment);
        handledRequest();
        Assert.assertEquals(WWW_AUTHENTICATE, headers.get(ACCESS_CONTROL_EXPOSE_HEADERS).get(0));
    }

    @Test
    public void testMatchHttpVerbsToScopes() {
        ClientResource clientResource = getClientResource(PolicyEnforcerTest.RESOURCE_SERVER_CLIENT_ID);
        ResourceRepresentation resource = createResource(clientResource, "Resource With HTTP Scopes", "/api/resource-with-scope");
        ResourcePermissionRepresentation permission = new ResourcePermissionRepresentation();
        permission.setName(((resource.getName()) + " Permission"));
        permission.addResource(resource.getName());
        permission.addPolicy("Always Grant Policy");
        PermissionsResource permissions = clientResource.authorization().permissions();
        permissions.resource().create(permission).close();
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getAdapterConfiguration("enforcer-match-http-verbs-scopes.json"));
        PolicyEnforcer policyEnforcer = deployment.getPolicyEnforcer();
        oauth.realm(PolicyEnforcerTest.REALM_NAME);
        oauth.clientId("public-client-test");
        oauth.doLogin("marta", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, null);
        String token = response.getAccessToken();
        OIDCHttpFacade httpFacade = createHttpFacade("/api/resource-with-scope", token);
        try {
            policyEnforcer.enforce(httpFacade);
            Assert.fail("Should fail because resource does not have any scope named GET");
        } catch (Exception ignore) {
            Assert.assertTrue(ignore.getCause().getMessage().contains("One of the given scopes [GET] is invalid"));
        }
        resource.addScope("GET", "POST");
        clientResource.authorization().resources().resource(resource.getId()).update(resource);
        deployment = KeycloakDeploymentBuilder.build(getAdapterConfiguration("enforcer-match-http-verbs-scopes.json"));
        policyEnforcer = deployment.getPolicyEnforcer();
        AuthorizationContext context = policyEnforcer.enforce(httpFacade);
        Assert.assertTrue(context.isGranted());
        httpFacade = createHttpFacade("/api/resource-with-scope", token, "POST");
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertTrue(context.isGranted());
        ScopePermissionRepresentation postPermission = new ScopePermissionRepresentation();
        postPermission.setName("GET permission");
        postPermission.addScope("GET");
        postPermission.addPolicy("Always Deny Policy");
        permissions.scope().create(postPermission).close();
        httpFacade = createHttpFacade("/api/resource-with-scope", token);
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertFalse(context.isGranted());
        postPermission = permissions.scope().findByName(postPermission.getName());
        postPermission.addScope("GET");
        postPermission.addPolicy("Always Grant Policy");
        permissions.scope().findById(postPermission.getId()).update(postPermission);
        AuthzClient authzClient = getAuthzClient("default-keycloak.json");
        AuthorizationResponse authorize = authzClient.authorization(token).authorize();
        token = authorize.getToken();
        httpFacade = createHttpFacade("/api/resource-with-scope", token);
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertTrue(context.isGranted());
        httpFacade = createHttpFacade("/api/resource-with-scope", token, "POST");
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertTrue(context.isGranted());
        postPermission = permissions.scope().findByName(postPermission.getName());
        postPermission.addScope("GET");
        postPermission.addPolicy("Always Deny Policy");
        permissions.scope().findById(postPermission.getId()).update(postPermission);
        authorize = authzClient.authorization(token).authorize();
        token = authorize.getToken();
        httpFacade = createHttpFacade("/api/resource-with-scope", token);
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertFalse(context.isGranted());
        httpFacade = createHttpFacade("/api/resource-with-scope", token, "POST");
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertTrue(context.isGranted());
        postPermission = permissions.scope().findByName(postPermission.getName());
        postPermission.addScope("GET");
        postPermission.addPolicy("Always Grant Policy");
        permissions.scope().findById(postPermission.getId()).update(postPermission);
        authorize = authzClient.authorization(token).authorize();
        token = authorize.getToken();
        httpFacade = createHttpFacade("/api/resource-with-scope", token);
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertTrue(context.isGranted());
        httpFacade = createHttpFacade("/api/resource-with-scope", token, "POST");
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertTrue(context.isGranted());
        postPermission = permissions.scope().findByName(postPermission.getName());
        postPermission.addScope("POST");
        postPermission.addPolicy("Always Deny Policy");
        permissions.scope().findById(postPermission.getId()).update(postPermission);
        AuthorizationRequest request = new AuthorizationRequest();
        request.addPermission(null, "GET");
        authorize = authzClient.authorization(token).authorize(request);
        token = authorize.getToken();
        httpFacade = createHttpFacade("/api/resource-with-scope", token);
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertTrue(context.isGranted());
        httpFacade = createHttpFacade("/api/resource-with-scope", token, "POST");
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertFalse(context.isGranted());
    }

    @Test
    public void testUsingSubjectToken() {
        ClientResource clientResource = getClientResource(PolicyEnforcerTest.RESOURCE_SERVER_CLIENT_ID);
        ResourceRepresentation resource = createResource(clientResource, "Resource Subject Token", "/api/check-subject-token");
        ResourcePermissionRepresentation permission = new ResourcePermissionRepresentation();
        permission.setName(((resource.getName()) + " Permission"));
        permission.addResource(resource.getName());
        permission.addPolicy("Only User Policy");
        PermissionsResource permissions = clientResource.authorization().permissions();
        permissions.resource().create(permission).close();
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getAdapterConfiguration("enforcer-bearer-only.json"));
        PolicyEnforcer policyEnforcer = deployment.getPolicyEnforcer();
        OIDCHttpFacade httpFacade = createHttpFacade("/api/check-subject-token");
        AuthorizationContext context = policyEnforcer.enforce(httpFacade);
        Assert.assertFalse(context.isGranted());
        Assert.assertEquals(403, PolicyEnforcerTest.TestResponse.class.cast(httpFacade.getResponse()).getStatus());
        oauth.realm(PolicyEnforcerTest.REALM_NAME);
        oauth.clientId("public-client-test");
        oauth.doLogin("marta", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, null);
        String token = response.getAccessToken();
        httpFacade = createHttpFacade("/api/check-subject-token", token);
        context = policyEnforcer.enforce(httpFacade);
        Assert.assertTrue(context.isGranted());
    }

    private class TestResponse implements Response {
        private final Map<String, List<String>> headers;

        private int status;

        public TestResponse(Map<String, List<String>> headers) {
            this.headers = headers;
        }

        @Override
        public void setStatus(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        @Override
        public void addHeader(String name, String value) {
            setHeader(name, value);
        }

        @Override
        public void setHeader(String name, String value) {
            headers.put(name, Arrays.asList(value));
        }

        public Map<String, List<String>> getHeaders() {
            return headers;
        }

        @Override
        public void resetCookie(String name, String path) {
        }

        @Override
        public void setCookie(String name, String value, String path, String domain, int maxAge, boolean secure, boolean httpOnly) {
        }

        @Override
        public OutputStream getOutputStream() {
            return null;
        }

        @Override
        public void sendError(int code) {
            status = code;
        }

        @Override
        public void sendError(int code, String message) {
            status = code;
        }

        @Override
        public void end() {
        }
    }
}

