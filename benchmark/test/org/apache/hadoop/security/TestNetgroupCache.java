/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.security;


import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class TestNetgroupCache {
    private static final String USER1 = "user1";

    private static final String USER2 = "user2";

    private static final String USER3 = "user3";

    private static final String GROUP1 = "group1";

    private static final String GROUP2 = "group2";

    /**
     * Cache two groups with a set of users.
     * Test membership correctness.
     */
    @Test
    public void testMembership() {
        List<String> users = new ArrayList<String>();
        users.add(TestNetgroupCache.USER1);
        users.add(TestNetgroupCache.USER2);
        NetgroupCache.add(TestNetgroupCache.GROUP1, users);
        users = new ArrayList<String>();
        users.add(TestNetgroupCache.USER1);
        users.add(TestNetgroupCache.USER3);
        NetgroupCache.add(TestNetgroupCache.GROUP2, users);
        verifyGroupMembership(TestNetgroupCache.USER1, 2, TestNetgroupCache.GROUP1);
        verifyGroupMembership(TestNetgroupCache.USER1, 2, TestNetgroupCache.GROUP2);
        verifyGroupMembership(TestNetgroupCache.USER2, 1, TestNetgroupCache.GROUP1);
        verifyGroupMembership(TestNetgroupCache.USER3, 1, TestNetgroupCache.GROUP2);
    }

    /**
     * Cache a group with a set of users.
     * Test membership correctness.
     * Clear cache, remove a user from the group and cache the group
     * Test membership correctness.
     */
    @Test
    public void testUserRemoval() {
        List<String> users = new ArrayList<String>();
        users.add(TestNetgroupCache.USER1);
        users.add(TestNetgroupCache.USER2);
        NetgroupCache.add(TestNetgroupCache.GROUP1, users);
        verifyGroupMembership(TestNetgroupCache.USER1, 1, TestNetgroupCache.GROUP1);
        verifyGroupMembership(TestNetgroupCache.USER2, 1, TestNetgroupCache.GROUP1);
        users.remove(TestNetgroupCache.USER2);
        NetgroupCache.clear();
        NetgroupCache.add(TestNetgroupCache.GROUP1, users);
        verifyGroupMembership(TestNetgroupCache.USER1, 1, TestNetgroupCache.GROUP1);
        verifyGroupMembership(TestNetgroupCache.USER2, 0, null);
    }

    /**
     * Cache two groups with a set of users.
     * Test membership correctness.
     * Clear cache, cache only one group.
     * Test membership correctness.
     */
    @Test
    public void testGroupRemoval() {
        List<String> users = new ArrayList<String>();
        users.add(TestNetgroupCache.USER1);
        users.add(TestNetgroupCache.USER2);
        NetgroupCache.add(TestNetgroupCache.GROUP1, users);
        users = new ArrayList<String>();
        users.add(TestNetgroupCache.USER1);
        users.add(TestNetgroupCache.USER3);
        NetgroupCache.add(TestNetgroupCache.GROUP2, users);
        verifyGroupMembership(TestNetgroupCache.USER1, 2, TestNetgroupCache.GROUP1);
        verifyGroupMembership(TestNetgroupCache.USER1, 2, TestNetgroupCache.GROUP2);
        verifyGroupMembership(TestNetgroupCache.USER2, 1, TestNetgroupCache.GROUP1);
        verifyGroupMembership(TestNetgroupCache.USER3, 1, TestNetgroupCache.GROUP2);
        NetgroupCache.clear();
        users = new ArrayList<String>();
        users.add(TestNetgroupCache.USER1);
        users.add(TestNetgroupCache.USER2);
        NetgroupCache.add(TestNetgroupCache.GROUP1, users);
        verifyGroupMembership(TestNetgroupCache.USER1, 1, TestNetgroupCache.GROUP1);
        verifyGroupMembership(TestNetgroupCache.USER2, 1, TestNetgroupCache.GROUP1);
        verifyGroupMembership(TestNetgroupCache.USER3, 0, null);
    }
}

