/**
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.test.context.cache;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.MergedContextConfiguration;


/**
 * Unit tests for the LRU eviction policy in {@link DefaultContextCache}.
 *
 * @author Sam Brannen
 * @since 4.3
 * @see ContextCacheTests
 */
public class LruContextCacheTests {
    private static final MergedContextConfiguration abcConfig = LruContextCacheTests.config(LruContextCacheTests.Abc.class);

    private static final MergedContextConfiguration fooConfig = LruContextCacheTests.config(LruContextCacheTests.Foo.class);

    private static final MergedContextConfiguration barConfig = LruContextCacheTests.config(LruContextCacheTests.Bar.class);

    private static final MergedContextConfiguration bazConfig = LruContextCacheTests.config(LruContextCacheTests.Baz.class);

    private final ConfigurableApplicationContext abcContext = Mockito.mock(ConfigurableApplicationContext.class);

    private final ConfigurableApplicationContext fooContext = Mockito.mock(ConfigurableApplicationContext.class);

    private final ConfigurableApplicationContext barContext = Mockito.mock(ConfigurableApplicationContext.class);

    private final ConfigurableApplicationContext bazContext = Mockito.mock(ConfigurableApplicationContext.class);

    @Test(expected = IllegalArgumentException.class)
    public void maxCacheSizeNegativeOne() {
        new DefaultContextCache((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void maxCacheSizeZero() {
        new DefaultContextCache(0);
    }

    @Test
    public void maxCacheSizeOne() {
        DefaultContextCache cache = new DefaultContextCache(1);
        Assert.assertEquals(0, cache.size());
        Assert.assertEquals(1, cache.getMaxSize());
        cache.put(LruContextCacheTests.fooConfig, fooContext);
        LruContextCacheTests.assertCacheContents(cache, "Foo");
        cache.put(LruContextCacheTests.fooConfig, fooContext);
        LruContextCacheTests.assertCacheContents(cache, "Foo");
        cache.put(LruContextCacheTests.barConfig, barContext);
        LruContextCacheTests.assertCacheContents(cache, "Bar");
        cache.put(LruContextCacheTests.fooConfig, fooContext);
        LruContextCacheTests.assertCacheContents(cache, "Foo");
    }

    @Test
    public void maxCacheSizeThree() {
        DefaultContextCache cache = new DefaultContextCache(3);
        Assert.assertEquals(0, cache.size());
        Assert.assertEquals(3, cache.getMaxSize());
        cache.put(LruContextCacheTests.fooConfig, fooContext);
        LruContextCacheTests.assertCacheContents(cache, "Foo");
        cache.put(LruContextCacheTests.fooConfig, fooContext);
        LruContextCacheTests.assertCacheContents(cache, "Foo");
        cache.put(LruContextCacheTests.barConfig, barContext);
        LruContextCacheTests.assertCacheContents(cache, "Foo", "Bar");
        cache.put(LruContextCacheTests.bazConfig, bazContext);
        LruContextCacheTests.assertCacheContents(cache, "Foo", "Bar", "Baz");
        cache.put(LruContextCacheTests.abcConfig, abcContext);
        LruContextCacheTests.assertCacheContents(cache, "Bar", "Baz", "Abc");
    }

    @Test
    public void ensureLruOrderingIsUpdated() {
        DefaultContextCache cache = new DefaultContextCache(3);
        // Note: when a new entry is added it is considered the MRU entry and inserted at the tail.
        cache.put(LruContextCacheTests.fooConfig, fooContext);
        cache.put(LruContextCacheTests.barConfig, barContext);
        cache.put(LruContextCacheTests.bazConfig, bazContext);
        LruContextCacheTests.assertCacheContents(cache, "Foo", "Bar", "Baz");
        // Note: the MRU entry is moved to the tail when accessed.
        cache.get(LruContextCacheTests.fooConfig);
        LruContextCacheTests.assertCacheContents(cache, "Bar", "Baz", "Foo");
        cache.get(LruContextCacheTests.barConfig);
        LruContextCacheTests.assertCacheContents(cache, "Baz", "Foo", "Bar");
        cache.get(LruContextCacheTests.bazConfig);
        LruContextCacheTests.assertCacheContents(cache, "Foo", "Bar", "Baz");
        cache.get(LruContextCacheTests.barConfig);
        LruContextCacheTests.assertCacheContents(cache, "Foo", "Baz", "Bar");
    }

    @Test
    public void ensureEvictedContextsAreClosed() {
        DefaultContextCache cache = new DefaultContextCache(2);
        cache.put(LruContextCacheTests.fooConfig, fooContext);
        cache.put(LruContextCacheTests.barConfig, barContext);
        LruContextCacheTests.assertCacheContents(cache, "Foo", "Bar");
        cache.put(LruContextCacheTests.bazConfig, bazContext);
        LruContextCacheTests.assertCacheContents(cache, "Bar", "Baz");
        Mockito.verify(fooContext, Mockito.times(1)).close();
        cache.put(LruContextCacheTests.abcConfig, abcContext);
        LruContextCacheTests.assertCacheContents(cache, "Baz", "Abc");
        Mockito.verify(barContext, Mockito.times(1)).close();
        Mockito.verify(abcContext, Mockito.never()).close();
        Mockito.verify(bazContext, Mockito.never()).close();
    }

    private static class Abc {}

    private static class Foo {}

    private static class Bar {}

    private static class Baz {}
}

