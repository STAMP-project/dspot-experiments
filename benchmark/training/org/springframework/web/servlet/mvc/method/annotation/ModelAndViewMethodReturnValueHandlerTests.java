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


import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import org.springframework.web.servlet.view.RedirectView;


/**
 * Test fixture with {@link ModelAndViewMethodReturnValueHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class ModelAndViewMethodReturnValueHandlerTests {
    private ModelAndViewMethodReturnValueHandler handler;

    private ModelAndViewContainer mavContainer;

    private ServletWebRequest webRequest;

    private MethodParameter returnParamModelAndView;

    @Test
    public void supportsReturnType() throws Exception {
        Assert.assertTrue(handler.supportsReturnType(returnParamModelAndView));
        Assert.assertFalse(handler.supportsReturnType(getReturnValueParam("viewName")));
    }

    @Test
    public void handleViewReference() throws Exception {
        ModelAndView mav = new ModelAndView("viewName", "attrName", "attrValue");
        handler.handleReturnValue(mav, returnParamModelAndView, mavContainer, webRequest);
        Assert.assertEquals("viewName", mavContainer.getView());
        Assert.assertEquals("attrValue", mavContainer.getModel().get("attrName"));
    }

    @Test
    public void handleViewInstance() throws Exception {
        ModelAndView mav = new ModelAndView(new RedirectView(), "attrName", "attrValue");
        handler.handleReturnValue(mav, returnParamModelAndView, mavContainer, webRequest);
        Assert.assertEquals(RedirectView.class, mavContainer.getView().getClass());
        Assert.assertEquals("attrValue", mavContainer.getModel().get("attrName"));
    }

    @Test
    public void handleNull() throws Exception {
        handler.handleReturnValue(null, returnParamModelAndView, mavContainer, webRequest);
        Assert.assertTrue(mavContainer.isRequestHandled());
    }

    @Test
    public void handleRedirectAttributesWithViewReference() throws Exception {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        mavContainer.setRedirectModel(redirectAttributes);
        ModelAndView mav = new ModelAndView(new RedirectView(), "attrName", "attrValue");
        handler.handleReturnValue(mav, returnParamModelAndView, mavContainer, webRequest);
        Assert.assertEquals(RedirectView.class, mavContainer.getView().getClass());
        Assert.assertEquals("attrValue", mavContainer.getModel().get("attrName"));
        Assert.assertSame("RedirectAttributes should be used if controller redirects", redirectAttributes, mavContainer.getModel());
    }

    @Test
    public void handleRedirectAttributesWithViewName() throws Exception {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        mavContainer.setRedirectModel(redirectAttributes);
        ModelAndView mav = new ModelAndView("redirect:viewName", "attrName", "attrValue");
        handler.handleReturnValue(mav, returnParamModelAndView, mavContainer, webRequest);
        ModelMap model = mavContainer.getModel();
        Assert.assertEquals("redirect:viewName", mavContainer.getViewName());
        Assert.assertEquals("attrValue", model.get("attrName"));
        Assert.assertSame(redirectAttributes, model);
    }

    @Test
    public void handleRedirectAttributesWithCustomPrefix() throws Exception {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        mavContainer.setRedirectModel(redirectAttributes);
        ModelAndView mav = new ModelAndView("myRedirect:viewName", "attrName", "attrValue");
        handler.setRedirectPatterns("myRedirect:*");
        handler.handleReturnValue(mav, returnParamModelAndView, mavContainer, webRequest);
        ModelMap model = mavContainer.getModel();
        Assert.assertEquals("myRedirect:viewName", mavContainer.getViewName());
        Assert.assertEquals("attrValue", model.get("attrName"));
        Assert.assertSame(redirectAttributes, model);
    }

    @Test
    public void handleRedirectAttributesWithoutRedirect() throws Exception {
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();
        mavContainer.setRedirectModel(redirectAttributes);
        ModelAndView mav = new ModelAndView();
        handler.handleReturnValue(mav, returnParamModelAndView, mavContainer, webRequest);
        ModelMap model = mavContainer.getModel();
        Assert.assertEquals(null, mavContainer.getView());
        Assert.assertTrue(mavContainer.getModel().isEmpty());
        Assert.assertNotSame("RedirectAttributes should not be used if controller doesn't redirect", redirectAttributes, model);
    }

    // SPR-14045
    @Test
    public void handleRedirectWithIgnoreDefaultModel() throws Exception {
        mavContainer.setIgnoreDefaultModelOnRedirect(true);
        RedirectView redirectView = new RedirectView();
        ModelAndView mav = new ModelAndView(redirectView, "name", "value");
        handler.handleReturnValue(mav, returnParamModelAndView, mavContainer, webRequest);
        ModelMap model = mavContainer.getModel();
        Assert.assertSame(redirectView, mavContainer.getView());
        Assert.assertEquals(1, model.size());
        Assert.assertEquals("value", model.get("name"));
    }
}

