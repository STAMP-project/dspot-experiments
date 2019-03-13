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
package org.apache.shardingsphere.core.merger.dql;


import com.google.common.base.Optional;
import java.sql.SQLException;
import java.util.List;
import org.apache.shardingsphere.core.constant.AggregationType;
import org.apache.shardingsphere.core.constant.DatabaseType;
import org.apache.shardingsphere.core.constant.OrderDirection;
import org.apache.shardingsphere.core.merger.MergedResult;
import org.apache.shardingsphere.core.merger.QueryResult;
import org.apache.shardingsphere.core.merger.dql.groupby.GroupByMemoryMergedResult;
import org.apache.shardingsphere.core.merger.dql.groupby.GroupByStreamMergedResult;
import org.apache.shardingsphere.core.merger.dql.iterator.IteratorStreamMergedResult;
import org.apache.shardingsphere.core.merger.dql.orderby.OrderByStreamMergedResult;
import org.apache.shardingsphere.core.merger.dql.pagination.LimitDecoratorMergedResult;
import org.apache.shardingsphere.core.merger.dql.pagination.RowNumberDecoratorMergedResult;
import org.apache.shardingsphere.core.merger.dql.pagination.TopAndRowNumberDecoratorMergedResult;
import org.apache.shardingsphere.core.parsing.parser.context.limit.Limit;
import org.apache.shardingsphere.core.parsing.parser.sql.dql.select.SelectStatement;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class DQLMergeEngineTest {
    private DQLMergeEngine mergeEngine;

    private List<QueryResult> singleQueryResult;

    private List<QueryResult> queryResults;

    private SelectStatement selectStatement;

    @Test
    public void assertBuildIteratorStreamMergedResult() throws SQLException {
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        Assert.assertThat(mergeEngine.merge(), CoreMatchers.instanceOf(IteratorStreamMergedResult.class));
    }

    @Test
    public void assertBuildIteratorStreamMergedResultWithLimit() throws SQLException {
        selectStatement.setLimit(new Limit());
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, singleQueryResult);
        Assert.assertThat(mergeEngine.merge(), CoreMatchers.instanceOf(IteratorStreamMergedResult.class));
    }

    @Test
    public void assertBuildIteratorStreamMergedResultWithMySQLLimit() throws SQLException {
        selectStatement.setLimit(new Limit());
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertThat(actual, CoreMatchers.instanceOf(LimitDecoratorMergedResult.class));
        Assert.assertThat(getMergedResult(), CoreMatchers.instanceOf(IteratorStreamMergedResult.class));
    }

    @Test
    public void assertBuildIteratorStreamMergedResultWithOracleLimit() throws SQLException {
        selectStatement.setLimit(new Limit());
        mergeEngine = new DQLMergeEngine(DatabaseType.Oracle, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertThat(actual, CoreMatchers.instanceOf(RowNumberDecoratorMergedResult.class));
        Assert.assertThat(getMergedResult(), CoreMatchers.instanceOf(IteratorStreamMergedResult.class));
    }

    @Test
    public void assertBuildIteratorStreamMergedResultWithSQLServerLimit() throws SQLException {
        selectStatement.setLimit(new Limit());
        mergeEngine = new DQLMergeEngine(DatabaseType.SQLServer, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertThat(actual, CoreMatchers.instanceOf(TopAndRowNumberDecoratorMergedResult.class));
        Assert.assertThat(getMergedResult(), CoreMatchers.instanceOf(IteratorStreamMergedResult.class));
    }

    @Test
    public void assertBuildOrderByStreamMergedResult() throws SQLException {
        selectStatement.getOrderByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC));
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        Assert.assertThat(mergeEngine.merge(), CoreMatchers.instanceOf(OrderByStreamMergedResult.class));
    }

    @Test
    public void assertBuildOrderByStreamMergedResultWithMySQLLimit() throws SQLException {
        selectStatement.setLimit(new Limit());
        selectStatement.getOrderByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC));
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertThat(actual, CoreMatchers.instanceOf(LimitDecoratorMergedResult.class));
        Assert.assertThat(getMergedResult(), CoreMatchers.instanceOf(OrderByStreamMergedResult.class));
    }

    @Test
    public void assertBuildOrderByStreamMergedResultWithOracleLimit() throws SQLException {
        selectStatement.setLimit(new Limit());
        selectStatement.getOrderByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC));
        mergeEngine = new DQLMergeEngine(DatabaseType.Oracle, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertThat(actual, CoreMatchers.instanceOf(RowNumberDecoratorMergedResult.class));
        Assert.assertThat(getMergedResult(), CoreMatchers.instanceOf(OrderByStreamMergedResult.class));
    }

    @Test
    public void assertBuildOrderByStreamMergedResultWithSQLServerLimit() throws SQLException {
        selectStatement.setLimit(new Limit());
        selectStatement.getOrderByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC));
        mergeEngine = new DQLMergeEngine(DatabaseType.SQLServer, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertThat(actual, CoreMatchers.instanceOf(TopAndRowNumberDecoratorMergedResult.class));
        Assert.assertThat(getMergedResult(), CoreMatchers.instanceOf(OrderByStreamMergedResult.class));
    }

    @Test
    public void assertBuildGroupByStreamMergedResult() throws SQLException {
        selectStatement.getGroupByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC));
        selectStatement.getOrderByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC));
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        Assert.assertThat(mergeEngine.merge(), CoreMatchers.instanceOf(GroupByStreamMergedResult.class));
    }

    @Test
    public void assertBuildGroupByStreamMergedResultWithMySQLLimit() throws SQLException {
        selectStatement.setLimit(new Limit());
        selectStatement.getGroupByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC));
        selectStatement.getOrderByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC));
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertThat(actual, CoreMatchers.instanceOf(LimitDecoratorMergedResult.class));
        Assert.assertThat(getMergedResult(), CoreMatchers.instanceOf(GroupByStreamMergedResult.class));
    }

    @Test
    public void assertBuildGroupByStreamMergedResultWithOracleLimit() throws SQLException {
        selectStatement.setLimit(new Limit());
        selectStatement.getGroupByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC));
        selectStatement.getOrderByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC));
        mergeEngine = new DQLMergeEngine(DatabaseType.Oracle, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertThat(actual, CoreMatchers.instanceOf(RowNumberDecoratorMergedResult.class));
        Assert.assertThat(getMergedResult(), CoreMatchers.instanceOf(GroupByStreamMergedResult.class));
    }

    @Test
    public void assertBuildGroupByStreamMergedResultWithSQLServerLimit() throws SQLException {
        selectStatement.setLimit(new Limit());
        selectStatement.getGroupByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC));
        selectStatement.getOrderByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC));
        mergeEngine = new DQLMergeEngine(DatabaseType.SQLServer, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertThat(actual, CoreMatchers.instanceOf(TopAndRowNumberDecoratorMergedResult.class));
        Assert.assertThat(getMergedResult(), CoreMatchers.instanceOf(GroupByStreamMergedResult.class));
    }

    @Test
    public void assertBuildGroupByMemoryMergedResult() throws SQLException {
        selectStatement.getGroupByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC));
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        Assert.assertThat(mergeEngine.merge(), CoreMatchers.instanceOf(GroupByMemoryMergedResult.class));
    }

    @Test
    public void assertBuildGroupByMemoryMergedResultWithMySQLLimit() throws SQLException {
        selectStatement.setLimit(new Limit());
        selectStatement.getGroupByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC));
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertThat(actual, CoreMatchers.instanceOf(LimitDecoratorMergedResult.class));
        Assert.assertThat(getMergedResult(), CoreMatchers.instanceOf(GroupByMemoryMergedResult.class));
    }

    @Test
    public void assertBuildGroupByMemoryMergedResultWithOracleLimit() throws SQLException {
        selectStatement.setLimit(new Limit());
        selectStatement.getGroupByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC));
        selectStatement.getOrderByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(2, OrderDirection.DESC, OrderDirection.ASC));
        mergeEngine = new DQLMergeEngine(DatabaseType.Oracle, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertThat(actual, CoreMatchers.instanceOf(RowNumberDecoratorMergedResult.class));
        Assert.assertThat(getMergedResult(), CoreMatchers.instanceOf(GroupByMemoryMergedResult.class));
    }

    @Test
    public void assertBuildGroupByMemoryMergedResultWithSQLServerLimit() throws SQLException {
        selectStatement.setLimit(new Limit());
        selectStatement.getGroupByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC));
        selectStatement.getGroupByItems().add(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.ASC, OrderDirection.ASC));
        mergeEngine = new DQLMergeEngine(DatabaseType.SQLServer, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertThat(actual, CoreMatchers.instanceOf(TopAndRowNumberDecoratorMergedResult.class));
        Assert.assertThat(getMergedResult(), CoreMatchers.instanceOf(GroupByMemoryMergedResult.class));
    }

    @Test
    public void assertBuildGroupByMemoryMergedResultWithAggregationOnly() throws SQLException {
        selectStatement.getItems().add(new org.apache.shardingsphere.core.parsing.parser.context.selectitem.AggregationSelectItem(AggregationType.COUNT, "(*)", Optional.<String>absent()));
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        Assert.assertThat(mergeEngine.merge(), CoreMatchers.instanceOf(GroupByMemoryMergedResult.class));
    }

    @Test
    public void assertBuildGroupByMemoryMergedResultWithAggregationOnlyWithMySQLLimit() throws SQLException {
        selectStatement.setLimit(new Limit());
        selectStatement.getItems().add(new org.apache.shardingsphere.core.parsing.parser.context.selectitem.AggregationSelectItem(AggregationType.COUNT, "(*)", Optional.<String>absent()));
        mergeEngine = new DQLMergeEngine(DatabaseType.MySQL, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertThat(actual, CoreMatchers.instanceOf(LimitDecoratorMergedResult.class));
        Assert.assertThat(getMergedResult(), CoreMatchers.instanceOf(GroupByMemoryMergedResult.class));
    }

    @Test
    public void assertBuildGroupByMemoryMergedResultWithAggregationOnlyWithOracleLimit() throws SQLException {
        selectStatement.setLimit(new Limit());
        selectStatement.getItems().add(new org.apache.shardingsphere.core.parsing.parser.context.selectitem.AggregationSelectItem(AggregationType.COUNT, "(*)", Optional.<String>absent()));
        mergeEngine = new DQLMergeEngine(DatabaseType.Oracle, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertThat(actual, CoreMatchers.instanceOf(RowNumberDecoratorMergedResult.class));
        Assert.assertThat(getMergedResult(), CoreMatchers.instanceOf(GroupByMemoryMergedResult.class));
    }

    @Test
    public void assertBuildGroupByMemoryMergedResultWithAggregationOnlyWithSQLServerLimit() throws SQLException {
        selectStatement.setLimit(new Limit());
        selectStatement.getItems().add(new org.apache.shardingsphere.core.parsing.parser.context.selectitem.AggregationSelectItem(AggregationType.COUNT, "(*)", Optional.<String>absent()));
        mergeEngine = new DQLMergeEngine(DatabaseType.SQLServer, selectStatement, queryResults);
        MergedResult actual = mergeEngine.merge();
        Assert.assertThat(actual, CoreMatchers.instanceOf(TopAndRowNumberDecoratorMergedResult.class));
        Assert.assertThat(getMergedResult(), CoreMatchers.instanceOf(GroupByMemoryMergedResult.class));
    }
}

