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


import AppPage.RequestType.AUTH_RESPONSE;
import Details.CONSENT;
import Details.REDIRECT_URI;
import Details.USERNAME;
import OAuth2Constants.CODE;
import javax.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.authentication.authenticators.x509.X509AuthenticatorConfigModel;
import org.keycloak.representations.idm.AuthenticatorConfigRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.ProfileAssume;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.x509.X509IdentityConfirmationPage;
import org.keycloak.testsuite.util.DroneUtils;


/**
 *
 *
 * @author <a href="mailto:brat000012001@gmail.com">Peter Nalyvayko</a>
 * @version $Revision: 1 $
 * @unknown 8/12/2016
 */
public class X509BrowserLoginTest extends AbstractX509AuthenticationTest {
    @Page
    protected AppPage appPage;

    @Page
    protected X509IdentityConfirmationPage loginConfirmationPage;

    @Page
    protected LoginPage loginPage;

    @Test
    public void loginAsUserFromCertSubjectEmail() throws Exception {
        // Login using an e-mail extracted from certificate's subject DN
        login(AbstractX509AuthenticationTest.createLoginSubjectEmail2UsernameOrEmailConfig(), userId, "test-user@localhost", "test-user@localhost");
    }

    @Test
    public void loginWithNonMatchingRegex() throws Exception {
        X509AuthenticatorConfigModel config = AbstractX509AuthenticationTest.createLoginIssuerDN_OU2CustomAttributeConfig();
        config.setRegularExpression("INVALID=(.*?)(?:,|$)");
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-browser-config", config.getConfig());
        String cfgId = createConfig(browserExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        loginConfirmationPage.open();
        events.expectLogin().user(((String) (null))).session(((String) (null))).error("invalid_user_credentials").removeDetail(CONSENT).removeDetail(REDIRECT_URI).assertEvent();
    }

    @Test
    public void loginWithNonSupportedCertKeyUsage() throws Exception {
        // Set the X509 authenticator configuration
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-browser-config", AbstractX509AuthenticationTest.createLoginSubjectEmailWithKeyUsage("dataEncipherment").getConfig());
        String cfgId = createConfig(browserExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        loginConfirmationPage.open();
        Assert.assertThat(loginPage.getError(), Matchers.containsString(("Certificate validation\'s failed.\n" + "Key Usage bit 'dataEncipherment' is not set.")));
    }

    @Test
    public void loginWithNonSupportedCertExtendedKeyUsage() throws Exception {
        login(AbstractX509AuthenticationTest.createLoginSubjectEmailWithExtendedKeyUsage("serverAuth"), userId, "test-user@localhost", "test-user@localhost");
    }

    @Test
    public void loginIgnoreX509IdentityContinueToFormLogin() throws Exception {
        // Set the X509 authenticator configuration
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-browser-config", AbstractX509AuthenticationTest.createLoginSubjectEmail2UsernameOrEmailConfig().getConfig());
        String cfgId = createConfig(browserExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        loginConfirmationPage.open();
        Assert.assertTrue(loginConfirmationPage.getSubjectDistinguishedNameText().startsWith("EMAILADDRESS=test-user@localhost"));
        Assert.assertEquals("test-user@localhost", loginConfirmationPage.getUsernameText());
        loginConfirmationPage.ignore();
        loginPage.login("test-user@localhost", "password");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        events.expectLogin().user(userId).detail(USERNAME, "test-user@localhost").removeDetail(REDIRECT_URI).assertEvent();
    }

    @Test
    public void loginAsUserFromCertSubjectCN() {
        // Login using a CN extracted from certificate's subject DN
        login(AbstractX509AuthenticationTest.createLoginSubjectCN2UsernameOrEmailConfig(), userId, "test-user@localhost", "test-user@localhost");
    }

    @Test
    public void loginAsUserFromCertIssuerCN() {
        login(AbstractX509AuthenticationTest.createLoginIssuerCNToUsernameOrEmailConfig(), userId2, "keycloak", "Keycloak");
    }

    @Test
    public void loginAsUserFromCertIssuerCNMappedToUserAttribute() {
        UserRepresentation user = testRealm().users().get(userId2).toRepresentation();
        Assert.assertNotNull(user);
        user.singleAttribute("x509_certificate_identity", "Red Hat");
        this.updateUser(user);
        events.clear();
        login(AbstractX509AuthenticationTest.createLoginIssuerDN_OU2CustomAttributeConfig(), userId2, "keycloak", "Red Hat");
    }

    @Test
    public void loginDuplicateUsersNotAllowed() {
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-browser-config", AbstractX509AuthenticationTest.createLoginIssuerDN_OU2CustomAttributeConfig().getConfig());
        String cfgId = createConfig(browserExecution.getId(), cfg);
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
        loginPage.open();
        Assert.assertThat(loginPage.getError(), Matchers.containsString("X509 certificate authentication's failed."));
        loginPage.login("test-user@localhost", "password");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        events.expectLogin().user(userId).detail(USERNAME, "test-user@localhost").removeDetail(REDIRECT_URI).assertEvent();
    }

    @Test
    public void loginAttemptedNoConfig() {
        loginConfirmationPage.open();
        loginPage.assertCurrent();
        Assert.assertThat(loginPage.getInfoMessage(), Matchers.containsString("X509 client authentication has not been configured yet"));
        // Continue with form based login
        loginPage.login("test-user@localhost", "password");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        events.expectLogin().user(userId).detail(USERNAME, "test-user@localhost").removeDetail(REDIRECT_URI).assertEvent();
    }

    @Test
    public void loginWithX509CertCustomAttributeUserNotFound() {
        X509AuthenticatorConfigModel config = new X509AuthenticatorConfigModel().setConfirmationPageAllowed(true).setMappingSourceType(SUBJECTDN).setRegularExpression("O=(.*?)(?:,|$)").setCustomAttributeName("x509_certificate_identity").setUserIdentityMapperType(USER_ATTRIBUTE);
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-browser-config", config.getConfig());
        String cfgId = createConfig(browserExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        loginConfirmationPage.open();
        loginPage.assertCurrent();
        // Verify there is an error message
        Assert.assertNotNull(loginPage.getError());
        Assert.assertThat(loginPage.getError(), Matchers.containsString("X509 certificate authentication's failed."));
        events.expectLogin().user(((String) (null))).session(((String) (null))).error("user_not_found").detail(USERNAME, "Red Hat").removeDetail(CONSENT).removeDetail(REDIRECT_URI).assertEvent();
        // Continue with form based login
        loginPage.login("test-user@localhost", "password");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        events.expectLogin().user(userId).detail(USERNAME, "test-user@localhost").removeDetail(REDIRECT_URI).assertEvent();
    }

    @Test
    public void loginWithX509CertCustomAttributeSuccess() {
        X509AuthenticatorConfigModel config = new X509AuthenticatorConfigModel().setConfirmationPageAllowed(true).setMappingSourceType(SUBJECTDN).setRegularExpression("O=(.*?)(?:,|$)").setCustomAttributeName("x509_certificate_identity").setUserIdentityMapperType(USER_ATTRIBUTE);
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-browser-config", config.getConfig());
        String cfgId = createConfig(browserExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        // Update the attribute used to match the user identity to that
        // extracted from the client certificate
        UserRepresentation user = findUser("test-user@localhost");
        Assert.assertNotNull(user);
        user.singleAttribute("x509_certificate_identity", "Red Hat");
        this.updateUser(user);
        events.clear();
        loginConfirmationPage.open();
        Assert.assertTrue(loginConfirmationPage.getSubjectDistinguishedNameText().startsWith("EMAILADDRESS=test-user@localhost"));
        Assert.assertEquals("test-user@localhost", loginConfirmationPage.getUsernameText());
        loginConfirmationPage.confirm();
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
    }

    @Test
    public void loginWithX509CertBadUserOrNotFound() {
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-browser-config", AbstractX509AuthenticationTest.createLoginSubjectEmail2UsernameOrEmailConfig().getConfig());
        String cfgId = createConfig(browserExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        // Delete user
        UserRepresentation user = findUser("test-user@localhost");
        Assert.assertNotNull(user);
        Response response = testRealm().users().delete(userId);
        Assert.assertEquals(204, response.getStatus());
        response.close();
        // TODO causes the test to fail
        // assertAdminEvents.assertEvent(REALM_NAME, OperationType.DELETE, AdminEventPaths.userResourcePath(userId));
        loginConfirmationPage.open();
        loginPage.assertCurrent();
        // Verify there is an error message
        Assert.assertNotNull(loginPage.getError());
        Assert.assertThat(loginPage.getError(), Matchers.containsString("X509 certificate authentication's failed."));
        events.expectLogin().user(((String) (null))).session(((String) (null))).error("user_not_found").detail(USERNAME, "test-user@localhost").removeDetail(CONSENT).removeDetail(REDIRECT_URI).assertEvent();
        // Continue with form based login
        loginPage.login("test-user@localhost", "password");
        loginPage.assertCurrent();
        Assert.assertEquals("test-user@localhost", loginPage.getUsername());
        Assert.assertEquals("", loginPage.getPassword());
        Assert.assertEquals("Invalid username or password.", loginPage.getError());
    }

    @Test
    public void loginValidCertificateDisabledUser() {
        setUserEnabled("test-user@localhost", false);
        try {
            AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-browser-config", AbstractX509AuthenticationTest.createLoginSubjectEmail2UsernameOrEmailConfig().getConfig());
            String cfgId = createConfig(browserExecution.getId(), cfg);
            Assert.assertNotNull(cfgId);
            loginConfirmationPage.open();
            loginPage.assertCurrent();
            Assert.assertNotNull(loginPage.getError());
            Assert.assertThat(loginPage.getError(), Matchers.containsString("X509 certificate authentication\'s failed.\nUser is disabled"));
            events.expectLogin().user(userId).session(((String) (null))).error("user_disabled").detail(USERNAME, "test-user@localhost").removeDetail(CONSENT).removeDetail(REDIRECT_URI).assertEvent();
            loginPage.login("test-user@localhost", "password");
            loginPage.assertCurrent();
            // KEYCLOAK-1741 - assert form field values kept
            Assert.assertEquals("test-user@localhost", loginPage.getUsername());
            Assert.assertEquals("", loginPage.getPassword());
            // KEYCLOAK-2024
            Assert.assertEquals("Account is disabled, contact admin.", loginPage.getError());
            events.expectLogin().user(userId).session(((String) (null))).error("user_disabled").detail(USERNAME, "test-user@localhost").removeDetail(CONSENT).removeDetail(REDIRECT_URI).assertEvent();
        } finally {
            setUserEnabled("test-user@localhost", true);
        }
    }

    @Test
    public void loginWithX509WithEmptyRevocationList() {
        X509AuthenticatorConfigModel config = new X509AuthenticatorConfigModel().setCRLEnabled(true).setCRLRelativePath(AbstractX509AuthenticationTest.EMPTY_CRL_PATH).setConfirmationPageAllowed(true).setMappingSourceType(SUBJECTDN_EMAIL).setUserIdentityMapperType(USERNAME_EMAIL);
        login(config, userId, "test-user@localhost", "test-user@localhost");
    }

    @Test
    public void loginCertificateRevoked() {
        X509AuthenticatorConfigModel config = new X509AuthenticatorConfigModel().setCRLEnabled(true).setCRLRelativePath(AbstractX509AuthenticationTest.CLIENT_CRL_PATH).setConfirmationPageAllowed(true).setMappingSourceType(SUBJECTDN_EMAIL).setUserIdentityMapperType(USERNAME_EMAIL);
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-browser-config", config.getConfig());
        String cfgId = createConfig(browserExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        loginConfirmationPage.open();
        loginPage.assertCurrent();
        // Verify there is an error message
        Assert.assertNotNull(loginPage.getError());
        Assert.assertThat(loginPage.getError(), Matchers.containsString("Certificate validation\'s failed.\nCertificate has been revoked, certificate\'s subject:"));
        // Continue with form based login
        loginPage.login("test-user@localhost", "password");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        events.expectLogin().user(userId).detail(USERNAME, "test-user@localhost").removeDetail(REDIRECT_URI).assertEvent();
    }

    @Test
    public void loginNoIdentityConfirmationPage() {
        X509AuthenticatorConfigModel config = new X509AuthenticatorConfigModel().setConfirmationPageAllowed(false).setMappingSourceType(SUBJECTDN_EMAIL).setUserIdentityMapperType(USERNAME_EMAIL);
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-browser-config", config.getConfig());
        String cfgId = createConfig(browserExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        oauth.openLoginForm();
        // X509 authenticator extracts the user identity, maps it to an existing
        // user and automatically logs the user in without prompting to confirm
        // the identity.
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
        events.expectLogin().user(userId).detail(USERNAME, "test-user@localhost").removeDetail(REDIRECT_URI).assertEvent();
    }

    // KEYCLOAK-5466
    @Test
    public void loginWithCertificateAddedLater() throws Exception {
        // Start with normal login form
        loginConfirmationPage.open();
        loginPage.assertCurrent();
        Assert.assertThat(loginPage.getInfoMessage(), Matchers.containsString("X509 client authentication has not been configured yet"));
        loginPage.assertCurrent();
        // Now setup certificate and login with certificate in existing authenticationSession (Not 100% same scenario as KEYCLOAK-5466, but very similar)
        loginAsUserFromCertSubjectEmail();
    }

    // KEYCLOAK-6866
    @Test
    public void changeLocaleOnX509InfoPage() {
        ProfileAssume.assumeCommunity();
        AuthenticatorConfigRepresentation cfg = AbstractX509AuthenticationTest.newConfig("x509-browser-config", AbstractX509AuthenticationTest.createLoginSubjectEmail2UsernameOrEmailConfig().getConfig());
        String cfgId = createConfig(browserExecution.getId(), cfg);
        Assert.assertNotNull(cfgId);
        log.debug("Open confirm page");
        loginConfirmationPage.open();
        log.debug("check if on confirm page");
        Assert.assertThat(loginConfirmationPage.getSubjectDistinguishedNameText(), Matchers.startsWith("EMAILADDRESS=test-user@localhost"));
        log.debug("check if locale is EN");
        Assert.assertThat(loginConfirmationPage.getLanguageDropdownText(), Matchers.is(Matchers.equalTo("English")));
        log.debug("change locale to DE");
        loginConfirmationPage.openLanguage("Deutsch");
        log.debug("check if locale is DE");
        Assert.assertThat(loginConfirmationPage.getLanguageDropdownText(), Matchers.is(Matchers.equalTo("Deutsch")));
        Assert.assertThat(DroneUtils.getCurrentDriver().getPageSource(), Matchers.containsString("X509 Client Zertifikat:"));
        log.debug("confirm cert");
        loginConfirmationPage.confirm();
        log.debug("check if logged in");
        Assert.assertThat(appPage.getRequestType(), Matchers.is(Matchers.equalTo(AUTH_RESPONSE)));
    }
}

