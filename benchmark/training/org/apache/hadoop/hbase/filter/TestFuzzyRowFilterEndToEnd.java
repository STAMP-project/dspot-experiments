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
package org.apache.hadoop.hbase.filter;


import Durability.SKIP_WAL;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.FilterTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ FilterTests.class, LargeTests.class })
public class TestFuzzyRowFilterEndToEnd {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFuzzyRowFilterEndToEnd.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final byte fuzzyValue = ((byte) (63));

    private static final Logger LOG = LoggerFactory.getLogger(TestFuzzyRowFilterEndToEnd.class);

    private static int firstPartCardinality = 50;

    private static int secondPartCardinality = 50;

    private static int thirdPartCardinality = 50;

    private static int colQualifiersTotal = 5;

    private static int totalFuzzyKeys = (TestFuzzyRowFilterEndToEnd.thirdPartCardinality) / 2;

    private static String table = "TestFuzzyRowFilterEndToEnd";

    @Rule
    public TestName name = new TestName();

    // HBASE-15676 Test that fuzzy info of all fixed bits (0s) finds matching row.
    @Test
    public void testAllFixedBits() throws IOException {
        String cf = "f";
        String cq = "q";
        Table ht = TestFuzzyRowFilterEndToEnd.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), Bytes.toBytes(cf), Integer.MAX_VALUE);
        // Load data
        String[] rows = new String[]{ "\\x9C\\x00\\x044\\x00\\x00\\x00\\x00", "\\x9C\\x00\\x044\\x01\\x00\\x00\\x00", "\\x9C\\x00\\x044\\x00\\x01\\x00\\x00", "\\x9B\\x00\\x044e\\x9B\\x02\\xBB", "\\x9C\\x00\\x044\\x00\\x00\\x01\\x00", "\\x9C\\x00\\x044\\x00\\x01\\x00\\x01", "\\x9B\\x00\\x044e\\xBB\\xB2\\xBB" };
        for (int i = 0; i < (rows.length); i++) {
            Put p = new Put(Bytes.toBytesBinary(rows[i]));
            p.addColumn(Bytes.toBytes(cf), Bytes.toBytes(cq), Bytes.toBytes("value"));
            ht.put(p);
        }
        TestFuzzyRowFilterEndToEnd.TEST_UTIL.flush();
        List<Pair<byte[], byte[]>> data = new ArrayList<>();
        byte[] fuzzyKey = Bytes.toBytesBinary("\\x9B\\x00\\x044e");
        byte[] mask = new byte[]{ 0, 0, 0, 0, 0 };
        // copy the fuzzy key and mask to test HBASE-18617
        byte[] copyFuzzyKey = Arrays.copyOf(fuzzyKey, fuzzyKey.length);
        byte[] copyMask = Arrays.copyOf(mask, mask.length);
        data.add(new Pair(fuzzyKey, mask));
        FuzzyRowFilter filter = new FuzzyRowFilter(data);
        Scan scan = new Scan();
        scan.setFilter(filter);
        ResultScanner scanner = ht.getScanner(scan);
        int total = 0;
        while ((scanner.next()) != null) {
            total++;
        } 
        Assert.assertEquals(2, total);
        Assert.assertEquals(true, Arrays.equals(copyFuzzyKey, fuzzyKey));
        Assert.assertEquals(true, Arrays.equals(copyMask, mask));
        TestFuzzyRowFilterEndToEnd.TEST_UTIL.deleteTable(TableName.valueOf(name.getMethodName()));
    }

    @Test
    public void testHBASE14782() throws IOException {
        String cf = "f";
        String cq = "q";
        Table ht = TestFuzzyRowFilterEndToEnd.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), Bytes.toBytes(cf), Integer.MAX_VALUE);
        // Load data
        String[] rows = new String[]{ "\\x9C\\x00\\x044\\x00\\x00\\x00\\x00", "\\x9C\\x00\\x044\\x01\\x00\\x00\\x00", "\\x9C\\x00\\x044\\x00\\x01\\x00\\x00", "\\x9C\\x00\\x044\\x00\\x00\\x01\\x00", "\\x9C\\x00\\x044\\x00\\x01\\x00\\x01", "\\x9B\\x00\\x044e\\xBB\\xB2\\xBB" };
        String badRow = "\\x9C\\x00\\x03\\xE9e\\xBB{X\\x1Fwts\\x1F\\x15vRX";
        for (int i = 0; i < (rows.length); i++) {
            Put p = new Put(Bytes.toBytesBinary(rows[i]));
            p.addColumn(Bytes.toBytes(cf), Bytes.toBytes(cq), Bytes.toBytes("value"));
            ht.put(p);
        }
        Put p = new Put(Bytes.toBytesBinary(badRow));
        p.addColumn(Bytes.toBytes(cf), Bytes.toBytes(cq), Bytes.toBytes("value"));
        ht.put(p);
        TestFuzzyRowFilterEndToEnd.TEST_UTIL.flush();
        List<Pair<byte[], byte[]>> data = new ArrayList<>();
        byte[] fuzzyKey = Bytes.toBytesBinary("\\x00\\x00\\x044");
        byte[] mask = new byte[]{ 1, 0, 0, 0 };
        data.add(new Pair(fuzzyKey, mask));
        FuzzyRowFilter filter = new FuzzyRowFilter(data);
        Scan scan = new Scan();
        scan.setFilter(filter);
        ResultScanner scanner = ht.getScanner(scan);
        int total = 0;
        while ((scanner.next()) != null) {
            total++;
        } 
        Assert.assertEquals(rows.length, total);
        TestFuzzyRowFilterEndToEnd.TEST_UTIL.deleteTable(TableName.valueOf(name.getMethodName()));
    }

    @Test
    public void testEndToEnd() throws Exception {
        String cf = "f";
        Table ht = TestFuzzyRowFilterEndToEnd.TEST_UTIL.createTable(TableName.valueOf(TestFuzzyRowFilterEndToEnd.table), Bytes.toBytes(cf), Integer.MAX_VALUE);
        // 10 byte row key - (2 bytes 4 bytes 4 bytes)
        // 4 byte qualifier
        // 4 byte value
        for (int i0 = 0; i0 < (TestFuzzyRowFilterEndToEnd.firstPartCardinality); i0++) {
            for (int i1 = 0; i1 < (TestFuzzyRowFilterEndToEnd.secondPartCardinality); i1++) {
                for (int i2 = 0; i2 < (TestFuzzyRowFilterEndToEnd.thirdPartCardinality); i2++) {
                    byte[] rk = new byte[10];
                    ByteBuffer buf = ByteBuffer.wrap(rk);
                    buf.clear();
                    buf.putShort(((short) (i0)));
                    buf.putInt(i1);
                    buf.putInt(i2);
                    for (int c = 0; c < (TestFuzzyRowFilterEndToEnd.colQualifiersTotal); c++) {
                        byte[] cq = new byte[4];
                        Bytes.putBytes(cq, 0, Bytes.toBytes(c), 0, 4);
                        Put p = new Put(rk);
                        p.setDurability(SKIP_WAL);
                        p.addColumn(Bytes.toBytes(cf), cq, Bytes.toBytes(c));
                        ht.put(p);
                    }
                }
            }
        }
        TestFuzzyRowFilterEndToEnd.TEST_UTIL.flush();
        // test passes
        runTest1(ht);
        runTest2(ht);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testFilterList() throws Exception {
        String cf = "f";
        Table ht = TestFuzzyRowFilterEndToEnd.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), Bytes.toBytes(cf), Integer.MAX_VALUE);
        // 10 byte row key - (2 bytes 4 bytes 4 bytes)
        // 4 byte qualifier
        // 4 byte value
        for (int i1 = 0; i1 < 5; i1++) {
            for (int i2 = 0; i2 < 5; i2++) {
                byte[] rk = new byte[10];
                ByteBuffer buf = ByteBuffer.wrap(rk);
                buf.clear();
                buf.putShort(((short) (2)));
                buf.putInt(i1);
                buf.putInt(i2);
                // Each row contains 5 columns
                for (int c = 0; c < 5; c++) {
                    byte[] cq = new byte[4];
                    Bytes.putBytes(cq, 0, Bytes.toBytes(c), 0, 4);
                    Put p = new Put(rk);
                    p.setDurability(SKIP_WAL);
                    p.addColumn(Bytes.toBytes(cf), cq, Bytes.toBytes(c));
                    ht.put(p);
                    TestFuzzyRowFilterEndToEnd.LOG.info(((("Inserting: rk: " + (Bytes.toStringBinary(rk))) + " cq: ") + (Bytes.toStringBinary(cq))));
                }
            }
        }
        TestFuzzyRowFilterEndToEnd.TEST_UTIL.flush();
        // test passes if we get back 5 KV's (1 row)
        runTest(ht, 5);
    }
}

