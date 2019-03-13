package org.keycloak.testsuite.federation.storage;


import CachePolicy.DEFAULT;
import CachePolicy.EVICT_WEEKLY;
import CachePolicy.MAX_LIFESPAN;
import CachePolicy.NO_CACHE;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.NotFoundException;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.cache.CachedUserModel;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.actions.RequiredActionEmailVerificationTest;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.RegisterPage;
import org.keycloak.testsuite.pages.VerifyEmailPage;
import org.keycloak.testsuite.util.GreenMailRule;


/**
 *
 *
 * @author tkyjovsk
 */
public class UserStorageTest extends AbstractAuthTest {
    private String memProviderId;

    private String propProviderROId;

    private String propProviderRWId;

    @Rule
    public GreenMailRule greenMail = new GreenMailRule();

    @Page
    protected LoginPage loginPage;

    @Page
    protected RegisterPage registerPage;

    @Page
    protected VerifyEmailPage verifyEmailPage;

    private static final File CONFIG_DIR = new File(System.getProperty("auth.server.config.dir", ""));

    @Test
    public void testLoginSuccess() {
        loginSuccessAndLogout("tbrady", "goat");
        loginSuccessAndLogout("thor", "hammer");
        loginBadPassword("tbrady");
    }

    @Test
    public void testUpdate() {
        UserRepresentation thor = ApiUtil.findUserByUsername(testRealmResource(), "thor");
        // update entity
        thor.setFirstName("Stian");
        thor.setLastName("Thorgersen");
        thor.setEmailVerified(true);
        long thorCreated = (System.currentTimeMillis()) - 100;
        thor.setCreatedTimestamp(thorCreated);
        thor.setEmail("thor@hammer.com");
        thor.setAttributes(new HashMap());
        thor.getAttributes().put("test-attribute", Arrays.asList("value"));
        thor.setRequiredActions(new ArrayList());
        thor.getRequiredActions().add(UPDATE_PROFILE.name());
        testRealmResource().users().get(thor.getId()).update(thor);
        // check entity
        thor = ApiUtil.findUserByUsername(testRealmResource(), "thor");
        Assert.assertEquals("Stian", thor.getFirstName());
        Assert.assertEquals("Thorgersen", thor.getLastName());
        Assert.assertEquals("thor@hammer.com", thor.getEmail());
        Assert.assertTrue(thor.getAttributes().containsKey("test-attribute"));
        Assert.assertEquals(1, thor.getAttributes().get("test-attribute").size());
        Assert.assertEquals("value", thor.getAttributes().get("test-attribute").get(0));
        Assert.assertTrue(thor.isEmailVerified());
        // update group
        GroupRepresentation g = new GroupRepresentation();
        g.setName("my-group");
        String gid = ApiUtil.getCreatedId(testRealmResource().groups().add(g));
        testRealmResource().users().get(thor.getId()).joinGroup(gid);
        // check group
        boolean foundGroup = false;
        for (GroupRepresentation ug : testRealmResource().users().get(thor.getId()).groups()) {
            if (ug.getId().equals(gid)) {
                foundGroup = true;
            }
        }
        Assert.assertTrue(foundGroup);
        // check required actions
        Assert.assertTrue(thor.getRequiredActions().contains(UPDATE_PROFILE.name()));
        // remove req. actions
        thor.getRequiredActions().remove(UPDATE_PROFILE.name());
        testRealmResource().users().get(thor.getId()).update(thor);
        // change pass
        ApiUtil.resetUserPassword(testRealmResource().users().get(thor.getId()), "lightning", false);
        loginSuccessAndLogout("thor", "lightning");
        // update role
        RoleRepresentation r = new RoleRepresentation("foo-role", "foo role", false);
        testRealmResource().roles().create(r);
        ApiUtil.assignRealmRoles(testRealmResource(), thor.getId(), "foo-role");
        // check role
        boolean foundRole = false;
        for (RoleRepresentation rr : user(thor.getId()).roles().getAll().getRealmMappings()) {
            if ("foo-role".equals(rr.getName())) {
                foundRole = true;
                break;
            }
        }
        Assert.assertTrue(foundRole);
        // test removal of provider
        testRealmResource().components().component(propProviderRWId).remove();
        propProviderRWId = addComponent(newPropProviderRW());
        loginSuccessAndLogout("thor", "hammer");
        thor = ApiUtil.findUserByUsername(testRealmResource(), "thor");
        Assert.assertNull(thor.getFirstName());
        Assert.assertNull(thor.getLastName());
        Assert.assertNull(thor.getEmail());
        Assert.assertNull(thor.getAttributes());
        Assert.assertFalse(thor.isEmailVerified());
        foundGroup = false;
        for (GroupRepresentation ug : testRealmResource().users().get(thor.getId()).groups()) {
            if (ug.getId().equals(gid)) {
                foundGroup = true;
            }
        }
        Assert.assertFalse(foundGroup);
        foundRole = false;
        for (RoleRepresentation rr : user(thor.getId()).roles().getAll().getRealmMappings()) {
            if ("foo-role".equals(rr.getName())) {
                foundRole = true;
                break;
            }
        }
        Assert.assertFalse(foundRole);
    }

    @Test
    public void testRegisterWithRequiredEmail() throws Exception {
        try (AutoCloseable c = updateWith(( r) -> {
            Map<String, String> config = new HashMap<>();
            config.put("from", "auto@keycloak.org");
            config.put("host", "localhost");
            config.put("port", "3025");
            r.setSmtpServer(config);
            r.setRegistrationAllowed(true);
            r.setVerifyEmail(true);
        }).update()) {
            testRealmAccountPage.navigateTo();
            loginPage.clickRegister();
            registerPage.register("firstName", "lastName", "email@mail.com", "verifyEmail", "password", "password");
            verifyEmailPage.assertCurrent();
            Assert.assertEquals(1, greenMail.getReceivedMessages().length);
            MimeMessage message = greenMail.getReceivedMessages()[0];
            String verificationUrl = RequiredActionEmailVerificationTest.getPasswordResetEmailLink(message);
            driver.navigate().to(verificationUrl.trim());
            testRealmAccountPage.assertCurrent();
        }
    }

    @Test
    public void testRegistration() {
        UserRepresentation memuser = new UserRepresentation();
        memuser.setUsername("memuser");
        String uid = ApiUtil.createUserAndResetPasswordWithAdminClient(testRealmResource(), memuser, "password");
        loginSuccessAndLogout("memuser", "password");
        loginSuccessAndLogout("memuser", "password");
        loginSuccessAndLogout("memuser", "password");
        memuser = user(uid).toRepresentation();
        Assert.assertNotNull(memuser);
        Assert.assertNotNull(memuser.getOrigin());
        ComponentRepresentation origin = testRealmResource().components().component(memuser.getOrigin()).toRepresentation();
        Assert.assertEquals("memory", origin.getName());
        testRealmResource().users().get(memuser.getId()).remove();
        try {
            user(uid).toRepresentation();// provider doesn't implement UserQueryProvider --> have to lookup by uid

            Assert.fail("`memuser` wasn't removed");
        } catch (NotFoundException nfe) {
            // expected
        }
    }

    @Test
    public void testQuery() {
        Set<UserRepresentation> queried = new HashSet<>();
        int first = 0;
        while ((queried.size()) < 8) {
            List<UserRepresentation> results = testRealmResource().users().search("", first, 3);
            log.debugf("first=%s, results: %s", first, results.size());
            if (results.isEmpty()) {
                break;
            }
            first += results.size();
            queried.addAll(results);
        } 
        Set<String> usernames = new HashSet<>();
        for (UserRepresentation user : queried) {
            usernames.add(user.getUsername());
            log.info(user.getUsername());
        }
        Assert.assertEquals(8, queried.size());
        Assert.assertTrue(usernames.contains("thor"));
        Assert.assertTrue(usernames.contains("zeus"));
        Assert.assertTrue(usernames.contains("apollo"));
        Assert.assertTrue(usernames.contains("perseus"));
        Assert.assertTrue(usernames.contains("tbrady"));
        Assert.assertTrue(usernames.contains("rob"));
        Assert.assertTrue(usernames.contains("jules"));
        Assert.assertTrue(usernames.contains("danny"));
        // test searchForUser
        List<UserRepresentation> users = testRealmResource().users().search("tbrady", 0, Integer.MAX_VALUE);
        Assert.assertTrue(((users.size()) == 1));
        Assert.assertTrue(users.get(0).getUsername().equals("tbrady"));
        // test getGroupMembers()
        GroupRepresentation g = new GroupRepresentation();
        g.setName("gods");
        String gid = ApiUtil.getCreatedId(testRealmResource().groups().add(g));
        UserRepresentation user = ApiUtil.findUserByUsername(testRealmResource(), "apollo");
        testRealmResource().users().get(user.getId()).joinGroup(gid);
        user = ApiUtil.findUserByUsername(testRealmResource(), "zeus");
        testRealmResource().users().get(user.getId()).joinGroup(gid);
        user = ApiUtil.findUserByUsername(testRealmResource(), "thor");
        testRealmResource().users().get(user.getId()).joinGroup(gid);
        queried.clear();
        usernames.clear();
        first = 0;
        while ((queried.size()) < 8) {
            List<UserRepresentation> results = testRealmResource().groups().group(gid).members(first, 1);
            log.debugf("first=%s, results: %s", first, results.size());
            if (results.isEmpty()) {
                break;
            }
            first += results.size();
            queried.addAll(results);
        } 
        for (UserRepresentation u : queried) {
            usernames.add(u.getUsername());
            log.info(u.getUsername());
        }
        Assert.assertEquals(3, queried.size());
        Assert.assertTrue(usernames.contains("apollo"));
        Assert.assertTrue(usernames.contains("zeus"));
        Assert.assertTrue(usernames.contains("thor"));
        // search by single attribute
        testingClient.server().run(( session) -> {
            System.out.println("search by single attribute");
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel userModel = session.users().getUserByUsername("thor", realm);
            userModel.setSingleAttribute("weapon", "hammer");
            List<UserModel> userModels = session.users().searchForUserByUserAttribute("weapon", "hammer", realm);
            for (UserModel u : userModels) {
                System.out.println(u.getUsername());
            }
            Assert.assertEquals(1, userModels.size());
            Assert.assertEquals("thor", userModels.get(0).getUsername());
        });
    }

    /**
     * Test daily eviction behaviour
     */
    @Test
    public void testDailyEviction() {
        // We need to test both cases: eviction the same day, and eviction the next day
        // Simplest is to take full control of the clock
        // set clock to 23:30 of current day
        setTimeOfDay(23, 30, 0);
        // test same day eviction behaviour
        // set eviction at 23:45
        setDailyEvictionTime(23, 45);
        // there are users in cache already from before-test import
        // and they didn't use any time offset clock so they may have timestamps in the 'future'
        // let's clear cache
        testingClient.server().run(( session) -> {
            session.userCache().clear();
        });
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            Assert.assertTrue((user instanceof CachedUserModel));// should be newly cached

        });
        setTimeOfDay(23, 40, 0);
        // lookup user again - make sure it's returned from cache
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            Assert.assertTrue((user instanceof CachedUserModel));// should be returned from cache

        });
        setTimeOfDay(23, 50, 0);
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            Assert.assertFalse((user instanceof CachedUserModel));// should have been invalidated

        });
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            Assert.assertTrue((user instanceof CachedUserModel));// should have been newly cached

        });
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            Assert.assertTrue((user instanceof CachedUserModel));// should be returned from cache

        });
        setTimeOfDay(23, 55, 0);
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            Assert.assertTrue((user instanceof CachedUserModel));// should be returned from cache

        });
        // at 00:30
        // it's next day now. the daily eviction time is now in the future
        setTimeOfDay(0, 30, 0, ((24 * 60) * 60));
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            Assert.assertTrue((user instanceof CachedUserModel));// should be returned from cache - it's still good for almost the whole day

        });
        // at 23:30 next day
        setTimeOfDay(23, 30, 0, ((24 * 60) * 60));
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            Assert.assertTrue((user instanceof CachedUserModel));// should be returned from cache - it's still good until 23:45

        });
        // at 23:50
        setTimeOfDay(23, 50, 0, ((24 * 60) * 60));
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            Assert.assertFalse((user instanceof CachedUserModel));// should be invalidated

        });
        setTimeOfDay(23, 55, 0, ((24 * 60) * 60));
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            Assert.assertTrue((user instanceof CachedUserModel));// should be newly cached

        });
        setTimeOfDay(23, 40, 0, (((2 * 24) * 60) * 60));
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            Assert.assertTrue((user instanceof CachedUserModel));// should be returned from cache

        });
        setTimeOfDay(23, 50, 0, (((2 * 24) * 60) * 60));
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            Assert.assertFalse((user instanceof CachedUserModel));// should be invalidated

        });
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            Assert.assertTrue((user instanceof CachedUserModel));// should be newly cached

        });
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            Assert.assertTrue((user instanceof CachedUserModel));// should be returned from cache

        });
    }

    @Test
    public void testWeeklyEviction() {
        ApiUtil.findUserByUsername(testRealmResource(), "thor");
        // set eviction to 4 days from now
        Calendar eviction = Calendar.getInstance();
        eviction.add(Calendar.HOUR, (4 * 24));
        ComponentRepresentation propProviderRW = testRealmResource().components().component(propProviderRWId).toRepresentation();
        propProviderRW.getConfig().putSingle(CACHE_POLICY, EVICT_WEEKLY.name());
        propProviderRW.getConfig().putSingle(EVICTION_DAY, Integer.toString(eviction.get(Calendar.DAY_OF_WEEK)));
        propProviderRW.getConfig().putSingle(EVICTION_HOUR, Integer.toString(eviction.get(Calendar.HOUR_OF_DAY)));
        propProviderRW.getConfig().putSingle(EVICTION_MINUTE, Integer.toString(eviction.get(Calendar.MINUTE)));
        testRealmResource().components().component(propProviderRWId).update(propProviderRW);
        // now
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            System.out.println(("User class: " + (user.getClass())));
            Assert.assertTrue((user instanceof CachedUserModel));// should still be cached

        });
        setTimeOffset((((2 * 24) * 60) * 60));// 2 days in future

        // now
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            System.out.println(("User class: " + (user.getClass())));
            Assert.assertTrue((user instanceof CachedUserModel));// should still be cached

        });
        setTimeOffset((((5 * 24) * 60) * 60));// 5 days in future

        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            System.out.println(("User class: " + (user.getClass())));
            Assert.assertFalse((user instanceof CachedUserModel));// should be evicted

        });
    }

    @Test
    public void testMaxLifespan() {
        ApiUtil.findUserByUsername(testRealmResource(), "thor");
        // set eviction to 1 hour from now
        ComponentRepresentation propProviderRW = testRealmResource().components().component(propProviderRWId).toRepresentation();
        propProviderRW.getConfig().putSingle(CACHE_POLICY, MAX_LIFESPAN.name());
        propProviderRW.getConfig().putSingle(MAX_LIFESPAN, Long.toString((((1 * 60) * 60) * 1000)));// 1 hour in milliseconds

        testRealmResource().components().component(propProviderRWId).update(propProviderRW);
        // now
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            System.out.println(("User class: " + (user.getClass())));
            Assert.assertTrue((user instanceof CachedUserModel));// should still be cached

        });
        setTimeOffset((((1 / 2) * 60) * 60));// 1/2 hour in future

        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            System.out.println(("User class: " + (user.getClass())));
            Assert.assertTrue((user instanceof CachedUserModel));// should still be cached

        });
        setTimeOffset(((2 * 60) * 60));// 2 hours in future

        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            System.out.println(("User class: " + (user.getClass())));
            Assert.assertFalse((user instanceof CachedUserModel));// should be evicted

        });
    }

    @Test
    public void testNoCache() {
        ApiUtil.findUserByUsername(testRealmResource(), "thor");
        // set NO_CACHE policy
        ComponentRepresentation propProviderRW = testRealmResource().components().component(propProviderRWId).toRepresentation();
        propProviderRW.getConfig().putSingle(CACHE_POLICY, NO_CACHE.name());
        testRealmResource().components().component(propProviderRWId).update(propProviderRW);
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().getUserByUsername("thor", realm);
            System.out.println(("User class: " + (user.getClass())));
            Assert.assertFalse((user instanceof CachedUserModel));// should be evicted

        });
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel thor2 = session.users().getUserByUsername("thor", realm);
            Assert.assertFalse((thor2 instanceof CachedUserModel));
        });
        propProviderRW = testRealmResource().components().component(propProviderRWId).toRepresentation();
        propProviderRW.getConfig().putSingle(CACHE_POLICY, DEFAULT.name());
        propProviderRW.getConfig().remove("evictionHour");
        propProviderRW.getConfig().remove("evictionMinute");
        propProviderRW.getConfig().remove("evictionDay");
        testRealmResource().components().component(propProviderRWId).update(propProviderRW);
        testingClient.server().run(( session) -> {
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel thor = session.users().getUserByUsername("thor", realm);
            System.out.println("Foo");
        });
    }

    @Test
    public void testLifecycle() {
        testingClient.server().run(( session) -> {
            UserMapStorage.allocations.set(0);
            UserMapStorage.closings.set(0);
            RealmModel realm = session.realms().getRealmByName("test");
            UserModel user = session.users().addUser(realm, "memuser");
            Assert.assertNotNull(user);
            user = session.users().getUserByUsername("nonexistent", realm);
            Assert.assertNull(user);
            Assert.assertEquals(1, UserMapStorage.allocations.get());
            Assert.assertEquals(0, UserMapStorage.closings.get());
        });
        testingClient.server().run(( session) -> {
            Assert.assertEquals(1, UserMapStorage.allocations.get());
            Assert.assertEquals(1, UserMapStorage.closings.get());
        });
    }

    @Test
    public void testEntityRemovalHooks() {
        testingClient.server().run(( session) -> {
            UserMapStorage.realmRemovals.set(0);
            UserMapStorage.groupRemovals.set(0);
            UserMapStorage.roleRemovals.set(0);
        });
        // remove group
        GroupRepresentation g1 = new GroupRepresentation();
        g1.setName("group1");
        GroupRepresentation g2 = new GroupRepresentation();
        g2.setName("group2");
        String gid1 = ApiUtil.getCreatedId(testRealmResource().groups().add(g1));
        String gid2 = ApiUtil.getCreatedId(testRealmResource().groups().add(g2));
        testRealmResource().groups().group(gid1).remove();
        testRealmResource().groups().group(gid2).remove();
        testingClient.server().run(( session) -> {
            Assert.assertEquals(2, UserMapStorage.groupRemovals.get());
            UserMapStorage.realmRemovals.set(0);
        });
        // remove role
        RoleRepresentation role1 = new RoleRepresentation();
        role1.setName("role1");
        RoleRepresentation role2 = new RoleRepresentation();
        role2.setName("role2");
        testRealmResource().roles().create(role1);
        testRealmResource().roles().create(role2);
        testRealmResource().roles().get("role1").remove();
        testRealmResource().roles().get("role2").remove();
        testingClient.server().run(( session) -> {
            Assert.assertEquals(2, UserMapStorage.roleRemovals.get());
            UserMapStorage.realmRemovals.set(0);
        });
        // remove realm
        RealmRepresentation testRealmRepresentation = testRealmResource().toRepresentation();
        testRealmResource().remove();
        testingClient.server().run(( session) -> {
            Assert.assertEquals(1, UserMapStorage.realmRemovals.get());
            UserMapStorage.realmRemovals.set(0);
        });
        // Re-create realm
        RealmRepresentation repOrig = testContext.getTestRealmReps().get(0);
        adminClient.realms().create(repOrig);
    }
}

