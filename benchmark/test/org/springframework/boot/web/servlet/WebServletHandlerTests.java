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


import java.io.IOException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;


/**
 * Tests for {@link WebServletHandler}.
 *
 * @author Andy Wilkinson
 */
public class WebServletHandlerTests {
    private final WebServletHandler handler = new WebServletHandler();

    private final SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();

    @SuppressWarnings("unchecked")
    @Test
    public void defaultServletConfiguration() throws IOException {
        ScannedGenericBeanDefinition scanned = new ScannedGenericBeanDefinition(new SimpleMetadataReaderFactory().getMetadataReader(WebServletHandlerTests.DefaultConfigurationServlet.class.getName()));
        this.handler.handle(scanned, this.registry);
        BeanDefinition servletRegistrationBean = this.registry.getBeanDefinition(WebServletHandlerTests.DefaultConfigurationServlet.class.getName());
        MutablePropertyValues propertyValues = servletRegistrationBean.getPropertyValues();
        assertThat(propertyValues.get("asyncSupported")).isEqualTo(false);
        assertThat(((java.util.Map<String, String>) (propertyValues.get("initParameters")))).isEmpty();
        assertThat(((Integer) (propertyValues.get("loadOnStartup")))).isEqualTo((-1));
        assertThat(propertyValues.get("name")).isEqualTo(WebServletHandlerTests.DefaultConfigurationServlet.class.getName());
        assertThat(((String[]) (propertyValues.get("urlMappings")))).isEmpty();
        assertThat(propertyValues.get("servlet")).isEqualTo(scanned);
    }

    @Test
    public void servletWithCustomName() throws IOException {
        ScannedGenericBeanDefinition scanned = new ScannedGenericBeanDefinition(new SimpleMetadataReaderFactory().getMetadataReader(WebServletHandlerTests.CustomNameServlet.class.getName()));
        this.handler.handle(scanned, this.registry);
        BeanDefinition servletRegistrationBean = this.registry.getBeanDefinition("custom");
        MutablePropertyValues propertyValues = servletRegistrationBean.getPropertyValues();
        assertThat(propertyValues.get("name")).isEqualTo("custom");
    }

    @Test
    public void asyncSupported() throws IOException {
        BeanDefinition servletRegistrationBean = getBeanDefinition(WebServletHandlerTests.AsyncSupportedServlet.class);
        MutablePropertyValues propertyValues = servletRegistrationBean.getPropertyValues();
        assertThat(propertyValues.get("asyncSupported")).isEqualTo(true);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void initParameters() throws IOException {
        BeanDefinition servletRegistrationBean = getBeanDefinition(WebServletHandlerTests.InitParametersServlet.class);
        MutablePropertyValues propertyValues = servletRegistrationBean.getPropertyValues();
        assertThat(((java.util.Map<String, String>) (propertyValues.get("initParameters")))).containsEntry("a", "alpha").containsEntry("b", "bravo");
    }

    @Test
    public void urlMappings() throws IOException {
        BeanDefinition servletRegistrationBean = getBeanDefinition(WebServletHandlerTests.UrlPatternsServlet.class);
        MutablePropertyValues propertyValues = servletRegistrationBean.getPropertyValues();
        assertThat(((String[]) (propertyValues.get("urlMappings")))).contains("alpha", "bravo");
    }

    @Test
    public void urlMappingsFromValue() throws IOException {
        BeanDefinition servletRegistrationBean = getBeanDefinition(WebServletHandlerTests.UrlPatternsFromValueServlet.class);
        MutablePropertyValues propertyValues = servletRegistrationBean.getPropertyValues();
        assertThat(((String[]) (propertyValues.get("urlMappings")))).contains("alpha", "bravo");
    }

    @Test
    public void urlPatternsDeclaredTwice() throws IOException {
        assertThatIllegalStateException().isThrownBy(() -> getBeanDefinition(.class)).withMessageContaining("The urlPatterns and value attributes are mutually exclusive.");
    }

    @WebServlet
    class DefaultConfigurationServlet extends HttpServlet {}

    @WebServlet(asyncSupported = true)
    class AsyncSupportedServlet extends HttpServlet {}

    @WebServlet(initParams = { @WebInitParam(name = "a", value = "alpha"), @WebInitParam(name = "b", value = "bravo") })
    class InitParametersServlet extends HttpServlet {}

    @WebServlet(urlPatterns = { "alpha", "bravo" })
    class UrlPatternsServlet extends HttpServlet {}

    @WebServlet({ "alpha", "bravo" })
    class UrlPatternsFromValueServlet extends HttpServlet {}

    @WebServlet(value = { "alpha", "bravo" }, urlPatterns = { "alpha", "bravo" })
    class UrlPatternsDeclaredTwiceServlet extends HttpServlet {}

    @WebServlet(name = "custom")
    class CustomNameServlet extends HttpServlet {}
}

