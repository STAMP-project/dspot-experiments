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
package org.springframework.boot.autoconfigure.web.reactive.function.client;


import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.reactive.function.client.WebClient;


/**
 * Tests for {@link ClientHttpConnectorAutoConfiguration}
 *
 * @author Brian Clozel
 */
public class ClientHttpConnectorAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(ClientHttpConnectorAutoConfiguration.class));

    @Test
    public void shouldCreateHttpClientBeans() {
        this.contextRunner.run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasSingleBean(.class);
            WebClientCustomizer clientCustomizer = context.getBean(.class);
            WebClient.Builder builder = mock(.class);
            clientCustomizer.customize(builder);
            verify(builder, times(1)).clientConnector(any(.class));
        });
    }

    @Test
    public void shouldNotOverrideCustomClientConnector() {
        this.contextRunner.withUserConfiguration(ClientHttpConnectorAutoConfigurationTests.CustomClientHttpConnectorConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class).hasBean("customConnector").doesNotHaveBean(.class);
            WebClientCustomizer clientCustomizer = context.getBean(.class);
            WebClient.Builder builder = mock(.class);
            clientCustomizer.customize(builder);
            verify(builder, times(1)).clientConnector(any(.class));
        });
    }

    @Test
    public void shouldUseCustomReactorResourceFactory() {
        this.contextRunner.withUserConfiguration(ClientHttpConnectorAutoConfigurationTests.CustomReactorResourceConfig.class).run(( context) -> assertThat(context).hasSingleBean(.class).hasSingleBean(.class).hasBean("customReactorResourceFactory"));
    }

    @Configuration
    static class CustomClientHttpConnectorConfig {
        @Bean
        public ClientHttpConnector customConnector() {
            return Mockito.mock(ClientHttpConnector.class);
        }
    }

    @Configuration
    static class CustomReactorResourceConfig {
        @Bean
        public ReactorResourceFactory customReactorResourceFactory() {
            return new ReactorResourceFactory();
        }
    }
}

