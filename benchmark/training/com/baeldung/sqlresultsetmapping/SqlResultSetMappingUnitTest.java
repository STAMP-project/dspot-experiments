package com.baeldung.sqlresultsetmapping;


import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import org.junit.Assert;
import org.junit.jupiter.api.Test;


public class SqlResultSetMappingUnitTest {
    private static EntityManager em;

    private static EntityManagerFactory emFactory;

    @Test
    public void whenNamedQuery_thenColumnResult() {
        List<Long> employeeIds = SqlResultSetMappingUnitTest.em.createNamedQuery("FridayEmployees").getResultList();
        Assert.assertEquals(2, employeeIds.size());
    }

    @Test
    public void whenNamedQuery_thenConstructorResult() {
        List<ScheduledDay> scheduleDays = Collections.checkedList(SqlResultSetMappingUnitTest.em.createNamedQuery("Schedules", ScheduledDay.class).getResultList(), ScheduledDay.class);
        Assert.assertEquals(2, scheduleDays.size());
        Assert.assertTrue(scheduleDays.stream().allMatch(( c) -> (c.getEmployeeId().longValue()) == 3));
    }

    @Test
    public void whenNamedQuery_thenSingleEntityResult() {
        List<Employee> employees = Collections.checkedList(SqlResultSetMappingUnitTest.em.createNamedQuery("Employees").getResultList(), Employee.class);
        Assert.assertEquals(3, employees.size());
        Assert.assertTrue(employees.stream().allMatch(( c) -> (c.getClass()) == (.class)));
    }

    @Test
    public void whenNamedQuery_thenMultipleEntityResult() {
        final Query query = SqlResultSetMappingUnitTest.em.createNativeQuery(("SELECT e.id, e.name, d.id, d.employeeId, d.dayOfWeek " + (" FROM employee e, schedule_days d " + " WHERE e.id = d.employeeId")), "EmployeeScheduleResults");
        List<Object[]> results = query.getResultList();
        Assert.assertEquals(4, results.size());
        Assert.assertTrue(((results.get(0).length) == 2));
        Employee emp = ((Employee) (results.get(1)[0]));
        ScheduledDay day = ((ScheduledDay) (results.get(1)[1]));
        Assert.assertTrue(((day.getEmployeeId()) == (emp.getId())));
    }
}

