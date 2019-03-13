/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.actuate.autoconfigure.endpoint.condition;


import org.junit.Test;
import org.springframework.boot.actuate.endpoint.EndpointFilter;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.EndpointExtension;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link ConditionalOnEnabledEndpoint}.
 *
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 */
public class ConditionalOnEnabledEndpointTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    public void outcomeWhenEndpointEnabledPropertyIsTrueShouldMatch() {
        this.contextRunner.withPropertyValues("management.endpoint.foo.enabled=true").withUserConfiguration(ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultFalseConfiguration.class).run(( context) -> assertThat(context).hasBean("foo"));
    }

    @Test
    public void outcomeWhenEndpointEnabledPropertyIsFalseShouldNotMatch() {
        this.contextRunner.withPropertyValues("management.endpoint.foo.enabled=false").withUserConfiguration(ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultTrueConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean("foo"));
    }

    @Test
    public void outcomeWhenNoEndpointPropertyAndUserDefinedDefaultIsTrueShouldMatch() {
        this.contextRunner.withPropertyValues("management.endpoints.enabled-by-default=true").withUserConfiguration(ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultFalseConfiguration.class).run(( context) -> assertThat(context).hasBean("foo"));
    }

    @Test
    public void outcomeWhenNoEndpointPropertyAndUserDefinedDefaultIsFalseShouldNotMatch() {
        this.contextRunner.withPropertyValues("management.endpoints.enabled-by-default=false").withUserConfiguration(ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultTrueConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean("foo"));
    }

    @Test
    public void outcomeWhenNoPropertiesAndAnnotationIsEnabledByDefaultShouldMatch() {
        this.contextRunner.withUserConfiguration(ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultTrueConfiguration.class).run(( context) -> assertThat(context).hasBean("foo"));
    }

    @Test
    public void outcomeWhenNoPropertiesAndAnnotationIsNotEnabledByDefaultShouldNotMatch() {
        this.contextRunner.withUserConfiguration(ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultFalseConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean("foo"));
    }

    @Test
    public void outcomeWhenNoPropertiesAndExtensionAnnotationIsEnabledByDefaultShouldMatch() {
        this.contextRunner.withUserConfiguration(ConditionalOnEnabledEndpointTests.FooEndpointAndExtensionEnabledByDefaultTrueConfiguration.class).run(( context) -> assertThat(context).hasBean("foo").hasBean("fooExt"));
    }

    @Test
    public void outcomeWhenNoPropertiesAndExtensionAnnotationIsNotEnabledByDefaultShouldNotMatch() {
        this.contextRunner.withUserConfiguration(ConditionalOnEnabledEndpointTests.FooEndpointAndExtensionEnabledByDefaultFalseConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean("foo").doesNotHaveBean("fooExt"));
    }

    @Test
    public void outcomeWithReferenceWhenNoPropertiesShouldMatch() {
        this.contextRunner.withUserConfiguration(ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultTrue.class, ConditionalOnEnabledEndpointTests.ComponentEnabledIfEndpointIsEnabledConfiguration.class).run(( context) -> assertThat(context).hasBean("fooComponent"));
    }

    @Test
    public void outcomeWithReferenceWhenEndpointEnabledPropertyIsTrueShouldMatch() {
        this.contextRunner.withPropertyValues("management.endpoint.foo.enabled=true").withUserConfiguration(ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultTrue.class, ConditionalOnEnabledEndpointTests.ComponentEnabledIfEndpointIsEnabledConfiguration.class).run(( context) -> assertThat(context).hasBean("fooComponent"));
    }

    @Test
    public void outcomeWithReferenceWhenEndpointEnabledPropertyIsFalseShouldNotMatch() {
        this.contextRunner.withPropertyValues("management.endpoint.foo.enabled=false").withUserConfiguration(ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultTrue.class, ConditionalOnEnabledEndpointTests.ComponentEnabledIfEndpointIsEnabledConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean("fooComponent"));
    }

    @Test
    public void outcomeWithNoReferenceShouldFail() {
        this.contextRunner.withUserConfiguration(ConditionalOnEnabledEndpointTests.ComponentWithNoEndpointReferenceConfiguration.class).run(( context) -> {
            assertThat(context).hasFailed();
            assertThat(context.getStartupFailure().getCause().getMessage()).contains(("No endpoint is specified and the return type of the @Bean method " + "is neither an @Endpoint, nor an @EndpointExtension"));
        });
    }

    @Test
    public void outcomeWhenEndpointEnabledPropertyIsTrueAndMixedCaseShouldMatch() {
        this.contextRunner.withPropertyValues("management.endpoint.foo-bar.enabled=true").withUserConfiguration(ConditionalOnEnabledEndpointTests.FooBarEndpointEnabledByDefaultFalseConfiguration.class).run(( context) -> assertThat(context).hasBean("fooBar"));
    }

    @Test
    public void outcomeWhenEndpointEnabledPropertyIsFalseOnClassShouldNotMatch() {
        this.contextRunner.withPropertyValues("management.endpoint.foo.enabled=false").withUserConfiguration(ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultTrueOnConfigurationConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean("foo"));
    }

    @Endpoint(id = "foo", enableByDefault = true)
    static class FooEndpointEnabledByDefaultTrue {}

    @Endpoint(id = "foo", enableByDefault = false)
    static class FooEndpointEnabledByDefaultFalse {}

    @Endpoint(id = "fooBar", enableByDefault = false)
    static class FooBarEndpointEnabledByDefaultFalse {}

    @EndpointExtension(endpoint = ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultTrue.class, filter = ConditionalOnEnabledEndpointTests.TestFilter.class)
    static class FooEndpointExtensionEnabledByDefaultTrue {}

    @EndpointExtension(endpoint = ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultFalse.class, filter = ConditionalOnEnabledEndpointTests.TestFilter.class)
    static class FooEndpointExtensionEnabledByDefaultFalse {}

    static class TestFilter implements EndpointFilter<ExposableEndpoint<?>> {
        @Override
        public boolean match(ExposableEndpoint<?> endpoint) {
            return true;
        }
    }

    @Configuration
    static class FooEndpointEnabledByDefaultTrueConfiguration {
        @Bean
        @ConditionalOnEnabledEndpoint
        public ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultTrue foo() {
            return new ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultTrue();
        }
    }

    @Configuration
    @ConditionalOnEnabledEndpoint(endpoint = ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultTrue.class)
    static class FooEndpointEnabledByDefaultTrueOnConfigurationConfiguration {
        @Bean
        public ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultTrue foo() {
            return new ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultTrue();
        }
    }

    @Configuration
    static class FooEndpointEnabledByDefaultFalseConfiguration {
        @Bean
        @ConditionalOnEnabledEndpoint
        public ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultFalse foo() {
            return new ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultFalse();
        }
    }

    @Configuration
    static class FooBarEndpointEnabledByDefaultFalseConfiguration {
        @Bean
        @ConditionalOnEnabledEndpoint
        public ConditionalOnEnabledEndpointTests.FooBarEndpointEnabledByDefaultFalse fooBar() {
            return new ConditionalOnEnabledEndpointTests.FooBarEndpointEnabledByDefaultFalse();
        }
    }

    @Configuration
    static class FooEndpointAndExtensionEnabledByDefaultTrueConfiguration {
        @Bean
        @ConditionalOnEnabledEndpoint
        public ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultTrue foo() {
            return new ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultTrue();
        }

        @Bean
        @ConditionalOnEnabledEndpoint
        public ConditionalOnEnabledEndpointTests.FooEndpointExtensionEnabledByDefaultTrue fooExt() {
            return new ConditionalOnEnabledEndpointTests.FooEndpointExtensionEnabledByDefaultTrue();
        }
    }

    @Configuration
    static class FooEndpointAndExtensionEnabledByDefaultFalseConfiguration {
        @Bean
        @ConditionalOnEnabledEndpoint
        public ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultFalse foo() {
            return new ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultFalse();
        }

        @Bean
        @ConditionalOnEnabledEndpoint
        public ConditionalOnEnabledEndpointTests.FooEndpointExtensionEnabledByDefaultFalse fooExt() {
            return new ConditionalOnEnabledEndpointTests.FooEndpointExtensionEnabledByDefaultFalse();
        }
    }

    @Configuration
    static class ComponentEnabledIfEndpointIsEnabledConfiguration {
        @Bean
        @ConditionalOnEnabledEndpoint(endpoint = ConditionalOnEnabledEndpointTests.FooEndpointEnabledByDefaultTrue.class)
        public String fooComponent() {
            return "foo";
        }
    }

    @Configuration
    static class ComponentWithNoEndpointReferenceConfiguration {
        @Bean
        @ConditionalOnEnabledEndpoint
        public String fooComponent() {
            return "foo";
        }
    }
}

