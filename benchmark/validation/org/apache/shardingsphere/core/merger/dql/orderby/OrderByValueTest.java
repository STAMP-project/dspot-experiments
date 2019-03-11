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
package org.apache.shardingsphere.core.merger.dql.orderby;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import org.apache.shardingsphere.core.constant.OrderDirection;
import org.apache.shardingsphere.core.merger.fixture.TestQueryResult;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class OrderByValueTest {
    @Mock
    private ResultSet resultSet1;

    @Mock
    private ResultSet resultSet2;

    @Test
    public void assertCompareToForAsc() throws SQLException {
        OrderByValue orderByValue1 = new OrderByValue(new TestQueryResult(resultSet1), Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.ASC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(2, OrderDirection.ASC, OrderDirection.ASC)));
        Assert.assertTrue(orderByValue1.next());
        Mockito.when(resultSet2.getObject(1)).thenReturn("3");
        Mockito.when(resultSet2.getObject(2)).thenReturn("4");
        OrderByValue orderByValue2 = new OrderByValue(new TestQueryResult(resultSet2), Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.ASC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(2, OrderDirection.ASC, OrderDirection.ASC)));
        Assert.assertTrue(orderByValue2.next());
        Assert.assertTrue(((orderByValue1.compareTo(orderByValue2)) < 0));
        Assert.assertFalse(orderByValue1.getQueryResult().next());
        Assert.assertFalse(orderByValue2.getQueryResult().next());
    }

    @Test
    public void assertCompareToForDesc() throws SQLException {
        OrderByValue orderByValue1 = new OrderByValue(new TestQueryResult(resultSet1), Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(2, OrderDirection.DESC, OrderDirection.ASC)));
        Assert.assertTrue(orderByValue1.next());
        Mockito.when(resultSet2.getObject(1)).thenReturn("3");
        Mockito.when(resultSet2.getObject(2)).thenReturn("4");
        OrderByValue orderByValue2 = new OrderByValue(new TestQueryResult(resultSet2), Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.DESC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(2, OrderDirection.DESC, OrderDirection.ASC)));
        Assert.assertTrue(orderByValue2.next());
        Assert.assertTrue(((orderByValue1.compareTo(orderByValue2)) > 0));
        Assert.assertFalse(orderByValue1.getQueryResult().next());
        Assert.assertFalse(orderByValue2.getQueryResult().next());
    }

    @Test
    public void assertCompareToWhenEqual() throws SQLException {
        OrderByValue orderByValue1 = new OrderByValue(new TestQueryResult(resultSet1), Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.ASC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(2, OrderDirection.DESC, OrderDirection.ASC)));
        Assert.assertTrue(orderByValue1.next());
        Mockito.when(resultSet2.getObject(1)).thenReturn("1");
        Mockito.when(resultSet2.getObject(2)).thenReturn("2");
        OrderByValue orderByValue2 = new OrderByValue(new TestQueryResult(resultSet2), Arrays.asList(new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(1, OrderDirection.ASC, OrderDirection.ASC), new org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem(2, OrderDirection.DESC, OrderDirection.ASC)));
        Assert.assertTrue(orderByValue2.next());
        Assert.assertThat(orderByValue1.compareTo(orderByValue2), CoreMatchers.is(0));
        Assert.assertFalse(orderByValue1.getQueryResult().next());
        Assert.assertFalse(orderByValue2.getQueryResult().next());
    }
}

