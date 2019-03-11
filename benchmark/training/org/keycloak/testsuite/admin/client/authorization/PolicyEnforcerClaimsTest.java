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


import OAuth2Constants.CODE;
import OAuthClient.AccessTokenResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.AuthorizationContext;
import org.keycloak.adapters.KeycloakDeployment;
import org.keycloak.adapters.KeycloakDeploymentBuilder;
import org.keycloak.adapters.authorization.PolicyEnforcer;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.representations.idm.authorization.AuthorizationRequest;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.keycloak.representations.idm.authorization.Permission;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.OAuthClient;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class PolicyEnforcerClaimsTest extends AbstractKeycloakTest {
    protected static final String REALM_NAME = "authz-test";

    @Test
    public void testEnforceUMAAccessWithClaimsUsingBearerToken() {
        initAuthorizationSettings(getClientResource("resource-server-uma-test"));
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getAdapterConfiguration("enforcer-uma-claims-test.json"));
        PolicyEnforcer policyEnforcer = deployment.getPolicyEnforcer();
        HashMap<String, List<String>> headers = new HashMap<>();
        HashMap<String, List<String>> parameters = new HashMap<>();
        parameters.put("withdrawal.amount", Arrays.asList("50"));
        AuthzClient authzClient = getAuthzClient("enforcer-uma-claims-test.json");
        String token = authzClient.obtainAccessToken("marta", "password").getToken();
        headers.put("Authorization", Arrays.asList(("Bearer " + token)));
        AuthorizationContext context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", "POST", token, headers, parameters));
        Assert.assertFalse(context.isGranted());
        AuthorizationRequest request = new AuthorizationRequest();
        request.setTicket(extractTicket(headers));
        AuthorizationResponse response = authzClient.authorization("marta", "password").authorize(request);
        token = response.getToken();
        Assert.assertNotNull(token);
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", "POST", token, headers, parameters));
        Assert.assertTrue(context.isGranted());
        parameters.put("withdrawal.amount", Arrays.asList("200"));
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", "POST", token, headers, parameters));
        Assert.assertFalse(context.isGranted());
        parameters.put("withdrawal.amount", Arrays.asList("50"));
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", "POST", token, headers, parameters));
        Assert.assertTrue(context.isGranted());
        parameters.put("withdrawal.amount", Arrays.asList("10"));
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", "POST", token, headers, parameters));
        request = new AuthorizationRequest();
        request.setTicket(extractTicket(headers));
        response = authzClient.authorization("marta", "password").authorize(request);
        token = response.getToken();
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", "POST", token, headers, parameters));
        Assert.assertTrue(context.isGranted());
        request = new AuthorizationRequest();
        request.setTicket(extractTicket(headers));
        response = authzClient.authorization("marta", "password").authorize(request);
        token = response.getToken();
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", "GET", token, headers, parameters));
        Assert.assertTrue(context.isGranted());
        Assert.assertEquals(1, context.getPermissions().size());
        Permission permission = context.getPermissions().get(0);
        Assert.assertEquals(parameters.get("withdrawal.amount").get(0), permission.getClaims().get("withdrawal.amount").iterator().next());
    }

    @Test
    public void testEnforceEntitlementAccessWithClaimsWithoutBearerToken() {
        initAuthorizationSettings(getClientResource("resource-server-test"));
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getAdapterConfiguration("enforcer-entitlement-claims-test.json"));
        PolicyEnforcer policyEnforcer = deployment.getPolicyEnforcer();
        HashMap<String, List<String>> headers = new HashMap<>();
        HashMap<String, List<String>> parameters = new HashMap<>();
        AuthzClient authzClient = getAuthzClient("enforcer-entitlement-claims-test.json");
        String token = authzClient.obtainAccessToken("marta", "password").getToken();
        AuthorizationContext context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", token, headers, parameters));
        Assert.assertFalse(context.isGranted());
        parameters.put("withdrawal.amount", Arrays.asList("50"));
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", token, headers, parameters));
        Assert.assertTrue(context.isGranted());
        Assert.assertEquals(1, context.getPermissions().size());
        Permission permission = context.getPermissions().get(0);
        Assert.assertEquals(parameters.get("withdrawal.amount").get(0), permission.getClaims().get("withdrawal.amount").iterator().next());
        parameters.put("withdrawal.amount", Arrays.asList("200"));
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", token, headers, parameters));
        Assert.assertFalse(context.isGranted());
        parameters.put("withdrawal.amount", Arrays.asList("50"));
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", token, headers, parameters));
        Assert.assertTrue(context.isGranted());
        parameters.put("withdrawal.amount", Arrays.asList("10"));
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", token, headers, parameters));
        Assert.assertTrue(context.isGranted());
        Assert.assertEquals(1, context.getPermissions().size());
        permission = context.getPermissions().get(0);
        Assert.assertEquals(parameters.get("withdrawal.amount").get(0), permission.getClaims().get("withdrawal.amount").iterator().next());
    }

    @Test
    public void testEnforceEntitlementAccessWithClaimsWithBearerToken() {
        initAuthorizationSettings(getClientResource("resource-server-test"));
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getAdapterConfiguration("enforcer-entitlement-claims-test.json"));
        PolicyEnforcer policyEnforcer = deployment.getPolicyEnforcer();
        HashMap<String, List<String>> headers = new HashMap<>();
        HashMap<String, List<String>> parameters = new HashMap<>();
        AuthzClient authzClient = getAuthzClient("enforcer-entitlement-claims-test.json");
        String token = authzClient.obtainAccessToken("marta", "password").getToken();
        headers.put("Authorization", Arrays.asList(("Bearer " + token)));
        AuthorizationContext context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", token, headers, parameters));
        Assert.assertFalse(context.isGranted());
        parameters.put("withdrawal.amount", Arrays.asList("50"));
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", token, headers, parameters));
        Assert.assertTrue(context.isGranted());
        parameters.put("withdrawal.amount", Arrays.asList("200"));
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", token, headers, parameters));
        Assert.assertFalse(context.isGranted());
        parameters.put("withdrawal.amount", Arrays.asList("50"));
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", token, headers, parameters));
        Assert.assertTrue(context.isGranted());
        parameters.put("withdrawal.amount", Arrays.asList("10"));
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", token, headers, parameters));
        Assert.assertTrue(context.isGranted());
    }

    @Test
    public void testEnforceEntitlementAccessWithClaimsWithBearerTokenFromPublicClient() {
        initAuthorizationSettings(getClientResource("resource-server-test"));
        KeycloakDeployment deployment = KeycloakDeploymentBuilder.build(getAdapterConfiguration("enforcer-entitlement-claims-test.json"));
        PolicyEnforcer policyEnforcer = deployment.getPolicyEnforcer();
        HashMap<String, List<String>> headers = new HashMap<>();
        HashMap<String, List<String>> parameters = new HashMap<>();
        oauth.realm(PolicyEnforcerClaimsTest.REALM_NAME);
        oauth.clientId("public-client-test");
        oauth.doLogin("marta", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, null);
        String token = response.getAccessToken();
        headers.put("Authorization", Arrays.asList(("Bearer " + token)));
        AuthorizationContext context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", token, headers, parameters));
        Assert.assertFalse(context.isGranted());
        parameters.put("withdrawal.amount", Arrays.asList("50"));
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", token, headers, parameters));
        Assert.assertTrue(context.isGranted());
        parameters.put("withdrawal.amount", Arrays.asList("200"));
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", token, headers, parameters));
        Assert.assertFalse(context.isGranted());
        parameters.put("withdrawal.amount", Arrays.asList("50"));
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", token, headers, parameters));
        Assert.assertTrue(context.isGranted());
        parameters.put("withdrawal.amount", Arrays.asList("10"));
        context = policyEnforcer.enforce(createHttpFacade("/api/bank/account/1/withdrawal", token, headers, parameters));
        Assert.assertTrue(context.isGranted());
    }
}

