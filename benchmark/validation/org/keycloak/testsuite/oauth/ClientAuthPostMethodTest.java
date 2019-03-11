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
package org.keycloak.testsuite.oauth;


import Details.CODE_ID;
import Details.REFRESH_TOKEN_ID;
import Details.TOKEN_ID;
import OAuth2Constants.CODE;
import OAuthClient.AccessTokenResponse;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.util.OAuthClient;


/**
 * Test for "client_secret_post" client authentication (clientID + clientSecret sent in the POST body instead of in "Authorization: Basic" header)
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ClientAuthPostMethodTest extends AbstractKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void testPostAuthentication() {
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = doAccessTokenRequestPostAuth(code, "password");
        Assert.assertEquals(200, response.getStatusCode());
        Assert.assertThat(response.getExpiresIn(), Matchers.allOf(Matchers.greaterThanOrEqualTo(250), Matchers.lessThanOrEqualTo(300)));
        Assert.assertThat(response.getRefreshExpiresIn(), Matchers.allOf(Matchers.greaterThanOrEqualTo(1750), Matchers.lessThanOrEqualTo(1800)));
        AccessToken token = oauth.verifyToken(response.getAccessToken());
        EventRepresentation event = events.expectCodeToToken(codeId, sessionId).assertEvent();
        Assert.assertEquals(token.getId(), event.getDetails().get(TOKEN_ID));
        Assert.assertEquals(oauth.parseRefreshToken(response.getRefreshToken()).getId(), event.getDetails().get(REFRESH_TOKEN_ID));
        Assert.assertEquals(sessionId, token.getSessionState());
    }
}

