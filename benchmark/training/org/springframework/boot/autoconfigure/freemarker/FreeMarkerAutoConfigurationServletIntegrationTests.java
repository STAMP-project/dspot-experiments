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
package org.springframework.boot.autoconfigure.freemarker;


import DispatcherType.ERROR;
import DispatcherType.INCLUDE;
import DispatcherType.REQUEST;
import java.io.StringWriter;
import java.util.EnumSet;
import java.util.Map;
import org.junit.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedCharacterEncodingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;


/**
 * Tests for {@link FreeMarkerAutoConfiguration} Servlet support.
 *
 * @author Andy Wilkinson
 * @author Kazuki Shimizu
 */
public class FreeMarkerAutoConfigurationServletIntegrationTests {
    private AnnotationConfigWebApplicationContext context;

    @Test
    public void defaultConfiguration() {
        load();
        assertThat(this.context.getBean(FreeMarkerViewResolver.class)).isNotNull();
        assertThat(this.context.getBean(FreeMarkerConfigurer.class)).isNotNull();
        assertThat(this.context.getBean(FreeMarkerConfig.class)).isNotNull();
        assertThat(this.context.getBean(Configuration.class)).isNotNull();
    }

    @Test
    public void defaultViewResolution() throws Exception {
        load();
        MockHttpServletResponse response = render("home");
        String result = response.getContentAsString();
        assertThat(result).contains("home");
        assertThat(response.getContentType()).isEqualTo("text/html;charset=UTF-8");
    }

    @Test
    public void customContentType() throws Exception {
        load("spring.freemarker.contentType:application/json");
        MockHttpServletResponse response = render("home");
        String result = response.getContentAsString();
        assertThat(result).contains("home");
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
    }

    @Test
    public void customPrefix() throws Exception {
        load("spring.freemarker.prefix:prefix/");
        MockHttpServletResponse response = render("prefixed");
        String result = response.getContentAsString();
        assertThat(result).contains("prefixed");
    }

    @Test
    public void customSuffix() throws Exception {
        load("spring.freemarker.suffix:.freemarker");
        MockHttpServletResponse response = render("suffixed");
        String result = response.getContentAsString();
        assertThat(result).contains("suffixed");
    }

    @Test
    public void customTemplateLoaderPath() throws Exception {
        load("spring.freemarker.templateLoaderPath:classpath:/custom-templates/");
        MockHttpServletResponse response = render("custom");
        String result = response.getContentAsString();
        assertThat(result).contains("custom");
    }

    @Test
    public void disableCache() {
        load("spring.freemarker.cache:false");
        assertThat(this.context.getBean(FreeMarkerViewResolver.class).getCacheLimit()).isEqualTo(0);
    }

    @Test
    public void allowSessionOverride() {
        load("spring.freemarker.allow-session-override:true");
        AbstractTemplateViewResolver viewResolver = this.context.getBean(FreeMarkerViewResolver.class);
        assertThat(viewResolver).hasFieldOrPropertyWithValue("allowSessionOverride", true);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void customFreeMarkerSettings() {
        load("spring.freemarker.settings.boolean_format:yup,nope");
        assertThat(this.context.getBean(FreeMarkerConfigurer.class).getConfiguration().getSetting("boolean_format")).isEqualTo("yup,nope");
    }

    @Test
    public void renderTemplate() throws Exception {
        load();
        FreeMarkerConfigurer freemarker = this.context.getBean(FreeMarkerConfigurer.class);
        StringWriter writer = new StringWriter();
        freemarker.getConfiguration().getTemplate("message.ftl").process(this, writer);
        assertThat(writer.toString()).contains("Hello World");
    }

    @Test
    public void registerResourceHandlingFilterDisabledByDefault() {
        load();
        assertThat(this.context.getBeansOfType(FilterRegistrationBean.class)).isEmpty();
    }

    @Test
    public void registerResourceHandlingFilterOnlyIfResourceChainIsEnabled() {
        load("spring.resources.chain.enabled:true");
        FilterRegistrationBean<?> registration = this.context.getBean(FilterRegistrationBean.class);
        assertThat(registration.getFilter()).isInstanceOf(ResourceUrlEncodingFilter.class);
        assertThat(registration).hasFieldOrPropertyWithValue("dispatcherTypes", EnumSet.of(REQUEST, ERROR));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void registerResourceHandlingFilterWithOtherRegistrationBean() {
        // gh-14897
        load(FreeMarkerAutoConfigurationServletIntegrationTests.FilterRegistrationOtherConfiguration.class, "spring.resources.chain.enabled:true");
        Map<String, FilterRegistrationBean> beans = this.context.getBeansOfType(FilterRegistrationBean.class);
        assertThat(beans).hasSize(2);
        FilterRegistrationBean registration = beans.values().stream().filter(( r) -> (r.getFilter()) instanceof ResourceUrlEncodingFilter).findFirst().get();
        assertThat(registration).hasFieldOrPropertyWithValue("dispatcherTypes", EnumSet.of(REQUEST, ERROR));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void registerResourceHandlingFilterWithResourceRegistrationBean() {
        // gh-14926
        load(FreeMarkerAutoConfigurationServletIntegrationTests.FilterRegistrationResourceConfiguration.class, "spring.resources.chain.enabled:true");
        Map<String, FilterRegistrationBean> beans = this.context.getBeansOfType(FilterRegistrationBean.class);
        assertThat(beans).hasSize(1);
        FilterRegistrationBean registration = beans.values().stream().filter(( r) -> (r.getFilter()) instanceof ResourceUrlEncodingFilter).findFirst().get();
        assertThat(registration).hasFieldOrPropertyWithValue("dispatcherTypes", EnumSet.of(INCLUDE));
    }

    @Configuration
    @ImportAutoConfiguration({ FreeMarkerAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class })
    static class BaseConfiguration {}

    @Configuration
    @Import(FreeMarkerAutoConfigurationServletIntegrationTests.BaseConfiguration.class)
    static class FilterRegistrationResourceConfiguration {
        @Bean
        public FilterRegistrationBean<ResourceUrlEncodingFilter> filterRegistration() {
            FilterRegistrationBean<ResourceUrlEncodingFilter> bean = new FilterRegistrationBean(new ResourceUrlEncodingFilter());
            bean.setDispatcherTypes(EnumSet.of(INCLUDE));
            return bean;
        }
    }

    @Configuration
    @Import(FreeMarkerAutoConfigurationServletIntegrationTests.BaseConfiguration.class)
    static class FilterRegistrationOtherConfiguration {
        @Bean
        public FilterRegistrationBean<OrderedCharacterEncodingFilter> filterRegistration() {
            return new FilterRegistrationBean(new OrderedCharacterEncodingFilter());
        }
    }
}

