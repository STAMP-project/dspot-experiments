/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.test.web.servlet.htmlunit;


import TestGroup.PERFORMANCE;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.tests.Assume;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * Integration tests for {@link MockMvcWebClientBuilder}.
 *
 * @author Rob Winch
 * @author Sam Brannen
 * @author Rossen Stoyanchev
 * @since 4.2
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@WebAppConfiguration
public class MockMvcWebClientBuilderTests {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Test(expected = IllegalArgumentException.class)
    public void mockMvcSetupNull() {
        MockMvcWebClientBuilder.mockMvcSetup(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void webAppContextSetupNull() {
        MockMvcWebClientBuilder.webAppContextSetup(null);
    }

    @Test
    public void mockMvcSetupWithDefaultWebClientDelegate() throws Exception {
        WebClient client = MockMvcWebClientBuilder.mockMvcSetup(this.mockMvc).build();
        assertMockMvcUsed(client, "http://localhost/test");
        Assume.group(PERFORMANCE, () -> assertMockMvcNotUsed(client, "http://example.com/"));
    }

    @Test
    public void mockMvcSetupWithCustomWebClientDelegate() throws Exception {
        WebClient otherClient = new WebClient();
        WebClient client = MockMvcWebClientBuilder.mockMvcSetup(this.mockMvc).withDelegate(otherClient).build();
        assertMockMvcUsed(client, "http://localhost/test");
        Assume.group(PERFORMANCE, () -> assertMockMvcNotUsed(client, "http://example.com/"));
    }

    // SPR-14066
    @Test
    public void cookieManagerShared() throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new MockMvcWebClientBuilderTests.CookieController()).build();
        WebClient client = MockMvcWebClientBuilder.mockMvcSetup(this.mockMvc).build();
        Assert.assertThat(getResponse(client, "http://localhost/").getContentAsString(), CoreMatchers.equalTo("NA"));
        client.getCookieManager().addCookie(new Cookie("localhost", "cookie", "cookieManagerShared"));
        Assert.assertThat(getResponse(client, "http://localhost/").getContentAsString(), CoreMatchers.equalTo("cookieManagerShared"));
    }

    // SPR-14265
    @Test
    public void cookiesAreManaged() throws Exception {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new MockMvcWebClientBuilderTests.CookieController()).build();
        WebClient client = MockMvcWebClientBuilder.mockMvcSetup(this.mockMvc).build();
        Assert.assertThat(getResponse(client, "http://localhost/").getContentAsString(), CoreMatchers.equalTo("NA"));
        Assert.assertThat(postResponse(client, "http://localhost/?cookie=foo").getContentAsString(), CoreMatchers.equalTo("Set"));
        Assert.assertThat(getResponse(client, "http://localhost/").getContentAsString(), CoreMatchers.equalTo("foo"));
        Assert.assertThat(deleteResponse(client, "http://localhost/").getContentAsString(), CoreMatchers.equalTo("Delete"));
        Assert.assertThat(getResponse(client, "http://localhost/").getContentAsString(), CoreMatchers.equalTo("NA"));
    }

    @Configuration
    @EnableWebMvc
    static class Config {
        @RestController
        static class ContextPathController {
            @RequestMapping
            public String contextPath(HttpServletRequest request) {
                return "mvc";
            }
        }
    }

    @RestController
    static class CookieController {
        static final String COOKIE_NAME = "cookie";

        @RequestMapping(path = "/", produces = "text/plain")
        String cookie(@CookieValue(name = MockMvcWebClientBuilderTests.CookieController.COOKIE_NAME, defaultValue = "NA")
        String cookie) {
            return cookie;
        }

        @PostMapping(path = "/", produces = "text/plain")
        String setCookie(@RequestParam
        String cookie, HttpServletResponse response) {
            response.addCookie(new javax.servlet.http.Cookie(MockMvcWebClientBuilderTests.CookieController.COOKIE_NAME, cookie));
            return "Set";
        }

        @DeleteMapping(path = "/", produces = "text/plain")
        String deleteCookie(HttpServletResponse response) {
            javax.servlet.http.Cookie cookie = new javax.servlet.http.Cookie(MockMvcWebClientBuilderTests.CookieController.COOKIE_NAME, "");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
            return "Delete";
        }
    }
}

