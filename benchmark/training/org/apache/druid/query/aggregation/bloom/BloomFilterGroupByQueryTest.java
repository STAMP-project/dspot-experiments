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
package org.apache.druid.query.aggregation.bloom;


import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Key;
import org.apache.druid.data.input.MapBasedRow;
import org.apache.druid.guice.BloomFilterExtensionModule;
import org.apache.druid.query.aggregation.AggregationTestHelper;
import org.apache.druid.query.filter.BloomKFilter;
import org.apache.druid.query.groupby.GroupByQueryConfig;
import org.apache.druid.segment.TestHelper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class BloomFilterGroupByQueryTest {
    private static final BloomFilterExtensionModule module = new BloomFilterExtensionModule();

    static {
        // throwaway, just using to properly initialize jackson modules
        Guice.createInjector(( binder) -> binder.bind(Key.get(.class, .class)).toInstance(TestHelper.makeJsonMapper()), BloomFilterGroupByQueryTest.module);
    }

    private AggregationTestHelper helper;

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    public BloomFilterGroupByQueryTest(final GroupByQueryConfig config) {
        helper = AggregationTestHelper.createGroupByQueryAggregationTestHelper(Lists.newArrayList(BloomFilterGroupByQueryTest.module.getJacksonModules()), config, tempFolder);
    }

    @Test
    public void testQuery() throws Exception {
        String query = "{" + ((((((((("\"queryType\": \"groupBy\"," + "\"dataSource\": \"test_datasource\",") + "\"granularity\": \"ALL\",") + "\"dimensions\": [],") + "\"filter\":{ \"type\":\"selector\", \"dimension\":\"market\", \"value\":\"upfront\"},") + "\"aggregations\": [") + "  { \"type\": \"bloom\", \"name\": \"blooming_quality\", \"field\": \"quality\" }") + "],") + "\"intervals\": [ \"1970/2050\" ]") + "}");
        MapBasedRow row = ingestAndQuery(query);
        Assert.assertTrue(testString("mezzanine"));
        Assert.assertTrue(testString("premium"));
        Assert.assertFalse(testString("entertainment"));
    }

    @Test
    public void testQueryFakeDimension() throws Exception {
        String query = "{" + ((((((((("\"queryType\": \"groupBy\"," + "\"dataSource\": \"test_datasource\",") + "\"granularity\": \"ALL\",") + "\"dimensions\": [],") + "\"filter\":{ \"type\":\"selector\", \"dimension\":\"market\", \"value\":\"upfront\"},") + "\"aggregations\": [") + "  { \"type\": \"bloom\", \"name\": \"blooming_quality\", \"field\": \"nope\" }") + "],") + "\"intervals\": [ \"1970/2050\" ]") + "}");
        MapBasedRow row = ingestAndQuery(query);
        // a nil column results in a totally empty bloom filter
        BloomKFilter filter = new BloomKFilter(1500);
        Object val = row.getRaw("blooming_quality");
        String serialized = BloomFilterAggregatorTest.filterToString(((BloomKFilter) (val)));
        String empty = BloomFilterAggregatorTest.filterToString(filter);
        Assert.assertEquals(empty, serialized);
    }
}

