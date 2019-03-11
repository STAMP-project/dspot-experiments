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
package org.apache.druid.query.groupby;


import Granularities.ALL;
import GroupByQueryConfig.CTX_KEY_FORCE_LIMIT_PUSH_DOWN;
import OrderByColumnSpec.Direction;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import org.apache.druid.data.input.Row;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.guava.Sequence;
import org.apache.druid.java.util.common.guava.Sequences;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.java.util.common.logger.Logger;
import org.apache.druid.math.expr.ExprMacroTable;
import org.apache.druid.query.Query;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerFactory;
import org.apache.druid.query.QueryToolChest;
import org.apache.druid.query.QueryWatcher;
import org.apache.druid.query.aggregation.LongSumAggregatorFactory;
import org.apache.druid.query.dimension.DefaultDimensionSpec;
import org.apache.druid.query.ordering.StringComparators;
import org.apache.druid.query.spec.QuerySegmentSpec;
import org.apache.druid.segment.IndexIO;
import org.apache.druid.segment.IndexMergerV9;
import org.apache.druid.segment.QueryableIndex;
import org.apache.druid.segment.column.ColumnConfig;
import org.apache.druid.segment.incremental.IncrementalIndex;
import org.apache.druid.segment.writeout.OffHeapMemorySegmentWriteOutMediumFactory;
import org.junit.Assert;
import org.junit.Test;


public class GroupByLimitPushDownInsufficientBufferTest {
    public static final ObjectMapper JSON_MAPPER;

    private static final IndexMergerV9 INDEX_MERGER_V9;

    private static final IndexIO INDEX_IO;

    private File tmpDir;

    private QueryRunnerFactory<Row, GroupByQuery> groupByFactory;

    private QueryRunnerFactory<Row, GroupByQuery> tooSmallGroupByFactory;

    private List<IncrementalIndex> incrementalIndices = new ArrayList<>();

    private List<QueryableIndex> groupByIndices = new ArrayList<>();

    private ExecutorService executorService;

    private Closer resourceCloser;

    static {
        JSON_MAPPER = new DefaultObjectMapper();
        GroupByLimitPushDownInsufficientBufferTest.JSON_MAPPER.setInjectableValues(new InjectableValues.Std().addValue(ExprMacroTable.class, ExprMacroTable.nil()));
        INDEX_IO = new IndexIO(GroupByLimitPushDownInsufficientBufferTest.JSON_MAPPER, new ColumnConfig() {
            @Override
            public int columnCacheSizeBytes() {
                return 0;
            }
        });
        INDEX_MERGER_V9 = new IndexMergerV9(GroupByLimitPushDownInsufficientBufferTest.JSON_MAPPER, GroupByLimitPushDownInsufficientBufferTest.INDEX_IO, OffHeapMemorySegmentWriteOutMediumFactory.instance());
    }

    @Test
    public void testPartialLimitPushDownMerge() {
        // one segment's results use limit push down, the other doesn't because of insufficient buffer capacity
        QueryToolChest<Row, GroupByQuery> toolChest = groupByFactory.getToolchest();
        QueryRunner<Row> theRunner = new org.apache.druid.query.FinalizeResultsQueryRunner(toolChest.mergeResults(groupByFactory.mergeRunners(executorService, getRunner1())), ((QueryToolChest) (toolChest)));
        QueryRunner<Row> theRunner2 = new org.apache.druid.query.FinalizeResultsQueryRunner(toolChest.mergeResults(tooSmallGroupByFactory.mergeRunners(executorService, getRunner2())), ((QueryToolChest) (toolChest)));
        QueryRunner<Row> theRunner3 = new org.apache.druid.query.FinalizeResultsQueryRunner(toolChest.mergeResults(new QueryRunner<Row>() {
            @Override
            public Sequence<Row> run(QueryPlus<Row> queryPlus, Map<String, Object> responseContext) {
                return Sequences.simple(ImmutableList.of(theRunner.run(queryPlus, responseContext), theRunner2.run(queryPlus, responseContext))).flatMerge(Function.identity(), queryPlus.getQuery().getResultOrdering());
            }
        }), ((QueryToolChest) (toolChest)));
        QuerySegmentSpec intervalSpec = new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.utc(0, 1000000)));
        GroupByQuery query = GroupByQuery.builder().setDataSource("blah").setQuerySegmentSpec(intervalSpec).setDimensions(new DefaultDimensionSpec("dimA", null)).setAggregatorSpecs(new LongSumAggregatorFactory("metA", "metA")).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(Collections.singletonList(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("dimA", Direction.DESCENDING)), 3)).setGranularity(ALL).build();
        Sequence<Row> queryResult = theRunner3.run(QueryPlus.wrap(query), new HashMap());
        List<Row> results = queryResult.toList();
        Row expectedRow0 = GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "dimA", "zortaxx", "metA", 999L);
        Row expectedRow1 = GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "dimA", "zebra", "metA", 180L);
        Row expectedRow2 = GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "dimA", "world", "metA", 150L);
        Assert.assertEquals(3, results.size());
        Assert.assertEquals(expectedRow0, results.get(0));
        Assert.assertEquals(expectedRow1, results.get(1));
        Assert.assertEquals(expectedRow2, results.get(2));
    }

    @Test
    public void testPartialLimitPushDownMergeForceAggs() {
        // one segment's results use limit push down, the other doesn't because of insufficient buffer capacity
        QueryToolChest<Row, GroupByQuery> toolChest = groupByFactory.getToolchest();
        QueryRunner<Row> theRunner = new org.apache.druid.query.FinalizeResultsQueryRunner(toolChest.mergeResults(groupByFactory.mergeRunners(executorService, getRunner1())), ((QueryToolChest) (toolChest)));
        QueryRunner<Row> theRunner2 = new org.apache.druid.query.FinalizeResultsQueryRunner(toolChest.mergeResults(tooSmallGroupByFactory.mergeRunners(executorService, getRunner2())), ((QueryToolChest) (toolChest)));
        QueryRunner<Row> theRunner3 = new org.apache.druid.query.FinalizeResultsQueryRunner(toolChest.mergeResults(new QueryRunner<Row>() {
            @Override
            public Sequence<Row> run(QueryPlus<Row> queryPlus, Map<String, Object> responseContext) {
                return Sequences.simple(ImmutableList.of(theRunner.run(queryPlus, responseContext), theRunner2.run(queryPlus, responseContext))).flatMerge(Function.identity(), queryPlus.getQuery().getResultOrdering());
            }
        }), ((QueryToolChest) (toolChest)));
        QuerySegmentSpec intervalSpec = new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.utc(0, 1000000)));
        GroupByQuery query = GroupByQuery.builder().setDataSource("blah").setQuerySegmentSpec(intervalSpec).setDimensions(new DefaultDimensionSpec("dimA", null)).setAggregatorSpecs(new LongSumAggregatorFactory("metA", "metA")).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(Collections.singletonList(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("metA", Direction.DESCENDING, StringComparators.NUMERIC)), 3)).setGranularity(ALL).setContext(ImmutableMap.of(CTX_KEY_FORCE_LIMIT_PUSH_DOWN, true)).build();
        Sequence<Row> queryResult = theRunner3.run(QueryPlus.wrap(query), new HashMap());
        List<Row> results = queryResult.toList();
        Row expectedRow0 = GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "dimA", "zortaxx", "metA", 999L);
        Row expectedRow1 = GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "dimA", "foo", "metA", 200L);
        Row expectedRow2 = GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "dimA", "mango", "metA", 190L);
        Assert.assertEquals(3, results.size());
        Assert.assertEquals(expectedRow0, results.get(0));
        Assert.assertEquals(expectedRow1, results.get(1));
        Assert.assertEquals(expectedRow2, results.get(2));
    }

    private static class OffheapBufferGenerator implements Supplier<ByteBuffer> {
        private static final Logger log = new Logger(GroupByLimitPushDownInsufficientBufferTest.OffheapBufferGenerator.class);

        private final String description;

        private final int computationBufferSize;

        private final AtomicLong count = new AtomicLong(0);

        public OffheapBufferGenerator(String description, int computationBufferSize) {
            this.description = description;
            this.computationBufferSize = computationBufferSize;
        }

        @Override
        public ByteBuffer get() {
            GroupByLimitPushDownInsufficientBufferTest.OffheapBufferGenerator.log.info("Allocating new %s buffer[%,d] of size[%,d]", description, count.getAndIncrement(), computationBufferSize);
            return ByteBuffer.allocateDirect(computationBufferSize);
        }
    }

    public static final QueryWatcher NOOP_QUERYWATCHER = new QueryWatcher() {
        @Override
        public void registerQuery(Query query, ListenableFuture future) {
        }
    };
}

