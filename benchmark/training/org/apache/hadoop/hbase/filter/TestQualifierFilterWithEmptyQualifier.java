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


import java.io.IOException;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.testclassification.FilterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test qualifierFilter with empty qualifier column
 */
@Category({ FilterTests.class, SmallTests.class })
public class TestQualifierFilterWithEmptyQualifier {
    private static final Logger LOG = LoggerFactory.getLogger(TestQualifierFilterWithEmptyQualifier.class);

    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestQualifierFilterWithEmptyQualifier.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private HRegion region;

    @Rule
    public TestName name = new TestName();

    private static final byte[][] ROWS = new byte[][]{ Bytes.toBytes("testRowOne-0"), Bytes.toBytes("testRowOne-1"), Bytes.toBytes("testRowOne-2"), Bytes.toBytes("testRowOne-3") };

    private static final byte[] FAMILY = Bytes.toBytes("testFamily");

    private static final byte[][] QUALIFIERS = new byte[][]{ HConstants.EMPTY_BYTE_ARRAY, Bytes.toBytes("testQualifier") };

    private static final byte[] VALUE = Bytes.toBytes("testValueOne");

    private long numRows = ((long) (TestQualifierFilterWithEmptyQualifier.ROWS.length));

    @Test
    public void testQualifierFilterWithEmptyColumn() throws IOException {
        long colsPerRow = 2;
        long expectedKeys = colsPerRow / 2;
        Filter f = new QualifierFilter(CompareOperator.EQUAL, new BinaryComparator(TestQualifierFilterWithEmptyQualifier.QUALIFIERS[0]));
        Scan s = new Scan();
        s.setFilter(f);
        verifyScanNoEarlyOut(s, this.numRows, expectedKeys);
        expectedKeys = colsPerRow / 2;
        f = new QualifierFilter(CompareOperator.EQUAL, new BinaryComparator(TestQualifierFilterWithEmptyQualifier.QUALIFIERS[1]));
        s = new Scan();
        s.setFilter(f);
        verifyScanNoEarlyOut(s, this.numRows, expectedKeys);
        expectedKeys = colsPerRow / 2;
        f = new QualifierFilter(CompareOperator.GREATER, new BinaryComparator(TestQualifierFilterWithEmptyQualifier.QUALIFIERS[0]));
        s = new Scan();
        s.setFilter(f);
        verifyScanNoEarlyOut(s, this.numRows, expectedKeys);
        expectedKeys = colsPerRow;
        f = new QualifierFilter(CompareOperator.GREATER_OR_EQUAL, new BinaryComparator(TestQualifierFilterWithEmptyQualifier.QUALIFIERS[0]));
        s = new Scan();
        s.setFilter(f);
        verifyScanNoEarlyOut(s, this.numRows, expectedKeys);
    }
}

