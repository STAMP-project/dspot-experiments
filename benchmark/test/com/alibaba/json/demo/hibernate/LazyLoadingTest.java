package com.alibaba.json.demo.hibernate;


import com.alibaba.fastjson.JSON;
import com.alibaba.json.demo.hibernate.data.Customer;
import com.alibaba.json.demo.hibernate.data.Payment;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import junit.framework.TestCase;


// @Test
// public void testSerializeIdentifierFeature() throws JsonProcessingException {
// Hibernate5Module module = new Hibernate5Module();
// module.enable(Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS);
// ObjectMapper objectMapper = new ObjectMapper().registerModule(module);
// 
// EntityManagerFactory emf = Persistence.createEntityManagerFactory("persistenceUnit");
// try {
// EntityManager em = emf.createEntityManager();
// Customer customerRef = em.getReference(Customer.class, 103);
// em.close();
// assertFalse(Hibernate.isInitialized(customerRef));
// 
// String json = objectMapper.writeValueAsString(customerRef);
// assertFalse(Hibernate.isInitialized(customerRef));
// assertEquals("{\"customerNumber\":103}", json);
// } finally {
// emf.close();
// }
// }
public class LazyLoadingTest extends TestCase {
    EntityManagerFactory emf;

    public void testGetCustomerJson() throws Exception {
        EntityManager em = emf.createEntityManager();
        // false -> no forcing of lazy loading
        // ObjectMapper mapper = mapperWithModule(false);
        Customer customer = em.find(Customer.class, 103);
        // assertFalse(Hibernate.isInitialized(customer.getPayments()));
        String json = JSON.toJSONString(customer);
        System.out.println(json);
        // should not force loading...
        Set<Payment> payments = customer.getPayments();
        /* System.out.println("--- JSON ---");
        System.out.println(json);
        System.out.println("--- /JSON ---");
         */
        // assertFalse(Hibernate.isInitialized(payments));
        // TODO: verify
        TestCase.assertNotNull(json);
        // Map<?,?> stuff = mapper.readValue(json, Map.class);
        // 
        // // "payments" is marked as lazily loaded AND "Include.NON_EMPTY"; should not be serialized
        // if (stuff.containsKey("payments")) {
        // fail("Should not find serialized property 'payments'; got: "+stuff.get("payments")
        // +" from JSON: "+json);
        // }
        // // orders, on the other hand, not:
        // assertTrue(stuff.containsKey("orders"));
        // assertNull(stuff.get("orderes"));
    }
}

