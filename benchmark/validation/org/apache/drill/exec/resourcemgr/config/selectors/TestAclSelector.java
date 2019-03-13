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
package org.apache.drill.exec.resourcemgr.config.selectors;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.drill.categories.ResourceManagerTest;
import org.apache.drill.exec.resourcemgr.config.exception.RMConfigException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(ResourceManagerTest.class)
public final class TestAclSelector {
    private static final List<String> groupsValue = new ArrayList<>();

    private static final List<String> usersValue = new ArrayList<>();

    private static final List<String> emptyList = new ArrayList<>();

    private static final Map<String, List<String>> aclConfigValue = new HashMap<>();

    @Test
    public void testValidACLSelector_shortSyntax() throws Exception {
        TestAclSelector.groupsValue.add("sales");
        TestAclSelector.groupsValue.add("marketing");
        TestAclSelector.usersValue.add("user1");
        TestAclSelector.usersValue.add("user2");
        TestAclSelector.aclConfigValue.put("groups", TestAclSelector.groupsValue);
        TestAclSelector.aclConfigValue.put("users", TestAclSelector.usersValue);
        AclSelector testSelector = ((AclSelector) (testCommonHelper(TestAclSelector.usersValue, TestAclSelector.groupsValue, TestAclSelector.emptyList, TestAclSelector.emptyList)));
        // check based on valid/invalid user
        Set<String> groups = new HashSet<>();
        Assert.assertFalse(testSelector.checkQueryUserGroups("user3", groups));
        Assert.assertTrue(testSelector.checkQueryUserGroups("user1", groups));
        // check based on correct group
        groups.add("sales");
        Assert.assertTrue(testSelector.checkQueryUserGroups("user3", groups));
    }

    @Test
    public void testACLSelector_onlyUsers() throws Exception {
        TestAclSelector.usersValue.add("user1");
        TestAclSelector.aclConfigValue.put("users", TestAclSelector.usersValue);
        testCommonHelper(TestAclSelector.usersValue, TestAclSelector.groupsValue, TestAclSelector.emptyList, TestAclSelector.emptyList);
    }

    @Test
    public void testACLSelector_onlyGroups() throws Exception {
        TestAclSelector.groupsValue.add("group1");
        TestAclSelector.aclConfigValue.put("groups", TestAclSelector.groupsValue);
        testCommonHelper(TestAclSelector.usersValue, TestAclSelector.groupsValue, TestAclSelector.emptyList, TestAclSelector.emptyList);
    }

    @Test(expected = RMConfigException.class)
    public void testInValidACLSelector_shortSyntax() throws Exception {
        TestAclSelector.aclConfigValue.put("groups", new ArrayList<>());
        TestAclSelector.aclConfigValue.put("users", new ArrayList<>());
        testNegativeHelper();
    }

    @Test
    public void testValidACLSelector_longSyntax() throws Exception {
        TestAclSelector.groupsValue.add("sales:+");
        TestAclSelector.groupsValue.add("marketing:-");
        List<String> expectedAllowedGroups = new ArrayList<>();
        expectedAllowedGroups.add("sales");
        List<String> expectedDisAllowedGroups = new ArrayList<>();
        expectedDisAllowedGroups.add("marketing");
        TestAclSelector.usersValue.add("user1:+");
        TestAclSelector.usersValue.add("user2:-");
        List<String> expectedAllowedUsers = new ArrayList<>();
        expectedAllowedUsers.add("user1");
        List<String> expectedDisAllowedUsers = new ArrayList<>();
        expectedDisAllowedUsers.add("user2");
        TestAclSelector.aclConfigValue.put("groups", TestAclSelector.groupsValue);
        TestAclSelector.aclConfigValue.put("users", TestAclSelector.usersValue);
        AclSelector testSelector = ((AclSelector) (testCommonHelper(expectedAllowedUsers, expectedAllowedGroups, expectedDisAllowedUsers, expectedDisAllowedGroups)));
        Set<String> queryGroups = new HashSet<>();
        // Negative user/group
        queryGroups.add("marketing");
        Assert.assertFalse(testSelector.checkQueryUserGroups("user2", queryGroups));
        // Invalid user -ve group
        Assert.assertFalse(testSelector.checkQueryUserGroups("user3", queryGroups));
        // -ve user +ve group
        queryGroups.clear();
        queryGroups.add("sales");
        Assert.assertFalse(testSelector.checkQueryUserGroups("user2", queryGroups));
        // Invalid user +ve group
        Assert.assertTrue(testSelector.checkQueryUserGroups("user3", queryGroups));
    }

    @Test(expected = RMConfigException.class)
    public void testInvalidLongSyntaxIdentifier() throws Exception {
        TestAclSelector.groupsValue.add("sales:|");
        TestAclSelector.aclConfigValue.put("groups", TestAclSelector.groupsValue);
        testNegativeHelper();
    }

    @Test
    public void testMixLongShortAclSyntax() throws Exception {
        TestAclSelector.groupsValue.add("groups1");
        TestAclSelector.groupsValue.add("groups2:+");
        TestAclSelector.groupsValue.add("groups3:-");
        List<String> expectedAllowedGroups = new ArrayList<>();
        expectedAllowedGroups.add("groups1");
        expectedAllowedGroups.add("groups2");
        List<String> expectedDisAllowedGroups = new ArrayList<>();
        expectedDisAllowedGroups.add("groups3");
        TestAclSelector.usersValue.add("user1");
        TestAclSelector.usersValue.add("user2:+");
        TestAclSelector.usersValue.add("user3:-");
        List<String> expectedAllowedUsers = new ArrayList<>();
        expectedAllowedUsers.add("user1");
        expectedAllowedUsers.add("user2");
        List<String> expectedDisAllowedUsers = new ArrayList<>();
        expectedDisAllowedUsers.add("user3");
        TestAclSelector.aclConfigValue.put("groups", TestAclSelector.groupsValue);
        TestAclSelector.aclConfigValue.put("users", TestAclSelector.usersValue);
        testCommonHelper(expectedAllowedUsers, expectedAllowedGroups, expectedDisAllowedUsers, expectedDisAllowedGroups);
    }

    @Test
    public void testSameUserBothInPositiveNegative() throws Exception {
        TestAclSelector.usersValue.add("user1:+");
        TestAclSelector.usersValue.add("user1:-");
        List<String> expectedDisAllowedUsers = new ArrayList<>();
        expectedDisAllowedUsers.add("user1");
        TestAclSelector.aclConfigValue.put("users", TestAclSelector.usersValue);
        testCommonHelper(TestAclSelector.emptyList, TestAclSelector.emptyList, expectedDisAllowedUsers, TestAclSelector.emptyList);
    }

    @Test
    public void testStarInPositiveUsers() throws Exception {
        TestAclSelector.usersValue.add("*:+");
        TestAclSelector.usersValue.add("user1:-");
        List<String> expectedAllowedUsers = new ArrayList<>();
        expectedAllowedUsers.add("*");
        List<String> expectedDisAllowedUsers = new ArrayList<>();
        expectedDisAllowedUsers.add("user1");
        TestAclSelector.aclConfigValue.put("users", TestAclSelector.usersValue);
        AclSelector testSelector = ((AclSelector) (testCommonHelper(expectedAllowedUsers, TestAclSelector.emptyList, expectedDisAllowedUsers, TestAclSelector.emptyList)));
        Set<String> queryGroups = new HashSet<>();
        // -ve user with Invalid groups
        Assert.assertFalse(testSelector.checkQueryUserGroups("user1", queryGroups));
        // Other user with invalid groups
        Assert.assertTrue(testSelector.checkQueryUserGroups("user2", queryGroups));
    }

    @Test
    public void testStarInNegativeUsers() throws Exception {
        TestAclSelector.usersValue.add("*:-");
        TestAclSelector.usersValue.add("user1:+");
        List<String> expectedAllowedUsers = new ArrayList<>();
        expectedAllowedUsers.add("user1");
        List<String> expectedDisAllowedUsers = new ArrayList<>();
        expectedDisAllowedUsers.add("*");
        TestAclSelector.aclConfigValue.put("users", TestAclSelector.usersValue);
        AclSelector testSelector = ((AclSelector) (testCommonHelper(expectedAllowedUsers, TestAclSelector.emptyList, expectedDisAllowedUsers, TestAclSelector.emptyList)));
        Set<String> queryGroups = new HashSet<>();
        // Other user with Invalid groups
        Assert.assertFalse(testSelector.checkQueryUserGroups("user2", queryGroups));
        // +ve user with invalid groups
        Assert.assertTrue(testSelector.checkQueryUserGroups("user1", queryGroups));
    }
}

