/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.servlet.config;


import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;


/**
 * Test fixture for the configuration in mvc-config-annotation-driven.xml.
 *
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @author Agim Emruli
 */
public class AnnotationDrivenBeanDefinitionParserTests {
    private final GenericWebApplicationContext appContext = new GenericWebApplicationContext();

    @Test
    public void testMessageCodesResolver() {
        loadBeanDefinitions("mvc-config-message-codes-resolver.xml");
        RequestMappingHandlerAdapter adapter = this.appContext.getBean(RequestMappingHandlerAdapter.class);
        Assert.assertNotNull(adapter);
        Object initializer = adapter.getWebBindingInitializer();
        Assert.assertNotNull(initializer);
        MessageCodesResolver resolver = getMessageCodesResolver();
        Assert.assertNotNull(resolver);
        Assert.assertEquals(TestMessageCodesResolver.class, resolver.getClass());
        Assert.assertEquals(false, getPropertyValue("ignoreDefaultModelOnRedirect"));
    }

    @Test
    public void testPathMatchingConfiguration() {
        loadBeanDefinitions("mvc-config-path-matching.xml");
        RequestMappingHandlerMapping hm = this.appContext.getBean(RequestMappingHandlerMapping.class);
        Assert.assertNotNull(hm);
        Assert.assertTrue(hm.useSuffixPatternMatch());
        Assert.assertFalse(hm.useTrailingSlashMatch());
        Assert.assertTrue(hm.useRegisteredSuffixPatternMatch());
        Assert.assertThat(hm.getUrlPathHelper(), Matchers.instanceOf(TestPathHelper.class));
        Assert.assertThat(hm.getPathMatcher(), Matchers.instanceOf(TestPathMatcher.class));
        List<String> fileExtensions = hm.getContentNegotiationManager().getAllFileExtensions();
        Assert.assertThat(fileExtensions, Matchers.contains("xml"));
        Assert.assertThat(fileExtensions, Matchers.hasSize(1));
    }

    @Test
    public void testMessageConverters() {
        loadBeanDefinitions("mvc-config-message-converters.xml");
        verifyMessageConverters(this.appContext.getBean(RequestMappingHandlerAdapter.class), true);
        verifyMessageConverters(this.appContext.getBean(ExceptionHandlerExceptionResolver.class), true);
        verifyRequestResponseBodyAdvice(this.appContext.getBean(RequestMappingHandlerAdapter.class));
        verifyResponseBodyAdvice(this.appContext.getBean(ExceptionHandlerExceptionResolver.class));
    }

    @Test
    public void testMessageConvertersWithoutDefaultRegistrations() {
        loadBeanDefinitions("mvc-config-message-converters-defaults-off.xml");
        verifyMessageConverters(this.appContext.getBean(RequestMappingHandlerAdapter.class), false);
        verifyMessageConverters(this.appContext.getBean(ExceptionHandlerExceptionResolver.class), false);
    }

    @Test
    public void testArgumentResolvers() {
        loadBeanDefinitions("mvc-config-argument-resolvers.xml");
        testArgumentResolvers(this.appContext.getBean(RequestMappingHandlerAdapter.class));
        testArgumentResolvers(this.appContext.getBean(ExceptionHandlerExceptionResolver.class));
    }

    @Test
    public void testReturnValueHandlers() {
        loadBeanDefinitions("mvc-config-return-value-handlers.xml");
        testReturnValueHandlers(this.appContext.getBean(RequestMappingHandlerAdapter.class));
        testReturnValueHandlers(this.appContext.getBean(ExceptionHandlerExceptionResolver.class));
    }

    @Test
    public void beanNameUrlHandlerMapping() {
        loadBeanDefinitions("mvc-config.xml");
        BeanNameUrlHandlerMapping mapping = this.appContext.getBean(BeanNameUrlHandlerMapping.class);
        Assert.assertNotNull(mapping);
        Assert.assertEquals(2, mapping.getOrder());
    }
}

