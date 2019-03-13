package com.baeldung.ndc;


import MediaType.APPLICATION_JSON;
import com.baeldung.config.AppConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfiguration.class)
@WebAppConfiguration
public class NDCLogIntegrationTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Investment investment;

    @Test
    public void givenLog4jLogger_whenNDCAdded_thenResponseOkAndNDCInLog() throws Exception {
        mockMvc.perform(post("/ndc/log4j", investment).contentType(APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(investment))).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void givenLog4j2Logger_whenNDCAdded_thenResponseOkAndNDCInLog() throws Exception {
        mockMvc.perform(post("/ndc/log4j2", investment).contentType(APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(investment))).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void givenJBossLoggerBridge_whenNDCAdded_thenResponseOkAndNDCInLog() throws Exception {
        mockMvc.perform(post("/ndc/jboss-logging", investment).contentType(APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(investment))).andExpect(status().is2xxSuccessful());
    }
}

