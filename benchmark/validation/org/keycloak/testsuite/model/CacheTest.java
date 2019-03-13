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


import org.junit.Test;
import org.keycloak.models.ClientModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.models.cache.infinispan.ClientAdapter;
import org.keycloak.models.cache.infinispan.RealmAdapter;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.Assert;


/**
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CacheTest extends AbstractTestRealmKeycloakTest {
    private ClientModel testApp = null;

    private int grantedRolesCount = 0;

    private RealmModel realm = null;

    private UserModel user = null;

    @Test
    public void testStaleCache() throws Exception {
        testingClient.server().run(( session) -> {
            String appId = null;
            {
                // load up cache
                RealmModel realm = session.realms().getRealmByName("test");
                assertTrue((realm instanceof RealmAdapter));
                ClientModel testApp = realm.getClientByClientId("test-app");
                assertTrue((testApp instanceof ClientAdapter));
                assertNotNull(testApp);
                appId = testApp.getId();
                assertTrue(testApp.isEnabled());
                // update realm, then get an AppModel and change it.  The AppModel would not be a cache adapter
                // KEYCLOAK-1240 - obtain the realm via session.realms().getRealms()
                realm = null;
                List<RealmModel> realms = session.realms().getRealms();
                for (RealmModel current : realms) {
                    assertTrue((current instanceof RealmAdapter));
                    if ("test".equals(current.getName())) {
                        realm = current;
                        break;
                    }
                }
                realm.setAccessCodeLifespanLogin(200);
                testApp = realm.getClientByClientId("test-app");
                assertNotNull(testApp);
                testApp.setEnabled(false);
                // make sure that app cache was flushed and enabled changed
                realm = session.realms().getRealmByName("test");
                Assert.assertEquals(200, realm.getAccessCodeLifespanLogin());
                testApp = session.realms().getClientById(appId, realm);
                Assert.assertFalse(testApp.isEnabled());
            }
        });
    }

    @Test
    public void testAddUserNotAddedToCache() {
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().addUser(realm, "testAddUserNotAddedToCache");
            user.setFirstName("firstName");
            user.addRequiredAction(UserModel.RequiredAction.CONFIGURE_TOTP);
            UserSessionModel userSession = session.sessions().createUserSession("123", realm, user, "testAddUserNotAddedToCache", "127.0.0.1", "auth", false, null, null);
            user = userSession.getUser();
            user.setLastName("lastName");
            assertNotNull(user.getLastName());
        });
    }

    // KEYCLOAK-1842
    @Test
    public void testRoleMappingsInvalidatedWhenClientRemoved() {
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().addUser(realm, "joel");
            ClientModel client = realm.addClient("foo");
            RoleModel fooRole = client.addRole("foo-role");
            user.grantRole(fooRole);
        });
        int gRolesCount = 0;
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("joel", realm);
            int grantedRolesCount = user.getRoleMappings().size();
            ClientModel client = realm.getClientByClientId("foo");
            realm.removeClient(client.getId());
            realm = session.realms().getRealmByName("test");
            user = session.users().getUserByUsername("joel", realm);
            Set<RoleModel> roles = user.getRoleMappings();
            for (RoleModel role : roles) {
                Assert.assertNotNull(role.getContainer());
            }
            Assert.assertEquals(roles.size(), (grantedRolesCount - 1));
        });
    }
}

