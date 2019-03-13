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
package org.springframework.web.reactive.config;


import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.reactive.accept.RequestedContentTypeResolverBuilder;


/**
 * Test fixture for {@link DelegatingWebFluxConfiguration} tests.
 *
 * @author Brian Clozel
 */
public class DelegatingWebFluxConfigurationTests {
    private DelegatingWebFluxConfiguration delegatingConfig;

    @Mock
    private WebFluxConfigurer webFluxConfigurer;

    @Captor
    private ArgumentCaptor<ServerCodecConfigurer> codecsConfigurer;

    @Captor
    private ArgumentCaptor<List<HttpMessageWriter<?>>> writers;

    @Captor
    private ArgumentCaptor<FormatterRegistry> formatterRegistry;

    @Test
    public void requestMappingHandlerMapping() throws Exception {
        delegatingConfig.setConfigurers(Collections.singletonList(webFluxConfigurer));
        delegatingConfig.requestMappingHandlerMapping();
        Mockito.verify(webFluxConfigurer).configureContentTypeResolver(ArgumentMatchers.any(RequestedContentTypeResolverBuilder.class));
        Mockito.verify(webFluxConfigurer).addCorsMappings(ArgumentMatchers.any(CorsRegistry.class));
        Mockito.verify(webFluxConfigurer).configurePathMatching(ArgumentMatchers.any(PathMatchConfigurer.class));
    }

    @Test
    public void requestMappingHandlerAdapter() throws Exception {
        delegatingConfig.setConfigurers(Collections.singletonList(webFluxConfigurer));
        ConfigurableWebBindingInitializer initializer = ((ConfigurableWebBindingInitializer) (this.delegatingConfig.requestMappingHandlerAdapter().getWebBindingInitializer()));
        Mockito.verify(webFluxConfigurer).configureHttpMessageCodecs(codecsConfigurer.capture());
        Mockito.verify(webFluxConfigurer).getValidator();
        Mockito.verify(webFluxConfigurer).getMessageCodesResolver();
        Mockito.verify(webFluxConfigurer).addFormatters(formatterRegistry.capture());
        Mockito.verify(webFluxConfigurer).configureArgumentResolvers(ArgumentMatchers.any());
        Assert.assertNotNull(initializer);
        Assert.assertTrue(((initializer.getValidator()) instanceof LocalValidatorFactoryBean));
        Assert.assertSame(formatterRegistry.getValue(), initializer.getConversionService());
        Assert.assertEquals(13, codecsConfigurer.getValue().getReaders().size());
    }

    @Test
    public void resourceHandlerMapping() throws Exception {
        delegatingConfig.setConfigurers(Collections.singletonList(webFluxConfigurer));
        Mockito.doAnswer(( invocation) -> {
            ResourceHandlerRegistry registry = invocation.getArgument(0);
            registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static");
            return null;
        }).when(webFluxConfigurer).addResourceHandlers(ArgumentMatchers.any(ResourceHandlerRegistry.class));
        delegatingConfig.resourceHandlerMapping();
        Mockito.verify(webFluxConfigurer).addResourceHandlers(ArgumentMatchers.any(ResourceHandlerRegistry.class));
        Mockito.verify(webFluxConfigurer).configurePathMatching(ArgumentMatchers.any(PathMatchConfigurer.class));
    }

    @Test
    public void responseBodyResultHandler() throws Exception {
        delegatingConfig.setConfigurers(Collections.singletonList(webFluxConfigurer));
        delegatingConfig.responseBodyResultHandler();
        Mockito.verify(webFluxConfigurer).configureHttpMessageCodecs(codecsConfigurer.capture());
        Mockito.verify(webFluxConfigurer).configureContentTypeResolver(ArgumentMatchers.any(RequestedContentTypeResolverBuilder.class));
    }

    @Test
    public void viewResolutionResultHandler() throws Exception {
        delegatingConfig.setConfigurers(Collections.singletonList(webFluxConfigurer));
        delegatingConfig.viewResolutionResultHandler();
        Mockito.verify(webFluxConfigurer).configureViewResolvers(ArgumentMatchers.any(ViewResolverRegistry.class));
    }
}

