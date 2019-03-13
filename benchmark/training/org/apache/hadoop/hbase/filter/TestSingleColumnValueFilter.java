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


import Filter.ReturnCode;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.regex.Pattern;
import org.apache.hadoop.hbase.ByteBufferKeyValue;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.testclassification.FilterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Tests the value filter
 */
@Category({ FilterTests.class, SmallTests.class })
public class TestSingleColumnValueFilter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSingleColumnValueFilter.class);

    private static final byte[] ROW = Bytes.toBytes("test");

    private static final byte[] COLUMN_FAMILY = Bytes.toBytes("test");

    private static final byte[] COLUMN_QUALIFIER = Bytes.toBytes("foo");

    private static final byte[] VAL_1 = Bytes.toBytes("a");

    private static final byte[] VAL_2 = Bytes.toBytes("ab");

    private static final byte[] VAL_3 = Bytes.toBytes("abc");

    private static final byte[] VAL_4 = Bytes.toBytes("abcd");

    private static final byte[] FULLSTRING_1 = Bytes.toBytes("The quick brown fox jumps over the lazy dog.");

    private static final byte[] FULLSTRING_2 = Bytes.toBytes("The slow grey fox trips over the lazy dog.");

    private static final String QUICK_SUBSTR = "quick";

    private static final String QUICK_REGEX = ".+quick.+";

    private static final Pattern QUICK_PATTERN = Pattern.compile("QuIcK", ((Pattern.CASE_INSENSITIVE) | (Pattern.DOTALL)));

    Filter basicFilter;

    Filter nullFilter;

    Filter substrFilter;

    Filter regexFilter;

    Filter regexPatternFilter;

    @Test
    public void testLongComparator() throws IOException {
        Filter filter = new SingleColumnValueFilter(TestSingleColumnValueFilter.COLUMN_FAMILY, TestSingleColumnValueFilter.COLUMN_QUALIFIER, CompareOperator.GREATER, new LongComparator(100L));
        KeyValue cell = new KeyValue(TestSingleColumnValueFilter.ROW, TestSingleColumnValueFilter.COLUMN_FAMILY, TestSingleColumnValueFilter.COLUMN_QUALIFIER, Bytes.toBytes(1L));
        Assert.assertTrue("less than", ((filter.filterCell(cell)) == (ReturnCode.NEXT_ROW)));
        filter.reset();
        byte[] buffer = cell.getBuffer();
        Cell c = new ByteBufferKeyValue(ByteBuffer.wrap(buffer), 0, buffer.length);
        Assert.assertTrue("less than", ((filter.filterCell(c)) == (ReturnCode.NEXT_ROW)));
        filter.reset();
        cell = new KeyValue(TestSingleColumnValueFilter.ROW, TestSingleColumnValueFilter.COLUMN_FAMILY, TestSingleColumnValueFilter.COLUMN_QUALIFIER, Bytes.toBytes(100L));
        Assert.assertTrue("Equals 100", ((filter.filterCell(cell)) == (ReturnCode.NEXT_ROW)));
        filter.reset();
        buffer = cell.getBuffer();
        c = new ByteBufferKeyValue(ByteBuffer.wrap(buffer), 0, buffer.length);
        Assert.assertTrue("Equals 100", ((filter.filterCell(c)) == (ReturnCode.NEXT_ROW)));
        filter.reset();
        cell = new KeyValue(TestSingleColumnValueFilter.ROW, TestSingleColumnValueFilter.COLUMN_FAMILY, TestSingleColumnValueFilter.COLUMN_QUALIFIER, Bytes.toBytes(120L));
        Assert.assertTrue("include 120", ((filter.filterCell(cell)) == (ReturnCode.INCLUDE)));
        filter.reset();
        buffer = cell.getBuffer();
        c = new ByteBufferKeyValue(ByteBuffer.wrap(buffer), 0, buffer.length);
        Assert.assertTrue("include 120", ((filter.filterCell(c)) == (ReturnCode.INCLUDE)));
    }

    /**
     * Tests identification of the stop row
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testStop() throws Exception {
        basicFilterTests(((SingleColumnValueFilter) (basicFilter)));
        nullFilterTests(nullFilter);
        substrFilterTests(substrFilter);
        regexFilterTests(regexFilter);
        regexPatternFilterTests(regexPatternFilter);
    }

    /**
     * Tests serialization
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSerialization() throws Exception {
        Filter newFilter = serializationTest(basicFilter);
        basicFilterTests(((SingleColumnValueFilter) (newFilter)));
        newFilter = serializationTest(nullFilter);
        nullFilterTests(newFilter);
        newFilter = serializationTest(substrFilter);
        substrFilterTests(newFilter);
        newFilter = serializationTest(regexFilter);
        regexFilterTests(newFilter);
        newFilter = serializationTest(regexPatternFilter);
        regexPatternFilterTests(newFilter);
    }
}

