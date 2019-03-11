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


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.inject.Inject;
import javax.inject.Qualifier;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.inject.hk2.Hk2RequestScope;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.AnnotationLiteral;
import org.glassfish.jersey.internal.inject.Binder;
import org.glassfish.jersey.internal.inject.Bindings;
import org.glassfish.jersey.internal.inject.ClassBinding;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.internal.inject.InstanceBinding;
import org.glassfish.jersey.process.internal.RequestScope;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Pavel Bucek (pavel.bucek at oracle.com)
 */
public class CustomInjectablesApplicationTest extends JerseyTest {
    public static class MyApplication extends Application {
        public static Set<Class<?>> classes = new HashSet<Class<?>>() {
            {
                add(CustomInjectablesApplicationTest.Resource.class);
            }
        };

        @Inject
        public MyApplication(InjectionManager injectionManager) {
            System.out.println("Registering injectables...");
            ClassBinding<CustomInjectablesApplicationTest.MyInjectablePerRequest> injectClassRequest = Bindings.serviceAsContract(CustomInjectablesApplicationTest.MyInjectablePerRequest.class).in(RequestScoped.class);
            ClassBinding<CustomInjectablesApplicationTest.MyInjectableSingleton> injectClassSingleton = Bindings.serviceAsContract(CustomInjectablesApplicationTest.MyInjectableSingleton.class).in(Singleton.class);
            InstanceBinding<CustomInjectablesApplicationTest.MyInjectableSingleton> injectInstanceSingleton = Bindings.serviceAsContract(new CustomInjectablesApplicationTest.MyInjectableSingleton());
            ClassBinding<CustomInjectablesApplicationTest.MyInjectablePerRequest> injectQualifiedClassRequest = Bindings.serviceAsContract(CustomInjectablesApplicationTest.MyInjectablePerRequest.class).qualifiedBy(new CustomInjectablesApplicationTest.MyQualifierImpl()).in(RequestScoped.class);
            injectionManager.register(Arrays.asList(injectClassRequest, injectClassSingleton, injectInstanceSingleton, injectQualifiedClassRequest));
        }

        @Override
        public Set<Class<?>> getClasses() {
            return CustomInjectablesApplicationTest.MyApplication.classes;
        }
    }

    public static class MyInjectablePerRequest {
        public int i = 0;
    }

    @Singleton
    public static class MyInjectableSingleton {
        public int i = 0;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @Qualifier
    public static @interface MyQualifier {}

    private static class MyQualifierImpl extends AnnotationLiteral<CustomInjectablesApplicationTest.MyQualifier> implements CustomInjectablesApplicationTest.MyQualifier {}

    @Path("/")
    public static class Resource {
        @Inject
        CustomInjectablesApplicationTest.MyInjectablePerRequest myInjectablePerRequest;

        @Inject
        CustomInjectablesApplicationTest.MyInjectableSingleton myInjectableSingleton;

        @Inject
        @CustomInjectablesApplicationTest.MyQualifier
        CustomInjectablesApplicationTest.MyInjectablePerRequest myInjectablePerRequest2;

        @GET
        @Path("/perrequest")
        public String getAndIncPerRequest() {
            return Integer.valueOf((++(myInjectablePerRequest.i))).toString();
        }

        @GET
        @Path("/perrequestCustomQualifier")
        public String getAndIncPerRequest2() {
            return Integer.valueOf((++(myInjectablePerRequest2.i))).toString();
        }

        @GET
        @Path("/singleton")
        @Produces("text/plain")
        public String getAndIncSingleton() {
            System.out.println(myInjectableSingleton);
            return Integer.valueOf((++(myInjectableSingleton.i))).toString();
        }
    }

    @Test
    public void testPerRequest() throws Exception {
        final WebTarget perrequest = target().path("perrequest");
        Assert.assertEquals("1", perrequest.request().get(String.class));
        Assert.assertEquals("1", perrequest.request().get(String.class));
        Assert.assertEquals("1", perrequest.request().get(String.class));
    }

    @Test
    public void testSingleton() throws Exception {
        final WebTarget perrequest = target().path("singleton");
        Assert.assertEquals("1", perrequest.request().get(String.class));
        Assert.assertEquals("2", perrequest.request().get(String.class));
        Assert.assertEquals("3", perrequest.request().get(String.class));
    }

    @Test
    public void testCustomQualifier() throws Exception {
        final WebTarget perrequestCustomAnnotation = target().path("perrequestCustomQualifier");
        Assert.assertEquals("1", perrequestCustomAnnotation.request().get(String.class));
        Assert.assertEquals("1", perrequestCustomAnnotation.request().get(String.class));
        Assert.assertEquals("1", perrequestCustomAnnotation.request().get(String.class));
    }

    @Test
    public void plainHK2Test() throws Exception {
        final InjectionManager injectionManager = Injections.createInjectionManager(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(Hk2RequestScope.class).to(RequestScope.class).in(Singleton.class);
                bindAsContract(CustomInjectablesApplicationTest.MyInjectablePerRequest.class).in(RequestScoped.class);
                bindAsContract(CustomInjectablesApplicationTest.MyInjectableSingleton.class).in(Singleton.class);
            }
        });
        injectionManager.completeRegistration();
        final RequestScope requestScope = injectionManager.getInstance(RequestScope.class);
        final CustomInjectablesApplicationTest.MyInjectableSingleton myInjectableSingleton = injectionManager.getInstance(CustomInjectablesApplicationTest.MyInjectableSingleton.class);
        Assert.assertEquals(myInjectableSingleton, injectionManager.getInstance(CustomInjectablesApplicationTest.MyInjectableSingleton.class));
        final CustomInjectablesApplicationTest.MyInjectablePerRequest myInjectablePerRequest = requestScope.runInScope(new Callable<CustomInjectablesApplicationTest.MyInjectablePerRequest>() {
            @Override
            public CustomInjectablesApplicationTest.MyInjectablePerRequest call() throws Exception {
                final CustomInjectablesApplicationTest.MyInjectablePerRequest myInjectablePerRequest = injectionManager.getInstance(CustomInjectablesApplicationTest.MyInjectablePerRequest.class);
                Assert.assertEquals(myInjectablePerRequest, injectionManager.getInstance(CustomInjectablesApplicationTest.MyInjectablePerRequest.class));
                return myInjectablePerRequest;
            }
        });
        requestScope.runInScope(new Runnable() {
            @Override
            public void run() {
                Assert.assertNotSame(myInjectablePerRequest, injectionManager.getInstance(CustomInjectablesApplicationTest.MyInjectablePerRequest.class));
            }
        });
    }

    @Test
    public void plainHK2DynamicTest() throws Exception {
        Binder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(Hk2RequestScope.class).to(RequestScope.class).in(Singleton.class);
                bindAsContract(CustomInjectablesApplicationTest.MyInjectablePerRequest.class).in(RequestScoped.class);
                bindAsContract(CustomInjectablesApplicationTest.MyInjectableSingleton.class).in(Singleton.class);
            }
        };
        InjectionManager injectionManager = Injections.createInjectionManager();
        injectionManager.register(binder);
        injectionManager.completeRegistration();
        final RequestScope requestScope = injectionManager.getInstance(RequestScope.class);
        final CustomInjectablesApplicationTest.MyInjectableSingleton myInjectableSingleton = injectionManager.getInstance(CustomInjectablesApplicationTest.MyInjectableSingleton.class);
        Assert.assertEquals(myInjectableSingleton, injectionManager.getInstance(CustomInjectablesApplicationTest.MyInjectableSingleton.class));
        final CustomInjectablesApplicationTest.MyInjectablePerRequest myInjectablePerRequest = requestScope.runInScope(new Callable<CustomInjectablesApplicationTest.MyInjectablePerRequest>() {
            @Override
            public CustomInjectablesApplicationTest.MyInjectablePerRequest call() throws Exception {
                final CustomInjectablesApplicationTest.MyInjectablePerRequest myInjectablePerRequest = injectionManager.getInstance(CustomInjectablesApplicationTest.MyInjectablePerRequest.class);
                Assert.assertEquals(myInjectablePerRequest, injectionManager.getInstance(CustomInjectablesApplicationTest.MyInjectablePerRequest.class));
                return myInjectablePerRequest;
            }
        });
        requestScope.runInScope(new Runnable() {
            @Override
            public void run() {
                Assert.assertNotSame(myInjectablePerRequest, injectionManager.getInstance(CustomInjectablesApplicationTest.MyInjectablePerRequest.class));
            }
        });
    }
}

