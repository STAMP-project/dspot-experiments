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
package org.apache.druid.query.scan;


import ColumnHolder.TIME_COLUMN_NAME;
import ScanQuery.RESULT_FORMAT_COMPACTED_LIST;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.query.DefaultGenericQueryMetricsFactory;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.expression.TestExprMacroTable;
import org.apache.druid.query.extraction.MapLookupExtractor;
import org.apache.druid.query.filter.SelectorDimFilter;
import org.apache.druid.query.lookup.LookupExtractionFn;
import org.apache.druid.query.spec.QuerySegmentSpec;
import org.apache.druid.segment.VirtualColumn;
import org.apache.druid.segment.column.ValueType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class ScanQueryRunnerTest {
    private static final VirtualColumn EXPR_COLUMN = new org.apache.druid.segment.virtual.ExpressionVirtualColumn("expr", "index * 2", ValueType.LONG, TestExprMacroTable.INSTANCE);

    // copied from druid.sample.numeric.tsv
    public static final String[] V_0112 = new String[]{ "2011-01-12T00:00:00.000Z\tspot\tautomotive\t1000\t10000.0\t10000.0\t100000\tpreferred\ta\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tbusiness\t1100\t11000.0\t11000.0\t110000\tpreferred\tb\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tentertainment\t1200\t12000.0\t12000.0\t120000\tpreferred\te\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\thealth\t1300\t13000.0\t13000.0\t130000\tpreferred\th\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tnews\t1500\t15000.0\t15000.0\t150000\tpreferred\tn\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\ttechnology\t1700\t17000.0\t17000.0\t170000\tpreferred\tt\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\ttravel\t1800\t18000.0\t18000.0\t180000\tpreferred\tt\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\ttotal_market\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t1000.000000", "2011-01-12T00:00:00.000Z\ttotal_market\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t1000.000000", "2011-01-12T00:00:00.000Z\tupfront\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t800.000000\tvalue", "2011-01-12T00:00:00.000Z\tupfront\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t800.000000\tvalue" };

    public static final String[] V_0113 = new String[]{ "2011-01-13T00:00:00.000Z\tspot\tautomotive\t1000\t10000.0\t10000.0\t100000\tpreferred\ta\u0001preferred\t94.874713", "2011-01-13T00:00:00.000Z\tspot\tbusiness\t1100\t11000.0\t11000.0\t110000\tpreferred\tb\u0001preferred\t103.629399", "2011-01-13T00:00:00.000Z\tspot\tentertainment\t1200\t12000.0\t12000.0\t120000\tpreferred\te\u0001preferred\t110.087299", "2011-01-13T00:00:00.000Z\tspot\thealth\t1300\t13000.0\t13000.0\t130000\tpreferred\th\u0001preferred\t114.947403", "2011-01-13T00:00:00.000Z\tspot\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t104.465767", "2011-01-13T00:00:00.000Z\tspot\tnews\t1500\t15000.0\t15000.0\t150000\tpreferred\tn\u0001preferred\t102.851683", "2011-01-13T00:00:00.000Z\tspot\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t108.863011", "2011-01-13T00:00:00.000Z\tspot\ttechnology\t1700\t17000.0\t17000.0\t170000\tpreferred\tt\u0001preferred\t111.356672", "2011-01-13T00:00:00.000Z\tspot\ttravel\t1800\t18000.0\t18000.0\t180000\tpreferred\tt\u0001preferred\t106.236928", "2011-01-13T00:00:00.000Z\ttotal_market\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t1040.945505", "2011-01-13T00:00:00.000Z\ttotal_market\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t1689.012875", "2011-01-13T00:00:00.000Z\tupfront\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t826.060182\tvalue", "2011-01-13T00:00:00.000Z\tupfront\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t1564.617729\tvalue" };

    public static final QuerySegmentSpec I_0112_0114 = new org.apache.druid.query.spec.LegacySegmentSpec(Intervals.of("2011-01-12T00:00:00.000Z/2011-01-14T00:00:00.000Z"));

    public static final String[] V_0112_0114 = ObjectArrays.concat(ScanQueryRunnerTest.V_0112, ScanQueryRunnerTest.V_0113, String.class);

    private static final ScanQueryQueryToolChest toolChest = new ScanQueryQueryToolChest(new ScanQueryConfig(), DefaultGenericQueryMetricsFactory.instance());

    private final QueryRunner runner;

    private final boolean legacy;

    public ScanQueryRunnerTest(final QueryRunner runner, final boolean legacy) {
        this.runner = runner;
        this.legacy = legacy;
    }

    @Test
    public void testFullOnSelect() {
        List<String> columns = Lists.newArrayList(getTimestampName(), "expr", "market", "quality", "qualityLong", "qualityFloat", "qualityDouble", "qualityNumericString", "placement", "placementish", "partial_null_column", "null_column", "index", "indexMin", "indexMaxPlusTen", "quality_uniques", "indexFloat", "indexMaxFloat", "indexMinFloat");
        ScanQuery query = newTestQuery().intervals(ScanQueryRunnerTest.I_0112_0114).virtualColumns(ScanQueryRunnerTest.EXPR_COLUMN).build();
        HashMap<String, Object> context = new HashMap<String, Object>();
        Iterable<ScanResultValue> results = runner.run(QueryPlus.wrap(query), context).toList();
        List<ScanResultValue> expectedResults = toExpected(toFullEvents(ScanQueryRunnerTest.V_0112_0114), columns, 0, 3);
        ScanQueryRunnerTest.verify(expectedResults, ScanQueryRunnerTest.populateNullColumnAtLastForQueryableIndexCase(results, "null_column"));
    }

    @Test
    public void testFullOnSelectAsCompactedList() {
        final List<String> columns = Lists.newArrayList(getTimestampName(), "expr", "market", "quality", "qualityLong", "qualityFloat", "qualityDouble", "qualityNumericString", "placement", "placementish", "partial_null_column", "null_column", "index", "indexMin", "indexMaxPlusTen", "quality_uniques", "indexFloat", "indexMaxFloat", "indexMinFloat");
        ScanQuery query = newTestQuery().intervals(ScanQueryRunnerTest.I_0112_0114).virtualColumns(ScanQueryRunnerTest.EXPR_COLUMN).resultFormat(RESULT_FORMAT_COMPACTED_LIST).build();
        HashMap<String, Object> context = new HashMap<String, Object>();
        Iterable<ScanResultValue> results = runner.run(QueryPlus.wrap(query), context).toList();
        List<ScanResultValue> expectedResults = toExpected(toFullEvents(ScanQueryRunnerTest.V_0112_0114), columns, 0, 3);
        ScanQueryRunnerTest.verify(expectedResults, ScanQueryRunnerTest.populateNullColumnAtLastForQueryableIndexCase(compactedListToRow(results), "null_column"));
    }

    @Test
    public void testSelectWithUnderscoreUnderscoreTime() {
        ScanQuery query = newTestQuery().intervals(ScanQueryRunnerTest.I_0112_0114).columns(TIME_COLUMN_NAME, QueryRunnerTestHelper.marketDimension, QueryRunnerTestHelper.indexMetric).build();
        HashMap<String, Object> context = new HashMap<String, Object>();
        Iterable<ScanResultValue> results = runner.run(QueryPlus.wrap(query), context).toList();
        final List<List<Map<String, Object>>> expectedEvents = toEvents(new String[]{ (getTimestampName()) + ":TIME", (QueryRunnerTestHelper.marketDimension) + ":STRING", null, null, null, null, null, null, null, (QueryRunnerTestHelper.indexMetric) + ":DOUBLE" }, ScanQueryRunnerTest.V_0112_0114);
        // Add "__time" to all the expected events in legacy mode
        if (legacy) {
            for (List<Map<String, Object>> batch : expectedEvents) {
                for (Map<String, Object> event : batch) {
                    event.put("__time", getMillis());
                }
            }
        }
        List<ScanResultValue> expectedResults = toExpected(expectedEvents, (legacy ? Lists.newArrayList(getTimestampName(), "__time", "market", "index") : Lists.newArrayList("__time", "market", "index")), 0, 3);
        ScanQueryRunnerTest.verify(expectedResults, results);
    }

    @Test
    public void testSelectWithDimsAndMets() {
        ScanQuery query = newTestQuery().intervals(ScanQueryRunnerTest.I_0112_0114).columns(QueryRunnerTestHelper.marketDimension, QueryRunnerTestHelper.indexMetric).build();
        HashMap<String, Object> context = new HashMap<String, Object>();
        Iterable<ScanResultValue> results = runner.run(QueryPlus.wrap(query), context).toList();
        List<ScanResultValue> expectedResults = toExpected(toEvents(new String[]{ legacy ? (getTimestampName()) + ":TIME" : null, (QueryRunnerTestHelper.marketDimension) + ":STRING", null, null, null, null, null, null, null, (QueryRunnerTestHelper.indexMetric) + ":DOUBLE" }, ScanQueryRunnerTest.V_0112_0114), (legacy ? Lists.newArrayList(getTimestampName(), "market", "index") : Lists.newArrayList("market", "index")), 0, 3);
        ScanQueryRunnerTest.verify(expectedResults, results);
    }

    @Test
    public void testSelectWithDimsAndMetsAsCompactedList() {
        ScanQuery query = newTestQuery().intervals(ScanQueryRunnerTest.I_0112_0114).columns(QueryRunnerTestHelper.marketDimension, QueryRunnerTestHelper.indexMetric).resultFormat(RESULT_FORMAT_COMPACTED_LIST).build();
        HashMap<String, Object> context = new HashMap<String, Object>();
        Iterable<ScanResultValue> results = runner.run(QueryPlus.wrap(query), context).toList();
        List<ScanResultValue> expectedResults = toExpected(toEvents(new String[]{ legacy ? (getTimestampName()) + ":TIME" : null, (QueryRunnerTestHelper.marketDimension) + ":STRING", null, null, null, null, null, null, null, (QueryRunnerTestHelper.indexMetric) + ":DOUBLE" }, ScanQueryRunnerTest.V_0112_0114), (legacy ? Lists.newArrayList(getTimestampName(), "market", "index") : Lists.newArrayList("market", "index")), 0, 3);
        ScanQueryRunnerTest.verify(expectedResults, compactedListToRow(results));
    }

    @Test
    public void testFullOnSelectWithFilterAndLimit() {
        // limits
        for (int limit : new int[]{ 3, 1, 5, 7, 0 }) {
            ScanQuery query = newTestQuery().intervals(ScanQueryRunnerTest.I_0112_0114).filters(new SelectorDimFilter(QueryRunnerTestHelper.marketDimension, "spot", null)).columns(QueryRunnerTestHelper.qualityDimension, QueryRunnerTestHelper.indexMetric).limit(limit).build();
            HashMap<String, Object> context = new HashMap<String, Object>();
            Iterable<ScanResultValue> results = runner.run(QueryPlus.wrap(query), context).toList();
            final List<List<Map<String, Object>>> events = // filtered values with day granularity
            toEvents(new String[]{ legacy ? (getTimestampName()) + ":TIME" : null, null, (QueryRunnerTestHelper.qualityDimension) + ":STRING", null, null, (QueryRunnerTestHelper.indexMetric) + ":DOUBLE" }, new String[]{ "2011-01-12T00:00:00.000Z\tspot\tautomotive\tpreferred\ta\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tbusiness\tpreferred\tb\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tentertainment\tpreferred\te\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\thealth\tpreferred\th\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tmezzanine\tpreferred\tm\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tnews\tpreferred\tn\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\tpremium\tpreferred\tp\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\ttechnology\tpreferred\tt\u0001preferred\t100.000000", "2011-01-12T00:00:00.000Z\tspot\ttravel\tpreferred\tt\u0001preferred\t100.000000" }, new String[]{ "2011-01-13T00:00:00.000Z\tspot\tautomotive\tpreferred\ta\u0001preferred\t94.874713", "2011-01-13T00:00:00.000Z\tspot\tbusiness\tpreferred\tb\u0001preferred\t103.629399", "2011-01-13T00:00:00.000Z\tspot\tentertainment\tpreferred\te\u0001preferred\t110.087299", "2011-01-13T00:00:00.000Z\tspot\thealth\tpreferred\th\u0001preferred\t114.947403", "2011-01-13T00:00:00.000Z\tspot\tmezzanine\tpreferred\tm\u0001preferred\t104.465767", "2011-01-13T00:00:00.000Z\tspot\tnews\tpreferred\tn\u0001preferred\t102.851683", "2011-01-13T00:00:00.000Z\tspot\tpremium\tpreferred\tp\u0001preferred\t108.863011", "2011-01-13T00:00:00.000Z\tspot\ttechnology\tpreferred\tt\u0001preferred\t111.356672", "2011-01-13T00:00:00.000Z\tspot\ttravel\tpreferred\tt\u0001preferred\t106.236928" });
            List<ScanResultValue> expectedResults = toExpected(events, (legacy ? Lists.newArrayList(getTimestampName(), "quality", "index") : Lists.newArrayList("quality", "index")), 0, limit);
            ScanQueryRunnerTest.verify(expectedResults, results);
        }
    }

    @Test
    public void testSelectWithFilterLookupExtractionFn() {
        Map<String, String> extractionMap = new HashMap<>();
        extractionMap.put("total_market", "replaced");
        MapLookupExtractor mapLookupExtractor = new MapLookupExtractor(extractionMap, false);
        LookupExtractionFn lookupExtractionFn = new LookupExtractionFn(mapLookupExtractor, false, null, true, true);
        ScanQuery query = newTestQuery().intervals(ScanQueryRunnerTest.I_0112_0114).filters(new SelectorDimFilter(QueryRunnerTestHelper.marketDimension, "replaced", lookupExtractionFn)).columns(QueryRunnerTestHelper.qualityDimension, QueryRunnerTestHelper.indexMetric).build();
        Iterable<ScanResultValue> results = runner.run(QueryPlus.wrap(query), new HashMap()).toList();
        Iterable<ScanResultValue> resultsOptimize = ScanQueryRunnerTest.toolChest.postMergeQueryDecoration(ScanQueryRunnerTest.toolChest.mergeResults(ScanQueryRunnerTest.toolChest.preMergeQueryDecoration(runner))).run(QueryPlus.wrap(query), new HashMap()).toList();
        final List<List<Map<String, Object>>> events = // filtered values with day granularity
        toEvents(new String[]{ legacy ? (getTimestampName()) + ":TIME" : null, null, (QueryRunnerTestHelper.qualityDimension) + ":STRING", null, null, (QueryRunnerTestHelper.indexMetric) + ":DOUBLE" }, new String[]{ "2011-01-12T00:00:00.000Z\ttotal_market\tmezzanine\tpreferred\tm\u0001preferred\t1000.000000", "2011-01-12T00:00:00.000Z\ttotal_market\tpremium\tpreferred\tp\u0001preferred\t1000.000000" }, new String[]{ "2011-01-13T00:00:00.000Z\ttotal_market\tmezzanine\tpreferred\tm\u0001preferred\t1040.945505", "2011-01-13T00:00:00.000Z\ttotal_market\tpremium\tpreferred\tp\u0001preferred\t1689.012875" });
        List<ScanResultValue> expectedResults = toExpected(events, (legacy ? Lists.newArrayList(getTimestampName(), QueryRunnerTestHelper.qualityDimension, QueryRunnerTestHelper.indexMetric) : Lists.newArrayList(QueryRunnerTestHelper.qualityDimension, QueryRunnerTestHelper.indexMetric)), 0, 3);
        ScanQueryRunnerTest.verify(expectedResults, results);
        ScanQueryRunnerTest.verify(expectedResults, resultsOptimize);
    }

    @Test
    public void testFullSelectNoResults() {
        ScanQuery query = newTestQuery().intervals(ScanQueryRunnerTest.I_0112_0114).filters(new org.apache.druid.query.filter.AndDimFilter(Arrays.asList(new SelectorDimFilter(QueryRunnerTestHelper.marketDimension, "spot", null), new SelectorDimFilter(QueryRunnerTestHelper.marketDimension, "foo", null)))).build();
        Iterable<ScanResultValue> results = runner.run(QueryPlus.wrap(query), new HashMap()).toList();
        List<ScanResultValue> expectedResults = Collections.emptyList();
        ScanQueryRunnerTest.verify(expectedResults, ScanQueryRunnerTest.populateNullColumnAtLastForQueryableIndexCase(results, "null_column"));
    }

    @Test
    public void testFullSelectNoDimensionAndMetric() {
        ScanQuery query = newTestQuery().intervals(ScanQueryRunnerTest.I_0112_0114).columns("foo", "foo2").build();
        Iterable<ScanResultValue> results = runner.run(QueryPlus.wrap(query), new HashMap()).toList();
        final List<List<Map<String, Object>>> events = toEvents((legacy ? new String[]{ (getTimestampName()) + ":TIME" } : new String[0]), ScanQueryRunnerTest.V_0112_0114);
        List<ScanResultValue> expectedResults = toExpected(events, (legacy ? Lists.newArrayList(getTimestampName(), "foo", "foo2") : Lists.newArrayList("foo", "foo2")), 0, 3);
        ScanQueryRunnerTest.verify(expectedResults, results);
    }
}

