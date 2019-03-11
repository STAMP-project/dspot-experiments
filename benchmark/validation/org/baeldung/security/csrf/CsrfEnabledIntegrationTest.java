package org.baeldung.security.csrf;


import MediaType.APPLICATION_JSON;
import org.baeldung.security.spring.SecurityWithCsrfConfig;
import org.baeldung.spring.MvcConfig;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(classes = { SecurityWithCsrfConfig.class, MvcConfig.class })
public class CsrfEnabledIntegrationTest extends CsrfAbstractIntegrationTest {
    @Test
    public void givenNoCsrf_whenAddFoo_thenForbidden() throws Exception {
        mvc.perform(post("/auth/foos").contentType(APPLICATION_JSON).content(createFoo()).with(testUser())).andExpect(status().isForbidden());
    }

    @Test
    public void givenCsrf_whenAddFoo_thenCreated() throws Exception {
        mvc.perform(post("/auth/foos").contentType(APPLICATION_JSON).content(createFoo()).with(testUser()).with(csrf())).andExpect(status().isCreated());
    }
}

