package com.baeldung.hibernate.onetoone;


import com.baeldung.hibernate.onetoone.sharedkeybased.Address;
import com.baeldung.hibernate.onetoone.sharedkeybased.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;


public class HibernateOneToOneAnnotationSPKBasedIntegrationTest {
    private static SessionFactory sessionFactory;

    private Session session;

    @Test
    public void givenData_whenInsert_thenCreates1to1relationship() {
        User user = new User();
        user.setUserName("alice@baeldung.com");
        Address address = new Address();
        address.setStreet("SPK Street");
        address.setCity("SPK City");
        address.setUser(user);
        user.setAddress(address);
        // Address entry will automatically be created by hibernate, since cascade type is specified as ALL
        session.persist(user);
        session.getTransaction().commit();
        assert1to1InsertedData();
    }
}

