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
import QueryContexts.TIMEOUT_KEY;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import org.apache.druid.collections.CloseableDefaultBlockingPool;
import org.apache.druid.collections.CloseableStupidPool;
import org.apache.druid.collections.ReferenceCountingResourceHolder;
import org.apache.druid.data.input.Row;
import org.apache.druid.java.util.common.concurrent.Execs;
import org.apache.druid.query.DruidProcessingConfig;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.aggregation.LongSumAggregatorFactory;
import org.apache.druid.query.dimension.DefaultDimensionSpec;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class GroupByQueryMergeBufferTest {
    private static final long TIMEOUT = 5000;

    private static class TestBlockingPool extends CloseableDefaultBlockingPool<ByteBuffer> {
        private int minRemainBufferNum;

        public TestBlockingPool(Supplier<ByteBuffer> generator, int limit) {
            super(generator, limit);
            minRemainBufferNum = limit;
        }

        @Override
        public ReferenceCountingResourceHolder<ByteBuffer> take(final long timeout) {
            final ReferenceCountingResourceHolder<ByteBuffer> holder = super.take(timeout);
            final int poolSize = getPoolSize();
            if ((minRemainBufferNum) > poolSize) {
                minRemainBufferNum = poolSize;
            }
            return holder;
        }

        @Override
        public List<ReferenceCountingResourceHolder<ByteBuffer>> takeBatch(final int maxElements, final long timeout) {
            final List<ReferenceCountingResourceHolder<ByteBuffer>> holder = super.takeBatch(maxElements, timeout);
            final int poolSize = getPoolSize();
            if ((minRemainBufferNum) > poolSize) {
                minRemainBufferNum = poolSize;
            }
            return holder;
        }

        public void resetMinRemainBufferNum() {
            minRemainBufferNum = GroupByQueryMergeBufferTest.PROCESSING_CONFIG.getNumMergeBuffers();
        }

        public int getMinRemainBufferNum() {
            return minRemainBufferNum;
        }
    }

    public static final DruidProcessingConfig PROCESSING_CONFIG = new DruidProcessingConfig() {
        @Override
        public String getFormatString() {
            return null;
        }

        @Override
        public int intermediateComputeSizeBytes() {
            return (10 * 1024) * 1024;
        }

        @Override
        public int getNumMergeBuffers() {
            return 3;
        }

        @Override
        public int getNumThreads() {
            return 1;
        }
    };

    private static final CloseableStupidPool<ByteBuffer> bufferPool = new CloseableStupidPool("GroupByQueryEngine-bufferPool", new Supplier<ByteBuffer>() {
        @Override
        public ByteBuffer get() {
            return ByteBuffer.allocateDirect(GroupByQueryMergeBufferTest.PROCESSING_CONFIG.intermediateComputeSizeBytes());
        }
    });

    private static final GroupByQueryMergeBufferTest.TestBlockingPool mergeBufferPool = new GroupByQueryMergeBufferTest.TestBlockingPool(new Supplier<ByteBuffer>() {
        @Override
        public ByteBuffer get() {
            return ByteBuffer.allocateDirect(GroupByQueryMergeBufferTest.PROCESSING_CONFIG.intermediateComputeSizeBytes());
        }
    }, GroupByQueryMergeBufferTest.PROCESSING_CONFIG.getNumMergeBuffers());

    private static final GroupByQueryRunnerFactory factory = GroupByQueryMergeBufferTest.makeQueryRunnerFactory(GroupByQueryRunnerTest.DEFAULT_MAPPER, new GroupByQueryConfig() {
        @Override
        public String getDefaultStrategy() {
            return "v2";
        }
    });

    private QueryRunner<Row> runner;

    public GroupByQueryMergeBufferTest(QueryRunner<Row> runner) {
        this.runner = GroupByQueryMergeBufferTest.factory.mergeRunners(Execs.directExecutor(), ImmutableList.of(runner));
    }

    @Test
    public void testSimpleGroupBy() {
        final GroupByQuery query = GroupByQuery.builder().setDataSource(QueryRunnerTestHelper.dataSource).setGranularity(ALL).setInterval(QueryRunnerTestHelper.firstToThird).setAggregatorSpecs(new LongSumAggregatorFactory("rows", "rows")).setContext(ImmutableMap.of(TIMEOUT_KEY, GroupByQueryMergeBufferTest.TIMEOUT)).build();
        GroupByQueryRunnerTestHelper.runQuery(GroupByQueryMergeBufferTest.factory, runner, query);
        Assert.assertEquals(2, GroupByQueryMergeBufferTest.mergeBufferPool.getMinRemainBufferNum());
        Assert.assertEquals(3, GroupByQueryMergeBufferTest.mergeBufferPool.getPoolSize());
    }

    @Test
    public void testNestedGroupBy() {
        final GroupByQuery query = GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(QueryRunnerTestHelper.dataSource).setInterval(QueryRunnerTestHelper.firstToThird).setGranularity(ALL).setDimensions(new DefaultDimensionSpec("quality", "alias")).setAggregatorSpecs(Collections.singletonList(QueryRunnerTestHelper.rowsCount)).build())).setGranularity(ALL).setInterval(QueryRunnerTestHelper.firstToThird).setAggregatorSpecs(new LongSumAggregatorFactory("rows", "rows")).setContext(ImmutableMap.of(TIMEOUT_KEY, GroupByQueryMergeBufferTest.TIMEOUT)).build();
        GroupByQueryRunnerTestHelper.runQuery(GroupByQueryMergeBufferTest.factory, runner, query);
        Assert.assertEquals(1, GroupByQueryMergeBufferTest.mergeBufferPool.getMinRemainBufferNum());
        Assert.assertEquals(3, GroupByQueryMergeBufferTest.mergeBufferPool.getPoolSize());
    }

    @Test
    public void testDoubleNestedGroupBy() {
        final GroupByQuery query = GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(GroupByQuery.builder().setDataSource(QueryRunnerTestHelper.dataSource).setInterval(QueryRunnerTestHelper.firstToThird).setGranularity(ALL).setDimensions(new DefaultDimensionSpec("quality", "alias"), new DefaultDimensionSpec("market", null)).setAggregatorSpecs(Collections.singletonList(QueryRunnerTestHelper.rowsCount)).build()).setInterval(QueryRunnerTestHelper.firstToThird).setGranularity(ALL).setDimensions(new DefaultDimensionSpec("quality", "alias")).setAggregatorSpecs(Collections.singletonList(QueryRunnerTestHelper.rowsCount)).build())).setGranularity(ALL).setInterval(QueryRunnerTestHelper.firstToThird).setAggregatorSpecs(new LongSumAggregatorFactory("rows", "rows")).setContext(ImmutableMap.of(TIMEOUT_KEY, GroupByQueryMergeBufferTest.TIMEOUT)).build();
        GroupByQueryRunnerTestHelper.runQuery(GroupByQueryMergeBufferTest.factory, runner, query);
        // This should be 0 because the broker needs 2 buffers and the queryable node needs one.
        Assert.assertEquals(0, GroupByQueryMergeBufferTest.mergeBufferPool.getMinRemainBufferNum());
        Assert.assertEquals(3, GroupByQueryMergeBufferTest.mergeBufferPool.getPoolSize());
    }

    @Test
    public void testTripleNestedGroupBy() {
        final GroupByQuery query = GroupByQuery.builder().setDataSource(new org.apache.druid.query.QueryDataSource(GroupByQuery.builder().setDataSource(GroupByQuery.builder().setDataSource(GroupByQuery.builder().setDataSource(QueryRunnerTestHelper.dataSource).setInterval(QueryRunnerTestHelper.firstToThird).setGranularity(ALL).setDimensions(Lists.newArrayList(new DefaultDimensionSpec("quality", "alias"), new DefaultDimensionSpec("market", null), new DefaultDimensionSpec("placement", null))).setAggregatorSpecs(Collections.singletonList(QueryRunnerTestHelper.rowsCount)).build()).setInterval(QueryRunnerTestHelper.firstToThird).setGranularity(ALL).setDimensions(new DefaultDimensionSpec("quality", "alias"), new DefaultDimensionSpec("market", null)).setAggregatorSpecs(Collections.singletonList(QueryRunnerTestHelper.rowsCount)).build()).setInterval(QueryRunnerTestHelper.firstToThird).setGranularity(ALL).setDimensions(new DefaultDimensionSpec("quality", "alias")).setAggregatorSpecs(Collections.singletonList(QueryRunnerTestHelper.rowsCount)).build())).setGranularity(ALL).setInterval(QueryRunnerTestHelper.firstToThird).setAggregatorSpecs(new LongSumAggregatorFactory("rows", "rows")).setContext(ImmutableMap.of(TIMEOUT_KEY, GroupByQueryMergeBufferTest.TIMEOUT)).build();
        GroupByQueryRunnerTestHelper.runQuery(GroupByQueryMergeBufferTest.factory, runner, query);
        // This should be 0 because the broker needs 2 buffers and the queryable node needs one.
        Assert.assertEquals(0, GroupByQueryMergeBufferTest.mergeBufferPool.getMinRemainBufferNum());
        Assert.assertEquals(3, GroupByQueryMergeBufferTest.mergeBufferPool.getPoolSize());
    }
}

