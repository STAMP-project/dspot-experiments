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
package org.apache.hadoop.hbase.mapreduce;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MapReduceTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.LauncherSecurityManager;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Basic test for the CopyTable M/R tool
 */
@Category({ MapReduceTests.class, LargeTests.class })
public class TestCopyTable {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCopyTable.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] ROW1 = Bytes.toBytes("row1");

    private static final byte[] ROW2 = Bytes.toBytes("row2");

    private static final String FAMILY_A_STRING = "a";

    private static final String FAMILY_B_STRING = "b";

    private static final byte[] FAMILY_A = Bytes.toBytes(TestCopyTable.FAMILY_A_STRING);

    private static final byte[] FAMILY_B = Bytes.toBytes(TestCopyTable.FAMILY_B_STRING);

    private static final byte[] QUALIFIER = Bytes.toBytes("q");

    @Rule
    public TestName name = new TestName();

    /**
     * Simple end-to-end test
     */
    @Test
    public void testCopyTable() throws Exception {
        doCopyTableTest(false);
    }

    /**
     * Simple end-to-end test with bulkload.
     */
    @Test
    public void testCopyTableWithBulkload() throws Exception {
        doCopyTableTest(true);
    }

    /**
     * Simple end-to-end test on table with MOB
     */
    @Test
    public void testCopyTableWithMob() throws Exception {
        doCopyTableTestWithMob(false);
    }

    /**
     * Simple end-to-end test with bulkload on table with MOB.
     */
    @Test
    public void testCopyTableWithBulkloadWithMob() throws Exception {
        doCopyTableTestWithMob(true);
    }

    @Test
    public void testStartStopRow() throws Exception {
        final TableName tableName1 = TableName.valueOf(((name.getMethodName()) + "1"));
        final TableName tableName2 = TableName.valueOf(((name.getMethodName()) + "2"));
        final byte[] FAMILY = Bytes.toBytes("family");
        final byte[] COLUMN1 = Bytes.toBytes("c1");
        final byte[] row0 = Bytes.toBytesBinary("\\x01row0");
        final byte[] row1 = Bytes.toBytesBinary("\\x01row1");
        final byte[] row2 = Bytes.toBytesBinary("\\x01row2");
        try (Table t1 = TestCopyTable.TEST_UTIL.createTable(tableName1, FAMILY);Table t2 = TestCopyTable.TEST_UTIL.createTable(tableName2, FAMILY)) {
            // put rows into the first table
            Put p = new Put(row0);
            p.addColumn(FAMILY, COLUMN1, COLUMN1);
            t1.put(p);
            p = new Put(row1);
            p.addColumn(FAMILY, COLUMN1, COLUMN1);
            t1.put(p);
            p = new Put(row2);
            p.addColumn(FAMILY, COLUMN1, COLUMN1);
            t1.put(p);
            CopyTable copy = new CopyTable();
            Assert.assertEquals(0, ToolRunner.run(new org.apache.hadoop.conf.Configuration(TestCopyTable.TEST_UTIL.getConfiguration()), copy, new String[]{ "--new.name=" + tableName2, "--startrow=\\x01row1", "--stoprow=\\x01row2", tableName1.getNameAsString() }));
            // verify the data was copied into table 2
            // row1 exist, row0, row2 do not exist
            Get g = new Get(row1);
            Result r = t2.get(g);
            Assert.assertEquals(1, r.size());
            Assert.assertTrue(CellUtil.matchingQualifier(r.rawCells()[0], COLUMN1));
            g = new Get(row0);
            r = t2.get(g);
            Assert.assertEquals(0, r.size());
            g = new Get(row2);
            r = t2.get(g);
            Assert.assertEquals(0, r.size());
        } finally {
            TestCopyTable.TEST_UTIL.deleteTable(tableName1);
            TestCopyTable.TEST_UTIL.deleteTable(tableName2);
        }
    }

    /**
     * Test copy of table from sourceTable to targetTable all rows from family a
     */
    @Test
    public void testRenameFamily() throws Exception {
        final TableName sourceTable = TableName.valueOf(((name.getMethodName()) + "source"));
        final TableName targetTable = TableName.valueOf(((name.getMethodName()) + "-target"));
        byte[][] families = new byte[][]{ TestCopyTable.FAMILY_A, TestCopyTable.FAMILY_B };
        Table t = TestCopyTable.TEST_UTIL.createTable(sourceTable, families);
        Table t2 = TestCopyTable.TEST_UTIL.createTable(targetTable, families);
        Put p = new Put(TestCopyTable.ROW1);
        p.addColumn(TestCopyTable.FAMILY_A, TestCopyTable.QUALIFIER, Bytes.toBytes("Data11"));
        p.addColumn(TestCopyTable.FAMILY_B, TestCopyTable.QUALIFIER, Bytes.toBytes("Data12"));
        p.addColumn(TestCopyTable.FAMILY_A, TestCopyTable.QUALIFIER, Bytes.toBytes("Data13"));
        t.put(p);
        p = new Put(TestCopyTable.ROW2);
        p.addColumn(TestCopyTable.FAMILY_B, TestCopyTable.QUALIFIER, Bytes.toBytes("Dat21"));
        p.addColumn(TestCopyTable.FAMILY_A, TestCopyTable.QUALIFIER, Bytes.toBytes("Data22"));
        p.addColumn(TestCopyTable.FAMILY_B, TestCopyTable.QUALIFIER, Bytes.toBytes("Data23"));
        t.put(p);
        long currentTime = System.currentTimeMillis();
        String[] args = new String[]{ "--new.name=" + targetTable, "--families=a:b", "--all.cells", "--starttime=" + (currentTime - 100000), "--endtime=" + (currentTime + 100000), "--versions=1", sourceTable.getNameAsString() };
        Assert.assertNull(t2.get(new Get(TestCopyTable.ROW1)).getRow());
        Assert.assertTrue(runCopy(args));
        Assert.assertNotNull(t2.get(new Get(TestCopyTable.ROW1)).getRow());
        Result res = t2.get(new Get(TestCopyTable.ROW1));
        byte[] b1 = res.getValue(TestCopyTable.FAMILY_B, TestCopyTable.QUALIFIER);
        Assert.assertEquals("Data13", Bytes.toString(b1));
        Assert.assertNotNull(t2.get(new Get(TestCopyTable.ROW2)).getRow());
        res = t2.get(new Get(TestCopyTable.ROW2));
        b1 = res.getValue(TestCopyTable.FAMILY_A, TestCopyTable.QUALIFIER);
        // Data from the family of B is not copied
        Assert.assertNull(b1);
    }

    /**
     * Test main method of CopyTable.
     */
    @Test
    public void testMainMethod() throws Exception {
        String[] emptyArgs = new String[]{ "-h" };
        PrintStream oldWriter = System.err;
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        PrintStream writer = new PrintStream(data);
        System.setErr(writer);
        SecurityManager SECURITY_MANAGER = System.getSecurityManager();
        LauncherSecurityManager newSecurityManager = new LauncherSecurityManager();
        System.setSecurityManager(newSecurityManager);
        try {
            CopyTable.main(emptyArgs);
            Assert.fail("should be exit");
        } catch (SecurityException e) {
            Assert.assertEquals(1, newSecurityManager.getExitCode());
        } finally {
            System.setErr(oldWriter);
            System.setSecurityManager(SECURITY_MANAGER);
        }
        Assert.assertTrue(data.toString().contains("rs.class"));
        // should print usage information
        Assert.assertTrue(data.toString().contains("Usage:"));
    }

    @Test
    public void testLoadingSnapshotToTable() throws Exception {
        testCopyTableBySnapshot("testLoadingSnapshotToTable", false, false);
    }

    @Test
    public void tsetLoadingSnapshotToMobTable() throws Exception {
        testCopyTableBySnapshot("testLoadingSnapshotToMobTable", false, true);
    }

    @Test
    public void testLoadingSnapshotAndBulkLoadToTable() throws Exception {
        testCopyTableBySnapshot("testLoadingSnapshotAndBulkLoadToTable", true, false);
    }

    @Test
    public void testLoadingSnapshotAndBulkLoadToMobTable() throws Exception {
        testCopyTableBySnapshot("testLoadingSnapshotAndBulkLoadToMobTable", true, true);
    }

    @Test
    public void testLoadingSnapshotToRemoteCluster() throws Exception {
        Assert.assertFalse(runCopy(new String[]{ "--snapshot", "--peerAdr=hbase://remoteHBase", "sourceSnapshotName" }));
    }

    @Test
    public void testLoadingSnapshotWithoutSnapshotName() throws Exception {
        Assert.assertFalse(runCopy(new String[]{ "--snapshot", "--peerAdr=hbase://remoteHBase" }));
    }

    @Test
    public void testLoadingSnapshotWithoutDestTable() throws Exception {
        Assert.assertFalse(runCopy(new String[]{ "--snapshot", "sourceSnapshotName" }));
    }
}

