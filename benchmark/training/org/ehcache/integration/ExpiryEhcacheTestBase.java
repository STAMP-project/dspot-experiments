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
package org.ehcache.integration;


import java.util.Arrays;
import java.util.HashSet;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ludovic Orban
 */
public abstract class ExpiryEhcacheTestBase {
    private CacheManager cacheManager;

    private Cache<Number, CharSequence> testCache;

    private final TestTimeSource manualTimeSource = new TestTimeSource();

    @Test
    public void testSimplePutWithExpiry() throws Exception {
        insert(testCache, getEntries());
        Assert.assertThat(ExpiryEhcacheTestBase.cacheSize(testCache), Is.is(2));
        manualTimeSource.setTimeMillis(1001);
        Assert.assertThat(ExpiryEhcacheTestBase.cacheSize(testCache), Is.is(0));
    }

    @Test
    public void testSimplePutWithExpiry_get() throws Exception {
        insert(testCache, getEntries());
        Assert.assertThat(ExpiryEhcacheTestBase.cacheSize(testCache), Is.is(2));
        manualTimeSource.setTimeMillis(1001);
        Assert.assertThat(testCache.get(1), Is.is(Matchers.nullValue()));
        Assert.assertThat(testCache.get(2), Is.is(Matchers.nullValue()));
    }

    @Test
    public void testSimplePutWithExpiry_getAll() throws Exception {
        insert(testCache, getEntries());
        Assert.assertThat(ExpiryEhcacheTestBase.cacheSize(testCache), Is.is(2));
        manualTimeSource.setTimeMillis(1001);
        Assert.assertThat(testCache.getAll(new HashSet<Number>(Arrays.asList(1, 2))).size(), Is.is(2));
    }

    @Test
    public void testSimplePutWithExpiry_putIfAbsent() throws Exception {
        insert(testCache, getEntries());
        Assert.assertThat(ExpiryEhcacheTestBase.cacheSize(testCache), Is.is(2));
        manualTimeSource.setTimeMillis(1001);
        Assert.assertThat(testCache.putIfAbsent(1, "one#2"), Is.is(Matchers.nullValue()));
        Assert.assertThat(testCache.get(1), Matchers.<CharSequence>equalTo("one#2"));
        Assert.assertThat(testCache.putIfAbsent(2, "two#2"), Is.is(Matchers.nullValue()));
        Assert.assertThat(testCache.get(2), Matchers.<CharSequence>equalTo("two#2"));
    }

    @Test
    public void testSimplePutWithExpiry_remove2Args() throws Exception {
        insert(testCache, getEntries());
        Assert.assertThat(ExpiryEhcacheTestBase.cacheSize(testCache), Is.is(2));
        manualTimeSource.setTimeMillis(1001);
        Assert.assertThat(testCache.remove(1, "one"), Is.is(false));
        Assert.assertThat(testCache.get(1), Is.is(Matchers.nullValue()));
        Assert.assertThat(testCache.remove(2, "two"), Is.is(false));
        Assert.assertThat(testCache.get(2), Is.is(Matchers.nullValue()));
    }

    @Test
    public void testSimplePutWithExpiry_replace2Args() throws Exception {
        insert(testCache, getEntries());
        Assert.assertThat(ExpiryEhcacheTestBase.cacheSize(testCache), Is.is(2));
        manualTimeSource.setTimeMillis(1001);
        Assert.assertThat(testCache.replace(1, "one#2"), Is.is(Matchers.nullValue()));
        Assert.assertThat(testCache.get(1), Is.is(Matchers.nullValue()));
        Assert.assertThat(testCache.replace(2, "two#2"), Is.is(Matchers.nullValue()));
        Assert.assertThat(testCache.get(2), Is.is(Matchers.nullValue()));
    }

    @Test
    public void testSimplePutWithExpiry_replace3Args() throws Exception {
        insert(testCache, getEntries());
        Assert.assertThat(ExpiryEhcacheTestBase.cacheSize(testCache), Is.is(2));
        manualTimeSource.setTimeMillis(1001);
        Assert.assertThat(testCache.replace(1, "one", "one#2"), Is.is(false));
        Assert.assertThat(testCache.get(1), Is.is(Matchers.nullValue()));
        Assert.assertThat(testCache.replace(2, "two", "two#2"), Is.is(false));
        Assert.assertThat(testCache.get(2), Is.is(Matchers.nullValue()));
    }
}

