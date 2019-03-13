/**
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.grpc;


import com.google.common.collect.ImmutableList;
import io.grpc.InternalServiceProviders.PriorityAccessor;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link ServiceProviders}.
 */
@RunWith(JUnit4.class)
public class ServiceProvidersTest {
    private static final List<Class<?>> NO_HARDCODED = Collections.emptyList();

    private static final PriorityAccessor<ServiceProvidersTestAbstractProvider> ACCESSOR = new PriorityAccessor<ServiceProvidersTestAbstractProvider>() {
        @Override
        public boolean isAvailable(ServiceProvidersTestAbstractProvider provider) {
            return provider.isAvailable();
        }

        @Override
        public int getPriority(ServiceProvidersTestAbstractProvider provider) {
            return provider.priority();
        }
    };

    private final String serviceFile = "META-INF/services/io.grpc.ServiceProvidersTestAbstractProvider";

    @Test
    public void contextClassLoaderProvider() {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader cl = new ReplacingClassLoader(getClass().getClassLoader(), serviceFile, "io/grpc/ServiceProvidersTestAbstractProvider-multipleProvider.txt");
            // test that the context classloader is used as fallback
            ClassLoader rcll = new ReplacingClassLoader(getClass().getClassLoader(), serviceFile, "io/grpc/ServiceProvidersTestAbstractProvider-empty.txt");
            Thread.currentThread().setContextClassLoader(rcll);
            Assert.assertEquals(ServiceProvidersTest.Available7Provider.class, ServiceProviders.load(ServiceProvidersTestAbstractProvider.class, ServiceProvidersTest.NO_HARDCODED, cl, ServiceProvidersTest.ACCESSOR).getClass());
        } finally {
            Thread.currentThread().setContextClassLoader(ccl);
        }
    }

    @Test
    public void noProvider() {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader cl = new ReplacingClassLoader(getClass().getClassLoader(), serviceFile, "io/grpc/ServiceProvidersTestAbstractProvider-doesNotExist.txt");
            Thread.currentThread().setContextClassLoader(cl);
            Assert.assertNull(ServiceProviders.load(ServiceProvidersTestAbstractProvider.class, ServiceProvidersTest.NO_HARDCODED, cl, ServiceProvidersTest.ACCESSOR));
        } finally {
            Thread.currentThread().setContextClassLoader(ccl);
        }
    }

    @Test
    public void multipleProvider() throws Exception {
        ClassLoader cl = new ReplacingClassLoader(getClass().getClassLoader(), serviceFile, "io/grpc/ServiceProvidersTestAbstractProvider-multipleProvider.txt");
        Assert.assertSame(ServiceProvidersTest.Available7Provider.class, ServiceProviders.load(ServiceProvidersTestAbstractProvider.class, ServiceProvidersTest.NO_HARDCODED, cl, ServiceProvidersTest.ACCESSOR).getClass());
        List<ServiceProvidersTestAbstractProvider> providers = ServiceProviders.loadAll(ServiceProvidersTestAbstractProvider.class, ServiceProvidersTest.NO_HARDCODED, cl, ServiceProvidersTest.ACCESSOR);
        Assert.assertEquals(3, providers.size());
        Assert.assertEquals(ServiceProvidersTest.Available7Provider.class, providers.get(0).getClass());
        Assert.assertEquals(ServiceProvidersTest.Available5Provider.class, providers.get(1).getClass());
        Assert.assertEquals(ServiceProvidersTest.Available0Provider.class, providers.get(2).getClass());
    }

    @Test
    public void unavailableProvider() {
        // tries to load Available7 and UnavailableProvider, which has priority 10
        ClassLoader cl = new ReplacingClassLoader(getClass().getClassLoader(), serviceFile, "io/grpc/ServiceProvidersTestAbstractProvider-unavailableProvider.txt");
        Assert.assertEquals(ServiceProvidersTest.Available7Provider.class, ServiceProviders.load(ServiceProvidersTestAbstractProvider.class, ServiceProvidersTest.NO_HARDCODED, cl, ServiceProvidersTest.ACCESSOR).getClass());
    }

    @Test
    public void unknownClassProvider() {
        ClassLoader cl = new ReplacingClassLoader(getClass().getClassLoader(), serviceFile, "io/grpc/ServiceProvidersTestAbstractProvider-unknownClassProvider.txt");
        try {
            ServiceProvidersTestAbstractProvider ignored = ServiceProviders.load(ServiceProvidersTestAbstractProvider.class, ServiceProvidersTest.NO_HARDCODED, cl, ServiceProvidersTest.ACCESSOR);
            Assert.fail("Exception expected");
        } catch (ServiceConfigurationError e) {
            // noop
        }
    }

    @Test
    public void exceptionSurfacedToCaller_failAtInit() {
        ClassLoader cl = new ReplacingClassLoader(getClass().getClassLoader(), serviceFile, "io/grpc/ServiceProvidersTestAbstractProvider-failAtInitProvider.txt");
        try {
            // Even though there is a working provider, if any providers fail then we should fail
            // completely to avoid returning something unexpected.
            ServiceProvidersTestAbstractProvider ignored = ServiceProviders.load(ServiceProvidersTestAbstractProvider.class, ServiceProvidersTest.NO_HARDCODED, cl, ServiceProvidersTest.ACCESSOR);
            Assert.fail("Expected exception");
        } catch (ServiceConfigurationError expected) {
            // noop
        }
    }

    @Test
    public void exceptionSurfacedToCaller_failAtPriority() {
        ClassLoader cl = new ReplacingClassLoader(getClass().getClassLoader(), serviceFile, "io/grpc/ServiceProvidersTestAbstractProvider-failAtPriorityProvider.txt");
        try {
            // The exception should be surfaced to the caller
            ServiceProvidersTestAbstractProvider ignored = ServiceProviders.load(ServiceProvidersTestAbstractProvider.class, ServiceProvidersTest.NO_HARDCODED, cl, ServiceProvidersTest.ACCESSOR);
            Assert.fail("Expected exception");
        } catch (ServiceProvidersTest.FailAtPriorityProvider.PriorityException expected) {
            // noop
        }
    }

    @Test
    public void exceptionSurfacedToCaller_failAtAvailable() {
        ClassLoader cl = new ReplacingClassLoader(getClass().getClassLoader(), serviceFile, "io/grpc/ServiceProvidersTestAbstractProvider-failAtAvailableProvider.txt");
        try {
            // The exception should be surfaced to the caller
            ServiceProvidersTestAbstractProvider ignored = ServiceProviders.load(ServiceProvidersTestAbstractProvider.class, ServiceProvidersTest.NO_HARDCODED, cl, ServiceProvidersTest.ACCESSOR);
            Assert.fail("Expected exception");
        } catch (ServiceProvidersTest.FailAtAvailableProvider.AvailableException expected) {
            // noop
        }
    }

    @Test
    public void getCandidatesViaHardCoded_multipleProvider() throws Exception {
        Iterator<ServiceProvidersTestAbstractProvider> candidates = ServiceProviders.getCandidatesViaHardCoded(ServiceProvidersTestAbstractProvider.class, ImmutableList.<Class<?>>of(ServiceProvidersTest.Available7Provider.class, ServiceProvidersTest.Available0Provider.class)).iterator();
        Assert.assertEquals(ServiceProvidersTest.Available7Provider.class, candidates.next().getClass());
        Assert.assertEquals(ServiceProvidersTest.Available0Provider.class, candidates.next().getClass());
        Assert.assertFalse(candidates.hasNext());
    }

    @Test
    public void getCandidatesViaHardCoded_failAtInit() throws Exception {
        try {
            Iterable<ServiceProvidersTestAbstractProvider> ignored = ServiceProviders.getCandidatesViaHardCoded(ServiceProvidersTestAbstractProvider.class, Collections.<Class<?>>singletonList(ServiceProvidersTest.FailAtInitProvider.class));
            Assert.fail("Expected exception");
        } catch (ServiceConfigurationError expected) {
            // noop
        }
    }

    @Test
    public void getCandidatesViaHardCoded_failAtInit_moreCandidates() throws Exception {
        try {
            Iterable<ServiceProvidersTestAbstractProvider> ignored = ServiceProviders.getCandidatesViaHardCoded(ServiceProvidersTestAbstractProvider.class, ImmutableList.<Class<?>>of(ServiceProvidersTest.FailAtInitProvider.class, ServiceProvidersTest.Available0Provider.class));
            Assert.fail("Expected exception");
        } catch (ServiceConfigurationError expected) {
            // noop
        }
    }

    @Test
    public void create_throwsErrorOnMisconfiguration() throws Exception {
        class PrivateClass {}
        try {
            ServiceProvidersTestAbstractProvider ignored = ServiceProviders.create(ServiceProvidersTestAbstractProvider.class, PrivateClass.class);
            Assert.fail("Expected exception");
        } catch (ServiceConfigurationError expected) {
            Assert.assertTrue(("Expected ClassCastException cause: " + (expected.getCause())), ((expected.getCause()) instanceof ClassCastException));
        }
    }

    private static class BaseProvider extends ServiceProvidersTestAbstractProvider {
        private final boolean isAvailable;

        private final int priority;

        public BaseProvider(boolean isAvailable, int priority) {
            this.isAvailable = isAvailable;
            this.priority = priority;
        }

        @Override
        public boolean isAvailable() {
            return isAvailable;
        }

        @Override
        public int priority() {
            return priority;
        }
    }

    public static final class Available0Provider extends ServiceProvidersTest.BaseProvider {
        public Available0Provider() {
            super(true, 0);
        }
    }

    public static final class Available5Provider extends ServiceProvidersTest.BaseProvider {
        public Available5Provider() {
            super(true, 5);
        }
    }

    public static final class Available7Provider extends ServiceProvidersTest.BaseProvider {
        public Available7Provider() {
            super(true, 7);
        }
    }

    public static final class UnavailableProvider extends ServiceProvidersTest.BaseProvider {
        public UnavailableProvider() {
            super(false, 10);
        }
    }

    public static final class FailAtInitProvider extends ServiceProvidersTestAbstractProvider {
        public FailAtInitProvider() {
            throw new RuntimeException("intentionally broken");
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public int priority() {
            return 0;
        }
    }

    public static final class FailAtPriorityProvider extends ServiceProvidersTestAbstractProvider {
        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public int priority() {
            throw new ServiceProvidersTest.FailAtPriorityProvider.PriorityException();
        }

        public static final class PriorityException extends RuntimeException {}
    }

    public static final class FailAtAvailableProvider extends ServiceProvidersTestAbstractProvider {
        @Override
        public boolean isAvailable() {
            throw new ServiceProvidersTest.FailAtAvailableProvider.AvailableException();
        }

        @Override
        public int priority() {
            return 0;
        }

        public static final class AvailableException extends RuntimeException {}
    }
}

