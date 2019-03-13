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


import HttpHeaders.LOCATION;
import HttpHeaders.WWW_AUTHENTICATE;
import OAuthClient.AccessTokenResponse;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.util.OAuthClient;


/**
 * Test that clients can override auth flows
 *
 * @author <a href="mailto:bburke@redhat.com">Bill Burke</a>
 */
public class ChallengeFlowTest extends AbstractTestRealmKeycloakTest {
    public static final String TEST_APP_DIRECT_OVERRIDE = "test-app-direct-override";

    public static final String TEST_APP_FLOW = "test-app-flow";

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected ErrorPage errorPage;

    @Test
    public void testChallengeFlow() throws Exception {
        oauth.clientId(ChallengeFlowTest.TEST_APP_FLOW);
        String loginFormUrl = oauth.getLoginFormUrl();
        Client client = ClientBuilder.newClient();
        WebTarget loginTarget = client.target(loginFormUrl);
        Response response = loginTarget.request().get();
        Assert.assertEquals(401, response.getStatus());
        String authenticateHeader = response.getHeaderString(WWW_AUTHENTICATE);
        Assert.assertNotNull(authenticateHeader);
        // System.out.println(authenticateHeader);
        String splash = response.readEntity(String.class);
        // System.out.println(splash);
        response.close();
        // respin Client to make absolutely sure no cookie caching.  need to test that it works with null auth_session_id cookie.
        client.close();
        client = ClientBuilder.newClient();
        authenticateHeader = authenticateHeader.trim();
        Pattern callbackPattern = Pattern.compile("callback\\s*=\\s*\"([^\"]+)\"");
        Pattern paramPattern = Pattern.compile("param=\"([^\"]+)\"\\s+label=\"([^\"]+)\"");
        Matcher m = callbackPattern.matcher(authenticateHeader);
        String callback = null;
        if (m.find()) {
            callback = m.group(1);
            // System.out.println("------");
            // System.out.println("callback:");
            // System.out.println("    " + callback);
        }
        m = paramPattern.matcher(authenticateHeader);
        List<String> params = new LinkedList<>();
        List<String> labels = new LinkedList<>();
        while (m.find()) {
            String param = m.group(1);
            String label = m.group(2);
            params.add(param);
            labels.add(label);
            // System.out.println("------");
            // System.out.println("param:" + param);
            // System.out.println("label:" + label);
        } 
        Assert.assertEquals("username", params.get(0));
        Assert.assertEquals("Username:", labels.get(0).trim());
        Assert.assertEquals("password", params.get(1));
        Assert.assertEquals("Password:", labels.get(1).trim());
        Form form = new Form();
        form.param("username", "test-user@localhost");
        form.param("password", "password");
        response = client.target(callback).request().post(Entity.form(form));
        Assert.assertEquals(302, response.getStatus());
        String redirect = response.getHeaderString(LOCATION);
        System.out.println("------");
        System.out.println(redirect);
        Pattern codePattern = Pattern.compile("code=([^&]+)");
        m = codePattern.matcher(redirect);
        Assert.assertTrue(m.find());
        String code = m.group(1);
        OAuthClient.AccessTokenResponse oauthResponse = oauth.doAccessTokenRequest(code, "password");
        Assert.assertNotNull(oauthResponse.getAccessToken());
        client.close();
    }
}

