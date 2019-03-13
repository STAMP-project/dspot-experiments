package com.baeldung.hibernate.onetoone;


import com.baeldung.hibernate.onetoone.jointablebased.Employee;
import com.baeldung.hibernate.onetoone.jointablebased.WorkStation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Test;


public class HibernateOneToOneAnnotationJTBasedIntegrationTest {
    private static SessionFactory sessionFactory;

    private Session session;

    @Test
    public void givenData_whenInsert_thenCreates1to1relationship() {
        Employee employee = new Employee();
        employee.setName("bob@baeldung.com");
        WorkStation workStation = new WorkStation();
        workStation.setWorkstationNumber(626);
        workStation.setFloor("Sixth Floor");
        employee.setWorkStation(workStation);
        workStation.setEmployee(employee);
        session.persist(employee);
        session.getTransaction().commit();
        assert1to1InsertedData();
    }
}

