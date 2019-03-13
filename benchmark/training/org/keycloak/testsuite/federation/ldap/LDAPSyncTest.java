/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.testsuite.federation.ldap;


import LDAPConstants.USERNAME_LDAP_ATTRIBUTE;
import LDAPConstants.UUID_LDAP_ATTRIBUTE;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserProvider;
import org.keycloak.models.cache.UserCache;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.services.managers.UserStorageSyncManager;
import org.keycloak.storage.UserStorageProviderModel;
import org.keycloak.storage.ldap.idm.model.LDAPObject;
import org.keycloak.storage.user.SynchronizationResult;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.LDAPRule;
import org.keycloak.testsuite.util.LDAPTestUtils;
import org.keycloak.testsuite.util.WaitUtils;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LDAPSyncTest extends AbstractLDAPTest {
    @ClassRule
    public static LDAPRule ldapRule = new LDAPRule();

    // @Test
    // public void test01runit() throws Exception {
    // Thread.sleep(10000000);
    // }
    @Test
    public void test01LDAPSync() {
        // wait a bit
        WaitUtils.pause(LDAPSyncTest.ldapRule.getSleepTime());
        // Sync 5 users from LDAP
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            UserStorageSyncManager usersSyncManager = new UserStorageSyncManager();
            KeycloakSessionFactory sessionFactory = session.getKeycloakSessionFactory();
            SynchronizationResult syncResult = usersSyncManager.syncAllUsers(sessionFactory, "test", ctx.getLdapModel());
            LDAPTestAsserts.assertSyncEquals(syncResult, 5, 0, 0, 0);
        });
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel testRealm = ctx.getRealm();
            UserProvider userProvider = session.userLocalStorage();
            // Assert users imported
            LDAPTestAsserts.assertUserImported(userProvider, testRealm, "user1", "User1FN", "User1LN", "user1@email.org", "121");
            LDAPTestAsserts.assertUserImported(userProvider, testRealm, "user2", "User2FN", "User2LN", "user2@email.org", "122");
            LDAPTestAsserts.assertUserImported(userProvider, testRealm, "user3", "User3FN", "User3LN", "user3@email.org", "123");
            LDAPTestAsserts.assertUserImported(userProvider, testRealm, "user4", "User4FN", "User4LN", "user4@email.org", "124");
            LDAPTestAsserts.assertUserImported(userProvider, testRealm, "user5", "User5FN", "User5LN", "user5@email.org", "125");
            // Assert lastSync time updated
            Assert.assertTrue(((ctx.getLdapModel().getLastSync()) > 0));
            for (UserStorageProviderModel persistentFedModel : testRealm.getUserStorageProviders()) {
                if (LDAPStorageProviderFactory.PROVIDER_NAME.equals(persistentFedModel.getProviderId())) {
                    Assert.assertTrue(((persistentFedModel.getLastSync()) > 0));
                } else {
                    // Dummy provider has still 0
                    Assert.assertEquals(0, persistentFedModel.getLastSync());
                }
            }
        });
        // wait a bit
        WaitUtils.pause(LDAPSyncTest.ldapRule.getSleepTime());
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel testRealm = ctx.getRealm();
            UserProvider userProvider = session.userLocalStorage();
            UserStorageSyncManager usersSyncManager = new UserStorageSyncManager();
            // Add user to LDAP and update 'user5' in LDAP
            LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), testRealm, "user6", "User6FN", "User6LN", "user6@email.org", null, "126");
            LDAPObject ldapUser5 = ctx.getLdapProvider().loadLDAPUserByUsername(testRealm, "user5");
            // NOTE: Changing LDAP attributes directly here
            ldapUser5.setSingleAttribute(LDAPConstants.EMAIL, "user5Updated@email.org");
            ldapUser5.setSingleAttribute(LDAPConstants.POSTAL_CODE, "521");
            ctx.getLdapProvider().getLdapIdentityStore().update(ldapUser5);
            // Assert still old users in local provider
            LDAPTestAsserts.assertUserImported(userProvider, testRealm, "user5", "User5FN", "User5LN", "user5@email.org", "125");
            Assert.assertNull(userProvider.getUserByUsername("user6", testRealm));
            // Trigger partial sync
            KeycloakSessionFactory sessionFactory = session.getKeycloakSessionFactory();
            SynchronizationResult syncResult = usersSyncManager.syncChangedUsers(sessionFactory, "test", ctx.getLdapModel());
            LDAPTestAsserts.assertSyncEquals(syncResult, 1, 1, 0, 0);
        });
        testingClient.server().run(( session) -> {
            RealmModel testRealm = session.realms().getRealm("test");
            UserProvider userProvider = session.userLocalStorage();
            // Assert users updated in local provider
            LDAPTestAsserts.assertUserImported(userProvider, testRealm, "user5", "User5FN", "User5LN", "user5updated@email.org", "521");
            LDAPTestAsserts.assertUserImported(userProvider, testRealm, "user6", "User6FN", "User6LN", "user6@email.org", "126");
        });
    }

    @Test
    public void test02duplicateUsernameAndEmailSync() {
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            LDAPTestUtils.addLocalUser(session, ctx.getRealm(), "user7", "user7@email.org", "password");
            // Add user to LDAP with duplicated username "user7"
            LDAPObject duplicatedLdapUser = LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), ctx.getRealm(), "user7", "User7FN", "User7LN", "user7-something@email.org", null, "126");
        });
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            // Assert syncing from LDAP fails due to duplicated username
            SynchronizationResult result = new UserStorageSyncManager().syncAllUsers(session.getKeycloakSessionFactory(), "test", ctx.getLdapModel());
            Assert.assertEquals(1, result.getFailed());
            // Remove "user7" from LDAP
            LDAPObject duplicatedLdapUser = ctx.getLdapProvider().loadLDAPUserByUsername(ctx.getRealm(), "user7");
            ctx.getLdapProvider().getLdapIdentityStore().remove(duplicatedLdapUser);
            // Add user to LDAP with duplicated email "user7@email.org"
            duplicatedLdapUser = LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), ctx.getRealm(), "user7-something", "User7FNN", "User7LNL", "user7@email.org", null, "126");
        });
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            // Assert syncing from LDAP fails due to duplicated email
            SynchronizationResult result = new UserStorageSyncManager().syncAllUsers(session.getKeycloakSessionFactory(), "test", ctx.getLdapModel());
            Assert.assertEquals(1, result.getFailed());
            Assert.assertNull(session.userLocalStorage().getUserByUsername("user7-something", ctx.getRealm()));
            // Update LDAP user to avoid duplicated email
            LDAPObject duplicatedLdapUser = ctx.getLdapProvider().loadLDAPUserByUsername(ctx.getRealm(), "user7-something");
            duplicatedLdapUser.setSingleAttribute(LDAPConstants.EMAIL, "user7-changed@email.org");
            ctx.getLdapProvider().getLdapIdentityStore().update(duplicatedLdapUser);
            // Assert user successfully synced now
            result = new UserStorageSyncManager().syncAllUsers(session.getKeycloakSessionFactory(), "test", ctx.getLdapModel());
            Assert.assertEquals(0, result.getFailed());
        });
        // Assert user was imported. Use another transaction for that
        testingClient.server().run(( session) -> {
            RealmModel testRealm = session.realms().getRealm("test");
            LDAPTestAsserts.assertUserImported(session.userLocalStorage(), testRealm, "user7-something", "User7FNN", "User7LNL", "user7-changed@email.org", "126");
        });
    }

    // KEYCLOAK-1571
    @Test
    public void test03SameUUIDAndUsernameSync() {
        String origUuidAttrName = testingClient.server().fetch(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            // Remove all users from model
            for (UserModel user : session.userLocalStorage().getUsers(ctx.getRealm(), true)) {
                session.userLocalStorage().removeUser(ctx.getRealm(), user);
            }
            // Change name of UUID attribute to same like usernameAttribute
            String uidAttrName = ctx.getLdapProvider().getLdapIdentityStore().getConfig().getUsernameLdapAttribute();
            String origUuidAttrNamee = ctx.getLdapModel().get(LDAPConstants.UUID_LDAP_ATTRIBUTE);
            ctx.getLdapModel().put(LDAPConstants.UUID_LDAP_ATTRIBUTE, uidAttrName);
            // Need to change this due to ApacheDS pagination bug (For other LDAP servers, pagination works fine) TODO: Remove once ApacheDS upgraded and pagination is fixed
            ctx.getLdapModel().put(LDAPConstants.BATCH_SIZE_FOR_SYNC, "10");
            ctx.getRealm().updateComponent(ctx.getLdapModel());
            return origUuidAttrNamee;
        }, String.class);
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            KeycloakSessionFactory sessionFactory = session.getKeycloakSessionFactory();
            SynchronizationResult syncResult = new UserStorageSyncManager().syncAllUsers(sessionFactory, "test", ctx.getLdapModel());
            Assert.assertEquals(0, syncResult.getFailed());
        });
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            // Assert users imported with correct LDAP_ID
            LDAPTestAsserts.assertUserImported(session.users(), ctx.getRealm(), "user1", "User1FN", "User1LN", "user1@email.org", "121");
            LDAPTestAsserts.assertUserImported(session.users(), ctx.getRealm(), "user2", "User2FN", "User2LN", "user2@email.org", "122");
            UserModel user1 = session.users().getUserByUsername("user1", ctx.getRealm());
            Assert.assertEquals("user1", user1.getFirstAttribute(LDAPConstants.LDAP_ID));
        });
        // Revert config changes
        ComponentRepresentation ldapRep = testRealm().components().component(AbstractLDAPTest.ldapModelId).toRepresentation();
        if (origUuidAttrName == null) {
            ldapRep.getConfig().remove(UUID_LDAP_ATTRIBUTE);
        } else {
            ldapRep.getConfig().putSingle(UUID_LDAP_ATTRIBUTE, origUuidAttrName);
        }
        testRealm().components().component(AbstractLDAPTest.ldapModelId).update(ldapRep);
    }

    // KEYCLOAK-1728
    @Test
    public void test04MissingLDAPUsernameSync() {
        String origUsernameAttrName = testingClient.server().fetch(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            // Remove all users from model
            for (UserModel user : session.userLocalStorage().getUsers(ctx.getRealm(), true)) {
                System.out.println(("trying to delete user: " + (user.getUsername())));
                UserCache userCache = session.userCache();
                if (userCache != null) {
                    userCache.evict(ctx.getRealm(), user);
                }
                session.userLocalStorage().removeUser(ctx.getRealm(), user);
            }
            // Add street mapper and add some user including street
            ComponentModel streetMapper = LDAPTestUtils.addUserAttributeMapper(ctx.getRealm(), ctx.getLdapModel(), "streetMapper", "street", LDAPConstants.STREET);
            LDAPObject streetUser = LDAPTestUtils.addLDAPUser(ctx.getLdapProvider(), ctx.getRealm(), "user8", "User8FN", "User8LN", "user8@email.org", "user8street", "126");
            // Change name of username attribute name to street
            String origUsernameAttrNamee = ctx.getLdapModel().get(LDAPConstants.USERNAME_LDAP_ATTRIBUTE);
            ctx.getLdapModel().getConfig().putSingle(LDAPConstants.USERNAME_LDAP_ATTRIBUTE, "street");
            // Need to change this due to ApacheDS pagination bug (For other LDAP servers, pagination works fine) TODO: Remove once ApacheDS upgraded and pagination is fixed
            ctx.getLdapModel().put(LDAPConstants.BATCH_SIZE_FOR_SYNC, "10");
            ctx.getRealm().updateComponent(ctx.getLdapModel());
            return origUsernameAttrNamee;
        }, String.class);
        // Just user8 synced. All others failed to sync
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            KeycloakSessionFactory sessionFactory = session.getKeycloakSessionFactory();
            SynchronizationResult syncResult = new UserStorageSyncManager().syncAllUsers(sessionFactory, "test", ctx.getLdapModel());
            Assert.assertEquals(1, syncResult.getAdded());
            Assert.assertTrue(((syncResult.getFailed()) > 0));
        });
        // Revert config changes
        ComponentRepresentation ldapRep = testRealm().components().component(AbstractLDAPTest.ldapModelId).toRepresentation();
        if (origUsernameAttrName == null) {
            ldapRep.getConfig().remove(USERNAME_LDAP_ATTRIBUTE);
        } else {
            ldapRep.getConfig().putSingle(USERNAME_LDAP_ATTRIBUTE, origUsernameAttrName);
        }
        testRealm().components().component(AbstractLDAPTest.ldapModelId).update(ldapRep);
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            // Revert config changes
            ComponentModel streetMapper = LDAPTestUtils.getSubcomponentByName(ctx.getRealm(), ctx.getLdapModel(), "streetMapper");
            ctx.getRealm().removeComponent(streetMapper);
        });
    }
}

