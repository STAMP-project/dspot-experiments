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


import Group.Builder;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class TestGroup {
    @Test
    public void testSimpleCreation() {
        final String id = "1";
        final String name = "group1";
        final String user1 = "user1";
        final String user2 = "user2";
        final Group group = build();
        Assert.assertEquals(id, group.getIdentifier());
        Assert.assertEquals(name, group.getName());
        Assert.assertNotNull(group.getUsers());
        Assert.assertEquals(2, group.getUsers().size());
        Assert.assertTrue(group.getUsers().contains(user1));
        Assert.assertTrue(group.getUsers().contains(user2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingId() {
        new Group.Builder().name("group1").addUser("user1").addUser("user2").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingName() {
        new Group.Builder().identifier("1").addUser("user1").addUser("user2").build();
    }

    @Test
    public void testMissingUsers() {
        final String id = "1";
        final String name = "group1";
        final Group group = build();
        Assert.assertEquals(id, group.getIdentifier());
        Assert.assertEquals(name, group.getName());
        Assert.assertNotNull(group.getUsers());
        Assert.assertEquals(0, group.getUsers().size());
    }

    @Test
    public void testFromGroup() {
        final String id = "1";
        final String name = "group1";
        final String user1 = "user1";
        final String user2 = "user2";
        final Group group1 = build();
        Assert.assertEquals(id, group1.getIdentifier());
        Assert.assertEquals(name, group1.getName());
        Assert.assertNotNull(group1.getUsers());
        Assert.assertEquals(2, group1.getUsers().size());
        Assert.assertTrue(group1.getUsers().contains(user1));
        Assert.assertTrue(group1.getUsers().contains(user2));
        final Group group2 = build();
        Assert.assertEquals(group1.getIdentifier(), group2.getIdentifier());
        Assert.assertEquals(group1.getName(), group2.getName());
        Assert.assertEquals(group1.getUsers(), group2.getUsers());
    }

    @Test(expected = IllegalStateException.class)
    public void testFromGroupAndChangeIdentifier() {
        final Group group1 = build();
        new Group.Builder(group1).identifier("2").build();
    }

    @Test
    public void testAddRemoveClearUsers() {
        final Group.Builder builder = new Group.Builder().identifier("1").name("group1").addUser("user1");
        final Group group1 = builder.build();
        Assert.assertNotNull(group1.getUsers());
        Assert.assertEquals(1, group1.getUsers().size());
        Assert.assertTrue(group1.getUsers().contains("user1"));
        final Set<String> moreUsers = new HashSet<>();
        moreUsers.add("user2");
        moreUsers.add("user3");
        moreUsers.add("user4");
        final Group group2 = build();
        Assert.assertEquals(4, group2.getUsers().size());
        Assert.assertTrue(group2.getUsers().contains("user1"));
        Assert.assertTrue(group2.getUsers().contains("user2"));
        Assert.assertTrue(group2.getUsers().contains("user3"));
        Assert.assertTrue(group2.getUsers().contains("user4"));
        final Group group3 = build();
        Assert.assertEquals(3, group3.getUsers().size());
        Assert.assertTrue(group3.getUsers().contains("user1"));
        Assert.assertTrue(group3.getUsers().contains("user3"));
        Assert.assertTrue(group3.getUsers().contains("user4"));
        final Set<String> removeUsers = new HashSet<>();
        removeUsers.add("user1");
        removeUsers.add("user4");
        final Group group4 = build();
        Assert.assertEquals(1, group4.getUsers().size());
        Assert.assertTrue(group4.getUsers().contains("user3"));
        final Group group5 = build();
        Assert.assertEquals(0, group5.getUsers().size());
    }
}

