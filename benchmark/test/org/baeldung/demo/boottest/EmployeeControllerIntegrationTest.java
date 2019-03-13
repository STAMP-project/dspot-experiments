package org.baeldung.demo.boottest;


import MediaType.APPLICATION_JSON;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@WebMvcTest(EmployeeRestController.class)
public class EmployeeControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeeService service;

    @Test
    public void whenPostEmployee_thenCreateEmployee() throws Exception {
        Employee alex = new Employee("alex");
        BDDMockito.given(service.save(Mockito.any())).willReturn(alex);
        mvc.perform(post("/api/employees").contentType(APPLICATION_JSON).content(JsonUtil.toJson(alex))).andExpect(status().isCreated()).andExpect(jsonPath("$.name", CoreMatchers.is("alex")));
        Mockito.verify(service, VerificationModeFactory.times(1)).save(Mockito.any());
        Mockito.reset(service);
    }

    @Test
    public void givenEmployees_whenGetEmployees_thenReturnJsonArray() throws Exception {
        Employee alex = new Employee("alex");
        Employee john = new Employee("john");
        Employee bob = new Employee("bob");
        List<Employee> allEmployees = Arrays.asList(alex, john, bob);
        BDDMockito.given(service.getAllEmployees()).willReturn(allEmployees);
        mvc.perform(get("/api/employees").contentType(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("$", Matchers.hasSize(3))).andExpect(jsonPath("$[0].name", CoreMatchers.is(alex.getName()))).andExpect(jsonPath("$[1].name", CoreMatchers.is(john.getName()))).andExpect(jsonPath("$[2].name", CoreMatchers.is(bob.getName())));
        Mockito.verify(service, VerificationModeFactory.times(1)).getAllEmployees();
        Mockito.reset(service);
    }
}

