package com.baeldung.spring.data.keyvalue.services.test;


import com.baeldung.spring.data.keyvalue.SpringDataKeyValueApplication;
import com.baeldung.spring.data.keyvalue.services.EmployeeService;
import com.baeldung.spring.data.keyvalue.vo.Employee;
import java.util.List;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.keyvalue.core.KeyValueTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringDataKeyValueApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmployeeServicesWithKeyValueRepositoryIntegrationTest {
    @Autowired
    @Qualifier("employeeServicesWithKeyValueTemplate")
    EmployeeService employeeService;

    @Autowired
    @Qualifier("keyValueTemplate")
    KeyValueTemplate keyValueTemplate;

    static Employee employee1;

    static Employee employee2;

    @Test
    public void test1_whenEmployeeSaved_thenEmployeeIsAddedToMap() {
        employeeService.save(EmployeeServicesWithKeyValueRepositoryIntegrationTest.employee1);
        Assert.assertEquals(keyValueTemplate.findById(1, Employee.class).get(), EmployeeServicesWithKeyValueRepositoryIntegrationTest.employee1);
    }

    @Test
    public void test2_whenEmployeeGet_thenEmployeeIsReturnedFromMap() {
        Employee employeeFetched = employeeService.get(1).get();
        Assert.assertEquals(employeeFetched, EmployeeServicesWithKeyValueRepositoryIntegrationTest.employee1);
    }

    @Test
    public void test3_whenEmployeesFetched_thenEmployeesAreReturnedFromMap() {
        List<Employee> employees = ((List<Employee>) (employeeService.fetchAll()));
        Assert.assertEquals(employees.size(), 1);
        Assert.assertEquals(employees.get(0), EmployeeServicesWithKeyValueRepositoryIntegrationTest.employee1);
    }

    @Test
    public void test4_whenEmployeeUpdated_thenEmployeeIsUpdatedToMap() {
        EmployeeServicesWithKeyValueRepositoryIntegrationTest.employee1.setName("Pawan");
        employeeService.update(EmployeeServicesWithKeyValueRepositoryIntegrationTest.employee1);
        Assert.assertEquals(keyValueTemplate.findById(1, Employee.class).get().getName(), "Pawan");
    }

    @Test
    public void test5_whenSortedEmployeesFetched_thenEmployeesAreReturnedFromMapInOrder() {
        employeeService.save(EmployeeServicesWithKeyValueRepositoryIntegrationTest.employee2);
        List<Employee> employees = ((List<Employee>) (employeeService.getSortedListOfEmployeesBySalary()));
        Assert.assertEquals(employees.size(), 2);
        Assert.assertEquals(employees.get(0), EmployeeServicesWithKeyValueRepositoryIntegrationTest.employee1);
        Assert.assertEquals(employees.get(1), EmployeeServicesWithKeyValueRepositoryIntegrationTest.employee2);
    }

    @Test
    public void test6_whenEmployeeDeleted_thenEmployeeIsRemovedMap() {
        employeeService.delete(1);
        Assert.assertEquals(keyValueTemplate.findById(1, Employee.class).isPresent(), false);
    }
}

