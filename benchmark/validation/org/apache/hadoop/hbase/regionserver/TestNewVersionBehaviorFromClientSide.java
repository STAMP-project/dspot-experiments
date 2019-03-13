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


import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestNewVersionBehaviorFromClientSide {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestNewVersionBehaviorFromClientSide.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte[] ROW = Bytes.toBytes("r1");

    private static final byte[] ROW2 = Bytes.toBytes("r2");

    private static final byte[] FAMILY = Bytes.toBytes("f");

    private static final byte[] value = Bytes.toBytes("value");

    private static final byte[] col1 = Bytes.toBytes("col1");

    private static final byte[] col2 = Bytes.toBytes("col2");

    private static final byte[] col3 = Bytes.toBytes("col3");

    @Rule
    public TestName name = new TestName();

    @Test
    public void testPutAndDeleteVersions() throws IOException {
        try (Table t = createTable()) {
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000001, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000002, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000004, TestNewVersionBehaviorFromClientSide.value));
            t.delete(new Delete(TestNewVersionBehaviorFromClientSide.ROW).addColumns(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 2000000));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000000, TestNewVersionBehaviorFromClientSide.value));
            TestNewVersionBehaviorFromClientSide.TEST_UTIL.getAdmin().flush(t.getName());
            Result r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(3));
            Assert.assertEquals(1, r.size());
            Assert.assertEquals(1000000, r.rawCells()[0].getTimestamp());
        }
    }

    @Test
    public void testPutMasked() throws IOException {
        try (Table t = createTable()) {
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000001, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000002, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000004, TestNewVersionBehaviorFromClientSide.value));
            t.delete(new Delete(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003));
            Result r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(3));
            Assert.assertEquals(2, r.size());
            Assert.assertEquals(1000004, r.rawCells()[0].getTimestamp());
            Assert.assertEquals(1000002, r.rawCells()[1].getTimestamp());
            TestNewVersionBehaviorFromClientSide.TEST_UTIL.getAdmin().flush(t.getName());
            r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(3));
            Assert.assertEquals(2, r.size());
            Assert.assertEquals(1000004, r.rawCells()[0].getTimestamp());
            Assert.assertEquals(1000002, r.rawCells()[1].getTimestamp());
        }
    }

    @Test
    public void testPutMasked2() throws IOException {
        try (Table t = createTable()) {
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000001, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000002, TestNewVersionBehaviorFromClientSide.value));
            t.delete(new Delete(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000004, TestNewVersionBehaviorFromClientSide.value));
            Result r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(3));
            Assert.assertEquals(3, r.size());
            Assert.assertEquals(1000004, r.rawCells()[0].getTimestamp());
            Assert.assertEquals(1000003, r.rawCells()[1].getTimestamp());
            Assert.assertEquals(1000002, r.rawCells()[2].getTimestamp());
            TestNewVersionBehaviorFromClientSide.TEST_UTIL.getAdmin().flush(t.getName());
            r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(3));
            Assert.assertEquals(3, r.size());
            Assert.assertEquals(1000004, r.rawCells()[0].getTimestamp());
            Assert.assertEquals(1000003, r.rawCells()[1].getTimestamp());
            Assert.assertEquals(1000002, r.rawCells()[2].getTimestamp());
        }
    }

    @Test
    public void testPutMaskedAndUserMaxVersion() throws IOException {
        try (Table t = createTable()) {
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000001, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000002, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000004, TestNewVersionBehaviorFromClientSide.value));
            t.delete(new Delete(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000004));
            t.delete(new Delete(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003));
            Result r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(1));
            Assert.assertEquals(1, r.size());
            Assert.assertEquals(1000002, r.rawCells()[0].getTimestamp());
            TestNewVersionBehaviorFromClientSide.TEST_UTIL.getAdmin().flush(t.getName());
            r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(1));
            Assert.assertEquals(1, r.size());
            Assert.assertEquals(1000002, r.rawCells()[0].getTimestamp());
        }
    }

    @Test
    public void testSameTs() throws IOException {
        try (Table t = createTable()) {
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000001, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000002, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000004, TestNewVersionBehaviorFromClientSide.value));
            Result r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(3));
            Assert.assertEquals(3, r.size());
            Assert.assertEquals(1000004, r.rawCells()[0].getTimestamp());
            Assert.assertEquals(1000003, r.rawCells()[1].getTimestamp());
            Assert.assertEquals(1000002, r.rawCells()[2].getTimestamp());
            TestNewVersionBehaviorFromClientSide.TEST_UTIL.getAdmin().flush(t.getName());
            r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(3));
            Assert.assertEquals(3, r.size());
            Assert.assertEquals(1000004, r.rawCells()[0].getTimestamp());
            Assert.assertEquals(1000003, r.rawCells()[1].getTimestamp());
            Assert.assertEquals(1000002, r.rawCells()[2].getTimestamp());
        }
    }

    @Test
    public void testSameTsAndDelete() throws IOException {
        try (Table t = createTable()) {
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000001, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000002, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003, TestNewVersionBehaviorFromClientSide.value));
            t.delete(new Delete(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000004, TestNewVersionBehaviorFromClientSide.value));
            Result r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(3));
            Assert.assertEquals(3, r.size());
            Assert.assertEquals(1000004, r.rawCells()[0].getTimestamp());
            Assert.assertEquals(1000002, r.rawCells()[1].getTimestamp());
            Assert.assertEquals(1000001, r.rawCells()[2].getTimestamp());
            TestNewVersionBehaviorFromClientSide.TEST_UTIL.getAdmin().flush(t.getName());
            r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(3));
            Assert.assertEquals(3, r.size());
            Assert.assertEquals(1000004, r.rawCells()[0].getTimestamp());
            Assert.assertEquals(1000002, r.rawCells()[1].getTimestamp());
            Assert.assertEquals(1000001, r.rawCells()[2].getTimestamp());
        }
    }

    @Test
    public void testDeleteFamily() throws IOException {
        try (Table t = createTable()) {
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000001, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000002, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col2, 1000002, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col3, 1000001, TestNewVersionBehaviorFromClientSide.value));
            t.delete(new Delete(TestNewVersionBehaviorFromClientSide.ROW).addFamily(TestNewVersionBehaviorFromClientSide.FAMILY, 2000000));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col3, 1500002, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col2, 1500001, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1500001, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1500002, TestNewVersionBehaviorFromClientSide.value));
            TestNewVersionBehaviorFromClientSide.TEST_UTIL.getAdmin().flush(t.getName());
            Result r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(3));
            Assert.assertEquals(4, r.size());
            Assert.assertEquals(1500002, r.rawCells()[0].getTimestamp());
            Assert.assertEquals(1500001, r.rawCells()[1].getTimestamp());
            Assert.assertEquals(1500001, r.rawCells()[2].getTimestamp());
            Assert.assertEquals(1500002, r.rawCells()[3].getTimestamp());
            t.delete(new Delete(TestNewVersionBehaviorFromClientSide.ROW).addFamilyVersion(TestNewVersionBehaviorFromClientSide.FAMILY, 1500001));
            r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(3));
            Assert.assertEquals(2, r.size());
            Assert.assertEquals(1500002, r.rawCells()[0].getTimestamp());
            Assert.assertEquals(1500002, r.rawCells()[1].getTimestamp());
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000001, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000002, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col2, 1000002, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col3, 1000001, TestNewVersionBehaviorFromClientSide.value));
            TestNewVersionBehaviorFromClientSide.TEST_UTIL.getAdmin().flush(t.getName());
            r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(3));
            Assert.assertEquals(6, r.size());
            Assert.assertEquals(1500002, r.rawCells()[0].getTimestamp());
            Assert.assertEquals(1000002, r.rawCells()[1].getTimestamp());
            Assert.assertEquals(1000001, r.rawCells()[2].getTimestamp());
            Assert.assertEquals(1000002, r.rawCells()[3].getTimestamp());
            Assert.assertEquals(1500002, r.rawCells()[4].getTimestamp());
            Assert.assertEquals(1000001, r.rawCells()[5].getTimestamp());
        }
    }

    @Test
    public void testTimeRange() throws IOException {
        try (Table t = createTable()) {
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000001, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000002, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000004, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000005, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000006, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000007, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000008, TestNewVersionBehaviorFromClientSide.value));
            Result r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(3).setTimeRange(0, 1000005));
            Assert.assertEquals(0, r.size());
            TestNewVersionBehaviorFromClientSide.TEST_UTIL.getAdmin().flush(t.getName());
            r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(3).setTimeRange(0, 1000005));
            Assert.assertEquals(0, r.size());
        }
    }

    @Test
    public void testExplicitColum() throws IOException {
        try (Table t = createTable()) {
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col2, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col2, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col2, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col2, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col3, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col3, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col3, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col3, TestNewVersionBehaviorFromClientSide.value));
            Result r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(3).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col2));
            Assert.assertEquals(3, r.size());
            TestNewVersionBehaviorFromClientSide.TEST_UTIL.getAdmin().flush(t.getName());
            r = t.get(new Get(TestNewVersionBehaviorFromClientSide.ROW).setMaxVersions(3).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col2));
            Assert.assertEquals(3, r.size());
            TestNewVersionBehaviorFromClientSide.TEST_UTIL.getAdmin().flush(t.getName());
        }
    }

    @Test
    public void testgetColumnHint() throws IOException {
        try (Table t = createTable()) {
            t.setOperationTimeout(10000);
            t.setRpcTimeout(10000);
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 100, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 101, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 102, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 103, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 104, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW2).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 104, TestNewVersionBehaviorFromClientSide.value));
            TestNewVersionBehaviorFromClientSide.TEST_UTIL.getAdmin().flush(t.getName());
            t.delete(new Delete(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1));
        }
    }

    @Test
    public void testRawScanAndMajorCompaction() throws IOException {
        try (Table t = createTable()) {
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000001, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000002, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003, TestNewVersionBehaviorFromClientSide.value));
            t.put(new Put(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000004, TestNewVersionBehaviorFromClientSide.value));
            t.delete(new Delete(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000004));
            t.delete(new Delete(TestNewVersionBehaviorFromClientSide.ROW).addColumn(TestNewVersionBehaviorFromClientSide.FAMILY, TestNewVersionBehaviorFromClientSide.col1, 1000003));
            try (ResultScanner scannner = t.getScanner(new Scan().setRaw(true).setMaxVersions())) {
                Result r = scannner.next();
                Assert.assertNull(scannner.next());
                Assert.assertEquals(6, r.size());
            }
            TestNewVersionBehaviorFromClientSide.TEST_UTIL.getAdmin().flush(t.getName());
            try (ResultScanner scannner = t.getScanner(new Scan().setRaw(true).setMaxVersions())) {
                Result r = scannner.next();
                Assert.assertNull(scannner.next());
                Assert.assertEquals(6, r.size());
            }
            TestNewVersionBehaviorFromClientSide.TEST_UTIL.getAdmin().majorCompact(t.getName());
            Threads.sleep(5000);
            try (ResultScanner scannner = t.getScanner(new Scan().setRaw(true).setMaxVersions())) {
                Result r = scannner.next();
                Assert.assertNull(scannner.next());
                Assert.assertEquals(1, r.size());
                Assert.assertEquals(1000002, r.rawCells()[0].getTimestamp());
            }
        }
    }
}

