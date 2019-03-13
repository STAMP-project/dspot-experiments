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
import org.springframework.boot.actuate.liquibase.LiquibaseEndpoint;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;


/**
 * Tests for generating documentation describing the {@link LiquibaseEndpoint}.
 *
 * @author Andy Wilkinson
 */
public class LiquibaseEndpointDocumentationTests extends MockMvcEndpointDocumentationTests {
    @Test
    public void liquibase() throws Exception {
        FieldDescriptor changeSetsField = fieldWithPath("contexts.*.liquibaseBeans.*.changeSets").description(("Change sets made by the Liquibase beans, keyed by " + "bean name."));
        this.mockMvc.perform(get("/actuator/liquibase")).andExpect(status().isOk()).andDo(MockMvcRestDocumentation.document("liquibase", responseFields(fieldWithPath("contexts").description("Application contexts keyed by id"), changeSetsField).andWithPrefix("contexts.*.liquibaseBeans.*.changeSets[].", getChangeSetFieldDescriptors()).and(parentIdField())));
    }

    @Configuration
    @Import({ AbstractEndpointDocumentationTests.BaseDocumentationConfiguration.class, EmbeddedDataSourceConfiguration.class, LiquibaseAutoConfiguration.class })
    static class TestConfiguration {
        @Bean
        public LiquibaseEndpoint endpoint(ApplicationContext context) {
            return new LiquibaseEndpoint(context);
        }
    }
}

