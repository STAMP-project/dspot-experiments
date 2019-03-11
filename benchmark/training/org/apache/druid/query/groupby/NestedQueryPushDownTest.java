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
import GroupByQueryConfig.CTX_KEY_FORCE_PUSH_DOWN_NESTED_QUERY;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.druid.data.input.Row;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.guava.Sequence;
import org.apache.druid.java.util.common.logger.Logger;
import org.apache.druid.js.JavaScriptConfig;
import org.apache.druid.math.expr.ExprMacroTable;
import org.apache.druid.query.Query;
import org.apache.druid.query.QueryRunnerFactory;
import org.apache.druid.query.QueryToolChest;
import org.apache.druid.query.QueryWatcher;
import org.apache.druid.query.aggregation.LongMaxAggregatorFactory;
import org.apache.druid.query.aggregation.LongSumAggregatorFactory;
import org.apache.druid.query.dimension.DefaultDimensionSpec;
import org.apache.druid.query.extraction.RegexDimExtractionFn;
import org.apache.druid.query.groupby.having.GreaterThanHavingSpec;
import org.apache.druid.query.spec.QuerySegmentSpec;
import org.apache.druid.segment.IndexIO;
import org.apache.druid.segment.IndexMergerV9;
import org.apache.druid.segment.QueryableIndex;
import org.apache.druid.segment.incremental.IncrementalIndex;
import org.apache.druid.segment.writeout.OffHeapMemorySegmentWriteOutMediumFactory;
import org.junit.Assert;
import org.junit.Test;


public class NestedQueryPushDownTest {
    private static final IndexIO INDEX_IO;

    private static final IndexMergerV9 INDEX_MERGER_V9;

    public static final ObjectMapper JSON_MAPPER;

    private File tmpDir;

    private QueryRunnerFactory<Row, GroupByQuery> groupByFactory;

    private QueryRunnerFactory<Row, GroupByQuery> groupByFactory2;

    private List<IncrementalIndex> incrementalIndices = new ArrayList<>();

    private List<QueryableIndex> groupByIndices = new ArrayList<>();

    private ExecutorService executorService;

    static {
        JSON_MAPPER = new DefaultObjectMapper();
        NestedQueryPushDownTest.JSON_MAPPER.setInjectableValues(new InjectableValues.Std().addValue(ExprMacroTable.class, ExprMacroTable.nil()));
        INDEX_IO = new IndexIO(NestedQueryPushDownTest.JSON_MAPPER, () -> 0);
        INDEX_MERGER_V9 = new IndexMergerV9(NestedQueryPushDownTest.JSON_MAPPER, NestedQueryPushDownTest.INDEX_IO, OffHeapMemorySegmentWriteOutMediumFactory.instance());
    }

    @Test
    public void testSimpleDoubleAggregation() {
        QuerySegmentSpec intervalSpec = new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.utc(1500000000000L, 1600000000000L)));
        GroupByQuery query = GroupByQuery.builder().setDataSource("blah").setQuerySegmentSpec(intervalSpec).setDimensions(new DefaultDimensionSpec("dimA", "dimA"), new DefaultDimensionSpec("dimB", "dimB")).setAggregatorSpecs(new LongSumAggregatorFactory("metASum", "metA"), new LongSumAggregatorFactory("metBSum", "metB")).setGranularity(ALL).build();
        GroupByQuery nestedQuery = GroupByQuery.builder().setDataSource(query).setQuerySegmentSpec(intervalSpec).setDimensions(new DefaultDimensionSpec("dimB", "dimB")).setAggregatorSpecs(new LongSumAggregatorFactory("totalSum", "metASum")).setContext(ImmutableMap.of(CTX_KEY_FORCE_PUSH_DOWN_NESTED_QUERY, true)).setGranularity(ALL).build();
        Sequence<Row> queryResult = runNestedQueryWithForcePushDown(nestedQuery, new HashMap());
        List<Row> results = queryResult.toList();
        Row expectedRow0 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "dimB", "sour", "totalSum", 2000L);
        Row expectedRow1 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "dimB", "sweet", "totalSum", 6000L);
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(expectedRow0, results.get(0));
        Assert.assertEquals(expectedRow1, results.get(1));
    }

    @Test
    public void testNestedQueryWithRenamedDimensions() {
        QuerySegmentSpec intervalSpec = new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.utc(1500000000000L, 1600000000000L)));
        GroupByQuery query = GroupByQuery.builder().setDataSource("blah").setQuerySegmentSpec(intervalSpec).setDimensions(new DefaultDimensionSpec("dimA", "dimA"), new DefaultDimensionSpec("dimB", "newDimB")).setAggregatorSpecs(new LongSumAggregatorFactory("metASum", "metA"), new LongSumAggregatorFactory("metBSum", "metB")).setGranularity(ALL).build();
        GroupByQuery nestedQuery = GroupByQuery.builder().setDataSource(query).setQuerySegmentSpec(intervalSpec).setDimensions(new DefaultDimensionSpec("newDimB", "renamedDimB")).setAggregatorSpecs(new LongMaxAggregatorFactory("maxBSum", "metBSum")).setContext(ImmutableMap.of(CTX_KEY_FORCE_PUSH_DOWN_NESTED_QUERY, true)).setGranularity(ALL).build();
        Sequence<Row> queryResult = runNestedQueryWithForcePushDown(nestedQuery, new HashMap());
        List<Row> results = queryResult.toList();
        Row expectedRow0 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "renamedDimB", "sour", "maxBSum", 20L);
        Row expectedRow1 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "renamedDimB", "sweet", "maxBSum", 60L);
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(expectedRow0, results.get(0));
        Assert.assertEquals(expectedRow1, results.get(1));
    }

    @Test
    public void testDimensionFilterOnOuterAndInnerQueries() {
        QuerySegmentSpec intervalSpec = new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.utc(1500000000000L, 1600000000000L)));
        GroupByQuery query = GroupByQuery.builder().setDataSource("blah").setDimensions(new DefaultDimensionSpec("dimA", "dimA"), new DefaultDimensionSpec("dimB", "dimB")).setAggregatorSpecs(new LongSumAggregatorFactory("metASum", "metA"), new LongSumAggregatorFactory("metBSum", "metB")).setGranularity(ALL).setQuerySegmentSpec(intervalSpec).setDimFilter(new org.apache.druid.query.filter.JavaScriptDimFilter("dimA", "function(dim){ return dim == 'mango' }", null, JavaScriptConfig.getEnabledInstance())).build();
        GroupByQuery nestedQuery = GroupByQuery.builder().setDataSource(query).setDimensions(new DefaultDimensionSpec("dimA", "newDimA")).setAggregatorSpecs(new LongSumAggregatorFactory("finalSum", "metASum")).setContext(ImmutableMap.of(CTX_KEY_FORCE_PUSH_DOWN_NESTED_QUERY, true)).setGranularity(ALL).setDimFilter(new org.apache.druid.query.filter.JavaScriptDimFilter("dimA", "function(dim){ return dim == 'pomegranate' }", null, JavaScriptConfig.getEnabledInstance())).setQuerySegmentSpec(intervalSpec).build();
        Sequence<Row> queryResult = runNestedQueryWithForcePushDown(nestedQuery, new HashMap());
        List<Row> results = queryResult.toList();
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void testDimensionFilterOnOuterQuery() {
        QuerySegmentSpec intervalSpec = new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.utc(1500000000000L, 1600000000000L)));
        GroupByQuery query = GroupByQuery.builder().setDataSource("blah").setDimensions(new DefaultDimensionSpec("dimA", "dimA"), new DefaultDimensionSpec("dimB", "dimB")).setAggregatorSpecs(new LongSumAggregatorFactory("metASum", "metA"), new LongSumAggregatorFactory("metBSum", "metB")).setGranularity(ALL).setQuerySegmentSpec(intervalSpec).build();
        GroupByQuery nestedQuery = GroupByQuery.builder().setDataSource(query).setDimensions(new DefaultDimensionSpec("dimA", "newDimA")).setAggregatorSpecs(new LongSumAggregatorFactory("finalSum", "metASum")).setContext(ImmutableMap.of(CTX_KEY_FORCE_PUSH_DOWN_NESTED_QUERY, true)).setGranularity(ALL).setDimFilter(new org.apache.druid.query.filter.JavaScriptDimFilter("dimA", "function(dim){ return dim == 'mango' }", null, JavaScriptConfig.getEnabledInstance())).setQuerySegmentSpec(intervalSpec).build();
        Row expectedRow0 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "finalSum", 4000L, "newDimA", "mango");
        Sequence<Row> queryResult = runNestedQueryWithForcePushDown(nestedQuery, new HashMap());
        List<Row> results = queryResult.toList();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(expectedRow0, results.get(0));
    }

    @Test
    public void testDimensionFilterOnInnerQuery() {
        QuerySegmentSpec intervalSpec = new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.utc(1500000000000L, 1600000000000L)));
        GroupByQuery query = GroupByQuery.builder().setDataSource("blah").setDimensions(new DefaultDimensionSpec("dimA", "dimA"), new DefaultDimensionSpec("dimB", "dimB")).setAggregatorSpecs(new LongSumAggregatorFactory("metASum", "metA"), new LongSumAggregatorFactory("metBSum", "metB")).setGranularity(ALL).setQuerySegmentSpec(intervalSpec).setDimFilter(new org.apache.druid.query.filter.JavaScriptDimFilter("dimA", "function(dim){ return dim == 'mango' }", null, JavaScriptConfig.getEnabledInstance())).build();
        GroupByQuery nestedQuery = GroupByQuery.builder().setDataSource(query).setDimensions(new DefaultDimensionSpec("dimA", "newDimA")).setAggregatorSpecs(new LongSumAggregatorFactory("finalSum", "metASum")).setContext(ImmutableMap.of(CTX_KEY_FORCE_PUSH_DOWN_NESTED_QUERY, true)).setGranularity(ALL).setQuerySegmentSpec(intervalSpec).build();
        Row expectedRow0 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "finalSum", 4000L, "newDimA", "mango");
        Sequence<Row> queryResult = runNestedQueryWithForcePushDown(nestedQuery, new HashMap());
        List<Row> results = queryResult.toList();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(expectedRow0, results.get(0));
    }

    @Test
    public void testSubqueryWithExtractionFnInOuterQuery() {
        QuerySegmentSpec intervalSpec = new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.utc(1500000000000L, 1600000000000L)));
        GroupByQuery query = GroupByQuery.builder().setDataSource("blah").setDimensions(new DefaultDimensionSpec("dimA", "dimA"), new DefaultDimensionSpec("dimB", "dimB")).setAggregatorSpecs(new LongSumAggregatorFactory("metASum", "metA"), new LongSumAggregatorFactory("metBSum", "metB")).setGranularity(ALL).setQuerySegmentSpec(intervalSpec).build();
        GroupByQuery nestedQuery = GroupByQuery.builder().setDataSource(query).setDimensions(new org.apache.druid.query.dimension.ExtractionDimensionSpec("dimA", "extractedDimA", new RegexDimExtractionFn("^(p)", true, "replacement"))).setAggregatorSpecs(new LongSumAggregatorFactory("finalSum", "metASum")).setContext(ImmutableMap.of(CTX_KEY_FORCE_PUSH_DOWN_NESTED_QUERY, true)).setGranularity(ALL).setQuerySegmentSpec(intervalSpec).build();
        Row expectedRow0 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "finalSum", 4000L, "extractedDimA", "p");
        Row expectedRow1 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "finalSum", 4000L, "extractedDimA", "replacement");
        Sequence<Row> queryResult = runNestedQueryWithForcePushDown(nestedQuery, new HashMap());
        List<Row> results = queryResult.toList();
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(expectedRow0, results.get(0));
        Assert.assertEquals(expectedRow1, results.get(1));
    }

    @Test
    public void testHavingClauseInNestedPushDownQuery() {
        QuerySegmentSpec intervalSpec = new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.utc(1500000000000L, 1600000000000L)));
        GroupByQuery innerQuery = GroupByQuery.builder().setDataSource("blah").setDimensions(new DefaultDimensionSpec("dimA", "dimA"), new DefaultDimensionSpec("dimB", "dimB")).setAggregatorSpecs(new LongSumAggregatorFactory("metASum", "metA"), new LongSumAggregatorFactory("metBSum", "metB")).setGranularity(ALL).setQuerySegmentSpec(intervalSpec).build();
        GroupByQuery nestedQuery = GroupByQuery.builder().setDataSource(innerQuery).setDimensions(new DefaultDimensionSpec("dimB", "dimB")).setAggregatorSpecs(new LongSumAggregatorFactory("finalSum", "metBSum")).setHavingSpec(new GreaterThanHavingSpec("finalSum", 70L)).setContext(ImmutableMap.of(CTX_KEY_FORCE_PUSH_DOWN_NESTED_QUERY, true)).setGranularity(ALL).setQuerySegmentSpec(intervalSpec).build();
        Row expectedRow0 = GroupByQueryRunnerTestHelper.createExpectedRow("2017-07-14T02:40:00.000Z", "dimB", "sweet", "finalSum", 90L);
        Sequence<Row> queryResult = runNestedQueryWithForcePushDown(nestedQuery, new HashMap());
        List<Row> results = queryResult.toList();
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(expectedRow0, results.get(0));
    }

    @Test
    public void testQueryRewriteForPushDown() {
        QuerySegmentSpec intervalSpec = new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.utc(1500000000000L, 1600000000000L)));
        String outputNameB = "dimBOutput";
        String outputNameAgg = "totalSum";
        GroupByQuery query = GroupByQuery.builder().setDataSource("blah").setQuerySegmentSpec(intervalSpec).setDimensions(new DefaultDimensionSpec("dimA", "dimA"), new DefaultDimensionSpec("dimB", "dimB")).setAggregatorSpecs(new LongSumAggregatorFactory("metASum", "metA"), new LongSumAggregatorFactory("metBSum", "metB")).setGranularity(ALL).build();
        GroupByQuery nestedQuery = GroupByQuery.builder().setDataSource(query).setQuerySegmentSpec(intervalSpec).setDimensions(new DefaultDimensionSpec("dimB", outputNameB)).setAggregatorSpecs(new LongSumAggregatorFactory(outputNameAgg, "metASum")).setContext(ImmutableMap.of(CTX_KEY_FORCE_PUSH_DOWN_NESTED_QUERY, true)).setGranularity(ALL).build();
        QueryToolChest<Row, GroupByQuery> toolChest = groupByFactory.getToolchest();
        GroupByQuery rewrittenQuery = ((GroupByQueryQueryToolChest) (toolChest)).rewriteNestedQueryForPushDown(nestedQuery);
        Assert.assertEquals(outputNameB, rewrittenQuery.getDimensions().get(0).getDimension());
        Assert.assertEquals(outputNameAgg, rewrittenQuery.getAggregatorSpecs().get(0).getName());
    }

    private static class OffheapBufferGenerator implements Supplier<ByteBuffer> {
        private static final Logger log = new Logger(NestedQueryPushDownTest.OffheapBufferGenerator.class);

        private final String description;

        private final int computationBufferSize;

        private final AtomicLong count = new AtomicLong(0);

        public OffheapBufferGenerator(String description, int computationBufferSize) {
            this.description = description;
            this.computationBufferSize = computationBufferSize;
        }

        @Override
        public ByteBuffer get() {
            NestedQueryPushDownTest.OffheapBufferGenerator.log.info("Allocating new %s buffer[%,d] of size[%,d]", description, count.getAndIncrement(), computationBufferSize);
            return ByteBuffer.allocateDirect(computationBufferSize);
        }
    }

    public static final QueryWatcher NOOP_QUERYWATCHER = new QueryWatcher() {
        @Override
        public void registerQuery(Query query, ListenableFuture future) {
        }
    };
}

