/**
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.web.servlet.mvc.annotation;


import java.io.IOException;
import java.io.Writer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.DispatcherServlet;


/**
 *
 *
 * @author Arjen Poutsma
 * @since 3.0
 */
public class JdkProxyControllerTests {
    private DispatcherServlet servlet;

    @Test
    public void typeLevel() throws Exception {
        initServlet(JdkProxyControllerTests.TypeLevelImpl.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.service(request, response);
        Assert.assertEquals("doIt", response.getContentAsString());
    }

    @Test
    public void methodLevel() throws Exception {
        initServlet(JdkProxyControllerTests.MethodLevelImpl.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.service(request, response);
        Assert.assertEquals("doIt", response.getContentAsString());
    }

    @Test
    public void typeAndMethodLevel() throws Exception {
        initServlet(JdkProxyControllerTests.TypeAndMethodLevelImpl.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/hotels/bookings");
        MockHttpServletResponse response = new MockHttpServletResponse();
        servlet.service(request, response);
        Assert.assertEquals("doIt", response.getContentAsString());
    }

    @Controller
    @RequestMapping("/test")
    public interface TypeLevel {
        @RequestMapping
        void doIt(Writer writer) throws IOException;
    }

    public static class TypeLevelImpl implements JdkProxyControllerTests.TypeLevel {
        @Override
        public void doIt(Writer writer) throws IOException {
            writer.write("doIt");
        }
    }

    @Controller
    public interface MethodLevel {
        @RequestMapping("/test")
        void doIt(Writer writer) throws IOException;
    }

    public static class MethodLevelImpl implements JdkProxyControllerTests.MethodLevel {
        @Override
        public void doIt(Writer writer) throws IOException {
            writer.write("doIt");
        }
    }

    @Controller
    @RequestMapping("/hotels")
    public interface TypeAndMethodLevel {
        @RequestMapping("/bookings")
        void doIt(Writer writer) throws IOException;
    }

    public static class TypeAndMethodLevelImpl implements JdkProxyControllerTests.TypeAndMethodLevel {
        @Override
        public void doIt(Writer writer) throws IOException {
            writer.write("doIt");
        }
    }
}

