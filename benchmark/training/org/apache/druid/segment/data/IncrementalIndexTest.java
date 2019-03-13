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
package org.apache.druid.segment.data;


import Granularities.ALL;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.druid.data.input.MapBasedInputRow;
import org.apache.druid.data.input.Row;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.common.guava.Sequence;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.query.Druids;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerFactory;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.Result;
import org.apache.druid.query.aggregation.Aggregator;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.filter.SelectorDimFilter;
import org.apache.druid.query.ordering.StringComparators;
import org.apache.druid.query.timeseries.TimeseriesQuery;
import org.apache.druid.query.timeseries.TimeseriesQueryEngine;
import org.apache.druid.query.timeseries.TimeseriesResultValue;
import org.apache.druid.segment.CloserRule;
import org.apache.druid.segment.Segment;
import org.apache.druid.segment.incremental.IncrementalIndex;
import org.apache.druid.segment.incremental.IncrementalIndexSchema;
import org.apache.druid.segment.incremental.IndexSizeExceededException;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class IncrementalIndexTest {
    interface IndexCreator {
        IncrementalIndex createIndex(AggregatorFactory[] aggregatorFactories);
    }

    private static final Closer resourceCloser = Closer.create();

    private final IncrementalIndexTest.IndexCreator indexCreator;

    @Rule
    public final CloserRule closerRule = new CloserRule(false);

    public IncrementalIndexTest(IncrementalIndexTest.IndexCreator indexCreator) {
        this.indexCreator = indexCreator;
    }

    private static final AggregatorFactory[] defaultAggregatorFactories = new AggregatorFactory[]{ new CountAggregatorFactory("count") };

    private static final AggregatorFactory[] defaultCombiningAggregatorFactories = new AggregatorFactory[]{ IncrementalIndexTest.defaultAggregatorFactories[0].getCombiningFactory() };

    @Test
    public void testCaseSensitivity() throws Exception {
        long timestamp = System.currentTimeMillis();
        IncrementalIndex index = closerRule.closeLater(indexCreator.createIndex(IncrementalIndexTest.defaultAggregatorFactories));
        IncrementalIndexTest.populateIndex(timestamp, index);
        Assert.assertEquals(Arrays.asList("dim1", "dim2"), index.getDimensionNames());
        Assert.assertEquals(2, index.size());
        final Iterator<Row> rows = index.iterator();
        Row row = rows.next();
        Assert.assertEquals(timestamp, row.getTimestampFromEpoch());
        Assert.assertEquals(Collections.singletonList("1"), row.getDimension("dim1"));
        Assert.assertEquals(Collections.singletonList("2"), row.getDimension("dim2"));
        row = rows.next();
        Assert.assertEquals(timestamp, row.getTimestampFromEpoch());
        Assert.assertEquals(Collections.singletonList("3"), row.getDimension("dim1"));
        Assert.assertEquals(Collections.singletonList("4"), row.getDimension("dim2"));
    }

    @Test
    public void testFilteredAggregators() throws Exception {
        long timestamp = System.currentTimeMillis();
        IncrementalIndex index = closerRule.closeLater(indexCreator.createIndex(new AggregatorFactory[]{ new CountAggregatorFactory("count"), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("count_selector_filtered"), new SelectorDimFilter("dim2", "2", null)), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("count_bound_filtered"), new org.apache.druid.query.filter.BoundDimFilter("dim2", "2", "3", false, true, null, null, StringComparators.NUMERIC)), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("count_multivaldim_filtered"), new SelectorDimFilter("dim3", "b", null)), new org.apache.druid.query.aggregation.FilteredAggregatorFactory(new CountAggregatorFactory("count_numeric_filtered"), new SelectorDimFilter("met1", "11", null)) }));
        index.add(new MapBasedInputRow(timestamp, Arrays.asList("dim1", "dim2", "dim3"), ImmutableMap.of("dim1", "1", "dim2", "2", "dim3", Lists.newArrayList("b", "a"), "met1", 10)));
        index.add(new MapBasedInputRow(timestamp, Arrays.asList("dim1", "dim2", "dim3"), ImmutableMap.of("dim1", "3", "dim2", "4", "dim3", Lists.newArrayList("c", "d"), "met1", 11)));
        Assert.assertEquals(Arrays.asList("dim1", "dim2", "dim3"), index.getDimensionNames());
        Assert.assertEquals(Arrays.asList("count", "count_selector_filtered", "count_bound_filtered", "count_multivaldim_filtered", "count_numeric_filtered"), index.getMetricNames());
        Assert.assertEquals(2, index.size());
        final Iterator<Row> rows = index.iterator();
        Row row = rows.next();
        Assert.assertEquals(timestamp, row.getTimestampFromEpoch());
        Assert.assertEquals(Collections.singletonList("1"), row.getDimension("dim1"));
        Assert.assertEquals(Collections.singletonList("2"), row.getDimension("dim2"));
        Assert.assertEquals(Arrays.asList("a", "b"), row.getDimension("dim3"));
        Assert.assertEquals(1L, row.getMetric("count"));
        Assert.assertEquals(1L, row.getMetric("count_selector_filtered"));
        Assert.assertEquals(1L, row.getMetric("count_bound_filtered"));
        Assert.assertEquals(1L, row.getMetric("count_multivaldim_filtered"));
        Assert.assertEquals(0L, row.getMetric("count_numeric_filtered"));
        row = rows.next();
        Assert.assertEquals(timestamp, row.getTimestampFromEpoch());
        Assert.assertEquals(Collections.singletonList("3"), row.getDimension("dim1"));
        Assert.assertEquals(Collections.singletonList("4"), row.getDimension("dim2"));
        Assert.assertEquals(Arrays.asList("c", "d"), row.getDimension("dim3"));
        Assert.assertEquals(1L, row.getMetric("count"));
        Assert.assertEquals(0L, row.getMetric("count_selector_filtered"));
        Assert.assertEquals(0L, row.getMetric("count_bound_filtered"));
        Assert.assertEquals(0L, row.getMetric("count_multivaldim_filtered"));
        Assert.assertEquals(1L, row.getMetric("count_numeric_filtered"));
    }

    @Test
    public void testSingleThreadedIndexingAndQuery() throws Exception {
        final int dimensionCount = 5;
        final ArrayList<AggregatorFactory> ingestAggregatorFactories = new ArrayList<>();
        ingestAggregatorFactories.add(new CountAggregatorFactory("rows"));
        for (int i = 0; i < dimensionCount; ++i) {
            ingestAggregatorFactories.add(new org.apache.druid.query.aggregation.LongSumAggregatorFactory(StringUtils.format("sumResult%s", i), StringUtils.format("Dim_%s", i)));
            ingestAggregatorFactories.add(new org.apache.druid.query.aggregation.DoubleSumAggregatorFactory(StringUtils.format("doubleSumResult%s", i), StringUtils.format("Dim_%s", i)));
        }
        final IncrementalIndex index = closerRule.closeLater(indexCreator.createIndex(ingestAggregatorFactories.toArray(new AggregatorFactory[0])));
        final long timestamp = System.currentTimeMillis();
        final int rows = 50;
        // ingesting same data twice to have some merging happening
        for (int i = 0; i < rows; i++) {
            index.add(IncrementalIndexTest.getLongRow((timestamp + i), dimensionCount));
        }
        for (int i = 0; i < rows; i++) {
            index.add(IncrementalIndexTest.getLongRow((timestamp + i), dimensionCount));
        }
        // run a timeseries query on the index and verify results
        final ArrayList<AggregatorFactory> queryAggregatorFactories = new ArrayList<>();
        queryAggregatorFactories.add(new CountAggregatorFactory("rows"));
        for (int i = 0; i < dimensionCount; ++i) {
            queryAggregatorFactories.add(new org.apache.druid.query.aggregation.LongSumAggregatorFactory(StringUtils.format("sumResult%s", i), StringUtils.format("sumResult%s", i)));
            queryAggregatorFactories.add(new org.apache.druid.query.aggregation.DoubleSumAggregatorFactory(StringUtils.format("doubleSumResult%s", i), StringUtils.format("doubleSumResult%s", i)));
        }
        TimeseriesQuery query = Druids.newTimeseriesQueryBuilder().dataSource("xxx").granularity(ALL).intervals(ImmutableList.of(Intervals.of("2000/2030"))).aggregators(queryAggregatorFactories).build();
        final Segment incrementalIndexSegment = new org.apache.druid.segment.IncrementalIndexSegment(index, null);
        final QueryRunnerFactory factory = new org.apache.druid.query.timeseries.TimeseriesQueryRunnerFactory(new org.apache.druid.query.timeseries.TimeseriesQueryQueryToolChest(QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()), new TimeseriesQueryEngine(), QueryRunnerTestHelper.NOOP_QUERYWATCHER);
        final QueryRunner<Result<TimeseriesResultValue>> runner = new org.apache.druid.query.FinalizeResultsQueryRunner<Result<TimeseriesResultValue>>(factory.createRunner(incrementalIndexSegment), factory.getToolchest());
        List<Result<TimeseriesResultValue>> results = runner.run(QueryPlus.wrap(query), new HashMap<String, Object>()).toList();
        Result<TimeseriesResultValue> result = Iterables.getOnlyElement(results);
        boolean isRollup = index.isRollup();
        Assert.assertEquals((rows * (isRollup ? 1 : 2)), result.getValue().getLongMetric("rows").intValue());
        for (int i = 0; i < dimensionCount; ++i) {
            Assert.assertEquals(("Failed long sum on dimension " + i), (2 * rows), result.getValue().getLongMetric(("sumResult" + i)).intValue());
            Assert.assertEquals(("Failed double sum on dimension " + i), (2 * rows), result.getValue().getDoubleMetric(("doubleSumResult" + i)).intValue());
        }
    }

    @Test(timeout = 60000L)
    public void testConcurrentAddRead() throws InterruptedException, ExecutionException {
        final int dimensionCount = 5;
        final ArrayList<AggregatorFactory> ingestAggregatorFactories = new ArrayList<>((dimensionCount + 1));
        ingestAggregatorFactories.add(new CountAggregatorFactory("rows"));
        for (int i = 0; i < dimensionCount; ++i) {
            ingestAggregatorFactories.add(new org.apache.druid.query.aggregation.LongSumAggregatorFactory(StringUtils.format("sumResult%s", i), StringUtils.format("Dim_%s", i)));
            ingestAggregatorFactories.add(new org.apache.druid.query.aggregation.DoubleSumAggregatorFactory(StringUtils.format("doubleSumResult%s", i), StringUtils.format("Dim_%s", i)));
        }
        final ArrayList<AggregatorFactory> queryAggregatorFactories = new ArrayList<>((dimensionCount + 1));
        queryAggregatorFactories.add(new CountAggregatorFactory("rows"));
        for (int i = 0; i < dimensionCount; ++i) {
            queryAggregatorFactories.add(new org.apache.druid.query.aggregation.LongSumAggregatorFactory(StringUtils.format("sumResult%s", i), StringUtils.format("sumResult%s", i)));
            queryAggregatorFactories.add(new org.apache.druid.query.aggregation.DoubleSumAggregatorFactory(StringUtils.format("doubleSumResult%s", i), StringUtils.format("doubleSumResult%s", i)));
        }
        final IncrementalIndex index = closerRule.closeLater(indexCreator.createIndex(ingestAggregatorFactories.toArray(new AggregatorFactory[0])));
        final int concurrentThreads = 2;
        final int elementsPerThread = 10000;
        final ListeningExecutorService indexExecutor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(concurrentThreads, new ThreadFactoryBuilder().setDaemon(false).setNameFormat("index-executor-%d").setPriority(Thread.MIN_PRIORITY).build()));
        final ListeningExecutorService queryExecutor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(concurrentThreads, new ThreadFactoryBuilder().setDaemon(false).setNameFormat("query-executor-%d").build()));
        final long timestamp = System.currentTimeMillis();
        final Interval queryInterval = Intervals.of("1900-01-01T00:00:00Z/2900-01-01T00:00:00Z");
        final List<ListenableFuture<?>> indexFutures = Lists.newArrayListWithExpectedSize(concurrentThreads);
        final List<ListenableFuture<?>> queryFutures = Lists.newArrayListWithExpectedSize(concurrentThreads);
        final Segment incrementalIndexSegment = new org.apache.druid.segment.IncrementalIndexSegment(index, null);
        final QueryRunnerFactory factory = new org.apache.druid.query.timeseries.TimeseriesQueryRunnerFactory(new org.apache.druid.query.timeseries.TimeseriesQueryQueryToolChest(QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()), new TimeseriesQueryEngine(), QueryRunnerTestHelper.NOOP_QUERYWATCHER);
        final AtomicInteger currentlyRunning = new AtomicInteger(0);
        final AtomicInteger concurrentlyRan = new AtomicInteger(0);
        final AtomicInteger someoneRan = new AtomicInteger(0);
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch readyLatch = new CountDownLatch((concurrentThreads * 2));
        final AtomicInteger queriesAccumualted = new AtomicInteger(0);
        for (int j = 0; j < concurrentThreads; j++) {
            indexFutures.add(indexExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    readyLatch.countDown();
                    try {
                        startLatch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw Throwables.propagate(e);
                    }
                    currentlyRunning.incrementAndGet();
                    try {
                        for (int i = 0; i < elementsPerThread; i++) {
                            index.add(IncrementalIndexTest.getLongRow((timestamp + i), dimensionCount));
                            someoneRan.incrementAndGet();
                        }
                    } catch (IndexSizeExceededException e) {
                        throw Throwables.propagate(e);
                    }
                    currentlyRunning.decrementAndGet();
                }
            }));
            final TimeseriesQuery query = Druids.newTimeseriesQueryBuilder().dataSource("xxx").granularity(ALL).intervals(ImmutableList.of(queryInterval)).aggregators(queryAggregatorFactories).build();
            queryFutures.add(queryExecutor.submit(new Runnable() {
                @Override
                public void run() {
                    readyLatch.countDown();
                    try {
                        startLatch.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw Throwables.propagate(e);
                    }
                    while ((concurrentlyRan.get()) == 0) {
                        QueryRunner<Result<TimeseriesResultValue>> runner = new org.apache.druid.query.FinalizeResultsQueryRunner<Result<TimeseriesResultValue>>(factory.createRunner(incrementalIndexSegment), factory.getToolchest());
                        Map<String, Object> context = new HashMap<String, Object>();
                        Sequence<Result<TimeseriesResultValue>> sequence = runner.run(QueryPlus.wrap(query), context);
                        Double[] results = sequence.accumulate(new Double[0], new org.apache.druid.java.util.common.guava.Accumulator<Double[], Result<TimeseriesResultValue>>() {
                            @Override
                            public Double[] accumulate(Double[] accumulated, Result<TimeseriesResultValue> in) {
                                if ((currentlyRunning.get()) > 0) {
                                    concurrentlyRan.incrementAndGet();
                                }
                                queriesAccumualted.incrementAndGet();
                                return Lists.asList(in.getValue().getDoubleMetric("doubleSumResult0"), accumulated).toArray(new Double[0]);
                            }
                        });
                        for (Double result : results) {
                            final Integer maxValueExpected = (someoneRan.get()) + concurrentThreads;
                            if (maxValueExpected > 0) {
                                // Eventually consistent, but should be somewhere in that range
                                // Actual result is validated after all writes are guaranteed done.
                                Assert.assertTrue(StringUtils.format("%d >= %g >= 0 violated", maxValueExpected, result), ((result >= 0) && (result <= maxValueExpected)));
                            }
                        }
                    } 
                }
            }));
        }
        readyLatch.await();
        startLatch.countDown();
        List<ListenableFuture<?>> allFutures = new ArrayList<>(((queryFutures.size()) + (indexFutures.size())));
        allFutures.addAll(queryFutures);
        allFutures.addAll(indexFutures);
        Futures.allAsList(allFutures).get();
        Assert.assertTrue("Queries ran too fast", ((queriesAccumualted.get()) > 0));
        Assert.assertTrue("Did not hit concurrency, please try again", ((concurrentlyRan.get()) > 0));
        queryExecutor.shutdown();
        indexExecutor.shutdown();
        QueryRunner<Result<TimeseriesResultValue>> runner = new org.apache.druid.query.FinalizeResultsQueryRunner<Result<TimeseriesResultValue>>(factory.createRunner(incrementalIndexSegment), factory.getToolchest());
        TimeseriesQuery query = Druids.newTimeseriesQueryBuilder().dataSource("xxx").granularity(ALL).intervals(ImmutableList.of(queryInterval)).aggregators(queryAggregatorFactories).build();
        Map<String, Object> context = new HashMap<String, Object>();
        List<Result<TimeseriesResultValue>> results = runner.run(QueryPlus.wrap(query), context).toList();
        boolean isRollup = index.isRollup();
        for (Result<TimeseriesResultValue> result : results) {
            Assert.assertEquals((elementsPerThread * (isRollup ? 1 : concurrentThreads)), result.getValue().getLongMetric("rows").intValue());
            for (int i = 0; i < dimensionCount; ++i) {
                Assert.assertEquals(StringUtils.format("Failed long sum on dimension %d", i), (elementsPerThread * concurrentThreads), result.getValue().getLongMetric(StringUtils.format("sumResult%s", i)).intValue());
                Assert.assertEquals(StringUtils.format("Failed double sum on dimension %d", i), (elementsPerThread * concurrentThreads), result.getValue().getDoubleMetric(StringUtils.format("doubleSumResult%s", i)).intValue());
            }
        }
    }

    @Test
    public void testConcurrentAdd() throws Exception {
        final IncrementalIndex index = closerRule.closeLater(indexCreator.createIndex(IncrementalIndexTest.defaultAggregatorFactories));
        final int threadCount = 10;
        final int elementsPerThread = 200;
        final int dimensionCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        final long timestamp = System.currentTimeMillis();
        final CountDownLatch latch = new CountDownLatch(threadCount);
        for (int j = 0; j < threadCount; j++) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        for (int i = 0; i < elementsPerThread; i++) {
                            index.add(IncrementalIndexTest.getRow((timestamp + i), i, dimensionCount));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    latch.countDown();
                }
            });
        }
        Assert.assertTrue(latch.await(60, TimeUnit.SECONDS));
        boolean isRollup = index.isRollup();
        Assert.assertEquals(dimensionCount, index.getDimensionNames().size());
        Assert.assertEquals((elementsPerThread * (isRollup ? 1 : threadCount)), index.size());
        Iterator<Row> iterator = index.iterator();
        int curr = 0;
        while (iterator.hasNext()) {
            Row row = iterator.next();
            Assert.assertEquals((timestamp + (isRollup ? curr : curr / threadCount)), row.getTimestampFromEpoch());
            Assert.assertEquals((isRollup ? threadCount : 1), row.getMetric("count").intValue());
            curr++;
        } 
        Assert.assertEquals((elementsPerThread * (isRollup ? 1 : threadCount)), curr);
    }

    @Test
    public void testgetDimensions() {
        final IncrementalIndex<Aggregator> incrementalIndex = new IncrementalIndex.Builder().setIndexSchema(new IncrementalIndexSchema.Builder().withMetrics(new CountAggregatorFactory("count")).withDimensionsSpec(new DimensionsSpec(DimensionsSpec.getDefaultSchemas(Arrays.asList("dim0", "dim1")), null, null)).build()).setMaxRowCount(1000000).buildOnheap();
        closerRule.closeLater(incrementalIndex);
        Assert.assertEquals(Arrays.asList("dim0", "dim1"), incrementalIndex.getDimensionNames());
    }

    @Test
    public void testDynamicSchemaRollup() throws IndexSizeExceededException {
        IncrementalIndex<Aggregator> index = /* empty */
        new IncrementalIndex.Builder().setSimpleTestingIndexSchema().setMaxRowCount(10).buildOnheap();
        closerRule.closeLater(index);
        index.add(new MapBasedInputRow(1481871600000L, Arrays.asList("name", "host"), ImmutableMap.of("name", "name1", "host", "host")));
        index.add(new MapBasedInputRow(1481871670000L, Arrays.asList("name", "table"), ImmutableMap.of("name", "name2", "table", "table")));
        index.add(new MapBasedInputRow(1481871600000L, Arrays.asList("name", "host"), ImmutableMap.of("name", "name1", "host", "host")));
        Assert.assertEquals(2, index.size());
    }
}

