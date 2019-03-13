/**
 * Copyright 2017 ObjectBox Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.objectbox.relation;


import Customer_.orders;
import Order_.customer;
import io.objectbox.query.Query;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class RelationEagerTest extends AbstractRelationTest {
    @Test
    public void testEagerToMany() {
        Customer customer = putCustomer();
        putOrder(customer, "Bananas");
        putOrder(customer, "Oranges");
        Customer customer2 = putCustomer();
        putOrder(customer2, "Apples");
        // full list
        List<Customer> customers = customerBox.query().eager(orders).build().find();
        Assert.assertEquals(2, customers.size());
        Assert.assertTrue(isResolved());
        Assert.assertTrue(isResolved());
        // full list paginated
        customers = customerBox.query().eager(orders).build().find(0, 10);
        Assert.assertEquals(2, customers.size());
        Assert.assertTrue(isResolved());
        Assert.assertTrue(isResolved());
        // list with eager limit
        customers = customerBox.query().eager(1, orders).build().find();
        Assert.assertEquals(2, customers.size());
        Assert.assertTrue(isResolved());
        Assert.assertFalse(isResolved());
        // forEach
        final int[] count = new int[]{ 0 };
        customerBox.query().eager(1, orders).build().forEach(new io.objectbox.query.QueryConsumer<Customer>() {
            @Override
            public void accept(Customer data) {
                Assert.assertEquals(((count[0]) == 0), isResolved());
                (count[0])++;
            }
        });
        Assert.assertEquals(2, count[0]);
        // first
        customer = customerBox.query().eager(orders).build().findFirst();
        Assert.assertTrue(isResolved());
        // unique
        customerBox.remove(customer);
        customer = customerBox.query().eager(orders).build().findUnique();
        Assert.assertTrue(isResolved());
    }

    @Test
    public void testEagerToMany_NoResult() {
        Query<Customer> query = customerBox.query().eager(orders).build();
        query.find();
        query.findFirst();
        query.forEach(new io.objectbox.query.QueryConsumer<Customer>() {
            @Override
            public void accept(Customer data) {
            }
        });
    }

    @Test
    public void testEagerToSingle() {
        Customer customer = putCustomer();
        putOrder(customer, "Bananas");
        putOrder(customer, "Oranges");
        // full list
        List<Order> orders = orderBox.query().eager(customer).build().find();
        Assert.assertEquals(2, orders.size());
        Assert.assertTrue(isResolved());
        Assert.assertTrue(isResolved());
        // full list paginated
        orders = orderBox.query().eager(customer).build().find(0, 10);
        Assert.assertEquals(2, orders.size());
        Assert.assertTrue(isResolved());
        Assert.assertTrue(isResolved());
        // list with eager limit
        orders = orderBox.query().eager(1, customer).build().find();
        Assert.assertEquals(2, orders.size());
        Assert.assertTrue(isResolved());
        Assert.assertFalse(isResolved());
        // forEach
        final int[] count = new int[]{ 0 };
        customerBox.query().eager(1, orders).build().forEach(new io.objectbox.query.QueryConsumer<Customer>() {
            @Override
            public void accept(Customer data) {
                Assert.assertEquals(((count[0]) == 0), isResolved());
                (count[0])++;
            }
        });
        Assert.assertEquals(1, count[0]);
        // first
        Order order = orderBox.query().eager(customer).build().findFirst();
        Assert.assertTrue(order.customer__toOne.isResolved());
        // unique
        orderBox.remove(order);
        order = orderBox.query().eager(customer).build().findUnique();
        Assert.assertTrue(order.customer__toOne.isResolved());
    }

    @Test
    public void testEagerToSingle_NoResult() {
        Query<Order> query = orderBox.query().eager(customer).build();
        query.find();
        query.findFirst();
        query.forEach(new io.objectbox.query.QueryConsumer<Order>() {
            @Override
            public void accept(Order data) {
            }
        });
    }
}

