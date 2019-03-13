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


import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.Filter;
import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor;
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter;
import org.springframework.boot.web.servlet.filter.OrderedHiddenHttpMethodFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;


/**
 * Tests for {@link HttpEncodingAutoConfiguration}
 *
 * @author Stephane Nicoll
 */
public class HttpEncodingAutoConfigurationTests {
    private AnnotationConfigWebApplicationContext context;

    @Test
    public void defaultConfiguration() {
        load(HttpEncodingAutoConfigurationTests.EmptyConfiguration.class);
        CharacterEncodingFilter filter = this.context.getBean(CharacterEncodingFilter.class);
        assertCharacterEncodingFilter(filter, "UTF-8", true, false);
    }

    @Test
    public void disableConfiguration() {
        load(HttpEncodingAutoConfigurationTests.EmptyConfiguration.class, "spring.http.encoding.enabled:false");
        assertThatExceptionOfType(NoSuchBeanDefinitionException.class).isThrownBy(() -> this.context.getBean(.class));
    }

    @Test
    public void customConfiguration() {
        load(HttpEncodingAutoConfigurationTests.EmptyConfiguration.class, "spring.http.encoding.charset:ISO-8859-15", "spring.http.encoding.force:false");
        CharacterEncodingFilter filter = this.context.getBean(CharacterEncodingFilter.class);
        assertCharacterEncodingFilter(filter, "ISO-8859-15", false, false);
    }

    @Test
    public void customFilterConfiguration() {
        load(HttpEncodingAutoConfigurationTests.FilterConfiguration.class, "spring.http.encoding.charset:ISO-8859-15", "spring.http.encoding.force:false");
        CharacterEncodingFilter filter = this.context.getBean(CharacterEncodingFilter.class);
        assertCharacterEncodingFilter(filter, "US-ASCII", false, false);
    }

    @Test
    public void forceRequest() {
        load(HttpEncodingAutoConfigurationTests.EmptyConfiguration.class, "spring.http.encoding.force-request:false");
        CharacterEncodingFilter filter = this.context.getBean(CharacterEncodingFilter.class);
        assertCharacterEncodingFilter(filter, "UTF-8", false, false);
    }

    @Test
    public void forceResponse() {
        load(HttpEncodingAutoConfigurationTests.EmptyConfiguration.class, "spring.http.encoding.force-response:true");
        CharacterEncodingFilter filter = this.context.getBean(CharacterEncodingFilter.class);
        assertCharacterEncodingFilter(filter, "UTF-8", true, true);
    }

    @Test
    public void forceRequestOverridesForce() {
        load(HttpEncodingAutoConfigurationTests.EmptyConfiguration.class, "spring.http.encoding.force:true", "spring.http.encoding.force-request:false");
        CharacterEncodingFilter filter = this.context.getBean(CharacterEncodingFilter.class);
        assertCharacterEncodingFilter(filter, "UTF-8", false, true);
    }

    @Test
    public void forceResponseOverridesForce() {
        load(HttpEncodingAutoConfigurationTests.EmptyConfiguration.class, "spring.http.encoding.force:true", "spring.http.encoding.force-response:false");
        CharacterEncodingFilter filter = this.context.getBean(CharacterEncodingFilter.class);
        assertCharacterEncodingFilter(filter, "UTF-8", true, false);
    }

    @Test
    public void filterIsOrderedHighest() {
        load(HttpEncodingAutoConfigurationTests.OrderedConfiguration.class);
        List<Filter> beans = new java.util.ArrayList(this.context.getBeansOfType(Filter.class).values());
        AnnotationAwareOrderComparator.sort(beans);
        assertThat(beans.get(0)).isInstanceOf(CharacterEncodingFilter.class);
        assertThat(beans.get(1)).isInstanceOf(HiddenHttpMethodFilter.class);
    }

    @Test
    public void noLocaleCharsetMapping() {
        load(HttpEncodingAutoConfigurationTests.EmptyConfiguration.class);
        Map<String, WebServerFactoryCustomizer<?>> beans = getWebServerFactoryCustomizerBeans();
        assertThat(beans.size()).isEqualTo(1);
        assertThat(this.context.getBean(MockServletWebServerFactory.class).getLocaleCharsetMappings()).isEmpty();
    }

    @Test
    public void customLocaleCharsetMappings() {
        load(HttpEncodingAutoConfigurationTests.EmptyConfiguration.class, "spring.http.encoding.mapping.en:UTF-8", "spring.http.encoding.mapping.fr_FR:UTF-8");
        Map<String, WebServerFactoryCustomizer<?>> beans = getWebServerFactoryCustomizerBeans();
        assertThat(beans.size()).isEqualTo(1);
        assertThat(this.context.getBean(MockServletWebServerFactory.class).getLocaleCharsetMappings().size()).isEqualTo(2);
        assertThat(this.context.getBean(MockServletWebServerFactory.class).getLocaleCharsetMappings().get(Locale.ENGLISH)).isEqualTo(StandardCharsets.UTF_8);
        assertThat(this.context.getBean(MockServletWebServerFactory.class).getLocaleCharsetMappings().get(Locale.FRANCE)).isEqualTo(StandardCharsets.UTF_8);
    }

    @Configuration
    static class EmptyConfiguration {}

    @Configuration
    static class FilterConfiguration {
        @Bean
        public CharacterEncodingFilter myCharacterEncodingFilter() {
            CharacterEncodingFilter filter = new CharacterEncodingFilter();
            filter.setEncoding("US-ASCII");
            filter.setForceEncoding(false);
            return filter;
        }
    }

    @Configuration
    static class OrderedConfiguration {
        @Bean
        public OrderedHiddenHttpMethodFilter hiddenHttpMethodFilter() {
            return new OrderedHiddenHttpMethodFilter();
        }

        @Bean
        public OrderedFormContentFilter formContentFilter() {
            return new OrderedFormContentFilter();
        }
    }

    @Configuration
    static class MinimalWebAutoConfiguration {
        @Bean
        public MockServletWebServerFactory MockServletWebServerFactory() {
            return new MockServletWebServerFactory();
        }

        @Bean
        public WebServerFactoryCustomizerBeanPostProcessor ServletWebServerCustomizerBeanPostProcessor() {
            return new WebServerFactoryCustomizerBeanPostProcessor();
        }
    }
}

