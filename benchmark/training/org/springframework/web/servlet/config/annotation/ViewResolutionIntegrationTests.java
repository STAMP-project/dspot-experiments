/**
 * Copyright 2002-2014 the original author or authors.
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
package org.springframework.web.servlet.config.annotation;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.view.groovy.GroovyMarkupConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;


/**
 * Integration tests for view resolution with {@code @EnableWebMvc}.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 */
public class ViewResolutionIntegrationTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void freemarker() throws Exception {
        MockHttpServletResponse response = runTest(ViewResolutionIntegrationTests.FreeMarkerWebConfig.class);
        Assert.assertEquals("<html><body>Hello World!</body></html>", response.getContentAsString());
    }

    @Test
    public void tiles() throws Exception {
        MockHttpServletResponse response = runTest(ViewResolutionIntegrationTests.TilesWebConfig.class);
        Assert.assertEquals("/WEB-INF/index.jsp", response.getForwardedUrl());
    }

    @Test
    public void groovyMarkup() throws Exception {
        MockHttpServletResponse response = runTest(ViewResolutionIntegrationTests.GroovyMarkupWebConfig.class);
        Assert.assertEquals("<html><body>Hello World!</body></html>", response.getContentAsString());
    }

    @Test
    public void freemarkerInvalidConfig() throws Exception {
        this.thrown.expectMessage("In addition to a FreeMarker view resolver ");
        runTest(ViewResolutionIntegrationTests.InvalidFreeMarkerWebConfig.class);
    }

    @Test
    public void tilesInvalidConfig() throws Exception {
        this.thrown.expectMessage("In addition to a Tiles view resolver ");
        runTest(ViewResolutionIntegrationTests.InvalidTilesWebConfig.class);
    }

    @Test
    public void groovyMarkupInvalidConfig() throws Exception {
        this.thrown.expectMessage("In addition to a Groovy markup view resolver ");
        runTest(ViewResolutionIntegrationTests.InvalidGroovyMarkupWebConfig.class);
    }

    // SPR-12013
    @Test
    public void existingViewResolver() throws Exception {
        MockHttpServletResponse response = runTest(ViewResolutionIntegrationTests.ExistingViewResolverConfig.class);
        Assert.assertEquals("<html><body>Hello World!</body></html>", response.getContentAsString());
    }

    @Controller
    static class SampleController {
        @RequestMapping(value = "/", method = RequestMethod.GET)
        public String sample(ModelMap model) {
            model.addAttribute("hello", "Hello World!");
            return "index";
        }
    }

    @EnableWebMvc
    abstract static class AbstractWebConfig implements WebMvcConfigurer {
        @Bean
        public ViewResolutionIntegrationTests.SampleController sampleController() {
            return new ViewResolutionIntegrationTests.SampleController();
        }
    }

    @Configuration
    static class FreeMarkerWebConfig extends ViewResolutionIntegrationTests.AbstractWebConfig {
        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            registry.freeMarker();
        }

        @Bean
        public FreeMarkerConfigurer freeMarkerConfigurer() {
            FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
            configurer.setTemplateLoaderPath("/WEB-INF/");
            return configurer;
        }
    }

    @Configuration
    static class TilesWebConfig extends ViewResolutionIntegrationTests.AbstractWebConfig {
        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            registry.tiles();
        }

        @Bean
        public TilesConfigurer tilesConfigurer() {
            TilesConfigurer configurer = new TilesConfigurer();
            configurer.setDefinitions("/WEB-INF/tiles.xml");
            return configurer;
        }
    }

    @Configuration
    static class GroovyMarkupWebConfig extends ViewResolutionIntegrationTests.AbstractWebConfig {
        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            registry.groovy();
        }

        @Bean
        public GroovyMarkupConfigurer groovyMarkupConfigurer() {
            GroovyMarkupConfigurer configurer = new GroovyMarkupConfigurer();
            configurer.setResourceLoaderPath("/WEB-INF/");
            return configurer;
        }
    }

    @Configuration
    static class InvalidFreeMarkerWebConfig extends WebMvcConfigurationSupport {
        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            registry.freeMarker();
        }
    }

    @Configuration
    static class InvalidTilesWebConfig extends WebMvcConfigurationSupport {
        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            registry.tiles();
        }
    }

    @Configuration
    static class InvalidGroovyMarkupWebConfig extends WebMvcConfigurationSupport {
        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            registry.groovy();
        }
    }

    /**
     * Test @EnableWebMvc in the presence of pre-existing ViewResolver.
     */
    @Configuration
    static class ExistingViewResolverConfig extends ViewResolutionIntegrationTests.AbstractWebConfig {
        @Bean
        public FreeMarkerViewResolver freeMarkerViewResolver() {
            return new FreeMarkerViewResolver("", ".ftl");
        }

        @Bean
        public FreeMarkerConfigurer freeMarkerConfigurer() {
            FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
            configurer.setTemplateLoaderPath("/WEB-INF/");
            return configurer;
        }
    }
}

