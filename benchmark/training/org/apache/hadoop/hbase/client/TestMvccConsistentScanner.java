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


import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ LargeTests.class, ClientTests.class })
public class TestMvccConsistentScanner {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMvccConsistentScanner.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static Connection CONN;

    private static final byte[] CF = Bytes.toBytes("cf");

    private static final byte[] CQ1 = Bytes.toBytes("cq1");

    private static final byte[] CQ2 = Bytes.toBytes("cq2");

    private static final byte[] CQ3 = Bytes.toBytes("cq3");

    @Rule
    public TestName testName = new TestName();

    private TableName tableName;

    @Test
    public void testRowAtomic() throws IOException, InterruptedException {
        byte[] row = Bytes.toBytes("row");
        put(row, TestMvccConsistentScanner.CQ1, Bytes.toBytes(1));
        put(row, TestMvccConsistentScanner.CQ2, Bytes.toBytes(2));
        try (Table table = TestMvccConsistentScanner.CONN.getTable(tableName);ResultScanner scanner = table.getScanner(new Scan().setBatch(1).setCaching(1))) {
            Result result = scanner.next();
            Assert.assertEquals(1, result.rawCells().length);
            Assert.assertEquals(1, Bytes.toInt(result.getValue(TestMvccConsistentScanner.CF, TestMvccConsistentScanner.CQ1)));
            move();
            put(row, TestMvccConsistentScanner.CQ3, Bytes.toBytes(3));
            result = scanner.next();
            Assert.assertEquals(1, result.rawCells().length);
            Assert.assertEquals(2, Bytes.toInt(result.getValue(TestMvccConsistentScanner.CF, TestMvccConsistentScanner.CQ2)));
            Assert.assertNull(scanner.next());
        }
    }

    @Test
    public void testCrossRowAtomicInRegion() throws IOException, InterruptedException {
        put(Bytes.toBytes("row1"), TestMvccConsistentScanner.CQ1, Bytes.toBytes(1));
        put(Bytes.toBytes("row2"), TestMvccConsistentScanner.CQ1, Bytes.toBytes(2));
        try (Table table = TestMvccConsistentScanner.CONN.getTable(tableName);ResultScanner scanner = table.getScanner(new Scan().setCaching(1))) {
            Result result = scanner.next();
            Assert.assertArrayEquals(Bytes.toBytes("row1"), result.getRow());
            Assert.assertEquals(1, Bytes.toInt(result.getValue(TestMvccConsistentScanner.CF, TestMvccConsistentScanner.CQ1)));
            move();
            put(Bytes.toBytes("row3"), TestMvccConsistentScanner.CQ1, Bytes.toBytes(3));
            result = scanner.next();
            Assert.assertArrayEquals(Bytes.toBytes("row2"), result.getRow());
            Assert.assertEquals(2, Bytes.toInt(result.getValue(TestMvccConsistentScanner.CF, TestMvccConsistentScanner.CQ1)));
            Assert.assertNull(scanner.next());
        }
    }
}

