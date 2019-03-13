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
package org.springframework.boot.test.autoconfigure.web.servlet;


import org.junit.Test;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Tests for {@link WebMvcTypeExcludeFilter}.
 *
 * @author Phillip Webb
 */
public class WebMvcTypeExcludeFilterTests {
    private MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();

    @Test
    public void matchWhenHasNoControllers() throws Exception {
        WebMvcTypeExcludeFilter filter = new WebMvcTypeExcludeFilter(WebMvcTypeExcludeFilterTests.WithNoControllers.class);
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.Controller1.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.Controller2.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleControllerAdvice.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleWeb.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleMessageConverter.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleService.class)).isTrue();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleRepository.class)).isTrue();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleWebSecurityConfigurer.class)).isFalse();
    }

    @Test
    public void matchWhenHasController() throws Exception {
        WebMvcTypeExcludeFilter filter = new WebMvcTypeExcludeFilter(WebMvcTypeExcludeFilterTests.WithController.class);
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.Controller1.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.Controller2.class)).isTrue();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleControllerAdvice.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleWeb.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleMessageConverter.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleService.class)).isTrue();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleRepository.class)).isTrue();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleWebSecurityConfigurer.class)).isFalse();
    }

    @Test
    public void matchNotUsingDefaultFilters() throws Exception {
        WebMvcTypeExcludeFilter filter = new WebMvcTypeExcludeFilter(WebMvcTypeExcludeFilterTests.NotUsingDefaultFilters.class);
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.Controller1.class)).isTrue();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.Controller2.class)).isTrue();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleControllerAdvice.class)).isTrue();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleWeb.class)).isTrue();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleMessageConverter.class)).isTrue();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleService.class)).isTrue();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleRepository.class)).isTrue();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleWebSecurityConfigurer.class)).isTrue();
    }

    @Test
    public void matchWithIncludeFilter() throws Exception {
        WebMvcTypeExcludeFilter filter = new WebMvcTypeExcludeFilter(WebMvcTypeExcludeFilterTests.WithIncludeFilter.class);
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.Controller1.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.Controller2.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleControllerAdvice.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleWeb.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleMessageConverter.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleService.class)).isTrue();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleRepository.class)).isFalse();
    }

    @Test
    public void matchWithExcludeFilter() throws Exception {
        WebMvcTypeExcludeFilter filter = new WebMvcTypeExcludeFilter(WebMvcTypeExcludeFilterTests.WithExcludeFilter.class);
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.Controller1.class)).isTrue();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.Controller2.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleControllerAdvice.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleWeb.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleMessageConverter.class)).isFalse();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleService.class)).isTrue();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleRepository.class)).isTrue();
        assertThat(excludes(filter, WebMvcTypeExcludeFilterTests.ExampleWebSecurityConfigurer.class)).isFalse();
    }

    @WebMvcTest
    static class WithNoControllers {}

    @WebMvcTest(WebMvcTypeExcludeFilterTests.Controller1.class)
    static class WithController {}

    @WebMvcTest(useDefaultFilters = false)
    static class NotUsingDefaultFilters {}

    @WebMvcTest(includeFilters = @Filter(Repository.class))
    static class WithIncludeFilter {}

    @WebMvcTest(excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebMvcTypeExcludeFilterTests.Controller1.class))
    static class WithExcludeFilter {}

    @Controller
    static class Controller1 {}

    @Controller
    static class Controller2 {}

    @ControllerAdvice
    static class ExampleControllerAdvice {}

    static class ExampleWeb implements WebMvcConfigurer {}

    static class ExampleMessageConverter extends MappingJackson2HttpMessageConverter {}

    @Service
    static class ExampleService {}

    @Repository
    static class ExampleRepository {}

    static class ExampleWebSecurityConfigurer extends WebSecurityConfigurerAdapter {}
}

