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
package org.springframework.boot.autoconfigure.web.client;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;


/**
 * Tests for {@link RestTemplateAutoConfiguration}
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 */
public class RestTemplateAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(RestTemplateAutoConfiguration.class));

    @Test
    public void restTemplateWhenMessageConvertersDefinedShouldHaveMessageConverters() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(HttpMessageConvertersAutoConfiguration.class)).withUserConfiguration(RestTemplateAutoConfigurationTests.RestTemplateConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            RestTemplate restTemplate = context.getBean(.class);
            List<HttpMessageConverter<?>> converters = context.getBean(.class).getConverters();
            assertThat(restTemplate.getMessageConverters()).containsExactlyElementsOf(converters);
            assertThat(restTemplate.getRequestFactory()).isInstanceOf(.class);
        });
    }

    @Test
    public void restTemplateWhenNoMessageConvertersDefinedShouldHaveDefaultMessageConverters() {
        this.contextRunner.withUserConfiguration(RestTemplateAutoConfigurationTests.RestTemplateConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            RestTemplate restTemplate = context.getBean(.class);
            assertThat(restTemplate.getMessageConverters().size()).isEqualTo(new RestTemplate().getMessageConverters().size());
        });
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void restTemplateWhenHasCustomMessageConvertersShouldHaveMessageConverters() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(HttpMessageConvertersAutoConfiguration.class)).withUserConfiguration(RestTemplateAutoConfigurationTests.CustomHttpMessageConverter.class, RestTemplateAutoConfigurationTests.RestTemplateConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            RestTemplate restTemplate = context.getBean(.class);
            assertThat(restTemplate.getMessageConverters()).extracting(org.springframework.http.converter.HttpMessageConverter::getClass).contains(((Class) (.class)));
        });
    }

    @Test
    public void restTemplateWhenHasCustomBuilderShouldUseCustomBuilder() {
        this.contextRunner.withUserConfiguration(RestTemplateAutoConfigurationTests.RestTemplateConfig.class, RestTemplateAutoConfigurationTests.CustomRestTemplateBuilderConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            RestTemplate restTemplate = context.getBean(.class);
            assertThat(restTemplate.getMessageConverters()).hasSize(1);
            assertThat(restTemplate.getMessageConverters().get(0)).isInstanceOf(.class);
        });
    }

    @Test
    public void restTemplateShouldApplyCustomizer() {
        this.contextRunner.withUserConfiguration(RestTemplateAutoConfigurationTests.RestTemplateConfig.class, RestTemplateAutoConfigurationTests.RestTemplateCustomizerConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            RestTemplate restTemplate = context.getBean(.class);
            RestTemplateCustomizer customizer = context.getBean(.class);
            verify(customizer).customize(restTemplate);
        });
    }

    @Test
    public void builderShouldBeFreshForEachUse() {
        this.contextRunner.withUserConfiguration(RestTemplateAutoConfigurationTests.DirtyRestTemplateConfig.class).run(( context) -> assertThat(context).hasNotFailed());
    }

    @Test
    public void whenServletWebApplicationRestTemplateBuilderIsConfigured() {
        new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(RestTemplateAutoConfiguration.class)).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void whenReactiveWebApplicationRestTemplateBuilderIsNotConfigured() {
        new ReactiveWebApplicationContextRunner().withConfiguration(AutoConfigurations.of(RestTemplateAutoConfiguration.class)).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Configuration
    static class RestTemplateConfig {
        @Bean
        public RestTemplate restTemplate(RestTemplateBuilder builder) {
            return builder.build();
        }
    }

    @Configuration
    static class DirtyRestTemplateConfig {
        @Bean
        public RestTemplate restTemplateOne(RestTemplateBuilder builder) {
            try {
                return builder.build();
            } finally {
                breakBuilderOnNextCall(builder);
            }
        }

        @Bean
        public RestTemplate restTemplateTwo(RestTemplateBuilder builder) {
            try {
                return builder.build();
            } finally {
                breakBuilderOnNextCall(builder);
            }
        }

        private void breakBuilderOnNextCall(RestTemplateBuilder builder) {
            builder.additionalCustomizers(( restTemplate) -> {
                throw new IllegalStateException();
            });
        }
    }

    @Configuration
    static class CustomRestTemplateBuilderConfig {
        @Bean
        public RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder().messageConverters(new RestTemplateAutoConfigurationTests.CustomHttpMessageConverter());
        }
    }

    @Configuration
    static class RestTemplateCustomizerConfig {
        @Bean
        public RestTemplateCustomizer restTemplateCustomizer() {
            return Mockito.mock(RestTemplateCustomizer.class);
        }
    }

    static class CustomHttpMessageConverter extends StringHttpMessageConverter {}
}

