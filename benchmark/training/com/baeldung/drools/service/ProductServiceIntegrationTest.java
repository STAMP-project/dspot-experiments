package com.baeldung.drools.service;


import com.baeldung.drools.model.Product;
import junit.framework.TestCase;
import org.junit.Test;


public class ProductServiceIntegrationTest {
    private ProductService productService;

    @Test
    public void whenProductTypeElectronic_ThenLabelBarcode() {
        Product product = new Product("Microwave", "Electronic");
        product = productService.applyLabelToProduct(product);
        TestCase.assertEquals("BarCode", product.getLabel());
    }

    @Test
    public void whenProductTypeBook_ThenLabelIsbn() {
        Product product = new Product("AutoBiography", "Book");
        product = productService.applyLabelToProduct(product);
        TestCase.assertEquals("ISBN", product.getLabel());
    }
}

