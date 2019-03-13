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
package org.springframework.web.servlet.mvc.method.annotation;


import DispatcherServlet.OUTPUT_FLASH_MAP_ATTRIBUTE;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ModelMethodProcessor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.NestedServletException;


/**
 * Test fixture with {@link ExceptionHandlerExceptionResolver}.
 *
 * @author Rossen Stoyanchev
 * @author Arjen Poutsma
 * @author Kazuki Shimizu
 * @author Brian Clozel
 * @since 3.1
 */
@SuppressWarnings("unused")
public class ExceptionHandlerExceptionResolverTests {
    private static int RESOLVER_COUNT;

    private static int HANDLER_COUNT;

    private ExceptionHandlerExceptionResolver resolver;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Test
    public void nullHandler() {
        Object handler = null;
        this.resolver.afterPropertiesSet();
        ModelAndView mav = this.resolver.resolveException(this.request, this.response, handler, null);
        Assert.assertNull("Exception can be resolved only if there is a HandlerMethod", mav);
    }

    @Test
    public void setCustomArgumentResolvers() {
        HandlerMethodArgumentResolver resolver = new ServletRequestMethodArgumentResolver();
        this.resolver.setCustomArgumentResolvers(Collections.singletonList(resolver));
        this.resolver.afterPropertiesSet();
        Assert.assertTrue(this.resolver.getArgumentResolvers().getResolvers().contains(resolver));
        assertMethodProcessorCount(((ExceptionHandlerExceptionResolverTests.RESOLVER_COUNT) + 1), ExceptionHandlerExceptionResolverTests.HANDLER_COUNT);
    }

    @Test
    public void setArgumentResolvers() {
        HandlerMethodArgumentResolver resolver = new ServletRequestMethodArgumentResolver();
        this.resolver.setArgumentResolvers(Collections.singletonList(resolver));
        this.resolver.afterPropertiesSet();
        assertMethodProcessorCount(1, ExceptionHandlerExceptionResolverTests.HANDLER_COUNT);
    }

    @Test
    public void setCustomReturnValueHandlers() {
        HandlerMethodReturnValueHandler handler = new ViewNameMethodReturnValueHandler();
        this.resolver.setCustomReturnValueHandlers(Collections.singletonList(handler));
        this.resolver.afterPropertiesSet();
        Assert.assertTrue(this.resolver.getReturnValueHandlers().getHandlers().contains(handler));
        assertMethodProcessorCount(ExceptionHandlerExceptionResolverTests.RESOLVER_COUNT, ((ExceptionHandlerExceptionResolverTests.HANDLER_COUNT) + 1));
    }

    @Test
    public void setReturnValueHandlers() {
        HandlerMethodReturnValueHandler handler = new ModelMethodProcessor();
        this.resolver.setReturnValueHandlers(Collections.singletonList(handler));
        this.resolver.afterPropertiesSet();
        assertMethodProcessorCount(ExceptionHandlerExceptionResolverTests.RESOLVER_COUNT, 1);
    }

    @Test
    public void resolveNoExceptionHandlerForException() throws NoSuchMethodException {
        Exception npe = new NullPointerException();
        HandlerMethod handlerMethod = new HandlerMethod(new ExceptionHandlerExceptionResolverTests.IoExceptionController(), "handle");
        this.resolver.afterPropertiesSet();
        ModelAndView mav = this.resolver.resolveException(this.request, this.response, handlerMethod, npe);
        Assert.assertNull("NPE should not have been handled", mav);
    }

    @Test
    public void resolveExceptionModelAndView() throws NoSuchMethodException {
        IllegalArgumentException ex = new IllegalArgumentException("Bad argument");
        HandlerMethod handlerMethod = new HandlerMethod(new ExceptionHandlerExceptionResolverTests.ModelAndViewController(), "handle");
        this.resolver.afterPropertiesSet();
        ModelAndView mav = this.resolver.resolveException(this.request, this.response, handlerMethod, ex);
        Assert.assertNotNull(mav);
        Assert.assertFalse(mav.isEmpty());
        Assert.assertEquals("errorView", mav.getViewName());
        Assert.assertEquals("Bad argument", mav.getModel().get("detail"));
    }

    @Test
    public void resolveExceptionResponseBody() throws UnsupportedEncodingException, NoSuchMethodException {
        IllegalArgumentException ex = new IllegalArgumentException();
        HandlerMethod handlerMethod = new HandlerMethod(new ExceptionHandlerExceptionResolverTests.ResponseBodyController(), "handle");
        this.resolver.afterPropertiesSet();
        ModelAndView mav = this.resolver.resolveException(this.request, this.response, handlerMethod, ex);
        Assert.assertNotNull(mav);
        Assert.assertTrue(mav.isEmpty());
        Assert.assertEquals("IllegalArgumentException", this.response.getContentAsString());
    }

    @Test
    public void resolveExceptionResponseWriter() throws Exception {
        IllegalArgumentException ex = new IllegalArgumentException();
        HandlerMethod handlerMethod = new HandlerMethod(new ExceptionHandlerExceptionResolverTests.ResponseWriterController(), "handle");
        this.resolver.afterPropertiesSet();
        ModelAndView mav = this.resolver.resolveException(this.request, this.response, handlerMethod, ex);
        Assert.assertNotNull(mav);
        Assert.assertTrue(mav.isEmpty());
        Assert.assertEquals("IllegalArgumentException", this.response.getContentAsString());
    }

    // SPR-13546
    @Test
    public void resolveExceptionModelAtArgument() throws Exception {
        IllegalArgumentException ex = new IllegalArgumentException();
        HandlerMethod handlerMethod = new HandlerMethod(new ExceptionHandlerExceptionResolverTests.ModelArgumentController(), "handle");
        this.resolver.afterPropertiesSet();
        ModelAndView mav = this.resolver.resolveException(this.request, this.response, handlerMethod, ex);
        Assert.assertNotNull(mav);
        Assert.assertEquals(1, mav.getModelMap().size());
        Assert.assertEquals("IllegalArgumentException", mav.getModelMap().get("exceptionClassName"));
    }

    // SPR-14651
    @Test
    public void resolveRedirectAttributesAtArgument() throws Exception {
        IllegalArgumentException ex = new IllegalArgumentException();
        HandlerMethod handlerMethod = new HandlerMethod(new ExceptionHandlerExceptionResolverTests.RedirectAttributesController(), "handle");
        this.resolver.afterPropertiesSet();
        ModelAndView mav = this.resolver.resolveException(this.request, this.response, handlerMethod, ex);
        Assert.assertNotNull(mav);
        Assert.assertEquals("redirect:/", mav.getViewName());
        FlashMap flashMap = ((FlashMap) (this.request.getAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE)));
        Assert.assertNotNull("output FlashMap should exist", flashMap);
        Assert.assertEquals("IllegalArgumentException", flashMap.get("exceptionClassName"));
    }

    @Test
    public void resolveExceptionGlobalHandler() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExceptionHandlerExceptionResolverTests.MyConfig.class);
        this.resolver.setApplicationContext(ctx);
        this.resolver.afterPropertiesSet();
        IllegalAccessException ex = new IllegalAccessException();
        HandlerMethod handlerMethod = new HandlerMethod(new ExceptionHandlerExceptionResolverTests.ResponseBodyController(), "handle");
        ModelAndView mav = this.resolver.resolveException(this.request, this.response, handlerMethod, ex);
        Assert.assertNotNull("Exception was not handled", mav);
        Assert.assertTrue(mav.isEmpty());
        Assert.assertEquals("AnotherTestExceptionResolver: IllegalAccessException", this.response.getContentAsString());
    }

    @Test
    public void resolveExceptionGlobalHandlerOrdered() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExceptionHandlerExceptionResolverTests.MyConfig.class);
        this.resolver.setApplicationContext(ctx);
        this.resolver.afterPropertiesSet();
        IllegalStateException ex = new IllegalStateException();
        HandlerMethod handlerMethod = new HandlerMethod(new ExceptionHandlerExceptionResolverTests.ResponseBodyController(), "handle");
        ModelAndView mav = this.resolver.resolveException(this.request, this.response, handlerMethod, ex);
        Assert.assertNotNull("Exception was not handled", mav);
        Assert.assertTrue(mav.isEmpty());
        Assert.assertEquals("TestExceptionResolver: IllegalStateException", this.response.getContentAsString());
    }

    // SPR-12605
    @Test
    public void resolveExceptionWithHandlerMethodArg() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExceptionHandlerExceptionResolverTests.MyConfig.class);
        this.resolver.setApplicationContext(ctx);
        this.resolver.afterPropertiesSet();
        ArrayIndexOutOfBoundsException ex = new ArrayIndexOutOfBoundsException();
        HandlerMethod handlerMethod = new HandlerMethod(new ExceptionHandlerExceptionResolverTests.ResponseBodyController(), "handle");
        ModelAndView mav = this.resolver.resolveException(this.request, this.response, handlerMethod, ex);
        Assert.assertNotNull("Exception was not handled", mav);
        Assert.assertTrue(mav.isEmpty());
        Assert.assertEquals("HandlerMethod: handle", this.response.getContentAsString());
    }

    @Test
    public void resolveExceptionWithAssertionError() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExceptionHandlerExceptionResolverTests.MyConfig.class);
        this.resolver.setApplicationContext(ctx);
        this.resolver.afterPropertiesSet();
        AssertionError err = new AssertionError("argh");
        HandlerMethod handlerMethod = new HandlerMethod(new ExceptionHandlerExceptionResolverTests.ResponseBodyController(), "handle");
        ModelAndView mav = this.resolver.resolveException(this.request, this.response, handlerMethod, new NestedServletException("Handler dispatch failed", err));
        Assert.assertNotNull("Exception was not handled", mav);
        Assert.assertTrue(mav.isEmpty());
        Assert.assertEquals(err.toString(), this.response.getContentAsString());
    }

    @Test
    public void resolveExceptionWithAssertionErrorAsRootCause() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExceptionHandlerExceptionResolverTests.MyConfig.class);
        this.resolver.setApplicationContext(ctx);
        this.resolver.afterPropertiesSet();
        AssertionError err = new AssertionError("argh");
        FatalBeanException ex = new FatalBeanException("wrapped", err);
        HandlerMethod handlerMethod = new HandlerMethod(new ExceptionHandlerExceptionResolverTests.ResponseBodyController(), "handle");
        ModelAndView mav = this.resolver.resolveException(this.request, this.response, handlerMethod, ex);
        Assert.assertNotNull("Exception was not handled", mav);
        Assert.assertTrue(mav.isEmpty());
        Assert.assertEquals(err.toString(), this.response.getContentAsString());
    }

    @Test
    public void resolveExceptionControllerAdviceHandler() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExceptionHandlerExceptionResolverTests.MyControllerAdviceConfig.class);
        this.resolver.setApplicationContext(ctx);
        this.resolver.afterPropertiesSet();
        IllegalStateException ex = new IllegalStateException();
        HandlerMethod handlerMethod = new HandlerMethod(new ExceptionHandlerExceptionResolverTests.ResponseBodyController(), "handle");
        ModelAndView mav = this.resolver.resolveException(this.request, this.response, handlerMethod, ex);
        Assert.assertNotNull("Exception was not handled", mav);
        Assert.assertTrue(mav.isEmpty());
        Assert.assertEquals("BasePackageTestExceptionResolver: IllegalStateException", this.response.getContentAsString());
    }

    @Test
    public void resolveExceptionControllerAdviceNoHandler() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExceptionHandlerExceptionResolverTests.MyControllerAdviceConfig.class);
        this.resolver.setApplicationContext(ctx);
        this.resolver.afterPropertiesSet();
        IllegalStateException ex = new IllegalStateException();
        ModelAndView mav = this.resolver.resolveException(this.request, this.response, null, ex);
        Assert.assertNotNull("Exception was not handled", mav);
        Assert.assertTrue(mav.isEmpty());
        Assert.assertEquals("DefaultTestExceptionResolver: IllegalStateException", this.response.getContentAsString());
    }

    // SPR-16496
    @Test
    public void resolveExceptionControllerAdviceAgainstProxy() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ExceptionHandlerExceptionResolverTests.MyControllerAdviceConfig.class);
        this.resolver.setApplicationContext(ctx);
        this.resolver.afterPropertiesSet();
        IllegalStateException ex = new IllegalStateException();
        HandlerMethod handlerMethod = new HandlerMethod(new ProxyFactory(new ExceptionHandlerExceptionResolverTests.ResponseBodyController()).getProxy(), "handle");
        ModelAndView mav = this.resolver.resolveException(this.request, this.response, handlerMethod, ex);
        Assert.assertNotNull("Exception was not handled", mav);
        Assert.assertTrue(mav.isEmpty());
        Assert.assertEquals("BasePackageTestExceptionResolver: IllegalStateException", this.response.getContentAsString());
    }

    @Controller
    static class ModelAndViewController {
        public void handle() {
        }

        @ExceptionHandler
        public ModelAndView handle(Exception ex) throws IOException {
            return new ModelAndView("errorView", "detail", ex.getMessage());
        }
    }

    @Controller
    static class ResponseWriterController {
        public void handle() {
        }

        @ExceptionHandler
        public void handleException(Exception ex, Writer writer) throws IOException {
            writer.write(ClassUtils.getShortName(ex.getClass()));
        }
    }

    interface ResponseBodyInterface {
        void handle();

        @ExceptionHandler
        @ResponseBody
        String handleException(IllegalArgumentException ex);
    }

    @Controller
    static class ResponseBodyController extends WebApplicationObjectSupport implements ExceptionHandlerExceptionResolverTests.ResponseBodyInterface {
        public void handle() {
        }

        @ExceptionHandler
        @ResponseBody
        public String handleException(IllegalArgumentException ex) {
            return ClassUtils.getShortName(ex.getClass());
        }
    }

    @Controller
    static class IoExceptionController {
        public void handle() {
        }

        @ExceptionHandler(IOException.class)
        public void handleException() {
        }
    }

    @Controller
    static class ModelArgumentController {
        public void handle() {
        }

        @ExceptionHandler
        public void handleException(Exception ex, Model model) {
            model.addAttribute("exceptionClassName", ClassUtils.getShortName(ex.getClass()));
        }
    }

    @Controller
    static class RedirectAttributesController {
        public void handle() {
        }

        @ExceptionHandler
        public String handleException(Exception ex, RedirectAttributes redirectAttributes) {
            redirectAttributes.addFlashAttribute("exceptionClassName", ClassUtils.getShortName(ex.getClass()));
            return "redirect:/";
        }
    }

    @RestControllerAdvice
    @Order(1)
    static class TestExceptionResolver {
        @ExceptionHandler
        public String handleException(IllegalStateException ex) {
            return "TestExceptionResolver: " + (ClassUtils.getShortName(ex.getClass()));
        }

        @ExceptionHandler(ArrayIndexOutOfBoundsException.class)
        public String handleWithHandlerMethod(HandlerMethod handlerMethod) {
            return "HandlerMethod: " + (handlerMethod.getMethod().getName());
        }

        @ExceptionHandler(AssertionError.class)
        public String handleAssertionError(Error err) {
            return err.toString();
        }
    }

    @RestControllerAdvice
    @Order(2)
    static class AnotherTestExceptionResolver {
        @ExceptionHandler({ IllegalStateException.class, IllegalAccessException.class })
        public String handleException(Exception ex) {
            return "AnotherTestExceptionResolver: " + (ClassUtils.getShortName(ex.getClass()));
        }
    }

    @Configuration
    static class MyConfig {
        @Bean
        public ExceptionHandlerExceptionResolverTests.TestExceptionResolver testExceptionResolver() {
            return new ExceptionHandlerExceptionResolverTests.TestExceptionResolver();
        }

        @Bean
        public ExceptionHandlerExceptionResolverTests.AnotherTestExceptionResolver anotherTestExceptionResolver() {
            return new ExceptionHandlerExceptionResolverTests.AnotherTestExceptionResolver();
        }
    }

    @RestControllerAdvice("java.lang")
    @Order(1)
    static class NotCalledTestExceptionResolver {
        @ExceptionHandler
        public String handleException(IllegalStateException ex) {
            return "NotCalledTestExceptionResolver: " + (ClassUtils.getShortName(ex.getClass()));
        }
    }

    @RestControllerAdvice(assignableTypes = WebApplicationObjectSupport.class)
    @Order(2)
    static class BasePackageTestExceptionResolver {
        @ExceptionHandler
        public String handleException(IllegalStateException ex) {
            return "BasePackageTestExceptionResolver: " + (ClassUtils.getShortName(ex.getClass()));
        }
    }

    @RestControllerAdvice
    @Order(3)
    static class DefaultTestExceptionResolver {
        @ExceptionHandler
        public String handleException(IllegalStateException ex) {
            return "DefaultTestExceptionResolver: " + (ClassUtils.getShortName(ex.getClass()));
        }
    }

    @Configuration
    static class MyControllerAdviceConfig {
        @Bean
        public ExceptionHandlerExceptionResolverTests.NotCalledTestExceptionResolver notCalledTestExceptionResolver() {
            return new ExceptionHandlerExceptionResolverTests.NotCalledTestExceptionResolver();
        }

        @Bean
        public ExceptionHandlerExceptionResolverTests.BasePackageTestExceptionResolver basePackageTestExceptionResolver() {
            return new ExceptionHandlerExceptionResolverTests.BasePackageTestExceptionResolver();
        }

        @Bean
        public ExceptionHandlerExceptionResolverTests.DefaultTestExceptionResolver defaultTestExceptionResolver() {
            return new ExceptionHandlerExceptionResolverTests.DefaultTestExceptionResolver();
        }
    }
}

