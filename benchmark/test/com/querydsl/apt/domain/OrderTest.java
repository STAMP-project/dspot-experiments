package com.querydsl.apt.domain;


import QOrderTest_Order.order.orderItems;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import org.junit.Assert;
import org.junit.Test;


public class OrderTest {
    @Entity
    public static class Order {
        @OneToMany(targetEntity = OrderTest.OrderItemImpl.class)
        List<OrderTest.OrderItem> orderItems;
    }

    @Entity
    public interface OrderItem {}

    @Entity
    public static class OrderItemImpl implements OrderTest.OrderItem {}

    @Test
    public void test() {
        Assert.assertEquals(QOrderTest_OrderItemImpl.class, orderItems.any().getClass());
    }
}

