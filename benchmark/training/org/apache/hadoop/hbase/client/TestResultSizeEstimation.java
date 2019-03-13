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


import org.apache.hadoop.hbase.ArrayBackedTag;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Tag;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category(LargeTests.class)
public class TestResultSizeEstimation {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestResultSizeEstimation.class);

    static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    static final int TAG_DATA_SIZE = 2048;

    static final int SCANNER_DATA_LIMIT = (TestResultSizeEstimation.TAG_DATA_SIZE) + 256;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testResultSizeEstimation() throws Exception {
        byte[] ROW1 = Bytes.toBytes("testRow1");
        byte[] ROW2 = Bytes.toBytes("testRow2");
        byte[] FAMILY = Bytes.toBytes("testFamily");
        byte[] QUALIFIER = Bytes.toBytes("testQualifier");
        byte[] VALUE = Bytes.toBytes("testValue");
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] FAMILIES = new byte[][]{ FAMILY };
        Table table = TestResultSizeEstimation.TEST_UTIL.createTable(tableName, FAMILIES);
        Put p = new Put(ROW1);
        p.add(new KeyValue(ROW1, FAMILY, QUALIFIER, Long.MAX_VALUE, VALUE));
        table.put(p);
        p = new Put(ROW2);
        p.add(new KeyValue(ROW2, FAMILY, QUALIFIER, Long.MAX_VALUE, VALUE));
        table.put(p);
        Scan s = new Scan();
        s.setMaxResultSize(TestResultSizeEstimation.SCANNER_DATA_LIMIT);
        ResultScanner rs = table.getScanner(s);
        int count = 0;
        while ((rs.next()) != null) {
            count++;
        } 
        Assert.assertEquals("Result size estimation did not work properly", 2, count);
        rs.close();
        table.close();
    }

    @Test
    public void testResultSizeEstimationWithTags() throws Exception {
        byte[] ROW1 = Bytes.toBytes("testRow1");
        byte[] ROW2 = Bytes.toBytes("testRow2");
        byte[] FAMILY = Bytes.toBytes("testFamily");
        byte[] QUALIFIER = Bytes.toBytes("testQualifier");
        byte[] VALUE = Bytes.toBytes("testValue");
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] FAMILIES = new byte[][]{ FAMILY };
        Table table = TestResultSizeEstimation.TEST_UTIL.createTable(tableName, FAMILIES);
        Put p = new Put(ROW1);
        p.add(new KeyValue(ROW1, FAMILY, QUALIFIER, Long.MAX_VALUE, VALUE, new Tag[]{ new ArrayBackedTag(((byte) (1)), new byte[TestResultSizeEstimation.TAG_DATA_SIZE]) }));
        table.put(p);
        p = new Put(ROW2);
        p.add(new KeyValue(ROW2, FAMILY, QUALIFIER, Long.MAX_VALUE, VALUE, new Tag[]{ new ArrayBackedTag(((byte) (1)), new byte[TestResultSizeEstimation.TAG_DATA_SIZE]) }));
        table.put(p);
        Scan s = new Scan();
        s.setMaxResultSize(TestResultSizeEstimation.SCANNER_DATA_LIMIT);
        ResultScanner rs = table.getScanner(s);
        int count = 0;
        while ((rs.next()) != null) {
            count++;
        } 
        Assert.assertEquals("Result size estimation did not work properly", 2, count);
        rs.close();
        table.close();
    }
}

