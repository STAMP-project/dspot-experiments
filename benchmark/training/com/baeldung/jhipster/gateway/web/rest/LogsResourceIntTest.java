package com.baeldung.jhipster.gateway.web.rest;


import MediaType.APPLICATION_JSON_UTF8_VALUE;
import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import com.baeldung.jhipster.gateway.GatewayApp;
import com.baeldung.jhipster.gateway.config.SecurityBeanOverrideConfiguration;
import com.baeldung.jhipster.gateway.web.rest.vm.LoggerVM;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


/**
 * Test class for the LogsResource REST controller.
 *
 * @see LogsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SecurityBeanOverrideConfiguration.class, GatewayApp.class })
public class LogsResourceIntTest {
    private MockMvc restLogsMockMvc;

    @Test
    public void getAllLogs() throws Exception {
        restLogsMockMvc.perform(get("/management/logs")).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void changeLogs() throws Exception {
        LoggerVM logger = new LoggerVM();
        logger.setLevel("INFO");
        logger.setName("ROOT");
        restLogsMockMvc.perform(content(TestUtil.convertObjectToJsonBytes(logger))).andExpect(status().isNoContent());
    }

    @Test
    public void testLogstashAppender() {
        LoggerContext context = ((LoggerContext) (LoggerFactory.getILoggerFactory()));
        assertThat(context.getLogger("ROOT").getAppender("ASYNC_LOGSTASH")).isInstanceOf(AsyncAppender.class);
    }
}

