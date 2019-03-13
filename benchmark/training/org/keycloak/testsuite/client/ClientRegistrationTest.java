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


import Constants.ADMIN_CLI_CLIENT_ID;
import java.util.ArrayList;
import javax.ws.rs.NotFoundException;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.client.registration.Auth;
import org.keycloak.client.registration.ClientRegistration;
import org.keycloak.client.registration.ClientRegistrationException;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class ClientRegistrationTest extends AbstractClientRegistrationTest {
    private static final String CLIENT_ID = "test-client";

    private static final String CLIENT_SECRET = "test-client-secret";

    @Test
    public void registerClientAsAdmin() throws ClientRegistrationException {
        authManageClients();
        registerClient();
    }

    // KEYCLOAK-5907
    @Test
    public void withServiceAccount() throws ClientRegistrationException {
        authManageClients();
        ClientRepresentation clientRep = buildClient();
        clientRep.setServiceAccountsEnabled(true);
        ClientRepresentation rep = registerClient(clientRep);
        UserRepresentation serviceAccountUser = adminClient.realm("test").clients().get(rep.getId()).getServiceAccountUser();
        Assert.assertNotNull(serviceAccountUser);
        deleteClient(rep);
        try {
            adminClient.realm("test").users().get(serviceAccountUser.getId()).toRepresentation();
            Assert.fail("Expected NotFoundException");
        } catch (NotFoundException e) {
        }
    }

    @Test
    public void registerClientInMasterRealm() throws Exception {
        ClientRegistration masterReg = ClientRegistration.create().url(((suiteContext.getAuthServerInfo().getContextRoot()) + "/auth"), "master").build();
        String token = oauth.doGrantAccessTokenRequest("master", "admin", "admin", null, ADMIN_CLI_CLIENT_ID, null).getAccessToken();
        masterReg.auth(Auth.token(token));
        ClientRepresentation client = new ClientRepresentation();
        client.setClientId(ClientRegistrationTest.CLIENT_ID);
        client.setSecret(ClientRegistrationTest.CLIENT_SECRET);
        ClientRepresentation createdClient = masterReg.create(client);
        Assert.assertNotNull(createdClient);
        adminClient.realm("master").clients().get(createdClient.getId()).remove();
    }

    @Test
    public void registerClientWithoutProtocol() throws ClientRegistrationException {
        authCreateClients();
        ClientRepresentation clientRepresentation = registerClient();
        Assert.assertEquals("openid-connect", clientRepresentation.getProtocol());
    }

    @Test
    public void registerClientAsAdminWithCreateOnly() throws ClientRegistrationException {
        authCreateClients();
        registerClient();
    }

    @Test
    public void registerClientAsAdminWithNoAccess() throws ClientRegistrationException {
        authNoAccess();
        try {
            registerClient();
            Assert.fail("Expected 403");
        } catch (ClientRegistrationException e) {
            Assert.assertEquals(403, getStatusLine().getStatusCode());
        }
    }

    @Test
    public void registerClientWithNonAsciiChars() throws ClientRegistrationException {
        authCreateClients();
        ClientRepresentation client = buildClient();
        String name = "Cli\u00ebnt";
        client.setName(name);
        ClientRepresentation createdClient = registerClient(client);
        Assert.assertEquals(name, createdClient.getName());
    }

    @Test
    public void getClientAsAdmin() throws ClientRegistrationException {
        registerClientAsAdmin();
        ClientRepresentation rep = reg.get(ClientRegistrationTest.CLIENT_ID);
        Assert.assertNotNull(rep);
    }

    @Test
    public void getClientAsAdminWithCreateOnly() throws ClientRegistrationException {
        registerClientAsAdmin();
        authCreateClients();
        try {
            reg.get(ClientRegistrationTest.CLIENT_ID);
            Assert.fail("Expected 403");
        } catch (ClientRegistrationException e) {
            Assert.assertEquals(403, getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getClientAsAdminWithNoAccess() throws ClientRegistrationException {
        registerClientAsAdmin();
        authNoAccess();
        try {
            reg.get(ClientRegistrationTest.CLIENT_ID);
            Assert.fail("Expected 403");
        } catch (ClientRegistrationException e) {
            Assert.assertEquals(403, getStatusLine().getStatusCode());
        }
    }

    @Test
    public void getClientNotFound() throws ClientRegistrationException {
        authManageClients();
        Assert.assertNull(reg.get("invalid"));
    }

    @Test
    public void getClientNotFoundNoAccess() throws ClientRegistrationException {
        authNoAccess();
        try {
            reg.get("invalid");
            Assert.fail("Expected 403");
        } catch (ClientRegistrationException e) {
            Assert.assertEquals(403, getStatusLine().getStatusCode());
        }
    }

    @Test
    public void updateClientAsAdmin() throws ClientRegistrationException {
        registerClientAsAdmin();
        authManageClients();
        updateClient();
    }

    @Test
    public void updateClientSecret() throws ClientRegistrationException {
        authManageClients();
        registerClient();
        ClientRepresentation client = reg.get(ClientRegistrationTest.CLIENT_ID);
        Assert.assertNotNull(client.getSecret());
        client.setSecret("mysecret");
        reg.update(client);
        ClientRepresentation updatedClient = reg.get(ClientRegistrationTest.CLIENT_ID);
        Assert.assertEquals("mysecret", updatedClient.getSecret());
    }

    @Test
    public void addClientProtcolMappers() throws ClientRegistrationException {
        authManageClients();
        ClientRepresentation initialClient = buildClient();
        registerClient(initialClient);
        ClientRepresentation client = reg.get(ClientRegistrationTest.CLIENT_ID);
        addProtocolMapper(client, "mapperA");
        reg.update(client);
        ClientRepresentation updatedClient = reg.get(ClientRegistrationTest.CLIENT_ID);
        Assert.assertThat("Adding protocolMapper failed", updatedClient.getProtocolMappers().size(), Is.is(1));
    }

    @Test
    public void removeClientProtcolMappers() throws ClientRegistrationException {
        authManageClients();
        ClientRepresentation initialClient = buildClient();
        addProtocolMapper(initialClient, "mapperA");
        registerClient(initialClient);
        ClientRepresentation client = reg.get(ClientRegistrationTest.CLIENT_ID);
        client.setProtocolMappers(new ArrayList());
        reg.update(client);
        ClientRepresentation updatedClient = reg.get(ClientRegistrationTest.CLIENT_ID);
        Assert.assertThat("Removing protocolMapper failed", updatedClient.getProtocolMappers(), Matchers.nullValue());
    }

    @Test
    public void updateClientProtcolMappers() throws ClientRegistrationException {
        authManageClients();
        ClientRepresentation initialClient = buildClient();
        addProtocolMapper(initialClient, "mapperA");
        registerClient(initialClient);
        ClientRepresentation client = reg.get(ClientRegistrationTest.CLIENT_ID);
        client.getProtocolMappers().get(0).getConfig().put("claim.name", "updatedClaimName");
        reg.update(client);
        ClientRepresentation updatedClient = reg.get(ClientRegistrationTest.CLIENT_ID);
        Assert.assertThat("Updating protocolMapper failed", updatedClient.getProtocolMappers().get(0).getConfig().get("claim.name"), Is.is("updatedClaimName"));
    }

    @Test
    public void updateClientAsAdminWithCreateOnly() throws ClientRegistrationException {
        authCreateClients();
        try {
            updateClient();
            Assert.fail("Expected 403");
        } catch (ClientRegistrationException e) {
            Assert.assertEquals(403, getStatusLine().getStatusCode());
        }
    }

    @Test
    public void updateClientAsAdminWithNoAccess() throws ClientRegistrationException {
        authNoAccess();
        try {
            updateClient();
            Assert.fail("Expected 403");
        } catch (ClientRegistrationException e) {
            Assert.assertEquals(403, getStatusLine().getStatusCode());
        }
    }

    @Test
    public void updateClientNotFound() throws ClientRegistrationException {
        authManageClients();
        try {
            ClientRepresentation client = new ClientRepresentation();
            client.setClientId("invalid");
            reg.update(client);
            Assert.fail("Expected 404");
        } catch (ClientRegistrationException e) {
            Assert.assertEquals(404, getStatusLine().getStatusCode());
        }
    }

    @Test
    public void updateClientWithNonAsciiChars() throws ClientRegistrationException {
        authCreateClients();
        registerClient();
        authManageClients();
        ClientRepresentation client = reg.get(ClientRegistrationTest.CLIENT_ID);
        String name = "Cli\u00ebnt";
        client.setName(name);
        ClientRepresentation updatedClient = reg.update(client);
        Assert.assertEquals(name, updatedClient.getName());
    }

    @Test
    public void deleteClientAsAdmin() throws ClientRegistrationException {
        authCreateClients();
        ClientRepresentation client = registerClient();
        authManageClients();
        deleteClient(client);
    }

    @Test
    public void deleteClientAsAdminWithCreateOnly() throws ClientRegistrationException {
        authManageClients();
        ClientRepresentation client = registerClient();
        try {
            authCreateClients();
            deleteClient(client);
            Assert.fail("Expected 403");
        } catch (ClientRegistrationException e) {
            Assert.assertEquals(403, getStatusLine().getStatusCode());
        }
    }

    @Test
    public void deleteClientAsAdminWithNoAccess() throws ClientRegistrationException {
        authManageClients();
        ClientRepresentation client = registerClient();
        try {
            authNoAccess();
            deleteClient(client);
            Assert.fail("Expected 403");
        } catch (ClientRegistrationException e) {
            Assert.assertEquals(403, getStatusLine().getStatusCode());
        }
    }
}

