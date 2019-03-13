package com.baeldung.spring.data.keyvalue.services.test;


import com.baeldung.spring.data.keyvalue.SpringDataKeyValueApplication;
import com.baeldung.spring.data.keyvalue.repositories.EmployeeRepository;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringDataKeyValueApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmployeeServicesWithRepositoryIntegrationTest {
    @Autowired
    @Qualifier("employeeServicesWithRepository")
    EmployeeService employeeService;

    @Autowired
    EmployeeRepository employeeRepository;

    static Employee employee1;

    @Test
    public void test1_whenEmployeeSaved_thenEmployeeIsAddedToMap() {
        employeeService.save(EmployeeServicesWithRepositoryIntegrationTest.employee1);
        Assert.assertEquals(employeeRepository.findById(1).get(), EmployeeServicesWithRepositoryIntegrationTest.employee1);
    }

    @Test
    public void test2_whenEmployeeGet_thenEmployeeIsReturnedFromMap() {
        Employee employeeFetched = employeeService.get(1).get();
        Assert.assertEquals(employeeFetched, EmployeeServicesWithRepositoryIntegrationTest.employee1);
    }

    @Test
    public void test3_whenEmployeesFetched_thenEmployeesAreReturnedFromMap() {
        List<Employee> employees = ((List<Employee>) (employeeService.fetchAll()));
        Assert.assertEquals(employees.size(), 1);
        Assert.assertEquals(employees.get(0), EmployeeServicesWithRepositoryIntegrationTest.employee1);
    }

    @Test
    public void test4_whenEmployeeUpdated_thenEmployeeIsUpdatedToMap() {
        EmployeeServicesWithRepositoryIntegrationTest.employee1.setName("Pawan");
        employeeService.update(EmployeeServicesWithRepositoryIntegrationTest.employee1);
        Assert.assertEquals(employeeRepository.findById(1).get().getName(), "Pawan");
    }

    @Test
    public void test5_whenEmployeeDeleted_thenEmployeeIsRemovedMap() {
        employeeService.delete(1);
        Assert.assertEquals(employeeRepository.findById(1).isPresent(), false);
    }
}

