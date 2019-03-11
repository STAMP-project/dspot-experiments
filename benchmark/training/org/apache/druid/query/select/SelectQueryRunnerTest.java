/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.select;


import ColumnHolder.TIME_COLUMN_NAME;
import EventHolder.timestampKey;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.js.JavaScriptConfig;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.Result;
import org.apache.druid.query.dimension.DefaultDimensionSpec;
import org.apache.druid.query.dimension.DimensionSpec;
import org.apache.druid.query.dimension.ExtractionDimensionSpec;
import org.apache.druid.query.expression.TestExprMacroTable;
import org.apache.druid.query.extraction.ExtractionFn;
import org.apache.druid.query.extraction.MapLookupExtractor;
import org.apache.druid.query.filter.SelectorDimFilter;
import org.apache.druid.query.lookup.LookupExtractionFn;
import org.apache.druid.query.ordering.StringComparators;
import org.apache.druid.query.spec.QuerySegmentSpec;
import org.apache.druid.segment.column.ColumnHolder;
import org.apache.druid.segment.column.ValueType;
import org.apache.druid.timeline.SegmentId;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static EventHolder.timestampKey;


/**
 *
 */
@RunWith(Parameterized.class)
public class SelectQueryRunnerTest {
    // copied from druid.sample.numeric.tsv
    public static final String[] V_0112 = new String[]{ "2011-01-12T00:00:00.000Z\tspot\tautomotive\t1000\t10000.0\t100000\tpreferred\ta\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tbusiness\t1100\t11000.0\t110000\tpreferred\tb\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tentertainment\t1200\t12000.0\t120000\tpreferred\te\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\thealth\t1300\t13000.0\t130000\tpreferred\th\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tmezzanine\t1400\t14000.0\t140000\tpreferred\tm\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tnews\t1500\t15000.0\t150000\tpreferred\tn\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tpremium\t1600\t16000.0\t160000\tpreferred\tp\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\ttechnology\t1700\t17000.0\t170000\tpreferred\tt\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\ttravel\t1800\t18000.0\t180000\tpreferred\tt\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\ttotal_market\tmezzanine\t1400\t14000.0\t140000\tpreferred\tm\u0001preferred\t1000.000000", "2011-01-12T00:00:00.000Z\ttotal_market\tpremium\t1600\t16000.0\t160000\tpreferred\tp\u0001preferred\t1000.000000", "2011-01-12T00:00:00.000Z\tupfront\tmezzanine\t1400\t14000.0\t140000\tpreferred\tm\u0001preferred\t800.000000\tvalue", "2011-01-12T00:00:00.000Z\tupfront\tpremium\t1600\t16000.0\t160000\tpreferred\tp\u0001preferred\t800.000000\tvalue" };

    public static final String[] V_0113 = new String[]{ "2011-01-13T00:00:00.000Z\tspot\tautomotive\t1000\t10000.0\t100000\tpreferred\ta\u0001preferred\t94.874713", "2011-01-13T00:00:00.000Z\tspot\tbusiness\t1100\t11000.0\t110000\tpreferred\tb\u0001preferred\t103.629399", "2011-01-13T00:00:00.000Z\tspot\tentertainment\t1200\t12000.0\t120000\tpreferred\te\u0001preferred\t110.087299", "2011-01-13T00:00:00.000Z\tspot\thealth\t1300\t13000.0\t130000\tpreferred\th\u0001preferred\t114.947403", "2011-01-13T00:00:00.000Z\tspot\tmezzanine\t1400\t14000.0\t140000\tpreferred\tm\u0001preferred\t104.465767", "2011-01-13T00:00:00.000Z\tspot\tnews\t1500\t15000.0\t150000\tpreferred\tn\u0001preferred\t102.851683", "2011-01-13T00:00:00.000Z\tspot\tpremium\t1600\t16000.0\t160000\tpreferred\tp\u0001preferred\t108.863011", "2011-01-13T00:00:00.000Z\tspot\ttechnology\t1700\t17000.0\t170000\tpreferred\tt\u0001preferred\t111.356672", "2011-01-13T00:00:00.000Z\tspot\ttravel\t1800\t18000.0\t180000\tpreferred\tt\u0001preferred\t106.236928", "2011-01-13T00:00:00.000Z\ttotal_market\tmezzanine\t1400\t14000.0\t140000\tpreferred\tm\u0001preferred\t1040.945505", "2011-01-13T00:00:00.000Z\ttotal_market\tpremium\t1600\t16000.0\t160000\tpreferred\tp\u0001preferred\t1689.012875", "2011-01-13T00:00:00.000Z\tupfront\tmezzanine\t1400\t14000.0\t140000\tpreferred\tm\u0001preferred\t826.060182\tvalue", "2011-01-13T00:00:00.000Z\tupfront\tpremium\t1600\t16000.0\t160000\tpreferred\tp\u0001preferred\t1564.617729\tvalue" };

    private static final Interval I_0112_0114 = Intervals.of("2011-01-12/2011-01-14");

    public static final QuerySegmentSpec I_0112_0114_SPEC = new org.apache.druid.query.spec.LegacySegmentSpec(SelectQueryRunnerTest.I_0112_0114);

    private static final SegmentId SEGMENT_ID_I_0112_0114 = QueryRunnerTestHelper.segmentId.withInterval(SelectQueryRunnerTest.I_0112_0114);

    private static final String segmentIdString = SelectQueryRunnerTest.SEGMENT_ID_I_0112_0114.toString();

    public static final String[] V_0112_0114 = ObjectArrays.concat(SelectQueryRunnerTest.V_0112, SelectQueryRunnerTest.V_0113, String.class);

    private static final boolean DEFAULT_FROM_NEXT = true;

    private static final SelectQueryConfig config = new SelectQueryConfig(true);

    static {
        SelectQueryRunnerTest.config.setEnableFromNextDefault(SelectQueryRunnerTest.DEFAULT_FROM_NEXT);
    }

    private static final Supplier<SelectQueryConfig> configSupplier = Suppliers.ofInstance(SelectQueryRunnerTest.config);

    private static final SelectQueryQueryToolChest toolChest = new SelectQueryQueryToolChest(new DefaultObjectMapper(), QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator(), SelectQueryRunnerTest.configSupplier);

    private final QueryRunner runner;

    private final boolean descending;

    public SelectQueryRunnerTest(QueryRunner runner, boolean descending) {
        this.runner = runner;
        this.descending = descending;
    }

    @Test
    public void testFullOnSelect() {
        SelectQuery query = newTestQuery().intervals(SelectQueryRunnerTest.I_0112_0114_SPEC).build();
        HashMap<String, Object> context = new HashMap<String, Object>();
        Iterable<Result<SelectResultValue>> results = runner.run(QueryPlus.wrap(query), context).toList();
        PagingOffset offset = query.getPagingOffset(SelectQueryRunnerTest.segmentIdString);
        List<Result<SelectResultValue>> expectedResults = toExpected(SelectQueryRunnerTest.segmentIdString, toFullEvents(SelectQueryRunnerTest.V_0112_0114), Lists.newArrayList("market", "quality", "qualityLong", "qualityFloat", "qualityDouble", "qualityNumericString", "placement", "placementish", "partial_null_column", "null_column"), Lists.newArrayList("index", "quality_uniques", "indexMin", "indexMaxPlusTen", "indexFloat", "indexMaxFloat", "indexMinFloat"), offset.startOffset(), offset.threshold());
        SelectQueryRunnerTest.verify(expectedResults, SelectQueryRunnerTest.populateNullColumnAtLastForQueryableIndexCase(results, "null_column"));
    }

    @Test
    public void testSequentialPaging() {
        int[] asc = new int[]{ 2, 5, 8, 11, 14, 17, 20, 23, 25 };
        int[] dsc = new int[]{ -3, -6, -9, -12, -15, -18, -21, -24, -26 };
        int[] expected = (descending) ? dsc : asc;
        SelectQuery query = newTestQuery().intervals(SelectQueryRunnerTest.I_0112_0114_SPEC).build();
        for (int offset : expected) {
            List<Result<SelectResultValue>> results = runner.run(QueryPlus.wrap(query), ImmutableMap.of()).toList();
            Assert.assertEquals(1, results.size());
            SelectResultValue result = results.get(0).getValue();
            Map<String, Integer> pagingIdentifiers = result.getPagingIdentifiers();
            Assert.assertEquals(offset, pagingIdentifiers.get(SelectQueryRunnerTest.SEGMENT_ID_I_0112_0114.toString()).intValue());
            Map<String, Integer> next = PagingSpec.next(pagingIdentifiers, descending);
            query = query.withPagingSpec(new PagingSpec(next, 3, false));
        }
        query = newTestQuery().intervals(SelectQueryRunnerTest.I_0112_0114_SPEC).build();
        for (int offset : expected) {
            List<Result<SelectResultValue>> results = runner.run(QueryPlus.wrap(query), ImmutableMap.of()).toList();
            Assert.assertEquals(1, results.size());
            SelectResultValue result = results.get(0).getValue();
            Map<String, Integer> pagingIdentifiers = result.getPagingIdentifiers();
            Assert.assertEquals(offset, pagingIdentifiers.get(SelectQueryRunnerTest.SEGMENT_ID_I_0112_0114.toString()).intValue());
            // use identifier as-is but with fromNext=true
            query = query.withPagingSpec(new PagingSpec(pagingIdentifiers, 3, true));
        }
    }

    @Test
    public void testFullOnSelectWithDimensionSpec() {
        Map<String, String> map = new HashMap<>();
        map.put("automotive", "automotive0");
        map.put("business", "business0");
        map.put("entertainment", "entertainment0");
        map.put("health", "health0");
        map.put("mezzanine", "mezzanine0");
        map.put("news", "news0");
        map.put("premium", "premium0");
        map.put("technology", "technology0");
        map.put("travel", "travel0");
        SelectQuery query = newTestQuery().dimensionSpecs(Arrays.asList(new DefaultDimensionSpec(QueryRunnerTestHelper.marketDimension, "mar"), new ExtractionDimensionSpec(QueryRunnerTestHelper.qualityDimension, "qual", new LookupExtractionFn(new MapLookupExtractor(map, true), false, null, true, false)), new DefaultDimensionSpec(QueryRunnerTestHelper.placementDimension, "place"))).build();
        HashMap<String, Object> context = new HashMap<String, Object>();
        Iterable<Result<SelectResultValue>> results = runner.run(QueryPlus.wrap(query), context).toList();
        String segmentIdInThisQuery = QueryRunnerTestHelper.segmentId.toString();
        List<Result<SelectResultValue>> expectedResultsAsc = Collections.singletonList(new Result<SelectResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new SelectResultValue(ImmutableMap.of(segmentIdInThisQuery, 2), Sets.newHashSet("mar", "qual", "place"), Sets.newHashSet("index", "quality_uniques", "indexMin", "indexMaxPlusTen", "indexMinFloat", "indexFloat", "indexMaxFloat"), Arrays.asList(new EventHolder(segmentIdInThisQuery, 0, new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-01-12T00:00:00.000Z")).put("mar", "spot").put("qual", "automotive0").put("place", "preferred").put(QueryRunnerTestHelper.indexMetric, 100.0F).build()), new EventHolder(segmentIdInThisQuery, 1, new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-01-12T00:00:00.000Z")).put("mar", "spot").put("qual", "business0").put("place", "preferred").put(QueryRunnerTestHelper.indexMetric, 100.0F).build()), new EventHolder(segmentIdInThisQuery, 2, new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-01-12T00:00:00.000Z")).put("mar", "spot").put("qual", "entertainment0").put("place", "preferred").put(QueryRunnerTestHelper.indexMetric, 100.0F).build())))));
        List<Result<SelectResultValue>> expectedResultsDsc = Collections.singletonList(new Result<SelectResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new SelectResultValue(ImmutableMap.of(segmentIdInThisQuery, (-3)), Sets.newHashSet("mar", "qual", "place"), Sets.newHashSet("index", "quality_uniques", "indexMin", "indexMaxPlusTen", "indexMinFloat", "indexFloat", "indexMaxFloat"), Arrays.asList(new EventHolder(segmentIdInThisQuery, (-1), new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-04-15T00:00:00.000Z")).put("mar", "upfront").put("qual", "premium0").put("place", "preferred").put(QueryRunnerTestHelper.indexMetric, 780.272F).build()), new EventHolder(segmentIdInThisQuery, (-2), new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-04-15T00:00:00.000Z")).put("mar", "upfront").put("qual", "mezzanine0").put("place", "preferred").put(QueryRunnerTestHelper.indexMetric, 962.7312F).build()), new EventHolder(segmentIdInThisQuery, (-3), new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-04-15T00:00:00.000Z")).put("mar", "total_market").put("qual", "premium0").put("place", "preferred").put(QueryRunnerTestHelper.indexMetric, 1029.057F).build())))));
        SelectQueryRunnerTest.verify((descending ? expectedResultsDsc : expectedResultsAsc), results);
    }

    @Test
    public void testSelectWithDimsAndMets() {
        SelectQuery query = newTestQuery().intervals(SelectQueryRunnerTest.I_0112_0114_SPEC).dimensionSpecs(DefaultDimensionSpec.toSpec(QueryRunnerTestHelper.marketDimension)).metrics(Collections.singletonList(QueryRunnerTestHelper.indexMetric)).build();
        HashMap<String, Object> context = new HashMap<String, Object>();
        Iterable<Result<SelectResultValue>> results = runner.run(QueryPlus.wrap(query), context).toList();
        PagingOffset offset = query.getPagingOffset(SelectQueryRunnerTest.segmentIdString);
        List<Result<SelectResultValue>> expectedResults = toExpected(SelectQueryRunnerTest.segmentIdString, toEvents(new String[]{ (timestampKey) + ":TIME", (QueryRunnerTestHelper.marketDimension) + ":STRING", null, null, null, null, null, null, (QueryRunnerTestHelper.indexMetric) + ":FLOAT" }, SelectQueryRunnerTest.V_0112_0114), Collections.singletonList("market"), Collections.singletonList("index"), offset.startOffset(), offset.threshold());
        SelectQueryRunnerTest.verify(expectedResults, results);
    }

    @Test
    public void testSelectPagination() {
        SelectQuery query = newTestQuery().intervals(SelectQueryRunnerTest.I_0112_0114_SPEC).dimensionSpecs(DefaultDimensionSpec.toSpec(QueryRunnerTestHelper.qualityDimension)).metrics(Collections.singletonList(QueryRunnerTestHelper.indexMetric)).pagingSpec(new PagingSpec(toPagingIdentifier(3, descending), 3)).build();
        Iterable<Result<SelectResultValue>> results = runner.run(QueryPlus.wrap(query), new HashMap()).toList();
        PagingOffset offset = query.getPagingOffset(SelectQueryRunnerTest.segmentIdString);
        List<Result<SelectResultValue>> expectedResults = toExpected(SelectQueryRunnerTest.segmentIdString, toEvents(new String[]{ (timestampKey) + ":TIME", "foo:NULL", "foo2:NULL" }, SelectQueryRunnerTest.V_0112_0114), Collections.singletonList("quality"), Collections.singletonList("index"), offset.startOffset(), offset.threshold());
        SelectQueryRunnerTest.verify(expectedResults, results);
    }

    @Test
    public void testFullOnSelectWithFilter() {
        // startDelta + threshold pairs
        for (int[] param : new int[][]{ new int[]{ 3, 3 }, new int[]{ 0, 1 }, new int[]{ 5, 5 }, new int[]{ 2, 7 }, new int[]{ 3, 0 } }) {
            SelectQuery query = newTestQuery().intervals(SelectQueryRunnerTest.I_0112_0114_SPEC).filters(new SelectorDimFilter(QueryRunnerTestHelper.marketDimension, "spot", null)).granularity(QueryRunnerTestHelper.dayGran).dimensionSpecs(DefaultDimensionSpec.toSpec(QueryRunnerTestHelper.qualityDimension)).metrics(Collections.singletonList(QueryRunnerTestHelper.indexMetric)).pagingSpec(new PagingSpec(toPagingIdentifier(param[0], descending), param[1])).build();
            HashMap<String, Object> context = new HashMap<String, Object>();
            Iterable<Result<SelectResultValue>> results = runner.run(QueryPlus.wrap(query), context).toList();
            final List<List<Map<String, Object>>> events = // filtered values with day granularity
            toEvents(new String[]{ (timestampKey) + ":TIME", null, (QueryRunnerTestHelper.qualityDimension) + ":STRING", null, null, (QueryRunnerTestHelper.indexMetric) + ":FLOAT" }, new String[]{ "2011-01-12T00:00:00.000Z\tspot\tautomotive\tpreferred\ta\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tbusiness\tpreferred\tb\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tentertainment\tpreferred\te\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\thealth\tpreferred\th\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tmezzanine\tpreferred\tm\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tnews\tpreferred\tn\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tpremium\tpreferred\tp\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\ttechnology\tpreferred\tt\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\ttravel\tpreferred\tt\u0001preferred\t100.000000" }, new String[]{ "2011-01-13T00:00:00.000Z\tspot\tautomotive\tpreferred\ta\u0001preferred\t94.874713", "2011-01-13T00:00:00.000Z\tspot\tbusiness\tpreferred\tb\u0001preferred\t103.629399", "2011-01-13T00:00:00.000Z\tspot\tentertainment\tpreferred\te\u0001preferred\t110.087299", "2011-01-13T00:00:00.000Z\tspot\thealth\tpreferred\th\u0001preferred\t114.947403", "2011-01-13T00:00:00.000Z\tspot\tmezzanine\tpreferred\tm\u0001preferred\t104.465767", "2011-01-13T00:00:00.000Z\tspot\tnews\tpreferred\tn\u0001preferred\t102.851683", "2011-01-13T00:00:00.000Z\tspot\tpremium\tpreferred\tp\u0001preferred\t108.863011", "2011-01-13T00:00:00.000Z\tspot\ttechnology\tpreferred\tt\u0001preferred\t111.356672", "2011-01-13T00:00:00.000Z\tspot\ttravel\tpreferred\tt\u0001preferred\t106.236928" });
            PagingOffset offset = query.getPagingOffset(SelectQueryRunnerTest.segmentIdString);
            List<Result<SelectResultValue>> expectedResults = toExpected(SelectQueryRunnerTest.segmentIdString, events, Collections.singletonList("quality"), Collections.singletonList("index"), offset.startOffset(), offset.threshold());
            SelectQueryRunnerTest.verify(expectedResults, results);
        }
    }

    @Test
    public void testFullOnSelectWithFilterOnVirtualColumn() {
        Interval interval = Intervals.of("2011-01-13/2011-01-14");
        SelectQuery query = newTestQuery().intervals(new org.apache.druid.query.spec.LegacySegmentSpec(interval)).filters(new org.apache.druid.query.filter.AndDimFilter(Arrays.asList(new SelectorDimFilter(QueryRunnerTestHelper.marketDimension, "spot", null), new org.apache.druid.query.filter.BoundDimFilter("expr", "11.1", null, false, false, null, null, StringComparators.NUMERIC)))).granularity(QueryRunnerTestHelper.allGran).dimensionSpecs(DefaultDimensionSpec.toSpec(QueryRunnerTestHelper.qualityDimension)).metrics(Collections.singletonList(QueryRunnerTestHelper.indexMetric)).pagingSpec(new PagingSpec(null, 10, true)).virtualColumns(new org.apache.druid.segment.virtual.ExpressionVirtualColumn("expr", "index / 10.0", ValueType.FLOAT, TestExprMacroTable.INSTANCE)).build();
        HashMap<String, Object> context = new HashMap<>();
        Iterable<Result<SelectResultValue>> results = runner.run(QueryPlus.wrap(query), context).toList();
        final List<List<Map<String, Object>>> events = // filtered values with all granularity
        toEvents(new String[]{ (timestampKey) + ":TIME", null, (QueryRunnerTestHelper.qualityDimension) + ":STRING", null, null, (QueryRunnerTestHelper.indexMetric) + ":FLOAT" }, new String[]{ "2011-01-13T00:00:00.000Z\tspot\thealth\tpreferred\th\u0001preferred\t114.947403", "2011-01-13T00:00:00.000Z\tspot\ttechnology\tpreferred\tt\u0001preferred\t111.356672" });
        String segmentIdInThisQuery = QueryRunnerTestHelper.segmentId.withInterval(interval).toString();
        PagingOffset offset = query.getPagingOffset(segmentIdInThisQuery);
        List<Result<SelectResultValue>> expectedResults = toExpected(segmentIdInThisQuery, events, Collections.singletonList("quality"), Collections.singletonList("index"), offset.startOffset(), offset.threshold());
        SelectQueryRunnerTest.verify(expectedResults, results);
    }

    @Test
    public void testSelectWithFilterLookupExtractionFn() {
        Map<String, String> extractionMap = new HashMap<>();
        extractionMap.put("total_market", "replaced");
        MapLookupExtractor mapLookupExtractor = new MapLookupExtractor(extractionMap, false);
        LookupExtractionFn lookupExtractionFn = new LookupExtractionFn(mapLookupExtractor, false, null, true, true);
        SelectQuery query = newTestQuery().intervals(SelectQueryRunnerTest.I_0112_0114_SPEC).filters(new SelectorDimFilter(QueryRunnerTestHelper.marketDimension, "replaced", lookupExtractionFn)).granularity(QueryRunnerTestHelper.dayGran).dimensionSpecs(DefaultDimensionSpec.toSpec(QueryRunnerTestHelper.qualityDimension)).metrics(Collections.singletonList(QueryRunnerTestHelper.indexMetric)).build();
        Iterable<Result<SelectResultValue>> results = runner.run(QueryPlus.wrap(query), new HashMap()).toList();
        Iterable<Result<SelectResultValue>> resultsOptimize = SelectQueryRunnerTest.toolChest.postMergeQueryDecoration(SelectQueryRunnerTest.toolChest.mergeResults(SelectQueryRunnerTest.toolChest.preMergeQueryDecoration(runner))).run(QueryPlus.wrap(query), new HashMap()).toList();
        final List<List<Map<String, Object>>> events = // filtered values with day granularity
        toEvents(new String[]{ (timestampKey) + ":TIME", null, (QueryRunnerTestHelper.qualityDimension) + ":STRING", null, null, (QueryRunnerTestHelper.indexMetric) + ":FLOAT" }, new String[]{ "2011-01-12T00:00:00.000Z\ttotal_market\tmezzanine\tpreferred\tm\u0001preferred\t1000.000000", "2011-01-12T00:00:00.000Z\ttotal_market\tpremium\tpreferred\tp\u0001preferred\t1000.000000" }, new String[]{ "2011-01-13T00:00:00.000Z\ttotal_market\tmezzanine\tpreferred\tm\u0001preferred\t1040.945505", "2011-01-13T00:00:00.000Z\ttotal_market\tpremium\tpreferred\tp\u0001preferred\t1689.012875" });
        PagingOffset offset = query.getPagingOffset(SelectQueryRunnerTest.segmentIdString);
        List<Result<SelectResultValue>> expectedResults = toExpected(SelectQueryRunnerTest.segmentIdString, events, Collections.singletonList(QueryRunnerTestHelper.qualityDimension), Collections.singletonList(QueryRunnerTestHelper.indexMetric), offset.startOffset(), offset.threshold());
        SelectQueryRunnerTest.verify(expectedResults, results);
        SelectQueryRunnerTest.verify(expectedResults, resultsOptimize);
    }

    @Test
    public void testFullSelectNoResults() {
        SelectQuery query = newTestQuery().intervals(SelectQueryRunnerTest.I_0112_0114_SPEC).filters(new org.apache.druid.query.filter.AndDimFilter(Arrays.asList(new SelectorDimFilter(QueryRunnerTestHelper.marketDimension, "spot", null), new SelectorDimFilter(QueryRunnerTestHelper.marketDimension, "foo", null)))).build();
        Iterable<Result<SelectResultValue>> results = runner.run(QueryPlus.wrap(query), new HashMap()).toList();
        List<Result<SelectResultValue>> expectedResults = Collections.singletonList(new Result<SelectResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new SelectResultValue(ImmutableMap.of(), Sets.newHashSet("market", "quality", "qualityLong", "qualityFloat", "qualityDouble", "qualityNumericString", "placement", "placementish", "partial_null_column", "null_column"), Sets.newHashSet("index", "quality_uniques", "indexMin", "indexMaxPlusTen", "indexMinFloat", "indexFloat", "indexMaxFloat"), new ArrayList())));
        SelectQueryRunnerTest.verify(expectedResults, SelectQueryRunnerTest.populateNullColumnAtLastForQueryableIndexCase(results, "null_column"));
    }

    @Test
    public void testFullSelectNoDimensionAndMetric() {
        SelectQuery query = newTestQuery().intervals(SelectQueryRunnerTest.I_0112_0114_SPEC).dimensionSpecs(DefaultDimensionSpec.toSpec("foo")).metrics(Collections.singletonList("foo2")).build();
        Iterable<Result<SelectResultValue>> results = runner.run(QueryPlus.wrap(query), new HashMap()).toList();
        final List<List<Map<String, Object>>> events = toEvents(new String[]{ (timestampKey) + ":TIME", "foo:NULL", "foo2:NULL" }, SelectQueryRunnerTest.V_0112_0114);
        PagingOffset offset = query.getPagingOffset(SelectQueryRunnerTest.segmentIdString);
        List<Result<SelectResultValue>> expectedResults = toExpected(SelectQueryRunnerTest.segmentIdString, events, Collections.singletonList("foo"), Collections.singletonList("foo2"), offset.startOffset(), offset.threshold());
        SelectQueryRunnerTest.verify(expectedResults, results);
    }

    @Test
    public void testFullOnSelectWithLongAndFloat() {
        List<DimensionSpec> dimSpecs = Arrays.asList(new DefaultDimensionSpec(QueryRunnerTestHelper.indexMetric, "floatIndex", ValueType.FLOAT), new DefaultDimensionSpec(ColumnHolder.TIME_COLUMN_NAME, "longTime", ValueType.LONG));
        SelectQuery query = newTestQuery().dimensionSpecs(dimSpecs).metrics(Arrays.asList(TIME_COLUMN_NAME, "index")).intervals(SelectQueryRunnerTest.I_0112_0114_SPEC).build();
        HashMap<String, Object> context = new HashMap<String, Object>();
        Iterable<Result<SelectResultValue>> results = runner.run(QueryPlus.wrap(query), context).toList();
        List<Result<SelectResultValue>> expectedResultsAsc = Collections.singletonList(new Result<SelectResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new SelectResultValue(ImmutableMap.of(SelectQueryRunnerTest.segmentIdString, 2), Sets.newHashSet("null_column", "floatIndex", "longTime"), Sets.newHashSet("__time", "index"), Arrays.asList(new EventHolder(SelectQueryRunnerTest.segmentIdString, 0, new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-01-12T00:00:00.000Z")).put("longTime", 1294790400000L).put("floatIndex", 100.0F).put(QueryRunnerTestHelper.indexMetric, 100.0F).put(TIME_COLUMN_NAME, 1294790400000L).build()), new EventHolder(SelectQueryRunnerTest.segmentIdString, 1, new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-01-12T00:00:00.000Z")).put("longTime", 1294790400000L).put("floatIndex", 100.0F).put(QueryRunnerTestHelper.indexMetric, 100.0F).put(TIME_COLUMN_NAME, 1294790400000L).build()), new EventHolder(SelectQueryRunnerTest.segmentIdString, 2, new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-01-12T00:00:00.000Z")).put("longTime", 1294790400000L).put("floatIndex", 100.0F).put(QueryRunnerTestHelper.indexMetric, 100.0F).put(TIME_COLUMN_NAME, 1294790400000L).build())))));
        List<Result<SelectResultValue>> expectedResultsDsc = Collections.singletonList(new Result<SelectResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new SelectResultValue(ImmutableMap.of(SelectQueryRunnerTest.segmentIdString, (-3)), Sets.newHashSet("null_column", "floatIndex", "longTime"), Sets.newHashSet("__time", "index"), Arrays.asList(new EventHolder(SelectQueryRunnerTest.segmentIdString, (-1), new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-01-13T00:00:00.000Z")).put("longTime", 1294876800000L).put("floatIndex", 1564.6177F).put(QueryRunnerTestHelper.indexMetric, 1564.6177F).put(TIME_COLUMN_NAME, 1294876800000L).build()), new EventHolder(SelectQueryRunnerTest.segmentIdString, (-2), new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-01-13T00:00:00.000Z")).put("longTime", 1294876800000L).put("floatIndex", 826.0602F).put(QueryRunnerTestHelper.indexMetric, 826.0602F).put(TIME_COLUMN_NAME, 1294876800000L).build()), new EventHolder(SelectQueryRunnerTest.segmentIdString, (-3), new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-01-13T00:00:00.000Z")).put("longTime", 1294876800000L).put("floatIndex", 1689.0128F).put(QueryRunnerTestHelper.indexMetric, 1689.0128F).put(TIME_COLUMN_NAME, 1294876800000L).build())))));
        SelectQueryRunnerTest.verify((descending ? expectedResultsDsc : expectedResultsAsc), SelectQueryRunnerTest.populateNullColumnAtLastForQueryableIndexCase(results, "null_column"));
    }

    @Test
    public void testFullOnSelectWithLongAndFloatWithExFn() {
        String jsFn = "function(str) { return 'super-' + str; }";
        ExtractionFn jsExtractionFn = new org.apache.druid.query.extraction.JavaScriptExtractionFn(jsFn, false, JavaScriptConfig.getEnabledInstance());
        List<DimensionSpec> dimSpecs = Arrays.asList(new ExtractionDimensionSpec(QueryRunnerTestHelper.indexMetric, "floatIndex", jsExtractionFn), new ExtractionDimensionSpec(ColumnHolder.TIME_COLUMN_NAME, "longTime", jsExtractionFn));
        SelectQuery query = newTestQuery().dimensionSpecs(dimSpecs).metrics(Arrays.asList(TIME_COLUMN_NAME, "index")).intervals(SelectQueryRunnerTest.I_0112_0114_SPEC).build();
        HashMap<String, Object> context = new HashMap<String, Object>();
        Iterable<Result<SelectResultValue>> results = runner.run(QueryPlus.wrap(query), context).toList();
        List<Result<SelectResultValue>> expectedResultsAsc = Collections.singletonList(new Result<SelectResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new SelectResultValue(ImmutableMap.of(SelectQueryRunnerTest.segmentIdString, 2), Sets.newHashSet("null_column", "floatIndex", "longTime"), Sets.newHashSet("__time", "index"), Arrays.asList(new EventHolder(SelectQueryRunnerTest.segmentIdString, 0, new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-01-12T00:00:00.000Z")).put("longTime", "super-1294790400000").put("floatIndex", "super-100").put(QueryRunnerTestHelper.indexMetric, 100.0F).put(TIME_COLUMN_NAME, 1294790400000L).build()), new EventHolder(SelectQueryRunnerTest.segmentIdString, 1, new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-01-12T00:00:00.000Z")).put("longTime", "super-1294790400000").put("floatIndex", "super-100").put(QueryRunnerTestHelper.indexMetric, 100.0F).put(TIME_COLUMN_NAME, 1294790400000L).build()), new EventHolder(SelectQueryRunnerTest.segmentIdString, 2, new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-01-12T00:00:00.000Z")).put("longTime", "super-1294790400000").put("floatIndex", "super-100").put(QueryRunnerTestHelper.indexMetric, 100.0F).put(TIME_COLUMN_NAME, 1294790400000L).build())))));
        List<Result<SelectResultValue>> expectedResultsDsc = Collections.singletonList(new Result<SelectResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new SelectResultValue(ImmutableMap.of(SelectQueryRunnerTest.segmentIdString, (-3)), Sets.newHashSet("null_column", "floatIndex", "longTime"), Sets.newHashSet("__time", "index"), Arrays.asList(new EventHolder(SelectQueryRunnerTest.segmentIdString, (-1), new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-01-13T00:00:00.000Z")).put("longTime", "super-1294876800000").put("floatIndex", "super-1564.617729").put(QueryRunnerTestHelper.indexMetric, 1564.6177F).put(TIME_COLUMN_NAME, 1294876800000L).build()), new EventHolder(SelectQueryRunnerTest.segmentIdString, (-2), new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-01-13T00:00:00.000Z")).put("longTime", "super-1294876800000").put("floatIndex", "super-826.060182").put(QueryRunnerTestHelper.indexMetric, 826.0602F).put(TIME_COLUMN_NAME, 1294876800000L).build()), new EventHolder(SelectQueryRunnerTest.segmentIdString, (-3), new ImmutableMap.Builder<String, Object>().put(timestampKey, DateTimes.of("2011-01-13T00:00:00.000Z")).put("longTime", "super-1294876800000").put("floatIndex", "super-1689.012875").put(QueryRunnerTestHelper.indexMetric, 1689.0128F).put(TIME_COLUMN_NAME, 1294876800000L).build())))));
        SelectQueryRunnerTest.verify((descending ? expectedResultsDsc : expectedResultsAsc), SelectQueryRunnerTest.populateNullColumnAtLastForQueryableIndexCase(results, "null_column"));
    }
}

