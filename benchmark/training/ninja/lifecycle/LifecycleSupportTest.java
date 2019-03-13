/**
 * Copyright (C) 2012-2019 the original author or authors.
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
package ninja.lifecycle;


import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class LifecycleSupportTest {
    @Test
    public void serviceShouldNotBeStartedBeforeLifecycleServiceIsStarted() {
        createInjector().getInstance(LifecycleSupportTest.MockService.class);
        Assert.assertThat(LifecycleSupportTest.MockService.started, IsEqual.equalTo(0));
    }

    @Test
    public void serviceShouldBeStartedWhenLifecycleServiceIsStarted() {
        Injector injector = createInjector();
        injector.getInstance(LifecycleSupportTest.MockService.class);
        start(injector);
        Assert.assertThat(LifecycleSupportTest.MockService.started, IsEqual.equalTo(1));
    }

    @Test
    public void serviceShouldBeStartedIfAccessedAfterLifecycleServiceIsStarted() {
        Injector injector = createInjector();
        start(injector);
        injector.getInstance(LifecycleSupportTest.MockService.class);
        Assert.assertThat(LifecycleSupportTest.MockService.started, IsEqual.equalTo(1));
    }

    @Test
    public void serviceShouldBeStartedIfExplicitlyBoundAndSingleton() {
        Injector injector = createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(LifecycleSupportTest.MockSingletonService.class);
            }
        });
        start(injector);
        Assert.assertThat(LifecycleSupportTest.MockSingletonService.started, IsEqual.equalTo(1));
    }

    @Test
    public void serviceShouldBeStartedIfExplicitlyBoundAsSingleton() {
        Injector injector = createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(LifecycleSupportTest.MockService.class).toInstance(new LifecycleSupportTest.MockService());
            }
        });
        start(injector);
        Assert.assertThat(LifecycleSupportTest.MockService.started, IsEqual.equalTo(1));
    }

    @Test
    public void serviceShouldNotBeStartedIfExplicitlyBoundAndNotSingleton() {
        Injector injector = createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(LifecycleSupportTest.MockService.class);
            }
        });
        start(injector);
        Assert.assertThat(LifecycleSupportTest.MockService.started, IsEqual.equalTo(0));
    }

    @Test
    public void singletonServiceShouldNotBeStartedTwice() {
        Injector injector = createInjector();
        injector.getInstance(LifecycleSupportTest.MockSingletonService.class);
        injector.getInstance(LifecycleSupportTest.MockSingletonService.class);
        start(injector);
        Assert.assertThat(LifecycleSupportTest.MockSingletonService.started, IsEqual.equalTo(1));
    }

    @Test
    public void nonSingletonServicesShouldBeInstantiatedForEachInstance() {
        Injector injector = createInjector();
        injector.getInstance(LifecycleSupportTest.MockService.class);
        injector.getInstance(LifecycleSupportTest.MockService.class);
        start(injector);
        Assert.assertThat(LifecycleSupportTest.MockService.started, IsEqual.equalTo(2));
    }

    @Test
    public void disposablesShouldBeDisposedOf() {
        Injector injector = createInjector();
        injector.getInstance(LifecycleSupportTest.MockService.class);
        start(injector);
        stop(injector);
        Assert.assertThat(LifecycleSupportTest.MockService.disposed, IsEqual.equalTo(1));
    }

    @Test
    public void providedSingletonStartableShouldBeStarted() {
        Injector injector = createInjector(new AbstractModule() {
            @Override
            protected void configure() {
            }

            @Provides
            @Singleton
            public LifecycleSupportTest.MockSingletonService provide() {
                return new LifecycleSupportTest.MockSingletonService();
            }
        });
        start(injector);
        Assert.assertThat(LifecycleSupportTest.MockSingletonService.started, IsEqual.equalTo(1));
    }

    @Test
    public void providedSingletonDisposableShouldBeDisposed() {
        Injector injector = createInjector(new AbstractModule() {
            @Override
            protected void configure() {
            }

            @Provides
            @Singleton
            public LifecycleSupportTest.MockSingletonService provide() {
                return new LifecycleSupportTest.MockSingletonService();
            }
        });
        start(injector);
        stop(injector);
        Assert.assertThat(LifecycleSupportTest.MockSingletonService.disposed, IsEqual.equalTo(1));
    }

    @Singleton
    public static class MockSingletonService {
        static int started;

        static int disposed;

        @Start
        public void start() {
            (LifecycleSupportTest.MockSingletonService.started)++;
        }

        @Dispose
        public void dispose() {
            (LifecycleSupportTest.MockSingletonService.disposed)++;
        }
    }

    public static class MockService {
        static int started;

        static int disposed;

        @Start
        public void start() {
            (LifecycleSupportTest.MockService.started)++;
        }

        @Dispose
        public void dispose() {
            (LifecycleSupportTest.MockService.disposed)++;
        }
    }
}

