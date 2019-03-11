package com.baeldung.web.controller.redirect;


import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/api-servlet.xml")
@WebAppConfiguration
public class RedirectControllerIntegrationTest {
    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Test
    public void whenRedirectOnUrlWithUsingXMLConfig_thenStatusRedirectionAndRedirectedOnUrl() throws Exception {
        mockMvc.perform(get("/redirectWithXMLConfig")).andExpect(status().is3xxRedirection()).andExpect(view().name("RedirectedUrl")).andExpect(model().attribute("attribute", CoreMatchers.equalTo("redirectWithXMLConfig"))).andExpect(redirectedUrl("redirectedUrl?attribute=redirectWithXMLConfig"));
    }

    @Test
    public void whenRedirectOnUrlWithUsingRedirectPrefix_thenStatusRedirectionAndRedirectedOnUrl() throws Exception {
        mockMvc.perform(get("/redirectWithRedirectPrefix")).andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/redirectedUrl")).andExpect(model().attribute("attribute", CoreMatchers.equalTo("redirectWithRedirectPrefix"))).andExpect(redirectedUrl("/redirectedUrl?attribute=redirectWithRedirectPrefix"));
    }

    @Test
    public void whenRedirectOnUrlWithUsingRedirectAttributes_thenStatusRedirectionAndRedirectedOnUrlAndAddedAttributeToFlashScope() throws Exception {
        mockMvc.perform(get("/redirectWithRedirectAttributes")).andExpect(status().is3xxRedirection()).andExpect(flash().attribute("flashAttribute", CoreMatchers.equalTo("redirectWithRedirectAttributes"))).andExpect(model().attribute("attribute", CoreMatchers.equalTo("redirectWithRedirectAttributes"))).andExpect(model().attribute("flashAttribute", CoreMatchers.equalTo(null))).andExpect(redirectedUrl("redirectedUrl?attribute=redirectWithRedirectAttributes"));
    }

    @Test
    public void whenRedirectOnUrlWithUsingRedirectView_thenStatusRedirectionAndRedirectedOnUrlAndAddedAttributeToFlashScope() throws Exception {
        mockMvc.perform(get("/redirectWithRedirectView")).andExpect(status().is3xxRedirection()).andExpect(model().attribute("attribute", CoreMatchers.equalTo("redirectWithRedirectView"))).andExpect(redirectedUrl("redirectedUrl?attribute=redirectWithRedirectView"));
    }

    @Test
    public void whenRedirectOnUrlWithUsingForwardPrefix_thenStatusOkAndForwardedOnUrl() throws Exception {
        mockMvc.perform(get("/forwardWithForwardPrefix")).andExpect(status().isOk()).andExpect(view().name("forward:/redirectedUrl")).andExpect(model().attribute("attribute", CoreMatchers.equalTo("redirectWithForwardPrefix"))).andExpect(forwardedUrl("/redirectedUrl"));
    }
}

