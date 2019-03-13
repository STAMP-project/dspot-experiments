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


import AccessControlLists.ACL_LIST_FAMILY;
import AccessControlLists.ACL_TABLE_NAME;
import Action.CREATE;
import Coprocessor.PRIORITY_HIGHEST;
import Permission.Action.ADMIN;
import Permission.Action.EXEC;
import Permission.Action.READ;
import Permission.Action.WRITE;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TestTableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionServerCoprocessorEnvironment;
import org.apache.hadoop.hbase.master.MasterCoprocessorHost;
import org.apache.hadoop.hbase.regionserver.RegionServerCoprocessorHost;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.zookeeper.ZKUtil;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ SecurityTests.class, LargeTests.class })
public class TestAccessController2 extends SecureTestUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAccessController2.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestAccessController2.class);

    private static final byte[] TEST_ROW = Bytes.toBytes("test");

    private static final byte[] TEST_FAMILY = Bytes.toBytes("f");

    private static final byte[] TEST_QUALIFIER = Bytes.toBytes("q");

    private static final byte[] TEST_VALUE = Bytes.toBytes("value");

    private static HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static Configuration conf;

    /**
     * The systemUserConnection created here is tied to the system user. In case, you are planning
     * to create AccessTestAction, DON'T use this systemUserConnection as the 'doAs' user
     * gets  eclipsed by the system user.
     */
    private static Connection systemUserConnection;

    private static final byte[] Q1 = Bytes.toBytes("q1");

    private static final byte[] value1 = Bytes.toBytes("value1");

    private static byte[] TEST_FAMILY_2 = Bytes.toBytes("f2");

    private static byte[] TEST_ROW_2 = Bytes.toBytes("r2");

    private static final byte[] Q2 = Bytes.toBytes("q2");

    private static final byte[] value2 = Bytes.toBytes("value2");

    private static byte[] TEST_ROW_3 = Bytes.toBytes("r3");

    private static final String TESTGROUP_1 = "testgroup_1";

    private static final String TESTGROUP_2 = "testgroup_2";

    private static User TESTGROUP1_USER1;

    private static User TESTGROUP2_USER1;

    @Rule
    public TestTableName TEST_TABLE = new TestTableName();

    private String namespace = "testNamespace";

    private String tname = (namespace) + ":testtable1";

    private TableName tableName = TableName.valueOf(tname);

    private static String TESTGROUP_1_NAME;

    @Test
    public void testCreateWithCorrectOwner() throws Exception {
        // Create a test user
        final User testUser = User.createUserForTesting(TestAccessController2.TEST_UTIL.getConfiguration(), "TestUser", new String[0]);
        // Grant the test user the ability to create tables
        SecureTestUtil.grantGlobal(TestAccessController2.TEST_UTIL, testUser.getShortName(), CREATE);
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                HTableDescriptor desc = new HTableDescriptor(TEST_TABLE.getTableName());
                desc.addFamily(new HColumnDescriptor(TestAccessController2.TEST_FAMILY));
                try (Connection connection = ConnectionFactory.createConnection(TestAccessController2.TEST_UTIL.getConfiguration(), testUser)) {
                    try (Admin admin = connection.getAdmin()) {
                        SecureTestUtil.createTable(TestAccessController2.TEST_UTIL, admin, desc);
                    }
                }
                return null;
            }
        }, testUser);
        TestAccessController2.TEST_UTIL.waitTableAvailable(TEST_TABLE.getTableName());
        // Verify that owner permissions have been granted to the test user on the
        // table just created
        List<UserPermission> perms = AccessControlLists.getTablePermissions(TestAccessController2.conf, TEST_TABLE.getTableName()).get(testUser.getShortName());
        Assert.assertNotNull(perms);
        Assert.assertFalse(perms.isEmpty());
        // Should be RWXCA
        Assert.assertTrue(perms.get(0).getPermission().implies(READ));
        Assert.assertTrue(perms.get(0).getPermission().implies(WRITE));
        Assert.assertTrue(perms.get(0).getPermission().implies(EXEC));
        Assert.assertTrue(perms.get(0).getPermission().implies(Permission.Action.CREATE));
        Assert.assertTrue(perms.get(0).getPermission().implies(ADMIN));
    }

    @Test
    public void testCreateTableWithGroupPermissions() throws Exception {
        SecureTestUtil.grantGlobal(TestAccessController2.TEST_UTIL, TestAccessController2.TESTGROUP_1_NAME, CREATE);
        try {
            SecureTestUtil.AccessTestAction createAction = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    HTableDescriptor desc = new HTableDescriptor(TEST_TABLE.getTableName());
                    desc.addFamily(new HColumnDescriptor(TestAccessController2.TEST_FAMILY));
                    try (Connection connection = ConnectionFactory.createConnection(TestAccessController2.TEST_UTIL.getConfiguration())) {
                        try (Admin admin = connection.getAdmin()) {
                            admin.createTable(desc);
                        }
                    }
                    return null;
                }
            };
            SecureTestUtil.verifyAllowed(createAction, TestAccessController2.TESTGROUP1_USER1);
            SecureTestUtil.verifyDenied(createAction, TestAccessController2.TESTGROUP2_USER1);
        } finally {
            SecureTestUtil.revokeGlobal(TestAccessController2.TEST_UTIL, TestAccessController2.TESTGROUP_1_NAME, CREATE);
        }
    }

    @Test
    public void testACLTableAccess() throws Exception {
        final Configuration conf = TestAccessController2.TEST_UTIL.getConfiguration();
        // Superuser
        User superUser = User.createUserForTesting(conf, "admin", new String[]{ "supergroup" });
        // Global users
        User globalRead = User.createUserForTesting(conf, "globalRead", new String[0]);
        User globalWrite = User.createUserForTesting(conf, "globalWrite", new String[0]);
        User globalCreate = User.createUserForTesting(conf, "globalCreate", new String[0]);
        User globalAdmin = User.createUserForTesting(conf, "globalAdmin", new String[0]);
        SecureTestUtil.grantGlobal(TestAccessController2.TEST_UTIL, globalRead.getShortName(), Action.READ);
        SecureTestUtil.grantGlobal(TestAccessController2.TEST_UTIL, globalWrite.getShortName(), Action.WRITE);
        SecureTestUtil.grantGlobal(TestAccessController2.TEST_UTIL, globalCreate.getShortName(), CREATE);
        SecureTestUtil.grantGlobal(TestAccessController2.TEST_UTIL, globalAdmin.getShortName(), Action.ADMIN);
        // Namespace users
        User nsRead = User.createUserForTesting(conf, "nsRead", new String[0]);
        User nsWrite = User.createUserForTesting(conf, "nsWrite", new String[0]);
        User nsCreate = User.createUserForTesting(conf, "nsCreate", new String[0]);
        User nsAdmin = User.createUserForTesting(conf, "nsAdmin", new String[0]);
        SecureTestUtil.grantOnNamespace(TestAccessController2.TEST_UTIL, nsRead.getShortName(), TEST_TABLE.getTableName().getNamespaceAsString(), Action.READ);
        SecureTestUtil.grantOnNamespace(TestAccessController2.TEST_UTIL, nsWrite.getShortName(), TEST_TABLE.getTableName().getNamespaceAsString(), Action.WRITE);
        SecureTestUtil.grantOnNamespace(TestAccessController2.TEST_UTIL, nsCreate.getShortName(), TEST_TABLE.getTableName().getNamespaceAsString(), CREATE);
        SecureTestUtil.grantOnNamespace(TestAccessController2.TEST_UTIL, nsAdmin.getShortName(), TEST_TABLE.getTableName().getNamespaceAsString(), Action.ADMIN);
        // Table users
        User tableRead = User.createUserForTesting(conf, "tableRead", new String[0]);
        User tableWrite = User.createUserForTesting(conf, "tableWrite", new String[0]);
        User tableCreate = User.createUserForTesting(conf, "tableCreate", new String[0]);
        User tableAdmin = User.createUserForTesting(conf, "tableAdmin", new String[0]);
        SecureTestUtil.grantOnTable(TestAccessController2.TEST_UTIL, tableRead.getShortName(), TEST_TABLE.getTableName(), null, null, Action.READ);
        SecureTestUtil.grantOnTable(TestAccessController2.TEST_UTIL, tableWrite.getShortName(), TEST_TABLE.getTableName(), null, null, Action.WRITE);
        SecureTestUtil.grantOnTable(TestAccessController2.TEST_UTIL, tableCreate.getShortName(), TEST_TABLE.getTableName(), null, null, CREATE);
        SecureTestUtil.grantOnTable(TestAccessController2.TEST_UTIL, tableAdmin.getShortName(), TEST_TABLE.getTableName(), null, null, Action.ADMIN);
        SecureTestUtil.grantGlobal(TestAccessController2.TEST_UTIL, TestAccessController2.TESTGROUP_1_NAME, Action.WRITE);
        try {
            // Write tests
            SecureTestUtil.AccessTestAction writeAction = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    try (Connection conn = ConnectionFactory.createConnection(conf);Table t = conn.getTable(ACL_TABLE_NAME)) {
                        t.put(new Put(TestAccessController2.TEST_ROW).addColumn(ACL_LIST_FAMILY, TestAccessController2.TEST_QUALIFIER, TestAccessController2.TEST_VALUE));
                        return null;
                    } finally {
                    }
                }
            };
            // All writes to ACL table denied except for GLOBAL WRITE permission and superuser
            SecureTestUtil.verifyDenied(writeAction, globalAdmin, globalCreate, globalRead, TestAccessController2.TESTGROUP2_USER1);
            SecureTestUtil.verifyDenied(writeAction, nsAdmin, nsCreate, nsRead, nsWrite);
            SecureTestUtil.verifyDenied(writeAction, tableAdmin, tableCreate, tableRead, tableWrite);
            SecureTestUtil.verifyAllowed(writeAction, superUser, globalWrite, TestAccessController2.TESTGROUP1_USER1);
        } finally {
            SecureTestUtil.revokeGlobal(TestAccessController2.TEST_UTIL, TestAccessController2.TESTGROUP_1_NAME, Action.WRITE);
        }
        SecureTestUtil.grantGlobal(TestAccessController2.TEST_UTIL, TestAccessController2.TESTGROUP_1_NAME, Action.READ);
        try {
            // Read tests
            SecureTestUtil.AccessTestAction scanAction = new SecureTestUtil.AccessTestAction() {
                @Override
                public Object run() throws Exception {
                    try (Connection conn = ConnectionFactory.createConnection(conf);Table t = conn.getTable(ACL_TABLE_NAME)) {
                        ResultScanner s = t.getScanner(new Scan());
                        try {
                            for (Result r = s.next(); r != null; r = s.next()) {
                                // do nothing
                            }
                        } finally {
                            s.close();
                        }
                        return null;
                    }
                }
            };
            // All reads from ACL table denied except for GLOBAL READ and superuser
            SecureTestUtil.verifyDenied(scanAction, globalAdmin, globalCreate, globalWrite, TestAccessController2.TESTGROUP2_USER1);
            SecureTestUtil.verifyDenied(scanAction, nsCreate, nsAdmin, nsRead, nsWrite);
            SecureTestUtil.verifyDenied(scanAction, tableCreate, tableAdmin, tableRead, tableWrite);
            SecureTestUtil.verifyAllowed(scanAction, superUser, globalRead, TestAccessController2.TESTGROUP1_USER1);
        } finally {
            SecureTestUtil.revokeGlobal(TestAccessController2.TEST_UTIL, TestAccessController2.TESTGROUP_1_NAME, Action.READ);
        }
    }

    /* Test table scan operation at table, column family and column qualifier level. */
    @Test
    public void testPostGrantAndRevokeScanAction() throws Exception {
        SecureTestUtil.AccessTestAction scanTableActionForGroupWithTableLevelAccess = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestAccessController2.conf);Table table = connection.getTable(tableName)) {
                    Scan s1 = new Scan();
                    try (ResultScanner scanner1 = table.getScanner(s1)) {
                        Result[] next1 = scanner1.next(5);
                        Assert.assertTrue(("User having table level access should be able to scan all " + "the data in the table."), ((next1.length) == 3));
                    }
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction scanTableActionForGroupWithFamilyLevelAccess = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestAccessController2.conf);Table table = connection.getTable(tableName)) {
                    Scan s1 = new Scan();
                    try (ResultScanner scanner1 = table.getScanner(s1)) {
                        Result[] next1 = scanner1.next(5);
                        Assert.assertTrue(("User having column family level access should be able to scan all " + "the data belonging to that family."), ((next1.length) == 2));
                    }
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction scanFamilyActionForGroupWithFamilyLevelAccess = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestAccessController2.conf);Table table = connection.getTable(tableName)) {
                    Scan s1 = new Scan();
                    s1.addFamily(TestAccessController2.TEST_FAMILY_2);
                    try (ResultScanner scanner1 = table.getScanner(s1)) {
                        scanner1.next();
                    }
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction scanTableActionForGroupWithQualifierLevelAccess = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestAccessController2.conf);Table table = connection.getTable(tableName)) {
                    Scan s1 = new Scan();
                    try (ResultScanner scanner1 = table.getScanner(s1)) {
                        Result[] next1 = scanner1.next(5);
                        Assert.assertTrue(("User having column qualifier level access should be able to scan " + "that column family qualifier data."), ((next1.length) == 1));
                    }
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction scanFamilyActionForGroupWithQualifierLevelAccess = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestAccessController2.conf);Table table = connection.getTable(tableName)) {
                    Scan s1 = new Scan();
                    s1.addFamily(TestAccessController2.TEST_FAMILY_2);
                    try (ResultScanner scanner1 = table.getScanner(s1)) {
                        scanner1.next();
                    }
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction scanQualifierActionForGroupWithQualifierLevelAccess = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestAccessController2.conf);Table table = connection.getTable(tableName)) {
                    Scan s1 = new Scan();
                    s1.addColumn(TestAccessController2.TEST_FAMILY, TestAccessController2.Q2);
                    try (ResultScanner scanner1 = table.getScanner(s1)) {
                        scanner1.next();
                    }
                }
                return null;
            }
        };
        // Verify user from a group which has table level access can read all the data and group which
        // has no access can't read any data.
        SecureTestUtil.grantOnTable(TestAccessController2.TEST_UTIL, TestAccessController2.TESTGROUP_1_NAME, tableName, null, null, Action.READ);
        SecureTestUtil.verifyAllowed(TestAccessController2.TESTGROUP1_USER1, scanTableActionForGroupWithTableLevelAccess);
        SecureTestUtil.verifyDenied(TestAccessController2.TESTGROUP2_USER1, scanTableActionForGroupWithTableLevelAccess);
        // Verify user from a group whose table level access has been revoked can't read any data.
        SecureTestUtil.revokeFromTable(TestAccessController2.TEST_UTIL, TestAccessController2.TESTGROUP_1_NAME, tableName, null, null);
        SecureTestUtil.verifyDenied(TestAccessController2.TESTGROUP1_USER1, scanTableActionForGroupWithTableLevelAccess);
        // Verify user from a group which has column family level access can read all the data
        // belonging to that family and group which has no access can't read any data.
        SecureTestUtil.grantOnTable(TestAccessController2.TEST_UTIL, TestAccessController2.TESTGROUP_1_NAME, tableName, TestAccessController2.TEST_FAMILY, null, READ);
        SecureTestUtil.verifyAllowed(TestAccessController2.TESTGROUP1_USER1, scanTableActionForGroupWithFamilyLevelAccess);
        SecureTestUtil.verifyDenied(TestAccessController2.TESTGROUP1_USER1, scanFamilyActionForGroupWithFamilyLevelAccess);
        SecureTestUtil.verifyDenied(TestAccessController2.TESTGROUP2_USER1, scanTableActionForGroupWithFamilyLevelAccess);
        SecureTestUtil.verifyDenied(TestAccessController2.TESTGROUP2_USER1, scanFamilyActionForGroupWithFamilyLevelAccess);
        // Verify user from a group whose column family level access has been revoked can't read any
        // data from that family.
        SecureTestUtil.revokeFromTable(TestAccessController2.TEST_UTIL, TestAccessController2.TESTGROUP_1_NAME, tableName, TestAccessController2.TEST_FAMILY, null);
        SecureTestUtil.verifyDenied(TestAccessController2.TESTGROUP1_USER1, scanTableActionForGroupWithFamilyLevelAccess);
        // Verify user from a group which has column qualifier level access can read data that has this
        // family and qualifier, and group which has no access can't read any data.
        SecureTestUtil.grantOnTable(TestAccessController2.TEST_UTIL, TestAccessController2.TESTGROUP_1_NAME, tableName, TestAccessController2.TEST_FAMILY, TestAccessController2.Q1, Action.READ);
        SecureTestUtil.verifyAllowed(TestAccessController2.TESTGROUP1_USER1, scanTableActionForGroupWithQualifierLevelAccess);
        SecureTestUtil.verifyDenied(TestAccessController2.TESTGROUP1_USER1, scanFamilyActionForGroupWithQualifierLevelAccess);
        SecureTestUtil.verifyDenied(TestAccessController2.TESTGROUP1_USER1, scanQualifierActionForGroupWithQualifierLevelAccess);
        SecureTestUtil.verifyDenied(TestAccessController2.TESTGROUP2_USER1, scanTableActionForGroupWithQualifierLevelAccess);
        SecureTestUtil.verifyDenied(TestAccessController2.TESTGROUP2_USER1, scanFamilyActionForGroupWithQualifierLevelAccess);
        SecureTestUtil.verifyDenied(TestAccessController2.TESTGROUP2_USER1, scanQualifierActionForGroupWithQualifierLevelAccess);
        // Verify user from a group whose column qualifier level access has been revoked can't read the
        // data having this column family and qualifier.
        SecureTestUtil.revokeFromTable(TestAccessController2.TEST_UTIL, TestAccessController2.TESTGROUP_1_NAME, tableName, TestAccessController2.TEST_FAMILY, TestAccessController2.Q1);
        SecureTestUtil.verifyDenied(TestAccessController2.TESTGROUP1_USER1, scanTableActionForGroupWithQualifierLevelAccess);
    }

    public static class MyAccessController extends AccessController {}

    @Test
    public void testCoprocessorLoading() throws Exception {
        MasterCoprocessorHost cpHost = TestAccessController2.TEST_UTIL.getMiniHBaseCluster().getMaster().getMasterCoprocessorHost();
        cpHost.load(TestAccessController2.MyAccessController.class, PRIORITY_HIGHEST, TestAccessController2.conf);
        AccessController ACCESS_CONTROLLER = cpHost.findCoprocessor(TestAccessController2.MyAccessController.class);
        MasterCoprocessorEnvironment CP_ENV = cpHost.createEnvironment(ACCESS_CONTROLLER, PRIORITY_HIGHEST, 1, TestAccessController2.conf);
        RegionServerCoprocessorHost rsHost = TestAccessController2.TEST_UTIL.getMiniHBaseCluster().getRegionServer(0).getRegionServerCoprocessorHost();
        RegionServerCoprocessorEnvironment RSCP_ENV = rsHost.createEnvironment(ACCESS_CONTROLLER, PRIORITY_HIGHEST, 1, TestAccessController2.conf);
    }

    @Test
    public void testACLZNodeDeletion() throws Exception {
        String baseAclZNode = "/hbase/acl/";
        String ns = "testACLZNodeDeletionNamespace";
        NamespaceDescriptor desc = NamespaceDescriptor.create(ns).build();
        SecureTestUtil.createNamespace(TestAccessController2.TEST_UTIL, desc);
        final TableName table = TableName.valueOf(ns, "testACLZNodeDeletionTable");
        final byte[] family = Bytes.toBytes("f1");
        HTableDescriptor htd = new HTableDescriptor(table);
        htd.addFamily(new HColumnDescriptor(family));
        SecureTestUtil.createTable(TestAccessController2.TEST_UTIL, htd);
        // Namespace needs this, as they follow the lazy creation of ACL znode.
        SecureTestUtil.grantOnNamespace(TestAccessController2.TEST_UTIL, TestAccessController2.TESTGROUP1_USER1.getShortName(), ns, Action.ADMIN);
        ZKWatcher zkw = TestAccessController2.TEST_UTIL.getMiniHBaseCluster().getMaster().getZooKeeper();
        Assert.assertTrue("The acl znode for table should exist", ((ZKUtil.checkExists(zkw, (baseAclZNode + (table.getNameAsString())))) != (-1)));
        Assert.assertTrue("The acl znode for namespace should exist", ((ZKUtil.checkExists(zkw, (baseAclZNode + (SecureTestUtil.convertToNamespace(ns))))) != (-1)));
        SecureTestUtil.revokeFromNamespace(TestAccessController2.TEST_UTIL, TestAccessController2.TESTGROUP1_USER1.getShortName(), ns, Action.ADMIN);
        SecureTestUtil.deleteTable(TestAccessController2.TEST_UTIL, table);
        SecureTestUtil.deleteNamespace(TestAccessController2.TEST_UTIL, ns);
        Assert.assertTrue("The acl znode for table should have been deleted", ((ZKUtil.checkExists(zkw, (baseAclZNode + (table.getNameAsString())))) == (-1)));
        Assert.assertTrue("The acl znode for namespace should have been deleted", ((ZKUtil.checkExists(zkw, (baseAclZNode + (SecureTestUtil.convertToNamespace(ns))))) == (-1)));
    }
}

