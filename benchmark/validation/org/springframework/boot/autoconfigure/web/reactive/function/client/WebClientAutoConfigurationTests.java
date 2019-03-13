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


import java.net.URI;
import java.time.Duration;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


/**
 * Tests for {@link WebClientAutoConfiguration}
 *
 * @author Brian Clozel
 */
public class WebClientAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(ClientHttpConnectorAutoConfiguration.class, WebClientAutoConfiguration.class));

    @Test
    public void shouldCreateBuilder() {
        this.contextRunner.run(( context) -> {
            WebClient.Builder builder = context.getBean(.class);
            WebClient webClient = builder.build();
            assertThat(webClient).isNotNull();
        });
    }

    @Test
    public void shouldCustomizeClientCodecs() {
        this.contextRunner.withUserConfiguration(WebClientAutoConfigurationTests.CodecConfiguration.class).run(( context) -> {
            WebClient.Builder builder = context.getBean(.class);
            CodecCustomizer codecCustomizer = context.getBean(.class);
            WebClientCodecCustomizer clientCustomizer = context.getBean(.class);
            builder.build();
            assertThat(clientCustomizer).isNotNull();
            verify(codecCustomizer).customize(any(.class));
        });
    }

    @Test
    public void webClientShouldApplyCustomizers() {
        this.contextRunner.withUserConfiguration(WebClientAutoConfigurationTests.WebClientCustomizerConfig.class).run(( context) -> {
            WebClient.Builder builder = context.getBean(.class);
            WebClientCustomizer customizer = context.getBean("webClientCustomizer", .class);
            builder.build();
            verify(customizer).customize(any(.class));
        });
    }

    @Test
    public void shouldGetPrototypeScopedBean() {
        this.contextRunner.withUserConfiguration(WebClientAutoConfigurationTests.WebClientCustomizerConfig.class).run(( context) -> {
            ClientHttpResponse response = mock(.class);
            ClientHttpConnector firstConnector = mock(.class);
            given(firstConnector.connect(any(), any(), any())).willReturn(Mono.just(response));
            WebClient.Builder firstBuilder = context.getBean(.class);
            firstBuilder.clientConnector(firstConnector).baseUrl("http://first.example.org");
            ClientHttpConnector secondConnector = mock(.class);
            given(secondConnector.connect(any(), any(), any())).willReturn(Mono.just(response));
            WebClient.Builder secondBuilder = context.getBean(.class);
            secondBuilder.clientConnector(secondConnector).baseUrl("http://second.example.org");
            assertThat(firstBuilder).isNotEqualTo(secondBuilder);
            firstBuilder.build().get().uri("/foo").exchange().block(Duration.ofSeconds(30));
            secondBuilder.build().get().uri("/foo").exchange().block(Duration.ofSeconds(30));
            verify(firstConnector).connect(eq(HttpMethod.GET), eq(URI.create("http://first.example.org/foo")), any());
            verify(secondConnector).connect(eq(HttpMethod.GET), eq(URI.create("http://second.example.org/foo")), any());
            WebClientCustomizer customizer = context.getBean("webClientCustomizer", .class);
            verify(customizer, times(1)).customize(any(.class));
        });
    }

    @Test
    public void shouldNotCreateClientBuilderIfAlreadyPresent() {
        this.contextRunner.withUserConfiguration(WebClientAutoConfigurationTests.WebClientCustomizerConfig.class, WebClientAutoConfigurationTests.CustomWebClientBuilderConfig.class).run(( context) -> {
            WebClient.Builder builder = context.getBean(.class);
            assertThat(builder).isInstanceOf(.class);
        });
    }

    @Configuration
    static class CodecConfiguration {
        @Bean
        public CodecCustomizer myCodecCustomizer() {
            return Mockito.mock(CodecCustomizer.class);
        }
    }

    @Configuration
    static class WebClientCustomizerConfig {
        @Bean
        public WebClientCustomizer webClientCustomizer() {
            return Mockito.mock(WebClientCustomizer.class);
        }
    }

    @Configuration
    static class CustomWebClientBuilderConfig {
        @Bean
        public WebClientAutoConfigurationTests.MyWebClientBuilder myWebClientBuilder() {
            return Mockito.mock(WebClientAutoConfigurationTests.MyWebClientBuilder.class);
        }
    }

    interface MyWebClientBuilder extends WebClient.Builder {}
}

