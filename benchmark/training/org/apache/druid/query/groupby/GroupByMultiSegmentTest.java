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
import OrderByColumnSpec.Direction;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Supplier;
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
import org.apache.druid.query.groupby.having.GreaterThanHavingSpec;
import org.apache.druid.query.spec.QuerySegmentSpec;
import org.apache.druid.segment.IndexIO;
import org.apache.druid.segment.IndexMergerV9;
import org.apache.druid.segment.QueryableIndex;
import org.apache.druid.segment.column.ColumnConfig;
import org.apache.druid.segment.incremental.IncrementalIndex;
import org.apache.druid.segment.writeout.OffHeapMemorySegmentWriteOutMediumFactory;
import org.junit.Assert;
import org.junit.Test;


public class GroupByMultiSegmentTest {
    public static final ObjectMapper JSON_MAPPER;

    private static final IndexMergerV9 INDEX_MERGER_V9;

    private static final IndexIO INDEX_IO;

    private File tmpDir;

    private QueryRunnerFactory<Row, GroupByQuery> groupByFactory;

    private List<IncrementalIndex> incrementalIndices = new ArrayList<>();

    private List<QueryableIndex> groupByIndices = new ArrayList<>();

    private ExecutorService executorService;

    private Closer resourceCloser;

    static {
        JSON_MAPPER = new DefaultObjectMapper();
        GroupByMultiSegmentTest.JSON_MAPPER.setInjectableValues(new InjectableValues.Std().addValue(ExprMacroTable.class, ExprMacroTable.nil()));
        INDEX_IO = new IndexIO(GroupByMultiSegmentTest.JSON_MAPPER, new ColumnConfig() {
            @Override
            public int columnCacheSizeBytes() {
                return 0;
            }
        });
        INDEX_MERGER_V9 = new IndexMergerV9(GroupByMultiSegmentTest.JSON_MAPPER, GroupByMultiSegmentTest.INDEX_IO, OffHeapMemorySegmentWriteOutMediumFactory.instance());
    }

    @Test
    public void testHavingAndNoLimitPushDown() {
        QueryToolChest<Row, GroupByQuery> toolChest = groupByFactory.getToolchest();
        QueryRunner<Row> theRunner = new org.apache.druid.query.FinalizeResultsQueryRunner(toolChest.mergeResults(groupByFactory.mergeRunners(executorService, makeGroupByMultiRunners())), ((QueryToolChest) (toolChest)));
        QuerySegmentSpec intervalSpec = new org.apache.druid.query.spec.MultipleIntervalSegmentSpec(Collections.singletonList(Intervals.utc(0, 1000000)));
        GroupByQuery query = GroupByQuery.builder().setDataSource("blah").setQuerySegmentSpec(intervalSpec).setDimensions(new DefaultDimensionSpec("dimA", null)).setAggregatorSpecs(new LongSumAggregatorFactory("metA", "metA")).setLimitSpec(new org.apache.druid.query.groupby.orderby.DefaultLimitSpec(Collections.singletonList(new org.apache.druid.query.groupby.orderby.OrderByColumnSpec("dimA", Direction.ASCENDING)), 1)).setHavingSpec(new GreaterThanHavingSpec("metA", 110)).setGranularity(ALL).build();
        Sequence<Row> queryResult = theRunner.run(QueryPlus.wrap(query), new HashMap());
        List<Row> results = queryResult.toList();
        Row expectedRow = GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "dimA", "world", "metA", 150L);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(expectedRow, results.get(0));
    }

    private static class OffheapBufferGenerator implements Supplier<ByteBuffer> {
        private static final Logger log = new Logger(GroupByMultiSegmentTest.OffheapBufferGenerator.class);

        private final String description;

        private final int computationBufferSize;

        private final AtomicLong count = new AtomicLong(0);

        public OffheapBufferGenerator(String description, int computationBufferSize) {
            this.description = description;
            this.computationBufferSize = computationBufferSize;
        }

        @Override
        public ByteBuffer get() {
            GroupByMultiSegmentTest.OffheapBufferGenerator.log.info("Allocating new %s buffer[%,d] of size[%,d]", description, count.getAndIncrement(), computationBufferSize);
            return ByteBuffer.allocateDirect(computationBufferSize);
        }
    }

    public static final QueryWatcher NOOP_QUERYWATCHER = new QueryWatcher() {
        @Override
        public void registerQuery(Query query, ListenableFuture future) {
        }
    };
}

