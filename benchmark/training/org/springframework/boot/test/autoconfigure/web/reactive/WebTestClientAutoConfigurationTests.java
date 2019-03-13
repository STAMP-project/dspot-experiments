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
package org.springframework.boot.test.autoconfigure.web.reactive;


import java.time.Duration;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebHandler;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;


/**
 * Tests for {@link WebTestClientAutoConfiguration}
 *
 * @author Brian Clozel
 * @author Stephane Nicoll
 */
public class WebTestClientAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(WebTestClientAutoConfiguration.class));

    @Test
    public void shouldNotBeConfiguredWithoutWebHandler() {
        this.contextRunner.run(( context) -> {
            assertThat(context).hasNotFailed();
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void shouldCustomizeClientCodecs() {
        this.contextRunner.withUserConfiguration(WebTestClientAutoConfigurationTests.CodecConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            verify(context.getBean(.class)).customize(any(.class));
        });
    }

    @Test
    public void shouldCustomizeTimeout() {
        this.contextRunner.withUserConfiguration(WebTestClientAutoConfigurationTests.BaseConfiguration.class).withPropertyValues("spring.test.webtestclient.timeout=15m").run(( context) -> {
            WebTestClient webTestClient = context.getBean(.class);
            Object duration = ReflectionTestUtils.getField(webTestClient, "timeout");
            assertThat(duration).isEqualTo(Duration.of(15, ChronoUnit.MINUTES));
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldApplySpringSecurityConfigurer() {
        this.contextRunner.withUserConfiguration(WebTestClientAutoConfigurationTests.BaseConfiguration.class).run(( context) -> {
            WebTestClient webTestClient = context.getBean(.class);
            WebTestClient.Builder builder = ((WebTestClient.Builder) (ReflectionTestUtils.getField(webTestClient, "builder")));
            WebHttpHandlerBuilder httpHandlerBuilder = ((WebHttpHandlerBuilder) (ReflectionTestUtils.getField(builder, "httpHandlerBuilder")));
            List<WebFilter> filters = ((List<WebFilter>) (ReflectionTestUtils.getField(httpHandlerBuilder, "filters")));
            assertThat(filters.get(0).getClass().getName()).isEqualTo("org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers$MutatorFilter");
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldNotApplySpringSecurityConfigurerWhenSpringSecurityNotOnClassPath() {
        FilteredClassLoader classLoader = new FilteredClassLoader(SecurityMockServerConfigurers.class);
        this.contextRunner.withUserConfiguration(WebTestClientAutoConfigurationTests.BaseConfiguration.class).withClassLoader(classLoader).run(( context) -> {
            WebTestClient webTestClient = context.getBean(.class);
            WebTestClient.Builder builder = ((WebTestClient.Builder) (ReflectionTestUtils.getField(webTestClient, "builder")));
            WebHttpHandlerBuilder httpHandlerBuilder = ((WebHttpHandlerBuilder) (ReflectionTestUtils.getField(builder, "httpHandlerBuilder")));
            List<WebFilter> filters = ((List<WebFilter>) (ReflectionTestUtils.getField(httpHandlerBuilder, "filters")));
            assertThat(filters).isEmpty();
        });
    }

    @Configuration
    static class BaseConfiguration {
        @Bean
        public WebHandler webHandler() {
            return Mockito.mock(WebHandler.class);
        }
    }

    @Configuration
    @Import(WebTestClientAutoConfigurationTests.BaseConfiguration.class)
    static class CodecConfiguration {
        @Bean
        public CodecCustomizer myCodecCustomizer() {
            return Mockito.mock(CodecCustomizer.class);
        }
    }
}

