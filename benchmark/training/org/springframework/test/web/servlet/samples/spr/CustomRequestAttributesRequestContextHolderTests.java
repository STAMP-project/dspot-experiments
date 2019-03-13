/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.test.web.servlet.samples.spr;


import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Integration tests for SPR-13211 which verify that a custom mock request
 * is not reused by MockMvc.
 *
 * @author Sam Brannen
 * @since 4.2
 * @see RequestContextHolderTests
 */
public class CustomRequestAttributesRequestContextHolderTests {
    private static final String FROM_CUSTOM_MOCK = "fromCustomMock";

    private static final String FROM_MVC_TEST_DEFAULT = "fromSpringMvcTestDefault";

    private static final String FROM_MVC_TEST_MOCK = "fromSpringMvcTestMock";

    private final GenericWebApplicationContext wac = new GenericWebApplicationContext();

    private MockMvc mockMvc;

    @Test
    public void singletonController() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/singletonController").requestAttr(CustomRequestAttributesRequestContextHolderTests.FROM_MVC_TEST_MOCK, CustomRequestAttributesRequestContextHolderTests.FROM_MVC_TEST_MOCK));
    }

    // -------------------------------------------------------------------
    @Configuration
    @EnableWebMvc
    static class WebConfig implements WebMvcConfigurer {
        @Bean
        public CustomRequestAttributesRequestContextHolderTests.SingletonController singletonController() {
            return new CustomRequestAttributesRequestContextHolderTests.SingletonController();
        }
    }

    @RestController
    private static class SingletonController {
        @RequestMapping("/singletonController")
        public void handle() {
            CustomRequestAttributesRequestContextHolderTests.assertRequestAttributes();
        }
    }
}

