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
package org.springframework.boot.autoconfigure.security.servlet;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Integration test to ensure {@link SecurityFilterAutoConfiguration} doesn't cause early
 * initialization.
 *
 * @author Phillip Webb
 */
public class SecurityFilterAutoConfigurationEarlyInitializationTests {
    @Rule
    public final OutputCapture output = new OutputCapture();

    @Test
    public void testSecurityFilterDoesNotCauseEarlyInitialization() {
        try (AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext()) {
            TestPropertyValues.of("server.port:0").applyTo(context);
            context.register(SecurityFilterAutoConfigurationEarlyInitializationTests.Config.class);
            context.refresh();
            int port = context.getWebServer().getPort();
            String password = this.output.toString().split("Using generated security password: ")[1].split("\n")[0].trim();
            new TestRestTemplate("user", password).getForEntity(("http://localhost:" + port), Object.class);
            // If early initialization occurred a ConverterNotFoundException is thrown
        }
    }

    @Configuration
    @Import({ SecurityFilterAutoConfigurationEarlyInitializationTests.DeserializerBean.class, SecurityFilterAutoConfigurationEarlyInitializationTests.JacksonModuleBean.class, SecurityFilterAutoConfigurationEarlyInitializationTests.ExampleController.class, SecurityFilterAutoConfigurationEarlyInitializationTests.ConverterBean.class })
    @ImportAutoConfiguration({ WebMvcAutoConfiguration.class, JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, DispatcherServletAutoConfiguration.class, SecurityAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class, SecurityFilterAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class })
    static class Config {
        @Bean
        public TomcatServletWebServerFactory webServerFactory() {
            TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
            factory.setPort(0);
            return factory;
        }
    }

    public static class SourceType {
        public String foo;
    }

    public static class DestinationType {
        public String bar;
    }

    @Component
    public static class JacksonModuleBean extends SimpleModule {
        private static final long serialVersionUID = 1L;

        public JacksonModuleBean(SecurityFilterAutoConfigurationEarlyInitializationTests.DeserializerBean myDeser) {
            addDeserializer(SecurityFilterAutoConfigurationEarlyInitializationTests.SourceType.class, myDeser);
        }
    }

    @Component
    public static class DeserializerBean extends StdDeserializer<SecurityFilterAutoConfigurationEarlyInitializationTests.SourceType> {
        @Autowired
        ConversionService conversionService;

        public DeserializerBean() {
            super(SecurityFilterAutoConfigurationEarlyInitializationTests.SourceType.class);
        }

        @Override
        public SecurityFilterAutoConfigurationEarlyInitializationTests.SourceType deserialize(JsonParser p, DeserializationContext ctxt) {
            return new SecurityFilterAutoConfigurationEarlyInitializationTests.SourceType();
        }
    }

    @RestController
    public static class ExampleController {
        @Autowired
        private ConversionService conversionService;

        @RequestMapping("/")
        public void convert() {
            this.conversionService.convert(new SecurityFilterAutoConfigurationEarlyInitializationTests.SourceType(), SecurityFilterAutoConfigurationEarlyInitializationTests.DestinationType.class);
        }
    }

    @Component
    public static class ConverterBean implements Converter<SecurityFilterAutoConfigurationEarlyInitializationTests.SourceType, SecurityFilterAutoConfigurationEarlyInitializationTests.DestinationType> {
        @Override
        public SecurityFilterAutoConfigurationEarlyInitializationTests.DestinationType convert(SecurityFilterAutoConfigurationEarlyInitializationTests.SourceType source) {
            return new SecurityFilterAutoConfigurationEarlyInitializationTests.DestinationType();
        }
    }
}

