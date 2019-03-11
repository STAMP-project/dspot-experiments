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
package org.keycloak.testsuite.forms;


import AuthenticationFlowBindings.BROWSER_BINDING;
import AuthenticationFlowBindings.DIRECT_GRANT_BINDING;
import Details.USERNAME;
import HttpHeaders.AUTHORIZATION;
import HttpHeaders.LOCATION;
import HttpHeaders.WWW_AUTHENTICATE;
import OAuth2Constants.AUTHORIZATION_CODE;
import OAuth2Constants.CLIENT_ID;
import OAuth2Constants.CODE;
import OAuth2Constants.GRANT_TYPE;
import OAuth2Constants.PASSWORD;
import OAuth2Constants.REDIRECT_URI;
import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.models.utils.TimeBasedOTP;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.OAuthClient;
import org.keycloak.testsuite.arquillian.annotation.UncaughtServerErrorExpected;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.util.UserBuilder;
import org.keycloak.util.BasicAuthHelper;
import org.openqa.selenium.By;


/**
 * Test that clients can override auth flows
 *
 * @author <a href="mailto:bburke@redhat.com">Bill Burke</a>
 */
public class FlowOverrideTest extends AbstractTestRealmKeycloakTest {
    public static final String TEST_APP_DIRECT_OVERRIDE = "test-app-direct-override";

    public static final String TEST_APP_FLOW = "test-app-flow";

    public static final String TEST_APP_HTTP_CHALLENGE = "http-challenge-client";

    public static final String TEST_APP_HTTP_CHALLENGE_OTP = "http-challenge-otp-client";

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected ErrorPage errorPage;

    private TimeBasedOTP totp = new TimeBasedOTP();

    @Test
    public void testWithClientBrowserOverride() throws Exception {
        oauth.clientId(FlowOverrideTest.TEST_APP_FLOW);
        String loginFormUrl = oauth.getLoginFormUrl();
        log.info(("loginFormUrl: " + loginFormUrl));
        // Thread.sleep(10000000);
        driver.navigate().to(loginFormUrl);
        Assert.assertEquals("PushTheButton", driver.getTitle());
        // Push the button. I am redirected to username+password form
        driver.findElement(By.name("submit1")).click();
        loginPage.assertCurrent();
        // Fill username+password. I am successfully authenticated
        oauth.fillLoginForm("test-user@localhost", "password");
        appPage.assertCurrent();
        events.expectLogin().client("test-app-flow").detail(USERNAME, "test-user@localhost").assertEvent();
    }

    @Test
    public void testNoOverrideBrowser() throws Exception {
        String clientId = "test-app";
        testNoOverrideBrowser(clientId);
    }

    @Test
    public void testGrantAccessTokenNoOverride() throws Exception {
        testDirectGrantNoOverride("test-app");
    }

    @Test
    public void testGrantAccessTokenWithClientOverride() throws Exception {
        String clientId = FlowOverrideTest.TEST_APP_DIRECT_OVERRIDE;
        Client httpClient = ClientBuilder.newClient();
        String grantUri = oauth.getResourceOwnerPasswordCredentialGrantUrl();
        WebTarget grantTarget = httpClient.target(grantUri);
        {
            // test no password
            String header = BasicAuthHelper.createHeader(clientId, "password");
            Form form = new Form();
            form.param(GRANT_TYPE, PASSWORD);
            form.param("username", "test-user@localhost");
            Response response = grantTarget.request().header(AUTHORIZATION, header).post(Entity.form(form));
            Assert.assertEquals(200, response.getStatus());
            response.close();
        }
        httpClient.close();
        events.clear();
    }

    @Test
    public void testClientOverrideFlowUsingDirectGrantHttpChallenge() {
        Client httpClient = ClientBuilder.newClient();
        String grantUri = oauth.getResourceOwnerPasswordCredentialGrantUrl();
        WebTarget grantTarget = httpClient.target(grantUri);
        // no username/password
        Form form = new Form();
        form.param(GRANT_TYPE, PASSWORD);
        form.param(CLIENT_ID, FlowOverrideTest.TEST_APP_HTTP_CHALLENGE);
        Response response = grantTarget.request().post(Entity.form(form));
        Assert.assertEquals("Basic realm=\"test\"", response.getHeaderString(WWW_AUTHENTICATE));
        Assert.assertEquals(401, response.getStatus());
        response.close();
        // now, username password using basic challenge response
        response = grantTarget.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("test-user@localhost", "password")).post(Entity.form(form));
        Assert.assertEquals(200, response.getStatus());
        response.close();
        httpClient.close();
        events.clear();
    }

    @Test
    public void testDirectGrantHttpChallengeOTP() {
        UserRepresentation user = adminClient.realm("test").users().search("test-user@localhost").get(0);
        UserRepresentation userUpdated = UserBuilder.edit(user).totpSecret("totpSecret").otpEnabled().build();
        adminClient.realm("test").users().get(user.getId()).update(userUpdated);
        setupBruteForce();
        Client httpClient = ClientBuilder.newClient();
        String grantUri = oauth.getResourceOwnerPasswordCredentialGrantUrl();
        WebTarget grantTarget = httpClient.target(grantUri);
        Form form = new Form();
        form.param(GRANT_TYPE, PASSWORD);
        form.param(CLIENT_ID, FlowOverrideTest.TEST_APP_HTTP_CHALLENGE_OTP);
        // correct password + totp
        String totpCode = totp.generateTOTP("totpSecret");
        Response response = grantTarget.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("test-user@localhost", ("password" + totpCode))).post(Entity.form(form));
        Assert.assertEquals(200, response.getStatus());
        response.close();
        // correct password + wrong totp 2x
        response = grantTarget.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("test-user@localhost", "password123456")).post(Entity.form(form));
        Assert.assertEquals(401, response.getStatus());
        response = grantTarget.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("test-user@localhost", "password123456")).post(Entity.form(form));
        Assert.assertEquals(401, response.getStatus());
        // correct password + totp but user is temporarily locked
        totpCode = totp.generateTOTP("totpSecret");
        response = grantTarget.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("test-user@localhost", ("password" + totpCode))).post(Entity.form(form));
        Assert.assertEquals(401, response.getStatus());
        response.close();
        clearBruteForce();
        adminClient.realm("test").users().get(user.getId()).removeTotp();
    }

    @Test
    public void testDirectGrantHttpChallengeUserDisabled() {
        setupBruteForce();
        Client httpClient = ClientBuilder.newClient();
        String grantUri = oauth.getResourceOwnerPasswordCredentialGrantUrl();
        WebTarget grantTarget = httpClient.target(grantUri);
        Form form = new Form();
        form.param(GRANT_TYPE, PASSWORD);
        form.param(CLIENT_ID, FlowOverrideTest.TEST_APP_HTTP_CHALLENGE);
        UserRepresentation user = adminClient.realm("test").users().search("test-user@localhost").get(0);
        user.setEnabled(false);
        adminClient.realm("test").users().get(user.getId()).update(user);
        // user disabled
        Response response = grantTarget.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("test-user@localhost", "password")).post(Entity.form(form));
        Assert.assertEquals(401, response.getStatus());
        Assert.assertEquals("Unauthorized", response.getStatusInfo().getReasonPhrase());
        response.close();
        user.setEnabled(true);
        adminClient.realm("test").users().get(user.getId()).update(user);
        // lock the user account
        grantTarget.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("test-user@localhost", "wrongpassword")).post(Entity.form(form));
        grantTarget.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("test-user@localhost", "wrongpassword")).post(Entity.form(form));
        // user is temporarily disabled
        response = grantTarget.request().header(AUTHORIZATION, BasicAuthHelper.createHeader("test-user@localhost", "password")).post(Entity.form(form));
        Assert.assertEquals(401, response.getStatus());
        Assert.assertEquals("Unauthorized", response.getStatusInfo().getReasonPhrase());
        response.close();
        clearBruteForce();
        httpClient.close();
        events.clear();
    }

    @Test
    public void testClientOverrideFlowUsingBrowserHttpChallenge() {
        Client httpClient = ClientBuilder.newClient();
        oauth.clientId(FlowOverrideTest.TEST_APP_HTTP_CHALLENGE);
        String grantUri = oauth.getLoginFormUrl();
        WebTarget grantTarget = httpClient.target(grantUri);
        Response response = grantTarget.request().get();
        Assert.assertEquals(302, response.getStatus());
        String location = response.getHeaderString(LOCATION);
        response.close();
        // first challenge
        response = httpClient.target(location).request().get();
        Assert.assertEquals("Basic realm=\"test\"", response.getHeaderString(WWW_AUTHENTICATE));
        Assert.assertEquals(401, response.getStatus());
        response.close();
        // now, username password using basic challenge response
        response = httpClient.target(location).request().header(AUTHORIZATION, BasicAuthHelper.createHeader("test-user@localhost", "password")).post(Entity.form(new Form()));
        Assert.assertEquals(302, response.getStatus());
        location = response.getHeaderString(LOCATION);
        response.close();
        Form form = new Form();
        form.param(GRANT_TYPE, AUTHORIZATION_CODE);
        form.param(CLIENT_ID, FlowOverrideTest.TEST_APP_HTTP_CHALLENGE);
        form.param(REDIRECT_URI, oauth.APP_AUTH_ROOT);
        form.param(CODE, location.substring((((location.indexOf(CODE)) + (CODE.length())) + 1)));
        // exchange code to token
        response = httpClient.target(oauth.getAccessTokenUrl()).request().post(Entity.form(form));
        Assert.assertEquals(200, response.getStatus());
        response.close();
        httpClient.close();
        events.clear();
    }

    @Test
    public void testRestInterface() throws Exception {
        ClientsResource clients = adminClient.realm("test").clients();
        List<ClientRepresentation> query = clients.findByClientId(FlowOverrideTest.TEST_APP_DIRECT_OVERRIDE);
        ClientRepresentation clientRep = query.get(0);
        String directGrantFlowId = clientRep.getAuthenticationFlowBindingOverrides().get(DIRECT_GRANT_BINDING);
        Assert.assertNotNull(directGrantFlowId);
        clientRep.getAuthenticationFlowBindingOverrides().put(DIRECT_GRANT_BINDING, "");
        clients.get(clientRep.getId()).update(clientRep);
        testDirectGrantNoOverride(FlowOverrideTest.TEST_APP_DIRECT_OVERRIDE);
        clientRep.getAuthenticationFlowBindingOverrides().put(DIRECT_GRANT_BINDING, directGrantFlowId);
        clients.get(clientRep.getId()).update(clientRep);
        testGrantAccessTokenWithClientOverride();
        query = clients.findByClientId(FlowOverrideTest.TEST_APP_FLOW);
        clientRep = query.get(0);
        String browserFlowId = clientRep.getAuthenticationFlowBindingOverrides().get(BROWSER_BINDING);
        Assert.assertNotNull(browserFlowId);
        clientRep.getAuthenticationFlowBindingOverrides().put(BROWSER_BINDING, "");
        clients.get(clientRep.getId()).update(clientRep);
        testNoOverrideBrowser(FlowOverrideTest.TEST_APP_FLOW);
        clientRep.getAuthenticationFlowBindingOverrides().put(BROWSER_BINDING, browserFlowId);
        clients.get(clientRep.getId()).update(clientRep);
        testWithClientBrowserOverride();
    }

    @Test
    @UncaughtServerErrorExpected
    public void testRestInterfaceWithBadId() throws Exception {
        ClientsResource clients = adminClient.realm("test").clients();
        List<ClientRepresentation> query = clients.findByClientId(FlowOverrideTest.TEST_APP_FLOW);
        ClientRepresentation clientRep = query.get(0);
        String browserFlowId = clientRep.getAuthenticationFlowBindingOverrides().get(BROWSER_BINDING);
        clientRep.getAuthenticationFlowBindingOverrides().put(BROWSER_BINDING, "bad-id");
        try {
            clients.get(clientRep.getId()).update(clientRep);
            Assert.fail();
        } catch (Exception e) {
        }
        query = clients.findByClientId(FlowOverrideTest.TEST_APP_FLOW);
        clientRep = query.get(0);
        Assert.assertEquals(browserFlowId, clientRep.getAuthenticationFlowBindingOverrides().get(BROWSER_BINDING));
    }
}

