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
package org.glassfish.jersey.tests.e2e.common.model.internal;


import ContractProvider.NO_PRIORITY;
import Priorities.AUTHENTICATION;
import Priorities.USER;
import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import org.glassfish.jersey.inject.hk2.Hk2InjectionManagerFactory;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.internal.inject.ProviderBinder;
import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.model.ContractProvider;
import org.glassfish.jersey.model.internal.CommonConfig;
import org.glassfish.jersey.model.internal.ComponentBag;
import org.glassfish.jersey.model.internal.ManagedObjectsFinalizer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Test cases for {@link javax.ws.rs.core.Configuration}.
 *
 * @author Michal Gajdos
 */
public class CommonConfigTest {
    private CommonConfig config;

    @Test
    public void testGetProperties() throws Exception {
        try {
            config.getConfiguration().getProperties().put("foo", "bar");
            Assert.fail("Returned properties collection should be immutable.");
        } catch (final Exception e) {
            // OK.
        }
    }

    @Test
    public void testSetProperties() throws Exception {
        config = config.property("foo", "bar");
        Assert.assertEquals("bar", config.getConfiguration().getProperty("foo"));
        final Map<String, String> properties = new HashMap<>();
        properties.put("hello", "world");
        config = config.setProperties(properties);
        Assert.assertEquals(1, config.getConfiguration().getProperties().size());
        Assert.assertEquals("world", config.getConfiguration().getProperty("hello"));
        properties.put("one", "two");
        Assert.assertEquals(1, config.getConfiguration().getProperties().size());
        Assert.assertNull(config.getConfiguration().getProperty("one"));
        config = config.setProperties(new HashMap());
        Assert.assertTrue(config.getConfiguration().getProperties().isEmpty());
    }

    @Test
    public void testSetGetProperty() throws Exception {
        config = config.property("foo", "bar");
        Assert.assertEquals("bar", config.getConfiguration().getProperty("foo"));
        config.property("hello", "world");
        config.property("foo", null);
        Assert.assertEquals(null, config.getConfiguration().getProperty("foo"));
        Assert.assertEquals(1, config.getConfiguration().getProperties().size());
    }

    public static class EmptyFeature implements Feature {
        @Override
        public boolean configure(final FeatureContext configuration) {
            return true;
        }
    }

    public static class UnconfigurableFeature implements Feature {
        @Override
        public boolean configure(final FeatureContext configuration) {
            return false;
        }
    }

    public static class ComplexEmptyProvider implements ContainerRequestFilter , ExceptionMapper , ReaderInterceptor {
        @Override
        public void filter(final ContainerRequestContext requestContext) throws IOException {
            // Do nothing.
        }

        @Override
        public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
            return context.proceed();
        }

        @Override
        public Response toResponse(final Throwable exception) {
            throw new UnsupportedOperationException();
        }
    }

    public static class ComplexEmptyProviderFeature extends CommonConfigTest.ComplexEmptyProvider implements Feature {
        @Override
        public boolean configure(final FeatureContext configuration) {
            throw new UnsupportedOperationException();
        }
    }

    @Test
    public void testReplaceWith() throws Exception {
        config.property("foo", "bar");
        final CommonConfigTest.EmptyFeature emptyFeature = new CommonConfigTest.EmptyFeature();
        config.register(emptyFeature);
        config.register(CommonConfigTest.ComplexEmptyProvider.class, ExceptionMapper.class);
        final CommonConfig other = new CommonConfig(null, ComponentBag.INCLUDE_ALL);
        other.property("foo", "baz");
        other.register(CommonConfigTest.UnconfigurableFeature.class);
        other.register(CommonConfigTest.ComplexEmptyProvider.class, ReaderInterceptor.class, ContainerRequestFilter.class);
        Assert.assertEquals("baz", other.getProperty("foo"));
        Assert.assertEquals(1, other.getProperties().size());
        Assert.assertEquals(2, other.getClasses().size());
        Assert.assertEquals(0, other.getInstances().size());
        Assert.assertEquals(2, other.getContracts(CommonConfigTest.ComplexEmptyProvider.class).size());
        Assert.assertTrue(other.getContracts(CommonConfigTest.ComplexEmptyProvider.class).containsKey(ReaderInterceptor.class));
        Assert.assertTrue(other.getContracts(CommonConfigTest.ComplexEmptyProvider.class).containsKey(ContainerRequestFilter.class));
        other.loadFrom(config);
        Assert.assertEquals("bar", other.getProperty("foo"));
        Assert.assertEquals(1, other.getProperties().size());
        Assert.assertEquals(1, other.getClasses().size());
        Assert.assertEquals(1, other.getInstances().size());
        Assert.assertEquals(1, other.getContracts(CommonConfigTest.ComplexEmptyProvider.class).size());
        Assert.assertSame(ExceptionMapper.class, other.getContracts(CommonConfigTest.ComplexEmptyProvider.class).keySet().iterator().next());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetFeatures() throws Exception {
        final CommonConfigTest.EmptyFeature emptyFeature = new CommonConfigTest.EmptyFeature();
        final CommonConfigTest.UnconfigurableFeature unconfigurableFeature = new CommonConfigTest.UnconfigurableFeature();
        final CommonConfigTest.ComplexEmptyProviderFeature providerFeature = new CommonConfigTest.ComplexEmptyProviderFeature();
        config.register(emptyFeature);
        config.register(unconfigurableFeature);
        config.register(providerFeature, ReaderInterceptor.class);
        Assert.assertFalse(config.getConfiguration().isEnabled(emptyFeature));
        Assert.assertFalse(config.getConfiguration().isEnabled(unconfigurableFeature));
        Assert.assertFalse(config.getConfiguration().isEnabled(providerFeature));
        Assert.assertTrue(config.getConfiguration().isRegistered(emptyFeature));
        Assert.assertTrue(config.getConfiguration().isRegistered(unconfigurableFeature));
        Assert.assertTrue(config.getConfiguration().isRegistered(providerFeature));
    }

    // Regression test for JERSEY-1638.
    @Test
    public void testGetNonExistentProviderContractsASEmptyMap() throws Exception {
        Assert.assertTrue(config.getConfiguration().getContracts(CommonConfigTest.class).isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetClasses() throws Exception {
        _testCollectionsCommon("GetProviderClasses", config.getClasses(), CommonConfigTest.EmptyFeature.class);
        config.register(CommonConfigTest.ComplexEmptyProviderFeature.class, WriterInterceptor.class, ReaderInterceptor.class, ContainerRequestFilter.class);
        Assert.assertEquals(1, config.getClasses().size());
        config.register(CommonConfigTest.EmptyFeature.class);
        final Set<Class<?>> providerClasses = config.getClasses();
        Assert.assertEquals(2, providerClasses.size());
        Assert.assertTrue(providerClasses.contains(CommonConfigTest.ComplexEmptyProviderFeature.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetInstances() throws Exception {
        _testCollectionsCommon("GetProviderInstances", config.getInstances(), new CommonConfigTest.EmptyFeature());
        final CommonConfigTest.ComplexEmptyProviderFeature providerFeature1 = new CommonConfigTest.ComplexEmptyProviderFeature();
        config.register(providerFeature1, WriterInterceptor.class);
        Assert.assertEquals(1, config.getInstances().size());
        Assert.assertTrue(config.getInstances().contains(providerFeature1));
        final CommonConfigTest.EmptyFeature emptyFeature = new CommonConfigTest.EmptyFeature();
        config.register(emptyFeature);
        Assert.assertEquals(2, config.getInstances().size());
        Assert.assertTrue(config.getInstances().contains(emptyFeature));
        final CommonConfigTest.ComplexEmptyProviderFeature providerFeature2 = new CommonConfigTest.ComplexEmptyProviderFeature();
        config.register(providerFeature2, ReaderInterceptor.class, ContainerRequestFilter.class);
        Assert.assertEquals(2, config.getInstances().size());
        Assert.assertFalse(config.getInstances().contains(providerFeature2));
    }

    @Test
    public void testRegisterClass() throws Exception {
        try {
            final Class clazz = null;
            // noinspection ConstantConditions
            config.register(clazz);
            Assert.fail("Cannot register null.");
        } catch (final IllegalArgumentException e) {
            // OK.
        }
        for (int i = 0; i < 2; i++) {
            config.register(CommonConfigTest.ComplexEmptyProvider.class);
        }
        final ContractProvider contractProvider = config.getComponentBag().getModel(CommonConfigTest.ComplexEmptyProvider.class);
        final Set<Class<?>> contracts = contractProvider.getContracts();
        Assert.assertEquals(3, contracts.size());
        Assert.assertTrue(contracts.contains(ReaderInterceptor.class));
        Assert.assertTrue(contracts.contains(ContainerRequestFilter.class));
        Assert.assertTrue(contracts.contains(ExceptionMapper.class));
        Assert.assertTrue(config.isRegistered(CommonConfigTest.ComplexEmptyProvider.class));
    }

    @Test
    public void testRegisterInstance() throws Exception {
        try {
            config.register(null);
            Assert.fail("Cannot register null.");
        } catch (final IllegalArgumentException e) {
            // OK.
        }
        final CommonConfigTest.ComplexEmptyProvider[] ceps = new CommonConfigTest.ComplexEmptyProvider[2];
        for (int i = 0; i < 2; i++) {
            ceps[i] = new CommonConfigTest.ComplexEmptyProvider();
            config.register(ceps[i]);
        }
        final ContractProvider contractProvider = config.getComponentBag().getModel(CommonConfigTest.ComplexEmptyProvider.class);
        final Set<Class<?>> contracts = contractProvider.getContracts();
        Assert.assertEquals(3, contracts.size());
        Assert.assertTrue(contracts.contains(ReaderInterceptor.class));
        Assert.assertTrue(contracts.contains(ContainerRequestFilter.class));
        Assert.assertTrue(contracts.contains(ExceptionMapper.class));
        Assert.assertTrue(config.isRegistered(CommonConfigTest.ComplexEmptyProvider.class));
        Assert.assertTrue(config.isRegistered(ceps[0]));
        Assert.assertFalse(config.isRegistered(ceps[1]));
    }

    @Test
    public void testRegisterClassInstanceClash() throws Exception {
        final CommonConfigTest.ComplexEmptyProvider complexEmptyProvider = new CommonConfigTest.ComplexEmptyProvider();
        config.register(CommonConfigTest.ComplexEmptyProvider.class);
        config.register(complexEmptyProvider);
        config.register(CommonConfigTest.ComplexEmptyProvider.class);
        Assert.assertTrue(config.getClasses().contains(CommonConfigTest.ComplexEmptyProvider.class));
        Assert.assertFalse(config.getInstances().contains(complexEmptyProvider));
        final ContractProvider contractProvider = config.getComponentBag().getModel(CommonConfigTest.ComplexEmptyProvider.class);
        final Set<Class<?>> contracts = contractProvider.getContracts();
        Assert.assertEquals(3, contracts.size());
        Assert.assertTrue(contracts.contains(ReaderInterceptor.class));
        Assert.assertTrue(contracts.contains(ContainerRequestFilter.class));
        Assert.assertTrue(contracts.contains(ExceptionMapper.class));
    }

    @Test
    public void testRegisterClassBingingPriority() throws Exception {
        try {
            final Class clazz = null;
            // noinspection ConstantConditions
            config.register(clazz, USER);
            Assert.fail("Cannot register null.");
        } catch (final IllegalArgumentException e) {
            // OK.
        }
        for (final int priority : new int[]{ Priorities.USER, Priorities.AUTHENTICATION }) {
            config.register(CommonConfigTest.ComplexEmptyProvider.class, priority);
            final ContractProvider contractProvider = config.getComponentBag().getModel(CommonConfigTest.ComplexEmptyProvider.class);
            final Set<Class<?>> contracts = contractProvider.getContracts();
            Assert.assertEquals(3, contracts.size());
            Assert.assertTrue(contracts.contains(ReaderInterceptor.class));
            Assert.assertTrue(contracts.contains(ContainerRequestFilter.class));
            Assert.assertTrue(contracts.contains(ExceptionMapper.class));
            // All priorities are the same.
            Assert.assertEquals(USER, contractProvider.getPriority(ReaderInterceptor.class));
            Assert.assertEquals(USER, contractProvider.getPriority(ContainerRequestFilter.class));
            Assert.assertEquals(USER, contractProvider.getPriority(ExceptionMapper.class));
        }
    }

    @Test
    public void testRegisterInstanceBingingPriority() throws Exception {
        try {
            config.register(null, USER);
            Assert.fail("Cannot register null.");
        } catch (final IllegalArgumentException e) {
            // OK.
        }
        final Class<CommonConfigTest.ComplexEmptyProvider> providerClass = CommonConfigTest.ComplexEmptyProvider.class;
        for (final int priority : new int[]{ Priorities.USER, Priorities.AUTHENTICATION }) {
            config.register(providerClass, priority);
            final CommonConfig commonConfig = config;
            final ContractProvider contractProvider = commonConfig.getComponentBag().getModel(providerClass);
            final Set<Class<?>> contracts = contractProvider.getContracts();
            Assert.assertEquals(3, contracts.size());// Feature is not there.

            Assert.assertTrue(contracts.contains(ReaderInterceptor.class));
            Assert.assertTrue(contracts.contains(ContainerRequestFilter.class));
            Assert.assertTrue(contracts.contains(ExceptionMapper.class));
            // All priorities are the same.
            Assert.assertEquals(USER, contractProvider.getPriority(ReaderInterceptor.class));
            Assert.assertEquals(USER, contractProvider.getPriority(ContainerRequestFilter.class));
            Assert.assertEquals(USER, contractProvider.getPriority(ExceptionMapper.class));
        }
    }

    @Test
    public void testRegisterClassInstanceBindingPriorityClash() throws Exception {
        final CommonConfigTest.ComplexEmptyProvider complexEmptyProvider = new CommonConfigTest.ComplexEmptyProvider();
        config.register(CommonConfigTest.ComplexEmptyProvider.class, AUTHENTICATION);
        config.register(complexEmptyProvider, USER);
        Assert.assertTrue(config.getClasses().contains(CommonConfigTest.ComplexEmptyProvider.class));
        Assert.assertFalse(config.getInstances().contains(complexEmptyProvider));
        final ComponentBag componentBag = config.getComponentBag();
        final ContractProvider contractProvider = componentBag.getModel(CommonConfigTest.ComplexEmptyProvider.class);
        final Set<Class<?>> contracts = contractProvider.getContracts();
        Assert.assertEquals(3, contracts.size());// Feature is not there.

        Assert.assertTrue(contracts.contains(ReaderInterceptor.class));
        Assert.assertTrue(contracts.contains(ContainerRequestFilter.class));
        Assert.assertTrue(contracts.contains(ExceptionMapper.class));
        // All priorities are the same.
        Assert.assertEquals(AUTHENTICATION, contractProvider.getPriority(ReaderInterceptor.class));
        Assert.assertEquals(AUTHENTICATION, contractProvider.getPriority(ContainerRequestFilter.class));
        Assert.assertEquals(AUTHENTICATION, contractProvider.getPriority(ExceptionMapper.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterClassContracts() throws Exception {
        try {
            final Class clazz = null;
            // noinspection ConstantConditions
            config.register(clazz, ReaderInterceptor.class);
            Assert.fail("Cannot register null.");
        } catch (final IllegalArgumentException e) {
            // OK.
        }
        config.register(CommonConfigTest.ComplexEmptyProvider.class, ReaderInterceptor.class, ContainerRequestFilter.class, WriterInterceptor.class);
        final ContractProvider contractProvider = config.getComponentBag().getModel(CommonConfigTest.ComplexEmptyProvider.class);
        final Set<Class<?>> contracts = contractProvider.getContracts();
        Assert.assertEquals(2, contracts.size());
        Assert.assertTrue(((ReaderInterceptor.class) + " is not registered."), contracts.contains(ReaderInterceptor.class));
        Assert.assertTrue(((ContainerRequestFilter.class) + " is not registered."), contracts.contains(ContainerRequestFilter.class));
        Assert.assertFalse(((WriterInterceptor.class) + " should not be registered."), contracts.contains(WriterInterceptor.class));
        Assert.assertTrue(config.getInstances().isEmpty());
        Assert.assertTrue(config.getClasses().contains(CommonConfigTest.ComplexEmptyProvider.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterInstancesContracts() throws Exception {
        try {
            config.register(null, ReaderInterceptor.class);
            Assert.fail("Cannot register null.");
        } catch (final IllegalArgumentException e) {
            // OK.
        }
        final CommonConfigTest.ComplexEmptyProvider complexEmptyProvider = new CommonConfigTest.ComplexEmptyProvider();
        config.register(complexEmptyProvider, ReaderInterceptor.class, ContainerRequestFilter.class, WriterInterceptor.class);
        final ContractProvider contractProvider = config.getComponentBag().getModel(CommonConfigTest.ComplexEmptyProvider.class);
        final Set<Class<?>> contracts = contractProvider.getContracts();
        Assert.assertEquals(2, contracts.size());
        Assert.assertTrue(((ReaderInterceptor.class) + " is not registered."), contracts.contains(ReaderInterceptor.class));
        Assert.assertTrue(((ContainerRequestFilter.class) + " is not registered."), contracts.contains(ContainerRequestFilter.class));
        Assert.assertFalse(((WriterInterceptor.class) + " should not be registered."), contracts.contains(WriterInterceptor.class));
        Assert.assertTrue(config.getInstances().contains(complexEmptyProvider));
        Assert.assertTrue(config.getClasses().isEmpty());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterClassContractsFeatureNotInvoked() throws Exception {
        config.register(CommonConfigTest.ComplexEmptyProviderFeature.class, ReaderInterceptor.class);
        Assert.assertFalse(config.getConfiguration().isEnabled(CommonConfigTest.ComplexEmptyProviderFeature.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterInstancesContractsFeatureNotInvoked() throws Exception {
        final CommonConfigTest.ComplexEmptyProviderFeature feature = new CommonConfigTest.ComplexEmptyProviderFeature();
        config.register(feature, ReaderInterceptor.class);
        Assert.assertFalse(config.getConfiguration().isEnabled(CommonConfigTest.ComplexEmptyProviderFeature.class));
        Assert.assertFalse(config.getConfiguration().isEnabled(feature));
    }

    @Test
    public void testRegisterClassNullContracts() throws Exception {
        config.register(CommonConfigTest.ComplexEmptyProvider.class, ((Class) (null)));
        final ContractProvider contractProvider = config.getComponentBag().getModel(CommonConfigTest.ComplexEmptyProvider.class);
        final Set<Class<?>> contracts = contractProvider.getContracts();
        Assert.assertEquals(0, contracts.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRegisterInstanceNullContracts() throws Exception {
        config.register(new CommonConfigTest.ComplexEmptyProvider(), ((Class) (null)));
        final ContractProvider contractProvider = config.getComponentBag().getModel(CommonConfigTest.ComplexEmptyProvider.class);
        final Set<Class<?>> contracts = contractProvider.getContracts();
        Assert.assertEquals(0, contracts.size());
    }

    // Reproducer JERSEY-1637
    @Test
    public void testRegisterNullOrEmptyContracts() {
        final CommonConfigTest.ComplexEmptyProvider provider = new CommonConfigTest.ComplexEmptyProvider();
        config.register(CommonConfigTest.ComplexEmptyProvider.class, ((Class<?>[]) (null)));
        Assert.assertFalse(config.getConfiguration().isRegistered(CommonConfigTest.ComplexEmptyProvider.class));
        config.register(provider, ((Class<?>[]) (null)));
        Assert.assertFalse(config.getConfiguration().isRegistered(CommonConfigTest.ComplexEmptyProvider.class));
        Assert.assertFalse(config.getConfiguration().isRegistered(provider));
        config.register(CommonConfigTest.ComplexEmptyProvider.class, new Class[0]);
        Assert.assertFalse(config.getConfiguration().isRegistered(CommonConfigTest.ComplexEmptyProvider.class));
        config.register(provider, new Class[0]);
        Assert.assertFalse(config.getConfiguration().isRegistered(CommonConfigTest.ComplexEmptyProvider.class));
        Assert.assertFalse(config.getConfiguration().isRegistered(provider));
    }

    @Priority(300)
    public static class LowPriorityProvider implements ReaderInterceptor , WriterInterceptor {
        @Override
        public void aroundWriteTo(final WriterInterceptorContext context) throws IOException, WebApplicationException {
            // Do nothing.
        }

        @Override
        public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
            return context.proceed();
        }
    }

    @Priority(200)
    public static class MidPriorityProvider implements ReaderInterceptor , WriterInterceptor {
        @Override
        public void aroundWriteTo(final WriterInterceptorContext context) throws IOException, WebApplicationException {
            // Do nothing.
        }

        @Override
        public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
            return context.proceed();
        }
    }

    @Priority(100)
    public static class HighPriorityProvider implements ReaderInterceptor , WriterInterceptor {
        @Override
        public void aroundWriteTo(final WriterInterceptorContext context) throws IOException, WebApplicationException {
            // Do nothing.
        }

        @Override
        public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
            return context.proceed();
        }
    }

    @Test
    public void testProviderOrderManual() throws Exception {
        InjectionManager injectionManager = Injections.createInjectionManager();
        config.register(CommonConfigTest.MidPriorityProvider.class, 500);
        config.register(CommonConfigTest.LowPriorityProvider.class, 20);
        config.register(CommonConfigTest.HighPriorityProvider.class, 150);
        ProviderBinder.bindProviders(config.getComponentBag(), injectionManager);
        injectionManager.completeRegistration();
        final Iterable<WriterInterceptor> allProviders = Providers.getAllProviders(injectionManager, WriterInterceptor.class, new org.glassfish.jersey.model.internal.RankedComparator());
        final Iterator<WriterInterceptor> iterator = allProviders.iterator();
        Assert.assertEquals(CommonConfigTest.LowPriorityProvider.class, iterator.next().getClass());
        Assert.assertEquals(CommonConfigTest.HighPriorityProvider.class, iterator.next().getClass());
        Assert.assertEquals(CommonConfigTest.MidPriorityProvider.class, iterator.next().getClass());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testProviderOrderSemiAutomatic() throws Exception {
        InjectionManager injectionManager = Injections.createInjectionManager();
        config.register(CommonConfigTest.MidPriorityProvider.class, 50);
        config.register(CommonConfigTest.LowPriorityProvider.class, 2000);
        config.register(CommonConfigTest.HighPriorityProvider.class);
        ProviderBinder.bindProviders(config.getComponentBag(), injectionManager);
        injectionManager.completeRegistration();
        final Iterable<WriterInterceptor> allProviders = Providers.getAllProviders(injectionManager, WriterInterceptor.class, new org.glassfish.jersey.model.internal.RankedComparator());
        final Iterator<WriterInterceptor> iterator = allProviders.iterator();
        Assert.assertEquals(CommonConfigTest.MidPriorityProvider.class, iterator.next().getClass());
        Assert.assertEquals(CommonConfigTest.HighPriorityProvider.class, iterator.next().getClass());
        Assert.assertEquals(CommonConfigTest.LowPriorityProvider.class, iterator.next().getClass());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void testProviderOrderAutomatic() throws Exception {
        InjectionManager injectionManager = Injections.createInjectionManager();
        config.register(CommonConfigTest.MidPriorityProvider.class);
        config.register(CommonConfigTest.LowPriorityProvider.class);
        config.register(CommonConfigTest.HighPriorityProvider.class);
        ProviderBinder.bindProviders(config.getComponentBag(), injectionManager);
        injectionManager.completeRegistration();
        final Iterable<WriterInterceptor> allProviders = Providers.getAllProviders(injectionManager, WriterInterceptor.class, new org.glassfish.jersey.model.internal.RankedComparator());
        final Iterator<WriterInterceptor> iterator = allProviders.iterator();
        Assert.assertEquals(CommonConfigTest.HighPriorityProvider.class, iterator.next().getClass());
        Assert.assertEquals(CommonConfigTest.MidPriorityProvider.class, iterator.next().getClass());
        Assert.assertEquals(CommonConfigTest.LowPriorityProvider.class, iterator.next().getClass());
        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProviderOrderDifForContracts() throws Exception {
        final Map<Class<?>, Integer> contracts = new IdentityHashMap<>();
        contracts.put(WriterInterceptor.class, NO_PRIORITY);
        contracts.put(ReaderInterceptor.class, 2000);
        config.register(CommonConfigTest.MidPriorityProvider.class, contracts);
        contracts.clear();
        contracts.put(WriterInterceptor.class, NO_PRIORITY);
        contracts.put(ReaderInterceptor.class, 1000);
        config.register(CommonConfigTest.LowPriorityProvider.class, contracts);
        contracts.clear();
        contracts.put(WriterInterceptor.class, NO_PRIORITY);
        contracts.put(ReaderInterceptor.class, 3000);
        config.register(CommonConfigTest.HighPriorityProvider.class, contracts);
        contracts.clear();
        InjectionManager injectionManager = Injections.createInjectionManager();
        ProviderBinder.bindProviders(config.getComponentBag(), injectionManager);
        injectionManager.completeRegistration();
        final Iterable<WriterInterceptor> writerInterceptors = Providers.getAllProviders(injectionManager, WriterInterceptor.class, new org.glassfish.jersey.model.internal.RankedComparator());
        final Iterator<WriterInterceptor> writerIterator = writerInterceptors.iterator();
        Assert.assertEquals(CommonConfigTest.HighPriorityProvider.class, writerIterator.next().getClass());
        Assert.assertEquals(CommonConfigTest.MidPriorityProvider.class, writerIterator.next().getClass());
        Assert.assertEquals(CommonConfigTest.LowPriorityProvider.class, writerIterator.next().getClass());
        Assert.assertFalse(writerIterator.hasNext());
        final Iterable<ReaderInterceptor> readerInterceptors = Providers.getAllProviders(injectionManager, ReaderInterceptor.class, new org.glassfish.jersey.model.internal.RankedComparator());
        final Iterator<ReaderInterceptor> readerIterator = readerInterceptors.iterator();
        Assert.assertEquals(CommonConfigTest.LowPriorityProvider.class, readerIterator.next().getClass());
        Assert.assertEquals(CommonConfigTest.MidPriorityProvider.class, readerIterator.next().getClass());
        Assert.assertEquals(CommonConfigTest.HighPriorityProvider.class, readerIterator.next().getClass());
        Assert.assertFalse(readerIterator.hasNext());
    }

    public static final class CustomReaderA implements ReaderInterceptor {
        @Override
        public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
            return null;
        }
    }

    public static final class CustomReaderB implements ReaderInterceptor {
        @Override
        public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
            return null;
        }
    }

    public static final class SimpleFeatureA implements Feature {
        private boolean initB;

        public SimpleFeatureA() {
        }

        public SimpleFeatureA(final boolean initB) {
            this.initB = initB;
        }

        @Override
        public boolean configure(final FeatureContext config) {
            config.register((initB ? CommonConfigTest.CustomReaderB.class : CommonConfigTest.CustomReaderA.class));
            return true;
        }
    }

    public static final class SimpleFeatureB implements Feature {
        @Override
        public boolean configure(final FeatureContext config) {
            config.register(CommonConfigTest.CustomReaderB.class);
            return true;
        }
    }

    public static final class InstanceFeatureA implements Feature {
        private boolean initB;

        public InstanceFeatureA() {
        }

        public InstanceFeatureA(final boolean initB) {
            this.initB = initB;
        }

        @Override
        public boolean configure(final FeatureContext config) {
            config.register((initB ? new CommonConfigTest.CustomReaderB() : new CommonConfigTest.CustomReaderA()));
            return true;
        }
    }

    public static final class ComplexFeature implements Feature {
        @Override
        public boolean configure(final FeatureContext config) {
            config.register(CommonConfigTest.SimpleFeatureA.class);
            config.register(CommonConfigTest.SimpleFeatureB.class);
            return true;
        }
    }

    public static final class RecursiveFeature implements Feature {
        @Override
        public boolean configure(final FeatureContext config) {
            config.register(new CommonConfigTest.CustomReaderA());
            config.register(CommonConfigTest.RecursiveFeature.class);
            return true;
        }
    }

    public static final class RecursiveInstanceFeature implements Feature {
        @Override
        public boolean configure(final FeatureContext config) {
            config.register(new CommonConfigTest.CustomReaderA());
            config.register(new CommonConfigTest.RecursiveInstanceFeature());
            return true;
        }
    }

    @Test
    public void testConfigureFeatureHierarchy() throws Exception {
        config.register(CommonConfigTest.ComplexFeature.class);
        InjectionManager injectionManager = Injections.createInjectionManager();
        ManagedObjectsFinalizer finalizer = new ManagedObjectsFinalizer(injectionManager);
        config.configureMetaProviders(injectionManager, finalizer);
        Assert.assertTrue(config.getConfiguration().isEnabled(CommonConfigTest.ComplexFeature.class));
        Assert.assertTrue(config.getConfiguration().isRegistered(CommonConfigTest.CustomReaderA.class));
        Assert.assertTrue(config.getConfiguration().isRegistered(CommonConfigTest.CustomReaderB.class));
    }

    @Test
    public void testConfigureFeatureRecursive() throws Exception {
        config.register(CommonConfigTest.RecursiveFeature.class);
        InjectionManager injectionManager = Injections.createInjectionManager();
        ManagedObjectsFinalizer finalizer = new ManagedObjectsFinalizer(injectionManager);
        config.configureMetaProviders(injectionManager, finalizer);
        Assert.assertTrue(config.getConfiguration().isEnabled(CommonConfigTest.RecursiveFeature.class));
        Assert.assertEquals(1, config.getInstances().size());
        Assert.assertSame(CommonConfigTest.CustomReaderA.class, config.getInstances().iterator().next().getClass());
    }

    @Test
    public void testConfigureFeatureInstances() throws Exception {
        final CommonConfigTest.SimpleFeatureA f1 = new CommonConfigTest.SimpleFeatureA();
        config.register(f1);
        final CommonConfigTest.SimpleFeatureA f2 = new CommonConfigTest.SimpleFeatureA(true);
        config.register(f2);
        InjectionManager injectionManager = Injections.createInjectionManager();
        ManagedObjectsFinalizer finalizer = new ManagedObjectsFinalizer(injectionManager);
        config.configureMetaProviders(injectionManager, finalizer);
        Assert.assertTrue(config.getConfiguration().isEnabled(f1));
        Assert.assertFalse(config.getConfiguration().isEnabled(f2));
        Assert.assertTrue(config.getConfiguration().isRegistered(CommonConfigTest.CustomReaderA.class));
        Assert.assertFalse(config.getConfiguration().isRegistered(CommonConfigTest.CustomReaderB.class));
    }

    @Test
    public void testConfigureFeatureInstancesProviderInstances() throws Exception {
        final CommonConfigTest.InstanceFeatureA f1 = new CommonConfigTest.InstanceFeatureA();
        config.register(f1);
        final CommonConfigTest.InstanceFeatureA f2 = new CommonConfigTest.InstanceFeatureA(true);
        config.register(f2);
        InjectionManager injectionManager = Injections.createInjectionManager();
        ManagedObjectsFinalizer finalizer = new ManagedObjectsFinalizer(injectionManager);
        config.configureMetaProviders(injectionManager, finalizer);
        Assert.assertTrue(config.getConfiguration().isEnabled(f1));
        Assert.assertFalse(config.getConfiguration().isEnabled(f2));
        final Set<Object> providerInstances = config.getInstances();
        Assert.assertEquals(2, providerInstances.size());
        final Set<Object> pureProviderInstances = config.getComponentBag().getInstances(ComponentBag.excludeMetaProviders(injectionManager));
        Assert.assertEquals(1, pureProviderInstances.size());
        int a = 0;
        int b = 0;
        for (final Object instance : pureProviderInstances) {
            if (instance instanceof CommonConfigTest.CustomReaderA) {
                a++;
            } else {
                b++;
            }
        }
        Assert.assertEquals(1, a);
        Assert.assertEquals(0, b);
    }

    @Test
    public void testConfigureFeatureInstanceRecursive() throws Exception {
        config.register(new CommonConfigTest.RecursiveInstanceFeature());
        InjectionManager injectionManager = Injections.createInjectionManager();
        ManagedObjectsFinalizer finalizer = new ManagedObjectsFinalizer(injectionManager);
        config.configureMetaProviders(injectionManager, finalizer);
        Assert.assertEquals(0, config.getClasses().size());
        Assert.assertEquals(2, config.getInstances().size());
        final Set<Object> pureProviders = config.getComponentBag().getInstances(ComponentBag.excludeMetaProviders(injectionManager));
        Assert.assertEquals(1, pureProviders.size());
        Assert.assertSame(CommonConfigTest.CustomReaderA.class, pureProviders.iterator().next().getClass());
    }

    public static interface Contract {}

    public static class Service implements CommonConfigTest.Contract {}

    public static class ContractBinder extends AbstractBinder {
        @Override
        protected void configure() {
            bind(CommonConfigTest.Service.class).to(CommonConfigTest.Contract.class);
        }
    }

    public static class ContractBinderFeature implements Feature {
        @Override
        public boolean configure(final FeatureContext context) {
            context.register(new CommonConfigTest.ContractBinder());
            return true;
        }
    }

    @Test
    public void testBinderConfiguringFeature() throws Exception {
        config.register(CommonConfigTest.ContractBinderFeature.class);
        InjectionManager injectionManager = Injections.createInjectionManager();
        ManagedObjectsFinalizer finalizer = new ManagedObjectsFinalizer(injectionManager);
        config.configureMetaProviders(injectionManager, finalizer);
        injectionManager.completeRegistration();
        Assert.assertTrue(config.isEnabled(CommonConfigTest.ContractBinderFeature.class));
        Assert.assertEquals(1, config.getInstances().size());
        Assert.assertSame(CommonConfigTest.ContractBinder.class, config.getInstances().iterator().next().getClass());
        final CommonConfigTest.Contract service = injectionManager.getInstance(CommonConfigTest.Contract.class);
        Assert.assertNotNull(service);
        Assert.assertSame(CommonConfigTest.Service.class, service.getClass());
    }

    public static class InjectMe {}

    public static class InjectIntoFeatureInstance implements Feature {
        @Inject
        private CommonConfigTest.InjectMe injectMe;

        @Override
        public boolean configure(final FeatureContext context) {
            context.property("instance-injected", ((injectMe) != null));
            return true;
        }
    }

    public static class InjectIntoFeatureClass implements Feature {
        @Inject
        private CommonConfigTest.InjectMe injectMe;

        @Override
        public boolean configure(final FeatureContext context) {
            context.property("class-injected", ((injectMe) != null));
            return true;
        }
    }

    public static class BindInjectMeInFeature implements Feature {
        @Override
        public boolean configure(FeatureContext context) {
            context.register(new AbstractBinder() {
                @Override
                protected void configure() {
                    bind(new CommonConfigTest.InjectMe());
                }
            });
            return true;
        }
    }

    @Test
    public void testFeatureInjections() throws Exception {
        Assume.assumeTrue(Hk2InjectionManagerFactory.isImmediateStrategy());
        config.register(CommonConfigTest.InjectIntoFeatureClass.class).register(new CommonConfigTest.InjectIntoFeatureInstance()).register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(new CommonConfigTest.InjectMe());
            }
        });
        InjectionManager injectionManager = Injections.createInjectionManager();
        ManagedObjectsFinalizer finalizer = new ManagedObjectsFinalizer(injectionManager);
        config.configureMetaProviders(injectionManager, finalizer);
        Assert.assertThat("Feature instance not injected", config.getProperty("instance-injected").toString(), CoreMatchers.is("true"));
        Assert.assertThat("Feature class not injected", config.getProperty("class-injected").toString(), CoreMatchers.is("true"));
    }
}

