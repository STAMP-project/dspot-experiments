/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2017 Oracle and/or its affiliates. All rights reserved.
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


import Resource.Builder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Priority;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.RequestContextBuilder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for JAX-RS filters.
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
public class ApplicationFilterTest {
    /**
     * Utility Injection binder that may be used for registering provider instances of provider
     * type {@code T} in HK2.
     */
    static class ProviderInstanceBindingBinder<T> extends AbstractBinder {
        private final Iterable<? extends T> providers;

        private final Class<T> providerType;

        /**
         * Create an injection binder for the supplied collection of provider instances.
         *
         * @param providers
         * 		list of provider instances.
         * @param providerType
         * 		registered provider contract type.
         */
        public ProviderInstanceBindingBinder(final Iterable<? extends T> providers, final Class<T> providerType) {
            this.providers = providers;
            this.providerType = providerType;
        }

        @Override
        protected void configure() {
            for (final T provider : providers) {
                ApplicationFilterTest.ProviderInstanceBindingBinder.bind(provider).to(providerType);
            }
        }
    }

    @Test
    public void testSingleRequestFilter() throws Exception {
        final AtomicInteger called = new AtomicInteger(0);
        final List<ContainerRequestFilter> requestFilters = new ArrayList<>();
        requestFilters.add(new ContainerRequestFilter() {
            @Override
            public void filter(final ContainerRequestContext context) throws IOException {
                called.incrementAndGet();
            }
        });
        final ResourceConfig resourceConfig = new ResourceConfig().register(new ApplicationFilterTest.ProviderInstanceBindingBinder(requestFilters, ContainerRequestFilter.class));
        final Resource.Builder rb = Resource.builder("test");
        rb.addMethod("GET").handledBy(new org.glassfish.jersey.process.Inflector<ContainerRequestContext, Response>() {
            @Override
            public Response apply(final ContainerRequestContext request) {
                return Response.ok().build();
            }
        });
        resourceConfig.registerResources(rb.build());
        final ApplicationHandler application = new ApplicationHandler(resourceConfig);
        Assert.assertEquals(200, application.apply(RequestContextBuilder.from("/test", "GET").build()).get().getStatus());
        Assert.assertEquals(1, called.intValue());
    }

    @Test
    public void testSingleResponseFilter() throws Exception {
        final AtomicInteger called = new AtomicInteger(0);
        final List<ContainerResponseFilter> responseFilterList = new ArrayList<>();
        responseFilterList.add(new ContainerResponseFilter() {
            @Override
            public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
                called.incrementAndGet();
            }
        });
        final ResourceConfig resourceConfig = new ResourceConfig().register(new ApplicationFilterTest.ProviderInstanceBindingBinder(responseFilterList, ContainerResponseFilter.class));
        final Resource.Builder rb = Resource.builder("test");
        rb.addMethod("GET").handledBy(new org.glassfish.jersey.process.Inflector<ContainerRequestContext, Response>() {
            @Override
            public Response apply(final ContainerRequestContext request) {
                return Response.ok().build();
            }
        });
        resourceConfig.registerResources(rb.build());
        final ApplicationHandler application = new ApplicationHandler(resourceConfig);
        Assert.assertEquals(200, application.apply(RequestContextBuilder.from("/test", "GET").build()).get().getStatus());
        Assert.assertEquals(1, called.intValue());
    }

    @Test
    public void testFilterCalledOn200() throws Exception {
        final ApplicationFilterTest.SimpleFilter simpleFilter = new ApplicationFilterTest.SimpleFilter();
        final ResourceConfig resourceConfig = new ResourceConfig(ApplicationFilterTest.SimpleResource.class).register(simpleFilter);
        final ApplicationHandler application = new ApplicationHandler(resourceConfig);
        final ContainerResponse response = application.apply(RequestContextBuilder.from("/simple", "GET").build()).get();
        Assert.assertEquals(200, response.getStatus());
        Assert.assertTrue(simpleFilter.called);
    }

    @Test
    public void testFilterNotCalledOn404() throws Exception {
        // not found
        final ApplicationFilterTest.SimpleFilter simpleFilter = new ApplicationFilterTest.SimpleFilter();
        final ResourceConfig resourceConfig = new ResourceConfig(ApplicationFilterTest.SimpleResource.class).register(simpleFilter);
        final ApplicationHandler application = new ApplicationHandler(resourceConfig);
        final ContainerResponse response = application.apply(RequestContextBuilder.from("/NOT-FOUND", "GET").build()).get();
        Assert.assertEquals(404, response.getStatus());
        Assert.assertFalse(simpleFilter.called);
    }

    @Test
    public void testFilterNotCalledOn405() throws Exception {
        // method not allowed
        final ApplicationFilterTest.SimpleFilter simpleFilter = new ApplicationFilterTest.SimpleFilter();
        final ResourceConfig resourceConfig = new ResourceConfig(ApplicationFilterTest.SimpleResource.class).register(simpleFilter);
        final ApplicationHandler application = new ApplicationHandler(resourceConfig);
        final ContainerResponse response = application.apply(RequestContextBuilder.from("/simple", "POST").entity("entity").build()).get();
        Assert.assertEquals(405, response.getStatus());
        Assert.assertFalse(simpleFilter.called);
    }

    @Path("simple")
    public static class SimpleResource {
        @GET
        public String get() {
            return "get";
        }
    }

    public abstract class CommonFilter implements ContainerRequestFilter {
        public boolean called = false;

        @Override
        public void filter(final ContainerRequestContext context) throws IOException {
            verify();
            called = true;
        }

        protected abstract void verify();
    }

    public class SimpleFilter extends ApplicationFilterTest.CommonFilter {
        @Override
        protected void verify() {
        }
    }

    @Priority(1)
    public class Filter1 extends ApplicationFilterTest.CommonFilter {
        private ApplicationFilterTest.Filter10 filter10;

        private ApplicationFilterTest.Filter100 filter100;

        public void setFilters(final ApplicationFilterTest.Filter10 filter10, final ApplicationFilterTest.Filter100 filter100) {
            this.filter10 = filter10;
            this.filter100 = filter100;
        }

        @Override
        protected void verify() {
            Assert.assertTrue(((filter10.called) == false));
            Assert.assertTrue(((filter100.called) == false));
        }
    }

    @Priority(10)
    public class Filter10 extends ApplicationFilterTest.CommonFilter {
        private ApplicationFilterTest.Filter1 filter1;

        private ApplicationFilterTest.Filter100 filter100;

        public void setFilters(final ApplicationFilterTest.Filter1 filter1, final ApplicationFilterTest.Filter100 filter100) {
            this.filter1 = filter1;
            this.filter100 = filter100;
        }

        @Override
        protected void verify() {
            Assert.assertTrue(((filter1.called) == true));
            Assert.assertTrue(((filter100.called) == false));
        }
    }

    @Priority(100)
    public class Filter100 extends ApplicationFilterTest.CommonFilter {
        private ApplicationFilterTest.Filter1 filter1;

        private ApplicationFilterTest.Filter10 filter10;

        public void setFilters(final ApplicationFilterTest.Filter1 filter1, final ApplicationFilterTest.Filter10 filter10) {
            this.filter1 = filter1;
            this.filter10 = filter10;
        }

        @Override
        protected void verify() {
            Assert.assertTrue(filter1.called);
            Assert.assertTrue(filter10.called);
        }
    }

    @Test
    public void testMultipleFiltersWithBindingPriority() throws Exception {
        final ApplicationFilterTest.Filter1 filter1 = new ApplicationFilterTest.Filter1();
        final ApplicationFilterTest.Filter10 filter10 = new ApplicationFilterTest.Filter10();
        final ApplicationFilterTest.Filter100 filter100 = new ApplicationFilterTest.Filter100();
        filter1.setFilters(filter10, filter100);
        filter10.setFilters(filter1, filter100);
        filter100.setFilters(filter1, filter10);
        final List<ContainerRequestFilter> requestFilterList = new ArrayList<>();
        requestFilterList.add(filter100);
        requestFilterList.add(filter1);
        requestFilterList.add(filter10);
        final ResourceConfig resourceConfig = new ResourceConfig().register(new ApplicationFilterTest.ProviderInstanceBindingBinder(requestFilterList, ContainerRequestFilter.class));
        final Resource.Builder rb = Resource.builder("test");
        rb.addMethod("GET").handledBy(new org.glassfish.jersey.process.Inflector<ContainerRequestContext, Response>() {
            @Override
            public Response apply(final ContainerRequestContext request) {
                return Response.ok().build();
            }
        });
        resourceConfig.registerResources(rb.build());
        final ApplicationHandler application = new ApplicationHandler(resourceConfig);
        Assert.assertEquals(200, application.apply(RequestContextBuilder.from("/test", "GET").build()).get().getStatus());
    }

    public class ExceptionFilter implements ContainerRequestFilter {
        @Override
        public void filter(final ContainerRequestContext context) throws IOException {
            throw new IOException("test");
        }
    }

    @Test
    public void testFilterExceptionHandling() throws Exception {
        final List<ContainerRequestFilter> requestFilterList = new ArrayList<>();
        requestFilterList.add(new ApplicationFilterTest.ExceptionFilter());
        final ResourceConfig resourceConfig = new ResourceConfig().register(new ApplicationFilterTest.ProviderInstanceBindingBinder(requestFilterList, ContainerRequestFilter.class));
        final Resource.Builder rb = Resource.builder("test");
        rb.addMethod("GET").handledBy(new org.glassfish.jersey.process.Inflector<ContainerRequestContext, Response>() {
            @Override
            public Response apply(final ContainerRequestContext request) {
                return Response.ok().build();
            }
        });
        resourceConfig.registerResources(rb.build());
        final ApplicationHandler application = new ApplicationHandler(resourceConfig);
        try {
            application.apply(RequestContextBuilder.from("/test", "GET").build()).get().getStatus();
            Assert.fail("should throw an exception");
        } catch (final Exception e) {
            // ok
        }
    }
}

