package org.baeldung.security.csrf;


import MediaType.APPLICATION_JSON;
import com.baeldung.thymeleaf.config.InitSecurity;
import com.baeldung.thymeleaf.config.WebApp;
import com.baeldung.thymeleaf.config.WebMVCConfig;
import com.baeldung.thymeleaf.config.WebMVCSecurity;
import javax.servlet.Filter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = { WebApp.class, WebMVCConfig.class, WebMVCSecurity.class, InitSecurity.class })
public class CsrfEnabledIntegrationTest {
    @Autowired
    WebApplicationContext wac;

    @Autowired
    MockHttpSession session;

    private MockMvc mockMvc;

    @Autowired
    private Filter springSecurityFilterChain;

    @Test
    public void addStudentWithoutCSRF() throws Exception {
        mockMvc.perform(post("/saveStudent").contentType(APPLICATION_JSON).param("id", "1234567").param("name", "Joe").param("gender", "M").with(testUser())).andExpect(status().isForbidden());
    }

    @Test
    public void addStudentWithCSRF() throws Exception {
        mockMvc.perform(post("/saveStudent").contentType(APPLICATION_JSON).param("id", "1234567").param("name", "Joe").param("gender", "M").with(testUser()).with(csrf())).andExpect(status().isOk());
    }

    @Test
    public void htmlInliningTest() throws Exception {
        mockMvc.perform(get("/html").with(testUser()).with(csrf())).andExpect(status().isOk()).andExpect(view().name("inliningExample.html"));
    }

    @Test
    public void jsInliningTest() throws Exception {
        mockMvc.perform(get("/js").with(testUser()).with(csrf())).andExpect(status().isOk()).andExpect(view().name("studentCheck.js"));
    }

    @Test
    public void plainInliningTest() throws Exception {
        mockMvc.perform(get("/plain").with(testUser()).with(csrf())).andExpect(status().isOk()).andExpect(view().name("studentsList.txt"));
    }
}

