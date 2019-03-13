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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.druid.data.input.Row;
import org.apache.druid.java.util.common.guava.Sequence;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.dimension.DefaultDimensionSpec;
import org.apache.druid.query.spec.LegacySegmentSpec;
import org.apache.druid.segment.CloserRule;
import org.apache.druid.segment.TestHelper;
import org.junit.Rule;
import org.junit.Test;

import static com.google.common.base.Throwables.propagate;
import static java.util.Arrays.asList;


/**
 *
 */
public class GroupByQueryRunnerFactoryTest {
    @Rule
    public CloserRule closerRule = new CloserRule(true);

    private GroupByQueryRunnerFactory factory;

    private Closer resourceCloser;

    @Test
    public void testMergeRunnersEnsureGroupMerging() {
        GroupByQuery query = GroupByQuery.builder().setDataSource("xx").setQuerySegmentSpec(new LegacySegmentSpec("1970/3000")).setGranularity(ALL).setDimensions(new DefaultDimensionSpec("tags", "tags")).setAggregatorSpecs(new CountAggregatorFactory("count")).build();
        QueryRunner mergedRunner = factory.getToolchest().mergeResults(new QueryRunner() {
            @Override
            public Sequence run(QueryPlus queryPlus, Map responseContext) {
                return factory.getToolchest().mergeResults(new QueryRunner() {
                    @Override
                    public Sequence run(QueryPlus queryPlus, Map responseContext) {
                        final org.apache.druid.query.Query query = queryPlus.getQuery();
                        try {
                            return new org.apache.druid.java.util.common.guava.MergeSequence(query.getResultOrdering(), org.apache.druid.java.util.common.guava.Sequences.simple(asList(factory.createRunner(createSegment()).run(queryPlus, responseContext), factory.createRunner(createSegment()).run(queryPlus, responseContext))));
                        } catch (Exception e) {
                            propagate(e);
                            return null;
                        }
                    }
                }).run(queryPlus, responseContext);
            }
        });
        Sequence<Row> result = mergedRunner.run(QueryPlus.wrap(query), new HashMap());
        List<Row> expectedResults = asList(GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "tags", "t1", "count", 2L), GroupByQueryRunnerTestHelper.createExpectedRow("1970-01-01T00:00:00.000Z", "tags", "t2", "count", 4L));
        TestHelper.assertExpectedObjects(expectedResults, result.toList(), "");
    }
}

