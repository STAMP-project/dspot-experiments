package com.baeldung;


import com.baeldung.extensions.EmployeeDaoParameterResolver;
import com.baeldung.extensions.EmployeeDatabaseSetupExtension;
import com.baeldung.extensions.EnvironmentExtension;
import com.baeldung.extensions.IgnoreFileNotFoundExceptionExtension;
import com.baeldung.extensions.LoggingExtension;
import com.baeldung.helpers.Employee;
import com.baeldung.helpers.EmployeeJdbcDao;
import java.sql.SQLException;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;


@ExtendWith({ EnvironmentExtension.class, EmployeeDatabaseSetupExtension.class, EmployeeDaoParameterResolver.class })
@ExtendWith(LoggingExtension.class)
@ExtendWith(IgnoreFileNotFoundExceptionExtension.class)
public class EmployeesUnitTest {
    private EmployeeJdbcDao employeeDao;

    private Logger logger;

    public EmployeesUnitTest(EmployeeJdbcDao employeeDao) {
        this.employeeDao = employeeDao;
    }

    @Test
    public void whenAddEmployee_thenGetEmployee() throws SQLException {
        Employee emp = new Employee(1, "john");
        employeeDao.add(emp);
        Assertions.assertEquals(1, employeeDao.findAll().size());
    }

    @Test
    public void whenGetEmployees_thenEmptyList() throws SQLException {
        Assertions.assertEquals(0, employeeDao.findAll().size());
    }
}

