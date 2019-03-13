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
package org.apache.druid.query.topn;


import BaseTopNAlgorithm.AggregatorArrayProvider;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.Collections;
import org.apache.druid.java.util.common.Pair;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.aggregation.DoubleMaxAggregatorFactory;
import org.apache.druid.query.aggregation.DoubleMinAggregatorFactory;
import org.apache.druid.segment.DimensionSelector;
import org.apache.druid.segment.StorageAdapter;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.druid.query.QueryRunnerTestHelper.allGran;
import static org.apache.druid.query.QueryRunnerTestHelper.dataSource;
import static org.apache.druid.query.QueryRunnerTestHelper.indexMetric;
import static org.apache.druid.query.QueryRunnerTestHelper.marketDimension;


public class TopNMetricSpecOptimizationsTest {
    @Test
    public void testShouldOptimizeLexicographic() {
        // query interval is greater than segment interval, no filters, can ignoreAfterThreshold
        int cardinality = 1234;
        int threshold = 4;
        TopNQuery query = new TopNQueryBuilder().dataSource(dataSource).granularity(allGran).dimension(marketDimension).metric(indexMetric).threshold(threshold).intervals("2018-05-30T00:00:00Z/2018-05-31T00:00:00Z").aggregators(Lists.newArrayList(Iterables.concat(QueryRunnerTestHelper.commonDoubleAggregators, Lists.newArrayList(new DoubleMaxAggregatorFactory("maxIndex", "index"), new DoubleMinAggregatorFactory("minIndex", "index"))))).postAggregators(Collections.singletonList(QueryRunnerTestHelper.addRowsIndexConstant)).build();
        StorageAdapter adapter = makeFakeStorageAdapter("2018-05-30T00:00:00Z", "2018-05-30T01:00:00Z", cardinality);
        DimensionSelector dimSelector = makeFakeDimSelector(cardinality);
        BaseTopNAlgorithm.AggregatorArrayProvider arrayProviderToTest = new BaseTopNAlgorithm.AggregatorArrayProvider(dimSelector, query, cardinality, adapter);
        arrayProviderToTest.ignoreAfterThreshold();
        Pair<Integer, Integer> thePair = arrayProviderToTest.computeStartEnd(cardinality);
        Assert.assertEquals(new Integer(0), thePair.lhs);
        Assert.assertEquals(new Integer(threshold), thePair.rhs);
    }

    @Test
    public void testAlsoShouldOptimizeLexicographic() {
        // query interval is same as segment interval, no filters, can ignoreAfterThreshold
        int cardinality = 1234;
        int threshold = 4;
        TopNQuery query = new TopNQueryBuilder().dataSource(dataSource).granularity(allGran).dimension(marketDimension).metric(indexMetric).threshold(threshold).intervals("2018-05-30T00:00:00Z/2018-05-30T01:00:00Z").aggregators(Lists.newArrayList(Iterables.concat(QueryRunnerTestHelper.commonDoubleAggregators, Lists.newArrayList(new DoubleMaxAggregatorFactory("maxIndex", "index"), new DoubleMinAggregatorFactory("minIndex", "index"))))).postAggregators(Collections.singletonList(QueryRunnerTestHelper.addRowsIndexConstant)).build();
        StorageAdapter adapter = makeFakeStorageAdapter("2018-05-30T00:00:00Z", "2018-05-30T01:00:00Z", cardinality);
        DimensionSelector dimSelector = makeFakeDimSelector(cardinality);
        BaseTopNAlgorithm.AggregatorArrayProvider arrayProviderToTest = new BaseTopNAlgorithm.AggregatorArrayProvider(dimSelector, query, cardinality, adapter);
        arrayProviderToTest.ignoreAfterThreshold();
        Pair<Integer, Integer> thePair = arrayProviderToTest.computeStartEnd(cardinality);
        Assert.assertEquals(new Integer(0), thePair.lhs);
        Assert.assertEquals(new Integer(threshold), thePair.rhs);
    }

    @Test
    public void testShouldNotOptimizeLexicographic() {
        // query interval is smaller than segment interval, no filters, can ignoreAfterThreshold
        int cardinality = 1234;
        int threshold = 4;
        TopNQuery query = new TopNQueryBuilder().dataSource(dataSource).granularity(allGran).dimension(marketDimension).metric(indexMetric).threshold(threshold).intervals("2018-05-30T00:00:00Z/2018-05-30T01:00:00Z").aggregators(Lists.newArrayList(Iterables.concat(QueryRunnerTestHelper.commonDoubleAggregators, Lists.newArrayList(new DoubleMaxAggregatorFactory("maxIndex", "index"), new DoubleMinAggregatorFactory("minIndex", "index"))))).postAggregators(Collections.singletonList(QueryRunnerTestHelper.addRowsIndexConstant)).build();
        StorageAdapter adapter = makeFakeStorageAdapter("2018-05-30T00:00:00Z", "2018-05-31T00:00:00Z", cardinality);
        DimensionSelector dimSelector = makeFakeDimSelector(cardinality);
        BaseTopNAlgorithm.AggregatorArrayProvider arrayProviderToTest = new BaseTopNAlgorithm.AggregatorArrayProvider(dimSelector, query, cardinality, adapter);
        arrayProviderToTest.ignoreAfterThreshold();
        Pair<Integer, Integer> thePair = arrayProviderToTest.computeStartEnd(cardinality);
        Assert.assertEquals(new Integer(0), thePair.lhs);
        Assert.assertEquals(new Integer(cardinality), thePair.rhs);
    }

    @Test
    public void testAlsoShouldNotOptimizeLexicographic() {
        // query interval is larger than segment interval, but has filters, can ignoreAfterThreshold
        int cardinality = 1234;
        int threshold = 4;
        TopNQuery query = new TopNQueryBuilder().dataSource(dataSource).granularity(allGran).dimension(marketDimension).filters(QueryRunnerTestHelper.qualityDimension, "entertainment").metric(indexMetric).threshold(threshold).intervals("2018-05-30T00:00:00Z/2018-05-31T00:00:00Z").aggregators(Lists.newArrayList(Iterables.concat(QueryRunnerTestHelper.commonDoubleAggregators, Lists.newArrayList(new DoubleMaxAggregatorFactory("maxIndex", "index"), new DoubleMinAggregatorFactory("minIndex", "index"))))).postAggregators(Collections.singletonList(QueryRunnerTestHelper.addRowsIndexConstant)).build();
        StorageAdapter adapter = makeFakeStorageAdapter("2018-05-30T00:00:00Z", "2018-05-30T01:00:00Z", cardinality);
        DimensionSelector dimSelector = makeFakeDimSelector(cardinality);
        BaseTopNAlgorithm.AggregatorArrayProvider arrayProviderToTest = new BaseTopNAlgorithm.AggregatorArrayProvider(dimSelector, query, cardinality, adapter);
        arrayProviderToTest.ignoreAfterThreshold();
        Pair<Integer, Integer> thePair = arrayProviderToTest.computeStartEnd(cardinality);
        Assert.assertEquals(new Integer(0), thePair.lhs);
        Assert.assertEquals(new Integer(cardinality), thePair.rhs);
    }

    @Test
    public void testAgainShouldNotOptimizeLexicographic() {
        // query interval is larger than segment interval, no filters, can NOT ignoreAfterThreshold
        int cardinality = 1234;
        int threshold = 4;
        TopNQuery query = new TopNQueryBuilder().dataSource(dataSource).granularity(allGran).dimension(marketDimension).metric(indexMetric).threshold(threshold).intervals("2018-05-30T00:00:00Z/2018-05-31T00:00:00Z").aggregators(Lists.newArrayList(Iterables.concat(QueryRunnerTestHelper.commonDoubleAggregators, Lists.newArrayList(new DoubleMaxAggregatorFactory("maxIndex", "index"), new DoubleMinAggregatorFactory("minIndex", "index"))))).postAggregators(Collections.singletonList(QueryRunnerTestHelper.addRowsIndexConstant)).build();
        StorageAdapter adapter = makeFakeStorageAdapter("2018-05-30T00:00:00Z", "2018-05-30T01:00:00Z", cardinality);
        DimensionSelector dimSelector = makeFakeDimSelector(cardinality);
        BaseTopNAlgorithm.AggregatorArrayProvider arrayProviderToTest = new BaseTopNAlgorithm.AggregatorArrayProvider(dimSelector, query, cardinality, adapter);
        Pair<Integer, Integer> thePair = arrayProviderToTest.computeStartEnd(cardinality);
        Assert.assertEquals(new Integer(0), thePair.lhs);
        Assert.assertEquals(new Integer(cardinality), thePair.rhs);
    }
}

