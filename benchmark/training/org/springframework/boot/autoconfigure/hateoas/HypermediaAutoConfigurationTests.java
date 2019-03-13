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
package org.springframework.boot.autoconfigure.hateoas;


import MediaType.APPLICATION_JSON;
import MediaTypes.HAL_JSON;
import MediaTypes.HAL_JSON_UTF8;
import java.util.Optional;
import org.junit.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration.EntityLinksConfiguration;
import org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration.HypermediaConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.hateoas.mediatype.hal.HalLinkDiscoverer;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;


/**
 * Tests for {@link HypermediaAutoConfiguration}.
 *
 * @author Roy Clarkson
 * @author Oliver Gierke
 * @author Andy Wilkinson
 */
public class HypermediaAutoConfigurationTests {
    private AnnotationConfigWebApplicationContext context;

    @Test
    public void linkDiscoverersCreated() {
        this.context = new AnnotationConfigWebApplicationContext();
        this.context.setServletContext(new MockServletContext());
        this.context.register(HypermediaAutoConfigurationTests.BaseConfig.class);
        this.context.refresh();
        LinkDiscoverers discoverers = this.context.getBean(LinkDiscoverers.class);
        assertThat(discoverers).isNotNull();
        Optional<LinkDiscoverer> discoverer = discoverers.getLinkDiscovererFor(HAL_JSON);
        assertThat(discoverer).containsInstanceOf(HalLinkDiscoverer.class);
    }

    @Test
    public void entityLinksCreated() {
        this.context = new AnnotationConfigWebApplicationContext();
        this.context.setServletContext(new MockServletContext());
        this.context.register(HypermediaAutoConfigurationTests.BaseConfig.class);
        this.context.refresh();
        EntityLinks discoverers = this.context.getBean(EntityLinks.class);
        assertThat(discoverers).isNotNull();
    }

    @Test
    public void doesBackOffIfEnableHypermediaSupportIsDeclaredManually() {
        this.context = new AnnotationConfigWebApplicationContext();
        this.context.setServletContext(new MockServletContext());
        this.context.register(HypermediaAutoConfigurationTests.EnableHypermediaSupportConfig.class, HypermediaAutoConfigurationTests.BaseConfig.class);
        TestPropertyValues.of("spring.jackson.serialization.INDENT_OUTPUT:true").applyTo(this.context);
        this.context.refresh();
        assertThat(this.context.getBeansOfType(HypermediaConfiguration.class)).isEmpty();
        assertThat(this.context.getBeansOfType(EntityLinksConfiguration.class)).isEmpty();
    }

    @Test
    public void supportedMediaTypesOfTypeConstrainedConvertersIsCustomized() {
        this.context = new AnnotationConfigWebApplicationContext();
        this.context.setServletContext(new MockServletContext());
        this.context.register(HypermediaAutoConfigurationTests.BaseConfig.class);
        this.context.refresh();
        RequestMappingHandlerAdapter handlerAdapter = this.context.getBean(RequestMappingHandlerAdapter.class);
        for (HttpMessageConverter<?> converter : handlerAdapter.getMessageConverters()) {
            if (converter instanceof TypeConstrainedMappingJackson2HttpMessageConverter) {
                assertThat(converter.getSupportedMediaTypes()).contains(APPLICATION_JSON, HAL_JSON);
            }
        }
    }

    @Test
    public void customizationOfSupportedMediaTypesCanBeDisabled() {
        this.context = new AnnotationConfigWebApplicationContext();
        this.context.setServletContext(new MockServletContext());
        this.context.register(HypermediaAutoConfigurationTests.BaseConfig.class);
        TestPropertyValues.of("spring.hateoas.use-hal-as-default-json-media-type:false").applyTo(this.context);
        this.context.refresh();
        RequestMappingHandlerAdapter handlerAdapter = this.context.getBean(RequestMappingHandlerAdapter.class);
        for (HttpMessageConverter<?> converter : handlerAdapter.getMessageConverters()) {
            if (converter instanceof TypeConstrainedMappingJackson2HttpMessageConverter) {
                assertThat(converter.getSupportedMediaTypes()).containsExactly(HAL_JSON, HAL_JSON_UTF8);
            }
        }
    }

    @ImportAutoConfiguration({ HttpMessageConvertersAutoConfiguration.class, WebMvcAutoConfiguration.class, JacksonAutoConfiguration.class, HypermediaAutoConfiguration.class })
    static class BaseConfig {}

    @Configuration
    @EnableHypermediaSupport(type = HypermediaType.HAL)
    static class EnableHypermediaSupportConfig {}
}

