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


import MvcUriComponentsBuilder.MVC_URI_COMPONENTS_CONTRIBUTOR_BEAN_NAME;
import Ordered.LOWEST_PRECEDENCE;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.CompositeUriComponentsContributor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.handler.ConversionServiceExposingInterceptor;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewRequestBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.JsonViewResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.servlet.resource.ResourceUrlProviderExposingInterceptor;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.ViewResolverComposite;
import org.springframework.web.util.UrlPathHelper;


/**
 * Integration tests for {@link WebMvcConfigurationSupport} (imported via
 * {@link EnableWebMvc @EnableWebMvc}).
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @author Sebastien Deleuze
 * @author Sam Brannen
 */
public class WebMvcConfigurationSupportTests {
    @Test
    public void requestMappingHandlerMapping() throws Exception {
        ApplicationContext context = initContext(WebMvcConfigurationSupportTests.WebConfig.class, WebMvcConfigurationSupportTests.ScopedController.class, WebMvcConfigurationSupportTests.ScopedProxyController.class);
        RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        Assert.assertEquals(0, handlerMapping.getOrder());
        HandlerExecutionChain chain = handlerMapping.getHandler(new MockHttpServletRequest("GET", "/"));
        Assert.assertNotNull(chain);
        Assert.assertNotNull(chain.getInterceptors());
        Assert.assertEquals(ConversionServiceExposingInterceptor.class, chain.getInterceptors()[0].getClass());
        chain = handlerMapping.getHandler(new MockHttpServletRequest("GET", "/scoped"));
        Assert.assertNotNull("HandlerExecutionChain for '/scoped' mapping should not be null.", chain);
        chain = handlerMapping.getHandler(new MockHttpServletRequest("GET", "/scopedProxy"));
        Assert.assertNotNull("HandlerExecutionChain for '/scopedProxy' mapping should not be null.", chain);
    }

    @Test
    public void emptyHandlerMappings() {
        ApplicationContext context = initContext(WebMvcConfigurationSupportTests.WebConfig.class);
        Map<String, HandlerMapping> handlerMappings = context.getBeansOfType(HandlerMapping.class);
        Assert.assertFalse(handlerMappings.containsKey("viewControllerHandlerMapping"));
        Assert.assertFalse(handlerMappings.containsKey("resourceHandlerMapping"));
        Assert.assertFalse(handlerMappings.containsKey("defaultServletHandlerMapping"));
        Object nullBean = context.getBean("viewControllerHandlerMapping");
        Assert.assertTrue(nullBean.equals(null));
        nullBean = context.getBean("resourceHandlerMapping");
        Assert.assertTrue(nullBean.equals(null));
        nullBean = context.getBean("defaultServletHandlerMapping");
        Assert.assertTrue(nullBean.equals(null));
    }

    @Test
    public void beanNameHandlerMapping() throws Exception {
        ApplicationContext context = initContext(WebMvcConfigurationSupportTests.WebConfig.class);
        BeanNameUrlHandlerMapping handlerMapping = context.getBean(BeanNameUrlHandlerMapping.class);
        Assert.assertEquals(2, handlerMapping.getOrder());
        HttpServletRequest request = new MockHttpServletRequest("GET", "/testController");
        HandlerExecutionChain chain = handlerMapping.getHandler(request);
        Assert.assertNotNull(chain);
        Assert.assertNotNull(chain.getInterceptors());
        Assert.assertEquals(3, chain.getInterceptors().length);
        Assert.assertEquals(ConversionServiceExposingInterceptor.class, chain.getInterceptors()[1].getClass());
        Assert.assertEquals(ResourceUrlProviderExposingInterceptor.class, chain.getInterceptors()[2].getClass());
    }

    @Test
    public void requestMappingHandlerAdapter() throws Exception {
        ApplicationContext context = initContext(WebMvcConfigurationSupportTests.WebConfig.class);
        RequestMappingHandlerAdapter adapter = context.getBean(RequestMappingHandlerAdapter.class);
        List<HttpMessageConverter<?>> converters = adapter.getMessageConverters();
        Assert.assertEquals(12, converters.size());
        converters.stream().filter(( converter) -> converter instanceof AbstractJackson2HttpMessageConverter).forEach(( converter) -> {
            ObjectMapper mapper = ((AbstractJackson2HttpMessageConverter) (converter)).getObjectMapper();
            assertFalse(mapper.getDeserializationConfig().isEnabled(DEFAULT_VIEW_INCLUSION));
            assertFalse(mapper.getSerializationConfig().isEnabled(DEFAULT_VIEW_INCLUSION));
            assertFalse(mapper.getDeserializationConfig().isEnabled(FAIL_ON_UNKNOWN_PROPERTIES));
            if (converter instanceof MappingJackson2XmlHttpMessageConverter) {
                assertEquals(.class, mapper.getClass());
            }
        });
        ConfigurableWebBindingInitializer initializer = ((ConfigurableWebBindingInitializer) (adapter.getWebBindingInitializer()));
        Assert.assertNotNull(initializer);
        ConversionService conversionService = initializer.getConversionService();
        Assert.assertNotNull(conversionService);
        Assert.assertTrue((conversionService instanceof FormattingConversionService));
        Validator validator = initializer.getValidator();
        Assert.assertNotNull(validator);
        Assert.assertTrue((validator instanceof LocalValidatorFactoryBean));
        DirectFieldAccessor fieldAccessor = new DirectFieldAccessor(adapter);
        @SuppressWarnings("unchecked")
        List<Object> bodyAdvice = ((List<Object>) (fieldAccessor.getPropertyValue("requestResponseBodyAdvice")));
        Assert.assertEquals(2, bodyAdvice.size());
        Assert.assertEquals(JsonViewRequestBodyAdvice.class, bodyAdvice.get(0).getClass());
        Assert.assertEquals(JsonViewResponseBodyAdvice.class, bodyAdvice.get(1).getClass());
    }

    @Test
    public void uriComponentsContributor() throws Exception {
        ApplicationContext context = initContext(WebMvcConfigurationSupportTests.WebConfig.class);
        CompositeUriComponentsContributor uriComponentsContributor = context.getBean(MVC_URI_COMPONENTS_CONTRIBUTOR_BEAN_NAME, CompositeUriComponentsContributor.class);
        Assert.assertNotNull(uriComponentsContributor);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handlerExceptionResolver() throws Exception {
        ApplicationContext context = initContext(WebMvcConfigurationSupportTests.WebConfig.class);
        HandlerExceptionResolverComposite compositeResolver = context.getBean("handlerExceptionResolver", HandlerExceptionResolverComposite.class);
        Assert.assertEquals(0, compositeResolver.getOrder());
        List<HandlerExceptionResolver> expectedResolvers = compositeResolver.getExceptionResolvers();
        Assert.assertEquals(ExceptionHandlerExceptionResolver.class, expectedResolvers.get(0).getClass());
        Assert.assertEquals(ResponseStatusExceptionResolver.class, expectedResolvers.get(1).getClass());
        Assert.assertEquals(DefaultHandlerExceptionResolver.class, expectedResolvers.get(2).getClass());
        ExceptionHandlerExceptionResolver eher = ((ExceptionHandlerExceptionResolver) (expectedResolvers.get(0)));
        Assert.assertNotNull(eher.getApplicationContext());
        DirectFieldAccessor fieldAccessor = new DirectFieldAccessor(eher);
        List<Object> interceptors = ((List<Object>) (fieldAccessor.getPropertyValue("responseBodyAdvice")));
        Assert.assertEquals(1, interceptors.size());
        Assert.assertEquals(JsonViewResponseBodyAdvice.class, interceptors.get(0).getClass());
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        try {
            ResponseStatusExceptionResolver rser = ((ResponseStatusExceptionResolver) (expectedResolvers.get(1)));
            MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
            MockHttpServletResponse response = new MockHttpServletResponse();
            rser.resolveException(request, response, context.getBean(WebMvcConfigurationSupportTests.TestController.class), new WebMvcConfigurationSupportTests.UserAlreadyExistsException());
            Assert.assertEquals("User already exists!", response.getErrorMessage());
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }
    }

    @Test
    public void customArgumentResolvers() {
        ApplicationContext context = initContext(WebMvcConfigurationSupportTests.CustomArgumentResolverConfig.class);
        RequestMappingHandlerAdapter adapter = context.getBean(RequestMappingHandlerAdapter.class);
        HandlerExceptionResolverComposite composite = context.getBean(HandlerExceptionResolverComposite.class);
        Assert.assertNotNull(adapter);
        Assert.assertEquals(1, adapter.getCustomArgumentResolvers().size());
        Assert.assertEquals(WebMvcConfigurationSupportTests.TestArgumentResolver.class, adapter.getCustomArgumentResolvers().get(0).getClass());
        Assert.assertEquals(1, adapter.getCustomReturnValueHandlers().size());
        Assert.assertEquals(WebMvcConfigurationSupportTests.TestReturnValueHandler.class, adapter.getCustomReturnValueHandlers().get(0).getClass());
        Assert.assertNotNull(composite);
        Assert.assertEquals(3, composite.getExceptionResolvers().size());
        Assert.assertEquals(ExceptionHandlerExceptionResolver.class, composite.getExceptionResolvers().get(0).getClass());
        ExceptionHandlerExceptionResolver resolver = ((ExceptionHandlerExceptionResolver) (composite.getExceptionResolvers().get(0)));
        Assert.assertEquals(1, resolver.getCustomArgumentResolvers().size());
        Assert.assertEquals(WebMvcConfigurationSupportTests.TestArgumentResolver.class, resolver.getCustomArgumentResolvers().get(0).getClass());
        Assert.assertEquals(1, resolver.getCustomReturnValueHandlers().size());
        Assert.assertEquals(WebMvcConfigurationSupportTests.TestReturnValueHandler.class, resolver.getCustomReturnValueHandlers().get(0).getClass());
    }

    @Test
    public void mvcViewResolver() {
        ApplicationContext context = initContext(WebMvcConfigurationSupportTests.WebConfig.class);
        ViewResolverComposite resolver = context.getBean("mvcViewResolver", ViewResolverComposite.class);
        Assert.assertNotNull(resolver);
        Assert.assertEquals(1, resolver.getViewResolvers().size());
        Assert.assertEquals(InternalResourceViewResolver.class, resolver.getViewResolvers().get(0).getClass());
        Assert.assertEquals(LOWEST_PRECEDENCE, resolver.getOrder());
    }

    @Test
    public void mvcViewResolverWithExistingResolver() throws Exception {
        ApplicationContext context = initContext(WebMvcConfigurationSupportTests.WebConfig.class, WebMvcConfigurationSupportTests.ViewResolverConfig.class);
        ViewResolverComposite resolver = context.getBean("mvcViewResolver", ViewResolverComposite.class);
        Assert.assertNotNull(resolver);
        Assert.assertEquals(0, resolver.getViewResolvers().size());
        Assert.assertEquals(LOWEST_PRECEDENCE, resolver.getOrder());
        Assert.assertNull(resolver.resolveViewName("anyViewName", Locale.ENGLISH));
    }

    @Test
    public void mvcViewResolverWithOrderSet() {
        ApplicationContext context = initContext(WebMvcConfigurationSupportTests.CustomViewResolverOrderConfig.class);
        ViewResolverComposite resolver = context.getBean("mvcViewResolver", ViewResolverComposite.class);
        Assert.assertNotNull(resolver);
        Assert.assertEquals(1, resolver.getViewResolvers().size());
        Assert.assertEquals(InternalResourceViewResolver.class, resolver.getViewResolvers().get(0).getClass());
        Assert.assertEquals(123, resolver.getOrder());
    }

    @Test
    public void defaultPathMatchConfiguration() throws Exception {
        ApplicationContext context = initContext(WebMvcConfigurationSupportTests.WebConfig.class);
        UrlPathHelper urlPathHelper = context.getBean(UrlPathHelper.class);
        PathMatcher pathMatcher = context.getBean(PathMatcher.class);
        Assert.assertNotNull(urlPathHelper);
        Assert.assertNotNull(pathMatcher);
        Assert.assertEquals(AntPathMatcher.class, pathMatcher.getClass());
    }

    @EnableWebMvc
    @Configuration
    static class WebConfig {
        @Bean("/testController")
        public WebMvcConfigurationSupportTests.TestController testController() {
            return new WebMvcConfigurationSupportTests.TestController();
        }

        @Bean
        public MessageSource messageSource() {
            StaticMessageSource messageSource = new StaticMessageSource();
            messageSource.addMessage("exception.user.exists", Locale.ENGLISH, "User already exists!");
            return messageSource;
        }
    }

    @Configuration
    static class ViewResolverConfig {
        @Bean
        public ViewResolver beanNameViewResolver() {
            return new BeanNameViewResolver();
        }
    }

    @EnableWebMvc
    @Configuration
    static class CustomViewResolverOrderConfig implements WebMvcConfigurer {
        @Override
        public void configureViewResolvers(ViewResolverRegistry registry) {
            registry.jsp();
            registry.order(123);
        }
    }

    @EnableWebMvc
    @Configuration
    static class CustomArgumentResolverConfig implements WebMvcConfigurer {
        @Override
        public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
            resolvers.add(new WebMvcConfigurationSupportTests.TestArgumentResolver());
        }

        @Override
        public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
            handlers.add(new WebMvcConfigurationSupportTests.TestReturnValueHandler());
        }
    }

    @Controller
    private static class TestController {
        @RequestMapping("/")
        public void handle() {
        }

        @RequestMapping("/foo/{id}/bar/{date}")
        public HttpEntity<Void> methodWithTwoPathVariables(@PathVariable
        Integer id, @DateTimeFormat(iso = ISO.DATE)
        @PathVariable
        DateTime date) {
            return null;
        }
    }

    @Controller
    @Scope("prototype")
    private static class ScopedController {
        @RequestMapping("/scoped")
        public void handle() {
        }
    }

    @Controller
    @Scope(scopeName = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
    static class ScopedProxyController {
        @RequestMapping("/scopedProxy")
        public void handle() {
        }
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "exception.user.exists")
    @SuppressWarnings("serial")
    private static class UserAlreadyExistsException extends RuntimeException {}

    private static class TestArgumentResolver implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return false;
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest request, WebDataBinderFactory factory) {
            return null;
        }
    }

    private static class TestReturnValueHandler implements HandlerMethodReturnValueHandler {
        @Override
        public boolean supportsReturnType(MethodParameter returnType) {
            return false;
        }

        @Override
        public void handleReturnValue(Object value, MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest request) {
        }
    }
}

