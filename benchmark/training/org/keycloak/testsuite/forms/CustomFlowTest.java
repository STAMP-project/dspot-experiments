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
package org.keycloak.testsuite.forms;


import AuthenticationExecutionModel.Requirement.REQUIRED;
import ClickThroughAuthenticator.PROVIDER_ID;
import Details.CODE_ID;
import Details.CONSENT;
import Details.REDIRECT_URI;
import Details.USERNAME;
import Errors.INVALID_CLIENT_CREDENTIALS;
import OAuth2Constants.CODE;
import OAuthClient.AccessTokenResponse;
import RequestType.AUTH_RESPONSE;
import Response.Status.CREATED;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.AuthenticationManagementResource;
import org.keycloak.representations.idm.AuthenticationExecutionRepresentation;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.LoginPasswordUpdatePage;
import org.keycloak.testsuite.pages.RegisterPage;
import org.keycloak.testsuite.pages.TermsAndConditionsPage;
import org.keycloak.testsuite.rest.representation.AuthenticatorState;
import org.keycloak.testsuite.util.ExecutionBuilder;
import org.keycloak.testsuite.util.Matchers;
import org.keycloak.testsuite.util.OAuthClient;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class CustomFlowTest extends AbstractFlowTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected ErrorPage errorPage;

    @Page
    protected TermsAndConditionsPage termsPage;

    @Page
    protected LoginPasswordUpdatePage updatePasswordPage;

    @Page
    protected RegisterPage registerPage;

    private static String userId;

    /**
     * KEYCLOAK-3506
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRequiredAfterAlternative() throws Exception {
        AuthenticationManagementResource authMgmtResource = testRealm().flows();
        Map<String, String> params = new HashMap();
        String flowAlias = "Browser Flow With Extra";
        params.put("newName", flowAlias);
        Response response = authMgmtResource.copy("browser", params);
        String flowId = null;
        try {
            Assert.assertThat("Copy flow", response, Matchers.statusCodeIs(CREATED));
            AuthenticationFlowRepresentation newFlow = findFlowByAlias(flowAlias);
            flowId = newFlow.getId();
        } finally {
            response.close();
        }
        AuthenticationExecutionRepresentation execution = ExecutionBuilder.create().parentFlow(flowId).requirement(REQUIRED.toString()).authenticator(PROVIDER_ID).priority(10).authenticatorFlow(false).build();
        testRealm().flows().addExecution(execution);
        RealmRepresentation rep = testRealm().toRepresentation();
        rep.setBrowserFlow(flowAlias);
        testRealm().update(rep);
        rep = testRealm().toRepresentation();
        Assert.assertEquals(flowAlias, rep.getBrowserFlow());
        loginPage.open();
        String url = driver.getCurrentUrl();
        // test to make sure we aren't skipping anything
        loginPage.login("test-user@localhost", "bad-password");
        Assert.assertTrue(loginPage.isCurrent());
        loginPage.login("test-user@localhost", "password");
        Assert.assertTrue(termsPage.isCurrent());
        // Revert dummy flow
        rep.setBrowserFlow("dummy");
        testRealm().update(rep);
    }

    @Test
    public void loginSuccess() {
        AuthenticatorState state = new AuthenticatorState();
        state.setUsername("login-test");
        state.setClientId("test-app");
        testingClient.testing().updateAuthenticator(state);
        oauth.openLoginForm();
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        events.expectLogin().user(CustomFlowTest.userId).detail(USERNAME, "login-test").assertEvent();
    }

    @Test
    public void grantTest() throws Exception {
        AuthenticatorState state = new AuthenticatorState();
        state.setUsername("login-test");
        state.setClientId("test-app");
        testingClient.testing().updateAuthenticator(state);
        grantAccessToken("test-app", "login-test");
    }

    @Test
    public void clientAuthTest() throws Exception {
        AuthenticatorState state = new AuthenticatorState();
        state.setClientId("dummy-client");
        state.setUsername("login-test");
        testingClient.testing().updateAuthenticator(state);
        grantAccessToken("dummy-client", "login-test");
        state.setClientId("test-app");
        testingClient.testing().updateAuthenticator(state);
        grantAccessToken("test-app", "login-test");
        state.setClientId("unknown");
        testingClient.testing().updateAuthenticator(state);
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("password", "test-user", "password");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("unauthorized_client", response.getError());
        events.expectLogin().client(((String) (null))).user(((String) (null))).session(((String) (null))).removeDetail(CODE_ID).removeDetail(REDIRECT_URI).removeDetail(CONSENT).error(INVALID_CLIENT_CREDENTIALS).assertEvent();
        state.setClientId("test-app");
        testingClient.testing().updateAuthenticator(state);
    }
}

