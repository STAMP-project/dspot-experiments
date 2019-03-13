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
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.AuthUtil;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TestTableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.SecurityTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ SecurityTests.class, LargeTests.class })
public class TestCellACLs extends SecureTestUtil {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCellACLs.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCellACLs.class);

    @Rule
    public TestTableName TEST_TABLE = new TestTableName();

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] TEST_FAMILY = Bytes.toBytes("f1");

    private static final byte[] TEST_ROW = Bytes.toBytes("cellpermtest");

    private static final byte[] TEST_Q1 = Bytes.toBytes("q1");

    private static final byte[] TEST_Q2 = Bytes.toBytes("q2");

    private static final byte[] TEST_Q3 = Bytes.toBytes("q3");

    private static final byte[] TEST_Q4 = Bytes.toBytes("q4");

    private static final byte[] ZERO = Bytes.toBytes(0L);

    private static final byte[] ONE = Bytes.toBytes(1L);

    private static Configuration conf;

    private static final String GROUP = "group";

    private static User GROUP_USER;

    private static User USER_OWNER;

    private static User USER_OTHER;

    private static String[] usersAndGroups;

    @Test
    public void testCellPermissions() throws Exception {
        // store two sets of values, one store with a cell level ACL, and one without
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLs.conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    Put p;
                    // with ro ACL
                    p = new Put(TestCellACLs.TEST_ROW).addColumn(TestCellACLs.TEST_FAMILY, TestCellACLs.TEST_Q1, TestCellACLs.ZERO);
                    p.setACL(prepareCellPermissions(TestCellACLs.usersAndGroups, READ));
                    t.put(p);
                    // with rw ACL
                    p = new Put(TestCellACLs.TEST_ROW).addColumn(TestCellACLs.TEST_FAMILY, TestCellACLs.TEST_Q2, TestCellACLs.ZERO);
                    p.setACL(prepareCellPermissions(TestCellACLs.usersAndGroups, READ, WRITE));
                    t.put(p);
                    // no ACL
                    p = new Put(TestCellACLs.TEST_ROW).addColumn(TestCellACLs.TEST_FAMILY, TestCellACLs.TEST_Q3, TestCellACLs.ZERO).addColumn(TestCellACLs.TEST_FAMILY, TestCellACLs.TEST_Q4, TestCellACLs.ZERO);
                    t.put(p);
                }
                return null;
            }
        }, TestCellACLs.USER_OWNER);
        /* ---- Gets ---- */
        SecureTestUtil.AccessTestAction getQ1 = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Get get = new Get(TestCellACLs.TEST_ROW).addColumn(TestCellACLs.TEST_FAMILY, TestCellACLs.TEST_Q1);
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLs.conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    return t.get(get).listCells();
                }
            }
        };
        SecureTestUtil.AccessTestAction getQ2 = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Get get = new Get(TestCellACLs.TEST_ROW).addColumn(TestCellACLs.TEST_FAMILY, TestCellACLs.TEST_Q2);
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLs.conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    return t.get(get).listCells();
                }
            }
        };
        SecureTestUtil.AccessTestAction getQ3 = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Get get = new Get(TestCellACLs.TEST_ROW).addColumn(TestCellACLs.TEST_FAMILY, TestCellACLs.TEST_Q3);
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLs.conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    return t.get(get).listCells();
                }
            }
        };
        SecureTestUtil.AccessTestAction getQ4 = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Get get = new Get(TestCellACLs.TEST_ROW).addColumn(TestCellACLs.TEST_FAMILY, TestCellACLs.TEST_Q4);
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLs.conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    return t.get(get).listCells();
                }
            }
        };
        // Confirm special read access set at cell level
        SecureTestUtil.verifyAllowed(getQ1, TestCellACLs.USER_OTHER, TestCellACLs.GROUP_USER);
        SecureTestUtil.verifyAllowed(getQ2, TestCellACLs.USER_OTHER, TestCellACLs.GROUP_USER);
        // Confirm this access does not extend to other cells
        SecureTestUtil.verifyIfNull(getQ3, TestCellACLs.USER_OTHER, TestCellACLs.GROUP_USER);
        SecureTestUtil.verifyIfNull(getQ4, TestCellACLs.USER_OTHER, TestCellACLs.GROUP_USER);
        /* ---- Scans ---- */
        // check that a scan over the test data returns the expected number of KVs
        final List<Cell> scanResults = Lists.newArrayList();
        SecureTestUtil.AccessTestAction scanAction = new SecureTestUtil.AccessTestAction() {
            @Override
            public List<Cell> run() throws Exception {
                Scan scan = new Scan();
                scan.setStartRow(TestCellACLs.TEST_ROW);
                scan.setStopRow(Bytes.add(TestCellACLs.TEST_ROW, new byte[]{ 0 }));
                scan.addFamily(TestCellACLs.TEST_FAMILY);
                Connection connection = ConnectionFactory.createConnection(TestCellACLs.conf);
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
        SecureTestUtil.verifyAllowed(scanAction, TestCellACLs.USER_OWNER);
        Assert.assertEquals(4, scanResults.size());
        // other user will see 2 values
        scanResults.clear();
        SecureTestUtil.verifyAllowed(scanAction, TestCellACLs.USER_OTHER);
        Assert.assertEquals(2, scanResults.size());
        scanResults.clear();
        SecureTestUtil.verifyAllowed(scanAction, TestCellACLs.GROUP_USER);
        Assert.assertEquals(2, scanResults.size());
        /* ---- Increments ---- */
        SecureTestUtil.AccessTestAction incrementQ1 = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Increment i = new Increment(TestCellACLs.TEST_ROW).addColumn(TestCellACLs.TEST_FAMILY, TestCellACLs.TEST_Q1, 1L);
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLs.conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    t.increment(i);
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction incrementQ2 = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Increment i = new Increment(TestCellACLs.TEST_ROW).addColumn(TestCellACLs.TEST_FAMILY, TestCellACLs.TEST_Q2, 1L);
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLs.conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    t.increment(i);
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction incrementQ2newDenyACL = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Increment i = new Increment(TestCellACLs.TEST_ROW).addColumn(TestCellACLs.TEST_FAMILY, TestCellACLs.TEST_Q2, 1L);
                // Tag this increment with an ACL that denies write permissions to USER_OTHER and GROUP
                i.setACL(prepareCellPermissions(TestCellACLs.usersAndGroups, READ));
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLs.conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    t.increment(i);
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction incrementQ3 = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Increment i = new Increment(TestCellACLs.TEST_ROW).addColumn(TestCellACLs.TEST_FAMILY, TestCellACLs.TEST_Q3, 1L);
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLs.conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    t.increment(i);
                }
                return null;
            }
        };
        SecureTestUtil.verifyDenied(incrementQ1, TestCellACLs.USER_OTHER, TestCellACLs.GROUP_USER);
        SecureTestUtil.verifyDenied(incrementQ3, TestCellACLs.USER_OTHER, TestCellACLs.GROUP_USER);
        // We should be able to increment until the permissions are revoked (including the action in
        // which permissions are revoked, the previous ACL will be carried forward)
        SecureTestUtil.verifyAllowed(incrementQ2, TestCellACLs.USER_OTHER, TestCellACLs.GROUP_USER);
        SecureTestUtil.verifyAllowed(incrementQ2newDenyACL, TestCellACLs.USER_OTHER);
        // But not again after we denied ourselves write permission with an ACL
        // update
        SecureTestUtil.verifyDenied(incrementQ2, TestCellACLs.USER_OTHER, TestCellACLs.GROUP_USER);
        /* ---- Deletes ---- */
        SecureTestUtil.AccessTestAction deleteFamily = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Delete delete = new Delete(TestCellACLs.TEST_ROW).addFamily(TestCellACLs.TEST_FAMILY);
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLs.conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    t.delete(delete);
                }
                return null;
            }
        };
        SecureTestUtil.AccessTestAction deleteQ1 = new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                Delete delete = new Delete(TestCellACLs.TEST_ROW).addColumn(TestCellACLs.TEST_FAMILY, TestCellACLs.TEST_Q1);
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLs.conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    t.delete(delete);
                }
                return null;
            }
        };
        SecureTestUtil.verifyDenied(deleteFamily, TestCellACLs.USER_OTHER, TestCellACLs.GROUP_USER);
        SecureTestUtil.verifyDenied(deleteQ1, TestCellACLs.USER_OTHER, TestCellACLs.GROUP_USER);
        SecureTestUtil.verifyAllowed(deleteQ1, TestCellACLs.USER_OWNER);
    }

    /**
     * Insure we are not granting access in the absence of any cells found
     * when scanning for covered cells.
     */
    @Test
    public void testCoveringCheck() throws Exception {
        // Grant read access to USER_OTHER
        SecureTestUtil.grantOnTable(TestCellACLs.TEST_UTIL, TestCellACLs.USER_OTHER.getShortName(), TEST_TABLE.getTableName(), TestCellACLs.TEST_FAMILY, null, READ);
        // Grant read access to GROUP
        SecureTestUtil.grantOnTable(TestCellACLs.TEST_UTIL, AuthUtil.toGroupEntry(TestCellACLs.GROUP), TEST_TABLE.getTableName(), TestCellACLs.TEST_FAMILY, null, READ);
        // A write by USER_OTHER should be denied.
        // This is where we could have a big problem if there is an error in the
        // covering check logic.
        verifyUserDeniedForWrite(TestCellACLs.USER_OTHER, TestCellACLs.ZERO);
        // A write by GROUP_USER from group GROUP should be denied.
        verifyUserDeniedForWrite(TestCellACLs.GROUP_USER, TestCellACLs.ZERO);
        // Add the cell
        SecureTestUtil.verifyAllowed(new SecureTestUtil.AccessTestAction() {
            @Override
            public Object run() throws Exception {
                try (Connection connection = ConnectionFactory.createConnection(TestCellACLs.conf);Table t = connection.getTable(TEST_TABLE.getTableName())) {
                    Put p;
                    p = new Put(TestCellACLs.TEST_ROW).addColumn(TestCellACLs.TEST_FAMILY, TestCellACLs.TEST_Q1, TestCellACLs.ZERO);
                    t.put(p);
                }
                return null;
            }
        }, TestCellACLs.USER_OWNER);
        // A write by USER_OTHER should still be denied, just to make sure
        verifyUserDeniedForWrite(TestCellACLs.USER_OTHER, TestCellACLs.ONE);
        // A write by GROUP_USER from group GROUP should still be denied
        verifyUserDeniedForWrite(TestCellACLs.GROUP_USER, TestCellACLs.ONE);
        // A read by USER_OTHER should be allowed, just to make sure
        verifyUserAllowedForRead(TestCellACLs.USER_OTHER);
        // A read by GROUP_USER from group GROUP should be allowed
        verifyUserAllowedForRead(TestCellACLs.GROUP_USER);
    }
}

