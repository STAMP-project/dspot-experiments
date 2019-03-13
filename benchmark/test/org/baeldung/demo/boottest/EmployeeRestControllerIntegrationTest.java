package org.baeldung.demo.boottest;


import MediaType.APPLICATION_JSON;
import java.io.IOException;
import java.util.List;
import org.baeldung.demo.DemoApplication;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


// @TestPropertySource(locations = "classpath:application-integrationtest.properties")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class EmployeeRestControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private EmployeeRepository repository;

    @Test
    public void whenValidInput_thenCreateEmployee() throws IOException, Exception {
        Employee bob = new Employee("bob");
        mvc.perform(content(JsonUtil.toJson(bob)));
        List<Employee> found = repository.findAll();
        assertThat(found).extracting(Employee::getName).containsOnly("bob");
    }

    @Test
    public void givenEmployees_whenGetEmployees_thenStatus200() throws Exception {
        createTestEmployee("bob");
        createTestEmployee("alex");
        // @formatter:off
        mvc.perform(get("/api/employees").contentType(APPLICATION_JSON)).andDo(print()).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON)).andExpect(jsonPath("$", Matchers.hasSize(Matchers.greaterThanOrEqualTo(2)))).andExpect(jsonPath("$[0].name", CoreMatchers.is("bob"))).andExpect(jsonPath("$[1].name", CoreMatchers.is("alex")));
        // @formatter:on
    }
}

