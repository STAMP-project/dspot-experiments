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
package org.keycloak.testsuite.admin;


import java.util.List;
import java.util.Map;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.pages.ConsentPage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginPage;


/**
 *
 *
 * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
 */
public class ConsentsTest extends AbstractKeycloakTest {
    static final String REALM_PROV_NAME = "provider";

    static final String REALM_CONS_NAME = "consumer";

    static final String IDP_OIDC_ALIAS = "kc-oidc-idp";

    static final String IDP_OIDC_PROVIDER_ID = "keycloak-oidc";

    static final String CLIENT_ID = "brokerapp";

    static final String CLIENT_SECRET = "secret";

    static final String USER_LOGIN = "testuser";

    static final String USER_EMAIL = "user@localhost.com";

    static final String USER_PASSWORD = "password";

    static final String USER_FIRSTNAME = "User";

    static final String USER_LASTNAME = "Tester";

    @Page
    protected LoginPage accountLoginPage;

    @Page
    protected ConsentPage consentPage;

    @Page
    protected ErrorPage errorPage;

    @Test
    public void testConsents() {
        driver.navigate().to(getAccountUrl(consumerRealmName()));
        log.debug(("Clicking social " + (getIDPAlias())));
        accountLoginPage.clickSocial(getIDPAlias());
        if (!(driver.getCurrentUrl().contains((("/auth/realms/" + (providerRealmName())) + "/")))) {
            log.debug(("Not on provider realm page, url: " + (driver.getCurrentUrl())));
        }
        org.keycloak.testsuite.Assert.assertTrue("Driver should be on the provider realm page right now", driver.getCurrentUrl().contains((("/auth/realms/" + (providerRealmName())) + "/")));
        log.debug("Logging in");
        accountLoginPage.login(getUserLogin(), getUserPassword());
        waitForPage("grant access");
        org.keycloak.testsuite.Assert.assertTrue(consentPage.isCurrent());
        consentPage.confirm();
        org.keycloak.testsuite.Assert.assertTrue("We must be on correct realm right now", driver.getCurrentUrl().contains((("/auth/realms/" + (consumerRealmName())) + "/")));
        UsersResource consumerUsers = adminClient.realm(consumerRealmName()).users();
        org.keycloak.testsuite.Assert.assertTrue("There must be at least one user", ((consumerUsers.count()) > 0));
        List<UserRepresentation> users = consumerUsers.search("", 0, 5);
        UserRepresentation foundUser = null;
        for (UserRepresentation user : users) {
            if ((user.getUsername().equals(getUserLogin())) && (user.getEmail().equals(getUserEmail()))) {
                foundUser = user;
                break;
            }
        }
        org.keycloak.testsuite.Assert.assertNotNull(((("There must be user " + (getUserLogin())) + " in realm ") + (consumerRealmName())), foundUser);
        // get user with the same username from provider realm
        RealmResource providerRealm = adminClient.realm(providerRealmName());
        users = providerRealm.users().search(null, foundUser.getFirstName(), foundUser.getLastName(), null, 0, 1);
        org.keycloak.testsuite.Assert.assertEquals("Same user should be in provider realm", 1, users.size());
        String userId = users.get(0).getId();
        UserResource userResource = providerRealm.users().get(userId);
        // list consents
        List<Map<String, Object>> consents = userResource.getConsents();
        org.keycloak.testsuite.Assert.assertEquals("There should be one consent", 1, consents.size());
        Map<String, Object> consent = consents.get(0);
        org.keycloak.testsuite.Assert.assertEquals(("Consent should be given to " + (ConsentsTest.CLIENT_ID)), ConsentsTest.CLIENT_ID, consent.get("clientId"));
        // list sessions
        List<UserSessionRepresentation> sessions = userResource.getUserSessions();
        org.keycloak.testsuite.Assert.assertEquals("There should be one active session", 1, sessions.size());
        // revoke consent
        userResource.revokeConsent(ConsentsTest.CLIENT_ID);
        // list consents
        consents = userResource.getConsents();
        org.keycloak.testsuite.Assert.assertEquals("There should be no consents", 0, consents.size());
        // list sessions
        sessions = userResource.getUserSessions();
        org.keycloak.testsuite.Assert.assertEquals("There should be no active session", 0, sessions.size());
    }

    @Test
    public void testConsentCancel() {
        // setup account client to require consent
        RealmResource providerRealm = adminClient.realm(providerRealmName());
        ClientResource accountClient = ApiUtil.findClientByClientId(providerRealm, "account");
        ClientRepresentation clientRepresentation = accountClient.toRepresentation();
        clientRepresentation.setConsentRequired(true);
        accountClient.update(clientRepresentation);
        // setup correct realm
        accountPage.setAuthRealm(providerRealmName());
        // navigate to account console and login
        accountPage.navigateTo();
        loginPage.form().login(getUserLogin(), getUserPassword());
        consentPage.assertCurrent();
        consentPage.cancel();
        // check an error page after cancelling the consent
        errorPage.assertCurrent();
        Assert.assertEquals("No access", errorPage.getError());
        // follow the link "back to application"
        errorPage.clickBackToApplication();
        loginPage.form().login(getUserLogin(), getUserPassword());
        consentPage.confirm();
        // successful login
        accountPage.assertCurrent();
    }
}

