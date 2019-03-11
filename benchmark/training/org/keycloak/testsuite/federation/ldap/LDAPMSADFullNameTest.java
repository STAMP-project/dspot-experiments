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


import AppPage.RequestType.AUTH_RESPONSE;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.LDAPRule;
import org.keycloak.testsuite.util.LDAPTestConfiguration;


/**
 * Test for the MSAD setup with usernameAttribute=sAMAccountName, rdnAttribute=cn and fullNameMapper mapped to cn
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LDAPMSADFullNameTest extends AbstractLDAPTest {
    // Run this test just on MSAD and just when sAMAccountName is mapped to username
    @ClassRule
    public static LDAPRule ldapRule = new LDAPRule().assumeTrue((LDAPTestConfiguration ldapConfig) -> {
        String vendor = ldapConfig.getLDAPConfig().get(LDAPConstants.VENDOR);
        if (!(vendor.equals(LDAPConstants.VENDOR_ACTIVE_DIRECTORY))) {
            return false;
        }
        String usernameAttr = ldapConfig.getLDAPConfig().get(LDAPConstants.USERNAME_LDAP_ATTRIBUTE);
        return usernameAttr.equalsIgnoreCase(LDAPConstants.SAM_ACCOUNT_NAME);
    });

    // @Test
    // public void test01Sleep() throws Exception {
    // Thread.sleep(1000000);
    // }
    @Test
    public void test01_addUserWithoutFullName() {
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            UserModel john = session.users().addUser(appRealm, "johnkeycloak");
            john.setEmail("johnkeycloak@email.cz");
        });
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            UserModel john = session.users().getUserByUsername("johnkeycloak", appRealm);
            Assert.assertNotNull(john.getFederationLink());
            assertDnStartsWith(session, ctx, john, "cn=johnkeycloak");
            session.users().removeUser(appRealm, john);
        });
    }

    @Test
    public void test02_registerUserWithFullName() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        registerPage.register("Johny", "Anthony", "johnyanth@check.cz", "johnkeycloak", "Password1", "Password1");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            UserModel john = session.users().getUserByUsername("johnkeycloak", appRealm);
            assertUser(session, ctx, john, "johnkeycloak", "Johny", "Anthony", true, "cn=Johny Anthony");
            session.users().removeUser(appRealm, john);
        });
    }

    @Test
    public void test03_addUserWithFirstNameOnly() {
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            UserModel john = session.users().addUser(appRealm, "johnkeycloak");
            john.setEmail("johnkeycloak@email.cz");
            john.setFirstName("Johnyyy");
            john.setEnabled(true);
        });
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            UserModel john = session.users().getUserByUsername("johnkeycloak", appRealm);
            assertUser(session, ctx, john, "johnkeycloak", "Johnyyy", "", true, "cn=Johnyyy");
            session.users().removeUser(appRealm, john);
        });
    }

    @Test
    public void test04_addUserWithLastNameOnly() {
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            UserModel john = session.users().addUser(appRealm, "johnkeycloak");
            john.setEmail("johnkeycloak@email.cz");
            john.setLastName("Anthonyy");
            john.setEnabled(true);
        });
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            UserModel john = session.users().getUserByUsername("johnkeycloak", appRealm);
            assertUser(session, ctx, john, "johnkeycloak", "", "Anthonyy", true, "cn=Anthonyy");
            session.users().removeUser(appRealm, john);
        });
    }

    @Test
    public void test05_registerUserWithFullNameSpecialChars() {
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        registerPage.register("Jo?,o", "Ba???", "johnyanth@check.cz", "johnkeycloak", "Password1", "Password1");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            UserModel john = session.users().getUserByUsername("johnkeycloak", appRealm);
            assertUser(session, ctx, john, "johnkeycloak", "Jo?,o", "Ba???", true, "cn=Jo\u017e\\,o Ba\u0159\u00ed\u010d");
            session.users().removeUser(appRealm, john);
        });
    }

    @Test
    public void test06_conflicts() {
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            UserModel john = session.users().addUser(appRealm, "existingkc");
            john.setFirstName("John");
            john.setLastName("Existing");
            john.setEnabled(true);
            UserModel john2 = session.users().addUser(appRealm, "existingkc1");
            john2.setEnabled(true);
        });
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        registerPage.register("John", "Existing", "johnyanth@check.cz", "existingkc", "Password1", "Password1");
        Assert.assertEquals("Username already exists.", registerPage.getError());
        registerPage.register("John", "Existing", "johnyanth@check.cz", "existingkc2", "Password1", "Password1");
        appPage.logout();
        loginPage.open();
        loginPage.clickRegister();
        registerPage.assertCurrent();
        registerPage.register("John", "Existing", "johnyanth2@check.cz", "existingkc3", "Password1", "Password1");
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            UserModel existingKc = session.users().getUserByUsername("existingkc", appRealm);
            assertUser(session, ctx, existingKc, "existingkc", "John", "Existing", true, "cn=John Existing");
            UserModel existingKc1 = session.users().getUserByUsername("existingkc1", appRealm);
            assertUser(session, ctx, existingKc1, "existingkc1", "", "", true, "cn=existingkc1");
            UserModel existingKc2 = session.users().getUserByUsername("existingkc2", appRealm);
            assertUser(session, ctx, existingKc2, "existingkc2", "John", "Existing", true, "cn=John Existing0");
            UserModel existingKc3 = session.users().getUserByUsername("existingkc3", appRealm);
            assertUser(session, ctx, existingKc3, "existingkc3", "John", "Existing", true, "cn=John Existing1");
            session.users().removeUser(appRealm, existingKc);
            session.users().removeUser(appRealm, existingKc1);
            session.users().removeUser(appRealm, existingKc2);
            session.users().removeUser(appRealm, existingKc3);
        });
    }
}

