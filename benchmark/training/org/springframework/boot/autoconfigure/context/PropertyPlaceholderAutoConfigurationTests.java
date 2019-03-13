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
package org.springframework.boot.autoconfigure.context;


import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.util.StringUtils;


/**
 * Tests for {@link PropertyPlaceholderAutoConfiguration}.
 *
 * @author Dave Syer
 */
public class PropertyPlaceholderAutoConfigurationTests {
    private final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

    @Test
    public void propertyPlaceholders() {
        this.context.register(PropertyPlaceholderAutoConfiguration.class, PropertyPlaceholderAutoConfigurationTests.PlaceholderConfig.class);
        TestPropertyValues.of("foo:two").applyTo(this.context);
        this.context.refresh();
        assertThat(this.context.getBean(PropertyPlaceholderAutoConfigurationTests.PlaceholderConfig.class).getFoo()).isEqualTo("two");
    }

    @Test
    public void propertyPlaceholdersOverride() {
        this.context.register(PropertyPlaceholderAutoConfiguration.class, PropertyPlaceholderAutoConfigurationTests.PlaceholderConfig.class, PropertyPlaceholderAutoConfigurationTests.PlaceholdersOverride.class);
        TestPropertyValues.of("foo:two").applyTo(this.context);
        this.context.refresh();
        assertThat(this.context.getBean(PropertyPlaceholderAutoConfigurationTests.PlaceholderConfig.class).getFoo()).isEqualTo("spam");
    }

    @Configuration
    static class PlaceholderConfig {
        @Value("${foo:bar}")
        private String foo;

        public String getFoo() {
            return this.foo;
        }
    }

    @Configuration
    static class PlaceholdersOverride {
        @Bean
        public static PropertySourcesPlaceholderConfigurer morePlaceholders() {
            PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
            configurer.setProperties(StringUtils.splitArrayElementsIntoProperties(new String[]{ "foo=spam" }, "="));
            configurer.setLocalOverride(true);
            configurer.setOrder(0);
            return configurer;
        }
    }
}

