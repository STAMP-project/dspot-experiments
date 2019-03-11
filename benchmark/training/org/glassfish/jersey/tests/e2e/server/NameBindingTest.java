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
package org.glassfish.jersey.tests.e2e.server;


import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Set;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NameBinding;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.util.runner.ConcurrentRunner;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test-suite ensuring the correct functionality of name binding.
 *
 * @author Miroslav Fuksa
 * @author Michal Gajdos
 */
@RunWith(ConcurrentRunner.class)
public class NameBindingTest extends JerseyTest {
    @Path("resource")
    public static class Resource {
        @GET
        public String noBinding() {
            return "noBinding";
        }

        @GET
        @NameBindingTest.FooBinding
        @Path("foo")
        public String foo() {
            return "foo";
        }

        @GET
        @NameBindingTest.BarBinding
        @Path("bar")
        public String bar() {
            return "bar";
        }

        @GET
        @NameBindingTest.FooBinding
        @NameBindingTest.BarBinding
        @Path("foobar")
        public String foobar() {
            return "foobar";
        }

        @GET
        @Path("preMatchingNameBinding")
        public String preMatchingNameBinding(@HeaderParam("header")
        @DefaultValue("bar")
        final String header) {
            return header;
        }
    }

    @NameBinding
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface FooBinding {}

    @NameBinding
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface BarBinding {}

    @NameBindingTest.FooBinding
    public static class FooFilter implements ContainerResponseFilter {
        @Override
        public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
            responseContext.getHeaders().add(this.getClass().getSimpleName(), "called");
        }
    }

    @PreMatching
    @NameBindingTest.FooBinding
    public static class PreMatchingFooFilter implements ContainerRequestFilter {
        @Override
        public void filter(final ContainerRequestContext requestContext) throws IOException {
            requestContext.getHeaders().putSingle("header", "foo");
        }
    }

    @NameBindingTest.BarBinding
    public static class BarFilter implements ContainerResponseFilter {
        @Override
        public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
            responseContext.getHeaders().add(this.getClass().getSimpleName(), "called");
        }
    }

    @NameBindingTest.FooBinding
    @NameBindingTest.BarBinding
    public static class FooBarFilter implements ContainerResponseFilter {
        @Override
        public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
            responseContext.getHeaders().add(this.getClass().getSimpleName(), "called");
        }
    }

    private static final Set<Class<?>> FILTERS = NameBindingTest.initialize();

    @Test
    public void testResourceNoBinding() {
        checkCalled(_getResponse("resource"));
    }

    @Test
    public void testResourceFooBinding() {
        checkCalled(_getResponse("resource/foo"), NameBindingTest.FooFilter.class);
    }

    /**
     * Reproducer for JERSEY-2739. Name bound annotation on a pre-matching filter should be ignored and the filter should be
     * invoked for each resource method (globally).
     */
    @Test
    public void preMatchingNameBinding() {
        final Response response = _getResponse("resource/preMatchingNameBinding");
        // Request filter - applied, even when the filter is name bound and the resource method is not.
        Assert.assertThat("Name binding on a @PreMatching filter not ignored.", response.readEntity(String.class), Is.is("foo"));
    }

    @Test
    public void testResourceBarBinding() {
        checkCalled(_getResponse("resource/bar"), NameBindingTest.BarFilter.class);
    }

    @Test
    public void testResourceFooBarBinding() {
        checkCalled(_getResponse("resource/foobar"), NameBindingTest.FooFilter.class, NameBindingTest.BarFilter.class, NameBindingTest.FooBarFilter.class);
    }

    @Path("foo-resource")
    @NameBindingTest.FooBinding
    public static class FooResource extends NameBindingTest.Resource {}

    @Test
    public void testFooResourceNoBinding() {
        checkCalled(_getResponse("foo-resource"), NameBindingTest.FooFilter.class);
    }

    @Test
    public void testFooResourceFooBinding() {
        checkCalled(_getResponse("foo-resource/foo"), NameBindingTest.FooFilter.class);
    }

    @Test
    public void testFooResourceBarBinding() {
        checkCalled(_getResponse("foo-resource/bar"), NameBindingTest.FooFilter.class, NameBindingTest.BarFilter.class, NameBindingTest.FooBarFilter.class);
    }

    @Test
    public void testFooResourceFooBarBinding() {
        checkCalled(_getResponse("foo-resource/foobar"), NameBindingTest.FooFilter.class, NameBindingTest.BarFilter.class, NameBindingTest.FooBarFilter.class);
    }

    @Path("bar-resource")
    @NameBindingTest.BarBinding
    public static class BarResource extends NameBindingTest.Resource {}

    @Test
    public void testBarResourceNoBinding() {
        checkCalled(_getResponse("bar-resource"), NameBindingTest.BarFilter.class);
    }

    @Test
    public void testBarResourceFooBinding() {
        checkCalled(_getResponse("bar-resource/foo"), NameBindingTest.BarFilter.class, NameBindingTest.FooFilter.class, NameBindingTest.FooBarFilter.class);
    }

    @Test
    public void testBarResourceBarBinding() {
        checkCalled(_getResponse("bar-resource/bar"), NameBindingTest.BarFilter.class);
    }

    @Test
    public void testBarResourceFooBarBinding() {
        checkCalled(_getResponse("bar-resource/foobar"), NameBindingTest.BarFilter.class, NameBindingTest.FooFilter.class, NameBindingTest.FooBarFilter.class);
    }

    @Path("foobar-resource")
    @NameBindingTest.BarBinding
    @NameBindingTest.FooBinding
    public static class FooBarResource extends NameBindingTest.Resource {}

    @Test
    public void testFooBarResourceNoBinding() {
        checkCalled(_getResponse("foobar-resource"), NameBindingTest.BarFilter.class, NameBindingTest.FooFilter.class, NameBindingTest.FooBarFilter.class);
    }

    @Test
    public void testFooBarResourceFooBinding() {
        checkCalled(_getResponse("foobar-resource/foo"), NameBindingTest.BarFilter.class, NameBindingTest.FooFilter.class, NameBindingTest.FooBarFilter.class);
    }

    @Test
    public void testFooBarResourceBarBinding() {
        checkCalled(_getResponse("foobar-resource/bar"), NameBindingTest.BarFilter.class, NameBindingTest.FooFilter.class, NameBindingTest.FooBarFilter.class);
    }

    @Test
    public void testFooBarResourceFooBarBinding() {
        checkCalled(_getResponse("foobar-resource/foobar"), NameBindingTest.BarFilter.class, NameBindingTest.FooFilter.class, NameBindingTest.FooBarFilter.class);
    }
}

