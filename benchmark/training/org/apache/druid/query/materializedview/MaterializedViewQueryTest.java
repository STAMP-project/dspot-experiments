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
package org.apache.druid.query.materializedview;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collections;
import org.apache.druid.query.Query;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.aggregation.DoubleMaxAggregatorFactory;
import org.apache.druid.query.aggregation.DoubleMinAggregatorFactory;
import org.apache.druid.query.topn.TopNQuery;
import org.apache.druid.query.topn.TopNQueryBuilder;
import org.apache.druid.segment.TestHelper;
import org.junit.Assert;
import org.junit.Test;


public class MaterializedViewQueryTest {
    private static final ObjectMapper jsonMapper = TestHelper.makeJsonMapper();

    private DataSourceOptimizer optimizer;

    @Test
    public void testQuerySerialization() throws IOException {
        TopNQuery topNQuery = new TopNQueryBuilder().dataSource(QueryRunnerTestHelper.dataSource).granularity(QueryRunnerTestHelper.allGran).dimension(QueryRunnerTestHelper.marketDimension).metric(QueryRunnerTestHelper.indexMetric).threshold(4).intervals(QueryRunnerTestHelper.fullOnIntervalSpec).aggregators(Lists.newArrayList(Iterables.concat(QueryRunnerTestHelper.commonDoubleAggregators, Lists.newArrayList(new DoubleMaxAggregatorFactory("maxIndex", "index"), new DoubleMinAggregatorFactory("minIndex", "index"))))).postAggregators(Collections.singletonList(QueryRunnerTestHelper.addRowsIndexConstant)).build();
        MaterializedViewQuery query = new MaterializedViewQuery(topNQuery, optimizer);
        String json = MaterializedViewQueryTest.jsonMapper.writeValueAsString(query);
        Query serdeQuery = MaterializedViewQueryTest.jsonMapper.readValue(json, Query.class);
        Assert.assertEquals(query, serdeQuery);
        Assert.assertEquals(new org.apache.druid.query.TableDataSource(QueryRunnerTestHelper.dataSource), query.getDataSource());
        Assert.assertEquals(QueryRunnerTestHelper.allGran, query.getGranularity());
        Assert.assertEquals(QueryRunnerTestHelper.fullOnIntervalSpec.getIntervals(), query.getIntervals());
    }
}

