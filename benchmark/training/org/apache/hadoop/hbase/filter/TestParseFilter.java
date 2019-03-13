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


import CompareOperator.EQUAL;
import CompareOperator.GREATER;
import CompareOperator.GREATER_OR_EQUAL;
import CompareOperator.LESS;
import CompareOperator.LESS_OR_EQUAL;
import CompareOperator.NOT_EQUAL;
import FilterList.Operator.MUST_PASS_ALL;
import FilterList.Operator.MUST_PASS_ONE;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * This class tests ParseFilter.java
 * It tests the entire work flow from when a string is given by the user
 * and how it is parsed to construct the corresponding Filter object
 */
@Category({ RegionServerTests.class, SmallTests.class })
public class TestParseFilter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestParseFilter.class);

    ParseFilter f;

    Filter filter;

    @Test
    public void testKeyOnlyFilter() throws IOException {
        String filterString = "KeyOnlyFilter()";
        doTestFilter(filterString, KeyOnlyFilter.class);
        String filterString2 = "KeyOnlyFilter ('') ";
        byte[] filterStringAsByteArray2 = Bytes.toBytes(filterString2);
        try {
            filter = f.parseFilterString(filterStringAsByteArray2);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testFirstKeyOnlyFilter() throws IOException {
        String filterString = " FirstKeyOnlyFilter( ) ";
        doTestFilter(filterString, FirstKeyOnlyFilter.class);
        String filterString2 = " FirstKeyOnlyFilter ('') ";
        byte[] filterStringAsByteArray2 = Bytes.toBytes(filterString2);
        try {
            filter = f.parseFilterString(filterStringAsByteArray2);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testPrefixFilter() throws IOException {
        String filterString = " PrefixFilter('row' ) ";
        PrefixFilter prefixFilter = doTestFilter(filterString, PrefixFilter.class);
        byte[] prefix = prefixFilter.getPrefix();
        Assert.assertEquals("row", new String(prefix, StandardCharsets.UTF_8));
        filterString = " PrefixFilter(row)";
        try {
            doTestFilter(filterString, PrefixFilter.class);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testColumnPrefixFilter() throws IOException {
        String filterString = " ColumnPrefixFilter('qualifier' ) ";
        ColumnPrefixFilter columnPrefixFilter = doTestFilter(filterString, ColumnPrefixFilter.class);
        byte[] columnPrefix = columnPrefixFilter.getPrefix();
        Assert.assertEquals("qualifier", new String(columnPrefix, StandardCharsets.UTF_8));
    }

    @Test
    public void testMultipleColumnPrefixFilter() throws IOException {
        String filterString = " MultipleColumnPrefixFilter('qualifier1', 'qualifier2' ) ";
        MultipleColumnPrefixFilter multipleColumnPrefixFilter = doTestFilter(filterString, MultipleColumnPrefixFilter.class);
        byte[][] prefixes = multipleColumnPrefixFilter.getPrefix();
        Assert.assertEquals("qualifier1", new String(prefixes[0], StandardCharsets.UTF_8));
        Assert.assertEquals("qualifier2", new String(prefixes[1], StandardCharsets.UTF_8));
    }

    @Test
    public void testColumnCountGetFilter() throws IOException {
        String filterString = " ColumnCountGetFilter(4)";
        ColumnCountGetFilter columnCountGetFilter = doTestFilter(filterString, ColumnCountGetFilter.class);
        int limit = columnCountGetFilter.getLimit();
        Assert.assertEquals(4, limit);
        filterString = " ColumnCountGetFilter('abc')";
        try {
            doTestFilter(filterString, ColumnCountGetFilter.class);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
        filterString = " ColumnCountGetFilter(2147483648)";
        try {
            doTestFilter(filterString, ColumnCountGetFilter.class);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testPageFilter() throws IOException {
        String filterString = " PageFilter(4)";
        PageFilter pageFilter = doTestFilter(filterString, PageFilter.class);
        long pageSize = pageFilter.getPageSize();
        Assert.assertEquals(4, pageSize);
        filterString = " PageFilter('123')";
        try {
            doTestFilter(filterString, PageFilter.class);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println("PageFilter needs an int as an argument");
        }
    }

    @Test
    public void testColumnPaginationFilter() throws IOException {
        String filterString = "ColumnPaginationFilter(4, 6)";
        ColumnPaginationFilter columnPaginationFilter = doTestFilter(filterString, ColumnPaginationFilter.class);
        int limit = columnPaginationFilter.getLimit();
        Assert.assertEquals(4, limit);
        int offset = columnPaginationFilter.getOffset();
        Assert.assertEquals(6, offset);
        filterString = " ColumnPaginationFilter('124')";
        try {
            doTestFilter(filterString, ColumnPaginationFilter.class);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println("ColumnPaginationFilter needs two arguments");
        }
        filterString = " ColumnPaginationFilter('4' , '123a')";
        try {
            doTestFilter(filterString, ColumnPaginationFilter.class);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println("ColumnPaginationFilter needs two ints as arguments");
        }
        filterString = " ColumnPaginationFilter('4' , '-123')";
        try {
            doTestFilter(filterString, ColumnPaginationFilter.class);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println("ColumnPaginationFilter arguments should not be negative");
        }
    }

    @Test
    public void testInclusiveStopFilter() throws IOException {
        String filterString = "InclusiveStopFilter ('row 3')";
        InclusiveStopFilter inclusiveStopFilter = doTestFilter(filterString, InclusiveStopFilter.class);
        byte[] stopRowKey = inclusiveStopFilter.getStopRowKey();
        Assert.assertEquals("row 3", new String(stopRowKey, StandardCharsets.UTF_8));
    }

    @Test
    public void testTimestampsFilter() throws IOException {
        String filterString = "TimestampsFilter(9223372036854775806, 6)";
        TimestampsFilter timestampsFilter = doTestFilter(filterString, TimestampsFilter.class);
        List<Long> timestamps = timestampsFilter.getTimestamps();
        Assert.assertEquals(2, timestamps.size());
        Assert.assertEquals(Long.valueOf(6), timestamps.get(0));
        filterString = "TimestampsFilter()";
        timestampsFilter = doTestFilter(filterString, TimestampsFilter.class);
        timestamps = timestampsFilter.getTimestamps();
        Assert.assertEquals(0, timestamps.size());
        filterString = "TimestampsFilter(9223372036854775808, 6)";
        try {
            doTestFilter(filterString, ColumnPaginationFilter.class);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println("Long Argument was too large");
        }
        filterString = "TimestampsFilter(-45, 6)";
        try {
            doTestFilter(filterString, ColumnPaginationFilter.class);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println("Timestamp Arguments should not be negative");
        }
    }

    @Test
    public void testRowFilter() throws IOException {
        String filterString = "RowFilter ( =,   'binary:regionse')";
        RowFilter rowFilter = doTestFilter(filterString, RowFilter.class);
        Assert.assertEquals(EQUAL, rowFilter.getCompareOperator());
        Assert.assertTrue(((rowFilter.getComparator()) instanceof BinaryComparator));
        BinaryComparator binaryComparator = ((BinaryComparator) (rowFilter.getComparator()));
        Assert.assertEquals("regionse", new String(binaryComparator.getValue(), StandardCharsets.UTF_8));
    }

    @Test
    public void testFamilyFilter() throws IOException {
        String filterString = "FamilyFilter(>=, 'binaryprefix:pre')";
        FamilyFilter familyFilter = doTestFilter(filterString, FamilyFilter.class);
        Assert.assertEquals(GREATER_OR_EQUAL, familyFilter.getCompareOperator());
        Assert.assertTrue(((familyFilter.getComparator()) instanceof BinaryPrefixComparator));
        BinaryPrefixComparator binaryPrefixComparator = ((BinaryPrefixComparator) (familyFilter.getComparator()));
        Assert.assertEquals("pre", new String(binaryPrefixComparator.getValue(), StandardCharsets.UTF_8));
    }

    @Test
    public void testQualifierFilter() throws IOException {
        String filterString = "QualifierFilter(=, 'regexstring:pre*')";
        QualifierFilter qualifierFilter = doTestFilter(filterString, QualifierFilter.class);
        Assert.assertEquals(EQUAL, qualifierFilter.getCompareOperator());
        Assert.assertTrue(((qualifierFilter.getComparator()) instanceof RegexStringComparator));
        RegexStringComparator regexStringComparator = ((RegexStringComparator) (qualifierFilter.getComparator()));
        Assert.assertEquals("pre*", new String(regexStringComparator.getValue(), StandardCharsets.UTF_8));
    }

    @Test
    public void testValueFilter() throws IOException {
        String filterString = "ValueFilter(!=, 'substring:pre')";
        ValueFilter valueFilter = doTestFilter(filterString, ValueFilter.class);
        Assert.assertEquals(NOT_EQUAL, valueFilter.getCompareOperator());
        Assert.assertTrue(((valueFilter.getComparator()) instanceof SubstringComparator));
        SubstringComparator substringComparator = ((SubstringComparator) (valueFilter.getComparator()));
        Assert.assertEquals("pre", new String(substringComparator.getValue(), StandardCharsets.UTF_8));
    }

    @Test
    public void testColumnRangeFilter() throws IOException {
        String filterString = "ColumnRangeFilter('abc', true, 'xyz', false)";
        ColumnRangeFilter columnRangeFilter = doTestFilter(filterString, ColumnRangeFilter.class);
        Assert.assertEquals("abc", new String(columnRangeFilter.getMinColumn(), StandardCharsets.UTF_8));
        Assert.assertEquals("xyz", new String(columnRangeFilter.getMaxColumn(), StandardCharsets.UTF_8));
        Assert.assertTrue(columnRangeFilter.isMinColumnInclusive());
        Assert.assertFalse(columnRangeFilter.isMaxColumnInclusive());
    }

    @Test
    public void testDependentColumnFilter() throws IOException {
        String filterString = "DependentColumnFilter('family', 'qualifier', true, =, 'binary:abc')";
        DependentColumnFilter dependentColumnFilter = doTestFilter(filterString, DependentColumnFilter.class);
        Assert.assertEquals("family", new String(dependentColumnFilter.getFamily(), StandardCharsets.UTF_8));
        Assert.assertEquals("qualifier", new String(dependentColumnFilter.getQualifier(), StandardCharsets.UTF_8));
        Assert.assertTrue(dependentColumnFilter.getDropDependentColumn());
        Assert.assertEquals(EQUAL, dependentColumnFilter.getCompareOperator());
        Assert.assertTrue(((dependentColumnFilter.getComparator()) instanceof BinaryComparator));
        BinaryComparator binaryComparator = ((BinaryComparator) (dependentColumnFilter.getComparator()));
        Assert.assertEquals("abc", new String(binaryComparator.getValue(), StandardCharsets.UTF_8));
    }

    @Test
    public void testSingleColumnValueFilter() throws IOException {
        String filterString = "SingleColumnValueFilter " + "('family', 'qualifier', >=, 'binary:a', true, false)";
        SingleColumnValueFilter singleColumnValueFilter = doTestFilter(filterString, SingleColumnValueFilter.class);
        Assert.assertEquals("family", new String(singleColumnValueFilter.getFamily(), StandardCharsets.UTF_8));
        Assert.assertEquals("qualifier", new String(singleColumnValueFilter.getQualifier(), StandardCharsets.UTF_8));
        Assert.assertEquals(GREATER_OR_EQUAL, singleColumnValueFilter.getCompareOperator());
        Assert.assertTrue(((singleColumnValueFilter.getComparator()) instanceof BinaryComparator));
        BinaryComparator binaryComparator = ((BinaryComparator) (singleColumnValueFilter.getComparator()));
        Assert.assertEquals("a", new String(binaryComparator.getValue(), StandardCharsets.UTF_8));
        Assert.assertTrue(singleColumnValueFilter.getFilterIfMissing());
        Assert.assertFalse(singleColumnValueFilter.getLatestVersionOnly());
        filterString = "SingleColumnValueFilter ('family', 'qualifier', >, 'binaryprefix:a')";
        singleColumnValueFilter = doTestFilter(filterString, SingleColumnValueFilter.class);
        Assert.assertEquals("family", new String(singleColumnValueFilter.getFamily(), StandardCharsets.UTF_8));
        Assert.assertEquals("qualifier", new String(singleColumnValueFilter.getQualifier(), StandardCharsets.UTF_8));
        Assert.assertEquals(GREATER, singleColumnValueFilter.getCompareOperator());
        Assert.assertTrue(((singleColumnValueFilter.getComparator()) instanceof BinaryPrefixComparator));
        BinaryPrefixComparator binaryPrefixComparator = ((BinaryPrefixComparator) (singleColumnValueFilter.getComparator()));
        Assert.assertEquals("a", new String(binaryPrefixComparator.getValue(), StandardCharsets.UTF_8));
        Assert.assertFalse(singleColumnValueFilter.getFilterIfMissing());
        Assert.assertTrue(singleColumnValueFilter.getLatestVersionOnly());
    }

    @Test
    public void testSingleColumnValueExcludeFilter() throws IOException {
        String filterString = "SingleColumnValueExcludeFilter ('family', 'qualifier', <, 'binaryprefix:a')";
        SingleColumnValueExcludeFilter singleColumnValueExcludeFilter = doTestFilter(filterString, SingleColumnValueExcludeFilter.class);
        Assert.assertEquals(LESS, singleColumnValueExcludeFilter.getCompareOperator());
        Assert.assertEquals("family", new String(singleColumnValueExcludeFilter.getFamily(), StandardCharsets.UTF_8));
        Assert.assertEquals("qualifier", new String(singleColumnValueExcludeFilter.getQualifier(), StandardCharsets.UTF_8));
        Assert.assertEquals("a", new String(singleColumnValueExcludeFilter.getComparator().getValue(), StandardCharsets.UTF_8));
        Assert.assertFalse(singleColumnValueExcludeFilter.getFilterIfMissing());
        Assert.assertTrue(singleColumnValueExcludeFilter.getLatestVersionOnly());
        filterString = "SingleColumnValueExcludeFilter " + "('family', 'qualifier', <=, 'binaryprefix:a', true, false)";
        singleColumnValueExcludeFilter = doTestFilter(filterString, SingleColumnValueExcludeFilter.class);
        Assert.assertEquals("family", new String(singleColumnValueExcludeFilter.getFamily(), StandardCharsets.UTF_8));
        Assert.assertEquals("qualifier", new String(singleColumnValueExcludeFilter.getQualifier(), StandardCharsets.UTF_8));
        Assert.assertEquals(LESS_OR_EQUAL, singleColumnValueExcludeFilter.getCompareOperator());
        Assert.assertTrue(((singleColumnValueExcludeFilter.getComparator()) instanceof BinaryPrefixComparator));
        BinaryPrefixComparator binaryPrefixComparator = ((BinaryPrefixComparator) (singleColumnValueExcludeFilter.getComparator()));
        Assert.assertEquals("a", new String(binaryPrefixComparator.getValue(), StandardCharsets.UTF_8));
        Assert.assertTrue(singleColumnValueExcludeFilter.getFilterIfMissing());
        Assert.assertFalse(singleColumnValueExcludeFilter.getLatestVersionOnly());
    }

    @Test
    public void testSkipFilter() throws IOException {
        String filterString = "SKIP ValueFilter( =,  'binary:0')";
        SkipFilter skipFilter = doTestFilter(filterString, SkipFilter.class);
        Assert.assertTrue(((skipFilter.getFilter()) instanceof ValueFilter));
        ValueFilter valueFilter = ((ValueFilter) (skipFilter.getFilter()));
        Assert.assertEquals(EQUAL, valueFilter.getCompareOperator());
        Assert.assertTrue(((valueFilter.getComparator()) instanceof BinaryComparator));
        BinaryComparator binaryComparator = ((BinaryComparator) (valueFilter.getComparator()));
        Assert.assertEquals("0", new String(binaryComparator.getValue(), StandardCharsets.UTF_8));
    }

    @Test
    public void testWhileFilter() throws IOException {
        String filterString = " WHILE   RowFilter ( !=, 'binary:row1')";
        WhileMatchFilter whileMatchFilter = doTestFilter(filterString, WhileMatchFilter.class);
        Assert.assertTrue(((whileMatchFilter.getFilter()) instanceof RowFilter));
        RowFilter rowFilter = ((RowFilter) (whileMatchFilter.getFilter()));
        Assert.assertEquals(NOT_EQUAL, rowFilter.getCompareOperator());
        Assert.assertTrue(((rowFilter.getComparator()) instanceof BinaryComparator));
        BinaryComparator binaryComparator = ((BinaryComparator) (rowFilter.getComparator()));
        Assert.assertEquals("row1", new String(binaryComparator.getValue(), StandardCharsets.UTF_8));
    }

    @Test
    public void testCompoundFilter1() throws IOException {
        String filterString = " (PrefixFilter ('realtime')AND  FirstKeyOnlyFilter())";
        FilterList filterList = doTestFilter(filterString, FilterList.class);
        ArrayList<Filter> filters = ((ArrayList<Filter>) (filterList.getFilters()));
        Assert.assertTrue(((filters.get(0)) instanceof PrefixFilter));
        Assert.assertTrue(((filters.get(1)) instanceof FirstKeyOnlyFilter));
        PrefixFilter PrefixFilter = ((PrefixFilter) (filters.get(0)));
        byte[] prefix = PrefixFilter.getPrefix();
        Assert.assertEquals("realtime", new String(prefix, StandardCharsets.UTF_8));
        FirstKeyOnlyFilter firstKeyOnlyFilter = ((FirstKeyOnlyFilter) (filters.get(1)));
    }

    @Test
    public void testCompoundFilter2() throws IOException {
        String filterString = "(PrefixFilter('realtime') AND QualifierFilter (>=, 'binary:e'))" + "OR FamilyFilter (=, 'binary:qualifier') ";
        FilterList filterList = doTestFilter(filterString, FilterList.class);
        ArrayList<Filter> filterListFilters = ((ArrayList<Filter>) (filterList.getFilters()));
        Assert.assertTrue(((filterListFilters.get(0)) instanceof FilterList));
        Assert.assertTrue(((filterListFilters.get(1)) instanceof FamilyFilter));
        Assert.assertEquals(MUST_PASS_ONE, filterList.getOperator());
        filterList = ((FilterList) (filterListFilters.get(0)));
        FamilyFilter familyFilter = ((FamilyFilter) (filterListFilters.get(1)));
        filterListFilters = ((ArrayList<Filter>) (filterList.getFilters()));
        Assert.assertTrue(((filterListFilters.get(0)) instanceof PrefixFilter));
        Assert.assertTrue(((filterListFilters.get(1)) instanceof QualifierFilter));
        Assert.assertEquals(MUST_PASS_ALL, filterList.getOperator());
        Assert.assertEquals(EQUAL, familyFilter.getCompareOperator());
        Assert.assertTrue(((familyFilter.getComparator()) instanceof BinaryComparator));
        BinaryComparator binaryComparator = ((BinaryComparator) (familyFilter.getComparator()));
        Assert.assertEquals("qualifier", new String(binaryComparator.getValue(), StandardCharsets.UTF_8));
        PrefixFilter prefixFilter = ((PrefixFilter) (filterListFilters.get(0)));
        byte[] prefix = prefixFilter.getPrefix();
        Assert.assertEquals("realtime", new String(prefix, StandardCharsets.UTF_8));
        QualifierFilter qualifierFilter = ((QualifierFilter) (filterListFilters.get(1)));
        Assert.assertEquals(GREATER_OR_EQUAL, qualifierFilter.getCompareOperator());
        Assert.assertTrue(((qualifierFilter.getComparator()) instanceof BinaryComparator));
        binaryComparator = ((BinaryComparator) (qualifierFilter.getComparator()));
        Assert.assertEquals("e", new String(binaryComparator.getValue(), StandardCharsets.UTF_8));
    }

    @Test
    public void testCompoundFilter3() throws IOException {
        String filterString = " ColumnPrefixFilter ('realtime')AND  " + "FirstKeyOnlyFilter() OR SKIP FamilyFilter(=, 'substring:hihi')";
        FilterList filterList = doTestFilter(filterString, FilterList.class);
        ArrayList<Filter> filters = ((ArrayList<Filter>) (filterList.getFilters()));
        Assert.assertTrue(((filters.get(0)) instanceof FilterList));
        Assert.assertTrue(((filters.get(1)) instanceof SkipFilter));
        filterList = ((FilterList) (filters.get(0)));
        SkipFilter skipFilter = ((SkipFilter) (filters.get(1)));
        filters = ((ArrayList<Filter>) (filterList.getFilters()));
        Assert.assertTrue(((filters.get(0)) instanceof ColumnPrefixFilter));
        Assert.assertTrue(((filters.get(1)) instanceof FirstKeyOnlyFilter));
        ColumnPrefixFilter columnPrefixFilter = ((ColumnPrefixFilter) (filters.get(0)));
        byte[] columnPrefix = columnPrefixFilter.getPrefix();
        Assert.assertEquals("realtime", new String(columnPrefix, StandardCharsets.UTF_8));
        FirstKeyOnlyFilter firstKeyOnlyFilter = ((FirstKeyOnlyFilter) (filters.get(1)));
        Assert.assertTrue(((skipFilter.getFilter()) instanceof FamilyFilter));
        FamilyFilter familyFilter = ((FamilyFilter) (skipFilter.getFilter()));
        Assert.assertEquals(EQUAL, familyFilter.getCompareOperator());
        Assert.assertTrue(((familyFilter.getComparator()) instanceof SubstringComparator));
        SubstringComparator substringComparator = ((SubstringComparator) (familyFilter.getComparator()));
        Assert.assertEquals("hihi", new String(substringComparator.getValue(), StandardCharsets.UTF_8));
    }

    @Test
    public void testCompoundFilter4() throws IOException {
        String filterString = " ColumnPrefixFilter ('realtime') OR " + "FirstKeyOnlyFilter() OR SKIP FamilyFilter(=, 'substring:hihi')";
        FilterList filterList = doTestFilter(filterString, FilterList.class);
        ArrayList<Filter> filters = ((ArrayList<Filter>) (filterList.getFilters()));
        Assert.assertTrue(((filters.get(0)) instanceof ColumnPrefixFilter));
        Assert.assertTrue(((filters.get(1)) instanceof FirstKeyOnlyFilter));
        Assert.assertTrue(((filters.get(2)) instanceof SkipFilter));
        ColumnPrefixFilter columnPrefixFilter = ((ColumnPrefixFilter) (filters.get(0)));
        FirstKeyOnlyFilter firstKeyOnlyFilter = ((FirstKeyOnlyFilter) (filters.get(1)));
        SkipFilter skipFilter = ((SkipFilter) (filters.get(2)));
        byte[] columnPrefix = columnPrefixFilter.getPrefix();
        Assert.assertEquals("realtime", new String(columnPrefix, StandardCharsets.UTF_8));
        Assert.assertTrue(((skipFilter.getFilter()) instanceof FamilyFilter));
        FamilyFilter familyFilter = ((FamilyFilter) (skipFilter.getFilter()));
        Assert.assertEquals(EQUAL, familyFilter.getCompareOperator());
        Assert.assertTrue(((familyFilter.getComparator()) instanceof SubstringComparator));
        SubstringComparator substringComparator = ((SubstringComparator) (familyFilter.getComparator()));
        Assert.assertEquals("hihi", new String(substringComparator.getValue(), StandardCharsets.UTF_8));
    }

    @Test
    public void testCompoundFilter5() throws IOException {
        String filterStr = "(ValueFilter(!=, 'substring:pre'))";
        ValueFilter valueFilter = doTestFilter(filterStr, ValueFilter.class);
        Assert.assertTrue(((valueFilter.getComparator()) instanceof SubstringComparator));
        filterStr = "(ValueFilter(>=,'binary:x') AND (ValueFilter(<=,'binary:y')))" + " OR ValueFilter(=,'binary:ab')";
        filter = f.parseFilterString(filterStr);
        Assert.assertTrue(((filter) instanceof FilterList));
        List<Filter> list = getFilters();
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(((list.get(0)) instanceof FilterList));
        Assert.assertTrue(((list.get(1)) instanceof ValueFilter));
    }

    @Test
    public void testIncorrectCompareOperator() throws IOException {
        String filterString = "RowFilter ('>>' , 'binary:region')";
        try {
            doTestFilter(filterString, RowFilter.class);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println("Incorrect compare operator >>");
        }
    }

    @Test
    public void testIncorrectComparatorType() throws IOException {
        String filterString = "RowFilter ('>=' , 'binaryoperator:region')";
        try {
            doTestFilter(filterString, RowFilter.class);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println("Incorrect comparator type: binaryoperator");
        }
        filterString = "RowFilter ('>=' 'regexstring:pre*')";
        try {
            doTestFilter(filterString, RowFilter.class);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println("RegexStringComparator can only be used with EQUAL or NOT_EQUAL");
        }
        filterString = "SingleColumnValueFilter" + " ('family', 'qualifier', '>=', 'substring:a', 'true', 'false')')";
        try {
            doTestFilter(filterString, RowFilter.class);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println("SubtringComparator can only be used with EQUAL or NOT_EQUAL");
        }
    }

    @Test
    public void testPrecedence1() throws IOException {
        String filterString = " (PrefixFilter ('realtime')AND  FirstKeyOnlyFilter()" + " OR KeyOnlyFilter())";
        FilterList filterList = doTestFilter(filterString, FilterList.class);
        ArrayList<Filter> filters = ((ArrayList<Filter>) (filterList.getFilters()));
        Assert.assertTrue(((filters.get(0)) instanceof FilterList));
        Assert.assertTrue(((filters.get(1)) instanceof KeyOnlyFilter));
        filterList = ((FilterList) (filters.get(0)));
        filters = ((ArrayList<Filter>) (filterList.getFilters()));
        Assert.assertTrue(((filters.get(0)) instanceof PrefixFilter));
        Assert.assertTrue(((filters.get(1)) instanceof FirstKeyOnlyFilter));
        PrefixFilter prefixFilter = ((PrefixFilter) (filters.get(0)));
        byte[] prefix = prefixFilter.getPrefix();
        Assert.assertEquals("realtime", new String(prefix, StandardCharsets.UTF_8));
    }

    @Test
    public void testPrecedence2() throws IOException {
        String filterString = " PrefixFilter ('realtime')AND  SKIP FirstKeyOnlyFilter()" + "OR KeyOnlyFilter()";
        FilterList filterList = doTestFilter(filterString, FilterList.class);
        ArrayList<Filter> filters = ((ArrayList<Filter>) (filterList.getFilters()));
        Assert.assertTrue(((filters.get(0)) instanceof FilterList));
        Assert.assertTrue(((filters.get(1)) instanceof KeyOnlyFilter));
        filterList = ((FilterList) (filters.get(0)));
        filters = ((ArrayList<Filter>) (filterList.getFilters()));
        Assert.assertTrue(((filters.get(0)) instanceof PrefixFilter));
        Assert.assertTrue(((filters.get(1)) instanceof SkipFilter));
        PrefixFilter prefixFilter = ((PrefixFilter) (filters.get(0)));
        byte[] prefix = prefixFilter.getPrefix();
        Assert.assertEquals("realtime", new String(prefix, StandardCharsets.UTF_8));
        SkipFilter skipFilter = ((SkipFilter) (filters.get(1)));
        Assert.assertTrue(((skipFilter.getFilter()) instanceof FirstKeyOnlyFilter));
    }

    @Test
    public void testUnescapedQuote1() throws IOException {
        String filterString = "InclusiveStopFilter ('row''3')";
        InclusiveStopFilter inclusiveStopFilter = doTestFilter(filterString, InclusiveStopFilter.class);
        byte[] stopRowKey = inclusiveStopFilter.getStopRowKey();
        Assert.assertEquals("row'3", new String(stopRowKey, StandardCharsets.UTF_8));
    }

    @Test
    public void testUnescapedQuote2() throws IOException {
        String filterString = "InclusiveStopFilter ('row''3''')";
        InclusiveStopFilter inclusiveStopFilter = doTestFilter(filterString, InclusiveStopFilter.class);
        byte[] stopRowKey = inclusiveStopFilter.getStopRowKey();
        Assert.assertEquals("row'3'", new String(stopRowKey, StandardCharsets.UTF_8));
    }

    @Test
    public void testUnescapedQuote3() throws IOException {
        String filterString = " InclusiveStopFilter ('''')";
        InclusiveStopFilter inclusiveStopFilter = doTestFilter(filterString, InclusiveStopFilter.class);
        byte[] stopRowKey = inclusiveStopFilter.getStopRowKey();
        Assert.assertEquals("'", new String(stopRowKey, StandardCharsets.UTF_8));
    }

    @Test
    public void testIncorrectFilterString() throws IOException {
        String filterString = "()";
        byte[] filterStringAsByteArray = Bytes.toBytes(filterString);
        try {
            filter = f.parseFilterString(filterStringAsByteArray);
            Assert.assertTrue(false);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testCorrectFilterString() throws IOException {
        String filterString = "(FirstKeyOnlyFilter())";
        FirstKeyOnlyFilter firstKeyOnlyFilter = doTestFilter(filterString, FirstKeyOnlyFilter.class);
    }

    @Test
    public void testRegisterFilter() {
        ParseFilter.registerFilter("MyFilter", "some.class");
        Assert.assertTrue(f.getSupportedFilters().contains("MyFilter"));
    }

    @Test
    public void testColumnValueFilter() throws IOException {
        String filterString = "ColumnValueFilter ('family', 'qualifier', <, 'binaryprefix:value')";
        ColumnValueFilter cvf = doTestFilter(filterString, ColumnValueFilter.class);
        Assert.assertEquals("family", new String(cvf.getFamily(), StandardCharsets.UTF_8));
        Assert.assertEquals("qualifier", new String(cvf.getQualifier(), StandardCharsets.UTF_8));
        Assert.assertEquals(LESS, cvf.getCompareOperator());
        Assert.assertTrue(((cvf.getComparator()) instanceof BinaryPrefixComparator));
        Assert.assertEquals("value", new String(cvf.getComparator().getValue(), StandardCharsets.UTF_8));
    }
}

