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
package org.apache.druid.query.filter.sql;


import CalciteTests.DATASOURCE1;
import Granularities.ALL;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import java.util.Collections;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.guice.BloomFilterExtensionModule;
import org.apache.druid.guice.BloomFilterSerializersModule;
import org.apache.druid.guice.annotations.Json;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.query.Druids;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.expression.LookupEnabledTestExprMacroTable;
import org.apache.druid.query.filter.BloomKFilter;
import org.apache.druid.query.filter.BloomKFilterHolder;
import org.apache.druid.query.filter.OrDimFilter;
import org.apache.druid.segment.TestHelper;
import org.apache.druid.sql.calcite.BaseCalciteQueryTest;
import org.apache.druid.sql.calcite.filtration.Filtration;
import org.junit.Test;


public class BloomDimFilterSqlTest extends BaseCalciteQueryTest {
    private static final Injector injector = Guice.createInjector(( binder) -> {
        binder.bind(Key.get(.class, .class)).toInstance(TestHelper.makeJsonMapper());
        binder.bind(.class).toInstance(LookupEnabledTestExprMacroTable.createTestLookupReferencesManager(ImmutableMap.of("a", "xa", "abc", "xabc")));
    }, new BloomFilterExtensionModule());

    private static ObjectMapper jsonMapper = BloomDimFilterSqlTest.injector.getInstance(Key.get(ObjectMapper.class, Json.class)).registerModules(Collections.singletonList(new BloomFilterSerializersModule()));

    @Test
    public void testBloomFilter() throws Exception {
        BloomKFilter filter = new BloomKFilter(1500);
        filter.addString("def");
        byte[] bytes = BloomFilterSerializersModule.bloomKFilterToBytes(filter);
        String base64 = StringUtils.encodeBase64String(bytes);
        testQuery(StringUtils.format("SELECT COUNT(*) FROM druid.foo WHERE bloom_filter_test(dim1, '%s')", base64), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(DATASOURCE1).intervals(querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(new org.apache.druid.query.filter.BloomDimFilter("dim1", BloomKFilterHolder.fromBloomKFilter(filter), null)).aggregators(aggregators(new CountAggregatorFactory("a0"))).context(TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L }));
    }

    @Test
    public void testBloomFilterVirtualColumn() throws Exception {
        BloomKFilter filter = new BloomKFilter(1500);
        filter.addString("a-foo");
        filter.addString("-foo");
        if (!(NullHandling.replaceWithDefault())) {
            filter.addBytes(null, 0, 0);
        }
        byte[] bytes = BloomFilterSerializersModule.bloomKFilterToBytes(filter);
        String base64 = StringUtils.encodeBase64String(bytes);
        testQuery(StringUtils.format("SELECT COUNT(*) FROM druid.foo WHERE bloom_filter_test(concat(dim2, '-foo'), '%s')", base64), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(DATASOURCE1).intervals(querySegmentSpec(Filtration.eternity())).granularity(ALL).virtualColumns().filters(new org.apache.druid.query.filter.ExpressionDimFilter(StringUtils.format("bloom_filter_test(concat(\"dim2\",\'-foo\'),\'%s\')", base64), BloomDimFilterSqlTest.createExprMacroTable())).aggregators(aggregators(new CountAggregatorFactory("a0"))).context(TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 5L }));
    }

    @Test
    public void testBloomFilterVirtualColumnNumber() throws Exception {
        BloomKFilter filter = new BloomKFilter(1500);
        filter.addDouble(20.2);
        byte[] bytes = BloomFilterSerializersModule.bloomKFilterToBytes(filter);
        String base64 = StringUtils.encodeBase64String(bytes);
        testQuery(StringUtils.format("SELECT COUNT(*) FROM druid.foo WHERE bloom_filter_test(2 * CAST(dim1 AS float), '%s')", base64), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(DATASOURCE1).intervals(querySegmentSpec(Filtration.eternity())).granularity(ALL).virtualColumns().filters(new org.apache.druid.query.filter.ExpressionDimFilter(StringUtils.format("bloom_filter_test((2 * CAST(\"dim1\", \'DOUBLE\')),\'%s\')", base64), BloomDimFilterSqlTest.createExprMacroTable())).aggregators(aggregators(new CountAggregatorFactory("a0"))).context(TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 1L }));
    }

    @Test
    public void testBloomFilters() throws Exception {
        BloomKFilter filter = new BloomKFilter(1500);
        filter.addString("def");
        BloomKFilter filter2 = new BloomKFilter(1500);
        filter.addString("abc");
        byte[] bytes = BloomFilterSerializersModule.bloomKFilterToBytes(filter);
        byte[] bytes2 = BloomFilterSerializersModule.bloomKFilterToBytes(filter2);
        String base64 = StringUtils.encodeBase64String(bytes);
        String base642 = StringUtils.encodeBase64String(bytes2);
        testQuery(StringUtils.format("SELECT COUNT(*) FROM druid.foo WHERE bloom_filter_test(dim1, '%s') OR bloom_filter_test(dim2, '%s')", base64, base642), ImmutableList.of(Druids.newTimeseriesQueryBuilder().dataSource(DATASOURCE1).intervals(querySegmentSpec(Filtration.eternity())).granularity(ALL).filters(new OrDimFilter(new org.apache.druid.query.filter.BloomDimFilter("dim1", BloomKFilterHolder.fromBloomKFilter(filter), null), new org.apache.druid.query.filter.BloomDimFilter("dim2", BloomKFilterHolder.fromBloomKFilter(filter2), null))).aggregators(aggregators(new CountAggregatorFactory("a0"))).context(TIMESERIES_CONTEXT_DEFAULT).build()), ImmutableList.of(new Object[]{ 2L }));
    }
}

