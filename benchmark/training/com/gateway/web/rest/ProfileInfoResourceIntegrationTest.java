package com.gateway.web.rest;


import JHipsterProperties.Ribbon;
import MediaType.APPLICATION_JSON_UTF8_VALUE;
import com.gateway.GatewayApp;
import io.github.jhipster.config.JHipsterProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


/**
 * Test class for the ProfileInfoResource REST controller.
 *
 * @see ProfileInfoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GatewayApp.class)
public class ProfileInfoResourceIntegrationTest {
    @Mock
    private Environment environment;

    @Mock
    private JHipsterProperties jHipsterProperties;

    private MockMvc restProfileMockMvc;

    @Test
    public void getProfileInfoWithRibbon() throws Exception {
        restProfileMockMvc.perform(get("/api/profile-info")).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void getProfileInfoWithoutRibbon() throws Exception {
        JHipsterProperties.Ribbon ribbon = new JHipsterProperties.Ribbon();
        ribbon.setDisplayOnActiveProfiles(null);
        Mockito.when(jHipsterProperties.getRibbon()).thenReturn(ribbon);
        restProfileMockMvc.perform(get("/api/profile-info")).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));
    }

    @Test
    public void getProfileInfoWithoutActiveProfiles() throws Exception {
        String[] emptyProfile = new String[]{  };
        Mockito.when(environment.getDefaultProfiles()).thenReturn(emptyProfile);
        Mockito.when(environment.getActiveProfiles()).thenReturn(emptyProfile);
        restProfileMockMvc.perform(get("/api/profile-info")).andExpect(status().isOk()).andExpect(content().contentType(APPLICATION_JSON_UTF8_VALUE));
    }
}

