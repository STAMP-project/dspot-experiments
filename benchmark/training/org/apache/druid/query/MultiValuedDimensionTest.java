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
package org.apache.druid.query;


import Granularities.ALL;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.druid.collections.CloseableStupidPool;
import org.apache.druid.data.input.Row;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.guava.Sequence;
import org.apache.druid.query.aggregation.AggregationTestHelper;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.dimension.DefaultDimensionSpec;
import org.apache.druid.query.filter.SelectorDimFilter;
import org.apache.druid.query.groupby.GroupByQuery;
import org.apache.druid.query.groupby.GroupByQueryConfig;
import org.apache.druid.query.groupby.GroupByQueryRunnerTestHelper;
import org.apache.druid.query.spec.LegacySegmentSpec;
import org.apache.druid.query.topn.TopNQuery;
import org.apache.druid.query.topn.TopNQueryBuilder;
import org.apache.druid.query.topn.TopNQueryConfig;
import org.apache.druid.query.topn.TopNResultValue;
import org.apache.druid.segment.QueryableIndex;
import org.apache.druid.segment.TestHelper;
import org.apache.druid.segment.incremental.IncrementalIndex;
import org.apache.druid.segment.writeout.SegmentWriteOutMediumFactory;
import org.apache.druid.timeline.SegmentId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class MultiValuedDimensionTest {
    private final AggregationTestHelper helper;

    private final SegmentWriteOutMediumFactory segmentWriteOutMediumFactory;

    private IncrementalIndex incrementalIndex;

    private QueryableIndex queryableIndex;

    private File persistedSegmentDir;

    public MultiValuedDimensionTest(final GroupByQueryConfig config, SegmentWriteOutMediumFactory segmentWriteOutMediumFactory) {
        helper = AggregationTestHelper.createGroupByQueryAggregationTestHelper(ImmutableList.of(), config, null);
        this.segmentWriteOutMediumFactory = segmentWriteOutMediumFactory;
    }

    @Test
    public void testGroupByNoFilter() {
        GroupByQuery query = GroupByQuery.builder().setDataSource("xx").setQuerySegmentSpec(new LegacySegmentSpec("1970/3000")).setGranularity(ALL).setDimensions(new DefaultDimensionSpec("tags", "tags")).setAggregatorSpecs(new CountAggregatorFactory("count")).build();
        Sequence<Row> result = helper.runQueryOnSegmentsObjs(ImmutableList.of(new org.apache.druid.segment.QueryableIndexSegment(queryableIndex, SegmentId.dummy("sid1")), new org.apache.druid.segment.IncrementalIndexSegment(incrementalIndex, SegmentId.dummy("sid2"))), query);
        List<Row> expectedResults = Arrays.asList(GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "tags", null, "count", 2L), GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "tags", "t1", "count", 2L), GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "tags", "t2", "count", 2L), GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "tags", "t3", "count", 4L), GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "tags", "t4", "count", 2L), GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "tags", "t5", "count", 4L), GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "tags", "t6", "count", 2L), GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "tags", "t7", "count", 2L));
        TestHelper.assertExpectedObjects(expectedResults, result.toList(), "noFilter");
    }

    @Test
    public void testGroupByWithDimFilter() {
        GroupByQuery query = GroupByQuery.builder().setDataSource("xx").setQuerySegmentSpec(new LegacySegmentSpec("1970/3000")).setGranularity(ALL).setDimensions(new DefaultDimensionSpec("tags", "tags")).setAggregatorSpecs(new CountAggregatorFactory("count")).setDimFilter(new SelectorDimFilter("tags", "t3", null)).build();
        Sequence<Row> result = helper.runQueryOnSegmentsObjs(ImmutableList.of(new org.apache.druid.segment.QueryableIndexSegment(queryableIndex, SegmentId.dummy("sid1")), new org.apache.druid.segment.IncrementalIndexSegment(incrementalIndex, SegmentId.dummy("sid2"))), query);
        List<Row> expectedResults = Arrays.asList(GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "tags", "t1", "count", 2L), GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "tags", "t2", "count", 2L), GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "tags", "t3", "count", 4L), GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "tags", "t4", "count", 2L), GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "tags", "t5", "count", 2L));
        TestHelper.assertExpectedObjects(expectedResults, result.toList(), "dimFilter");
    }

    @Test
    public void testGroupByWithDimFilterAndWithFilteredDimSpec() {
        GroupByQuery query = GroupByQuery.builder().setDataSource("xx").setQuerySegmentSpec(new LegacySegmentSpec("1970/3000")).setGranularity(ALL).setDimensions(new org.apache.druid.query.dimension.RegexFilteredDimensionSpec(new DefaultDimensionSpec("tags", "tags"), "t3")).setAggregatorSpecs(new CountAggregatorFactory("count")).setDimFilter(new SelectorDimFilter("tags", "t3", null)).build();
        Sequence<Row> result = helper.runQueryOnSegmentsObjs(ImmutableList.of(new org.apache.druid.segment.QueryableIndexSegment(queryableIndex, SegmentId.dummy("sid1")), new org.apache.druid.segment.IncrementalIndexSegment(incrementalIndex, SegmentId.dummy("sid2"))), query);
        List<Row> expectedResults = Collections.singletonList(GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "tags", "t3", "count", 4L));
        TestHelper.assertExpectedObjects(expectedResults, result.toList(), "filteredDim");
    }

    @Test
    public void testTopNWithDimFilterAndWithFilteredDimSpec() {
        TopNQuery query = new TopNQueryBuilder().dataSource("xx").granularity(ALL).dimension(new org.apache.druid.query.dimension.ListFilteredDimensionSpec(new DefaultDimensionSpec("tags", "tags"), ImmutableSet.of("t3"), null)).metric("count").intervals(QueryRunnerTestHelper.fullOnIntervalSpec).aggregators(Collections.singletonList(new CountAggregatorFactory("count"))).threshold(5).filters(new SelectorDimFilter("tags", "t3", null)).build();
        try (CloseableStupidPool<ByteBuffer> pool = TestQueryRunners.createDefaultNonBlockingPool()) {
            QueryRunnerFactory factory = new org.apache.druid.query.topn.TopNQueryRunnerFactory(pool, new org.apache.druid.query.topn.TopNQueryQueryToolChest(new TopNQueryConfig(), QueryRunnerTestHelper.noopIntervalChunkingQueryRunnerDecorator()), QueryRunnerTestHelper.NOOP_QUERYWATCHER);
            QueryRunner<Result<TopNResultValue>> runner = QueryRunnerTestHelper.makeQueryRunner(factory, new org.apache.druid.segment.QueryableIndexSegment(queryableIndex, SegmentId.dummy("sid1")), null);
            Map<String, Object> context = new HashMap<>();
            Sequence<Result<TopNResultValue>> result = runner.run(QueryPlus.wrap(query), context);
            List<Result<TopNResultValue>> expectedResults = Collections.singletonList(new Result<TopNResultValue>(DateTimes.of("2011-01-12T00:00:00.000Z"), new TopNResultValue(Collections.<Map<String, Object>>singletonList(ImmutableMap.of("tags", "t3", "count", 2L)))));
            TestHelper.assertExpectedObjects(expectedResults, result.toList(), "filteredDim");
        }
    }
}

