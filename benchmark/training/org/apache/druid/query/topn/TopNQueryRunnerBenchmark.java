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
package org.apache.druid.query.topn;


import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.aggregation.DoubleMaxAggregatorFactory;
import org.apache.druid.query.aggregation.DoubleMinAggregatorFactory;
import org.apache.druid.timeline.SegmentId;
import org.junit.Test;


/**
 * Based on TopNQueryRunnerTest
 */
public class TopNQueryRunnerBenchmark extends AbstractBenchmark {
    public enum TestCases {

        rtIndex,
        mMappedTestIndex,
        mergedRealtimeIndex,
        rtIndexOffheap;}

    private static final String marketDimension = "market";

    private static final SegmentId segmentId = SegmentId.dummy("testSegment");

    private static final HashMap<String, Object> context = new HashMap<String, Object>();

    private static final TopNQuery query = new TopNQueryBuilder().dataSource(QueryRunnerTestHelper.dataSource).granularity(QueryRunnerTestHelper.allGran).dimension(TopNQueryRunnerBenchmark.marketDimension).metric(QueryRunnerTestHelper.indexMetric).threshold(4).intervals(QueryRunnerTestHelper.fullOnIntervalSpec).aggregators(Lists.newArrayList(Iterables.concat(QueryRunnerTestHelper.commonDoubleAggregators, Lists.newArrayList(new DoubleMaxAggregatorFactory("maxIndex", "index"), new DoubleMinAggregatorFactory("minIndex", "index"))))).postAggregators(Collections.singletonList(QueryRunnerTestHelper.addRowsIndexConstant)).build();

    private static final Map<TopNQueryRunnerBenchmark.TestCases, QueryRunner> testCaseMap = new HashMap<>();

    @BenchmarkOptions(warmupRounds = 10000, benchmarkRounds = 10000)
    @Test
    public void testmMapped() {
        TopNQueryRunnerBenchmark.testCaseMap.get(TopNQueryRunnerBenchmark.TestCases.mMappedTestIndex).run(QueryPlus.wrap(TopNQueryRunnerBenchmark.query), TopNQueryRunnerBenchmark.context);
    }
}

