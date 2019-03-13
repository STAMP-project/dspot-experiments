package com.baeldung.spring.cloud.springcloudcontractconsumer.controller;


import MediaType.APPLICATION_JSON;
import SpringBootTest.WebEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@AutoConfigureStubRunner(workOffline = true, ids = "com.baeldung.spring.cloud:spring-cloud-contract-producer:+:stubs:8090")
public class BasicMathControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void given_WhenPassEvenNumberInQueryParam_ThenReturnEven() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/calculate?number=2").contentType(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().string("Even"));
    }

    @Test
    public void given_WhenPassOddNumberInQueryParam_ThenReturnOdd() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/calculate?number=1").contentType(APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().string("Odd"));
    }
}

