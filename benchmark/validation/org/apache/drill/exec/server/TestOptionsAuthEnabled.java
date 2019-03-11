/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.server;


import ExecConstants.ADMIN_USERS_KEY;
import ExecConstants.ADMIN_USERS_VALIDATOR;
import ExecConstants.ADMIN_USERS_VALIDATOR.DEFAULT_ADMIN_USERS;
import ExecConstants.ADMIN_USER_GROUPS_KEY;
import ExecConstants.ADMIN_USER_GROUPS_VALIDATOR;
import ExecConstants.ADMIN_USER_GROUPS_VALIDATOR.DEFAULT_ADMIN_USER_GROUPS;
import ExecConstants.SLICE_TARGET;
import org.apache.drill.common.util.DrillStringUtils;
import org.apache.drill.exec.ExecTest;
import org.apache.drill.exec.rpc.user.security.testing.UserAuthenticatorTestImpl;
import org.apache.drill.exec.server.options.OptionManager;
import org.apache.drill.exec.util.ImpersonationUtil;
import org.apache.drill.shaded.guava.com.google.common.base.Joiner;
import org.apache.drill.test.BaseTestQuery;
import org.apache.drill.test.ClientFixture;
import org.apache.drill.test.ClusterFixture;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test setting system scoped options with user authentication enabled. (DRILL-3622)
 */
public class TestOptionsAuthEnabled extends BaseTestQuery {
    private static final String setSysOptionQuery = String.format("ALTER SYSTEM SET `%s` = %d;", SLICE_TARGET, 200);

    @Test
    public void updateSysOptAsAdminUser() throws Exception {
        BaseTestQuery.updateClient(UserAuthenticatorTestImpl.ADMIN_USER, UserAuthenticatorTestImpl.ADMIN_USER_PASSWORD);
        setOptHelper();
    }

    @Test
    public void updateSysOptAsNonAdminUser() throws Exception {
        BaseTestQuery.updateClient(UserAuthenticatorTestImpl.TEST_USER_2, UserAuthenticatorTestImpl.TEST_USER_2_PASSWORD);
        BaseTestQuery.errorMsgTestHelper(TestOptionsAuthEnabled.setSysOptionQuery, "Not authorized to change SYSTEM options.");
    }

    @Test
    public void updateSysOptAsUserInAdminGroup() throws Exception {
        BaseTestQuery.updateClient(UserAuthenticatorTestImpl.TEST_USER_1, UserAuthenticatorTestImpl.TEST_USER_1_PASSWORD);
        setOptHelper();
    }

    @Test
    public void trySettingAdminOptsAtSessionScopeAsAdmin() throws Exception {
        BaseTestQuery.updateClient(UserAuthenticatorTestImpl.ADMIN_USER, UserAuthenticatorTestImpl.ADMIN_USER_PASSWORD);
        final String setOptionQuery = String.format("ALTER SESSION SET `%s`='%s,%s'", ADMIN_USERS_KEY, UserAuthenticatorTestImpl.ADMIN_USER, UserAuthenticatorTestImpl.PROCESS_USER);
        BaseTestQuery.errorMsgTestHelper(setOptionQuery, "PERMISSION ERROR: Cannot change option security.admin.users in scope SESSION");
    }

    @Test
    public void trySettingAdminOptsAtSessionScopeAsNonAdmin() throws Exception {
        BaseTestQuery.updateClient(UserAuthenticatorTestImpl.TEST_USER_2, UserAuthenticatorTestImpl.TEST_USER_2_PASSWORD);
        final String setOptionQuery = String.format("ALTER SESSION SET `%s`='%s,%s'", ADMIN_USERS_KEY, UserAuthenticatorTestImpl.ADMIN_USER, UserAuthenticatorTestImpl.PROCESS_USER);
        BaseTestQuery.errorMsgTestHelper(setOptionQuery, "PERMISSION ERROR: Cannot change option security.admin.users in scope SESSION");
    }

    @Test
    public void testAdminUserOptions() throws Exception {
        try (ClusterFixture cluster = ClusterFixture.standardCluster(ExecTest.dirTestWatcher);ClientFixture client = cluster.clientFixture()) {
            OptionManager optionManager = cluster.drillbit().getContext().getOptionManager();
            // Admin Users Tests
            // config file should have the 'fake' default admin user and it should be returned
            // by the option manager if the option has not been set by the user
            String configAdminUser = optionManager.getOption(ADMIN_USERS_VALIDATOR);
            Assert.assertEquals(configAdminUser, DEFAULT_ADMIN_USERS);
            // Option accessor should never return the 'fake' default from the config
            String adminUser1 = ADMIN_USERS_VALIDATOR.getAdminUsers(optionManager);
            Assert.assertNotEquals(adminUser1, DEFAULT_ADMIN_USERS);
            // Change testAdminUser if necessary
            String testAdminUser = "ronswanson";
            if (adminUser1.equals(testAdminUser)) {
                testAdminUser += "thefirst";
            }
            // Check if the admin option accessor honors a user-supplied value
            client.alterSystem(ADMIN_USERS_KEY, testAdminUser);
            String adminUser2 = ADMIN_USERS_VALIDATOR.getAdminUsers(optionManager);
            Assert.assertEquals(adminUser2, testAdminUser);
            // Ensure that the default admin users have admin privileges
            client.resetSystem(ADMIN_USERS_KEY);
            client.resetSystem(ADMIN_USER_GROUPS_KEY);
            String systemAdminUsersList0 = ADMIN_USERS_VALIDATOR.getAdminUsers(optionManager);
            String systemAdminUserGroupsList0 = ADMIN_USER_GROUPS_VALIDATOR.getAdminUserGroups(optionManager);
            for (String user : systemAdminUsersList0.split(",")) {
                Assert.assertTrue(ImpersonationUtil.hasAdminPrivileges(user, systemAdminUsersList0, systemAdminUserGroupsList0));
            }
            // test if admin users, set by the user, have admin privileges
            // test if we can handle a user-supplied list that is not well formatted
            String crummyTestAdminUsersList = " alice, bob bob, charlie  ,, dave ";
            client.alterSystem(ADMIN_USERS_KEY, crummyTestAdminUsersList);
            String[] sanitizedAdminUsers = new String[]{ "alice", "bob bob", "charlie", "dave" };
            // also test the CSV sanitizer
            Assert.assertEquals(Joiner.on(",").join(sanitizedAdminUsers), DrillStringUtils.sanitizeCSV(crummyTestAdminUsersList));
            String systemAdminUsersList1 = ADMIN_USERS_VALIDATOR.getAdminUsers(optionManager);
            String systemAdminUserGroupsList1 = ADMIN_USER_GROUPS_VALIDATOR.getAdminUserGroups(optionManager);
            for (String user : sanitizedAdminUsers) {
                Assert.assertTrue(ImpersonationUtil.hasAdminPrivileges(user, systemAdminUsersList1, systemAdminUserGroupsList1));
            }
            // Admin User Groups Tests
            // config file should have the 'fake' default admin user and it should be returned
            // by the option manager if the option has not been set by the user
            String configAdminUserGroups = optionManager.getOption(ADMIN_USER_GROUPS_VALIDATOR);
            Assert.assertEquals(configAdminUserGroups, DEFAULT_ADMIN_USER_GROUPS);
            // Option accessor should never return the 'fake' default from the config
            String adminUserGroups1 = ADMIN_USER_GROUPS_VALIDATOR.getAdminUserGroups(optionManager);
            Assert.assertNotEquals(adminUserGroups1, DEFAULT_ADMIN_USER_GROUPS);
            // Change testAdminUserGroups if necessary
            String testAdminUserGroups = "yakshavers";
            if (adminUserGroups1.equals(testAdminUserGroups)) {
                testAdminUserGroups += ",wormracers";
            }
            // Check if the admin option accessor honors a user-supplied values
            client.alterSystem(ADMIN_USER_GROUPS_KEY, testAdminUserGroups);
            String adminUserGroups2 = ADMIN_USER_GROUPS_VALIDATOR.getAdminUserGroups(optionManager);
            Assert.assertEquals(adminUserGroups2, testAdminUserGroups);
            // Test if we can handle a user-supplied admin user groups list that is not well formatted
            String crummyTestAdminUserGroupsList = " g1, g 2, g4 ,, g5 ";
            client.alterSystem(ADMIN_USER_GROUPS_KEY, crummyTestAdminUserGroupsList);
            String systemAdminUserGroupsList2 = ADMIN_USER_GROUPS_VALIDATOR.getAdminUserGroups(optionManager);
            // test if all the group tokens are well-formed
            // Note: A hasAdminPrivilege() test cannot be done here, like in the tests for handling a crummy admin user list.
            // This is because ImpersonationUtil currently does not implement an API that takes a group as an input to check
            // for admin privileges
            for (String group : systemAdminUserGroupsList2.split(",")) {
                Assert.assertTrue(((group.length()) != 0));
                Assert.assertTrue(group.trim().equals(group));
            }
        }
    }
}

