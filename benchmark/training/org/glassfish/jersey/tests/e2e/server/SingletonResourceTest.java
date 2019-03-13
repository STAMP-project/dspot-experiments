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
package org.glassfish.jersey.tests.e2e.server;


import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.internal.inject.PerLookup;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Class testing Resources managed as singletons.
 *
 * @author Miroslav Fuksa
 */
public class SingletonResourceTest extends JerseyTest {
    @Test
    public void singletonResourceTest() {
        String str;
        str = target().path("singleton").request().get().readEntity(String.class);
        Assert.assertEquals("res:1", str);
        str = target().path("singleton").request().get().readEntity(String.class);
        Assert.assertEquals("res:2", str);
        str = target().path("singleton/sub").request().get().readEntity(String.class);
        Assert.assertEquals("sub:1", str);
        str = target().path("singleton").request().get().readEntity(String.class);
        Assert.assertEquals("res:3", str);
        str = target().path("singleton/sub").request().get().readEntity(String.class);
        Assert.assertEquals("sub:2", str);
        str = target().path("singleton/sub-not-singleton").request().get().readEntity(String.class);
        Assert.assertEquals("not-singleton:1", str);
        str = target().path("singleton/sub-not-singleton").request().get().readEntity(String.class);
        Assert.assertEquals("not-singleton:1", str);
        str = target().path("singleton/instance").request().get().readEntity(String.class);
        Assert.assertEquals("sub:1", str);
        str = target().path("singleton/instance").request().get().readEntity(String.class);
        Assert.assertEquals("sub:1", str);
        str = target().path("singleton/sub").request().get().readEntity(String.class);
        Assert.assertEquals("sub:3", str);
        // one instance
        str = target().path("programmatic").path("instance").request().get().readEntity(String.class);
        Assert.assertEquals("prg-instance:1", str);
        str = target().path("programmatic").path("instance").request().get().readEntity(String.class);
        Assert.assertEquals("prg-instance:2", str);
        // singleton
        str = target().path("programmatic").path("singleton").request().get().readEntity(String.class);
        Assert.assertEquals("prg-singleton:1", str);
        str = target().path("programmatic").path("singleton").request().get().readEntity(String.class);
        Assert.assertEquals("prg-singleton:2", str);
        // request to the SubResourceSingleton (same class as sub resource on path "singleton/sub")
        str = target().path("programmatic").path("reused-singleton").request().get().readEntity(String.class);
        Assert.assertEquals("reused-singleton:4", str);
        // not singleton
        str = target().path("programmatic").path("not-singleton").request().get().readEntity(String.class);
        Assert.assertEquals("prg-not-singleton:1", str);
        str = target().path("programmatic").path("not-singleton").request().get().readEntity(String.class);
        Assert.assertEquals("prg-not-singleton:1", str);
    }

    @Test
    public void singletonAnnotationInheritedTest() {
        // Singleton annotation is not inherited
        String str;
        str = target().path("inherit").request().get().readEntity(String.class);
        Assert.assertEquals("inherit:1", str);
        str = target().path("inherit").request().get().readEntity(String.class);
        Assert.assertEquals("inherit:1", str);
    }

    @Test
    public void singletonAnnotationInterfaceTest() {
        // Singleton annotation is not inherited
        String str;
        str = target().path("interface").request().get().readEntity(String.class);
        Assert.assertEquals("interface:1", str);
        str = target().path("interface").request().get().readEntity(String.class);
        Assert.assertEquals("interface:1", str);
    }

    /**
     * Tests that resources are by default managed in {@link org.glassfish.jersey.process.internal.RequestScope request scope}.
     */
    @Test
    public void testResourceInRequestScope() {
        String str = target().path("testScope/request").request().get().readEntity(String.class);
        Assert.assertEquals("same-instances", str);
    }

    @Test
    public void testResourceInPerLookupScope() {
        String str = target().path("testScope/perlookup").request().get().readEntity(String.class);
        Assert.assertEquals("different-instances", str);
    }

    @Test
    public void testResourceInSingletonScope() {
        String str = target().path("testScope/singleton").request().get().readEntity(String.class);
        Assert.assertEquals("same-instances", str);
    }

    @Path("test-requestScope")
    public static class RequestScopeResource {
        public String get() {
            return "get";
        }
    }

    @Path("test-perlookupScope")
    @PerLookup
    public static class PerLookupScopeResource {
        public String get() {
            return "get";
        }
    }

    @Path("test-singletonScope")
    @Singleton
    public static class SingletonScopeResource {
        public String get() {
            return "get";
        }
    }

    @Path("testScope")
    public static class TestResource {
        @Inject
        InjectionManager injectionManager;

        private String compareInstances(Class<?> clazz) {
            final Object res1 = Injections.getOrCreate(injectionManager, clazz);
            final Object res2 = Injections.getOrCreate(injectionManager, clazz);
            return res1 == res2 ? "same-instances" : "different-instances";
        }

        @GET
        @Path("request")
        public String compareRequestScopedInstances() {
            return compareInstances(SingletonResourceTest.RequestScopeResource.class);
        }

        @GET
        @Path("perlookup")
        public String comparePerLookupScopedInstances() {
            return compareInstances(SingletonResourceTest.PerLookupScopeResource.class);
        }

        @GET
        @Path("singleton")
        public String compareSingletonInstances() {
            return compareInstances(SingletonResourceTest.SingletonScopeResource.class);
        }
    }

    @Singleton
    public static class Parent {}

    @Path("inherit")
    public static class ChildInheritsParentAnnotation extends SingletonResourceTest.Parent {
        private int counter = 1;

        @GET
        public String get() {
            return "inherit:" + ((counter)++);
        }
    }

    @Singleton
    public static interface AnnotatedBySingleton {}

    @Path("interface")
    public static class ChildImplementsInterfaceAnnotation implements SingletonResourceTest.AnnotatedBySingleton {
        private int counter = 1;

        @GET
        public String get() {
            return "interface:" + ((counter)++);
        }
    }

    @Singleton
    public static class SingletonProgrammatic implements Inflector<Request, Response> {
        private int counter = 1;

        @Override
        public Response apply(Request data) {
            return Response.ok(("prg-singleton:" + ((counter)++))).build();
        }
    }

    public static class NotSingletonProgrammatic implements Inflector<Request, Response> {
        private int counter = 1;

        @Override
        public Response apply(Request data) {
            return Response.ok(("prg-not-singleton:" + ((counter)++))).build();
        }
    }

    @Singleton
    @Path("singleton")
    public static class SingletonResource {
        private int counter = 1;

        @GET
        @Produces("text/html")
        public String getCounter() {
            return "res:" + ((counter)++);
        }

        @Path("sub")
        public Class getSubResourceSingleton() {
            return SingletonResourceTest.SubResourceSingleton.class;
        }

        @Path("sub-not-singleton")
        public Class getSubResource() {
            return SingletonResourceTest.SubResource.class;
        }

        @Path("instance")
        public Object getSubResourceInstance() {
            return new SingletonResourceTest.SubResourceSingleton();
        }

        @GET
        @Path("filter")
        public String getCounterFromFilter(@HeaderParam("counter")
        int counter) {
            return "filter:" + counter;
        }
    }

    @Singleton
    public static class SubResourceSingleton implements Inflector<Request, Response> {
        private int counter = 1;

        @GET
        public String getInternalCounter() {
            return "sub:" + ((counter)++);
        }

        @Override
        public Response apply(Request request) {
            return Response.ok(("reused-singleton:" + ((counter)++))).build();
        }
    }

    public static class SubResource {
        private int counter = 1;

        @GET
        public String getInternalCounter() {
            return "not-singleton:" + ((counter)++);
        }
    }
}

