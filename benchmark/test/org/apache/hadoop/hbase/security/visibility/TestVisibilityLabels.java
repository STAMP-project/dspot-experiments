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
package org.apache.hadoop.hbase.security.visibility;


import BloomType.ROWCOL;
import HConstants.LATEST_TIMESTAMP;
import SecurityCapability.CELL_VISIBILITY;
import VisibilityConstants.LABELS_TABLE_FAMILY;
import com.google.protobuf.ByteString;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RowMutations;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.security.SecurityCapability;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos.RegionActionResult;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.GetAuthsResponse;
import org.apache.hadoop.hbase.protobuf.generated.VisibilityLabelsProtos.VisibilityLabelsResponse;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HStore;
import org.apache.hadoop.hbase.regionserver.HStoreFile;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import static VisibilityConstants.LABELS_TABLE_FAMILY;
import static VisibilityUtils.SYSTEM_LABEL;


/**
 * Base test class for visibility labels basic features
 */
public abstract class TestVisibilityLabels {
    public static final String TOPSECRET = "topsecret";

    public static final String PUBLIC = "public";

    public static final String PRIVATE = "private";

    public static final String CONFIDENTIAL = "confidential";

    public static final String SECRET = "secret";

    public static final String COPYRIGHT = "\u00a9ABC";

    public static final String ACCENT = "\u0941";

    public static final String UNICODE_VIS_TAG = ((((((TestVisibilityLabels.COPYRIGHT) + "\"") + (TestVisibilityLabels.ACCENT)) + "\\") + (TestVisibilityLabels.SECRET)) + "\"") + "\'&\\";

    public static final String UC1 = "\'\"+";

    public static final String UC2 = "-?";

    public static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    public static final byte[] row1 = Bytes.toBytes("row1");

    public static final byte[] row2 = Bytes.toBytes("row2");

    public static final byte[] row3 = Bytes.toBytes("row3");

    public static final byte[] row4 = Bytes.toBytes("row4");

    public static final byte[] fam = Bytes.toBytes("info");

    public static final byte[] qual = Bytes.toBytes("qual");

    public static final byte[] value = Bytes.toBytes("value");

    public static Configuration conf;

    private volatile boolean killedRS = false;

    @Rule
    public final TestName TEST_NAME = new TestName();

    public static User SUPERUSER;

    public static User USER1;

    @Test
    public void testSecurityCapabilities() throws Exception {
        List<SecurityCapability> capabilities = TestVisibilityLabels.TEST_UTIL.getConnection().getAdmin().getSecurityCapabilities();
        Assert.assertTrue("CELL_VISIBILITY capability is missing", capabilities.contains(CELL_VISIBILITY));
    }

    @Test
    public void testSimpleVisibilityLabels() throws Exception {
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        try (Table table = TestVisibilityLabels.createTableAndWriteDataWithLabels(tableName, (((TestVisibilityLabels.SECRET) + "|") + (TestVisibilityLabels.CONFIDENTIAL)), (((TestVisibilityLabels.PRIVATE) + "|") + (TestVisibilityLabels.CONFIDENTIAL)))) {
            Scan s = new Scan();
            s.setAuthorizations(new Authorizations(TestVisibilityLabels.SECRET, TestVisibilityLabels.CONFIDENTIAL, TestVisibilityLabels.PRIVATE));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 2));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), TestVisibilityLabels.row1, 0, TestVisibilityLabels.row1.length));
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), TestVisibilityLabels.row2, 0, TestVisibilityLabels.row2.length));
        }
    }

    @Test
    public void testSimpleVisibilityLabelsWithUniCodeCharacters() throws Exception {
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        try (Table table = TestVisibilityLabels.createTableAndWriteDataWithLabels(tableName, (((TestVisibilityLabels.SECRET) + "|") + (CellVisibility.quote(TestVisibilityLabels.COPYRIGHT))), ((((("(" + (CellVisibility.quote(TestVisibilityLabels.COPYRIGHT))) + "&") + (CellVisibility.quote(TestVisibilityLabels.ACCENT))) + ")|") + (TestVisibilityLabels.CONFIDENTIAL)), (((CellVisibility.quote(TestVisibilityLabels.UNICODE_VIS_TAG)) + "&") + (TestVisibilityLabels.SECRET)))) {
            Scan s = new Scan();
            s.setAuthorizations(new Authorizations(TestVisibilityLabels.SECRET, TestVisibilityLabels.CONFIDENTIAL, TestVisibilityLabels.PRIVATE, TestVisibilityLabels.COPYRIGHT, TestVisibilityLabels.ACCENT, TestVisibilityLabels.UNICODE_VIS_TAG));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 3));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), TestVisibilityLabels.row1, 0, TestVisibilityLabels.row1.length));
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), TestVisibilityLabels.row2, 0, TestVisibilityLabels.row2.length));
            cellScanner = next[2].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), TestVisibilityLabels.row3, 0, TestVisibilityLabels.row3.length));
        }
    }

    @Test
    public void testAuthorizationsWithSpecialUnicodeCharacters() throws Exception {
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        try (Table table = TestVisibilityLabels.createTableAndWriteDataWithLabels(tableName, (((CellVisibility.quote(TestVisibilityLabels.UC1)) + "|") + (CellVisibility.quote(TestVisibilityLabels.UC2))), CellVisibility.quote(TestVisibilityLabels.UC1), CellVisibility.quote(TestVisibilityLabels.UNICODE_VIS_TAG))) {
            Scan s = new Scan();
            s.setAuthorizations(new Authorizations(TestVisibilityLabels.UC1, TestVisibilityLabels.UC2, TestVisibilityLabels.ACCENT, TestVisibilityLabels.UNICODE_VIS_TAG));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 3));
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), TestVisibilityLabels.row1, 0, TestVisibilityLabels.row1.length));
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), TestVisibilityLabels.row2, 0, TestVisibilityLabels.row2.length));
            cellScanner = next[2].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), TestVisibilityLabels.row3, 0, TestVisibilityLabels.row3.length));
        }
    }

    @Test
    public void testVisibilityLabelsWithComplexLabels() throws Exception {
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        try (Table table = TestVisibilityLabels.createTableAndWriteDataWithLabels(tableName, ((((((("(" + (TestVisibilityLabels.SECRET)) + "|") + (TestVisibilityLabels.CONFIDENTIAL)) + ")") + "&") + "!") + (TestVisibilityLabels.TOPSECRET)), (((((("(" + (TestVisibilityLabels.PRIVATE)) + "&") + (TestVisibilityLabels.CONFIDENTIAL)) + "&") + (TestVisibilityLabels.SECRET)) + ")"), (((((("(" + (TestVisibilityLabels.PRIVATE)) + "&") + (TestVisibilityLabels.CONFIDENTIAL)) + "&") + (TestVisibilityLabels.SECRET)) + ")"), (((((("(" + (TestVisibilityLabels.PRIVATE)) + "&") + (TestVisibilityLabels.CONFIDENTIAL)) + "&") + (TestVisibilityLabels.SECRET)) + ")"))) {
            Scan s = new Scan();
            s.setAuthorizations(new Authorizations(TestVisibilityLabels.TOPSECRET, TestVisibilityLabels.CONFIDENTIAL, TestVisibilityLabels.PRIVATE, TestVisibilityLabels.PUBLIC, TestVisibilityLabels.SECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(4);
            Assert.assertEquals(3, next.length);
            CellScanner cellScanner = next[0].cellScanner();
            cellScanner.advance();
            Cell current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), TestVisibilityLabels.row2, 0, TestVisibilityLabels.row2.length));
            cellScanner = next[1].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), TestVisibilityLabels.row3, 0, TestVisibilityLabels.row3.length));
            cellScanner = next[2].cellScanner();
            cellScanner.advance();
            current = cellScanner.current();
            Assert.assertTrue(Bytes.equals(current.getRowArray(), current.getRowOffset(), current.getRowLength(), TestVisibilityLabels.row4, 0, TestVisibilityLabels.row4.length));
        }
    }

    @Test
    public void testVisibilityLabelsThatDoesNotPassTheCriteria() throws Exception {
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        try (Table table = TestVisibilityLabels.createTableAndWriteDataWithLabels(tableName, (((("(" + (TestVisibilityLabels.SECRET)) + "|") + (TestVisibilityLabels.CONFIDENTIAL)) + ")"), TestVisibilityLabels.PRIVATE)) {
            Scan s = new Scan();
            s.setAuthorizations(new Authorizations(TestVisibilityLabels.PUBLIC));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 0));
        }
    }

    @Test
    public void testVisibilityLabelsInPutsThatDoesNotMatchAnyDefinedLabels() throws Exception {
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        try {
            TestVisibilityLabels.createTableAndWriteDataWithLabels(tableName, "SAMPLE_LABEL", "TEST");
            Assert.fail("Should have failed with failed sanity check exception");
        } catch (Exception e) {
        }
    }

    @Test
    public void testVisibilityLabelsInScanThatDoesNotMatchAnyDefinedLabels() throws Exception {
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        try (Table table = TestVisibilityLabels.createTableAndWriteDataWithLabels(tableName, (((("(" + (TestVisibilityLabels.SECRET)) + "|") + (TestVisibilityLabels.CONFIDENTIAL)) + ")"), TestVisibilityLabels.PRIVATE)) {
            Scan s = new Scan();
            s.setAuthorizations(new Authorizations("SAMPLE"));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 0));
        }
    }

    @Test
    public void testVisibilityLabelsWithGet() throws Exception {
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        try (Table table = TestVisibilityLabels.createTableAndWriteDataWithLabels(tableName, (((((TestVisibilityLabels.SECRET) + "&") + (TestVisibilityLabels.CONFIDENTIAL)) + "&!") + (TestVisibilityLabels.PRIVATE)), (((((TestVisibilityLabels.SECRET) + "&") + (TestVisibilityLabels.CONFIDENTIAL)) + "&") + (TestVisibilityLabels.PRIVATE)))) {
            Get get = new Get(TestVisibilityLabels.row1);
            get.setAuthorizations(new Authorizations(TestVisibilityLabels.SECRET, TestVisibilityLabels.CONFIDENTIAL));
            Result result = table.get(get);
            Assert.assertTrue((!(result.isEmpty())));
            Cell cell = result.getColumnLatestCell(TestVisibilityLabels.fam, TestVisibilityLabels.qual);
            Assert.assertTrue(Bytes.equals(TestVisibilityLabels.value, 0, TestVisibilityLabels.value.length, cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
        }
    }

    @Test
    public void testVisibilityLabelsOnKillingOfRSContainingLabelsTable() throws Exception {
        List<RegionServerThread> regionServerThreads = TestVisibilityLabels.TEST_UTIL.getHBaseCluster().getRegionServerThreads();
        int liveRS = 0;
        for (RegionServerThread rsThreads : regionServerThreads) {
            if (!(rsThreads.getRegionServer().isAborted())) {
                liveRS++;
            }
        }
        if (liveRS == 1) {
            TestVisibilityLabels.TEST_UTIL.getHBaseCluster().startRegionServer();
        }
        Thread t1 = new Thread() {
            @Override
            public void run() {
                List<RegionServerThread> regionServerThreads = TestVisibilityLabels.TEST_UTIL.getHBaseCluster().getRegionServerThreads();
                for (RegionServerThread rsThread : regionServerThreads) {
                    List<HRegion> onlineRegions = rsThread.getRegionServer().getRegions(VisibilityConstants.LABELS_TABLE_NAME);
                    if ((onlineRegions.size()) > 0) {
                        rsThread.getRegionServer().abort("Aborting ");
                        killedRS = true;
                        break;
                    }
                }
            }
        };
        t1.start();
        final TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!(killedRS)) {
                        Thread.sleep(1);
                    } 
                    TestVisibilityLabels.createTableAndWriteDataWithLabels(tableName, (((("(" + (TestVisibilityLabels.SECRET)) + "|") + (TestVisibilityLabels.CONFIDENTIAL)) + ")"), TestVisibilityLabels.PRIVATE);
                } catch (Exception e) {
                }
            }
        };
        t.start();
        regionServerThreads = TestVisibilityLabels.TEST_UTIL.getHBaseCluster().getRegionServerThreads();
        while (!(killedRS)) {
            Thread.sleep(10);
        } 
        regionServerThreads = TestVisibilityLabels.TEST_UTIL.getHBaseCluster().getRegionServerThreads();
        for (RegionServerThread rsThread : regionServerThreads) {
            while (true) {
                if (!(rsThread.getRegionServer().isAborted())) {
                    List<HRegion> onlineRegions = rsThread.getRegionServer().getRegions(VisibilityConstants.LABELS_TABLE_NAME);
                    if ((onlineRegions.size()) > 0) {
                        break;
                    } else {
                        Thread.sleep(10);
                    }
                } else {
                    break;
                }
            } 
        }
        TestVisibilityLabels.TEST_UTIL.waitTableEnabled(VisibilityConstants.LABELS_TABLE_NAME.getName(), 50000);
        t.join();
        try (Table table = TestVisibilityLabels.TEST_UTIL.getConnection().getTable(tableName)) {
            Scan s = new Scan();
            s.setAuthorizations(new Authorizations(TestVisibilityLabels.SECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 1));
        }
    }

    @Test
    public void testVisibilityLabelsOnRSRestart() throws Exception {
        final TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        List<RegionServerThread> regionServerThreads = TestVisibilityLabels.TEST_UTIL.getHBaseCluster().getRegionServerThreads();
        for (RegionServerThread rsThread : regionServerThreads) {
            rsThread.getRegionServer().abort("Aborting ");
        }
        // Start one new RS
        RegionServerThread rs = TestVisibilityLabels.TEST_UTIL.getHBaseCluster().startRegionServer();
        waitForLabelsRegionAvailability(rs.getRegionServer());
        try (Table table = TestVisibilityLabels.createTableAndWriteDataWithLabels(tableName, (((("(" + (TestVisibilityLabels.SECRET)) + "|") + (TestVisibilityLabels.CONFIDENTIAL)) + ")"), TestVisibilityLabels.PRIVATE)) {
            Scan s = new Scan();
            s.setAuthorizations(new Authorizations(TestVisibilityLabels.SECRET));
            ResultScanner scanner = table.getScanner(s);
            Result[] next = scanner.next(3);
            Assert.assertTrue(((next.length) == 1));
        }
    }

    @Test
    public void testVisibilityLabelsInGetThatDoesNotMatchAnyDefinedLabels() throws Exception {
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        try (Table table = TestVisibilityLabels.createTableAndWriteDataWithLabels(tableName, (((("(" + (TestVisibilityLabels.SECRET)) + "|") + (TestVisibilityLabels.CONFIDENTIAL)) + ")"), TestVisibilityLabels.PRIVATE)) {
            Get get = new Get(TestVisibilityLabels.row1);
            get.setAuthorizations(new Authorizations("SAMPLE"));
            Result result = table.get(get);
            Assert.assertTrue(result.isEmpty());
        }
    }

    @Test
    public void testSetAndGetUserAuths() throws Throwable {
        final String user = "user1";
        PrivilegedExceptionAction<Void> action = new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                String[] auths = new String[]{ TestVisibilityLabels.SECRET, TestVisibilityLabels.CONFIDENTIAL };
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabels.conf)) {
                    VisibilityClient.setAuths(conn, auths, user);
                } catch (Throwable e) {
                    throw new IOException(e);
                }
                return null;
            }
        };
        TestVisibilityLabels.SUPERUSER.runAs(action);
        try (Table ht = TestVisibilityLabels.TEST_UTIL.getConnection().getTable(VisibilityConstants.LABELS_TABLE_NAME)) {
            Scan scan = new Scan();
            scan.setAuthorizations(new Authorizations(SYSTEM_LABEL));
            ResultScanner scanner = ht.getScanner(scan);
            Result result = null;
            List<Result> results = new ArrayList<>();
            while ((result = scanner.next()) != null) {
                results.add(result);
            } 
            List<String> auths = extractAuths(user, results);
            Assert.assertTrue(auths.contains(TestVisibilityLabels.SECRET));
            Assert.assertTrue(auths.contains(TestVisibilityLabels.CONFIDENTIAL));
            Assert.assertEquals(2, auths.size());
        }
        action = new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                GetAuthsResponse authsResponse = null;
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabels.conf)) {
                    authsResponse = VisibilityClient.getAuths(conn, user);
                } catch (Throwable e) {
                    throw new IOException(e);
                }
                List<String> authsList = new ArrayList(authsResponse.getAuthList().size());
                for (ByteString authBS : authsResponse.getAuthList()) {
                    authsList.add(Bytes.toString(authBS.toByteArray()));
                }
                Assert.assertEquals(2, authsList.size());
                Assert.assertTrue(authsList.contains(TestVisibilityLabels.SECRET));
                Assert.assertTrue(authsList.contains(TestVisibilityLabels.CONFIDENTIAL));
                return null;
            }
        };
        TestVisibilityLabels.SUPERUSER.runAs(action);
        // Try doing setAuths once again and there should not be any duplicates
        action = new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                String[] auths1 = new String[]{ TestVisibilityLabels.SECRET, TestVisibilityLabels.CONFIDENTIAL };
                GetAuthsResponse authsResponse = null;
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabels.conf)) {
                    VisibilityClient.setAuths(conn, auths1, user);
                    try {
                        authsResponse = VisibilityClient.getAuths(conn, user);
                    } catch (Throwable e) {
                        throw new IOException(e);
                    }
                } catch (Throwable e) {
                }
                List<String> authsList = new ArrayList(authsResponse.getAuthList().size());
                for (ByteString authBS : authsResponse.getAuthList()) {
                    authsList.add(Bytes.toString(authBS.toByteArray()));
                }
                Assert.assertEquals(2, authsList.size());
                Assert.assertTrue(authsList.contains(TestVisibilityLabels.SECRET));
                Assert.assertTrue(authsList.contains(TestVisibilityLabels.CONFIDENTIAL));
                return null;
            }
        };
        TestVisibilityLabels.SUPERUSER.runAs(action);
    }

    @Test
    public void testClearUserAuths() throws Throwable {
        PrivilegedExceptionAction<Void> action = new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                String[] auths = new String[]{ TestVisibilityLabels.SECRET, TestVisibilityLabels.CONFIDENTIAL, TestVisibilityLabels.PRIVATE };
                String user = "testUser";
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabels.conf)) {
                    VisibilityClient.setAuths(conn, auths, user);
                } catch (Throwable e) {
                    throw new IOException(e);
                }
                // Removing the auths for SECRET and CONFIDENTIAL for the user.
                // Passing a non existing auth also.
                auths = new String[]{ TestVisibilityLabels.SECRET, TestVisibilityLabels.PUBLIC, TestVisibilityLabels.CONFIDENTIAL };
                VisibilityLabelsResponse response = null;
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabels.conf)) {
                    response = VisibilityClient.clearAuths(conn, auths, user);
                } catch (Throwable e) {
                    Assert.fail("Should not have failed");
                }
                List<RegionActionResult> resultList = response.getResultList();
                Assert.assertEquals(3, resultList.size());
                Assert.assertTrue(resultList.get(0).getException().getValue().isEmpty());
                Assert.assertEquals("org.apache.hadoop.hbase.DoNotRetryIOException", resultList.get(1).getException().getName());
                Assert.assertTrue(Bytes.toString(resultList.get(1).getException().getValue().toByteArray()).contains(("org.apache.hadoop.hbase.security.visibility.InvalidLabelException: " + "Label 'public' is not set for the user testUser")));
                Assert.assertTrue(resultList.get(2).getException().getValue().isEmpty());
                try (Connection connection = ConnectionFactory.createConnection(TestVisibilityLabels.conf);Table ht = connection.getTable(VisibilityConstants.LABELS_TABLE_NAME)) {
                    ResultScanner scanner = ht.getScanner(new Scan());
                    Result result = null;
                    List<Result> results = new ArrayList<>();
                    while ((result = scanner.next()) != null) {
                        results.add(result);
                    } 
                    List<String> curAuths = extractAuths(user, results);
                    Assert.assertTrue(curAuths.contains(TestVisibilityLabels.PRIVATE));
                    Assert.assertEquals(1, curAuths.size());
                }
                GetAuthsResponse authsResponse = null;
                try (Connection conn = ConnectionFactory.createConnection(TestVisibilityLabels.conf)) {
                    authsResponse = VisibilityClient.getAuths(conn, user);
                } catch (Throwable e) {
                    throw new IOException(e);
                }
                List<String> authsList = new ArrayList(authsResponse.getAuthList().size());
                for (ByteString authBS : authsResponse.getAuthList()) {
                    authsList.add(Bytes.toString(authBS.toByteArray()));
                }
                Assert.assertEquals(1, authsList.size());
                Assert.assertTrue(authsList.contains(TestVisibilityLabels.PRIVATE));
                return null;
            }
        };
        TestVisibilityLabels.SUPERUSER.runAs(action);
    }

    @Test
    public void testLabelsWithCheckAndPut() throws Throwable {
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        try (Table table = TestVisibilityLabels.TEST_UTIL.createTable(tableName, TestVisibilityLabels.fam)) {
            byte[] row1 = Bytes.toBytes("row1");
            Put put = new Put(row1);
            put.addColumn(TestVisibilityLabels.fam, TestVisibilityLabels.qual, LATEST_TIMESTAMP, TestVisibilityLabels.value);
            put.setCellVisibility(new CellVisibility((((TestVisibilityLabels.SECRET) + " & ") + (TestVisibilityLabels.CONFIDENTIAL))));
            table.checkAndMutate(row1, TestVisibilityLabels.fam).qualifier(TestVisibilityLabels.qual).ifNotExists().thenPut(put);
            byte[] row2 = Bytes.toBytes("row2");
            put = new Put(row2);
            put.addColumn(TestVisibilityLabels.fam, TestVisibilityLabels.qual, LATEST_TIMESTAMP, TestVisibilityLabels.value);
            put.setCellVisibility(new CellVisibility(TestVisibilityLabels.SECRET));
            table.checkAndMutate(row2, TestVisibilityLabels.fam).qualifier(TestVisibilityLabels.qual).ifNotExists().thenPut(put);
            Scan scan = new Scan();
            scan.setAuthorizations(new Authorizations(TestVisibilityLabels.SECRET));
            ResultScanner scanner = table.getScanner(scan);
            Result result = scanner.next();
            Assert.assertTrue((!(result.isEmpty())));
            Assert.assertTrue(Bytes.equals(row2, result.getRow()));
            result = scanner.next();
            Assert.assertNull(result);
        }
    }

    @Test
    public void testLabelsWithIncrement() throws Throwable {
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        try (Table table = TestVisibilityLabels.TEST_UTIL.createTable(tableName, TestVisibilityLabels.fam)) {
            byte[] row1 = Bytes.toBytes("row1");
            byte[] val = Bytes.toBytes(1L);
            Put put = new Put(row1);
            put.addColumn(TestVisibilityLabels.fam, TestVisibilityLabels.qual, LATEST_TIMESTAMP, val);
            put.setCellVisibility(new CellVisibility((((TestVisibilityLabels.SECRET) + " & ") + (TestVisibilityLabels.CONFIDENTIAL))));
            table.put(put);
            Get get = new Get(row1);
            get.setAuthorizations(new Authorizations(TestVisibilityLabels.SECRET));
            Result result = table.get(get);
            Assert.assertTrue(result.isEmpty());
            table.incrementColumnValue(row1, TestVisibilityLabels.fam, TestVisibilityLabels.qual, 2L);
            result = table.get(get);
            Assert.assertTrue(result.isEmpty());
            Increment increment = new Increment(row1);
            increment.addColumn(TestVisibilityLabels.fam, TestVisibilityLabels.qual, 2L);
            increment.setCellVisibility(new CellVisibility(TestVisibilityLabels.SECRET));
            table.increment(increment);
            result = table.get(get);
            Assert.assertTrue((!(result.isEmpty())));
        }
    }

    @Test
    public void testLabelsWithAppend() throws Throwable {
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        try (Table table = TestVisibilityLabels.TEST_UTIL.createTable(tableName, TestVisibilityLabels.fam)) {
            byte[] row1 = Bytes.toBytes("row1");
            byte[] val = Bytes.toBytes("a");
            Put put = new Put(row1);
            put.addColumn(TestVisibilityLabels.fam, TestVisibilityLabels.qual, LATEST_TIMESTAMP, val);
            put.setCellVisibility(new CellVisibility((((TestVisibilityLabels.SECRET) + " & ") + (TestVisibilityLabels.CONFIDENTIAL))));
            table.put(put);
            Get get = new Get(row1);
            get.setAuthorizations(new Authorizations(TestVisibilityLabels.SECRET));
            Result result = table.get(get);
            Assert.assertTrue(result.isEmpty());
            Append append = new Append(row1);
            append.addColumn(TestVisibilityLabels.fam, TestVisibilityLabels.qual, Bytes.toBytes("b"));
            table.append(append);
            result = table.get(get);
            Assert.assertTrue(result.isEmpty());
            append = new Append(row1);
            append.addColumn(TestVisibilityLabels.fam, TestVisibilityLabels.qual, Bytes.toBytes("c"));
            append.setCellVisibility(new CellVisibility(TestVisibilityLabels.SECRET));
            table.append(append);
            result = table.get(get);
            Assert.assertTrue((!(result.isEmpty())));
        }
    }

    @Test
    public void testUserShouldNotDoDDLOpOnLabelsTable() throws Exception {
        Admin admin = TestVisibilityLabels.TEST_UTIL.getAdmin();
        try {
            admin.disableTable(VisibilityConstants.LABELS_TABLE_NAME);
            Assert.fail("Lables table should not get disabled by user.");
        } catch (Exception e) {
        }
        try {
            admin.deleteTable(VisibilityConstants.LABELS_TABLE_NAME);
            Assert.fail("Lables table should not get disabled by user.");
        } catch (Exception e) {
        }
        try {
            HColumnDescriptor hcd = new HColumnDescriptor("testFamily");
            admin.addColumnFamily(VisibilityConstants.LABELS_TABLE_NAME, hcd);
            Assert.fail("Lables table should not get altered by user.");
        } catch (Exception e) {
        }
        try {
            admin.deleteColumnFamily(VisibilityConstants.LABELS_TABLE_NAME, LABELS_TABLE_FAMILY);
            Assert.fail("Lables table should not get altered by user.");
        } catch (Exception e) {
        }
        try {
            HColumnDescriptor hcd = new HColumnDescriptor(LABELS_TABLE_FAMILY);
            hcd.setBloomFilterType(ROWCOL);
            admin.modifyColumnFamily(VisibilityConstants.LABELS_TABLE_NAME, hcd);
            Assert.fail("Lables table should not get altered by user.");
        } catch (Exception e) {
        }
        try {
            HTableDescriptor htd = new HTableDescriptor(VisibilityConstants.LABELS_TABLE_NAME);
            htd.addFamily(new HColumnDescriptor("f1"));
            htd.addFamily(new HColumnDescriptor("f2"));
            admin.modifyTable(VisibilityConstants.LABELS_TABLE_NAME, htd);
            Assert.fail("Lables table should not get altered by user.");
        } catch (Exception e) {
        }
    }

    @Test
    public void testMultipleVersions() throws Exception {
        final byte[] r1 = Bytes.toBytes("row1");
        final byte[] r2 = Bytes.toBytes("row2");
        final byte[] v1 = Bytes.toBytes("100");
        final byte[] v2 = Bytes.toBytes("101");
        final byte[] fam2 = Bytes.toBytes("info2");
        final byte[] qual2 = Bytes.toBytes("qual2");
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        HTableDescriptor desc = new HTableDescriptor(tableName);
        HColumnDescriptor col = new HColumnDescriptor(TestVisibilityLabels.fam);// Default max versions is 1.

        desc.addFamily(col);
        col = new HColumnDescriptor(fam2);
        col.setMaxVersions(5);
        desc.addFamily(col);
        TestVisibilityLabels.TEST_UTIL.getAdmin().createTable(desc);
        try (Table table = TestVisibilityLabels.TEST_UTIL.getConnection().getTable(tableName)) {
            Put put = new Put(r1);
            put.addColumn(TestVisibilityLabels.fam, TestVisibilityLabels.qual, 3L, v1);
            put.addColumn(TestVisibilityLabels.fam, qual2, 3L, v1);
            put.addColumn(fam2, TestVisibilityLabels.qual, 3L, v1);
            put.addColumn(fam2, qual2, 3L, v1);
            put.setCellVisibility(new CellVisibility(TestVisibilityLabels.SECRET));
            table.put(put);
            put = new Put(r1);
            put.addColumn(TestVisibilityLabels.fam, TestVisibilityLabels.qual, 4L, v2);
            put.addColumn(TestVisibilityLabels.fam, qual2, 4L, v2);
            put.addColumn(fam2, TestVisibilityLabels.qual, 4L, v2);
            put.addColumn(fam2, qual2, 4L, v2);
            put.setCellVisibility(new CellVisibility(TestVisibilityLabels.PRIVATE));
            table.put(put);
            put = new Put(r2);
            put.addColumn(TestVisibilityLabels.fam, TestVisibilityLabels.qual, 3L, v1);
            put.addColumn(TestVisibilityLabels.fam, qual2, 3L, v1);
            put.addColumn(fam2, TestVisibilityLabels.qual, 3L, v1);
            put.addColumn(fam2, qual2, 3L, v1);
            put.setCellVisibility(new CellVisibility(TestVisibilityLabels.SECRET));
            table.put(put);
            put = new Put(r2);
            put.addColumn(TestVisibilityLabels.fam, TestVisibilityLabels.qual, 4L, v2);
            put.addColumn(TestVisibilityLabels.fam, qual2, 4L, v2);
            put.addColumn(fam2, TestVisibilityLabels.qual, 4L, v2);
            put.addColumn(fam2, qual2, 4L, v2);
            put.setCellVisibility(new CellVisibility(TestVisibilityLabels.SECRET));
            table.put(put);
            Scan s = new Scan();
            s.setMaxVersions(1);
            s.setAuthorizations(new Authorizations(TestVisibilityLabels.SECRET));
            ResultScanner scanner = table.getScanner(s);
            Result result = scanner.next();
            Assert.assertTrue(Bytes.equals(r1, result.getRow()));
            // for cf 'fam' max versions in HCD is 1. So the old version cells, which are having matching
            // CellVisibility with Authorizations, should not get considered in the label evaluation at
            // all.
            Assert.assertNull(result.getColumnLatestCell(TestVisibilityLabels.fam, TestVisibilityLabels.qual));
            Assert.assertNull(result.getColumnLatestCell(TestVisibilityLabels.fam, qual2));
            // for cf 'fam2' max versions in HCD is > 1. So we can consider the old version cells, which
            // are having matching CellVisibility with Authorizations, in the label evaluation. It can
            // just skip those recent versions for which visibility is not there as per the new version's
            // CellVisibility. The old versions which are having visibility can be send back
            Cell cell = result.getColumnLatestCell(fam2, TestVisibilityLabels.qual);
            Assert.assertNotNull(cell);
            Assert.assertTrue(Bytes.equals(v1, 0, v1.length, cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
            cell = result.getColumnLatestCell(fam2, qual2);
            Assert.assertNotNull(cell);
            Assert.assertTrue(Bytes.equals(v1, 0, v1.length, cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
            result = scanner.next();
            Assert.assertTrue(Bytes.equals(r2, result.getRow()));
            cell = result.getColumnLatestCell(TestVisibilityLabels.fam, TestVisibilityLabels.qual);
            Assert.assertNotNull(cell);
            Assert.assertTrue(Bytes.equals(v2, 0, v2.length, cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
            cell = result.getColumnLatestCell(TestVisibilityLabels.fam, qual2);
            Assert.assertNotNull(cell);
            Assert.assertTrue(Bytes.equals(v2, 0, v2.length, cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
            cell = result.getColumnLatestCell(fam2, TestVisibilityLabels.qual);
            Assert.assertNotNull(cell);
            Assert.assertTrue(Bytes.equals(v2, 0, v2.length, cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
            cell = result.getColumnLatestCell(fam2, qual2);
            Assert.assertNotNull(cell);
            Assert.assertTrue(Bytes.equals(v2, 0, v2.length, cell.getValueArray(), cell.getValueOffset(), cell.getValueLength()));
        }
    }

    @Test
    public void testMutateRow() throws Exception {
        final byte[] qual2 = Bytes.toBytes("qual2");
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        HTableDescriptor desc = new HTableDescriptor(tableName);
        HColumnDescriptor col = new HColumnDescriptor(TestVisibilityLabels.fam);
        desc.addFamily(col);
        TestVisibilityLabels.TEST_UTIL.getAdmin().createTable(desc);
        try (Table table = TestVisibilityLabels.TEST_UTIL.getConnection().getTable(tableName)) {
            Put p1 = new Put(TestVisibilityLabels.row1);
            p1.addColumn(TestVisibilityLabels.fam, TestVisibilityLabels.qual, TestVisibilityLabels.value);
            p1.setCellVisibility(new CellVisibility(TestVisibilityLabels.CONFIDENTIAL));
            Put p2 = new Put(TestVisibilityLabels.row1);
            p2.addColumn(TestVisibilityLabels.fam, qual2, TestVisibilityLabels.value);
            p2.setCellVisibility(new CellVisibility(TestVisibilityLabels.SECRET));
            RowMutations rm = new RowMutations(TestVisibilityLabels.row1);
            rm.add(p1);
            rm.add(p2);
            table.mutateRow(rm);
            Get get = new Get(TestVisibilityLabels.row1);
            get.setAuthorizations(new Authorizations(TestVisibilityLabels.CONFIDENTIAL));
            Result result = table.get(get);
            Assert.assertTrue(result.containsColumn(TestVisibilityLabels.fam, TestVisibilityLabels.qual));
            Assert.assertFalse(result.containsColumn(TestVisibilityLabels.fam, qual2));
            get.setAuthorizations(new Authorizations(TestVisibilityLabels.SECRET));
            result = table.get(get);
            Assert.assertFalse(result.containsColumn(TestVisibilityLabels.fam, TestVisibilityLabels.qual));
            Assert.assertTrue(result.containsColumn(TestVisibilityLabels.fam, qual2));
        }
    }

    @Test
    public void testFlushedFileWithVisibilityTags() throws Exception {
        final byte[] qual2 = Bytes.toBytes("qual2");
        TableName tableName = TableName.valueOf(TEST_NAME.getMethodName());
        HTableDescriptor desc = new HTableDescriptor(tableName);
        HColumnDescriptor col = new HColumnDescriptor(TestVisibilityLabels.fam);
        desc.addFamily(col);
        TestVisibilityLabels.TEST_UTIL.getAdmin().createTable(desc);
        try (Table table = TestVisibilityLabels.TEST_UTIL.getConnection().getTable(tableName)) {
            Put p1 = new Put(TestVisibilityLabels.row1);
            p1.addColumn(TestVisibilityLabels.fam, TestVisibilityLabels.qual, TestVisibilityLabels.value);
            p1.setCellVisibility(new CellVisibility(TestVisibilityLabels.CONFIDENTIAL));
            Put p2 = new Put(TestVisibilityLabels.row1);
            p2.addColumn(TestVisibilityLabels.fam, qual2, TestVisibilityLabels.value);
            p2.setCellVisibility(new CellVisibility(TestVisibilityLabels.SECRET));
            RowMutations rm = new RowMutations(TestVisibilityLabels.row1);
            rm.add(p1);
            rm.add(p2);
            table.mutateRow(rm);
        }
        TestVisibilityLabels.TEST_UTIL.getAdmin().flush(tableName);
        List<HRegion> regions = TestVisibilityLabels.TEST_UTIL.getHBaseCluster().getRegions(tableName);
        HStore store = regions.get(0).getStore(TestVisibilityLabels.fam);
        Collection<HStoreFile> storefiles = store.getStorefiles();
        Assert.assertTrue(((storefiles.size()) > 0));
        for (HStoreFile storeFile : storefiles) {
            Assert.assertTrue(storeFile.getReader().getHFileReader().getFileContext().isIncludesTags());
        }
    }
}

