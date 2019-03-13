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
package org.apache.shardingsphere.core.parsing.parser.context;


import org.apache.shardingsphere.core.constant.OrderDirection;
import org.apache.shardingsphere.core.parsing.parser.context.orderby.OrderItem;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class OrderItemTest {
    @Test
    public void assertGetColumnLabelWithoutAlias() {
        OrderItem actualOrderItem = new OrderItem("column_name", OrderDirection.ASC, OrderDirection.ASC);
        Assert.assertThat(actualOrderItem.getColumnLabel(), CoreMatchers.is("column_name"));
    }

    @Test
    public void assertGetColumnLabelWithAlias() {
        OrderItem actualOrderItem = new OrderItem("column_name", OrderDirection.ASC, OrderDirection.ASC);
        actualOrderItem.setAlias("column_alias");
        Assert.assertThat(actualOrderItem.getColumnLabel(), CoreMatchers.is("column_alias"));
    }

    @Test
    public void assertGetColumnLabelWithIndex() {
        OrderItem actualOrderItem = new OrderItem(1, OrderDirection.ASC, OrderDirection.ASC);
        Assert.assertNull(actualOrderItem.getColumnLabel());
    }

    @Test
    public void assertGetQualifiedNameWithoutName() {
        OrderItem actualOrderItem = new OrderItem(1, OrderDirection.ASC, OrderDirection.ASC);
        Assert.assertNull(actualOrderItem.getQualifiedName().orNull());
    }

    @Test
    public void assertGetColumnLabelWithOutOwner() {
        OrderItem actualOrderItem = new OrderItem("column_name", OrderDirection.ASC, OrderDirection.ASC);
        actualOrderItem.setAlias("column_alias");
        Assert.assertThat(actualOrderItem.getQualifiedName().orNull(), CoreMatchers.is("column_name"));
    }

    @Test
    public void assertGetColumnLabelWithOwner() {
        OrderItem actualOrderItem = new OrderItem("tbl", "column_name", OrderDirection.ASC, OrderDirection.ASC);
        actualOrderItem.setAlias("column_alias");
        Assert.assertThat(actualOrderItem.getQualifiedName().orNull(), CoreMatchers.is("tbl.column_name"));
    }

    @SuppressWarnings("ObjectEqualsNull")
    @Test
    public void assertEqualsWithNull() {
        Assert.assertFalse(new OrderItem(1, OrderDirection.ASC, OrderDirection.ASC).equals(null));
    }

    @Test
    public void assertEqualsWithOtherObject() {
        Assert.assertFalse(new OrderItem(1, OrderDirection.ASC, OrderDirection.ASC).equals(new Object()));
    }

    @Test
    public void assertEqualsWithDifferentOrderType() {
        Assert.assertFalse(new OrderItem(1, OrderDirection.ASC, OrderDirection.ASC).equals(new OrderItem(1, OrderDirection.DESC, OrderDirection.ASC)));
    }

    @Test
    public void assertEqualsWithSameColumnLabel() {
        OrderItem orderItem1 = new OrderItem("column_name", OrderDirection.ASC, OrderDirection.ASC);
        orderItem1.setAlias("column_alias");
        OrderItem orderItem2 = new OrderItem("tbl", "column_name", OrderDirection.ASC, OrderDirection.ASC);
        orderItem2.setAlias("column_alias");
        Assert.assertThat(orderItem1, CoreMatchers.is(orderItem2));
    }

    @Test
    public void assertEqualsWithSameQualifiedName() {
        OrderItem orderItem1 = new OrderItem("tbl", "column_name", OrderDirection.ASC, OrderDirection.ASC);
        orderItem1.setAlias("column_alias");
        OrderItem orderItem2 = new OrderItem("tbl", "column_name", OrderDirection.ASC, OrderDirection.ASC);
        Assert.assertThat(orderItem1, CoreMatchers.is(orderItem2));
    }

    @Test
    public void assertEqualsWithSameIndex() {
        Assert.assertTrue(new OrderItem(1, OrderDirection.ASC, OrderDirection.ASC).equals(new OrderItem(1, OrderDirection.ASC, OrderDirection.ASC)));
    }

    @Test
    public void assertNotEquals() {
        OrderItem orderItem1 = new OrderItem("tbl", "column_name", OrderDirection.ASC, OrderDirection.ASC);
        orderItem1.setAlias("column_alias");
        OrderItem orderItem2 = new OrderItem("column_name", OrderDirection.ASC, OrderDirection.ASC);
        Assert.assertThat(orderItem1, CoreMatchers.not(orderItem2));
    }
}

