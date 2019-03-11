package com.baeldung.hibernate.namingstrategy;


import org.hibernate.Session;
import org.junit.Test;


public class NamingStrategyLiveTest {
    private Session session;

    @Test
    public void testCustomPhysicalNamingStrategy() {
        Customer customer = new Customer();
        customer.setFirstName("first name");
        customer.setLastName("last name");
        customer.setEmailAddress("customer@example.com");
        session.beginTransaction();
        Long id = ((Long) (session.save(customer)));
        session.flush();
        session.clear();
        Object[] result = ((Object[]) (session.createNativeQuery("select c.first_name, c.last_name, c.email from customers c where c.id = :id").setParameter("id", id).getSingleResult()));
    }
}

