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


import JsonFieldType.OBJECT;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.actuate.web.mappings.MappingDescriptionProvider;
import org.springframework.boot.actuate.web.mappings.MappingsEndpoint;
import org.springframework.boot.actuate.web.mappings.servlet.DispatcherServletsMappingDescriptionProvider;
import org.springframework.boot.actuate.web.mappings.servlet.FiltersMappingDescriptionProvider;
import org.springframework.boot.actuate.web.mappings.servlet.ServletsMappingDescriptionProvider;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Tests for generating documentation describing {@link MappingsEndpoint}.
 *
 * @author Andy Wilkinson
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class MappingsEndpointServletDocumentationTests extends AbstractEndpointDocumentationTests {
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @LocalServerPort
    private int port;

    private WebTestClient client;

    @Test
    public void mappings() throws Exception {
        ResponseFieldsSnippet commonResponseFields = responseFields(fieldWithPath("contexts").description("Application contexts keyed by id."), fieldWithPath("contexts.*.mappings").description("Mappings in the context, keyed by mapping type."), subsectionWithPath("contexts.*.mappings.dispatcherServlets").description("Dispatcher servlet mappings, if any."), subsectionWithPath("contexts.*.mappings.servletFilters").description("Servlet filter mappings, if any."), subsectionWithPath("contexts.*.mappings.servlets").description("Servlet mappings, if any."), subsectionWithPath("contexts.*.mappings.dispatcherHandlers").description("Dispatcher handler mappings, if any.").optional().type(OBJECT), parentIdField());
        List<FieldDescriptor> dispatcherServletFields = new java.util.ArrayList(Arrays.asList(fieldWithPath("*").description(("Dispatcher servlet mappings, if any, keyed by " + "dispatcher servlet bean name.")), fieldWithPath("*.[].details").optional().type(OBJECT).description(("Additional implementation-specific " + "details about the mapping. Optional.")), fieldWithPath("*.[].handler").description("Handler for the mapping."), fieldWithPath("*.[].predicate").description("Predicate for the mapping.")));
        List<FieldDescriptor> requestMappingConditions = Arrays.asList(requestMappingConditionField("").description("Details of the request mapping conditions.").optional(), requestMappingConditionField(".consumes").description("Details of the consumes condition"), requestMappingConditionField(".consumes.[].mediaType").description("Consumed media type."), requestMappingConditionField(".consumes.[].negated").description("Whether the media type is negated."), requestMappingConditionField(".headers").description("Details of the headers condition."), requestMappingConditionField(".headers.[].name").description("Name of the header."), requestMappingConditionField(".headers.[].value").description("Required value of the header, if any."), requestMappingConditionField(".headers.[].negated").description("Whether the value is negated."), requestMappingConditionField(".methods").description("HTTP methods that are handled."), requestMappingConditionField(".params").description("Details of the params condition."), requestMappingConditionField(".params.[].name").description("Name of the parameter."), requestMappingConditionField(".params.[].value").description("Required value of the parameter, if any."), requestMappingConditionField(".params.[].negated").description("Whether the value is negated."), requestMappingConditionField(".patterns").description("Patterns identifying the paths handled by the mapping."), requestMappingConditionField(".produces").description("Details of the produces condition."), requestMappingConditionField(".produces.[].mediaType").description("Produced media type."), requestMappingConditionField(".produces.[].negated").description("Whether the media type is negated."));
        List<FieldDescriptor> handlerMethod = Arrays.asList(fieldWithPath("*.[].details.handlerMethod").optional().type(OBJECT).description(("Details of the method, if any, " + "that will handle requests to this mapping.")), fieldWithPath("*.[].details.handlerMethod.className").description("Fully qualified name of the class of the method."), fieldWithPath("*.[].details.handlerMethod.name").description("Name of the method."), fieldWithPath("*.[].details.handlerMethod.descriptor").description(("Descriptor of the method as specified in the Java " + "Language Specification.")));
        dispatcherServletFields.addAll(handlerMethod);
        dispatcherServletFields.addAll(requestMappingConditions);
        this.client.get().uri("/actuator/mappings").exchange().expectBody().consumeWith(document("mappings", commonResponseFields, responseFields(beneathPath("contexts.*.mappings.dispatcherServlets").withSubsectionId("dispatcher-servlets"), dispatcherServletFields), responseFields(beneathPath("contexts.*.mappings.servletFilters").withSubsectionId("servlet-filters"), fieldWithPath("[].servletNameMappings").description("Names of the servlets to which the filter is mapped."), fieldWithPath("[].urlPatternMappings").description("URL pattern to which the filter is mapped."), fieldWithPath("[].name").description("Name of the filter."), fieldWithPath("[].className").description("Class name of the filter")), responseFields(beneathPath("contexts.*.mappings.servlets").withSubsectionId("servlets"), fieldWithPath("[].mappings").description("Mappings of the servlet."), fieldWithPath("[].name").description("Name of the servlet."), fieldWithPath("[].className").description("Class name of the servlet"))));
    }

    @Configuration
    @Import(AbstractEndpointDocumentationTests.BaseDocumentationConfiguration.class)
    static class TestConfiguration {
        @Bean
        public TomcatServletWebServerFactory tomcat() {
            return new TomcatServletWebServerFactory(0);
        }

        @Bean
        public DispatcherServletsMappingDescriptionProvider dispatcherServletsMappingDescriptionProvider() {
            return new DispatcherServletsMappingDescriptionProvider();
        }

        @Bean
        public ServletsMappingDescriptionProvider servletsMappingDescriptionProvider() {
            return new ServletsMappingDescriptionProvider();
        }

        @Bean
        public FiltersMappingDescriptionProvider filtersMappingDescriptionProvider() {
            return new FiltersMappingDescriptionProvider();
        }

        @Bean
        public MappingsEndpoint mappingsEndpoint(Collection<MappingDescriptionProvider> descriptionProviders, ConfigurableApplicationContext context) {
            return new MappingsEndpoint(descriptionProviders, context);
        }

        @Bean
        public MappingsEndpointServletDocumentationTests.ExampleController exampleController() {
            return new MappingsEndpointServletDocumentationTests.ExampleController();
        }
    }

    @RestController
    private static class ExampleController {
        @PostMapping(path = "/", consumes = { MediaType.APPLICATION_JSON_VALUE, "!application/xml" }, produces = MediaType.TEXT_PLAIN_VALUE, headers = "X-Custom=Foo", params = "a!=alpha")
        public String example() {
            return "Hello World";
        }
    }
}

