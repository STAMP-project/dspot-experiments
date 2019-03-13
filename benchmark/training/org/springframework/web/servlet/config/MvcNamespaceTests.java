/**
 * Copyright 2002-2017 the original author or authors.
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


import MediaType.APPLICATION_JSON;
import MediaType.TEXT_PLAIN_VALUE;
import MvcUriComponentsBuilder.MVC_URI_COMPONENTS_CONTRIBUTOR_BEAN_NAME;
import NumberFormat.Style;
import Ordered.HIGHEST_PRECEDENCE;
import Ordered.LOWEST_PRECEDENCE;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.servlet.RequestDispatcher;
import javax.validation.constraints.NotNull;
import org.apache.tiles.definition.UnresolvingLocaleDefinitionsFactory;
import org.hamcrest.Matchers;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.TypeMismatchException;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.core.Ordered;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockRequestDispatcher;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.util.PathMatcher;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.CompositeUriComponentsContributor;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.ConversionServiceExposingInterceptor;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.handler.MappedInterceptor;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.resource.AppCacheManifestTransformer;
import org.springframework.web.servlet.resource.CachingResourceResolver;
import org.springframework.web.servlet.resource.CachingResourceTransformer;
import org.springframework.web.servlet.resource.ContentVersionStrategy;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.FixedVersionStrategy;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceTransformer;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.resource.ResourceUrlProviderExposingInterceptor;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.resource.WebJarsResourceResolver;
import org.springframework.web.servlet.theme.ThemeChangeInterceptor;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.view.ViewResolverComposite;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.servlet.view.groovy.GroovyMarkupConfigurer;
import org.springframework.web.servlet.view.groovy.GroovyMarkupViewResolver;
import org.springframework.web.servlet.view.script.ScriptTemplateConfigurer;
import org.springframework.web.servlet.view.script.ScriptTemplateViewResolver;
import org.springframework.web.servlet.view.tiles3.SpringBeanPreparerFactory;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;
import org.springframework.web.util.UrlPathHelper;


/**
 * Tests loading actual MVC namespace configuration.
 *
 * @author Keith Donald
 * @author Arjen Poutsma
 * @author Jeremy Grelle
 * @author Brian Clozel
 * @author Sebastien Deleuze
 * @author Kazuki Shimizu
 * @author Sam Brannen
 */
public class MvcNamespaceTests {
    public static final String VIEWCONTROLLER_BEAN_NAME = "org.springframework.web.servlet.config.viewControllerHandlerMapping";

    private XmlWebApplicationContext appContext;

    private MvcNamespaceTests.TestController handler;

    private HandlerMethod handlerMethod;

    @Test
    public void testDefaultConfig() throws Exception {
        loadBeanDefinitions("mvc-config.xml");
        RequestMappingHandlerMapping mapping = appContext.getBean(RequestMappingHandlerMapping.class);
        Assert.assertNotNull(mapping);
        Assert.assertEquals(0, mapping.getOrder());
        Assert.assertTrue(mapping.getUrlPathHelper().shouldRemoveSemicolonContent());
        mapping.setDefaultHandler(handlerMethod);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo.json");
        NativeWebRequest webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        ContentNegotiationManager manager = mapping.getContentNegotiationManager();
        Assert.assertEquals(Collections.singletonList(APPLICATION_JSON), manager.resolveMediaTypes(webRequest));
        RequestMappingHandlerAdapter adapter = appContext.getBean(RequestMappingHandlerAdapter.class);
        Assert.assertNotNull(adapter);
        Assert.assertEquals(false, getPropertyValue("ignoreDefaultModelOnRedirect"));
        List<HttpMessageConverter<?>> converters = adapter.getMessageConverters();
        Assert.assertTrue(((converters.size()) > 0));
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof AbstractJackson2HttpMessageConverter) {
                ObjectMapper objectMapper = getObjectMapper();
                Assert.assertFalse(objectMapper.getDeserializationConfig().isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION));
                Assert.assertFalse(objectMapper.getSerializationConfig().isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION));
                Assert.assertFalse(objectMapper.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
                if (converter instanceof MappingJackson2XmlHttpMessageConverter) {
                    Assert.assertEquals(XmlMapper.class, objectMapper.getClass());
                }
            }
        }
        Assert.assertNotNull(appContext.getBean(FormattingConversionServiceFactoryBean.class));
        Assert.assertNotNull(appContext.getBean(ConversionService.class));
        Assert.assertNotNull(appContext.getBean(LocalValidatorFactoryBean.class));
        Assert.assertNotNull(appContext.getBean(Validator.class));
        // default web binding initializer behavior test
        request = new MockHttpServletRequest("GET", "/");
        request.addParameter("date", "2009-10-31");
        request.addParameter("percent", "99.99%");
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecutionChain chain = mapping.getHandler(request);
        Assert.assertEquals(1, chain.getInterceptors().length);
        Assert.assertTrue(((chain.getInterceptors()[0]) instanceof ConversionServiceExposingInterceptor));
        ConversionServiceExposingInterceptor interceptor = ((ConversionServiceExposingInterceptor) (chain.getInterceptors()[0]));
        interceptor.preHandle(request, response, handlerMethod);
        Assert.assertSame(appContext.getBean(ConversionService.class), request.getAttribute(ConversionService.class.getName()));
        adapter.handle(request, response, handlerMethod);
        Assert.assertTrue(handler.recordedValidationError);
        Assert.assertEquals(LocalDate.parse("2009-10-31").toDate(), handler.date);
        Assert.assertEquals(Double.valueOf(0.9999), handler.percent);
        CompositeUriComponentsContributor uriComponentsContributor = this.appContext.getBean(MVC_URI_COMPONENTS_CONTRIBUTOR_BEAN_NAME, CompositeUriComponentsContributor.class);
        Assert.assertNotNull(uriComponentsContributor);
        String name = "mvcHandlerMappingIntrospector";
        HandlerMappingIntrospector introspector = this.appContext.getBean(name, HandlerMappingIntrospector.class);
        Assert.assertNotNull(introspector);
        Assert.assertEquals(2, introspector.getHandlerMappings().size());
        Assert.assertSame(mapping, introspector.getHandlerMappings().get(0));
        Assert.assertEquals(BeanNameUrlHandlerMapping.class, introspector.getHandlerMappings().get(1).getClass());
    }

    @Test(expected = TypeMismatchException.class)
    public void testCustomConversionService() throws Exception {
        loadBeanDefinitions("mvc-config-custom-conversion-service.xml");
        RequestMappingHandlerMapping mapping = appContext.getBean(RequestMappingHandlerMapping.class);
        Assert.assertNotNull(mapping);
        mapping.setDefaultHandler(handlerMethod);
        // default web binding initializer behavior test
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        request.setRequestURI("/accounts/12345");
        request.addParameter("date", "2009-10-31");
        MockHttpServletResponse response = new MockHttpServletResponse();
        HandlerExecutionChain chain = mapping.getHandler(request);
        Assert.assertEquals(1, chain.getInterceptors().length);
        Assert.assertTrue(((chain.getInterceptors()[0]) instanceof ConversionServiceExposingInterceptor));
        ConversionServiceExposingInterceptor interceptor = ((ConversionServiceExposingInterceptor) (chain.getInterceptors()[0]));
        interceptor.preHandle(request, response, handler);
        Assert.assertSame(appContext.getBean("conversionService"), request.getAttribute(ConversionService.class.getName()));
        RequestMappingHandlerAdapter adapter = appContext.getBean(RequestMappingHandlerAdapter.class);
        Assert.assertNotNull(adapter);
        adapter.handle(request, response, handlerMethod);
    }

    @Test
    public void testCustomValidator() throws Exception {
        doTestCustomValidator("mvc-config-custom-validator.xml");
    }

    @Test
    public void testInterceptors() throws Exception {
        loadBeanDefinitions("mvc-config-interceptors.xml");
        RequestMappingHandlerMapping mapping = appContext.getBean(RequestMappingHandlerMapping.class);
        Assert.assertNotNull(mapping);
        mapping.setDefaultHandler(handlerMethod);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        request.setRequestURI("/accounts/12345");
        request.addParameter("locale", "en");
        request.addParameter("theme", "green");
        HandlerExecutionChain chain = mapping.getHandler(request);
        Assert.assertEquals(4, chain.getInterceptors().length);
        Assert.assertTrue(((chain.getInterceptors()[0]) instanceof ConversionServiceExposingInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[1]) instanceof LocaleChangeInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[2]) instanceof ThemeChangeInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[3]) instanceof UserRoleAuthorizationInterceptor));
        request.setRequestURI("/admin/users");
        chain = mapping.getHandler(request);
        Assert.assertEquals(2, chain.getInterceptors().length);
        request.setRequestURI("/logged/accounts/12345");
        chain = mapping.getHandler(request);
        Assert.assertEquals(3, chain.getInterceptors().length);
        request.setRequestURI("/foo/logged");
        chain = mapping.getHandler(request);
        Assert.assertEquals(3, chain.getInterceptors().length);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResources() throws Exception {
        loadBeanDefinitions("mvc-config-resources.xml");
        HttpRequestHandlerAdapter adapter = appContext.getBean(HttpRequestHandlerAdapter.class);
        Assert.assertNotNull(adapter);
        RequestMappingHandlerMapping mapping = appContext.getBean(RequestMappingHandlerMapping.class);
        ContentNegotiationManager manager = mapping.getContentNegotiationManager();
        ResourceHttpRequestHandler handler = appContext.getBean(ResourceHttpRequestHandler.class);
        Assert.assertNotNull(handler);
        Assert.assertSame(manager, handler.getContentNegotiationManager());
        SimpleUrlHandlerMapping resourceMapping = appContext.getBean(SimpleUrlHandlerMapping.class);
        Assert.assertNotNull(resourceMapping);
        Assert.assertEquals(((Ordered.LOWEST_PRECEDENCE) - 1), resourceMapping.getOrder());
        BeanNameUrlHandlerMapping beanNameMapping = appContext.getBean(BeanNameUrlHandlerMapping.class);
        Assert.assertNotNull(beanNameMapping);
        Assert.assertEquals(2, beanNameMapping.getOrder());
        ResourceUrlProvider urlProvider = appContext.getBean(ResourceUrlProvider.class);
        Assert.assertNotNull(urlProvider);
        Map<String, MappedInterceptor> beans = appContext.getBeansOfType(MappedInterceptor.class);
        List<Class<?>> interceptors = beans.values().stream().map(( mappedInterceptor) -> mappedInterceptor.getInterceptor().getClass()).collect(Collectors.toList());
        Assert.assertThat(interceptors, containsInAnyOrder(ConversionServiceExposingInterceptor.class, ResourceUrlProviderExposingInterceptor.class));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/resources/foo.css");
        request.setMethod("GET");
        HandlerExecutionChain chain = resourceMapping.getHandler(request);
        Assert.assertNotNull(chain);
        Assert.assertTrue(((chain.getHandler()) instanceof ResourceHttpRequestHandler));
        MockHttpServletResponse response = new MockHttpServletResponse();
        for (HandlerInterceptor interceptor : chain.getInterceptors()) {
            interceptor.preHandle(request, response, chain.getHandler());
        }
        ModelAndView mv = adapter.handle(request, response, chain.getHandler());
        Assert.assertNull(mv);
    }

    @Test
    public void testResourcesWithOptionalAttributes() throws Exception {
        loadBeanDefinitions("mvc-config-resources-optional-attrs.xml");
        SimpleUrlHandlerMapping mapping = appContext.getBean(SimpleUrlHandlerMapping.class);
        Assert.assertNotNull(mapping);
        Assert.assertEquals(5, mapping.getOrder());
        Assert.assertNotNull(mapping.getUrlMap().get("/resources/**"));
        ResourceHttpRequestHandler handler = appContext.getBean(((String) (mapping.getUrlMap().get("/resources/**"))), ResourceHttpRequestHandler.class);
        Assert.assertNotNull(handler);
        Assert.assertEquals(3600, handler.getCacheSeconds());
    }

    @Test
    public void testResourcesWithResolversTransformers() throws Exception {
        loadBeanDefinitions("mvc-config-resources-chain.xml");
        SimpleUrlHandlerMapping mapping = appContext.getBean(SimpleUrlHandlerMapping.class);
        Assert.assertNotNull(mapping);
        Assert.assertNotNull(mapping.getUrlMap().get("/resources/**"));
        String beanName = ((String) (mapping.getUrlMap().get("/resources/**")));
        ResourceHttpRequestHandler handler = appContext.getBean(beanName, ResourceHttpRequestHandler.class);
        Assert.assertNotNull(handler);
        Assert.assertNotNull(handler.getUrlPathHelper());
        List<ResourceResolver> resolvers = handler.getResourceResolvers();
        Assert.assertThat(resolvers, Matchers.hasSize(4));
        Assert.assertThat(resolvers.get(0), Matchers.instanceOf(CachingResourceResolver.class));
        Assert.assertThat(resolvers.get(1), Matchers.instanceOf(VersionResourceResolver.class));
        Assert.assertThat(resolvers.get(2), Matchers.instanceOf(WebJarsResourceResolver.class));
        Assert.assertThat(resolvers.get(3), Matchers.instanceOf(PathResourceResolver.class));
        CachingResourceResolver cachingResolver = ((CachingResourceResolver) (resolvers.get(0)));
        Assert.assertThat(cachingResolver.getCache(), Matchers.instanceOf(ConcurrentMapCache.class));
        Assert.assertEquals("test-resource-cache", cachingResolver.getCache().getName());
        VersionResourceResolver versionResolver = ((VersionResourceResolver) (resolvers.get(1)));
        Assert.assertThat(versionResolver.getStrategyMap().get("/**/*.js"), Matchers.instanceOf(FixedVersionStrategy.class));
        Assert.assertThat(versionResolver.getStrategyMap().get("/**"), Matchers.instanceOf(ContentVersionStrategy.class));
        PathResourceResolver pathResolver = ((PathResourceResolver) (resolvers.get(3)));
        Map<Resource, Charset> locationCharsets = pathResolver.getLocationCharsets();
        Assert.assertEquals(1, locationCharsets.size());
        Assert.assertEquals(StandardCharsets.ISO_8859_1, locationCharsets.values().iterator().next());
        List<ResourceTransformer> transformers = handler.getResourceTransformers();
        Assert.assertThat(transformers, Matchers.hasSize(3));
        Assert.assertThat(transformers.get(0), Matchers.instanceOf(CachingResourceTransformer.class));
        Assert.assertThat(transformers.get(1), Matchers.instanceOf(CssLinkResourceTransformer.class));
        Assert.assertThat(transformers.get(2), Matchers.instanceOf(AppCacheManifestTransformer.class));
        CachingResourceTransformer cachingTransformer = ((CachingResourceTransformer) (transformers.get(0)));
        Assert.assertThat(cachingTransformer.getCache(), Matchers.instanceOf(ConcurrentMapCache.class));
        Assert.assertEquals("test-resource-cache", cachingTransformer.getCache().getName());
    }

    @Test
    public void testResourcesWithResolversTransformersCustom() throws Exception {
        loadBeanDefinitions("mvc-config-resources-chain-no-auto.xml");
        SimpleUrlHandlerMapping mapping = appContext.getBean(SimpleUrlHandlerMapping.class);
        Assert.assertNotNull(mapping);
        Assert.assertNotNull(mapping.getUrlMap().get("/resources/**"));
        ResourceHttpRequestHandler handler = appContext.getBean(((String) (mapping.getUrlMap().get("/resources/**"))), ResourceHttpRequestHandler.class);
        Assert.assertNotNull(handler);
        Assert.assertThat(handler.getCacheControl().getHeaderValue(), Matchers.equalTo(CacheControl.maxAge(1, TimeUnit.HOURS).sMaxAge(30, TimeUnit.MINUTES).cachePublic().getHeaderValue()));
        List<ResourceResolver> resolvers = handler.getResourceResolvers();
        Assert.assertThat(resolvers, Matchers.hasSize(3));
        Assert.assertThat(resolvers.get(0), Matchers.instanceOf(VersionResourceResolver.class));
        Assert.assertThat(resolvers.get(1), Matchers.instanceOf(EncodedResourceResolver.class));
        Assert.assertThat(resolvers.get(2), Matchers.instanceOf(PathResourceResolver.class));
        VersionResourceResolver versionResolver = ((VersionResourceResolver) (resolvers.get(0)));
        Assert.assertThat(versionResolver.getStrategyMap().get("/**/*.js"), Matchers.instanceOf(FixedVersionStrategy.class));
        Assert.assertThat(versionResolver.getStrategyMap().get("/**"), Matchers.instanceOf(ContentVersionStrategy.class));
        List<ResourceTransformer> transformers = handler.getResourceTransformers();
        Assert.assertThat(transformers, Matchers.hasSize(2));
        Assert.assertThat(transformers.get(0), Matchers.instanceOf(CachingResourceTransformer.class));
        Assert.assertThat(transformers.get(1), Matchers.instanceOf(AppCacheManifestTransformer.class));
    }

    @Test
    public void testDefaultServletHandler() throws Exception {
        loadBeanDefinitions("mvc-config-default-servlet.xml");
        HttpRequestHandlerAdapter adapter = appContext.getBean(HttpRequestHandlerAdapter.class);
        Assert.assertNotNull(adapter);
        DefaultServletHttpRequestHandler handler = appContext.getBean(DefaultServletHttpRequestHandler.class);
        Assert.assertNotNull(handler);
        SimpleUrlHandlerMapping mapping = appContext.getBean(SimpleUrlHandlerMapping.class);
        Assert.assertNotNull(mapping);
        Assert.assertEquals(LOWEST_PRECEDENCE, mapping.getOrder());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/foo.css");
        request.setMethod("GET");
        HandlerExecutionChain chain = mapping.getHandler(request);
        Assert.assertTrue(((chain.getHandler()) instanceof DefaultServletHttpRequestHandler));
        MockHttpServletResponse response = new MockHttpServletResponse();
        ModelAndView mv = adapter.handle(request, response, chain.getHandler());
        Assert.assertNull(mv);
    }

    @Test
    public void testDefaultServletHandlerWithOptionalAttributes() throws Exception {
        loadBeanDefinitions("mvc-config-default-servlet-optional-attrs.xml");
        HttpRequestHandlerAdapter adapter = appContext.getBean(HttpRequestHandlerAdapter.class);
        Assert.assertNotNull(adapter);
        DefaultServletHttpRequestHandler handler = appContext.getBean(DefaultServletHttpRequestHandler.class);
        Assert.assertNotNull(handler);
        SimpleUrlHandlerMapping mapping = appContext.getBean(SimpleUrlHandlerMapping.class);
        Assert.assertNotNull(mapping);
        Assert.assertEquals(LOWEST_PRECEDENCE, mapping.getOrder());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/foo.css");
        request.setMethod("GET");
        HandlerExecutionChain chain = mapping.getHandler(request);
        Assert.assertTrue(((chain.getHandler()) instanceof DefaultServletHttpRequestHandler));
        MockHttpServletResponse response = new MockHttpServletResponse();
        ModelAndView mv = adapter.handle(request, response, chain.getHandler());
        Assert.assertNull(mv);
    }

    @Test
    public void testBeanDecoration() throws Exception {
        loadBeanDefinitions("mvc-config-bean-decoration.xml");
        RequestMappingHandlerMapping mapping = appContext.getBean(RequestMappingHandlerMapping.class);
        Assert.assertNotNull(mapping);
        mapping.setDefaultHandler(handlerMethod);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        HandlerExecutionChain chain = mapping.getHandler(request);
        Assert.assertEquals(3, chain.getInterceptors().length);
        Assert.assertTrue(((chain.getInterceptors()[0]) instanceof ConversionServiceExposingInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[1]) instanceof LocaleChangeInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[2]) instanceof ThemeChangeInterceptor));
        LocaleChangeInterceptor interceptor = ((LocaleChangeInterceptor) (chain.getInterceptors()[1]));
        Assert.assertEquals("lang", interceptor.getParamName());
        ThemeChangeInterceptor interceptor2 = ((ThemeChangeInterceptor) (chain.getInterceptors()[2]));
        Assert.assertEquals("style", interceptor2.getParamName());
    }

    @Test
    public void testViewControllers() throws Exception {
        loadBeanDefinitions("mvc-config-view-controllers.xml");
        RequestMappingHandlerMapping mapping = appContext.getBean(RequestMappingHandlerMapping.class);
        Assert.assertNotNull(mapping);
        mapping.setDefaultHandler(handlerMethod);
        BeanNameUrlHandlerMapping beanNameMapping = appContext.getBean(BeanNameUrlHandlerMapping.class);
        Assert.assertNotNull(beanNameMapping);
        Assert.assertEquals(2, beanNameMapping.getOrder());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        HandlerExecutionChain chain = mapping.getHandler(request);
        Assert.assertEquals(3, chain.getInterceptors().length);
        Assert.assertTrue(((chain.getInterceptors()[0]) instanceof ConversionServiceExposingInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[1]) instanceof LocaleChangeInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[2]) instanceof ThemeChangeInterceptor));
        SimpleUrlHandlerMapping mapping2 = appContext.getBean(SimpleUrlHandlerMapping.class);
        Assert.assertNotNull(mapping2);
        SimpleControllerHandlerAdapter adapter = appContext.getBean(SimpleControllerHandlerAdapter.class);
        Assert.assertNotNull(adapter);
        request = new MockHttpServletRequest("GET", "/foo");
        chain = mapping2.getHandler(request);
        Assert.assertEquals(4, chain.getInterceptors().length);
        Assert.assertTrue(((chain.getInterceptors()[1]) instanceof ConversionServiceExposingInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[2]) instanceof LocaleChangeInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[3]) instanceof ThemeChangeInterceptor));
        ModelAndView mv = adapter.handle(request, new MockHttpServletResponse(), chain.getHandler());
        Assert.assertNull(mv.getViewName());
        request = new MockHttpServletRequest("GET", "/myapp/app/bar");
        request.setContextPath("/myapp");
        request.setServletPath("/app");
        chain = mapping2.getHandler(request);
        Assert.assertEquals(4, chain.getInterceptors().length);
        Assert.assertTrue(((chain.getInterceptors()[1]) instanceof ConversionServiceExposingInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[2]) instanceof LocaleChangeInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[3]) instanceof ThemeChangeInterceptor));
        mv = adapter.handle(request, new MockHttpServletResponse(), chain.getHandler());
        Assert.assertEquals("baz", mv.getViewName());
        request = new MockHttpServletRequest("GET", "/myapp/app/");
        request.setContextPath("/myapp");
        request.setServletPath("/app");
        chain = mapping2.getHandler(request);
        Assert.assertEquals(4, chain.getInterceptors().length);
        Assert.assertTrue(((chain.getInterceptors()[1]) instanceof ConversionServiceExposingInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[2]) instanceof LocaleChangeInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[3]) instanceof ThemeChangeInterceptor));
        mv = adapter.handle(request, new MockHttpServletResponse(), chain.getHandler());
        Assert.assertEquals("root", mv.getViewName());
        request = new MockHttpServletRequest("GET", "/myapp/app/old");
        request.setContextPath("/myapp");
        request.setServletPath("/app");
        request.setQueryString("a=b");
        chain = mapping2.getHandler(request);
        mv = adapter.handle(request, new MockHttpServletResponse(), chain.getHandler());
        Assert.assertNotNull(mv.getView());
        Assert.assertEquals(RedirectView.class, mv.getView().getClass());
        RedirectView redirectView = ((RedirectView) (mv.getView()));
        MockHttpServletResponse response = new MockHttpServletResponse();
        redirectView.render(Collections.emptyMap(), request, response);
        Assert.assertEquals("/new?a=b", response.getRedirectedUrl());
        Assert.assertEquals(308, response.getStatus());
        request = new MockHttpServletRequest("GET", "/bad");
        chain = mapping2.getHandler(request);
        response = new MockHttpServletResponse();
        mv = adapter.handle(request, response, chain.getHandler());
        Assert.assertNull(mv);
        Assert.assertEquals(404, response.getStatus());
    }

    /**
     * WebSphere gives trailing servlet path slashes by default!!
     */
    @Test
    public void testViewControllersOnWebSphere() throws Exception {
        loadBeanDefinitions("mvc-config-view-controllers.xml");
        SimpleUrlHandlerMapping mapping2 = appContext.getBean(SimpleUrlHandlerMapping.class);
        SimpleControllerHandlerAdapter adapter = appContext.getBean(SimpleControllerHandlerAdapter.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.setRequestURI("/myapp/app/bar");
        request.setContextPath("/myapp");
        request.setServletPath("/app/");
        request.setAttribute("com.ibm.websphere.servlet.uri_non_decoded", "/myapp/app/bar");
        HandlerExecutionChain chain = mapping2.getHandler(request);
        Assert.assertEquals(4, chain.getInterceptors().length);
        Assert.assertTrue(((chain.getInterceptors()[1]) instanceof ConversionServiceExposingInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[2]) instanceof LocaleChangeInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[3]) instanceof ThemeChangeInterceptor));
        ModelAndView mv2 = adapter.handle(request, new MockHttpServletResponse(), chain.getHandler());
        Assert.assertEquals("baz", mv2.getViewName());
        request.setRequestURI("/myapp/app/");
        request.setContextPath("/myapp");
        request.setServletPath("/app/");
        chain = mapping2.getHandler(request);
        Assert.assertEquals(4, chain.getInterceptors().length);
        Assert.assertTrue(((chain.getInterceptors()[1]) instanceof ConversionServiceExposingInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[2]) instanceof LocaleChangeInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[3]) instanceof ThemeChangeInterceptor));
        ModelAndView mv3 = adapter.handle(request, new MockHttpServletResponse(), chain.getHandler());
        Assert.assertEquals("root", mv3.getViewName());
        request.setRequestURI("/myapp/");
        request.setContextPath("/myapp");
        request.setServletPath("/");
        chain = mapping2.getHandler(request);
        Assert.assertEquals(4, chain.getInterceptors().length);
        Assert.assertTrue(((chain.getInterceptors()[1]) instanceof ConversionServiceExposingInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[2]) instanceof LocaleChangeInterceptor));
        Assert.assertTrue(((chain.getInterceptors()[3]) instanceof ThemeChangeInterceptor));
        mv3 = adapter.handle(request, new MockHttpServletResponse(), chain.getHandler());
        Assert.assertEquals("root", mv3.getViewName());
    }

    @Test
    public void testViewControllersDefaultConfig() {
        loadBeanDefinitions("mvc-config-view-controllers-minimal.xml");
        SimpleUrlHandlerMapping hm = this.appContext.getBean(SimpleUrlHandlerMapping.class);
        Assert.assertNotNull(hm);
        ParameterizableViewController viewController = ((ParameterizableViewController) (hm.getUrlMap().get("/path")));
        Assert.assertNotNull(viewController);
        Assert.assertEquals("home", viewController.getViewName());
        ParameterizableViewController redirectViewController = ((ParameterizableViewController) (hm.getUrlMap().get("/old")));
        Assert.assertNotNull(redirectViewController);
        Assert.assertThat(redirectViewController.getView(), Matchers.instanceOf(RedirectView.class));
        ParameterizableViewController statusViewController = ((ParameterizableViewController) (hm.getUrlMap().get("/bad")));
        Assert.assertNotNull(statusViewController);
        Assert.assertEquals(404, statusViewController.getStatusCode().value());
        BeanNameUrlHandlerMapping beanNameMapping = this.appContext.getBean(BeanNameUrlHandlerMapping.class);
        Assert.assertNotNull(beanNameMapping);
        Assert.assertEquals(2, beanNameMapping.getOrder());
    }

    @Test
    public void testContentNegotiationManager() throws Exception {
        loadBeanDefinitions("mvc-config-content-negotiation-manager.xml");
        RequestMappingHandlerMapping mapping = appContext.getBean(RequestMappingHandlerMapping.class);
        ContentNegotiationManager manager = mapping.getContentNegotiationManager();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo.xml");
        NativeWebRequest webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Assert.assertEquals(Collections.singletonList(MediaType.valueOf("application/rss+xml")), manager.resolveMediaTypes(webRequest));
        ViewResolverComposite compositeResolver = this.appContext.getBean(ViewResolverComposite.class);
        Assert.assertNotNull(compositeResolver);
        Assert.assertEquals(("Actual: " + (compositeResolver.getViewResolvers())), 1, compositeResolver.getViewResolvers().size());
        ViewResolver resolver = compositeResolver.getViewResolvers().get(0);
        Assert.assertEquals(ContentNegotiatingViewResolver.class, resolver.getClass());
        ContentNegotiatingViewResolver cnvr = ((ContentNegotiatingViewResolver) (resolver));
        Assert.assertSame(manager, cnvr.getContentNegotiationManager());
    }

    @Test
    public void testAsyncSupportOptions() throws Exception {
        loadBeanDefinitions("mvc-config-async-support.xml");
        RequestMappingHandlerAdapter adapter = appContext.getBean(RequestMappingHandlerAdapter.class);
        Assert.assertNotNull(adapter);
        DirectFieldAccessor fieldAccessor = new DirectFieldAccessor(adapter);
        Assert.assertEquals(ConcurrentTaskExecutor.class, fieldAccessor.getPropertyValue("taskExecutor").getClass());
        Assert.assertEquals(2500L, fieldAccessor.getPropertyValue("asyncRequestTimeout"));
        CallableProcessingInterceptor[] callableInterceptors = ((CallableProcessingInterceptor[]) (fieldAccessor.getPropertyValue("callableInterceptors")));
        Assert.assertEquals(1, callableInterceptors.length);
        DeferredResultProcessingInterceptor[] deferredResultInterceptors = ((DeferredResultProcessingInterceptor[]) (fieldAccessor.getPropertyValue("deferredResultInterceptors")));
        Assert.assertEquals(1, deferredResultInterceptors.length);
    }

    @Test
    public void testViewResolution() throws Exception {
        loadBeanDefinitions("mvc-config-view-resolution.xml");
        ViewResolverComposite compositeResolver = this.appContext.getBean(ViewResolverComposite.class);
        Assert.assertNotNull(compositeResolver);
        Assert.assertEquals(("Actual: " + (compositeResolver.getViewResolvers())), 8, compositeResolver.getViewResolvers().size());
        Assert.assertEquals(LOWEST_PRECEDENCE, compositeResolver.getOrder());
        List<ViewResolver> resolvers = compositeResolver.getViewResolvers();
        Assert.assertEquals(BeanNameViewResolver.class, resolvers.get(0).getClass());
        ViewResolver resolver = resolvers.get(1);
        Assert.assertEquals(InternalResourceViewResolver.class, resolver.getClass());
        DirectFieldAccessor accessor = new DirectFieldAccessor(resolver);
        Assert.assertEquals(InternalResourceView.class, accessor.getPropertyValue("viewClass"));
        Assert.assertEquals(TilesViewResolver.class, resolvers.get(2).getClass());
        resolver = resolvers.get(3);
        Assert.assertThat(resolver, instanceOf(FreeMarkerViewResolver.class));
        accessor = new DirectFieldAccessor(resolver);
        Assert.assertEquals("freemarker-", accessor.getPropertyValue("prefix"));
        Assert.assertEquals(".freemarker", accessor.getPropertyValue("suffix"));
        Assert.assertArrayEquals(new String[]{ "my*", "*Report" }, ((String[]) (accessor.getPropertyValue("viewNames"))));
        Assert.assertEquals(1024, accessor.getPropertyValue("cacheLimit"));
        resolver = resolvers.get(4);
        Assert.assertThat(resolver, instanceOf(GroovyMarkupViewResolver.class));
        accessor = new DirectFieldAccessor(resolver);
        Assert.assertEquals("", accessor.getPropertyValue("prefix"));
        Assert.assertEquals(".tpl", accessor.getPropertyValue("suffix"));
        Assert.assertEquals(1024, accessor.getPropertyValue("cacheLimit"));
        resolver = resolvers.get(5);
        Assert.assertThat(resolver, instanceOf(ScriptTemplateViewResolver.class));
        accessor = new DirectFieldAccessor(resolver);
        Assert.assertEquals("", accessor.getPropertyValue("prefix"));
        Assert.assertEquals("", accessor.getPropertyValue("suffix"));
        Assert.assertEquals(1024, accessor.getPropertyValue("cacheLimit"));
        Assert.assertEquals(InternalResourceViewResolver.class, resolvers.get(6).getClass());
        Assert.assertEquals(InternalResourceViewResolver.class, resolvers.get(7).getClass());
        TilesConfigurer tilesConfigurer = appContext.getBean(TilesConfigurer.class);
        Assert.assertNotNull(tilesConfigurer);
        String[] definitions = new String[]{ "/org/springframework/web/servlet/resource/tiles/tiles1.xml", "/org/springframework/web/servlet/resource/tiles/tiles2.xml" };
        accessor = new DirectFieldAccessor(tilesConfigurer);
        Assert.assertArrayEquals(definitions, ((String[]) (accessor.getPropertyValue("definitions"))));
        Assert.assertTrue(((boolean) (accessor.getPropertyValue("checkRefresh"))));
        Assert.assertEquals(UnresolvingLocaleDefinitionsFactory.class, accessor.getPropertyValue("definitionsFactoryClass"));
        Assert.assertEquals(SpringBeanPreparerFactory.class, accessor.getPropertyValue("preparerFactoryClass"));
        FreeMarkerConfigurer freeMarkerConfigurer = appContext.getBean(FreeMarkerConfigurer.class);
        Assert.assertNotNull(freeMarkerConfigurer);
        accessor = new DirectFieldAccessor(freeMarkerConfigurer);
        Assert.assertArrayEquals(new String[]{ "/", "/test" }, ((String[]) (accessor.getPropertyValue("templateLoaderPaths"))));
        GroovyMarkupConfigurer groovyMarkupConfigurer = appContext.getBean(GroovyMarkupConfigurer.class);
        Assert.assertNotNull(groovyMarkupConfigurer);
        Assert.assertEquals("/test", groovyMarkupConfigurer.getResourceLoaderPath());
        Assert.assertTrue(groovyMarkupConfigurer.isAutoIndent());
        Assert.assertFalse(groovyMarkupConfigurer.isCacheTemplates());
        ScriptTemplateConfigurer scriptTemplateConfigurer = appContext.getBean(ScriptTemplateConfigurer.class);
        Assert.assertNotNull(scriptTemplateConfigurer);
        Assert.assertEquals("render", scriptTemplateConfigurer.getRenderFunction());
        Assert.assertEquals(TEXT_PLAIN_VALUE, scriptTemplateConfigurer.getContentType());
        Assert.assertEquals(StandardCharsets.ISO_8859_1, scriptTemplateConfigurer.getCharset());
        Assert.assertEquals("classpath:", scriptTemplateConfigurer.getResourceLoaderPath());
        Assert.assertFalse(scriptTemplateConfigurer.isSharedEngine());
        String[] scripts = new String[]{ "org/springframework/web/servlet/view/script/nashorn/render.js" };
        accessor = new DirectFieldAccessor(scriptTemplateConfigurer);
        Assert.assertArrayEquals(scripts, ((String[]) (accessor.getPropertyValue("scripts"))));
    }

    @Test
    public void testViewResolutionWithContentNegotiation() throws Exception {
        loadBeanDefinitions("mvc-config-view-resolution-content-negotiation.xml");
        ViewResolverComposite compositeResolver = this.appContext.getBean(ViewResolverComposite.class);
        Assert.assertNotNull(compositeResolver);
        Assert.assertEquals(1, compositeResolver.getViewResolvers().size());
        Assert.assertEquals(HIGHEST_PRECEDENCE, compositeResolver.getOrder());
        List<ViewResolver> resolvers = compositeResolver.getViewResolvers();
        Assert.assertEquals(ContentNegotiatingViewResolver.class, resolvers.get(0).getClass());
        ContentNegotiatingViewResolver cnvr = ((ContentNegotiatingViewResolver) (resolvers.get(0)));
        Assert.assertEquals(6, cnvr.getViewResolvers().size());
        Assert.assertEquals(1, cnvr.getDefaultViews().size());
        Assert.assertTrue(cnvr.isUseNotAcceptableStatusCode());
        String beanName = "contentNegotiationManager";
        DirectFieldAccessor accessor = new DirectFieldAccessor(cnvr);
        ContentNegotiationManager manager = ((ContentNegotiationManager) (accessor.getPropertyValue(beanName)));
        Assert.assertNotNull(manager);
        Assert.assertSame(manager, this.appContext.getBean(ContentNegotiationManager.class));
        Assert.assertSame(manager, this.appContext.getBean("mvcContentNegotiationManager"));
    }

    @Test
    public void testViewResolutionWithOrderSet() throws Exception {
        loadBeanDefinitions("mvc-config-view-resolution-custom-order.xml");
        ViewResolverComposite compositeResolver = this.appContext.getBean(ViewResolverComposite.class);
        Assert.assertNotNull(compositeResolver);
        Assert.assertEquals(("Actual: " + (compositeResolver.getViewResolvers())), 1, compositeResolver.getViewResolvers().size());
        Assert.assertEquals(123, compositeResolver.getOrder());
    }

    @Test
    public void testPathMatchingHandlerMappings() throws Exception {
        loadBeanDefinitions("mvc-config-path-matching-mappings.xml");
        RequestMappingHandlerMapping requestMapping = appContext.getBean(RequestMappingHandlerMapping.class);
        Assert.assertNotNull(requestMapping);
        Assert.assertEquals(MvcNamespaceTests.TestPathHelper.class, requestMapping.getUrlPathHelper().getClass());
        Assert.assertEquals(MvcNamespaceTests.TestPathMatcher.class, requestMapping.getPathMatcher().getClass());
        SimpleUrlHandlerMapping viewController = appContext.getBean(MvcNamespaceTests.VIEWCONTROLLER_BEAN_NAME, SimpleUrlHandlerMapping.class);
        Assert.assertNotNull(viewController);
        Assert.assertEquals(MvcNamespaceTests.TestPathHelper.class, viewController.getUrlPathHelper().getClass());
        Assert.assertEquals(MvcNamespaceTests.TestPathMatcher.class, viewController.getPathMatcher().getClass());
        for (SimpleUrlHandlerMapping handlerMapping : appContext.getBeansOfType(SimpleUrlHandlerMapping.class).values()) {
            Assert.assertNotNull(handlerMapping);
            Assert.assertEquals(MvcNamespaceTests.TestPathHelper.class, handlerMapping.getUrlPathHelper().getClass());
            Assert.assertEquals(MvcNamespaceTests.TestPathMatcher.class, handlerMapping.getPathMatcher().getClass());
        }
    }

    @Test
    public void testCorsMinimal() throws Exception {
        loadBeanDefinitions("mvc-config-cors-minimal.xml");
        String[] beanNames = appContext.getBeanNamesForType(AbstractHandlerMapping.class);
        Assert.assertEquals(2, beanNames.length);
        for (String beanName : beanNames) {
            AbstractHandlerMapping handlerMapping = ((AbstractHandlerMapping) (appContext.getBean(beanName)));
            Assert.assertNotNull(handlerMapping);
            DirectFieldAccessor accessor = new DirectFieldAccessor(handlerMapping);
            Map<String, CorsConfiguration> configs = getCorsConfigurations();
            Assert.assertNotNull(configs);
            Assert.assertEquals(1, configs.size());
            CorsConfiguration config = configs.get("/**");
            Assert.assertNotNull(config);
            Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedOrigins().toArray());
            Assert.assertArrayEquals(new String[]{ "GET", "HEAD", "POST" }, config.getAllowedMethods().toArray());
            Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedHeaders().toArray());
            Assert.assertNull(config.getExposedHeaders());
            Assert.assertNull(config.getAllowCredentials());
            Assert.assertEquals(Long.valueOf(1800), config.getMaxAge());
        }
    }

    @Test
    public void testCors() throws Exception {
        loadBeanDefinitions("mvc-config-cors.xml");
        String[] beanNames = appContext.getBeanNamesForType(AbstractHandlerMapping.class);
        Assert.assertEquals(2, beanNames.length);
        for (String beanName : beanNames) {
            AbstractHandlerMapping handlerMapping = ((AbstractHandlerMapping) (appContext.getBean(beanName)));
            Assert.assertNotNull(handlerMapping);
            DirectFieldAccessor accessor = new DirectFieldAccessor(handlerMapping);
            Map<String, CorsConfiguration> configs = getCorsConfigurations();
            Assert.assertNotNull(configs);
            Assert.assertEquals(2, configs.size());
            CorsConfiguration config = configs.get("/api/**");
            Assert.assertNotNull(config);
            Assert.assertArrayEquals(new String[]{ "http://domain1.com", "http://domain2.com" }, config.getAllowedOrigins().toArray());
            Assert.assertArrayEquals(new String[]{ "GET", "PUT" }, config.getAllowedMethods().toArray());
            Assert.assertArrayEquals(new String[]{ "header1", "header2", "header3" }, config.getAllowedHeaders().toArray());
            Assert.assertArrayEquals(new String[]{ "header1", "header2" }, config.getExposedHeaders().toArray());
            Assert.assertFalse(config.getAllowCredentials());
            Assert.assertEquals(Long.valueOf(123), config.getMaxAge());
            config = configs.get("/resources/**");
            Assert.assertArrayEquals(new String[]{ "http://domain1.com" }, config.getAllowedOrigins().toArray());
            Assert.assertArrayEquals(new String[]{ "GET", "HEAD", "POST" }, config.getAllowedMethods().toArray());
            Assert.assertArrayEquals(new String[]{ "*" }, config.getAllowedHeaders().toArray());
            Assert.assertNull(config.getExposedHeaders());
            Assert.assertNull(config.getAllowCredentials());
            Assert.assertEquals(Long.valueOf(1800), config.getMaxAge());
        }
    }

    @DateTimeFormat(iso = ISO.DATE)
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface IsoDate {}

    @NumberFormat(style = Style.PERCENT)
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface PercentNumber {}

    @Validated(MvcNamespaceTests.MyGroup.class)
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyValid {}

    @Controller
    public static class TestController {
        private Date date;

        private Double percent;

        private boolean recordedValidationError;

        @RequestMapping
        public void testBind(@RequestParam
        @MvcNamespaceTests.IsoDate
        Date date, @RequestParam(required = false)
        @MvcNamespaceTests.PercentNumber
        Double percent, @MvcNamespaceTests.MyValid
        MvcNamespaceTests.TestBean bean, BindingResult result) {
            this.date = date;
            this.percent = percent;
            this.recordedValidationError = (result.getErrorCount()) == 1;
        }
    }

    public static class TestValidator implements Validator {
        boolean validatorInvoked;

        @Override
        public boolean supports(Class<?> clazz) {
            return true;
        }

        @Override
        public void validate(@Nullable
        Object target, Errors errors) {
            this.validatorInvoked = true;
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyGroup {}

    private static class TestBean {
        @NotNull(groups = MvcNamespaceTests.MyGroup.class)
        private String field;

        @SuppressWarnings("unused")
        public String getField() {
            return field;
        }

        @SuppressWarnings("unused")
        public void setField(String field) {
            this.field = field;
        }
    }

    private static class TestMockServletContext extends MockServletContext {
        @Override
        public RequestDispatcher getNamedDispatcher(String path) {
            if ((path.equals("default")) || (path.equals("custom"))) {
                return new MockRequestDispatcher("/");
            } else {
                return null;
            }
        }

        @Override
        public String getVirtualServerName() {
            return null;
        }
    }

    public static class TestCallableProcessingInterceptor implements CallableProcessingInterceptor {}

    public static class TestDeferredResultProcessingInterceptor implements DeferredResultProcessingInterceptor {}

    public static class TestPathMatcher implements PathMatcher {
        @Override
        public boolean isPattern(String path) {
            return false;
        }

        @Override
        public boolean match(String pattern, String path) {
            return path.matches(pattern);
        }

        @Override
        public boolean matchStart(String pattern, String path) {
            return false;
        }

        @Override
        public String extractPathWithinPattern(String pattern, String path) {
            return null;
        }

        @Override
        public Map<String, String> extractUriTemplateVariables(String pattern, String path) {
            return null;
        }

        @Override
        public Comparator<String> getPatternComparator(String path) {
            return null;
        }

        @Override
        public String combine(String pattern1, String pattern2) {
            return null;
        }
    }

    public static class TestPathHelper extends UrlPathHelper {}

    public static class TestCacheManager implements CacheManager {
        @Override
        public Cache getCache(String name) {
            return new ConcurrentMapCache(name);
        }

        @Override
        public Collection<String> getCacheNames() {
            return null;
        }
    }
}

