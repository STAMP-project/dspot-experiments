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
package org.springframework.boot.autoconfigure.web.servlet;


import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

import static DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME;
import static DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME;


/**
 * Tests for {@link DispatcherServletAutoConfiguration}.
 *
 * @author Dave Syer
 * @author Andy Wilkinson
 * @author Brian Clozel
 */
public class DispatcherServletAutoConfigurationTests {
    private WebApplicationContextRunner contextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(DispatcherServletAutoConfiguration.class));

    @Test
    public void registrationProperties() {
        this.contextRunner.run(( context) -> {
            assertThat(context.getBean(.class)).isNotNull();
            ServletRegistrationBean<?> registration = context.getBean(.class);
            assertThat(registration.getUrlMappings()).containsExactly("/");
        });
    }

    @Test
    public void registrationNonServletBean() {
        this.contextRunner.withUserConfiguration(DispatcherServletAutoConfigurationTests.NonServletConfiguration.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    // If a DispatcherServlet instance is registered with a name different
    // from the default one, we're registering one anyway
    @Test
    public void registrationOverrideWithDispatcherServletWrongName() {
        this.contextRunner.withUserConfiguration(DispatcherServletAutoConfigurationTests.CustomDispatcherServletDifferentName.class, DispatcherServletAutoConfigurationTests.CustomDispatcherServletPath.class).run(( context) -> {
            ServletRegistrationBean<?> registration = context.getBean(.class);
            assertThat(registration.getUrlMappings()).containsExactly("/");
            assertThat(registration.getServletName()).isEqualTo("dispatcherServlet");
            assertThat(context).getBeanNames(.class).hasSize(2);
        });
    }

    @Test
    public void registrationOverrideWithAutowiredServlet() {
        this.contextRunner.withUserConfiguration(DispatcherServletAutoConfigurationTests.CustomAutowiredRegistration.class, DispatcherServletAutoConfigurationTests.CustomDispatcherServletPath.class).run(( context) -> {
            ServletRegistrationBean<?> registration = context.getBean(.class);
            assertThat(registration.getUrlMappings()).containsExactly("/foo");
            assertThat(registration.getServletName()).isEqualTo("customDispatcher");
            assertThat(context).hasSingleBean(.class);
        });
    }

    @Test
    public void servletPath() {
        this.contextRunner.withPropertyValues("spring.mvc.servlet.path:/spring").run(( context) -> {
            assertThat(context.getBean(.class)).isNotNull();
            ServletRegistrationBean<?> registration = context.getBean(.class);
            assertThat(registration.getUrlMappings()).containsExactly("/spring/*");
            assertThat(registration.getMultipartConfig()).isNull();
            assertThat(context.getBean(.class).getPath()).isEqualTo("/spring");
        });
    }

    @Test
    public void dispatcherServletPathWhenCustomDispatcherServletSameNameShouldReturnConfiguredServletPath() {
        this.contextRunner.withUserConfiguration(DispatcherServletAutoConfigurationTests.CustomDispatcherServletSameName.class).withPropertyValues("spring.mvc.servlet.path:/spring").run(( context) -> assertThat(context.getBean(.class).getPath()).isEqualTo("/spring"));
    }

    @Test
    public void dispatcherServletPathNotCreatedWhenDefaultDispatcherServletNotAvailable() {
        this.contextRunner.withUserConfiguration(DispatcherServletAutoConfigurationTests.CustomDispatcherServletDifferentName.class, DispatcherServletAutoConfigurationTests.NonServletConfiguration.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void dispatcherServletPathNotCreatedWhenCustomRegistrationBeanPresent() {
        this.contextRunner.withUserConfiguration(DispatcherServletAutoConfigurationTests.CustomDispatcherServletRegistration.class).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void multipartConfig() {
        this.contextRunner.withUserConfiguration(DispatcherServletAutoConfigurationTests.MultipartConfiguration.class).run(( context) -> {
            ServletRegistrationBean<?> registration = context.getBean(.class);
            assertThat(registration.getMultipartConfig()).isNotNull();
        });
    }

    @Test
    public void renamesMultipartResolver() {
        this.contextRunner.withUserConfiguration(DispatcherServletAutoConfigurationTests.MultipartResolverConfiguration.class).run(( context) -> {
            DispatcherServlet dispatcherServlet = context.getBean(.class);
            dispatcherServlet.onApplicationEvent(new ContextRefreshedEvent(context));
            assertThat(dispatcherServlet.getMultipartResolver()).isInstanceOf(.class);
        });
    }

    @Test
    public void dispatcherServletDefaultConfig() {
        this.contextRunner.run(( context) -> {
            DispatcherServlet dispatcherServlet = context.getBean(.class);
            assertThat(dispatcherServlet).extracting("throwExceptionIfNoHandlerFound").containsExactly(false);
            assertThat(dispatcherServlet).extracting("dispatchOptionsRequest").containsExactly(true);
            assertThat(dispatcherServlet).extracting("dispatchTraceRequest").containsExactly(false);
            assertThat(dispatcherServlet).extracting("enableLoggingRequestDetails").containsExactly(false);
            assertThat(context.getBean("dispatcherServletRegistration")).hasFieldOrPropertyWithValue("loadOnStartup", (-1));
        });
    }

    @Test
    public void dispatcherServletCustomConfig() {
        this.contextRunner.withPropertyValues("spring.mvc.throw-exception-if-no-handler-found:true", "spring.mvc.dispatch-options-request:false", "spring.mvc.dispatch-trace-request:true", "spring.mvc.servlet.load-on-startup=5").run(( context) -> {
            DispatcherServlet dispatcherServlet = context.getBean(.class);
            assertThat(dispatcherServlet).extracting("throwExceptionIfNoHandlerFound").containsExactly(true);
            assertThat(dispatcherServlet).extracting("dispatchOptionsRequest").containsExactly(false);
            assertThat(dispatcherServlet).extracting("dispatchTraceRequest").containsExactly(true);
            assertThat(context.getBean("dispatcherServletRegistration")).hasFieldOrPropertyWithValue("loadOnStartup", 5);
        });
    }

    @Configuration
    protected static class MultipartConfiguration {
        @Bean
        public MultipartConfigElement multipartConfig() {
            MultipartConfigFactory factory = new MultipartConfigFactory();
            factory.setMaxFileSize(DataSize.ofKilobytes(128));
            factory.setMaxRequestSize(DataSize.ofKilobytes(128));
            return factory.createMultipartConfig();
        }
    }

    @Configuration
    protected static class CustomDispatcherServletDifferentName {
        @Bean
        public DispatcherServlet customDispatcherServlet() {
            return new DispatcherServlet();
        }
    }

    @Configuration
    protected static class CustomDispatcherServletPath {
        @Bean
        public DispatcherServletPath dispatcherServletPath() {
            return Mockito.mock(DispatcherServletPath.class);
        }
    }

    @Configuration
    protected static class CustomAutowiredRegistration {
        @Bean
        public org.springframework.boot.web.servlet.ServletRegistrationBean<?> dispatcherServletRegistration(DispatcherServlet dispatcherServlet) {
            org.springframework.boot.web.servlet.ServletRegistrationBean<DispatcherServlet> registration = new org.springframework.boot.web.servlet.ServletRegistrationBean(dispatcherServlet, "/foo");
            registration.setName("customDispatcher");
            return registration;
        }

        @Bean
        public DispatcherServletPath dispatcherServletPath() {
            return Mockito.mock(DispatcherServletPath.class);
        }
    }

    @Configuration
    protected static class NonServletConfiguration {
        @Bean
        public String dispatcherServlet() {
            return "spring";
        }
    }

    @Configuration
    protected static class MultipartResolverConfiguration {
        @Bean
        public MultipartResolver getMultipartResolver() {
            return new DispatcherServletAutoConfigurationTests.MockMultipartResolver();
        }
    }

    @Configuration
    protected static class CustomDispatcherServletSameName {
        @Bean(name = DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
        }
    }

    @Configuration
    protected static class CustomDispatcherServletRegistration {
        @Bean(name = DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME)
        public org.springframework.boot.web.servlet.ServletRegistrationBean<DispatcherServlet> dispatcherServletRegistration(DispatcherServlet dispatcherServlet) {
            org.springframework.boot.web.servlet.ServletRegistrationBean<DispatcherServlet> registration = new org.springframework.boot.web.servlet.ServletRegistrationBean(dispatcherServlet, "/foo");
            registration.setName("customDispatcher");
            return registration;
        }
    }

    private static class MockMultipartResolver implements MultipartResolver {
        @Override
        public boolean isMultipart(HttpServletRequest request) {
            return false;
        }

        @Override
        public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
            return null;
        }

        @Override
        public void cleanupMultipart(MultipartHttpServletRequest request) {
        }
    }
}

