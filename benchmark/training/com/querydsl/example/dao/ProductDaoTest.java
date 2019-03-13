package com.querydsl.example.dao;


import com.google.common.collect.ImmutableSet;
import com.querydsl.example.dto.Product;
import com.querydsl.example.dto.ProductL10n;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Assert;
import org.junit.Test;


public class ProductDaoTest extends AbstractDaoTest {
    @Resource
    SupplierDao supplierDao;

    @Resource
    ProductDao productDao;

    @Test
    public void findAll() {
        List<Product> products = productDao.findAll();
        Assert.assertFalse(products.isEmpty());
    }

    @Test
    public void findById() {
        Assert.assertNotNull(productDao.findById(1));
    }

    @Test
    public void update() {
        Product product = productDao.findById(1);
        productDao.save(product);
    }

    @Test
    public void delete() {
        Product product = new Product();
        product.setSupplier(supplierDao.findById(1));
        product.setName("ProductX");
        product.setLocalizations(ImmutableSet.of(new ProductL10n()));
        productDao.save(product);
        Assert.assertNotNull(productDao.findById(product.getId()));
        productDao.delete(product);
        Assert.assertNull(productDao.findById(product.getId()));
    }
}

