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
package org.ehcache.core.spi;


import ServiceLocator.DependencySet;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.ehcache.core.spi.services.DefaultTestProvidedService;
import org.ehcache.core.spi.services.DefaultTestService;
import org.ehcache.core.spi.services.FancyCacheProvider;
import org.ehcache.core.spi.services.TestMandatoryServiceFactory;
import org.ehcache.core.spi.services.TestProvidedService;
import org.ehcache.core.spi.services.TestService;
import org.ehcache.core.spi.services.ranking.RankServiceA;
import org.ehcache.core.spi.services.ranking.RankServiceB;
import org.ehcache.core.spi.store.CacheProvider;
import org.ehcache.spi.loaderwriter.CacheLoaderWriterProvider;
import org.ehcache.spi.service.Service;
import org.ehcache.spi.service.ServiceDependencies;
import org.ehcache.spi.service.ServiceProvider;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link ServiceLocator}.
 */
public class ServiceLocatorTest {
    @Test
    public void testClassHierarchies() {
        ServiceLocator.DependencySet dependencySet = ServiceLocator.dependencySet();
        final Service service = new ChildTestService();
        dependencySet.with(service);
        Assert.assertThat(dependencySet.providerOf(FooProvider.class), CoreMatchers.sameInstance(service));
        final Service fancyCacheProvider = new FancyCacheProvider();
        dependencySet.with(fancyCacheProvider);
        final Collection<CacheProvider> servicesOfType = dependencySet.providersOf(CacheProvider.class);
        Assert.assertThat(servicesOfType, CoreMatchers.is(not(empty())));
        Assert.assertThat(servicesOfType.iterator().next(), CoreMatchers.sameInstance(fancyCacheProvider));
    }

    @Test
    public void testDoesNotUseTCCL() {
        Thread.currentThread().setContextClassLoader(new ClassLoader() {
            @Override
            public Enumeration<URL> getResources(String name) throws IOException {
                throw new AssertionError();
            }
        });
        ServiceLocator.dependencySet().with(TestService.class).build().getService(TestService.class);
    }

    @Test
    public void testAttemptsToStopStartedServicesOnInitFailure() {
        Service s1 = new ParentTestService();
        FancyCacheProvider s2 = new FancyCacheProvider();
        ServiceLocator locator = ServiceLocator.dependencySet().with(s1).with(s2).build();
        try {
            locator.startAllServices();
            Assert.fail();
        } catch (Exception e) {
            // see org.ehcache.spi.ParentTestService.start()
            Assert.assertThat(e, CoreMatchers.instanceOf(RuntimeException.class));
            Assert.assertThat(e.getMessage(), CoreMatchers.is("Implement me!"));
        }
        Assert.assertThat(s2.startStopCounter, CoreMatchers.is(0));
    }

    @Test
    public void testAttemptsToStopAllServicesOnCloseFailure() {
        Service s1 = Mockito.mock(CacheProvider.class);
        Service s2 = Mockito.mock(FooProvider.class);
        Service s3 = Mockito.mock(CacheLoaderWriterProvider.class);
        ServiceLocator locator = ServiceLocator.dependencySet().with(s1).with(s2).with(s3).build();
        try {
            locator.startAllServices();
        } catch (Exception e) {
            Assert.fail();
        }
        final RuntimeException thrown = new RuntimeException();
        Mockito.doThrow(thrown).when(s1).stop();
        try {
            locator.stopAllServices();
            Assert.fail();
        } catch (Exception e) {
            Assert.assertThat(e, CoreMatchers.<Exception>sameInstance(thrown));
        }
        Mockito.verify(s1).stop();
        Mockito.verify(s2).stop();
        Mockito.verify(s3).stop();
    }

    @Test
    public void testStopAllServicesOnlyStopsEachServiceOnce() throws Exception {
        Service s1 = Mockito.mock(CacheProvider.class, Mockito.withSettings().extraInterfaces(CacheLoaderWriterProvider.class));
        ServiceLocator locator = ServiceLocator.dependencySet().with(s1).build();
        try {
            locator.startAllServices();
        } catch (Exception e) {
            Assert.fail();
        }
        locator.stopAllServices();
        Mockito.verify(s1, Mockito.times(1)).stop();
    }

    @Test
    public void testCanOverrideDefaultServiceFromServiceLoader() {
        ServiceLocator locator = ServiceLocator.dependencySet().with(new ExtendedTestService()).build();
        TestService testService = locator.getService(TestService.class);
        Assert.assertThat(testService, CoreMatchers.instanceOf(ExtendedTestService.class));
    }

    @Test
    public void testCanOverrideServiceDependencyWithoutOrderingProblem() throws Exception {
        final AtomicBoolean started = new AtomicBoolean(false);
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(new TestServiceConsumerService()).with(new TestService() {
            @Override
            public void start(ServiceProvider<Service> serviceProvider) {
                started.set(true);
            }

            @Override
            public void stop() {
                // no-op
            }
        }).build();
        serviceLocator.startAllServices();
        Assert.assertThat(started.get(), CoreMatchers.is(true));
    }

    @Test
    public void testServicesInstanciatedOnceAndStartedOnce() throws Exception {
        @ServiceDependencies(TestProvidedService.class)
        class Consumer1 implements Service {
            @Override
            public void start(ServiceProvider<Service> serviceProvider) {
            }

            @Override
            public void stop() {
            }
        }
        @ServiceDependencies(TestProvidedService.class)
        class Consumer2 implements Service {
            TestProvidedService testProvidedService;

            @Override
            public void start(ServiceProvider<Service> serviceProvider) {
                testProvidedService = serviceProvider.getService(TestProvidedService.class);
            }

            @Override
            public void stop() {
            }
        }
        Consumer1 consumer1 = Mockito.spy(new Consumer1());
        Consumer2 consumer2 = new Consumer2();
        ServiceLocator.DependencySet dependencySet = ServiceLocator.dependencySet();
        // add some services
        dependencySet.with(consumer1);
        dependencySet.with(consumer2);
        dependencySet.with(new TestService() {
            @Override
            public void start(ServiceProvider<Service> serviceProvider) {
            }

            @Override
            public void stop() {
                // no-op
            }
        });
        // simulate what is done in ehcachemanager
        dependencySet.with(TestService.class);
        ServiceLocator serviceLocator = dependencySet.build();
        serviceLocator.startAllServices();
        serviceLocator.stopAllServices();
        Mockito.verify(consumer1, Mockito.times(1)).start(serviceLocator);
        Mockito.verify(consumer1, Mockito.times(1)).stop();
        Assert.assertThat(consumer2.testProvidedService.ctors(), greaterThanOrEqualTo(1));
        Assert.assertThat(consumer2.testProvidedService.stops(), equalTo(1));
        Assert.assertThat(consumer2.testProvidedService.starts(), equalTo(1));
    }

    @Test
    public void testRedefineDefaultServiceWhileDependingOnIt() throws Exception {
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(new YetAnotherCacheProvider()).build();
        serviceLocator.startAllServices();
    }

    @Test(expected = IllegalStateException.class)
    public void testCircularDeps() throws Exception {
        final class StartStopCounter {
            final AtomicInteger startCounter = new AtomicInteger(0);

            final AtomicReference<ServiceProvider<Service>> startServiceProvider = new AtomicReference<>();

            final AtomicInteger stopCounter = new AtomicInteger(0);

            public void countStart(ServiceProvider<Service> serviceProvider) {
                startCounter.incrementAndGet();
                startServiceProvider.set(serviceProvider);
            }

            public void countStop() {
                stopCounter.incrementAndGet();
            }
        }
        @ServiceDependencies(TestProvidedService.class)
        class Consumer1 implements Service {
            final StartStopCounter startStopCounter = new StartStopCounter();

            @Override
            public void start(ServiceProvider<Service> serviceProvider) {
                Assert.assertThat(serviceProvider.getService(TestProvidedService.class), CoreMatchers.is(CoreMatchers.notNullValue()));
                startStopCounter.countStart(serviceProvider);
            }

            @Override
            public void stop() {
                startStopCounter.countStop();
            }
        }
        @ServiceDependencies(Consumer1.class)
        class Consumer2 implements Service {
            final StartStopCounter startStopCounter = new StartStopCounter();

            @Override
            public void start(ServiceProvider<Service> serviceProvider) {
                Assert.assertThat(serviceProvider.getService(Consumer1.class), CoreMatchers.is(CoreMatchers.notNullValue()));
                startStopCounter.countStart(serviceProvider);
            }

            @Override
            public void stop() {
                startStopCounter.countStop();
            }
        }
        @ServiceDependencies(Consumer2.class)
        class MyTestProvidedService extends DefaultTestProvidedService {
            final StartStopCounter startStopCounter = new StartStopCounter();

            @Override
            public void start(ServiceProvider<Service> serviceProvider) {
                Assert.assertThat(serviceProvider.getService(Consumer2.class), CoreMatchers.is(CoreMatchers.notNullValue()));
                startStopCounter.countStart(serviceProvider);
                super.start(serviceProvider);
            }

            @Override
            public void stop() {
                startStopCounter.countStop();
                super.stop();
            }
        }
        @ServiceDependencies(DependsOnMe.class)
        class DependsOnMe implements Service {
            final StartStopCounter startStopCounter = new StartStopCounter();

            @Override
            public void start(ServiceProvider<Service> serviceProvider) {
                Assert.assertThat(serviceProvider.getService(DependsOnMe.class), CoreMatchers.sameInstance(this));
                startStopCounter.countStart(serviceProvider);
            }

            @Override
            public void stop() {
                startStopCounter.countStop();
            }
        }
        ServiceLocator.DependencySet dependencySet = ServiceLocator.dependencySet();
        Consumer1 consumer1 = new Consumer1();
        Consumer2 consumer2 = new Consumer2();
        MyTestProvidedService myTestProvidedService = new MyTestProvidedService();
        DependsOnMe dependsOnMe = new DependsOnMe();
        // add some services
        dependencySet.with(consumer1);
        dependencySet.with(consumer2);
        dependencySet.with(myTestProvidedService);
        dependencySet.with(dependsOnMe);
        ServiceLocator serviceLocator = dependencySet.build();
        // simulate what is done in ehcachemanager
        serviceLocator.startAllServices();
        serviceLocator.stopAllServices();
        Assert.assertThat(consumer1.startStopCounter.startCounter.get(), CoreMatchers.is(1));
        Assert.assertThat(consumer1.startStopCounter.startServiceProvider.get(), CoreMatchers.<ServiceProvider<Service>>is(serviceLocator));
        Assert.assertThat(consumer2.startStopCounter.startCounter.get(), CoreMatchers.is(1));
        Assert.assertThat(consumer2.startStopCounter.startServiceProvider.get(), CoreMatchers.<ServiceProvider<Service>>is(serviceLocator));
        Assert.assertThat(myTestProvidedService.startStopCounter.startCounter.get(), CoreMatchers.is(1));
        Assert.assertThat(myTestProvidedService.startStopCounter.startServiceProvider.get(), CoreMatchers.<ServiceProvider<Service>>is(serviceLocator));
        Assert.assertThat(dependsOnMe.startStopCounter.startCounter.get(), CoreMatchers.is(1));
        Assert.assertThat(dependsOnMe.startStopCounter.startServiceProvider.get(), CoreMatchers.<ServiceProvider<Service>>is(serviceLocator));
        Assert.assertThat(consumer1.startStopCounter.stopCounter.get(), CoreMatchers.is(1));
        Assert.assertThat(consumer2.startStopCounter.stopCounter.get(), CoreMatchers.is(1));
        Assert.assertThat(myTestProvidedService.startStopCounter.stopCounter.get(), CoreMatchers.is(1));
        Assert.assertThat(dependsOnMe.startStopCounter.stopCounter.get(), CoreMatchers.is(1));
    }

    @Test
    public void testAbsentOptionalDepGetIgnored() {
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(new ServiceWithOptionalDeps()).build();
        Assert.assertThat(serviceLocator.getService(ServiceWithOptionalDeps.class), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(serviceLocator.getService(TestService.class), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(serviceLocator.getService(OptService1.class), CoreMatchers.is(nullValue()));
        Assert.assertThat(serviceLocator.getService(OptService2.class), CoreMatchers.is(nullValue()));
    }

    @Test
    public void testPresentOptionalDepGetLoaded() {
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(new ServiceWithOptionalDeps()).with(new OptService1()).with(new OptService2()).build();
        Assert.assertThat(serviceLocator.getService(ServiceWithOptionalDeps.class), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(serviceLocator.getService(TestService.class), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(serviceLocator.getService(OptService1.class), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(serviceLocator.getService(OptService2.class), CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void testMixedPresentAndAbsentOptionalDepGetLoadedAndIgnored() {
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(new ServiceWithOptionalDeps()).with(new OptService2()).build();
        Assert.assertThat(serviceLocator.getService(ServiceWithOptionalDeps.class), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(serviceLocator.getService(TestService.class), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(serviceLocator.getService(OptService1.class), CoreMatchers.is(nullValue()));
        Assert.assertThat(serviceLocator.getService(OptService2.class), CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void testOptionalDepWithAbsentClass() {
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(new ServiceWithOptionalNonExistentDeps()).with(new OptService2()).build();
        Assert.assertThat(serviceLocator.getService(ServiceWithOptionalNonExistentDeps.class), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(serviceLocator.getService(TestService.class), CoreMatchers.is(CoreMatchers.notNullValue()));
        Assert.assertThat(serviceLocator.getService(OptService2.class), CoreMatchers.is(CoreMatchers.notNullValue()));
    }

    @Test
    public void testManadatoryDependencyIsAddedToEmptySet() {
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().build();
        TestMandatoryServiceFactory.TestMandatoryService service = serviceLocator.getService(TestMandatoryServiceFactory.TestMandatoryService.class);
        Assert.assertThat(service, CoreMatchers.notNullValue());
        Assert.assertThat(service.getConfig(), nullValue());
    }

    @Test
    public void testManadatoryDependenciesCanBeDisabled() {
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().withoutMandatoryServices().build();
        Assert.assertThat(serviceLocator.getService(TestMandatoryServiceFactory.TestMandatoryService.class), nullValue());
    }

    @Test
    public void testMandatoryDependencyIsAddedToNonEmptySet() {
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(new DefaultTestService()).build();
        TestMandatoryServiceFactory.TestMandatoryService service = serviceLocator.getService(TestMandatoryServiceFactory.TestMandatoryService.class);
        Assert.assertThat(service, CoreMatchers.notNullValue());
        Assert.assertThat(service.getConfig(), nullValue());
    }

    @Test
    public void testMandatoryDependencyCanStillBeRequested() {
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(TestMandatoryServiceFactory.TestMandatoryService.class).build();
        TestMandatoryServiceFactory.TestMandatoryService service = serviceLocator.getService(TestMandatoryServiceFactory.TestMandatoryService.class);
        Assert.assertThat(service, CoreMatchers.notNullValue());
        Assert.assertThat(service.getConfig(), nullValue());
    }

    @Test
    public void testMandatoryDependencyWithProvidedConfigIsHonored() {
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(new TestMandatoryServiceFactory.TestMandatoryServiceConfiguration("apple")).build();
        TestMandatoryServiceFactory.TestMandatoryService service = serviceLocator.getService(TestMandatoryServiceFactory.TestMandatoryService.class);
        Assert.assertThat(service, CoreMatchers.notNullValue());
        Assert.assertThat(service.getConfig(), CoreMatchers.is("apple"));
    }

    @Test
    public void testMandatoryDependencyCanBeDependedOn() {
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(new NeedsMandatoryService()).build();
        TestMandatoryServiceFactory.TestMandatoryService service = serviceLocator.getService(TestMandatoryServiceFactory.TestMandatoryService.class);
        Assert.assertThat(service, CoreMatchers.notNullValue());
        Assert.assertThat(service.getConfig(), nullValue());
        Assert.assertThat(serviceLocator.getService(NeedsMandatoryService.class), CoreMatchers.notNullValue());
    }

    @Test
    public void testRankedServiceOverrides() {
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().with(RankServiceA.class).build();
        Assert.assertThat(serviceLocator.getService(RankServiceA.class).getSource(), CoreMatchers.is("high-rank"));
    }

    @Test
    public void testRankedServiceOverridesMandatory() {
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().build();
        Assert.assertThat(serviceLocator.getService(RankServiceA.class), nullValue());
    }

    @Test
    public void testRankedServiceBecomesMandatory() {
        ServiceLocator serviceLocator = ServiceLocator.dependencySet().build();
        Assert.assertThat(serviceLocator.getService(RankServiceB.class), CoreMatchers.notNullValue());
    }
}

