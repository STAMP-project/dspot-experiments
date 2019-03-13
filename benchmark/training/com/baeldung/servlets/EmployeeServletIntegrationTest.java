package com.baeldung.servlets;


import com.baeldung.model.Employee;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class EmployeeServletIntegrationTest {
    @Mock
    HttpServletRequest httpServletRequest;

    @Mock
    HttpServletResponse httpServletResponse;

    Employee employee;

    @Test
    public void whenPostRequestToEmployeeServlet_thenEmployeeReturnedAsJson() throws Exception {
        // Given
        int id = 1;
        String name = "Karan Khanna";
        String department = "IT";
        long salary = 5000;
        employee = new Employee(id, name, department, salary);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        Mockito.when(httpServletResponse.getWriter()).thenReturn(pw);
        EmployeeServlet employeeServlet = new EmployeeServlet();
        employeeServlet.doGet(httpServletRequest, httpServletResponse);
        String employeeJsonString = sw.getBuffer().toString().trim();
        Employee fetchedEmployee = new Gson().fromJson(employeeJsonString, Employee.class);
        Assert.assertEquals(fetchedEmployee, employee);
    }
}

