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
import OAuth2Constants.CODE;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.GroupModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.LDAPRule;
import org.keycloak.testsuite.util.LDAPTestConfiguration;
import org.keycloak.testsuite.util.LDAPTestUtils;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LDAPSpecialCharsTest extends AbstractLDAPTest {
    // Skip this test for MSAD with sAMAccountName as it is not allowed to use specialCharacters in sAMAccountName attribute
    @ClassRule
    public static LDAPRule ldapRule = new LDAPRule().assumeTrue((LDAPTestConfiguration ldapConfig) -> {
        String vendor = ldapConfig.getLDAPConfig().get(LDAPConstants.VENDOR);
        String usernameAttr = ldapConfig.getLDAPConfig().get(LDAPConstants.USERNAME_LDAP_ATTRIBUTE);
        boolean skip = (vendor.equals(LDAPConstants.VENDOR_ACTIVE_DIRECTORY)) && (usernameAttr.equalsIgnoreCase(LDAPConstants.SAM_ACCOUNT_NAME));
        return !skip;
    });

    @Test
    public void test01_userSearch() {
        List<UserRepresentation> users = adminClient.realm("test").users().search("j*", 0, 10);
        assertContainsUsername(users, "jamees,key*clo?ak)ppp");
        assertContainsUsername(users, "jameskeycloak");
        assertContainsUsername(users, "johnkeycloak");
    }

    @Test
    public void test02_loginWithSpecialCharacter() {
        // Fail login with wildcard
        loginPage.open();
        loginPage.login("john*", "Password1");
        Assert.assertEquals("Invalid username or password.", loginPage.getError());
        // Fail login with wildcard
        loginPage.login("j*", "Password1");
        Assert.assertEquals("Invalid username or password.", loginPage.getError());
        // Success login as username exactly match
        loginPage.login("jamees,key*clo?ak)ppp", "Password1");
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        Assert.assertNotNull(oauth.getCurrentQuery().get(CODE));
    }

    @Test
    public void test03_specialCharUserJoiningSpecialCharGroup() {
        testingClient.server().run(( session) -> {
            LDAPTestContext ctx = LDAPTestContext.init(session);
            RealmModel appRealm = ctx.getRealm();
            ComponentModel mapperModel = LDAPTestUtils.getSubcomponentByName(appRealm, ctx.getLdapModel(), "groupsMapper");
            LDAPTestUtils.updateGroupMapperConfigOptions(mapperModel, GroupMapperConfig.MODE, LDAPGroupMapperMode.LDAP_ONLY.toString());
            appRealm.updateComponent(mapperModel);
            UserModel specialUser = session.users().getUserByUsername("jamees,key*clo?ak)ppp", appRealm);
            Assert.assertNotNull(specialUser);
            // 1 - Grant some groups in LDAP
            // This group should already exists as it was imported from LDAP
            GroupModel specialGroup = KeycloakModelUtils.findGroupByPath(appRealm, "/group-spec,ia*l_charac?ter)s");
            Assert.assertNotNull(specialGroup);
            specialUser.joinGroup(specialGroup);
            GroupModel groupWithSlashes = KeycloakModelUtils.findGroupByPath(appRealm, "/group/with/three/slashes");
            Assert.assertNotNull(groupWithSlashes);
            specialUser.joinGroup(groupWithSlashes);
            // 2 - Check that group mappings are in LDAP and hence available through federation
            Set<GroupModel> userGroups = specialUser.getGroups();
            Assert.assertEquals(2, userGroups.size());
            Assert.assertTrue(userGroups.contains(specialGroup));
            // 3 - Check through userProvider
            List<UserModel> groupMembers = session.users().getGroupMembers(appRealm, specialGroup, 0, 10);
            Assert.assertEquals(1, groupMembers.size());
            Assert.assertEquals("jamees,key*clo?ak)ppp", groupMembers.get(0).getUsername());
            groupMembers = session.users().getGroupMembers(appRealm, groupWithSlashes, 0, 10);
            Assert.assertEquals(1, groupMembers.size());
            Assert.assertEquals("jamees,key*clo?ak)ppp", groupMembers.get(0).getUsername());
            // 4 - Delete some group mappings and check they are deleted
            specialUser.leaveGroup(specialGroup);
            specialUser.leaveGroup(groupWithSlashes);
            userGroups = specialUser.getGroups();
            Assert.assertEquals(0, userGroups.size());
        });
    }
}

