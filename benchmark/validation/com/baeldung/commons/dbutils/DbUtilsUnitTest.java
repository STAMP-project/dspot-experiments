package com.baeldung.commons.dbutils;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.commons.dbutils.AsyncQueryRunner;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.Assert;
import org.junit.Test;


public class DbUtilsUnitTest {
    private Connection connection;

    @Test
    public void givenResultHandler_whenExecutingQuery_thenExpectedList() throws SQLException {
        MapListHandler beanListHandler = new MapListHandler();
        QueryRunner runner = new QueryRunner();
        List<Map<String, Object>> list = runner.query(connection, "SELECT * FROM employee", beanListHandler);
        Assert.assertEquals(list.size(), 5);
        Assert.assertEquals(list.get(0).get("firstname"), "John");
        Assert.assertEquals(list.get(4).get("firstname"), "Christian");
    }

    @Test
    public void givenResultHandler_whenExecutingQuery_thenEmployeeList() throws SQLException {
        BeanListHandler<Employee> beanListHandler = new BeanListHandler(Employee.class);
        QueryRunner runner = new QueryRunner();
        List<Employee> employeeList = runner.query(connection, "SELECT * FROM employee", beanListHandler);
        Assert.assertEquals(employeeList.size(), 5);
        Assert.assertEquals(employeeList.get(0).getFirstName(), "John");
        Assert.assertEquals(employeeList.get(4).getFirstName(), "Christian");
    }

    @Test
    public void givenResultHandler_whenExecutingQuery_thenExpectedScalar() throws SQLException {
        ScalarHandler<Long> scalarHandler = new ScalarHandler();
        QueryRunner runner = new QueryRunner();
        String query = "SELECT COUNT(*) FROM employee";
        long count = runner.query(connection, query, scalarHandler);
        Assert.assertEquals(count, 5);
    }

    @Test
    public void givenResultHandler_whenExecutingQuery_thenEmailsSetted() throws SQLException {
        EmployeeHandler employeeHandler = new EmployeeHandler(connection);
        QueryRunner runner = new QueryRunner();
        List<Employee> employees = runner.query(connection, "SELECT * FROM employee", employeeHandler);
        Assert.assertEquals(employees.get(0).getEmails().size(), 2);
        Assert.assertEquals(employees.get(2).getEmails().size(), 3);
        Assert.assertNotNull(employees.get(0).getEmails().get(0).getEmployeeId());
    }

    @Test
    public void givenResultHandler_whenExecutingQuery_thenAllPropertiesSetted() throws SQLException {
        EmployeeHandler employeeHandler = new EmployeeHandler(connection);
        QueryRunner runner = new QueryRunner();
        String query = "SELECT * FROM employee_legacy";
        List<Employee> employees = runner.query(connection, query, employeeHandler);
        Assert.assertEquals(((int) (employees.get(0).getId())), 1);
        Assert.assertEquals(employees.get(0).getFirstName(), "John");
    }

    @Test
    public void whenInserting_thenInserted() throws SQLException {
        QueryRunner runner = new QueryRunner();
        String insertSQL = "INSERT INTO employee (firstname,lastname,salary, hireddate) VALUES (?, ?, ?, ?)";
        int numRowsInserted = runner.update(connection, insertSQL, "Leia", "Kane", 60000.6, new Date());
        Assert.assertEquals(numRowsInserted, 1);
    }

    @Test
    public void givenHandler_whenInserting_thenExpectedId() throws SQLException {
        ScalarHandler<Integer> scalarHandler = new ScalarHandler();
        QueryRunner runner = new QueryRunner();
        String insertSQL = "INSERT INTO employee (firstname,lastname,salary, hireddate) VALUES (?, ?, ?, ?)";
        int newId = runner.insert(connection, insertSQL, scalarHandler, "Jenny", "Medici", 60000.6, new Date());
        Assert.assertEquals(newId, 6);
    }

    @Test
    public void givenSalary_whenUpdating_thenUpdated() throws SQLException {
        double salary = 35000;
        QueryRunner runner = new QueryRunner();
        String updateSQL = "UPDATE employee SET salary = salary * 1.1 WHERE salary <= ?";
        int numRowsUpdated = runner.update(connection, updateSQL, salary);
        Assert.assertEquals(numRowsUpdated, 3);
    }

    @Test
    public void whenDeletingRecord_thenDeleted() throws SQLException {
        QueryRunner runner = new QueryRunner();
        String deleteSQL = "DELETE FROM employee WHERE id = ?";
        int numRowsDeleted = runner.update(connection, deleteSQL, 3);
        Assert.assertEquals(numRowsDeleted, 1);
    }

    @Test
    public void givenAsyncRunner_whenExecutingQuery_thenExpectedList() throws Exception {
        AsyncQueryRunner runner = new AsyncQueryRunner(Executors.newCachedThreadPool());
        EmployeeHandler employeeHandler = new EmployeeHandler(connection);
        String query = "SELECT * FROM employee";
        Future<List<Employee>> future = runner.query(connection, query, employeeHandler);
        List<Employee> employeeList = future.get(10, TimeUnit.SECONDS);
        Assert.assertEquals(employeeList.size(), 5);
    }
}

