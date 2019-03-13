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
package org.springframework.boot.actuate.endpoint.web.annotation;


import WebEndpointHttpMethod.DELETE;
import WebEndpointHttpMethod.GET;
import WebEndpointHttpMethod.POST;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.Test;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.invoker.cache.CachingOperationInvoker;
import org.springframework.boot.actuate.endpoint.jmx.annotation.JmxEndpoint;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebEndpointHttpMethod;
import org.springframework.boot.actuate.endpoint.web.WebOperation;
import org.springframework.boot.actuate.endpoint.web.WebOperationRequestPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link WebEndpointDiscoverer}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Phillip Webb
 */
public class WebEndpointDiscovererTests {
    @Test
    public void getEndpointsWhenNoEndpointBeansShouldReturnEmptyCollection() {
        load(WebEndpointDiscovererTests.EmptyConfiguration.class, ( discoverer) -> assertThat(discoverer.getEndpoints()).isEmpty());
    }

    @Test
    public void getEndpointsWhenWebExtensionIsMissingEndpointShouldThrowException() {
        load(WebEndpointDiscovererTests.TestWebEndpointExtensionConfiguration.class, ( discoverer) -> assertThatIllegalStateException().isThrownBy(discoverer::getEndpoints).withMessageContaining(("Invalid extension 'endpointExtension': no endpoint found with id '" + "test'")));
    }

    @Test
    public void getEndpointsWhenHasFilteredEndpointShouldOnlyDiscoverWebEndpoints() {
        load(WebEndpointDiscovererTests.MultipleEndpointsConfiguration.class, ( discoverer) -> {
            Map<EndpointId, ExposableWebEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"));
        });
    }

    @Test
    public void getEndpointsWhenHasWebExtensionShouldOverrideStandardEndpoint() {
        load(WebEndpointDiscovererTests.OverriddenOperationWebEndpointExtensionConfiguration.class, ( discoverer) -> {
            Map<EndpointId, ExposableWebEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"));
            ExposableWebEndpoint endpoint = endpoints.get(EndpointId.of("test"));
            assertThat(requestPredicates(endpoint)).has(requestPredicates(path("test").httpMethod(GET).consumes().produces("application/json")));
        });
    }

    @Test
    public void getEndpointsWhenExtensionAddsOperationShouldHaveBothOperations() {
        load(WebEndpointDiscovererTests.AdditionalOperationWebEndpointConfiguration.class, ( discoverer) -> {
            Map<EndpointId, ExposableWebEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"));
            ExposableWebEndpoint endpoint = endpoints.get(EndpointId.of("test"));
            assertThat(requestPredicates(endpoint)).has(requestPredicates(path("test").httpMethod(GET).consumes().produces("application/json"), path("test/{id}").httpMethod(GET).consumes().produces("application/json")));
        });
    }

    @Test
    public void getEndpointsWhenPredicateForWriteOperationThatReturnsVoidShouldHaveNoProducedMediaTypes() {
        load(WebEndpointDiscovererTests.VoidWriteOperationEndpointConfiguration.class, ( discoverer) -> {
            Map<EndpointId, ExposableWebEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("voidwrite"));
            ExposableWebEndpoint endpoint = endpoints.get(EndpointId.of("voidwrite"));
            assertThat(requestPredicates(endpoint)).has(requestPredicates(path("voidwrite").httpMethod(POST).produces().consumes("application/json")));
        });
    }

    @Test
    public void getEndpointsWhenTwoExtensionsHaveTheSameEndpointTypeShouldThrowException() {
        load(WebEndpointDiscovererTests.ClashingWebEndpointConfiguration.class, ( discoverer) -> assertThatIllegalStateException().isThrownBy(discoverer::getEndpoints).withMessageContaining(("Found multiple extensions for the endpoint bean " + "testEndpoint (testExtensionOne, testExtensionTwo)")));
    }

    @Test
    public void getEndpointsWhenTwoStandardEndpointsHaveTheSameIdShouldThrowException() {
        load(WebEndpointDiscovererTests.ClashingStandardEndpointConfiguration.class, ( discoverer) -> assertThatIllegalStateException().isThrownBy(discoverer::getEndpoints).withMessageContaining("Found two endpoints with the id 'test': "));
    }

    @Test
    public void getEndpointsWhenWhenEndpointHasTwoOperationsWithTheSameNameShouldThrowException() {
        load(WebEndpointDiscovererTests.ClashingOperationsEndpointConfiguration.class, ( discoverer) -> assertThatIllegalStateException().isThrownBy(discoverer::getEndpoints).withMessageContaining(("Unable to map duplicate endpoint operations: " + ("[web request predicate GET to path 'test' " + "produces: application/json] to clashingOperationsEndpoint"))));
    }

    @Test
    public void getEndpointsWhenExtensionIsNotCompatibleWithTheEndpointTypeShouldThrowException() {
        load(WebEndpointDiscovererTests.InvalidWebExtensionConfiguration.class, ( discoverer) -> assertThatIllegalStateException().isThrownBy(discoverer::getEndpoints).withMessageContaining(("Endpoint bean 'nonWebEndpoint' cannot support the " + "extension bean 'nonWebWebEndpointExtension'")));
    }

    @Test
    public void getEndpointsWhenWhenExtensionHasTwoOperationsWithTheSameNameShouldThrowException() {
        load(WebEndpointDiscovererTests.ClashingSelectorsWebEndpointExtensionConfiguration.class, ( discoverer) -> assertThatIllegalStateException().isThrownBy(discoverer::getEndpoints).withMessageContaining("Unable to map duplicate endpoint operations").withMessageContaining("to testEndpoint (clashingSelectorsExtension)"));
    }

    @Test
    public void getEndpointsWhenHasCacheWithTtlShouldCacheReadOperationWithTtlValue() {
        load(( id) -> 500L, EndpointId::toString, WebEndpointDiscovererTests.TestEndpointConfiguration.class, ( discoverer) -> {
            Map<EndpointId, ExposableWebEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"));
            ExposableWebEndpoint endpoint = endpoints.get(EndpointId.of("test"));
            assertThat(endpoint.getOperations()).hasSize(1);
            WebOperation operation = endpoint.getOperations().iterator().next();
            Object invoker = ReflectionTestUtils.getField(operation, "invoker");
            assertThat(invoker).isInstanceOf(.class);
            assertThat(((CachingOperationInvoker) (invoker)).getTimeToLive()).isEqualTo(500);
        });
    }

    @Test
    public void getEndpointsWhenOperationReturnsResourceShouldProduceApplicationOctetStream() {
        load(WebEndpointDiscovererTests.ResourceEndpointConfiguration.class, ( discoverer) -> {
            Map<EndpointId, ExposableWebEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("resource"));
            ExposableWebEndpoint endpoint = endpoints.get(EndpointId.of("resource"));
            assertThat(requestPredicates(endpoint)).has(requestPredicates(path("resource").httpMethod(GET).consumes().produces("application/octet-stream")));
        });
    }

    @Test
    public void getEndpointsWhenHasCustomMediaTypeShouldProduceCustomMediaType() {
        load(WebEndpointDiscovererTests.CustomMediaTypesEndpointConfiguration.class, ( discoverer) -> {
            Map<EndpointId, ExposableWebEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("custommediatypes"));
            ExposableWebEndpoint endpoint = endpoints.get(EndpointId.of("custommediatypes"));
            assertThat(requestPredicates(endpoint)).has(requestPredicates(path("custommediatypes").httpMethod(GET).consumes().produces("text/plain"), path("custommediatypes").httpMethod(POST).consumes().produces("a/b", "c/d"), path("custommediatypes").httpMethod(DELETE).consumes().produces("text/plain")));
        });
    }

    @Test
    public void getEndpointsWhenHasCustomPathShouldReturnCustomPath() {
        load(( id) -> null, ( id) -> "custom/" + id, WebEndpointDiscovererTests.AdditionalOperationWebEndpointConfiguration.class, ( discoverer) -> {
            Map<EndpointId, ExposableWebEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"));
            ExposableWebEndpoint endpoint = endpoints.get(EndpointId.of("test"));
            Condition<List<? extends WebOperationRequestPredicate>> expected = requestPredicates(path("custom/test").httpMethod(GET).consumes().produces("application/json"), path("custom/test/{id}").httpMethod(GET).consumes().produces("application/json"));
            assertThat(requestPredicates(endpoint)).has(expected);
        });
    }

    @Configuration
    static class EmptyConfiguration {}

    @Configuration
    static class MultipleEndpointsConfiguration {
        @Bean
        public WebEndpointDiscovererTests.TestEndpoint testEndpoint() {
            return new WebEndpointDiscovererTests.TestEndpoint();
        }

        @Bean
        public WebEndpointDiscovererTests.NonWebEndpoint nonWebEndpoint() {
            return new WebEndpointDiscovererTests.NonWebEndpoint();
        }
    }

    @Configuration
    static class TestWebEndpointExtensionConfiguration {
        @Bean
        public WebEndpointDiscovererTests.TestWebEndpointExtension endpointExtension() {
            return new WebEndpointDiscovererTests.TestWebEndpointExtension();
        }
    }

    @Configuration
    static class ClashingOperationsEndpointConfiguration {
        @Bean
        public WebEndpointDiscovererTests.ClashingOperationsEndpoint clashingOperationsEndpoint() {
            return new WebEndpointDiscovererTests.ClashingOperationsEndpoint();
        }
    }

    @Configuration
    static class ClashingOperationsWebEndpointExtensionConfiguration {
        @Bean
        public WebEndpointDiscovererTests.ClashingOperationsWebEndpointExtension clashingOperationsExtension() {
            return new WebEndpointDiscovererTests.ClashingOperationsWebEndpointExtension();
        }
    }

    @Configuration
    @Import(WebEndpointDiscovererTests.TestEndpointConfiguration.class)
    static class OverriddenOperationWebEndpointExtensionConfiguration {
        @Bean
        public WebEndpointDiscovererTests.OverriddenOperationWebEndpointExtension overriddenOperationExtension() {
            return new WebEndpointDiscovererTests.OverriddenOperationWebEndpointExtension();
        }
    }

    @Configuration
    @Import(WebEndpointDiscovererTests.TestEndpointConfiguration.class)
    static class AdditionalOperationWebEndpointConfiguration {
        @Bean
        public WebEndpointDiscovererTests.AdditionalOperationWebEndpointExtension additionalOperationExtension() {
            return new WebEndpointDiscovererTests.AdditionalOperationWebEndpointExtension();
        }
    }

    @Configuration
    static class TestEndpointConfiguration {
        @Bean
        public WebEndpointDiscovererTests.TestEndpoint testEndpoint() {
            return new WebEndpointDiscovererTests.TestEndpoint();
        }
    }

    @Configuration
    static class ClashingWebEndpointConfiguration {
        @Bean
        public WebEndpointDiscovererTests.TestEndpoint testEndpoint() {
            return new WebEndpointDiscovererTests.TestEndpoint();
        }

        @Bean
        public WebEndpointDiscovererTests.TestWebEndpointExtension testExtensionOne() {
            return new WebEndpointDiscovererTests.TestWebEndpointExtension();
        }

        @Bean
        public WebEndpointDiscovererTests.TestWebEndpointExtension testExtensionTwo() {
            return new WebEndpointDiscovererTests.TestWebEndpointExtension();
        }
    }

    @Configuration
    static class ClashingStandardEndpointConfiguration {
        @Bean
        public WebEndpointDiscovererTests.TestEndpoint testEndpointTwo() {
            return new WebEndpointDiscovererTests.TestEndpoint();
        }

        @Bean
        public WebEndpointDiscovererTests.TestEndpoint testEndpointOne() {
            return new WebEndpointDiscovererTests.TestEndpoint();
        }
    }

    @Configuration
    static class ClashingSelectorsWebEndpointExtensionConfiguration {
        @Bean
        public WebEndpointDiscovererTests.TestEndpoint testEndpoint() {
            return new WebEndpointDiscovererTests.TestEndpoint();
        }

        @Bean
        public WebEndpointDiscovererTests.ClashingSelectorsWebEndpointExtension clashingSelectorsExtension() {
            return new WebEndpointDiscovererTests.ClashingSelectorsWebEndpointExtension();
        }
    }

    @Configuration
    static class InvalidWebExtensionConfiguration {
        @Bean
        public WebEndpointDiscovererTests.NonWebEndpoint nonWebEndpoint() {
            return new WebEndpointDiscovererTests.NonWebEndpoint();
        }

        @Bean
        public WebEndpointDiscovererTests.NonWebWebEndpointExtension nonWebWebEndpointExtension() {
            return new WebEndpointDiscovererTests.NonWebWebEndpointExtension();
        }
    }

    @Configuration
    static class VoidWriteOperationEndpointConfiguration {
        @Bean
        public WebEndpointDiscovererTests.VoidWriteOperationEndpoint voidWriteOperationEndpoint() {
            return new WebEndpointDiscovererTests.VoidWriteOperationEndpoint();
        }
    }

    @Configuration
    @Import(BaseConfiguration.class)
    static class ResourceEndpointConfiguration {
        @Bean
        public WebEndpointDiscovererTests.ResourceEndpoint resourceEndpoint() {
            return new WebEndpointDiscovererTests.ResourceEndpoint();
        }
    }

    @Configuration
    @Import(BaseConfiguration.class)
    static class CustomMediaTypesEndpointConfiguration {
        @Bean
        public WebEndpointDiscovererTests.CustomMediaTypesEndpoint customMediaTypesEndpoint() {
            return new WebEndpointDiscovererTests.CustomMediaTypesEndpoint();
        }
    }

    @EndpointWebExtension(endpoint = WebEndpointDiscovererTests.TestEndpoint.class)
    static class TestWebEndpointExtension {
        @ReadOperation
        public Object getAll() {
            return null;
        }

        @ReadOperation
        public Object getOne(@Selector
        String id) {
            return null;
        }

        @WriteOperation
        public void update(String foo, String bar) {
        }

        public void someOtherMethod() {
        }
    }

    @Endpoint(id = "test")
    static class TestEndpoint {
        @ReadOperation
        public Object getAll() {
            return null;
        }
    }

    @EndpointWebExtension(endpoint = WebEndpointDiscovererTests.TestEndpoint.class)
    static class OverriddenOperationWebEndpointExtension {
        @ReadOperation
        public Object getAll() {
            return null;
        }
    }

    @EndpointWebExtension(endpoint = WebEndpointDiscovererTests.TestEndpoint.class)
    static class AdditionalOperationWebEndpointExtension {
        @ReadOperation
        public Object getOne(@Selector
        String id) {
            return null;
        }
    }

    @Endpoint(id = "test")
    static class ClashingOperationsEndpoint {
        @ReadOperation
        public Object getAll() {
            return null;
        }

        @ReadOperation
        public Object getAgain() {
            return null;
        }
    }

    @EndpointWebExtension(endpoint = WebEndpointDiscovererTests.TestEndpoint.class)
    static class ClashingOperationsWebEndpointExtension {
        @ReadOperation
        public Object getAll() {
            return null;
        }

        @ReadOperation
        public Object getAgain() {
            return null;
        }
    }

    @EndpointWebExtension(endpoint = WebEndpointDiscovererTests.TestEndpoint.class)
    static class ClashingSelectorsWebEndpointExtension {
        @ReadOperation
        public Object readOne(@Selector
        String oneA, @Selector
        String oneB) {
            return null;
        }

        @ReadOperation
        public Object readTwo(@Selector
        String twoA, @Selector
        String twoB) {
            return null;
        }
    }

    @JmxEndpoint(id = "nonweb")
    static class NonWebEndpoint {
        @ReadOperation
        public Object getData() {
            return null;
        }
    }

    @EndpointWebExtension(endpoint = WebEndpointDiscovererTests.NonWebEndpoint.class)
    static class NonWebWebEndpointExtension {
        @ReadOperation
        public Object getSomething(@Selector
        String name) {
            return null;
        }
    }

    @Endpoint(id = "voidwrite")
    static class VoidWriteOperationEndpoint {
        @WriteOperation
        public void write(String foo, String bar) {
        }
    }

    @Endpoint(id = "resource")
    static class ResourceEndpoint {
        @ReadOperation
        public Resource read() {
            return new ByteArrayResource(new byte[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        }
    }

    @Endpoint(id = "custommediatypes")
    static class CustomMediaTypesEndpoint {
        @ReadOperation(produces = "text/plain")
        public String read() {
            return "read";
        }

        @WriteOperation(produces = { "a/b", "c/d" })
        public String write() {
            return "write";
        }

        @DeleteOperation(produces = "text/plain")
        public String delete() {
            return "delete";
        }
    }

    private static final class RequestPredicateMatcher {
        private final String path;

        private List<String> produces;

        private List<String> consumes;

        private WebEndpointHttpMethod httpMethod;

        private RequestPredicateMatcher(String path) {
            this.path = path;
        }

        public WebEndpointDiscovererTests.RequestPredicateMatcher produces(String... mediaTypes) {
            this.produces = Arrays.asList(mediaTypes);
            return this;
        }

        public WebEndpointDiscovererTests.RequestPredicateMatcher consumes(String... mediaTypes) {
            this.consumes = Arrays.asList(mediaTypes);
            return this;
        }

        private WebEndpointDiscovererTests.RequestPredicateMatcher httpMethod(WebEndpointHttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        private boolean matches(WebOperationRequestPredicate predicate) {
            return (((((this.path) == null) || (this.path.equals(predicate.getPath()))) && (((this.httpMethod) == null) || ((this.httpMethod) == (predicate.getHttpMethod())))) && (((this.produces) == null) || (this.produces.equals(new java.util.ArrayList(predicate.getProduces()))))) && (((this.consumes) == null) || (this.consumes.equals(new java.util.ArrayList(predicate.getConsumes()))));
        }

        @Override
        public String toString() {
            return ((((("Request predicate with path = '" + (this.path)) + "', httpMethod = '") + (this.httpMethod)) + "', produces = '") + (this.produces)) + "'";
        }
    }
}

