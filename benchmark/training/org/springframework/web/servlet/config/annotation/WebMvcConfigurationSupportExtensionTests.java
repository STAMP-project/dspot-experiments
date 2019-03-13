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


import HttpStatus.NOT_FOUND;
import HttpStatus.PERMANENT_REDIRECT;
import Ordered.HIGHEST_PRECEDENCE;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.Errors;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.HandlerTypePredicate;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.ConversionServiceExposingInterceptor;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceUrlProviderExposingInterceptor;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.ViewResolverComposite;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.springframework.web.util.UrlPathHelper;


/**
 * A test fixture with a sub-class of {@link WebMvcConfigurationSupport} that also
 * implements the various {@link WebMvcConfigurer} extension points.
 *
 * The former doesn't implement the latter but the two must have compatible
 * callback method signatures to support moving from simple to advanced
 * configuration -- i.e. dropping @EnableWebMvc + WebMvcConfigurer and extending
 * directly from WebMvcConfigurationSupport.
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 */
public class WebMvcConfigurationSupportExtensionTests {
    private WebMvcConfigurationSupportExtensionTests.TestWebMvcConfigurationSupport config;

    private StaticWebApplicationContext context;

    @Test
    public void handlerMappings() throws Exception {
        RequestMappingHandlerMapping rmHandlerMapping = requestMappingHandlerMapping();
        rmHandlerMapping.setApplicationContext(this.context);
        rmHandlerMapping.afterPropertiesSet();
        Assert.assertEquals(WebMvcConfigurationSupportExtensionTests.TestPathHelper.class, rmHandlerMapping.getUrlPathHelper().getClass());
        Assert.assertEquals(WebMvcConfigurationSupportExtensionTests.TestPathMatcher.class, rmHandlerMapping.getPathMatcher().getClass());
        HandlerExecutionChain chain = rmHandlerMapping.getHandler(new MockHttpServletRequest("GET", "/"));
        Assert.assertNotNull(chain);
        Assert.assertNotNull(chain.getInterceptors());
        Assert.assertEquals(3, chain.getInterceptors().length);
        Assert.assertEquals(LocaleChangeInterceptor.class, chain.getInterceptors()[0].getClass());
        Assert.assertEquals(ConversionServiceExposingInterceptor.class, chain.getInterceptors()[1].getClass());
        Assert.assertEquals(ResourceUrlProviderExposingInterceptor.class, chain.getInterceptors()[2].getClass());
        Map<RequestMappingInfo, HandlerMethod> map = rmHandlerMapping.getHandlerMethods();
        Assert.assertEquals(2, map.size());
        RequestMappingInfo info = map.entrySet().stream().filter(( entry) -> entry.getValue().getBeanType().equals(.class)).findFirst().orElseThrow(() -> new AssertionError("UserController bean not found")).getKey();
        Assert.assertEquals(Collections.singleton("/api/user/{id}"), info.getPatternsCondition().getPatterns());
        AbstractHandlerMapping handlerMapping = ((AbstractHandlerMapping) (viewControllerHandlerMapping()));
        handlerMapping.setApplicationContext(this.context);
        Assert.assertNotNull(handlerMapping);
        Assert.assertEquals(1, handlerMapping.getOrder());
        Assert.assertEquals(WebMvcConfigurationSupportExtensionTests.TestPathHelper.class, handlerMapping.getUrlPathHelper().getClass());
        Assert.assertEquals(WebMvcConfigurationSupportExtensionTests.TestPathMatcher.class, handlerMapping.getPathMatcher().getClass());
        chain = handlerMapping.getHandler(new MockHttpServletRequest("GET", "/path"));
        Assert.assertNotNull(chain);
        Assert.assertNotNull(chain.getHandler());
        chain = handlerMapping.getHandler(new MockHttpServletRequest("GET", "/bad"));
        Assert.assertNotNull(chain);
        Assert.assertNotNull(chain.getHandler());
        chain = handlerMapping.getHandler(new MockHttpServletRequest("GET", "/old"));
        Assert.assertNotNull(chain);
        Assert.assertNotNull(chain.getHandler());
        handlerMapping = ((AbstractHandlerMapping) (resourceHandlerMapping()));
        handlerMapping.setApplicationContext(this.context);
        Assert.assertNotNull(handlerMapping);
        Assert.assertEquals(((Integer.MAX_VALUE) - 1), handlerMapping.getOrder());
        Assert.assertEquals(WebMvcConfigurationSupportExtensionTests.TestPathHelper.class, handlerMapping.getUrlPathHelper().getClass());
        Assert.assertEquals(WebMvcConfigurationSupportExtensionTests.TestPathMatcher.class, handlerMapping.getPathMatcher().getClass());
        chain = handlerMapping.getHandler(new MockHttpServletRequest("GET", "/resources/foo.gif"));
        Assert.assertNotNull(chain);
        Assert.assertNotNull(chain.getHandler());
        Assert.assertEquals(Arrays.toString(chain.getInterceptors()), 4, chain.getInterceptors().length);
        // PathExposingHandlerInterceptor at chain.getInterceptors()[0]
        Assert.assertEquals(LocaleChangeInterceptor.class, chain.getInterceptors()[1].getClass());
        Assert.assertEquals(ConversionServiceExposingInterceptor.class, chain.getInterceptors()[2].getClass());
        Assert.assertEquals(ResourceUrlProviderExposingInterceptor.class, chain.getInterceptors()[3].getClass());
        handlerMapping = ((AbstractHandlerMapping) (defaultServletHandlerMapping()));
        handlerMapping.setApplicationContext(this.context);
        Assert.assertNotNull(handlerMapping);
        Assert.assertEquals(Integer.MAX_VALUE, handlerMapping.getOrder());
        chain = handlerMapping.getHandler(new MockHttpServletRequest("GET", "/anyPath"));
        Assert.assertNotNull(chain);
        Assert.assertNotNull(chain.getHandler());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void requestMappingHandlerAdapter() throws Exception {
        RequestMappingHandlerAdapter adapter = this.config.requestMappingHandlerAdapter();
        // ConversionService
        String actual = mvcConversionService().convert(new TestBean(), String.class);
        Assert.assertEquals("converted", actual);
        // Message converters
        List<HttpMessageConverter<?>> converters = adapter.getMessageConverters();
        Assert.assertEquals(2, converters.size());
        Assert.assertEquals(StringHttpMessageConverter.class, converters.get(0).getClass());
        Assert.assertEquals(MappingJackson2HttpMessageConverter.class, converters.get(1).getClass());
        ObjectMapper objectMapper = getObjectMapper();
        Assert.assertFalse(objectMapper.getDeserializationConfig().isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION));
        Assert.assertFalse(objectMapper.getSerializationConfig().isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION));
        Assert.assertFalse(objectMapper.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        DirectFieldAccessor fieldAccessor = new DirectFieldAccessor(adapter);
        // Custom argument resolvers and return value handlers
        List<HandlerMethodArgumentResolver> argResolvers = ((List<HandlerMethodArgumentResolver>) (fieldAccessor.getPropertyValue("customArgumentResolvers")));
        Assert.assertEquals(1, argResolvers.size());
        List<HandlerMethodReturnValueHandler> handlers = ((List<HandlerMethodReturnValueHandler>) (fieldAccessor.getPropertyValue("customReturnValueHandlers")));
        Assert.assertEquals(1, handlers.size());
        // Async support options
        Assert.assertEquals(ConcurrentTaskExecutor.class, fieldAccessor.getPropertyValue("taskExecutor").getClass());
        Assert.assertEquals(2500L, fieldAccessor.getPropertyValue("asyncRequestTimeout"));
        CallableProcessingInterceptor[] callableInterceptors = ((CallableProcessingInterceptor[]) (fieldAccessor.getPropertyValue("callableInterceptors")));
        Assert.assertEquals(1, callableInterceptors.length);
        DeferredResultProcessingInterceptor[] deferredResultInterceptors = ((DeferredResultProcessingInterceptor[]) (fieldAccessor.getPropertyValue("deferredResultInterceptors")));
        Assert.assertEquals(1, deferredResultInterceptors.length);
        Assert.assertEquals(false, fieldAccessor.getPropertyValue("ignoreDefaultModelOnRedirect"));
    }

    @Test
    public void webBindingInitializer() throws Exception {
        RequestMappingHandlerAdapter adapter = this.config.requestMappingHandlerAdapter();
        ConfigurableWebBindingInitializer initializer = ((ConfigurableWebBindingInitializer) (adapter.getWebBindingInitializer()));
        Assert.assertNotNull(initializer);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(null, "");
        initializer.getValidator().validate(null, bindingResult);
        Assert.assertEquals("invalid", bindingResult.getAllErrors().get(0).getCode());
        String[] codes = initializer.getMessageCodesResolver().resolveMessageCodes("invalid", null);
        Assert.assertEquals("custom.invalid", codes[0]);
    }

    @Test
    public void contentNegotiation() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo.json");
        NativeWebRequest webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        RequestMappingHandlerMapping mapping = requestMappingHandlerMapping();
        ContentNegotiationManager manager = mapping.getContentNegotiationManager();
        Assert.assertEquals(Collections.singletonList(APPLICATION_JSON), manager.resolveMediaTypes(webRequest));
        request.setRequestURI("/foo.xml");
        Assert.assertEquals(Collections.singletonList(APPLICATION_XML), manager.resolveMediaTypes(webRequest));
        request.setRequestURI("/foo.rss");
        Assert.assertEquals(Collections.singletonList(MediaType.valueOf("application/rss+xml")), manager.resolveMediaTypes(webRequest));
        request.setRequestURI("/foo.atom");
        Assert.assertEquals(Collections.singletonList(APPLICATION_ATOM_XML), manager.resolveMediaTypes(webRequest));
        request.setRequestURI("/foo");
        request.setParameter("f", "json");
        Assert.assertEquals(Collections.singletonList(APPLICATION_JSON), manager.resolveMediaTypes(webRequest));
        request.setRequestURI("/resources/foo.gif");
        SimpleUrlHandlerMapping handlerMapping = ((SimpleUrlHandlerMapping) (resourceHandlerMapping()));
        handlerMapping.setApplicationContext(this.context);
        HandlerExecutionChain chain = handlerMapping.getHandler(request);
        Assert.assertNotNull(chain);
        ResourceHttpRequestHandler handler = ((ResourceHttpRequestHandler) (chain.getHandler()));
        Assert.assertNotNull(handler);
        Assert.assertSame(manager, handler.getContentNegotiationManager());
    }

    @Test
    public void exceptionResolvers() throws Exception {
        List<HandlerExceptionResolver> resolvers = getExceptionResolvers();
        Assert.assertEquals(2, resolvers.size());
        Assert.assertEquals(ResponseStatusExceptionResolver.class, resolvers.get(0).getClass());
        Assert.assertEquals(SimpleMappingExceptionResolver.class, resolvers.get(1).getClass());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void viewResolvers() throws Exception {
        ViewResolverComposite viewResolver = ((ViewResolverComposite) (mvcViewResolver()));
        Assert.assertEquals(HIGHEST_PRECEDENCE, viewResolver.getOrder());
        List<ViewResolver> viewResolvers = viewResolver.getViewResolvers();
        DirectFieldAccessor accessor = new DirectFieldAccessor(viewResolvers.get(0));
        Assert.assertEquals(1, viewResolvers.size());
        Assert.assertEquals(ContentNegotiatingViewResolver.class, viewResolvers.get(0).getClass());
        Assert.assertFalse(((Boolean) (accessor.getPropertyValue("useNotAcceptableStatusCode"))));
        Assert.assertNotNull(accessor.getPropertyValue("contentNegotiationManager"));
        List<View> defaultViews = ((List<View>) (accessor.getPropertyValue("defaultViews")));
        Assert.assertNotNull(defaultViews);
        Assert.assertEquals(1, defaultViews.size());
        Assert.assertEquals(MappingJackson2JsonView.class, defaultViews.get(0).getClass());
        viewResolvers = ((List<ViewResolver>) (accessor.getPropertyValue("viewResolvers")));
        Assert.assertNotNull(viewResolvers);
        Assert.assertEquals(1, viewResolvers.size());
        Assert.assertEquals(InternalResourceViewResolver.class, viewResolvers.get(0).getClass());
        accessor = new DirectFieldAccessor(viewResolvers.get(0));
        Assert.assertEquals("/", accessor.getPropertyValue("prefix"));
        Assert.assertEquals(".jsp", accessor.getPropertyValue("suffix"));
    }

    @Test
    public void crossOrigin() {
        Map<String, CorsConfiguration> configs = getCorsConfigurations();
        Assert.assertEquals(1, configs.size());
        Assert.assertEquals("*", configs.get("/resources/**").getAllowedOrigins().get(0));
    }

    @Controller
    private static class TestController {
        @RequestMapping("/")
        public void handle() {
        }
    }

    /**
     * Since WebMvcConfigurationSupport does not implement WebMvcConfigurer, the purpose
     * of this test class is also to ensure the two are in sync with each other. Effectively
     * that ensures that application config classes that use the combo {@code @EnableWebMvc}
     * plus WebMvcConfigurer can switch to extending WebMvcConfigurationSupport directly for
     * more advanced configuration needs.
     */
    private class TestWebMvcConfigurationSupport extends WebMvcConfigurationSupport implements WebMvcConfigurer {
        @Override
        public void addFormatters(FormatterRegistry registry) {
            registry.addConverter(new org.springframework.core.convert.converter.Converter<TestBean, String>() {
                @Override
                public String convert(TestBean source) {
                    return "converted";
                }
            });
        }

        @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(new MappingJackson2HttpMessageConverter());
        }

        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(0, new StringHttpMessageConverter());
        }

        @Override
        public Validator getValidator() {
            return new Validator() {
                @Override
                public void validate(@Nullable
                Object target, Errors errors) {
                    errors.reject("invalid");
                }

                @Override
                public boolean supports(Class<?> clazz) {
                    return true;
                }
            };
        }

        @Override
        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
            configurer.favorParameter(true).parameterName("f");
        }

        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
            configurer.setDefaultTimeout(2500).setTaskExecutor(new ConcurrentTaskExecutor()).registerCallableInterceptors(new CallableProcessingInterceptor() {}).registerDeferredResultInterceptors(new DeferredResultProcessingInterceptor() {});
        }

        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
            argumentResolvers.add(new ModelAttributeMethodProcessor(true));
        }

        @Override
        public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
            returnValueHandlers.add(new ModelAttributeMethodProcessor(true));
        }

        @Override
        public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
            exceptionResolvers.add(new SimpleMappingExceptionResolver());
        }

        @Override
        public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
            exceptionResolvers.add(0, new ResponseStatusExceptionResolver());
        }

        @Override
        public void configurePathMatch(PathMatchConfigurer configurer) {
            configurer.setPathMatcher(new WebMvcConfigurationSupportExtensionTests.TestPathMatcher());
            configurer.setUrlPathHelper(new WebMvcConfigurationSupportExtensionTests.TestPathHelper());
            configurer.addPathPrefix("/api", HandlerTypePredicate.forAnnotation(RestController.class));
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new LocaleChangeInterceptor());
        }

        @SuppressWarnings("serial")
        @Override
        public MessageCodesResolver getMessageCodesResolver() {
            return new DefaultMessageCodesResolver() {
                @Override
                public String[] resolveMessageCodes(String errorCode, String objectName) {
                    return new String[]{ "custom." + errorCode };
                }
            };
        }

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/path").setViewName("view");
            registry.addRedirectViewController("/old", "/new").setStatusCode(PERMANENT_REDIRECT);
            registry.addStatusController("/bad", NOT_FOUND);
        }

        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            registry.enableContentNegotiation(new MappingJackson2JsonView());
            registry.jsp("/", ".jsp");
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/resources/**").addResourceLocations("src/test/java");
        }

        @Override
        public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
            configurer.enable("default");
        }

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/resources/**");
        }
    }

    private class TestPathHelper extends UrlPathHelper {}

    private class TestPathMatcher extends AntPathMatcher {}

    @RestController
    @RequestMapping("/user")
    static class UserController {
        @GetMapping("/{id}")
        public Principal getUser() {
            return Mockito.mock(Principal.class);
        }
    }
}

