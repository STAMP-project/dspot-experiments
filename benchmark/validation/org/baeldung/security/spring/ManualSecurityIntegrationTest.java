package org.baeldung.security.spring;


import javax.servlet.http.HttpSession;
import org.baeldung.spring.MvcConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { MvcConfig.class, ManualSecurityConfig.class })
public class ManualSecurityIntegrationTest {
    @Autowired
    WebApplicationContext wac;

    private MockMvc mockMvc;

    /**
     * Execute custom login and access the endpoint
     */
    @Test
    public void whenLoginIsSuccessFulThenEndpointCanBeAccessedAndCurrentUserPrinted() throws Exception {
        mockMvc.perform(get("/custom/print")).andExpect(status().isUnauthorized());
        HttpSession session = mockMvc.perform(post("/custom/login").param("username", "user1").param("password", "user1Pass")).andExpect(status().isOk()).andReturn().getRequest().getSession();
        mockMvc.perform(get("/custom/print").session(((org.springframework.mock.web.MockHttpSession) (session)))).andExpect(status().is2xxSuccessful());
    }
}

