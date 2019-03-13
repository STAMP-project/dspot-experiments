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


import HttpServletResponse.SC_BAD_REQUEST;
import HttpServletResponse.SC_METHOD_NOT_ALLOWED;
import HttpServletResponse.SC_OK;
import MediaType.APPLICATION_XML;
import MockHttpServletResponse.SC_CREATED;
import java.beans.ConstructorProperties;
import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.interceptor.SimpleTraceInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockMultipartFile;
import org.springframework.mock.web.test.MockMultipartHttpServletRequest;
import org.springframework.mock.web.test.MockServletConfig;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.tests.sample.beans.DerivedTestBean;
import org.springframework.tests.sample.beans.GenericBean;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.SerializationTestUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.support.StringMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractView;


/**
 *
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
public class ServletAnnotationControllerHandlerMethodTests extends AbstractServletHandlerMethodTests {
    @Test
    public void emptyValueMapping() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ControllerWithEmptyValueMapping.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
        request.setContextPath("/foo");
        request.setServletPath("");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("test", response.getContentAsString());
    }

    @Test
    public void errorThrownFromHandlerMethod() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ControllerWithErrorThrown.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo");
        request.setContextPath("/foo");
        request.setServletPath("");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("test", response.getContentAsString());
    }

    @Test
    public void customAnnotationController() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.CustomAnnotationController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("Invalid response status code", SC_OK, response.getStatus());
    }

    @Test
    public void requiredParamMissing() throws Exception {
        WebApplicationContext webAppContext = initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.RequiredParamController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("Invalid response status code", SC_BAD_REQUEST, response.getStatus());
        Assert.assertTrue(webAppContext.isSingleton(ServletAnnotationControllerHandlerMethodTests.RequiredParamController.class.getSimpleName()));
    }

    @Test
    public void typeConversionError() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.RequiredParamController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath.do");
        request.addParameter("id", "foo");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("Invalid response status code", SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void optionalParamPresent() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.OptionalParamController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath.do");
        request.addParameter("id", "val");
        request.addParameter("flag", "true");
        request.addHeader("header", "otherVal");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("val-true-otherVal", response.getContentAsString());
    }

    @Test
    public void optionalParamMissing() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.OptionalParamController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("null-false-null", response.getContentAsString());
    }

    @Test
    public void defaultParameters() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.DefaultValueParamController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("foo--bar", response.getContentAsString());
    }

    @Test
    public void defaultExpressionParameters() throws Exception {
        initServlet(( wac) -> {
            RootBeanDefinition ppc = new RootBeanDefinition(.class);
            ppc.getPropertyValues().add("properties", "myKey=foo");
            wac.registerBeanDefinition("ppc", ppc);
        }, ServletAnnotationControllerHandlerMethodTests.DefaultExpressionValueParamController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myApp/myPath.do");
        request.setContextPath("/myApp");
        MockHttpServletResponse response = new MockHttpServletResponse();
        System.setProperty("myHeader", "bar");
        try {
            getServlet().service(request, response);
        } finally {
            System.clearProperty("myHeader");
        }
        Assert.assertEquals("foo-bar-/myApp", response.getContentAsString());
    }

    @Test
    public void typeNestedSetBinding() throws Exception {
        initServlet(( wac) -> {
            RootBeanDefinition csDef = new RootBeanDefinition(.class);
            csDef.getPropertyValues().add("converters", new org.springframework.web.servlet.mvc.method.annotation.TestBeanConverter());
            RootBeanDefinition wbiDef = new RootBeanDefinition(.class);
            wbiDef.getPropertyValues().add("conversionService", csDef);
            RootBeanDefinition adapterDef = new RootBeanDefinition(.class);
            adapterDef.getPropertyValues().add("webBindingInitializer", wbiDef);
            wac.registerBeanDefinition("handlerAdapter", adapterDef);
        }, ServletAnnotationControllerHandlerMethodTests.NestedSetController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath.do");
        request.addParameter("testBeanSet", "1", "2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("[1, 2]-org.springframework.tests.sample.beans.TestBean", response.getContentAsString());
    }

    // SPR-12903
    @Test
    public void pathVariableWithCustomConverter() throws Exception {
        initServlet(( wac) -> {
            RootBeanDefinition csDef = new RootBeanDefinition(.class);
            csDef.getPropertyValues().add("converters", new org.springframework.web.servlet.mvc.method.annotation.AnnotatedExceptionRaisingConverter());
            RootBeanDefinition wbiDef = new RootBeanDefinition(.class);
            wbiDef.getPropertyValues().add("conversionService", csDef);
            RootBeanDefinition adapterDef = new RootBeanDefinition(.class);
            adapterDef.getPropertyValues().add("webBindingInitializer", wbiDef);
            wac.registerBeanDefinition("handlerAdapter", adapterDef);
        }, ServletAnnotationControllerHandlerMethodTests.PathVariableWithCustomConverterController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath/1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(404, response.getStatus());
    }

    @Test
    public void methodNotAllowed() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.MethodNotAllowedController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("Invalid response status", SC_METHOD_NOT_ALLOWED, response.getStatus());
        String allowHeader = response.getHeader("Allow");
        Assert.assertNotNull("No Allow header", allowHeader);
        Set<String> allowedMethods = new HashSet<>();
        allowedMethods.addAll(Arrays.asList(StringUtils.delimitedListToStringArray(allowHeader, ", ")));
        Assert.assertEquals("Invalid amount of supported methods", 6, allowedMethods.size());
        Assert.assertTrue("PUT not allowed", allowedMethods.contains("PUT"));
        Assert.assertTrue("DELETE not allowed", allowedMethods.contains("DELETE"));
        Assert.assertTrue("HEAD not allowed", allowedMethods.contains("HEAD"));
        Assert.assertTrue("TRACE not allowed", allowedMethods.contains("TRACE"));
        Assert.assertTrue("OPTIONS not allowed", allowedMethods.contains("OPTIONS"));
        Assert.assertTrue("POST not allowed", allowedMethods.contains("POST"));
    }

    @Test
    public void emptyParameterListHandleMethod() throws Exception {
        initServlet(( wac) -> {
            RootBeanDefinition vrDef = new RootBeanDefinition(.class);
            vrDef.getPropertyValues().add("suffix", ".jsp");
            wac.registerBeanDefinition("viewResolver", vrDef);
        }, ServletAnnotationControllerHandlerMethodTests.EmptyParameterListHandlerMethodController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/emptyParameterListHandler");
        MockHttpServletResponse response = new MockHttpServletResponse();
        ServletAnnotationControllerHandlerMethodTests.EmptyParameterListHandlerMethodController.called = false;
        getServlet().service(request, response);
        Assert.assertTrue(ServletAnnotationControllerHandlerMethodTests.EmptyParameterListHandlerMethodController.called);
        Assert.assertEquals("", response.getContentAsString());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void sessionAttributeExposure() throws Exception {
        initServlet(( wac) -> wac.registerBeanDefinition("viewResolver", new RootBeanDefinition(.class)), ServletAnnotationControllerHandlerMethodTests.MySessionAttributesController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPage");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("page1", request.getAttribute("viewName"));
        HttpSession session = request.getSession();
        Assert.assertTrue(((session.getAttribute("object1")) != null));
        Assert.assertTrue(((session.getAttribute("object2")) != null));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("object1"));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("object2"));
        request = new MockHttpServletRequest("POST", "/myPage");
        request.setSession(session);
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("page2", request.getAttribute("viewName"));
        Assert.assertTrue(((session.getAttribute("object1")) != null));
        Assert.assertTrue(((session.getAttribute("object2")) != null));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("object1"));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("object2"));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void sessionAttributeExposureWithInterface() throws Exception {
        initServlet(( wac) -> {
            wac.registerBeanDefinition("viewResolver", new RootBeanDefinition(.class));
            DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
            autoProxyCreator.setBeanFactory(wac.getBeanFactory());
            wac.getBeanFactory().addBeanPostProcessor(autoProxyCreator);
            wac.getBeanFactory().registerSingleton("advisor", new DefaultPointcutAdvisor(new SimpleTraceInterceptor()));
        }, ServletAnnotationControllerHandlerMethodTests.MySessionAttributesControllerImpl.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPage");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("page1", request.getAttribute("viewName"));
        HttpSession session = request.getSession();
        Assert.assertTrue(((session.getAttribute("object1")) != null));
        Assert.assertTrue(((session.getAttribute("object2")) != null));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("object1"));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("object2"));
        request = new MockHttpServletRequest("POST", "/myPage");
        request.setSession(session);
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("page2", request.getAttribute("viewName"));
        Assert.assertTrue(((session.getAttribute("object1")) != null));
        Assert.assertTrue(((session.getAttribute("object2")) != null));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("object1"));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("object2"));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void parameterizedAnnotatedInterface() throws Exception {
        initServlet(( wac) -> wac.registerBeanDefinition("viewResolver", new RootBeanDefinition(.class)), ServletAnnotationControllerHandlerMethodTests.MyParameterizedControllerImpl.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPage");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("page1", request.getAttribute("viewName"));
        HttpSession session = request.getSession();
        Assert.assertTrue(((session.getAttribute("object1")) != null));
        Assert.assertTrue(((session.getAttribute("object2")) != null));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("object1"));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("object2"));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("testBeanList"));
        request = new MockHttpServletRequest("POST", "/myPage");
        request.setSession(session);
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("page2", request.getAttribute("viewName"));
        Assert.assertTrue(((session.getAttribute("object1")) != null));
        Assert.assertTrue(((session.getAttribute("object2")) != null));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("object1"));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("object2"));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("testBeanList"));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void parameterizedAnnotatedInterfaceWithOverriddenMappingsInImpl() throws Exception {
        initServlet(( wac) -> wac.registerBeanDefinition("viewResolver", new RootBeanDefinition(.class)), ServletAnnotationControllerHandlerMethodTests.MyParameterizedControllerImplWithOverriddenMappings.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPage");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("page1", request.getAttribute("viewName"));
        HttpSession session = request.getSession();
        Assert.assertTrue(((session.getAttribute("object1")) != null));
        Assert.assertTrue(((session.getAttribute("object2")) != null));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("object1"));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("object2"));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("testBeanList"));
        request = new MockHttpServletRequest("POST", "/myPage");
        request.setSession(session);
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("page2", request.getAttribute("viewName"));
        Assert.assertTrue(((session.getAttribute("object1")) != null));
        Assert.assertTrue(((session.getAttribute("object2")) != null));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("object1"));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("object2"));
        Assert.assertTrue(((Map) (session.getAttribute("model"))).containsKey("testBeanList"));
    }

    @Test
    public void adaptedHandleMethods() throws Exception {
        doTestAdaptedHandleMethods(ServletAnnotationControllerHandlerMethodTests.MyAdaptedController.class);
    }

    @Test
    public void adaptedHandleMethods2() throws Exception {
        doTestAdaptedHandleMethods(ServletAnnotationControllerHandlerMethodTests.MyAdaptedController2.class);
    }

    @Test
    public void adaptedHandleMethods3() throws Exception {
        doTestAdaptedHandleMethods(ServletAnnotationControllerHandlerMethodTests.MyAdaptedController3.class);
    }

    @Test
    public void formController() throws Exception {
        initServlet(( wac) -> wac.registerBeanDefinition("viewResolver", new RootBeanDefinition(.class)), ServletAnnotationControllerHandlerMethodTests.MyFormController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath.do");
        request.addParameter("name", "name1");
        request.addParameter("age", "value2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myView-name1-typeMismatch-tb1-myValue", response.getContentAsString());
    }

    @Test
    public void modelFormController() throws Exception {
        initServlet(( wac) -> wac.registerBeanDefinition("viewResolver", new RootBeanDefinition(.class)), ServletAnnotationControllerHandlerMethodTests.MyModelFormController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath.do");
        request.addParameter("name", "name1");
        request.addParameter("age", "value2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myPath-name1-typeMismatch-tb1-myValue-yourValue", response.getContentAsString());
    }

    @Test
    public void lateBindingFormController() throws Exception {
        initServlet(( wac) -> wac.registerBeanDefinition("viewResolver", new RootBeanDefinition(.class)), ServletAnnotationControllerHandlerMethodTests.LateBindingFormController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath.do");
        request.addParameter("name", "name1");
        request.addParameter("age", "value2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myView-name1-typeMismatch-tb1-myValue", response.getContentAsString());
    }

    @Test
    public void proxiedFormController() throws Exception {
        initServlet(( wac) -> {
            wac.registerBeanDefinition("viewResolver", new RootBeanDefinition(.class));
            DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
            autoProxyCreator.setBeanFactory(wac.getBeanFactory());
            wac.getBeanFactory().addBeanPostProcessor(autoProxyCreator);
            wac.getBeanFactory().registerSingleton("advisor", new DefaultPointcutAdvisor(new SimpleTraceInterceptor()));
        }, ServletAnnotationControllerHandlerMethodTests.MyFormController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath.do");
        request.addParameter("name", "name1");
        request.addParameter("age", "value2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myView-name1-typeMismatch-tb1-myValue", response.getContentAsString());
    }

    @Test
    public void commandProvidingFormControllerWithCustomEditor() throws Exception {
        initServlet(( wac) -> {
            wac.registerBeanDefinition("viewResolver", new RootBeanDefinition(.class));
            RootBeanDefinition adapterDef = new RootBeanDefinition(.class);
            adapterDef.getPropertyValues().add("webBindingInitializer", new org.springframework.web.servlet.mvc.method.annotation.MyWebBindingInitializer());
            wac.registerBeanDefinition("handlerAdapter", adapterDef);
        }, ServletAnnotationControllerHandlerMethodTests.MyCommandProvidingFormController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath.do");
        request.addParameter("defaultName", "myDefaultName");
        request.addParameter("age", "value2");
        request.addParameter("date", "2007-10-02");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myView-String:myDefaultName-typeMismatch-tb1-myOriginalValue", response.getContentAsString());
    }

    @Test
    public void typedCommandProvidingFormController() throws Exception {
        initServlet(( wac) -> {
            wac.registerBeanDefinition("viewResolver", new RootBeanDefinition(.class));
            RootBeanDefinition adapterDef = new RootBeanDefinition(.class);
            adapterDef.getPropertyValues().add("webBindingInitializer", new org.springframework.web.servlet.mvc.method.annotation.MyWebBindingInitializer());
            List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>();
            argumentResolvers.add(new ServletWebArgumentResolverAdapter(new org.springframework.web.servlet.mvc.method.annotation.MySpecialArgumentResolver()));
            adapterDef.getPropertyValues().add("customArgumentResolvers", argumentResolvers);
            wac.registerBeanDefinition("handlerAdapter", adapterDef);
        }, ServletAnnotationControllerHandlerMethodTests.MyTypedCommandProvidingFormController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath.do");
        request.addParameter("defaultName", "10");
        request.addParameter("age", "value2");
        request.addParameter("date", "2007-10-02");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myView-Integer:10-typeMismatch-tb1-myOriginalValue", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/myOtherPath.do");
        request.addParameter("defaultName", "10");
        request.addParameter("age", "value2");
        request.addParameter("date", "2007-10-02");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myView-myName-typeMismatch-tb1-myOriginalValue", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/myThirdPath.do");
        request.addParameter("defaultName", "10");
        request.addParameter("age", "100");
        request.addParameter("date", "2007-10-02");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myView-special-99-special-99", response.getContentAsString());
    }

    @Test
    public void binderInitializingCommandProvidingFormController() throws Exception {
        initServlet(( wac) -> wac.registerBeanDefinition("viewResolver", new RootBeanDefinition(.class)), ServletAnnotationControllerHandlerMethodTests.MyBinderInitializingCommandProvidingFormController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath.do");
        request.addParameter("defaultName", "myDefaultName");
        request.addParameter("age", "value2");
        request.addParameter("date", "2007-10-02");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myView-String:myDefaultName-typeMismatch-tb1-myOriginalValue", response.getContentAsString());
    }

    @Test
    public void specificBinderInitializingCommandProvidingFormController() throws Exception {
        initServlet(( wac) -> wac.registerBeanDefinition("viewResolver", new RootBeanDefinition(.class)), ServletAnnotationControllerHandlerMethodTests.MySpecificBinderInitializingCommandProvidingFormController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath.do");
        request.addParameter("defaultName", "myDefaultName");
        request.addParameter("age", "value2");
        request.addParameter("date", "2007-10-02");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myView-String:myDefaultName-typeMismatch-tb1-myOriginalValue", response.getContentAsString());
    }

    @Test
    public void parameterDispatchingController() throws Exception {
        final MockServletContext servletContext = new MockServletContext();
        final MockServletConfig servletConfig = new MockServletConfig(servletContext);
        WebApplicationContext webAppContext = initServlet(( wac) -> {
            wac.setServletContext(servletContext);
            AnnotationConfigUtils.registerAnnotationConfigProcessors(wac);
            wac.getBeanFactory().registerResolvableDependency(.class, servletConfig);
        }, ServletAnnotationControllerHandlerMethodTests.MyParameterDispatchingController.class);
        MockHttpServletRequest request = new MockHttpServletRequest(servletContext, "GET", "/myPath.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpSession session = request.getSession();
        getServlet().service(request, response);
        Assert.assertEquals("myView", response.getContentAsString());
        Assert.assertSame(servletContext, request.getAttribute("servletContext"));
        Assert.assertSame(servletConfig, request.getAttribute("servletConfig"));
        Assert.assertSame(session.getId(), request.getAttribute("sessionId"));
        Assert.assertSame(request.getRequestURI(), request.getAttribute("requestUri"));
        Assert.assertSame(request.getLocale(), request.getAttribute("locale"));
        request = new MockHttpServletRequest(servletContext, "GET", "/myPath.do");
        response = new MockHttpServletResponse();
        session = request.getSession();
        getServlet().service(request, response);
        Assert.assertEquals("myView", response.getContentAsString());
        Assert.assertSame(servletContext, request.getAttribute("servletContext"));
        Assert.assertSame(servletConfig, request.getAttribute("servletConfig"));
        Assert.assertSame(session.getId(), request.getAttribute("sessionId"));
        Assert.assertSame(request.getRequestURI(), request.getAttribute("requestUri"));
        request = new MockHttpServletRequest(servletContext, "GET", "/myPath.do");
        request.addParameter("view", "other");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myOtherView", response.getContentAsString());
        request = new MockHttpServletRequest(servletContext, "GET", "/myPath.do");
        request.addParameter("view", "my");
        request.addParameter("lang", "de");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myLangView", response.getContentAsString());
        request = new MockHttpServletRequest(servletContext, "GET", "/myPath.do");
        request.addParameter("surprise", "!");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("mySurpriseView", response.getContentAsString());
        ServletAnnotationControllerHandlerMethodTests.MyParameterDispatchingController deserialized = ((ServletAnnotationControllerHandlerMethodTests.MyParameterDispatchingController) (SerializationTestUtils.serializeAndDeserialize(webAppContext.getBean(ServletAnnotationControllerHandlerMethodTests.MyParameterDispatchingController.class.getSimpleName()))));
        Assert.assertNotNull(deserialized.request);
        Assert.assertNotNull(deserialized.session);
    }

    @Test
    public void relativePathDispatchingController() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.MyRelativePathDispatchingController.class);
        getServlet().init(new MockServletConfig());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myApp/myHandle");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myView", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/myApp/myOther");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myOtherView", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/myApp/myLang");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myLangView", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/myApp/surprise.do");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("mySurpriseView", response.getContentAsString());
    }

    @Test
    public void relativeMethodPathDispatchingController() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.MyRelativeMethodPathDispatchingController.class);
        getServlet().init(new MockServletConfig());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myApp/myHandle");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myView", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/yourApp/myOther");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myOtherView", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/hisApp/myLang");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myLangView", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/herApp/surprise.do");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("mySurpriseView", response.getContentAsString());
    }

    @Test
    public void nullCommandController() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.MyNullCommandController.class);
        getServlet().init(new MockServletConfig());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/myPath");
        request.setUserPrincipal(new ServletAnnotationControllerHandlerMethodTests.OtherPrincipal());
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myView", response.getContentAsString());
    }

    @Test
    public void equivalentMappingsWithSameMethodName() throws Exception {
        try {
            initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ChildController.class);
            Assert.fail("Expected 'method already mapped' error");
        } catch (BeanCreationException e) {
            Assert.assertTrue(((e.getCause()) instanceof IllegalStateException));
            Assert.assertTrue(e.getCause().getMessage().contains("Ambiguous mapping"));
        }
    }

    @Test
    public void pathOrdering() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.PathOrderingController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/dir/myPath1.do");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("method1", response.getContentAsString());
    }

    @Test
    public void requestBodyResponseBody() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.RequestResponseBodyController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/something");
        String requestBody = "Hello World";
        request.setContent(requestBody.getBytes("UTF-8"));
        request.addHeader("Content-Type", "text/plain; charset=utf-8");
        request.addHeader("Accept", "text/*, */*");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(requestBody, response.getContentAsString());
    }

    @Test
    public void httpPatch() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.RequestResponseBodyController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("PATCH", "/something");
        String requestBody = "Hello world!";
        request.setContent(requestBody.getBytes("UTF-8"));
        request.addHeader("Content-Type", "text/plain; charset=utf-8");
        request.addHeader("Accept", "text/*, */*");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(requestBody, response.getContentAsString());
    }

    @Test
    public void responseBodyNoAcceptableMediaType() throws Exception {
        initServlet(( wac) -> {
            RootBeanDefinition adapterDef = new RootBeanDefinition(.class);
            StringHttpMessageConverter converter = new StringHttpMessageConverter();
            adapterDef.getPropertyValues().add("messageConverters", converter);
            wac.registerBeanDefinition("handlerAdapter", adapterDef);
        }, ServletAnnotationControllerHandlerMethodTests.RequestResponseBodyProducesController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/something");
        String requestBody = "Hello World";
        request.setContent(requestBody.getBytes("UTF-8"));
        request.addHeader("Content-Type", "text/plain; charset=utf-8");
        request.addHeader("Accept", "application/pdf, application/msword");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(406, response.getStatus());
    }

    @Test
    public void responseBodyWildCardMediaType() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.RequestResponseBodyController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/something");
        String requestBody = "Hello World";
        request.setContent(requestBody.getBytes("UTF-8"));
        request.addHeader("Content-Type", "text/plain; charset=utf-8");
        request.addHeader("Accept", "*/*");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(requestBody, response.getContentAsString());
    }

    @Test
    public void unsupportedRequestBody() throws Exception {
        initServlet(( wac) -> {
            RootBeanDefinition adapterDef = new RootBeanDefinition(.class);
            adapterDef.getPropertyValues().add("messageConverters", new ByteArrayHttpMessageConverter());
            wac.registerBeanDefinition("handlerAdapter", adapterDef);
        }, ServletAnnotationControllerHandlerMethodTests.RequestResponseBodyController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/something");
        String requestBody = "Hello World";
        request.setContent(requestBody.getBytes("UTF-8"));
        request.addHeader("Content-Type", "application/pdf");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(415, response.getStatus());
        Assert.assertNotNull("No Accept response header set", response.getHeader("Accept"));
    }

    @Test
    public void responseBodyNoAcceptHeader() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.RequestResponseBodyController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/something");
        String requestBody = "Hello World";
        request.setContent(requestBody.getBytes("UTF-8"));
        request.addHeader("Content-Type", "text/plain; charset=utf-8");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals(requestBody, response.getContentAsString());
    }

    @Test
    public void badRequestRequestBody() throws Exception {
        initServlet(( wac) -> {
            RootBeanDefinition adapterDef = new RootBeanDefinition(.class);
            adapterDef.getPropertyValues().add("messageConverters", new org.springframework.web.servlet.mvc.method.annotation.NotReadableMessageConverter());
            wac.registerBeanDefinition("handlerAdapter", adapterDef);
        }, ServletAnnotationControllerHandlerMethodTests.RequestResponseBodyController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/something");
        String requestBody = "Hello World";
        request.setContent(requestBody.getBytes("UTF-8"));
        request.addHeader("Content-Type", "application/pdf");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("Invalid response status code", SC_BAD_REQUEST, response.getStatus());
    }

    @Test
    public void httpEntity() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ResponseEntityController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/foo");
        String requestBody = "Hello World";
        request.setContent(requestBody.getBytes("UTF-8"));
        request.addHeader("Content-Type", "text/plain; charset=utf-8");
        request.addHeader("Accept", "text/*, */*");
        request.addHeader("MyRequestHeader", "MyValue");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals(requestBody, response.getContentAsString());
        Assert.assertEquals("MyValue", response.getHeader("MyResponseHeader"));
        request = new MockHttpServletRequest("GET", "/bar");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("MyValue", response.getHeader("MyResponseHeader"));
        Assert.assertEquals(404, response.getStatus());
    }

    // SPR-16172
    @Test
    public void httpEntityWithContentType() throws Exception {
        initServlet(( wac) -> {
            RootBeanDefinition adapterDef = new RootBeanDefinition(.class);
            List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
            messageConverters.add(new MappingJackson2HttpMessageConverter());
            messageConverters.add(new Jaxb2RootElementHttpMessageConverter());
            adapterDef.getPropertyValues().add("messageConverters", messageConverters);
            wac.registerBeanDefinition("handlerAdapter", adapterDef);
        }, ServletAnnotationControllerHandlerMethodTests.ResponseEntityController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test-entity");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("application/xml", response.getHeader("Content-Type"));
        Assert.assertEquals(("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<testEntity><name>Foo Bar</name></testEntity>"), response.getContentAsString());
    }

    // SPR-6877
    @Test
    public void overlappingMessageConvertersRequestBody() throws Exception {
        initServlet(( wac) -> {
            RootBeanDefinition adapterDef = new RootBeanDefinition(.class);
            List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
            messageConverters.add(new StringHttpMessageConverter());
            messageConverters.add(new org.springframework.web.servlet.mvc.method.annotation.SimpleMessageConverter(new MediaType("application", "json"), MediaType.ALL));
            adapterDef.getPropertyValues().add("messageConverters", messageConverters);
            wac.registerBeanDefinition("handlerAdapter", adapterDef);
        }, ServletAnnotationControllerHandlerMethodTests.RequestResponseBodyController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/something");
        request.setContent("Hello World".getBytes("UTF-8"));
        request.addHeader("Content-Type", "text/plain; charset=utf-8");
        request.addHeader("Accept", "application/json, text/javascript, */*");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("Invalid content-type", "application/json;charset=ISO-8859-1", response.getHeader("Content-Type"));
    }

    @Test
    public void responseBodyVoid() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ResponseBodyVoidController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/something");
        request.addHeader("Accept", "text/*, */*");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
    }

    @Test
    public void responseBodyArgMismatch() throws Exception {
        initServlet(( wac) -> {
            Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
            marshaller.setClassesToBeBound(.class, .class);
            try {
                marshaller.afterPropertiesSet();
            } catch ( ex) {
                throw new <ex>BeanCreationException(ex.getMessage());
            }
            MarshallingHttpMessageConverter messageConverter = new MarshallingHttpMessageConverter(marshaller);
            RootBeanDefinition adapterDef = new RootBeanDefinition(.class);
            adapterDef.getPropertyValues().add("messageConverters", messageConverter);
            wac.registerBeanDefinition("handlerAdapter", adapterDef);
        }, ServletAnnotationControllerHandlerMethodTests.RequestBodyArgMismatchController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/something");
        String requestBody = "<b/>";
        request.setContent(requestBody.getBytes("UTF-8"));
        request.addHeader("Content-Type", "application/xml; charset=utf-8");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(400, response.getStatus());
    }

    @Test
    public void contentTypeHeaders() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ContentTypeHeadersController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/something");
        request.setContentType("application/pdf");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("pdf", response.getContentAsString());
        request = new MockHttpServletRequest("POST", "/something");
        request.setContentType("text/html");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("text", response.getContentAsString());
        request = new MockHttpServletRequest("POST", "/something");
        request.setContentType("application/xml");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(415, response.getStatus());
    }

    @Test
    public void consumes() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ConsumesController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/something");
        request.setContentType("application/pdf");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("pdf", response.getContentAsString());
        request = new MockHttpServletRequest("POST", "/something");
        request.setContentType("text/html");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("text", response.getContentAsString());
        request = new MockHttpServletRequest("POST", "/something");
        request.setContentType("application/xml");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(415, response.getStatus());
    }

    @Test
    public void negatedContentTypeHeaders() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.NegatedContentTypeHeadersController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/something");
        request.setContentType("application/pdf");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("pdf", response.getContentAsString());
        request = new MockHttpServletRequest("POST", "/something");
        request.setContentType("text/html");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("non-pdf", response.getContentAsString());
    }

    @Test
    public void acceptHeaders() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.AcceptHeadersController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/something");
        request.addHeader("Accept", "text/html");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("html", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/something");
        request.addHeader("Accept", "application/xml");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("xml", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/something");
        request.addHeader("Accept", "application/xml, text/html");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("xml", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/something");
        request.addHeader("Accept", "text/html;q=0.9, application/xml");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("xml", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/something");
        request.addHeader("Accept", "application/msword");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(406, response.getStatus());
    }

    @Test
    public void produces() throws Exception {
        initServlet(( wac) -> {
            List<HttpMessageConverter<?>> converters = new ArrayList<>();
            converters.add(new MappingJackson2HttpMessageConverter());
            converters.add(new Jaxb2RootElementHttpMessageConverter());
            RootBeanDefinition beanDef;
            beanDef = new RootBeanDefinition(.class);
            beanDef.getPropertyValues().add("messageConverters", converters);
            wac.registerBeanDefinition("handlerAdapter", beanDef);
            beanDef = new RootBeanDefinition(.class);
            beanDef.getPropertyValues().add("messageConverters", converters);
            wac.registerBeanDefinition("requestMappingResolver", beanDef);
        }, ServletAnnotationControllerHandlerMethodTests.ProducesController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/something");
        request.addHeader("Accept", "text/html");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("html", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/something");
        request.addHeader("Accept", "application/xml");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("xml", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/something");
        request.addHeader("Accept", "application/xml, text/html");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("xml", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/something");
        request.addHeader("Accept", "text/html;q=0.9, application/xml");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("xml", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/something");
        request.addHeader("Accept", "application/msword");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(406, response.getStatus());
        // SPR-16318
        request = new MockHttpServletRequest("GET", "/something");
        request.addHeader("Accept", "text/csv,application/problem+json");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(500, response.getStatus());
        Assert.assertEquals("application/problem+json;charset=UTF-8", response.getContentType());
        Assert.assertEquals("{\"reason\":\"error\"}", response.getContentAsString());
    }

    @Test
    public void responseStatus() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ResponseStatusController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/something");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("something", response.getContentAsString());
        Assert.assertEquals(201, response.getStatus());
        Assert.assertEquals("It's alive!", response.getErrorMessage());
    }

    @Test
    public void mavResolver() throws Exception {
        initServlet(( wac) -> {
            RootBeanDefinition adapterDef = new RootBeanDefinition(.class);
            ModelAndViewResolver[] mavResolvers = new ModelAndViewResolver[]{ new org.springframework.web.servlet.mvc.method.annotation.MyModelAndViewResolver() };
            adapterDef.getPropertyValues().add("modelAndViewResolvers", mavResolvers);
            wac.registerBeanDefinition("handlerAdapter", adapterDef);
        }, ServletAnnotationControllerHandlerMethodTests.ModelAndViewResolverController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myValue", response.getContentAsString());
    }

    @Test
    public void bindingCookieValue() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.BindingCookieValueController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        request.setCookies(new Cookie("date", "2008-11-18"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("test-2008", response.getContentAsString());
    }

    @Test
    public void ambiguousParams() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.AmbiguousParamsController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("noParams", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/test");
        request.addParameter("myParam", "42");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("myParam-42", response.getContentAsString());
    }

    // SPR-9062
    @Test
    public void ambiguousPathAndRequestMethod() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.AmbiguousPathAndRequestMethodController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bug/EXISTING");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("Pattern", response.getContentAsString());
    }

    @Test
    public void bridgeMethods() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.TestControllerImpl.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/method");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
    }

    @Test
    public void bridgeMethodsWithMultipleInterfaces() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ArticleController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/method");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
    }

    @Test
    public void requestParamMap() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.RequestParamMapController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/map");
        request.addParameter("key1", "value1");
        request.addParameter("key2", new String[]{ "value21", "value22" });
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("key1=value1,key2=value21", response.getContentAsString());
        request.setRequestURI("/multiValueMap");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("key1=[value1],key2=[value21,value22]", response.getContentAsString());
    }

    @Test
    public void requestHeaderMap() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.RequestHeaderMapController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/map");
        request.addHeader("Content-Type", "text/html");
        request.addHeader("Custom-Header", new String[]{ "value21", "value22" });
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("Content-Type=text/html,Custom-Header=value21", response.getContentAsString());
        request.setRequestURI("/multiValueMap");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("Content-Type=[text/html],Custom-Header=[value21,value22]", response.getContentAsString());
        request.setRequestURI("/httpHeaders");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("Content-Type=[text/html],Custom-Header=[value21,value22]", response.getContentAsString());
    }

    @Test
    public void requestMappingInterface() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.IMyControllerImpl.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/handle");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("handle null", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/handle");
        request.addParameter("p", "value");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("handle value", response.getContentAsString());
    }

    @Test
    public void requestMappingInterfaceWithProxy() throws Exception {
        initServlet(( wac) -> {
            DefaultAdvisorAutoProxyCreator autoProxyCreator = new DefaultAdvisorAutoProxyCreator();
            autoProxyCreator.setBeanFactory(wac.getBeanFactory());
            wac.getBeanFactory().addBeanPostProcessor(autoProxyCreator);
            wac.getBeanFactory().registerSingleton("advisor", new DefaultPointcutAdvisor(new SimpleTraceInterceptor()));
        }, ServletAnnotationControllerHandlerMethodTests.IMyControllerImpl.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/handle");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("handle null", response.getContentAsString());
        request = new MockHttpServletRequest("GET", "/handle");
        request.addParameter("p", "value");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("handle value", response.getContentAsString());
    }

    @Test
    public void requestMappingBaseClass() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.MyAbstractControllerImpl.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/handle");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("handle", response.getContentAsString());
    }

    @Test
    public void trailingSlash() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.TrailingSlashController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/foo/");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("templatePath", response.getContentAsString());
    }

    /* See SPR-6021 */
    @Test
    public void customMapEditor() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.CustomMapEditorController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/handle");
        request.addParameter("map", "bar");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("test-{foo=bar}", response.getContentAsString());
    }

    @Test
    public void multipartFileAsSingleString() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.MultipartController.class);
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        request.setRequestURI("/singleString");
        request.addFile(new MockMultipartFile("content", "Juergen".getBytes()));
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("Juergen", response.getContentAsString());
    }

    @Test
    public void regularParameterAsSingleString() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.MultipartController.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/singleString");
        request.setMethod("POST");
        request.addParameter("content", "Juergen");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("Juergen", response.getContentAsString());
    }

    @Test
    public void multipartFileAsStringArray() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.MultipartController.class);
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        request.setRequestURI("/stringArray");
        request.addFile(new MockMultipartFile("content", "Juergen".getBytes()));
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("Juergen", response.getContentAsString());
    }

    @Test
    public void regularParameterAsStringArray() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.MultipartController.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/stringArray");
        request.setMethod("POST");
        request.addParameter("content", "Juergen");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("Juergen", response.getContentAsString());
    }

    @Test
    public void multipartFilesAsStringArray() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.MultipartController.class);
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        request.setRequestURI("/stringArray");
        request.addFile(new MockMultipartFile("content", "Juergen".getBytes()));
        request.addFile(new MockMultipartFile("content", "Eva".getBytes()));
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("Juergen-Eva", response.getContentAsString());
    }

    @Test
    public void regularParametersAsStringArray() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.MultipartController.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/stringArray");
        request.setMethod("POST");
        request.addParameter("content", "Juergen");
        request.addParameter("content", "Eva");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("Juergen-Eva", response.getContentAsString());
    }

    @Test
    public void parameterCsvAsStringArray() throws Exception {
        initServlet(( wac) -> {
            RootBeanDefinition csDef = new RootBeanDefinition(.class);
            RootBeanDefinition wbiDef = new RootBeanDefinition(.class);
            wbiDef.getPropertyValues().add("conversionService", csDef);
            RootBeanDefinition adapterDef = new RootBeanDefinition(.class);
            adapterDef.getPropertyValues().add("webBindingInitializer", wbiDef);
            wac.registerBeanDefinition("handlerAdapter", adapterDef);
        }, ServletAnnotationControllerHandlerMethodTests.CsvController.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/integerArray");
        request.setMethod("POST");
        request.addParameter("content", "1,2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("1-2", response.getContentAsString());
    }

    @Test
    public void testMatchWithoutMethodLevelPath() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.NoPathGetAndM2PostController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/t1/m2");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(405, response.getStatus());
    }

    // SPR-8536
    @Test
    public void testHeadersCondition() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.HeadersConditionController.class);
        // No "Accept" header
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("home", response.getForwardedUrl());
        // Accept "*/*"
        request = new MockHttpServletRequest("GET", "/");
        request.addHeader("Accept", "*/*");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("home", response.getForwardedUrl());
        // Accept "application/json"
        request = new MockHttpServletRequest("GET", "/");
        request.addHeader("Accept", "application/json");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("application/json;charset=ISO-8859-1", response.getHeader("Content-Type"));
        Assert.assertEquals("homeJson", response.getContentAsString());
    }

    @Test
    public void redirectAttribute() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.RedirectAttributesController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/messages");
        HttpSession session = request.getSession();
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        // POST -> bind error
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("messages/new", response.getForwardedUrl());
        Assert.assertTrue(RequestContextUtils.getOutputFlashMap(request).isEmpty());
        // POST -> success
        request = new MockHttpServletRequest("POST", "/messages");
        request.setSession(session);
        request.addParameter("name", "Jeff");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(302, response.getStatus());
        Assert.assertEquals("/messages/1?name=value", response.getRedirectedUrl());
        Assert.assertEquals("yay!", RequestContextUtils.getOutputFlashMap(request).get("successMessage"));
        // GET after POST
        request = new MockHttpServletRequest("GET", "/messages/1");
        request.setQueryString("name=value");
        request.setSession(session);
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("Got: yay!", response.getContentAsString());
        Assert.assertTrue(RequestContextUtils.getOutputFlashMap(request).isEmpty());
    }

    // SPR-15176
    @Test
    public void flashAttributesWithResponseEntity() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.RedirectAttributesController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/messages-response-entity");
        MockHttpServletResponse response = new MockHttpServletResponse();
        HttpSession session = request.getSession();
        getServlet().service(request, response);
        Assert.assertEquals(302, response.getStatus());
        Assert.assertEquals("/messages/1?name=value", response.getRedirectedUrl());
        Assert.assertEquals("yay!", RequestContextUtils.getOutputFlashMap(request).get("successMessage"));
        // GET after POST
        request = new MockHttpServletRequest("GET", "/messages/1");
        request.setQueryString("name=value");
        request.setSession(session);
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("Got: yay!", response.getContentAsString());
        Assert.assertTrue(RequestContextUtils.getOutputFlashMap(request).isEmpty());
    }

    @Test
    public void prototypeController() throws Exception {
        initServlet(( wac) -> {
            RootBeanDefinition beanDef = new RootBeanDefinition(.class);
            beanDef.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            wac.registerBeanDefinition("controller", beanDef);
        });
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        request.addParameter("param", "1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("count:3", response.getContentAsString());
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("count:3", response.getContentAsString());
    }

    @Test
    public void restController() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ThisWillActuallyRun.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("Hello World!", response.getContentAsString());
    }

    @Test
    public void responseAsHttpHeaders() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.HttpHeadersResponseController.class);
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(new MockHttpServletRequest("POST", "/"), response);
        Assert.assertEquals("Wrong status code", SC_CREATED, response.getStatus());
        Assert.assertEquals("Wrong number of headers", 1, response.getHeaderNames().size());
        Assert.assertEquals("Wrong value for 'location' header", "/test/items/123", response.getHeader("location"));
        Assert.assertEquals("Expected an empty content", 0, response.getContentLength());
    }

    @Test
    public void responseAsHttpHeadersNoHeader() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.HttpHeadersResponseController.class);
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(new MockHttpServletRequest("POST", "/empty"), response);
        Assert.assertEquals("Wrong status code", SC_CREATED, response.getStatus());
        Assert.assertEquals("Wrong number of headers", 0, response.getHeaderNames().size());
        Assert.assertEquals("Expected an empty content", 0, response.getContentLength());
    }

    @Test
    public void responseBodyAsHtml() throws Exception {
        initServlet(( wac) -> {
            ContentNegotiationManagerFactoryBean factoryBean = new ContentNegotiationManagerFactoryBean();
            factoryBean.afterPropertiesSet();
            RootBeanDefinition adapterDef = new RootBeanDefinition(.class);
            adapterDef.getPropertyValues().add("contentNegotiationManager", factoryBean.getObject());
            wac.registerBeanDefinition("handlerAdapter", adapterDef);
        }, ServletAnnotationControllerHandlerMethodTests.TextRestController.class);
        byte[] content = "alert('boo')".getBytes(StandardCharsets.ISO_8859_1);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/a1.html");
        request.setContent(content);
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("text/html;charset=ISO-8859-1", response.getContentType());
        Assert.assertEquals("inline;filename=f.txt", response.getHeader("Content-Disposition"));
        Assert.assertArrayEquals(content, response.getContentAsByteArray());
    }

    @Test
    public void responseBodyAsHtmlWithSuffixPresent() throws Exception {
        initServlet(( wac) -> {
            ContentNegotiationManagerFactoryBean factoryBean = new ContentNegotiationManagerFactoryBean();
            factoryBean.afterPropertiesSet();
            RootBeanDefinition adapterDef = new RootBeanDefinition(.class);
            adapterDef.getPropertyValues().add("contentNegotiationManager", factoryBean.getObject());
            wac.registerBeanDefinition("handlerAdapter", adapterDef);
        }, ServletAnnotationControllerHandlerMethodTests.TextRestController.class);
        byte[] content = "alert('boo')".getBytes(StandardCharsets.ISO_8859_1);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/a2.html");
        request.setContent(content);
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("text/html;charset=ISO-8859-1", response.getContentType());
        Assert.assertNull(response.getHeader("Content-Disposition"));
        Assert.assertArrayEquals(content, response.getContentAsByteArray());
    }

    @Test
    public void responseBodyAsHtmlWithProducesCondition() throws Exception {
        initServlet(( wac) -> {
            ContentNegotiationManagerFactoryBean factoryBean = new ContentNegotiationManagerFactoryBean();
            factoryBean.afterPropertiesSet();
            RootBeanDefinition adapterDef = new RootBeanDefinition(.class);
            adapterDef.getPropertyValues().add("contentNegotiationManager", factoryBean.getObject());
            wac.registerBeanDefinition("handlerAdapter", adapterDef);
        }, ServletAnnotationControllerHandlerMethodTests.TextRestController.class);
        byte[] content = "alert('boo')".getBytes(StandardCharsets.ISO_8859_1);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/a3.html");
        request.setContent(content);
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("text/html;charset=ISO-8859-1", response.getContentType());
        Assert.assertNull(response.getHeader("Content-Disposition"));
        Assert.assertArrayEquals(content, response.getContentAsByteArray());
    }

    @Test
    public void responseBodyAsTextWithCssExtension() throws Exception {
        initServlet(( wac) -> {
            ContentNegotiationManagerFactoryBean factoryBean = new ContentNegotiationManagerFactoryBean();
            factoryBean.afterPropertiesSet();
            RootBeanDefinition adapterDef = new RootBeanDefinition(.class);
            adapterDef.getPropertyValues().add("contentNegotiationManager", factoryBean.getObject());
            wac.registerBeanDefinition("handlerAdapter", adapterDef);
        }, ServletAnnotationControllerHandlerMethodTests.TextRestController.class);
        byte[] content = "body".getBytes(StandardCharsets.ISO_8859_1);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/a4.css");
        request.setContent(content);
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("text/css;charset=ISO-8859-1", response.getContentType());
        Assert.assertNull(response.getHeader("Content-Disposition"));
        Assert.assertArrayEquals(content, response.getContentAsByteArray());
    }

    @Test
    public void modelAndViewWithStatus() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ModelAndViewController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/path");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(422, response.getStatus());
        Assert.assertEquals("view", response.getForwardedUrl());
    }

    // SPR-14796
    @Test
    public void modelAndViewWithStatusInExceptionHandler() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ModelAndViewController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/exception");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(422, response.getStatus());
        Assert.assertEquals("view", response.getForwardedUrl());
    }

    @Test
    public void httpHead() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ResponseEntityController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("HEAD", "/baz");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("MyValue", response.getHeader("MyResponseHeader"));
        Assert.assertEquals(4, response.getContentLength());
        Assert.assertTrue(((response.getContentAsByteArray().length) == 0));
        // Now repeat with GET
        request = new MockHttpServletRequest("GET", "/baz");
        response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("MyValue", response.getHeader("MyResponseHeader"));
        Assert.assertEquals(4, response.getContentLength());
        Assert.assertEquals("body", response.getContentAsString());
    }

    @Test
    public void httpHeadExplicit() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ResponseEntityController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("HEAD", "/stores");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("v1", response.getHeader("h1"));
    }

    @Test
    public void httpOptions() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ResponseEntityController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/baz");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("GET,HEAD,OPTIONS", response.getHeader("Allow"));
        Assert.assertTrue(((response.getContentAsByteArray().length) == 0));
    }

    @Test
    public void dataClassBinding() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.DataClassController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bind");
        request.addParameter("param1", "value1");
        request.addParameter("param2", "true");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("value1-true-0", response.getContentAsString());
    }

    @Test
    public void dataClassBindingWithAdditionalSetter() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.DataClassController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bind");
        request.addParameter("param1", "value1");
        request.addParameter("param2", "true");
        request.addParameter("param3", "3");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("value1-true-3", response.getContentAsString());
    }

    @Test
    public void dataClassBindingWithResult() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ValidatedDataClassController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bind");
        request.addParameter("param1", "value1");
        request.addParameter("param2", "true");
        request.addParameter("param3", "3");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("value1-true-3", response.getContentAsString());
    }

    @Test
    public void dataClassBindingWithOptionalParameter() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ValidatedDataClassController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bind");
        request.addParameter("param1", "value1");
        request.addParameter("param2", "true");
        request.addParameter("optionalParam", "8");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("value1-true-8", response.getContentAsString());
    }

    @Test
    public void dataClassBindingWithMissingParameter() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ValidatedDataClassController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bind");
        request.addParameter("param1", "value1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("1:value1-null-null", response.getContentAsString());
    }

    @Test
    public void dataClassBindingWithConversionError() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ValidatedDataClassController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bind");
        request.addParameter("param1", "value1");
        request.addParameter("param2", "x");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("1:value1-x-null", response.getContentAsString());
    }

    @Test
    public void dataClassBindingWithValidationError() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ValidatedDataClassController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bind");
        request.addParameter("param2", "true");
        request.addParameter("param3", "0");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("1:null-true-0", response.getContentAsString());
    }

    @Test
    public void dataClassBindingWithValidationErrorAndConversionError() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.ValidatedDataClassController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bind");
        request.addParameter("param2", "x");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("2:null-x-null", response.getContentAsString());
    }

    @Test
    public void dataClassBindingWithOptional() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.OptionalDataClassController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bind");
        request.addParameter("param1", "value1");
        request.addParameter("param2", "true");
        request.addParameter("param3", "3");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("value1-true-3", response.getContentAsString());
    }

    @Test
    public void dataClassBindingWithOptionalAndConversionError() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.OptionalDataClassController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bind");
        request.addParameter("param1", "value1");
        request.addParameter("param2", "x");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("value1-x-null", response.getContentAsString());
    }

    @Test
    public void dataClassBindingWithFieldMarker() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.DataClassController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bind");
        request.addParameter("param1", "value1");
        request.addParameter("param2", "true");
        request.addParameter("_param2", "on");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("value1-true-0", response.getContentAsString());
    }

    @Test
    public void dataClassBindingWithFieldMarkerFallback() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.DataClassController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bind");
        request.addParameter("param1", "value1");
        request.addParameter("_param2", "on");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("value1-false-0", response.getContentAsString());
    }

    @Test
    public void dataClassBindingWithFieldDefault() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.DataClassController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bind");
        request.addParameter("param1", "value1");
        request.addParameter("param2", "true");
        request.addParameter("!param2", "false");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("value1-true-0", response.getContentAsString());
    }

    @Test
    public void dataClassBindingWithFieldDefaultFallback() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.DataClassController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bind");
        request.addParameter("param1", "value1");
        request.addParameter("!param2", "false");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("value1-false-0", response.getContentAsString());
    }

    @Test
    public void dataClassBindingWithLocalDate() throws Exception {
        initServletWithControllers(ServletAnnotationControllerHandlerMethodTests.DateClassController.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/bind");
        request.addParameter("date", "2010-01-01");
        MockHttpServletResponse response = new MockHttpServletResponse();
        getServlet().service(request, response);
        Assert.assertEquals("2010-01-01", response.getContentAsString());
    }

    @Controller
    static class ControllerWithEmptyValueMapping {
        @RequestMapping("")
        public void myPath2(HttpServletResponse response) throws IOException {
            throw new IllegalStateException("test");
        }

        @RequestMapping("/bar")
        public void myPath3(HttpServletResponse response) throws IOException {
            response.getWriter().write("testX");
        }

        @ExceptionHandler
        public void myPath2(Exception ex, HttpServletResponse response) throws IOException {
            response.getWriter().write(ex.getMessage());
        }
    }

    @Controller
    private static class ControllerWithErrorThrown {
        @RequestMapping("")
        public void myPath2(HttpServletResponse response) throws IOException {
            throw new AssertionError("test");
        }

        @RequestMapping("/bar")
        public void myPath3(HttpServletResponse response) throws IOException {
            response.getWriter().write("testX");
        }

        @ExceptionHandler
        public void myPath2(Error err, HttpServletResponse response) throws IOException {
            response.getWriter().write(err.getMessage());
        }
    }

    @Controller
    static class MyAdaptedController {
        @RequestMapping("/myPath1.do")
        public void myHandle(HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.getWriter().write("test");
        }

        @RequestMapping("/myPath2.do")
        public void myHandle(@RequestParam("param1")
        String p1, @RequestParam("param2")
        int p2, @RequestHeader("header1")
        long h1, @CookieValue(name = "cookie1")
        Cookie c1, HttpServletResponse response) throws IOException {
            response.getWriter().write(((((((("test-" + p1) + "-") + p2) + "-") + h1) + "-") + (c1.getValue())));
        }

        @RequestMapping("/myPath3")
        public void myHandle(TestBean tb, HttpServletResponse response) throws IOException {
            response.getWriter().write(((("test-" + (tb.getName())) + "-") + (tb.getAge())));
        }

        @RequestMapping("/myPath4.do")
        public void myHandle(TestBean tb, Errors errors, HttpServletResponse response) throws IOException {
            response.getWriter().write(((("test-" + (tb.getName())) + "-") + (errors.getFieldError("age").getCode())));
        }
    }

    @Controller
    @RequestMapping("/*.do")
    static class MyAdaptedController2 {
        @RequestMapping
        public void myHandle(HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.getWriter().write("test");
        }

        @RequestMapping("/myPath2.do")
        public void myHandle(@RequestParam("param1")
        String p1, int param2, HttpServletResponse response, @RequestHeader("header1")
        String h1, @CookieValue("cookie1")
        String c1) throws IOException {
            response.getWriter().write(((((((("test-" + p1) + "-") + param2) + "-") + h1) + "-") + c1));
        }

        @RequestMapping("/myPath3")
        public void myHandle(TestBean tb, HttpServletResponse response) throws IOException {
            response.getWriter().write(((("test-" + (tb.getName())) + "-") + (tb.getAge())));
        }

        @RequestMapping("/myPath4.*")
        public void myHandle(TestBean tb, Errors errors, HttpServletResponse response) throws IOException {
            response.getWriter().write(((("test-" + (tb.getName())) + "-") + (errors.getFieldError("age").getCode())));
        }
    }

    @Controller
    static class MyAdaptedControllerBase<T> {
        @RequestMapping("/myPath2.do")
        public void myHandle(@RequestParam("param1")
        T p1, int param2, @RequestHeader
        Integer header1, @CookieValue
        int cookie1, HttpServletResponse response) throws IOException {
            response.getWriter().write(((((((("test-" + p1) + "-") + param2) + "-") + header1) + "-") + cookie1));
        }

        @InitBinder
        public void initBinder(@RequestParam("param1")
        String p1, @RequestParam(value = "paramX", required = false)
        String px, int param2) {
            Assert.assertNull(px);
        }

        @ModelAttribute
        public void modelAttribute(@RequestParam("param1")
        String p1, @RequestParam(value = "paramX", required = false)
        String px, int param2) {
            Assert.assertNull(px);
        }
    }

    @RequestMapping("/*.do")
    static class MyAdaptedController3 extends ServletAnnotationControllerHandlerMethodTests.MyAdaptedControllerBase<String> {
        @RequestMapping
        public void myHandle(HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.getWriter().write("test");
        }

        @Override
        public void myHandle(@RequestParam("param1")
        String p1, int param2, @RequestHeader
        Integer header1, @CookieValue
        int cookie1, HttpServletResponse response) throws IOException {
            response.getWriter().write(((((((("test-" + p1) + "-") + param2) + "-") + header1) + "-") + cookie1));
        }

        @RequestMapping("/myPath3")
        public void myHandle(TestBean tb, HttpServletResponse response) throws IOException {
            response.getWriter().write(((("test-" + (tb.getName())) + "-") + (tb.getAge())));
        }

        @RequestMapping("/myPath4.*")
        public void myHandle(TestBean tb, Errors errors, HttpServletResponse response) throws IOException {
            response.getWriter().write(((("test-" + (tb.getName())) + "-") + (errors.getFieldError("age").getCode())));
        }

        @Override
        @InitBinder
        public void initBinder(@RequestParam("param1")
        String p1, @RequestParam(value = "paramX", required = false)
        String px, int param2) {
            Assert.assertNull(px);
        }

        @Override
        @ModelAttribute
        public void modelAttribute(@RequestParam("param1")
        String p1, @RequestParam(value = "paramX", required = false)
        String px, int param2) {
            Assert.assertNull(px);
        }
    }

    @Controller
    @RequestMapping(method = RequestMethod.GET)
    static class EmptyParameterListHandlerMethodController {
        static boolean called;

        @RequestMapping("/emptyParameterListHandler")
        public void emptyParameterListHandler() {
            ServletAnnotationControllerHandlerMethodTests.EmptyParameterListHandlerMethodController.called = true;
        }

        @RequestMapping("/nonEmptyParameterListHandler")
        public void nonEmptyParameterListHandler(HttpServletResponse response) {
        }
    }

    @Controller
    @RequestMapping("/myPage")
    @SessionAttributes(names = { "object1", "object2" })
    public static class MySessionAttributesController {
        @RequestMapping(method = RequestMethod.GET)
        public String get(Model model) {
            model.addAttribute("object1", new Object());
            model.addAttribute("object2", new Object());
            return "page1";
        }

        @RequestMapping(method = RequestMethod.POST)
        public String post(@ModelAttribute("object1")
        Object object1) {
            // do something with object1
            return "page2";
        }
    }

    @RequestMapping("/myPage")
    @SessionAttributes({ "object1", "object2" })
    @Controller
    public interface MySessionAttributesControllerIfc {
        @RequestMapping(method = RequestMethod.GET)
        String get(Model model);

        @RequestMapping(method = RequestMethod.POST)
        String post(@ModelAttribute("object1")
        Object object1);
    }

    public static class MySessionAttributesControllerImpl implements ServletAnnotationControllerHandlerMethodTests.MySessionAttributesControllerIfc {
        @Override
        public String get(Model model) {
            model.addAttribute("object1", new Object());
            model.addAttribute("object2", new Object());
            return "page1";
        }

        @Override
        public String post(@ModelAttribute("object1")
        Object object1) {
            // do something with object1
            return "page2";
        }
    }

    @RequestMapping("/myPage")
    @SessionAttributes({ "object1", "object2" })
    public interface MyParameterizedControllerIfc<T> {
        @ModelAttribute("testBeanList")
        java.util.List<TestBean> getTestBeans();

        @RequestMapping(method = RequestMethod.GET)
        String get(Model model);
    }

    public interface MyEditableParameterizedControllerIfc<T> extends ServletAnnotationControllerHandlerMethodTests.MyParameterizedControllerIfc<T> {
        @RequestMapping(method = RequestMethod.POST)
        String post(@ModelAttribute("object1")
        T object);
    }

    @Controller
    public static class MyParameterizedControllerImpl implements ServletAnnotationControllerHandlerMethodTests.MyEditableParameterizedControllerIfc<TestBean> {
        @Override
        public java.util.List<TestBean> getTestBeans() {
            java.util.List<TestBean> list = new LinkedList<>();
            list.add(new TestBean("tb1"));
            list.add(new TestBean("tb2"));
            return list;
        }

        @Override
        public String get(Model model) {
            model.addAttribute("object1", new TestBean());
            model.addAttribute("object2", new TestBean());
            return "page1";
        }

        @Override
        public String post(TestBean object) {
            // do something with object1
            return "page2";
        }
    }

    @Controller
    public static class MyParameterizedControllerImplWithOverriddenMappings implements ServletAnnotationControllerHandlerMethodTests.MyEditableParameterizedControllerIfc<TestBean> {
        @Override
        @ModelAttribute("testBeanList")
        public java.util.List<TestBean> getTestBeans() {
            java.util.List<TestBean> list = new LinkedList<>();
            list.add(new TestBean("tb1"));
            list.add(new TestBean("tb2"));
            return list;
        }

        @Override
        @RequestMapping(method = RequestMethod.GET)
        public String get(Model model) {
            model.addAttribute("object1", new TestBean());
            model.addAttribute("object2", new TestBean());
            return "page1";
        }

        @Override
        @RequestMapping(method = RequestMethod.POST)
        public String post(@ModelAttribute("object1")
        TestBean object1) {
            // do something with object1
            return "page2";
        }
    }

    @Controller
    public static class MyFormController {
        @ModelAttribute("testBeanList")
        public java.util.List<TestBean> getTestBeans() {
            java.util.List<TestBean> list = new LinkedList<>();
            list.add(new TestBean("tb1"));
            list.add(new TestBean("tb2"));
            return list;
        }

        @RequestMapping("/myPath.do")
        public String myHandle(@ModelAttribute("myCommand")
        TestBean tb, BindingResult errors, ModelMap model) {
            FieldError error = errors.getFieldError("age");
            Assert.assertNotNull("Must have field error for age property", error);
            Assert.assertEquals("value2", error.getRejectedValue());
            if (!(model.containsKey("myKey"))) {
                model.addAttribute("myKey", "myValue");
            }
            return "myView";
        }
    }

    public static class ValidTestBean extends TestBean {
        @NotNull
        private String validCountry;

        public void setValidCountry(String validCountry) {
            this.validCountry = validCountry;
        }

        public String getValidCountry() {
            return this.validCountry;
        }
    }

    @Controller
    public static class MyModelFormController {
        @ModelAttribute
        public java.util.List<TestBean> getTestBeans() {
            java.util.List<TestBean> list = new LinkedList<>();
            list.add(new TestBean("tb1"));
            list.add(new TestBean("tb2"));
            return list;
        }

        @RequestMapping("/myPath.do")
        @ModelAttribute("yourKey")
        public String myHandle(@ModelAttribute("myCommand")
        TestBean tb, BindingResult errors, Model model) {
            if (!(model.containsAttribute("myKey"))) {
                model.addAttribute("myKey", "myValue");
            }
            return "yourValue";
        }
    }

    @Controller
    public static class LateBindingFormController {
        @ModelAttribute("testBeanList")
        public java.util.List<TestBean> getTestBeans(@ModelAttribute(name = "myCommand", binding = false)
        TestBean tb) {
            java.util.List<TestBean> list = new LinkedList<>();
            list.add(new TestBean("tb1"));
            list.add(new TestBean("tb2"));
            return list;
        }

        @RequestMapping("/myPath.do")
        public String myHandle(@ModelAttribute(name = "myCommand", binding = true)
        TestBean tb, BindingResult errors, ModelMap model) {
            FieldError error = errors.getFieldError("age");
            Assert.assertNotNull("Must have field error for age property", error);
            Assert.assertEquals("value2", error.getRejectedValue());
            if (!(model.containsKey("myKey"))) {
                model.addAttribute("myKey", "myValue");
            }
            return "myView";
        }
    }

    @Controller
    static class MyCommandProvidingFormController<T, TB, TB2> extends ServletAnnotationControllerHandlerMethodTests.MyFormController {
        @ModelAttribute("myCommand")
        public ServletAnnotationControllerHandlerMethodTests.ValidTestBean createTestBean(@RequestParam
        T defaultName, Map<String, Object> model, @RequestParam
        Date date) {
            model.put("myKey", "myOriginalValue");
            ServletAnnotationControllerHandlerMethodTests.ValidTestBean tb = new ServletAnnotationControllerHandlerMethodTests.ValidTestBean();
            setName((((defaultName.getClass().getSimpleName()) + ":") + (defaultName.toString())));
            return tb;
        }

        @Override
        @RequestMapping("/myPath.do")
        public String myHandle(@ModelAttribute("myCommand")
        @Valid
        TestBean tb, BindingResult errors, ModelMap model) {
            if (!(errors.hasFieldErrors("validCountry"))) {
                throw new IllegalStateException("Declarative validation not applied");
            }
            return super.myHandle(tb, errors, model);
        }

        @RequestMapping("/myOtherPath.do")
        public String myOtherHandle(TB tb, BindingResult errors, ExtendedModelMap model, ServletAnnotationControllerHandlerMethodTests.MySpecialArg arg) {
            TestBean tbReal = ((TestBean) (tb));
            tbReal.setName("myName");
            Assert.assertTrue(((model.get("ITestBean")) instanceof DerivedTestBean));
            Assert.assertNotNull(arg);
            return super.myHandle(tbReal, errors, model);
        }

        @RequestMapping("/myThirdPath.do")
        public String myThirdHandle(TB tb, Model model) {
            model.addAttribute("testBean", new TestBean("special", 99));
            return "myView";
        }

        @SuppressWarnings("unchecked")
        @ModelAttribute
        protected TB2 getModelAttr() {
            return ((TB2) (new DerivedTestBean()));
        }
    }

    static class MySpecialArg {
        public MySpecialArg(String value) {
        }
    }

    @Controller
    static class MyTypedCommandProvidingFormController extends ServletAnnotationControllerHandlerMethodTests.MyCommandProvidingFormController<Integer, TestBean, ITestBean> {}

    @Controller
    static class MyBinderInitializingCommandProvidingFormController extends ServletAnnotationControllerHandlerMethodTests.MyCommandProvidingFormController<String, TestBean, ITestBean> {
        @InitBinder
        public void initBinder(WebDataBinder binder) {
            binder.initBeanPropertyAccess();
            binder.setRequiredFields("sex");
            LocalValidatorFactoryBean vf = new LocalValidatorFactoryBean();
            vf.afterPropertiesSet();
            binder.setValidator(vf);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
        }

        @Override
        @RequestMapping("/myPath.do")
        public String myHandle(@ModelAttribute("myCommand")
        @Valid
        TestBean tb, BindingResult errors, ModelMap model) {
            if (!(errors.hasFieldErrors("sex"))) {
                throw new IllegalStateException("requiredFields not applied");
            }
            return super.myHandle(tb, errors, model);
        }
    }

    @Controller
    static class MySpecificBinderInitializingCommandProvidingFormController extends ServletAnnotationControllerHandlerMethodTests.MyCommandProvidingFormController<String, TestBean, ITestBean> {
        @InitBinder({ "myCommand", "date" })
        public void initBinder(WebDataBinder binder, String date, @RequestParam("date")
        String[] date2) {
            LocalValidatorFactoryBean vf = new LocalValidatorFactoryBean();
            vf.afterPropertiesSet();
            binder.setValidator(vf);
            Assert.assertEquals("2007-10-02", date);
            Assert.assertEquals(1, date2.length);
            Assert.assertEquals("2007-10-02", date2[0]);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
        }
    }

    static class MyWebBindingInitializer implements WebBindingInitializer {
        @Override
        public void initBinder(WebDataBinder binder) {
            LocalValidatorFactoryBean vf = new LocalValidatorFactoryBean();
            vf.afterPropertiesSet();
            binder.setValidator(vf);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
        }
    }

    static class MySpecialArgumentResolver implements WebArgumentResolver {
        @Override
        public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) {
            if (methodParameter.getParameterType().equals(ServletAnnotationControllerHandlerMethodTests.MySpecialArg.class)) {
                return new ServletAnnotationControllerHandlerMethodTests.MySpecialArg("myValue");
            }
            return UNRESOLVED;
        }
    }

    @Controller
    @RequestMapping("/myPath.do")
    static class MyParameterDispatchingController implements Serializable {
        private static final long serialVersionUID = 1L;

        @Autowired
        private transient ServletContext servletContext;

        @Autowired
        private transient ServletConfig servletConfig;

        @Autowired
        private HttpSession session;

        @Autowired
        private HttpServletRequest request;

        @Autowired
        private WebRequest webRequest;

        @RequestMapping
        public void myHandle(HttpServletResponse response, HttpServletRequest request) throws IOException {
            if ((((((this.servletContext) == null) || ((this.servletConfig) == null)) || ((this.session) == null)) || ((this.request) == null)) || ((this.webRequest) == null)) {
                throw new IllegalStateException();
            }
            response.getWriter().write("myView");
            request.setAttribute("servletContext", this.servletContext);
            request.setAttribute("servletConfig", this.servletConfig);
            request.setAttribute("sessionId", this.session.getId());
            request.setAttribute("requestUri", this.request.getRequestURI());
            request.setAttribute("locale", this.webRequest.getLocale());
        }

        @RequestMapping(params = { "view", "!lang" })
        public void myOtherHandle(HttpServletResponse response) throws IOException {
            response.getWriter().write("myOtherView");
        }

        @RequestMapping(method = RequestMethod.GET, params = { "view=my", "lang=de" })
        public void myLangHandle(HttpServletResponse response) throws IOException {
            response.getWriter().write("myLangView");
        }

        @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, params = "surprise")
        public void mySurpriseHandle(HttpServletResponse response) throws IOException {
            response.getWriter().write("mySurpriseView");
        }
    }

    @Controller
    @RequestMapping(value = "/myPath.do", params = { "active" })
    static class MyConstrainedParameterDispatchingController {
        @RequestMapping(params = { "view", "!lang" })
        public void myOtherHandle(HttpServletResponse response) throws IOException {
            response.getWriter().write("myOtherView");
        }

        @RequestMapping(method = RequestMethod.GET, params = { "view=my", "lang=de" })
        public void myLangHandle(HttpServletResponse response) throws IOException {
            response.getWriter().write("myLangView");
        }
    }

    @Controller
    @RequestMapping("/myApp/*")
    static class MyRelativePathDispatchingController {
        @RequestMapping
        public void myHandle(HttpServletResponse response) throws IOException {
            response.getWriter().write("myView");
        }

        @RequestMapping("*Other")
        public void myOtherHandle(HttpServletResponse response) throws IOException {
            response.getWriter().write("myOtherView");
        }

        @RequestMapping("myLang")
        public void myLangHandle(HttpServletResponse response) throws IOException {
            response.getWriter().write("myLangView");
        }

        @RequestMapping("surprise")
        public void mySurpriseHandle(HttpServletResponse response) throws IOException {
            response.getWriter().write("mySurpriseView");
        }
    }

    @Controller
    static class MyRelativeMethodPathDispatchingController {
        // was **/myHandle
        @RequestMapping("*/myHandle")
        public void myHandle(HttpServletResponse response) throws IOException {
            response.getWriter().write("myView");
        }

        // was /**/*Other
        @RequestMapping("/*/*Other")
        public void myOtherHandle(HttpServletResponse response) throws IOException {
            response.getWriter().write("myOtherView");
        }

        // was **/myLang
        @RequestMapping("*/myLang")
        public void myLangHandle(HttpServletResponse response) throws IOException {
            response.getWriter().write("myLangView");
        }

        // was /**/surprise
        @RequestMapping("/*/surprise")
        public void mySurpriseHandle(HttpServletResponse response) throws IOException {
            response.getWriter().write("mySurpriseView");
        }
    }

    @Controller
    static class MyNullCommandController {
        @ModelAttribute
        public TestBean getTestBean() {
            return null;
        }

        @ModelAttribute
        public Principal getPrincipal() {
            return new ServletAnnotationControllerHandlerMethodTests.TestPrincipal();
        }

        @RequestMapping("/myPath")
        public void handle(@ModelAttribute
        TestBean testBean, Errors errors, @ModelAttribute
        ServletAnnotationControllerHandlerMethodTests.TestPrincipal modelPrinc, ServletAnnotationControllerHandlerMethodTests.OtherPrincipal requestPrinc, Writer writer) throws IOException {
            Assert.assertNull(testBean);
            Assert.assertNotNull(modelPrinc);
            Assert.assertNotNull(requestPrinc);
            Assert.assertFalse(errors.hasErrors());
            errors.reject("myCode");
            writer.write("myView");
        }
    }

    static class TestPrincipal implements Principal {
        @Override
        public String getName() {
            return "test";
        }
    }

    static class OtherPrincipal implements Principal {
        @Override
        public String getName() {
            return "other";
        }
    }

    static class TestViewResolver implements ViewResolver {
        @Override
        public View resolveViewName(final String viewName, Locale locale) throws Exception {
            return new View() {
                @Override
                public String getContentType() {
                    return null;
                }

                @Override
                @SuppressWarnings({ "unchecked", "deprecation", "rawtypes" })
                public void render(@Nullable
                Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
                    TestBean tb = ((TestBean) (model.get("testBean")));
                    if (tb == null) {
                        tb = ((TestBean) (model.get("myCommand")));
                    }
                    if (((tb.getName()) != null) && (tb.getName().endsWith("myDefaultName"))) {
                        Assert.assertEquals(107, tb.getDate().getYear());
                    }
                    Errors errors = ((Errors) (model.get(((BindingResult.MODEL_KEY_PREFIX) + "testBean"))));
                    if (errors == null) {
                        errors = ((Errors) (model.get(((BindingResult.MODEL_KEY_PREFIX) + "myCommand"))));
                    }
                    if (errors.hasFieldErrors("date")) {
                        throw new IllegalStateException();
                    }
                    if (model.containsKey("ITestBean")) {
                        Assert.assertTrue(((model.get(((BindingResult.MODEL_KEY_PREFIX) + "ITestBean"))) instanceof Errors));
                    }
                    java.util.List<TestBean> testBeans = ((java.util.List<TestBean>) (model.get("testBeanList")));
                    if (errors.hasFieldErrors("age")) {
                        response.getWriter().write((((((((((viewName + "-") + (tb.getName())) + "-") + (errors.getFieldError("age").getCode())) + "-") + (testBeans.get(0).getName())) + "-") + (model.get("myKey"))) + (model.containsKey("yourKey") ? "-" + (model.get("yourKey")) : "")));
                    } else {
                        response.getWriter().write(((((((((viewName + "-") + (tb.getName())) + "-") + (tb.getAge())) + "-") + (errors.getFieldValue("name"))) + "-") + (errors.getFieldValue("age"))));
                    }
                }
            };
        }
    }

    public static class ModelExposingViewResolver implements ViewResolver {
        @Override
        public View resolveViewName(final String viewName, Locale locale) throws Exception {
            return new View() {
                @Override
                public String getContentType() {
                    return null;
                }

                @Override
                public void render(@Nullable
                Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) {
                    request.setAttribute("viewName", viewName);
                    request.getSession().setAttribute("model", model);
                }
            };
        }
    }

    public static class ParentController {
        @RequestMapping(method = RequestMethod.GET)
        public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        }
    }

    @Controller
    @RequestMapping("/child/test")
    public static class ChildController extends ServletAnnotationControllerHandlerMethodTests.ParentController {
        @RequestMapping(method = RequestMethod.GET)
        public void doGet(HttpServletRequest req, HttpServletResponse resp, @RequestParam("childId")
        String id) {
        }
    }

    @Target({ ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Controller
    public @interface MyControllerAnnotation {}

    @ServletAnnotationControllerHandlerMethodTests.MyControllerAnnotation
    public static class CustomAnnotationController {
        @RequestMapping("/myPath.do")
        public void myHandle() {
        }
    }

    @Controller
    public static class RequiredParamController {
        @RequestMapping("/myPath.do")
        public void myHandle(@RequestParam(value = "id", required = true)
        int id, @RequestHeader(value = "header", required = true)
        String header) {
        }
    }

    @Controller
    public static class OptionalParamController {
        @RequestMapping("/myPath.do")
        public void myHandle(@RequestParam(required = false)
        String id, @RequestParam(required = false)
        boolean flag, @RequestHeader(value = "header", required = false)
        String header, HttpServletResponse response) throws IOException {
            response.getWriter().write((((((String.valueOf(id)) + "-") + flag) + "-") + (String.valueOf(header))));
        }
    }

    @Controller
    public static class DefaultValueParamController {
        @RequestMapping("/myPath.do")
        public void myHandle(@RequestParam(value = "id", defaultValue = "foo")
        String id, @RequestParam(value = "otherId", defaultValue = "")
        String id2, @RequestHeader(defaultValue = "bar")
        String header, HttpServletResponse response) throws IOException {
            response.getWriter().write((((((String.valueOf(id)) + "-") + (String.valueOf(id2))) + "-") + (String.valueOf(header))));
        }
    }

    @Controller
    public static class DefaultExpressionValueParamController {
        @RequestMapping("/myPath.do")
        public void myHandle(@RequestParam(value = "id", defaultValue = "${myKey}")
        String id, @RequestHeader(defaultValue = "#{systemProperties.myHeader}")
        String header, @Value("#{request.contextPath}")
        String contextPath, HttpServletResponse response) throws IOException {
            response.getWriter().write((((((String.valueOf(id)) + "-") + (String.valueOf(header))) + "-") + contextPath));
        }
    }

    @Controller
    public static class NestedSetController {
        @RequestMapping("/myPath.do")
        public void myHandle(GenericBean<?> gb, HttpServletResponse response) throws Exception {
            response.getWriter().write((((gb.getTestBeanSet().toString()) + "-") + (gb.getTestBeanSet().iterator().next().getClass().getName())));
        }
    }

    public static class TestBeanConverter implements Converter<String, ITestBean> {
        @Override
        public ITestBean convert(String source) {
            return new TestBean(source);
        }
    }

    @Controller
    public static class PathVariableWithCustomConverterController {
        @RequestMapping("/myPath/{id}")
        public void myHandle(@PathVariable("id")
        ITestBean bean) throws Exception {
        }
    }

    public static class AnnotatedExceptionRaisingConverter implements Converter<String, ITestBean> {
        @Override
        public ITestBean convert(String source) {
            throw new ServletAnnotationControllerHandlerMethodTests.AnnotatedExceptionRaisingConverter.NotFoundException();
        }

        @ResponseStatus(HttpStatus.NOT_FOUND)
        @SuppressWarnings("serial")
        private static class NotFoundException extends RuntimeException {}
    }

    @Controller
    public static class MethodNotAllowedController {
        @RequestMapping(value = "/myPath.do", method = RequestMethod.DELETE)
        public void delete() {
        }

        @RequestMapping(value = "/myPath.do", method = RequestMethod.HEAD)
        public void head() {
        }

        @RequestMapping(value = "/myPath.do", method = RequestMethod.OPTIONS)
        public void options() {
        }

        @RequestMapping(value = "/myPath.do", method = RequestMethod.POST)
        public void post() {
        }

        @RequestMapping(value = "/myPath.do", method = RequestMethod.PUT)
        public void put() {
        }

        @RequestMapping(value = "/myPath.do", method = RequestMethod.TRACE)
        public void trace() {
        }

        @RequestMapping(value = "/otherPath.do", method = RequestMethod.GET)
        public void get() {
        }
    }

    @Controller
    public static class PathOrderingController {
        @RequestMapping({ "/dir/myPath1.do", "/*/*.do" })
        public void method1(Writer writer) throws IOException {
            writer.write("method1");
        }

        @RequestMapping("/dir/*.do")
        public void method2(Writer writer) throws IOException {
            writer.write("method2");
        }
    }

    @Controller
    public static class RequestResponseBodyController {
        @RequestMapping(value = "/something", method = RequestMethod.PUT)
        @ResponseBody
        public String handle(@RequestBody
        String body) throws IOException {
            return body;
        }

        @RequestMapping(value = "/something", method = RequestMethod.PATCH)
        @ResponseBody
        public String handlePartialUpdate(@RequestBody
        String content) throws IOException {
            return content;
        }
    }

    @Controller
    public static class RequestResponseBodyProducesController {
        @RequestMapping(value = "/something", method = RequestMethod.PUT, produces = "text/plain")
        @ResponseBody
        public String handle(@RequestBody
        String body) throws IOException {
            return body;
        }
    }

    @Controller
    public static class ResponseBodyVoidController {
        @RequestMapping("/something")
        @ResponseBody
        public void handle() throws IOException {
        }
    }

    @Controller
    public static class RequestBodyArgMismatchController {
        @RequestMapping(value = "/something", method = RequestMethod.PUT)
        public void handle(@RequestBody
        ServletAnnotationControllerHandlerMethodTests.A a) throws IOException {
        }
    }

    @XmlRootElement
    public static class A {}

    @XmlRootElement
    public static class B {}

    public static class NotReadableMessageConverter implements org.springframework.http.converter.HttpMessageConverter<Object> {
        @Override
        public boolean canRead(Class<?> clazz, @Nullable
        MediaType mediaType) {
            return true;
        }

        @Override
        public boolean canWrite(Class<?> clazz, @Nullable
        MediaType mediaType) {
            return true;
        }

        @Override
        public java.util.List<MediaType> getSupportedMediaTypes() {
            return Collections.singletonList(new MediaType("application", "pdf"));
        }

        @Override
        public Object read(Class<?> clazz, HttpInputMessage inputMessage) {
            throw new HttpMessageNotReadableException("Could not read", inputMessage);
        }

        @Override
        public void write(Object o, @Nullable
        MediaType contentType, HttpOutputMessage outputMessage) {
            throw new UnsupportedOperationException("Not implemented");
        }
    }

    public static class SimpleMessageConverter implements org.springframework.http.converter.HttpMessageConverter<Object> {
        private final java.util.List<MediaType> supportedMediaTypes;

        public SimpleMessageConverter(MediaType... supportedMediaTypes) {
            this.supportedMediaTypes = Arrays.asList(supportedMediaTypes);
        }

        @Override
        public boolean canRead(Class<?> clazz, @Nullable
        MediaType mediaType) {
            return supportedMediaTypes.contains(mediaType);
        }

        @Override
        public boolean canWrite(Class<?> clazz, @Nullable
        MediaType mediaType) {
            return supportedMediaTypes.contains(mediaType);
        }

        @Override
        public java.util.List<MediaType> getSupportedMediaTypes() {
            return supportedMediaTypes;
        }

        @Override
        public Object read(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
            return null;
        }

        @Override
        public void write(Object o, @Nullable
        MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
            outputMessage.getHeaders().setContentType(contentType);
            outputMessage.getBody();// force a header write

        }
    }

    @Controller
    public static class ContentTypeHeadersController {
        @RequestMapping(value = "/something", headers = "content-type=application/pdf")
        public void handlePdf(Writer writer) throws IOException {
            writer.write("pdf");
        }

        @RequestMapping(value = "/something", headers = "content-type=text/*")
        public void handleHtml(Writer writer) throws IOException {
            writer.write("text");
        }
    }

    @Controller
    public static class ConsumesController {
        @RequestMapping(value = "/something", consumes = "application/pdf")
        public void handlePdf(Writer writer) throws IOException {
            writer.write("pdf");
        }

        @RequestMapping(value = "/something", consumes = "text/*")
        public void handleHtml(Writer writer) throws IOException {
            writer.write("text");
        }
    }

    @Controller
    public static class NegatedContentTypeHeadersController {
        @RequestMapping(value = "/something", headers = "content-type=application/pdf")
        public void handlePdf(Writer writer) throws IOException {
            writer.write("pdf");
        }

        @RequestMapping(value = "/something", headers = "content-type!=application/pdf")
        public void handleNonPdf(Writer writer) throws IOException {
            writer.write("non-pdf");
        }
    }

    @Controller
    public static class AcceptHeadersController {
        @RequestMapping(value = "/something", headers = "accept=text/html")
        public void handleHtml(Writer writer) throws IOException {
            writer.write("html");
        }

        @RequestMapping(value = "/something", headers = "accept=application/xml")
        public void handleXml(Writer writer) throws IOException {
            writer.write("xml");
        }
    }

    @Controller
    public static class ProducesController {
        @GetMapping(path = "/something", produces = "text/html")
        public void handleHtml(Writer writer) throws IOException {
            writer.write("html");
        }

        @GetMapping(path = "/something", produces = "application/xml")
        public void handleXml(Writer writer) throws IOException {
            writer.write("xml");
        }

        @GetMapping(path = "/something", produces = "text/csv")
        public String handleCsv() {
            throw new IllegalArgumentException();
        }

        @ExceptionHandler
        public ResponseEntity<Map<String, String>> handle(IllegalArgumentException ex) {
            return ResponseEntity.status(500).body(Collections.singletonMap("reason", "error"));
        }
    }

    @Controller
    public static class ResponseStatusController {
        @RequestMapping("/something")
        @ResponseStatus(code = HttpStatus.CREATED, reason = "It's alive!")
        public void handle(Writer writer) throws IOException {
            writer.write("something");
        }
    }

    @Controller
    public static class ModelAndViewResolverController {
        @RequestMapping("/")
        public ServletAnnotationControllerHandlerMethodTests.MySpecialArg handle() {
            return new ServletAnnotationControllerHandlerMethodTests.MySpecialArg("foo");
        }
    }

    public static class MyModelAndViewResolver implements ModelAndViewResolver {
        @Override
        public ModelAndView resolveModelAndView(Method handlerMethod, Class<?> handlerType, Object returnValue, ExtendedModelMap implicitModel, NativeWebRequest webRequest) {
            if (returnValue instanceof ServletAnnotationControllerHandlerMethodTests.MySpecialArg) {
                return new ModelAndView(new View() {
                    @Override
                    public String getContentType() {
                        return "text/html";
                    }

                    @Override
                    public void render(@Nullable
                    Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
                        response.getWriter().write("myValue");
                    }
                });
            }
            return UNRESOLVED;
        }
    }

    @Controller
    @RequestMapping("/test*")
    static class AmbiguousParamsController {
        @RequestMapping(method = RequestMethod.GET)
        public void noParams(Writer writer) throws IOException {
            writer.write("noParams");
        }

        @RequestMapping(params = "myParam")
        public void param(@RequestParam("myParam")
        int myParam, Writer writer) throws IOException {
            writer.write(("myParam-" + myParam));
        }
    }

    @Controller
    static class AmbiguousPathAndRequestMethodController {
        @RequestMapping(value = "/bug/EXISTING", method = RequestMethod.POST)
        public void directMatch(Writer writer) throws IOException {
            writer.write("Direct");
        }

        @RequestMapping(value = "/bug/{type}", method = RequestMethod.GET)
        public void patternMatch(Writer writer) throws IOException {
            writer.write("Pattern");
        }
    }

    @Controller
    @RequestMapping("/test*")
    public static class BindingCookieValueController {
        @InitBinder
        public void initBinder(WebDataBinder binder) {
            binder.initBeanPropertyAccess();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
        }

        @RequestMapping(method = RequestMethod.GET)
        public void handle(@CookieValue("date")
        Date date, Writer writer) throws IOException {
            Assert.assertEquals("Invalid path variable value", new GregorianCalendar(2008, 10, 18).getTime(), date);
            writer.write(("test-" + (new SimpleDateFormat("yyyy").format(date))));
        }
    }

    public interface TestController<T> {
        ModelAndView method(T object);
    }

    public static class MyEntity {}

    @Controller
    public static class TestControllerImpl implements ServletAnnotationControllerHandlerMethodTests.TestController<ServletAnnotationControllerHandlerMethodTests.MyEntity> {
        @Override
        @RequestMapping("/method")
        public ModelAndView method(ServletAnnotationControllerHandlerMethodTests.MyEntity object) {
            return new ModelAndView("/something");
        }
    }

    @RestController
    @RequestMapping(path = ServletAnnotationControllerHandlerMethodTests.ApiConstants.ARTICLES_PATH)
    public static class ArticleController implements ServletAnnotationControllerHandlerMethodTests.ApiConstants , ServletAnnotationControllerHandlerMethodTests.ResourceEndpoint<ServletAnnotationControllerHandlerMethodTests.Article, ServletAnnotationControllerHandlerMethodTests.ArticlePredicate> {
        @GetMapping(params = "page")
        public Collection<ServletAnnotationControllerHandlerMethodTests.Article> find(String pageable, ServletAnnotationControllerHandlerMethodTests.ArticlePredicate predicate) {
            throw new UnsupportedOperationException("not implemented");
        }

        @GetMapping
        public java.util.List<ServletAnnotationControllerHandlerMethodTests.Article> find(boolean sort, ServletAnnotationControllerHandlerMethodTests.ArticlePredicate predicate) {
            throw new UnsupportedOperationException("not implemented");
        }
    }

    interface ApiConstants {
        String API_V1 = "/v1";

        String ARTICLES_PATH = (ServletAnnotationControllerHandlerMethodTests.ApiConstants.API_V1) + "/articles";
    }

    public interface ResourceEndpoint<E extends ServletAnnotationControllerHandlerMethodTests.Entity, P extends ServletAnnotationControllerHandlerMethodTests.EntityPredicate<?>> {
        Collection<E> find(String pageable, P predicate) throws IOException;

        java.util.List<E> find(boolean sort, P predicate) throws IOException;
    }

    public abstract static class Entity {
        public UUID id;

        public String createdBy;

        public Instant createdDate;
    }

    public static class Article extends ServletAnnotationControllerHandlerMethodTests.Entity {
        public String slug;

        public String title;

        public String content;
    }

    public abstract static class EntityPredicate<E extends ServletAnnotationControllerHandlerMethodTests.Entity> {
        public String createdBy;

        public Instant createdBefore;

        public Instant createdAfter;

        public boolean accept(E entity) {
            return ((((createdBy) == null) || (createdBy.equals(entity.createdBy))) && (((createdBefore) == null) || ((createdBefore.compareTo(entity.createdDate)) >= 0))) && (((createdAfter) == null) || ((createdAfter.compareTo(entity.createdDate)) >= 0));
        }
    }

    public static class ArticlePredicate extends ServletAnnotationControllerHandlerMethodTests.EntityPredicate<ServletAnnotationControllerHandlerMethodTests.Article> {
        public String query;

        @Override
        public boolean accept(ServletAnnotationControllerHandlerMethodTests.Article entity) {
            return (super.accept(entity)) && (((query) == null) || ((entity.title.contains(query)) || (entity.content.contains(query))));
        }
    }

    @Controller
    public static class RequestParamMapController {
        @RequestMapping("/map")
        public void map(@RequestParam
        Map<String, String> params, Writer writer) throws IOException {
            for (Iterator<Map.Entry<String, String>> it = params.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, String> entry = it.next();
                writer.write((((entry.getKey()) + "=") + (entry.getValue())));
                if (it.hasNext()) {
                    writer.write(',');
                }
            }
        }

        @RequestMapping("/multiValueMap")
        public void multiValueMap(@RequestParam
        MultiValueMap<String, String> params, Writer writer) throws IOException {
            for (Iterator<Map.Entry<String, java.util.List<String>>> it1 = params.entrySet().iterator(); it1.hasNext();) {
                Map.Entry<String, java.util.List<String>> entry = it1.next();
                writer.write(((entry.getKey()) + "=["));
                for (Iterator<String> it2 = entry.getValue().iterator(); it2.hasNext();) {
                    String value = it2.next();
                    writer.write(value);
                    if (it2.hasNext()) {
                        writer.write(',');
                    }
                }
                writer.write(']');
                if (it1.hasNext()) {
                    writer.write(',');
                }
            }
        }
    }

    @Controller
    public static class RequestHeaderMapController {
        @RequestMapping("/map")
        public void map(@RequestHeader
        Map<String, String> headers, Writer writer) throws IOException {
            for (Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, String> entry = it.next();
                writer.write((((entry.getKey()) + "=") + (entry.getValue())));
                if (it.hasNext()) {
                    writer.write(',');
                }
            }
        }

        @RequestMapping("/multiValueMap")
        public void multiValueMap(@RequestHeader
        MultiValueMap<String, String> headers, Writer writer) throws IOException {
            for (Iterator<Map.Entry<String, java.util.List<String>>> it1 = headers.entrySet().iterator(); it1.hasNext();) {
                Map.Entry<String, java.util.List<String>> entry = it1.next();
                writer.write(((entry.getKey()) + "=["));
                for (Iterator<String> it2 = entry.getValue().iterator(); it2.hasNext();) {
                    String value = it2.next();
                    writer.write(value);
                    if (it2.hasNext()) {
                        writer.write(',');
                    }
                }
                writer.write(']');
                if (it1.hasNext()) {
                    writer.write(',');
                }
            }
        }

        @RequestMapping("/httpHeaders")
        public void httpHeaders(@RequestHeader
        HttpHeaders headers, Writer writer) throws IOException {
            Assert.assertEquals("Invalid Content-Type", new MediaType("text", "html"), headers.getContentType());
            multiValueMap(headers, writer);
        }
    }

    @Controller
    public interface IMyController {
        @RequestMapping("/handle")
        void handle(Writer writer, @RequestParam(value = "p", required = false)
        String param) throws IOException;
    }

    @Controller
    public static class IMyControllerImpl implements ServletAnnotationControllerHandlerMethodTests.IMyController {
        @Override
        public void handle(Writer writer, @RequestParam(value = "p", required = false)
        String param) throws IOException {
            writer.write(("handle " + param));
        }
    }

    public abstract static class MyAbstractController {
        @RequestMapping("/handle")
        public abstract void handle(Writer writer) throws IOException;
    }

    @Controller
    public static class MyAbstractControllerImpl extends ServletAnnotationControllerHandlerMethodTests.MyAbstractController {
        @Override
        public void handle(Writer writer) throws IOException {
            writer.write("handle");
        }
    }

    @Controller
    public static class TrailingSlashController {
        @RequestMapping(value = "/", method = RequestMethod.GET)
        public void root(Writer writer) throws IOException {
            writer.write("root");
        }

        @RequestMapping(value = "/{templatePath}/", method = RequestMethod.GET)
        public void templatePath(Writer writer) throws IOException {
            writer.write("templatePath");
        }
    }

    @Controller
    public static class ResponseEntityController {
        @PostMapping("/foo")
        public ResponseEntity<String> foo(HttpEntity<byte[]> requestEntity) throws Exception {
            Assert.assertNotNull(requestEntity);
            Assert.assertEquals("MyValue", requestEntity.getHeaders().getFirst("MyRequestHeader"));
            String body = new String(requestEntity.getBody(), "UTF-8");
            Assert.assertEquals("Hello World", body);
            URI location = new URI("/foo");
            return ResponseEntity.created(location).header("MyResponseHeader", "MyValue").body(body);
        }

        @GetMapping("/bar")
        public ResponseEntity<Void> bar() {
            return ResponseEntity.notFound().header("MyResponseHeader", "MyValue").build();
        }

        @GetMapping("/baz")
        public ResponseEntity<String> baz() {
            return ResponseEntity.ok().header("MyResponseHeader", "MyValue").body("body");
        }

        @RequestMapping(path = "/stores", method = RequestMethod.HEAD)
        public ResponseEntity<Void> headResource() {
            return ResponseEntity.ok().header("h1", "v1").build();
        }

        @GetMapping("/stores")
        public ResponseEntity<String> getResource() {
            return ResponseEntity.ok().body("body");
        }

        @GetMapping("/test-entity")
        public ResponseEntity<ServletAnnotationControllerHandlerMethodTests.TestEntity> testEntity() {
            ServletAnnotationControllerHandlerMethodTests.TestEntity entity = new ServletAnnotationControllerHandlerMethodTests.TestEntity();
            entity.setName("Foo Bar");
            return ResponseEntity.ok().contentType(APPLICATION_XML).body(entity);
        }
    }

    @XmlRootElement
    static class TestEntity {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Controller
    public static class CustomMapEditorController {
        @InitBinder
        public void initBinder(WebDataBinder binder) {
            binder.initBeanPropertyAccess();
            binder.registerCustomEditor(Map.class, new ServletAnnotationControllerHandlerMethodTests.CustomMapEditor());
        }

        @SuppressWarnings("rawtypes")
        @RequestMapping("/handle")
        public void handle(@RequestParam("map")
        Map map, Writer writer) throws IOException {
            writer.write(("test-" + map));
        }
    }

    public static class CustomMapEditor extends PropertyEditorSupport {
        @Override
        public void setAsText(String text) throws IllegalArgumentException {
            if (StringUtils.hasText(text)) {
                setValue(Collections.singletonMap("foo", text));
            } else {
                setValue(null);
            }
        }
    }

    @Controller
    public static class MultipartController {
        @InitBinder
        public void initBinder(WebDataBinder binder) {
            binder.registerCustomEditor(String.class, new StringMultipartFileEditor());
        }

        @RequestMapping("/singleString")
        public void processMultipart(@RequestParam("content")
        String content, HttpServletResponse response) throws IOException {
            response.getWriter().write(content);
        }

        @RequestMapping("/stringArray")
        public void processMultipart(@RequestParam("content")
        String[] content, HttpServletResponse response) throws IOException {
            response.getWriter().write(StringUtils.arrayToDelimitedString(content, "-"));
        }
    }

    @Controller
    public static class CsvController {
        @RequestMapping("/singleInteger")
        public void processCsv(@RequestParam("content")
        Integer content, HttpServletResponse response) throws IOException {
            response.getWriter().write(content.toString());
        }

        @RequestMapping("/integerArray")
        public void processCsv(@RequestParam("content")
        Integer[] content, HttpServletResponse response) throws IOException {
            response.getWriter().write(StringUtils.arrayToDelimitedString(content, "-"));
        }
    }

    @Controller
    @RequestMapping("/t1")
    protected static class NoPathGetAndM2PostController {
        @RequestMapping(method = RequestMethod.GET)
        public void handle1(Writer writer) throws IOException {
            writer.write("handle1");
        }

        @RequestMapping(value = "/m2", method = RequestMethod.POST)
        public void handle2(Writer writer) throws IOException {
            writer.write("handle2");
        }
    }

    @Controller
    static class HeadersConditionController {
        @RequestMapping(value = "/", method = RequestMethod.GET)
        public String home() {
            return "home";
        }

        @RequestMapping(value = "/", method = RequestMethod.GET, headers = "Accept=application/json")
        @ResponseBody
        public String homeJson() {
            return "homeJson";
        }
    }

    @Controller
    static class RedirectAttributesController {
        @InitBinder
        public void initBinder(WebDataBinder dataBinder) {
            dataBinder.setRequiredFields("name");
        }

        @GetMapping("/messages/{id}")
        public void message(ModelMap model, Writer writer) throws IOException {
            writer.write(("Got: " + (model.get("successMessage"))));
        }

        @PostMapping("/messages")
        public String sendMessage(TestBean testBean, BindingResult result, RedirectAttributes attributes) {
            if (result.hasErrors()) {
                return "messages/new";
            }
            attributes.addAttribute("id", "1").addAttribute("name", "value");
            attributes.addFlashAttribute("successMessage", "yay!");
            return "redirect:/messages/{id}";
        }

        @PostMapping("/messages-response-entity")
        public ResponseEntity<Void> sendMessage(RedirectAttributes attributes) {
            attributes.addFlashAttribute("successMessage", "yay!");
            URI location = URI.create("/messages/1?name=value");
            return ResponseEntity.status(HttpStatus.FOUND).location(location).build();
        }
    }

    @Controller
    static class PrototypeController {
        private int count;

        @InitBinder
        public void initBinder(WebDataBinder dataBinder) {
            (this.count)++;
        }

        @ModelAttribute
        public void populate(Model model) {
            (this.count)++;
        }

        @RequestMapping("/")
        public void message(int param, Writer writer) throws IOException {
            (this.count)++;
            writer.write(("count:" + (this.count)));
        }
    }

    @RestController
    static class ThisWillActuallyRun {
        @RequestMapping(value = "/", method = RequestMethod.GET)
        public String home() {
            return "Hello World!";
        }
    }

    @Controller
    static class HttpHeadersResponseController {
        @RequestMapping(value = "", method = RequestMethod.POST)
        @ResponseStatus(HttpStatus.CREATED)
        public HttpHeaders create() throws URISyntaxException {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(new URI("/test/items/123"));
            return headers;
        }

        @RequestMapping(value = "empty", method = RequestMethod.POST)
        @ResponseStatus(HttpStatus.CREATED)
        public HttpHeaders createNoHeader() {
            return new HttpHeaders();
        }
    }

    @RestController
    public static class TextRestController {
        @RequestMapping(path = "/a1", method = RequestMethod.GET)
        public String a1(@RequestBody
        String body) {
            return body;
        }

        @RequestMapping(path = "/a2.html", method = RequestMethod.GET)
        public String a2(@RequestBody
        String body) {
            return body;
        }

        @RequestMapping(path = "/a3", method = RequestMethod.GET, produces = "text/html")
        public String a3(@RequestBody
        String body) throws IOException {
            return body;
        }

        @RequestMapping(path = "/a4.css", method = RequestMethod.GET)
        public String a4(@RequestBody
        String body) {
            return body;
        }
    }

    @Controller
    public static class ModelAndViewController {
        @RequestMapping("/path")
        public ModelAndView methodWithHttpStatus(ServletAnnotationControllerHandlerMethodTests.MyEntity object) {
            return new ModelAndView("view", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        @RequestMapping("/exception")
        public void raiseException() throws Exception {
            throw new ServletAnnotationControllerHandlerMethodTests.ModelAndViewController.TestException();
        }

        @ExceptionHandler(ServletAnnotationControllerHandlerMethodTests.ModelAndViewController.TestException.class)
        public ModelAndView handleException() {
            return new ModelAndView("view", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        @SuppressWarnings("serial")
        private static class TestException extends Exception {}
    }

    public static class DataClass {
        @NotNull
        public final String param1;

        public final boolean param2;

        public int param3;

        @ConstructorProperties({ "param1", "param2", "optionalParam" })
        public DataClass(String param1, boolean p2, Optional<Integer> optionalParam) {
            this.param1 = param1;
            this.param2 = p2;
            org.springframework.util.Assert.notNull(optionalParam, "Optional must not be null");
            optionalParam.ifPresent(( integer) -> this.param3 = integer);
        }

        public void setParam3(int param3) {
            this.param3 = param3;
        }
    }

    @RestController
    public static class DataClassController {
        @RequestMapping("/bind")
        public String handle(ServletAnnotationControllerHandlerMethodTests.DataClass data) {
            return ((((data.param1) + "-") + (data.param2)) + "-") + (data.param3);
        }
    }

    @RestController
    public static class ValidatedDataClassController {
        @InitBinder
        public void initBinder(WebDataBinder binder) {
            binder.initDirectFieldAccess();
            binder.setConversionService(new DefaultFormattingConversionService());
            LocalValidatorFactoryBean vf = new LocalValidatorFactoryBean();
            vf.afterPropertiesSet();
            binder.setValidator(vf);
        }

        @RequestMapping("/bind")
        public ServletAnnotationControllerHandlerMethodTests.BindStatusView handle(@Valid
        ServletAnnotationControllerHandlerMethodTests.DataClass data, BindingResult result) {
            if (result.hasErrors()) {
                return new ServletAnnotationControllerHandlerMethodTests.BindStatusView((((((((result.getErrorCount()) + ":") + (result.getFieldValue("param1"))) + "-") + (result.getFieldValue("param2"))) + "-") + (result.getFieldValue("param3"))));
            }
            return new ServletAnnotationControllerHandlerMethodTests.BindStatusView((((((data.param1) + "-") + (data.param2)) + "-") + (data.param3)));
        }
    }

    public static class BindStatusView extends AbstractView {
        private final String content;

        public BindStatusView(String content) {
            this.content = content;
        }

        @Override
        protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            RequestContext rc = new RequestContext(request, model);
            rc.getBindStatus("dataClass");
            rc.getBindStatus("dataClass.param1");
            rc.getBindStatus("dataClass.param2");
            rc.getBindStatus("dataClass.param3");
            response.getWriter().write(this.content);
        }
    }

    @RestController
    public static class OptionalDataClassController {
        @RequestMapping("/bind")
        public String handle(Optional<ServletAnnotationControllerHandlerMethodTests.DataClass> optionalData, BindingResult result) {
            if (result.hasErrors()) {
                Assert.assertNotNull(optionalData);
                Assert.assertFalse(optionalData.isPresent());
                return ((((result.getFieldValue("param1")) + "-") + (result.getFieldValue("param2"))) + "-") + (result.getFieldValue("param3"));
            }
            return optionalData.map(( data) -> ((((data.param1) + "-") + (data.param2)) + "-") + (data.param3)).orElse("");
        }
    }

    public static class DateClass {
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        public LocalDate date;

        public DateClass(LocalDate date) {
            this.date = date;
        }
    }

    @RestController
    public static class DateClassController {
        @InitBinder
        public void initBinder(WebDataBinder binder) {
            binder.initDirectFieldAccess();
            binder.setConversionService(new DefaultFormattingConversionService());
        }

        @RequestMapping("/bind")
        public String handle(ServletAnnotationControllerHandlerMethodTests.DateClass data, BindingResult result) {
            if (result.hasErrors()) {
                return result.getFieldError().toString();
            }
            Assert.assertNotNull(data);
            Assert.assertNotNull(data.date);
            Assert.assertEquals(2010, data.date.getYear());
            Assert.assertEquals(1, data.date.getMonthValue());
            Assert.assertEquals(1, data.date.getDayOfMonth());
            return result.getFieldValue("date").toString();
        }
    }
}

