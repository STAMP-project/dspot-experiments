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


import Errors.INVALID_CLIENT;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.client.registration.Auth;
import org.keycloak.client.registration.ClientRegistrationException;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class SAMLClientRegistrationTest extends AbstractClientRegistrationTest {
    @Test
    public void createClient() throws IOException, ClientRegistrationException {
        String entityDescriptor = IOUtils.toString(getClass().getResourceAsStream("/clientreg-test/saml-entity-descriptor.xml"));
        ClientRepresentation response = reg.saml().create(entityDescriptor);
        Assert.assertThat(response.getRegistrationAccessToken(), notNullValue());
        Assert.assertThat(response.getClientId(), is("loadbalancer-9.siroe.com"));
        Assert.assertThat(response.getRedirectUris(), containsInAnyOrder("https://LoadBalancer-9.siroe.com:3443/federation/Consumer/metaAlias/sp/post", "https://LoadBalancer-9.siroe.com:3443/federation/Consumer/metaAlias/sp/soap", "https://LoadBalancer-9.siroe.com:3443/federation/Consumer/metaAlias/sp/paos", "https://LoadBalancer-9.siroe.com:3443/federation/Consumer/metaAlias/sp/redirect"));// No redirect URI for ARTIFACT binding which is unsupported

        Assert.assertThat(response.getAttributes().get("saml_single_logout_service_url_redirect"), is("https://LoadBalancer-9.siroe.com:3443/federation/SPSloRedirect/metaAlias/sp"));
    }

    @Test
    public void testSAMLEndpointCreateWithOIDCClient() throws Exception {
        ClientsResource clientsResource = adminClient.realm(TEST).clients();
        ClientRepresentation oidcClient = clientsResource.findByClientId("oidc-client").get(0);
        String oidcClientServiceId = clientsResource.get(oidcClient.getId()).getServiceAccountUser().getId();
        String realmManagementId = clientsResource.findByClientId("realm-management").get(0).getId();
        RoleRepresentation role = clientsResource.get(realmManagementId).roles().get("create-client").toRepresentation();
        adminClient.realm(TEST).users().get(oidcClientServiceId).roles().clientLevel(realmManagementId).add(Arrays.asList(role));
        String accessToken = oauth.clientId("oidc-client").doClientCredentialsGrantAccessTokenRequest("secret").getAccessToken();
        reg.auth(Auth.token(accessToken));
        String entityDescriptor = IOUtils.toString(getClass().getResourceAsStream("/clientreg-test/saml-entity-descriptor.xml"));
        assertCreateFail(entityDescriptor, 400, INVALID_CLIENT);
    }
}

