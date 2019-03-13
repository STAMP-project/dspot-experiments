package com.baeldung.hibernate;


import com.baeldung.hibernate.entities.DeptEmployee;
import com.baeldung.hibernate.pojo.Result;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.transform.Transformers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class CustomClassIntegrationTest {
    private Session session;

    private Transaction transaction;

    @Test
    public void whenAllManagersAreSelected_ThenObjectGraphIsReturned() {
        Query<DeptEmployee> query = session.createQuery("from com.baeldung.hibernate.entities.DeptEmployee");
        List<DeptEmployee> deptEmployees = query.list();
        DeptEmployee deptEmployee = deptEmployees.get(0);
        Assertions.assertEquals("John Smith", deptEmployee.getName());
        Assertions.assertEquals("Sales", deptEmployee.getDepartment().getName());
    }

    @Test
    public void whenIndividualPropertiesAreSelected_ThenObjectArrayIsReturned() {
        Query query = session.createQuery("select m.name, m.department.name from com.baeldung.hibernate.entities.DeptEmployee m");
        List managers = query.list();
        Object[] manager = ((Object[]) (managers.get(0)));
        Assertions.assertEquals("John Smith", manager[0]);
        Assertions.assertEquals("Sales", manager[1]);
    }

    @Test
    public void whenResultConstructorInSelect_ThenListOfResultIsReturned() {
        Query<Result> query = session.createQuery(("select new com.baeldung.hibernate.pojo.Result(m.name, m.department.name) " + "from DeptEmployee m"));
        List<Result> results = query.list();
        Result result = results.get(0);
        Assertions.assertEquals("John Smith", result.getEmployeeName());
        Assertions.assertEquals("Sales", result.getDepartmentName());
    }

    @Test
    public void whenResultTransformerOnQuery_ThenListOfResultIsReturned() {
        Query query = session.createQuery(("select m.name as employeeName, m.department.name as departmentName " + "from com.baeldung.hibernate.entities.DeptEmployee m"));
        query.setResultTransformer(Transformers.aliasToBean(Result.class));
        List<Result> results = query.list();
        Result result = results.get(0);
        Assertions.assertEquals("John Smith", result.getEmployeeName());
        Assertions.assertEquals("Sales", result.getDepartmentName());
    }
}

