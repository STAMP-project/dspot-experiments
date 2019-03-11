package org.baeldung.web.service;


import MediaType.APPLICATION_JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import org.baeldung.SpringTestConfig;
import org.baeldung.web.model.Employee;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SpringTestConfig.class)
public class EmployeeServiceMockRestServiceServerUnitTest {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceMockRestServiceServerUnitTest.class);

    @Autowired
    private EmployeeService empService;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void givenMockingIsDoneByMockRestServiceServer_whenGetIsCalled_shouldReturnMockedObject() throws Exception {
        Employee emp = new Employee("E001", "Eric Simmons");
        mockServer.expect(ExpectedCount.once(), requestTo(new URI("http://localhost:8080/employee/E001"))).andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.OK).contentType(APPLICATION_JSON).body(mapper.writeValueAsString(emp)));
        Employee employee = empService.getEmployee("E001");
        mockServer.verify();
        Assert.assertEquals(emp, employee);
    }
}

