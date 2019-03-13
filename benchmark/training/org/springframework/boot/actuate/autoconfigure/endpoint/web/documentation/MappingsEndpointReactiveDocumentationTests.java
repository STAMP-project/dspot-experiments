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
import JsonFieldType.STRING;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.actuate.web.mappings.MappingDescriptionProvider;
import org.springframework.boot.actuate.web.mappings.MappingsEndpoint;
import org.springframework.boot.actuate.web.mappings.reactive.DispatcherHandlersMappingDescriptionProvider;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


/**
 * Tests for generating documentation describing {@link MappingsEndpoint}.
 *
 * @author Andy Wilkinson
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "spring.main.web-application-type=reactive")
@RunWith(SpringRunner.class)
public class MappingsEndpointReactiveDocumentationTests extends AbstractEndpointDocumentationTests {
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @LocalServerPort
    private int port;

    private WebTestClient client;

    @Test
    public void mappings() throws Exception {
        List<FieldDescriptor> requestMappingConditions = Arrays.asList(requestMappingConditionField("").description("Details of the request mapping conditions.").optional(), requestMappingConditionField(".consumes").description("Details of the consumes condition"), requestMappingConditionField(".consumes.[].mediaType").description("Consumed media type."), requestMappingConditionField(".consumes.[].negated").description("Whether the media type is negated."), requestMappingConditionField(".headers").description("Details of the headers condition."), requestMappingConditionField(".headers.[].name").description("Name of the header."), requestMappingConditionField(".headers.[].value").description("Required value of the header, if any."), requestMappingConditionField(".headers.[].negated").description("Whether the value is negated."), requestMappingConditionField(".methods").description("HTTP methods that are handled."), requestMappingConditionField(".params").description("Details of the params condition."), requestMappingConditionField(".params.[].name").description("Name of the parameter."), requestMappingConditionField(".params.[].value").description("Required value of the parameter, if any."), requestMappingConditionField(".params.[].negated").description("Whether the value is negated."), requestMappingConditionField(".patterns").description("Patterns identifying the paths handled by the mapping."), requestMappingConditionField(".produces").description("Details of the produces condition."), requestMappingConditionField(".produces.[].mediaType").description("Produced media type."), requestMappingConditionField(".produces.[].negated").description("Whether the media type is negated."));
        List<FieldDescriptor> handlerMethod = Arrays.asList(fieldWithPath("*.[].details.handlerMethod").optional().type(OBJECT).description(("Details of the method, if any, " + "that will handle requests to this mapping.")), fieldWithPath("*.[].details.handlerMethod.className").type(STRING).description("Fully qualified name of the class of the method."), fieldWithPath("*.[].details.handlerMethod.name").type(STRING).description("Name of the method."), fieldWithPath("*.[].details.handlerMethod.descriptor").type(STRING).description(("Descriptor of the method as specified in the Java " + "Language Specification.")));
        List<FieldDescriptor> handlerFunction = Arrays.asList(fieldWithPath("*.[].details.handlerFunction").optional().type(OBJECT).description(("Details of the function, if any, that will handle " + "requests to this mapping.")), fieldWithPath("*.[].details.handlerFunction.className").type(STRING).description("Fully qualified name of the class of the function."));
        List<FieldDescriptor> dispatcherHandlerFields = new java.util.ArrayList(Arrays.asList(fieldWithPath("*").description(("Dispatcher handler mappings, if any, keyed by " + "dispatcher handler bean name.")), fieldWithPath("*.[].details").optional().type(OBJECT).description(("Additional implementation-specific " + "details about the mapping. Optional.")), fieldWithPath("*.[].handler").description("Handler for the mapping."), fieldWithPath("*.[].predicate").description("Predicate for the mapping.")));
        dispatcherHandlerFields.addAll(requestMappingConditions);
        dispatcherHandlerFields.addAll(handlerMethod);
        dispatcherHandlerFields.addAll(handlerFunction);
        this.client.get().uri("/actuator/mappings").exchange().expectStatus().isOk().expectBody().consumeWith(document("mappings", responseFields(beneathPath("contexts.*.mappings.dispatcherHandlers").withSubsectionId("dispatcher-handlers"), dispatcherHandlerFields)));
    }

    @Configuration
    @Import(AbstractEndpointDocumentationTests.BaseDocumentationConfiguration.class)
    static class TestConfiguration {
        @Bean
        public NettyReactiveWebServerFactory netty() {
            return new NettyReactiveWebServerFactory(0);
        }

        @Bean
        public DispatcherHandlersMappingDescriptionProvider dispatcherHandlersMappingDescriptionProvider() {
            return new DispatcherHandlersMappingDescriptionProvider();
        }

        @Bean
        public MappingsEndpoint mappingsEndpoint(Collection<MappingDescriptionProvider> descriptionProviders, ConfigurableApplicationContext context) {
            return new MappingsEndpoint(descriptionProviders, context);
        }

        @Bean
        public RouterFunction<ServerResponse> exampleRouter() {
            return route(GET("/foo"), ( request) -> ServerResponse.ok().build());
        }

        @Bean
        public MappingsEndpointReactiveDocumentationTests.ExampleController exampleController() {
            return new MappingsEndpointReactiveDocumentationTests.ExampleController();
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

