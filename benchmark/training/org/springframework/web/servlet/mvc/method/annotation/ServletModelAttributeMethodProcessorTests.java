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


import HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;


/**
 * Test fixture for {@link ServletModelAttributeMethodProcessor} specific tests.
 * Also see org.springframework.web.method.annotation.support.ModelAttributeMethodProcessorTests
 *
 * @author Rossen Stoyanchev
 */
public class ServletModelAttributeMethodProcessorTests {
    private ServletModelAttributeMethodProcessor processor;

    private WebDataBinderFactory binderFactory;

    private ModelAndViewContainer mavContainer;

    private MockHttpServletRequest request;

    private NativeWebRequest webRequest;

    private MethodParameter testBeanModelAttr;

    private MethodParameter testBeanWithoutStringConstructorModelAttr;

    private MethodParameter testBeanWithOptionalModelAttr;

    @Test
    public void createAttributeUriTemplateVar() throws Exception {
        Map<String, String> uriTemplateVars = new HashMap<>();
        uriTemplateVars.put("testBean1", "Patty");
        request.setAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVars);
        // Type conversion from "Patty" to TestBean via TestBean(String) constructor
        TestBean testBean = ((TestBean) (processor.resolveArgument(testBeanModelAttr, mavContainer, webRequest, binderFactory)));
        Assert.assertEquals("Patty", testBean.getName());
    }

    @Test
    public void createAttributeUriTemplateVarCannotConvert() throws Exception {
        Map<String, String> uriTemplateVars = new HashMap<>();
        uriTemplateVars.put("testBean2", "Patty");
        request.setAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVars);
        ServletModelAttributeMethodProcessorTests.TestBeanWithoutStringConstructor testBean = ((ServletModelAttributeMethodProcessorTests.TestBeanWithoutStringConstructor) (processor.resolveArgument(testBeanWithoutStringConstructorModelAttr, mavContainer, webRequest, binderFactory)));
        Assert.assertNotNull(testBean);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createAttributeUriTemplateVarWithOptional() throws Exception {
        Map<String, String> uriTemplateVars = new HashMap<>();
        uriTemplateVars.put("testBean3", "Patty");
        request.setAttribute(URI_TEMPLATE_VARIABLES_ATTRIBUTE, uriTemplateVars);
        // Type conversion from "Patty" to TestBean via TestBean(String) constructor
        Optional<TestBean> testBean = ((Optional<TestBean>) (processor.resolveArgument(testBeanWithOptionalModelAttr, mavContainer, webRequest, binderFactory)));
        Assert.assertEquals("Patty", testBean.get().getName());
    }

    @Test
    public void createAttributeRequestParameter() throws Exception {
        request.addParameter("testBean1", "Patty");
        // Type conversion from "Patty" to TestBean via TestBean(String) constructor
        TestBean testBean = ((TestBean) (processor.resolveArgument(testBeanModelAttr, mavContainer, webRequest, binderFactory)));
        Assert.assertEquals("Patty", testBean.getName());
    }

    @Test
    public void createAttributeRequestParameterCannotConvert() throws Exception {
        request.addParameter("testBean2", "Patty");
        ServletModelAttributeMethodProcessorTests.TestBeanWithoutStringConstructor testBean = ((ServletModelAttributeMethodProcessorTests.TestBeanWithoutStringConstructor) (processor.resolveArgument(testBeanWithoutStringConstructorModelAttr, mavContainer, webRequest, binderFactory)));
        Assert.assertNotNull(testBean);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void createAttributeRequestParameterWithOptional() throws Exception {
        request.addParameter("testBean3", "Patty");
        Optional<TestBean> testBean = ((Optional<TestBean>) (processor.resolveArgument(testBeanWithOptionalModelAttr, mavContainer, webRequest, binderFactory)));
        Assert.assertEquals("Patty", testBean.get().getName());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void attributesAsNullValues() throws Exception {
        request.addParameter("name", "Patty");
        mavContainer.getModel().put("testBean1", null);
        mavContainer.getModel().put("testBean2", null);
        mavContainer.getModel().put("testBean3", null);
        Assert.assertNull(processor.resolveArgument(testBeanModelAttr, mavContainer, webRequest, binderFactory));
        Assert.assertNull(processor.resolveArgument(testBeanWithoutStringConstructorModelAttr, mavContainer, webRequest, binderFactory));
        Optional<TestBean> testBean = ((Optional<TestBean>) (processor.resolveArgument(testBeanWithOptionalModelAttr, mavContainer, webRequest, binderFactory)));
        Assert.assertFalse(testBean.isPresent());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void attributesAsOptionalEmpty() throws Exception {
        request.addParameter("name", "Patty");
        mavContainer.getModel().put("testBean1", Optional.empty());
        mavContainer.getModel().put("testBean2", Optional.empty());
        mavContainer.getModel().put("testBean3", Optional.empty());
        Assert.assertNull(processor.resolveArgument(testBeanModelAttr, mavContainer, webRequest, binderFactory));
        Assert.assertNull(processor.resolveArgument(testBeanWithoutStringConstructorModelAttr, mavContainer, webRequest, binderFactory));
        Optional<TestBean> testBean = ((Optional<TestBean>) (processor.resolveArgument(testBeanWithOptionalModelAttr, mavContainer, webRequest, binderFactory)));
        Assert.assertFalse(testBean.isPresent());
    }

    @SuppressWarnings("unused")
    private static class TestBeanWithoutStringConstructor {
        public TestBeanWithoutStringConstructor() {
        }

        public TestBeanWithoutStringConstructor(int i) {
        }
    }
}

