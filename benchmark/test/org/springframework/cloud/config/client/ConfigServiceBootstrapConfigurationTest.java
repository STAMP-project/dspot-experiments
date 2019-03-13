/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.config.client;


import java.lang.reflect.Field;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.client.RestTemplate;


/**
 *
 *
 * @author Tristan Hanson
 */
public class ConfigServiceBootstrapConfigurationTest {
    private AnnotationConfigApplicationContext context;

    @Test
    public void overrideConfigServicePropertySourceLocatorWhenBeanIsProvided() {
        TestPropertyValues.of("spring.cloud.config.enabled=true").applyTo(this.context);
        this.context.register(ConfigServiceBootstrapConfigurationTest.ConfigServicePropertySourceLocatorOverrideConfig.class);
        this.context.register(ConfigServiceBootstrapConfiguration.class);
        this.context.refresh();
        ConfigServicePropertySourceLocator locator = this.context.getBean(ConfigServicePropertySourceLocator.class);
        Field restTemplateField = ReflectionUtils.findField(ConfigServicePropertySourceLocator.class, "restTemplate");
        restTemplateField.setAccessible(true);
        RestTemplate restTemplate = ((RestTemplate) (ReflectionUtils.getField(restTemplateField, locator)));
        assertThat(restTemplate).isNotNull();
    }

    @Configuration
    protected static class ConfigServicePropertySourceLocatorOverrideConfig {
        @Autowired
        private Environment environment;

        @Bean
        public ConfigServicePropertySourceLocator locator() {
            ConfigServicePropertySourceLocator locator = new ConfigServicePropertySourceLocator(new ConfigClientProperties(this.environment));
            locator.setRestTemplate(new RestTemplate());
            return locator;
        }
    }
}

