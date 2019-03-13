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


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FrameworkServlet;

import static DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME;
import static DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME;


/**
 * Tests for {@link ServletWebServerFactoryAutoConfiguration}.
 *
 * @author Dave Syer
 * @author Phillip Webb
 * @author Stephane Nicoll
 * @author Raheela Aslam
 */
public class ServletWebServerFactoryAutoConfigurationTests {
    private WebApplicationContextRunner contextRunner = new WebApplicationContextRunner(AnnotationConfigServletWebServerApplicationContext::new).withConfiguration(AutoConfigurations.of(ServletWebServerFactoryAutoConfiguration.class, DispatcherServletAutoConfiguration.class)).withUserConfiguration(ServletWebServerFactoryAutoConfigurationTests.WebServerConfiguration.class);

    @Test
    public void createFromConfigClass() {
        this.contextRunner.run(verifyContext());
    }

    @Test
    public void contextAlreadyHasDispatcherServletWithDefaultName() {
        this.contextRunner.withUserConfiguration(ServletWebServerFactoryAutoConfigurationTests.DispatcherServletConfiguration.class).run(verifyContext());
    }

    @Test
    public void contextAlreadyHasDispatcherServlet() {
        this.contextRunner.withUserConfiguration(ServletWebServerFactoryAutoConfigurationTests.SpringServletConfiguration.class).run(( context) -> {
            verifyContext(context);
            assertThat(context.getBeanNamesForType(.class)).hasSize(2);
        });
    }

    @Test
    public void contextAlreadyHasNonDispatcherServlet() {
        this.contextRunner.withUserConfiguration(ServletWebServerFactoryAutoConfigurationTests.NonSpringServletConfiguration.class).run(( context) -> {
            verifyContext(context);// the non default servlet is still registered

            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void contextAlreadyHasNonServlet() {
        this.contextRunner.withUserConfiguration(ServletWebServerFactoryAutoConfigurationTests.NonServletConfiguration.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void contextAlreadyHasDispatcherServletAndRegistration() {
        this.contextRunner.withUserConfiguration(ServletWebServerFactoryAutoConfigurationTests.DispatcherServletWithRegistrationConfiguration.class).run(( context) -> {
            verifyContext(context);
            assertThat(context).hasSingleBean(.class);
        });
    }

    @Test
    public void webServerHasNoServletContext() {
        this.contextRunner.withUserConfiguration(ServletWebServerFactoryAutoConfigurationTests.EnsureWebServerHasNoServletContext.class).run(verifyContext());
    }

    @Test
    public void customizeWebServerFactoryThroughCallback() {
        this.contextRunner.withUserConfiguration(ServletWebServerFactoryAutoConfigurationTests.CallbackEmbeddedServerFactoryCustomizer.class).run(( context) -> {
            verifyContext(context);
            assertThat(context.getBean(.class).getPort()).isEqualTo(9000);
        });
    }

    @Test
    public void initParametersAreConfiguredOnTheServletContext() {
        this.contextRunner.withPropertyValues("server.servlet.context-parameters.a:alpha", "server.servlet.context-parameters.b:bravo").run(( context) -> {
            ServletContext servletContext = context.getServletContext();
            assertThat(servletContext.getInitParameter("a")).isEqualTo("alpha");
            assertThat(servletContext.getInitParameter("b")).isEqualTo("bravo");
        });
    }

    @Test
    public void tomcatConnectorCustomizerBeanIsAddedToFactory() {
        WebApplicationContextRunner runner = new WebApplicationContextRunner(AnnotationConfigServletWebServerApplicationContext::new).withConfiguration(AutoConfigurations.of(ServletWebServerFactoryAutoConfiguration.class)).withUserConfiguration(ServletWebServerFactoryAutoConfigurationTests.TomcatConnectorCustomizerConfiguration.class);
        runner.run(( context) -> {
            TomcatServletWebServerFactory factory = context.getBean(.class);
            assertThat(factory.getTomcatConnectorCustomizers()).hasSize(1);
        });
    }

    @Test
    public void tomcatContextCustomizerBeanIsAddedToFactory() {
        WebApplicationContextRunner runner = new WebApplicationContextRunner(AnnotationConfigServletWebServerApplicationContext::new).withConfiguration(AutoConfigurations.of(ServletWebServerFactoryAutoConfiguration.class));
        runner.run(( context) -> {
            TomcatServletWebServerFactory factory = context.getBean(.class);
            assertThat(factory.getTomcatContextCustomizers()).hasSize(1);
        });
    }

    @Configuration
    @ConditionalOnExpression("true")
    public static class WebServerConfiguration {
        @Bean
        public ServletWebServerFactory webServerFactory() {
            return new MockServletWebServerFactory();
        }
    }

    @Configuration
    public static class DispatcherServletConfiguration {
        @Bean
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
        }
    }

    @Configuration
    public static class SpringServletConfiguration {
        @Bean
        public DispatcherServlet springServlet() {
            return new DispatcherServlet();
        }
    }

    @Configuration
    public static class NonSpringServletConfiguration {
        @Bean
        public FrameworkServlet dispatcherServlet() {
            return new FrameworkServlet() {
                @Override
                protected void doService(HttpServletRequest request, HttpServletResponse response) {
                }
            };
        }
    }

    @Configuration
    public static class NonServletConfiguration {
        @Bean
        public String dispatcherServlet() {
            return "foo";
        }
    }

    @Configuration
    public static class DispatcherServletWithRegistrationConfiguration {
        @Bean(name = DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
        }

        @Bean(name = DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME)
        public ServletRegistrationBean<DispatcherServlet> dispatcherRegistration() {
            return new ServletRegistrationBean(dispatcherServlet(), "/app/*");
        }
    }

    @Component
    public static class EnsureWebServerHasNoServletContext implements BeanPostProcessor {
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof ConfigurableServletWebServerFactory) {
                MockServletWebServerFactory webServerFactory = ((MockServletWebServerFactory) (bean));
                assertThat(webServerFactory.getServletContext()).isNull();
            }
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            return bean;
        }
    }

    @Component
    public static class CallbackEmbeddedServerFactoryCustomizer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
        @Override
        public void customize(ConfigurableServletWebServerFactory serverFactory) {
            serverFactory.setPort(9000);
        }
    }

    @Configuration
    static class TomcatConnectorCustomizerConfiguration {
        @Bean
        public TomcatConnectorCustomizer connectorCustomizer() {
            return ( connector) -> {
            };
        }
    }

    @Configuration
    static class TomcatContextCustomizerConfiguration {
        @Bean
        public TomcatContextCustomizer contextCustomizer() {
            return ( context) -> {
            };
        }
    }
}

