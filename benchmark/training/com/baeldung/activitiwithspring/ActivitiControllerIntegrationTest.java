package com.baeldung.activitiwithspring;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@SpringBootTest
public class ActivitiControllerIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(ActivitiControllerIntegrationTest.class);

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    RuntimeService runtimeService;

    @Test
    public void givenProcess_whenStartProcess_thenIncreaseInProcessInstanceCount() throws Exception {
        String responseBody = this.mockMvc.perform(MockMvcRequestBuilders.get("/start-process")).andReturn().getResponse().getContentAsString();
        Assert.assertEquals("Process started. Number of currently running process instances = 1", responseBody);
        responseBody = this.mockMvc.perform(MockMvcRequestBuilders.get("/start-process")).andReturn().getResponse().getContentAsString();
        Assert.assertEquals("Process started. Number of currently running process instances = 2", responseBody);
        responseBody = this.mockMvc.perform(MockMvcRequestBuilders.get("/start-process")).andReturn().getResponse().getContentAsString();
        Assert.assertEquals("Process started. Number of currently running process instances = 3", responseBody);
    }

    @Test
    public void givenProcess_whenProcessInstance_thenReceivedRunningTask() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/start-process")).andReturn().getResponse();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().orderByProcessInstanceId().desc().list().get(0);
        ActivitiControllerIntegrationTest.logger.info(("process instance = " + (pi.getId())));
        String responseBody = this.mockMvc.perform(MockMvcRequestBuilders.get(("/get-tasks/" + (pi.getId())))).andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        List<TaskRepresentation> tasks = Arrays.asList(mapper.readValue(responseBody, TaskRepresentation[].class));
        Assert.assertEquals(1, tasks.size());
        Assert.assertEquals("A", tasks.get(0).getName());
    }

    @Test
    public void givenProcess_whenCompleteTaskA_thenReceivedNextTask() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/start-process")).andReturn().getResponse();
        ProcessInstance pi = runtimeService.createProcessInstanceQuery().orderByProcessInstanceId().desc().list().get(0);
        ActivitiControllerIntegrationTest.logger.info(("process instance = " + (pi.getId())));
        this.mockMvc.perform(MockMvcRequestBuilders.get(("/complete-task-A/" + (pi.getId())))).andReturn().getResponse().getContentAsString();
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().list();
        Assert.assertEquals(0, list.size());
    }
}

