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
package org.keycloak.testsuite.oidc.flows;


import OAuthClient.AuthorizationEndpointResponse;
import OAuthErrorException.INVALID_REQUEST;
import java.io.IOException;
import java.util.List;
import javax.ws.rs.core.UriBuilder;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.TokenSignatureUtil;


/**
 * Abstract test for various values of response_type
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public abstract class AbstractOIDCResponseTypeTest extends AbstractTestRealmKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Test
    public void nonceAndSessionStateMatches() {
        EventRepresentation loginEvent = loginUser("abcdef123456");
        OAuthClient.AuthorizationEndpointResponse authzResponse = new OAuthClient.AuthorizationEndpointResponse(oauth, isFragment());
        org.keycloak.testsuite.Assert.assertNotNull(authzResponse.getSessionState());
        List<IDToken> idTokens = testAuthzResponseAndRetrieveIDTokens(authzResponse, loginEvent);
        for (IDToken idToken : idTokens) {
            org.keycloak.testsuite.Assert.assertEquals("abcdef123456", idToken.getNonce());
            org.keycloak.testsuite.Assert.assertEquals(authzResponse.getSessionState(), idToken.getSessionState());
        }
    }

    @Test
    public void initialSessionStateUsedInRedirect() {
        EventRepresentation loginEvent = loginUserWithRedirect("abcdef123456", ((OAuthClient.APP_ROOT) + "/auth?session_state=foo"));
        OAuthClient.AuthorizationEndpointResponse authzResponse = new OAuthClient.AuthorizationEndpointResponse(oauth, isFragment());
        org.keycloak.testsuite.Assert.assertNotNull(authzResponse.getSessionState());
        List<IDToken> idTokens = testAuthzResponseAndRetrieveIDTokens(authzResponse, loginEvent);
        for (IDToken idToken : idTokens) {
            org.keycloak.testsuite.Assert.assertEquals(authzResponse.getSessionState(), idToken.getSessionState());
        }
    }

    @Test
    public void authorizationRequestMissingResponseType() throws IOException {
        oauth.responseType(null);
        UriBuilder b = UriBuilder.fromUri(oauth.getLoginFormUrl());
        driver.navigate().to(b.build().toURL());
        // Always read error from the "query"
        OAuthClient.AuthorizationEndpointResponse errorResponse = new OAuthClient.AuthorizationEndpointResponse(oauth, false);
        Assert.assertTrue(errorResponse.isRedirected());
        Assert.assertEquals(errorResponse.getError(), INVALID_REQUEST);
        events.expectLogin().error(Errors.INVALID_REQUEST).user(((String) (null))).session(((String) (null))).clearDetails().assertEvent();
    }

    @Test
    public void oidcFlow_RealmRS256_ClientRS384_EffectiveRS384() throws Exception {
        try {
            setSignatureAlgorithm("RS384");
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, "RS256");
            TokenSignatureUtil.changeClientIdTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), "RS384");
            oidcFlow("RS256", "RS384");
        } finally {
            setSignatureAlgorithm("RS256");
            TokenSignatureUtil.changeClientIdTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), "RS256");
        }
    }

    @Test
    public void oidcFlow_RealmES256_ClientES384_EffectiveES384() throws Exception {
        try {
            setSignatureAlgorithm("ES384");
            TokenSignatureUtil.changeRealmTokenSignatureProvider(adminClient, "ES256");
            TokenSignatureUtil.changeClientIdTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), "ES384");
            oidcFlow("ES256", "ES384");
        } finally {
            setSignatureAlgorithm("RS256");
            TokenSignatureUtil.changeClientIdTokenSignatureProvider(ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app"), "RS256");
        }
    }

    private String sigAlgName = "RS256";
}

