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
import GroupByQueryConfig.CTX_KEY_APPLY_LIMIT_PUSH_DOWN;
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
import java.util.Arrays;
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
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.aggregation.LongSumAggregatorFactory;
import org.apache.druid.query.dimension.DefaultDimensionSpec;
import org.apache.druid.query.expression.TestExprMacroTable;
import org.apache.druid.query.extraction.TimeFormatExtractionFn;
import org.apache.druid.query.groupby.orderby.DefaultLimitSpec;
import org.apache.druid.query.ordering.StringComparators;
import org.apache.druid.query.spec.QuerySegmentSpec;
import org.apache.druid.segment.IndexIO;
import org.apache.druid.segment.IndexMergerV9;
import org.apache.druid.segment.QueryableIndex;
import org.apache.druid.segment.column.ColumnConfig;
import org.apache.druid.segment.column.ColumnHolder;
import org.apache.druid.segment.column.ValueType;
import org.apache.druid.segment.incremental.IncrementalIndex;
import org.apache.druid.segment.writeout.OffHeapMemorySegmentWriteOutMediumFactory;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Test;


public class GroupByLimitPushDownMultiNodeMergeTest {
    public static final ObjectMapper JSON_MAPPER;

    private static final IndexMergerV9 INDEX_MERGER_V9;

    private static final IndexIO INDEX_IO;

    private File tmpDir;

    private QueryRunnerFactory<Row, GroupByQuery> groupByFactory;

    private QueryRunnerFactory<Row, GroupByQuery> groupByFactory2;

    private List<IncrementalIndex> incrementalIndices = new ArrayList<>();

    private List<QueryableIndex> groupByIndices = new ArrayList<>();

    private ExecutorService executorService;

    private Closer resourceCloser;

    static {
        JSON_MAPPER = new DefaultObjectMapper();
        GroupByLimitPushDownMultiNodeMergeTest.JSON_MAPPER.setInjectableValues(new InjectableValues.Std().addValue(ExprMacroTable.class, ExprMacroTable.nil()));
        INDEX_IO = new IndexIO(GroupByLimitPushDownMultiNodeMergeTest.JSON_MAPPER, new ColumnConfig() {
            @Override
            public int columnCacheSizeBytes() {
                return 0;
            }
        });
        INDEX_MERGER_V9 = new IndexMergerV9(GroupByLimitPushDownMultiNodeMergeTest.JSON_MAPPER, GroupByLimitPushDownMultiNodeMergeTest.INDEX_IO, OffHeapMemorySegmentWriteOutMediumFactory.instance());
    }

    @Test
    public void testDescendingNumerics() {
        QueryToolChest<Row, GroupByQuery> toolChest = groupByFactory.getToolchest();
        QueryRunner<Row> theRunner = new org.apache.druid.query.FinalizeResultsQueryRunner(toolChest.mergeResults(groupByFactory.mergeRunners(executorService, getRunner1(2))), ((QueryToolChest) (toolChest)));
        QueryRunner<Row> theRunner2 = new org.apache.druid.query.FinalizeResultsQueryRunner(toolChest.mergeResults(groupByFactory2.mergeRunners(executorService, getRunner2(3))), ((QueryToolChest) (toolChest)));
        QueryRunner<Row> finalRunner = new org.apache.druid.query.FinalizeResultsQueryRunner(toolChest.mergeResults(new QueryRunner<Row>() {
            @Override
            public Sequence<Row> run(QueryPlus<Row> queryPlus, Map<String, Object> responseContext) {
                return Sequences.simple(ImmutableList.of(theRunner.run(queryPlus, responseContext), theRunner2.run(queryPlus, responseContext))).flatMerge(Function.identity(), queryPlus.getQuery().getResultOrdering());
            }
        }), ((QueryToolChest) (toolChest)));
        QuerySegmentSpec intervalSpec = new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.utc(1500000000000L, 1900000000000L)));
        DefaultLimitSpec ls2 = new DefaultLimitSpec(Arrays.asList(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d0", Direction.DESCENDING, StringComparators.NUMERIC), new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d1", Direction.DESCENDING, StringComparators.NUMERIC), new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("d2", Direction.DESCENDING, StringComparators.NUMERIC)), 100);
        GroupByQuery query = GroupByQuery.builder().setDataSource("blah").setQuerySegmentSpec(intervalSpec).setVirtualColumns(new org.apache.druid.segment.virtual.ExpressionVirtualColumn("d0:v", "timestamp_extract(\"__time\",\'YEAR\',\'UTC\')", ValueType.LONG, TestExprMacroTable.INSTANCE), new org.apache.druid.segment.virtual.ExpressionVirtualColumn("d1:v", "timestamp_extract(\"__time\",\'MONTH\',\'UTC\')", ValueType.LONG, TestExprMacroTable.INSTANCE), new org.apache.druid.segment.virtual.ExpressionVirtualColumn("d2:v", "timestamp_extract(\"__time\",\'DAY\',\'UTC\')", ValueType.LONG, TestExprMacroTable.INSTANCE)).setDimensions(new DefaultDimensionSpec("d0:v", "d0", ValueType.LONG), new DefaultDimensionSpec("d1:v", "d1", ValueType.LONG), new DefaultDimensionSpec("d2:v", "d2", ValueType.LONG)).setAggregatorSpecs(new CountAggregatorFactory("a0")).setLimitSpec(ls2).setContext(ImmutableMap.of(CTX_KEY_APPLY_LIMIT_PUSH_DOWN, true)).setGranularity(ALL).build();
        Sequence<Row> queryResult = finalRunner.run(QueryPlus.wrap(query), new HashMap());
        List<Row> results = queryResult.toList();
        Row expectedRow0 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "d0", 2027L, "d1", 3L, "d2", 17L, "a0", 2L);
        Row expectedRow1 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "d0", 2024L, "d1", 1L, "d2", 14L, "a0", 2L);
        Row expectedRow2 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "d0", 2020L, "d1", 11L, "d2", 13L, "a0", 2L);
        Row expectedRow3 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "d0", 2017L, "d1", 9L, "d2", 13L, "a0", 2L);
        System.out.println(results);
        Assert.assertEquals(4, results.size());
        Assert.assertEquals(expectedRow0, results.get(0));
        Assert.assertEquals(expectedRow1, results.get(1));
        Assert.assertEquals(expectedRow2, results.get(2));
        Assert.assertEquals(expectedRow3, results.get(3));
    }

    @Test
    public void testPartialLimitPushDownMerge() {
        // one segment's results use limit push down, the other doesn't because of insufficient buffer capacity
        QueryToolChest<Row, GroupByQuery> toolChest = groupByFactory.getToolchest();
        QueryRunner<Row> theRunner = new org.apache.druid.query.FinalizeResultsQueryRunner(toolChest.mergeResults(groupByFactory.mergeRunners(executorService, getRunner1(0))), ((QueryToolChest) (toolChest)));
        QueryRunner<Row> theRunner2 = new org.apache.druid.query.FinalizeResultsQueryRunner(toolChest.mergeResults(groupByFactory2.mergeRunners(executorService, getRunner2(1))), ((QueryToolChest) (toolChest)));
        QueryRunner<Row> finalRunner = new org.apache.druid.query.FinalizeResultsQueryRunner(toolChest.mergeResults(new QueryRunner<Row>() {
            @Override
            public Sequence<Row> run(QueryPlus<Row> queryPlus, Map<String, Object> responseContext) {
                return Sequences.simple(ImmutableList.of(theRunner.run(queryPlus, responseContext), theRunner2.run(queryPlus, responseContext))).flatMerge(Function.identity(), queryPlus.getQuery().getResultOrdering());
            }
        }), ((QueryToolChest) (toolChest)));
        QuerySegmentSpec intervalSpec = new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.utc(1500000000000L, 1600000000000L)));
        GroupByQuery query = GroupByQuery.builder().setDataSource("blah").setQuerySegmentSpec(intervalSpec).setDimensions(new DefaultDimensionSpec("dimA", "dimA"), new org.apache.druid.query.dimension.ExtractionDimensionSpec(ColumnHolder.TIME_COLUMN_NAME, "hour", ValueType.LONG, new TimeFormatExtractionFn(null, null, null, new org.apache.druid.java.util.common.granularity.PeriodGranularity(new Period("PT1H"), null, DateTimeZone.UTC), true))).setAggregatorSpecs(new LongSumAggregatorFactory("metASum", "metA")).setLimitSpec(new DefaultLimitSpec(Arrays.asList(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("hour", Direction.ASCENDING, StringComparators.NUMERIC), new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("dimA", Direction.ASCENDING)), 1000)).setContext(ImmutableMap.of(CTX_KEY_APPLY_LIMIT_PUSH_DOWN, true)).setGranularity(ALL).build();
        Sequence<Row> queryResult = finalRunner.run(QueryPlus.wrap(query), new HashMap());
        List<Row> results = queryResult.toList();
        Row expectedRow0 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "dimA", "mango", "hour", 1505260800000L, "metASum", 26L);
        Row expectedRow1 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "dimA", "pomegranate", "hour", 1505260800000L, "metASum", 7113L);
        Row expectedRow2 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "dimA", "mango", "hour", 1505264400000L, "metASum", 10L);
        Row expectedRow3 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "dimA", "pomegranate", "hour", 1505264400000L, "metASum", 7726L);
        Assert.assertEquals(4, results.size());
        Assert.assertEquals(expectedRow0, results.get(0));
        Assert.assertEquals(expectedRow1, results.get(1));
        Assert.assertEquals(expectedRow2, results.get(2));
        Assert.assertEquals(expectedRow3, results.get(3));
    }

    private static class OffheapBufferGenerator implements Supplier<ByteBuffer> {
        private static final Logger log = new Logger(GroupByLimitPushDownMultiNodeMergeTest.OffheapBufferGenerator.class);

        private final String description;

        private final int computationBufferSize;

        private final AtomicLong count = new AtomicLong(0);

        public OffheapBufferGenerator(String description, int computationBufferSize) {
            this.description = description;
            this.computationBufferSize = computationBufferSize;
        }

        @Override
        public ByteBuffer get() {
            GroupByLimitPushDownMultiNodeMergeTest.OffheapBufferGenerator.log.info("Allocating new %s buffer[%,d] of size[%,d]", description, count.getAndIncrement(), computationBufferSize);
            return ByteBuffer.allocateDirect(computationBufferSize);
        }
    }

    public static final QueryWatcher NOOP_QUERYWATCHER = new QueryWatcher() {
        @Override
        public void registerQuery(Query query, ListenableFuture future) {
        }
    };
}

