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
package org.springframework.web.servlet.config.annotation;


import HttpStatus.NOT_FOUND;
import HttpStatus.PERMANENT_REDIRECT;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ParameterizableViewController;
import org.springframework.web.servlet.view.RedirectView;


/**
 * Test fixture with a {@link ViewControllerRegistry}.
 *
 * @author Rossen Stoyanchev
 */
public class ViewControllerRegistryTests {
    private ViewControllerRegistry registry;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Test
    public void noViewControllers() {
        Assert.assertNull(this.registry.buildHandlerMapping());
    }

    @Test
    public void addViewController() {
        this.registry.addViewController("/path").setViewName("viewName");
        ParameterizableViewController controller = getController("/path");
        Assert.assertEquals("viewName", controller.getViewName());
        Assert.assertNull(controller.getStatusCode());
        Assert.assertFalse(controller.isStatusOnly());
        Assert.assertNotNull(controller.getApplicationContext());
    }

    @Test
    public void addViewControllerWithDefaultViewName() {
        this.registry.addViewController("/path");
        ParameterizableViewController controller = getController("/path");
        Assert.assertNull(controller.getViewName());
        Assert.assertNull(controller.getStatusCode());
        Assert.assertFalse(controller.isStatusOnly());
        Assert.assertNotNull(controller.getApplicationContext());
    }

    @Test
    public void addRedirectViewController() throws Exception {
        this.registry.addRedirectViewController("/path", "/redirectTo");
        RedirectView redirectView = getRedirectView("/path");
        this.request.setQueryString("a=b");
        this.request.setContextPath("/context");
        redirectView.render(Collections.emptyMap(), this.request, this.response);
        Assert.assertEquals(302, this.response.getStatus());
        Assert.assertEquals("/context/redirectTo", this.response.getRedirectedUrl());
        Assert.assertNotNull(redirectView.getApplicationContext());
    }

    @Test
    public void addRedirectViewControllerWithCustomSettings() throws Exception {
        this.registry.addRedirectViewController("/path", "/redirectTo").setContextRelative(false).setKeepQueryParams(true).setStatusCode(PERMANENT_REDIRECT);
        RedirectView redirectView = getRedirectView("/path");
        this.request.setQueryString("a=b");
        this.request.setContextPath("/context");
        redirectView.render(Collections.emptyMap(), this.request, this.response);
        Assert.assertEquals(308, this.response.getStatus());
        Assert.assertEquals("/redirectTo?a=b", response.getRedirectedUrl());
        Assert.assertNotNull(redirectView.getApplicationContext());
    }

    @Test
    public void addStatusController() {
        this.registry.addStatusController("/path", NOT_FOUND);
        ParameterizableViewController controller = getController("/path");
        Assert.assertNull(controller.getViewName());
        Assert.assertEquals(NOT_FOUND, controller.getStatusCode());
        Assert.assertTrue(controller.isStatusOnly());
        Assert.assertNotNull(controller.getApplicationContext());
    }

    @Test
    public void order() {
        this.registry.addViewController("/path");
        SimpleUrlHandlerMapping handlerMapping = this.registry.buildHandlerMapping();
        Assert.assertEquals(1, handlerMapping.getOrder());
        this.registry.setOrder(2);
        handlerMapping = this.registry.buildHandlerMapping();
        Assert.assertEquals(2, handlerMapping.getOrder());
    }
}

