package org.baeldung.spring43.scopeannotations;


import MediaType.ALL;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@ContextConfiguration(classes = ScopeAnnotationsConfiguration.class)
@WebAppConfiguration
public class ScopeAnnotationsIntegrationTest extends AbstractJUnit4SpringContextTests {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Test
    public void whenDifferentRequests_thenDifferentInstancesOfRequestScopedBeans() throws Exception {
        MockHttpSession session = new MockHttpSession();
        String requestScopedServiceInstanceNumber1 = this.mockMvc.perform(get("/appointments/request").session(session).accept(ALL)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String requestScopedServiceInstanceNumber2 = this.mockMvc.perform(get("/appointments/request").session(session).accept(ALL)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Assert.assertNotEquals(requestScopedServiceInstanceNumber1, requestScopedServiceInstanceNumber2);
    }

    @Test
    public void whenDifferentSessions_thenDifferentInstancesOfSessionScopedBeans() throws Exception {
        MockHttpSession session1 = new MockHttpSession();
        MockHttpSession session2 = new MockHttpSession();
        String sessionScopedServiceInstanceNumber1 = this.mockMvc.perform(get("/appointments/session").session(session1).accept(ALL)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String sessionScopedServiceInstanceNumber2 = this.mockMvc.perform(get("/appointments/session").session(session1).accept(ALL)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String sessionScopedServiceInstanceNumber3 = this.mockMvc.perform(get("/appointments/session").session(session2).accept(ALL)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Assert.assertEquals(sessionScopedServiceInstanceNumber1, sessionScopedServiceInstanceNumber2);
        Assert.assertNotEquals(sessionScopedServiceInstanceNumber1, sessionScopedServiceInstanceNumber3);
    }

    @Test
    public void whenDifferentSessionsAndRequests_thenAlwaysSingleApplicationScopedBean() throws Exception {
        MockHttpSession session1 = new MockHttpSession();
        MockHttpSession session2 = new MockHttpSession();
        String applicationScopedServiceInstanceNumber1 = this.mockMvc.perform(get("/appointments/application").session(session1).accept(ALL)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String applicationScopedServiceInstanceNumber2 = this.mockMvc.perform(get("/appointments/application").session(session2).accept(ALL)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        Assert.assertEquals(applicationScopedServiceInstanceNumber1, applicationScopedServiceInstanceNumber2);
    }
}

