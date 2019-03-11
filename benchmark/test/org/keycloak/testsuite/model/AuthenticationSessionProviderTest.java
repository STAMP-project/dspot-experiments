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
package org.keycloak.testsuite.model;


import CommonClientSessionModel.ExecutionStatus.SUCCESS;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.keycloak.common.util.Time;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.ClientManager;
import org.keycloak.sessions.AuthenticationSessionModel;
import org.keycloak.sessions.RootAuthenticationSessionModel;
import org.keycloak.testsuite.rule.KeycloakRule;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class AuthenticationSessionProviderTest {
    @ClassRule
    public static KeycloakRule kc = new KeycloakRule();

    private KeycloakSession session;

    private RealmModel realm;

    @Test
    public void testLoginSessionsCRUD() {
        ClientModel client1 = realm.getClientByClientId("test-app");
        UserModel user1 = session.users().getUserByUsername("user1", realm);
        RootAuthenticationSessionModel rootAuthSession = session.authenticationSessions().createRootAuthenticationSession(realm);
        AuthenticationSessionModel authSession = rootAuthSession.createAuthenticationSession(client1);
        String tabId = authSession.getTabId();
        authSession.setAction("foo");
        rootAuthSession.setTimestamp(100);
        resetSession();
        client1 = realm.getClientByClientId("test-app");
        // Ensure session is here
        rootAuthSession = session.authenticationSessions().getRootAuthenticationSession(realm, rootAuthSession.getId());
        authSession = rootAuthSession.getAuthenticationSession(client1, tabId);
        testAuthenticationSession(authSession, client1.getId(), null, "foo");
        Assert.assertEquals(100, rootAuthSession.getTimestamp());
        // Update and commit
        authSession.setAction("foo-updated");
        rootAuthSession.setTimestamp(200);
        authSession.setAuthenticatedUser(session.users().getUserByUsername("user1", realm));
        resetSession();
        // Ensure session was updated
        rootAuthSession = session.authenticationSessions().getRootAuthenticationSession(realm, rootAuthSession.getId());
        client1 = realm.getClientByClientId("test-app");
        authSession = rootAuthSession.getAuthenticationSession(client1, tabId);
        testAuthenticationSession(authSession, client1.getId(), user1.getId(), "foo-updated");
        Assert.assertEquals(200, rootAuthSession.getTimestamp());
        // Remove and commit
        session.authenticationSessions().removeRootAuthenticationSession(realm, rootAuthSession);
        resetSession();
        // Ensure session was removed
        Assert.assertNull(session.authenticationSessions().getRootAuthenticationSession(realm, rootAuthSession.getId()));
    }

    @Test
    public void testAuthenticationSessionRestart() {
        ClientModel client1 = realm.getClientByClientId("test-app");
        UserModel user1 = session.users().getUserByUsername("user1", realm);
        AuthenticationSessionModel authSession = session.authenticationSessions().createRootAuthenticationSession(realm).createAuthenticationSession(client1);
        String tabId = authSession.getTabId();
        authSession.setAction("foo");
        authSession.getParentSession().setTimestamp(100);
        authSession.setAuthenticatedUser(user1);
        authSession.setAuthNote("foo", "bar");
        authSession.setClientNote("foo2", "bar2");
        authSession.setExecutionStatus("123", SUCCESS);
        resetSession();
        // Test restart root authentication session
        client1 = realm.getClientByClientId("test-app");
        authSession = session.authenticationSessions().getRootAuthenticationSession(realm, authSession.getParentSession().getId()).getAuthenticationSession(client1, tabId);
        authSession.getParentSession().restartSession(realm);
        resetSession();
        RootAuthenticationSessionModel rootAuthSession = session.authenticationSessions().getRootAuthenticationSession(realm, authSession.getParentSession().getId());
        Assert.assertNull(rootAuthSession.getAuthenticationSession(client1, tabId));
        Assert.assertTrue(((rootAuthSession.getTimestamp()) > 0));
    }

    @Test
    public void testExpiredAuthSessions() {
        try {
            realm.setAccessCodeLifespan(10);
            realm.setAccessCodeLifespanUserAction(10);
            realm.setAccessCodeLifespanLogin(30);
            // Login lifespan is largest
            String authSessionId = session.authenticationSessions().createRootAuthenticationSession(realm).getId();
            resetSession();
            Time.setOffset(25);
            session.authenticationSessions().removeExpired(realm);
            resetSession();
            Assert.assertNotNull(session.authenticationSessions().getRootAuthenticationSession(realm, authSessionId));
            Time.setOffset(35);
            session.authenticationSessions().removeExpired(realm);
            resetSession();
            Assert.assertNull(session.authenticationSessions().getRootAuthenticationSession(realm, authSessionId));
            // User action is largest
            realm.setAccessCodeLifespanUserAction(40);
            Time.setOffset(0);
            authSessionId = session.authenticationSessions().createRootAuthenticationSession(realm).getId();
            resetSession();
            Time.setOffset(35);
            session.authenticationSessions().removeExpired(realm);
            resetSession();
            Assert.assertNotNull(session.authenticationSessions().getRootAuthenticationSession(realm, authSessionId));
            Time.setOffset(45);
            session.authenticationSessions().removeExpired(realm);
            resetSession();
            Assert.assertNull(session.authenticationSessions().getRootAuthenticationSession(realm, authSessionId));
            // Access code is largest
            realm.setAccessCodeLifespan(50);
            Time.setOffset(0);
            authSessionId = session.authenticationSessions().createRootAuthenticationSession(realm).getId();
            resetSession();
            Time.setOffset(45);
            session.authenticationSessions().removeExpired(realm);
            resetSession();
            Assert.assertNotNull(session.authenticationSessions().getRootAuthenticationSession(realm, authSessionId));
            Time.setOffset(55);
            session.authenticationSessions().removeExpired(realm);
            resetSession();
            Assert.assertNull(session.authenticationSessions().getRootAuthenticationSession(realm, authSessionId));
        } finally {
            Time.setOffset(0);
            realm.setAccessCodeLifespan(60);
            realm.setAccessCodeLifespanUserAction(300);
            realm.setAccessCodeLifespanLogin(1800);
        }
    }

    @Test
    public void testOnRealmRemoved() {
        RealmModel fooRealm = session.realms().createRealm("foo-realm");
        ClientModel fooClient = fooRealm.addClient("foo-client");
        String authSessionId = session.authenticationSessions().createRootAuthenticationSession(realm).getId();
        String authSessionId2 = session.authenticationSessions().createRootAuthenticationSession(fooRealm).getId();
        resetSession();
        new org.keycloak.services.managers.RealmManager(session).removeRealm(session.realms().getRealmByName("foo-realm"));
        resetSession();
        RootAuthenticationSessionModel authSession = session.authenticationSessions().getRootAuthenticationSession(realm, authSessionId);
        Assert.assertNotNull(authSession);
        Assert.assertNull(session.authenticationSessions().getRootAuthenticationSession(realm, authSessionId2));
    }

    @Test
    public void testOnClientRemoved() {
        String authSessionId = session.authenticationSessions().createRootAuthenticationSession(realm).getId();
        AuthenticationSessionModel authSession1 = session.authenticationSessions().getRootAuthenticationSession(realm, authSessionId).createAuthenticationSession(realm.getClientByClientId("test-app"));
        AuthenticationSessionModel authSession2 = session.authenticationSessions().getRootAuthenticationSession(realm, authSessionId).createAuthenticationSession(realm.getClientByClientId("third-party"));
        String tab1Id = authSession1.getTabId();
        String tab2Id = authSession2.getTabId();
        authSession1.setAuthNote("foo", "bar");
        authSession2.setAuthNote("foo", "baz");
        resetSession();
        RootAuthenticationSessionModel rootAuthSession = session.authenticationSessions().getRootAuthenticationSession(realm, authSessionId);
        Assert.assertEquals(2, rootAuthSession.getAuthenticationSessions().size());
        Assert.assertEquals("bar", rootAuthSession.getAuthenticationSession(realm.getClientByClientId("test-app"), tab1Id).getAuthNote("foo"));
        Assert.assertEquals("baz", rootAuthSession.getAuthenticationSession(realm.getClientByClientId("third-party"), tab2Id).getAuthNote("foo"));
        new ClientManager(new org.keycloak.services.managers.RealmManager(session)).removeClient(realm, realm.getClientByClientId("third-party"));
        resetSession();
        rootAuthSession = session.authenticationSessions().getRootAuthenticationSession(realm, authSessionId);
        Assert.assertEquals("bar", rootAuthSession.getAuthenticationSession(realm.getClientByClientId("test-app"), tab1Id).getAuthNote("foo"));
        Assert.assertNull(rootAuthSession.getAuthenticationSession(realm.getClientByClientId("third-party"), tab2Id));
        // Revert client
        realm.addClient("third-party");
    }
}

