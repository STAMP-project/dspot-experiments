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


import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;


/**
 * Tests for generating documentation describing {@link BeansEndpoint}.
 *
 * @author Andy Wilkinson
 */
public class BeansEndpointDocumentationTests extends MockMvcEndpointDocumentationTests {
    @Test
    public void beans() throws Exception {
        List<FieldDescriptor> beanFields = Arrays.asList(fieldWithPath("aliases").description("Names of any aliases."), fieldWithPath("scope").description("Scope of the bean."), fieldWithPath("type").description("Fully qualified type of the bean."), fieldWithPath("resource").description("Resource in which the bean was defined, if any.").optional(), fieldWithPath("dependencies").description("Names of any dependencies."));
        ResponseFieldsSnippet responseFields = responseFields(fieldWithPath("contexts").description("Application contexts keyed by id."), parentIdField(), fieldWithPath("contexts.*.beans").description("Beans in the application context keyed by name.")).andWithPrefix("contexts.*.beans.*.", beanFields);
        this.mockMvc.perform(get("/actuator/beans")).andExpect(status().isOk()).andDo(document("beans", preprocessResponse(limit(this::isIndependentBean, "contexts", getApplicationContext().getId(), "beans")), responseFields));
    }

    @Configuration
    @Import(AbstractEndpointDocumentationTests.BaseDocumentationConfiguration.class)
    static class TestConfiguration {
        @Bean
        public BeansEndpoint beansEndpoint(ConfigurableApplicationContext context) {
            return new BeansEndpoint(context);
        }
    }
}

