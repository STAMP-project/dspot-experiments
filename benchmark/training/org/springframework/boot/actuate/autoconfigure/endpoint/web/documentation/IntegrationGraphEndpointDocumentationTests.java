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
import org.springframework.boot.actuate.integration.IntegrationGraphEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.graph.IntegrationGraphServer;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;


/**
 * Tests for generating documentation describing the {@link IntegrationGraphEndpoint}.
 *
 * @author Tim Ysewyn
 */
public class IntegrationGraphEndpointDocumentationTests extends MockMvcEndpointDocumentationTests {
    @Test
    public void graph() throws Exception {
        this.mockMvc.perform(get("/actuator/integrationgraph")).andExpect(status().isOk()).andDo(MockMvcRestDocumentation.document("integrationgraph/graph"));
    }

    @Test
    public void rebuild() throws Exception {
        this.mockMvc.perform(post("/actuator/integrationgraph")).andExpect(status().isNoContent()).andDo(MockMvcRestDocumentation.document("integrationgraph/rebuild"));
    }

    @Configuration
    @EnableIntegration
    @Import(AbstractEndpointDocumentationTests.BaseDocumentationConfiguration.class)
    static class TestConfiguration {
        @Bean
        public IntegrationGraphServer integrationGraphServer() {
            return new IntegrationGraphServer();
        }

        @Bean
        public IntegrationGraphEndpoint endpoint() {
            return new IntegrationGraphEndpoint(integrationGraphServer());
        }
    }
}

