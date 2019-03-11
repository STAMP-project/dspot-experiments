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


import Cache.NamedKey;
import Druids.SearchQueryBuilder;
import Druids.SelectQueryBuilder;
import Druids.TimeseriesQueryBuilder;
import GroupByQuery.Builder;
import GroupByStrategySelector.STRATEGY_V1;
import TimeBoundaryQuery.MAX_TIME;
import TimeBoundaryQuery.MIN_TIME;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ForwardingListeningExecutorService;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import javax.annotation.Nullable;
import org.apache.druid.client.cache.Cache;
import org.apache.druid.client.cache.CachePopulatorStats;
import org.apache.druid.client.selector.RandomServerSelectorStrategy;
import org.apache.druid.client.selector.ServerSelector;
import org.apache.druid.hll.HyperLogLogCollector;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.Pair;
import org.apache.druid.java.util.common.concurrent.Execs;
import org.apache.druid.java.util.common.granularity.Granularities;
import org.apache.druid.java.util.common.granularity.Granularity;
import org.apache.druid.java.util.common.guava.Sequence;
import org.apache.druid.java.util.common.guava.Sequences;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.query.Druids;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.QueryToolChestWarehouse;
import org.apache.druid.query.Result;
import org.apache.druid.query.SegmentDescriptor;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.aggregation.LongSumAggregatorFactory;
import org.apache.druid.query.aggregation.PostAggregator;
import org.apache.druid.query.aggregation.hyperloglog.HyperUniquesAggregatorFactory;
import org.apache.druid.query.aggregation.post.ConstantPostAggregator;
import org.apache.druid.query.aggregation.post.FieldAccessPostAggregator;
import org.apache.druid.query.dimension.DefaultDimensionSpec;
import org.apache.druid.query.filter.AndDimFilter;
import org.apache.druid.query.filter.DimFilter;
import org.apache.druid.query.filter.InDimFilter;
import org.apache.druid.query.filter.SelectorDimFilter;
import org.apache.druid.query.groupby.GroupByQuery;
import org.apache.druid.query.ordering.StringComparators;
import org.apache.druid.query.search.SearchQuery;
import org.apache.druid.query.search.SearchQueryConfig;
import org.apache.druid.query.select.SelectQuery;
import org.apache.druid.query.select.SelectQueryConfig;
import org.apache.druid.query.spec.MultipleIntervalSegmentSpec;
import org.apache.druid.query.spec.MultipleSpecificSegmentSpec;
import org.apache.druid.query.timeboundary.TimeBoundaryQuery;
import org.apache.druid.query.timeseries.TimeseriesQuery;
import org.apache.druid.query.timeseries.TimeseriesResultValue;
import org.apache.druid.query.topn.TopNQuery;
import org.apache.druid.query.topn.TopNQueryBuilder;
import org.apache.druid.query.topn.TopNQueryConfig;
import org.apache.druid.query.topn.TopNResultValue;
import org.apache.druid.segment.TestHelper;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.SegmentId;
import org.apache.druid.timeline.VersionedIntervalTimeline;
import org.apache.druid.timeline.partition.NoneShardSpec;
import org.apache.druid.timeline.partition.ShardSpec;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class CachingClusteredClientTest {
    private static final ImmutableMap<String, Object> CONTEXT = // GroupBy v2 won't cache on the broker, so test with v1.
    ImmutableMap.of("finalize", false, "groupByStrategy", STRATEGY_V1);

    private static final MultipleIntervalSegmentSpec SEG_SPEC = new MultipleIntervalSegmentSpec(ImmutableList.of());

    private static final String DATA_SOURCE = "test";

    private static final ObjectMapper JSON_MAPPER = CachingClusteredClientTestUtils.createObjectMapper();

    /**
     * We want a deterministic test, but we'd also like a bit of randomness for the distribution of segments
     * across servers.  Thus, we loop multiple times and each time use a deterministically created Random instance.
     * Increase this value to increase exposure to random situations at the expense of test run time.
     */
    private static final int RANDOMNESS = 10;

    private static final List<AggregatorFactory> AGGS = Arrays.asList(new CountAggregatorFactory("rows"), new LongSumAggregatorFactory("imps", "imps"), new LongSumAggregatorFactory("impers", "imps"));

    private static final List<PostAggregator> POST_AGGS = Arrays.asList(new org.apache.druid.query.aggregation.post.ArithmeticPostAggregator("avg_imps_per_row", "/", Arrays.asList(new FieldAccessPostAggregator("imps", "imps"), new FieldAccessPostAggregator("rows", "rows"))), new org.apache.druid.query.aggregation.post.ArithmeticPostAggregator("avg_imps_per_row_double", "*", Arrays.asList(new FieldAccessPostAggregator("avg_imps_per_row", "avg_imps_per_row"), new ConstantPostAggregator("constant", 2))), new org.apache.druid.query.aggregation.post.ArithmeticPostAggregator("avg_imps_per_row_half", "/", Arrays.asList(new FieldAccessPostAggregator("avg_imps_per_row", "avg_imps_per_row"), new ConstantPostAggregator("constant", 2))));

    private static final List<AggregatorFactory> RENAMED_AGGS = Arrays.asList(new CountAggregatorFactory("rows"), new LongSumAggregatorFactory("imps", "imps"), new LongSumAggregatorFactory("impers2", "imps"));

    private static final List<PostAggregator> DIFF_ORDER_POST_AGGS = Arrays.asList(new org.apache.druid.query.aggregation.post.ArithmeticPostAggregator("avg_imps_per_row", "/", Arrays.asList(new FieldAccessPostAggregator("imps", "imps"), new FieldAccessPostAggregator("rows", "rows"))), new org.apache.druid.query.aggregation.post.ArithmeticPostAggregator("avg_imps_per_row_half", "/", Arrays.asList(new FieldAccessPostAggregator("avg_imps_per_row", "avg_imps_per_row"), new ConstantPostAggregator("constant", 2))), new org.apache.druid.query.aggregation.post.ArithmeticPostAggregator("avg_imps_per_row_double", "*", Arrays.asList(new FieldAccessPostAggregator("avg_imps_per_row", "avg_imps_per_row"), new ConstantPostAggregator("constant", 2))));

    private static final DimFilter DIM_FILTER = null;

    private static final List<PostAggregator> RENAMED_POST_AGGS = ImmutableList.of();

    private static final Granularity GRANULARITY = Granularities.DAY;

    private static final DateTimeZone TIMEZONE = DateTimes.inferTzFromString("America/Los_Angeles");

    private static final Granularity PT1H_TZ_GRANULARITY = new org.apache.druid.java.util.common.granularity.PeriodGranularity(new Period("PT1H"), null, CachingClusteredClientTest.TIMEZONE);

    private static final String TOP_DIM = "a_dim";

    private static final Supplier<SelectQueryConfig> SELECT_CONFIG_SUPPLIER = Suppliers.ofInstance(new SelectQueryConfig(true));

    private static final Pair<QueryToolChestWarehouse, Closer> WAREHOUSE_AND_CLOSER = CachingClusteredClientTestUtils.createWarehouse(CachingClusteredClientTest.JSON_MAPPER, CachingClusteredClientTest.SELECT_CONFIG_SUPPLIER);

    private static final QueryToolChestWarehouse WAREHOUSE = CachingClusteredClientTest.WAREHOUSE_AND_CLOSER.lhs;

    private static final Closer RESOURCE_CLOSER = CachingClusteredClientTest.WAREHOUSE_AND_CLOSER.rhs;

    private final Random random;

    private CachingClusteredClient client;

    private Runnable queryCompletedCallback;

    private TimelineServerView serverView;

    private VersionedIntervalTimeline<String, ServerSelector> timeline;

    private Cache cache;

    private DruidServer[] servers;

    public CachingClusteredClientTest(int randomSeed) {
        this.random = new Random(randomSeed);
    }

    @Test
    public void testOutOfOrderBackgroundCachePopulation() {
        // This test is a bit whacky, but I couldn't find a better way to do it in the current framework.
        // The purpose of this special executor is to randomize execution of tasks on purpose.
        // Since we don't know the number of tasks to be executed, a special DrainTask is used
        // to trigger the actual execution when we are ready to shuffle the order.
        abstract class DrainTask implements Runnable {}
        final ForwardingListeningExecutorService randomizingExecutorService = new ForwardingListeningExecutorService() {
            final ConcurrentLinkedDeque<Pair<SettableFuture, Object>> taskQueue = new ConcurrentLinkedDeque<>();

            final ListeningExecutorService delegate = // we need to run everything in the same thread to ensure all callbacks on futures in CachingClusteredClient
            // are complete before moving on to the next query run.
            MoreExecutors.listeningDecorator(Execs.directExecutor());

            @Override
            protected ListeningExecutorService delegate() {
                return delegate;
            }

            private <T> ListenableFuture<T> maybeSubmitTask(Object task, boolean wait) {
                if (wait) {
                    SettableFuture<T> future = SettableFuture.create();
                    taskQueue.addFirst(Pair.of(future, task));
                    return future;
                } else {
                    List<Pair<SettableFuture, Object>> tasks = Lists.newArrayList(taskQueue.iterator());
                    Collections.shuffle(tasks, new Random(0));
                    for (final Pair<SettableFuture, Object> pair : tasks) {
                        ListenableFuture future = ((pair.rhs) instanceof Callable) ? delegate.submit(((Callable) (pair.rhs))) : delegate.submit(((Runnable) (pair.rhs)));
                        Futures.addCallback(future, new FutureCallback() {
                            @Override
                            public void onSuccess(@Nullable
                            Object result) {
                                pair.lhs.set(result);
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                pair.lhs.setException(t);
                            }
                        });
                    }
                }
                return task instanceof Callable ? delegate.submit(((Callable) (task))) : ((ListenableFuture<T>) (delegate.submit(((Runnable) (task)))));
            }

            @SuppressWarnings("ParameterPackage")
            @Override
            public <T> ListenableFuture<T> submit(Callable<T> task) {
                return maybeSubmitTask(task, true);
            }

            @Override
            public ListenableFuture<?> submit(Runnable task) {
                if (task instanceof DrainTask) {
                    return maybeSubmitTask(task, false);
                } else {
                    return maybeSubmitTask(task, true);
                }
            }
        };
        client = makeClient(new org.apache.druid.client.cache.BackgroundCachePopulator(randomizingExecutorService, CachingClusteredClientTest.JSON_MAPPER, new CachePopulatorStats(), (-1)));
        // callback to be run every time a query run is complete, to ensure all background
        // caching tasks are executed, and cache is populated before we move onto the next query
        queryCompletedCallback = new Runnable() {
            @Override
            public void run() {
                try {
                    randomizingExecutorService.submit(new DrainTask() {
                        @Override
                        public void run() {
                            // no-op
                        }
                    }).get();
                } catch (Exception e) {
                    Throwables.propagate(e);
                }
            }
        };
        final Druids.TimeseriesQueryBuilder builder = Druids.newTimeseriesQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).intervals(CachingClusteredClientTest.SEG_SPEC).filters(CachingClusteredClientTest.DIM_FILTER).granularity(CachingClusteredClientTest.GRANULARITY).aggregators(CachingClusteredClientTest.AGGS).postAggregators(CachingClusteredClientTest.POST_AGGS).context(CachingClusteredClientTest.CONTEXT);
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), new org.apache.druid.query.timeseries.TimeseriesQueryQueryToolChest(QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()));
        testQueryCaching(runner, builder.build(), Intervals.of("2011-01-05/2011-01-10"), makeTimeResults(DateTimes.of("2011-01-05"), 85, 102, DateTimes.of("2011-01-06"), 412, 521, DateTimes.of("2011-01-07"), 122, 21894, DateTimes.of("2011-01-08"), 5, 20, DateTimes.of("2011-01-09"), 18, 521), Intervals.of("2011-01-10/2011-01-13"), makeTimeResults(DateTimes.of("2011-01-10"), 85, 102, DateTimes.of("2011-01-11"), 412, 521, DateTimes.of("2011-01-12"), 122, 21894));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTimeseriesCaching() {
        final Druids.TimeseriesQueryBuilder builder = Druids.newTimeseriesQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).intervals(CachingClusteredClientTest.SEG_SPEC).filters(CachingClusteredClientTest.DIM_FILTER).granularity(CachingClusteredClientTest.GRANULARITY).aggregators(CachingClusteredClientTest.AGGS).postAggregators(CachingClusteredClientTest.POST_AGGS).context(CachingClusteredClientTest.CONTEXT);
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), new org.apache.druid.query.timeseries.TimeseriesQueryQueryToolChest(QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()));
        testQueryCaching(runner, builder.build(), Intervals.of("2011-01-01/2011-01-02"), makeTimeResults(DateTimes.of("2011-01-01"), 50, 5000), Intervals.of("2011-01-02/2011-01-03"), makeTimeResults(DateTimes.of("2011-01-02"), 30, 6000), Intervals.of("2011-01-04/2011-01-05"), makeTimeResults(DateTimes.of("2011-01-04"), 23, 85312), Intervals.of("2011-01-05/2011-01-10"), makeTimeResults(DateTimes.of("2011-01-05"), 85, 102, DateTimes.of("2011-01-06"), 412, 521, DateTimes.of("2011-01-07"), 122, 21894, DateTimes.of("2011-01-08"), 5, 20, DateTimes.of("2011-01-09"), 18, 521), Intervals.of("2011-01-05/2011-01-10"), makeTimeResults(DateTimes.of("2011-01-05T01"), 80, 100, DateTimes.of("2011-01-06T01"), 420, 520, DateTimes.of("2011-01-07T01"), 12, 2194, DateTimes.of("2011-01-08T01"), 59, 201, DateTimes.of("2011-01-09T01"), 181, 52));
        HashMap<String, List> context = new HashMap<String, List>();
        TimeseriesQuery query = builder.intervals("2011-01-01/2011-01-10").aggregators(CachingClusteredClientTest.RENAMED_AGGS).postAggregators(CachingClusteredClientTest.RENAMED_POST_AGGS).build();
        TestHelper.assertExpectedResults(makeRenamedTimeResults(DateTimes.of("2011-01-01"), 50, 5000, DateTimes.of("2011-01-02"), 30, 6000, DateTimes.of("2011-01-04"), 23, 85312, DateTimes.of("2011-01-05"), 85, 102, DateTimes.of("2011-01-05T01"), 80, 100, DateTimes.of("2011-01-06"), 412, 521, DateTimes.of("2011-01-06T01"), 420, 520, DateTimes.of("2011-01-07"), 122, 21894, DateTimes.of("2011-01-07T01"), 12, 2194, DateTimes.of("2011-01-08"), 5, 20, DateTimes.of("2011-01-08T01"), 59, 201, DateTimes.of("2011-01-09"), 18, 521, DateTimes.of("2011-01-09T01"), 181, 52), runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCachingOverBulkLimitEnforcesLimit() {
        final int limit = 10;
        final Interval interval = Intervals.of("2011-01-01/2011-01-02");
        final TimeseriesQuery query = Druids.newTimeseriesQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).intervals(new MultipleIntervalSegmentSpec(ImmutableList.of(interval))).filters(CachingClusteredClientTest.DIM_FILTER).granularity(CachingClusteredClientTest.GRANULARITY).aggregators(CachingClusteredClientTest.AGGS).postAggregators(CachingClusteredClientTest.POST_AGGS).context(CachingClusteredClientTest.CONTEXT).build();
        final Map<String, Object> context = new HashMap<>();
        final Cache cache = EasyMock.createStrictMock(Cache.class);
        final Capture<Iterable<Cache.NamedKey>> cacheKeyCapture = EasyMock.newCapture();
        EasyMock.expect(cache.getBulk(EasyMock.capture(cacheKeyCapture))).andReturn(ImmutableMap.of()).once();
        EasyMock.replay(cache);
        client = makeClient(new org.apache.druid.client.cache.ForegroundCachePopulator(CachingClusteredClientTest.JSON_MAPPER, new CachePopulatorStats(), (-1)), cache, limit);
        final DruidServer lastServer = servers[random.nextInt(servers.length)];
        final DataSegment dataSegment = EasyMock.createNiceMock(DataSegment.class);
        EasyMock.expect(dataSegment.getId()).andReturn(SegmentId.dummy(CachingClusteredClientTest.DATA_SOURCE)).anyTimes();
        EasyMock.replay(dataSegment);
        final ServerSelector selector = new ServerSelector(dataSegment, new org.apache.druid.client.selector.HighestPriorityTierSelectorStrategy(new RandomServerSelectorStrategy()));
        selector.addServerAndUpdateSegment(new org.apache.druid.client.selector.QueryableDruidServer(lastServer, null), dataSegment);
        timeline.add(interval, "v", new org.apache.druid.timeline.partition.SingleElementPartitionChunk(selector));
        getDefaultQueryRunner().run(QueryPlus.wrap(query), context);
        Assert.assertTrue("Capture cache keys", cacheKeyCapture.hasCaptured());
        Assert.assertTrue("Cache key below limit", ((ImmutableList.copyOf(cacheKeyCapture.getValue()).size()) <= limit));
        EasyMock.verify(cache);
        EasyMock.reset(cache);
        cacheKeyCapture.reset();
        EasyMock.expect(cache.getBulk(EasyMock.capture(cacheKeyCapture))).andReturn(ImmutableMap.of()).once();
        EasyMock.replay(cache);
        client = makeClient(new org.apache.druid.client.cache.ForegroundCachePopulator(CachingClusteredClientTest.JSON_MAPPER, new CachePopulatorStats(), (-1)), cache, 0);
        getDefaultQueryRunner().run(QueryPlus.wrap(query), context);
        EasyMock.verify(cache);
        EasyMock.verify(dataSegment);
        Assert.assertTrue("Capture cache keys", cacheKeyCapture.hasCaptured());
        Assert.assertTrue("Cache Keys empty", ImmutableList.copyOf(cacheKeyCapture.getValue()).isEmpty());
    }

    @Test
    public void testTimeseriesMergingOutOfOrderPartitions() {
        final Druids.TimeseriesQueryBuilder builder = Druids.newTimeseriesQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).intervals(CachingClusteredClientTest.SEG_SPEC).filters(CachingClusteredClientTest.DIM_FILTER).granularity(CachingClusteredClientTest.GRANULARITY).aggregators(CachingClusteredClientTest.AGGS).postAggregators(CachingClusteredClientTest.POST_AGGS).context(CachingClusteredClientTest.CONTEXT);
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), new org.apache.druid.query.timeseries.TimeseriesQueryQueryToolChest(QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()));
        testQueryCaching(runner, builder.build(), Intervals.of("2011-01-05/2011-01-10"), makeTimeResults(DateTimes.of("2011-01-05T02"), 80, 100, DateTimes.of("2011-01-06T02"), 420, 520, DateTimes.of("2011-01-07T02"), 12, 2194, DateTimes.of("2011-01-08T02"), 59, 201, DateTimes.of("2011-01-09T02"), 181, 52), Intervals.of("2011-01-05/2011-01-10"), makeTimeResults(DateTimes.of("2011-01-05T00"), 85, 102, DateTimes.of("2011-01-06T00"), 412, 521, DateTimes.of("2011-01-07T00"), 122, 21894, DateTimes.of("2011-01-08T00"), 5, 20, DateTimes.of("2011-01-09T00"), 18, 521));
        TimeseriesQuery query = builder.intervals("2011-01-05/2011-01-10").aggregators(CachingClusteredClientTest.RENAMED_AGGS).postAggregators(CachingClusteredClientTest.RENAMED_POST_AGGS).build();
        TestHelper.assertExpectedResults(makeRenamedTimeResults(DateTimes.of("2011-01-05T00"), 85, 102, DateTimes.of("2011-01-05T02"), 80, 100, DateTimes.of("2011-01-06T00"), 412, 521, DateTimes.of("2011-01-06T02"), 420, 520, DateTimes.of("2011-01-07T00"), 122, 21894, DateTimes.of("2011-01-07T02"), 12, 2194, DateTimes.of("2011-01-08T00"), 5, 20, DateTimes.of("2011-01-08T02"), 59, 201, DateTimes.of("2011-01-09T00"), 18, 521, DateTimes.of("2011-01-09T02"), 181, 52), runner.run(QueryPlus.wrap(query), new HashMap()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTimeseriesCachingTimeZone() {
        final Druids.TimeseriesQueryBuilder builder = Druids.newTimeseriesQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).intervals(CachingClusteredClientTest.SEG_SPEC).filters(CachingClusteredClientTest.DIM_FILTER).granularity(CachingClusteredClientTest.PT1H_TZ_GRANULARITY).aggregators(CachingClusteredClientTest.AGGS).postAggregators(CachingClusteredClientTest.POST_AGGS).context(CachingClusteredClientTest.CONTEXT);
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), new org.apache.druid.query.timeseries.TimeseriesQueryQueryToolChest(QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()));
        testQueryCaching(runner, builder.build(), Intervals.of("2011-11-04/2011-11-08"), makeTimeResults(new org.joda.time.DateTime("2011-11-04", CachingClusteredClientTest.TIMEZONE), 50, 5000, new org.joda.time.DateTime("2011-11-05", CachingClusteredClientTest.TIMEZONE), 30, 6000, new org.joda.time.DateTime("2011-11-06", CachingClusteredClientTest.TIMEZONE), 23, 85312, new org.joda.time.DateTime("2011-11-07", CachingClusteredClientTest.TIMEZONE), 85, 102));
        HashMap<String, List> context = new HashMap<String, List>();
        TimeseriesQuery query = builder.intervals("2011-11-04/2011-11-08").aggregators(CachingClusteredClientTest.RENAMED_AGGS).postAggregators(CachingClusteredClientTest.RENAMED_POST_AGGS).build();
        TestHelper.assertExpectedResults(makeRenamedTimeResults(new org.joda.time.DateTime("2011-11-04", CachingClusteredClientTest.TIMEZONE), 50, 5000, new org.joda.time.DateTime("2011-11-05", CachingClusteredClientTest.TIMEZONE), 30, 6000, new org.joda.time.DateTime("2011-11-06", CachingClusteredClientTest.TIMEZONE), 23, 85312, new org.joda.time.DateTime("2011-11-07", CachingClusteredClientTest.TIMEZONE), 85, 102), runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testDisableUseCache() {
        final Druids.TimeseriesQueryBuilder builder = Druids.newTimeseriesQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).intervals(CachingClusteredClientTest.SEG_SPEC).filters(CachingClusteredClientTest.DIM_FILTER).granularity(CachingClusteredClientTest.GRANULARITY).aggregators(CachingClusteredClientTest.AGGS).postAggregators(CachingClusteredClientTest.POST_AGGS).context(CachingClusteredClientTest.CONTEXT);
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), new org.apache.druid.query.timeseries.TimeseriesQueryQueryToolChest(QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()));
        testQueryCaching(runner, 1, true, builder.context(ImmutableMap.of("useCache", "false", "populateCache", "true")).build(), Intervals.of("2011-01-01/2011-01-02"), makeTimeResults(DateTimes.of("2011-01-01"), 50, 5000));
        Assert.assertEquals(1, cache.getStats().getNumEntries());
        Assert.assertEquals(0, cache.getStats().getNumHits());
        Assert.assertEquals(0, cache.getStats().getNumMisses());
        cache.close(SegmentId.dummy("0_0").toString());
        testQueryCaching(runner, 1, false, builder.context(ImmutableMap.of("useCache", "false", "populateCache", "false")).build(), Intervals.of("2011-01-01/2011-01-02"), makeTimeResults(DateTimes.of("2011-01-01"), 50, 5000));
        Assert.assertEquals(0, cache.getStats().getNumEntries());
        Assert.assertEquals(0, cache.getStats().getNumHits());
        Assert.assertEquals(0, cache.getStats().getNumMisses());
        testQueryCaching(getDefaultQueryRunner(), 1, false, builder.context(ImmutableMap.of("useCache", "true", "populateCache", "false")).build(), Intervals.of("2011-01-01/2011-01-02"), makeTimeResults(DateTimes.of("2011-01-01"), 50, 5000));
        Assert.assertEquals(0, cache.getStats().getNumEntries());
        Assert.assertEquals(0, cache.getStats().getNumHits());
        Assert.assertEquals(1, cache.getStats().getNumMisses());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTopNCaching() {
        final TopNQueryBuilder builder = new TopNQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).dimension(CachingClusteredClientTest.TOP_DIM).metric("imps").threshold(3).intervals(CachingClusteredClientTest.SEG_SPEC).filters(CachingClusteredClientTest.DIM_FILTER).granularity(CachingClusteredClientTest.GRANULARITY).aggregators(CachingClusteredClientTest.AGGS).postAggregators(CachingClusteredClientTest.POST_AGGS).context(CachingClusteredClientTest.CONTEXT);
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), new org.apache.druid.query.topn.TopNQueryQueryToolChest(new TopNQueryConfig(), QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()));
        testQueryCaching(runner, builder.build(), Intervals.of("2011-01-01/2011-01-02"), makeTopNResultsWithoutRename(DateTimes.of("2011-01-01"), "a", 50, 5000, "b", 50, 4999, "c", 50, 4998), Intervals.of("2011-01-02/2011-01-03"), makeTopNResultsWithoutRename(DateTimes.of("2011-01-02"), "a", 50, 4997, "b", 50, 4996, "c", 50, 4995), Intervals.of("2011-01-05/2011-01-10"), makeTopNResultsWithoutRename(DateTimes.of("2011-01-05"), "a", 50, 4994, "b", 50, 4993, "c", 50, 4992, DateTimes.of("2011-01-06"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-07"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-08"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-09"), "c1", 50, 4985, "b", 50, 4984, "c", 50, 4983), Intervals.of("2011-01-05/2011-01-10"), makeTopNResultsWithoutRename(DateTimes.of("2011-01-05T01"), "a", 50, 4994, "b", 50, 4993, "c", 50, 4992, DateTimes.of("2011-01-06T01"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-07T01"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-08T01"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-09T01"), "c2", 50, 4985, "b", 50, 4984, "c", 50, 4983));
        HashMap<String, List> context = new HashMap<String, List>();
        TopNQuery query = builder.intervals("2011-01-01/2011-01-10").metric("imps").aggregators(CachingClusteredClientTest.RENAMED_AGGS).postAggregators(CachingClusteredClientTest.DIFF_ORDER_POST_AGGS).build();
        TestHelper.assertExpectedResults(makeRenamedTopNResults(DateTimes.of("2011-01-01"), "a", 50, 5000, "b", 50, 4999, "c", 50, 4998, DateTimes.of("2011-01-02"), "a", 50, 4997, "b", 50, 4996, "c", 50, 4995, DateTimes.of("2011-01-05"), "a", 50, 4994, "b", 50, 4993, "c", 50, 4992, DateTimes.of("2011-01-05T01"), "a", 50, 4994, "b", 50, 4993, "c", 50, 4992, DateTimes.of("2011-01-06"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-06T01"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-07"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-07T01"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-08"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-08T01"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-09"), "c1", 50, 4985, "b", 50, 4984, "c", 50, 4983, DateTimes.of("2011-01-09T01"), "c2", 50, 4985, "b", 50, 4984, "c", 50, 4983), runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTopNCachingTimeZone() {
        final TopNQueryBuilder builder = new TopNQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).dimension(CachingClusteredClientTest.TOP_DIM).metric("imps").threshold(3).intervals(CachingClusteredClientTest.SEG_SPEC).filters(CachingClusteredClientTest.DIM_FILTER).granularity(CachingClusteredClientTest.PT1H_TZ_GRANULARITY).aggregators(CachingClusteredClientTest.AGGS).postAggregators(CachingClusteredClientTest.POST_AGGS).context(CachingClusteredClientTest.CONTEXT);
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), new org.apache.druid.query.topn.TopNQueryQueryToolChest(new TopNQueryConfig(), QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()));
        testQueryCaching(runner, builder.build(), Intervals.of("2011-11-04/2011-11-08"), makeTopNResultsWithoutRename(new org.joda.time.DateTime("2011-11-04", CachingClusteredClientTest.TIMEZONE), "a", 50, 4994, "b", 50, 4993, "c", 50, 4992, new org.joda.time.DateTime("2011-11-05", CachingClusteredClientTest.TIMEZONE), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, new org.joda.time.DateTime("2011-11-06", CachingClusteredClientTest.TIMEZONE), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, new org.joda.time.DateTime("2011-11-07", CachingClusteredClientTest.TIMEZONE), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986));
        HashMap<String, List> context = new HashMap<String, List>();
        TopNQuery query = builder.intervals("2011-11-04/2011-11-08").metric("imps").aggregators(CachingClusteredClientTest.RENAMED_AGGS).postAggregators(CachingClusteredClientTest.DIFF_ORDER_POST_AGGS).build();
        TestHelper.assertExpectedResults(makeRenamedTopNResults(new org.joda.time.DateTime("2011-11-04", CachingClusteredClientTest.TIMEZONE), "a", 50, 4994, "b", 50, 4993, "c", 50, 4992, new org.joda.time.DateTime("2011-11-05", CachingClusteredClientTest.TIMEZONE), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, new org.joda.time.DateTime("2011-11-06", CachingClusteredClientTest.TIMEZONE), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, new org.joda.time.DateTime("2011-11-07", CachingClusteredClientTest.TIMEZONE), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986), runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testOutOfOrderSequenceMerging() {
        List<Sequence<Result<TopNResultValue>>> sequences = ImmutableList.of(Sequences.simple(makeTopNResultsWithoutRename(DateTimes.of("2011-01-07"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-08"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-09"), "a", 50, 4985, "b", 50, 4984, "c", 50, 4983)), Sequences.simple(makeTopNResultsWithoutRename(DateTimes.of("2011-01-06T01"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-07T01"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-08T01"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-09T01"), "a", 50, 4985, "b", 50, 4984, "c", 50, 4983)));
        TestHelper.assertExpectedResults(makeTopNResultsWithoutRename(DateTimes.of("2011-01-06T01"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-07"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-07T01"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-08"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-08T01"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-09"), "a", 50, 4985, "b", 50, 4984, "c", 50, 4983, DateTimes.of("2011-01-09T01"), "a", 50, 4985, "b", 50, 4984, "c", 50, 4983), CachingClusteredClientTest.mergeSequences(new TopNQueryBuilder().dataSource("test").intervals("2011-01-06/2011-01-10").dimension("a").metric("b").threshold(3).aggregators(Collections.<AggregatorFactory>singletonList(new CountAggregatorFactory("b"))).build(), sequences));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTopNCachingEmptyResults() {
        final TopNQueryBuilder builder = new TopNQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).dimension(CachingClusteredClientTest.TOP_DIM).metric("imps").threshold(3).intervals(CachingClusteredClientTest.SEG_SPEC).filters(CachingClusteredClientTest.DIM_FILTER).granularity(CachingClusteredClientTest.GRANULARITY).aggregators(CachingClusteredClientTest.AGGS).postAggregators(CachingClusteredClientTest.POST_AGGS).context(CachingClusteredClientTest.CONTEXT);
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), new org.apache.druid.query.topn.TopNQueryQueryToolChest(new TopNQueryConfig(), QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()));
        testQueryCaching(runner, builder.build(), Intervals.of("2011-01-01/2011-01-02"), makeTopNResultsWithoutRename(), Intervals.of("2011-01-02/2011-01-03"), makeTopNResultsWithoutRename(), Intervals.of("2011-01-05/2011-01-10"), makeTopNResultsWithoutRename(DateTimes.of("2011-01-05"), "a", 50, 4994, "b", 50, 4993, "c", 50, 4992, DateTimes.of("2011-01-06"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-07"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-08"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-09"), "a", 50, 4985, "b", 50, 4984, "c", 50, 4983), Intervals.of("2011-01-05/2011-01-10"), makeTopNResultsWithoutRename(DateTimes.of("2011-01-05T01"), "a", 50, 4994, "b", 50, 4993, "c", 50, 4992, DateTimes.of("2011-01-06T01"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-07T01"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-08T01"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-09T01"), "a", 50, 4985, "b", 50, 4984, "c", 50, 4983));
        HashMap<String, List> context = new HashMap<String, List>();
        TopNQuery query = builder.intervals("2011-01-01/2011-01-10").metric("imps").aggregators(CachingClusteredClientTest.RENAMED_AGGS).postAggregators(CachingClusteredClientTest.DIFF_ORDER_POST_AGGS).build();
        TestHelper.assertExpectedResults(makeRenamedTopNResults(DateTimes.of("2011-01-05"), "a", 50, 4994, "b", 50, 4993, "c", 50, 4992, DateTimes.of("2011-01-05T01"), "a", 50, 4994, "b", 50, 4993, "c", 50, 4992, DateTimes.of("2011-01-06"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-06T01"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-07"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-07T01"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-08"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-08T01"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-09"), "a", 50, 4985, "b", 50, 4984, "c", 50, 4983, DateTimes.of("2011-01-09T01"), "a", 50, 4985, "b", 50, 4984, "c", 50, 4983), runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testTopNOnPostAggMetricCaching() {
        final TopNQueryBuilder builder = new TopNQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).dimension(CachingClusteredClientTest.TOP_DIM).metric("avg_imps_per_row_double").threshold(3).intervals(CachingClusteredClientTest.SEG_SPEC).filters(CachingClusteredClientTest.DIM_FILTER).granularity(CachingClusteredClientTest.GRANULARITY).aggregators(CachingClusteredClientTest.AGGS).postAggregators(CachingClusteredClientTest.POST_AGGS).context(CachingClusteredClientTest.CONTEXT);
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), new org.apache.druid.query.topn.TopNQueryQueryToolChest(new TopNQueryConfig(), QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()));
        testQueryCaching(runner, builder.build(), Intervals.of("2011-01-01/2011-01-02"), makeTopNResultsWithoutRename(), Intervals.of("2011-01-02/2011-01-03"), makeTopNResultsWithoutRename(), Intervals.of("2011-01-05/2011-01-10"), makeTopNResultsWithoutRename(DateTimes.of("2011-01-05"), "a", 50, 4994, "b", 50, 4993, "c", 50, 4992, DateTimes.of("2011-01-06"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-07"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-08"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-09"), "c1", 50, 4985, "b", 50, 4984, "c", 50, 4983), Intervals.of("2011-01-05/2011-01-10"), makeTopNResultsWithoutRename(DateTimes.of("2011-01-05T01"), "a", 50, 4994, "b", 50, 4993, "c", 50, 4992, DateTimes.of("2011-01-06T01"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-07T01"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-08T01"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-09T01"), "c2", 50, 4985, "b", 50, 4984, "c", 50, 4983));
        HashMap<String, List> context = new HashMap<String, List>();
        TopNQuery query = builder.intervals("2011-01-01/2011-01-10").metric("avg_imps_per_row_double").aggregators(CachingClusteredClientTest.AGGS).postAggregators(CachingClusteredClientTest.DIFF_ORDER_POST_AGGS).build();
        TestHelper.assertExpectedResults(makeTopNResultsWithoutRename(DateTimes.of("2011-01-05"), "a", 50, 4994, "b", 50, 4993, "c", 50, 4992, DateTimes.of("2011-01-05T01"), "a", 50, 4994, "b", 50, 4993, "c", 50, 4992, DateTimes.of("2011-01-06"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-06T01"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-07"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-07T01"), "a", 50, 4991, "b", 50, 4990, "c", 50, 4989, DateTimes.of("2011-01-08"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-08T01"), "a", 50, 4988, "b", 50, 4987, "c", 50, 4986, DateTimes.of("2011-01-09"), "c1", 50, 4985, "b", 50, 4984, "c", 50, 4983, DateTimes.of("2011-01-09T01"), "c2", 50, 4985, "b", 50, 4984, "c", 50, 4983), runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testSearchCaching() {
        final Druids.SearchQueryBuilder builder = Druids.newSearchQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).filters(CachingClusteredClientTest.DIM_FILTER).granularity(CachingClusteredClientTest.GRANULARITY).limit(1000).intervals(CachingClusteredClientTest.SEG_SPEC).dimensions(Collections.singletonList(CachingClusteredClientTest.TOP_DIM)).query("how").context(CachingClusteredClientTest.CONTEXT);
        testQueryCaching(getDefaultQueryRunner(), builder.build(), Intervals.of("2011-01-01/2011-01-02"), makeSearchResults(CachingClusteredClientTest.TOP_DIM, DateTimes.of("2011-01-01"), "how", 1, "howdy", 2, "howwwwww", 3, "howwy", 4), Intervals.of("2011-01-02/2011-01-03"), makeSearchResults(CachingClusteredClientTest.TOP_DIM, DateTimes.of("2011-01-02"), "how1", 1, "howdy1", 2, "howwwwww1", 3, "howwy1", 4), Intervals.of("2011-01-05/2011-01-10"), makeSearchResults(CachingClusteredClientTest.TOP_DIM, DateTimes.of("2011-01-05"), "how2", 1, "howdy2", 2, "howwwwww2", 3, "howww2", 4, DateTimes.of("2011-01-06"), "how3", 1, "howdy3", 2, "howwwwww3", 3, "howww3", 4, DateTimes.of("2011-01-07"), "how4", 1, "howdy4", 2, "howwwwww4", 3, "howww4", 4, DateTimes.of("2011-01-08"), "how5", 1, "howdy5", 2, "howwwwww5", 3, "howww5", 4, DateTimes.of("2011-01-09"), "how6", 1, "howdy6", 2, "howwwwww6", 3, "howww6", 4), Intervals.of("2011-01-05/2011-01-10"), makeSearchResults(CachingClusteredClientTest.TOP_DIM, DateTimes.of("2011-01-05T01"), "how2", 1, "howdy2", 2, "howwwwww2", 3, "howww2", 4, DateTimes.of("2011-01-06T01"), "how3", 1, "howdy3", 2, "howwwwww3", 3, "howww3", 4, DateTimes.of("2011-01-07T01"), "how4", 1, "howdy4", 2, "howwwwww4", 3, "howww4", 4, DateTimes.of("2011-01-08T01"), "how5", 1, "howdy5", 2, "howwwwww5", 3, "howww5", 4, DateTimes.of("2011-01-09T01"), "how6", 1, "howdy6", 2, "howwwwww6", 3, "howww6", 4));
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), new org.apache.druid.query.search.SearchQueryQueryToolChest(new SearchQueryConfig(), QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()));
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(makeSearchResults(CachingClusteredClientTest.TOP_DIM, DateTimes.of("2011-01-01"), "how", 1, "howdy", 2, "howwwwww", 3, "howwy", 4, DateTimes.of("2011-01-02"), "how1", 1, "howdy1", 2, "howwwwww1", 3, "howwy1", 4, DateTimes.of("2011-01-05"), "how2", 1, "howdy2", 2, "howwwwww2", 3, "howww2", 4, DateTimes.of("2011-01-05T01"), "how2", 1, "howdy2", 2, "howwwwww2", 3, "howww2", 4, DateTimes.of("2011-01-06"), "how3", 1, "howdy3", 2, "howwwwww3", 3, "howww3", 4, DateTimes.of("2011-01-06T01"), "how3", 1, "howdy3", 2, "howwwwww3", 3, "howww3", 4, DateTimes.of("2011-01-07"), "how4", 1, "howdy4", 2, "howwwwww4", 3, "howww4", 4, DateTimes.of("2011-01-07T01"), "how4", 1, "howdy4", 2, "howwwwww4", 3, "howww4", 4, DateTimes.of("2011-01-08"), "how5", 1, "howdy5", 2, "howwwwww5", 3, "howww5", 4, DateTimes.of("2011-01-08T01"), "how5", 1, "howdy5", 2, "howwwwww5", 3, "howww5", 4, DateTimes.of("2011-01-09"), "how6", 1, "howdy6", 2, "howwwwww6", 3, "howww6", 4, DateTimes.of("2011-01-09T01"), "how6", 1, "howdy6", 2, "howwwwww6", 3, "howww6", 4), runner.run(QueryPlus.wrap(builder.intervals("2011-01-01/2011-01-10").build()), context));
    }

    @Test
    public void testSearchCachingRenamedOutput() {
        final Druids.SearchQueryBuilder builder = Druids.newSearchQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).filters(CachingClusteredClientTest.DIM_FILTER).granularity(CachingClusteredClientTest.GRANULARITY).limit(1000).intervals(CachingClusteredClientTest.SEG_SPEC).dimensions(Collections.singletonList(CachingClusteredClientTest.TOP_DIM)).query("how").context(CachingClusteredClientTest.CONTEXT);
        testQueryCaching(getDefaultQueryRunner(), builder.build(), Intervals.of("2011-01-01/2011-01-02"), makeSearchResults(CachingClusteredClientTest.TOP_DIM, DateTimes.of("2011-01-01"), "how", 1, "howdy", 2, "howwwwww", 3, "howwy", 4), Intervals.of("2011-01-02/2011-01-03"), makeSearchResults(CachingClusteredClientTest.TOP_DIM, DateTimes.of("2011-01-02"), "how1", 1, "howdy1", 2, "howwwwww1", 3, "howwy1", 4), Intervals.of("2011-01-05/2011-01-10"), makeSearchResults(CachingClusteredClientTest.TOP_DIM, DateTimes.of("2011-01-05"), "how2", 1, "howdy2", 2, "howwwwww2", 3, "howww2", 4, DateTimes.of("2011-01-06"), "how3", 1, "howdy3", 2, "howwwwww3", 3, "howww3", 4, DateTimes.of("2011-01-07"), "how4", 1, "howdy4", 2, "howwwwww4", 3, "howww4", 4, DateTimes.of("2011-01-08"), "how5", 1, "howdy5", 2, "howwwwww5", 3, "howww5", 4, DateTimes.of("2011-01-09"), "how6", 1, "howdy6", 2, "howwwwww6", 3, "howww6", 4), Intervals.of("2011-01-05/2011-01-10"), makeSearchResults(CachingClusteredClientTest.TOP_DIM, DateTimes.of("2011-01-05T01"), "how2", 1, "howdy2", 2, "howwwwww2", 3, "howww2", 4, DateTimes.of("2011-01-06T01"), "how3", 1, "howdy3", 2, "howwwwww3", 3, "howww3", 4, DateTimes.of("2011-01-07T01"), "how4", 1, "howdy4", 2, "howwwwww4", 3, "howww4", 4, DateTimes.of("2011-01-08T01"), "how5", 1, "howdy5", 2, "howwwwww5", 3, "howww5", 4, DateTimes.of("2011-01-09T01"), "how6", 1, "howdy6", 2, "howwwwww6", 3, "howww6", 4));
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), new org.apache.druid.query.search.SearchQueryQueryToolChest(new SearchQueryConfig(), QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()));
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(makeSearchResults(CachingClusteredClientTest.TOP_DIM, DateTimes.of("2011-01-01"), "how", 1, "howdy", 2, "howwwwww", 3, "howwy", 4, DateTimes.of("2011-01-02"), "how1", 1, "howdy1", 2, "howwwwww1", 3, "howwy1", 4, DateTimes.of("2011-01-05"), "how2", 1, "howdy2", 2, "howwwwww2", 3, "howww2", 4, DateTimes.of("2011-01-05T01"), "how2", 1, "howdy2", 2, "howwwwww2", 3, "howww2", 4, DateTimes.of("2011-01-06"), "how3", 1, "howdy3", 2, "howwwwww3", 3, "howww3", 4, DateTimes.of("2011-01-06T01"), "how3", 1, "howdy3", 2, "howwwwww3", 3, "howww3", 4, DateTimes.of("2011-01-07"), "how4", 1, "howdy4", 2, "howwwwww4", 3, "howww4", 4, DateTimes.of("2011-01-07T01"), "how4", 1, "howdy4", 2, "howwwwww4", 3, "howww4", 4, DateTimes.of("2011-01-08"), "how5", 1, "howdy5", 2, "howwwwww5", 3, "howww5", 4, DateTimes.of("2011-01-08T01"), "how5", 1, "howdy5", 2, "howwwwww5", 3, "howww5", 4, DateTimes.of("2011-01-09"), "how6", 1, "howdy6", 2, "howwwwww6", 3, "howww6", 4, DateTimes.of("2011-01-09T01"), "how6", 1, "howdy6", 2, "howwwwww6", 3, "howww6", 4), runner.run(QueryPlus.wrap(builder.intervals("2011-01-01/2011-01-10").build()), context));
        SearchQuery query = builder.intervals("2011-01-01/2011-01-10").dimensions(new DefaultDimensionSpec(CachingClusteredClientTest.TOP_DIM, "new_dim")).build();
        TestHelper.assertExpectedResults(makeSearchResults("new_dim", DateTimes.of("2011-01-01"), "how", 1, "howdy", 2, "howwwwww", 3, "howwy", 4, DateTimes.of("2011-01-02"), "how1", 1, "howdy1", 2, "howwwwww1", 3, "howwy1", 4, DateTimes.of("2011-01-05"), "how2", 1, "howdy2", 2, "howwwwww2", 3, "howww2", 4, DateTimes.of("2011-01-05T01"), "how2", 1, "howdy2", 2, "howwwwww2", 3, "howww2", 4, DateTimes.of("2011-01-06"), "how3", 1, "howdy3", 2, "howwwwww3", 3, "howww3", 4, DateTimes.of("2011-01-06T01"), "how3", 1, "howdy3", 2, "howwwwww3", 3, "howww3", 4, DateTimes.of("2011-01-07"), "how4", 1, "howdy4", 2, "howwwwww4", 3, "howww4", 4, DateTimes.of("2011-01-07T01"), "how4", 1, "howdy4", 2, "howwwwww4", 3, "howww4", 4, DateTimes.of("2011-01-08"), "how5", 1, "howdy5", 2, "howwwwww5", 3, "howww5", 4, DateTimes.of("2011-01-08T01"), "how5", 1, "howdy5", 2, "howwwwww5", 3, "howww5", 4, DateTimes.of("2011-01-09"), "how6", 1, "howdy6", 2, "howwwwww6", 3, "howww6", 4, DateTimes.of("2011-01-09T01"), "how6", 1, "howdy6", 2, "howwwwww6", 3, "howww6", 4), runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testSelectCaching() {
        final Set<String> dimensions = Sets.newHashSet("a");
        final Set<String> metrics = Sets.newHashSet("rows");
        Druids.SelectQueryBuilder builder = Druids.newSelectQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).intervals(CachingClusteredClientTest.SEG_SPEC).filters(CachingClusteredClientTest.DIM_FILTER).granularity(CachingClusteredClientTest.GRANULARITY).dimensions(Collections.singletonList("a")).metrics(Collections.singletonList("rows")).pagingSpec(new org.apache.druid.query.select.PagingSpec(null, 3)).context(CachingClusteredClientTest.CONTEXT);
        testQueryCaching(getDefaultQueryRunner(), builder.build(), Intervals.of("2011-01-01/2011-01-02"), makeSelectResults(dimensions, metrics, DateTimes.of("2011-01-01"), ImmutableMap.of("a", "b", "rows", 1)), Intervals.of("2011-01-02/2011-01-03"), makeSelectResults(dimensions, metrics, DateTimes.of("2011-01-02"), ImmutableMap.of("a", "c", "rows", 5)), Intervals.of("2011-01-05/2011-01-10"), makeSelectResults(dimensions, metrics, DateTimes.of("2011-01-05"), DateTimes.of("2011-01-06"), DateTimes.of("2011-01-07"), ImmutableMap.of("a", "f", "rows", 7), ImmutableMap.of("a", "ff"), DateTimes.of("2011-01-08"), ImmutableMap.of("a", "g", "rows", 8), DateTimes.of("2011-01-09"), ImmutableMap.of("a", "h", "rows", 9)), Intervals.of("2011-01-05/2011-01-10"), makeSelectResults(dimensions, metrics, DateTimes.of("2011-01-05T01"), ImmutableMap.of("a", "d", "rows", 5), DateTimes.of("2011-01-06T01"), ImmutableMap.of("a", "e", "rows", 6), DateTimes.of("2011-01-07T01"), ImmutableMap.of("a", "f", "rows", 7), DateTimes.of("2011-01-08T01"), ImmutableMap.of("a", "g", "rows", 8), DateTimes.of("2011-01-09T01"), ImmutableMap.of("a", "h", "rows", 9)));
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), new org.apache.druid.query.select.SelectQueryQueryToolChest(CachingClusteredClientTest.JSON_MAPPER, QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator(), CachingClusteredClientTest.SELECT_CONFIG_SUPPLIER));
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(makeSelectResults(dimensions, metrics, DateTimes.of("2011-01-01"), ImmutableMap.of("a", "b", "rows", 1), DateTimes.of("2011-01-02"), ImmutableMap.of("a", "c", "rows", 5), DateTimes.of("2011-01-05"), DateTimes.of("2011-01-05T01"), ImmutableMap.of("a", "d", "rows", 5), DateTimes.of("2011-01-06"), DateTimes.of("2011-01-06T01"), ImmutableMap.of("a", "e", "rows", 6), DateTimes.of("2011-01-07"), ImmutableMap.of("a", "f", "rows", 7), ImmutableMap.of("a", "ff"), DateTimes.of("2011-01-07T01"), ImmutableMap.of("a", "f", "rows", 7), DateTimes.of("2011-01-08"), ImmutableMap.of("a", "g", "rows", 8), DateTimes.of("2011-01-08T01"), ImmutableMap.of("a", "g", "rows", 8), DateTimes.of("2011-01-09"), ImmutableMap.of("a", "h", "rows", 9), DateTimes.of("2011-01-09T01"), ImmutableMap.of("a", "h", "rows", 9)), runner.run(QueryPlus.wrap(builder.intervals("2011-01-01/2011-01-10").build()), context));
    }

    @Test
    public void testSelectCachingRenamedOutputName() {
        final Set<String> dimensions = Sets.newHashSet("a");
        final Set<String> metrics = Sets.newHashSet("rows");
        Druids.SelectQueryBuilder builder = Druids.newSelectQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).intervals(CachingClusteredClientTest.SEG_SPEC).filters(CachingClusteredClientTest.DIM_FILTER).granularity(CachingClusteredClientTest.GRANULARITY).dimensions(Collections.singletonList("a")).metrics(Collections.singletonList("rows")).pagingSpec(new org.apache.druid.query.select.PagingSpec(null, 3)).context(CachingClusteredClientTest.CONTEXT);
        testQueryCaching(getDefaultQueryRunner(), builder.build(), Intervals.of("2011-01-01/2011-01-02"), makeSelectResults(dimensions, metrics, DateTimes.of("2011-01-01"), ImmutableMap.of("a", "b", "rows", 1)), Intervals.of("2011-01-02/2011-01-03"), makeSelectResults(dimensions, metrics, DateTimes.of("2011-01-02"), ImmutableMap.of("a", "c", "rows", 5)), Intervals.of("2011-01-05/2011-01-10"), makeSelectResults(dimensions, metrics, DateTimes.of("2011-01-05"), ImmutableMap.of("a", "d", "rows", 5), DateTimes.of("2011-01-06"), ImmutableMap.of("a", "e", "rows", 6), DateTimes.of("2011-01-07"), ImmutableMap.of("a", "f", "rows", 7), DateTimes.of("2011-01-08"), ImmutableMap.of("a", "g", "rows", 8), DateTimes.of("2011-01-09"), ImmutableMap.of("a", "h", "rows", 9)), Intervals.of("2011-01-05/2011-01-10"), makeSelectResults(dimensions, metrics, DateTimes.of("2011-01-05T01"), ImmutableMap.of("a", "d", "rows", 5), DateTimes.of("2011-01-06T01"), ImmutableMap.of("a", "e", "rows", 6), DateTimes.of("2011-01-07T01"), ImmutableMap.of("a", "f", "rows", 7), DateTimes.of("2011-01-08T01"), ImmutableMap.of("a", "g", "rows", 8), DateTimes.of("2011-01-09T01"), ImmutableMap.of("a", "h", "rows", 9)));
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), new org.apache.druid.query.select.SelectQueryQueryToolChest(CachingClusteredClientTest.JSON_MAPPER, QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator(), CachingClusteredClientTest.SELECT_CONFIG_SUPPLIER));
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedResults(makeSelectResults(dimensions, metrics, DateTimes.of("2011-01-01"), ImmutableMap.of("a", "b", "rows", 1), DateTimes.of("2011-01-02"), ImmutableMap.of("a", "c", "rows", 5), DateTimes.of("2011-01-05"), ImmutableMap.of("a", "d", "rows", 5), DateTimes.of("2011-01-05T01"), ImmutableMap.of("a", "d", "rows", 5), DateTimes.of("2011-01-06"), ImmutableMap.of("a", "e", "rows", 6), DateTimes.of("2011-01-06T01"), ImmutableMap.of("a", "e", "rows", 6), DateTimes.of("2011-01-07"), ImmutableMap.of("a", "f", "rows", 7), DateTimes.of("2011-01-07T01"), ImmutableMap.of("a", "f", "rows", 7), DateTimes.of("2011-01-08"), ImmutableMap.of("a", "g", "rows", 8), DateTimes.of("2011-01-08T01"), ImmutableMap.of("a", "g", "rows", 8), DateTimes.of("2011-01-09"), ImmutableMap.of("a", "h", "rows", 9), DateTimes.of("2011-01-09T01"), ImmutableMap.of("a", "h", "rows", 9)), runner.run(QueryPlus.wrap(builder.intervals("2011-01-01/2011-01-10").build()), context));
        SelectQuery query = builder.intervals("2011-01-01/2011-01-10").dimensionSpecs(Collections.singletonList(new DefaultDimensionSpec("a", "a2"))).build();
        TestHelper.assertExpectedResults(makeSelectResults(dimensions, metrics, DateTimes.of("2011-01-01"), ImmutableMap.of("a2", "b", "rows", 1), DateTimes.of("2011-01-02"), ImmutableMap.of("a2", "c", "rows", 5), DateTimes.of("2011-01-05"), ImmutableMap.of("a2", "d", "rows", 5), DateTimes.of("2011-01-05T01"), ImmutableMap.of("a2", "d", "rows", 5), DateTimes.of("2011-01-06"), ImmutableMap.of("a2", "e", "rows", 6), DateTimes.of("2011-01-06T01"), ImmutableMap.of("a2", "e", "rows", 6), DateTimes.of("2011-01-07"), ImmutableMap.of("a2", "f", "rows", 7), DateTimes.of("2011-01-07T01"), ImmutableMap.of("a2", "f", "rows", 7), DateTimes.of("2011-01-08"), ImmutableMap.of("a2", "g", "rows", 8), DateTimes.of("2011-01-08T01"), ImmutableMap.of("a2", "g", "rows", 8), DateTimes.of("2011-01-09"), ImmutableMap.of("a2", "h", "rows", 9), DateTimes.of("2011-01-09T01"), ImmutableMap.of("a2", "h", "rows", 9)), runner.run(QueryPlus.wrap(query), context));
    }

    @Test
    public void testGroupByCaching() {
        List<AggregatorFactory> aggsWithUniques = ImmutableList.<AggregatorFactory>builder().addAll(CachingClusteredClientTest.AGGS).add(new HyperUniquesAggregatorFactory("uniques", "uniques")).build();
        final HashFunction hashFn = Hashing.murmur3_128();
        GroupByQuery.Builder builder = new GroupByQuery.Builder().setDataSource(CachingClusteredClientTest.DATA_SOURCE).setQuerySegmentSpec(CachingClusteredClientTest.SEG_SPEC).setDimFilter(CachingClusteredClientTest.DIM_FILTER).setGranularity(CachingClusteredClientTest.GRANULARITY).setDimensions(new DefaultDimensionSpec("a", "a")).setAggregatorSpecs(aggsWithUniques).setPostAggregatorSpecs(CachingClusteredClientTest.POST_AGGS).setContext(CachingClusteredClientTest.CONTEXT);
        final HyperLogLogCollector collector = HyperLogLogCollector.makeLatestCollector();
        collector.add(hashFn.hashString("abc123", StandardCharsets.UTF_8).asBytes());
        collector.add(hashFn.hashString("123abc", StandardCharsets.UTF_8).asBytes());
        final GroupByQuery query = builder.build();
        testQueryCaching(getDefaultQueryRunner(), query, Intervals.of("2011-01-01/2011-01-02"), makeGroupByResults(DateTimes.of("2011-01-01"), ImmutableMap.of("a", "a", "rows", 1, "imps", 1, "impers", 1, "uniques", collector)), Intervals.of("2011-01-02/2011-01-03"), makeGroupByResults(DateTimes.of("2011-01-02"), ImmutableMap.of("a", "b", "rows", 2, "imps", 2, "impers", 2, "uniques", collector)), Intervals.of("2011-01-05/2011-01-10"), makeGroupByResults(DateTimes.of("2011-01-05"), ImmutableMap.of("a", "c", "rows", 3, "imps", 3, "impers", 3, "uniques", collector), DateTimes.of("2011-01-06"), ImmutableMap.of("a", "d", "rows", 4, "imps", 4, "impers", 4, "uniques", collector), DateTimes.of("2011-01-07"), ImmutableMap.of("a", "e", "rows", 5, "imps", 5, "impers", 5, "uniques", collector), DateTimes.of("2011-01-08"), ImmutableMap.of("a", "f", "rows", 6, "imps", 6, "impers", 6, "uniques", collector), DateTimes.of("2011-01-09"), ImmutableMap.of("a", "g", "rows", 7, "imps", 7, "impers", 7, "uniques", collector)), Intervals.of("2011-01-05/2011-01-10"), makeGroupByResults(DateTimes.of("2011-01-05T01"), ImmutableMap.of("a", "c", "rows", 3, "imps", 3, "impers", 3, "uniques", collector), DateTimes.of("2011-01-06T01"), ImmutableMap.of("a", "d", "rows", 4, "imps", 4, "impers", 4, "uniques", collector), DateTimes.of("2011-01-07T01"), ImmutableMap.of("a", "e", "rows", 5, "imps", 5, "impers", 5, "uniques", collector), DateTimes.of("2011-01-08T01"), ImmutableMap.of("a", "f", "rows", 6, "imps", 6, "impers", 6, "uniques", collector), DateTimes.of("2011-01-09T01"), ImmutableMap.of("a", "g", "rows", 7, "imps", 7, "impers", 7, "uniques", collector)));
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), CachingClusteredClientTest.WAREHOUSE.getToolChest(query));
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedObjects(makeGroupByResults(DateTimes.of("2011-01-05T"), ImmutableMap.of("a", "c", "rows", 3, "imps", 3, "impers", 3, "uniques", collector), DateTimes.of("2011-01-05T01"), ImmutableMap.of("a", "c", "rows", 3, "imps", 3, "impers", 3, "uniques", collector), DateTimes.of("2011-01-06T"), ImmutableMap.of("a", "d", "rows", 4, "imps", 4, "impers", 4, "uniques", collector), DateTimes.of("2011-01-06T01"), ImmutableMap.of("a", "d", "rows", 4, "imps", 4, "impers", 4, "uniques", collector), DateTimes.of("2011-01-07T"), ImmutableMap.of("a", "e", "rows", 5, "imps", 5, "impers", 5, "uniques", collector), DateTimes.of("2011-01-07T01"), ImmutableMap.of("a", "e", "rows", 5, "imps", 5, "impers", 5, "uniques", collector), DateTimes.of("2011-01-08T"), ImmutableMap.of("a", "f", "rows", 6, "imps", 6, "impers", 6, "uniques", collector), DateTimes.of("2011-01-08T01"), ImmutableMap.of("a", "f", "rows", 6, "imps", 6, "impers", 6, "uniques", collector), DateTimes.of("2011-01-09T"), ImmutableMap.of("a", "g", "rows", 7, "imps", 7, "impers", 7, "uniques", collector), DateTimes.of("2011-01-09T01"), ImmutableMap.of("a", "g", "rows", 7, "imps", 7, "impers", 7, "uniques", collector)), runner.run(QueryPlus.wrap(builder.setInterval("2011-01-05/2011-01-10").build()), context), "");
    }

    @Test
    public void testTimeBoundaryCaching() {
        testQueryCaching(getDefaultQueryRunner(), Druids.newTimeBoundaryQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).intervals(CachingClusteredClientTest.SEG_SPEC).context(CachingClusteredClientTest.CONTEXT).build(), Intervals.of("2011-01-01/2011-01-02"), makeTimeBoundaryResult(DateTimes.of("2011-01-01"), DateTimes.of("2011-01-01"), DateTimes.of("2011-01-02")), Intervals.of("2011-01-01/2011-01-03"), makeTimeBoundaryResult(DateTimes.of("2011-01-02"), DateTimes.of("2011-01-02"), DateTimes.of("2011-01-03")), Intervals.of("2011-01-01/2011-01-10"), makeTimeBoundaryResult(DateTimes.of("2011-01-05"), DateTimes.of("2011-01-05"), DateTimes.of("2011-01-10")), Intervals.of("2011-01-01/2011-01-10"), makeTimeBoundaryResult(DateTimes.of("2011-01-05T01"), DateTimes.of("2011-01-05T01"), DateTimes.of("2011-01-10")));
        testQueryCaching(getDefaultQueryRunner(), Druids.newTimeBoundaryQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).intervals(CachingClusteredClientTest.SEG_SPEC).context(CachingClusteredClientTest.CONTEXT).bound(MAX_TIME).build(), Intervals.of("2011-01-01/2011-01-02"), makeTimeBoundaryResult(DateTimes.of("2011-01-01"), null, DateTimes.of("2011-01-02")), Intervals.of("2011-01-01/2011-01-03"), makeTimeBoundaryResult(DateTimes.of("2011-01-02"), null, DateTimes.of("2011-01-03")), Intervals.of("2011-01-01/2011-01-10"), makeTimeBoundaryResult(DateTimes.of("2011-01-05"), null, DateTimes.of("2011-01-10")), Intervals.of("2011-01-01/2011-01-10"), makeTimeBoundaryResult(DateTimes.of("2011-01-05T01"), null, DateTimes.of("2011-01-10")));
        testQueryCaching(getDefaultQueryRunner(), Druids.newTimeBoundaryQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).intervals(CachingClusteredClientTest.SEG_SPEC).context(CachingClusteredClientTest.CONTEXT).bound(MIN_TIME).build(), Intervals.of("2011-01-01/2011-01-02"), makeTimeBoundaryResult(DateTimes.of("2011-01-01"), DateTimes.of("2011-01-01"), null), Intervals.of("2011-01-01/2011-01-03"), makeTimeBoundaryResult(DateTimes.of("2011-01-02"), DateTimes.of("2011-01-02"), null), Intervals.of("2011-01-01/2011-01-10"), makeTimeBoundaryResult(DateTimes.of("2011-01-05"), DateTimes.of("2011-01-05"), null), Intervals.of("2011-01-01/2011-01-10"), makeTimeBoundaryResult(DateTimes.of("2011-01-05T01"), DateTimes.of("2011-01-05T01"), null));
    }

    @Test
    public void testTimeSeriesWithFilter() {
        DimFilter filter = new AndDimFilter(new org.apache.druid.query.filter.OrDimFilter(new SelectorDimFilter("dim0", "1", null), new org.apache.druid.query.filter.BoundDimFilter("dim0", "222", "333", false, false, false, null, StringComparators.LEXICOGRAPHIC)), new AndDimFilter(new InDimFilter("dim1", Arrays.asList("0", "1", "2", "3", "4"), null), new org.apache.druid.query.filter.BoundDimFilter("dim1", "0", "3", false, true, false, null, StringComparators.LEXICOGRAPHIC), new org.apache.druid.query.filter.BoundDimFilter("dim1", "1", "9999", true, false, false, null, StringComparators.LEXICOGRAPHIC)));
        final Druids.TimeseriesQueryBuilder builder = Druids.newTimeseriesQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).intervals(CachingClusteredClientTest.SEG_SPEC).filters(filter).granularity(CachingClusteredClientTest.GRANULARITY).aggregators(CachingClusteredClientTest.AGGS).postAggregators(CachingClusteredClientTest.POST_AGGS).context(CachingClusteredClientTest.CONTEXT);
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), new org.apache.druid.query.timeseries.TimeseriesQueryQueryToolChest(QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()));
        /* For dim0 (2011-01-01/2011-01-05), the combined range is {[1,1], [222,333]}, so segments [-inf,1], [1,2], [2,3], and
        [3,4] is needed
        For dim1 (2011-01-06/2011-01-10), the combined range for the bound filters is {(1,3)}, combined this with the in
        filter result in {[2,2]}, so segments [1,2] and [2,3] is needed
         */
        List<Iterable<Result<TimeseriesResultValue>>> expectedResult = Arrays.asList(makeTimeResults(DateTimes.of("2011-01-01"), 50, 5000, DateTimes.of("2011-01-02"), 10, 1252, DateTimes.of("2011-01-03"), 20, 6213, DateTimes.of("2011-01-04"), 30, 743), makeTimeResults(DateTimes.of("2011-01-07"), 60, 6020, DateTimes.of("2011-01-08"), 70, 250));
        testQueryCachingWithFilter(runner, 3, builder.build(), expectedResult, Intervals.of("2011-01-01/2011-01-05"), makeTimeResults(DateTimes.of("2011-01-01"), 50, 5000), Intervals.of("2011-01-01/2011-01-05"), makeTimeResults(DateTimes.of("2011-01-02"), 10, 1252), Intervals.of("2011-01-01/2011-01-05"), makeTimeResults(DateTimes.of("2011-01-03"), 20, 6213), Intervals.of("2011-01-01/2011-01-05"), makeTimeResults(DateTimes.of("2011-01-04"), 30, 743), Intervals.of("2011-01-01/2011-01-05"), makeTimeResults(DateTimes.of("2011-01-05"), 40, 6000), Intervals.of("2011-01-06/2011-01-10"), makeTimeResults(DateTimes.of("2011-01-06"), 50, 425), Intervals.of("2011-01-06/2011-01-10"), makeTimeResults(DateTimes.of("2011-01-07"), 60, 6020), Intervals.of("2011-01-06/2011-01-10"), makeTimeResults(DateTimes.of("2011-01-08"), 70, 250), Intervals.of("2011-01-06/2011-01-10"), makeTimeResults(DateTimes.of("2011-01-09"), 23, 85312), Intervals.of("2011-01-06/2011-01-10"), makeTimeResults(DateTimes.of("2011-01-10"), 100, 512));
    }

    @Test
    public void testSingleDimensionPruning() {
        DimFilter filter = new AndDimFilter(new org.apache.druid.query.filter.OrDimFilter(new SelectorDimFilter("dim1", "a", null), new org.apache.druid.query.filter.BoundDimFilter("dim1", "from", "to", false, false, false, null, StringComparators.LEXICOGRAPHIC)), new AndDimFilter(new InDimFilter("dim2", Arrays.asList("a", "c", "e", "g"), null), new org.apache.druid.query.filter.BoundDimFilter("dim2", "aaa", "hi", false, false, false, null, StringComparators.LEXICOGRAPHIC), new org.apache.druid.query.filter.BoundDimFilter("dim2", "e", "zzz", true, true, false, null, StringComparators.LEXICOGRAPHIC)));
        final Druids.TimeseriesQueryBuilder builder = Druids.newTimeseriesQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).filters(filter).granularity(CachingClusteredClientTest.GRANULARITY).intervals(CachingClusteredClientTest.SEG_SPEC).context(CachingClusteredClientTest.CONTEXT).intervals("2011-01-05/2011-01-10").aggregators(CachingClusteredClientTest.RENAMED_AGGS).postAggregators(CachingClusteredClientTest.RENAMED_POST_AGGS);
        TimeseriesQuery query = builder.build();
        Map<String, Object> context = new HashMap<>();
        final Interval interval1 = Intervals.of("2011-01-06/2011-01-07");
        final Interval interval2 = Intervals.of("2011-01-07/2011-01-08");
        final Interval interval3 = Intervals.of("2011-01-08/2011-01-09");
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), new org.apache.druid.query.timeseries.TimeseriesQueryQueryToolChest(QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()));
        final DruidServer lastServer = servers[random.nextInt(servers.length)];
        ServerSelector selector1 = makeMockSingleDimensionSelector(lastServer, "dim1", null, "b", 1);
        ServerSelector selector2 = makeMockSingleDimensionSelector(lastServer, "dim1", "e", "f", 2);
        ServerSelector selector3 = makeMockSingleDimensionSelector(lastServer, "dim1", "hi", "zzz", 3);
        ServerSelector selector4 = makeMockSingleDimensionSelector(lastServer, "dim2", "a", "e", 4);
        ServerSelector selector5 = makeMockSingleDimensionSelector(lastServer, "dim2", null, null, 5);
        ServerSelector selector6 = makeMockSingleDimensionSelector(lastServer, "other", "b", null, 6);
        timeline.add(interval1, "v", new org.apache.druid.timeline.partition.StringPartitionChunk(null, "a", 1, selector1));
        timeline.add(interval1, "v", new org.apache.druid.timeline.partition.StringPartitionChunk("a", "b", 2, selector2));
        timeline.add(interval1, "v", new org.apache.druid.timeline.partition.StringPartitionChunk("b", null, 3, selector3));
        timeline.add(interval2, "v", new org.apache.druid.timeline.partition.StringPartitionChunk(null, "d", 4, selector4));
        timeline.add(interval2, "v", new org.apache.druid.timeline.partition.StringPartitionChunk("d", null, 5, selector5));
        timeline.add(interval3, "v", new org.apache.druid.timeline.partition.StringPartitionChunk(null, null, 6, selector6));
        final Capture<QueryPlus> capture = Capture.newInstance();
        final Capture<Map<String, Object>> contextCap = Capture.newInstance();
        QueryRunner mockRunner = EasyMock.createNiceMock(QueryRunner.class);
        EasyMock.expect(mockRunner.run(EasyMock.capture(capture), EasyMock.capture(contextCap))).andReturn(Sequences.empty()).anyTimes();
        EasyMock.expect(serverView.getQueryRunner(lastServer)).andReturn(mockRunner).anyTimes();
        EasyMock.replay(serverView);
        EasyMock.replay(mockRunner);
        List<SegmentDescriptor> descriptors = new ArrayList<>();
        descriptors.add(new SegmentDescriptor(interval1, "v", 1));
        descriptors.add(new SegmentDescriptor(interval1, "v", 3));
        descriptors.add(new SegmentDescriptor(interval2, "v", 5));
        descriptors.add(new SegmentDescriptor(interval3, "v", 6));
        MultipleSpecificSegmentSpec expected = new MultipleSpecificSegmentSpec(descriptors);
        runner.run(QueryPlus.wrap(query), context).toList();
        Assert.assertEquals(expected, getQuerySegmentSpec());
    }

    private static class ServerExpectation<T> {
        private final SegmentId segmentId;

        private final Interval interval;

        private final DataSegment segment;

        private final Iterable<Result<T>> results;

        public ServerExpectation(SegmentId segmentId, Interval interval, DataSegment segment, Iterable<Result<T>> results) {
            this.segmentId = segmentId;
            this.interval = interval;
            this.segment = segment;
            this.results = results;
        }

        public SegmentId getSegmentId() {
            return segmentId;
        }

        public Interval getInterval() {
            return interval;
        }

        public DataSegment getSegment() {
            return new MyDataSegment();
        }

        public Iterable<Result<T>> getResults() {
            return results;
        }

        private class MyDataSegment extends DataSegment {
            private final DataSegment baseSegment = segment;

            private MyDataSegment() {
                super("", Intervals.utc(0, 1), "", null, null, null, NoneShardSpec.instance(), null, (-1));
            }

            @Override
            @JsonProperty
            public String getDataSource() {
                return baseSegment.getDataSource();
            }

            @Override
            @JsonProperty
            public Interval getInterval() {
                return baseSegment.getInterval();
            }

            @Override
            @JsonProperty
            public Map<String, Object> getLoadSpec() {
                return baseSegment.getLoadSpec();
            }

            @Override
            @JsonProperty
            public String getVersion() {
                return baseSegment.getVersion();
            }

            @Override
            @JsonSerialize
            @JsonProperty
            public List<String> getDimensions() {
                return baseSegment.getDimensions();
            }

            @Override
            @JsonSerialize
            @JsonProperty
            public List<String> getMetrics() {
                return baseSegment.getMetrics();
            }

            @Override
            @JsonProperty
            public ShardSpec getShardSpec() {
                try {
                    return baseSegment.getShardSpec();
                } catch (IllegalStateException e) {
                    return NoneShardSpec.instance();
                }
            }

            @Override
            @JsonProperty
            public long getSize() {
                return baseSegment.getSize();
            }

            @Override
            public SegmentId getId() {
                return segmentId;
            }

            @Override
            public SegmentDescriptor toDescriptor() {
                return baseSegment.toDescriptor();
            }

            @Override
            public int compareTo(DataSegment dataSegment) {
                return baseSegment.compareTo(dataSegment);
            }

            @Override
            public boolean equals(Object o) {
                if (!(o instanceof DataSegment)) {
                    return false;
                }
                return baseSegment.equals(o);
            }

            @Override
            public int hashCode() {
                return baseSegment.hashCode();
            }

            @Override
            public String toString() {
                return baseSegment.toString();
            }
        }
    }

    private static class ServerExpectations implements Iterable<CachingClusteredClientTest.ServerExpectation> {
        private final DruidServer server;

        private final QueryRunner queryRunner;

        private final List<CachingClusteredClientTest.ServerExpectation> expectations = new ArrayList<>();

        public ServerExpectations(DruidServer server, QueryRunner queryRunner) {
            this.server = server;
            this.queryRunner = queryRunner;
        }

        public QueryRunner getQueryRunner() {
            return queryRunner;
        }

        public void addExpectation(CachingClusteredClientTest.ServerExpectation expectation) {
            expectations.add(expectation);
        }

        @Override
        public Iterator<CachingClusteredClientTest.ServerExpectation> iterator() {
            return expectations.iterator();
        }
    }

    @Test
    public void testTimeBoundaryCachingWhenTimeIsInteger() {
        testQueryCaching(getDefaultQueryRunner(), Druids.newTimeBoundaryQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).intervals(CachingClusteredClientTest.SEG_SPEC).context(CachingClusteredClientTest.CONTEXT).build(), Intervals.of("1970-01-01/1970-01-02"), makeTimeBoundaryResult(DateTimes.of("1970-01-01"), DateTimes.of("1970-01-01"), DateTimes.of("1970-01-02")), Intervals.of("1970-01-01/2011-01-03"), makeTimeBoundaryResult(DateTimes.of("1970-01-02"), DateTimes.of("1970-01-02"), DateTimes.of("1970-01-03")), Intervals.of("1970-01-01/2011-01-10"), makeTimeBoundaryResult(DateTimes.of("1970-01-05"), DateTimes.of("1970-01-05"), DateTimes.of("1970-01-10")), Intervals.of("1970-01-01/2011-01-10"), makeTimeBoundaryResult(DateTimes.of("1970-01-05T01"), DateTimes.of("1970-01-05T01"), DateTimes.of("1970-01-10")));
        testQueryCaching(getDefaultQueryRunner(), Druids.newTimeBoundaryQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).intervals(CachingClusteredClientTest.SEG_SPEC).context(CachingClusteredClientTest.CONTEXT).bound(MAX_TIME).build(), Intervals.of("1970-01-01/2011-01-02"), makeTimeBoundaryResult(DateTimes.of("1970-01-01"), null, DateTimes.of("1970-01-02")), Intervals.of("1970-01-01/2011-01-03"), makeTimeBoundaryResult(DateTimes.of("1970-01-02"), null, DateTimes.of("1970-01-03")), Intervals.of("1970-01-01/2011-01-10"), makeTimeBoundaryResult(DateTimes.of("1970-01-05"), null, DateTimes.of("1970-01-10")), Intervals.of("1970-01-01/2011-01-10"), makeTimeBoundaryResult(DateTimes.of("1970-01-05T01"), null, DateTimes.of("1970-01-10")));
        testQueryCaching(getDefaultQueryRunner(), Druids.newTimeBoundaryQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).intervals(CachingClusteredClientTest.SEG_SPEC).context(CachingClusteredClientTest.CONTEXT).bound(MIN_TIME).build(), Intervals.of("1970-01-01/2011-01-02"), makeTimeBoundaryResult(DateTimes.of("1970-01-01"), DateTimes.of("1970-01-01"), null), Intervals.of("1970-01-01/2011-01-03"), makeTimeBoundaryResult(DateTimes.of("1970-01-02"), DateTimes.of("1970-01-02"), null), Intervals.of("1970-01-01/1970-01-10"), makeTimeBoundaryResult(DateTimes.of("1970-01-05"), DateTimes.of("1970-01-05"), null), Intervals.of("1970-01-01/2011-01-10"), makeTimeBoundaryResult(DateTimes.of("1970-01-05T01"), DateTimes.of("1970-01-05T01"), null));
    }

    @Test
    public void testGroupByCachingRenamedAggs() {
        GroupByQuery.Builder builder = new GroupByQuery.Builder().setDataSource(CachingClusteredClientTest.DATA_SOURCE).setQuerySegmentSpec(CachingClusteredClientTest.SEG_SPEC).setDimFilter(CachingClusteredClientTest.DIM_FILTER).setGranularity(CachingClusteredClientTest.GRANULARITY).setDimensions(new DefaultDimensionSpec("a", "output")).setAggregatorSpecs(CachingClusteredClientTest.AGGS).setContext(CachingClusteredClientTest.CONTEXT);
        final GroupByQuery query1 = builder.build();
        testQueryCaching(getDefaultQueryRunner(), query1, Intervals.of("2011-01-01/2011-01-02"), makeGroupByResults(DateTimes.of("2011-01-01"), ImmutableMap.of("output", "a", "rows", 1, "imps", 1, "impers", 1)), Intervals.of("2011-01-02/2011-01-03"), makeGroupByResults(DateTimes.of("2011-01-02"), ImmutableMap.of("output", "b", "rows", 2, "imps", 2, "impers", 2)), Intervals.of("2011-01-05/2011-01-10"), makeGroupByResults(DateTimes.of("2011-01-05"), ImmutableMap.of("output", "c", "rows", 3, "imps", 3, "impers", 3), DateTimes.of("2011-01-06"), ImmutableMap.of("output", "d", "rows", 4, "imps", 4, "impers", 4), DateTimes.of("2011-01-07"), ImmutableMap.of("output", "e", "rows", 5, "imps", 5, "impers", 5), DateTimes.of("2011-01-08"), ImmutableMap.of("output", "f", "rows", 6, "imps", 6, "impers", 6), DateTimes.of("2011-01-09"), ImmutableMap.of("output", "g", "rows", 7, "imps", 7, "impers", 7)), Intervals.of("2011-01-05/2011-01-10"), makeGroupByResults(DateTimes.of("2011-01-05T01"), ImmutableMap.of("output", "c", "rows", 3, "imps", 3, "impers", 3), DateTimes.of("2011-01-06T01"), ImmutableMap.of("output", "d", "rows", 4, "imps", 4, "impers", 4), DateTimes.of("2011-01-07T01"), ImmutableMap.of("output", "e", "rows", 5, "imps", 5, "impers", 5), DateTimes.of("2011-01-08T01"), ImmutableMap.of("output", "f", "rows", 6, "imps", 6, "impers", 6), DateTimes.of("2011-01-09T01"), ImmutableMap.of("output", "g", "rows", 7, "imps", 7, "impers", 7)));
        QueryRunner runner = new org.apache.druid.query.FinalizeResultsQueryRunner(getDefaultQueryRunner(), CachingClusteredClientTest.WAREHOUSE.getToolChest(query1));
        HashMap<String, Object> context = new HashMap<String, Object>();
        TestHelper.assertExpectedObjects(makeGroupByResults(DateTimes.of("2011-01-05T"), ImmutableMap.of("output", "c", "rows", 3, "imps", 3, "impers", 3), DateTimes.of("2011-01-05T01"), ImmutableMap.of("output", "c", "rows", 3, "imps", 3, "impers", 3), DateTimes.of("2011-01-06T"), ImmutableMap.of("output", "d", "rows", 4, "imps", 4, "impers", 4), DateTimes.of("2011-01-06T01"), ImmutableMap.of("output", "d", "rows", 4, "imps", 4, "impers", 4), DateTimes.of("2011-01-07T"), ImmutableMap.of("output", "e", "rows", 5, "imps", 5, "impers", 5), DateTimes.of("2011-01-07T01"), ImmutableMap.of("output", "e", "rows", 5, "imps", 5, "impers", 5), DateTimes.of("2011-01-08T"), ImmutableMap.of("output", "f", "rows", 6, "imps", 6, "impers", 6), DateTimes.of("2011-01-08T01"), ImmutableMap.of("output", "f", "rows", 6, "imps", 6, "impers", 6), DateTimes.of("2011-01-09T"), ImmutableMap.of("output", "g", "rows", 7, "imps", 7, "impers", 7), DateTimes.of("2011-01-09T01"), ImmutableMap.of("output", "g", "rows", 7, "imps", 7, "impers", 7)), runner.run(QueryPlus.wrap(builder.setInterval("2011-01-05/2011-01-10").build()), context), "");
        final GroupByQuery query2 = builder.setInterval("2011-01-05/2011-01-10").setDimensions(new DefaultDimensionSpec("a", "output2")).setAggregatorSpecs(CachingClusteredClientTest.RENAMED_AGGS).build();
        TestHelper.assertExpectedObjects(makeGroupByResults(DateTimes.of("2011-01-05T"), ImmutableMap.of("output2", "c", "rows", 3, "imps", 3, "impers2", 3), DateTimes.of("2011-01-05T01"), ImmutableMap.of("output2", "c", "rows", 3, "imps", 3, "impers2", 3), DateTimes.of("2011-01-06T"), ImmutableMap.of("output2", "d", "rows", 4, "imps", 4, "impers2", 4), DateTimes.of("2011-01-06T01"), ImmutableMap.of("output2", "d", "rows", 4, "imps", 4, "impers2", 4), DateTimes.of("2011-01-07T"), ImmutableMap.of("output2", "e", "rows", 5, "imps", 5, "impers2", 5), DateTimes.of("2011-01-07T01"), ImmutableMap.of("output2", "e", "rows", 5, "imps", 5, "impers2", 5), DateTimes.of("2011-01-08T"), ImmutableMap.of("output2", "f", "rows", 6, "imps", 6, "impers2", 6), DateTimes.of("2011-01-08T01"), ImmutableMap.of("output2", "f", "rows", 6, "imps", 6, "impers2", 6), DateTimes.of("2011-01-09T"), ImmutableMap.of("output2", "g", "rows", 7, "imps", 7, "impers2", 7), DateTimes.of("2011-01-09T01"), ImmutableMap.of("output2", "g", "rows", 7, "imps", 7, "impers2", 7)), runner.run(QueryPlus.wrap(query2), context), "renamed aggregators test");
    }

    @Test
    public void testIfNoneMatch() {
        Interval interval = Intervals.of("2016/2017");
        final DataSegment dataSegment = new DataSegment("dataSource", interval, "ver", ImmutableMap.of("type", "hdfs", "path", "/tmp"), ImmutableList.of("product"), ImmutableList.of("visited_sum"), NoneShardSpec.instance(), 9, 12334);
        final ServerSelector selector = new ServerSelector(dataSegment, new org.apache.druid.client.selector.HighestPriorityTierSelectorStrategy(new RandomServerSelectorStrategy()));
        selector.addServerAndUpdateSegment(new org.apache.druid.client.selector.QueryableDruidServer(servers[0], null), dataSegment);
        timeline.add(interval, "ver", new org.apache.druid.timeline.partition.SingleElementPartitionChunk(selector));
        TimeBoundaryQuery query = Druids.newTimeBoundaryQueryBuilder().dataSource(CachingClusteredClientTest.DATA_SOURCE).intervals(new MultipleIntervalSegmentSpec(ImmutableList.of(interval))).context(ImmutableMap.of("If-None-Match", "aVJV29CJY93rszVW/QBy0arWZo0=")).build();
        Map<String, Object> responseContext = new HashMap<>();
        getDefaultQueryRunner().run(QueryPlus.wrap(query), responseContext);
        Assert.assertEquals("Z/eS4rQz5v477iq7Aashr6JPZa0=", responseContext.get("ETag"));
    }
}

