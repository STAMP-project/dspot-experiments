package org.baeldung.spring43.attributeannotations;


import MediaType.ALL;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@ContextConfiguration(classes = AttributeAnnotationConfiguration.class)
@WebAppConfiguration
public class AttributeAnnotationIntegrationTest extends AbstractJUnit4SpringContextTests {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Test
    public void whenInterceptorAddsRequestAndSessionParams_thenParamsInjectedWithAttributesAnnotations() throws Exception {
        String result = this.mockMvc.perform(get("/test").accept(ALL)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Assert.assertEquals("login = john, query = invoices", result);
    }
}

