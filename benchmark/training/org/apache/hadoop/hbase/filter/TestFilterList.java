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


import Filter.ReturnCode.INCLUDE_AND_NEXT_COL;
import ReturnCode.INCLUDE;
import ReturnCode.INCLUDE_AND_SEEK_NEXT_ROW;
import ReturnCode.NEXT_COL;
import ReturnCode.NEXT_ROW;
import ReturnCode.SEEK_NEXT_USING_HINT;
import ReturnCode.SKIP;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellComparator;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValueUtil;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.filter.Filter.ReturnCode;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.testclassification.FilterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category({ FilterTests.class, SmallTests.class })
public class TestFilterList {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFilterList.class);

    static final int MAX_PAGES = 2;

    @Test
    public void testAddFilter() throws Exception {
        Filter filter1 = new FirstKeyOnlyFilter();
        Filter filter2 = new FirstKeyOnlyFilter();
        FilterList filterList = new FilterList(filter1, filter2);
        filterList.addFilter(new FirstKeyOnlyFilter());
        filterList = new FilterList(Arrays.asList(filter1, filter2));
        filterList.addFilter(new FirstKeyOnlyFilter());
        filterList = new FilterList(Operator.MUST_PASS_ALL, filter1, filter2);
        filterList.addFilter(new FirstKeyOnlyFilter());
        filterList = new FilterList(Operator.MUST_PASS_ALL, Arrays.asList(filter1, filter2));
        filterList.addFilter(new FirstKeyOnlyFilter());
        filterList.setReversed(false);
        FirstKeyOnlyFilter f = new FirstKeyOnlyFilter();
        f.setReversed(true);
        try {
            filterList.addFilter(f);
            Assert.fail("The IllegalArgumentException should be thrown because the added filter is reversed");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testConstruction() {
        FirstKeyOnlyFilter f1 = new FirstKeyOnlyFilter();
        FirstKeyOnlyFilter f2 = new FirstKeyOnlyFilter();
        f1.setReversed(true);
        f2.setReversed(false);
        try {
            FilterList ff = new FilterList(f1, f2);
            Assert.fail("The IllegalArgumentException should be thrown");
        } catch (IllegalArgumentException e) {
        }
        try {
            FilterList ff = new FilterList(Arrays.asList(f1, f2));
            Assert.fail("The IllegalArgumentException should be thrown because the added filter is reversed");
        } catch (IllegalArgumentException e) {
        }
        try {
            FilterList ff = new FilterList(Operator.MUST_PASS_ALL, Arrays.asList(f1, f2));
            Assert.fail("The IllegalArgumentException should be thrown because the added filter is reversed");
        } catch (IllegalArgumentException e) {
        }
        try {
            FilterList ff = new FilterList(Operator.MUST_PASS_ALL, f1, f2);
            Assert.fail("The IllegalArgumentException should be thrown because the added filter is reversed");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * Test "must pass one"
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMPONE() throws Exception {
        mpOneTest(getFilterMPONE());
    }

    /**
     * Test "must pass all"
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMPALL() throws Exception {
        mpAllTest(getMPALLFilter());
    }

    /**
     * Test list ordering
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testOrdering() throws Exception {
        orderingTest(getOrderingFilter());
    }

    /**
     * When we do a "MUST_PASS_ONE" (a logical 'OR') of the above two filters
     * we expect to get the same result as the 'prefix' only result.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFilterListTwoFiltersMustPassOne() throws Exception {
        byte[] r1 = Bytes.toBytes("Row1");
        byte[] r11 = Bytes.toBytes("Row11");
        byte[] r2 = Bytes.toBytes("Row2");
        FilterList flist = new FilterList(Operator.MUST_PASS_ONE);
        flist.addFilter(new PrefixFilter(r1));
        flist.filterRowKey(KeyValueUtil.createFirstOnRow(r1));
        Assert.assertEquals(INCLUDE, flist.filterCell(new KeyValue(r1, r1, r1)));
        Assert.assertEquals(INCLUDE, flist.filterCell(new KeyValue(r11, r11, r11)));
        flist.reset();
        flist.filterRowKey(KeyValueUtil.createFirstOnRow(r2));
        Assert.assertEquals(SKIP, flist.filterCell(new KeyValue(r2, r2, r2)));
        flist = new FilterList(Operator.MUST_PASS_ONE);
        flist.addFilter(new TestFilterList.AlwaysNextColFilter());
        flist.addFilter(new PrefixFilter(r1));
        flist.filterRowKey(KeyValueUtil.createFirstOnRow(r1));
        Assert.assertEquals(INCLUDE, flist.filterCell(new KeyValue(r1, r1, r1)));
        Assert.assertEquals(INCLUDE, flist.filterCell(new KeyValue(r11, r11, r11)));
        flist.reset();
        flist.filterRowKey(KeyValueUtil.createFirstOnRow(r2));
        Assert.assertEquals(NEXT_COL, flist.filterCell(new KeyValue(r2, r2, r2)));
    }

    /**
     * When we do a "MUST_PASS_ONE" (a logical 'OR') of the two filters
     * we expect to get the same result as the inclusive stop result.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFilterListWithInclusiveStopFilterMustPassOne() throws Exception {
        byte[] r1 = Bytes.toBytes("Row1");
        byte[] r11 = Bytes.toBytes("Row11");
        byte[] r2 = Bytes.toBytes("Row2");
        FilterList flist = new FilterList(Operator.MUST_PASS_ONE);
        flist.addFilter(new TestFilterList.AlwaysNextColFilter());
        flist.addFilter(new InclusiveStopFilter(r1));
        flist.filterRowKey(KeyValueUtil.createFirstOnRow(r1));
        Assert.assertEquals(INCLUDE, flist.filterCell(new KeyValue(r1, r1, r1)));
        Assert.assertEquals(INCLUDE, flist.filterCell(new KeyValue(r11, r11, r11)));
        flist.reset();
        flist.filterRowKey(KeyValueUtil.createFirstOnRow(r2));
        Assert.assertEquals(NEXT_COL, flist.filterCell(new KeyValue(r2, r2, r2)));
    }

    public static class AlwaysNextColFilter extends FilterBase {
        public AlwaysNextColFilter() {
            super();
        }

        @Override
        public ReturnCode filterCell(final Cell v) {
            return ReturnCode.NEXT_COL;
        }

        public static TestFilterList.AlwaysNextColFilter parseFrom(final byte[] pbBytes) throws DeserializationException {
            return new TestFilterList.AlwaysNextColFilter();
        }
    }

    /**
     * Test serialization
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSerialization() throws Exception {
        List<Filter> filters = new ArrayList<>();
        filters.add(new PageFilter(TestFilterList.MAX_PAGES));
        filters.add(new WhileMatchFilter(new PrefixFilter(Bytes.toBytes("yyy"))));
        Filter filterMPALL = new FilterList(Operator.MUST_PASS_ALL, filters);
        // Decompose filterMPALL to bytes.
        byte[] buffer = filterMPALL.toByteArray();
        // Recompose filterMPALL.
        FilterList newFilter = FilterList.parseFrom(buffer);
        // Run tests
        mpOneTest(ProtobufUtil.toFilter(ProtobufUtil.toFilter(getFilterMPONE())));
        mpAllTest(ProtobufUtil.toFilter(ProtobufUtil.toFilter(getMPALLFilter())));
        orderingTest(ProtobufUtil.toFilter(ProtobufUtil.toFilter(getOrderingFilter())));
    }

    /**
     * Test filterCell logic.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFilterCell() throws Exception {
        Filter includeFilter = new FilterBase() {
            @Override
            public ReturnCode filterCell(final Cell v) {
                return ReturnCode.INCLUDE;
            }
        };
        Filter alternateFilter = new FilterBase() {
            boolean returnInclude = true;

            @Override
            public ReturnCode filterCell(final Cell v) {
                Filter.ReturnCode returnCode = (returnInclude) ? ReturnCode.INCLUDE : ReturnCode.SKIP;
                returnInclude = !(returnInclude);
                return returnCode;
            }
        };
        Filter alternateIncludeFilter = new FilterBase() {
            boolean returnIncludeOnly = false;

            @Override
            public ReturnCode filterCell(final Cell v) {
                Filter.ReturnCode returnCode = (returnIncludeOnly) ? ReturnCode.INCLUDE : ReturnCode.INCLUDE_AND_NEXT_COL;
                returnIncludeOnly = !(returnIncludeOnly);
                return returnCode;
            }
        };
        // Check must pass one filter.
        FilterList mpOnefilterList = new FilterList(Operator.MUST_PASS_ONE, Arrays.asList(new Filter[]{ includeFilter, alternateIncludeFilter, alternateFilter }));
        // INCLUDE, INCLUDE, INCLUDE_AND_NEXT_COL.
        Assert.assertEquals(INCLUDE, mpOnefilterList.filterCell(null));
        // INCLUDE, SKIP, INCLUDE.
        Assert.assertEquals(Filter.ReturnCode.INCLUDE, mpOnefilterList.filterCell(null));
        // Check must pass all filter.
        FilterList mpAllfilterList = new FilterList(Operator.MUST_PASS_ALL, Arrays.asList(new Filter[]{ includeFilter, alternateIncludeFilter, alternateFilter }));
        // INCLUDE, INCLUDE, INCLUDE_AND_NEXT_COL.
        Assert.assertEquals(INCLUDE_AND_NEXT_COL, mpAllfilterList.filterCell(null));
        // INCLUDE, SKIP, INCLUDE.
        Assert.assertEquals(Filter.ReturnCode.SKIP, mpAllfilterList.filterCell(null));
    }

    /**
     * Test pass-thru of hints.
     */
    @Test
    public void testHintPassThru() throws Exception {
        final KeyValue minKeyValue = new KeyValue(Bytes.toBytes(0L), null, null);
        final KeyValue maxKeyValue = new KeyValue(Bytes.toBytes(Long.MAX_VALUE), null, null);
        Filter filterNoHint = new FilterBase() {
            @Override
            public byte[] toByteArray() {
                return null;
            }

            @Override
            public ReturnCode filterCell(final Cell ignored) throws IOException {
                return ReturnCode.INCLUDE;
            }
        };
        Filter filterMinHint = new FilterBase() {
            @Override
            public ReturnCode filterCell(final Cell ignored) {
                return ReturnCode.SEEK_NEXT_USING_HINT;
            }

            @Override
            public Cell getNextCellHint(Cell currentKV) {
                return minKeyValue;
            }

            @Override
            public byte[] toByteArray() {
                return null;
            }
        };
        Filter filterMaxHint = new FilterBase() {
            @Override
            public ReturnCode filterCell(final Cell ignored) {
                return ReturnCode.SEEK_NEXT_USING_HINT;
            }

            @Override
            public Cell getNextCellHint(Cell cell) {
                return new KeyValue(Bytes.toBytes(Long.MAX_VALUE), null, null);
            }

            @Override
            public byte[] toByteArray() {
                return null;
            }
        };
        CellComparator comparator = CellComparator.getInstance();
        // MUST PASS ONE
        // Should take the min if given two hints
        FilterList filterList = new FilterList(Operator.MUST_PASS_ONE, Arrays.asList(new Filter[]{ filterMinHint, filterMaxHint }));
        Assert.assertEquals(0, comparator.compare(filterList.getNextCellHint(null), minKeyValue));
        // Should have no hint if any filter has no hint
        filterList = new FilterList(Operator.MUST_PASS_ONE, Arrays.asList(new Filter[]{ filterMinHint, filterMaxHint, filterNoHint }));
        Assert.assertNull(filterList.getNextCellHint(null));
        filterList = new FilterList(Operator.MUST_PASS_ONE, Arrays.asList(new Filter[]{ filterNoHint, filterMaxHint }));
        Assert.assertNull(filterList.getNextCellHint(null));
        // Should give max hint if its the only one
        filterList = new FilterList(Operator.MUST_PASS_ONE, Arrays.asList(new Filter[]{ filterMaxHint, filterMaxHint }));
        Assert.assertEquals(0, comparator.compare(filterList.getNextCellHint(null), maxKeyValue));
        // MUST PASS ALL
        // Should take the first hint
        filterList = new FilterList(Operator.MUST_PASS_ALL, Arrays.asList(new Filter[]{ filterMinHint, filterMaxHint }));
        filterList.filterCell(null);
        Assert.assertEquals(0, comparator.compare(filterList.getNextCellHint(null), minKeyValue));
        filterList = new FilterList(Operator.MUST_PASS_ALL, Arrays.asList(new Filter[]{ filterMaxHint, filterMinHint }));
        filterList.filterCell(null);
        Assert.assertEquals(0, comparator.compare(filterList.getNextCellHint(null), maxKeyValue));
        // Should have first hint even if a filter has no hint
        filterList = new FilterList(Operator.MUST_PASS_ALL, Arrays.asList(new Filter[]{ filterNoHint, filterMinHint, filterMaxHint }));
        filterList.filterCell(null);
        Assert.assertEquals(0, comparator.compare(filterList.getNextCellHint(null), minKeyValue));
        filterList = new FilterList(Operator.MUST_PASS_ALL, Arrays.asList(new Filter[]{ filterNoHint, filterMaxHint }));
        filterList.filterCell(null);
        Assert.assertEquals(0, comparator.compare(filterList.getNextCellHint(null), maxKeyValue));
        filterList = new FilterList(Operator.MUST_PASS_ALL, Arrays.asList(new Filter[]{ filterNoHint, filterMinHint }));
        filterList.filterCell(null);
        Assert.assertEquals(0, comparator.compare(filterList.getNextCellHint(null), minKeyValue));
    }

    /**
     * Tests the behavior of transform() in a hierarchical filter.
     *
     * transform() only applies after a filterCell() whose return-code includes the KeyValue.
     * Lazy evaluation of AND
     */
    @Test
    public void testTransformMPO() throws Exception {
        // Apply the following filter:
        // (family=fam AND qualifier=qual1 AND KeyOnlyFilter)
        // OR (family=fam AND qualifier=qual2)
        final FilterList flist = new FilterList(Operator.MUST_PASS_ONE, Lists.<Filter>newArrayList(new FilterList(Operator.MUST_PASS_ALL, Lists.<Filter>newArrayList(new FamilyFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes("fam"))), new QualifierFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes("qual1"))), new KeyOnlyFilter())), new FilterList(Operator.MUST_PASS_ALL, Lists.<Filter>newArrayList(new FamilyFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes("fam"))), new QualifierFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes("qual2")))))));
        final KeyValue kvQual1 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam"), Bytes.toBytes("qual1"), Bytes.toBytes("value"));
        final KeyValue kvQual2 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam"), Bytes.toBytes("qual2"), Bytes.toBytes("value"));
        final KeyValue kvQual3 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam"), Bytes.toBytes("qual3"), Bytes.toBytes("value"));
        // Value for fam:qual1 should be stripped:
        Assert.assertEquals(Filter.ReturnCode.INCLUDE, flist.filterCell(kvQual1));
        final KeyValue transformedQual1 = KeyValueUtil.ensureKeyValue(flist.transformCell(kvQual1));
        Assert.assertEquals(0, transformedQual1.getValueLength());
        // Value for fam:qual2 should not be stripped:
        Assert.assertEquals(Filter.ReturnCode.INCLUDE, flist.filterCell(kvQual2));
        final KeyValue transformedQual2 = KeyValueUtil.ensureKeyValue(flist.transformCell(kvQual2));
        Assert.assertEquals("value", Bytes.toString(transformedQual2.getValueArray(), transformedQual2.getValueOffset(), transformedQual2.getValueLength()));
        // Other keys should be skipped:
        Assert.assertEquals(Filter.ReturnCode.SKIP, flist.filterCell(kvQual3));
    }

    @Test
    public void testWithMultiVersionsInSameRow() throws Exception {
        FilterList filterList01 = new FilterList(Operator.MUST_PASS_ONE, new ColumnPaginationFilter(1, 0));
        KeyValue kv1 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam"), Bytes.toBytes("qual"), 1, Bytes.toBytes("value"));
        KeyValue kv2 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam"), Bytes.toBytes("qual"), 2, Bytes.toBytes("value"));
        KeyValue kv3 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam"), Bytes.toBytes("qual"), 3, Bytes.toBytes("value"));
        Assert.assertEquals(ReturnCode.INCLUDE_AND_NEXT_COL, filterList01.filterCell(kv1));
        Assert.assertEquals(NEXT_COL, filterList01.filterCell(kv2));
        Assert.assertEquals(NEXT_COL, filterList01.filterCell(kv3));
        FilterList filterList11 = new FilterList(Operator.MUST_PASS_ONE, new ColumnPaginationFilter(1, 1));
        Assert.assertEquals(NEXT_COL, filterList11.filterCell(kv1));
        Assert.assertEquals(NEXT_COL, filterList11.filterCell(kv2));
        Assert.assertEquals(NEXT_COL, filterList11.filterCell(kv3));
    }

    @Test
    public void testMPONEWithSeekNextUsingHint() throws Exception {
        byte[] col = Bytes.toBytes("c");
        FilterList filterList = new FilterList(Operator.MUST_PASS_ONE, new ColumnPaginationFilter(1, col));
        KeyValue kv1 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam"), Bytes.toBytes("a"), 1, Bytes.toBytes("value"));
        KeyValue kv2 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam"), Bytes.toBytes("b"), 2, Bytes.toBytes("value"));
        KeyValue kv3 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam"), Bytes.toBytes("c"), 3, Bytes.toBytes("value"));
        KeyValue kv4 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam"), Bytes.toBytes("c"), 4, Bytes.toBytes("value"));
        Assert.assertEquals(SEEK_NEXT_USING_HINT, filterList.filterCell(kv1));
        Assert.assertEquals(SEEK_NEXT_USING_HINT, filterList.filterCell(kv2));
        Assert.assertEquals(ReturnCode.INCLUDE_AND_NEXT_COL, filterList.filterCell(kv3));
        Assert.assertEquals(NEXT_COL, filterList.filterCell(kv4));
    }

    private static class MockFilter extends FilterBase {
        private ReturnCode targetRetCode;

        public boolean didCellPassToTheFilter = false;

        public MockFilter(ReturnCode targetRetCode) {
            this.targetRetCode = targetRetCode;
        }

        @Override
        public ReturnCode filterCell(final Cell v) throws IOException {
            this.didCellPassToTheFilter = true;
            return targetRetCode;
        }

        @Override
        public boolean equals(Object obj) {
            if ((obj == null) || (!(obj instanceof TestFilterList.MockFilter))) {
                return false;
            }
            if (obj == (this)) {
                return true;
            }
            TestFilterList.MockFilter f = ((TestFilterList.MockFilter) (obj));
            return this.targetRetCode.equals(f.targetRetCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.targetRetCode);
        }
    }

    @Test
    public void testShouldPassCurrentCellToFilter() throws IOException {
        KeyValue kv1 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam"), Bytes.toBytes("a"), 1, Bytes.toBytes("value"));
        KeyValue kv2 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam"), Bytes.toBytes("a"), 2, Bytes.toBytes("value"));
        KeyValue kv3 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam"), Bytes.toBytes("b"), 3, Bytes.toBytes("value"));
        KeyValue kv4 = new KeyValue(Bytes.toBytes("row1"), Bytes.toBytes("fam"), Bytes.toBytes("c"), 4, Bytes.toBytes("value"));
        TestFilterList.MockFilter mockFilter = new TestFilterList.MockFilter(ReturnCode.NEXT_COL);
        FilterList filter = new FilterList(Operator.MUST_PASS_ONE, mockFilter);
        filter.filterCell(kv1);
        Assert.assertTrue(mockFilter.didCellPassToTheFilter);
        mockFilter.didCellPassToTheFilter = false;
        filter.filterCell(kv2);
        Assert.assertFalse(mockFilter.didCellPassToTheFilter);
        mockFilter.didCellPassToTheFilter = false;
        filter.filterCell(kv3);
        Assert.assertTrue(mockFilter.didCellPassToTheFilter);
        mockFilter = new TestFilterList.MockFilter(ReturnCode.INCLUDE_AND_NEXT_COL);
        filter = new FilterList(Operator.MUST_PASS_ONE, mockFilter);
        filter.filterCell(kv1);
        Assert.assertTrue(mockFilter.didCellPassToTheFilter);
        mockFilter.didCellPassToTheFilter = false;
        filter.filterCell(kv2);
        Assert.assertFalse(mockFilter.didCellPassToTheFilter);
        mockFilter.didCellPassToTheFilter = false;
        filter.filterCell(kv3);
        Assert.assertTrue(mockFilter.didCellPassToTheFilter);
        mockFilter = new TestFilterList.MockFilter(ReturnCode.NEXT_ROW);
        filter = new FilterList(Operator.MUST_PASS_ONE, mockFilter);
        filter.filterCell(kv1);
        Assert.assertTrue(mockFilter.didCellPassToTheFilter);
        mockFilter.didCellPassToTheFilter = false;
        filter.filterCell(kv2);
        Assert.assertFalse(mockFilter.didCellPassToTheFilter);
        mockFilter.didCellPassToTheFilter = false;
        filter.filterCell(kv3);
        Assert.assertFalse(mockFilter.didCellPassToTheFilter);
        filter.reset();
        mockFilter.didCellPassToTheFilter = false;
        filter.filterCell(kv4);
        Assert.assertTrue(mockFilter.didCellPassToTheFilter);
        mockFilter = new TestFilterList.MockFilter(ReturnCode.INCLUDE_AND_SEEK_NEXT_ROW);
        filter = new FilterList(Operator.MUST_PASS_ONE, mockFilter);
        filter.filterCell(kv1);
        Assert.assertTrue(mockFilter.didCellPassToTheFilter);
        mockFilter.didCellPassToTheFilter = false;
        filter.filterCell(kv2);
        Assert.assertFalse(mockFilter.didCellPassToTheFilter);
        mockFilter.didCellPassToTheFilter = false;
        filter.filterCell(kv3);
        Assert.assertFalse(mockFilter.didCellPassToTheFilter);
        filter.reset();
        mockFilter.didCellPassToTheFilter = false;
        filter.filterCell(kv4);
        Assert.assertTrue(mockFilter.didCellPassToTheFilter);
    }

    @Test
    public void testTheMaximalRule() throws IOException {
        KeyValue kv1 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam"), Bytes.toBytes("a"), 1, Bytes.toBytes("value"));
        TestFilterList.MockFilter filter1 = new TestFilterList.MockFilter(ReturnCode.INCLUDE);
        TestFilterList.MockFilter filter2 = new TestFilterList.MockFilter(ReturnCode.INCLUDE_AND_NEXT_COL);
        TestFilterList.MockFilter filter3 = new TestFilterList.MockFilter(ReturnCode.INCLUDE_AND_SEEK_NEXT_ROW);
        TestFilterList.MockFilter filter4 = new TestFilterList.MockFilter(ReturnCode.NEXT_COL);
        TestFilterList.MockFilter filter5 = new TestFilterList.MockFilter(ReturnCode.SKIP);
        TestFilterList.MockFilter filter6 = new TestFilterList.MockFilter(ReturnCode.SEEK_NEXT_USING_HINT);
        TestFilterList.MockFilter filter7 = new TestFilterList.MockFilter(ReturnCode.NEXT_ROW);
        FilterList filterList = new FilterList(Operator.MUST_PASS_ALL, filter1, filter2);
        Assert.assertEquals(ReturnCode.INCLUDE_AND_NEXT_COL, filterList.filterCell(kv1));
        filterList = new FilterList(Operator.MUST_PASS_ALL, filter2, filter3);
        Assert.assertEquals(INCLUDE_AND_SEEK_NEXT_ROW, filterList.filterCell(kv1));
        filterList = new FilterList(Operator.MUST_PASS_ALL, filter4, filter5, filter6);
        Assert.assertEquals(NEXT_COL, filterList.filterCell(kv1));
        filterList = new FilterList(Operator.MUST_PASS_ALL, filter4, filter6);
        Assert.assertEquals(NEXT_COL, filterList.filterCell(kv1));
        filterList = new FilterList(Operator.MUST_PASS_ALL, filter3, filter1);
        Assert.assertEquals(INCLUDE_AND_SEEK_NEXT_ROW, filterList.filterCell(kv1));
        filterList = new FilterList(Operator.MUST_PASS_ALL, filter3, filter2, filter1, filter5);
        Assert.assertEquals(NEXT_ROW, filterList.filterCell(kv1));
        filterList = new FilterList(Operator.MUST_PASS_ALL, filter2, new FilterList(Operator.MUST_PASS_ALL, filter3, filter4));
        Assert.assertEquals(NEXT_ROW, filterList.filterCell(kv1));
        filterList = new FilterList(Operator.MUST_PASS_ALL, filter3, filter7);
        Assert.assertEquals(NEXT_ROW, filterList.filterCell(kv1));
    }

    @Test
    public void testTheMinimalRule() throws IOException {
        KeyValue kv1 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam"), Bytes.toBytes("a"), 1, Bytes.toBytes("value"));
        TestFilterList.MockFilter filter1 = new TestFilterList.MockFilter(ReturnCode.INCLUDE);
        TestFilterList.MockFilter filter2 = new TestFilterList.MockFilter(ReturnCode.INCLUDE_AND_NEXT_COL);
        TestFilterList.MockFilter filter3 = new TestFilterList.MockFilter(ReturnCode.INCLUDE_AND_SEEK_NEXT_ROW);
        TestFilterList.MockFilter filter4 = new TestFilterList.MockFilter(ReturnCode.NEXT_COL);
        TestFilterList.MockFilter filter5 = new TestFilterList.MockFilter(ReturnCode.SKIP);
        TestFilterList.MockFilter filter6 = new TestFilterList.MockFilter(ReturnCode.SEEK_NEXT_USING_HINT);
        FilterList filterList = new FilterList(Operator.MUST_PASS_ONE, filter1, filter2);
        Assert.assertEquals(INCLUDE, filterList.filterCell(kv1));
        filterList = new FilterList(Operator.MUST_PASS_ONE, filter2, filter3);
        Assert.assertEquals(ReturnCode.INCLUDE_AND_NEXT_COL, filterList.filterCell(kv1));
        filterList = new FilterList(Operator.MUST_PASS_ONE, filter4, filter5, filter6);
        Assert.assertEquals(SKIP, filterList.filterCell(kv1));
        filterList = new FilterList(Operator.MUST_PASS_ONE, filter4, filter6);
        Assert.assertEquals(SKIP, filterList.filterCell(kv1));
        filterList = new FilterList(Operator.MUST_PASS_ONE, filter3, filter1);
        Assert.assertEquals(INCLUDE, filterList.filterCell(kv1));
        filterList = new FilterList(Operator.MUST_PASS_ONE, filter3, filter2, filter1, filter5);
        Assert.assertEquals(INCLUDE, filterList.filterCell(kv1));
        filterList = new FilterList(Operator.MUST_PASS_ONE, filter2, new FilterList(Operator.MUST_PASS_ONE, filter3, filter4));
        Assert.assertEquals(ReturnCode.INCLUDE_AND_NEXT_COL, filterList.filterCell(kv1));
        filterList = new FilterList(Operator.MUST_PASS_ONE, filter2, new FilterList(Operator.MUST_PASS_ONE, filter3, filter4));
        Assert.assertEquals(ReturnCode.INCLUDE_AND_NEXT_COL, filterList.filterCell(kv1));
        filterList = new FilterList(Operator.MUST_PASS_ONE, filter6, filter6);
        Assert.assertEquals(SEEK_NEXT_USING_HINT, filterList.filterCell(kv1));
    }

    static class MockSeekHintFilter extends FilterBase {
        private Cell returnCell;

        public MockSeekHintFilter(Cell returnCell) {
            this.returnCell = returnCell;
        }

        @Override
        public ReturnCode filterCell(final Cell v) throws IOException {
            return ReturnCode.SEEK_NEXT_USING_HINT;
        }

        @Override
        public Cell getNextCellHint(Cell currentCell) throws IOException {
            return this.returnCell;
        }

        @Override
        public boolean equals(Object obj) {
            if ((obj == null) || (!(obj instanceof TestFilterList.MockSeekHintFilter))) {
                return false;
            }
            if (obj == (this)) {
                return true;
            }
            TestFilterList.MockSeekHintFilter f = ((TestFilterList.MockSeekHintFilter) (obj));
            return this.returnCell.equals(f.returnCell);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.returnCell);
        }
    }

    @Test
    public void testReversedFilterListWithMockSeekHintFilter() throws IOException {
        KeyValue kv1 = new KeyValue(Bytes.toBytes("row1"), Bytes.toBytes("fam"), Bytes.toBytes("a"), 1, Bytes.toBytes("value"));
        KeyValue kv2 = new KeyValue(Bytes.toBytes("row2"), Bytes.toBytes("fam"), Bytes.toBytes("a"), 1, Bytes.toBytes("value"));
        KeyValue kv3 = new KeyValue(Bytes.toBytes("row3"), Bytes.toBytes("fam"), Bytes.toBytes("a"), 1, Bytes.toBytes("value"));
        Filter filter1 = new TestFilterList.MockSeekHintFilter(kv1);
        filter1.setReversed(true);
        Filter filter2 = new TestFilterList.MockSeekHintFilter(kv2);
        filter2.setReversed(true);
        Filter filter3 = new TestFilterList.MockSeekHintFilter(kv3);
        filter3.setReversed(true);
        FilterList filterList = new FilterList(Operator.MUST_PASS_ONE);
        filterList.setReversed(true);
        filterList.addFilter(filter1);
        filterList.addFilter(filter2);
        filterList.addFilter(filter3);
        Assert.assertEquals(SEEK_NEXT_USING_HINT, filterList.filterCell(kv1));
        Assert.assertEquals(kv3, filterList.getNextCellHint(kv1));
        filterList = new FilterList(Operator.MUST_PASS_ALL);
        filterList.setReversed(true);
        filterList.addFilter(filter1);
        filterList.addFilter(filter2);
        filterList.addFilter(filter3);
        Assert.assertEquals(SEEK_NEXT_USING_HINT, filterList.filterCell(kv1));
        Assert.assertEquals(kv1, filterList.getNextCellHint(kv1));
    }

    @Test
    public void testReversedFilterListWithOR() throws IOException {
        byte[] r22 = Bytes.toBytes("Row22");
        byte[] r2 = Bytes.toBytes("Row2");
        byte[] r1 = Bytes.toBytes("Row1");
        FilterList filterList = new FilterList(Operator.MUST_PASS_ONE);
        filterList.setReversed(true);
        PrefixFilter prefixFilter = new PrefixFilter(r2);
        prefixFilter.setReversed(true);
        filterList.addFilter(prefixFilter);
        filterList.filterRowKey(KeyValueUtil.createFirstOnRow(r22));
        Assert.assertEquals(INCLUDE, filterList.filterCell(new KeyValue(r22, r22, r22)));
        Assert.assertEquals(INCLUDE, filterList.filterCell(new KeyValue(r2, r2, r2)));
        filterList.reset();
        filterList.filterRowKey(KeyValueUtil.createFirstOnRow(r1));
        Assert.assertEquals(SKIP, filterList.filterCell(new KeyValue(r1, r1, r1)));
        filterList = new FilterList(Operator.MUST_PASS_ONE);
        filterList.setReversed(true);
        TestFilterList.AlwaysNextColFilter alwaysNextColFilter = new TestFilterList.AlwaysNextColFilter();
        setReversed(true);
        prefixFilter = new PrefixFilter(r2);
        prefixFilter.setReversed(true);
        filterList.addFilter(alwaysNextColFilter);
        filterList.addFilter(prefixFilter);
        filterList.filterRowKey(KeyValueUtil.createFirstOnRow(r22));
        Assert.assertEquals(INCLUDE, filterList.filterCell(new KeyValue(r22, r22, r22)));
        Assert.assertEquals(INCLUDE, filterList.filterCell(new KeyValue(r2, r2, r2)));
        filterList.reset();
        filterList.filterRowKey(KeyValueUtil.createFirstOnRow(r1));
        Assert.assertEquals(NEXT_COL, filterList.filterCell(new KeyValue(r1, r1, r1)));
    }

    @Test
    public void testKeyOnlyFilterTransformCell() throws IOException {
        Cell c;
        KeyValue kv1 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("cf"), Bytes.toBytes("column1"), 1, Bytes.toBytes("value1"));
        KeyValue kv2 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("cf"), Bytes.toBytes("column1"), 2, Bytes.toBytes("value2"));
        Filter filter1 = new SingleColumnValueFilter(Bytes.toBytes("cf"), Bytes.toBytes("column1"), CompareOperator.EQUAL, Bytes.toBytes("value1"));
        Filter filter2 = new SingleColumnValueFilter(Bytes.toBytes("cf"), Bytes.toBytes("column1"), CompareOperator.EQUAL, Bytes.toBytes("value2"));
        FilterList internalFilterList = new FilterList(Operator.MUST_PASS_ONE, filter1, filter2);
        FilterList keyOnlyFilterFirst = new FilterList(Operator.MUST_PASS_ALL, new KeyOnlyFilter(), internalFilterList);
        Assert.assertEquals(INCLUDE, keyOnlyFilterFirst.filterCell(kv1));
        c = keyOnlyFilterFirst.transformCell(kv1);
        Assert.assertEquals(0, c.getValueLength());
        Assert.assertEquals(INCLUDE, keyOnlyFilterFirst.filterCell(kv2));
        c = keyOnlyFilterFirst.transformCell(kv2);
        Assert.assertEquals(0, c.getValueLength());
        internalFilterList.reset();
        FilterList keyOnlyFilterLast = new FilterList(Operator.MUST_PASS_ALL, new KeyOnlyFilter(), internalFilterList);
        Assert.assertEquals(INCLUDE, keyOnlyFilterLast.filterCell(kv1));
        c = keyOnlyFilterLast.transformCell(kv1);
        Assert.assertEquals(0, c.getValueLength());
        Assert.assertEquals(INCLUDE, keyOnlyFilterLast.filterCell(kv2));
        c = keyOnlyFilterLast.transformCell(kv2);
        Assert.assertEquals(0, c.getValueLength());
    }

    @Test
    public void testEmptyFilterListTransformCell() throws IOException {
        KeyValue kv = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("cf"), Bytes.toBytes("column1"), 1, Bytes.toBytes("value"));
        FilterList filterList = new FilterList(Operator.MUST_PASS_ALL);
        Assert.assertEquals(INCLUDE, filterList.filterCell(kv));
        Assert.assertEquals(kv, filterList.transformCell(kv));
        filterList = new FilterList(Operator.MUST_PASS_ONE);
        Assert.assertEquals(INCLUDE, filterList.filterCell(kv));
        Assert.assertEquals(kv, filterList.transformCell(kv));
    }

    private static class MockNextRowFilter extends FilterBase {
        private int hitCount = 0;

        @Override
        public ReturnCode filterCell(final Cell v) throws IOException {
            (hitCount)++;
            return ReturnCode.NEXT_ROW;
        }

        public int getHitCount() {
            return hitCount;
        }
    }

    @Test
    public void testRowCountFilter() throws IOException {
        KeyValue kv1 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam1"), Bytes.toBytes("a"), 1, Bytes.toBytes("value"));
        KeyValue kv2 = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("fam2"), Bytes.toBytes("a"), 2, Bytes.toBytes("value"));
        TestFilterList.MockNextRowFilter mockNextRowFilter = new TestFilterList.MockNextRowFilter();
        FilterList filter = new FilterList(Operator.MUST_PASS_ONE, mockNextRowFilter);
        filter.filterCell(kv1);
        filter.filterCell(kv2);
        Assert.assertEquals(2, mockNextRowFilter.getHitCount());
    }

    private static class TransformFilter extends FilterBase {
        private ReturnCode targetRetCode;

        private boolean transformed = false;

        public TransformFilter(ReturnCode targetRetCode) {
            this.targetRetCode = targetRetCode;
        }

        @Override
        public ReturnCode filterCell(final Cell v) throws IOException {
            return targetRetCode;
        }

        @Override
        public Cell transformCell(Cell c) throws IOException {
            transformed = true;
            return super.transformCell(c);
        }

        public boolean getTransformed() {
            return this.transformed;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof TestFilterList.TransformFilter)) {
                return false;
            }
            if (obj == (this)) {
                return true;
            }
            TestFilterList.TransformFilter f = ((TestFilterList.TransformFilter) (obj));
            return this.targetRetCode.equals(f.targetRetCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.targetRetCode);
        }
    }

    @Test
    public void testTransformCell() throws IOException {
        KeyValue kv = new KeyValue(Bytes.toBytes("row"), Bytes.toBytes("cf"), Bytes.toBytes("column1"), 1, Bytes.toBytes("value"));
        // case MUST_PASS_ONE
        TestFilterList.TransformFilter filter1 = new TestFilterList.TransformFilter(ReturnCode.INCLUDE);
        TestFilterList.TransformFilter filter2 = new TestFilterList.TransformFilter(ReturnCode.NEXT_ROW);
        TestFilterList.TransformFilter filter3 = new TestFilterList.TransformFilter(ReturnCode.SEEK_NEXT_USING_HINT);
        FilterList filterList = new FilterList(Operator.MUST_PASS_ONE, filter1, filter2, filter3);
        Assert.assertEquals(INCLUDE, filterList.filterCell(kv));
        Assert.assertEquals(kv, filterList.transformCell(kv));
        Assert.assertEquals(true, filter1.getTransformed());
        Assert.assertEquals(false, filter2.getTransformed());
        Assert.assertEquals(false, filter3.getTransformed());
        // case MUST_PASS_ALL
        filter1 = new TestFilterList.TransformFilter(ReturnCode.INCLUDE);
        filter2 = new TestFilterList.TransformFilter(ReturnCode.INCLUDE_AND_SEEK_NEXT_ROW);
        filter3 = new TestFilterList.TransformFilter(ReturnCode.INCLUDE_AND_NEXT_COL);
        filterList = new FilterList(Operator.MUST_PASS_ALL, filter1, filter2, filter3);
        Assert.assertEquals(INCLUDE_AND_SEEK_NEXT_ROW, filterList.filterCell(kv));
        Assert.assertEquals(kv, filterList.transformCell(kv));
        Assert.assertEquals(true, filter1.getTransformed());
        Assert.assertEquals(true, filter2.getTransformed());
        Assert.assertEquals(true, filter3.getTransformed());
    }

    @Test
    public void testFilterListWithORWhenPassingCellMismatchPreviousRC() throws IOException {
        // Mainly test FilterListWithOR#calculateReturnCodeByPrevCellAndRC method with two sub-filters.
        KeyValue kv1 = new KeyValue(Bytes.toBytes("row1"), Bytes.toBytes("fam"), Bytes.toBytes("a"), 100, Bytes.toBytes("value"));
        KeyValue kv2 = new KeyValue(Bytes.toBytes("row1"), Bytes.toBytes("fam"), Bytes.toBytes("a"), 99, Bytes.toBytes("value"));
        KeyValue kv3 = new KeyValue(Bytes.toBytes("row1"), Bytes.toBytes("fam"), Bytes.toBytes("b"), 1, Bytes.toBytes("value"));
        KeyValue kv4 = new KeyValue(Bytes.toBytes("row1"), Bytes.toBytes("fan"), Bytes.toBytes("a"), 1, Bytes.toBytes("value"));
        Filter subFilter1 = Mockito.mock(FilterBase.class);
        Mockito.when(subFilter1.filterCell(kv1)).thenReturn(ReturnCode.INCLUDE_AND_NEXT_COL);
        Mockito.when(subFilter1.filterCell(kv2)).thenReturn(NEXT_COL);
        Mockito.when(subFilter1.filterCell(kv3)).thenReturn(ReturnCode.INCLUDE_AND_NEXT_COL);
        Mockito.when(subFilter1.filterCell(kv4)).thenReturn(ReturnCode.INCLUDE_AND_NEXT_COL);
        Filter subFilter2 = Mockito.mock(FilterBase.class);
        Mockito.when(subFilter2.filterCell(kv1)).thenReturn(SKIP);
        Mockito.when(subFilter2.filterCell(kv2)).thenReturn(NEXT_ROW);
        Mockito.when(subFilter2.filterCell(kv3)).thenReturn(NEXT_ROW);
        Mockito.when(subFilter2.filterCell(kv4)).thenReturn(INCLUDE_AND_SEEK_NEXT_ROW);
        Filter filterList = new FilterList(Operator.MUST_PASS_ONE, subFilter1, subFilter2);
        Assert.assertEquals(INCLUDE, filterList.filterCell(kv1));
        Assert.assertEquals(NEXT_COL, filterList.filterCell(kv2));
        Assert.assertEquals(ReturnCode.INCLUDE_AND_NEXT_COL, filterList.filterCell(kv3));
        Assert.assertEquals(ReturnCode.INCLUDE_AND_NEXT_COL, filterList.filterCell(kv4));
        // One sub-filter will filterAllRemaining but other sub-filter will return SEEK_HINT
        subFilter1 = Mockito.mock(FilterBase.class);
        Mockito.when(subFilter1.filterAllRemaining()).thenReturn(true);
        Mockito.when(subFilter1.filterCell(kv1)).thenReturn(NEXT_ROW);
        subFilter2 = Mockito.mock(FilterBase.class);
        Mockito.when(subFilter2.filterCell(kv1)).thenReturn(SEEK_NEXT_USING_HINT);
        filterList = new FilterList(Operator.MUST_PASS_ONE, subFilter1, subFilter2);
        Assert.assertEquals(SEEK_NEXT_USING_HINT, filterList.filterCell(kv1));
        // Two sub-filter returns SEEK_NEXT_USING_HINT, then we should return SEEK_NEXT_USING_HINT.
        subFilter1 = Mockito.mock(FilterBase.class);
        Mockito.when(subFilter1.filterCell(kv1)).thenReturn(SEEK_NEXT_USING_HINT);
        subFilter2 = Mockito.mock(FilterBase.class);
        Mockito.when(subFilter2.filterCell(kv1)).thenReturn(SEEK_NEXT_USING_HINT);
        filterList = new FilterList(Operator.MUST_PASS_ONE, subFilter1, subFilter2);
        Assert.assertEquals(SEEK_NEXT_USING_HINT, filterList.filterCell(kv1));
    }
}

