/**
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.console.clients;


import java.util.ArrayList;
import java.util.List;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.testsuite.console.page.clients.settings.ClientSettings;
import org.openqa.selenium.By;


/**
 *
 *
 * @author Filip Kiss
 * @author tkyjovsk
 * @author Vaclav Muzikar <vmuzikar@redhat.com>
 */
public class ClientSettingsTest extends AbstractClientTest {
    @Page
    private ClientSettings clientSettingsPage;

    private ClientRepresentation newClient;

    @Test
    public void crudOIDCPublic() {
        newClient = AbstractClientTest.createClientRep("oidc-public", OIDC);
        createClient(newClient);
        // read & verify
        ClientRepresentation found = findClientByClientId(newClient.getClientId());
        Assert.assertNotNull((("Client " + (newClient.getClientId())) + " was not found."), found);
        assertClientSettingsEqual(newClient, found);
        // update & verify
        newClient.setClientId("oidc-public-updated");
        newClient.setName("updatedName");
        List<String> redirectUris = new ArrayList<>();
        redirectUris.add("http://example2.test/app/*");
        redirectUris.add("http://example2.test/app2/*");
        redirectUris.add("http://example3.test/app/*");
        newClient.setRedirectUris(redirectUris);
        List<String> webOrigins = new ArrayList<>();
        webOrigins.clear();
        webOrigins.add("http://example2.test");
        webOrigins.add("http://example3.test");
        newClient.setWebOrigins(webOrigins);
        clientSettingsPage.form().setClientId("oidc-public-updated");
        clientSettingsPage.form().setName("updatedName");
        clientSettingsPage.form().setRedirectUris(redirectUris);
        clientSettingsPage.form().setWebOrigins(webOrigins);
        clientSettingsPage.form().save();
        assertAlertSuccess();
        found = findClientByClientId(newClient.getClientId());
        Assert.assertNotNull((("Client " + (newClient.getClientId())) + " was not found."), found);
        assertClientSettingsEqual(newClient, found);
        // delete
        clientPage.delete();
        assertAlertSuccess();
        found = findClientByClientId(newClient.getClientId());
        Assert.assertNull((("Deleted client " + (newClient.getClientId())) + " was found."), found);
    }

    @Test
    public void createOIDCConfidential() {
        newClient = AbstractClientTest.createClientRep("oidc-confidetial", OIDC);
        createClient(newClient);
        newClient.setRedirectUris(TEST_REDIRECT_URIs);
        newClient.setPublicClient(false);
        clientSettingsPage.form().setAccessType(CONFIDENTIAL);
        clientSettingsPage.form().setRedirectUris(TEST_REDIRECT_URIs);
        clientSettingsPage.form().save();
        ClientRepresentation found = findClientByClientId(newClient.getClientId());
        Assert.assertNotNull((("Client " + (newClient.getClientId())) + " was not found."), found);
        assertClientSettingsEqual(newClient, found);
    }

    // KEYCLOAK-4022
    @Test
    public void testOIDCConfidentialServiceAccountRolesTab() {
        newClient = AbstractClientTest.createClientRep("oidc-service-account-tab", OIDC);
        createClient(newClient);
        newClient.setRedirectUris(TEST_REDIRECT_URIs);
        newClient.setPublicClient(false);
        clientSettingsPage.form().setAccessType(CONFIDENTIAL);
        clientSettingsPage.form().setServiceAccountsEnabled(true);
        Assert.assertTrue(clientSettingsPage.form().isServiceAccountsEnabled());
        // check if Service Account Roles tab is not present
        Assert.assertFalse(clientSettingsPage.tabs().isServiceAccountRolesDisplayed());
        clientSettingsPage.form().setRedirectUris(TEST_REDIRECT_URIs);
        clientSettingsPage.form().save();
        // should be there now
        Assert.assertTrue(clientSettingsPage.tabs().getTabs().findElement(By.linkText("Service Account Roles")).isDisplayed());
    }

    @Test
    public void saveOIDCConfidentialWithoutRedirectURIs() {
        newClient = AbstractClientTest.createClientRep("oidc-confidential", OIDC);
        createClient(newClient);
        clientSettingsPage.form().setName("name");
        clientSettingsPage.form().save();
        assertAlertDanger();
    }

    @Test
    public void createOIDCBearerOnly() {
        newClient = AbstractClientTest.createClientRep("oidc-bearer-only", OIDC);
        createClient(newClient);
        clientSettingsPage.form().setAccessType(BEARER_ONLY);
        clientSettingsPage.form().save();
        newClient.setBearerOnly(true);
        newClient.setPublicClient(false);
        ClientRepresentation found = findClientByClientId(newClient.getClientId());
        Assert.assertNotNull((("Client " + (newClient.getClientId())) + " was not found."), found);
        assertClientSettingsEqual(newClient, found);
    }

    @Test
    public void createSAML() {
        newClient = AbstractClientTest.createClientRep("saml", SAML);
        createClient(newClient);
        ClientRepresentation found = findClientByClientId(newClient.getClientId());
        Assert.assertNotNull((("Client " + (newClient.getClientId())) + " was not found."), found);
        assertClientSettingsEqual(newClient, found);
        assertClientSamlAttributes(AbstractClientTest.getSAMLAttributes(), found.getAttributes());
    }

    @Test
    public void invalidSettings() {
        clientsPage.table().createClient();
        createClientPage.form().save();
        assertAlertDanger();
        clientsPage.navigateTo();
        newClient = AbstractClientTest.createClientRep(TEST_CLIENT_ID, OIDC);
        createClient(newClient);
        clientsPage.navigateTo();
        clientsPage.table().createClient();
        createClientPage.form().setClientId(TEST_CLIENT_ID);
        createClientPage.form().save();
        assertAlertDanger();
    }

    @Test
    public void disabledClient() {
        newClient = AbstractClientTest.createClientRep("disabled-client", OIDC);
        newClient.setEnabled(false);
        createClient(newClient);
        ClientRepresentation clientRepre = findClientByClientId("disabled-client");
        Assert.assertTrue("Client should be disabled", clientRepre.isEnabled());
    }
}

