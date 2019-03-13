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


import CellComparatorImpl.COMPARATOR;
import HConstants.LATEST_TIMESTAMP;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.ColumnCountGetFilter;
import org.apache.hadoop.hbase.filter.ColumnPaginationFilter;
import org.apache.hadoop.hbase.filter.ColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.ColumnRangeFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.DependentColumnFilter;
import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.filter.InclusiveStopFilter;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.filter.MultipleColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueExcludeFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.SkipFilter;
import org.apache.hadoop.hbase.filter.TimestampsFilter;
import org.apache.hadoop.hbase.filter.ValueFilter;
import org.apache.hadoop.hbase.filter.WhileMatchFilter;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.BuilderStyleTest;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.GsonUtil;
import org.apache.hbase.thirdparty.com.google.gson.Gson;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Run tests that use the functionality of the Operation superclass for
 * Puts, Gets, Deletes, Scans, and MultiPuts.
 */
@Category({ ClientTests.class, SmallTests.class })
public class TestOperation {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestOperation.class);

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    private static byte[] VALUE = Bytes.toBytes("testValue");

    private static Gson GSON = GsonUtil.createGson().create();

    private static List<Long> TS_LIST = Arrays.asList(2L, 3L, 5L);

    private static TimestampsFilter TS_FILTER = new TimestampsFilter(TestOperation.TS_LIST);

    private static String STR_TS_FILTER = (TestOperation.TS_FILTER.getClass().getSimpleName()) + " (3/3): [2, 3, 5]";

    private static List<Long> L_TS_LIST = Arrays.asList(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

    private static TimestampsFilter L_TS_FILTER = new TimestampsFilter(TestOperation.L_TS_LIST);

    private static String STR_L_TS_FILTER = (TestOperation.L_TS_FILTER.getClass().getSimpleName()) + " (5/11): [0, 1, 2, 3, 4]";

    private static String COL_NAME_1 = "col1";

    private static ColumnPrefixFilter COL_PRE_FILTER = new ColumnPrefixFilter(Bytes.toBytes(TestOperation.COL_NAME_1));

    private static String STR_COL_PRE_FILTER = ((TestOperation.COL_PRE_FILTER.getClass().getSimpleName()) + " ") + (TestOperation.COL_NAME_1);

    private static String COL_NAME_2 = "col2";

    private static ColumnRangeFilter CR_FILTER = new ColumnRangeFilter(Bytes.toBytes(TestOperation.COL_NAME_1), true, Bytes.toBytes(TestOperation.COL_NAME_2), false);

    private static String STR_CR_FILTER = (((((TestOperation.CR_FILTER.getClass().getSimpleName()) + " [") + (TestOperation.COL_NAME_1)) + ", ") + (TestOperation.COL_NAME_2)) + ")";

    private static int COL_COUNT = 9;

    private static ColumnCountGetFilter CCG_FILTER = new ColumnCountGetFilter(TestOperation.COL_COUNT);

    private static String STR_CCG_FILTER = ((TestOperation.CCG_FILTER.getClass().getSimpleName()) + " ") + (TestOperation.COL_COUNT);

    private static int LIMIT = 3;

    private static int OFFSET = 4;

    private static ColumnPaginationFilter CP_FILTER = new ColumnPaginationFilter(TestOperation.LIMIT, TestOperation.OFFSET);

    private static String STR_CP_FILTER = (((((TestOperation.CP_FILTER.getClass().getSimpleName()) + " (") + (TestOperation.LIMIT)) + ", ") + (TestOperation.OFFSET)) + ")";

    private static String STOP_ROW_KEY = "stop";

    private static InclusiveStopFilter IS_FILTER = new InclusiveStopFilter(Bytes.toBytes(TestOperation.STOP_ROW_KEY));

    private static String STR_IS_FILTER = ((TestOperation.IS_FILTER.getClass().getSimpleName()) + " ") + (TestOperation.STOP_ROW_KEY);

    private static String PREFIX = "prefix";

    private static PrefixFilter PREFIX_FILTER = new PrefixFilter(Bytes.toBytes(TestOperation.PREFIX));

    private static String STR_PREFIX_FILTER = "PrefixFilter " + (TestOperation.PREFIX);

    private static byte[][] PREFIXES = new byte[][]{ Bytes.toBytes("0"), Bytes.toBytes("1"), Bytes.toBytes("2") };

    private static MultipleColumnPrefixFilter MCP_FILTER = new MultipleColumnPrefixFilter(TestOperation.PREFIXES);

    private static String STR_MCP_FILTER = (TestOperation.MCP_FILTER.getClass().getSimpleName()) + " (3/3): [0, 1, 2]";

    private static byte[][] L_PREFIXES = new byte[][]{ Bytes.toBytes("0"), Bytes.toBytes("1"), Bytes.toBytes("2"), Bytes.toBytes("3"), Bytes.toBytes("4"), Bytes.toBytes("5"), Bytes.toBytes("6"), Bytes.toBytes("7") };

    private static MultipleColumnPrefixFilter L_MCP_FILTER = new MultipleColumnPrefixFilter(TestOperation.L_PREFIXES);

    private static String STR_L_MCP_FILTER = (TestOperation.L_MCP_FILTER.getClass().getSimpleName()) + " (5/8): [0, 1, 2, 3, 4]";

    private static int PAGE_SIZE = 9;

    private static PageFilter PAGE_FILTER = new PageFilter(TestOperation.PAGE_SIZE);

    private static String STR_PAGE_FILTER = ((TestOperation.PAGE_FILTER.getClass().getSimpleName()) + " ") + (TestOperation.PAGE_SIZE);

    private static SkipFilter SKIP_FILTER = new SkipFilter(TestOperation.L_TS_FILTER);

    private static String STR_SKIP_FILTER = ((TestOperation.SKIP_FILTER.getClass().getSimpleName()) + " ") + (TestOperation.STR_L_TS_FILTER);

    private static WhileMatchFilter WHILE_FILTER = new WhileMatchFilter(TestOperation.L_TS_FILTER);

    private static String STR_WHILE_FILTER = ((TestOperation.WHILE_FILTER.getClass().getSimpleName()) + " ") + (TestOperation.STR_L_TS_FILTER);

    private static KeyOnlyFilter KEY_ONLY_FILTER = new KeyOnlyFilter();

    private static String STR_KEY_ONLY_FILTER = TestOperation.KEY_ONLY_FILTER.getClass().getSimpleName();

    private static FirstKeyOnlyFilter FIRST_KEY_ONLY_FILTER = new FirstKeyOnlyFilter();

    private static String STR_FIRST_KEY_ONLY_FILTER = TestOperation.FIRST_KEY_ONLY_FILTER.getClass().getSimpleName();

    private static CompareOp CMP_OP = CompareOp.EQUAL;

    private static byte[] CMP_VALUE = Bytes.toBytes("value");

    private static BinaryComparator BC = new BinaryComparator(TestOperation.CMP_VALUE);

    private static DependentColumnFilter DC_FILTER = new DependentColumnFilter(TestOperation.FAMILY, TestOperation.QUALIFIER, true, TestOperation.CMP_OP, TestOperation.BC);

    private static String STR_DC_FILTER = String.format("%s (%s, %s, %s, %s, %s)", TestOperation.DC_FILTER.getClass().getSimpleName(), Bytes.toStringBinary(TestOperation.FAMILY), Bytes.toStringBinary(TestOperation.QUALIFIER), true, TestOperation.CMP_OP.name(), Bytes.toStringBinary(TestOperation.BC.getValue()));

    private static FamilyFilter FAMILY_FILTER = new FamilyFilter(TestOperation.CMP_OP, TestOperation.BC);

    private static String STR_FAMILY_FILTER = (TestOperation.FAMILY_FILTER.getClass().getSimpleName()) + " (EQUAL, value)";

    private static QualifierFilter QUALIFIER_FILTER = new QualifierFilter(TestOperation.CMP_OP, TestOperation.BC);

    private static String STR_QUALIFIER_FILTER = (TestOperation.QUALIFIER_FILTER.getClass().getSimpleName()) + " (EQUAL, value)";

    private static RowFilter ROW_FILTER = new RowFilter(TestOperation.CMP_OP, TestOperation.BC);

    private static String STR_ROW_FILTER = (TestOperation.ROW_FILTER.getClass().getSimpleName()) + " (EQUAL, value)";

    private static ValueFilter VALUE_FILTER = new ValueFilter(TestOperation.CMP_OP, TestOperation.BC);

    private static String STR_VALUE_FILTER = (TestOperation.VALUE_FILTER.getClass().getSimpleName()) + " (EQUAL, value)";

    private static SingleColumnValueFilter SCV_FILTER = new SingleColumnValueFilter(TestOperation.FAMILY, TestOperation.QUALIFIER, TestOperation.CMP_OP, TestOperation.CMP_VALUE);

    private static String STR_SCV_FILTER = String.format("%s (%s, %s, %s, %s)", TestOperation.SCV_FILTER.getClass().getSimpleName(), Bytes.toStringBinary(TestOperation.FAMILY), Bytes.toStringBinary(TestOperation.QUALIFIER), TestOperation.CMP_OP.name(), Bytes.toStringBinary(TestOperation.CMP_VALUE));

    private static SingleColumnValueExcludeFilter SCVE_FILTER = new SingleColumnValueExcludeFilter(TestOperation.FAMILY, TestOperation.QUALIFIER, TestOperation.CMP_OP, TestOperation.CMP_VALUE);

    private static String STR_SCVE_FILTER = String.format("%s (%s, %s, %s, %s)", TestOperation.SCVE_FILTER.getClass().getSimpleName(), Bytes.toStringBinary(TestOperation.FAMILY), Bytes.toStringBinary(TestOperation.QUALIFIER), TestOperation.CMP_OP.name(), Bytes.toStringBinary(TestOperation.CMP_VALUE));

    private static FilterList AND_FILTER_LIST = new FilterList(Operator.MUST_PASS_ALL, Arrays.asList(((Filter) (TestOperation.TS_FILTER)), TestOperation.L_TS_FILTER, TestOperation.CR_FILTER));

    private static String STR_AND_FILTER_LIST = String.format("%s AND (3/3): [%s, %s, %s]", TestOperation.AND_FILTER_LIST.getClass().getSimpleName(), TestOperation.STR_TS_FILTER, TestOperation.STR_L_TS_FILTER, TestOperation.STR_CR_FILTER);

    private static FilterList OR_FILTER_LIST = new FilterList(Operator.MUST_PASS_ONE, Arrays.asList(((Filter) (TestOperation.TS_FILTER)), TestOperation.L_TS_FILTER, TestOperation.CR_FILTER));

    private static String STR_OR_FILTER_LIST = String.format("%s OR (3/3): [%s, %s, %s]", TestOperation.AND_FILTER_LIST.getClass().getSimpleName(), TestOperation.STR_TS_FILTER, TestOperation.STR_L_TS_FILTER, TestOperation.STR_CR_FILTER);

    private static FilterList L_FILTER_LIST = new FilterList(Arrays.asList(((Filter) (TestOperation.TS_FILTER)), TestOperation.L_TS_FILTER, TestOperation.CR_FILTER, TestOperation.COL_PRE_FILTER, TestOperation.CCG_FILTER, TestOperation.CP_FILTER, TestOperation.PREFIX_FILTER, TestOperation.PAGE_FILTER));

    private static String STR_L_FILTER_LIST = String.format("%s AND (5/8): [%s, %s, %s, %s, %s, %s]", TestOperation.L_FILTER_LIST.getClass().getSimpleName(), TestOperation.STR_TS_FILTER, TestOperation.STR_L_TS_FILTER, TestOperation.STR_CR_FILTER, TestOperation.STR_COL_PRE_FILTER, TestOperation.STR_CCG_FILTER, TestOperation.STR_CP_FILTER);

    private static Filter[] FILTERS = new Filter[]{ TestOperation.TS_FILTER, // TimestampsFilter
    TestOperation.L_TS_FILTER, // TimestampsFilter
    TestOperation.COL_PRE_FILTER, // ColumnPrefixFilter
    TestOperation.CP_FILTER, // ColumnPaginationFilter
    TestOperation.CR_FILTER, // ColumnRangeFilter
    TestOperation.CCG_FILTER, // ColumnCountGetFilter
    TestOperation.IS_FILTER, // InclusiveStopFilter
    TestOperation.PREFIX_FILTER, // PrefixFilter
    TestOperation.PAGE_FILTER, // PageFilter
    TestOperation.SKIP_FILTER, // SkipFilter
    TestOperation.WHILE_FILTER, // WhileMatchFilter
    TestOperation.KEY_ONLY_FILTER, // KeyOnlyFilter
    TestOperation.FIRST_KEY_ONLY_FILTER// FirstKeyOnlyFilter
    , TestOperation.MCP_FILTER, // MultipleColumnPrefixFilter
    TestOperation.L_MCP_FILTER, // MultipleColumnPrefixFilter
    TestOperation.DC_FILTER, // DependentColumnFilter
    TestOperation.FAMILY_FILTER, // FamilyFilter
    TestOperation.QUALIFIER_FILTER, // QualifierFilter
    TestOperation.ROW_FILTER, // RowFilter
    TestOperation.VALUE_FILTER, // ValueFilter
    TestOperation.SCV_FILTER, // SingleColumnValueFilter
    TestOperation.SCVE_FILTER, // SingleColumnValueExcludeFilter
    TestOperation.AND_FILTER_LIST, // FilterList
    TestOperation.OR_FILTER_LIST, // FilterList
    TestOperation.L_FILTER_LIST// FilterList
     };

    private static String[] FILTERS_INFO = new String[]{ TestOperation.STR_TS_FILTER, // TimestampsFilter
    TestOperation.STR_L_TS_FILTER, // TimestampsFilter
    TestOperation.STR_COL_PRE_FILTER, // ColumnPrefixFilter
    TestOperation.STR_CP_FILTER, // ColumnPaginationFilter
    TestOperation.STR_CR_FILTER, // ColumnRangeFilter
    TestOperation.STR_CCG_FILTER, // ColumnCountGetFilter
    TestOperation.STR_IS_FILTER, // InclusiveStopFilter
    TestOperation.STR_PREFIX_FILTER, // PrefixFilter
    TestOperation.STR_PAGE_FILTER, // PageFilter
    TestOperation.STR_SKIP_FILTER, // SkipFilter
    TestOperation.STR_WHILE_FILTER, // WhileMatchFilter
    TestOperation.STR_KEY_ONLY_FILTER, // KeyOnlyFilter
    TestOperation.STR_FIRST_KEY_ONLY_FILTER// FirstKeyOnlyFilter
    , TestOperation.STR_MCP_FILTER, // MultipleColumnPrefixFilter
    TestOperation.STR_L_MCP_FILTER, // MultipleColumnPrefixFilter
    TestOperation.STR_DC_FILTER, // DependentColumnFilter
    TestOperation.STR_FAMILY_FILTER, // FamilyFilter
    TestOperation.STR_QUALIFIER_FILTER, // QualifierFilter
    TestOperation.STR_ROW_FILTER, // RowFilter
    TestOperation.STR_VALUE_FILTER, // ValueFilter
    TestOperation.STR_SCV_FILTER, // SingleColumnValueFilter
    TestOperation.STR_SCVE_FILTER, // SingleColumnValueExcludeFilter
    TestOperation.STR_AND_FILTER_LIST, // FilterList
    TestOperation.STR_OR_FILTER_LIST, // FilterList
    TestOperation.STR_L_FILTER_LIST// FilterList
     };

    static {
        Assert.assertEquals(("The sizes of static arrays do not match: " + "[FILTERS: %d <=> FILTERS_INFO: %d]"), TestOperation.FILTERS.length, TestOperation.FILTERS_INFO.length);
    }

    /**
     * Test the client Operations' JSON encoding to ensure that produced JSON is
     * parseable and that the details are present and not corrupted.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testOperationJSON() throws IOException {
        // produce a Scan Operation
        Scan scan = new Scan(TestOperation.ROW);
        scan.addColumn(TestOperation.FAMILY, TestOperation.QUALIFIER);
        // get its JSON representation, and parse it
        String json = scan.toJSON();
        Type typeOfHashMap = getType();
        Map<String, Object> parsedJSON = TestOperation.GSON.fromJson(json, typeOfHashMap);
        // check for the row
        Assert.assertEquals("startRow incorrect in Scan.toJSON()", Bytes.toStringBinary(TestOperation.ROW), parsedJSON.get("startRow"));
        // check for the family and the qualifier.
        List familyInfo = ((List) (((Map) (parsedJSON.get("families"))).get(Bytes.toStringBinary(TestOperation.FAMILY))));
        Assert.assertNotNull("Family absent in Scan.toJSON()", familyInfo);
        Assert.assertEquals("Qualifier absent in Scan.toJSON()", 1, familyInfo.size());
        Assert.assertEquals("Qualifier incorrect in Scan.toJSON()", Bytes.toStringBinary(TestOperation.QUALIFIER), familyInfo.get(0));
        // produce a Get Operation
        Get get = new Get(TestOperation.ROW);
        get.addColumn(TestOperation.FAMILY, TestOperation.QUALIFIER);
        // get its JSON representation, and parse it
        json = get.toJSON();
        parsedJSON = TestOperation.GSON.fromJson(json, typeOfHashMap);
        // check for the row
        Assert.assertEquals("row incorrect in Get.toJSON()", Bytes.toStringBinary(TestOperation.ROW), parsedJSON.get("row"));
        // check for the family and the qualifier.
        familyInfo = ((List) (((Map) (parsedJSON.get("families"))).get(Bytes.toStringBinary(TestOperation.FAMILY))));
        Assert.assertNotNull("Family absent in Get.toJSON()", familyInfo);
        Assert.assertEquals("Qualifier absent in Get.toJSON()", 1, familyInfo.size());
        Assert.assertEquals("Qualifier incorrect in Get.toJSON()", Bytes.toStringBinary(TestOperation.QUALIFIER), familyInfo.get(0));
        // produce a Put operation
        Put put = new Put(TestOperation.ROW);
        put.addColumn(TestOperation.FAMILY, TestOperation.QUALIFIER, TestOperation.VALUE);
        // get its JSON representation, and parse it
        json = put.toJSON();
        parsedJSON = TestOperation.GSON.fromJson(json, typeOfHashMap);
        // check for the row
        Assert.assertEquals("row absent in Put.toJSON()", Bytes.toStringBinary(TestOperation.ROW), parsedJSON.get("row"));
        // check for the family and the qualifier.
        familyInfo = ((List) (((Map) (parsedJSON.get("families"))).get(Bytes.toStringBinary(TestOperation.FAMILY))));
        Assert.assertNotNull("Family absent in Put.toJSON()", familyInfo);
        Assert.assertEquals("KeyValue absent in Put.toJSON()", 1, familyInfo.size());
        Map kvMap = ((Map) (familyInfo.get(0)));
        Assert.assertEquals("Qualifier incorrect in Put.toJSON()", Bytes.toStringBinary(TestOperation.QUALIFIER), kvMap.get("qualifier"));
        Assert.assertEquals("Value length incorrect in Put.toJSON()", TestOperation.VALUE.length, ((Number) (kvMap.get("vlen"))).intValue());
        // produce a Delete operation
        Delete delete = new Delete(TestOperation.ROW);
        delete.addColumn(TestOperation.FAMILY, TestOperation.QUALIFIER);
        // get its JSON representation, and parse it
        json = delete.toJSON();
        parsedJSON = TestOperation.GSON.fromJson(json, typeOfHashMap);
        // check for the row
        Assert.assertEquals("row absent in Delete.toJSON()", Bytes.toStringBinary(TestOperation.ROW), parsedJSON.get("row"));
        // check for the family and the qualifier.
        familyInfo = ((List) (((Map) (parsedJSON.get("families"))).get(Bytes.toStringBinary(TestOperation.FAMILY))));
        Assert.assertNotNull("Family absent in Delete.toJSON()", familyInfo);
        Assert.assertEquals("KeyValue absent in Delete.toJSON()", 1, familyInfo.size());
        kvMap = ((Map) (familyInfo.get(0)));
        Assert.assertEquals("Qualifier incorrect in Delete.toJSON()", Bytes.toStringBinary(TestOperation.QUALIFIER), kvMap.get("qualifier"));
    }

    @Test
    public void testPutCreationWithByteBuffer() {
        Put p = new Put(TestOperation.ROW);
        List<Cell> c = p.get(TestOperation.FAMILY, TestOperation.QUALIFIER);
        Assert.assertEquals(0, c.size());
        Assert.assertEquals(LATEST_TIMESTAMP, p.getTimestamp());
        p.addColumn(TestOperation.FAMILY, ByteBuffer.wrap(TestOperation.QUALIFIER), 1984L, ByteBuffer.wrap(TestOperation.VALUE));
        c = p.get(TestOperation.FAMILY, TestOperation.QUALIFIER);
        Assert.assertEquals(1, c.size());
        Assert.assertEquals(1984L, c.get(0).getTimestamp());
        Assert.assertArrayEquals(TestOperation.VALUE, CellUtil.cloneValue(c.get(0)));
        Assert.assertEquals(LATEST_TIMESTAMP, p.getTimestamp());
        Assert.assertEquals(0, COMPARATOR.compare(c.get(0), new org.apache.hadoop.hbase.KeyValue(c.get(0))));
        p = new Put(TestOperation.ROW);
        p.addColumn(TestOperation.FAMILY, ByteBuffer.wrap(TestOperation.QUALIFIER), 2013L, null);
        c = p.get(TestOperation.FAMILY, TestOperation.QUALIFIER);
        Assert.assertEquals(1, c.size());
        Assert.assertEquals(2013L, c.get(0).getTimestamp());
        Assert.assertArrayEquals(new byte[]{  }, CellUtil.cloneValue(c.get(0)));
        Assert.assertEquals(LATEST_TIMESTAMP, p.getTimestamp());
        Assert.assertEquals(0, COMPARATOR.compare(c.get(0), new org.apache.hadoop.hbase.KeyValue(c.get(0))));
        p = new Put(ByteBuffer.wrap(TestOperation.ROW));
        p.addColumn(TestOperation.FAMILY, ByteBuffer.wrap(TestOperation.QUALIFIER), 2001L, null);
        c = p.get(TestOperation.FAMILY, TestOperation.QUALIFIER);
        Assert.assertEquals(1, c.size());
        Assert.assertEquals(2001L, c.get(0).getTimestamp());
        Assert.assertArrayEquals(new byte[]{  }, CellUtil.cloneValue(c.get(0)));
        Assert.assertArrayEquals(TestOperation.ROW, CellUtil.cloneRow(c.get(0)));
        Assert.assertEquals(LATEST_TIMESTAMP, p.getTimestamp());
        Assert.assertEquals(0, COMPARATOR.compare(c.get(0), new org.apache.hadoop.hbase.KeyValue(c.get(0))));
        p = new Put(ByteBuffer.wrap(TestOperation.ROW), 1970L);
        p.addColumn(TestOperation.FAMILY, ByteBuffer.wrap(TestOperation.QUALIFIER), 2001L, null);
        c = p.get(TestOperation.FAMILY, TestOperation.QUALIFIER);
        Assert.assertEquals(1, c.size());
        Assert.assertEquals(2001L, c.get(0).getTimestamp());
        Assert.assertArrayEquals(new byte[]{  }, CellUtil.cloneValue(c.get(0)));
        Assert.assertArrayEquals(TestOperation.ROW, CellUtil.cloneRow(c.get(0)));
        Assert.assertEquals(1970L, p.getTimestamp());
        Assert.assertEquals(0, COMPARATOR.compare(c.get(0), new org.apache.hadoop.hbase.KeyValue(c.get(0))));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void testOperationSubClassMethodsAreBuilderStyle() {
        /* All Operation subclasses should have a builder style setup where setXXX/addXXX methods
        can be chainable together:
        . For example:
        Scan scan = new Scan()
            .setFoo(foo)
            .setBar(bar)
            .setBuz(buz)

        This test ensures that all methods starting with "set" returns the declaring object
         */
        // TODO: We should ensure all subclasses of Operation is checked.
        Class[] classes = new Class[]{ Operation.class, OperationWithAttributes.class, Mutation.class, Query.class, Delete.class, Increment.class, Append.class, Put.class, Get.class, Scan.class };
        BuilderStyleTest.assertClassesAreBuilderStyle(classes);
    }
}

