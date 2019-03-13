package com.alibaba.json.demo.hibernate;


import com.alibaba.fastjson.JSON;
import com.alibaba.json.demo.hibernate.data.Customer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import junit.framework.TestCase;
import org.hibernate.Hibernate;


public class ForceLazyLoadingTest extends TestCase {
    EntityManagerFactory emf;

    public void testGetCustomerJson() throws Exception {
        EntityManager em = emf.createEntityManager();
        // false -> no forcing of lazy loading
        Customer customer = em.find(Customer.class, 103);
        TestCase.assertFalse(Hibernate.isInitialized(customer.getPayments()));
        String json = JSON.toJSONString(customer);
        System.out.println(json);
        // should force loading...
        // Set<Payment> payments = customer.getPayments();
        // /*
        // System.out.println("--- JSON ---");
        // System.out.println(json);
        // System.out.println("--- /JSON ---");
        // */
        // 
        // assertTrue(Hibernate.isInitialized(payments));
        // // TODO: verify
        // assertNotNull(json);
        // 
        // Map<?,?> stuff = mapper.readValue(json, Map.class);
        // 
        // assertTrue(stuff.containsKey("payments"));
        // assertTrue(stuff.containsKey("orders"));
        // assertNull(stuff.get("orderes"));
    }
}

