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


import SslRequired.EXTERNAL;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.client.registration.Auth;
import org.keycloak.client.registration.ClientRegistrationException;
import org.keycloak.representations.adapters.config.AdapterConfig;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class AdapterInstallationConfigTest extends AbstractClientRegistrationTest {
    private ClientRepresentation client;

    private ClientRepresentation client2;

    private ClientRepresentation clientPublic;

    @Test
    public void getConfigWithRegistrationAccessToken() throws ClientRegistrationException {
        reg.auth(Auth.token(client.getRegistrationAccessToken()));
        AdapterConfig config = reg.getAdapterConfig(client.getClientId());
        Assert.assertNotNull(config);
    }

    @Test
    public void getConfig() throws ClientRegistrationException {
        reg.auth(Auth.client(client.getClientId(), "RegistrationAccessTokenTestClientSecret"));
        AdapterConfig config = reg.getAdapterConfig(client.getClientId());
        Assert.assertNotNull(config);
        Assert.assertEquals(((suiteContext.getAuthServerInfo().getContextRoot()) + "/auth"), config.getAuthServerUrl());
        Assert.assertEquals("test", config.getRealm());
        Assert.assertEquals(1, config.getCredentials().size());
        Assert.assertEquals("RegistrationAccessTokenTestClientSecret", config.getCredentials().get("secret"));
        Assert.assertEquals(client.getClientId(), config.getResource());
        Assert.assertEquals(EXTERNAL.name().toLowerCase(), config.getSslRequired());
    }

    @Test
    public void getConfigMissingSecret() throws ClientRegistrationException {
        reg.auth(null);
        try {
            reg.getAdapterConfig(client.getClientId());
            Assert.fail("Expected 401");
        } catch (ClientRegistrationException e) {
            Assert.assertEquals(401, getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getConfigWrongClient() throws ClientRegistrationException {
        reg.auth(Auth.client(client.getClientId(), client.getSecret()));
        try {
            reg.getAdapterConfig(client2.getClientId());
            Assert.fail("Expected 401");
        } catch (ClientRegistrationException e) {
            Assert.assertEquals(401, getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getConfigPublicClient() throws ClientRegistrationException {
        reg.auth(null);
        AdapterConfig config = reg.getAdapterConfig(clientPublic.getClientId());
        Assert.assertNotNull(config);
        Assert.assertEquals("test", config.getRealm());
        Assert.assertEquals(0, config.getCredentials().size());
        Assert.assertEquals(clientPublic.getClientId(), config.getResource());
        Assert.assertEquals(EXTERNAL.name().toLowerCase(), config.getSslRequired());
    }
}

