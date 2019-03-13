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
package org.springframework.web.method.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.bind.support.WebRequestDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;


/**
 * Test fixture with {@link ModelAttributeMethodProcessor}.
 *
 * @author Rossen Stoyanchev
 */
public class ModelAttributeMethodProcessorTests {
    private NativeWebRequest request;

    private ModelAndViewContainer container;

    private ModelAttributeMethodProcessor processor;

    private MethodParameter paramNamedValidModelAttr;

    private MethodParameter paramErrors;

    private MethodParameter paramInt;

    private MethodParameter paramModelAttr;

    private MethodParameter paramBindingDisabledAttr;

    private MethodParameter paramNonSimpleType;

    private MethodParameter returnParamNamedModelAttr;

    private MethodParameter returnParamNonSimpleType;

    @Test
    public void supportedParameters() throws Exception {
        Assert.assertTrue(this.processor.supportsParameter(this.paramNamedValidModelAttr));
        Assert.assertTrue(this.processor.supportsParameter(this.paramModelAttr));
        Assert.assertFalse(this.processor.supportsParameter(this.paramErrors));
        Assert.assertFalse(this.processor.supportsParameter(this.paramInt));
        Assert.assertFalse(this.processor.supportsParameter(this.paramNonSimpleType));
    }

    @Test
    public void supportedParametersInDefaultResolutionMode() throws Exception {
        processor = new ModelAttributeMethodProcessor(true);
        // Only non-simple types, even if not annotated
        Assert.assertTrue(this.processor.supportsParameter(this.paramNamedValidModelAttr));
        Assert.assertTrue(this.processor.supportsParameter(this.paramErrors));
        Assert.assertTrue(this.processor.supportsParameter(this.paramModelAttr));
        Assert.assertTrue(this.processor.supportsParameter(this.paramNonSimpleType));
        Assert.assertFalse(this.processor.supportsParameter(this.paramInt));
    }

    @Test
    public void supportedReturnTypes() throws Exception {
        processor = new ModelAttributeMethodProcessor(false);
        Assert.assertTrue(this.processor.supportsReturnType(returnParamNamedModelAttr));
        Assert.assertFalse(this.processor.supportsReturnType(returnParamNonSimpleType));
    }

    @Test
    public void supportedReturnTypesInDefaultResolutionMode() throws Exception {
        processor = new ModelAttributeMethodProcessor(true);
        Assert.assertTrue(this.processor.supportsReturnType(returnParamNamedModelAttr));
        Assert.assertTrue(this.processor.supportsReturnType(returnParamNonSimpleType));
    }

    @Test
    public void bindExceptionRequired() throws Exception {
        Assert.assertTrue(this.processor.isBindExceptionRequired(null, this.paramNonSimpleType));
        Assert.assertFalse(this.processor.isBindExceptionRequired(null, this.paramNamedValidModelAttr));
    }

    @Test
    public void resolveArgumentFromModel() throws Exception {
        testGetAttributeFromModel("attrName", this.paramNamedValidModelAttr);
        testGetAttributeFromModel("testBean", this.paramModelAttr);
        testGetAttributeFromModel("testBean", this.paramNonSimpleType);
    }

    @Test
    public void resolveArgumentViaDefaultConstructor() throws Exception {
        WebDataBinder dataBinder = new WebRequestDataBinder(null);
        WebDataBinderFactory factory = Mockito.mock(WebDataBinderFactory.class);
        BDDMockito.given(factory.createBinder(ArgumentMatchers.any(), ArgumentMatchers.notNull(), ArgumentMatchers.eq("attrName"))).willReturn(dataBinder);
        this.processor.resolveArgument(this.paramNamedValidModelAttr, this.container, this.request, factory);
        Mockito.verify(factory).createBinder(ArgumentMatchers.any(), ArgumentMatchers.notNull(), ArgumentMatchers.eq("attrName"));
    }

    @Test
    public void resolveArgumentValidation() throws Exception {
        String name = "attrName";
        Object target = new TestBean();
        this.container.addAttribute(name, target);
        ModelAttributeMethodProcessorTests.StubRequestDataBinder dataBinder = new ModelAttributeMethodProcessorTests.StubRequestDataBinder(target, name);
        WebDataBinderFactory factory = Mockito.mock(WebDataBinderFactory.class);
        BDDMockito.given(factory.createBinder(this.request, target, name)).willReturn(dataBinder);
        this.processor.resolveArgument(this.paramNamedValidModelAttr, this.container, this.request, factory);
        Assert.assertTrue(dataBinder.isBindInvoked());
        Assert.assertTrue(dataBinder.isValidateInvoked());
    }

    @Test
    public void resolveArgumentBindingDisabledPreviously() throws Exception {
        String name = "attrName";
        Object target = new TestBean();
        this.container.addAttribute(name, target);
        // Declare binding disabled (e.g. via @ModelAttribute method)
        this.container.setBindingDisabled(name);
        ModelAttributeMethodProcessorTests.StubRequestDataBinder dataBinder = new ModelAttributeMethodProcessorTests.StubRequestDataBinder(target, name);
        WebDataBinderFactory factory = Mockito.mock(WebDataBinderFactory.class);
        BDDMockito.given(factory.createBinder(this.request, target, name)).willReturn(dataBinder);
        this.processor.resolveArgument(this.paramNamedValidModelAttr, this.container, this.request, factory);
        Assert.assertFalse(dataBinder.isBindInvoked());
        Assert.assertTrue(dataBinder.isValidateInvoked());
    }

    @Test
    public void resolveArgumentBindingDisabled() throws Exception {
        String name = "noBindAttr";
        Object target = new TestBean();
        this.container.addAttribute(name, target);
        ModelAttributeMethodProcessorTests.StubRequestDataBinder dataBinder = new ModelAttributeMethodProcessorTests.StubRequestDataBinder(target, name);
        WebDataBinderFactory factory = Mockito.mock(WebDataBinderFactory.class);
        BDDMockito.given(factory.createBinder(this.request, target, name)).willReturn(dataBinder);
        this.processor.resolveArgument(this.paramBindingDisabledAttr, this.container, this.request, factory);
        Assert.assertFalse(dataBinder.isBindInvoked());
        Assert.assertTrue(dataBinder.isValidateInvoked());
    }

    @Test(expected = BindException.class)
    public void resolveArgumentBindException() throws Exception {
        String name = "testBean";
        Object target = new TestBean();
        this.container.getModel().addAttribute(target);
        ModelAttributeMethodProcessorTests.StubRequestDataBinder dataBinder = new ModelAttributeMethodProcessorTests.StubRequestDataBinder(target, name);
        getBindingResult().reject("error");
        WebDataBinderFactory binderFactory = Mockito.mock(WebDataBinderFactory.class);
        BDDMockito.given(binderFactory.createBinder(this.request, target, name)).willReturn(dataBinder);
        this.processor.resolveArgument(this.paramNonSimpleType, this.container, this.request, binderFactory);
        Mockito.verify(binderFactory).createBinder(this.request, target, name);
    }

    // SPR-9378
    @Test
    public void resolveArgumentOrdering() throws Exception {
        String name = "testBean";
        Object testBean = new TestBean(name);
        this.container.addAttribute(name, testBean);
        this.container.addAttribute(((BindingResult.MODEL_KEY_PREFIX) + name), testBean);
        Object anotherTestBean = new TestBean();
        this.container.addAttribute("anotherTestBean", anotherTestBean);
        ModelAttributeMethodProcessorTests.StubRequestDataBinder dataBinder = new ModelAttributeMethodProcessorTests.StubRequestDataBinder(testBean, name);
        WebDataBinderFactory binderFactory = Mockito.mock(WebDataBinderFactory.class);
        BDDMockito.given(binderFactory.createBinder(this.request, testBean, name)).willReturn(dataBinder);
        this.processor.resolveArgument(this.paramModelAttr, this.container, this.request, binderFactory);
        Object[] values = this.container.getModel().values().toArray();
        Assert.assertSame("Resolved attribute should be updated to be last", testBean, values[1]);
        Assert.assertSame("BindingResult of resolved attr should be last", getBindingResult(), values[2]);
    }

    @Test
    public void handleAnnotatedReturnValue() throws Exception {
        this.processor.handleReturnValue("expected", this.returnParamNamedModelAttr, this.container, this.request);
        Assert.assertEquals("expected", this.container.getModel().get("modelAttrName"));
    }

    @Test
    public void handleNotAnnotatedReturnValue() throws Exception {
        TestBean testBean = new TestBean("expected");
        this.processor.handleReturnValue(testBean, this.returnParamNonSimpleType, this.container, this.request);
        Assert.assertSame(testBean, this.container.getModel().get("testBean"));
    }

    private static class StubRequestDataBinder extends WebRequestDataBinder {
        private boolean bindInvoked;

        private boolean validateInvoked;

        public StubRequestDataBinder(Object target, String objectName) {
            super(target, objectName);
        }

        public boolean isBindInvoked() {
            return bindInvoked;
        }

        public boolean isValidateInvoked() {
            return validateInvoked;
        }

        @Override
        public void bind(WebRequest request) {
            bindInvoked = true;
        }

        @Override
        public void validate() {
            validateInvoked = true;
        }

        @Override
        public void validate(Object... validationHints) {
            validateInvoked = true;
        }
    }

    @Target({ ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Valid {}

    @SessionAttributes(types = TestBean.class)
    private static class ModelAttributeHandler {
        @SuppressWarnings("unused")
        public void modelAttribute(@ModelAttribute("attrName")
        @ModelAttributeMethodProcessorTests.Valid
        TestBean annotatedAttr, Errors errors, int intArg, @ModelAttribute
        TestBean defaultNameAttr, @ModelAttribute(name = "noBindAttr", binding = false)
        @ModelAttributeMethodProcessorTests.Valid
        TestBean noBindAttr, TestBean notAnnotatedAttr) {
        }
    }
}

