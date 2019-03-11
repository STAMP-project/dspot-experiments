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
public class ApproximateHistogramAggregationTest {
    private AggregationTestHelper helper;

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    public ApproximateHistogramAggregationTest(final GroupByQueryConfig config) {
        ApproximateHistogramDruidModule.registerSerde();
        helper = AggregationTestHelper.createGroupByQueryAggregationTestHelper(Lists.newArrayList(new ApproximateHistogramDruidModule().getJacksonModules()), config, tempFolder);
    }

    @Test
    public void testIngestWithNullsIgnoredAndQuery() throws Exception {
        MapBasedRow row = ingestAndQuery(true);
        Assert.assertEquals(92.78276, row.getMetric("index_min").floatValue(), 1.0E-4);
        Assert.assertEquals(135.109191, row.getMetric("index_max").floatValue(), 1.0E-4);
        Assert.assertEquals(133.6934, row.getMetric("index_quantile").floatValue(), 1.0E-4);
    }

    @Test
    public void testIngestWithNullsToZeroAndQuery() throws Exception {
        // Nulls are ignored and not replaced with default for SQL compatible null handling.
        // This is already tested in testIngestWithNullsIgnoredAndQuery()
        if (NullHandling.replaceWithDefault()) {
            MapBasedRow row = ingestAndQuery(false);
            Assert.assertEquals(0.0F, row.getMetric("index_min"));
            Assert.assertEquals(135.109191, row.getMetric("index_max").floatValue(), 1.0E-4);
            Assert.assertEquals(131.428176, row.getMetric("index_quantile").floatValue(), 1.0E-4);
        }
    }
}

