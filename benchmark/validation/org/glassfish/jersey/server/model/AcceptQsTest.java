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
package org.glassfish.jersey.server.model;


import Resource.Builder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;


/**
 *
 *
 * @author Jakub Podlesak
 */
public class AcceptQsTest {
    private static class StringReturningInflector implements Inflector<ContainerRequestContext, Response> {
        String entity;

        StringReturningInflector(String entity) {
            this.entity = entity;
        }

        @Override
        public Response apply(ContainerRequestContext data) {
            return Response.ok(entity).build();
        }
    }

    @Path("/")
    public static class TestResource {
        @Produces("application/foo;qs=0.4")
        @GET
        public String doGetFoo() {
            return "foo";
        }

        @Produces("application/bar;qs=0.5")
        @GET
        public String doGetBar() {
            return "bar";
        }

        @Produces("application/baz")
        @GET
        public String doGetBaz() {
            return "baz";
        }
    }

    @Test
    public void testAcceptGetDeclarative() throws Exception {
        runTestAcceptGet(createApplication(AcceptQsTest.TestResource.class));
    }

    @Test
    public void testAcceptGetProgrammatic() throws Exception {
        final Resource.Builder rb = Resource.builder("/");
        rb.addMethod("GET").produces(MediaType.valueOf("application/foo;qs=0.4")).handledBy(stringResponse("foo"));
        rb.addMethod("GET").produces(MediaType.valueOf("application/bar;qs=0.5")).handledBy(stringResponse("bar"));
        rb.addMethod("GET").produces(MediaType.valueOf("application/baz")).handledBy(stringResponse("baz"));
        ResourceConfig rc = new ResourceConfig();
        rc.registerResources(rb.build());
        runTestAcceptGet(new org.glassfish.jersey.server.ApplicationHandler(rc));
    }

    @Path("/")
    public static class MultipleResource {
        @Produces({ "application/foo;qs=0.5", "application/bar" })
        @GET
        public String get() {
            return "GET";
        }
    }

    @Test
    public void testAcceptMultipleDeclarative() throws Exception {
        runTestAcceptMultiple(createApplication(AcceptQsTest.MultipleResource.class));
    }

    @Test
    public void testAcceptMultipleProgrammatic() throws Exception {
        final Resource.Builder rb = Resource.builder("/");
        rb.addMethod("GET").produces(MediaType.valueOf("application/foo;qs=0.5"), MediaType.valueOf("application/bar")).handledBy(stringResponse("GET"));
        ResourceConfig rc = new ResourceConfig();
        rc.registerResources(rb.build());
        runTestAcceptMultiple(new org.glassfish.jersey.server.ApplicationHandler(rc));
    }

    @Path("/")
    public static class SubTypeResource {
        @Produces("text/*;qs=0.5")
        @GET
        public String getWildcard() {
            return "*";
        }

        @Produces("text/plain;qs=0.6")
        @GET
        public String getPlain() {
            return "plain";
        }

        @Produces("text/html;qs=0.7")
        @GET
        public String getXml() {
            return "html";
        }
    }

    @Test
    public void testAcceptSubTypeDeclarative() throws Exception {
        runTestAcceptSubType(createApplication(AcceptQsTest.SubTypeResource.class));
    }

    @Test
    public void testAcceptSubTypeProgrammatic() throws Exception {
        final Resource.Builder rb = Resource.builder("/");
        rb.addMethod("GET").produces(MediaType.valueOf("text/*;qs=0.5")).handledBy(stringResponse("*"));
        rb.addMethod("GET").produces(MediaType.valueOf("text/plain;qs=0.6")).handledBy(stringResponse("plain"));
        rb.addMethod("GET").produces(MediaType.valueOf("text/html;qs=0.7")).handledBy(stringResponse("html"));
        ResourceConfig rc = new ResourceConfig();
        rc.registerResources(rb.build());
        runTestAcceptSubType(new org.glassfish.jersey.server.ApplicationHandler(rc));
    }

    @Path("/")
    public static class SubTypeResourceNotIntuitive {
        @Produces("text/*;qs=0.9")
        @GET
        public String getWildcard() {
            return "*";
        }

        @Produces("text/plain;qs=0.7")
        @GET
        public String getPlain() {
            return "plain";
        }

        @Produces("text/html;qs=0.5")
        @GET
        public String getXml() {
            return "html";
        }
    }

    @Test
    public void testAcceptSubTypeNotIntuitiveDeclarative() throws Exception {
        runTestAcceptSubTypeNotIntuitive(createApplication(AcceptQsTest.SubTypeResourceNotIntuitive.class));
    }

    @Test
    public void testAcceptSubTypeNotIntuitiveProgrammatic() throws Exception {
        final Resource.Builder rb = Resource.builder("/");
        rb.addMethod("GET").produces(MediaType.valueOf("text/*;qs=0.9")).handledBy(stringResponse("*"));
        rb.addMethod("GET").produces(MediaType.valueOf("text/plain;qs=0.7")).handledBy(stringResponse("plain"));
        rb.addMethod("GET").produces(MediaType.valueOf("text/html;qs=0.5")).handledBy(stringResponse("html"));
        ResourceConfig rc = new ResourceConfig();
        rc.registerResources(rb.build());
        runTestAcceptSubTypeNotIntuitive(new org.glassfish.jersey.server.ApplicationHandler(rc));
    }

    @Path("/")
    public static class NoProducesResource {
        @GET
        public String get() {
            return "GET";
        }
    }

    @Test
    public void testAcceptNoProducesDeclarative() throws Exception {
        runTestAcceptNoProduces(createApplication(AcceptQsTest.NoProducesResource.class));
    }

    @Test
    public void testAcceptNoProducesProgrammatic() throws Exception {
        final Resource.Builder rb = Resource.builder("/");
        rb.addMethod("GET").handledBy(stringResponse("GET"));
        ResourceConfig rc = new ResourceConfig();
        rc.registerResources(rb.build());
        runTestAcceptNoProduces(new org.glassfish.jersey.server.ApplicationHandler(rc));
    }

    @Path("/")
    public static class ProducesOneMethodFooBarResource {
        @GET
        @Produces({ "application/foo;qs=0.1", "application/bar" })
        public String get() {
            return "FOOBAR";
        }
    }

    @Test
    public void testProducesOneMethodFooBarResourceDeclarative() throws Exception {
        runTestFooBar(createApplication(AcceptQsTest.ProducesOneMethodFooBarResource.class), "FOOBAR", "FOOBAR");
    }

    @Test
    public void testProducesOneMethodFooBarResourceProgrammatic() throws Exception {
        final Resource.Builder rb = Resource.builder("/");
        rb.addMethod("GET").produces(MediaType.valueOf("application/foo;qs=0.1"), MediaType.valueOf("application/bar")).handledBy(stringResponse("FOOBAR"));
        ResourceConfig rc = new ResourceConfig();
        rc.registerResources(rb.build());
        runTestFooBar(new org.glassfish.jersey.server.ApplicationHandler(rc), "FOOBAR", "FOOBAR");
    }

    @Path("/")
    public static class ProducesTwoMethodsFooBarResource {
        @GET
        @Produces("application/foo;qs=0.1")
        public String getFoo() {
            return "FOO";
        }

        @GET
        @Produces("application/bar")
        public String getBar() {
            return "BAR";
        }
    }

    @Test
    public void testProducesTwoMethodsFooBarResourceProgrammatic() throws Exception {
        final Resource.Builder rb = Resource.builder("/");
        rb.addMethod("GET").produces(MediaType.valueOf("application/foo;qs=0.1")).handledBy(stringResponse("FOO"));
        rb.addMethod("GET").produces(MediaType.valueOf("application/bar")).handledBy(stringResponse("BAR"));
        ResourceConfig rc = new ResourceConfig();
        rc.registerResources(rb.build());
        runTestFooBar(new org.glassfish.jersey.server.ApplicationHandler(rc), "FOO", "BAR");
    }

    @Test
    public void testProducesTwoMethodsFooBarResourceDeclarative() throws Exception {
        runTestFooBar(createApplication(AcceptQsTest.ProducesTwoMethodsFooBarResource.class), "FOO", "BAR");
    }

    @Path("/")
    public static class ProducesTwoMethodsBarFooResource {
        @GET
        @Produces("application/bar")
        public String getBar() {
            return "BAR";
        }

        @GET
        @Produces("application/foo;qs=0.1")
        public String getFoo() {
            return "FOO";
        }
    }

    @Test
    public void testProducesTwoMethodsBarFooResourceProgrammatic() throws Exception {
        final Resource.Builder rb = Resource.builder("/");
        rb.addMethod("GET").produces(MediaType.valueOf("application/bar")).handledBy(stringResponse("BAR"));
        rb.addMethod("GET").produces(MediaType.valueOf("application/foo;qs=0.1")).handledBy(stringResponse("FOO"));
        ResourceConfig rc = new ResourceConfig();
        rc.registerResources(rb.build());
        runTestFooBar(new org.glassfish.jersey.server.ApplicationHandler(rc), "FOO", "BAR");
    }

    @Test
    public void testProducesTwoMethodsBarFooResourceDeclarative() throws Exception {
        runTestFooBar(createApplication(AcceptQsTest.ProducesTwoMethodsBarFooResource.class), "FOO", "BAR");
    }
}

