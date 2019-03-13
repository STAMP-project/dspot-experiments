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


import java.util.regex.Pattern;
import org.junit.Test;
import org.springframework.boot.actuate.env.EnvironmentEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.TestPropertySource;


/**
 * Tests for generating documentation describing the {@link EnvironmentEndpoint}.
 *
 * @author Andy Wilkinson
 */
@TestPropertySource(properties = "spring.config.location=classpath:/org/springframework/boot/actuate/autoconfigure/endpoint/web/documentation/")
public class EnvironmentEndpointDocumentationTests extends MockMvcEndpointDocumentationTests {
    private static final FieldDescriptor activeProfiles = fieldWithPath("activeProfiles").description("Names of the active profiles, if any.");

    private static final FieldDescriptor propertySources = fieldWithPath("propertySources").description("Property sources in order of precedence.");

    private static final FieldDescriptor propertySourceName = fieldWithPath("propertySources.[].name").description("Name of the property source.");

    @Test
    public void env() throws Exception {
        this.mockMvc.perform(get("/actuator/env")).andExpect(status().isOk()).andDo(document("env/all", preprocessResponse(replacePattern(Pattern.compile("org/springframework/boot/actuate/autoconfigure/endpoint/web/documentation/"), ""), filterProperties()), responseFields(EnvironmentEndpointDocumentationTests.activeProfiles, EnvironmentEndpointDocumentationTests.propertySources, EnvironmentEndpointDocumentationTests.propertySourceName, fieldWithPath("propertySources.[].properties").description("Properties in the property source keyed by property name."), fieldWithPath("propertySources.[].properties.*.value").description("Value of the property."), fieldWithPath("propertySources.[].properties.*.origin").description("Origin of the property, if any.").optional())));
    }

    @Test
    public void singlePropertyFromEnv() throws Exception {
        this.mockMvc.perform(get("/actuator/env/com.example.cache.max-size")).andExpect(status().isOk()).andDo(document("env/single", preprocessResponse(replacePattern(Pattern.compile("org/springframework/boot/actuate/autoconfigure/endpoint/web/documentation/"), "")), responseFields(fieldWithPath("property").description("Property from the environment, if found.").optional(), fieldWithPath("property.source").description("Name of the source of the property."), fieldWithPath("property.value").description("Value of the property."), EnvironmentEndpointDocumentationTests.activeProfiles, EnvironmentEndpointDocumentationTests.propertySources, EnvironmentEndpointDocumentationTests.propertySourceName, fieldWithPath("propertySources.[].property").description("Property in the property source, if any.").optional(), fieldWithPath("propertySources.[].property.value").description("Value of the property."), fieldWithPath("propertySources.[].property.origin").description("Origin of the property, if any.").optional())));
    }

    @Configuration
    @Import(AbstractEndpointDocumentationTests.BaseDocumentationConfiguration.class)
    static class TestConfiguration {
        @Bean
        public EnvironmentEndpoint endpoint(ConfigurableEnvironment environment) {
            return new EnvironmentEndpoint(new AbstractEnvironment() {
                @Override
                protected void customizePropertySources(MutablePropertySources propertySources) {
                    environment.getPropertySources().stream().filter(this::includedPropertySource).forEach(propertySources::addLast);
                }

                private boolean includedPropertySource(PropertySource<?> propertySource) {
                    return (propertySource instanceof EnumerablePropertySource) && (!("Inlined Test Properties".equals(propertySource.getName())));
                }
            });
        }
    }
}

