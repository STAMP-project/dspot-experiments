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
package org.apache.hadoop.hbase.client;


import Permission.Action.READ;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.security.access.SecureTestUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;


public abstract class SnapshotWithAclTestBase extends SecureTestUtil {
    private TableName TEST_TABLE = TableName.valueOf(getRandomUUID().toString());

    private static final int ROW_COUNT = 30000;

    private static byte[] TEST_FAMILY = Bytes.toBytes("f1");

    private static byte[] TEST_QUALIFIER = Bytes.toBytes("cq");

    private static byte[] TEST_ROW = Bytes.toBytes(0);

    protected static HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    // user is table owner. will have all permissions on table
    private static User USER_OWNER;

    // user with rw permissions on column family.
    private static User USER_RW;

    // user with read-only permissions
    private static User USER_RO;

    // user with none permissions
    private static User USER_NONE;

    static class AccessReadAction implements SecureTestUtil.AccessTestAction {
        private TableName tableName;

        public AccessReadAction(TableName tableName) {
            this.tableName = tableName;
        }

        @Override
        public Object run() throws Exception {
            Get g = new Get(SnapshotWithAclTestBase.TEST_ROW);
            g.addFamily(SnapshotWithAclTestBase.TEST_FAMILY);
            try (Connection conn = ConnectionFactory.createConnection(SnapshotWithAclTestBase.TEST_UTIL.getConfiguration());Table t = conn.getTable(tableName)) {
                t.get(g);
            }
            return null;
        }
    }

    static class AccessWriteAction implements SecureTestUtil.AccessTestAction {
        private TableName tableName;

        public AccessWriteAction(TableName tableName) {
            this.tableName = tableName;
        }

        @Override
        public Object run() throws Exception {
            Put p = new Put(SnapshotWithAclTestBase.TEST_ROW);
            p.addColumn(SnapshotWithAclTestBase.TEST_FAMILY, SnapshotWithAclTestBase.TEST_QUALIFIER, Bytes.toBytes(0));
            try (Connection conn = ConnectionFactory.createConnection(SnapshotWithAclTestBase.TEST_UTIL.getConfiguration());Table t = conn.getTable(tableName)) {
                t.put(p);
            }
            return null;
        }
    }

    @Test
    public void testRestoreSnapshot() throws Exception {
        SecureTestUtil.verifyAllowed(new SnapshotWithAclTestBase.AccessReadAction(TEST_TABLE), SnapshotWithAclTestBase.USER_OWNER, SnapshotWithAclTestBase.USER_RO, SnapshotWithAclTestBase.USER_RW);
        SecureTestUtil.verifyDenied(new SnapshotWithAclTestBase.AccessReadAction(TEST_TABLE), SnapshotWithAclTestBase.USER_NONE);
        SecureTestUtil.verifyAllowed(new SnapshotWithAclTestBase.AccessWriteAction(TEST_TABLE), SnapshotWithAclTestBase.USER_OWNER, SnapshotWithAclTestBase.USER_RW);
        SecureTestUtil.verifyDenied(new SnapshotWithAclTestBase.AccessWriteAction(TEST_TABLE), SnapshotWithAclTestBase.USER_RO, SnapshotWithAclTestBase.USER_NONE);
        loadData();
        verifyRows(TEST_TABLE);
        String snapshotName1 = getRandomUUID().toString();
        snapshot(snapshotName1, TEST_TABLE);
        // clone snapshot with restoreAcl true.
        TableName tableName1 = TableName.valueOf(getRandomUUID().toString());
        cloneSnapshot(snapshotName1, tableName1, true);
        verifyRows(tableName1);
        SecureTestUtil.verifyAllowed(new SnapshotWithAclTestBase.AccessReadAction(tableName1), SnapshotWithAclTestBase.USER_OWNER, SnapshotWithAclTestBase.USER_RO, SnapshotWithAclTestBase.USER_RW);
        SecureTestUtil.verifyDenied(new SnapshotWithAclTestBase.AccessReadAction(tableName1), SnapshotWithAclTestBase.USER_NONE);
        SecureTestUtil.verifyAllowed(new SnapshotWithAclTestBase.AccessWriteAction(tableName1), SnapshotWithAclTestBase.USER_OWNER, SnapshotWithAclTestBase.USER_RW);
        SecureTestUtil.verifyDenied(new SnapshotWithAclTestBase.AccessWriteAction(tableName1), SnapshotWithAclTestBase.USER_RO, SnapshotWithAclTestBase.USER_NONE);
        // clone snapshot with restoreAcl false.
        TableName tableName2 = TableName.valueOf(getRandomUUID().toString());
        cloneSnapshot(snapshotName1, tableName2, false);
        verifyRows(tableName2);
        SecureTestUtil.verifyAllowed(new SnapshotWithAclTestBase.AccessReadAction(tableName2), SnapshotWithAclTestBase.USER_OWNER);
        SecureTestUtil.verifyDenied(new SnapshotWithAclTestBase.AccessReadAction(tableName2), SnapshotWithAclTestBase.USER_NONE, SnapshotWithAclTestBase.USER_RO, SnapshotWithAclTestBase.USER_RW);
        SecureTestUtil.verifyAllowed(new SnapshotWithAclTestBase.AccessWriteAction(tableName2), SnapshotWithAclTestBase.USER_OWNER);
        SecureTestUtil.verifyDenied(new SnapshotWithAclTestBase.AccessWriteAction(tableName2), SnapshotWithAclTestBase.USER_RO, SnapshotWithAclTestBase.USER_RW, SnapshotWithAclTestBase.USER_NONE);
        // remove read permission for USER_RO.
        SecureTestUtil.revokeFromTable(SnapshotWithAclTestBase.TEST_UTIL, SnapshotWithAclTestBase.USER_RO.getShortName(), TEST_TABLE, SnapshotWithAclTestBase.TEST_FAMILY, null, READ);
        SecureTestUtil.verifyAllowed(new SnapshotWithAclTestBase.AccessReadAction(TEST_TABLE), SnapshotWithAclTestBase.USER_OWNER, SnapshotWithAclTestBase.USER_RW);
        SecureTestUtil.verifyDenied(new SnapshotWithAclTestBase.AccessReadAction(TEST_TABLE), SnapshotWithAclTestBase.USER_RO, SnapshotWithAclTestBase.USER_NONE);
        SecureTestUtil.verifyAllowed(new SnapshotWithAclTestBase.AccessWriteAction(TEST_TABLE), SnapshotWithAclTestBase.USER_OWNER, SnapshotWithAclTestBase.USER_RW);
        SecureTestUtil.verifyDenied(new SnapshotWithAclTestBase.AccessWriteAction(TEST_TABLE), SnapshotWithAclTestBase.USER_RO, SnapshotWithAclTestBase.USER_NONE);
        // restore snapshot with restoreAcl false.
        SnapshotWithAclTestBase.TEST_UTIL.getAdmin().disableTable(TEST_TABLE);
        restoreSnapshot(snapshotName1, false);
        SnapshotWithAclTestBase.TEST_UTIL.getAdmin().enableTable(TEST_TABLE);
        SecureTestUtil.verifyAllowed(new SnapshotWithAclTestBase.AccessReadAction(TEST_TABLE), SnapshotWithAclTestBase.USER_OWNER, SnapshotWithAclTestBase.USER_RW);
        SecureTestUtil.verifyDenied(new SnapshotWithAclTestBase.AccessReadAction(TEST_TABLE), SnapshotWithAclTestBase.USER_RO, SnapshotWithAclTestBase.USER_NONE);
        SecureTestUtil.verifyAllowed(new SnapshotWithAclTestBase.AccessWriteAction(TEST_TABLE), SnapshotWithAclTestBase.USER_OWNER, SnapshotWithAclTestBase.USER_RW);
        SecureTestUtil.verifyDenied(new SnapshotWithAclTestBase.AccessWriteAction(TEST_TABLE), SnapshotWithAclTestBase.USER_RO, SnapshotWithAclTestBase.USER_NONE);
        // restore snapshot with restoreAcl true.
        SnapshotWithAclTestBase.TEST_UTIL.getAdmin().disableTable(TEST_TABLE);
        restoreSnapshot(snapshotName1, true);
        SnapshotWithAclTestBase.TEST_UTIL.getAdmin().enableTable(TEST_TABLE);
        SecureTestUtil.verifyAllowed(new SnapshotWithAclTestBase.AccessReadAction(TEST_TABLE), SnapshotWithAclTestBase.USER_OWNER, SnapshotWithAclTestBase.USER_RO, SnapshotWithAclTestBase.USER_RW);
        SecureTestUtil.verifyDenied(new SnapshotWithAclTestBase.AccessReadAction(TEST_TABLE), SnapshotWithAclTestBase.USER_NONE);
        SecureTestUtil.verifyAllowed(new SnapshotWithAclTestBase.AccessWriteAction(TEST_TABLE), SnapshotWithAclTestBase.USER_OWNER, SnapshotWithAclTestBase.USER_RW);
        SecureTestUtil.verifyDenied(new SnapshotWithAclTestBase.AccessWriteAction(TEST_TABLE), SnapshotWithAclTestBase.USER_RO, SnapshotWithAclTestBase.USER_NONE);
    }
}

