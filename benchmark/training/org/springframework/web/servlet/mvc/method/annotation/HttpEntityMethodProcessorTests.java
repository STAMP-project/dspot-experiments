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
package org.springframework.web.servlet.mvc.method.annotation;


import MediaType.APPLICATION_JSON_VALUE;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;


/**
 * Test fixture with {@link HttpEntityMethodProcessor} delegating to
 * actual {@link HttpMessageConverter} instances.
 *
 * <p>Also see {@link HttpEntityMethodProcessorMockTests}.
 *
 * @author Rossen Stoyanchev
 */
@SuppressWarnings("unused")
public class HttpEntityMethodProcessorTests {
    private MethodParameter paramList;

    private MethodParameter paramSimpleBean;

    private ModelAndViewContainer mavContainer;

    private WebDataBinderFactory binderFactory;

    private MockHttpServletRequest servletRequest;

    private ServletWebRequest webRequest;

    private MockHttpServletResponse servletResponse;

    @Test
    public void resolveArgument() throws Exception {
        String content = "{\"name\" : \"Jad\"}";
        this.servletRequest.setContent(content.getBytes("UTF-8"));
        this.servletRequest.setContentType("application/json");
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        HttpEntityMethodProcessor processor = new HttpEntityMethodProcessor(converters);
        @SuppressWarnings("unchecked")
        HttpEntity<HttpEntityMethodProcessorTests.SimpleBean> result = ((HttpEntity<HttpEntityMethodProcessorTests.SimpleBean>) (processor.resolveArgument(paramSimpleBean, mavContainer, webRequest, binderFactory)));
        Assert.assertNotNull(result);
        Assert.assertEquals("Jad", result.getBody().getName());
    }

    // SPR-12861
    @Test
    public void resolveArgumentWithEmptyBody() throws Exception {
        this.servletRequest.setContent(new byte[0]);
        this.servletRequest.setContentType("application/json");
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        HttpEntityMethodProcessor processor = new HttpEntityMethodProcessor(converters);
        HttpEntity<?> result = ((HttpEntity<?>) (processor.resolveArgument(this.paramSimpleBean, this.mavContainer, this.webRequest, this.binderFactory)));
        Assert.assertNotNull(result);
        Assert.assertNull(result.getBody());
    }

    @Test
    public void resolveGenericArgument() throws Exception {
        String content = "[{\"name\" : \"Jad\"}, {\"name\" : \"Robert\"}]";
        this.servletRequest.setContent(content.getBytes("UTF-8"));
        this.servletRequest.setContentType("application/json");
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        HttpEntityMethodProcessor processor = new HttpEntityMethodProcessor(converters);
        @SuppressWarnings("unchecked")
        HttpEntity<List<HttpEntityMethodProcessorTests.SimpleBean>> result = ((HttpEntity<List<HttpEntityMethodProcessorTests.SimpleBean>>) (processor.resolveArgument(paramList, mavContainer, webRequest, binderFactory)));
        Assert.assertNotNull(result);
        Assert.assertEquals("Jad", result.getBody().get(0).getName());
        Assert.assertEquals("Robert", result.getBody().get(1).getName());
    }

    @Test
    public void resolveArgumentTypeVariable() throws Exception {
        Method method = HttpEntityMethodProcessorTests.MySimpleParameterizedController.class.getMethod("handleDto", HttpEntity.class);
        HandlerMethod handlerMethod = new HandlerMethod(new HttpEntityMethodProcessorTests.MySimpleParameterizedController(), method);
        MethodParameter methodParam = handlerMethod.getMethodParameters()[0];
        String content = "{\"name\" : \"Jad\"}";
        this.servletRequest.setContent(content.getBytes("UTF-8"));
        this.servletRequest.setContentType(APPLICATION_JSON_VALUE);
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        HttpEntityMethodProcessor processor = new HttpEntityMethodProcessor(converters);
        @SuppressWarnings("unchecked")
        HttpEntity<HttpEntityMethodProcessorTests.SimpleBean> result = ((HttpEntity<HttpEntityMethodProcessorTests.SimpleBean>) (processor.resolveArgument(methodParam, mavContainer, webRequest, binderFactory)));
        Assert.assertNotNull(result);
        Assert.assertEquals("Jad", result.getBody().getName());
    }

    // SPR-12811
    @Test
    public void jacksonTypeInfoList() throws Exception {
        Method method = HttpEntityMethodProcessorTests.JacksonController.class.getMethod("handleList");
        HandlerMethod handlerMethod = new HandlerMethod(new HttpEntityMethodProcessorTests.JacksonController(), method);
        MethodParameter methodReturnType = handlerMethod.getReturnType();
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());
        HttpEntityMethodProcessor processor = new HttpEntityMethodProcessor(converters);
        Object returnValue = new HttpEntityMethodProcessorTests.JacksonController().handleList();
        processor.handleReturnValue(returnValue, methodReturnType, this.mavContainer, this.webRequest);
        String content = this.servletResponse.getContentAsString();
        Assert.assertTrue(content.contains("\"type\":\"foo\""));
        Assert.assertTrue(content.contains("\"type\":\"bar\""));
    }

    // SPR-13423
    @Test
    public void handleReturnValueCharSequence() throws Exception {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new StringHttpMessageConverter());
        Method method = getClass().getDeclaredMethod("handle");
        MethodParameter returnType = new MethodParameter(method, (-1));
        ResponseEntity<StringBuilder> returnValue = ResponseEntity.ok(new StringBuilder("Foo"));
        HttpEntityMethodProcessor processor = new HttpEntityMethodProcessor(converters);
        processor.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        Assert.assertEquals("text/plain;charset=ISO-8859-1", servletResponse.getHeader("Content-Type"));
        Assert.assertEquals("Foo", servletResponse.getContentAsString());
    }

    @SuppressWarnings("unused")
    private abstract static class MyParameterizedController<DTO extends HttpEntityMethodProcessorTests.Identifiable> {
        public void handleDto(HttpEntity<DTO> dto) {
        }
    }

    @SuppressWarnings("unused")
    private static class MySimpleParameterizedController extends HttpEntityMethodProcessorTests.MyParameterizedController<HttpEntityMethodProcessorTests.SimpleBean> {}

    private interface Identifiable extends Serializable {
        Long getId();

        void setId(Long id);
    }

    @SuppressWarnings({ "serial" })
    private static class SimpleBean implements HttpEntityMethodProcessorTests.Identifiable {
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

        @SuppressWarnings("unused")
        public void setName(String name) {
            this.name = name;
        }
    }

    private final class ValidatingBinderFactory implements WebDataBinderFactory {
        @Override
        public WebDataBinder createBinder(NativeWebRequest webRequest, @Nullable
        Object target, String objectName) {
            LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
            validator.afterPropertiesSet();
            WebDataBinder dataBinder = new WebDataBinder(target, objectName);
            dataBinder.setValidator(validator);
            return dataBinder;
        }
    }

    @JsonTypeInfo(use = NAME, include = PROPERTY, property = "type")
    private static class ParentClass {
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
    private static class Foo extends HttpEntityMethodProcessorTests.ParentClass {
        public Foo() {
        }

        public Foo(String parentProperty) {
            super(parentProperty);
        }
    }

    @JsonTypeName("bar")
    private static class Bar extends HttpEntityMethodProcessorTests.ParentClass {
        public Bar() {
        }

        public Bar(String parentProperty) {
            super(parentProperty);
        }
    }

    private static class JacksonController {
        @RequestMapping
        @ResponseBody
        public HttpEntity<List<HttpEntityMethodProcessorTests.ParentClass>> handleList() {
            List<HttpEntityMethodProcessorTests.ParentClass> list = new ArrayList<>();
            list.add(new HttpEntityMethodProcessorTests.Foo("foo"));
            list.add(new HttpEntityMethodProcessorTests.Bar("bar"));
            return new HttpEntity(list);
        }
    }
}

