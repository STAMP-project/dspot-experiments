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
package org.apache.druid.client;


import Granularities.ALL;
import QueryRunnerTestHelper.UNIQUES_9;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.io.Closeable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.druid.client.cache.CachePopulator;
import org.apache.druid.client.cache.CachePopulatorStats;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.concurrent.Execs;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.QueryToolChest;
import org.apache.druid.query.Result;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.aggregation.LongSumAggregatorFactory;
import org.apache.druid.query.timeseries.TimeseriesQuery;
import org.apache.druid.query.topn.TopNQueryBuilder;
import org.apache.druid.query.topn.TopNQueryConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class CachingQueryRunnerTest {
    private static final List<AggregatorFactory> AGGS = Arrays.asList(new CountAggregatorFactory("rows"), new LongSumAggregatorFactory("imps", "imps"), new LongSumAggregatorFactory("impers", "imps"));

    private static final Object[] objects = new Object[]{ DateTimes.of("2011-01-05"), "a", 50, 4994, "b", 50, 4993, "c", 50, 4992, DateTimes.of("2011-01-06"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-07"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-08"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-09"), "a", 50, 4985, "b", 50, 4984, "c", 50, 4983 };

    private ObjectMapper objectMapper;

    private CachePopulator cachePopulator;

    public CachingQueryRunnerTest(int numBackgroundThreads) {
        objectMapper = new DefaultObjectMapper();
        if (numBackgroundThreads > 0) {
            cachePopulator = new org.apache.druid.client.cache.BackgroundCachePopulator(Execs.multiThreaded(numBackgroundThreads, "CachingQueryRunnerTest-%d"), objectMapper, new CachePopulatorStats(), (-1));
        } else {
            cachePopulator = new org.apache.druid.client.cache.ForegroundCachePopulator(objectMapper, new CachePopulatorStats(), (-1));
        }
    }

    @Test
    public void testCloseAndPopulate() throws Exception {
        List<Result> expectedRes = makeTopNResults(false, CachingQueryRunnerTest.objects);
        List<Result> expectedCacheRes = makeTopNResults(true, CachingQueryRunnerTest.objects);
        TopNQueryBuilder builder = new TopNQueryBuilder().dataSource("ds").dimension("top_dim").metric("imps").threshold(3).intervals("2011-01-05/2011-01-10").aggregators(CachingQueryRunnerTest.AGGS).granularity(ALL);
        QueryToolChest toolchest = new org.apache.druid.query.topn.TopNQueryQueryToolChest(new TopNQueryConfig(), QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator());
        testCloseAndPopulate(expectedRes, expectedCacheRes, builder.build(), toolchest);
        testUseCache(expectedCacheRes, builder.build(), toolchest);
    }

    @Test
    public void testTimeseries() throws Exception {
        for (boolean descending : new boolean[]{ false, true }) {
            TimeseriesQuery query = org.apache.druid.query.Druids.newTimeseriesQueryBuilder().dataSource(QueryRunnerTestHelper.dataSource).granularity(QueryRunnerTestHelper.dayGran).intervals(QueryRunnerTestHelper.firstToThird).aggregators(Arrays.asList(QueryRunnerTestHelper.rowsCount, new LongSumAggregatorFactory("idx", "index"), QueryRunnerTestHelper.qualityUniques)).descending(descending).build();
            Result row1 = new Result(DateTimes.of("2011-04-01"), new org.apache.druid.query.timeseries.TimeseriesResultValue(ImmutableMap.of("rows", 13L, "idx", 6619L, "uniques", UNIQUES_9)));
            Result row2 = new Result(DateTimes.of("2011-04-02"), new org.apache.druid.query.timeseries.TimeseriesResultValue(ImmutableMap.of("rows", 13L, "idx", 5827L, "uniques", UNIQUES_9)));
            List<Result> expectedResults;
            if (descending) {
                expectedResults = Lists.newArrayList(row2, row1);
            } else {
                expectedResults = Lists.newArrayList(row1, row2);
            }
            QueryToolChest toolChest = new org.apache.druid.query.timeseries.TimeseriesQueryQueryToolChest(QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator());
            testCloseAndPopulate(expectedResults, expectedResults, query, toolChest);
            testUseCache(expectedResults, query, toolChest);
        }
    }

    private static class AssertingClosable implements Closeable {
        private final AtomicBoolean closed = new AtomicBoolean(false);

        @Override
        public void close() {
            Assert.assertFalse(closed.get());
            Assert.assertTrue(closed.compareAndSet(false, true));
        }

        public boolean isClosed() {
            return closed.get();
        }
    }
}

