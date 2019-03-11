package com.baeldung.client;


import com.baeldung.client.rest.RestClient;
import com.baeldung.server.model.Employee;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;


public class JerseyClientLiveTest {
    public static final int HTTP_CREATED = 201;

    private RestClient client = new RestClient();

    @Test
    public void givenCorrectObject_whenCorrectJsonRequest_thenResponseCodeCreated() {
        Employee emp = new Employee(6, "Johny");
        Response response = client.createJsonEmployee(emp);
        Assert.assertEquals(response.getStatus(), JerseyClientLiveTest.HTTP_CREATED);
    }

    @Test
    public void givenCorrectObject_whenCorrectXmlRequest_thenResponseCodeCreated() {
        Employee emp = new Employee(7, "Jacky");
        Response response = client.createXmlEmployee(emp);
        Assert.assertEquals(response.getStatus(), JerseyClientLiveTest.HTTP_CREATED);
    }

    @Test
    public void givenCorrectId_whenCorrectJsonRequest_thenCorrectEmployeeRetrieved() {
        int employeeId = 1;
        Employee emp = client.getJsonEmployee(employeeId);
        Assert.assertEquals(emp.getFirstName(), "Jane");
    }

    @Test
    public void givenCorrectId_whenCorrectXmlRequest_thenCorrectEmployeeRetrieved() {
        int employeeId = 1;
        Employee emp = client.getXmlEmployee(employeeId);
        Assert.assertEquals(emp.getFirstName(), "Jane");
    }
}

