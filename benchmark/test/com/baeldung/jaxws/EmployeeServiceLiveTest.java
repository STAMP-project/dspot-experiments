package com.baeldung.jaxws;


import com.baeldung.jaxws.client.Employee;
import com.baeldung.jaxws.client.EmployeeAlreadyExists_Exception;
import com.baeldung.jaxws.client.EmployeeNotFound_Exception;
import com.baeldung.jaxws.client.EmployeeService;
import java.net.URL;
import java.util.List;
import javax.xml.namespace.QName;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(Arquillian.class)
public class EmployeeServiceLiveTest {
    private static final String APP_NAME = "jee7";

    private static final String WSDL_PATH = "EmployeeService?wsdl";

    private static QName SERVICE_NAME = new QName("http://bottomup.server.jaxws.baeldung.com/", "EmployeeService");

    private static URL wsdlUrl;

    @ArquillianResource
    private URL deploymentUrl;

    private EmployeeService employeeServiceProxy;

    @Test
    public void givenEmployees_whenGetCount_thenCorrectNumberOfEmployeesReturned() {
        int employeeCount = employeeServiceProxy.countEmployees();
        List<Employee> employeeList = employeeServiceProxy.getAllEmployees();
        Assert.assertEquals(employeeList.size(), employeeCount);
    }

    @Test
    public void givenEmployees_whenGetAvailableEmployee_thenCorrectEmployeeReturned() throws EmployeeNotFound_Exception {
        Employee employee = employeeServiceProxy.getEmployee(2);
        Assert.assertEquals(employee.getFirstName(), "Jack");
    }

    @Test(expected = EmployeeNotFound_Exception.class)
    public void givenEmployees_whenGetNonAvailableEmployee_thenEmployeeNotFoundException() throws EmployeeNotFound_Exception {
        employeeServiceProxy.getEmployee(20);
    }

    @Test
    public void givenEmployees_whenAddNewEmployee_thenEmployeeCountIncreased() throws EmployeeAlreadyExists_Exception {
        int employeeCount = employeeServiceProxy.countEmployees();
        employeeServiceProxy.addEmployee(4, "Anna");
        Assert.assertEquals(employeeServiceProxy.countEmployees(), (employeeCount + 1));
    }

    @Test(expected = EmployeeAlreadyExists_Exception.class)
    public void givenEmployees_whenAddAlreadyExistingEmployee_thenEmployeeAlreadyExistsException() throws EmployeeAlreadyExists_Exception {
        employeeServiceProxy.addEmployee(1, "Anna");
    }

    @Test
    public void givenEmployees_whenUpdateExistingEmployee_thenUpdatedEmployeeReturned() throws EmployeeNotFound_Exception {
        Employee updated = employeeServiceProxy.updateEmployee(1, "Joan");
        Assert.assertEquals(updated.getFirstName(), "Joan");
    }

    @Test(expected = EmployeeNotFound_Exception.class)
    public void givenEmployees_whenUpdateNonExistingEmployee_thenEmployeeNotFoundException() throws EmployeeNotFound_Exception {
        employeeServiceProxy.updateEmployee(20, "Joan");
    }

    @Test
    public void givenEmployees_whenDeleteExistingEmployee_thenSuccessReturned() throws EmployeeNotFound_Exception {
        boolean deleteEmployee = employeeServiceProxy.deleteEmployee(3);
        Assert.assertEquals(deleteEmployee, true);
    }

    @Test(expected = EmployeeNotFound_Exception.class)
    public void givenEmployee_whenDeleteNonExistingEmployee_thenEmployeeNotFoundException() throws EmployeeNotFound_Exception {
        employeeServiceProxy.deleteEmployee(20);
    }
}

