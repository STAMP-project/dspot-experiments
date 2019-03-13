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


import javax.sql.DataSource;
import org.junit.Test;
import org.springframework.boot.actuate.flyway.FlywayEndpoint;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;


/**
 * Tests for generating documentation describing the {@link FlywayEndpoint}.
 *
 * @author Andy Wilkinson
 */
public class FlywayEndpointDocumentationTests extends MockMvcEndpointDocumentationTests {
    @Test
    public void flyway() throws Exception {
        this.mockMvc.perform(get("/actuator/flyway")).andExpect(status().isOk()).andDo(MockMvcRestDocumentation.document("flyway", responseFields(fieldWithPath("contexts").description("Application contexts keyed by id"), fieldWithPath("contexts.*.flywayBeans.*.migrations").description(("Migrations performed by the Flyway instance, keyed by" + " Flyway bean name."))).andWithPrefix("contexts.*.flywayBeans.*.migrations.[].", migrationFieldDescriptors()).and(parentIdField())));
    }

    @Configuration
    @Import(AbstractEndpointDocumentationTests.BaseDocumentationConfiguration.class)
    @ImportAutoConfiguration(FlywayAutoConfiguration.class)
    static class TestConfiguration {
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder().generateUniqueName(true).setType(EmbeddedDatabaseConnection.get(getClass().getClassLoader()).getType()).build();
        }

        @Bean
        public FlywayEndpoint endpoint(ApplicationContext context) {
            return new FlywayEndpoint(context);
        }
    }
}

