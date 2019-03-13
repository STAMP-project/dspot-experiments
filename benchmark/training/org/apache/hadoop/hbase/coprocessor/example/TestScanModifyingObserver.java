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
public class TestScanModifyingObserver {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScanModifyingObserver.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final TableName NAME = TableName.valueOf("TestScanModifications");

    private static final byte[] FAMILY = Bytes.toBytes("f");

    private static final ColumnFamilyDescriptor CFD = ColumnFamilyDescriptorBuilder.newBuilder(TestScanModifyingObserver.FAMILY).build();

    private static final int NUM_ROWS = 5;

    private static final byte[] EXPLICIT_QUAL = Bytes.toBytes("our_qualifier");

    private static final byte[] IMPLICIT_QUAL = Bytes.toBytes("their_qualifier");

    private static final byte[] EXPLICIT_VAL = Bytes.toBytes("provided");

    private static final byte[] IMPLICIT_VAL = Bytes.toBytes("implicit");

    @Test
    public void test() throws IOException {
        try (Table t = TestScanModifyingObserver.UTIL.getConnection().getTable(TestScanModifyingObserver.NAME)) {
            writeData(t);
            Scan s = new Scan();
            s.addColumn(TestScanModifyingObserver.FAMILY, TestScanModifyingObserver.EXPLICIT_QUAL);
            try (ResultScanner scanner = t.getScanner(s)) {
                for (int i = 0; i < (TestScanModifyingObserver.NUM_ROWS); i++) {
                    Result result = scanner.next();
                    Assert.assertNotNull((("The " + (i + 1)) + "th result was unexpectedly null"), result);
                    Assert.assertEquals(2, result.getFamilyMap(TestScanModifyingObserver.FAMILY).size());
                    Assert.assertArrayEquals(Bytes.toBytes((i + 1)), result.getRow());
                    Assert.assertArrayEquals(TestScanModifyingObserver.EXPLICIT_VAL, result.getValue(TestScanModifyingObserver.FAMILY, TestScanModifyingObserver.EXPLICIT_QUAL));
                    Assert.assertArrayEquals(TestScanModifyingObserver.IMPLICIT_VAL, result.getValue(TestScanModifyingObserver.FAMILY, TestScanModifyingObserver.IMPLICIT_QUAL));
                }
                Assert.assertNull(scanner.next());
            }
        }
    }
}

