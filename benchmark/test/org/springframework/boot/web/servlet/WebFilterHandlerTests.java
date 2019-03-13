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
package org.springframework.boot.web.servlet;


import DispatcherType.FORWARD;
import DispatcherType.INCLUDE;
import DispatcherType.REQUEST;
import java.io.IOException;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;


/**
 * Tests for {@link WebFilterHandler}
 *
 * @author Andy Wilkinson
 */
public class WebFilterHandlerTests {
    private final WebFilterHandler handler = new WebFilterHandler();

    private final SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();

    @SuppressWarnings("unchecked")
    @Test
    public void defaultFilterConfiguration() throws IOException {
        ScannedGenericBeanDefinition scanned = new ScannedGenericBeanDefinition(new SimpleMetadataReaderFactory().getMetadataReader(WebFilterHandlerTests.DefaultConfigurationFilter.class.getName()));
        this.handler.handle(scanned, this.registry);
        BeanDefinition filterRegistrationBean = this.registry.getBeanDefinition(WebFilterHandlerTests.DefaultConfigurationFilter.class.getName());
        MutablePropertyValues propertyValues = filterRegistrationBean.getPropertyValues();
        assertThat(propertyValues.get("asyncSupported")).isEqualTo(false);
        assertThat(((java.util.EnumSet<DispatcherType>) (propertyValues.get("dispatcherTypes")))).containsExactly(REQUEST);
        assertThat(((java.util.Map<String, String>) (propertyValues.get("initParameters")))).isEmpty();
        assertThat(((String[]) (propertyValues.get("servletNames")))).isEmpty();
        assertThat(((String[]) (propertyValues.get("urlPatterns")))).isEmpty();
        assertThat(propertyValues.get("name")).isEqualTo(WebFilterHandlerTests.DefaultConfigurationFilter.class.getName());
        assertThat(propertyValues.get("filter")).isEqualTo(scanned);
    }

    @Test
    public void filterWithCustomName() throws IOException {
        ScannedGenericBeanDefinition scanned = new ScannedGenericBeanDefinition(new SimpleMetadataReaderFactory().getMetadataReader(WebFilterHandlerTests.CustomNameFilter.class.getName()));
        this.handler.handle(scanned, this.registry);
        BeanDefinition filterRegistrationBean = this.registry.getBeanDefinition("custom");
        MutablePropertyValues propertyValues = filterRegistrationBean.getPropertyValues();
        assertThat(propertyValues.get("name")).isEqualTo("custom");
    }

    @Test
    public void asyncSupported() throws IOException {
        BeanDefinition filterRegistrationBean = getBeanDefinition(WebFilterHandlerTests.AsyncSupportedFilter.class);
        MutablePropertyValues propertyValues = filterRegistrationBean.getPropertyValues();
        assertThat(propertyValues.get("asyncSupported")).isEqualTo(true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void dispatcherTypes() throws IOException {
        BeanDefinition filterRegistrationBean = getBeanDefinition(WebFilterHandlerTests.DispatcherTypesFilter.class);
        MutablePropertyValues propertyValues = filterRegistrationBean.getPropertyValues();
        assertThat(((java.util.Set<DispatcherType>) (propertyValues.get("dispatcherTypes")))).containsExactly(FORWARD, INCLUDE, REQUEST);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void initParameters() throws IOException {
        BeanDefinition filterRegistrationBean = getBeanDefinition(WebFilterHandlerTests.InitParametersFilter.class);
        MutablePropertyValues propertyValues = filterRegistrationBean.getPropertyValues();
        assertThat(((java.util.Map<String, String>) (propertyValues.get("initParameters")))).containsEntry("a", "alpha").containsEntry("b", "bravo");
    }

    @Test
    public void servletNames() throws IOException {
        BeanDefinition filterRegistrationBean = getBeanDefinition(WebFilterHandlerTests.ServletNamesFilter.class);
        MutablePropertyValues propertyValues = filterRegistrationBean.getPropertyValues();
        assertThat(((String[]) (propertyValues.get("servletNames")))).contains("alpha", "bravo");
    }

    @Test
    public void urlPatterns() throws IOException {
        BeanDefinition filterRegistrationBean = getBeanDefinition(WebFilterHandlerTests.UrlPatternsFilter.class);
        MutablePropertyValues propertyValues = filterRegistrationBean.getPropertyValues();
        assertThat(((String[]) (propertyValues.get("urlPatterns")))).contains("alpha", "bravo");
    }

    @Test
    public void urlPatternsFromValue() throws IOException {
        BeanDefinition filterRegistrationBean = getBeanDefinition(WebFilterHandlerTests.UrlPatternsFromValueFilter.class);
        MutablePropertyValues propertyValues = filterRegistrationBean.getPropertyValues();
        assertThat(((String[]) (propertyValues.get("urlPatterns")))).contains("alpha", "bravo");
    }

    @Test
    public void urlPatternsDeclaredTwice() throws IOException {
        assertThatIllegalStateException().isThrownBy(() -> getBeanDefinition(.class)).withMessageContaining("The urlPatterns and value attributes are mutually exclusive.");
    }

    @WebFilter
    class DefaultConfigurationFilter extends WebFilterHandlerTests.BaseFilter {}

    @WebFilter(asyncSupported = true)
    class AsyncSupportedFilter extends WebFilterHandlerTests.BaseFilter {}

    @WebFilter(dispatcherTypes = { DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE })
    class DispatcherTypesFilter extends WebFilterHandlerTests.BaseFilter {}

    @WebFilter(initParams = { @WebInitParam(name = "a", value = "alpha"), @WebInitParam(name = "b", value = "bravo") })
    class InitParametersFilter extends WebFilterHandlerTests.BaseFilter {}

    @WebFilter(servletNames = { "alpha", "bravo" })
    class ServletNamesFilter extends WebFilterHandlerTests.BaseFilter {}

    @WebFilter(urlPatterns = { "alpha", "bravo" })
    class UrlPatternsFilter extends WebFilterHandlerTests.BaseFilter {}

    @WebFilter({ "alpha", "bravo" })
    class UrlPatternsFromValueFilter extends WebFilterHandlerTests.BaseFilter {}

    @WebFilter(value = { "alpha", "bravo" }, urlPatterns = { "alpha", "bravo" })
    class UrlPatternsDeclaredTwiceFilter extends WebFilterHandlerTests.BaseFilter {}

    @WebFilter(filterName = "custom")
    class CustomNameFilter extends WebFilterHandlerTests.BaseFilter {}

    class BaseFilter implements Filter {
        @Override
        public void init(FilterConfig filterConfig) {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        }

        @Override
        public void destroy() {
        }
    }
}

