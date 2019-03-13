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
package org.keycloak.testsuite.ssl;


import Details.CODE_ID;
import Details.EMAIL;
import Details.REDIRECT_URI;
import Details.USERNAME;
import MailServerConfiguration.FROM;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.auth.page.AuthRealm;
import org.keycloak.testsuite.auth.page.account.AccountManagement;
import org.keycloak.testsuite.auth.page.login.OIDCLogin;
import org.keycloak.testsuite.auth.page.login.VerifyEmail;
import org.keycloak.testsuite.util.MailAssert;
import org.keycloak.testsuite.util.SslMailServer;
import org.keycloak.testsuite.util.URLAssert;


/**
 *
 *
 * @author fkiss
 */
public class TrustStoreEmailTest extends AbstractTestRealmKeycloakTest {
    @Page
    protected OIDCLogin testRealmLoginPage;

    @Page
    protected AuthRealm testRealmPage;

    @Page
    protected AccountManagement accountManagement;

    @Page
    private VerifyEmail testRealmVerifyEmailPage;

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void verifyEmailWithSslEnabled() {
        UserRepresentation user = ApiUtil.findUserByUsername(testRealm(), "test-user@localhost");
        SslMailServer.startWithSsl(this.getClass().getClassLoader().getResource(SslMailServer.PRIVATE_KEY).getFile());
        accountManagement.navigateTo();
        testRealmLoginPage.form().login(user.getUsername(), "password");
        EventRepresentation sendEvent = events.expectRequiredAction(EventType.SEND_VERIFY_EMAIL).user(user.getId()).client("account").detail(USERNAME, "test-user@localhost").detail(EMAIL, "test-user@localhost").removeDetail(REDIRECT_URI).assertEvent();
        String mailCodeId = sendEvent.getDetails().get(CODE_ID);
        Assert.assertEquals("You need to verify your email address to activate your account.", testRealmVerifyEmailPage.feedbackMessage().getText());
        String verifyEmailUrl = MailAssert.assertEmailAndGetUrl(FROM, user.getEmail(), "Someone has created a Test account with this email address.", true);
        log.info(("navigating to url from email: " + verifyEmailUrl));
        driver.navigate().to(verifyEmailUrl);
        events.expectRequiredAction(EventType.VERIFY_EMAIL).user(user.getId()).client("account").detail(USERNAME, "test-user@localhost").detail(EMAIL, "test-user@localhost").detail(CODE_ID, mailCodeId).removeDetail(REDIRECT_URI).assertEvent();
        events.expectLogin().client("account").user(user.getId()).session(mailCodeId).detail(USERNAME, "test-user@localhost").removeDetail(REDIRECT_URI).assertEvent();
        URLAssert.assertCurrentUrlStartsWith(accountManagement);
        accountManagement.signOut();
        testRealmLoginPage.form().login(user.getUsername(), "password");
        URLAssert.assertCurrentUrlStartsWith(accountManagement);
    }

    @Test
    public void verifyEmailWithSslWrongCertificate() throws Exception {
        UserRepresentation user = ApiUtil.findUserByUsername(testRealm(), "test-user@localhost");
        SslMailServer.startWithSsl(this.getClass().getClassLoader().getResource(SslMailServer.INVALID_KEY).getFile());
        accountManagement.navigateTo();
        loginPage.form().login(user.getUsername(), "password");
        events.expectRequiredAction(EventType.SEND_VERIFY_EMAIL_ERROR).error(Errors.EMAIL_SEND_FAILED).user(user.getId()).client("account").detail(USERNAME, "test-user@localhost").detail(EMAIL, "test-user@localhost").removeDetail(REDIRECT_URI).assertEvent();
        // Email wasn't send
        org.keycloak.testsuite.Assert.assertNull(SslMailServer.getLastReceivedMessage());
        // Email wasn't send, but we won't notify end user about that. Admin is aware due to the error in the logs and the SEND_VERIFY_EMAIL_ERROR event.
        Assert.assertEquals("You need to verify your email address to activate your account.", testRealmVerifyEmailPage.feedbackMessage().getText());
    }
}

