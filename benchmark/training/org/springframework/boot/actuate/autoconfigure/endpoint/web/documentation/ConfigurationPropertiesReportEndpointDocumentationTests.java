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
import org.springframework.boot.actuate.context.properties.ConfigurationPropertiesReportEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;


/**
 * Tests for generating documentation describing
 * {@link ConfigurationPropertiesReportEndpoint}.
 *
 * @author Andy Wilkinson
 */
public class ConfigurationPropertiesReportEndpointDocumentationTests extends MockMvcEndpointDocumentationTests {
    @Test
    public void configProps() throws Exception {
        this.mockMvc.perform(get("/actuator/configprops")).andExpect(status().isOk()).andDo(MockMvcRestDocumentation.document("configprops", preprocessResponse(limit("contexts", getApplicationContext().getId(), "beans")), responseFields(fieldWithPath("contexts").description("Application contexts keyed by id."), fieldWithPath("contexts.*.beans.*").description("`@ConfigurationProperties` beans keyed by bean name."), fieldWithPath("contexts.*.beans.*.prefix").description("Prefix applied to the names of the bean's properties."), subsectionWithPath("contexts.*.beans.*.properties").description("Properties of the bean as name-value pairs."), parentIdField())));
    }

    @Configuration
    @Import(AbstractEndpointDocumentationTests.BaseDocumentationConfiguration.class)
    static class TestConfiguration {
        @Bean
        public ConfigurationPropertiesReportEndpoint endpoint() {
            return new ConfigurationPropertiesReportEndpoint();
        }
    }
}

