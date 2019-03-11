/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.impl.internal.classes;


import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.ehcache.spi.service.Service;
import org.ehcache.spi.service.ServiceConfiguration;
import org.ehcache.spi.service.ServiceProvider;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Ludovic Orban
 */
public class ClassInstanceProviderTest {
    @SuppressWarnings("unchecked")
    private Class<ClassInstanceConfiguration<ClassInstanceProviderTest.TestService>> configClass = ((Class) (ClassInstanceConfiguration.class));

    @Test
    public void testNewInstanceUsingAliasAndNoArgs() throws Exception {
        ClassInstanceProvider<String, ClassInstanceConfiguration<ClassInstanceProviderTest.TestService>, ClassInstanceProviderTest.TestService> classInstanceProvider = new ClassInstanceProvider<>(null, configClass);
        classInstanceProvider.preconfigured.put("test stuff", new ClassInstanceConfiguration<ClassInstanceProviderTest.TestService>(ClassInstanceProviderTest.TestService.class));
        ClassInstanceProviderTest.TestService obj = classInstanceProvider.newInstance("test stuff", ((ServiceConfiguration) (null)));
        MatcherAssert.assertThat(obj.theString, Is.is(Matchers.nullValue()));
    }

    @Test
    public void testNewInstanceUsingAliasAndArg() throws Exception {
        ClassInstanceProvider<String, ClassInstanceConfiguration<ClassInstanceProviderTest.TestService>, ClassInstanceProviderTest.TestService> classInstanceProvider = new ClassInstanceProvider<>(null, configClass);
        classInstanceProvider.preconfigured.put("test stuff", new ClassInstanceConfiguration(ClassInstanceProviderTest.TestService.class, "test string"));
        ClassInstanceProviderTest.TestService obj = classInstanceProvider.newInstance("test stuff", ((ServiceConfiguration<?>) (null)));
        MatcherAssert.assertThat(obj.theString, Matchers.equalTo("test string"));
    }

    @Test
    public void testNewInstanceUsingServiceConfig() throws Exception {
        ClassInstanceProvider<String, ClassInstanceConfiguration<ClassInstanceProviderTest.TestService>, ClassInstanceProviderTest.TestService> classInstanceProvider = new ClassInstanceProvider<>(null, configClass);
        ClassInstanceProviderTest.TestServiceConfiguration config = new ClassInstanceProviderTest.TestServiceConfiguration();
        ClassInstanceProviderTest.TestService obj = classInstanceProvider.newInstance("test stuff", config);
        MatcherAssert.assertThat(obj.theString, Is.is(Matchers.nullValue()));
    }

    @Test
    public void testNewInstanceUsingServiceConfigFactory() throws Exception {
        ClassInstanceProviderTest.TestServiceProviderConfiguration factoryConfig = new ClassInstanceProviderTest.TestServiceProviderConfiguration();
        factoryConfig.getDefaults().put("test stuff", new ClassInstanceConfiguration<ClassInstanceProviderTest.TestService>(ClassInstanceProviderTest.TestService.class));
        ClassInstanceProvider<String, ClassInstanceConfiguration<ClassInstanceProviderTest.TestService>, ClassInstanceProviderTest.TestService> classInstanceProvider = new ClassInstanceProvider(factoryConfig, configClass);
        classInstanceProvider.start(null);
        ClassInstanceProviderTest.TestService obj = classInstanceProvider.newInstance("test stuff", ((ServiceConfiguration) (null)));
        MatcherAssert.assertThat(obj.theString, Is.is(Matchers.nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReleaseInstanceByAnotherProvider() throws Exception {
        ClassInstanceProvider<String, ClassInstanceConfiguration<String>, String> classInstanceProvider = new ClassInstanceProvider<>(null, null);
        classInstanceProvider.releaseInstance("foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReleaseSameInstanceMultipleTimesThrows() throws Exception {
        ClassInstanceProvider<String, ClassInstanceConfiguration<String>, String> classInstanceProvider = new ClassInstanceProvider<>(null, null);
        classInstanceProvider.providedVsCount.put("foo", new AtomicInteger(1));
        classInstanceProvider.releaseInstance("foo");
        classInstanceProvider.releaseInstance("foo");
    }

    @Test
    public void testReleaseCloseableInstance() throws Exception {
        ClassInstanceProvider<String, ClassInstanceConfiguration<Closeable>, Closeable> classInstanceProvider = new ClassInstanceProvider<>(null, null);
        Closeable closeable = Mockito.mock(Closeable.class);
        classInstanceProvider.providedVsCount.put(closeable, new AtomicInteger(1));
        classInstanceProvider.instantiated.add(closeable);
        classInstanceProvider.releaseInstance(closeable);
        Mockito.verify(closeable).close();
    }

    @Test(expected = IOException.class)
    public void testReleaseCloseableInstanceThrows() throws Exception {
        ClassInstanceProvider<String, ClassInstanceConfiguration<Closeable>, Closeable> classInstanceProvider = new ClassInstanceProvider<>(null, null);
        Closeable closeable = Mockito.mock(Closeable.class);
        Mockito.doThrow(IOException.class).when(closeable).close();
        classInstanceProvider.providedVsCount.put(closeable, new AtomicInteger(1));
        classInstanceProvider.instantiated.add(closeable);
        classInstanceProvider.releaseInstance(closeable);
    }

    @Test
    public void testNewInstanceWithActualInstanceInServiceConfig() throws Exception {
        ClassInstanceProvider<String, ClassInstanceConfiguration<ClassInstanceProviderTest.TestService>, ClassInstanceProviderTest.TestService> classInstanceProvider = new ClassInstanceProvider<>(null, configClass);
        ClassInstanceProviderTest.TestService service = new ClassInstanceProviderTest.TestService();
        ClassInstanceProviderTest.TestServiceConfiguration config = new ClassInstanceProviderTest.TestServiceConfiguration(service);
        ClassInstanceProviderTest.TestService newService = classInstanceProvider.newInstance("test stuff", config);
        MatcherAssert.assertThat(newService, Matchers.sameInstance(service));
    }

    @Test
    public void testSameInstanceRetrievedMultipleTimesUpdatesTheProvidedCount() throws Exception {
        ClassInstanceProvider<String, ClassInstanceConfiguration<ClassInstanceProviderTest.TestService>, ClassInstanceProviderTest.TestService> classInstanceProvider = new ClassInstanceProvider<>(null, configClass);
        ClassInstanceProviderTest.TestService service = new ClassInstanceProviderTest.TestService();
        ClassInstanceProviderTest.TestServiceConfiguration config = new ClassInstanceProviderTest.TestServiceConfiguration(service);
        ClassInstanceProviderTest.TestService newService = classInstanceProvider.newInstance("test stuff", config);
        MatcherAssert.assertThat(newService, Matchers.sameInstance(service));
        MatcherAssert.assertThat(classInstanceProvider.providedVsCount.get(service).get(), Is.is(1));
        newService = classInstanceProvider.newInstance("test stuff", config);
        MatcherAssert.assertThat(newService, Matchers.sameInstance(service));
        MatcherAssert.assertThat(classInstanceProvider.providedVsCount.get(service).get(), Is.is(2));
    }

    @Test
    public void testInstancesNotCreatedByProviderDoesNotClose() throws IOException {
        @SuppressWarnings("unchecked")
        Class<ClassInstanceConfiguration<ClassInstanceProviderTest.TestCloseableService>> configClass = ((Class) (ClassInstanceConfiguration.class));
        ClassInstanceProvider<String, ClassInstanceConfiguration<ClassInstanceProviderTest.TestCloseableService>, ClassInstanceProviderTest.TestCloseableService> classInstanceProvider = new ClassInstanceProvider<>(null, configClass);
        ClassInstanceProviderTest.TestCloseableService service = Mockito.mock(ClassInstanceProviderTest.TestCloseableService.class);
        ClassInstanceProviderTest.TestCloaseableServiceConfig config = new ClassInstanceProviderTest.TestCloaseableServiceConfig(service);
        ClassInstanceProviderTest.TestCloseableService newService = classInstanceProvider.newInstance("testClose", config);
        MatcherAssert.assertThat(newService, Matchers.sameInstance(service));
        classInstanceProvider.releaseInstance(newService);
        Mockito.verify(service, Mockito.times(0)).close();
    }

    public abstract static class TestCloseableService implements Closeable , Service {}

    public static class TestCloaseableServiceConfig extends ClassInstanceConfiguration<ClassInstanceProviderTest.TestCloseableService> implements ServiceConfiguration<ClassInstanceProviderTest.TestCloseableService> {
        public TestCloaseableServiceConfig() {
            super(ClassInstanceProviderTest.TestCloseableService.class);
        }

        public TestCloaseableServiceConfig(ClassInstanceProviderTest.TestCloseableService testCloseableService) {
            super(testCloseableService);
        }

        @Override
        public Class<ClassInstanceProviderTest.TestCloseableService> getServiceType() {
            return ClassInstanceProviderTest.TestCloseableService.class;
        }
    }

    public static class TestService implements Service {
        public final String theString;

        public TestService() {
            this(null);
        }

        public TestService(String theString) {
            this.theString = theString;
        }

        @Override
        public void start(ServiceProvider<Service> serviceProvider) {
        }

        @Override
        public void stop() {
        }
    }

    public static class TestServiceConfiguration extends ClassInstanceConfiguration<ClassInstanceProviderTest.TestService> implements ServiceConfiguration<ClassInstanceProviderTest.TestService> {
        public TestServiceConfiguration() {
            super(ClassInstanceProviderTest.TestService.class);
        }

        public TestServiceConfiguration(ClassInstanceProviderTest.TestService service) {
            super(service);
        }

        @Override
        public Class<ClassInstanceProviderTest.TestService> getServiceType() {
            return ClassInstanceProviderTest.TestService.class;
        }
    }

    public static class TestServiceProviderConfiguration extends ClassInstanceProviderConfiguration<String, ClassInstanceConfiguration<ClassInstanceProviderTest.TestService>> implements ServiceConfiguration<ClassInstanceProviderTest.TestService> {
        @Override
        public Class<ClassInstanceProviderTest.TestService> getServiceType() {
            return ClassInstanceProviderTest.TestService.class;
        }
    }
}

