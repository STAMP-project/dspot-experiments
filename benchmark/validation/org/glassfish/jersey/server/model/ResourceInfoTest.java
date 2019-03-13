/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.server.model;


import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerResponse;
import org.junit.Assert;
import org.junit.Test;

import static org.glassfish.jersey.server.RequestContextBuilder.from;


/**
 * Test cases for injected {@link ResourceInfo} in filters.
 *
 * @author Miroslav Fuksa
 */
public class ResourceInfoTest {
    public static class MyRequestFilter implements ContainerRequestFilter {
        @Context
        ResourceInfo resourceInfo;

        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
            requestContext.getHeaders().add("MyRequestFilter-called", "called");
            requestContext.getHeaders().add("MyRequestFilter-class", resourceInfo.getResourceClass().getSimpleName());
            requestContext.getHeaders().add("MyRequestFilter-method", resourceInfo.getResourceMethod().getName());
        }
    }

    public static class MyResponseFilter implements ContainerResponseFilter {
        @Context
        ResourceInfo resourceInfo;

        @Override
        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
            responseContext.getHeaders().add("MyResponseFilter-called", "called");
            final Class<?> resourceClass = resourceInfo.getResourceClass();
            final Method resourceMethod = resourceInfo.getResourceMethod();
            responseContext.getHeaders().add("MyResponseFilter-class", (resourceClass == null ? "<null>" : resourceClass.getSimpleName()));
            responseContext.getHeaders().add("MyResponseFilter-method", (resourceMethod == null ? "<null>" : resourceMethod.getName()));
        }
    }

    @PreMatching
    public static class MyPrematchingFilter implements ContainerRequestFilter {
        @Context
        ResourceInfo resourceInfo;

        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
            Assert.assertNull(resourceInfo.getResourceClass());
            Assert.assertNull(resourceInfo.getResourceMethod());
        }
    }

    @Path("resource")
    public static class MyResource {
        @GET
        public String get(@Context
        ContainerRequestContext request) {
            Assert.assertEquals("called", request.getHeaderString("MyRequestFilter-called"));
            final String className = "MyResource";
            final String methodName = "get";
            ResourceInfoTest.assertRequestHeader(request, className, methodName);
            return "get";
        }

        @GET
        @Path("get-child")
        public String getChild(@Context
        ContainerRequestContext request) {
            ResourceInfoTest.assertRequestHeader(request, "MyResource", "getChild");
            return "get-child";
        }

        @POST
        public String post(@Context
        ContainerRequestContext request) {
            ResourceInfoTest.assertRequestHeader(request, "MyResource", "post");
            return "post";
        }

        @Path("locator")
        public Class<ResourceInfoTest.SubResource> getSubResource() {
            return ResourceInfoTest.SubResource.class;
        }
    }

    @Path("info")
    public static class ResourceTestingInfo {
        @Context
        ResourceInfo resourceInfo;

        @GET
        public String getInfo(@Context
        ContainerRequestContext request) {
            ResourceInfoTest.assertRequestHeader(request, "ResourceTestingInfo", "getInfo");
            return "get-info";
        }

        @GET
        @Path("child")
        public String getChildInfo(@Context
        ContainerRequestContext request) {
            ResourceInfoTest.assertRequestHeader(request, "ResourceTestingInfo", "getChildInfo");
            return "get-info-child";
        }
    }

    public static class SubResource {
        @GET
        public String getFromSubResource(@Context
        ContainerRequestContext request) {
            ResourceInfoTest.assertRequestHeader(request, "SubResource", "getFromSubResource");
            return "get-sub-resource";
        }
    }

    @Path("resource-another")
    public static class MyAnotherResource {
        @GET
        public String getAnother(@Context
        ContainerRequestContext request) {
            ResourceInfoTest.assertRequestHeader(request, "MyAnotherResource", "getAnother");
            return "get-another";
        }
    }

    @Test
    public void testGet() throws InterruptedException, ExecutionException {
        ApplicationHandler handler = getApplication();
        final ContainerResponse response = handler.apply(from("/resource", "GET").build()).get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("get", response.getEntity());
        assertResponseHeaders(response, "MyResource", "get");
    }

    @Test
    public void testGetMultiple() throws InterruptedException, ExecutionException {
        ApplicationHandler handler = getApplication();
        ContainerResponse response = handler.apply(from("/resource", "GET").build()).get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("get", response.getEntity());
        assertResponseHeaders(response, "MyResource", "get");
        response = handler.apply(from("/resource/get-child", "GET").build()).get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("get-child", response.getEntity());
        assertResponseHeaders(response, "MyResource", "getChild");
    }

    @Test
    public void testGetChild() throws InterruptedException, ExecutionException {
        ApplicationHandler handler = getApplication();
        final ContainerResponse response = handler.apply(from("/resource/get-child", "GET").build()).get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("get-child", response.getEntity());
        final String className = "MyResource";
        final String methodName = "getChild";
        assertResponseHeaders(response, className, methodName);
    }

    @Test
    public void testPost() throws InterruptedException, ExecutionException {
        ApplicationHandler handler = getApplication();
        final ContainerResponse response = handler.apply(from("/resource", "POST").entity("entity").build()).get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("post", response.getEntity());
        assertResponseHeaders(response, "MyResource", "post");
    }

    @Test
    public void testGetAnotherResource() throws InterruptedException, ExecutionException {
        ApplicationHandler handler = getApplication();
        final ContainerResponse response = handler.apply(from("/resource-another", "GET").build()).get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("get-another", response.getEntity());
        assertResponseHeaders(response, "MyAnotherResource", "getAnother");
    }

    @Test
    public void testGetSubResource() throws InterruptedException, ExecutionException {
        ApplicationHandler handler = getApplication();
        final ContainerResponse response = handler.apply(from("/resource/locator", "GET").build()).get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("get-sub-resource", response.getEntity());
        assertResponseHeaders(response, "SubResource", "getFromSubResource");
    }

    @Test
    public void testInfoGet() throws InterruptedException, ExecutionException {
        ApplicationHandler handler = getApplication();
        final ContainerResponse response = handler.apply(from("/info", "GET").build()).get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("get-info", response.getEntity());
        assertResponseHeaders(response, "ResourceTestingInfo", "getInfo");
    }

    @Test
    public void testInfoGetChild() throws InterruptedException, ExecutionException {
        ApplicationHandler handler = getApplication();
        final ContainerResponse response = handler.apply(from("/info/child", "GET").build()).get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("get-info-child", response.getEntity());
        assertResponseHeaders(response, "ResourceTestingInfo", "getChildInfo");
    }

    @Test
    public void test404() throws InterruptedException, ExecutionException {
        ApplicationHandler handler = getApplication();
        final ContainerResponse response = handler.apply(from("/NOT_FOUND", "GET").build()).get();
        Assert.assertEquals(404, response.getStatus());
        assertResponseHeaders(response, "<null>", "<null>");
    }
}

