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
package org.apache.druid.server.coordination;


import Granularities.DAY;
import Granularities.HOUR;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.guava.Sequence;
import org.apache.druid.java.util.common.guava.Sequences;
import org.apache.druid.java.util.common.guava.Yielder;
import org.apache.druid.java.util.common.guava.YieldingAccumulator;
import org.apache.druid.java.util.common.guava.YieldingSequenceBase;
import org.apache.druid.query.Query;
import org.apache.druid.query.QueryMetrics;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerFactory;
import org.apache.druid.query.QueryToolChest;
import org.apache.druid.query.Result;
import org.apache.druid.query.aggregation.MetricManipulationFn;
import org.apache.druid.query.search.SearchQuery;
import org.apache.druid.query.search.SearchResultValue;
import org.apache.druid.segment.AbstractSegment;
import org.apache.druid.segment.QueryableIndex;
import org.apache.druid.segment.ReferenceCountingSegment;
import org.apache.druid.segment.Segment;
import org.apache.druid.segment.StorageAdapter;
import org.apache.druid.server.SegmentManager;
import org.apache.druid.timeline.SegmentId;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class ServerManagerTest {
    private ServerManager serverManager;

    private ServerManagerTest.MyQueryRunnerFactory factory;

    private CountDownLatch queryWaitLatch;

    private CountDownLatch queryWaitYieldLatch;

    private CountDownLatch queryNotifyLatch;

    private ExecutorService serverManagerExec;

    private SegmentManager segmentManager;

    @Test
    public void testSimpleGet() {
        Future future = assertQueryable(DAY, "test", Intervals.of("P1d/2011-04-01"), ImmutableList.of(new org.apache.druid.java.util.common.Pair<String, Interval>("1", Intervals.of("P1d/2011-04-01"))));
        waitForTestVerificationAndCleanup(future);
        future = assertQueryable(DAY, "test", Intervals.of("P2d/2011-04-02"), ImmutableList.of(new org.apache.druid.java.util.common.Pair<String, Interval>("1", Intervals.of("P1d/2011-04-01")), new org.apache.druid.java.util.common.Pair<String, Interval>("2", Intervals.of("P1d/2011-04-02"))));
        waitForTestVerificationAndCleanup(future);
    }

    @Test
    public void testDelete1() {
        final String dataSouce = "test";
        final Interval interval = Intervals.of("2011-04-01/2011-04-02");
        Future future = assertQueryable(DAY, dataSouce, interval, ImmutableList.of(new org.apache.druid.java.util.common.Pair<String, Interval>("2", interval)));
        waitForTestVerificationAndCleanup(future);
        dropQueryable(dataSouce, "2", interval);
        future = assertQueryable(DAY, dataSouce, interval, ImmutableList.of(new org.apache.druid.java.util.common.Pair<String, Interval>("1", interval)));
        waitForTestVerificationAndCleanup(future);
    }

    @Test
    public void testDelete2() {
        loadQueryable("test", "3", Intervals.of("2011-04-04/2011-04-05"));
        Future future = assertQueryable(DAY, "test", Intervals.of("2011-04-04/2011-04-06"), ImmutableList.of(new org.apache.druid.java.util.common.Pair<String, Interval>("3", Intervals.of("2011-04-04/2011-04-05"))));
        waitForTestVerificationAndCleanup(future);
        dropQueryable("test", "3", Intervals.of("2011-04-04/2011-04-05"));
        dropQueryable("test", "1", Intervals.of("2011-04-04/2011-04-05"));
        future = assertQueryable(HOUR, "test", Intervals.of("2011-04-04/2011-04-04T06"), ImmutableList.of(new org.apache.druid.java.util.common.Pair<String, Interval>("2", Intervals.of("2011-04-04T00/2011-04-04T01")), new org.apache.druid.java.util.common.Pair<String, Interval>("2", Intervals.of("2011-04-04T01/2011-04-04T02")), new org.apache.druid.java.util.common.Pair<String, Interval>("2", Intervals.of("2011-04-04T02/2011-04-04T03")), new org.apache.druid.java.util.common.Pair<String, Interval>("2", Intervals.of("2011-04-04T04/2011-04-04T05")), new org.apache.druid.java.util.common.Pair<String, Interval>("2", Intervals.of("2011-04-04T05/2011-04-04T06"))));
        waitForTestVerificationAndCleanup(future);
        future = assertQueryable(HOUR, "test", Intervals.of("2011-04-04/2011-04-04T03"), ImmutableList.of(new org.apache.druid.java.util.common.Pair<String, Interval>("2", Intervals.of("2011-04-04T00/2011-04-04T01")), new org.apache.druid.java.util.common.Pair<String, Interval>("2", Intervals.of("2011-04-04T01/2011-04-04T02")), new org.apache.druid.java.util.common.Pair<String, Interval>("2", Intervals.of("2011-04-04T02/2011-04-04T03"))));
        waitForTestVerificationAndCleanup(future);
        future = assertQueryable(HOUR, "test", Intervals.of("2011-04-04T04/2011-04-04T06"), ImmutableList.of(new org.apache.druid.java.util.common.Pair<String, Interval>("2", Intervals.of("2011-04-04T04/2011-04-04T05")), new org.apache.druid.java.util.common.Pair<String, Interval>("2", Intervals.of("2011-04-04T05/2011-04-04T06"))));
        waitForTestVerificationAndCleanup(future);
    }

    @Test
    public void testReferenceCounting() throws Exception {
        loadQueryable("test", "3", Intervals.of("2011-04-04/2011-04-05"));
        Future future = assertQueryable(DAY, "test", Intervals.of("2011-04-04/2011-04-06"), ImmutableList.of(new org.apache.druid.java.util.common.Pair<String, Interval>("3", Intervals.of("2011-04-04/2011-04-05"))));
        queryNotifyLatch.await(1000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, factory.getSegmentReferences().size());
        for (ReferenceCountingSegment referenceCountingSegment : factory.getSegmentReferences()) {
            Assert.assertEquals(1, referenceCountingSegment.getNumReferences());
        }
        queryWaitYieldLatch.countDown();
        Assert.assertTrue(((factory.getAdapters().size()) == 1));
        for (ServerManagerTest.SegmentForTesting segmentForTesting : factory.getAdapters()) {
            Assert.assertFalse(segmentForTesting.isClosed());
        }
        queryWaitLatch.countDown();
        future.get();
        dropQueryable("test", "3", Intervals.of("2011-04-04/2011-04-05"));
        for (ServerManagerTest.SegmentForTesting segmentForTesting : factory.getAdapters()) {
            Assert.assertTrue(segmentForTesting.isClosed());
        }
    }

    @Test
    public void testReferenceCountingWhileQueryExecuting() throws Exception {
        loadQueryable("test", "3", Intervals.of("2011-04-04/2011-04-05"));
        Future future = assertQueryable(DAY, "test", Intervals.of("2011-04-04/2011-04-06"), ImmutableList.of(new org.apache.druid.java.util.common.Pair<String, Interval>("3", Intervals.of("2011-04-04/2011-04-05"))));
        queryNotifyLatch.await(1000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, factory.getSegmentReferences().size());
        for (ReferenceCountingSegment referenceCountingSegment : factory.getSegmentReferences()) {
            Assert.assertEquals(1, referenceCountingSegment.getNumReferences());
        }
        queryWaitYieldLatch.countDown();
        Assert.assertEquals(1, factory.getAdapters().size());
        for (ServerManagerTest.SegmentForTesting segmentForTesting : factory.getAdapters()) {
            Assert.assertFalse(segmentForTesting.isClosed());
        }
        dropQueryable("test", "3", Intervals.of("2011-04-04/2011-04-05"));
        for (ServerManagerTest.SegmentForTesting segmentForTesting : factory.getAdapters()) {
            Assert.assertFalse(segmentForTesting.isClosed());
        }
        queryWaitLatch.countDown();
        future.get();
        for (ServerManagerTest.SegmentForTesting segmentForTesting : factory.getAdapters()) {
            Assert.assertTrue(segmentForTesting.isClosed());
        }
    }

    @Test
    public void testMultipleDrops() throws Exception {
        loadQueryable("test", "3", Intervals.of("2011-04-04/2011-04-05"));
        Future future = assertQueryable(DAY, "test", Intervals.of("2011-04-04/2011-04-06"), ImmutableList.of(new org.apache.druid.java.util.common.Pair<String, Interval>("3", Intervals.of("2011-04-04/2011-04-05"))));
        queryNotifyLatch.await(1000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, factory.getSegmentReferences().size());
        for (ReferenceCountingSegment referenceCountingSegment : factory.getSegmentReferences()) {
            Assert.assertEquals(1, referenceCountingSegment.getNumReferences());
        }
        queryWaitYieldLatch.countDown();
        Assert.assertEquals(1, factory.getAdapters().size());
        for (ServerManagerTest.SegmentForTesting segmentForTesting : factory.getAdapters()) {
            Assert.assertFalse(segmentForTesting.isClosed());
        }
        dropQueryable("test", "3", Intervals.of("2011-04-04/2011-04-05"));
        dropQueryable("test", "3", Intervals.of("2011-04-04/2011-04-05"));
        for (ServerManagerTest.SegmentForTesting segmentForTesting : factory.getAdapters()) {
            Assert.assertFalse(segmentForTesting.isClosed());
        }
        queryWaitLatch.countDown();
        future.get();
        for (ServerManagerTest.SegmentForTesting segmentForTesting : factory.getAdapters()) {
            Assert.assertTrue(segmentForTesting.isClosed());
        }
    }

    public static class MyQueryRunnerFactory implements QueryRunnerFactory<Result<SearchResultValue>, SearchQuery> {
        private final CountDownLatch waitLatch;

        private final CountDownLatch waitYieldLatch;

        private final CountDownLatch notifyLatch;

        private List<ServerManagerTest.SegmentForTesting> adapters = new ArrayList<>();

        private List<ReferenceCountingSegment> segmentReferences = new ArrayList<>();

        public MyQueryRunnerFactory(CountDownLatch waitLatch, CountDownLatch waitYieldLatch, CountDownLatch notifyLatch) {
            this.waitLatch = waitLatch;
            this.waitYieldLatch = waitYieldLatch;
            this.notifyLatch = notifyLatch;
        }

        @Override
        public QueryRunner<Result<SearchResultValue>> createRunner(Segment adapter) {
            if (!(adapter instanceof ReferenceCountingSegment)) {
                throw new org.apache.druid.java.util.common.IAE("Expected instance of ReferenceCountingSegment, got %s", adapter.getClass());
            }
            final ReferenceCountingSegment segment = ((ReferenceCountingSegment) (adapter));
            Assert.assertTrue(((segment.getNumReferences()) > 0));
            segmentReferences.add(segment);
            adapters.add(((ServerManagerTest.SegmentForTesting) (segment.getBaseSegment())));
            return new ServerManagerTest.BlockingQueryRunner(new org.apache.druid.query.NoopQueryRunner(), waitLatch, waitYieldLatch, notifyLatch);
        }

        @Override
        public QueryRunner<Result<SearchResultValue>> mergeRunners(ExecutorService queryExecutor, Iterable<QueryRunner<Result<SearchResultValue>>> queryRunners) {
            return new org.apache.druid.query.ConcatQueryRunner(Sequences.simple(queryRunners));
        }

        @Override
        public QueryToolChest<Result<SearchResultValue>, SearchQuery> getToolchest() {
            return new ServerManagerTest.NoopQueryToolChest();
        }

        public List<ServerManagerTest.SegmentForTesting> getAdapters() {
            return adapters;
        }

        public List<ReferenceCountingSegment> getSegmentReferences() {
            return segmentReferences;
        }

        public void clearAdapters() {
            adapters.clear();
        }
    }

    public static class NoopQueryToolChest<T, QueryType extends Query<T>> extends QueryToolChest<T, QueryType> {
        @Override
        public QueryRunner<T> mergeResults(QueryRunner<T> runner) {
            return runner;
        }

        @Override
        public QueryMetrics<Query<?>> makeMetrics(QueryType query) {
            return new org.apache.druid.query.DefaultQueryMetrics(new DefaultObjectMapper());
        }

        @Override
        public Function<T, T> makePreComputeManipulatorFn(QueryType query, MetricManipulationFn fn) {
            return Functions.identity();
        }

        @Override
        public TypeReference<T> getResultTypeReference() {
            return new TypeReference<T>() {};
        }
    }

    private static class SegmentForTesting extends AbstractSegment {
        private final String version;

        private final Interval interval;

        private final Object lock = new Object();

        private volatile boolean closed = false;

        SegmentForTesting(String version, Interval interval) {
            this.version = version;
            this.interval = interval;
        }

        public String getVersion() {
            return version;
        }

        public Interval getInterval() {
            return interval;
        }

        @Override
        public SegmentId getId() {
            return SegmentId.dummy(version);
        }

        public boolean isClosed() {
            return closed;
        }

        @Override
        public Interval getDataInterval() {
            return interval;
        }

        @Override
        public QueryableIndex asQueryableIndex() {
            throw new UnsupportedOperationException();
        }

        @Override
        public StorageAdapter asStorageAdapter() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() {
            synchronized(lock) {
                closed = true;
            }
        }
    }

    private static class BlockingQueryRunner<T> implements QueryRunner<T> {
        private final QueryRunner<T> runner;

        private final CountDownLatch waitLatch;

        private final CountDownLatch waitYieldLatch;

        private final CountDownLatch notifyLatch;

        public BlockingQueryRunner(QueryRunner<T> runner, CountDownLatch waitLatch, CountDownLatch waitYieldLatch, CountDownLatch notifyLatch) {
            this.runner = runner;
            this.waitLatch = waitLatch;
            this.waitYieldLatch = waitYieldLatch;
            this.notifyLatch = notifyLatch;
        }

        @Override
        public Sequence<T> run(QueryPlus<T> queryPlus, Map<String, Object> responseContext) {
            return new ServerManagerTest.BlockingSequence(runner.run(queryPlus, responseContext), waitLatch, waitYieldLatch, notifyLatch);
        }
    }

    private static class BlockingSequence<T> extends YieldingSequenceBase<T> {
        private final Sequence<T> baseSequence;

        private final CountDownLatch waitLatch;

        private final CountDownLatch waitYieldLatch;

        private final CountDownLatch notifyLatch;

        private BlockingSequence(Sequence<T> baseSequence, CountDownLatch waitLatch, CountDownLatch waitYieldLatch, CountDownLatch notifyLatch) {
            this.baseSequence = baseSequence;
            this.waitLatch = waitLatch;
            this.waitYieldLatch = waitYieldLatch;
            this.notifyLatch = notifyLatch;
        }

        @Override
        public <OutType> Yielder<OutType> toYielder(final OutType initValue, final YieldingAccumulator<OutType, T> accumulator) {
            notifyLatch.countDown();
            try {
                waitYieldLatch.await(1000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
            final Yielder<OutType> baseYielder = baseSequence.toYielder(initValue, accumulator);
            return new Yielder<OutType>() {
                @Override
                public OutType get() {
                    try {
                        waitLatch.await(1000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        throw Throwables.propagate(e);
                    }
                    return baseYielder.get();
                }

                @Override
                public Yielder<OutType> next(OutType initValue) {
                    return baseYielder.next(initValue);
                }

                @Override
                public boolean isDone() {
                    return baseYielder.isDone();
                }

                @Override
                public void close() throws IOException {
                    baseYielder.close();
                }
            };
        }
    }
}

