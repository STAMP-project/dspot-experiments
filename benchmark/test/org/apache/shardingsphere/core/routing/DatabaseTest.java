/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.routing;


import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.InlineShardingStrategyConfiguration;
import org.apache.shardingsphere.api.hint.HintManager;
import org.apache.shardingsphere.core.parsing.parser.sql.dql.select.SelectStatement;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class DatabaseTest {
    private ShardingRule shardingRule;

    @Test
    public void assertHintSQL() {
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.setDatabaseShardingValue(1);
            assertTarget("select * from tesT", "ds_1");
            assertTarget("insert into test values (1,2)", "ds_1");
            assertTarget("update test set a = 1", "ds_1");
            assertTarget("delete from test where id = 2", "ds_1");
            hintManager.setDatabaseShardingValue(2);
            assertTarget("select * from tesT", "ds_0");
            hintManager.close();
        }
    }

    @Test
    public void assertDatabaseAllRoutingSQL() {
        String originSQL = "select * from tesT";
        SQLRouteResult actual = new StatementRoutingEngine(shardingRule, org.mockito.Mockito.mock(org.apache.shardingsphere.core.metadata.ShardingMetaData.class), org.apache.shardingsphere.core.constant.DatabaseType.MySQL, new org.apache.shardingsphere.core.parsing.cache.ParsingResultCache(), false).route(originSQL);
        Assert.assertThat(actual.getRouteUnits().size(), CoreMatchers.is(1));
        Set<String> actualDataSources = new java.util.HashSet(Collections2.transform(actual.getRouteUnits(), new Function<RouteUnit, String>() {
            @Override
            public String apply(final RouteUnit input) {
                return input.getDataSourceName();
            }
        }));
        Assert.assertThat(actualDataSources.size(), CoreMatchers.is(1));
        Collection<String> actualSQLs = Collections2.transform(actual.getRouteUnits(), new Function<RouteUnit, String>() {
            @Override
            public String apply(final RouteUnit input) {
                return input.getSqlUnit().getSql();
            }
        });
        Assert.assertThat(originSQL, CoreMatchers.is(actualSQLs.iterator().next()));
    }

    @Test
    public void assertDatabaseSelectSQLPagination() {
        String originSQL = "select user_id from tbl_pagination limit 0,5";
        SQLRouteResult actual = new StatementRoutingEngine(shardingRule, org.mockito.Mockito.mock(org.apache.shardingsphere.core.metadata.ShardingMetaData.class), org.apache.shardingsphere.core.constant.DatabaseType.MySQL, new org.apache.shardingsphere.core.parsing.cache.ParsingResultCache(), false).route(originSQL);
        SelectStatement stmt = ((SelectStatement) (actual.getSqlStatement()));
        Assert.assertThat(stmt.getLimit().getOffsetValue(), CoreMatchers.is(0));
        Assert.assertThat(stmt.getLimit().getRowCountValue(), CoreMatchers.is(5));
        originSQL = "select user_id from tbl_pagination limit 5,5";
        actual = new StatementRoutingEngine(shardingRule, org.mockito.Mockito.mock(org.apache.shardingsphere.core.metadata.ShardingMetaData.class), org.apache.shardingsphere.core.constant.DatabaseType.MySQL, new org.apache.shardingsphere.core.parsing.cache.ParsingResultCache(), false).route(originSQL);
        stmt = ((SelectStatement) (actual.getSqlStatement()));
        Assert.assertThat(stmt.getLimit().getOffsetValue(), CoreMatchers.is(5));
        Assert.assertThat(stmt.getLimit().getRowCountValue(), CoreMatchers.is(5));
    }

    @Test
    public void assertDatabasePrepareSelectSQLPagination() {
        String shardingPrefix = "user_db";
        String shardingTable = "user";
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        dataSourceMap.put((shardingPrefix + "1"), null);
        dataSourceMap.put((shardingPrefix + "2"), null);
        TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration(shardingTable, ((shardingPrefix + "${1..2}.") + shardingTable));
        tableRuleConfig.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration("city_id", (shardingPrefix + "${city_id % 2 + 1}")));
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        shardingRuleConfig.getTableRuleConfigs().add(tableRuleConfig);
        ShardingRule rule = new ShardingRule(shardingRuleConfig, dataSourceMap.keySet());
        String originSQL = "select city_id from user where city_id in (?,?) limit 5,10";
        SQLRouteResult actual = route(Lists.<Object>newArrayList(13, 173));
        SelectStatement selectStatement = ((SelectStatement) (actual.getSqlStatement()));
        Assert.assertThat(selectStatement.getLimit().getOffsetValue(), CoreMatchers.is(5));
        Assert.assertThat(selectStatement.getLimit().getRowCountValue(), CoreMatchers.is(10));
        Assert.assertThat(actual.getRouteUnits().size(), CoreMatchers.is(1));
        originSQL = "select city_id from user where city_id in (?,?) limit 5,10";
        actual = new PreparedStatementRoutingEngine(originSQL, rule, org.mockito.Mockito.mock(org.apache.shardingsphere.core.metadata.ShardingMetaData.class), org.apache.shardingsphere.core.constant.DatabaseType.MySQL, new org.apache.shardingsphere.core.parsing.cache.ParsingResultCache(), false).route(Lists.<Object>newArrayList(89, 84));
        selectStatement = ((SelectStatement) (actual.getSqlStatement()));
        Assert.assertThat(selectStatement.getLimit().getOffsetValue(), CoreMatchers.is(5));
        Assert.assertThat(selectStatement.getLimit().getRowCountValue(), CoreMatchers.is(10));
        Assert.assertThat(actual.getRouteUnits().size(), CoreMatchers.is(2));
    }
}

