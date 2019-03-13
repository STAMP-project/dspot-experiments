package com.baeldung.hibernate.persistjson;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;


public class PersistJSONUnitTest {
    private Session session;

    @Test
    public void givenCustomer_whenCallingSerializeCustomerAttributes_thenAttributesAreConverted() throws IOException {
        Customer customer = new Customer();
        customer.setFirstName("first name");
        customer.setLastName("last name");
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("address", "123 Main Street");
        attributes.put("zipcode", 12345);
        customer.setCustomerAttributes(attributes);
        customer.serializeCustomerAttributes();
        String serialized = customer.getCustomerAttributeJSON();
        customer.setCustomerAttributeJSON(serialized);
        customer.deserializeCustomerAttributes();
        Map<String, Object> deserialized = customer.getCustomerAttributes();
        Assert.assertEquals("123 Main Street", deserialized.get("address"));
    }

    @Test
    public void givenCustomer_whenSaving_thenAttributesAreConverted() {
        Customer customer = new Customer();
        customer.setFirstName("first name");
        customer.setLastName("last name");
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("address", "123 Main Street");
        attributes.put("zipcode", 12345);
        customer.setCustomerAttributes(attributes);
        session.beginTransaction();
        int id = ((int) (session.save(customer)));
        session.flush();
        session.clear();
        Customer result = session.createNativeQuery("select * from Customers where Customers.id = :id", Customer.class).setParameter("id", id).getSingleResult();
        Assert.assertEquals(2, result.getCustomerAttributes().size());
    }
}

