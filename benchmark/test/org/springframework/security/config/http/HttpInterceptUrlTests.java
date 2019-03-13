/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.security.config.http;


import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ConfigurableWebApplicationContext;


public class HttpInterceptUrlTests {
    ConfigurableWebApplicationContext context;

    MockMvc mockMvc;

    @Test
    public void interceptUrlWhenRequestMatcherRefThenWorks() throws Exception {
        loadConfig("interceptUrlWhenRequestMatcherRefThenWorks.xml");
        mockMvc.perform(get("/foo")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/FOO")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/other")).andExpect(status().isOk());
    }

    @RestController
    static class FooController {
        @GetMapping("/*")
        String foo() {
            return "foo";
        }
    }
}

