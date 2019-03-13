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
package org.springframework.boot.devtools.remote.client;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.devtools.classpath.ClassPathChangedEvent;
import org.springframework.boot.devtools.filewatch.ChangedFiles;
import org.springframework.boot.devtools.livereload.LiveReloadServer;
import org.springframework.boot.devtools.remote.client.RemoteClientConfiguration.LiveReloadConfiguration;
import org.springframework.boot.devtools.remote.server.Dispatcher;
import org.springframework.boot.devtools.remote.server.DispatcherFilter;
import org.springframework.boot.devtools.restart.MockRestarter;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;


/**
 * Tests for {@link RemoteClientConfiguration}.
 *
 * @author Phillip Webb
 */
public class RemoteClientConfigurationTests {
    @Rule
    public MockRestarter restarter = new MockRestarter();

    @Rule
    public OutputCapture output = new OutputCapture();

    private AnnotationConfigServletWebServerApplicationContext context;

    private AnnotationConfigApplicationContext clientContext;

    @Test
    public void warnIfRestartDisabled() {
        configure("spring.devtools.remote.restart.enabled:false");
        assertThat(this.output.toString()).contains("Remote restart is disabled");
    }

    @Test
    public void warnIfNotHttps() {
        configure("http://localhost", true);
        assertThat(this.output.toString()).contains("is insecure");
    }

    @Test
    public void doesntWarnIfUsingHttps() {
        configure("https://localhost", true);
        assertThat(this.output.toString()).doesNotContain("is insecure");
    }

    @Test
    public void failIfNoSecret() {
        assertThatExceptionOfType(BeanCreationException.class).isThrownBy(() -> configure("http://localhost", false)).withMessageContaining("required to secure your connection");
    }

    @Test
    public void liveReloadOnClassPathChanged() throws Exception {
        configure();
        Set<ChangedFiles> changeSet = new HashSet<>();
        ClassPathChangedEvent event = new ClassPathChangedEvent(this, changeSet, false);
        this.clientContext.publishEvent(event);
        LiveReloadConfiguration configuration = this.clientContext.getBean(LiveReloadConfiguration.class);
        configuration.getExecutor().shutdown();
        configuration.getExecutor().awaitTermination(2, TimeUnit.SECONDS);
        LiveReloadServer server = this.clientContext.getBean(LiveReloadServer.class);
        Mockito.verify(server).triggerReload();
    }

    @Test
    public void liveReloadDisabled() {
        configure("spring.devtools.livereload.enabled:false");
        assertThatExceptionOfType(NoSuchBeanDefinitionException.class).isThrownBy(() -> this.context.getBean(.class));
    }

    @Test
    public void remoteRestartDisabled() {
        configure("spring.devtools.remote.restart.enabled:false");
        assertThatExceptionOfType(NoSuchBeanDefinitionException.class).isThrownBy(() -> this.context.getBean(.class));
    }

    @Configuration
    static class Config {
        @Bean
        public TomcatServletWebServerFactory tomcat() {
            return new TomcatServletWebServerFactory(0);
        }

        @Bean
        public DispatcherFilter dispatcherFilter() throws IOException {
            return new DispatcherFilter(dispatcher());
        }

        public Dispatcher dispatcher() throws IOException {
            Dispatcher dispatcher = Mockito.mock(Dispatcher.class);
            ServerHttpRequest anyRequest = ArgumentMatchers.any(ServerHttpRequest.class);
            ServerHttpResponse anyResponse = ArgumentMatchers.any(ServerHttpResponse.class);
            BDDMockito.given(dispatcher.handle(anyRequest, anyResponse)).willReturn(true);
            return dispatcher;
        }
    }

    @Configuration
    static class ClientConfig {
        @Bean
        public LiveReloadServer liveReloadServer() {
            return Mockito.mock(LiveReloadServer.class);
        }
    }
}

