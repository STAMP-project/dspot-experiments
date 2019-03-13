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
package org.apache.hadoop.hbase.regionserver;


import MobConstants.EMPTY_VALUE_ON_MOBCELL_MISS;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.hfile.CorruptHFileException;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category(MediumTests.class)
public class TestMobStoreScanner {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMobStoreScanner.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] row1 = Bytes.toBytes("row1");

    private static final byte[] row2 = Bytes.toBytes("row2");

    private static final byte[] family = Bytes.toBytes("family");

    private static final byte[] qf1 = Bytes.toBytes("qualifier1");

    private static final byte[] qf2 = Bytes.toBytes("qualifier2");

    protected final byte[] qf3 = Bytes.toBytes("qualifier3");

    private static Table table;

    private static Admin admin;

    private static HColumnDescriptor hcd;

    private static HTableDescriptor desc;

    private static Random random = new Random();

    private static long defaultThreshold = 10;

    private FileSystem fs;

    private Configuration conf;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testMobStoreScanner() throws Exception {
        testGetFromFiles(false);
        testGetFromMemStore(false);
        testGetReferences(false);
        testMobThreshold(false);
        testGetFromArchive(false);
    }

    @Test
    public void testReversedMobStoreScanner() throws Exception {
        testGetFromFiles(true);
        testGetFromMemStore(true);
        testGetReferences(true);
        testMobThreshold(true);
        testGetFromArchive(true);
    }

    @Test
    public void testGetMassive() throws Exception {
        setUp(TestMobStoreScanner.defaultThreshold, TableName.valueOf(name.getMethodName()));
        // Put some data 5 10, 15, 20  mb ok  (this would be right below protobuf
        // default max size of 64MB.
        // 25, 30, 40 fail.  these is above protobuf max size of 64MB
        byte[] bigValue = new byte[(25 * 1024) * 1024];
        Put put = new Put(TestMobStoreScanner.row1);
        put.addColumn(TestMobStoreScanner.family, TestMobStoreScanner.qf1, bigValue);
        put.addColumn(TestMobStoreScanner.family, TestMobStoreScanner.qf2, bigValue);
        put.addColumn(TestMobStoreScanner.family, qf3, bigValue);
        TestMobStoreScanner.table.put(put);
        Get g = new Get(TestMobStoreScanner.row1);
        TestMobStoreScanner.table.get(g);
        // should not have blown up.
    }

    @Test
    public void testReadPt() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        setUp(0L, tableName);
        long ts = System.currentTimeMillis();
        byte[] value1 = Bytes.toBytes("value1");
        Put put1 = new Put(TestMobStoreScanner.row1);
        put1.addColumn(TestMobStoreScanner.family, TestMobStoreScanner.qf1, ts, value1);
        TestMobStoreScanner.table.put(put1);
        Put put2 = new Put(TestMobStoreScanner.row2);
        byte[] value2 = Bytes.toBytes("value2");
        put2.addColumn(TestMobStoreScanner.family, TestMobStoreScanner.qf1, ts, value2);
        TestMobStoreScanner.table.put(put2);
        Scan scan = new Scan();
        scan.setCaching(1);
        ResultScanner rs = TestMobStoreScanner.table.getScanner(scan);
        Result result = rs.next();
        Put put3 = new Put(TestMobStoreScanner.row1);
        byte[] value3 = Bytes.toBytes("value3");
        put3.addColumn(TestMobStoreScanner.family, TestMobStoreScanner.qf1, ts, value3);
        TestMobStoreScanner.table.put(put3);
        Put put4 = new Put(TestMobStoreScanner.row2);
        byte[] value4 = Bytes.toBytes("value4");
        put4.addColumn(TestMobStoreScanner.family, TestMobStoreScanner.qf1, ts, value4);
        TestMobStoreScanner.table.put(put4);
        Cell cell = result.getColumnLatestCell(TestMobStoreScanner.family, TestMobStoreScanner.qf1);
        Assert.assertArrayEquals(value1, CellUtil.cloneValue(cell));
        TestMobStoreScanner.admin.flush(tableName);
        result = rs.next();
        cell = result.getColumnLatestCell(TestMobStoreScanner.family, TestMobStoreScanner.qf1);
        Assert.assertArrayEquals(value2, CellUtil.cloneValue(cell));
    }

    @Test
    public void testReadFromCorruptMobFilesWithReadEmptyValueOnMobCellMiss() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        setUp(0, tableName);
        createRecordAndCorruptMobFile(tableName, TestMobStoreScanner.row1, TestMobStoreScanner.family, TestMobStoreScanner.qf1, Bytes.toBytes("value1"));
        Get get = new Get(TestMobStoreScanner.row1);
        get.setAttribute(EMPTY_VALUE_ON_MOBCELL_MISS, Bytes.toBytes(true));
        Result result = TestMobStoreScanner.table.get(get);
        Cell cell = result.getColumnLatestCell(TestMobStoreScanner.family, TestMobStoreScanner.qf1);
        Assert.assertEquals(0, cell.getValueLength());
    }

    @Test
    public void testReadFromCorruptMobFiles() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        setUp(0, tableName);
        createRecordAndCorruptMobFile(tableName, TestMobStoreScanner.row1, TestMobStoreScanner.family, TestMobStoreScanner.qf1, Bytes.toBytes("value1"));
        Get get = new Get(TestMobStoreScanner.row1);
        IOException ioe = null;
        try {
            TestMobStoreScanner.table.get(get);
        } catch (IOException e) {
            ioe = e;
        }
        Assert.assertNotNull(ioe);
        Assert.assertEquals(CorruptHFileException.class.getName(), ioe.getClass().getName());
    }
}

