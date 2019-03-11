package com.baeldung.hibernate.joincolumn;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;


public class JoinColumnIntegrationTest {
    private Session session;

    private Transaction transaction;

    @Test
    public void givenOfficeEntity_setAddress_shouldPersist() {
        Office office = new Office();
        Address address = new Address();
        address.setZipCode("11-111");
        office.setAddress(address);
        session.save(office);
        session.flush();
        session.clear();
    }

    @Test
    public void givenEmployeeEntity_setEmails_shouldPersist() {
        Employee employee = new Employee();
        Email email = new Email();
        email.setAddress("example@email.com");
        email.setEmployee(employee);
        session.save(employee);
        session.flush();
        session.clear();
    }
}

