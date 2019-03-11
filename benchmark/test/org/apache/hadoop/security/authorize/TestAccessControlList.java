/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.security.authorize;


import CommonConfigurationKeysPublic.HADOOP_SECURITY_GROUP_MAPPING;
import InterfaceAudience.LimitedPrivate;
import InterfaceStability.Evolving;
import java.util.Collection;
import java.util.Iterator;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.Groups;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.NativeCodeLoader;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@InterfaceAudience.LimitedPrivate({ "HDFS", "MapReduce" })
@InterfaceStability.Evolving
public class TestAccessControlList {
    private static final Logger LOG = LoggerFactory.getLogger(TestAccessControlList.class);

    /**
     * Test the netgroups (groups in ACL rules that start with @)
     *
     * This is a  manual test because it requires:
     *   - host setup
     *   - native code compiled
     *   - specify the group mapping class
     *
     * Host setup:
     *
     * /etc/nsswitch.conf should have a line like this:
     * netgroup: files
     *
     * /etc/netgroup should be (the whole file):
     * lasVegas (,elvis,)
     * memphis (,elvis,) (,jerryLeeLewis,)
     *
     * To run this test:
     *
     * export JAVA_HOME='path/to/java'
     * ant \
     *   -Dtestcase=TestAccessControlList \
     *   -Dtest.output=yes \
     *   -DTestAccessControlListGroupMapping=$className \
     *   compile-native test
     *
     * where $className is one of the classes that provide group
     * mapping services, i.e. classes that implement
     * GroupMappingServiceProvider interface, at this time:
     *   - org.apache.hadoop.security.JniBasedUnixGroupsNetgroupMapping
     *   - org.apache.hadoop.security.ShellBasedUnixGroupsNetgroupMapping
     */
    @Test
    public void testNetgroups() throws Exception {
        if (!(NativeCodeLoader.isNativeCodeLoaded())) {
            TestAccessControlList.LOG.info(("Not testing netgroups, " + "this test only runs when native code is compiled"));
            return;
        }
        String groupMappingClassName = System.getProperty("TestAccessControlListGroupMapping");
        if (groupMappingClassName == null) {
            TestAccessControlList.LOG.info(("Not testing netgroups, no group mapping class specified, " + (("use -DTestAccessControlListGroupMapping=$className to specify " + "group mapping class (must implement GroupMappingServiceProvider ") + "interface and support netgroups)")));
            return;
        }
        TestAccessControlList.LOG.info(("Testing netgroups using: " + groupMappingClassName));
        Configuration conf = new Configuration();
        conf.set(HADOOP_SECURITY_GROUP_MAPPING, groupMappingClassName);
        Groups groups = Groups.getUserToGroupsMappingService(conf);
        AccessControlList acl;
        // create these ACLs to populate groups cache
        acl = new AccessControlList("ja my");// plain

        acl = new AccessControlList("sinatra ratpack,@lasVegas");// netgroup

        acl = new AccessControlList(" somegroup,@someNetgroup");// no user

        // this ACL will be used for testing ACLs
        acl = new AccessControlList("carlPerkins ratpack,@lasVegas");
        acl.addGroup("@memphis");
        // validate the netgroups before and after rehresh to make
        // sure refresh works correctly
        validateNetgroups(groups, acl);
        groups.refresh();
        validateNetgroups(groups, acl);
    }

    @Test
    public void testWildCardAccessControlList() throws Exception {
        AccessControlList acl;
        acl = new AccessControlList("*");
        Assert.assertTrue(acl.isAllAllowed());
        acl = new AccessControlList("  * ");
        Assert.assertTrue(acl.isAllAllowed());
        acl = new AccessControlList(" *");
        Assert.assertTrue(acl.isAllAllowed());
        acl = new AccessControlList("*  ");
        Assert.assertTrue(acl.isAllAllowed());
    }

    // Check if AccessControlList.toString() works as expected.
    // Also validate if getAclString() for various cases.
    @Test
    public void testAclString() {
        AccessControlList acl;
        acl = new AccessControlList("*");
        Assert.assertEquals("All users are allowed", acl.toString());
        validateGetAclString(acl);
        acl = new AccessControlList(" ");
        Assert.assertEquals("No users are allowed", acl.toString());
        acl = new AccessControlList("user1,user2");
        Assert.assertEquals("Users [user1, user2] are allowed", acl.toString());
        validateGetAclString(acl);
        acl = new AccessControlList("user1,user2 ");// with space

        Assert.assertEquals("Users [user1, user2] are allowed", acl.toString());
        validateGetAclString(acl);
        acl = new AccessControlList(" group1,group2");
        Assert.assertTrue(acl.toString().equals("Members of the groups [group1, group2] are allowed"));
        validateGetAclString(acl);
        acl = new AccessControlList("user1,user2 group1,group2");
        Assert.assertTrue(acl.toString().equals(("Users [user1, user2] and " + "members of the groups [group1, group2] are allowed")));
        validateGetAclString(acl);
    }

    @Test
    public void testAccessControlList() throws Exception {
        AccessControlList acl;
        Collection<String> users;
        Collection<String> groups;
        acl = new AccessControlList("drwho tardis");
        users = acl.getUsers();
        Assert.assertEquals(users.size(), 1);
        Assert.assertEquals(users.iterator().next(), "drwho");
        groups = acl.getGroups();
        Assert.assertEquals(groups.size(), 1);
        Assert.assertEquals(groups.iterator().next(), "tardis");
        acl = new AccessControlList("drwho");
        users = acl.getUsers();
        Assert.assertEquals(users.size(), 1);
        Assert.assertEquals(users.iterator().next(), "drwho");
        groups = acl.getGroups();
        Assert.assertEquals(groups.size(), 0);
        acl = new AccessControlList("drwho ");
        users = acl.getUsers();
        Assert.assertEquals(users.size(), 1);
        Assert.assertEquals(users.iterator().next(), "drwho");
        groups = acl.getGroups();
        Assert.assertEquals(groups.size(), 0);
        acl = new AccessControlList(" tardis");
        users = acl.getUsers();
        Assert.assertEquals(users.size(), 0);
        groups = acl.getGroups();
        Assert.assertEquals(groups.size(), 1);
        Assert.assertEquals(groups.iterator().next(), "tardis");
        Iterator<String> iter;
        acl = new AccessControlList("drwho,joe tardis, users");
        users = acl.getUsers();
        Assert.assertEquals(users.size(), 2);
        iter = users.iterator();
        Assert.assertEquals(iter.next(), "drwho");
        Assert.assertEquals(iter.next(), "joe");
        groups = acl.getGroups();
        Assert.assertEquals(groups.size(), 2);
        iter = groups.iterator();
        Assert.assertEquals(iter.next(), "tardis");
        Assert.assertEquals(iter.next(), "users");
    }

    /**
     * Test addUser/Group and removeUser/Group api.
     */
    @Test
    public void testAddRemoveAPI() {
        AccessControlList acl;
        Collection<String> users;
        Collection<String> groups;
        acl = new AccessControlList(" ");
        Assert.assertEquals(0, acl.getUsers().size());
        Assert.assertEquals(0, acl.getGroups().size());
        Assert.assertEquals(" ", acl.getAclString());
        acl.addUser("drwho");
        users = acl.getUsers();
        Assert.assertEquals(users.size(), 1);
        Assert.assertEquals(users.iterator().next(), "drwho");
        Assert.assertEquals("drwho ", acl.getAclString());
        acl.addGroup("tardis");
        groups = acl.getGroups();
        Assert.assertEquals(groups.size(), 1);
        Assert.assertEquals(groups.iterator().next(), "tardis");
        Assert.assertEquals("drwho tardis", acl.getAclString());
        acl.addUser("joe");
        acl.addGroup("users");
        users = acl.getUsers();
        Assert.assertEquals(users.size(), 2);
        Iterator<String> iter = users.iterator();
        Assert.assertEquals(iter.next(), "drwho");
        Assert.assertEquals(iter.next(), "joe");
        groups = acl.getGroups();
        Assert.assertEquals(groups.size(), 2);
        iter = groups.iterator();
        Assert.assertEquals(iter.next(), "tardis");
        Assert.assertEquals(iter.next(), "users");
        Assert.assertEquals("drwho,joe tardis,users", acl.getAclString());
        acl.removeUser("joe");
        acl.removeGroup("users");
        users = acl.getUsers();
        Assert.assertEquals(users.size(), 1);
        Assert.assertFalse(users.contains("joe"));
        groups = acl.getGroups();
        Assert.assertEquals(groups.size(), 1);
        Assert.assertFalse(groups.contains("users"));
        Assert.assertEquals("drwho tardis", acl.getAclString());
        acl.removeGroup("tardis");
        groups = acl.getGroups();
        Assert.assertEquals(0, groups.size());
        Assert.assertFalse(groups.contains("tardis"));
        Assert.assertEquals("drwho ", acl.getAclString());
        acl.removeUser("drwho");
        Assert.assertEquals(0, users.size());
        Assert.assertFalse(users.contains("drwho"));
        Assert.assertEquals(0, acl.getGroups().size());
        Assert.assertEquals(0, acl.getUsers().size());
        Assert.assertEquals(" ", acl.getAclString());
    }

    /**
     * Tests adding/removing wild card as the user/group.
     */
    @Test
    public void testAddRemoveWildCard() {
        AccessControlList acl = new AccessControlList("drwho tardis");
        Throwable th = null;
        try {
            acl.addUser(" * ");
        } catch (Throwable t) {
            th = t;
        }
        Assert.assertNotNull(th);
        Assert.assertTrue((th instanceof IllegalArgumentException));
        th = null;
        try {
            acl.addGroup(" * ");
        } catch (Throwable t) {
            th = t;
        }
        Assert.assertNotNull(th);
        Assert.assertTrue((th instanceof IllegalArgumentException));
        th = null;
        try {
            acl.removeUser(" * ");
        } catch (Throwable t) {
            th = t;
        }
        Assert.assertNotNull(th);
        Assert.assertTrue((th instanceof IllegalArgumentException));
        th = null;
        try {
            acl.removeGroup(" * ");
        } catch (Throwable t) {
            th = t;
        }
        Assert.assertNotNull(th);
        Assert.assertTrue((th instanceof IllegalArgumentException));
    }

    /**
     * Tests adding user/group to an wild card acl.
     */
    @Test
    public void testAddRemoveToWildCardACL() {
        AccessControlList acl = new AccessControlList(" * ");
        Assert.assertTrue(acl.isAllAllowed());
        UserGroupInformation drwho = UserGroupInformation.createUserForTesting("drwho@EXAMPLE.COM", new String[]{ "aliens" });
        UserGroupInformation drwho2 = UserGroupInformation.createUserForTesting("drwho2@EXAMPLE.COM", new String[]{ "tardis" });
        acl.addUser("drwho");
        Assert.assertTrue(acl.isAllAllowed());
        Assert.assertFalse(acl.getAclString().contains("drwho"));
        acl.addGroup("tardis");
        Assert.assertTrue(acl.isAllAllowed());
        Assert.assertFalse(acl.getAclString().contains("tardis"));
        acl.removeUser("drwho");
        Assert.assertTrue(acl.isAllAllowed());
        assertUserAllowed(drwho, acl);
        acl.removeGroup("tardis");
        Assert.assertTrue(acl.isAllAllowed());
        assertUserAllowed(drwho2, acl);
    }

    /**
     * Verify the method isUserAllowed()
     */
    @Test
    public void testIsUserAllowed() {
        AccessControlList acl;
        UserGroupInformation drwho = UserGroupInformation.createUserForTesting("drwho@EXAMPLE.COM", new String[]{ "aliens", "humanoids", "timelord" });
        UserGroupInformation susan = UserGroupInformation.createUserForTesting("susan@EXAMPLE.COM", new String[]{ "aliens", "humanoids", "timelord" });
        UserGroupInformation barbara = UserGroupInformation.createUserForTesting("barbara@EXAMPLE.COM", new String[]{ "humans", "teachers" });
        UserGroupInformation ian = UserGroupInformation.createUserForTesting("ian@EXAMPLE.COM", new String[]{ "humans", "teachers" });
        acl = new AccessControlList("drwho humanoids");
        assertUserAllowed(drwho, acl);
        assertUserAllowed(susan, acl);
        assertUserNotAllowed(barbara, acl);
        assertUserNotAllowed(ian, acl);
        acl = new AccessControlList("drwho");
        assertUserAllowed(drwho, acl);
        assertUserNotAllowed(susan, acl);
        assertUserNotAllowed(barbara, acl);
        assertUserNotAllowed(ian, acl);
        acl = new AccessControlList("drwho ");
        assertUserAllowed(drwho, acl);
        assertUserNotAllowed(susan, acl);
        assertUserNotAllowed(barbara, acl);
        assertUserNotAllowed(ian, acl);
        acl = new AccessControlList(" humanoids");
        assertUserAllowed(drwho, acl);
        assertUserAllowed(susan, acl);
        assertUserNotAllowed(barbara, acl);
        assertUserNotAllowed(ian, acl);
        acl = new AccessControlList("drwho,ian aliens,teachers");
        assertUserAllowed(drwho, acl);
        assertUserAllowed(susan, acl);
        assertUserAllowed(barbara, acl);
        assertUserAllowed(ian, acl);
        acl = new AccessControlList("");
        UserGroupInformation spyUser = Mockito.spy(drwho);
        acl.isUserAllowed(spyUser);
        Mockito.verify(spyUser, Mockito.never()).getGroupNames();
    }
}

