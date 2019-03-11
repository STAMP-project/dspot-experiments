package com.baeldung.oauth2extractors;


import SpringBootTest.WebEnvironment;
import com.baeldung.oauth2extractors.configuration.SecurityConfig;
import javax.servlet.Filter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExtractorsApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { SecurityConfig.class })
@ActiveProfiles("oauth2-extractors-github")
public class ExtractorsUnitTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private Filter springSecurityFilterChain;

    private MockMvc mvc;

    @Test
    public void givenValidRequestWithoutAuthentication_shouldFailWith302() throws Exception {
        mvc.perform(get("/")).andExpect(status().isFound()).andReturn();
    }
}

