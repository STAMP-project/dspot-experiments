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


import Action.READ;
import Action.WRITE;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.AuthUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TestTableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.security.access.Permission.Action;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ SecurityTests.class, MediumTests.class })
public class TestCellACLWithMultipleVersions extends SecureTestUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCellACLWithMultipleVersions.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCellACLWithMultipleVersions.class);

    @Rule
    public TestTableName TEST_TABLE = new TestTableName();

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] TEST_FAMILY1 = Bytes.toBytes("f1");

    private static final byte[] TEST_FAMILY2 = Bytes.toBytes("f2");

    private static final byte[] TEST_ROW = Bytes.toBytes("cellpermtest");

    private static final byte[] TEST_Q1 = Bytes.toBytes("q1");

    private static final byte[] TEST_Q2 = Bytes.toBytes("q2");

    private static final byte[] ZERO = Bytes.toBytes(0L);

    private static final byte[] ONE = Bytes.toBytes(1L);

    private static final byte[] TWO = Bytes.toBytes(2L);

    private static Configuration conf;

    private static final String GROUP = "group";

    private static User GROUP_USER;

    private static User USER_OWNER;

    private static User USER_OTHER;

    private static User USER_OTHER2;

    private static String[] usersAndGroups;

    @Test
    public void testCellPermissionwithVersions() throws Exception {
        // store two sets of values, one store with a cell level ACL, and one
        // without
        final Map<String, Permission> writePerms = prepareCellPermissions(TestCellACLWithMultipleVersions.usersAndGroups, WRITE);
        final Map<String, Permission> readPerms = prepareCellPermissions(TestCellACLWithMultipleVersions.usersAndGroups, READ);
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    Put p;
                    // with ro ACL
                    p = new Put(TestCellACLWithMultipleVersions.TEST_ROW).addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1, TestCellACLWithMultipleVersions.ZERO);
                    p.setACL(writePerms);
                    t.put(p);
                    // with ro ACL
                    p = new Put(TestCellACLWithMultipleVersions.TEST_ROW).addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1, TestCellACLWithMultipleVersions.ZERO);
                    p.setACL(readPerms);
                    t.put(p);
                    p = new Put(TestCellACLWithMultipleVersions.TEST_ROW).addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1, TestCellACLWithMultipleVersions.ZERO);
                    p.setACL(writePerms);
                    t.put(p);
                    p = new Put(TestCellACLWithMultipleVersions.TEST_ROW).addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1, TestCellACLWithMultipleVersions.ZERO);
                    p.setACL(readPerms);
                    t.put(p);
                    p = new Put(TestCellACLWithMultipleVersions.TEST_ROW).addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1, TestCellACLWithMultipleVersions.ZERO);
                    p.setACL(writePerms);
                    t.put(p);
                }
                return null;
            }
        }, TestCellACLWithMultipleVersions.USER_OWNER);
        /* ---- Gets ---- */
        SecureTestUtil.AccessTestAction getQ1 = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Get get = new Get(TestCellACLWithMultipleVersions.TEST_ROW);
                get.setMaxVersions(10);
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    return t.get(get).listCells();
                }
            }
        };
        SecureTestUtil.AccessTestAction get2 = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Get get = new Get(TestCellACLWithMultipleVersions.TEST_ROW);
                get.setMaxVersions(10);
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    return t.get(get).listCells();
                }
            }
        };
        // Confirm special read access set at cell level
        SecureTestUtil.verifyAllowed(TestCellACLWithMultipleVersions.GROUP_USER, getQ1, 2);
        SecureTestUtil.verifyAllowed(TestCellACLWithMultipleVersions.USER_OTHER, getQ1, 2);
        // store two sets of values, one store with a cell level ACL, and one
        // without
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    Put p;
                    p = new Put(TestCellACLWithMultipleVersions.TEST_ROW).addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1, TestCellACLWithMultipleVersions.ZERO);
                    p.setACL(writePerms);
                    t.put(p);
                    p = new Put(TestCellACLWithMultipleVersions.TEST_ROW).addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1, TestCellACLWithMultipleVersions.ZERO);
                    p.setACL(readPerms);
                    t.put(p);
                    p = new Put(TestCellACLWithMultipleVersions.TEST_ROW).addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1, TestCellACLWithMultipleVersions.ZERO);
                    p.setACL(writePerms);
                    t.put(p);
                }
                return null;
            }
        }, TestCellACLWithMultipleVersions.USER_OWNER);
        // Confirm special read access set at cell level
        SecureTestUtil.verifyAllowed(TestCellACLWithMultipleVersions.USER_OTHER, get2, 1);
        SecureTestUtil.verifyAllowed(TestCellACLWithMultipleVersions.GROUP_USER, get2, 1);
    }

    @Test
    public void testCellPermissionsWithDeleteMutipleVersions() throws Exception {
        // table/column/qualifier level permissions
        final byte[] TEST_ROW1 = Bytes.toBytes("r1");
        final byte[] TEST_ROW2 = Bytes.toBytes("r2");
        final byte[] TEST_Q1 = Bytes.toBytes("q1");
        final byte[] TEST_Q2 = Bytes.toBytes("q2");
        final byte[] ZERO = Bytes.toBytes(0L);
        // additional test user
        final User user1 = User.createUserForTesting(TestCellACLWithMultipleVersions.conf, "user1", new String[0]);
        final User user2 = User.createUserForTesting(TestCellACLWithMultipleVersions.conf, "user2", new String[0]);
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        // with rw ACL for "user1"
                        Put p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q1, ZERO);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q2, ZERO);
                        p.setACL(user1.getShortName(), new Permission(Action.READ, Action.WRITE));
                        t.put(p);
                        // with rw ACL for "user1"
                        p = new Put(TEST_ROW2);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q1, ZERO);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q2, ZERO);
                        p.setACL(user1.getShortName(), new Permission(Action.READ, Action.WRITE));
                        t.put(p);
                    }
                }
                return null;
            }
        }, TestCellACLWithMultipleVersions.USER_OWNER);
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        // with rw ACL for "user1", "user2" and "@group"
                        Put p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q1, ZERO);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q2, ZERO);
                        Map<String, Permission> perms = prepareCellPermissions(new String[]{ user1.getShortName(), user2.getShortName(), AuthUtil.toGroupEntry(TestCellACLWithMultipleVersions.GROUP) }, READ, WRITE);
                        p.setACL(perms);
                        t.put(p);
                        // with rw ACL for "user1", "user2" and "@group"
                        p = new Put(TEST_ROW2);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q1, ZERO);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q2, ZERO);
                        p.setACL(perms);
                        t.put(p);
                    }
                }
                return null;
            }
        }, user1);
        // user1 should be allowed to delete TEST_ROW1 as he is having write permission on both
        // versions of the cells
        user1.runAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        Delete d = new Delete(TEST_ROW1);
                        d.addColumns(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q1);
                        d.addColumns(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q2);
                        t.delete(d);
                    }
                }
                return null;
            }
        });
        // user2 should not be allowed to delete TEST_ROW2 as he is having write permission only on one
        // version of the cells.
        verifyUserDeniedForDeleteMultipleVersions(user2, TEST_ROW2, TEST_Q1, TEST_Q2);
        // GROUP_USER should not be allowed to delete TEST_ROW2 as he is having write permission only on
        // one version of the cells.
        verifyUserDeniedForDeleteMultipleVersions(TestCellACLWithMultipleVersions.GROUP_USER, TEST_ROW2, TEST_Q1, TEST_Q2);
        // user1 should be allowed to delete the cf. (All data under cf for a row)
        user1.runAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        Delete d = new Delete(TEST_ROW2);
                        d.addFamily(TestCellACLWithMultipleVersions.TEST_FAMILY1);
                        t.delete(d);
                    }
                }
                return null;
            }
        });
    }

    @Test
    public void testDeleteWithFutureTimestamp() throws Exception {
        // Store two values, one in the future
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        // Store a read write ACL without a timestamp, server will use current time
                        Put p = new Put(TestCellACLWithMultipleVersions.TEST_ROW).addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q2, TestCellACLWithMultipleVersions.ONE);
                        Map<String, Permission> readAndWritePerms = prepareCellPermissions(TestCellACLWithMultipleVersions.usersAndGroups, READ, WRITE);
                        p.setACL(readAndWritePerms);
                        t.put(p);
                        p = new Put(TestCellACLWithMultipleVersions.TEST_ROW).addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY2, TestCellACLWithMultipleVersions.TEST_Q2, TestCellACLWithMultipleVersions.ONE);
                        p.setACL(readAndWritePerms);
                        t.put(p);
                        TestCellACLWithMultipleVersions.LOG.info("Stored at current time");
                        // Store read only ACL at a future time
                        p = new Put(TestCellACLWithMultipleVersions.TEST_ROW).addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1, ((EnvironmentEdgeManager.currentTime()) + 1000000), TestCellACLWithMultipleVersions.ZERO);
                        p.setACL(prepareCellPermissions(new String[]{ TestCellACLWithMultipleVersions.USER_OTHER.getShortName(), AuthUtil.toGroupEntry(TestCellACLWithMultipleVersions.GROUP) }, READ));
                        t.put(p);
                    }
                }
                return null;
            }
        }, TestCellACLWithMultipleVersions.USER_OWNER);
        // Confirm stores are visible
        SecureTestUtil.AccessTestAction getQ1 = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Get get = new Get(TestCellACLWithMultipleVersions.TEST_ROW).addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1);
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        return t.get(get).listCells();
                    }
                }
            }
        };
        SecureTestUtil.AccessTestAction getQ2 = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Get get = new Get(TestCellACLWithMultipleVersions.TEST_ROW).addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q2);
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        return t.get(get).listCells();
                    }
                }
            }
        };
        SecureTestUtil.verifyAllowed(getQ1, TestCellACLWithMultipleVersions.USER_OWNER, TestCellACLWithMultipleVersions.USER_OTHER, TestCellACLWithMultipleVersions.GROUP_USER);
        SecureTestUtil.verifyAllowed(getQ2, TestCellACLWithMultipleVersions.USER_OWNER, TestCellACLWithMultipleVersions.USER_OTHER, TestCellACLWithMultipleVersions.GROUP_USER);
        // Issue a DELETE for the family, should succeed because the future ACL is
        // not considered
        SecureTestUtil.AccessTestAction deleteFamily1 = getDeleteFamilyAction(TestCellACLWithMultipleVersions.TEST_FAMILY1);
        SecureTestUtil.AccessTestAction deleteFamily2 = getDeleteFamilyAction(TestCellACLWithMultipleVersions.TEST_FAMILY2);
        SecureTestUtil.verifyAllowed(deleteFamily1, TestCellACLWithMultipleVersions.USER_OTHER);
        SecureTestUtil.verifyAllowed(deleteFamily2, TestCellACLWithMultipleVersions.GROUP_USER);
        // The future put should still exist
        SecureTestUtil.verifyAllowed(getQ1, TestCellACLWithMultipleVersions.USER_OWNER, TestCellACLWithMultipleVersions.USER_OTHER, TestCellACLWithMultipleVersions.GROUP_USER);
        // The other put should be covered by the tombstone
        SecureTestUtil.verifyIfNull(getQ2, TestCellACLWithMultipleVersions.USER_OTHER, TestCellACLWithMultipleVersions.GROUP_USER);
    }

    @Test
    public void testCellPermissionsWithDeleteWithUserTs() throws Exception {
        TestCellACLWithMultipleVersions.USER_OWNER.runAs(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        // This version (TS = 123) with rw ACL for USER_OTHER and USER_OTHER2
                        Put p = new Put(TestCellACLWithMultipleVersions.TEST_ROW);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1, 123L, TestCellACLWithMultipleVersions.ZERO);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q2, 123L, TestCellACLWithMultipleVersions.ZERO);
                        p.setACL(prepareCellPermissions(new String[]{ TestCellACLWithMultipleVersions.USER_OTHER.getShortName(), AuthUtil.toGroupEntry(TestCellACLWithMultipleVersions.GROUP), TestCellACLWithMultipleVersions.USER_OTHER2.getShortName() }, Permission.Action.READ, Permission.Action.WRITE));
                        t.put(p);
                        // This version (TS = 125) with rw ACL for USER_OTHER
                        p = new Put(TestCellACLWithMultipleVersions.TEST_ROW);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1, 125L, TestCellACLWithMultipleVersions.ONE);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q2, 125L, TestCellACLWithMultipleVersions.ONE);
                        p.setACL(prepareCellPermissions(new String[]{ TestCellACLWithMultipleVersions.USER_OTHER.getShortName(), AuthUtil.toGroupEntry(TestCellACLWithMultipleVersions.GROUP) }, READ, WRITE));
                        t.put(p);
                        // This version (TS = 127) with rw ACL for USER_OTHER
                        p = new Put(TestCellACLWithMultipleVersions.TEST_ROW);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1, 127L, TestCellACLWithMultipleVersions.TWO);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q2, 127L, TestCellACLWithMultipleVersions.TWO);
                        p.setACL(prepareCellPermissions(new String[]{ TestCellACLWithMultipleVersions.USER_OTHER.getShortName(), AuthUtil.toGroupEntry(TestCellACLWithMultipleVersions.GROUP) }, READ, WRITE));
                        t.put(p);
                        return null;
                    }
                }
            }
        });
        // USER_OTHER2 should be allowed to delete the column f1:q1 versions older than TS 124L
        TestCellACLWithMultipleVersions.USER_OTHER2.runAs(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        Delete d = new Delete(TestCellACLWithMultipleVersions.TEST_ROW, 124L);
                        d.addColumns(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1);
                        t.delete(d);
                    }
                }
                return null;
            }
        });
        // USER_OTHER2 should be allowed to delete the column f1:q2 versions older than TS 124L
        TestCellACLWithMultipleVersions.USER_OTHER2.runAs(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        Delete d = new Delete(TestCellACLWithMultipleVersions.TEST_ROW);
                        d.addColumns(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q2, 124L);
                        t.delete(d);
                    }
                }
                return null;
            }
        });
    }

    @Test
    public void testCellPermissionsWithDeleteExactVersion() throws Exception {
        final byte[] TEST_ROW1 = Bytes.toBytes("r1");
        final byte[] TEST_Q1 = Bytes.toBytes("q1");
        final byte[] TEST_Q2 = Bytes.toBytes("q2");
        final byte[] ZERO = Bytes.toBytes(0L);
        final User user1 = User.createUserForTesting(TestCellACLWithMultipleVersions.conf, "user1", new String[0]);
        final User user2 = User.createUserForTesting(TestCellACLWithMultipleVersions.conf, "user2", new String[0]);
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        Map<String, Permission> permsU1andOwner = prepareCellPermissions(new String[]{ user1.getShortName(), TestCellACLWithMultipleVersions.USER_OWNER.getShortName() }, READ, WRITE);
                        Map<String, Permission> permsU2andGUandOwner = prepareCellPermissions(new String[]{ user2.getShortName(), AuthUtil.toGroupEntry(TestCellACLWithMultipleVersions.GROUP), TestCellACLWithMultipleVersions.USER_OWNER.getShortName() }, READ, WRITE);
                        Put p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q1, 123, ZERO);
                        p.setACL(permsU1andOwner);
                        t.put(p);
                        p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q2, 123, ZERO);
                        p.setACL(permsU2andGUandOwner);
                        t.put(p);
                        p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY2, TEST_Q1, 123, ZERO);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY2, TEST_Q2, 123, ZERO);
                        p.setACL(permsU2andGUandOwner);
                        t.put(p);
                        p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY2, TEST_Q1, 125, ZERO);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY2, TEST_Q2, 125, ZERO);
                        p.setACL(permsU1andOwner);
                        t.put(p);
                        p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q1, 127, ZERO);
                        p.setACL(permsU2andGUandOwner);
                        t.put(p);
                        p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q2, 127, ZERO);
                        p.setACL(permsU1andOwner);
                        t.put(p);
                        p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY2, TEST_Q1, 129, ZERO);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY2, TEST_Q2, 129, ZERO);
                        p.setACL(permsU1andOwner);
                        t.put(p);
                    }
                }
                return null;
            }
        }, TestCellACLWithMultipleVersions.USER_OWNER);
        // user1 should be allowed to delete TEST_ROW1 as he is having write permission on both
        // versions of the cells
        user1.runAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        Delete d = new Delete(TEST_ROW1);
                        d.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q1, 123);
                        d.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q2);
                        d.addFamilyVersion(TestCellACLWithMultipleVersions.TEST_FAMILY2, 125);
                        t.delete(d);
                    }
                }
                return null;
            }
        });
        verifyUserDeniedForDeleteExactVersion(user2, TEST_ROW1, TEST_Q1, TEST_Q2);
        verifyUserDeniedForDeleteExactVersion(TestCellACLWithMultipleVersions.GROUP_USER, TEST_ROW1, TEST_Q1, TEST_Q2);
    }

    @Test
    public void testCellPermissionsForIncrementWithMultipleVersions() throws Exception {
        final byte[] TEST_ROW1 = Bytes.toBytes("r1");
        final byte[] TEST_Q1 = Bytes.toBytes("q1");
        final byte[] TEST_Q2 = Bytes.toBytes("q2");
        final byte[] ZERO = Bytes.toBytes(0L);
        final User user1 = User.createUserForTesting(TestCellACLWithMultipleVersions.conf, "user1", new String[0]);
        final User user2 = User.createUserForTesting(TestCellACLWithMultipleVersions.conf, "user2", new String[0]);
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        Map<String, Permission> permsU1andOwner = prepareCellPermissions(new String[]{ user1.getShortName(), TestCellACLWithMultipleVersions.USER_OWNER.getShortName() }, READ, WRITE);
                        Map<String, Permission> permsU2andGUandOwner = prepareCellPermissions(new String[]{ user2.getShortName(), AuthUtil.toGroupEntry(TestCellACLWithMultipleVersions.GROUP), TestCellACLWithMultipleVersions.USER_OWNER.getShortName() }, READ, WRITE);
                        Put p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q1, 123, ZERO);
                        p.setACL(permsU1andOwner);
                        t.put(p);
                        p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q2, 123, ZERO);
                        p.setACL(permsU2andGUandOwner);
                        t.put(p);
                        p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q1, 127, ZERO);
                        p.setACL(permsU2andGUandOwner);
                        t.put(p);
                        p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q2, 127, ZERO);
                        p.setACL(permsU1andOwner);
                        t.put(p);
                    }
                }
                return null;
            }
        }, TestCellACLWithMultipleVersions.USER_OWNER);
        // Increment considers the TimeRange set on it.
        user1.runAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        Increment inc = new Increment(TEST_ROW1);
                        inc.setTimeRange(0, 123);
                        inc.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q1, 2L);
                        t.increment(inc);
                        t.incrementColumnValue(TEST_ROW1, TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q2, 1L);
                    }
                }
                return null;
            }
        });
        verifyUserDeniedForIncrementMultipleVersions(user2, TEST_ROW1, TEST_Q2);
        verifyUserDeniedForIncrementMultipleVersions(TestCellACLWithMultipleVersions.GROUP_USER, TEST_ROW1, TEST_Q2);
    }

    @Test
    public void testCellPermissionsForPutWithMultipleVersions() throws Exception {
        final byte[] TEST_ROW1 = Bytes.toBytes("r1");
        final byte[] TEST_Q1 = Bytes.toBytes("q1");
        final byte[] TEST_Q2 = Bytes.toBytes("q2");
        final byte[] ZERO = Bytes.toBytes(0L);
        final User user1 = User.createUserForTesting(TestCellACLWithMultipleVersions.conf, "user1", new String[0]);
        final User user2 = User.createUserForTesting(TestCellACLWithMultipleVersions.conf, "user2", new String[0]);
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        Map<String, Permission> permsU1andOwner = prepareCellPermissions(new String[]{ user1.getShortName(), TestCellACLWithMultipleVersions.USER_OWNER.getShortName() }, READ, WRITE);
                        Map<String, Permission> permsU2andGUandOwner = prepareCellPermissions(new String[]{ user1.getShortName(), AuthUtil.toGroupEntry(TestCellACLWithMultipleVersions.GROUP), TestCellACLWithMultipleVersions.USER_OWNER.getShortName() }, READ, WRITE);
                        permsU2andGUandOwner.put(user2.getShortName(), new Permission(Action.READ, Action.WRITE));
                        permsU2andGUandOwner.put(TestCellACLWithMultipleVersions.USER_OWNER.getShortName(), new Permission(Action.READ, Action.WRITE));
                        Put p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q1, 123, ZERO);
                        p.setACL(permsU1andOwner);
                        t.put(p);
                        p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q2, 123, ZERO);
                        p.setACL(permsU2andGUandOwner);
                        t.put(p);
                        p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q1, 127, ZERO);
                        p.setACL(permsU2andGUandOwner);
                        t.put(p);
                        p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q2, 127, ZERO);
                        p.setACL(permsU1andOwner);
                        t.put(p);
                    }
                }
                return null;
            }
        }, TestCellACLWithMultipleVersions.USER_OWNER);
        // new Put with TEST_Q1 column having TS=125. This covers old cell with TS 123 and user1 is
        // having RW permission. While TEST_Q2 is with latest TS and so it covers old cell with TS 127.
        // User1 is having RW permission on that too.
        user1.runAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        Put p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q1, 125, ZERO);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q2, ZERO);
                        p.setACL(user2.getShortName(), new Permission(Action.READ, Action.WRITE));
                        t.put(p);
                    }
                }
                return null;
            }
        });
        verifyUserDeniedForPutMultipleVersions(user2, TEST_ROW1, TEST_Q1, TEST_Q2, ZERO);
        verifyUserDeniedForPutMultipleVersions(TestCellACLWithMultipleVersions.GROUP_USER, TEST_ROW1, TEST_Q1, TEST_Q2, ZERO);
    }

    @Test
    public void testCellPermissionsForCheckAndDelete() throws Exception {
        final byte[] TEST_ROW1 = Bytes.toBytes("r1");
        final byte[] TEST_Q3 = Bytes.toBytes("q3");
        final byte[] ZERO = Bytes.toBytes(0L);
        final User user1 = User.createUserForTesting(TestCellACLWithMultipleVersions.conf, "user1", new String[0]);
        final User user2 = User.createUserForTesting(TestCellACLWithMultipleVersions.conf, "user2", new String[0]);
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        Map<String, Permission> permsU1andOwner = prepareCellPermissions(new String[]{ user1.getShortName(), TestCellACLWithMultipleVersions.USER_OWNER.getShortName() }, READ, WRITE);
                        Map<String, Permission> permsU1andU2andGUandOwner = prepareCellPermissions(new String[]{ user1.getShortName(), user2.getShortName(), AuthUtil.toGroupEntry(TestCellACLWithMultipleVersions.GROUP), TestCellACLWithMultipleVersions.USER_OWNER.getShortName() }, READ, WRITE);
                        Map<String, Permission> permsU1_U2andGU = prepareCellPermissions(new String[]{ user1.getShortName(), user2.getShortName(), AuthUtil.toGroupEntry(TestCellACLWithMultipleVersions.GROUP) }, READ, WRITE);
                        Put p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1, 120, ZERO);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q2, 120, ZERO);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q3, 120, ZERO);
                        p.setACL(permsU1andU2andGUandOwner);
                        t.put(p);
                        p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1, 123, ZERO);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q2, 123, ZERO);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q3, 123, ZERO);
                        p.setACL(permsU1andOwner);
                        t.put(p);
                        p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1, 127, ZERO);
                        p.setACL(permsU1_U2andGU);
                        t.put(p);
                        p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q2, 127, ZERO);
                        p.setACL(user2.getShortName(), new Permission(Action.READ));
                        t.put(p);
                        p = new Put(TEST_ROW1);
                        p.addColumn(TestCellACLWithMultipleVersions.TEST_FAMILY1, TEST_Q3, 127, ZERO);
                        p.setACL(AuthUtil.toGroupEntry(TestCellACLWithMultipleVersions.GROUP), new Permission(Action.READ));
                        t.put(p);
                    }
                }
                return null;
            }
        }, TestCellACLWithMultipleVersions.USER_OWNER);
        // user1 should be allowed to do the checkAndDelete. user1 having read permission on the latest
        // version cell and write permission on all versions
        user1.runAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLWithMultipleVersions.conf)) {
                    try (Table t = connection.getTable(TEST_TABLE.getTableName())) {
                        Delete d = new Delete(TEST_ROW1);
                        d.addColumns(TestCellACLWithMultipleVersions.TEST_FAMILY1, TestCellACLWithMultipleVersions.TEST_Q1, 120);
                        t.checkAndMutate(TEST_ROW1, TestCellACLWithMultipleVersions.TEST_FAMILY1).qualifier(TestCellACLWithMultipleVersions.TEST_Q1).ifEquals(ZERO).thenDelete(d);
                    }
                }
                return null;
            }
        });
        // user2 shouldn't be allowed to do the checkAndDelete. user2 having RW permission on the latest
        // version cell but not on cell version TS=123
        verifyUserDeniedForCheckAndDelete(user2, TEST_ROW1, ZERO);
        // GROUP_USER shouldn't be allowed to do the checkAndDelete. GROUP_USER having RW permission on
        // the latest
        // version cell but not on cell version TS=123
        verifyUserDeniedForCheckAndDelete(TestCellACLWithMultipleVersions.GROUP_USER, TEST_ROW1, ZERO);
        // user2 should be allowed to do the checkAndDelete when delete tries to delete the old version
        // TS=120. user2 having R permission on the latest version(no W permission) cell
        // and W permission on cell version TS=120.
        verifyUserAllowedforCheckAndDelete(user2, TEST_ROW1, TestCellACLWithMultipleVersions.TEST_Q2, ZERO);
        // GROUP_USER should be allowed to do the checkAndDelete when delete tries to delete the old
        // version
        // TS=120. user2 having R permission on the latest version(no W permission) cell
        // and W permission on cell version TS=120.
        verifyUserAllowedforCheckAndDelete(TestCellACLWithMultipleVersions.GROUP_USER, TEST_ROW1, TEST_Q3, ZERO);
    }
}

