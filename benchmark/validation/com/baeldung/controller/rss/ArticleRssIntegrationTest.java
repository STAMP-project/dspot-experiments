package com.baeldung.controller.rss;


import com.baeldung.spring.configuration.ApplicationConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


@SpringJUnitWebConfig(ApplicationConfiguration.class)
public class ArticleRssIntegrationTest {
    public static final String APPLICATION_RSS_XML = "application/rss+xml";

    public static final String APPLICATION_RSS_JSON = "application/rss+json";

    public static final String APPLICATION_RSS_XML_CHARSET_UTF_8 = "application/rss+xml;charset=UTF-8";

    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mockMvc;

    @Test
    public void whenRequestingXMLFeed_thenContentTypeIsOk() throws Exception {
        mockMvc.perform(get("/rss2").accept(ArticleRssIntegrationTest.APPLICATION_RSS_XML)).andExpect(status().isOk()).andExpect(content().contentType(ArticleRssIntegrationTest.APPLICATION_RSS_XML_CHARSET_UTF_8));
    }

    @Test
    public void whenRequestingJSONFeed_thenContentTypeIsOk() throws Exception {
        mockMvc.perform(get("/rss2").accept(ArticleRssIntegrationTest.APPLICATION_RSS_JSON)).andExpect(status().isOk()).andExpect(content().contentType(ArticleRssIntegrationTest.APPLICATION_RSS_JSON));
    }
}

