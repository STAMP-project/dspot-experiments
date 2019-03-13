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
package org.keycloak.testsuite.oidc;


import AppPage.RequestType.AUTH_RESPONSE;
import Details.USERNAME;
import OAuthClient.AuthorizationEndpointResponse;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.protocol.oidc.OIDCAdvancedConfigWrapper;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.pages.AccountUpdateProfilePage;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.OAuthGrantPage;
import org.keycloak.testsuite.util.OAuthClient;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class OIDCBackwardsCompatibilityTest extends AbstractTestRealmKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected AccountUpdateProfilePage profilePage;

    @Page
    protected OAuthGrantPage grantPage;

    @Page
    protected ErrorPage errorPage;

    // KEYCLOAK-6286
    @Test
    public void testExcludeSessionStateParameter() {
        // Open login form and login successfully. Assert session_state is present
        OAuthClient.AuthorizationEndpointResponse authzResponse = oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        org.keycloak.testsuite.Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        org.keycloak.testsuite.Assert.assertNotNull(authzResponse.getSessionState());
        // Switch "exclude session_state" to on
        ClientResource client = ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app");
        ClientRepresentation clientRep = client.toRepresentation();
        OIDCAdvancedConfigWrapper config = OIDCAdvancedConfigWrapper.fromClientRepresentation(clientRep);
        config.setExcludeSessionStateFromAuthResponse(true);
        client.update(clientRep);
        // Open login again and assert session_state not present
        driver.navigate().to(oauth.getLoginFormUrl());
        org.keycloak.testsuite.Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        loginEvent = events.expectLogin().detail(USERNAME, "test-user@localhost").assertEvent();
        authzResponse = new OAuthClient.AuthorizationEndpointResponse(oauth);
        org.keycloak.testsuite.Assert.assertNull(authzResponse.getSessionState());
        // Revert
        config.setExcludeSessionStateFromAuthResponse(false);
        client.update(clientRep);
    }
}

