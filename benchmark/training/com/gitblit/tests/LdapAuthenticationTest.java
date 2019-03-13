/**
 * Copyright 2012 John Crygier
 * Copyright 2012 gitblit.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gitblit.tests;


import Keys.realm.ldap.bindpattern;
import Keys.realm.ldap.maintainTeams;
import Keys.realm.ldap.password;
import Keys.realm.ldap.synchronize;
import Keys.realm.ldap.username;
import SearchScope.SUB;
import com.gitblit.auth.LdapAuthProvider;
import com.gitblit.manager.AuthenticationManager;
import com.gitblit.manager.IUserManager;
import com.gitblit.models.TeamModel;
import com.gitblit.models.UserModel;
import com.gitblit.tests.mock.MemorySettings;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldif.LDIFReader;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.gitblit.tests.LdapBasedUnitTest.AuthMode.ANONYMOUS;
import static com.gitblit.tests.LdapBasedUnitTest.AuthMode.DS_MANAGER;


/**
 * An Integration test for LDAP that tests going against an in-memory UnboundID
 * LDAP server.
 *
 * @author jcrygier
 */
@RunWith(Parameterized.class)
public class LdapAuthenticationTest extends LdapBasedUnitTest {
    private LdapAuthProvider ldap;

    private IUserManager userManager;

    private AuthenticationManager auth;

    @Test
    public void testAuthenticate() {
        UserModel userOneModel = ldap.authenticate("UserOne", "userOnePassword".toCharArray());
        Assert.assertNotNull(userOneModel);
        Assert.assertNotNull(userOneModel.getTeam("git_admins"));
        Assert.assertNotNull(userOneModel.getTeam("git_users"));
        UserModel userOneModelFailedAuth = ldap.authenticate("UserOne", "userTwoPassword".toCharArray());
        Assert.assertNull(userOneModelFailedAuth);
        UserModel userTwoModel = ldap.authenticate("UserTwo", "userTwoPassword".toCharArray());
        Assert.assertNotNull(userTwoModel);
        Assert.assertNotNull(userTwoModel.getTeam("git_users"));
        Assert.assertNull(userTwoModel.getTeam("git_admins"));
        Assert.assertNotNull(userTwoModel.getTeam("git admins"));
        UserModel userThreeModel = ldap.authenticate("UserThree", "userThreePassword".toCharArray());
        Assert.assertNotNull(userThreeModel);
        Assert.assertNotNull(userThreeModel.getTeam("git_users"));
        Assert.assertNull(userThreeModel.getTeam("git_admins"));
        UserModel userFourModel = ldap.authenticate("UserFour", "userFourPassword".toCharArray());
        Assert.assertNotNull(userFourModel);
        Assert.assertNotNull(userFourModel.getTeam("git_users"));
        Assert.assertNull(userFourModel.getTeam("git_admins"));
        Assert.assertNull(userFourModel.getTeam("git admins"));
    }

    @Test
    public void testAdminPropertyTeamsInLdap() {
        UserModel userOneModel = ldap.authenticate("UserOne", "userOnePassword".toCharArray());
        Assert.assertNotNull(userOneModel);
        Assert.assertNotNull(userOneModel.getTeam("git_admins"));
        Assert.assertNull(userOneModel.getTeam("git admins"));
        Assert.assertNotNull(userOneModel.getTeam("git_users"));
        Assert.assertFalse(userOneModel.canAdmin);
        Assert.assertTrue(userOneModel.canAdmin());
        Assert.assertTrue(userOneModel.getTeam("git_admins").canAdmin);
        Assert.assertFalse(userOneModel.getTeam("git_users").canAdmin);
        UserModel userTwoModel = ldap.authenticate("UserTwo", "userTwoPassword".toCharArray());
        Assert.assertNotNull(userTwoModel);
        Assert.assertNotNull(userTwoModel.getTeam("git_users"));
        Assert.assertNull(userTwoModel.getTeam("git_admins"));
        Assert.assertNotNull(userTwoModel.getTeam("git admins"));
        Assert.assertFalse(userTwoModel.canAdmin);
        Assert.assertTrue(userTwoModel.canAdmin());
        Assert.assertTrue(userTwoModel.getTeam("git admins").canAdmin);
        Assert.assertFalse(userTwoModel.getTeam("git_users").canAdmin);
        UserModel userThreeModel = ldap.authenticate("UserThree", "userThreePassword".toCharArray());
        Assert.assertNotNull(userThreeModel);
        Assert.assertNotNull(userThreeModel.getTeam("git_users"));
        Assert.assertNull(userThreeModel.getTeam("git_admins"));
        Assert.assertNull(userThreeModel.getTeam("git admins"));
        Assert.assertTrue(userThreeModel.canAdmin);
        Assert.assertTrue(userThreeModel.canAdmin());
        Assert.assertFalse(userThreeModel.getTeam("git_users").canAdmin);
        UserModel userFourModel = ldap.authenticate("UserFour", "userFourPassword".toCharArray());
        Assert.assertNotNull(userFourModel);
        Assert.assertNotNull(userFourModel.getTeam("git_users"));
        Assert.assertNull(userFourModel.getTeam("git_admins"));
        Assert.assertNull(userFourModel.getTeam("git admins"));
        Assert.assertFalse(userFourModel.canAdmin);
        Assert.assertFalse(userFourModel.canAdmin());
        Assert.assertFalse(userFourModel.getTeam("git_users").canAdmin);
    }

    @Test
    public void testAdminPropertyTeamsNotInLdap() {
        settings.put(maintainTeams, "false");
        UserModel userOneModel = ldap.authenticate("UserOne", "userOnePassword".toCharArray());
        Assert.assertNotNull(userOneModel);
        Assert.assertNotNull(userOneModel.getTeam("git_admins"));
        Assert.assertNull(userOneModel.getTeam("git admins"));
        Assert.assertNotNull(userOneModel.getTeam("git_users"));
        Assert.assertTrue(userOneModel.canAdmin);
        Assert.assertTrue(userOneModel.canAdmin());
        Assert.assertFalse(userOneModel.getTeam("git_admins").canAdmin);
        Assert.assertFalse(userOneModel.getTeam("git_users").canAdmin);
        UserModel userTwoModel = ldap.authenticate("UserTwo", "userTwoPassword".toCharArray());
        Assert.assertNotNull(userTwoModel);
        Assert.assertNotNull(userTwoModel.getTeam("git_users"));
        Assert.assertNull(userTwoModel.getTeam("git_admins"));
        Assert.assertNotNull(userTwoModel.getTeam("git admins"));
        Assert.assertFalse(userTwoModel.canAdmin);
        Assert.assertTrue(userTwoModel.canAdmin());
        Assert.assertTrue(userTwoModel.getTeam("git admins").canAdmin);
        Assert.assertFalse(userTwoModel.getTeam("git_users").canAdmin);
        UserModel userThreeModel = ldap.authenticate("UserThree", "userThreePassword".toCharArray());
        Assert.assertNotNull(userThreeModel);
        Assert.assertNotNull(userThreeModel.getTeam("git_users"));
        Assert.assertNull(userThreeModel.getTeam("git_admins"));
        Assert.assertNull(userThreeModel.getTeam("git admins"));
        Assert.assertFalse(userThreeModel.canAdmin);
        Assert.assertFalse(userThreeModel.canAdmin());
        Assert.assertFalse(userThreeModel.getTeam("git_users").canAdmin);
        UserModel userFourModel = ldap.authenticate("UserFour", "userFourPassword".toCharArray());
        Assert.assertNotNull(userFourModel);
        Assert.assertNotNull(userFourModel.getTeam("git_users"));
        Assert.assertNull(userFourModel.getTeam("git_admins"));
        Assert.assertNull(userFourModel.getTeam("git admins"));
        Assert.assertFalse(userFourModel.canAdmin);
        Assert.assertFalse(userFourModel.canAdmin());
        Assert.assertFalse(userFourModel.getTeam("git_users").canAdmin);
    }

    @Test
    public void testDisplayName() {
        UserModel userOneModel = ldap.authenticate("UserOne", "userOnePassword".toCharArray());
        Assert.assertNotNull(userOneModel);
        Assert.assertEquals("User One", userOneModel.displayName);
        // Test more complicated scenarios - concat
        MemorySettings ms = getSettings();
        ms.put("realm.ldap.displayName", "${personalTitle}. ${givenName} ${surname}");
        ldap = newLdapAuthentication(ms);
        userOneModel = ldap.authenticate("UserOne", "userOnePassword".toCharArray());
        Assert.assertNotNull(userOneModel);
        Assert.assertEquals("Mr. User One", userOneModel.displayName);
    }

    @Test
    public void testEmail() {
        UserModel userOneModel = ldap.authenticate("UserOne", "userOnePassword".toCharArray());
        Assert.assertNotNull(userOneModel);
        Assert.assertEquals("userone@gitblit.com", userOneModel.emailAddress);
        // Test more complicated scenarios - concat
        MemorySettings ms = getSettings();
        ms.put("realm.ldap.email", "${givenName}.${surname}@gitblit.com");
        ldap = newLdapAuthentication(ms);
        userOneModel = ldap.authenticate("UserOne", "userOnePassword".toCharArray());
        Assert.assertNotNull(userOneModel);
        Assert.assertEquals("User.One@gitblit.com", userOneModel.emailAddress);
    }

    @Test
    public void testLdapInjection() {
        // Inject so "(&(objectClass=person)(sAMAccountName=${username}))" becomes "(&(objectClass=person)(sAMAccountName=*)(userPassword=userOnePassword))"
        // Thus searching by password
        UserModel userOneModel = ldap.authenticate("*)(userPassword=userOnePassword", "userOnePassword".toCharArray());
        Assert.assertNull(userOneModel);
    }

    @Test
    public void checkIfUsersConfContainsAllUsersFromSampleDataLdif() throws Exception {
        SearchResult searchResult = getDS().search(LdapBasedUnitTest.ACCOUNT_BASE, SUB, "objectClass=person");
        Assert.assertEquals("Number of ldap users in gitblit user model", searchResult.getEntryCount(), countLdapUsersInUserManager());
    }

    @Test
    public void addingUserInLdapShouldNotUpdateGitBlitUsersAndGroups() throws Exception {
        getDS().addEntries(LDIFReader.readEntries(((LdapBasedUnitTest.RESOURCE_DIR) + "adduser.ldif")));
        ldap.sync();
        Assert.assertEquals("Number of ldap users in gitblit user model", 5, countLdapUsersInUserManager());
    }

    @Test
    public void addingUserInLdapShouldUpdateGitBlitUsersAndGroups() throws Exception {
        settings.put(synchronize, "true");
        getDS().addEntries(LDIFReader.readEntries(((LdapBasedUnitTest.RESOURCE_DIR) + "adduser.ldif")));
        ldap.sync();
        Assert.assertEquals("Number of ldap users in gitblit user model", 6, countLdapUsersInUserManager());
    }

    @Test
    public void addingGroupsInLdapShouldNotUpdateGitBlitUsersAndGroups() throws Exception {
        getDS().addEntries(LDIFReader.readEntries(((LdapBasedUnitTest.RESOURCE_DIR) + "addgroup.ldif")));
        ldap.sync();
        Assert.assertEquals("Number of ldap groups in gitblit team model", 0, countLdapTeamsInUserManager());
    }

    @Test
    public void addingGroupsInLdapShouldUpdateGitBlitUsersNotGroups2() throws Exception {
        settings.put(synchronize, "true");
        settings.put(maintainTeams, "false");
        getDS().addEntries(LDIFReader.readEntries(((LdapBasedUnitTest.RESOURCE_DIR) + "adduser.ldif")));
        getDS().addEntries(LDIFReader.readEntries(((LdapBasedUnitTest.RESOURCE_DIR) + "addgroup.ldif")));
        ldap.sync();
        Assert.assertEquals("Number of ldap users in gitblit user model", 6, countLdapUsersInUserManager());
        Assert.assertEquals("Number of ldap groups in gitblit team model", 0, countLdapTeamsInUserManager());
    }

    @Test
    public void addingGroupsInLdapShouldUpdateGitBlitUsersAndGroups() throws Exception {
        // This test only makes sense if the authentication mode allows for synchronization.
        Assume.assumeTrue((((authMode) == (ANONYMOUS)) || ((authMode) == (DS_MANAGER))));
        settings.put(synchronize, "true");
        getDS().addEntries(LDIFReader.readEntries(((LdapBasedUnitTest.RESOURCE_DIR) + "addgroup.ldif")));
        ldap.sync();
        Assert.assertEquals("Number of ldap groups in gitblit team model", 1, countLdapTeamsInUserManager());
    }

    @Test
    public void syncUpdateUsersAndGroupsAdminProperty() throws Exception {
        // This test only makes sense if the authentication mode allows for synchronization.
        Assume.assumeTrue((((authMode) == (ANONYMOUS)) || ((authMode) == (DS_MANAGER))));
        settings.put(synchronize, "true");
        ldap.sync();
        UserModel user = userManager.getUserModel("UserOne");
        Assert.assertNotNull(user);
        Assert.assertFalse(user.canAdmin);
        Assert.assertTrue(user.canAdmin());
        user = userManager.getUserModel("UserTwo");
        Assert.assertNotNull(user);
        Assert.assertFalse(user.canAdmin);
        Assert.assertTrue(user.canAdmin());
        user = userManager.getUserModel("UserThree");
        Assert.assertNotNull(user);
        Assert.assertTrue(user.canAdmin);
        Assert.assertTrue(user.canAdmin());
        user = userManager.getUserModel("UserFour");
        Assert.assertNotNull(user);
        Assert.assertFalse(user.canAdmin);
        Assert.assertFalse(user.canAdmin());
        TeamModel team = userManager.getTeamModel("Git_Admins");
        Assert.assertNotNull(team);
        Assert.assertTrue(team.canAdmin);
        team = userManager.getTeamModel("Git Admins");
        Assert.assertNotNull(team);
        Assert.assertTrue(team.canAdmin);
        team = userManager.getTeamModel("Git_Users");
        Assert.assertNotNull(team);
        Assert.assertFalse(team.canAdmin);
    }

    @Test
    public void syncNotUpdateUsersAndGroupsAdminProperty() throws Exception {
        settings.put(synchronize, "true");
        settings.put(maintainTeams, "false");
        ldap.sync();
        UserModel user = userManager.getUserModel("UserOne");
        Assert.assertNotNull(user);
        Assert.assertTrue(user.canAdmin);
        Assert.assertTrue(user.canAdmin());
        user = userManager.getUserModel("UserTwo");
        Assert.assertNotNull(user);
        Assert.assertFalse(user.canAdmin);
        Assert.assertTrue(user.canAdmin());
        user = userManager.getUserModel("UserThree");
        Assert.assertNotNull(user);
        Assert.assertFalse(user.canAdmin);
        Assert.assertFalse(user.canAdmin());
        user = userManager.getUserModel("UserFour");
        Assert.assertNotNull(user);
        Assert.assertFalse(user.canAdmin);
        Assert.assertFalse(user.canAdmin());
        TeamModel team = userManager.getTeamModel("Git_Admins");
        Assert.assertNotNull(team);
        Assert.assertFalse(team.canAdmin);
        team = userManager.getTeamModel("Git Admins");
        Assert.assertNotNull(team);
        Assert.assertTrue(team.canAdmin);
        team = userManager.getTeamModel("Git_Users");
        Assert.assertNotNull(team);
        Assert.assertFalse(team.canAdmin);
    }

    @Test
    public void testAuthenticationManager() {
        UserModel userOneModel = auth.authenticate("UserOne", "userOnePassword".toCharArray(), null);
        Assert.assertNotNull(userOneModel);
        Assert.assertNotNull(userOneModel.getTeam("git_admins"));
        Assert.assertNotNull(userOneModel.getTeam("git_users"));
        UserModel userOneModelFailedAuth = auth.authenticate("UserOne", "userTwoPassword".toCharArray(), null);
        Assert.assertNull(userOneModelFailedAuth);
        UserModel userTwoModel = auth.authenticate("UserTwo", "userTwoPassword".toCharArray(), null);
        Assert.assertNotNull(userTwoModel);
        Assert.assertNotNull(userTwoModel.getTeam("git_users"));
        Assert.assertNull(userTwoModel.getTeam("git_admins"));
        Assert.assertNotNull(userTwoModel.getTeam("git admins"));
        UserModel userThreeModel = auth.authenticate("UserThree", "userThreePassword".toCharArray(), null);
        Assert.assertNotNull(userThreeModel);
        Assert.assertNotNull(userThreeModel.getTeam("git_users"));
        Assert.assertNull(userThreeModel.getTeam("git_admins"));
        UserModel userFourModel = auth.authenticate("UserFour", "userFourPassword".toCharArray(), null);
        Assert.assertNotNull(userFourModel);
        Assert.assertNotNull(userFourModel.getTeam("git_users"));
        Assert.assertNull(userFourModel.getTeam("git_admins"));
        Assert.assertNull(userFourModel.getTeam("git admins"));
    }

    @Test
    public void testAuthenticationManagerAdminPropertyTeamsInLdap() {
        UserModel userOneModel = auth.authenticate("UserOne", "userOnePassword".toCharArray(), null);
        Assert.assertNotNull(userOneModel);
        Assert.assertNotNull(userOneModel.getTeam("git_admins"));
        Assert.assertNull(userOneModel.getTeam("git admins"));
        Assert.assertNotNull(userOneModel.getTeam("git_users"));
        Assert.assertFalse(userOneModel.canAdmin);
        Assert.assertTrue(userOneModel.canAdmin());
        Assert.assertTrue(userOneModel.getTeam("git_admins").canAdmin);
        Assert.assertFalse(userOneModel.getTeam("git_users").canAdmin);
        UserModel userOneModelFailedAuth = auth.authenticate("UserOne", "userTwoPassword".toCharArray(), null);
        Assert.assertNull(userOneModelFailedAuth);
        UserModel userTwoModel = auth.authenticate("UserTwo", "userTwoPassword".toCharArray(), null);
        Assert.assertNotNull(userTwoModel);
        Assert.assertNotNull(userTwoModel.getTeam("git_users"));
        Assert.assertNull(userTwoModel.getTeam("git_admins"));
        Assert.assertNotNull(userTwoModel.getTeam("git admins"));
        Assert.assertFalse(userTwoModel.canAdmin);
        Assert.assertTrue(userTwoModel.canAdmin());
        Assert.assertTrue(userTwoModel.getTeam("git admins").canAdmin);
        Assert.assertFalse(userTwoModel.getTeam("git_users").canAdmin);
        UserModel userThreeModel = auth.authenticate("UserThree", "userThreePassword".toCharArray(), null);
        Assert.assertNotNull(userThreeModel);
        Assert.assertNotNull(userThreeModel.getTeam("git_users"));
        Assert.assertNull(userThreeModel.getTeam("git_admins"));
        Assert.assertNull(userThreeModel.getTeam("git admins"));
        Assert.assertTrue(userThreeModel.canAdmin);
        Assert.assertTrue(userThreeModel.canAdmin());
        Assert.assertFalse(userThreeModel.getTeam("git_users").canAdmin);
        UserModel userFourModel = auth.authenticate("UserFour", "userFourPassword".toCharArray(), null);
        Assert.assertNotNull(userFourModel);
        Assert.assertNotNull(userFourModel.getTeam("git_users"));
        Assert.assertNull(userFourModel.getTeam("git_admins"));
        Assert.assertNull(userFourModel.getTeam("git admins"));
        Assert.assertFalse(userFourModel.canAdmin);
        Assert.assertFalse(userFourModel.canAdmin());
        Assert.assertFalse(userFourModel.getTeam("git_users").canAdmin);
    }

    @Test
    public void testAuthenticationManagerAdminPropertyTeamsNotInLdap() {
        settings.put(maintainTeams, "false");
        UserModel userOneModel = auth.authenticate("UserOne", "userOnePassword".toCharArray(), null);
        Assert.assertNotNull(userOneModel);
        Assert.assertNotNull(userOneModel.getTeam("git_admins"));
        Assert.assertNull(userOneModel.getTeam("git admins"));
        Assert.assertNotNull(userOneModel.getTeam("git_users"));
        Assert.assertTrue(userOneModel.canAdmin);
        Assert.assertTrue(userOneModel.canAdmin());
        Assert.assertFalse(userOneModel.getTeam("git_admins").canAdmin);
        Assert.assertFalse(userOneModel.getTeam("git_users").canAdmin);
        UserModel userOneModelFailedAuth = auth.authenticate("UserOne", "userTwoPassword".toCharArray(), null);
        Assert.assertNull(userOneModelFailedAuth);
        UserModel userTwoModel = auth.authenticate("UserTwo", "userTwoPassword".toCharArray(), null);
        Assert.assertNotNull(userTwoModel);
        Assert.assertNotNull(userTwoModel.getTeam("git_users"));
        Assert.assertNull(userTwoModel.getTeam("git_admins"));
        Assert.assertNotNull(userTwoModel.getTeam("git admins"));
        Assert.assertFalse(userTwoModel.canAdmin);
        Assert.assertTrue(userTwoModel.canAdmin());
        Assert.assertTrue(userTwoModel.getTeam("git admins").canAdmin);
        Assert.assertFalse(userTwoModel.getTeam("git_users").canAdmin);
        UserModel userThreeModel = auth.authenticate("UserThree", "userThreePassword".toCharArray(), null);
        Assert.assertNotNull(userThreeModel);
        Assert.assertNotNull(userThreeModel.getTeam("git_users"));
        Assert.assertNull(userThreeModel.getTeam("git_admins"));
        Assert.assertNull(userThreeModel.getTeam("git admins"));
        Assert.assertFalse(userThreeModel.canAdmin);
        Assert.assertFalse(userThreeModel.canAdmin());
        Assert.assertFalse(userThreeModel.getTeam("git_users").canAdmin);
        UserModel userFourModel = auth.authenticate("UserFour", "userFourPassword".toCharArray(), null);
        Assert.assertNotNull(userFourModel);
        Assert.assertNotNull(userFourModel.getTeam("git_users"));
        Assert.assertNull(userFourModel.getTeam("git_admins"));
        Assert.assertNull(userFourModel.getTeam("git admins"));
        Assert.assertFalse(userFourModel.canAdmin);
        Assert.assertFalse(userFourModel.canAdmin());
        Assert.assertFalse(userFourModel.getTeam("git_users").canAdmin);
    }

    @Test
    public void testBindWithUser() {
        // This test only makes sense if the user is not prevented from reading users and teams.
        Assume.assumeTrue(((authMode) != (DS_MANAGER)));
        settings.put(bindpattern, ("CN=${username},OU=US," + (LdapBasedUnitTest.ACCOUNT_BASE)));
        settings.put(username, "");
        settings.put(password, "");
        UserModel userOneModel = auth.authenticate("UserOne", "userOnePassword".toCharArray(), null);
        Assert.assertNotNull(userOneModel);
        UserModel userOneModelFailedAuth = auth.authenticate("UserOne", "userTwoPassword".toCharArray(), null);
        Assert.assertNull(userOneModelFailedAuth);
    }
}

