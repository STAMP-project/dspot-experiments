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


import Druids.SelectQueryBuilder;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.query.Druids;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerFactory;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.Result;
import org.apache.druid.query.TableDataSource;
import org.apache.druid.query.dimension.DefaultDimensionSpec;
import org.apache.druid.segment.Segment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class MultiSegmentSelectQueryTest {
    private static final Supplier<SelectQueryConfig> configSupplier = Suppliers.ofInstance(new SelectQueryConfig(true));

    private static final SelectQueryQueryToolChest toolChest = new SelectQueryQueryToolChest(new DefaultObjectMapper(), QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator(), MultiSegmentSelectQueryTest.configSupplier);

    private static final QueryRunnerFactory factory = new SelectQueryRunnerFactory(MultiSegmentSelectQueryTest.toolChest, new SelectQueryEngine(), QueryRunnerTestHelper.NOOP_QUERYWATCHER);

    // time modified version of druid.sample.numeric.tsv
    public static final String[] V_0112 = new String[]{ "2011-01-12T00:00:00.000Z\tspot\tautomotive\t1000\t10000.0\t10000.0\t100000\tpreferred\ta\u0001preferred\t100.000000", "2011-01-12T01:00:00.000Z\tspot\tbusiness\t1100\t11000.0\t11000.0\t110000\tpreferred\tb\u0001preferred\t100.000000", "2011-01-12T02:00:00.000Z\tspot\tentertainment\t1200\t12000.0\t12000.0\t120000\tpreferred\te\u0001preferred\t100.000000", "2011-01-12T03:00:00.000Z\tspot\thealth\t1300\t13000.0\t13000.0\t130000\tpreferred\th\u0001preferred\t100.000000", "2011-01-12T04:00:00.000Z\tspot\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t100.000000", "2011-01-12T05:00:00.000Z\tspot\tnews\t1500\t15000.0\t15000.0\t150000\tpreferred\tn\u0001preferred\t100.000000", "2011-01-12T06:00:00.000Z\tspot\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t100.000000", "2011-01-12T07:00:00.000Z\tspot\ttechnology\t1700\t17000.0\t17000.0\t170000\tpreferred\tt\u0001preferred\t100.000000", "2011-01-12T08:00:00.000Z\tspot\ttravel\t1800\t18000.0\t18000.0\t180000\tpreferred\tt\u0001preferred\t100.000000", "2011-01-12T09:00:00.000Z\ttotal_market\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t1000.000000", "2011-01-12T10:00:00.000Z\ttotal_market\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t1000.000000", "2011-01-12T11:00:00.000Z\tupfront\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t800.000000\tvalue", "2011-01-12T12:00:00.000Z\tupfront\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t800.000000\tvalue" };

    public static final String[] V_0113 = new String[]{ "2011-01-13T00:00:00.000Z\tspot\tautomotive\t1000\t10000.0\t10000.0\t100000\tpreferred\ta\u0001preferred\t94.874713", "2011-01-13T01:00:00.000Z\tspot\tbusiness\t1100\t11000.0\t11000.0\t110000\tpreferred\tb\u0001preferred\t103.629399", "2011-01-13T02:00:00.000Z\tspot\tentertainment\t1200\t12000.0\t12000.0\t120000\tpreferred\te\u0001preferred\t110.087299", "2011-01-13T03:00:00.000Z\tspot\thealth\t1300\t13000.0\t13000.0\t130000\tpreferred\th\u0001preferred\t114.947403", "2011-01-13T04:00:00.000Z\tspot\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t104.465767", "2011-01-13T05:00:00.000Z\tspot\tnews\t1500\t15000.0\t15000.0\t150000\tpreferred\tn\u0001preferred\t102.851683", "2011-01-13T06:00:00.000Z\tspot\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t108.863011", "2011-01-13T07:00:00.000Z\tspot\ttechnology\t1700\t17000.0\t17000.0\t170000\tpreferred\tt\u0001preferred\t111.356672", "2011-01-13T08:00:00.000Z\tspot\ttravel\t1800\t18000.0\t18000.0\t180000\tpreferred\tt\u0001preferred\t106.236928", "2011-01-13T09:00:00.000Z\ttotal_market\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t1040.945505", "2011-01-13T10:00:00.000Z\ttotal_market\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t1689.012875", "2011-01-13T11:00:00.000Z\tupfront\tmezzanine\t1400\t14000.0\t14000.0\t140000\tpreferred\tm\u0001preferred\t826.060182\tvalue", "2011-01-13T12:00:00.000Z\tupfront\tpremium\t1600\t16000.0\t16000.0\t160000\tpreferred\tp\u0001preferred\t1564.617729\tvalue" };

    public static final String[] V_OVERRIDE = new String[]{ "2011-01-12T04:00:00.000Z\tspot\tautomotive\t1000\t10000.0\t10000.0\t100000\tpreferred\ta\u0001preferred\t999.000000", "2011-01-12T05:00:00.000Z\tspot\tbusiness\t1100\t11000.0\t11000.0\t110000\tpreferred\tb\u0001preferred\t999.000000", "2011-01-12T06:00:00.000Z\tspot\tentertainment\t1200\t12000.0\t12000.0\t120000\tpreferred\te\u0001preferred\t999.000000", "2011-01-12T07:00:00.000Z\tspot\thealth\t1300\t13000.0\t13000.0\t130000\tpreferred\th\u0001preferred\t999.000000" };

    private static Segment segment0;

    private static Segment segment1;

    private static Segment segment_override;// this makes segment0 split into three logical segments


    private static List<String> segmentIdentifiers;

    private static QueryRunner runner;

    private final boolean fromNext;

    public MultiSegmentSelectQueryTest(boolean fromNext) {
        this.fromNext = fromNext;
    }

    @Test
    public void testAllGranularity() {
        runAllGranularityTest(newBuilder().build(), new int[][]{ new int[]{ 2, -1, -1, -1, 3 }, new int[]{ 3, 1, -1, -1, 3 }, new int[]{ -1, 3, 0, -1, 3 }, new int[]{ -1, -1, 3, -1, 3 }, new int[]{ -1, -1, 4, 1, 3 }, new int[]{ -1, -1, -1, 4, 3 }, new int[]{ -1, -1, -1, 7, 3 }, new int[]{ -1, -1, -1, 10, 3 }, new int[]{ -1, -1, -1, 12, 2 }, new int[]{ -1, -1, -1, 13, 0 } });
        runAllGranularityTest(newBuilder().descending(true).build(), new int[][]{ new int[]{ 0, 0, 0, -3, 3 }, new int[]{ 0, 0, 0, -6, 3 }, new int[]{ 0, 0, 0, -9, 3 }, new int[]{ 0, 0, 0, -12, 3 }, new int[]{ 0, 0, -2, -13, 3 }, new int[]{ 0, 0, -5, 0, 3 }, new int[]{ 0, -3, 0, 0, 3 }, new int[]{ -2, -4, 0, 0, 3 }, new int[]{ -4, 0, 0, 0, 2 }, new int[]{ -5, 0, 0, 0, 0 } });
    }

    @Test
    public void testDayGranularity() {
        runDayGranularityTest(newBuilder().granularity(QueryRunnerTestHelper.dayGran).build(), new int[][]{ new int[]{ 2, -1, -1, 2, 3, 0, 0, 3 }, new int[]{ 3, 1, -1, 5, 1, 2, 0, 3 }, new int[]{ -1, 3, 0, 8, 0, 2, 1, 3 }, new int[]{ -1, -1, 3, 11, 0, 0, 3, 3 }, new int[]{ -1, -1, 4, 12, 0, 0, 1, 1 }, new int[]{ -1, -1, 5, 13, 0, 0, 0, 0 } });
        runDayGranularityTest(newBuilder().granularity(QueryRunnerTestHelper.dayGran).descending(true).build(), new int[][]{ new int[]{ 0, 0, -3, -3, 0, 0, 3, 3 }, new int[]{ 0, -1, -5, -6, 0, 1, 2, 3 }, new int[]{ 0, -4, 0, -9, 0, 3, 0, 3 }, new int[]{ -3, 0, 0, -12, 3, 0, 0, 3 }, new int[]{ -4, 0, 0, -13, 1, 0, 0, 1 }, new int[]{ -5, 0, 0, -14, 0, 0, 0, 0 } });
    }

    @Test
    public void testPagingIdentifiersForUnionDatasource() {
        Druids.SelectQueryBuilder selectQueryBuilder = Druids.newSelectQueryBuilder().dataSource(new org.apache.druid.query.UnionDataSource(ImmutableList.of(new TableDataSource(QueryRunnerTestHelper.dataSource), new TableDataSource("testing-2")))).intervals(SelectQueryRunnerTest.I_0112_0114_SPEC).granularity(QueryRunnerTestHelper.allGran).dimensionSpecs(DefaultDimensionSpec.toSpec(QueryRunnerTestHelper.dimensions)).pagingSpec(PagingSpec.newSpec(3));
        SelectQuery query = selectQueryBuilder.build();
        QueryRunner unionQueryRunner = new org.apache.druid.query.UnionQueryRunner(MultiSegmentSelectQueryTest.runner);
        List<Result<SelectResultValue>> results = unionQueryRunner.run(QueryPlus.wrap(query), ImmutableMap.of()).toList();
        Map<String, Integer> pagingIdentifiers = results.get(0).getValue().getPagingIdentifiers();
        query = query.withPagingSpec(toNextCursor(PagingSpec.merge(Collections.singletonList(pagingIdentifiers)), query, 3));
        unionQueryRunner.run(QueryPlus.wrap(query), ImmutableMap.of()).toList();
    }
}

