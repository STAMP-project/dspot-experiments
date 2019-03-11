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
package org.keycloak.testsuite.client;


import HttpHeaders.AUTHORIZATION;
import OAuth2Constants.CODE;
import OAuthErrorException.INVALID_REDIRECT_URI;
import OIDCLoginProtocol.REDIRECT_URI_PARAM;
import Status.CREATED;
import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.common.util.KeycloakUriBuilder;
import org.keycloak.constants.ServiceUrlConstants;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.util.Matchers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.keycloak.testsuite.util.ClientBuilder.create;


/**
 *
 *
 * @author <a href="mailto:thomas.darimont@gmail.com">Thomas Darimont</a>
 */
public class ClientRedirectTest extends AbstractTestRealmKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    /**
     * Integration test for {@link org.keycloak.services.resources.RealmsResource#getRedirect(String, String)}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testClientRedirectEndpoint() throws Exception {
        oauth.doLogin("test-user@localhost", "password");
        driver.get(((getAuthServerRoot().toString()) + "realms/test/clients/launchpad-test/redirect"));
        Assert.assertEquals("http://example.org/launchpad", driver.getCurrentUrl());
        driver.get(((getAuthServerRoot().toString()) + "realms/test/clients/dummy-test/redirect"));
        Assert.assertEquals("http://example.org/dummy/base-path", driver.getCurrentUrl());
        driver.get(((getAuthServerRoot().toString()) + "realms/test/clients/account/redirect"));
        Assert.assertEquals(((getAuthServerRoot().toString()) + "realms/test/account"), driver.getCurrentUrl());
    }

    @Test
    public void testRedirectStatusCode() {
        oauth.doLogin("test-user@localhost", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        String token = oauth.doAccessTokenRequest(code, "password").getAccessToken();
        Client client = ClientBuilder.newClient();
        String redirectUrl = (getAuthServerRoot().toString()) + "realms/test/clients/launchpad-test/redirect";
        Response response = client.target(redirectUrl).request().header(AUTHORIZATION, ("Bearer " + token)).get();
        Assert.assertEquals(303, response.getStatus());
        client.close();
    }

    // KEYCLOAK-7707
    @Test
    public void testRedirectToDisabledClientRedirectURI() throws Exception {
        log.debug("Creating disabled-client with redirect uri \"*\"");
        String clientId;
        try (Response create = adminClient.realm("test").clients().create(create().clientId("disabled-client").enabled(false).redirectUris("*").build())) {
            clientId = ApiUtil.getCreatedId(create);
            Assert.assertThat(create, Matchers.statusCodeIs(CREATED));
        }
        try {
            log.debug("log in");
            oauth.doLogin("test-user@localhost", "password");
            events.expectLogin().assertEvent();
            URI logout = KeycloakUriBuilder.fromUri(suiteContext.getAuthServerInfo().getBrowserContextRoot().toURI()).path(("auth" + (ServiceUrlConstants.TOKEN_SERVICE_LOGOUT_PATH))).queryParam(REDIRECT_URI_PARAM, "http://example.org/redirected").build("test");
            log.debug(("log out using: " + (logout.toURL())));
            driver.navigate().to(logout.toURL());
            log.debug(("Current URL: " + (driver.getCurrentUrl())));
            log.debug("check logout_error");
            events.expectLogoutError(INVALID_REDIRECT_URI).assertEvent();
            Assert.assertThat(driver.getCurrentUrl(), is(not(equalTo("http://example.org/redirected"))));
        } finally {
            log.debug("removing disabled-client");
            adminClient.realm("test").clients().get(clientId).remove();
        }
    }
}

