/**
 * Copyright 2017 Analytical Graphics, Inc. and/or its affiliates
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
package org.keycloak.testsuite.x509;


import Details.CODE_ID;
import Details.CONSENT;
import Details.REDIRECT_URI;
import Details.USERNAME;
import Errors.INVALID_USER_CREDENTIALS;
import Errors.USER_DISABLED;
import OAuthClient.AccessTokenResponse;
import Response.Status.BAD_REQUEST;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.authentication.authenticators.x509.X509AuthenticatorConfigModel;
import org.keycloak.representations.idm.AuthenticatorConfigRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.OAuthClient;


/**
 *
 *
 * @author <a href="mailto:brat000012001@gmail.com">Peter Nalyvayko</a>
 * @version $Revision: 1 $
 * @since 10/28/2016
 */
public class X509DirectGrantTest extends AbstractX509AuthenticationTest {
    @Test
    public void loginFailedOnDuplicateUsers() throws Exception {
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-directgrant-config", AbstractX509AuthenticationTest.createLoginIssuerDN_OU2CustomAttributeConfig().getConfig());
        String cfgId = createConfig(directGrantExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        // Set up the users so that the identity extracted from X509 client cert
        // matches more than a single user to trigger DuplicateModelException.
        UserRepresentation user = testRealm().users().get(userId2).toRepresentation();
        Assert.assertNotNull(user);
        user.singleAttribute("x509_certificate_identity", "Red Hat");
        this.updateUser(user);
        user = testRealm().users().get(userId).toRepresentation();
        Assert.assertNotNull(user);
        user.singleAttribute("x509_certificate_identity", "Red Hat");
        this.updateUser(user);
        events.clear();
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "", "", null);
        Assert.assertEquals(401, response.getStatusCode());
        Assert.assertEquals("invalid_request", response.getError());
        Assert.assertThat(response.getErrorDescription(), Matchers.containsString("X509 certificate authentication's failed."));
    }

    @Test
    public void loginFailedOnInvalidUser() throws Exception {
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-directgrant-config", AbstractX509AuthenticationTest.createLoginIssuerDN_OU2CustomAttributeConfig().getConfig());
        String cfgId = createConfig(directGrantExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        UserRepresentation user = testRealm().users().get(userId2).toRepresentation();
        Assert.assertNotNull(user);
        user.singleAttribute("x509_certificate_identity", "-");
        this.updateUser(user);
        events.clear();
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "", "", null);
        events.expectLogin().user(((String) (null))).session(((String) (null))).error(INVALID_USER_CREDENTIALS).client("resource-owner").removeDetail(CODE_ID).removeDetail(USERNAME).removeDetail(CONSENT).removeDetail(REDIRECT_URI).assertEvent();
        Assert.assertEquals(401, response.getStatusCode());
        Assert.assertEquals("invalid_grant", response.getError());
        Assert.assertEquals("Invalid user credentials", response.getErrorDescription());
    }

    @Test
    public void loginWithNonSupportedCertKeyUsage() throws Exception {
        // Set the X509 authenticator configuration
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-directgrant-config", AbstractX509AuthenticationTest.createLoginSubjectEmailWithKeyUsage("dataEncipherment").getConfig());
        String cfgId = createConfig(directGrantExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "", "", null);
        Assert.assertEquals(401, response.getStatusCode());
        Assert.assertEquals("invalid_request", response.getError());
        Assert.assertThat(response.getErrorDescription(), Matchers.containsString("Key Usage bit 'dataEncipherment' is not set."));
        events.clear();
    }

    @Test
    public void loginWithNonSupportedCertExtendedKeyUsage() throws Exception {
        // Set the X509 authenticator configuration
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-directgrant-config", AbstractX509AuthenticationTest.createLoginSubjectEmailWithExtendedKeyUsage("serverAuth").getConfig());
        String cfgId = createConfig(directGrantExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "", "", null);
        Assert.assertEquals(200, response.getStatusCode());
    }

    @Test
    public void loginWithNonMatchingRegex() throws Exception {
        X509AuthenticatorConfigModel config = AbstractX509AuthenticationTest.createLoginIssuerDN_OU2CustomAttributeConfig();
        config.setRegularExpression("INVALID=(.*?)(?:,|$)");
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-directgrant-config", config.getConfig());
        String cfgId = createConfig(directGrantExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "", "", null);
        Assert.assertEquals(401, response.getStatusCode());
        events.expectLogin().user(((String) (null))).session(((String) (null))).error("invalid_user_credentials").client("resource-owner").removeDetail(CODE_ID).removeDetail(CONSENT).removeDetail(REDIRECT_URI).assertEvent();
    }

    @Test
    public void loginFailedDisabledUser() throws Exception {
        setUserEnabled("test-user@localhost", false);
        try {
            AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-directgrant-config", AbstractX509AuthenticationTest.createLoginSubjectEmail2UsernameOrEmailConfig().getConfig());
            String cfgId = createConfig(directGrantExecution.getId(), cfg);
            Assert.assertNotNull(cfgId);
            oauth.clientId("resource-owner");
            OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "", "", null);
            events.expectLogin().user(userId).session(((String) (null))).error(USER_DISABLED).client("resource-owner").detail(USERNAME, "test-user@localhost").removeDetail(CODE_ID).removeDetail(CONSENT).removeDetail(REDIRECT_URI).assertEvent();
            Assert.assertEquals(BAD_REQUEST.getStatusCode(), response.getStatusCode());
            Assert.assertEquals("invalid_grant", response.getError());
            Assert.assertEquals("Account disabled", response.getErrorDescription());
        } finally {
            setUserEnabled("test-user@localhost", true);
        }
    }

    @Test
    public void loginCertificateRevoked() throws Exception {
        X509AuthenticatorConfigModel config = new X509AuthenticatorConfigModel().setCRLEnabled(true).setCRLRelativePath(AbstractX509AuthenticationTest.CLIENT_CRL_PATH).setConfirmationPageAllowed(true).setMappingSourceType(SUBJECTDN_EMAIL).setUserIdentityMapperType(USERNAME_EMAIL);
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-directgrant-config", config.getConfig());
        String cfgId = createConfig(directGrantExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        oauth.clientId("resource-owner");
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("secret", "", "", null);
        Assert.assertEquals(401, response.getStatusCode());
        Assert.assertEquals("invalid_request", response.getError());
        Assert.assertThat(response.getErrorDescription(), Matchers.containsString("Certificate has been revoked, certificate's subject:"));
    }

    @Test
    public void loginResourceOwnerCredentialsSuccess() throws Exception {
        X509AuthenticatorConfigModel config = new X509AuthenticatorConfigModel().setMappingSourceType(SUBJECTDN_EMAIL).setUserIdentityMapperType(USERNAME_EMAIL);
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-directgrant-config", config.getConfig());
        String cfgId = createConfig(directGrantExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        doResourceOwnerCredentialsLogin("resource-owner", "secret", "test-user@localhost", "");
    }
}

