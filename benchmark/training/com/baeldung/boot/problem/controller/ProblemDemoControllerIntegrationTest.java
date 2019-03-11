package com.baeldung.boot.problem.controller;


import MediaType.APPLICATION_JSON_VALUE;
import MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import SpringBootTest.WebEnvironment;
import com.baeldung.boot.problem.SpringProblemApplication;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = SpringProblemApplication.class)
@AutoConfigureMockMvc
public class ProblemDemoControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenRequestingAllTasks_thenReturnSuccessfulResponseWithArrayWithTwoTasks() throws Exception {
        mockMvc.perform(get("/tasks").contentType(APPLICATION_JSON_VALUE)).andDo(print()).andExpect(jsonPath("$.length()", CoreMatchers.equalTo(2))).andExpect(status().isOk());
    }

    @Test
    public void whenRequestingExistingTask_thenReturnSuccessfulResponse() throws Exception {
        mockMvc.perform(get("/tasks/1").contentType(APPLICATION_JSON_VALUE)).andDo(print()).andExpect(jsonPath("$.id", CoreMatchers.equalTo(1))).andExpect(status().isOk());
    }

    @Test
    public void whenRequestingMissingTask_thenReturnNotFoundProblemResponse() throws Exception {
        mockMvc.perform(get("/tasks/5").contentType(APPLICATION_PROBLEM_JSON_VALUE)).andDo(print()).andExpect(jsonPath("$.title", CoreMatchers.equalTo("Not found"))).andExpect(jsonPath("$.status", CoreMatchers.equalTo(404))).andExpect(jsonPath("$.detail", CoreMatchers.equalTo("Task '5' not found"))).andExpect(status().isNotFound());
    }

    @Test
    public void whenMakePutCall_thenReturnNotImplementedProblemResponse() throws Exception {
        mockMvc.perform(put("/tasks/1").contentType(APPLICATION_PROBLEM_JSON_VALUE)).andDo(print()).andExpect(jsonPath("$.title", CoreMatchers.equalTo("Not Implemented"))).andExpect(jsonPath("$.status", CoreMatchers.equalTo(501))).andExpect(status().isNotImplemented());
    }

    @Test
    public void whenMakeDeleteCall_thenReturnForbiddenProblemResponse() throws Exception {
        mockMvc.perform(delete("/tasks/2").contentType(APPLICATION_PROBLEM_JSON_VALUE)).andDo(print()).andExpect(jsonPath("$.title", CoreMatchers.equalTo("Forbidden"))).andExpect(jsonPath("$.status", CoreMatchers.equalTo(403))).andExpect(jsonPath("$.detail", CoreMatchers.equalTo("You can't delete this task"))).andExpect(status().isForbidden());
    }

    @Test
    public void whenMakeGetCallWithInvalidIdFormat_thenReturnBadRequestResponseWithStackTrace() throws Exception {
        mockMvc.perform(get("/tasks/invalid-id").contentType(APPLICATION_PROBLEM_JSON_VALUE)).andDo(print()).andExpect(jsonPath("$.title", CoreMatchers.equalTo("Bad Request"))).andExpect(jsonPath("$.status", CoreMatchers.equalTo(400))).andExpect(jsonPath("$.stacktrace", CoreMatchers.notNullValue())).andExpect(status().isBadRequest());
    }

    @Test
    public void whenMakeGetCallWithInvalidIdFormat_thenReturnBadRequestResponseWithCause() throws Exception {
        mockMvc.perform(get("/tasks/invalid-id").contentType(APPLICATION_PROBLEM_JSON_VALUE)).andDo(print()).andExpect(jsonPath("$.title", CoreMatchers.equalTo("Bad Request"))).andExpect(jsonPath("$.status", CoreMatchers.equalTo(400))).andExpect(jsonPath("$.cause", CoreMatchers.notNullValue())).andExpect(jsonPath("$.cause.title", CoreMatchers.equalTo("Internal Server Error"))).andExpect(jsonPath("$.cause.status", CoreMatchers.equalTo(500))).andExpect(jsonPath("$.cause.detail", CoreMatchers.containsString("For input string:"))).andExpect(jsonPath("$.cause.stacktrace", CoreMatchers.notNullValue())).andExpect(status().isBadRequest());
    }
}

