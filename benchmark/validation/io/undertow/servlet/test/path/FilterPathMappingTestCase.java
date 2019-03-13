/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.servlet.test.path;


import DispatcherType.REQUEST;
import ServletContainer.Factory;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.ServletContainer;
import io.undertow.servlet.api.ServletInfo;
import io.undertow.servlet.test.util.TestClassIntrospector;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import javax.servlet.ServletException;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class FilterPathMappingTestCase {
    @Test
    public void testBasicFilterMappings() throws IOException, ServletException {
        DeploymentInfo builder = new DeploymentInfo();
        final PathHandler root = new PathHandler();
        final ServletContainer container = Factory.newInstance();
        builder.addServlet(new ServletInfo("/a/*", PathMappingServlet.class).addMapping("/a/*"));
        builder.addServlet(new ServletInfo("/aa", PathMappingServlet.class).addMapping("/aa"));
        builder.addServlet(new ServletInfo("/", PathMappingServlet.class).addMapping("/"));
        builder.addServlet(new ServletInfo("contextRoot", PathMappingServlet.class).addMapping(""));
        builder.addServlet(new ServletInfo("/myservlet/*", PathMappingServlet.class).addMapping("/myservlet/*"));
        builder.addServlet(new ServletInfo("*.jsp", PathMappingServlet.class).addMapping("*.jsp"));
        builder.addServlet(new ServletInfo("/hello/*", PathMappingServlet.class).addMapping("/hello/*"));
        builder.addServlet(new ServletInfo("/test/*", PathMappingServlet.class).addMapping("/test/*"));
        builder.addFilter(new FilterInfo("/*", PathFilter.class));
        builder.addFilterUrlMapping("/*", "/*", REQUEST);
        // non standard, but we still support it
        builder.addFilter(new FilterInfo("*", PathFilter.class));
        builder.addFilterUrlMapping("*", "*", REQUEST);
        builder.addFilter(new FilterInfo("/a/*", PathFilter.class));
        builder.addFilterUrlMapping("/a/*", "/a/*", REQUEST);
        builder.addFilter(new FilterInfo("/aa", PathFilter.class));
        builder.addFilterUrlMapping("/aa", "/aa", REQUEST);
        builder.addFilter(new FilterInfo("*.bop", PathFilter.class));
        builder.addFilterUrlMapping("*.bop", "*.bop", REQUEST);
        builder.addFilter(new FilterInfo("/myservlet/myfilter/*", PathFilter.class));
        builder.addFilterUrlMapping("/myservlet/myfilter/*", "/myservlet/myfilter/*", REQUEST);
        builder.addFilter(new FilterInfo("/myfilter/*", PathFilter.class));
        builder.addFilterUrlMapping("/myfilter/*", "/myfilter/*", REQUEST);
        builder.addFilter(new FilterInfo("contextRoot", PathFilter.class));
        builder.addFilterServletNameMapping("contextRoot", "contextRoot", REQUEST);
        builder.addFilter(new FilterInfo("defaultName", PathFilter.class));
        builder.addFilterServletNameMapping("defaultName", "/", REQUEST);
        builder.addFilter(new FilterInfo("/helloworld/index.html", PathFilter.class));
        builder.addFilterUrlMapping("/helloworld/index.html", "/helloworld/index.html", REQUEST);
        builder.addFilter(new FilterInfo("/test", PathFilter.class));
        builder.addFilterUrlMapping("/test", "/test", REQUEST);
        builder.addFilter(new FilterInfo("allByName", PathFilter.class));
        builder.addFilterServletNameMapping("allByName", "*", REQUEST);
        builder.setClassIntrospecter(TestClassIntrospector.INSTANCE).setClassLoader(FilterPathMappingTestCase.class.getClassLoader()).setContextPath("/servletContext").setDeploymentName("servletContext.war");
        final DeploymentManager manager = container.addDeployment(builder);
        manager.deploy();
        root.addPrefixPath(builder.getContextPath(), manager.start());
        DefaultServer.setRootHandler(root);
        TestHttpClient client = new TestHttpClient();
        try {
            runTest(client, "test", "/test/* - /test - null", "/*", "*", "/test", "allByName");
            runTest(client, "aa", "/aa - /aa - null", "/*", "*", "/aa", "allByName");
            runTest(client, "a/c", "/a/* - /a - /c", "/*", "*", "/a/*", "allByName");
            runTest(client, "a", "/a/* - /a - null", "/*", "*", "/a/*", "allByName");
            runTest(client, "aa/b", "/ - /aa/b - null", "/*", "*", "defaultName", "allByName");
            runTest(client, "a/b/c/d", "/a/* - /a - /b/c/d", "/*", "*", "/a/*", "allByName");
            runTest(client, "defaultStuff", "/ - /defaultStuff - null", "/*", "*", "defaultName", "allByName");
            runTest(client, "", "contextRoot - / - null", "/*", "*", "contextRoot", "allByName");
            runTest(client, "yyyy.bop", "/ - /yyyy.bop - null", "/*", "*", "*.bop", "defaultName", "allByName");
            runTest(client, "a/yyyy.bop", "/a/* - /a - /yyyy.bop", "/*", "*", "*.bop", "/a/*", "allByName");
            runTest(client, "myservlet/myfilter/file.dat", "/myservlet/* - /myservlet - /myfilter/file.dat", "/*", "*", "/myservlet/myfilter/*", "allByName");
            runTest(client, "myservlet/myfilter/file.jsp", "/myservlet/* - /myservlet - /myfilter/file.jsp", "/*", "*", "/myservlet/myfilter/*", "allByName");
            runTest(client, "otherservlet/myfilter/file.jsp", "*.jsp - /otherservlet/myfilter/file.jsp - null", "/*", "*", "allByName");
            runTest(client, "myfilter/file.jsp", "*.jsp - /myfilter/file.jsp - null", "/*", "*", "/myfilter/*", "allByName");
            runTest(client, "helloworld/index.html", "/ - /helloworld/index.html - null", "/*", "*", "/helloworld/index.html", "defaultName", "allByName");
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testExtensionMatchServletWithGlobalFilter() throws IOException, ServletException {
        DeploymentInfo builder = new DeploymentInfo();
        final PathHandler root = new PathHandler();
        final ServletContainer container = Factory.newInstance();
        builder.addServlet(new ServletInfo("*.jsp", PathMappingServlet.class).addMapping("*.jsp"));
        builder.addFilter(new FilterInfo("/*", PathFilter.class));
        builder.addFilterUrlMapping("/*", "/*", REQUEST);
        builder.setClassIntrospecter(TestClassIntrospector.INSTANCE).setClassLoader(FilterPathMappingTestCase.class.getClassLoader()).setContextPath("/servletContext").setDeploymentName("servletContext.war");
        final DeploymentManager manager = container.addDeployment(builder);
        manager.deploy();
        root.addPrefixPath(builder.getContextPath(), manager.start());
        DefaultServer.setRootHandler(root);
        TestHttpClient client = new TestHttpClient();
        try {
            runTest(client, "aa.jsp", "*.jsp - /aa.jsp - null", "/*");
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void test_WFLY_1935() throws IOException, ServletException {
        DeploymentInfo builder = new DeploymentInfo();
        final PathHandler root = new PathHandler();
        final ServletContainer container = Factory.newInstance();
        builder.addServlet(new ServletInfo("*.a", PathMappingServlet.class).addMapping("*.a"));
        builder.addFilter(new FilterInfo("/*", PathFilter.class));
        builder.addFilterUrlMapping("/*", "/*", REQUEST);
        // non standard, but we still support it
        builder.addFilter(new FilterInfo("/SimpleServlet.a", PathFilter.class));
        builder.addFilterUrlMapping("/SimpleServlet.a", "/SimpleServlet.a", REQUEST);
        builder.setClassIntrospecter(TestClassIntrospector.INSTANCE).setClassLoader(FilterPathMappingTestCase.class.getClassLoader()).setContextPath("/servletContext").setDeploymentName("servletContext.war");
        final DeploymentManager manager = container.addDeployment(builder);
        manager.deploy();
        root.addPrefixPath(builder.getContextPath(), manager.start());
        DefaultServer.setRootHandler(root);
        TestHttpClient client = new TestHttpClient();
        try {
            runTest(client, "SimpleServlet.a", "*.a - /SimpleServlet.a - null", "/*", "/SimpleServlet.a");
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

