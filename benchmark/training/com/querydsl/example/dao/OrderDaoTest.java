package com.querydsl.example.dao;


import com.google.common.collect.ImmutableSet;
import com.querydsl.example.dto.CustomerPaymentMethod;
import com.querydsl.example.dto.Order;
import com.querydsl.example.dto.OrderProduct;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;


public class OrderDaoTest extends AbstractDaoTest {
    @Resource
    OrderDao orderDao;

    @Test
    public void findAll() {
        List<Order> orders = orderDao.findAll();
        Assert.assertFalse(orders.isEmpty());
    }

    @Test
    public void findById() {
        Assert.assertNotNull(orderDao.findById(1));
    }

    @Test
    public void update() {
        Order order = orderDao.findById(1);
        orderDao.save(order);
    }

    @Test
    public void delete() {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProductId(1L);
        orderProduct.setQuantity(1);
        // FIXME
        CustomerPaymentMethod paymentMethod = new CustomerPaymentMethod();
        Order order = new Order();
        order.setCustomerPaymentMethod(paymentMethod);
        order.setOrderProducts(ImmutableSet.of(orderProduct));
        orderDao.save(order);
        Assert.assertNotNull(order.getId());
        orderDao.delete(order);
        Assert.assertNull(orderDao.findById(order.getId()));
    }
}

