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
package org.apache.hadoop.hbase.security.access;


import AccessControlLists.ACL_TABLE_NAME;
import Permission.Action;
import Permission.Action.ADMIN;
import Permission.Scope.TABLE;
import TablePermission.Action.READ;
import TablePermission.Action.WRITE;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Abortable;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.apache.hbase.thirdparty.com.google.common.collect.ListMultimap;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static TablePermission.Action.READ;
import static TablePermission.Action.WRITE;


/**
 * Test the reading and writing of access permissions on {@code _acl_} table.
 */
@Category({ SecurityTests.class, LargeTests.class })
public class TestTablePermissions {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTablePermissions.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestTablePermissions.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static ZKWatcher ZKW;

    private static final Abortable ABORTABLE = new Abortable() {
        private final AtomicBoolean abort = new AtomicBoolean(false);

        @Override
        public void abort(String why, Throwable e) {
            TestTablePermissions.LOG.info(why, e);
            abort.set(true);
        }

        @Override
        public boolean isAborted() {
            return abort.get();
        }
    };

    private static String TEST_NAMESPACE = "perms_test_ns";

    private static String TEST_NAMESPACE2 = "perms_test_ns2";

    private static TableName TEST_TABLE = TableName.valueOf("perms_test");

    private static TableName TEST_TABLE2 = TableName.valueOf("perms_test2");

    private static byte[] TEST_FAMILY = Bytes.toBytes("f1");

    private static byte[] TEST_QUALIFIER = Bytes.toBytes("col1");

    @Test
    public void testBasicWrite() throws Exception {
        Configuration conf = TestTablePermissions.UTIL.getConfiguration();
        try (Connection connection = ConnectionFactory.createConnection(conf)) {
            // add some permissions
            addUserPermission(conf, new UserPermission("george", TestTablePermissions.TEST_TABLE, Action.READ, Action.WRITE), connection.getTable(ACL_TABLE_NAME));
            addUserPermission(conf, new UserPermission("hubert", TestTablePermissions.TEST_TABLE, Action.READ), connection.getTable(ACL_TABLE_NAME));
            addUserPermission(conf, new UserPermission("humphrey", TestTablePermissions.TEST_TABLE, TestTablePermissions.TEST_FAMILY, TestTablePermissions.TEST_QUALIFIER, Action.READ), connection.getTable(ACL_TABLE_NAME));
        }
        // retrieve the same
        ListMultimap<String, UserPermission> perms = AccessControlLists.getTablePermissions(conf, TestTablePermissions.TEST_TABLE);
        List<UserPermission> userPerms = perms.get("george");
        Assert.assertNotNull("Should have permissions for george", userPerms);
        Assert.assertEquals("Should have 1 permission for george", 1, userPerms.size());
        Assert.assertEquals(TABLE, userPerms.get(0).getAccessScope());
        TablePermission permission = ((TablePermission) (userPerms.get(0).getPermission()));
        Assert.assertEquals(("Permission should be for " + (TestTablePermissions.TEST_TABLE)), TestTablePermissions.TEST_TABLE, permission.getTableName());
        Assert.assertNull("Column family should be empty", permission.getFamily());
        // check actions
        Assert.assertNotNull(permission.getActions());
        Assert.assertEquals(2, permission.getActions().length);
        List<Permission.Action> actions = Arrays.asList(permission.getActions());
        Assert.assertTrue(actions.contains(READ));
        Assert.assertTrue(actions.contains(WRITE));
        userPerms = perms.get("hubert");
        Assert.assertNotNull("Should have permissions for hubert", userPerms);
        Assert.assertEquals("Should have 1 permission for hubert", 1, userPerms.size());
        Assert.assertEquals(TABLE, userPerms.get(0).getAccessScope());
        permission = ((TablePermission) (userPerms.get(0).getPermission()));
        Assert.assertEquals(("Permission should be for " + (TestTablePermissions.TEST_TABLE)), TestTablePermissions.TEST_TABLE, permission.getTableName());
        Assert.assertNull("Column family should be empty", permission.getFamily());
        // check actions
        Assert.assertNotNull(permission.getActions());
        Assert.assertEquals(1, permission.getActions().length);
        actions = Arrays.asList(permission.getActions());
        Assert.assertTrue(actions.contains(READ));
        Assert.assertFalse(actions.contains(WRITE));
        userPerms = perms.get("humphrey");
        Assert.assertNotNull("Should have permissions for humphrey", userPerms);
        Assert.assertEquals("Should have 1 permission for humphrey", 1, userPerms.size());
        Assert.assertEquals(TABLE, userPerms.get(0).getAccessScope());
        permission = ((TablePermission) (userPerms.get(0).getPermission()));
        Assert.assertEquals(("Permission should be for " + (TestTablePermissions.TEST_TABLE)), TestTablePermissions.TEST_TABLE, permission.getTableName());
        Assert.assertTrue(("Permission should be for family " + (Bytes.toString(TestTablePermissions.TEST_FAMILY))), Bytes.equals(TestTablePermissions.TEST_FAMILY, permission.getFamily()));
        Assert.assertTrue(("Permission should be for qualifier " + (Bytes.toString(TestTablePermissions.TEST_QUALIFIER))), Bytes.equals(TestTablePermissions.TEST_QUALIFIER, permission.getQualifier()));
        // check actions
        Assert.assertNotNull(permission.getActions());
        Assert.assertEquals(1, permission.getActions().length);
        actions = Arrays.asList(permission.getActions());
        Assert.assertTrue(actions.contains(READ));
        Assert.assertFalse(actions.contains(WRITE));
        // table 2 permissions
        try (Connection connection = ConnectionFactory.createConnection(conf);Table table = connection.getTable(ACL_TABLE_NAME)) {
            AccessControlLists.addUserPermission(conf, new UserPermission("hubert", TestTablePermissions.TEST_TABLE2, Action.READ, Action.WRITE), table);
        }
        // check full load
        Map<byte[], ListMultimap<String, UserPermission>> allPerms = AccessControlLists.loadAll(conf);
        Assert.assertEquals("Full permission map should have entries for both test tables", 2, allPerms.size());
        userPerms = allPerms.get(TestTablePermissions.TEST_TABLE.getName()).get("hubert");
        Assert.assertNotNull(userPerms);
        Assert.assertEquals(1, userPerms.size());
        Assert.assertEquals(TABLE, userPerms.get(0).getAccessScope());
        permission = ((TablePermission) (userPerms.get(0).getPermission()));
        Assert.assertEquals(TestTablePermissions.TEST_TABLE, permission.getTableName());
        Assert.assertEquals(1, permission.getActions().length);
        Assert.assertEquals(Permission.Action.READ, permission.getActions()[0]);
        userPerms = allPerms.get(TestTablePermissions.TEST_TABLE2.getName()).get("hubert");
        Assert.assertNotNull(userPerms);
        Assert.assertEquals(1, userPerms.size());
        Assert.assertEquals(TABLE, userPerms.get(0).getAccessScope());
        permission = ((TablePermission) (userPerms.get(0).getPermission()));
        Assert.assertEquals(TestTablePermissions.TEST_TABLE2, permission.getTableName());
        Assert.assertEquals(2, permission.getActions().length);
        actions = Arrays.asList(permission.getActions());
        Assert.assertTrue(actions.contains(Permission.Action.READ));
        Assert.assertTrue(actions.contains(Permission.Action.WRITE));
    }

    @Test
    public void testPersistence() throws Exception {
        Configuration conf = TestTablePermissions.UTIL.getConfiguration();
        try (Connection connection = ConnectionFactory.createConnection(conf)) {
            addUserPermission(conf, new UserPermission("albert", TestTablePermissions.TEST_TABLE, Action.READ), connection.getTable(ACL_TABLE_NAME));
            addUserPermission(conf, new UserPermission("betty", TestTablePermissions.TEST_TABLE, Action.READ, Action.WRITE), connection.getTable(ACL_TABLE_NAME));
            addUserPermission(conf, new UserPermission("clark", TestTablePermissions.TEST_TABLE, TestTablePermissions.TEST_FAMILY, Action.READ), connection.getTable(ACL_TABLE_NAME));
            addUserPermission(conf, new UserPermission("dwight", TestTablePermissions.TEST_TABLE, TestTablePermissions.TEST_FAMILY, TestTablePermissions.TEST_QUALIFIER, Action.WRITE), connection.getTable(ACL_TABLE_NAME));
        }
        // verify permissions survive changes in table metadata
        ListMultimap<String, UserPermission> preperms = AccessControlLists.getTablePermissions(conf, TestTablePermissions.TEST_TABLE);
        Table table = TestTablePermissions.UTIL.getConnection().getTable(TestTablePermissions.TEST_TABLE);
        table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes("row1")).addColumn(TestTablePermissions.TEST_FAMILY, TestTablePermissions.TEST_QUALIFIER, Bytes.toBytes("v1")));
        table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes("row2")).addColumn(TestTablePermissions.TEST_FAMILY, TestTablePermissions.TEST_QUALIFIER, Bytes.toBytes("v2")));
        Admin admin = TestTablePermissions.UTIL.getAdmin();
        try {
            admin.split(TestTablePermissions.TEST_TABLE);
        } catch (IOException e) {
            // although split fail, this may not affect following check
            // In old Split API without AM2, if region's best split key is not found,
            // there are not exception thrown. But in current API, exception
            // will be thrown.
            TestTablePermissions.LOG.debug(("region is not splittable, because " + e));
        }
        // wait for split
        Thread.sleep(10000);
        ListMultimap<String, UserPermission> postperms = AccessControlLists.getTablePermissions(conf, TestTablePermissions.TEST_TABLE);
        checkMultimapEqual(preperms, postperms);
    }

    @Test
    public void testSerialization() throws Exception {
        Configuration conf = TestTablePermissions.UTIL.getConfiguration();
        ListMultimap<String, UserPermission> permissions = createPermissions();
        byte[] permsData = AccessControlLists.writePermissionsAsBytes(permissions, conf);
        ListMultimap<String, UserPermission> copy = AccessControlLists.readUserPermission(permsData, conf);
        checkMultimapEqual(permissions, copy);
    }

    @Test
    public void testEquals() throws Exception {
        Permission p1 = new TablePermission(TestTablePermissions.TEST_TABLE, Action.READ);
        Permission p2 = new TablePermission(TestTablePermissions.TEST_TABLE, Action.READ);
        Assert.assertTrue(p1.equals(p2));
        Assert.assertTrue(p2.equals(p1));
        p1 = new TablePermission(TestTablePermissions.TEST_TABLE, READ, WRITE);
        p2 = new TablePermission(TestTablePermissions.TEST_TABLE, WRITE, READ);
        Assert.assertTrue(p1.equals(p2));
        Assert.assertTrue(p2.equals(p1));
        p1 = new TablePermission(TestTablePermissions.TEST_TABLE, TestTablePermissions.TEST_FAMILY, READ, WRITE);
        p2 = new TablePermission(TestTablePermissions.TEST_TABLE, TestTablePermissions.TEST_FAMILY, WRITE, READ);
        Assert.assertTrue(p1.equals(p2));
        Assert.assertTrue(p2.equals(p1));
        p1 = new TablePermission(TestTablePermissions.TEST_TABLE, TestTablePermissions.TEST_FAMILY, TestTablePermissions.TEST_QUALIFIER, READ, WRITE);
        p2 = new TablePermission(TestTablePermissions.TEST_TABLE, TestTablePermissions.TEST_FAMILY, TestTablePermissions.TEST_QUALIFIER, WRITE, READ);
        Assert.assertTrue(p1.equals(p2));
        Assert.assertTrue(p2.equals(p1));
        p1 = new TablePermission(TestTablePermissions.TEST_TABLE, READ);
        p2 = new TablePermission(TestTablePermissions.TEST_TABLE, TestTablePermissions.TEST_FAMILY, READ);
        Assert.assertFalse(p1.equals(p2));
        Assert.assertFalse(p2.equals(p1));
        p1 = new TablePermission(TestTablePermissions.TEST_TABLE, READ);
        p2 = new TablePermission(TestTablePermissions.TEST_TABLE, WRITE);
        Assert.assertFalse(p1.equals(p2));
        Assert.assertFalse(p2.equals(p1));
        p2 = new TablePermission(TestTablePermissions.TEST_TABLE, READ, WRITE);
        Assert.assertFalse(p1.equals(p2));
        Assert.assertFalse(p2.equals(p1));
        p1 = new TablePermission(TestTablePermissions.TEST_TABLE, READ);
        p2 = new TablePermission(TestTablePermissions.TEST_TABLE2, READ);
        Assert.assertFalse(p1.equals(p2));
        Assert.assertFalse(p2.equals(p1));
        p1 = new NamespacePermission(TestTablePermissions.TEST_NAMESPACE, READ);
        p2 = new NamespacePermission(TestTablePermissions.TEST_NAMESPACE, READ);
        Assert.assertEquals(p1, p2);
        p1 = new NamespacePermission(TestTablePermissions.TEST_NAMESPACE, READ);
        p2 = new NamespacePermission(TestTablePermissions.TEST_NAMESPACE2, READ);
        Assert.assertFalse(p1.equals(p2));
        Assert.assertFalse(p2.equals(p1));
    }

    @Test
    public void testGlobalPermission() throws Exception {
        Configuration conf = TestTablePermissions.UTIL.getConfiguration();
        // add some permissions
        try (Connection connection = ConnectionFactory.createConnection(conf)) {
            addUserPermission(conf, new UserPermission("user1", Action.READ, Action.WRITE), connection.getTable(ACL_TABLE_NAME));
            addUserPermission(conf, new UserPermission("user2", Action.CREATE), connection.getTable(ACL_TABLE_NAME));
            addUserPermission(conf, new UserPermission("user3", Action.ADMIN, Action.READ, Action.CREATE), connection.getTable(ACL_TABLE_NAME));
        }
        ListMultimap<String, UserPermission> perms = AccessControlLists.getTablePermissions(conf, null);
        List<UserPermission> user1Perms = perms.get("user1");
        Assert.assertEquals("Should have 1 permission for user1", 1, user1Perms.size());
        Assert.assertEquals("user1 should have WRITE permission", new Permission.Action[]{ Action.READ, Action.WRITE }, user1Perms.get(0).getPermission().getActions());
        List<UserPermission> user2Perms = perms.get("user2");
        Assert.assertEquals("Should have 1 permission for user2", 1, user2Perms.size());
        Assert.assertEquals("user2 should have CREATE permission", new Permission.Action[]{ Action.CREATE }, user2Perms.get(0).getPermission().getActions());
        List<UserPermission> user3Perms = perms.get("user3");
        Assert.assertEquals("Should have 1 permission for user3", 1, user3Perms.size());
        Assert.assertEquals("user3 should have ADMIN, READ, CREATE permission", new Permission.Action[]{ Action.READ, Action.CREATE, Action.ADMIN }, user3Perms.get(0).getPermission().getActions());
    }

    @Test
    public void testAuthManager() throws Exception {
        Configuration conf = TestTablePermissions.UTIL.getConfiguration();
        /**
         * test a race condition causing AuthManager to sometimes fail global permissions checks
         * when the global cache is being updated
         */
        AuthManager authManager = AuthManager.getOrCreate(TestTablePermissions.ZKW, conf);
        // currently running user is the system user and should have global admin perms
        User currentUser = User.getCurrent();
        Assert.assertTrue(authManager.authorizeUserGlobal(currentUser, ADMIN));
        try (Connection connection = ConnectionFactory.createConnection(conf)) {
            for (int i = 1; i <= 50; i++) {
                addUserPermission(conf, new UserPermission(("testauth" + i), Action.ADMIN, Action.READ, Action.WRITE), connection.getTable(ACL_TABLE_NAME));
                // make sure the system user still shows as authorized
                Assert.assertTrue(("Failed current user auth check on iter " + i), authManager.authorizeUserGlobal(currentUser, ADMIN));
            }
        }
    }
}

