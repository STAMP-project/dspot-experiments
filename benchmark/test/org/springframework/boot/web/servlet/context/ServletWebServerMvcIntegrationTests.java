/**
 * Copyright 2012-2017 the original author or authors.
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
package org.springframework.boot.web.servlet.context;


import org.junit.Test;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * Integration tests for {@link ServletWebServerApplicationContext} and {@link WebServer}s
 * running Spring MVC.
 *
 * @author Phillip Webb
 * @author Ivan Sopov
 */
public class ServletWebServerMvcIntegrationTests {
    private AnnotationConfigServletWebServerApplicationContext context;

    @Test
    public void tomcat() throws Exception {
        this.context = new AnnotationConfigServletWebServerApplicationContext(ServletWebServerMvcIntegrationTests.TomcatConfig.class);
        doTest(this.context, "/hello");
    }

    @Test
    public void jetty() throws Exception {
        this.context = new AnnotationConfigServletWebServerApplicationContext(ServletWebServerMvcIntegrationTests.JettyConfig.class);
        doTest(this.context, "/hello");
    }

    @Test
    public void undertow() throws Exception {
        this.context = new AnnotationConfigServletWebServerApplicationContext(ServletWebServerMvcIntegrationTests.UndertowConfig.class);
        doTest(this.context, "/hello");
    }

    @Test
    public void advancedConfig() throws Exception {
        this.context = new AnnotationConfigServletWebServerApplicationContext(ServletWebServerMvcIntegrationTests.AdvancedConfig.class);
        doTest(this.context, "/example/spring/hello");
    }

    @Configuration
    @Import(ServletWebServerMvcIntegrationTests.Config.class)
    public static class TomcatConfig {
        @Bean
        public ServletWebServerFactory webServerFactory() {
            return new TomcatServletWebServerFactory(0);
        }
    }

    @Configuration
    @Import(ServletWebServerMvcIntegrationTests.Config.class)
    public static class JettyConfig {
        @Bean
        public ServletWebServerFactory webServerFactory() {
            return new JettyServletWebServerFactory(0);
        }
    }

    @Configuration
    @Import(ServletWebServerMvcIntegrationTests.Config.class)
    public static class UndertowConfig {
        @Bean
        public ServletWebServerFactory webServerFactory() {
            return new UndertowServletWebServerFactory(0);
        }
    }

    @Configuration
    @EnableWebMvc
    public static class Config {
        @Bean
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
            // Alternatively you can use ServletContextInitializer beans including
            // ServletRegistration and FilterRegistration. Read the
            // EmbeddedWebApplicationContext Javadoc for details.
        }

        @Bean
        public ServletWebServerMvcIntegrationTests.HelloWorldController helloWorldController() {
            return new ServletWebServerMvcIntegrationTests.HelloWorldController();
        }
    }

    @Configuration
    @EnableWebMvc
    @PropertySource("classpath:/org/springframework/boot/web/servlet/context/conf.properties")
    public static class AdvancedConfig {
        private final Environment env;

        public AdvancedConfig(Environment env) {
            this.env = env;
        }

        @Bean
        public ServletWebServerFactory webServerFactory() {
            JettyServletWebServerFactory factory = new JettyServletWebServerFactory(0);
            factory.setContextPath(this.env.getProperty("context"));
            return factory;
        }

        @Bean
        public ServletRegistrationBean<DispatcherServlet> dispatcherRegistration() {
            ServletRegistrationBean<DispatcherServlet> registration = new ServletRegistrationBean(dispatcherServlet());
            registration.addUrlMappings("/spring/*");
            return registration;
        }

        @Bean
        public DispatcherServlet dispatcherServlet() {
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            // Can configure dispatcher servlet here as would usually do via init-params
            return dispatcherServlet;
        }

        @Bean
        public ServletWebServerMvcIntegrationTests.HelloWorldController helloWorldController() {
            return new ServletWebServerMvcIntegrationTests.HelloWorldController();
        }
    }

    @Controller
    public static class HelloWorldController {
        @RequestMapping("/hello")
        @ResponseBody
        public String sayHello() {
            return "Hello World";
        }
    }
}

