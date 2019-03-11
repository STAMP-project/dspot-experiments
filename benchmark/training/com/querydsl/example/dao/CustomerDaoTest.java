package com.querydsl.example.dao;


import com.querydsl.example.dto.Customer;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;


public class CustomerDaoTest extends AbstractDaoTest {
    @Resource
    CustomerDao customerDao;

    @Test
    public void findAll() {
        List<Customer> customers = customerDao.findAll();
        Assert.assertFalse(customers.isEmpty());
    }

    @Test
    public void findById() {
        Assert.assertNotNull(customerDao.findById(1));
    }

    @Test
    public void update() {
        Customer customer = customerDao.findById(1);
        customerDao.save(customer);
    }

    @Test
    public void delete() {
        Customer customer = customerDao.findById(1);
        customerDao.delete(customer);
        Assert.assertNull(customerDao.findById(1));
    }
}

