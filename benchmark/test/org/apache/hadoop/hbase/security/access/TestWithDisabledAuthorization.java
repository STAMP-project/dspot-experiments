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


import CompareOperator.EQUAL;
import Durability.USE_DEFAULT;
import FlushLifeCycleTracker.DUMMY;
import Permission.Action.ADMIN;
import Permission.Action.CREATE;
import Permission.Action.READ;
import Permission.Action.WRITE;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TestTableName;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.SnapshotDescription;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.ObserverContextImpl;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionServerCoprocessorEnvironment;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.security.access.Permission.Action;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ SecurityTests.class, LargeTests.class })
public class TestWithDisabledAuthorization extends SecureTestUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestWithDisabledAuthorization.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestWithDisabledAuthorization.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] TEST_FAMILY = Bytes.toBytes("f1");

    private static final byte[] TEST_FAMILY2 = Bytes.toBytes("f2");

    private static final byte[] TEST_ROW = Bytes.toBytes("testrow");

    private static final byte[] TEST_Q1 = Bytes.toBytes("q1");

    private static final byte[] TEST_Q2 = Bytes.toBytes("q2");

    private static final byte[] TEST_Q3 = Bytes.toBytes("q3");

    private static final byte[] TEST_Q4 = Bytes.toBytes("q4");

    private static final byte[] ZERO = Bytes.toBytes(0L);

    private static MasterCoprocessorEnvironment CP_ENV;

    private static AccessController ACCESS_CONTROLLER;

    private static RegionServerCoprocessorEnvironment RSCP_ENV;

    private RegionCoprocessorEnvironment RCP_ENV;

    @Rule
    public TestTableName TEST_TABLE = new TestTableName();

    // default users
    // superuser
    private static User SUPERUSER;

    // user granted with all global permission
    private static User USER_ADMIN;

    // user with rw permissions on column family.
    private static User USER_RW;

    // user with read-only permissions
    private static User USER_RO;

    // user is table owner. will have all permissions on table
    private static User USER_OWNER;

    // user with create table permissions alone
    private static User USER_CREATE;

    // user with no permissions
    private static User USER_NONE;

    // user with only partial read-write perms (on family:q1 only)
    private static User USER_QUAL;

    @Test
    public void testCheckPermissions() throws Exception {
        SecureTestUtil.AccessTestAction checkGlobalAdmin = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                SecureTestUtil.checkGlobalPerms(TestWithDisabledAuthorization.TEST_UTIL, ADMIN);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(checkGlobalAdmin, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN);
        SecureTestUtil.verifyDenied(checkGlobalAdmin, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        SecureTestUtil.AccessTestAction checkGlobalRead = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                SecureTestUtil.checkGlobalPerms(TestWithDisabledAuthorization.TEST_UTIL, READ);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(checkGlobalRead, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN);
        SecureTestUtil.verifyDenied(checkGlobalRead, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        SecureTestUtil.AccessTestAction checkGlobalReadWrite = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                SecureTestUtil.checkGlobalPerms(TestWithDisabledAuthorization.TEST_UTIL, READ, WRITE);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(checkGlobalReadWrite, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN);
        SecureTestUtil.verifyDenied(checkGlobalReadWrite, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        SecureTestUtil.AccessTestAction checkTableAdmin = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                SecureTestUtil.checkTablePerms(TestWithDisabledAuthorization.TEST_UTIL, TEST_TABLE.getTableName(), null, null, ADMIN);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(checkTableAdmin, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_OWNER);
        SecureTestUtil.verifyDenied(checkTableAdmin, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        SecureTestUtil.AccessTestAction checkTableCreate = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                SecureTestUtil.checkTablePerms(TestWithDisabledAuthorization.TEST_UTIL, TEST_TABLE.getTableName(), null, null, CREATE);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(checkTableCreate, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE);
        SecureTestUtil.verifyDenied(checkTableCreate, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        SecureTestUtil.AccessTestAction checkTableRead = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                SecureTestUtil.checkTablePerms(TestWithDisabledAuthorization.TEST_UTIL, TEST_TABLE.getTableName(), null, null, READ);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(checkTableRead, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE);
        SecureTestUtil.verifyDenied(checkTableRead, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        SecureTestUtil.AccessTestAction checkTableReadWrite = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                SecureTestUtil.checkTablePerms(TestWithDisabledAuthorization.TEST_UTIL, TEST_TABLE.getTableName(), null, null, READ, WRITE);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(checkTableReadWrite, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE);
        SecureTestUtil.verifyDenied(checkTableReadWrite, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        SecureTestUtil.AccessTestAction checkColumnRead = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                SecureTestUtil.checkTablePerms(TestWithDisabledAuthorization.TEST_UTIL, TEST_TABLE.getTableName(), TestWithDisabledAuthorization.TEST_FAMILY, null, READ);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(checkColumnRead, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO);
        SecureTestUtil.verifyDenied(checkColumnRead, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        SecureTestUtil.AccessTestAction checkColumnReadWrite = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                SecureTestUtil.checkTablePerms(TestWithDisabledAuthorization.TEST_UTIL, TEST_TABLE.getTableName(), TestWithDisabledAuthorization.TEST_FAMILY, null, READ, WRITE);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(checkColumnReadWrite, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_RW);
        SecureTestUtil.verifyDenied(checkColumnReadWrite, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        SecureTestUtil.AccessTestAction checkQualifierRead = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                SecureTestUtil.checkTablePerms(TestWithDisabledAuthorization.TEST_UTIL, TEST_TABLE.getTableName(), TestWithDisabledAuthorization.TEST_FAMILY, TestWithDisabledAuthorization.TEST_Q1, READ);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(checkQualifierRead, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_QUAL);
        SecureTestUtil.verifyDenied(checkQualifierRead, TestWithDisabledAuthorization.USER_NONE);
        SecureTestUtil.AccessTestAction checkQualifierReadWrite = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                SecureTestUtil.checkTablePerms(TestWithDisabledAuthorization.TEST_UTIL, TEST_TABLE.getTableName(), TestWithDisabledAuthorization.TEST_FAMILY, TestWithDisabledAuthorization.TEST_Q1, READ, WRITE);
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(checkQualifierReadWrite, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_QUAL);
        SecureTestUtil.verifyDenied(checkQualifierReadWrite, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_NONE);
        SecureTestUtil.AccessTestAction checkMultiQualifierRead = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                SecureTestUtil.checkTablePerms(TestWithDisabledAuthorization.TEST_UTIL, TEST_TABLE.getTableName(), new Permission[]{ new TablePermission(TEST_TABLE.getTableName(), TestWithDisabledAuthorization.TEST_FAMILY, TestWithDisabledAuthorization.TEST_Q1, Action.READ), new TablePermission(TEST_TABLE.getTableName(), TestWithDisabledAuthorization.TEST_FAMILY, TestWithDisabledAuthorization.TEST_Q2, Action.READ) });
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(checkMultiQualifierRead, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO);
        SecureTestUtil.verifyDenied(checkMultiQualifierRead, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        SecureTestUtil.AccessTestAction checkMultiQualifierReadWrite = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                SecureTestUtil.checkTablePerms(TestWithDisabledAuthorization.TEST_UTIL, TEST_TABLE.getTableName(), new Permission[]{ new TablePermission(TEST_TABLE.getTableName(), TestWithDisabledAuthorization.TEST_FAMILY, TestWithDisabledAuthorization.TEST_Q1, Action.READ, Action.WRITE), new TablePermission(TEST_TABLE.getTableName(), TestWithDisabledAuthorization.TEST_FAMILY, TestWithDisabledAuthorization.TEST_Q2, Action.READ, Action.WRITE) });
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(checkMultiQualifierReadWrite, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_RW);
        SecureTestUtil.verifyDenied(checkMultiQualifierReadWrite, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
    }

    /**
     * Test grants and revocations with authorization disabled
     */
    @Test
    public void testPassiveGrantRevoke() throws Exception {
        // Add a test user
        User tblUser = User.createUserForTesting(TestWithDisabledAuthorization.TEST_UTIL.getConfiguration(), "tbluser", new String[0]);
        // If we check now, the test user won't have permissions
        SecureTestUtil.AccessTestAction checkTableRead = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                SecureTestUtil.checkTablePerms(TestWithDisabledAuthorization.TEST_UTIL, TEST_TABLE.getTableName(), TestWithDisabledAuthorization.TEST_FAMILY, null, READ);
                return null;
            }
        };
        SecureTestUtil.verifyDenied(tblUser, checkTableRead);
        // An actual read won't be denied
        SecureTestUtil.AccessTestAction tableRead = new SecureTestUtil.AccessTestAction() {
            @Override
            public Void run() throws Exception {
                try (Connection conn = ConnectionFactory.createConnection(TestWithDisabledAuthorization.TEST_UTIL.getConfiguration());Table t = conn.getTable(TEST_TABLE.getTableName())) {
                    t.get(new Get(TestWithDisabledAuthorization.TEST_ROW).addFamily(TestWithDisabledAuthorization.TEST_FAMILY));
                }
                return null;
            }
        };
        SecureTestUtil.verifyAllowed(tblUser, tableRead);
        // Grant read perms to the test user
        SecureTestUtil.grantOnTable(TestWithDisabledAuthorization.TEST_UTIL, tblUser.getShortName(), TEST_TABLE.getTableName(), TestWithDisabledAuthorization.TEST_FAMILY, null, READ);
        // Now both the permission check and actual op will succeed
        SecureTestUtil.verifyAllowed(tblUser, checkTableRead);
        SecureTestUtil.verifyAllowed(tblUser, tableRead);
        // Revoke read perms from the test user
        SecureTestUtil.revokeFromTable(TestWithDisabledAuthorization.TEST_UTIL, tblUser.getShortName(), TEST_TABLE.getTableName(), TestWithDisabledAuthorization.TEST_FAMILY, null, READ);
        // Now the permission check will indicate revocation but the actual op will still succeed
        SecureTestUtil.verifyDenied(tblUser, checkTableRead);
        SecureTestUtil.verifyAllowed(tblUser, tableRead);
    }

    /**
     * Test master observer
     */
    @Test
    public void testPassiveMasterOperations() throws Exception {
        // preCreateTable
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                HTableDescriptor htd = new HTableDescriptor(TEST_TABLE.getTableName());
                htd.addFamily(new HColumnDescriptor(TestWithDisabledAuthorization.TEST_FAMILY));
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preCreateTable(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), htd, null);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preModifyTable
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                HTableDescriptor htd = new HTableDescriptor(TEST_TABLE.getTableName());
                htd.addFamily(new HColumnDescriptor(TestWithDisabledAuthorization.TEST_FAMILY));
                htd.addFamily(new HColumnDescriptor(TestWithDisabledAuthorization.TEST_FAMILY2));
                // not needed by AccessController
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preModifyTable(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), TEST_TABLE.getTableName(), null, htd);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preDeleteTable
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preDeleteTable(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), TEST_TABLE.getTableName());
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preTruncateTable
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preTruncateTable(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), TEST_TABLE.getTableName());
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preEnableTable
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preEnableTable(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), TEST_TABLE.getTableName());
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preDisableTable
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preDisableTable(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), TEST_TABLE.getTableName());
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preMove
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                HRegionInfo region = new HRegionInfo(TEST_TABLE.getTableName());
                ServerName srcServer = ServerName.valueOf("1.1.1.1", 1, 0);
                ServerName destServer = ServerName.valueOf("2.2.2.2", 2, 0);
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preMove(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), region, srcServer, destServer);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preAssign
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                HRegionInfo region = new HRegionInfo(TEST_TABLE.getTableName());
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preAssign(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), region);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preUnassign
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                HRegionInfo region = new HRegionInfo(TEST_TABLE.getTableName());
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preUnassign(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), region, true);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preBalance
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preBalance(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV));
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preBalanceSwitch
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preBalanceSwitch(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), true);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preSnapshot
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                SnapshotDescription snapshot = new SnapshotDescription("foo");
                HTableDescriptor htd = new HTableDescriptor(TEST_TABLE.getTableName());
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preSnapshot(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), snapshot, htd);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preListSnapshot
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                SnapshotDescription snapshot = new SnapshotDescription("foo");
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preListSnapshot(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), snapshot);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preCloneSnapshot
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                SnapshotDescription snapshot = new SnapshotDescription("foo");
                HTableDescriptor htd = new HTableDescriptor(TEST_TABLE.getTableName());
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preCloneSnapshot(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), snapshot, htd);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preRestoreSnapshot
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                SnapshotDescription snapshot = new SnapshotDescription("foo");
                HTableDescriptor htd = new HTableDescriptor(TEST_TABLE.getTableName());
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preRestoreSnapshot(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), snapshot, htd);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preDeleteSnapshot
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                SnapshotDescription snapshot = new SnapshotDescription("foo");
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preDeleteSnapshot(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), snapshot);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preGetTableDescriptors
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                List<TableName> tableNamesList = Lists.newArrayList();
                tableNamesList.add(TEST_TABLE.getTableName());
                List<TableDescriptor> descriptors = Lists.newArrayList();
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preGetTableDescriptors(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), tableNamesList, descriptors, ".+");
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preGetTableNames
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                List<TableDescriptor> descriptors = Lists.newArrayList();
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preGetTableNames(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), descriptors, ".+");
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preCreateNamespace
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                NamespaceDescriptor ns = NamespaceDescriptor.create("test").build();
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preCreateNamespace(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), ns);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preDeleteNamespace
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preDeleteNamespace(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), "test");
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preModifyNamespace
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                NamespaceDescriptor ns = NamespaceDescriptor.create("test").build();
                // not needed by AccessController
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preModifyNamespace(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), null, ns);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preGetNamespaceDescriptor
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preGetNamespaceDescriptor(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), "test");
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preListNamespaceDescriptors
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                List<NamespaceDescriptor> descriptors = Lists.newArrayList();
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preListNamespaceDescriptors(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), descriptors);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preSplit
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preSplitRegion(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), TEST_TABLE.getTableName(), Bytes.toBytes("ss"));
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preSetUserQuota
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preSetUserQuota(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), "testuser", null);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preSetTableQuota
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preSetTableQuota(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), TEST_TABLE.getTableName(), null);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preSetNamespaceQuota
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preSetNamespaceQuota(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.CP_ENV), "test", null);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
    }

    /**
     * Test region server observer
     */
    @Test
    public void testPassiveRegionServerOperations() throws Exception {
        // preStopRegionServer
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preStopRegionServer(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.RSCP_ENV));
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preRollWALWriterRequest
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preRollWALWriterRequest(ObserverContextImpl.createAndPrepare(TestWithDisabledAuthorization.RSCP_ENV));
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
    }

    /**
     * Test region observer
     */
    @Test
    public void testPassiveRegionOperations() throws Exception {
        // preOpen
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preOpen(ObserverContextImpl.createAndPrepare(RCP_ENV));
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preFlush
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preFlush(ObserverContextImpl.createAndPrepare(RCP_ENV), DUMMY);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preGetOp
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                List<Cell> cells = Lists.newArrayList();
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preGetOp(ObserverContextImpl.createAndPrepare(RCP_ENV), new Get(TestWithDisabledAuthorization.TEST_ROW), cells);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preExists
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preExists(ObserverContextImpl.createAndPrepare(RCP_ENV), new Get(TestWithDisabledAuthorization.TEST_ROW), true);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // prePut
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.prePut(ObserverContextImpl.createAndPrepare(RCP_ENV), new Put(TestWithDisabledAuthorization.TEST_ROW), new WALEdit(), USE_DEFAULT);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preDelete
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preDelete(ObserverContextImpl.createAndPrepare(RCP_ENV), new Delete(TestWithDisabledAuthorization.TEST_ROW), new WALEdit(), USE_DEFAULT);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preBatchMutate
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preBatchMutate(ObserverContextImpl.createAndPrepare(RCP_ENV), new org.apache.hadoop.hbase.regionserver.MiniBatchOperationInProgress(null, null, null, 0, 0, 0));
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preCheckAndPut
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preCheckAndPut(ObserverContextImpl.createAndPrepare(RCP_ENV), TestWithDisabledAuthorization.TEST_ROW, TestWithDisabledAuthorization.TEST_FAMILY, TestWithDisabledAuthorization.TEST_Q1, EQUAL, new org.apache.hadoop.hbase.filter.BinaryComparator(Bytes.toBytes("foo")), new Put(TestWithDisabledAuthorization.TEST_ROW), true);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preCheckAndDelete
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preCheckAndDelete(ObserverContextImpl.createAndPrepare(RCP_ENV), TestWithDisabledAuthorization.TEST_ROW, TestWithDisabledAuthorization.TEST_FAMILY, TestWithDisabledAuthorization.TEST_Q1, EQUAL, new org.apache.hadoop.hbase.filter.BinaryComparator(Bytes.toBytes("foo")), new Delete(TestWithDisabledAuthorization.TEST_ROW), true);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preAppend
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preAppend(ObserverContextImpl.createAndPrepare(RCP_ENV), new Append(TestWithDisabledAuthorization.TEST_ROW));
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preIncrement
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preIncrement(ObserverContextImpl.createAndPrepare(RCP_ENV), new Increment(TestWithDisabledAuthorization.TEST_ROW));
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preScannerOpen
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preScannerOpen(ObserverContextImpl.createAndPrepare(RCP_ENV), new Scan());
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
        // preBulkLoadHFile
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                List<Pair<byte[], String>> paths = Lists.newArrayList();
                TestWithDisabledAuthorization.ACCESS_CONTROLLER.preBulkLoadHFile(ObserverContextImpl.createAndPrepare(RCP_ENV), paths);
                return null;
            }
        }, TestWithDisabledAuthorization.SUPERUSER, TestWithDisabledAuthorization.USER_ADMIN, TestWithDisabledAuthorization.USER_RW, TestWithDisabledAuthorization.USER_RO, TestWithDisabledAuthorization.USER_OWNER, TestWithDisabledAuthorization.USER_CREATE, TestWithDisabledAuthorization.USER_QUAL, TestWithDisabledAuthorization.USER_NONE);
    }

    @Test
    public void testPassiveCellPermissions() throws Exception {
        final Configuration conf = TestWithDisabledAuthorization.TEST_UTIL.getConfiguration();
        // store two sets of values, one store with a cell level ACL, and one without
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    Put p;
                    // with ro ACL
                    p = new Put(TestWithDisabledAuthorization.TEST_ROW).addColumn(TestWithDisabledAuthorization.TEST_FAMILY, TestWithDisabledAuthorization.TEST_Q1, TestWithDisabledAuthorization.ZERO);
                    p.setACL(TestWithDisabledAuthorization.USER_NONE.getShortName(), new Permission(Action.READ));
                    t.put(p);
                    // with rw ACL
                    p = new Put(TestWithDisabledAuthorization.TEST_ROW).addColumn(TestWithDisabledAuthorization.TEST_FAMILY, TestWithDisabledAuthorization.TEST_Q2, TestWithDisabledAuthorization.ZERO);
                    p.setACL(TestWithDisabledAuthorization.USER_NONE.getShortName(), new Permission(Action.READ, Action.WRITE));
                    t.put(p);
                    // no ACL
                    p = new Put(TestWithDisabledAuthorization.TEST_ROW).addColumn(TestWithDisabledAuthorization.TEST_FAMILY, TestWithDisabledAuthorization.TEST_Q3, TestWithDisabledAuthorization.ZERO).addColumn(TestWithDisabledAuthorization.TEST_FAMILY, TestWithDisabledAuthorization.TEST_Q4, TestWithDisabledAuthorization.ZERO);
                    t.put(p);
                }
                return null;
            }
        }, TestWithDisabledAuthorization.USER_OWNER);
        // check that a scan over the test data returns the expected number of KVs
        final List<Cell> scanResults = Lists.newArrayList();
        SecureTestUtil.AccessTestAction scanAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public List<Cell> run() throws Exception {
                Scan scan = new Scan();
                scan.setStartRow(TestWithDisabledAuthorization.TEST_ROW);
                scan.setStopRow(Bytes.add(TestWithDisabledAuthorization.TEST_ROW, new byte[]{ 0 }));
                scan.addFamily(TestWithDisabledAuthorization.TEST_FAMILY);
                Connection connection = ConnectionFactory.createConnection(conf);
                Table t = connection.getTable(TEST_TABLE.getTableName());
                try {
                    ResultScanner scanner = t.getScanner(scan);
                    Result result = null;
                    do {
                        result = scanner.next();
                        if (result != null) {
                            scanResults.addAll(result.listCells());
                        }
                    } while (result != null );
                } finally {
                    t.close();
                    connection.close();
                }
                return scanResults;
            }
        };
        // owner will see all values
        scanResults.clear();
        SecureTestUtil.verifyAllowed(scanAction, TestWithDisabledAuthorization.USER_OWNER);
        Assert.assertEquals(4, scanResults.size());
        // other user will also see 4 values
        // if cell filtering was active, we would only see 2 values
        scanResults.clear();
        SecureTestUtil.verifyAllowed(scanAction, TestWithDisabledAuthorization.USER_NONE);
        Assert.assertEquals(4, scanResults.size());
    }
}

