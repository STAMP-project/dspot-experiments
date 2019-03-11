package com.querydsl.example.dao;


import com.querydsl.example.dto.Supplier;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;


public class SupplierDaoTest extends AbstractDaoTest {
    @Resource
    SupplierDao supplierDao;

    @Test
    public void findAll() {
        List<Supplier> suppliers = supplierDao.findAll();
        Assert.assertFalse(suppliers.isEmpty());
    }

    @Test
    public void findById() {
        Assert.assertNotNull(supplierDao.findById(1));
    }

    @Test
    public void update() {
        Supplier supplier = supplierDao.findById(1);
        supplierDao.save(supplier);
    }

    @Test
    public void delete() {
        Supplier supplier = new Supplier();
        supplierDao.save(supplier);
        Assert.assertNotNull(supplier.getId());
        supplierDao.delete(supplier);
        Assert.assertNull(supplierDao.findById(supplier.getId()));
    }
}

