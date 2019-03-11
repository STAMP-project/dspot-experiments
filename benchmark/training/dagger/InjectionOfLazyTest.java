/**
 * Copyright (C) 2012 Google Inc.
 * Copyright (C) 2012 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dagger;


import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import javax.inject.Provider;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests of injection of Lazy<T> bindings.
 */
@RunWith(JUnit4.class)
public final class InjectionOfLazyTest {
    @Test
    public void lazyValueCreation() {
        final AtomicInteger counter = new AtomicInteger();
        class TestEntryPoint {
            @Inject
            Lazy<Integer> i;

            @Inject
            Lazy<Integer> j;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides
            Integer provideInteger() {
                return counter.incrementAndGet();
            }
        }
        TestEntryPoint ep = injectWithModule(new TestEntryPoint(), new TestModule());
        Assert.assertEquals(0, counter.get());
        Assert.assertEquals(1, ep.i.get().intValue());
        Assert.assertEquals(1, counter.get());
        Assert.assertEquals(2, ep.j.get().intValue());
        Assert.assertEquals(1, ep.i.get().intValue());
        Assert.assertEquals(2, counter.get());
    }

    @Test
    public void lazyNullCreation() {
        final AtomicInteger provideCounter = new AtomicInteger(0);
        class TestEntryPoint {
            @Inject
            Lazy<String> i;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides
            String provideInteger() {
                provideCounter.incrementAndGet();
                return null;
            }
        }
        TestEntryPoint ep = injectWithModule(new TestEntryPoint(), new TestModule());
        Assert.assertEquals(0, provideCounter.get());
        Assert.assertNull(ep.i.get());
        Assert.assertEquals(1, provideCounter.get());
        Assert.assertNull(ep.i.get());// still null

        Assert.assertEquals(1, provideCounter.get());// still only called once.

    }

    @Test
    public void providerOfLazyOfSomething() {
        final AtomicInteger counter = new AtomicInteger();
        class TestEntryPoint {
            @Inject
            Provider<Lazy<Integer>> providerOfLazyInteger;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides
            Integer provideInteger() {
                return counter.incrementAndGet();
            }
        }
        TestEntryPoint ep = injectWithModule(new TestEntryPoint(), new TestModule());
        Assert.assertEquals(0, counter.get());
        Lazy<Integer> i = ep.providerOfLazyInteger.get();
        Assert.assertEquals(1, i.get().intValue());
        Assert.assertEquals(1, counter.get());
        Assert.assertEquals(1, i.get().intValue());
        Lazy<Integer> j = ep.providerOfLazyInteger.get();
        Assert.assertEquals(2, j.get().intValue());
        Assert.assertEquals(2, counter.get());
        Assert.assertEquals(1, i.get().intValue());
    }

    @Test
    public void sideBySideLazyVsProvider() {
        final AtomicInteger counter = new AtomicInteger();
        class TestEntryPoint {
            @Inject
            Provider<Integer> providerOfInteger;

            @Inject
            Lazy<Integer> lazyInteger;
        }
        @Module(injects = TestEntryPoint.class)
        class TestModule {
            @Provides
            Integer provideInteger() {
                return counter.incrementAndGet();
            }
        }
        TestEntryPoint ep = injectWithModule(new TestEntryPoint(), new TestModule());
        Assert.assertEquals(0, counter.get());
        Assert.assertEquals(0, counter.get());
        Assert.assertEquals(1, ep.lazyInteger.get().intValue());
        Assert.assertEquals(1, counter.get());
        Assert.assertEquals(2, ep.providerOfInteger.get().intValue());// fresh instance

        Assert.assertEquals(1, ep.lazyInteger.get().intValue());// still the same instance

        Assert.assertEquals(2, counter.get());
        Assert.assertEquals(3, ep.providerOfInteger.get().intValue());// fresh instance

        Assert.assertEquals(1, ep.lazyInteger.get().intValue());// still the same instance.

    }
}

