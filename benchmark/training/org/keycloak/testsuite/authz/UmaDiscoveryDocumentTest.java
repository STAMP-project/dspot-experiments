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
package org.keycloak.testsuite.authz;


import OAuthClient.AUTH_SERVER_ROOT;
import UmaWellKnownProviderFactory.PROVIDER_ID;
import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.authorization.config.UmaConfiguration;
import org.keycloak.protocol.oidc.OIDCLoginProtocolService;
import org.keycloak.services.resources.RealmsResource;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.OAuthClient;


public class UmaDiscoveryDocumentTest extends AbstractKeycloakTest {
    @ArquillianResource
    protected OAuthClient oauth;

    @Test
    public void testFetchDiscoveryDocument() {
        Client client = ClientBuilder.newClient();
        UriBuilder builder = UriBuilder.fromUri(AUTH_SERVER_ROOT);
        URI oidcDiscoveryUri = RealmsResource.wellKnownProviderUrl(builder).build("test", PROVIDER_ID);
        WebTarget oidcDiscoveryTarget = client.target(oidcDiscoveryUri);
        try (Response response = oidcDiscoveryTarget.request().get()) {
            Assert.assertEquals("no-cache, must-revalidate, no-transform, no-store", response.getHeaders().getFirst("Cache-Control"));
            UmaConfiguration configuration = response.readEntity(UmaConfiguration.class);
            Assert.assertEquals(configuration.getAuthorizationEndpoint(), OIDCLoginProtocolService.authUrl(UriBuilder.fromUri(AUTH_SERVER_ROOT)).build("test").toString());
            Assert.assertEquals(configuration.getTokenEndpoint(), oauth.getAccessTokenUrl());
            Assert.assertEquals(configuration.getJwksUri(), oauth.getCertsUrl("test"));
            Assert.assertEquals(configuration.getTokenIntrospectionEndpoint(), oauth.getTokenIntrospectionUrl());
            String registrationUri = UriBuilder.fromUri(AUTH_SERVER_ROOT).path(RealmsResource.class).path(RealmsResource.class, "getRealmResource").build(realmsResouce().realm("test").toRepresentation().getRealm()).toString();
            Assert.assertEquals((registrationUri + "/authz/protection/permission"), configuration.getPermissionEndpoint().toString());
            Assert.assertEquals((registrationUri + "/authz/protection/resource_set"), configuration.getResourceRegistrationEndpoint().toString());
        }
    }
}

