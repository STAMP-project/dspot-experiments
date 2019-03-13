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


import InfinispanConnectionProvider.REALM_CACHE_NAME;
import InfinispanConnectionProvider.USER_CACHE_NAME;
import Listener.Observation;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.infinispan.Cache;
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryRemoved;
import org.infinispan.notifications.cachelistener.event.CacheEntryRemovedEvent;
import org.jboss.logging.Logger;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.keycloak.connections.infinispan.InfinispanConnectionProvider;
import org.keycloak.models.ClientModel;
import org.keycloak.models.ClientScopeModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.testsuite.KeycloakServer;
import org.keycloak.testsuite.rule.KeycloakRule;


/**
 * Requires execution with cluster (or external JDG) enabled and real database, which will be shared for both cluster nodes. Everything set by system properties:
 *
 * 1) Use those system properties to run against shared MySQL:
 *
 *  -Dkeycloak.connectionsJpa.url=jdbc:mysql://localhost/keycloak -Dkeycloak.connectionsJpa.driver=com.mysql.jdbc.Driver -Dkeycloak.connectionsJpa.user=keycloak
 *  -Dkeycloak.connectionsJpa.password=keycloak
 *
 *
 * 2) Then either choose from:
 *
 * 2.a) Run test with 2 keycloak nodes in cluster. Add this system property for that: -Dkeycloak.connectionsInfinispan.clustered=true
 *
 * 2.b) Run test with 2 keycloak nodes without cluster, but instead with external JDG. Both keycloak servers will send invalidation events to the JDG server and receive the events from this JDG server.
 * They don't communicate with each other. So JDG is man-in-the-middle.
 *
 * This assumes that you have JDG 7.0 server running on localhost with HotRod endpoint on port 11222 (which is default port anyway).
 *
 * You also need to have this cache configured in JDG_HOME/standalone/configuration/standalone.xml to infinispan subsystem :
 *
 *  <local-cache name="work" start="EAGER" batching="false" />
 *
 * Finally, add this system properties when running the test: -Dkeycloak.connectionsInfinispan.remoteStoreEnabled=true -Dkeycloak.connectionsInfinispan.siteName=dc-0
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@Ignore
public class ClusterInvalidationTest {
    protected static final Logger logger = Logger.getLogger(ClusterInvalidationTest.class);

    private static final String REALM_NAME = "test";

    private static final int SLEEP_TIME_MS = Integer.parseInt(System.getProperty("sleep.time", "500"));

    private static ClusterInvalidationTest.TestListener listener1realms;

    private static ClusterInvalidationTest.TestListener listener1users;

    private static ClusterInvalidationTest.TestListener listener2realms;

    private static ClusterInvalidationTest.TestListener listener2users;

    @ClassRule
    public static KeycloakRule server1 = new KeycloakRule(new KeycloakRule.KeycloakSetup() {
        @Override
        public void config(RealmManager manager, RealmModel adminstrationRealm, RealmModel appRealm) {
            InfinispanConnectionProvider infinispan = manager.getSession().getProvider(InfinispanConnectionProvider.class);
            Cache cache = infinispan.getCache(REALM_CACHE_NAME);
            ClusterInvalidationTest.listener1realms = new ClusterInvalidationTest.TestListener("server1 - realms", cache);
            cache.addListener(ClusterInvalidationTest.listener1realms);
            cache = infinispan.getCache(USER_CACHE_NAME);
            ClusterInvalidationTest.listener1users = new ClusterInvalidationTest.TestListener("server1 - users", cache);
            cache.addListener(ClusterInvalidationTest.listener1users);
        }
    });

    @ClassRule
    public static KeycloakRule server2 = new KeycloakRule(new KeycloakRule.KeycloakSetup() {
        @Override
        public void config(RealmManager manager, RealmModel adminstrationRealm, RealmModel appRealm) {
            InfinispanConnectionProvider infinispan = manager.getSession().getProvider(InfinispanConnectionProvider.class);
            Cache cache = infinispan.getCache(REALM_CACHE_NAME);
            ClusterInvalidationTest.listener2realms = new ClusterInvalidationTest.TestListener("server2 - realms", cache);
            cache.addListener(ClusterInvalidationTest.listener2realms);
            cache = infinispan.getCache(USER_CACHE_NAME);
            ClusterInvalidationTest.listener2users = new ClusterInvalidationTest.TestListener("server2 - users", cache);
            cache.addListener(ClusterInvalidationTest.listener2users);
        }
    }) {
        @Override
        protected void configureServer(KeycloakServer server) {
            server.getConfig().setPort(8082);
        }

        @Override
        protected void importRealm() {
        }

        @Override
        protected void removeTestRealms() {
        }
    };

    @Test
    public void testClusterInvalidation() throws Exception {
        cacheEverything();
        ClusterInvalidationTest.clearListeners();
        KeycloakSession session1 = ClusterInvalidationTest.server1.startSession();
        ClusterInvalidationTest.logger.info("UPDATE REALM");
        RealmModel realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        realm.setDisplayName("foo");
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1realms.getInvalidationsAndClear(), 1, 3, realm.getId());
        assertInvalidations(ClusterInvalidationTest.listener2realms.getInvalidationsAndClear(), 1, 3, realm.getId());
        // CREATES
        ClusterInvalidationTest.logger.info("CREATE ROLE");
        realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        realm.addRole("foo-role");
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1realms.getInvalidationsAndClear(), 1, 1, "test.roles");
        assertInvalidations(ClusterInvalidationTest.listener2realms.getInvalidationsAndClear(), 1, 1, "test.roles");
        ClusterInvalidationTest.logger.info("CREATE CLIENT");
        realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        realm.addClient("foo-client");
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1realms.getInvalidationsAndClear(), 1, 1, "test.realm.clients");
        assertInvalidations(ClusterInvalidationTest.listener2realms.getInvalidationsAndClear(), 1, 1, "test.realm.clients");
        ClusterInvalidationTest.logger.info("CREATE GROUP");
        realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        GroupModel group = realm.createGroup("foo-group");
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1realms.getInvalidationsAndClear(), 1, 1, "test.top.groups");
        assertInvalidations(ClusterInvalidationTest.listener2realms.getInvalidationsAndClear(), 1, 1, "test.top.groups");
        ClusterInvalidationTest.logger.info("CREATE CLIENT SCOPE");
        realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        realm.addClientScope("foo-scope");
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1realms.getInvalidationsAndClear(), 2, 3, realm.getId());
        assertInvalidations(ClusterInvalidationTest.listener2realms.getInvalidationsAndClear(), 0, 2);// realm not cached on server2 due to previous invalidation

        // UPDATES
        ClusterInvalidationTest.logger.info("UPDATE ROLE");
        realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        ClientModel testApp = realm.getClientByClientId("test-app");
        RoleModel role = session1.realms().getClientRole(realm, testApp, "customer-user");
        role.setDescription("Foo");
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1realms.getInvalidationsAndClear(), 2, 3, role.getId());
        assertInvalidations(ClusterInvalidationTest.listener2realms.getInvalidationsAndClear(), 2, 3, role.getId());
        ClusterInvalidationTest.logger.info("UPDATE GROUP");
        realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        group = KeycloakModelUtils.findGroupByPath(realm, "/topGroup");
        group.grantRole(role);
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1realms.getInvalidationsAndClear(), 1, 1, group.getId());
        assertInvalidations(ClusterInvalidationTest.listener2realms.getInvalidationsAndClear(), 1, 1, group.getId());
        ClusterInvalidationTest.logger.info("UPDATE CLIENT");
        realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        testApp = realm.getClientByClientId("test-app");
        testApp.setDescription("foo");
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1realms.getInvalidationsAndClear(), 2, 3, testApp.getId());
        assertInvalidations(ClusterInvalidationTest.listener2realms.getInvalidationsAndClear(), 2, 3, testApp.getId());
        // Cache client scope on server2
        KeycloakSession session2 = ClusterInvalidationTest.server2.startSession();
        realm = session2.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        realm.getClientScopes().get(0);
        ClusterInvalidationTest.logger.info("UPDATE CLIENT SCOPE");
        realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        ClientScopeModel clientScope = realm.getClientScopes().get(0);
        clientScope.setDescription("bar");
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1realms.getInvalidationsAndClear(), 1, 1, clientScope.getId());
        assertInvalidations(ClusterInvalidationTest.listener2realms.getInvalidationsAndClear(), 1, 1, clientScope.getId());
        // Nothing yet invalidated in user cache
        assertInvalidations(ClusterInvalidationTest.listener1users.getInvalidationsAndClear(), 0, 0);
        assertInvalidations(ClusterInvalidationTest.listener2users.getInvalidationsAndClear(), 0, 0);
        ClusterInvalidationTest.logger.info("UPDATE USER");
        realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        UserModel user = session1.users().getUserByEmail("keycloak-user@localhost", realm);
        user.setSingleAttribute("foo", "Bar");
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1users.getInvalidationsAndClear(), 1, 5, user.getId(), "test.email.keycloak-user@localhost");
        assertInvalidations(ClusterInvalidationTest.listener2users.getInvalidationsAndClear(), 1, 5, user.getId());
        ClusterInvalidationTest.logger.info("UPDATE USER CONSENTS");
        realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        testApp = realm.getClientByClientId("test-app");
        user = session1.users().getUserByEmail("keycloak-user@localhost", realm);
        session1.users().addConsent(realm, user.getId(), new org.keycloak.models.UserConsentModel(testApp));
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1users.getInvalidationsAndClear(), 1, 1, ((user.getId()) + ".consents"));
        assertInvalidations(ClusterInvalidationTest.listener2users.getInvalidationsAndClear(), 1, 1, ((user.getId()) + ".consents"));
        // REMOVALS
        ClusterInvalidationTest.logger.info("REMOVE USER");
        realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        user = session1.users().getUserByUsername("john-doh@localhost", realm);
        session1.users().removeUser(realm, user);
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1users.getInvalidationsAndClear(), 3, 5, user.getId(), ((user.getId()) + ".consents"), "test.username.john-doh@localhost");
        assertInvalidations(ClusterInvalidationTest.listener2users.getInvalidationsAndClear(), 2, 5, user.getId(), ((user.getId()) + ".consents"));
        cacheEverything();
        ClusterInvalidationTest.logger.info("REMOVE CLIENT SCOPE");
        realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        realm.removeClientScope(clientScope.getId());
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1realms.getInvalidationsAndClear(), 2, 5, realm.getId(), clientScope.getId());
        assertInvalidations(ClusterInvalidationTest.listener2realms.getInvalidationsAndClear(), 2, 5, realm.getId(), clientScope.getId());
        cacheEverything();
        ClusterInvalidationTest.logger.info("REMOVE ROLE");
        realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        role = realm.getRole("user");
        realm.removeRole(role);
        ClientModel thirdparty = session1.realms().getClientByClientId("third-party", realm);
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1realms.getInvalidationsAndClear(), 7, 10, role.getId(), realm.getId(), "test.roles", "test.user.roles", testApp.getId(), thirdparty.getId(), group.getId());
        assertInvalidations(ClusterInvalidationTest.listener2realms.getInvalidationsAndClear(), 7, 10, role.getId(), realm.getId(), "test.roles", "test.user.roles", testApp.getId(), thirdparty.getId(), group.getId());
        // all users invalidated
        assertInvalidations(ClusterInvalidationTest.listener1users.getInvalidationsAndClear(), 10, 100);
        assertInvalidations(ClusterInvalidationTest.listener2users.getInvalidationsAndClear(), 10, 100);
        cacheEverything();
        ClusterInvalidationTest.logger.info("REMOVE GROUP");
        realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        group = realm.getGroupById(group.getId());
        String subgroupId = group.getSubGroups().iterator().next().getId();
        realm.removeGroup(group);
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1realms.getInvalidationsAndClear(), 3, 5, group.getId(), subgroupId, "test.top.groups");
        assertInvalidations(ClusterInvalidationTest.listener2realms.getInvalidationsAndClear(), 3, 5, group.getId(), subgroupId, "test.top.groups");
        // all users invalidated
        assertInvalidations(ClusterInvalidationTest.listener1users.getInvalidationsAndClear(), 10, 100);
        assertInvalidations(ClusterInvalidationTest.listener2users.getInvalidationsAndClear(), 10, 100);
        cacheEverything();
        ClusterInvalidationTest.logger.info("REMOVE CLIENT");
        realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        testApp = realm.getClientByClientId("test-app");
        role = testApp.getRole("customer-user");
        realm.removeClient(testApp.getId());
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1realms.getInvalidationsAndClear(), 8, 12, testApp.getId(), ((testApp.getId()) + ".roles"), role.getId(), ((testApp.getId()) + ".customer-user.roles"), "test.realm.clients", thirdparty.getId());
        assertInvalidations(ClusterInvalidationTest.listener2realms.getInvalidationsAndClear(), 8, 12, testApp.getId(), ((testApp.getId()) + ".roles"), role.getId(), ((testApp.getId()) + ".customer-user.roles"), "test.realm.clients", thirdparty.getId());
        // all users invalidated
        assertInvalidations(ClusterInvalidationTest.listener1users.getInvalidationsAndClear(), 10, 100);
        assertInvalidations(ClusterInvalidationTest.listener2users.getInvalidationsAndClear(), 10, 100);
        cacheEverything();
        ClusterInvalidationTest.logger.info("REMOVE REALM");
        realm = session1.realms().getRealmByName(ClusterInvalidationTest.REALM_NAME);
        session1.realms().removeRealm(realm.getId());
        session1 = commit(ClusterInvalidationTest.server1, session1, true);
        assertInvalidations(ClusterInvalidationTest.listener1realms.getInvalidationsAndClear(), 50, 200, realm.getId(), thirdparty.getId());
        assertInvalidations(ClusterInvalidationTest.listener2realms.getInvalidationsAndClear(), 50, 200, realm.getId(), thirdparty.getId());
        // all users invalidated
        assertInvalidations(ClusterInvalidationTest.listener1users.getInvalidationsAndClear(), 10, 100);
        assertInvalidations(ClusterInvalidationTest.listener2users.getInvalidationsAndClear(), 10, 100);
        // Thread.sleep(10000000);
    }

    @Listener(observation = Observation.PRE)
    public static class TestListener {
        private final String name;

        private final Cache cache;// Just for debugging


        private Map<String, Object> invalidations = new ConcurrentHashMap<>();

        public TestListener(String name, Cache cache) {
            this.name = name;
            this.cache = cache;
        }

        @CacheEntryRemoved
        public void cacheEntryRemoved(CacheEntryRemovedEvent event) {
            ClusterInvalidationTest.logger.infof("%s: Invalidated %s: %s", name, event.getKey(), event.getValue());
            invalidations.put(event.getKey().toString(), event.getValue());
        }

        Map<String, Object> getInvalidationsAndClear() {
            Map<String, Object> newMap = new HashMap<>(invalidations);
            invalidations.clear();
            return newMap;
        }
    }
}

