/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.authorization;


import AccessPolicy.Builder;
import RequestAction.READ;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

import static RequestAction.READ;


public class TestAccessPolicy {
    static final String TEST_RESOURCE = "1";

    @Test
    public void testSimpleCreation() {
        final String identifier = "1";
        final String user1 = "user1";
        final String user2 = "user2";
        final RequestAction action = READ;
        final AccessPolicy policy = build();
        Assert.assertEquals(identifier, policy.getIdentifier());
        Assert.assertNotNull(policy.getResource());
        Assert.assertEquals(TestAccessPolicy.TEST_RESOURCE, policy.getResource());
        Assert.assertNotNull(policy.getUsers());
        Assert.assertEquals(2, policy.getUsers().size());
        Assert.assertTrue(policy.getUsers().contains(user1));
        Assert.assertTrue(policy.getUsers().contains(user2));
        Assert.assertNotNull(policy.getAction());
        Assert.assertEquals(READ, policy.getAction());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingIdentifier() {
        new AccessPolicy.Builder().resource(TestAccessPolicy.TEST_RESOURCE).addUser("user1").action(READ).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingResource() {
        new AccessPolicy.Builder().identifier("1").addUser("user1").action(READ).build();
    }

    @Test
    public void testMissingUsersAndGroups() {
        final AccessPolicy policy = build();
        Assert.assertNotNull(policy);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingActions() {
        new AccessPolicy.Builder().identifier("1").resource(TestAccessPolicy.TEST_RESOURCE).addUser("user1").build();
    }

    @Test
    public void testFromPolicy() {
        final String identifier = "1";
        final String user1 = "user1";
        final String user2 = "user2";
        final String group1 = "group1";
        final String group2 = "group2";
        final RequestAction action = READ;
        final AccessPolicy policy = build();
        Assert.assertEquals(identifier, policy.getIdentifier());
        Assert.assertNotNull(policy.getResource());
        Assert.assertEquals(TestAccessPolicy.TEST_RESOURCE, policy.getResource());
        Assert.assertNotNull(policy.getUsers());
        Assert.assertEquals(2, policy.getUsers().size());
        Assert.assertTrue(policy.getUsers().contains(user1));
        Assert.assertTrue(policy.getUsers().contains(user2));
        Assert.assertNotNull(policy.getGroups());
        Assert.assertEquals(2, policy.getGroups().size());
        Assert.assertTrue(policy.getGroups().contains(group1));
        Assert.assertTrue(policy.getGroups().contains(group2));
        Assert.assertNotNull(policy.getAction());
        Assert.assertEquals(READ, policy.getAction());
        final AccessPolicy policy2 = build();
        Assert.assertEquals(policy.getIdentifier(), policy2.getIdentifier());
        Assert.assertEquals(policy.getResource(), policy2.getResource());
        Assert.assertEquals(policy.getUsers(), policy2.getUsers());
        Assert.assertEquals(policy.getAction(), policy2.getAction());
    }

    @Test(expected = IllegalStateException.class)
    public void testFromPolicyAndChangeIdentifier() {
        final AccessPolicy policy = build();
        new AccessPolicy.Builder(policy).identifier("2").build();
    }

    @Test
    public void testAddRemoveClearUsers() {
        final AccessPolicy.Builder builder = new AccessPolicy.Builder().identifier("1").resource(TestAccessPolicy.TEST_RESOURCE).addUser("user1").action(READ);
        final AccessPolicy policy1 = builder.build();
        Assert.assertEquals(1, policy1.getUsers().size());
        Assert.assertTrue(policy1.getUsers().contains("user1"));
        final Set<String> moreEntities = new HashSet<>();
        moreEntities.add("user2");
        moreEntities.add("user3");
        moreEntities.add("user4");
        final AccessPolicy policy2 = build();
        Assert.assertEquals(4, policy2.getUsers().size());
        Assert.assertTrue(policy2.getUsers().contains("user1"));
        Assert.assertTrue(policy2.getUsers().contains("user2"));
        Assert.assertTrue(policy2.getUsers().contains("user3"));
        Assert.assertTrue(policy2.getUsers().contains("user4"));
        final AccessPolicy policy3 = build();
        Assert.assertEquals(3, policy3.getUsers().size());
        Assert.assertTrue(policy3.getUsers().contains("user1"));
        Assert.assertTrue(policy3.getUsers().contains("user2"));
        Assert.assertTrue(policy3.getUsers().contains("user4"));
        final Set<String> removeEntities = new HashSet<>();
        removeEntities.add("user1");
        removeEntities.add("user4");
        final AccessPolicy policy4 = build();
        Assert.assertEquals(1, policy4.getUsers().size());
        Assert.assertTrue(policy4.getUsers().contains("user2"));
        final AccessPolicy policy5 = build();
        Assert.assertEquals(0, policy5.getUsers().size());
    }

    @Test
    public void testAddRemoveClearGroups() {
        final AccessPolicy.Builder builder = new AccessPolicy.Builder().identifier("1").resource(TestAccessPolicy.TEST_RESOURCE).addGroup("group1").action(READ);
        final AccessPolicy policy1 = builder.build();
        Assert.assertEquals(1, policy1.getGroups().size());
        Assert.assertTrue(policy1.getGroups().contains("group1"));
        final Set<String> moreGroups = new HashSet<>();
        moreGroups.add("group2");
        moreGroups.add("group3");
        moreGroups.add("group4");
        final AccessPolicy policy2 = build();
        Assert.assertEquals(4, policy2.getGroups().size());
        Assert.assertTrue(policy2.getGroups().contains("group1"));
        Assert.assertTrue(policy2.getGroups().contains("group2"));
        Assert.assertTrue(policy2.getGroups().contains("group3"));
        Assert.assertTrue(policy2.getGroups().contains("group4"));
        final AccessPolicy policy3 = build();
        Assert.assertEquals(3, policy3.getGroups().size());
        Assert.assertTrue(policy3.getGroups().contains("group1"));
        Assert.assertTrue(policy3.getGroups().contains("group2"));
        Assert.assertTrue(policy3.getGroups().contains("group4"));
        final Set<String> removeGroups = new HashSet<>();
        removeGroups.add("group1");
        removeGroups.add("group4");
        final AccessPolicy policy4 = build();
        Assert.assertEquals(1, policy4.getGroups().size());
        Assert.assertTrue(policy4.getGroups().contains("group2"));
        final AccessPolicy policy5 = build();
        Assert.assertEquals(0, policy5.getUsers().size());
    }
}

