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
package org.springframework.boot.actuate.autoconfigure.endpoint.web.documentation;


import org.junit.Test;
import org.springframework.boot.actuate.context.ShutdownEndpoint;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;


/**
 * Tests for generating documentation describing the {@link ShutdownEndpoint}.
 *
 * @author Andy Wilkinson
 */
public class ShutdownEndpointDocumentationTests extends MockMvcEndpointDocumentationTests {
    @Test
    public void shutdown() throws Exception {
        this.mockMvc.perform(post("/actuator/shutdown")).andExpect(status().isOk()).andDo(MockMvcRestDocumentation.document("shutdown", responseFields(fieldWithPath("message").description("Message describing the result of the request."))));
    }

    @Configuration
    @Import(AbstractEndpointDocumentationTests.BaseDocumentationConfiguration.class)
    static class TestConfiguration {
        @Bean
        public ShutdownEndpoint endpoint(Environment environment) {
            ShutdownEndpoint endpoint = new ShutdownEndpoint();
            endpoint.setApplicationContext(new AnnotationConfigApplicationContext());
            return endpoint;
        }
    }
}

