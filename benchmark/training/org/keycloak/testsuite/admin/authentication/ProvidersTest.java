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
package org.keycloak.testsuite.admin.authentication;


import IdpCreateUserIfUniqueAuthenticatorFactory.PROVIDER_ID;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.NotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.AuthenticatorConfigInfoRepresentation;
import org.keycloak.representations.idm.ConfigPropertyRepresentation;

import static org.keycloak.testsuite.Assert.assertProviderConfigProperty;


/**
 *
 *
 * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
 */
public class ProvidersTest extends AbstractAuthenticationTest {
    @Test
    public void testFormProviders() {
        List<Map<String, Object>> result = authMgmtResource.getFormProviders();
        org.keycloak.testsuite.Assert.assertNotNull("null result", result);
        org.keycloak.testsuite.Assert.assertEquals("size", 1, result.size());
        Map<String, Object> item = result.get(0);
        org.keycloak.testsuite.Assert.assertEquals("id", "registration-page-form", item.get("id"));
        org.keycloak.testsuite.Assert.assertEquals("displayName", "Registration Page", item.get("displayName"));
        org.keycloak.testsuite.Assert.assertEquals("description", "This is the controller for the registration page", item.get("description"));
    }

    @Test
    public void testFormActionProviders() {
        List<Map<String, Object>> result = authMgmtResource.getFormActionProviders();
        List<Map<String, Object>> expected = new LinkedList<>();
        addProviderInfo(expected, "registration-profile-action", "Profile Validation", "Validates email, first name, and last name attributes and stores them in user data.");
        addProviderInfo(expected, "registration-recaptcha-action", "Recaptcha", ("Adds Google Recaptcha button.  Recaptchas verify that the entity that is registering is a human.  " + "This can only be used on the internet and must be configured after you add it."));
        addProviderInfo(expected, "registration-password-action", "Password Validation", "Validates that password matches password confirmation field.  It also will store password in user's credential store.");
        addProviderInfo(expected, "registration-user-creation", "Registration User Creation", ("This action must always be first! Validates the username of the user in validation phase.  " + "In success phase, this will create the user in the database."));
        compareProviders(expected, result);
    }

    @Test
    public void testClientAuthenticatorProviders() {
        List<Map<String, Object>> result = authMgmtResource.getClientAuthenticatorProviders();
        List<Map<String, Object>> expected = new LinkedList<>();
        addProviderInfo(expected, "client-jwt", "Signed Jwt", "Validates client based on signed JWT issued by client and signed with the Client private key");
        addProviderInfo(expected, "client-secret", "Client Id and Secret", ("Validates client based on 'client_id' and " + "'client_secret' sent either in request parameters or in 'Authorization: Basic' header"));
        addProviderInfo(expected, "testsuite-client-passthrough", "Testsuite Dummy Client Validation", ("Testsuite dummy authenticator, " + "which automatically authenticates hardcoded client (like 'test-app' )"));
        addProviderInfo(expected, "testsuite-client-dummy", "Testsuite ClientId Dummy", "Dummy client authenticator, which authenticates the client with clientId only");
        addProviderInfo(expected, "client-x509", "X509 Certificate", "Validates client based on a X509 Certificate");
        addProviderInfo(expected, "client-secret-jwt", "Signed Jwt with Client Secret", "Validates client based on signed JWT issued by client and signed with the Client Secret");
        compareProviders(expected, result);
    }

    @Test
    public void testPerClientConfigDescriptions() {
        Map<String, List<ConfigPropertyRepresentation>> configs = authMgmtResource.getPerClientConfigDescription();
        org.keycloak.testsuite.Assert.assertTrue(configs.containsKey("client-jwt"));
        org.keycloak.testsuite.Assert.assertTrue(configs.containsKey("client-secret"));
        org.keycloak.testsuite.Assert.assertTrue(configs.containsKey("testsuite-client-passthrough"));
        org.keycloak.testsuite.Assert.assertTrue(configs.get("client-jwt").isEmpty());
        org.keycloak.testsuite.Assert.assertTrue(configs.get("client-secret").isEmpty());
        List<ConfigPropertyRepresentation> cfg = configs.get("testsuite-client-passthrough");
        assertProviderConfigProperty(cfg.get(0), "passthroughauth.foo", "Foo Property", null, "Foo Property of this authenticator, which does nothing", "String");
        assertProviderConfigProperty(cfg.get(1), "passthroughauth.bar", "Bar Property", null, "Bar Property of this authenticator, which does nothing", "boolean");
    }

    @Test
    public void testAuthenticatorConfigDescription() {
        // Try some not-existent provider
        try {
            authMgmtResource.getAuthenticatorConfigDescription("not-existent");
            org.keycloak.testsuite.Assert.fail("Don't expected to find provider 'not-existent'");
        } catch (NotFoundException nfe) {
            // Expected
        }
        AuthenticatorConfigInfoRepresentation infoRep = authMgmtResource.getAuthenticatorConfigDescription(PROVIDER_ID);
        org.keycloak.testsuite.Assert.assertEquals("Create User If Unique", infoRep.getName());
        org.keycloak.testsuite.Assert.assertEquals(PROVIDER_ID, infoRep.getProviderId());
        org.keycloak.testsuite.Assert.assertEquals("Detect if there is existing Keycloak account with same email like identity provider. If no, create new user", infoRep.getHelpText());
        org.keycloak.testsuite.Assert.assertEquals(1, infoRep.getProperties().size());
        assertProviderConfigProperty(infoRep.getProperties().get(0), "require.password.update.after.registration", "Require Password Update After Registration", null, "If this option is true and new user is successfully imported from Identity Provider to Keycloak (there is no duplicated email or username detected in Keycloak DB), then this user is required to update his password", "boolean");
    }

    @Test
    public void testInitialAuthenticationProviders() {
        List<Map<String, Object>> providers = authMgmtResource.getAuthenticatorProviders();
        providers = sortProviders(providers);
        compareProviders(expectedAuthProviders(), providers);
    }

    private static class ProviderComparator implements Comparator<Map<String, Object>> {
        @Override
        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
            return String.valueOf(o1.get("id")).compareTo(String.valueOf(o2.get("id")));
        }
    }
}

