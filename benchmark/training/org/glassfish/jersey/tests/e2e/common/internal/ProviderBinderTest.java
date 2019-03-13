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
package org.glassfish.jersey.tests.e2e.common.internal;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.RuntimeDelegate;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.internal.inject.ProviderBinder;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.tests.e2e.common.TestRuntimeDelegate;
import org.junit.Assert;
import org.junit.Test;


/**
 * ServiceProviders unit test.
 *
 * @author Santiago Pericas-Geertsen (santiago.pericasgeertsen at oracle.com)
 * @author Marek Potociar (marek.potociar at oracle.com)
 * @author Libor Kramolis (libor.kramolis at oracle.com)
 */
public class ProviderBinderTest {
    private static class MyProvider implements MessageBodyReader , MessageBodyWriter {
        @Override
        public boolean isReadable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Object readFrom(Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public long getSize(Object t, Class type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void writeTo(Object t, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public ProviderBinderTest() {
        RuntimeDelegate.setInstance(new TestRuntimeDelegate());
    }

    @Test
    public void testServicesNotEmpty() {
        InjectionManager injectionManager = Injections.createInjectionManager(ProviderBinderTest.initBinders());
        injectionManager.completeRegistration();
        Set<MessageBodyReader> providers = Providers.getProviders(injectionManager, MessageBodyReader.class);
        Assert.assertTrue(((providers.size()) > 0));
    }

    @Test
    public void testServicesMbr() {
        InjectionManager injectionManager = Injections.createInjectionManager(ProviderBinderTest.initBinders());
        injectionManager.completeRegistration();
        Set<MessageBodyReader> providers = Providers.getProviders(injectionManager, MessageBodyReader.class);
        Assert.assertTrue(((providers.size()) > 0));
    }

    @Test
    public void testServicesMbw() {
        InjectionManager injectionManager = Injections.createInjectionManager(ProviderBinderTest.initBinders());
        injectionManager.completeRegistration();
        Set<MessageBodyWriter> providers = Providers.getProviders(injectionManager, MessageBodyWriter.class);
        Assert.assertTrue(((providers.size()) > 0));
    }

    @Test
    public void testProvidersMbr() {
        InjectionManager injectionManager = Injections.createInjectionManager(ProviderBinderTest.initBinders());
        ProviderBinder providerBinder = new ProviderBinder(injectionManager);
        providerBinder.bindClasses(Collections.singleton(ProviderBinderTest.MyProvider.class));
        injectionManager.completeRegistration();
        Set<MessageBodyReader> providers = Providers.getCustomProviders(injectionManager, MessageBodyReader.class);
        Assert.assertEquals(1, instancesOfType(ProviderBinderTest.MyProvider.class, providers).size());
    }

    @Test
    public void testProvidersMbw() {
        InjectionManager injectionManager = Injections.createInjectionManager(ProviderBinderTest.initBinders());
        ProviderBinder providerBinder = new ProviderBinder(injectionManager);
        providerBinder.bindClasses(Collections.singleton(ProviderBinderTest.MyProvider.class));
        injectionManager.completeRegistration();
        Set<MessageBodyWriter> providers = Providers.getCustomProviders(injectionManager, MessageBodyWriter.class);
        final Collection<ProviderBinderTest.MyProvider> myProviders = instancesOfType(ProviderBinderTest.MyProvider.class, providers);
        Assert.assertEquals(1, myProviders.size());
    }

    @Test
    public void testProvidersMbrInstance() {
        InjectionManager injectionManager = Injections.createInjectionManager(ProviderBinderTest.initBinders());
        ProviderBinder providerBinder = new ProviderBinder(injectionManager);
        providerBinder.bindInstances(Collections.singleton(new ProviderBinderTest.MyProvider()));
        injectionManager.completeRegistration();
        Set<MessageBodyReader> providers = Providers.getCustomProviders(injectionManager, MessageBodyReader.class);
        Assert.assertEquals(1, instancesOfType(ProviderBinderTest.MyProvider.class, providers).size());
    }

    @Test
    public void testProvidersMbwInstance() {
        InjectionManager injectionManager = Injections.createInjectionManager(ProviderBinderTest.initBinders());
        ProviderBinder providerBinder = new ProviderBinder(injectionManager);
        providerBinder.bindInstances(Collections.singleton(new ProviderBinderTest.MyProvider()));
        injectionManager.completeRegistration();
        Set<MessageBodyWriter> providers = Providers.getCustomProviders(injectionManager, MessageBodyWriter.class);
        Assert.assertEquals(instancesOfType(ProviderBinderTest.MyProvider.class, providers).size(), 1);
    }

    @Test
    public void testCustomRegistration() {
        InjectionManager injectionManager = Injections.createInjectionManager();
        ProviderBinder providerBinder = new ProviderBinder(injectionManager);
        providerBinder.bindClasses(ProviderBinderTest.Child.class);
        providerBinder.bindClasses(ProviderBinderTest.NotFilterChild.class);
        injectionManager.completeRegistration();
        ContainerRequestFilter requestFilter = getRequestFilter(injectionManager);
        ContainerRequestFilter requestFilter2 = getRequestFilter(injectionManager);
        Assert.assertEquals(requestFilter, requestFilter2);
        ContainerResponseFilter responseFilter = getResponseFilter(injectionManager);
        ContainerResponseFilter responseFilter2 = getResponseFilter(injectionManager);
        Assert.assertTrue((responseFilter == responseFilter2));
        Assert.assertTrue((responseFilter == requestFilter));
        // only one filter should be registered
        Collection<ContainerResponseFilter> filters = Providers.getCustomProviders(injectionManager, ContainerResponseFilter.class);
        Assert.assertEquals(1, filters.size());
        ProviderBinderTest.Child child = injectionManager.getInstance(ProviderBinderTest.Child.class);
        ProviderBinderTest.Child child2 = injectionManager.getInstance(ProviderBinderTest.Child.class);
        Assert.assertTrue((child != responseFilter));
        Assert.assertTrue((child == child2));
    }

    interface ParentInterface {}

    interface ChildInterface extends ProviderBinderTest.ChildSuperInterface {}

    interface SecondChildInterface {}

    interface ChildSuperInterface extends ContainerResponseFilter {}

    @Singleton
    public static class Parent implements ContainerRequestFilter , ProviderBinderTest.ParentInterface {
        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
        }
    }

    @Singleton
    public static class Child extends ProviderBinderTest.Parent implements ProviderBinderTest.ChildInterface , ProviderBinderTest.SecondChildInterface {
        @Override
        public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        }
    }

    private static class NotFilterChild implements ProviderBinderTest.ParentInterface {}
}

