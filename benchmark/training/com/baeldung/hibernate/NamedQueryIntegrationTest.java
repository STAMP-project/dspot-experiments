package com.baeldung.hibernate;


import com.baeldung.hibernate.entities.Department;
import com.baeldung.hibernate.entities.DeptEmployee;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.junit.Assert;
import org.junit.Test;


public class NamedQueryIntegrationTest {
    private static Session session;

    private Transaction transaction;

    private Long purchaseDeptId;

    @Test
    public void whenNamedQueryIsCalledUsingCreateNamedQuery_ThenOk() {
        Query<DeptEmployee> query = NamedQueryIntegrationTest.session.createNamedQuery("DeptEmployee_FindByEmployeeNumber", DeptEmployee.class);
        query.setParameter("employeeNo", "001");
        DeptEmployee result = query.getSingleResult();
        Assert.assertNotNull(result);
        Assert.assertEquals("John Wayne", result.getName());
    }

    @Test
    public void whenNamedNativeQueryIsCalledUsingCreateNamedQuery_ThenOk() {
        Query<DeptEmployee> query = NamedQueryIntegrationTest.session.createNamedQuery("DeptEmployee_FindByEmployeeName", DeptEmployee.class);
        query.setParameter("name", "John Wayne");
        DeptEmployee result = query.getSingleResult();
        Assert.assertNotNull(result);
        Assert.assertEquals("001", result.getEmployeeNumber());
    }

    @Test
    public void whenNamedNativeQueryIsCalledUsingGetNamedNativeQuery_ThenOk() {
        @SuppressWarnings("rawtypes")
        NativeQuery query = NamedQueryIntegrationTest.session.getNamedNativeQuery("DeptEmployee_FindByEmployeeName");
        query.setParameter("name", "John Wayne");
        DeptEmployee result = ((DeptEmployee) (query.getSingleResult()));
        Assert.assertNotNull(result);
        Assert.assertEquals("001", result.getEmployeeNumber());
    }

    @Test
    public void whenUpdateQueryIsCalledWithCreateNamedQuery_ThenOk() {
        Query spQuery = NamedQueryIntegrationTest.session.createNamedQuery("DeptEmployee_UpdateEmployeeDepartment");
        spQuery.setParameter("employeeNo", "001");
        Department newDepartment = NamedQueryIntegrationTest.session.find(Department.class, purchaseDeptId);
        spQuery.setParameter("newDepartment", newDepartment);
        spQuery.executeUpdate();
        transaction.commit();
    }

    @Test
    public void whenNamedStoredProcedureIsCalledWithCreateNamedQuery_ThenOk() {
        Query spQuery = NamedQueryIntegrationTest.session.createNamedQuery("DeptEmployee_UpdateEmployeeDesignation");
        spQuery.setParameter("employeeNumber", "002");
        spQuery.setParameter("newDesignation", "Supervisor");
        spQuery.executeUpdate();
        transaction.commit();
    }
}

