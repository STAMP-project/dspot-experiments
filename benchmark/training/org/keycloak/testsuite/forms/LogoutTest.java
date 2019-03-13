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
package org.keycloak.testsuite.forms;


import Constants.ACCOUNT_MANAGEMENT_CLIENT_ID;
import Details.REDIRECT_URI;
import Details.USERNAME;
import java.io.Closeable;
import java.io.IOException;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.common.util.Retry;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.OAuthClient;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.auth.page.account.AccountManagement;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.updaters.ClientAttributeUpdater;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class LogoutTest extends AbstractTestRealmKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected AccountManagement accountManagementPage;

    @Test
    public void logoutRedirect() {
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        Assert.assertTrue(appPage.isCurrent());
        String sessionId = events.expectLogin().assertEvent().getSessionId();
        String redirectUri = (oauth.APP_AUTH_ROOT) + "?logout";
        String logoutUrl = oauth.getLogoutUrl().redirectUri(redirectUri).build();
        driver.navigate().to(logoutUrl);
        events.expectLogout(sessionId).detail(REDIRECT_URI, redirectUri).assertEvent();
        Assert.assertEquals(redirectUri, driver.getCurrentUrl());
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        Assert.assertTrue(appPage.isCurrent());
        String sessionId2 = events.expectLogin().assertEvent().getSessionId();
        Assert.assertNotEquals(sessionId, sessionId2);
        driver.navigate().to(logoutUrl);
        events.expectLogout(sessionId2).detail(REDIRECT_URI, redirectUri).assertEvent();
    }

    @Test
    public void logoutSession() {
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        Assert.assertTrue(appPage.isCurrent());
        String sessionId = events.expectLogin().assertEvent().getSessionId();
        String logoutUrl = oauth.getLogoutUrl().sessionState(sessionId).build();
        driver.navigate().to(logoutUrl);
        events.expectLogout(sessionId).removeDetail(REDIRECT_URI).assertEvent();
        Assert.assertEquals(logoutUrl, driver.getCurrentUrl());
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        Assert.assertTrue(appPage.isCurrent());
        String sessionId2 = events.expectLogin().assertEvent().getSessionId();
        Assert.assertNotEquals(sessionId, sessionId2);
    }

    @Test
    public void logoutMultipleSessions() throws IOException {
        // Login session 1
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        Assert.assertTrue(appPage.isCurrent());
        String sessionId = events.expectLogin().assertEvent().getSessionId();
        // Check session 1 logged-in
        oauth.openLoginForm();
        events.expectLogin().session(sessionId).removeDetail(USERNAME).assertEvent();
        // Logout session 1 by redirect
        driver.navigate().to(oauth.getLogoutUrl().redirectUri(oauth.APP_AUTH_ROOT).build());
        events.expectLogout(sessionId).detail(REDIRECT_URI, oauth.APP_AUTH_ROOT).assertEvent();
        // Check session 1 not logged-in
        oauth.openLoginForm();
        loginPage.assertCurrent();
        // Login session 3
        oauth.doLogin("test-user@localhost", "password");
        String sessionId3 = events.expectLogin().assertEvent().getSessionId();
        Assert.assertNotEquals(sessionId, sessionId3);
        // Check session 3 logged-in
        oauth.openLoginForm();
        events.expectLogin().session(sessionId3).removeDetail(USERNAME).assertEvent();
        // Logout session 3 by redirect
        driver.navigate().to(oauth.getLogoutUrl().redirectUri(oauth.APP_AUTH_ROOT).build());
        events.expectLogout(sessionId3).detail(REDIRECT_URI, oauth.APP_AUTH_ROOT).assertEvent();
    }

    // KEYCLOAK-2741
    @Test
    public void logoutWithRememberMe() {
        setRememberMe(true);
        try {
            loginPage.open();
            Assert.assertFalse(loginPage.isRememberMeChecked());
            loginPage.setRememberMe(true);
            Assert.assertTrue(loginPage.isRememberMeChecked());
            loginPage.login("test-user@localhost", "password");
            String sessionId = events.expectLogin().assertEvent().getSessionId();
            // Expire session
            testingClient.testing().removeUserSession("test", sessionId);
            // Assert rememberMe checked and username/email prefilled
            loginPage.open();
            Assert.assertTrue(loginPage.isRememberMeChecked());
            Assert.assertEquals("test-user@localhost", loginPage.getUsername());
            loginPage.login("test-user@localhost", "password");
            // log out
            appPage.openAccount();
            accountManagementPage.signOut();
            // Assert rememberMe not checked nor username/email prefilled
            Assert.assertTrue(loginPage.isCurrent());
            Assert.assertFalse(loginPage.isRememberMeChecked());
            Assert.assertNotEquals("test-user@localhost", loginPage.getUsername());
        } finally {
            setRememberMe(false);
        }
    }

    @Test
    public void logoutSessionWhenLoggedOutByAdmin() {
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        Assert.assertTrue(appPage.isCurrent());
        String sessionId = events.expectLogin().assertEvent().getSessionId();
        adminClient.realm("test").logoutAll();
        String logoutUrl = oauth.getLogoutUrl().sessionState(sessionId).build();
        driver.navigate().to(logoutUrl);
        Assert.assertEquals(logoutUrl, driver.getCurrentUrl());
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        Assert.assertTrue(appPage.isCurrent());
        String sessionId2 = events.expectLogin().assertEvent().getSessionId();
        Assert.assertNotEquals(sessionId, sessionId2);
        driver.navigate().to(logoutUrl);
        events.expectLogout(sessionId2).removeDetail(REDIRECT_URI).assertEvent();
    }

    @Test
    public void logoutUserByAdmin() {
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        Assert.assertTrue(appPage.isCurrent());
        String sessionId = events.expectLogin().assertEvent().getSessionId();
        UserRepresentation user = ApiUtil.findUserByUsername(adminClient.realm("test"), "test-user@localhost");
        org.keycloak.testsuite.Assert.assertEquals(((Object) (0)), user.getNotBefore());
        adminClient.realm("test").users().get(user.getId()).logout();
        Retry.execute(() -> {
            UserRepresentation u = adminClient.realm("test").users().get(user.getId()).toRepresentation();
            org.keycloak.testsuite.Assert.assertTrue(((u.getNotBefore()) > 0));
            loginPage.open();
            loginPage.assertCurrent();
        }, 10, 200);
    }

    // KEYCLOAK-5982
    @Test
    public void testLogoutWhenAccountClientRenamed() throws IOException {
        // Temporarily rename client "account" . Revert it back after the test
        try (Closeable accountClientUpdater = ClientAttributeUpdater.forClient(adminClient, "test", ACCOUNT_MANAGEMENT_CLIENT_ID).setClientId("account-changed").update()) {
            // Assert logout works
            logoutRedirect();
        }
    }
}

