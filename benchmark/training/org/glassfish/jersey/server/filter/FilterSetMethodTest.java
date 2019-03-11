/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.server.filter;


import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import javax.annotation.Priority;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.RequestContextBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests calling {@link ContainerRequestContext#setMethod(String)} in different request/response phases.
 *
 * @author Miroslav Fuksa
 */
public class FilterSetMethodTest {
    @Test
    public void testResponseFilter() throws InterruptedException, ExecutionException {
        ApplicationHandler handler = new ApplicationHandler(new ResourceConfig(FilterSetMethodTest.Resource.class, FilterSetMethodTest.ResponseFilter.class));
        ContainerResponse res = handler.apply(RequestContextBuilder.from("", "/resource/setMethod", "GET").build()).get();
        Assert.assertEquals(200, res.getStatus());
    }

    @Test
    public void testPreMatchingFilter() throws InterruptedException, ExecutionException {
        ApplicationHandler handler = new ApplicationHandler(new ResourceConfig(FilterSetMethodTest.Resource.class, FilterSetMethodTest.PreMatchFilter.class));
        ContainerResponse res = handler.apply(RequestContextBuilder.from("", "/resource/setMethod", "GET").build()).get();
        Assert.assertEquals(200, res.getStatus());
    }

    @Test
    public void testPostMatchingFilter() throws InterruptedException, ExecutionException {
        ApplicationHandler handler = new ApplicationHandler(new ResourceConfig(FilterSetMethodTest.Resource.class, FilterSetMethodTest.PostMatchFilter.class));
        ContainerResponse res = handler.apply(RequestContextBuilder.from("", "/resource/setMethod", "GET").build()).get();
        Assert.assertEquals(200, res.getStatus());
    }

    @Test
    public void testResource() throws InterruptedException, ExecutionException {
        ApplicationHandler handler = new ApplicationHandler(new ResourceConfig(FilterSetMethodTest.Resource.class, FilterSetMethodTest.PostMatchFilter.class));
        ContainerResponse res = handler.apply(RequestContextBuilder.from("", "/resource/setMethodInResource", "GET").build()).get();
        Assert.assertEquals(200, res.getStatus());
    }

    @Test
    public void testSubResourceLocator() throws InterruptedException, ExecutionException {
        ApplicationHandler handler = new ApplicationHandler(new ResourceConfig(FilterSetMethodTest.AnotherResource.class));
        ContainerResponse res = handler.apply(RequestContextBuilder.from("", "/another/locator", "GET").build()).get();
        Assert.assertEquals(200, res.getStatus());
    }

    @Test
    public void testResourceUri() throws InterruptedException, ExecutionException {
        ApplicationHandler handler = new ApplicationHandler(new ResourceConfig(FilterSetMethodTest.ResourceChangeUri.class, FilterSetMethodTest.PreMatchChangingUriFilter.class));
        ContainerResponse res = handler.apply(RequestContextBuilder.from("", "/resourceChangeUri/first", "GET").build()).get();
        Assert.assertEquals(200, res.getStatus());
        Assert.assertEquals("ok", res.getEntity());
    }

    @Path("resourceChangeUri")
    public static class ResourceChangeUri {
        @Path("first")
        @GET
        public String first() {
            Assert.fail("should not be called.");
            return "fail";
        }

        @Path("first/a")
        @GET
        public String a() {
            return "ok";
        }
    }

    @Provider
    @Priority(500)
    @PreMatching
    public static class PreMatchChangingUriFilter implements ContainerRequestFilter {
        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
            final URI requestUri = requestContext.getUriInfo().getRequestUriBuilder().path("a").build();
            requestContext.setRequestUri(requestUri);
        }
    }

    @Provider
    @Priority(500)
    public static class ResponseFilter implements ContainerResponseFilter {
        @Override
        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
            FilterSetMethodTest.checkExceptionThrown(new FilterSetMethodTest.SetMethodClosure(requestContext));
            FilterSetMethodTest.checkExceptionThrown(new FilterSetMethodTest.SetUriClosure(requestContext));
        }
    }

    @Provider
    @Priority(500)
    @PreMatching
    public static class PreMatchFilter implements ContainerRequestFilter {
        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
            new FilterSetMethodTest.SetMethodClosure(requestContext).f();
            new FilterSetMethodTest.SetUriClosure(requestContext).f();
            // Should not throw IllegalArgumentException exception in pre match filter.
        }
    }

    @Provider
    @Priority(500)
    public static class PostMatchFilter implements ContainerRequestFilter {
        @Override
        public void filter(final ContainerRequestContext requestContext) throws IOException {
            FilterSetMethodTest.checkExceptionThrown(new FilterSetMethodTest.SetMethodClosure(requestContext));
            FilterSetMethodTest.checkExceptionThrown(new FilterSetMethodTest.SetUriClosure(requestContext));
        }
    }

    @Path("resource")
    public static class Resource {
        @GET
        @Path("setMethod")
        public Response setMethod() {
            Response response = Response.ok().build();
            return response;
        }

        @GET
        @Path("setMethodInResource")
        public Response setMethodInResource(@Context
        ContainerRequestContext request) {
            FilterSetMethodTest.checkExceptionThrown(new FilterSetMethodTest.SetMethodClosure(request));
            FilterSetMethodTest.checkExceptionThrown(new FilterSetMethodTest.SetUriClosure(request));
            return Response.ok().build();
        }
    }

    @Path("another")
    public static class AnotherResource {
        @Path("locator")
        public FilterSetMethodTest.AnotherResource.SubResource methodInSubResource(@Context
        ContainerRequestContext request) {
            FilterSetMethodTest.checkExceptionThrown(new FilterSetMethodTest.SetMethodClosure(request));
            FilterSetMethodTest.checkExceptionThrown(new FilterSetMethodTest.SetUriClosure(request));
            return new FilterSetMethodTest.AnotherResource.SubResource();
        }

        public static class SubResource {
            @GET
            public Response get(@Context
            ContainerRequestContext request) {
                FilterSetMethodTest.checkExceptionThrown(new FilterSetMethodTest.SetMethodClosure(request));
                FilterSetMethodTest.checkExceptionThrown(new FilterSetMethodTest.SetUriClosure(request));
                return Response.ok().build();
            }
        }
    }

    public static interface Closure {
        void f();
    }

    public static class SetMethodClosure implements FilterSetMethodTest.Closure {
        final ContainerRequestContext requestContext;

        public SetMethodClosure(ContainerRequestContext requestContext) {
            this.requestContext = requestContext;
        }

        @Override
        public void f() {
            requestContext.setMethod("OPTIONS");
        }
    }

    public static class SetUriClosure implements FilterSetMethodTest.Closure {
        final ContainerRequestContext requestContext;

        public SetUriClosure(ContainerRequestContext requestContext) {
            this.requestContext = requestContext;
        }

        @Override
        public void f() {
            requestContext.setRequestUri(requestContext.getUriInfo().getRequestUri());
        }
    }
}

