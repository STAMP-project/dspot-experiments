package com.baeldung.jhipster.quotes.web.rest.errors;


import ErrorConstants.ERR_CONCURRENCY_FAILURE;
import ErrorConstants.ERR_VALIDATION;
import MediaType.APPLICATION_JSON;
import MediaType.APPLICATION_PROBLEM_JSON;
import com.baeldung.jhipster.quotes.QuotesApp;
import com.baeldung.jhipster.quotes.config.SecurityBeanOverrideConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


/**
 * Test class for the ExceptionTranslator controller advice.
 *
 * @see ExceptionTranslator
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { SecurityBeanOverrideConfiguration.class, QuotesApp.class })
public class ExceptionTranslatorIntTest {
    @Autowired
    private ExceptionTranslatorTestController controller;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    private MockMvc mockMvc;

    @Test
    public void testConcurrencyFailure() throws Exception {
        mockMvc.perform(get("/test/concurrency-failure")).andExpect(status().isConflict()).andExpect(content().contentType(APPLICATION_PROBLEM_JSON)).andExpect(jsonPath("$.message").value(ERR_CONCURRENCY_FAILURE));
    }

    @Test
    public void testMethodArgumentNotValid() throws Exception {
        mockMvc.perform(post("/test/method-argument").content("{}").contentType(APPLICATION_JSON)).andExpect(status().isBadRequest()).andExpect(content().contentType(APPLICATION_PROBLEM_JSON)).andExpect(jsonPath("$.message").value(ERR_VALIDATION)).andExpect(jsonPath("$.fieldErrors.[0].objectName").value("testDTO")).andExpect(jsonPath("$.fieldErrors.[0].field").value("test")).andExpect(jsonPath("$.fieldErrors.[0].message").value("NotNull"));
    }

    @Test
    public void testParameterizedError() throws Exception {
        mockMvc.perform(get("/test/parameterized-error")).andExpect(status().isBadRequest()).andExpect(content().contentType(APPLICATION_PROBLEM_JSON)).andExpect(jsonPath("$.message").value("test parameterized error")).andExpect(jsonPath("$.params.param0").value("param0_value")).andExpect(jsonPath("$.params.param1").value("param1_value"));
    }

    @Test
    public void testParameterizedError2() throws Exception {
        mockMvc.perform(get("/test/parameterized-error2")).andExpect(status().isBadRequest()).andExpect(content().contentType(APPLICATION_PROBLEM_JSON)).andExpect(jsonPath("$.message").value("test parameterized error")).andExpect(jsonPath("$.params.foo").value("foo_value")).andExpect(jsonPath("$.params.bar").value("bar_value"));
    }

    @Test
    public void testMissingServletRequestPartException() throws Exception {
        mockMvc.perform(get("/test/missing-servlet-request-part")).andExpect(status().isBadRequest()).andExpect(content().contentType(APPLICATION_PROBLEM_JSON)).andExpect(jsonPath("$.message").value("error.http.400"));
    }

    @Test
    public void testMissingServletRequestParameterException() throws Exception {
        mockMvc.perform(get("/test/missing-servlet-request-parameter")).andExpect(status().isBadRequest()).andExpect(content().contentType(APPLICATION_PROBLEM_JSON)).andExpect(jsonPath("$.message").value("error.http.400"));
    }

    @Test
    public void testAccessDenied() throws Exception {
        mockMvc.perform(get("/test/access-denied")).andExpect(status().isForbidden()).andExpect(content().contentType(APPLICATION_PROBLEM_JSON)).andExpect(jsonPath("$.message").value("error.http.403")).andExpect(jsonPath("$.detail").value("test access denied!"));
    }

    @Test
    public void testUnauthorized() throws Exception {
        mockMvc.perform(get("/test/unauthorized")).andExpect(status().isUnauthorized()).andExpect(content().contentType(APPLICATION_PROBLEM_JSON)).andExpect(jsonPath("$.message").value("error.http.401")).andExpect(jsonPath("$.path").value("/test/unauthorized")).andExpect(jsonPath("$.detail").value("test authentication failed!"));
    }

    @Test
    public void testMethodNotSupported() throws Exception {
        mockMvc.perform(post("/test/access-denied")).andExpect(status().isMethodNotAllowed()).andExpect(content().contentType(APPLICATION_PROBLEM_JSON)).andExpect(jsonPath("$.message").value("error.http.405")).andExpect(jsonPath("$.detail").value("Request method 'POST' not supported"));
    }

    @Test
    public void testExceptionWithResponseStatus() throws Exception {
        mockMvc.perform(get("/test/response-status")).andExpect(status().isBadRequest()).andExpect(content().contentType(APPLICATION_PROBLEM_JSON)).andExpect(jsonPath("$.message").value("error.http.400")).andExpect(jsonPath("$.title").value("test response status"));
    }

    @Test
    public void testInternalServerError() throws Exception {
        mockMvc.perform(get("/test/internal-server-error")).andExpect(status().isInternalServerError()).andExpect(content().contentType(APPLICATION_PROBLEM_JSON)).andExpect(jsonPath("$.message").value("error.http.500")).andExpect(jsonPath("$.title").value("Internal Server Error"));
    }
}

