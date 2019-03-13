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


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import org.apache.shardingsphere.core.constant.OrderDirection;
import org.apache.shardingsphere.core.merger.fixture.TestQueryResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class GroupByValueTest {
    @Mock
    private ResultSet resultSet;

    @Test
    public void assertGetGroupByValues() throws SQLException {
        List<?> actual = getGroupValues();
        List<?> expected = Arrays.asList("1", "3");
        Assert.assertTrue(actual.equals(expected));
    }

    @Test
    public void assertGroupByValueEquals() throws SQLException {
        GroupByValue groupByValue1 = new GroupByValue(new TestQueryResult(resultSet), Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.ASC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(3, OrderDirection.DESC, OrderDirection.ASC)));
        GroupByValue groupByValue2 = new GroupByValue(new TestQueryResult(resultSet), Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.ASC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(3, OrderDirection.DESC, OrderDirection.ASC)));
        Assert.assertTrue(groupByValue1.equals(groupByValue2));
        Assert.assertTrue(groupByValue2.equals(groupByValue1));
        Assert.assertTrue(((groupByValue1.hashCode()) == (groupByValue2.hashCode())));
    }

    @Test
    public void assertGroupByValueNotEquals() throws SQLException {
        GroupByValue groupByValue1 = new GroupByValue(new TestQueryResult(resultSet), Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.ASC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(3, OrderDirection.DESC, OrderDirection.ASC)));
        GroupByValue groupByValue2 = new GroupByValue(new TestQueryResult(resultSet), Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(3, OrderDirection.ASC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC)));
        Assert.assertFalse(groupByValue1.equals(groupByValue2));
        Assert.assertFalse(((groupByValue1.hashCode()) == (groupByValue2.hashCode())));
    }
}

