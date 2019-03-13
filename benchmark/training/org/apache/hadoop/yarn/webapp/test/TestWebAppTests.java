/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.webapp.test;


import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.servlet.RequestScoped;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestWebAppTests {
    static final Logger LOG = LoggerFactory.getLogger(TestWebAppTests.class);

    @Test
    public void testInstances() throws Exception {
        Injector injector = WebAppTests.createMockInjector(this);
        HttpServletRequest req = injector.getInstance(HttpServletRequest.class);
        HttpServletResponse res = injector.getInstance(HttpServletResponse.class);
        String val = req.getParameter("foo");
        PrintWriter out = res.getWriter();
        out.println("Hello world!");
        logInstances(req, res, out);
        Assert.assertSame(req, injector.getInstance(HttpServletRequest.class));
        Assert.assertSame(res, injector.getInstance(HttpServletResponse.class));
        Assert.assertSame(this, injector.getInstance(TestWebAppTests.class));
        Mockito.verify(req).getParameter("foo");
        Mockito.verify(res).getWriter();
        Mockito.verify(out).println("Hello world!");
    }

    interface Foo {}

    static class Bar implements TestWebAppTests.Foo {}

    static class FooBar extends TestWebAppTests.Bar {}

    @Test
    public void testCreateInjector() throws Exception {
        TestWebAppTests.Bar bar = new TestWebAppTests.Bar();
        Injector injector = WebAppTests.createMockInjector(TestWebAppTests.Foo.class, bar);
        logInstances(injector.getInstance(HttpServletRequest.class), injector.getInstance(HttpServletResponse.class), injector.getInstance(HttpServletResponse.class).getWriter());
        Assert.assertSame(bar, injector.getInstance(TestWebAppTests.Foo.class));
    }

    @Test
    public void testCreateInjector2() {
        final TestWebAppTests.FooBar foobar = new TestWebAppTests.FooBar();
        TestWebAppTests.Bar bar = new TestWebAppTests.Bar();
        Injector injector = WebAppTests.createMockInjector(TestWebAppTests.Foo.class, bar, new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestWebAppTests.Bar.class).toInstance(foobar);
            }
        });
        Assert.assertNotSame(bar, injector.getInstance(TestWebAppTests.Bar.class));
        Assert.assertSame(foobar, injector.getInstance(TestWebAppTests.Bar.class));
    }

    @RequestScoped
    static class ScopeTest {}

    @Test
    public void testRequestScope() {
        Injector injector = WebAppTests.createMockInjector(this);
        Assert.assertSame(injector.getInstance(TestWebAppTests.ScopeTest.class), injector.getInstance(TestWebAppTests.ScopeTest.class));
    }
}

