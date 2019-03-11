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
package org.apache.shardingsphere.core.merger.dql.groupby;


import java.sql.SQLException;
import java.util.Arrays;
import org.apache.shardingsphere.core.constant.OrderDirection;
import org.apache.shardingsphere.core.merger.dql.common.MemoryQueryResultRow;
import org.apache.shardingsphere.core.parsing.parser.sql.dql.select.SelectStatement;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class GroupByRowComparatorTest {
    @Test
    public void assertCompareToForAscWithOrderByItems() throws SQLException {
        MemoryQueryResultRow o1 = new MemoryQueryResultRow(mockQueryResult("1", "2"));
        MemoryQueryResultRow o2 = new MemoryQueryResultRow(mockQueryResult("3", "4"));
        SelectStatement selectStatement = new SelectStatement();
        selectStatement.getOrderByItems().addAll(Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.ASC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(2, OrderDirection.ASC, OrderDirection.ASC)));
        selectStatement.getGroupByItems().addAll(Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(2, OrderDirection.DESC, OrderDirection.ASC)));
        GroupByRowComparator groupByRowComparator = new GroupByRowComparator(selectStatement);
        Assert.assertTrue(((groupByRowComparator.compare(o1, o2)) < 0));
    }

    @Test
    public void assertCompareToForDecsWithOrderByItems() throws SQLException {
        MemoryQueryResultRow o1 = new MemoryQueryResultRow(mockQueryResult("1", "2"));
        MemoryQueryResultRow o2 = new MemoryQueryResultRow(mockQueryResult("3", "4"));
        SelectStatement selectStatement = new SelectStatement();
        selectStatement.getOrderByItems().addAll(Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(2, OrderDirection.DESC, OrderDirection.ASC)));
        selectStatement.getGroupByItems().addAll(Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.ASC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(2, OrderDirection.ASC, OrderDirection.ASC)));
        GroupByRowComparator groupByRowComparator = new GroupByRowComparator(selectStatement);
        Assert.assertTrue(((groupByRowComparator.compare(o1, o2)) > 0));
    }

    @Test
    public void assertCompareToForEqualWithOrderByItems() throws SQLException {
        MemoryQueryResultRow o1 = new MemoryQueryResultRow(mockQueryResult("1", "2"));
        MemoryQueryResultRow o2 = new MemoryQueryResultRow(mockQueryResult("1", "2"));
        SelectStatement selectStatement = new SelectStatement();
        selectStatement.getOrderByItems().addAll(Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.ASC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(2, OrderDirection.DESC, OrderDirection.ASC)));
        selectStatement.getGroupByItems().addAll(Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(2, OrderDirection.ASC, OrderDirection.ASC)));
        GroupByRowComparator groupByRowComparator = new GroupByRowComparator(selectStatement);
        Assert.assertThat(groupByRowComparator.compare(o1, o2), CoreMatchers.is(0));
    }

    @Test
    public void assertCompareToForAscWithGroupByItems() throws SQLException {
        MemoryQueryResultRow o1 = new MemoryQueryResultRow(mockQueryResult("1", "2"));
        MemoryQueryResultRow o2 = new MemoryQueryResultRow(mockQueryResult("3", "4"));
        SelectStatement selectStatement = new SelectStatement();
        selectStatement.getGroupByItems().addAll(Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.ASC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(2, OrderDirection.ASC, OrderDirection.ASC)));
        GroupByRowComparator groupByRowComparator = new GroupByRowComparator(selectStatement);
        Assert.assertTrue(((groupByRowComparator.compare(o1, o2)) < 0));
    }

    @Test
    public void assertCompareToForDecsWithGroupByItems() throws SQLException {
        MemoryQueryResultRow o1 = new MemoryQueryResultRow(mockQueryResult("1", "2"));
        MemoryQueryResultRow o2 = new MemoryQueryResultRow(mockQueryResult("3", "4"));
        SelectStatement selectStatement = new SelectStatement();
        selectStatement.getGroupByItems().addAll(Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(2, OrderDirection.DESC, OrderDirection.ASC)));
        GroupByRowComparator groupByRowComparator = new GroupByRowComparator(selectStatement);
        Assert.assertTrue(((groupByRowComparator.compare(o1, o2)) > 0));
    }

    @Test
    public void assertCompareToForEqualWithGroupByItems() throws SQLException {
        MemoryQueryResultRow o1 = new MemoryQueryResultRow(mockQueryResult("1", "2"));
        MemoryQueryResultRow o2 = new MemoryQueryResultRow(mockQueryResult("1", "2"));
        SelectStatement selectStatement = new SelectStatement();
        selectStatement.getGroupByItems().addAll(Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.ASC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(2, OrderDirection.DESC, OrderDirection.ASC)));
        GroupByRowComparator groupByRowComparator = new GroupByRowComparator(selectStatement);
        Assert.assertThat(groupByRowComparator.compare(o1, o2), CoreMatchers.is(0));
    }
}

