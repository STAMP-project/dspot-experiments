/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.test.web.client.samples;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Tests that use a {@link RestTemplate} configured with a
 * {@link MockMvcClientHttpRequestFactory} that is in turn configured with a
 * {@link MockMvc} instance that uses a {@link WebApplicationContext} loaded by
 * the TestContext framework.
 *
 * @author Rossen Stoyanchev
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration
public class MockMvcClientHttpRequestFactoryTests {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Test
    public void test() throws Exception {
        RestTemplate template = new RestTemplate(new org.springframework.test.web.client.MockMvcClientHttpRequestFactory(this.mockMvc));
        String result = template.getForObject("/foo", String.class);
        Assert.assertEquals("bar", result);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testAsyncTemplate() throws Exception {
        AsyncRestTemplate template = new AsyncRestTemplate(new org.springframework.test.web.client.MockMvcClientHttpRequestFactory(this.mockMvc));
        ListenableFuture<ResponseEntity<String>> entity = template.getForEntity("/foo", String.class);
        Assert.assertEquals("bar", entity.get().getBody());
    }

    @EnableWebMvc
    @Configuration
    @ComponentScan(basePackageClasses = MockMvcClientHttpRequestFactoryTests.class)
    static class MyWebConfig implements WebMvcConfigurer {}

    @Controller
    static class MyController {
        @RequestMapping(value = "/foo", method = RequestMethod.GET)
        @ResponseBody
        public String handle() {
            return "bar";
        }
    }
}

