package com.baeldung.drools.service;


import com.baeldung.drools.model.Customer;
import com.baeldung.drools.model.Customer.CustomerType;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;


public class DiscountExcelIntegrationTest {
    private KieSession kSession;

    @Test
    public void giveIndvidualLongStanding_whenFireRule_thenCorrectDiscount() throws Exception {
        Customer customer = new Customer(CustomerType.INDIVIDUAL, 5);
        kSession.insert(customer);
        kSession.fireAllRules();
        Assert.assertEquals(customer.getDiscount(), 15);
    }

    @Test
    public void giveIndvidualRecent_whenFireRule_thenCorrectDiscount() throws Exception {
        Customer customer = new Customer(CustomerType.INDIVIDUAL, 1);
        kSession.insert(customer);
        kSession.fireAllRules();
        Assert.assertEquals(customer.getDiscount(), 5);
    }

    @Test
    public void giveBusinessAny_whenFireRule_thenCorrectDiscount() throws Exception {
        Customer customer = new Customer(CustomerType.BUSINESS, 0);
        kSession.insert(customer);
        kSession.fireAllRules();
        Assert.assertEquals(customer.getDiscount(), 20);
    }
}

