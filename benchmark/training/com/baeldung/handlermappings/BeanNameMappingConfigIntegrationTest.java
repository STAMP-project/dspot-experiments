package com.baeldung.handlermappings;


import com.baeldung.config.BeanNameUrlHandlerMappingConfig;
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
@ContextConfiguration(classes = BeanNameUrlHandlerMappingConfig.class)
public class BeanNameMappingConfigIntegrationTest {
    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mockMvc;

    @Test
    public void whenBeanNameMapping_thenMappedOK() throws Exception {
        mockMvc.perform(get("/beanNameUrl")).andExpect(status().isOk()).andExpect(view().name("welcome")).andDo(print());
    }
}

