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


import HttpMethod.DELETE;
import HttpMethod.POST;
import MediaType.APPLICATION_ATOM_XML;
import MediaType.APPLICATION_XML;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import javax.servlet.ServletException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletConfig;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;


/**
 * Test fixture for {@link ResponseEntityExceptionHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class ResponseEntityExceptionHandlerTests {
    private ResponseEntityExceptionHandler exceptionHandlerSupport;

    private DefaultHandlerExceptionResolver defaultExceptionResolver;

    private WebRequest request;

    private MockHttpServletRequest servletRequest;

    private MockHttpServletResponse servletResponse;

    @Test
    public void supportsAllDefaultHandlerExceptionResolverExceptionTypes() throws Exception {
        Class<ResponseEntityExceptionHandler> clazz = ResponseEntityExceptionHandler.class;
        Method handleExceptionMethod = clazz.getMethod("handleException", Exception.class, WebRequest.class);
        ExceptionHandler annotation = handleExceptionMethod.getAnnotation(ExceptionHandler.class);
        List<Class<?>> exceptionTypes = Arrays.asList(annotation.value());
        for (Method method : DefaultHandlerExceptionResolver.class.getDeclaredMethods()) {
            Class<?>[] paramTypes = method.getParameterTypes();
            if ((method.getName().startsWith("handle")) && ((paramTypes.length) == 4)) {
                String name = paramTypes[0].getSimpleName();
                Assert.assertTrue(("@ExceptionHandler is missing " + name), exceptionTypes.contains(paramTypes[0]));
            }
        }
    }

    @Test
    public void httpRequestMethodNotSupported() {
        List<String> supported = Arrays.asList("POST", "DELETE");
        Exception ex = new HttpRequestMethodNotSupportedException("GET", supported);
        ResponseEntity<Object> responseEntity = testException(ex);
        Assert.assertEquals(EnumSet.of(POST, DELETE), responseEntity.getHeaders().getAllow());
    }

    @Test
    public void handleHttpMediaTypeNotSupported() {
        List<MediaType> acceptable = Arrays.asList(APPLICATION_ATOM_XML, APPLICATION_XML);
        Exception ex = new org.springframework.web.HttpMediaTypeNotSupportedException(MediaType.APPLICATION_JSON, acceptable);
        ResponseEntity<Object> responseEntity = testException(ex);
        Assert.assertEquals(acceptable, responseEntity.getHeaders().getAccept());
    }

    @Test
    public void httpMediaTypeNotAcceptable() {
        Exception ex = new HttpMediaTypeNotAcceptableException("");
        testException(ex);
    }

    @Test
    public void missingPathVariable() throws NoSuchMethodException {
        Method method = getClass().getDeclaredMethod("handle", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        Exception ex = new org.springframework.web.bind.MissingPathVariableException("param", parameter);
        testException(ex);
    }

    @Test
    public void missingServletRequestParameter() {
        Exception ex = new MissingServletRequestParameterException("param", "type");
        testException(ex);
    }

    @Test
    public void servletRequestBindingException() {
        Exception ex = new ServletRequestBindingException("message");
        testException(ex);
    }

    @Test
    public void conversionNotSupported() {
        Exception ex = new ConversionNotSupportedException(new Object(), Object.class, null);
        testException(ex);
    }

    @Test
    public void typeMismatch() {
        Exception ex = new TypeMismatchException("foo", String.class);
        testException(ex);
    }

    @Test
    public void httpMessageNotReadable() {
        Exception ex = new HttpMessageNotReadableException("message");
        testException(ex);
    }

    @Test
    public void httpMessageNotWritable() {
        Exception ex = new HttpMessageNotWritableException("");
        testException(ex);
    }

    @Test
    public void methodArgumentNotValid() {
        Exception ex = Mockito.mock(MethodArgumentNotValidException.class);
        testException(ex);
    }

    @Test
    public void missingServletRequestPart() {
        Exception ex = new MissingServletRequestPartException("partName");
        testException(ex);
    }

    @Test
    public void bindException() {
        Exception ex = new BindException(new Object(), "name");
        testException(ex);
    }

    @Test
    public void noHandlerFoundException() {
        ServletServerHttpRequest req = new ServletServerHttpRequest(new MockHttpServletRequest("GET", "/resource"));
        Exception ex = new org.springframework.web.servlet.NoHandlerFoundException(req.getMethod().toString(), req.getServletRequest().getRequestURI(), req.getHeaders());
        testException(ex);
    }

    @Test
    public void asyncRequestTimeoutException() {
        testException(new AsyncRequestTimeoutException());
    }

    @Test
    public void controllerAdvice() throws Exception {
        StaticWebApplicationContext ctx = new StaticWebApplicationContext();
        ctx.registerSingleton("exceptionHandler", ResponseEntityExceptionHandlerTests.ApplicationExceptionHandler.class);
        ctx.refresh();
        ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
        resolver.setApplicationContext(ctx);
        resolver.afterPropertiesSet();
        ServletRequestBindingException ex = new ServletRequestBindingException("message");
        Assert.assertNotNull(resolver.resolveException(this.servletRequest, this.servletResponse, null, ex));
        Assert.assertEquals(400, this.servletResponse.getStatus());
        Assert.assertEquals("error content", this.servletResponse.getContentAsString());
        Assert.assertEquals("someHeaderValue", this.servletResponse.getHeader("someHeader"));
    }

    @Test
    public void controllerAdviceWithNestedException() {
        StaticWebApplicationContext ctx = new StaticWebApplicationContext();
        ctx.registerSingleton("exceptionHandler", ResponseEntityExceptionHandlerTests.ApplicationExceptionHandler.class);
        ctx.refresh();
        ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
        resolver.setApplicationContext(ctx);
        resolver.afterPropertiesSet();
        IllegalStateException ex = new IllegalStateException(new ServletRequestBindingException("message"));
        Assert.assertNull(resolver.resolveException(this.servletRequest, this.servletResponse, null, ex));
    }

    @Test
    public void controllerAdviceWithinDispatcherServlet() throws Exception {
        StaticWebApplicationContext ctx = new StaticWebApplicationContext();
        ctx.registerSingleton("controller", ResponseEntityExceptionHandlerTests.ExceptionThrowingController.class);
        ctx.registerSingleton("exceptionHandler", ResponseEntityExceptionHandlerTests.ApplicationExceptionHandler.class);
        ctx.refresh();
        DispatcherServlet servlet = new DispatcherServlet(ctx);
        servlet.init(new MockServletConfig());
        servlet.service(this.servletRequest, this.servletResponse);
        Assert.assertEquals(400, this.servletResponse.getStatus());
        Assert.assertEquals("error content", this.servletResponse.getContentAsString());
        Assert.assertEquals("someHeaderValue", this.servletResponse.getHeader("someHeader"));
    }

    @Test
    public void controllerAdviceWithNestedExceptionWithinDispatcherServlet() throws Exception {
        StaticWebApplicationContext ctx = new StaticWebApplicationContext();
        ctx.registerSingleton("controller", ResponseEntityExceptionHandlerTests.NestedExceptionThrowingController.class);
        ctx.registerSingleton("exceptionHandler", ResponseEntityExceptionHandlerTests.ApplicationExceptionHandler.class);
        ctx.refresh();
        DispatcherServlet servlet = new DispatcherServlet(ctx);
        servlet.init(new MockServletConfig());
        try {
            servlet.service(this.servletRequest, this.servletResponse);
        } catch (ServletException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof IllegalStateException));
            Assert.assertTrue(((ex.getCause().getCause()) instanceof ServletRequestBindingException));
        }
    }

    @Controller
    private static class ExceptionThrowingController {
        @RequestMapping("/")
        public void handleRequest() throws Exception {
            throw new ServletRequestBindingException("message");
        }
    }

    @Controller
    private static class NestedExceptionThrowingController {
        @RequestMapping("/")
        public void handleRequest() throws Exception {
            throw new IllegalStateException(new ServletRequestBindingException("message"));
        }
    }

    @ControllerAdvice
    private static class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {
        @Override
        protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
            headers.set("someHeader", "someHeaderValue");
            return handleExceptionInternal(ex, "error content", headers, status, request);
        }
    }
}

