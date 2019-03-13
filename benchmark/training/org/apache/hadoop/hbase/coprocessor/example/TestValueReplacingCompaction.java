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
package org.apache.hadoop.hbase.coprocessor.example;


import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ CoprocessorTests.class, MediumTests.class })
public class TestValueReplacingCompaction {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestValueReplacingCompaction.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final TableName NAME = TableName.valueOf("TestValueReplacement");

    private static final byte[] FAMILY = Bytes.toBytes("f");

    private static final byte[] QUALIFIER = Bytes.toBytes("q");

    private static final ColumnFamilyDescriptor CFD = ColumnFamilyDescriptorBuilder.newBuilder(TestValueReplacingCompaction.FAMILY).build();

    private static final int NUM_ROWS = 5;

    private static final String value = "foo";

    private static final String replacedValue = "bar";

    @Test
    public void test() throws IOException, InterruptedException {
        try (Table t = TestValueReplacingCompaction.UTIL.getConnection().getTable(TestValueReplacingCompaction.NAME)) {
            writeData(t);
            // Flush the data
            TestValueReplacingCompaction.UTIL.flush(TestValueReplacingCompaction.NAME);
            // Issue a compaction
            TestValueReplacingCompaction.UTIL.compact(TestValueReplacingCompaction.NAME, true);
            Scan s = new Scan();
            s.addColumn(TestValueReplacingCompaction.FAMILY, TestValueReplacingCompaction.QUALIFIER);
            try (ResultScanner scanner = t.getScanner(s)) {
                for (int i = 0; i < (TestValueReplacingCompaction.NUM_ROWS); i++) {
                    Result result = scanner.next();
                    Assert.assertNotNull((("The " + (i + 1)) + "th result was unexpectedly null"), result);
                    Assert.assertEquals(1, result.getFamilyMap(TestValueReplacingCompaction.FAMILY).size());
                    Assert.assertArrayEquals(Bytes.toBytes((i + 1)), result.getRow());
                    Assert.assertArrayEquals(Bytes.toBytes(TestValueReplacingCompaction.replacedValue), result.getValue(TestValueReplacingCompaction.FAMILY, TestValueReplacingCompaction.QUALIFIER));
                }
                Assert.assertNull(scanner.next());
            }
        }
    }
}

