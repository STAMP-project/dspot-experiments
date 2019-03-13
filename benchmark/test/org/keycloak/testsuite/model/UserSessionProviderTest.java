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


import AuthenticatedClientSessionModel.Action.LOGGED_OUT;
import OIDCLoginProtocol.STATE_PARAM;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.keycloak.common.util.Time;
import org.keycloak.models.AuthenticatedClientSessionModel;
import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserLoginFailureModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.testsuite.rule.KeycloakRule;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class UserSessionProviderTest {
    @ClassRule
    public static KeycloakRule kc = new KeycloakRule();

    private KeycloakSession session;

    private RealmModel realm;

    @Test
    public void testCreateSessions() {
        int started = Time.currentTime();
        UserSessionModel[] sessions = createSessions();
        UserSessionProviderTest.assertSession(session.sessions().getUserSession(realm, sessions[0].getId()), session.users().getUserByUsername("user1", realm), "127.0.0.1", started, started, "test-app", "third-party");
        UserSessionProviderTest.assertSession(session.sessions().getUserSession(realm, sessions[1].getId()), session.users().getUserByUsername("user1", realm), "127.0.0.2", started, started, "test-app");
        UserSessionProviderTest.assertSession(session.sessions().getUserSession(realm, sessions[2].getId()), session.users().getUserByUsername("user2", realm), "127.0.0.3", started, started, "test-app");
    }

    @Test
    public void testUpdateSession() {
        UserSessionModel[] sessions = createSessions();
        session.sessions().getUserSession(realm, sessions[0].getId()).setLastSessionRefresh(1000);
        resetSession();
        Assert.assertEquals(1000, session.sessions().getUserSession(realm, sessions[0].getId()).getLastSessionRefresh());
    }

    @Test
    public void testUpdateSessionInSameTransaction() {
        UserSessionModel[] sessions = createSessions();
        session.sessions().getUserSession(realm, sessions[0].getId()).setLastSessionRefresh(1000);
        Assert.assertEquals(1000, session.sessions().getUserSession(realm, sessions[0].getId()).getLastSessionRefresh());
    }

    @Test
    public void testRestartSession() {
        int started = Time.currentTime();
        UserSessionModel[] sessions = createSessions();
        Time.setOffset(100);
        UserSessionModel userSession = session.sessions().getUserSession(realm, sessions[0].getId());
        UserSessionProviderTest.assertSession(userSession, session.users().getUserByUsername("user1", realm), "127.0.0.1", started, started, "test-app", "third-party");
        userSession.restartSession(realm, session.users().getUserByUsername("user2", realm), "user2", "127.0.0.6", "form", true, null, null);
        resetSession();
        userSession = session.sessions().getUserSession(realm, sessions[0].getId());
        UserSessionProviderTest.assertSession(userSession, session.users().getUserByUsername("user2", realm), "127.0.0.6", (started + 100), (started + 100));
        Time.setOffset(0);
    }

    @Test
    public void testCreateClientSession() {
        UserSessionModel[] sessions = createSessions();
        Map<String, AuthenticatedClientSessionModel> clientSessions = session.sessions().getUserSession(realm, sessions[0].getId()).getAuthenticatedClientSessions();
        Assert.assertEquals(2, clientSessions.size());
        String clientUUID = realm.getClientByClientId("test-app").getId();
        AuthenticatedClientSessionModel session1 = clientSessions.get(clientUUID);
        Assert.assertEquals(null, session1.getAction());
        Assert.assertEquals(realm.getClientByClientId("test-app").getClientId(), session1.getClient().getClientId());
        Assert.assertEquals(sessions[0].getId(), session1.getUserSession().getId());
        Assert.assertEquals("http://redirect", session1.getRedirectUri());
        Assert.assertEquals("state", session1.getNote(STATE_PARAM));
    }

    @Test
    public void testUpdateClientSession() {
        UserSessionModel[] sessions = createSessions();
        String userSessionId = sessions[0].getId();
        String clientUUID = realm.getClientByClientId("test-app").getId();
        UserSessionModel userSession = session.sessions().getUserSession(realm, userSessionId);
        AuthenticatedClientSessionModel clientSession = userSession.getAuthenticatedClientSessions().get(clientUUID);
        int time = clientSession.getTimestamp();
        Assert.assertEquals(null, clientSession.getAction());
        clientSession.setAction(LOGGED_OUT.name());
        clientSession.setTimestamp((time + 10));
        UserSessionProviderTest.kc.stopSession(session, true);
        session = UserSessionProviderTest.kc.startSession();
        AuthenticatedClientSessionModel updated = session.sessions().getUserSession(realm, userSessionId).getAuthenticatedClientSessions().get(clientUUID);
        Assert.assertEquals(LOGGED_OUT.name(), updated.getAction());
        Assert.assertEquals((time + 10), updated.getTimestamp());
    }

    @Test
    public void testUpdateClientSessionWithGetByClientId() {
        UserSessionModel[] sessions = createSessions();
        String userSessionId = sessions[0].getId();
        String clientUUID = realm.getClientByClientId("test-app").getId();
        UserSessionModel userSession = session.sessions().getUserSession(realm, userSessionId);
        AuthenticatedClientSessionModel clientSession = userSession.getAuthenticatedClientSessionByClient(clientUUID);
        int time = clientSession.getTimestamp();
        Assert.assertEquals(null, clientSession.getAction());
        clientSession.setAction(LOGGED_OUT.name());
        clientSession.setTimestamp((time + 10));
        UserSessionProviderTest.kc.stopSession(session, true);
        session = UserSessionProviderTest.kc.startSession();
        AuthenticatedClientSessionModel updated = session.sessions().getUserSession(realm, userSessionId).getAuthenticatedClientSessionByClient(clientUUID);
        Assert.assertEquals(LOGGED_OUT.name(), updated.getAction());
        Assert.assertEquals((time + 10), updated.getTimestamp());
    }

    @Test
    public void testUpdateClientSessionInSameTransaction() {
        UserSessionModel[] sessions = createSessions();
        String userSessionId = sessions[0].getId();
        String clientUUID = realm.getClientByClientId("test-app").getId();
        UserSessionModel userSession = session.sessions().getUserSession(realm, userSessionId);
        AuthenticatedClientSessionModel clientSession = userSession.getAuthenticatedClientSessionByClient(clientUUID);
        clientSession.setAction(LOGGED_OUT.name());
        clientSession.setNote("foo", "bar");
        AuthenticatedClientSessionModel updated = session.sessions().getUserSession(realm, userSessionId).getAuthenticatedClientSessionByClient(clientUUID);
        Assert.assertEquals(LOGGED_OUT.name(), updated.getAction());
        Assert.assertEquals("bar", updated.getNote("foo"));
    }

    @Test
    public void testGetUserSessions() {
        UserSessionModel[] sessions = createSessions();
        UserSessionProviderTest.assertSessions(session.sessions().getUserSessions(realm, session.users().getUserByUsername("user1", realm)), sessions[0], sessions[1]);
        UserSessionProviderTest.assertSessions(session.sessions().getUserSessions(realm, session.users().getUserByUsername("user2", realm)), sessions[2]);
    }

    @Test
    public void testRemoveUserSessionsByUser() {
        UserSessionModel[] sessions = createSessions();
        Map<String, Integer> clientSessionsKept = new HashMap<>();
        for (UserSessionModel s : sessions) {
            s = session.sessions().getUserSession(realm, s.getId());
            if (!(s.getUser().getUsername().equals("user1"))) {
                clientSessionsKept.put(s.getId(), s.getAuthenticatedClientSessions().keySet().size());
            }
        }
        session.sessions().removeUserSessions(realm, session.users().getUserByUsername("user1", realm));
        resetSession();
        Assert.assertTrue(session.sessions().getUserSessions(realm, session.users().getUserByUsername("user1", realm)).isEmpty());
        List<UserSessionModel> userSessions = session.sessions().getUserSessions(realm, session.users().getUserByUsername("user2", realm));
        Assert.assertFalse(userSessions.isEmpty());
        Assert.assertEquals(userSessions.size(), clientSessionsKept.size());
        for (UserSessionModel userSession : userSessions) {
            Assert.assertEquals(((int) (clientSessionsKept.get(userSession.getId()))), userSession.getAuthenticatedClientSessions().size());
        }
    }

    @Test
    public void testRemoveUserSession() {
        UserSessionModel userSession = createSessions()[0];
        session.sessions().removeUserSession(realm, userSession);
        resetSession();
        Assert.assertNull(session.sessions().getUserSession(realm, userSession.getId()));
    }

    @Test
    public void testRemoveUserSessionsByRealm() {
        UserSessionModel[] sessions = createSessions();
        session.sessions().removeUserSessions(realm);
        resetSession();
        Assert.assertTrue(session.sessions().getUserSessions(realm, session.users().getUserByUsername("user1", realm)).isEmpty());
        Assert.assertTrue(session.sessions().getUserSessions(realm, session.users().getUserByUsername("user2", realm)).isEmpty());
    }

    @Test
    public void testOnClientRemoved() {
        UserSessionModel[] sessions = createSessions();
        String thirdPartyClientUUID = realm.getClientByClientId("third-party").getId();
        Map<String, Set<String>> clientSessionsKept = new HashMap<>();
        for (UserSessionModel s : sessions) {
            Set<String> clientUUIDS = new HashSet(s.getAuthenticatedClientSessions().keySet());
            clientUUIDS.remove(thirdPartyClientUUID);// This client will be later removed, hence his clientSessions too

            clientSessionsKept.put(s.getId(), clientUUIDS);
        }
        realm.removeClient(thirdPartyClientUUID);
        resetSession();
        for (UserSessionModel s : sessions) {
            s = session.sessions().getUserSession(realm, s.getId());
            Set<String> clientUUIDS = s.getAuthenticatedClientSessions().keySet();
            Assert.assertEquals(clientUUIDS, clientSessionsKept.get(s.getId()));
        }
        // Revert client
        realm.addClient("third-party");
    }

    @Test
    public void testRemoveUserSessionsByExpired() {
        session.sessions().getUserSessions(realm, session.users().getUserByUsername("user1", realm));
        ClientModel client = realm.getClientByClientId("test-app");
        try {
            Set<String> expired = new HashSet<String>();
            Time.setOffset((-((realm.getSsoSessionMaxLifespan()) + 1)));
            UserSessionModel userSession = session.sessions().createUserSession(realm, session.users().getUserByUsername("user1", realm), "user1", "127.0.0.1", "form", true, null, null);
            expired.add(userSession.getId());
            AuthenticatedClientSessionModel clientSession = session.sessions().createClientSession(realm, client, userSession);
            Assert.assertEquals(userSession, clientSession.getUserSession());
            Time.setOffset(0);
            UserSessionModel s = session.sessions().createUserSession(realm, session.users().getUserByUsername("user2", realm), "user2", "127.0.0.1", "form", true, null, null);
            // s.setLastSessionRefresh(Time.currentTime() - (realm.getSsoSessionIdleTimeout() + 1));
            s.setLastSessionRefresh(0);
            expired.add(s.getId());
            Set<String> valid = new HashSet<String>();
            Set<String> validClientSessions = new HashSet<String>();
            userSession = session.sessions().createUserSession(realm, session.users().getUserByUsername("user1", realm), "user1", "127.0.0.1", "form", true, null, null);
            valid.add(userSession.getId());
            validClientSessions.add(session.sessions().createClientSession(realm, client, userSession).getId());
            resetSession();
            session.sessions().removeExpired(realm);
            resetSession();
            for (String e : expired) {
                Assert.assertNull(session.sessions().getUserSession(realm, e));
            }
            for (String v : valid) {
                UserSessionModel userSessionLoaded = session.sessions().getUserSession(realm, v);
                Assert.assertNotNull(userSessionLoaded);
                Assert.assertEquals(1, userSessionLoaded.getAuthenticatedClientSessions().size());
                Assert.assertNotNull(userSessionLoaded.getAuthenticatedClientSessions().get(client.getId()));
            }
        } finally {
            Time.setOffset(0);
        }
    }

    // KEYCLOAK-2508
    @Test
    public void testRemovingExpiredSession() {
        UserSessionModel[] sessions = createSessions();
        try {
            Time.setOffset(3600000);
            UserSessionModel userSession = sessions[0];
            RealmModel realm = userSession.getRealm();
            session.sessions().removeExpired(realm);
            resetSession();
            // Assert no exception is thrown here
            session.sessions().removeUserSession(realm, userSession);
        } finally {
            Time.setOffset(0);
        }
    }

    @Test
    public void testGetByClient() {
        UserSessionModel[] sessions = createSessions();
        UserSessionProviderTest.assertSessions(session.sessions().getUserSessions(realm, realm.getClientByClientId("test-app")), sessions[0], sessions[1], sessions[2]);
        UserSessionProviderTest.assertSessions(session.sessions().getUserSessions(realm, realm.getClientByClientId("third-party")), sessions[0]);
    }

    @Test
    public void testGetByClientPaginated() {
        try {
            for (int i = 0; i < 25; i++) {
                Time.setOffset(i);
                UserSessionModel userSession = session.sessions().createUserSession(realm, session.users().getUserByUsername("user1", realm), "user1", ("127.0.0." + i), "form", false, null, null);
                AuthenticatedClientSessionModel clientSession = session.sessions().createClientSession(realm, realm.getClientByClientId("test-app"), userSession);
                clientSession.setRedirectUri("http://redirect");
                clientSession.setNote(STATE_PARAM, "state");
                clientSession.setTimestamp(userSession.getStarted());
                userSession.setLastSessionRefresh(userSession.getStarted());
            }
        } finally {
            Time.setOffset(0);
        }
        resetSession();
        assertPaginatedSession(realm, realm.getClientByClientId("test-app"), 0, 1, 1);
        assertPaginatedSession(realm, realm.getClientByClientId("test-app"), 0, 10, 10);
        assertPaginatedSession(realm, realm.getClientByClientId("test-app"), 10, 10, 10);
        assertPaginatedSession(realm, realm.getClientByClientId("test-app"), 20, 10, 5);
        assertPaginatedSession(realm, realm.getClientByClientId("test-app"), 30, 10, 0);
    }

    @Test
    public void testCreateAndGetInSameTransaction() {
        ClientModel client = realm.getClientByClientId("test-app");
        UserSessionModel userSession = session.sessions().createUserSession(realm, session.users().getUserByUsername("user1", realm), "user1", "127.0.0.2", "form", true, null, null);
        AuthenticatedClientSessionModel clientSession = createClientSession(client, userSession, "http://redirect", "state");
        UserSessionModel userSessionLoaded = session.sessions().getUserSession(realm, userSession.getId());
        AuthenticatedClientSessionModel clientSessionLoaded = userSessionLoaded.getAuthenticatedClientSessions().get(client.getId());
        Assert.assertNotNull(userSessionLoaded);
        Assert.assertNotNull(clientSessionLoaded);
        Assert.assertEquals(userSession.getId(), clientSessionLoaded.getUserSession().getId());
        Assert.assertEquals(1, userSessionLoaded.getAuthenticatedClientSessions().size());
    }

    @Test
    public void testAuthenticatedClientSessions() {
        UserSessionModel userSession = session.sessions().createUserSession(realm, session.users().getUserByUsername("user1", realm), "user1", "127.0.0.2", "form", true, null, null);
        ClientModel client1 = realm.getClientByClientId("test-app");
        ClientModel client2 = realm.getClientByClientId("third-party");
        // Create client1 session
        AuthenticatedClientSessionModel clientSession1 = session.sessions().createClientSession(realm, client1, userSession);
        clientSession1.setAction("foo1");
        clientSession1.setTimestamp(100);
        // Create client2 session
        AuthenticatedClientSessionModel clientSession2 = session.sessions().createClientSession(realm, client2, userSession);
        clientSession2.setAction("foo2");
        clientSession2.setTimestamp(200);
        // commit
        resetSession();
        // Ensure sessions are here
        userSession = session.sessions().getUserSession(realm, userSession.getId());
        Map<String, AuthenticatedClientSessionModel> clientSessions = userSession.getAuthenticatedClientSessions();
        Assert.assertEquals(2, clientSessions.size());
        testAuthenticatedClientSession(clientSessions.get(client1.getId()), "test-app", userSession.getId(), "foo1", 100);
        testAuthenticatedClientSession(clientSessions.get(client2.getId()), "third-party", userSession.getId(), "foo2", 200);
        // Update session1
        clientSessions.get(client1.getId()).setAction("foo1-updated");
        // commit
        resetSession();
        // Ensure updated
        userSession = session.sessions().getUserSession(realm, userSession.getId());
        clientSessions = userSession.getAuthenticatedClientSessions();
        testAuthenticatedClientSession(clientSessions.get(client1.getId()), "test-app", userSession.getId(), "foo1-updated", 100);
        // Rewrite session2
        clientSession2 = session.sessions().createClientSession(realm, client2, userSession);
        clientSession2.setAction("foo2-rewrited");
        clientSession2.setTimestamp(300);
        // commit
        resetSession();
        // Ensure updated
        userSession = session.sessions().getUserSession(realm, userSession.getId());
        clientSessions = userSession.getAuthenticatedClientSessions();
        Assert.assertEquals(2, clientSessions.size());
        testAuthenticatedClientSession(clientSessions.get(client1.getId()), "test-app", userSession.getId(), "foo1-updated", 100);
        testAuthenticatedClientSession(clientSessions.get(client2.getId()), "third-party", userSession.getId(), "foo2-rewrited", 300);
        // remove session
        clientSession1 = userSession.getAuthenticatedClientSessions().get(client1.getId());
        clientSession1.detachFromUserSession();
        // Commit and ensure removed
        resetSession();
        userSession = session.sessions().getUserSession(realm, userSession.getId());
        clientSessions = userSession.getAuthenticatedClientSessions();
        Assert.assertEquals(1, clientSessions.size());
        Assert.assertNull(clientSessions.get(client1.getId()));
    }

    @Test
    public void testGetCountByClient() {
        createSessions();
        Assert.assertEquals(3, session.sessions().getActiveUserSessions(realm, realm.getClientByClientId("test-app")));
        Assert.assertEquals(1, session.sessions().getActiveUserSessions(realm, realm.getClientByClientId("third-party")));
    }

    @Test
    public void loginFailures() {
        UserLoginFailureModel failure1 = session.sessions().addUserLoginFailure(realm, "user1");
        failure1.incrementFailures();
        UserLoginFailureModel failure2 = session.sessions().addUserLoginFailure(realm, "user2");
        failure2.incrementFailures();
        failure2.incrementFailures();
        resetSession();
        failure1 = session.sessions().getUserLoginFailure(realm, "user1");
        Assert.assertEquals(1, failure1.getNumFailures());
        failure2 = session.sessions().getUserLoginFailure(realm, "user2");
        Assert.assertEquals(2, failure2.getNumFailures());
        resetSession();
        // Add the failure, which already exists
        failure1 = session.sessions().addUserLoginFailure(realm, "user1");
        failure1.incrementFailures();
        resetSession();
        failure1 = session.sessions().getUserLoginFailure(realm, "user1");
        Assert.assertEquals(2, failure1.getNumFailures());
        failure1 = session.sessions().getUserLoginFailure(realm, "user1");
        failure1.clearFailures();
        resetSession();
        failure1 = session.sessions().getUserLoginFailure(realm, "user1");
        Assert.assertEquals(0, failure1.getNumFailures());
        session.sessions().removeUserLoginFailure(realm, "user1");
        resetSession();
        Assert.assertNull(session.sessions().getUserLoginFailure(realm, "user1"));
        session.sessions().removeAllUserLoginFailures(realm);
        resetSession();
        Assert.assertNull(session.sessions().getUserLoginFailure(realm, "user2"));
    }

    @Test
    public void testOnUserRemoved() {
        createSessions();
        UserModel user1 = session.users().getUserByUsername("user1", realm);
        UserModel user2 = session.users().getUserByUsername("user2", realm);
        session.sessions().addUserLoginFailure(realm, user1.getId());
        session.sessions().addUserLoginFailure(realm, user2.getId());
        resetSession();
        user1 = session.users().getUserByUsername("user1", realm);
        new org.keycloak.models.UserManager(session).removeUser(realm, user1);
        resetSession();
        Assert.assertTrue(session.sessions().getUserSessions(realm, user1).isEmpty());
        Assert.assertFalse(session.sessions().getUserSessions(realm, session.users().getUserByUsername("user2", realm)).isEmpty());
        Assert.assertNull(session.sessions().getUserLoginFailure(realm, user1.getId()));
        Assert.assertNotNull(session.sessions().getUserLoginFailure(realm, user2.getId()));
    }
}

