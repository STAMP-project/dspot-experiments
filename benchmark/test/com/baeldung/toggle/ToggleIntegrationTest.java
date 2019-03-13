package com.baeldung.toggle;


import SpringBootTest.WebEnvironment;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = ToggleApplication.class)
@AutoConfigureMockMvc
public class ToggleIntegrationTest {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Test
    public void givenFeaturePropertyFalse_whenIncreaseSalary_thenNoIncrease() throws Exception {
        Employee emp = new Employee(1, 2000);
        employeeRepository.save(emp);
        System.setProperty("employee.feature", "false");
        mockMvc.perform(post("/increaseSalary").param("id", ((emp.getId()) + ""))).andExpect(status().is(200));
        emp = employeeRepository.findById(1L).orElse(null);
        Assert.assertEquals("salary incorrect", 2000, emp.getSalary(), 0.5);
    }

    @Test
    public void givenFeaturePropertyTrue_whenIncreaseSalary_thenIncrease() throws Exception {
        Employee emp = new Employee(1, 2000);
        employeeRepository.save(emp);
        System.setProperty("employee.feature", "true");
        mockMvc.perform(post("/increaseSalary").param("id", ((emp.getId()) + ""))).andExpect(status().is(200));
        emp = employeeRepository.findById(1L).orElse(null);
        Assert.assertEquals("salary incorrect", 2200, emp.getSalary(), 0.5);
    }
}

