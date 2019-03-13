/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.test.context;


import javax.servlet.ServletContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * Tests for {@link WebAppConfiguration} integration.
 *
 * @author Stephane Nicoll
 */
@RunWith(SpringRunner.class)
@DirtiesContext
@ContextConfiguration(loader = SpringBootContextLoader.class)
@WebAppConfiguration
public class SpringBootContextLoaderMockMvcTests {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ServletContext servletContext;

    private MockMvc mvc;

    @Test
    public void testMockHttpEndpoint() throws Exception {
        this.mvc.perform(get("/")).andExpect(status().isOk()).andExpect(content().string("Hello World"));
    }

    @Test
    public void validateWebApplicationContextIsSet() {
        assertThat(this.context).isSameAs(WebApplicationContextUtils.getWebApplicationContext(this.servletContext));
    }

    @Configuration
    @EnableWebMvc
    @RestController
    protected static class Config {
        @RequestMapping("/")
        public String home() {
            return "Hello World";
        }
    }
}

