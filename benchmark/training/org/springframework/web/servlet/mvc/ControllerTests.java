/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.web.servlet.mvc;


import java.util.Properties;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;


/**
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class ControllerTests {
    @Test
    public void parameterizableViewController() throws Exception {
        String viewName = "viewName";
        ParameterizableViewController pvc = new ParameterizableViewController();
        pvc.setViewName(viewName);
        // We don't care about the params.
        ModelAndView mv = pvc.handleRequest(new MockHttpServletRequest("GET", "foo.html"), new MockHttpServletResponse());
        Assert.assertTrue("model has no data", ((mv.getModel().size()) == 0));
        Assert.assertTrue("model has correct viewname", mv.getViewName().equals(viewName));
        Assert.assertTrue("getViewName matches", pvc.getViewName().equals(viewName));
    }

    @Test
    public void servletForwardingController() throws Exception {
        ServletForwardingController sfc = new ServletForwardingController();
        sfc.setServletName("action");
        doTestServletForwardingController(sfc, false);
    }

    @Test
    public void servletForwardingControllerWithInclude() throws Exception {
        ServletForwardingController sfc = new ServletForwardingController();
        sfc.setServletName("action");
        doTestServletForwardingController(sfc, true);
    }

    @Test
    public void servletForwardingControllerWithBeanName() throws Exception {
        ServletForwardingController sfc = new ServletForwardingController();
        sfc.setBeanName("action");
        doTestServletForwardingController(sfc, false);
    }

    @Test
    public void servletWrappingController() throws Exception {
        HttpServletRequest request = new MockHttpServletRequest("GET", "/somePath");
        HttpServletResponse response = new MockHttpServletResponse();
        ServletWrappingController swc = new ServletWrappingController();
        swc.setServletClass(ControllerTests.TestServlet.class);
        swc.setServletName("action");
        Properties props = new Properties();
        props.setProperty("config", "myValue");
        swc.setInitParameters(props);
        swc.afterPropertiesSet();
        Assert.assertNotNull(ControllerTests.TestServlet.config);
        Assert.assertEquals("action", ControllerTests.TestServlet.config.getServletName());
        Assert.assertEquals("myValue", ControllerTests.TestServlet.config.getInitParameter("config"));
        Assert.assertNull(ControllerTests.TestServlet.request);
        Assert.assertFalse(ControllerTests.TestServlet.destroyed);
        Assert.assertNull(swc.handleRequest(request, response));
        Assert.assertEquals(request, ControllerTests.TestServlet.request);
        Assert.assertEquals(response, ControllerTests.TestServlet.response);
        Assert.assertFalse(ControllerTests.TestServlet.destroyed);
        swc.destroy();
        Assert.assertTrue(ControllerTests.TestServlet.destroyed);
    }

    @Test
    public void servletWrappingControllerWithBeanName() throws Exception {
        HttpServletRequest request = new MockHttpServletRequest("GET", "/somePath");
        HttpServletResponse response = new MockHttpServletResponse();
        ServletWrappingController swc = new ServletWrappingController();
        swc.setServletClass(ControllerTests.TestServlet.class);
        swc.setBeanName("action");
        swc.afterPropertiesSet();
        Assert.assertNotNull(ControllerTests.TestServlet.config);
        Assert.assertEquals("action", ControllerTests.TestServlet.config.getServletName());
        Assert.assertNull(ControllerTests.TestServlet.request);
        Assert.assertFalse(ControllerTests.TestServlet.destroyed);
        Assert.assertNull(swc.handleRequest(request, response));
        Assert.assertEquals(request, ControllerTests.TestServlet.request);
        Assert.assertEquals(response, ControllerTests.TestServlet.response);
        Assert.assertFalse(ControllerTests.TestServlet.destroyed);
        swc.destroy();
        Assert.assertTrue(ControllerTests.TestServlet.destroyed);
    }

    public static class TestServlet implements Servlet {
        private static ServletConfig config;

        private static ServletRequest request;

        private static ServletResponse response;

        private static boolean destroyed;

        public TestServlet() {
            ControllerTests.TestServlet.config = null;
            ControllerTests.TestServlet.request = null;
            ControllerTests.TestServlet.response = null;
            ControllerTests.TestServlet.destroyed = false;
        }

        @Override
        public void init(ServletConfig servletConfig) {
            ControllerTests.TestServlet.config = servletConfig;
        }

        @Override
        public ServletConfig getServletConfig() {
            return ControllerTests.TestServlet.config;
        }

        @Override
        public void service(ServletRequest servletRequest, ServletResponse servletResponse) {
            ControllerTests.TestServlet.request = servletRequest;
            ControllerTests.TestServlet.response = servletResponse;
        }

        @Override
        public String getServletInfo() {
            return "TestServlet";
        }

        @Override
        public void destroy() {
            ControllerTests.TestServlet.destroyed = true;
        }
    }
}

