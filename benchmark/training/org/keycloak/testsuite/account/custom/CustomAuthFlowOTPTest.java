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
package org.keycloak.testsuite.account.custom;


import Requirement.REQUIRED;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.authentication.authenticators.browser.ConditionalOtpFormAuthenticator;
import org.keycloak.models.utils.TimeBasedOTP;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.auth.page.login.OneTimeCode;
import org.keycloak.testsuite.pages.LoginConfigTotpPage;
import org.keycloak.testsuite.pages.PageUtils;
import org.keycloak.testsuite.util.URLAssert;


/**
 *
 *
 * @author <a href="mailto:vramik@redhat.com">Vlastislav Ramik</a>
 */
public class CustomAuthFlowOTPTest extends AbstractCustomAccountManagementTest {
    private final TimeBasedOTP totp = new TimeBasedOTP();

    @Page
    private OneTimeCode testLoginOneTimeCodePage;

    @Page
    private LoginConfigTotpPage loginConfigTotpPage;

    @Test
    public void requireOTPTest() {
        // update realm browser flow
        RealmRepresentation realm = testRealmResource().toRepresentation();
        realm.setBrowserFlow("browser");
        testRealmResource().update(realm);
        updateRequirement("browser", "auth-otp-form", REQUIRED);
        testRealmAccountManagementPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        Assert.assertTrue(loginConfigTotpPage.isCurrent());
        configureOTP();
        testRealmLoginPage.form().login(testUser);
        // verify that the page is login page, not totp setup
        URLAssert.assertCurrentUrlStartsWith(testLoginOneTimeCodePage);
    }

    @Test
    public void conditionalOTPNoDefault() {
        configureRequiredActions();
        configureOTP();
        // prepare config - no configuration specified
        Map<String, String> config = new HashMap<>();
        setConditionalOTPForm(config);
        // test OTP is required
        testRealmAccountManagementPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        // verify that the page is login page, not totp setup
        URLAssert.assertCurrentUrlStartsWith(testLoginOneTimeCodePage);
    }

    @Test
    public void conditionalOTPDefaultSkip() {
        // prepare config - default skip
        Map<String, String> config = new HashMap<>();
        config.put(ConditionalOtpFormAuthenticator.DEFAULT_OTP_OUTCOME, ConditionalOtpFormAuthenticator.SKIP);
        setConditionalOTPForm(config);
        // test OTP is skipped
        testRealmAccountManagementPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        URLAssert.assertCurrentUrlStartsWith(testRealmAccountManagementPage);
    }

    @Test
    public void conditionalOTPDefaultForce() {
        // prepare config - default force
        Map<String, String> config = new HashMap<>();
        config.put(ConditionalOtpFormAuthenticator.DEFAULT_OTP_OUTCOME, ConditionalOtpFormAuthenticator.FORCE);
        setConditionalOTPForm(config);
        // test OTP is forced
        testRealmAccountManagementPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        Assert.assertTrue(loginConfigTotpPage.isCurrent());
        configureOTP();
        testRealmLoginPage.form().login(testUser);
        // verify that the page is login page, not totp setup
        URLAssert.assertCurrentUrlStartsWith(testLoginOneTimeCodePage);
    }

    @Test
    public void conditionalOTPNoDefaultWithChecks() {
        configureRequiredActions();
        configureOTP();
        // prepare config - no configuration specified
        Map<String, String> config = new HashMap<>();
        config.put(ConditionalOtpFormAuthenticator.OTP_CONTROL_USER_ATTRIBUTE, "noSuchUserSkipAttribute");
        config.put(ConditionalOtpFormAuthenticator.SKIP_OTP_ROLE, "no_such_otp_role");
        config.put(ConditionalOtpFormAuthenticator.FORCE_OTP_ROLE, "no_such_otp_role");
        config.put(ConditionalOtpFormAuthenticator.SKIP_OTP_FOR_HTTP_HEADER, "NoSuchHost: nolocalhost:65536");
        config.put(ConditionalOtpFormAuthenticator.FORCE_OTP_FOR_HTTP_HEADER, "NoSuchHost: nolocalhost:65536");
        setConditionalOTPForm(config);
        // test OTP is required
        testRealmAccountManagementPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        // verify that the page is login page, not totp setup
        URLAssert.assertCurrentUrlStartsWith(testLoginOneTimeCodePage);
    }

    @Test
    public void conditionalOTPDefaultSkipWithChecks() {
        // prepare config - default skip
        Map<String, String> config = new HashMap<>();
        config.put(ConditionalOtpFormAuthenticator.OTP_CONTROL_USER_ATTRIBUTE, "noSuchUserSkipAttribute");
        config.put(ConditionalOtpFormAuthenticator.SKIP_OTP_ROLE, "no_such_otp_role");
        config.put(ConditionalOtpFormAuthenticator.FORCE_OTP_ROLE, "no_such_otp_role");
        config.put(ConditionalOtpFormAuthenticator.SKIP_OTP_FOR_HTTP_HEADER, "NoSuchHost: nolocalhost:65536");
        config.put(ConditionalOtpFormAuthenticator.FORCE_OTP_FOR_HTTP_HEADER, "NoSuchHost: nolocalhost:65536");
        config.put(ConditionalOtpFormAuthenticator.DEFAULT_OTP_OUTCOME, ConditionalOtpFormAuthenticator.SKIP);
        setConditionalOTPForm(config);
        // test OTP is skipped
        testRealmAccountManagementPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        URLAssert.assertCurrentUrlStartsWith(testRealmAccountManagementPage);
    }

    @Test
    public void conditionalOTPDefaultForceWithChecks() {
        // prepare config - default force
        Map<String, String> config = new HashMap<>();
        config.put(ConditionalOtpFormAuthenticator.OTP_CONTROL_USER_ATTRIBUTE, "noSuchUserSkipAttribute");
        config.put(ConditionalOtpFormAuthenticator.SKIP_OTP_ROLE, "no_such_otp_role");
        config.put(ConditionalOtpFormAuthenticator.FORCE_OTP_ROLE, "no_such_otp_role");
        config.put(ConditionalOtpFormAuthenticator.SKIP_OTP_FOR_HTTP_HEADER, "NoSuchHost: nolocalhost:65536");
        config.put(ConditionalOtpFormAuthenticator.FORCE_OTP_FOR_HTTP_HEADER, "NoSuchHost: nolocalhost:65536");
        config.put(ConditionalOtpFormAuthenticator.DEFAULT_OTP_OUTCOME, ConditionalOtpFormAuthenticator.FORCE);
        setConditionalOTPForm(config);
        // test OTP is forced
        testRealmAccountManagementPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        Assert.assertTrue(loginConfigTotpPage.isCurrent());
        configureOTP();
        testRealmLoginPage.form().login(testUser);
        // verify that the page is login page, not totp setup
        URLAssert.assertCurrentUrlStartsWith(testLoginOneTimeCodePage);
    }

    @Test
    public void conditionalOTPUserAttributeSkip() {
        // prepare config - user attribute, default to force
        Map<String, String> config = new HashMap<>();
        config.put(ConditionalOtpFormAuthenticator.OTP_CONTROL_USER_ATTRIBUTE, "userSkipAttribute");
        config.put(ConditionalOtpFormAuthenticator.DEFAULT_OTP_OUTCOME, ConditionalOtpFormAuthenticator.FORCE);
        setConditionalOTPForm(config);
        // add skip user attribute to user
        testUser.singleAttribute("userSkipAttribute", "skip");
        testRealmResource().users().get(testUser.getId()).update(testUser);
        // test OTP is skipped
        testRealmAccountManagementPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        URLAssert.assertCurrentUrlStartsWith(testRealmAccountManagementPage);
    }

    @Test
    public void conditionalOTPUserAttributeForce() {
        // prepare config - user attribute, default to skip
        Map<String, String> config = new HashMap<>();
        config.put(ConditionalOtpFormAuthenticator.OTP_CONTROL_USER_ATTRIBUTE, "userSkipAttribute");
        config.put(ConditionalOtpFormAuthenticator.DEFAULT_OTP_OUTCOME, ConditionalOtpFormAuthenticator.SKIP);
        setConditionalOTPForm(config);
        // add force user attribute to user
        testUser.singleAttribute("userSkipAttribute", "force");
        testRealmResource().users().get(testUser.getId()).update(testUser);
        // test OTP is required
        testRealmAccountManagementPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        Assert.assertTrue(loginConfigTotpPage.isCurrent());
        configureOTP();
        testRealmLoginPage.form().login(testUser);
        // verify that the page is login page, not totp setup
        URLAssert.assertCurrentUrlStartsWith(testLoginOneTimeCodePage);
    }

    @Test
    public void conditionalOTPRoleSkip() {
        // prepare config - role, default to force
        Map<String, String> config = new HashMap<>();
        config.put(ConditionalOtpFormAuthenticator.SKIP_OTP_ROLE, "otp_role");
        config.put(ConditionalOtpFormAuthenticator.DEFAULT_OTP_OUTCOME, ConditionalOtpFormAuthenticator.FORCE);
        setConditionalOTPForm(config);
        // create role
        RoleRepresentation role = getOrCreateOTPRole();
        // add role to user
        List<RoleRepresentation> realmRoles = new ArrayList<>();
        realmRoles.add(role);
        testRealmResource().users().get(testUser.getId()).roles().realmLevel().add(realmRoles);
        // test OTP is skipped
        testRealmAccountManagementPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        URLAssert.assertCurrentUrlStartsWith(testRealmAccountManagementPage);
    }

    @Test
    public void conditionalOTPRoleForce() {
        // prepare config - role, default to skip
        Map<String, String> config = new HashMap<>();
        config.put(ConditionalOtpFormAuthenticator.FORCE_OTP_ROLE, "otp_role");
        config.put(ConditionalOtpFormAuthenticator.DEFAULT_OTP_OUTCOME, ConditionalOtpFormAuthenticator.SKIP);
        setConditionalOTPForm(config);
        // create role
        RoleRepresentation role = getOrCreateOTPRole();
        // add role to user
        List<RoleRepresentation> realmRoles = new ArrayList<>();
        realmRoles.add(role);
        testRealmResource().users().get(testUser.getId()).roles().realmLevel().add(realmRoles);
        // test OTP is required
        testRealmAccountManagementPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        Assert.assertTrue(loginConfigTotpPage.isCurrent());
        configureOTP();
        testRealmLoginPage.form().login(testUser);
        // verify that the page is login page, not totp setup
        URLAssert.assertCurrentUrlStartsWith(testLoginOneTimeCodePage);
    }

    @Test
    public void conditionalOTPRequestHeaderSkip() {
        // prepare config - request header skip, default to force
        Map<String, String> config = new HashMap<>();
        String port = AbstractKeycloakTest.AUTH_SERVER_PORT;
        config.put(ConditionalOtpFormAuthenticator.SKIP_OTP_FOR_HTTP_HEADER, ("Host: localhost:" + port));
        config.put(ConditionalOtpFormAuthenticator.DEFAULT_OTP_OUTCOME, ConditionalOtpFormAuthenticator.FORCE);
        setConditionalOTPForm(config);
        // test OTP is skipped
        testRealmAccountManagementPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        URLAssert.assertCurrentUrlStartsWith(testRealmAccountManagementPage);
    }

    @Test
    public void conditionalOTPRequestHeaderForce() {
        // prepare config - equest header force, default to skip
        Map<String, String> config = new HashMap<>();
        String port = AbstractKeycloakTest.AUTH_SERVER_PORT;
        config.put(ConditionalOtpFormAuthenticator.FORCE_OTP_FOR_HTTP_HEADER, ("Host: localhost:" + port));
        config.put(ConditionalOtpFormAuthenticator.DEFAULT_OTP_OUTCOME, ConditionalOtpFormAuthenticator.SKIP);
        setConditionalOTPForm(config);
        // test OTP is required
        testRealmAccountManagementPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        Assert.assertEquals(PageUtils.getPageTitle(driver), "Mobile Authenticator Setup");
        configureOTP();
        testRealmLoginPage.form().login(testUser);
        // verify that the page is login page, not totp setup
        URLAssert.assertCurrentUrlStartsWith(testLoginOneTimeCodePage);
    }
}

