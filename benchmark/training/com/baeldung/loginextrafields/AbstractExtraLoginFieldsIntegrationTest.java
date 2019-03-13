package com.baeldung.loginextrafields;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


public abstract class AbstractExtraLoginFieldsIntegrationTest {
    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private WebApplicationContext wac;

    protected MockMvc mockMvc;

    @Test
    public void givenRootPathAccess_thenRedirectToIndex() throws Exception {
        this.mockMvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrlPattern("/index*"));
    }

    @Test
    public void givenSecuredResource_whenAccessUnauthenticated_thenRequiresAuthentication() throws Exception {
        this.mockMvc.perform(get("/user/index")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrlPattern("**/login"));
    }
}

