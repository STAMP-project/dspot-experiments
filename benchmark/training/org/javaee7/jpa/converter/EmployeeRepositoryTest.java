package org.javaee7.jpa.converter;


import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class EmployeeRepositoryTest {
    @Inject
    private EmployeeRepository repository;

    @Test
    public void should_return_all_employee_records() throws Exception {
        // When
        final List<Employee> actualEmployees = repository.all();
        // Then
        Assert.assertTrue(((actualEmployees.size()) == 6));
        List<Employee> expectedEmployees = Arrays.asList(EmployeeRepositoryTest.employee("Leonard", "11-22-33-44"), EmployeeRepositoryTest.employee("Sheldon", "22-33-44-55"), EmployeeRepositoryTest.employee("Penny", "33-44-55-66"), EmployeeRepositoryTest.employee("Raj", "44-55-66-77"), EmployeeRepositoryTest.employee("Howard", "55-66-77-88"), EmployeeRepositoryTest.employee("Bernadette", "66-77-88-99"));
        for (Employee employee : expectedEmployees) {
            Assert.assertTrue(actualEmployees.contains(employee));
        }
    }
}

