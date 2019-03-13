/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2017 Oracle and/or its affiliates. All rights reserved.
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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.annotation.Priority;
import javax.ws.rs.GET;
import javax.ws.rs.NameBinding;
import javax.ws.rs.Path;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Assert;
import org.junit.Test;

import static org.glassfish.jersey.server.RequestContextBuilder.from;


/**
 * Tests layering of filters applied on appropriate methods (using named bindings) on resource method, sub-method,
 * sub-resource locator, sub-resource method. Jersey 2 does not support full functionality of Jersey 1 speaking about
 * filter layering. Jersey 2 implementation is JAX-RS compliant.
 * <p/>
 * But it could be implemented as Jersey specific extension - JERSEY-2414.
 * Please un-ignore tests whenever JERSEY-2414 fixed.
 *
 * @author Paul Sandoz
 * @author Libor Kramolis (libor.kramolis at oracle.com)
 */
public class LayeredFiltersTest {
    @Path("/")
    public static class ResourceWithSubresourceLocator {
        @Path("sub")
        @LayeredFiltersTest.One
        public Object get() {
            return new LayeredFiltersTest.ResourceWithMethod();
        }
    }

    @Path("/")
    public static class ResourceWithMethod {
        @GET
        @LayeredFiltersTest.Two
        public String get(@Context
        HttpHeaders hh) {
            List<String> xTest = hh.getRequestHeaders().get("X-TEST");
            Assert.assertEquals(2, xTest.size());
            return (xTest.get(0)) + (xTest.get(1));
        }

        @GET
        @Path("submethod")
        @LayeredFiltersTest.Two
        public String getSubmethod(@Context
        HttpHeaders hh) {
            List<String> xTest = hh.getRequestHeaders().get("X-TEST");
            Assert.assertEquals(2, xTest.size());
            return (xTest.get(0)) + (xTest.get(1));
        }
    }

    @NameBinding
    @Retention(RetentionPolicy.RUNTIME)
    public @interface One {}

    @LayeredFiltersTest.One
    @Priority((Priorities.USER) + 1)
    public static class FilterOne implements ContainerRequestFilter , ContainerResponseFilter {
        public void filter(ContainerRequestContext requestContext) throws IOException {
            List<String> xTest = requestContext.getHeaders().get("X-TEST");
            Assert.assertNull(xTest);
            requestContext.getHeaders().add("X-TEST", "one");
        }

        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
            List<Object> xTest = responseContext.getHeaders().get("X-TEST");
            Assert.assertEquals(1, xTest.size());
            Assert.assertEquals("two", xTest.get(0));
            responseContext.getHeaders().add("X-TEST", "one");
        }
    }

    @NameBinding
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Two {}

    @LayeredFiltersTest.Two
    @Priority((Priorities.USER) + 2)
    public static class FilterTwo implements ContainerRequestFilter , ContainerResponseFilter {
        public void filter(ContainerRequestContext requestContext) throws IOException {
            List<String> xTest = requestContext.getHeaders().get("X-TEST");
            Assert.assertNotNull("FilterOne not called", xTest);
            Assert.assertEquals(1, xTest.size());
            Assert.assertEquals("one", xTest.get(0));
            requestContext.getHeaders().add("X-TEST", "two");
        }

        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
            List<Object> xTest = responseContext.getHeaders().get("X-TEST");
            Assert.assertNull(xTest);
            responseContext.getHeaders().add("X-TEST", "two");
        }
    }

    @Path("/")
    @LayeredFiltersTest.One
    public static class ResourceWithSubresourceLocatorOnClass {
        @Path("sub")
        public Object get() {
            return new LayeredFiltersTest.ResourceWithMethodOnClass();
        }
    }

    @Path("/")
    @LayeredFiltersTest.Two
    public static class ResourceWithMethodOnClass {
        @GET
        public String get(@Context
        HttpHeaders hh) {
            List<String> xTest = hh.getRequestHeaders().get("X-TEST");
            Assert.assertEquals(2, xTest.size());
            return (xTest.get(0)) + (xTest.get(1));
        }

        @GET
        @Path("submethod")
        public String getSubmethod(@Context
        HttpHeaders hh) {
            List<String> xTest = hh.getRequestHeaders().get("X-TEST");
            Assert.assertEquals(2, xTest.size());
            return (xTest.get(0)) + (xTest.get(1));
        }
    }

    @Path("/")
    public static class ResourceWithMethodMultiple {
        @GET
        @LayeredFiltersTest.One
        @LayeredFiltersTest.Two
        public String get(@Context
        HttpHeaders hh) {
            List<String> xTest = hh.getRequestHeaders().get("X-TEST");
            Assert.assertEquals(2, xTest.size());
            return (xTest.get(0)) + (xTest.get(1));
        }

        @GET
        @Path("submethod")
        @LayeredFiltersTest.One
        @LayeredFiltersTest.Two
        public String getSubmethod(@Context
        HttpHeaders hh) {
            List<String> xTest = hh.getRequestHeaders().get("X-TEST");
            Assert.assertEquals(2, xTest.size());
            return (xTest.get(0)) + (xTest.get(1));
        }
    }

    @Test
    public void testResourceMethodMultiple() throws InterruptedException, ExecutionException {
        final ResourceConfig resourceConfig = new ResourceConfig(LayeredFiltersTest.ResourceWithMethodMultiple.class).register(LayeredFiltersTest.FilterOne.class).register(LayeredFiltersTest.FilterTwo.class);
        final ApplicationHandler application = new ApplicationHandler(resourceConfig);
        final ContainerResponse response = application.apply(from("/", "GET").build()).get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("onetwo", response.getEntity());
        List<Object> xTest = response.getHeaders().get("X-TEST");
        Assert.assertEquals(2, xTest.size());
        Assert.assertEquals("two", xTest.get(0));
        Assert.assertEquals("one", xTest.get(1));
    }

    @Test
    public void testResourceSubresourceMethodMultiple() throws InterruptedException, ExecutionException {
        final ResourceConfig resourceConfig = new ResourceConfig(LayeredFiltersTest.ResourceWithMethodMultiple.class).register(LayeredFiltersTest.FilterOne.class).register(LayeredFiltersTest.FilterTwo.class);
        final ApplicationHandler application = new ApplicationHandler(resourceConfig);
        final ContainerResponse response = application.apply(from("/submethod", "GET").build()).get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("onetwo", response.getEntity());
        List<Object> xTest = response.getHeaders().get("X-TEST");
        Assert.assertEquals(2, xTest.size());
        Assert.assertEquals("two", xTest.get(0));
        Assert.assertEquals("one", xTest.get(1));
    }
}

