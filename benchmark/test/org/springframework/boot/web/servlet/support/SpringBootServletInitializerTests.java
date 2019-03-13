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
package org.springframework.boot.web.servlet.support;


import StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME;
import java.util.Collections;
import javax.servlet.ServletContext;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;


/**
 * Tests for {@link SpringBootServletInitializer}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class SpringBootServletInitializerTests {
    @Rule
    public OutputCapture output = new OutputCapture();

    private ServletContext servletContext = new MockServletContext();

    private SpringApplication application;

    @Test
    public void failsWithoutConfigure() {
        assertThatIllegalStateException().isThrownBy(() -> new org.springframework.boot.web.servlet.support.MockSpringBootServletInitializer().createRootApplicationContext(this.servletContext)).withMessageContaining("No SpringApplication sources have been defined");
    }

    @Test
    public void withConfigurationAnnotation() {
        new SpringBootServletInitializerTests.WithConfigurationAnnotation().createRootApplicationContext(this.servletContext);
        assertThat(this.application.getAllSources()).containsOnly(SpringBootServletInitializerTests.WithConfigurationAnnotation.class, ErrorPageFilterConfiguration.class);
    }

    @Test
    public void withConfiguredSource() {
        new SpringBootServletInitializerTests.WithConfiguredSource().createRootApplicationContext(this.servletContext);
        assertThat(this.application.getAllSources()).containsOnly(SpringBootServletInitializerTests.Config.class, ErrorPageFilterConfiguration.class);
    }

    @Test
    public void applicationBuilderCanBeCustomized() {
        SpringBootServletInitializerTests.CustomSpringBootServletInitializer servletInitializer = new SpringBootServletInitializerTests.CustomSpringBootServletInitializer();
        servletInitializer.createRootApplicationContext(this.servletContext);
        assertThat(servletInitializer.applicationBuilder.built).isTrue();
    }

    @Test
    public void mainClassHasSensibleDefault() {
        new SpringBootServletInitializerTests.WithConfigurationAnnotation().createRootApplicationContext(this.servletContext);
        assertThat(this.application).hasFieldOrPropertyWithValue("mainApplicationClass", SpringBootServletInitializerTests.WithConfigurationAnnotation.class);
    }

    @Test
    public void errorPageFilterRegistrationCanBeDisabled() {
        WebServer webServer = new UndertowServletWebServerFactory(0).getWebServer(( servletContext) -> {
            try (AbstractApplicationContext context = ((AbstractApplicationContext) (new org.springframework.boot.web.servlet.support.WithErrorPageFilterNotRegistered().createRootApplicationContext(servletContext)))) {
                assertThat(context.getBeansOfType(.class)).hasSize(0);
            }
        });
        try {
            webServer.start();
        } finally {
            webServer.stop();
        }
    }

    @Test
    public void executableWarThatUsesServletInitializerDoesNotHaveErrorPageFilterConfigured() {
        try (ConfigurableApplicationContext context = new SpringApplication(SpringBootServletInitializerTests.ExecutableWar.class).run()) {
            assertThat(context.getBeansOfType(ErrorPageFilter.class)).hasSize(0);
        }
    }

    @Test
    public void servletContextPropertySourceIsAvailablePriorToRefresh() {
        ServletContext servletContext = Mockito.mock(ServletContext.class);
        BDDMockito.given(servletContext.getInitParameterNames()).willReturn(Collections.enumeration(Collections.singletonList("spring.profiles.active")));
        BDDMockito.given(servletContext.getInitParameter("spring.profiles.active")).willReturn("from-servlet-context");
        BDDMockito.given(servletContext.getAttributeNames()).willReturn(Collections.enumeration(Collections.emptyList()));
        try (ConfigurableApplicationContext context = ((ConfigurableApplicationContext) (new SpringBootServletInitializerTests.PropertySourceVerifyingSpringBootServletInitializer().createRootApplicationContext(servletContext)))) {
            assertThat(context.getEnvironment().getActiveProfiles()).containsExactly("from-servlet-context");
        }
    }

    private static class PropertySourceVerifyingSpringBootServletInitializer extends SpringBootServletInitializer {
        @Override
        protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
            return builder.sources(SpringBootServletInitializerTests.TestApp.class).listeners(new SpringBootServletInitializerTests.PropertySourceVerifyingApplicationListener());
        }
    }

    @Configuration
    static class TestApp {}

    private class MockSpringBootServletInitializer extends SpringBootServletInitializer {
        @Override
        protected WebApplicationContext run(SpringApplication application) {
            SpringBootServletInitializerTests.this.application = application;
            return null;
        }
    }

    private class CustomSpringBootServletInitializer extends SpringBootServletInitializerTests.MockSpringBootServletInitializer {
        private final SpringBootServletInitializerTests.CustomSpringApplicationBuilder applicationBuilder = new SpringBootServletInitializerTests.CustomSpringApplicationBuilder();

        @Override
        protected SpringApplicationBuilder createSpringApplicationBuilder() {
            return this.applicationBuilder;
        }

        @Override
        protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
            return application.sources(SpringBootServletInitializerTests.Config.class);
        }
    }

    @Configuration
    public class WithConfigurationAnnotation extends SpringBootServletInitializerTests.MockSpringBootServletInitializer {}

    public class WithConfiguredSource extends SpringBootServletInitializerTests.MockSpringBootServletInitializer {
        @Override
        protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
            return application.sources(SpringBootServletInitializerTests.Config.class);
        }
    }

    @Configuration
    public static class WithErrorPageFilterNotRegistered extends SpringBootServletInitializer {
        public WithErrorPageFilterNotRegistered() {
            setRegisterErrorPageFilter(false);
        }
    }

    @Configuration
    public static class ExecutableWar extends SpringBootServletInitializer {
        @Bean
        public ServletWebServerFactory webServerFactory() {
            return new UndertowServletWebServerFactory(0);
        }
    }

    @Configuration
    public static class Config {}

    private static class CustomSpringApplicationBuilder extends SpringApplicationBuilder {
        private boolean built;

        @Override
        public SpringApplication build() {
            this.built = true;
            return super.build();
        }
    }

    private static final class PropertySourceVerifyingApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
        @Override
        public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
            PropertySource<?> propertySource = event.getEnvironment().getPropertySources().get(SERVLET_CONTEXT_PROPERTY_SOURCE_NAME);
            assertThat(propertySource.getProperty("spring.profiles.active")).isEqualTo("from-servlet-context");
        }
    }
}

