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
package org.springframework.boot.autoconfigure.web.servlet;


import javax.servlet.MultipartConfigElement;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * Tests for {@link MultipartAutoConfiguration}. Tests an empty configuration, no
 * multipart configuration, and a multipart configuration (with both Jetty and Tomcat).
 *
 * @author Greg Turnquist
 * @author Dave Syer
 * @author Josh Long
 * @author Ivan Sopov
 * @author Toshiaki Maki
 */
public class MultipartAutoConfigurationTests {
    private AnnotationConfigServletWebServerApplicationContext context;

    @Test
    public void webServerWithNothing() throws Exception {
        this.context = new AnnotationConfigServletWebServerApplicationContext(MultipartAutoConfigurationTests.WebServerWithNothing.class, MultipartAutoConfigurationTests.BaseConfiguration.class);
        DispatcherServlet servlet = this.context.getBean(DispatcherServlet.class);
        verify404();
        assertThat(servlet.getMultipartResolver()).isNotNull();
        assertThat(this.context.getBeansOfType(StandardServletMultipartResolver.class)).hasSize(1);
        assertThat(this.context.getBeansOfType(MultipartResolver.class)).hasSize(1);
    }

    @Test
    public void webServerWithNoMultipartJettyConfiguration() {
        this.context = new AnnotationConfigServletWebServerApplicationContext(MultipartAutoConfigurationTests.WebServerWithNoMultipartJetty.class, MultipartAutoConfigurationTests.BaseConfiguration.class);
        DispatcherServlet servlet = this.context.getBean(DispatcherServlet.class);
        assertThat(servlet.getMultipartResolver()).isNotNull();
        assertThat(this.context.getBeansOfType(StandardServletMultipartResolver.class)).hasSize(1);
        assertThat(this.context.getBeansOfType(MultipartResolver.class)).hasSize(1);
        verifyServletWorks();
    }

    @Test
    public void webServerWithNoMultipartUndertowConfiguration() {
        this.context = new AnnotationConfigServletWebServerApplicationContext(MultipartAutoConfigurationTests.WebServerWithNoMultipartUndertow.class, MultipartAutoConfigurationTests.BaseConfiguration.class);
        DispatcherServlet servlet = this.context.getBean(DispatcherServlet.class);
        verifyServletWorks();
        assertThat(servlet.getMultipartResolver()).isNotNull();
        assertThat(this.context.getBeansOfType(StandardServletMultipartResolver.class)).hasSize(1);
        assertThat(this.context.getBeansOfType(MultipartResolver.class)).hasSize(1);
    }

    @Test
    public void webServerWithNoMultipartTomcatConfiguration() {
        this.context = new AnnotationConfigServletWebServerApplicationContext(MultipartAutoConfigurationTests.WebServerWithNoMultipartTomcat.class, MultipartAutoConfigurationTests.BaseConfiguration.class);
        DispatcherServlet servlet = this.context.getBean(DispatcherServlet.class);
        assertThat(servlet.getMultipartResolver()).isNull();
        assertThat(this.context.getBeansOfType(StandardServletMultipartResolver.class)).hasSize(1);
        assertThat(this.context.getBeansOfType(MultipartResolver.class)).hasSize(1);
        verifyServletWorks();
    }

    @Test
    public void webServerWithAutomatedMultipartJettyConfiguration() {
        this.context = new AnnotationConfigServletWebServerApplicationContext(MultipartAutoConfigurationTests.WebServerWithEverythingJetty.class, MultipartAutoConfigurationTests.BaseConfiguration.class);
        this.context.getBean(MultipartConfigElement.class);
        assertThat(this.context.getBean(StandardServletMultipartResolver.class)).isSameAs(this.context.getBean(DispatcherServlet.class).getMultipartResolver());
        verifyServletWorks();
    }

    @Test
    public void webServerWithAutomatedMultipartTomcatConfiguration() {
        this.context = new AnnotationConfigServletWebServerApplicationContext(MultipartAutoConfigurationTests.WebServerWithEverythingTomcat.class, MultipartAutoConfigurationTests.BaseConfiguration.class);
        new RestTemplate().getForObject((("http://localhost:" + (this.context.getWebServer().getPort())) + "/"), String.class);
        this.context.getBean(MultipartConfigElement.class);
        assertThat(this.context.getBean(StandardServletMultipartResolver.class)).isSameAs(this.context.getBean(DispatcherServlet.class).getMultipartResolver());
        verifyServletWorks();
    }

    @Test
    public void webServerWithAutomatedMultipartUndertowConfiguration() {
        this.context = new AnnotationConfigServletWebServerApplicationContext(MultipartAutoConfigurationTests.WebServerWithEverythingUndertow.class, MultipartAutoConfigurationTests.BaseConfiguration.class);
        this.context.getBean(MultipartConfigElement.class);
        verifyServletWorks();
        assertThat(this.context.getBean(StandardServletMultipartResolver.class)).isSameAs(this.context.getBean(DispatcherServlet.class).getMultipartResolver());
    }

    @Test
    public void webServerWithMultipartConfigDisabled() {
        testWebServerWithCustomMultipartConfigEnabledSetting("false", 0);
    }

    @Test
    public void webServerWithMultipartConfigEnabled() {
        testWebServerWithCustomMultipartConfigEnabledSetting("true", 1);
    }

    @Test
    public void webServerWithCustomMultipartResolver() {
        this.context = new AnnotationConfigServletWebServerApplicationContext(MultipartAutoConfigurationTests.WebServerWithCustomMultipartResolver.class, MultipartAutoConfigurationTests.BaseConfiguration.class);
        MultipartResolver multipartResolver = this.context.getBean(MultipartResolver.class);
        assertThat(multipartResolver).isNotInstanceOf(StandardServletMultipartResolver.class);
        assertThat(this.context.getBeansOfType(MultipartConfigElement.class)).hasSize(1);
    }

    @Test
    public void containerWithCommonsMultipartResolver() {
        this.context = new AnnotationConfigServletWebServerApplicationContext(MultipartAutoConfigurationTests.ContainerWithCommonsMultipartResolver.class, MultipartAutoConfigurationTests.BaseConfiguration.class);
        MultipartResolver multipartResolver = this.context.getBean(MultipartResolver.class);
        assertThat(multipartResolver).isInstanceOf(CommonsMultipartResolver.class);
        assertThat(this.context.getBeansOfType(MultipartConfigElement.class)).hasSize(0);
    }

    @Test
    public void configureResolveLazily() {
        this.context = new AnnotationConfigServletWebServerApplicationContext();
        TestPropertyValues.of("spring.servlet.multipart.resolve-lazily=true").applyTo(this.context);
        this.context.register(MultipartAutoConfigurationTests.WebServerWithNothing.class, MultipartAutoConfigurationTests.BaseConfiguration.class);
        this.context.refresh();
        StandardServletMultipartResolver multipartResolver = this.context.getBean(StandardServletMultipartResolver.class);
        assertThat(multipartResolver).hasFieldOrPropertyWithValue("resolveLazily", true);
    }

    @Test
    public void configureMultipartProperties() {
        this.context = new AnnotationConfigServletWebServerApplicationContext();
        TestPropertyValues.of("spring.servlet.multipart.max-file-size=2048KB", "spring.servlet.multipart.max-request-size=15MB").applyTo(this.context);
        this.context.register(MultipartAutoConfigurationTests.WebServerWithNothing.class, MultipartAutoConfigurationTests.BaseConfiguration.class);
        this.context.refresh();
        MultipartConfigElement multipartConfigElement = this.context.getBean(MultipartConfigElement.class);
        assertThat(multipartConfigElement.getMaxFileSize()).isEqualTo((2048 * 1024));
        assertThat(multipartConfigElement.getMaxRequestSize()).isEqualTo(((15 * 1024) * 1024));
    }

    @Test
    public void configureMultipartPropertiesWithRawLongValues() {
        this.context = new AnnotationConfigServletWebServerApplicationContext();
        TestPropertyValues.of("spring.servlet.multipart.max-file-size=512", "spring.servlet.multipart.max-request-size=2048").applyTo(this.context);
        this.context.register(MultipartAutoConfigurationTests.WebServerWithNothing.class, MultipartAutoConfigurationTests.BaseConfiguration.class);
        this.context.refresh();
        MultipartConfigElement multipartConfigElement = this.context.getBean(MultipartConfigElement.class);
        assertThat(multipartConfigElement.getMaxFileSize()).isEqualTo(512);
        assertThat(multipartConfigElement.getMaxRequestSize()).isEqualTo(2048);
    }

    @Configuration
    public static class WebServerWithNothing {}

    @Configuration
    public static class WebServerWithNoMultipartJetty {
        @Bean
        JettyServletWebServerFactory webServerFactory() {
            return new JettyServletWebServerFactory();
        }

        @Bean
        MultipartAutoConfigurationTests.WebController controller() {
            return new MultipartAutoConfigurationTests.WebController();
        }
    }

    @Configuration
    public static class WebServerWithNoMultipartUndertow {
        @Bean
        UndertowServletWebServerFactory webServerFactory() {
            return new UndertowServletWebServerFactory();
        }

        @Bean
        MultipartAutoConfigurationTests.WebController controller() {
            return new MultipartAutoConfigurationTests.WebController();
        }
    }

    @Configuration
    @Import({ ServletWebServerFactoryAutoConfiguration.class, DispatcherServletAutoConfiguration.class, MultipartAutoConfiguration.class })
    @EnableConfigurationProperties(MultipartProperties.class)
    protected static class BaseConfiguration {
        @Bean
        public ServerProperties serverProperties() {
            ServerProperties properties = new ServerProperties();
            properties.setPort(0);
            return properties;
        }
    }

    @Configuration
    public static class WebServerWithNoMultipartTomcat {
        @Bean
        TomcatServletWebServerFactory webServerFactory() {
            return new TomcatServletWebServerFactory();
        }

        @Bean
        MultipartAutoConfigurationTests.WebController controller() {
            return new MultipartAutoConfigurationTests.WebController();
        }
    }

    @Configuration
    public static class WebServerWithEverythingJetty {
        @Bean
        MultipartConfigElement multipartConfigElement() {
            return new MultipartConfigElement("");
        }

        @Bean
        JettyServletWebServerFactory webServerFactory() {
            return new JettyServletWebServerFactory();
        }

        @Bean
        MultipartAutoConfigurationTests.WebController webController() {
            return new MultipartAutoConfigurationTests.WebController();
        }
    }

    @Configuration
    @EnableWebMvc
    public static class WebServerWithEverythingTomcat {
        @Bean
        MultipartConfigElement multipartConfigElement() {
            return new MultipartConfigElement("");
        }

        @Bean
        TomcatServletWebServerFactory webServerFactory() {
            return new TomcatServletWebServerFactory();
        }

        @Bean
        MultipartAutoConfigurationTests.WebController webController() {
            return new MultipartAutoConfigurationTests.WebController();
        }
    }

    @Configuration
    @EnableWebMvc
    public static class WebServerWithEverythingUndertow {
        @Bean
        MultipartConfigElement multipartConfigElement() {
            return new MultipartConfigElement("");
        }

        @Bean
        UndertowServletWebServerFactory webServerFactory() {
            return new UndertowServletWebServerFactory();
        }

        @Bean
        MultipartAutoConfigurationTests.WebController webController() {
            return new MultipartAutoConfigurationTests.WebController();
        }
    }

    @Configuration
    public static class WebServerWithCustomMultipartResolver {
        @Bean
        MultipartResolver multipartResolver() {
            return Mockito.mock(MultipartResolver.class);
        }
    }

    @Configuration
    public static class ContainerWithCommonsMultipartResolver {
        @Bean
        CommonsMultipartResolver multipartResolver() {
            return Mockito.mock(CommonsMultipartResolver.class);
        }
    }

    @Controller
    public static class WebController {
        @RequestMapping("/")
        @ResponseBody
        public String index() {
            return "Hello";
        }
    }
}

