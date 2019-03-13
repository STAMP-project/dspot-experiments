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
package org.springframework.web.servlet.config.annotation;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.PathMatcher;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.util.UrlPathHelper;


/**
 * A test fixture for {@link DelegatingWebMvcConfiguration} tests.
 *
 * @author Rossen Stoyanchev
 */
public class DelegatingWebMvcConfigurationTests {
    private DelegatingWebMvcConfiguration delegatingConfig;

    @Mock
    private WebMvcConfigurer webMvcConfigurer;

    @Captor
    private ArgumentCaptor<List<HttpMessageConverter<?>>> converters;

    @Captor
    private ArgumentCaptor<ContentNegotiationConfigurer> contentNegotiationConfigurer;

    @Captor
    private ArgumentCaptor<FormattingConversionService> conversionService;

    @Captor
    private ArgumentCaptor<List<HandlerMethodArgumentResolver>> resolvers;

    @Captor
    private ArgumentCaptor<List<HandlerMethodReturnValueHandler>> handlers;

    @Captor
    private ArgumentCaptor<AsyncSupportConfigurer> asyncConfigurer;

    @Captor
    private ArgumentCaptor<List<HandlerExceptionResolver>> exceptionResolvers;

    @Test
    public void requestMappingHandlerAdapter() throws Exception {
        delegatingConfig.setConfigurers(Collections.singletonList(webMvcConfigurer));
        RequestMappingHandlerAdapter adapter = this.delegatingConfig.requestMappingHandlerAdapter();
        ConfigurableWebBindingInitializer initializer = ((ConfigurableWebBindingInitializer) (adapter.getWebBindingInitializer()));
        Mockito.verify(webMvcConfigurer).configureMessageConverters(converters.capture());
        Mockito.verify(webMvcConfigurer).configureContentNegotiation(contentNegotiationConfigurer.capture());
        Mockito.verify(webMvcConfigurer).addFormatters(conversionService.capture());
        Mockito.verify(webMvcConfigurer).addArgumentResolvers(resolvers.capture());
        Mockito.verify(webMvcConfigurer).addReturnValueHandlers(handlers.capture());
        Mockito.verify(webMvcConfigurer).configureAsyncSupport(asyncConfigurer.capture());
        Assert.assertNotNull(initializer);
        Assert.assertSame(conversionService.getValue(), initializer.getConversionService());
        Assert.assertTrue(((initializer.getValidator()) instanceof LocalValidatorFactoryBean));
        Assert.assertEquals(0, resolvers.getValue().size());
        Assert.assertEquals(0, handlers.getValue().size());
        Assert.assertEquals(converters.getValue(), adapter.getMessageConverters());
        Assert.assertNotNull(asyncConfigurer);
    }

    @Test
    public void configureMessageConverters() {
        final HttpMessageConverter<?> customConverter = Mockito.mock(HttpMessageConverter.class);
        final StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        List<WebMvcConfigurer> configurers = new ArrayList<>();
        configurers.add(new WebMvcConfigurer() {
            @Override
            public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
                converters.add(stringConverter);
            }

            @Override
            public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
                converters.add(0, customConverter);
            }
        });
        delegatingConfig = new DelegatingWebMvcConfiguration();
        delegatingConfig.setConfigurers(configurers);
        RequestMappingHandlerAdapter adapter = delegatingConfig.requestMappingHandlerAdapter();
        Assert.assertEquals("Only one custom converter should be registered", 2, adapter.getMessageConverters().size());
        Assert.assertSame(customConverter, adapter.getMessageConverters().get(0));
        Assert.assertSame(stringConverter, adapter.getMessageConverters().get(1));
    }

    @Test
    public void getCustomValidator() {
        BDDMockito.given(webMvcConfigurer.getValidator()).willReturn(new LocalValidatorFactoryBean());
        delegatingConfig.setConfigurers(Collections.singletonList(webMvcConfigurer));
        delegatingConfig.mvcValidator();
        Mockito.verify(webMvcConfigurer).getValidator();
    }

    @Test
    public void getCustomMessageCodesResolver() {
        BDDMockito.given(webMvcConfigurer.getMessageCodesResolver()).willReturn(new DefaultMessageCodesResolver());
        delegatingConfig.setConfigurers(Collections.singletonList(webMvcConfigurer));
        delegatingConfig.getMessageCodesResolver();
        Mockito.verify(webMvcConfigurer).getMessageCodesResolver();
    }

    @Test
    public void handlerExceptionResolver() throws Exception {
        delegatingConfig.setConfigurers(Collections.singletonList(webMvcConfigurer));
        delegatingConfig.handlerExceptionResolver();
        Mockito.verify(webMvcConfigurer).configureMessageConverters(converters.capture());
        Mockito.verify(webMvcConfigurer).configureContentNegotiation(contentNegotiationConfigurer.capture());
        Mockito.verify(webMvcConfigurer).configureHandlerExceptionResolvers(exceptionResolvers.capture());
        Assert.assertEquals(3, exceptionResolvers.getValue().size());
        Assert.assertTrue(((exceptionResolvers.getValue().get(0)) instanceof ExceptionHandlerExceptionResolver));
        Assert.assertTrue(((exceptionResolvers.getValue().get(1)) instanceof ResponseStatusExceptionResolver));
        Assert.assertTrue(((exceptionResolvers.getValue().get(2)) instanceof DefaultHandlerExceptionResolver));
        Assert.assertTrue(((converters.getValue().size()) > 0));
    }

    @Test
    public void configureExceptionResolvers() throws Exception {
        List<WebMvcConfigurer> configurers = new ArrayList<>();
        configurers.add(new WebMvcConfigurer() {
            @Override
            public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
                exceptionResolvers.add(new DefaultHandlerExceptionResolver());
            }
        });
        delegatingConfig.setConfigurers(configurers);
        HandlerExceptionResolverComposite composite = ((HandlerExceptionResolverComposite) (delegatingConfig.handlerExceptionResolver()));
        Assert.assertEquals("Only one custom converter is expected", 1, composite.getExceptionResolvers().size());
    }

    @Test
    public void configurePathMatch() throws Exception {
        final PathMatcher pathMatcher = Mockito.mock(PathMatcher.class);
        final UrlPathHelper pathHelper = Mockito.mock(UrlPathHelper.class);
        List<WebMvcConfigurer> configurers = new ArrayList<>();
        configurers.add(new WebMvcConfigurer() {
            @Override
            public void configurePathMatch(PathMatchConfigurer configurer) {
                configurer.setUseRegisteredSuffixPatternMatch(true).setUseTrailingSlashMatch(false).setUrlPathHelper(pathHelper).setPathMatcher(pathMatcher);
            }
        });
        delegatingConfig.setConfigurers(configurers);
        RequestMappingHandlerMapping handlerMapping = delegatingConfig.requestMappingHandlerMapping();
        Assert.assertNotNull(handlerMapping);
        Assert.assertEquals("PathMatchConfigurer should configure RegisteredSuffixPatternMatch", true, handlerMapping.useRegisteredSuffixPatternMatch());
        Assert.assertEquals("PathMatchConfigurer should configure SuffixPatternMatch", true, handlerMapping.useSuffixPatternMatch());
        Assert.assertEquals("PathMatchConfigurer should configure TrailingSlashMatch", false, handlerMapping.useTrailingSlashMatch());
        Assert.assertEquals("PathMatchConfigurer should configure UrlPathHelper", pathHelper, handlerMapping.getUrlPathHelper());
        Assert.assertEquals("PathMatchConfigurer should configure PathMatcher", pathMatcher, handlerMapping.getPathMatcher());
    }
}

