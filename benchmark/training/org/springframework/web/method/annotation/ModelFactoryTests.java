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


import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.ModelAndViewContainer;


/**
 * Text fixture for {@link ModelFactory} tests.
 *
 * @author Rossen Stoyanchev
 */
public class ModelFactoryTests {
    private NativeWebRequest webRequest;

    private SessionAttributesHandler attributeHandler;

    private SessionAttributeStore attributeStore;

    private ModelFactoryTests.TestController controller = new ModelFactoryTests.TestController();

    private ModelAndViewContainer mavContainer;

    @Test
    public void modelAttributeMethod() throws Exception {
        ModelFactory modelFactory = createModelFactory("modelAttr", Model.class);
        HandlerMethod handlerMethod = createHandlerMethod("handle");
        modelFactory.initModel(this.webRequest, this.mavContainer, handlerMethod);
        Assert.assertEquals(Boolean.TRUE, this.mavContainer.getModel().get("modelAttr"));
    }

    @Test
    public void modelAttributeMethodWithExplicitName() throws Exception {
        ModelFactory modelFactory = createModelFactory("modelAttrWithName");
        HandlerMethod handlerMethod = createHandlerMethod("handle");
        modelFactory.initModel(this.webRequest, this.mavContainer, handlerMethod);
        Assert.assertEquals(Boolean.TRUE, this.mavContainer.getModel().get("name"));
    }

    @Test
    public void modelAttributeMethodWithNameByConvention() throws Exception {
        ModelFactory modelFactory = createModelFactory("modelAttrConvention");
        HandlerMethod handlerMethod = createHandlerMethod("handle");
        modelFactory.initModel(this.webRequest, this.mavContainer, handlerMethod);
        Assert.assertEquals(Boolean.TRUE, this.mavContainer.getModel().get("boolean"));
    }

    @Test
    public void modelAttributeMethodWithNullReturnValue() throws Exception {
        ModelFactory modelFactory = createModelFactory("nullModelAttr");
        HandlerMethod handlerMethod = createHandlerMethod("handle");
        modelFactory.initModel(this.webRequest, this.mavContainer, handlerMethod);
        Assert.assertTrue(this.mavContainer.containsAttribute("name"));
        Assert.assertNull(this.mavContainer.getModel().get("name"));
    }

    @Test
    public void modelAttributeWithBindingDisabled() throws Exception {
        ModelFactory modelFactory = createModelFactory("modelAttrWithBindingDisabled");
        HandlerMethod handlerMethod = createHandlerMethod("handle");
        modelFactory.initModel(this.webRequest, this.mavContainer, handlerMethod);
        Assert.assertTrue(this.mavContainer.containsAttribute("foo"));
        Assert.assertTrue(this.mavContainer.isBindingDisabled("foo"));
    }

    @Test
    public void modelAttributeFromSessionWithBindingDisabled() throws Exception {
        ModelFactoryTests.Foo foo = new ModelFactoryTests.Foo();
        this.attributeStore.storeAttribute(this.webRequest, "foo", foo);
        ModelFactory modelFactory = createModelFactory("modelAttrWithBindingDisabled");
        HandlerMethod handlerMethod = createHandlerMethod("handle");
        modelFactory.initModel(this.webRequest, this.mavContainer, handlerMethod);
        Assert.assertTrue(this.mavContainer.containsAttribute("foo"));
        Assert.assertSame(foo, this.mavContainer.getModel().get("foo"));
        Assert.assertTrue(this.mavContainer.isBindingDisabled("foo"));
    }

    @Test
    public void sessionAttribute() throws Exception {
        this.attributeStore.storeAttribute(this.webRequest, "sessionAttr", "sessionAttrValue");
        ModelFactory modelFactory = createModelFactory("modelAttr", Model.class);
        HandlerMethod handlerMethod = createHandlerMethod("handle");
        modelFactory.initModel(this.webRequest, this.mavContainer, handlerMethod);
        Assert.assertEquals("sessionAttrValue", this.mavContainer.getModel().get("sessionAttr"));
    }

    @Test
    public void sessionAttributeNotPresent() throws Exception {
        ModelFactory modelFactory = new ModelFactory(null, null, this.attributeHandler);
        HandlerMethod handlerMethod = createHandlerMethod("handleSessionAttr", String.class);
        try {
            modelFactory.initModel(this.webRequest, this.mavContainer, handlerMethod);
            Assert.fail("Expected HttpSessionRequiredException");
        } catch (HttpSessionRequiredException ex) {
            // expected
        }
        // Now add attribute and try again
        this.attributeStore.storeAttribute(this.webRequest, "sessionAttr", "sessionAttrValue");
        modelFactory.initModel(this.webRequest, this.mavContainer, handlerMethod);
        Assert.assertEquals("sessionAttrValue", this.mavContainer.getModel().get("sessionAttr"));
    }

    @Test
    public void updateModelBindingResult() throws Exception {
        String commandName = "attr1";
        Object command = new Object();
        ModelAndViewContainer container = new ModelAndViewContainer();
        container.addAttribute(commandName, command);
        WebDataBinder dataBinder = new WebDataBinder(command, commandName);
        WebDataBinderFactory binderFactory = Mockito.mock(WebDataBinderFactory.class);
        BDDMockito.given(binderFactory.createBinder(this.webRequest, command, commandName)).willReturn(dataBinder);
        ModelFactory modelFactory = new ModelFactory(null, binderFactory, this.attributeHandler);
        modelFactory.updateModel(this.webRequest, container);
        Assert.assertEquals(command, container.getModel().get(commandName));
        String bindingResultKey = (BindingResult.MODEL_KEY_PREFIX) + commandName;
        Assert.assertSame(dataBinder.getBindingResult(), container.getModel().get(bindingResultKey));
        Assert.assertEquals(2, container.getModel().size());
    }

    @Test
    public void updateModelSessionAttributesSaved() throws Exception {
        String attributeName = "sessionAttr";
        String attribute = "value";
        ModelAndViewContainer container = new ModelAndViewContainer();
        container.addAttribute(attributeName, attribute);
        WebDataBinder dataBinder = new WebDataBinder(attribute, attributeName);
        WebDataBinderFactory binderFactory = Mockito.mock(WebDataBinderFactory.class);
        BDDMockito.given(binderFactory.createBinder(this.webRequest, attribute, attributeName)).willReturn(dataBinder);
        ModelFactory modelFactory = new ModelFactory(null, binderFactory, this.attributeHandler);
        modelFactory.updateModel(this.webRequest, container);
        Assert.assertEquals(attribute, container.getModel().get(attributeName));
        Assert.assertEquals(attribute, this.attributeStore.retrieveAttribute(this.webRequest, attributeName));
    }

    @Test
    public void updateModelSessionAttributesRemoved() throws Exception {
        String attributeName = "sessionAttr";
        String attribute = "value";
        ModelAndViewContainer container = new ModelAndViewContainer();
        container.addAttribute(attributeName, attribute);
        this.attributeStore.storeAttribute(this.webRequest, attributeName, attribute);
        WebDataBinder dataBinder = new WebDataBinder(attribute, attributeName);
        WebDataBinderFactory binderFactory = Mockito.mock(WebDataBinderFactory.class);
        BDDMockito.given(binderFactory.createBinder(this.webRequest, attribute, attributeName)).willReturn(dataBinder);
        container.getSessionStatus().setComplete();
        ModelFactory modelFactory = new ModelFactory(null, binderFactory, this.attributeHandler);
        modelFactory.updateModel(this.webRequest, container);
        Assert.assertEquals(attribute, container.getModel().get(attributeName));
        Assert.assertNull(this.attributeStore.retrieveAttribute(this.webRequest, attributeName));
    }

    // SPR-12542
    @Test
    public void updateModelWhenRedirecting() throws Exception {
        String attributeName = "sessionAttr";
        String attribute = "value";
        ModelAndViewContainer container = new ModelAndViewContainer();
        container.addAttribute(attributeName, attribute);
        String queryParam = "123";
        String queryParamName = "q";
        container.setRedirectModel(new ModelMap(queryParamName, queryParam));
        container.setRedirectModelScenario(true);
        WebDataBinder dataBinder = new WebDataBinder(attribute, attributeName);
        WebDataBinderFactory binderFactory = Mockito.mock(WebDataBinderFactory.class);
        BDDMockito.given(binderFactory.createBinder(this.webRequest, attribute, attributeName)).willReturn(dataBinder);
        ModelFactory modelFactory = new ModelFactory(null, binderFactory, this.attributeHandler);
        modelFactory.updateModel(this.webRequest, container);
        Assert.assertEquals(queryParam, container.getModel().get(queryParamName));
        Assert.assertEquals(1, container.getModel().size());
        Assert.assertEquals(attribute, this.attributeStore.retrieveAttribute(this.webRequest, attributeName));
    }

    @SessionAttributes({ "sessionAttr", "foo" })
    static class TestController {
        @ModelAttribute
        public void modelAttr(Model model) {
            model.addAttribute("modelAttr", Boolean.TRUE);
        }

        @ModelAttribute("name")
        public Boolean modelAttrWithName() {
            return Boolean.TRUE;
        }

        @ModelAttribute
        public Boolean modelAttrConvention() {
            return Boolean.TRUE;
        }

        @ModelAttribute("name")
        public Boolean nullModelAttr() {
            return null;
        }

        @ModelAttribute(name = "foo", binding = false)
        public ModelFactoryTests.Foo modelAttrWithBindingDisabled() {
            return new ModelFactoryTests.Foo();
        }

        public void handle() {
        }

        public void handleSessionAttr(@ModelAttribute("sessionAttr")
        String sessionAttr) {
        }
    }

    private static class Foo {}
}

