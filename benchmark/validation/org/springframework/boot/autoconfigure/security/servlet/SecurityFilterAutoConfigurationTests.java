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


import org.junit.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;


/**
 * Tests for {@link SecurityFilterAutoConfiguration}.
 *
 * @author Andy Wilkinson
 */
public class SecurityFilterAutoConfigurationTests {
    @Test
    public void filterAutoConfigurationWorksWithoutSecurityAutoConfiguration() {
        try (AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext()) {
            context.setServletContext(new MockServletContext());
            context.register(SecurityFilterAutoConfigurationTests.Config.class);
            context.refresh();
        }
    }

    @Configuration
    @Import({ SecurityFilterAutoConfigurationEarlyInitializationTests.DeserializerBean.class, SecurityFilterAutoConfigurationEarlyInitializationTests.JacksonModuleBean.class, SecurityFilterAutoConfigurationEarlyInitializationTests.ExampleController.class, SecurityFilterAutoConfigurationEarlyInitializationTests.ConverterBean.class })
    @ImportAutoConfiguration({ WebMvcAutoConfiguration.class, JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, DispatcherServletAutoConfiguration.class, SecurityAutoConfigurationTests.WebSecurity.class, SecurityFilterAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class })
    static class Config {}
}

