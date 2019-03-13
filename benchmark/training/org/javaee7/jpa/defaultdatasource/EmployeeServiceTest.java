package org.javaee7.jpa.defaultdatasource;


import java.util.List;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;


/**
 *
 *
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmployeeServiceTest {
    @Inject
    EmployeeService employeeService;

    @Test
    public void T1_testGet() throws Exception {
        Assert.assertNotNull(employeeService);
        List<Employee> employees = employeeService.findAll();
        Assert.assertNotNull(employees);
        Assert.assertEquals(8, employees.size());
        Assert.assertFalse(employees.contains(new Employee("Penny")));
        Assert.assertFalse(employees.contains(new Employee("Sheldon")));
        Assert.assertFalse(employees.contains(new Employee("Amy")));
        Assert.assertFalse(employees.contains(new Employee("Leonard")));
        Assert.assertFalse(employees.contains(new Employee("Bernadette")));
        Assert.assertFalse(employees.contains(new Employee("Raj")));
        Assert.assertFalse(employees.contains(new Employee("Howard")));
        Assert.assertFalse(employees.contains(new Employee("Priya")));
    }

    @Test
    public void T2_testPersist() throws Exception {
        Employee newEmployee = new Employee("Reza");
        employeeService.persist(newEmployee);
        List<Employee> employees = employeeService.findAll();
        Assert.assertNotNull(employees);
        Assert.assertEquals(9, employees.size());
        boolean rezaInList = false;
        for (Employee employee : employees) {
            if (employee.getName().equals("Reza")) {
                rezaInList = true;
                break;
            }
        }
        Assert.assertTrue(rezaInList);
    }
}

