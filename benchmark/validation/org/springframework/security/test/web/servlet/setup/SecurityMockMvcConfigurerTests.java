/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.security.test.web.servlet.setup;


import BeanIds.SPRING_SECURITY_FILTER_CHAIN;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;
import org.springframework.web.context.WebApplicationContext;


@RunWith(MockitoJUnitRunner.class)
public class SecurityMockMvcConfigurerTests {
    @Mock
    private Filter filter;

    @Mock
    private Filter beanFilter;

    @Mock
    private ConfigurableMockMvcBuilder<?> builder;

    @Mock
    private WebApplicationContext context;

    @Mock
    private ServletContext servletContext;

    @Test
    public void beforeMockMvcCreatedOverrideBean() throws Exception {
        returnFilterBean();
        SecurityMockMvcConfigurer configurer = new SecurityMockMvcConfigurer(this.filter);
        configurer.beforeMockMvcCreated(this.builder, this.context);
        Mockito.verify(this.builder).addFilters(this.filter);
        Mockito.verify(this.servletContext).setAttribute(SPRING_SECURITY_FILTER_CHAIN, this.filter);
    }

    @Test
    public void beforeMockMvcCreatedBean() throws Exception {
        returnFilterBean();
        SecurityMockMvcConfigurer configurer = new SecurityMockMvcConfigurer();
        configurer.beforeMockMvcCreated(this.builder, this.context);
        Mockito.verify(this.builder).addFilters(this.beanFilter);
    }

    @Test
    public void beforeMockMvcCreatedNoBean() throws Exception {
        SecurityMockMvcConfigurer configurer = new SecurityMockMvcConfigurer(this.filter);
        configurer.beforeMockMvcCreated(this.builder, this.context);
        Mockito.verify(this.builder).addFilters(this.filter);
    }

    @Test(expected = IllegalStateException.class)
    public void beforeMockMvcCreatedNoFilter() throws Exception {
        SecurityMockMvcConfigurer configurer = new SecurityMockMvcConfigurer();
        configurer.beforeMockMvcCreated(this.builder, this.context);
    }
}

