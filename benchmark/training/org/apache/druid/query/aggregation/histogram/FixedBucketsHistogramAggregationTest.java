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
package org.apache.druid.query.aggregation.histogram;


import com.google.common.collect.Lists;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.data.input.MapBasedRow;
import org.apache.druid.query.aggregation.AggregationTestHelper;
import org.apache.druid.query.groupby.GroupByQueryConfig;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class FixedBucketsHistogramAggregationTest {
    private AggregationTestHelper helper;

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    public FixedBucketsHistogramAggregationTest(final GroupByQueryConfig config) {
        ApproximateHistogramDruidModule.registerSerde();
        helper = AggregationTestHelper.createGroupByQueryAggregationTestHelper(Lists.newArrayList(new ApproximateHistogramDruidModule().getJacksonModules()), config, tempFolder);
    }

    @Test
    public void testIngestWithNullsIgnoredAndQuery() throws Exception {
        MapBasedRow row = ingestAndQuery();
        if (!(NullHandling.replaceWithDefault())) {
            Assert.assertEquals(92.78276, row.getMetric("index_min").floatValue(), 1.0E-4);
            Assert.assertEquals(135.109191, row.getMetric("index_max").floatValue(), 1.0E-4);
            Assert.assertEquals(135.9499969482422, row.getMetric("index_quantile").floatValue(), 1.0E-4);
        } else {
            Assert.assertEquals(0.0, row.getMetric("index_min"));
            Assert.assertEquals(135.109191, row.getMetric("index_max").floatValue(), 1.0E-4);
            Assert.assertEquals(135.8699951171875, row.getMetric("index_quantile").floatValue(), 1.0E-4);
        }
    }
}

