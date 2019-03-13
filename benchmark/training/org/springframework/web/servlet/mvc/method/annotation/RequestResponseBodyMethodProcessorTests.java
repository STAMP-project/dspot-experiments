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


import MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import MediaType.APPLICATION_JSON_UTF8_VALUE;
import MediaType.APPLICATION_JSON_VALUE;
import MediaType.APPLICATION_XML_VALUE;
import WebUtils.FORWARD_REQUEST_URI_ATTRIBUTE;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;


/**
 * Test fixture for a {@link RequestResponseBodyMethodProcessor} with
 * actual delegation to {@link HttpMessageConverter} instances. Also see
 * {@link RequestResponseBodyMethodProcessorMockTests}.
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 */
@SuppressWarnings("unused")
public class RequestResponseBodyMethodProcessorTests {
    private ModelAndViewContainer container;

    private MockHttpServletRequest servletRequest;

    private MockHttpServletResponse servletResponse;

    private NativeWebRequest request;

    private RequestResponseBodyMethodProcessorTests.ValidatingBinderFactory factory;

    private MethodParameter paramGenericList;

    private MethodParameter paramSimpleBean;

    private MethodParameter paramMultiValueMap;

    private MethodParameter paramString;

    private MethodParameter returnTypeString;

    @Test
    public void resolveArgumentParameterizedType() throws Exception {
        String content = "[{\"name\" : \"Jad\"}, {\"name\" : \"Robert\"}]";
        this.servletRequest.setContent(content.getBytes("UTF-8"));
        this.servletRequest.setContentType(APPLICATION_JSON_VALUE);
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        @SuppressWarnings("unchecked")
        List<RequestResponseBodyMethodProcessorTests.SimpleBean> result = ((List<RequestResponseBodyMethodProcessorTests.SimpleBean>) (processor.resolveArgument(paramGenericList, container, request, factory)));
        Assert.assertNotNull(result);
        Assert.assertEquals("Jad", result.get(0).getName());
        Assert.assertEquals("Robert", result.get(1).getName());
    }

    @Test
    public void resolveArgumentRawTypeFromParameterizedType() throws Exception {
        String content = "fruit=apple&vegetable=kale";
        this.servletRequest.setMethod("GET");
        this.servletRequest.setContent(content.getBytes("UTF-8"));
        this.servletRequest.setContentType(APPLICATION_FORM_URLENCODED_VALUE);
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new AllEncompassingFormHttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        @SuppressWarnings("unchecked")
        MultiValueMap<String, String> result = ((MultiValueMap<String, String>) (processor.resolveArgument(paramMultiValueMap, container, request, factory)));
        Assert.assertNotNull(result);
        Assert.assertEquals("apple", result.getFirst("fruit"));
        Assert.assertEquals("kale", result.getFirst("vegetable"));
    }

    @Test
    public void resolveArgumentClassJson() throws Exception {
        String content = "{\"name\" : \"Jad\"}";
        this.servletRequest.setContent(content.getBytes("UTF-8"));
        this.servletRequest.setContentType("application/json");
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        RequestResponseBodyMethodProcessorTests.SimpleBean result = ((RequestResponseBodyMethodProcessorTests.SimpleBean) (processor.resolveArgument(paramSimpleBean, container, request, factory)));
        Assert.assertNotNull(result);
        Assert.assertEquals("Jad", result.getName());
    }

    @Test
    public void resolveArgumentClassString() throws Exception {
        String content = "foobarbaz";
        this.servletRequest.setContent(content.getBytes("UTF-8"));
        this.servletRequest.setContentType("application/json");
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new StringHttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        String result = ((String) (processor.resolveArgument(paramString, container, request, factory)));
        Assert.assertNotNull(result);
        Assert.assertEquals("foobarbaz", result);
    }

    // SPR-9942
    @Test(expected = HttpMessageNotReadableException.class)
    public void resolveArgumentRequiredNoContent() throws Exception {
        this.servletRequest.setContent(new byte[0]);
        this.servletRequest.setContentType("text/plain");
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new StringHttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        processor.resolveArgument(paramString, container, request, factory);
    }

    // SPR-12778
    @Test
    public void resolveArgumentRequiredNoContentDefaultValue() throws Exception {
        this.servletRequest.setContent(new byte[0]);
        this.servletRequest.setContentType("text/plain");
        List<HttpMessageConverter<?>> converters = Collections.singletonList(new StringHttpMessageConverter());
        List<Object> advice = Collections.singletonList(new RequestResponseBodyMethodProcessorTests.EmptyRequestBodyAdvice());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters, advice);
        String arg = ((String) (processor.resolveArgument(paramString, container, request, factory)));
        Assert.assertNotNull(arg);
        Assert.assertEquals("default value for empty body", arg);
    }

    // SPR-9964
    @Test
    public void resolveArgumentTypeVariable() throws Exception {
        Method method = RequestResponseBodyMethodProcessorTests.MyParameterizedController.class.getMethod("handleDto", RequestResponseBodyMethodProcessorTests.Identifiable.class);
        HandlerMethod handlerMethod = new HandlerMethod(new RequestResponseBodyMethodProcessorTests.MySimpleParameterizedController(), method);
        MethodParameter methodParam = handlerMethod.getMethodParameters()[0];
        String content = "{\"name\" : \"Jad\"}";
        this.servletRequest.setContent(content.getBytes("UTF-8"));
        this.servletRequest.setContentType(APPLICATION_JSON_VALUE);
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        RequestResponseBodyMethodProcessorTests.SimpleBean result = ((RequestResponseBodyMethodProcessorTests.SimpleBean) (processor.resolveArgument(methodParam, container, request, factory)));
        Assert.assertNotNull(result);
        Assert.assertEquals("Jad", result.getName());
    }

    // SPR-14470
    @Test
    public void resolveParameterizedWithTypeVariableArgument() throws Exception {
        Method method = RequestResponseBodyMethodProcessorTests.MyParameterizedControllerWithList.class.getMethod("handleDto", List.class);
        HandlerMethod handlerMethod = new HandlerMethod(new RequestResponseBodyMethodProcessorTests.MySimpleParameterizedControllerWithList(), method);
        MethodParameter methodParam = handlerMethod.getMethodParameters()[0];
        String content = "[{\"name\" : \"Jad\"}, {\"name\" : \"Robert\"}]";
        this.servletRequest.setContent(content.getBytes("UTF-8"));
        this.servletRequest.setContentType(APPLICATION_JSON_VALUE);
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        @SuppressWarnings("unchecked")
        List<RequestResponseBodyMethodProcessorTests.SimpleBean> result = ((List<RequestResponseBodyMethodProcessorTests.SimpleBean>) (processor.resolveArgument(methodParam, container, request, factory)));
        Assert.assertNotNull(result);
        Assert.assertEquals("Jad", result.get(0).getName());
        Assert.assertEquals("Robert", result.get(1).getName());
    }

    // SPR-11225
    @Test
    public void resolveArgumentTypeVariableWithNonGenericConverter() throws Exception {
        Method method = RequestResponseBodyMethodProcessorTests.MyParameterizedController.class.getMethod("handleDto", RequestResponseBodyMethodProcessorTests.Identifiable.class);
        HandlerMethod handlerMethod = new HandlerMethod(new RequestResponseBodyMethodProcessorTests.MySimpleParameterizedController(), method);
        MethodParameter methodParam = handlerMethod.getMethodParameters()[0];
        String content = "{\"name\" : \"Jad\"}";
        this.servletRequest.setContent(content.getBytes("UTF-8"));
        this.servletRequest.setContentType(APPLICATION_JSON_VALUE);
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        HttpMessageConverter<Object> target = new MappingJackson2HttpMessageConverter();
        HttpMessageConverter<?> proxy = ProxyFactory.getProxy(HttpMessageConverter.class, new org.springframework.aop.target.SingletonTargetSource(target));
        converters.add(proxy);
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        RequestResponseBodyMethodProcessorTests.SimpleBean result = ((RequestResponseBodyMethodProcessorTests.SimpleBean) (processor.resolveArgument(methodParam, container, request, factory)));
        Assert.assertNotNull(result);
        Assert.assertEquals("Jad", result.getName());
    }

    // SPR-9160
    @Test
    public void handleReturnValueSortByQuality() throws Exception {
        this.servletRequest.addHeader("Accept", "text/plain; q=0.5, application/json");
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        processor.writeWithMessageConverters("Foo", returnTypeString, request);
        Assert.assertEquals("application/json;charset=UTF-8", servletResponse.getHeader("Content-Type"));
    }

    @Test
    public void handleReturnValueString() throws Exception {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        processor.handleReturnValue("Foo", returnTypeString, container, request);
        Assert.assertEquals("text/plain;charset=ISO-8859-1", servletResponse.getHeader("Content-Type"));
        Assert.assertEquals("Foo", servletResponse.getContentAsString());
    }

    // SPR-13423
    @Test
    public void handleReturnValueCharSequence() throws Exception {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        Method method = RequestResponseBodyMethodProcessorTests.ResponseBodyController.class.getMethod("handleWithCharSequence");
        MethodParameter returnType = new MethodParameter(method, (-1));
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        processor.handleReturnValue(new StringBuilder("Foo"), returnType, container, request);
        Assert.assertEquals("text/plain;charset=ISO-8859-1", servletResponse.getHeader("Content-Type"));
        Assert.assertEquals("Foo", servletResponse.getContentAsString());
    }

    @Test
    public void handleReturnValueStringAcceptCharset() throws Exception {
        this.servletRequest.addHeader("Accept", "text/plain;charset=UTF-8");
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        processor.writeWithMessageConverters("Foo", returnTypeString, request);
        Assert.assertEquals("text/plain;charset=UTF-8", servletResponse.getHeader("Content-Type"));
    }

    // SPR-12894
    @Test
    public void handleReturnValueImage() throws Exception {
        this.servletRequest.addHeader("Accept", "*/*");
        Method method = getClass().getDeclaredMethod("getImage");
        MethodParameter returnType = new MethodParameter(method, (-1));
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new ResourceHttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        ClassPathResource resource = new ClassPathResource("logo.jpg", getClass());
        processor.writeWithMessageConverters(resource, returnType, this.request);
        Assert.assertEquals("image/jpeg", this.servletResponse.getHeader("Content-Type"));
    }

    // SPR-13135
    @Test(expected = IllegalArgumentException.class)
    public void handleReturnValueWithInvalidReturnType() throws Exception {
        Method method = getClass().getDeclaredMethod("handleAndReturnOutputStream");
        MethodParameter returnType = new MethodParameter(method, (-1));
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(new ArrayList());
        processor.writeWithMessageConverters(new ByteArrayOutputStream(), returnType, this.request);
    }

    @Test
    public void addContentDispositionHeader() throws Exception {
        ContentNegotiationManagerFactoryBean factory = new ContentNegotiationManagerFactoryBean();
        factory.addMediaType("pdf", new MediaType("application", "pdf"));
        factory.afterPropertiesSet();
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(Collections.singletonList(new StringHttpMessageConverter()), factory.getObject());
        assertContentDisposition(processor, false, "/hello.json", "whitelisted extension");
        assertContentDisposition(processor, false, "/hello.pdf", "registered extension");
        assertContentDisposition(processor, true, "/hello.dataless", "unknown extension");
        // path parameters
        assertContentDisposition(processor, false, "/hello.json;a=b", "path param shouldn't cause issue");
        assertContentDisposition(processor, true, "/hello.json;a=b;setup.dataless", "unknown ext in path params");
        assertContentDisposition(processor, true, "/hello.dataless;a=b;setup.json", "unknown ext in filename");
        assertContentDisposition(processor, false, "/hello.json;a=b;setup.json", "whitelisted extensions");
        // encoded dot
        assertContentDisposition(processor, true, "/hello%2Edataless;a=b;setup.json", "encoded dot in filename");
        assertContentDisposition(processor, true, "/hello.json;a=b;setup%2Edataless", "encoded dot in path params");
        assertContentDisposition(processor, true, "/hello.dataless%3Bsetup.bat", "encoded dot in path params");
        this.servletRequest.setAttribute(FORWARD_REQUEST_URI_ATTRIBUTE, "/hello.bat");
        assertContentDisposition(processor, true, "/bonjour", "forwarded URL");
        this.servletRequest.removeAttribute(FORWARD_REQUEST_URI_ATTRIBUTE);
    }

    @Test
    public void supportsReturnTypeResponseBodyOnType() throws Exception {
        Method method = RequestResponseBodyMethodProcessorTests.ResponseBodyController.class.getMethod("handle");
        MethodParameter returnType = new MethodParameter(method, (-1));
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new StringHttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        Assert.assertTrue("Failed to recognize type-level @ResponseBody", processor.supportsReturnType(returnType));
    }

    @Test
    public void supportsReturnTypeRestController() throws Exception {
        Method method = RequestResponseBodyMethodProcessorTests.TestRestController.class.getMethod("handle");
        MethodParameter returnType = new MethodParameter(method, (-1));
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new StringHttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        Assert.assertTrue("Failed to recognize type-level @RestController", processor.supportsReturnType(returnType));
    }

    @Test
    public void jacksonJsonViewWithResponseBodyAndJsonMessageConverter() throws Exception {
        Method method = RequestResponseBodyMethodProcessorTests.JacksonController.class.getMethod("handleResponseBody");
        HandlerMethod handlerMethod = new HandlerMethod(new RequestResponseBodyMethodProcessorTests.JacksonController(), method);
        MethodParameter methodReturnType = handlerMethod.getReturnType();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters, null, Collections.singletonList(new JsonViewResponseBodyAdvice()));
        Object returnValue = new RequestResponseBodyMethodProcessorTests.JacksonController().handleResponseBody();
        processor.handleReturnValue(returnValue, methodReturnType, this.container, this.request);
        String content = this.servletResponse.getContentAsString();
        Assert.assertFalse(content.contains("\"withView1\":\"with\""));
        Assert.assertTrue(content.contains("\"withView2\":\"with\""));
        Assert.assertFalse(content.contains("\"withoutView\":\"without\""));
    }

    @Test
    public void jacksonJsonViewWithResponseEntityAndJsonMessageConverter() throws Exception {
        Method method = RequestResponseBodyMethodProcessorTests.JacksonController.class.getMethod("handleResponseEntity");
        HandlerMethod handlerMethod = new HandlerMethod(new RequestResponseBodyMethodProcessorTests.JacksonController(), method);
        MethodParameter methodReturnType = handlerMethod.getReturnType();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        HttpEntityMethodProcessor processor = new HttpEntityMethodProcessor(converters, null, Collections.singletonList(new JsonViewResponseBodyAdvice()));
        Object returnValue = new RequestResponseBodyMethodProcessorTests.JacksonController().handleResponseEntity();
        processor.handleReturnValue(returnValue, methodReturnType, this.container, this.request);
        String content = this.servletResponse.getContentAsString();
        Assert.assertFalse(content.contains("\"withView1\":\"with\""));
        Assert.assertTrue(content.contains("\"withView2\":\"with\""));
        Assert.assertFalse(content.contains("\"withoutView\":\"without\""));
    }

    // SPR-12149
    @Test
    public void jacksonJsonViewWithResponseBodyAndXmlMessageConverter() throws Exception {
        Method method = RequestResponseBodyMethodProcessorTests.JacksonController.class.getMethod("handleResponseBody");
        HandlerMethod handlerMethod = new HandlerMethod(new RequestResponseBodyMethodProcessorTests.JacksonController(), method);
        MethodParameter methodReturnType = handlerMethod.getReturnType();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2XmlHttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters, null, Collections.singletonList(new JsonViewResponseBodyAdvice()));
        Object returnValue = new RequestResponseBodyMethodProcessorTests.JacksonController().handleResponseBody();
        processor.handleReturnValue(returnValue, methodReturnType, this.container, this.request);
        String content = this.servletResponse.getContentAsString();
        Assert.assertFalse(content.contains("<withView1>with</withView1>"));
        Assert.assertTrue(content.contains("<withView2>with</withView2>"));
        Assert.assertFalse(content.contains("<withoutView>without</withoutView>"));
    }

    // SPR-12149
    @Test
    public void jacksonJsonViewWithResponseEntityAndXmlMessageConverter() throws Exception {
        Method method = RequestResponseBodyMethodProcessorTests.JacksonController.class.getMethod("handleResponseEntity");
        HandlerMethod handlerMethod = new HandlerMethod(new RequestResponseBodyMethodProcessorTests.JacksonController(), method);
        MethodParameter methodReturnType = handlerMethod.getReturnType();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2XmlHttpMessageConverter());
        HttpEntityMethodProcessor processor = new HttpEntityMethodProcessor(converters, null, Collections.singletonList(new JsonViewResponseBodyAdvice()));
        Object returnValue = new RequestResponseBodyMethodProcessorTests.JacksonController().handleResponseEntity();
        processor.handleReturnValue(returnValue, methodReturnType, this.container, this.request);
        String content = this.servletResponse.getContentAsString();
        Assert.assertFalse(content.contains("<withView1>with</withView1>"));
        Assert.assertTrue(content.contains("<withView2>with</withView2>"));
        Assert.assertFalse(content.contains("<withoutView>without</withoutView>"));
    }

    // SPR-12501
    @Test
    public void resolveArgumentWithJacksonJsonView() throws Exception {
        String content = "{\"withView1\" : \"with\", \"withView2\" : \"with\", \"withoutView\" : \"without\"}";
        this.servletRequest.setContent(content.getBytes("UTF-8"));
        this.servletRequest.setContentType(APPLICATION_JSON_VALUE);
        Method method = RequestResponseBodyMethodProcessorTests.JacksonController.class.getMethod("handleRequestBody", RequestResponseBodyMethodProcessorTests.JacksonViewBean.class);
        HandlerMethod handlerMethod = new HandlerMethod(new RequestResponseBodyMethodProcessorTests.JacksonController(), method);
        MethodParameter methodParameter = handlerMethod.getMethodParameters()[0];
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters, null, Collections.singletonList(new JsonViewRequestBodyAdvice()));
        @SuppressWarnings("unchecked")
        RequestResponseBodyMethodProcessorTests.JacksonViewBean result = ((RequestResponseBodyMethodProcessorTests.JacksonViewBean) (processor.resolveArgument(methodParameter, this.container, this.request, this.factory)));
        Assert.assertNotNull(result);
        Assert.assertEquals("with", result.getWithView1());
        Assert.assertNull(result.getWithView2());
        Assert.assertNull(result.getWithoutView());
    }

    // SPR-12501
    @Test
    public void resolveHttpEntityArgumentWithJacksonJsonView() throws Exception {
        String content = "{\"withView1\" : \"with\", \"withView2\" : \"with\", \"withoutView\" : \"without\"}";
        this.servletRequest.setContent(content.getBytes("UTF-8"));
        this.servletRequest.setContentType(APPLICATION_JSON_VALUE);
        Method method = RequestResponseBodyMethodProcessorTests.JacksonController.class.getMethod("handleHttpEntity", HttpEntity.class);
        HandlerMethod handlerMethod = new HandlerMethod(new RequestResponseBodyMethodProcessorTests.JacksonController(), method);
        MethodParameter methodParameter = handlerMethod.getMethodParameters()[0];
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        HttpEntityMethodProcessor processor = new HttpEntityMethodProcessor(converters, null, Collections.singletonList(new JsonViewRequestBodyAdvice()));
        @SuppressWarnings("unchecked")
        HttpEntity<RequestResponseBodyMethodProcessorTests.JacksonViewBean> result = ((HttpEntity<RequestResponseBodyMethodProcessorTests.JacksonViewBean>) (processor.resolveArgument(methodParameter, this.container, this.request, this.factory)));
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals("with", result.getBody().getWithView1());
        Assert.assertNull(result.getBody().getWithView2());
        Assert.assertNull(result.getBody().getWithoutView());
    }

    // SPR-12501
    @Test
    public void resolveArgumentWithJacksonJsonViewAndXmlMessageConverter() throws Exception {
        String content = "<root>" + (("<withView1>with</withView1>" + "<withView2>with</withView2>") + "<withoutView>without</withoutView></root>");
        this.servletRequest.setContent(content.getBytes("UTF-8"));
        this.servletRequest.setContentType(APPLICATION_XML_VALUE);
        Method method = RequestResponseBodyMethodProcessorTests.JacksonController.class.getMethod("handleRequestBody", RequestResponseBodyMethodProcessorTests.JacksonViewBean.class);
        HandlerMethod handlerMethod = new HandlerMethod(new RequestResponseBodyMethodProcessorTests.JacksonController(), method);
        MethodParameter methodParameter = handlerMethod.getMethodParameters()[0];
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2XmlHttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters, null, Collections.singletonList(new JsonViewRequestBodyAdvice()));
        @SuppressWarnings("unchecked")
        RequestResponseBodyMethodProcessorTests.JacksonViewBean result = ((RequestResponseBodyMethodProcessorTests.JacksonViewBean) (processor.resolveArgument(methodParameter, this.container, this.request, this.factory)));
        Assert.assertNotNull(result);
        Assert.assertEquals("with", result.getWithView1());
        Assert.assertNull(result.getWithView2());
        Assert.assertNull(result.getWithoutView());
    }

    // SPR-12501
    @Test
    public void resolveHttpEntityArgumentWithJacksonJsonViewAndXmlMessageConverter() throws Exception {
        String content = "<root>" + (("<withView1>with</withView1>" + "<withView2>with</withView2>") + "<withoutView>without</withoutView></root>");
        this.servletRequest.setContent(content.getBytes("UTF-8"));
        this.servletRequest.setContentType(APPLICATION_XML_VALUE);
        Method method = RequestResponseBodyMethodProcessorTests.JacksonController.class.getMethod("handleHttpEntity", HttpEntity.class);
        HandlerMethod handlerMethod = new HandlerMethod(new RequestResponseBodyMethodProcessorTests.JacksonController(), method);
        MethodParameter methodParameter = handlerMethod.getMethodParameters()[0];
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2XmlHttpMessageConverter());
        HttpEntityMethodProcessor processor = new HttpEntityMethodProcessor(converters, null, Collections.singletonList(new JsonViewRequestBodyAdvice()));
        @SuppressWarnings("unchecked")
        HttpEntity<RequestResponseBodyMethodProcessorTests.JacksonViewBean> result = ((HttpEntity<RequestResponseBodyMethodProcessorTests.JacksonViewBean>) (processor.resolveArgument(methodParameter, this.container, this.request, this.factory)));
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getBody());
        Assert.assertEquals("with", result.getBody().getWithView1());
        Assert.assertNull(result.getBody().getWithView2());
        Assert.assertNull(result.getBody().getWithoutView());
    }

    // SPR-12811
    @Test
    public void jacksonTypeInfoList() throws Exception {
        Method method = RequestResponseBodyMethodProcessorTests.JacksonController.class.getMethod("handleTypeInfoList");
        HandlerMethod handlerMethod = new HandlerMethod(new RequestResponseBodyMethodProcessorTests.JacksonController(), method);
        MethodParameter methodReturnType = handlerMethod.getReturnType();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        Object returnValue = new RequestResponseBodyMethodProcessorTests.JacksonController().handleTypeInfoList();
        processor.handleReturnValue(returnValue, methodReturnType, this.container, this.request);
        String content = this.servletResponse.getContentAsString();
        Assert.assertTrue(content.contains("\"type\":\"foo\""));
        Assert.assertTrue(content.contains("\"type\":\"bar\""));
    }

    // SPR-13318
    @Test
    public void jacksonSubType() throws Exception {
        Method method = RequestResponseBodyMethodProcessorTests.JacksonController.class.getMethod("handleSubType");
        HandlerMethod handlerMethod = new HandlerMethod(new RequestResponseBodyMethodProcessorTests.JacksonController(), method);
        MethodParameter methodReturnType = handlerMethod.getReturnType();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        Object returnValue = new RequestResponseBodyMethodProcessorTests.JacksonController().handleSubType();
        processor.handleReturnValue(returnValue, methodReturnType, this.container, this.request);
        String content = this.servletResponse.getContentAsString();
        Assert.assertTrue(content.contains("\"id\":123"));
        Assert.assertTrue(content.contains("\"name\":\"foo\""));
    }

    // SPR-13318
    @Test
    public void jacksonSubTypeList() throws Exception {
        Method method = RequestResponseBodyMethodProcessorTests.JacksonController.class.getMethod("handleSubTypeList");
        HandlerMethod handlerMethod = new HandlerMethod(new RequestResponseBodyMethodProcessorTests.JacksonController(), method);
        MethodParameter methodReturnType = handlerMethod.getReturnType();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        Object returnValue = new RequestResponseBodyMethodProcessorTests.JacksonController().handleSubTypeList();
        processor.handleReturnValue(returnValue, methodReturnType, this.container, this.request);
        String content = this.servletResponse.getContentAsString();
        Assert.assertTrue(content.contains("\"id\":123"));
        Assert.assertTrue(content.contains("\"name\":\"foo\""));
        Assert.assertTrue(content.contains("\"id\":456"));
        Assert.assertTrue(content.contains("\"name\":\"bar\""));
    }

    // SPR-13631
    @Test
    public void defaultCharset() throws Exception {
        Method method = RequestResponseBodyMethodProcessorTests.JacksonController.class.getMethod("defaultCharset");
        HandlerMethod handlerMethod = new HandlerMethod(new RequestResponseBodyMethodProcessorTests.JacksonController(), method);
        MethodParameter methodReturnType = handlerMethod.getReturnType();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        Object returnValue = new RequestResponseBodyMethodProcessorTests.JacksonController().defaultCharset();
        processor.handleReturnValue(returnValue, methodReturnType, this.container, this.request);
        Assert.assertEquals("UTF-8", this.servletResponse.getCharacterEncoding());
    }

    // SPR-14520
    @Test
    public void resolveArgumentTypeVariableWithGenericInterface() throws Exception {
        this.servletRequest.setContent("\"foo\"".getBytes("UTF-8"));
        this.servletRequest.setContentType(APPLICATION_JSON_UTF8_VALUE);
        Method method = RequestResponseBodyMethodProcessorTests.MyControllerImplementingInterface.class.getMethod("handle", Object.class);
        HandlerMethod handlerMethod = new HandlerMethod(new RequestResponseBodyMethodProcessorTests.MyControllerImplementingInterface(), method);
        MethodParameter methodParameter = handlerMethod.getMethodParameters()[0];
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        RequestResponseBodyMethodProcessor processor = new RequestResponseBodyMethodProcessor(converters);
        String value = ((String) (processor.readWithMessageConverters(this.request, methodParameter, methodParameter.getGenericParameterType())));
        Assert.assertEquals("foo", value);
    }

    private abstract static class MyParameterizedController<DTO extends RequestResponseBodyMethodProcessorTests.Identifiable> {
        @SuppressWarnings("unused")
        public void handleDto(@RequestBody
        DTO dto) {
        }
    }

    private static class MySimpleParameterizedController extends RequestResponseBodyMethodProcessorTests.MyParameterizedController<RequestResponseBodyMethodProcessorTests.SimpleBean> {}

    private interface Identifiable extends Serializable {
        Long getId();

        void setId(Long id);
    }

    @SuppressWarnings("unused")
    private abstract static class MyParameterizedControllerWithList<DTO extends RequestResponseBodyMethodProcessorTests.Identifiable> {
        public void handleDto(@RequestBody
        List<DTO> dto) {
        }
    }

    @SuppressWarnings("unused")
    private static class MySimpleParameterizedControllerWithList extends RequestResponseBodyMethodProcessorTests.MyParameterizedControllerWithList<RequestResponseBodyMethodProcessorTests.SimpleBean> {}

    @SuppressWarnings({ "serial" })
    private static class SimpleBean implements RequestResponseBodyMethodProcessorTests.Identifiable {
        private Long id;

        private String name;

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private final class ValidatingBinderFactory implements WebDataBinderFactory {
        @Override
        public WebDataBinder createBinder(NativeWebRequest request, @Nullable
        Object target, String objectName) {
            LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
            validator.afterPropertiesSet();
            WebDataBinder dataBinder = new WebDataBinder(target, objectName);
            dataBinder.setValidator(validator);
            return dataBinder;
        }
    }

    @ResponseBody
    private static class ResponseBodyController {
        @RequestMapping
        public String handle() {
            return "hello";
        }

        @RequestMapping
        public CharSequence handleWithCharSequence() {
            return null;
        }
    }

    @RestController
    private static class TestRestController {
        @RequestMapping
        public String handle() {
            return "hello";
        }
    }

    private interface MyJacksonView1 {}

    private interface MyJacksonView2 {}

    private static class JacksonViewBean {
        @JsonView(RequestResponseBodyMethodProcessorTests.MyJacksonView1.class)
        private String withView1;

        @JsonView(RequestResponseBodyMethodProcessorTests.MyJacksonView2.class)
        private String withView2;

        private String withoutView;

        public String getWithView1() {
            return withView1;
        }

        public void setWithView1(String withView1) {
            this.withView1 = withView1;
        }

        public String getWithView2() {
            return withView2;
        }

        public void setWithView2(String withView2) {
            this.withView2 = withView2;
        }

        public String getWithoutView() {
            return withoutView;
        }

        public void setWithoutView(String withoutView) {
            this.withoutView = withoutView;
        }
    }

    @JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
    public static class ParentClass {
        private String parentProperty;

        public ParentClass() {
        }

        public ParentClass(String parentProperty) {
            this.parentProperty = parentProperty;
        }

        public String getParentProperty() {
            return parentProperty;
        }

        public void setParentProperty(String parentProperty) {
            this.parentProperty = parentProperty;
        }
    }

    @JsonTypeName("foo")
    public static class Foo extends RequestResponseBodyMethodProcessorTests.ParentClass {
        public Foo() {
        }

        public Foo(String parentProperty) {
            super(parentProperty);
        }
    }

    @JsonTypeName("bar")
    public static class Bar extends RequestResponseBodyMethodProcessorTests.ParentClass {
        public Bar() {
        }

        public Bar(String parentProperty) {
            super(parentProperty);
        }
    }

    private static class BaseController<T> {
        @RequestMapping
        @ResponseBody
        @SuppressWarnings("unchecked")
        public List<T> handleTypeInfoList() {
            List<T> list = new ArrayList<>();
            list.add(((T) (new RequestResponseBodyMethodProcessorTests.Foo("foo"))));
            list.add(((T) (new RequestResponseBodyMethodProcessorTests.Bar("bar"))));
            return list;
        }
    }

    private static class JacksonController extends RequestResponseBodyMethodProcessorTests.BaseController<RequestResponseBodyMethodProcessorTests.ParentClass> {
        @RequestMapping
        @ResponseBody
        @JsonView(RequestResponseBodyMethodProcessorTests.MyJacksonView2.class)
        public RequestResponseBodyMethodProcessorTests.JacksonViewBean handleResponseBody() {
            RequestResponseBodyMethodProcessorTests.JacksonViewBean bean = new RequestResponseBodyMethodProcessorTests.JacksonViewBean();
            bean.setWithView1("with");
            bean.setWithView2("with");
            bean.setWithoutView("without");
            return bean;
        }

        @RequestMapping
        @JsonView(RequestResponseBodyMethodProcessorTests.MyJacksonView2.class)
        public ResponseEntity<RequestResponseBodyMethodProcessorTests.JacksonViewBean> handleResponseEntity() {
            RequestResponseBodyMethodProcessorTests.JacksonViewBean bean = new RequestResponseBodyMethodProcessorTests.JacksonViewBean();
            bean.setWithView1("with");
            bean.setWithView2("with");
            bean.setWithoutView("without");
            ModelAndView mav = new ModelAndView(new MappingJackson2JsonView());
            mav.addObject("bean", bean);
            return new ResponseEntity(bean, HttpStatus.OK);
        }

        @RequestMapping
        @ResponseBody
        public RequestResponseBodyMethodProcessorTests.JacksonViewBean handleRequestBody(@JsonView(RequestResponseBodyMethodProcessorTests.MyJacksonView1.class)
        @RequestBody
        RequestResponseBodyMethodProcessorTests.JacksonViewBean bean) {
            return bean;
        }

        @RequestMapping
        @ResponseBody
        public RequestResponseBodyMethodProcessorTests.JacksonViewBean handleHttpEntity(@JsonView(RequestResponseBodyMethodProcessorTests.MyJacksonView1.class)
        HttpEntity<RequestResponseBodyMethodProcessorTests.JacksonViewBean> entity) {
            return entity.getBody();
        }

        @RequestMapping
        @ResponseBody
        public RequestResponseBodyMethodProcessorTests.Identifiable handleSubType() {
            RequestResponseBodyMethodProcessorTests.SimpleBean foo = new RequestResponseBodyMethodProcessorTests.SimpleBean();
            foo.setId(123L);
            foo.setName("foo");
            return foo;
        }

        @RequestMapping
        @ResponseBody
        public List<RequestResponseBodyMethodProcessorTests.Identifiable> handleSubTypeList() {
            RequestResponseBodyMethodProcessorTests.SimpleBean foo = new RequestResponseBodyMethodProcessorTests.SimpleBean();
            foo.setId(123L);
            foo.setName("foo");
            RequestResponseBodyMethodProcessorTests.SimpleBean bar = new RequestResponseBodyMethodProcessorTests.SimpleBean();
            bar.setId(456L);
            bar.setName("bar");
            return Arrays.asList(foo, bar);
        }

        @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
        @ResponseBody
        public String defaultCharset() {
            return "foo";
        }
    }

    private static class EmptyRequestBodyAdvice implements RequestBodyAdvice {
        @Override
        public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
            return StringHttpMessageConverter.class.equals(converterType);
        }

        @Override
        public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
            return inputMessage;
        }

        @Override
        public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
            return body;
        }

        @Override
        public Object handleEmptyBody(@Nullable
        Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
            return "default value for empty body";
        }
    }

    interface MappingInterface<A> {
        default A handle(@RequestBody
        A arg) {
            return arg;
        }
    }

    static class MyControllerImplementingInterface implements RequestResponseBodyMethodProcessorTests.MappingInterface<String> {}
}

