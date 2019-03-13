/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.cache.concurrent;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.cache.AbstractValueAdaptingCacheTests;


/**
 *
 *
 * @author Costin Leau
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 */
public class ConcurrentMapCacheTests extends AbstractValueAdaptingCacheTests<ConcurrentMapCache> {
    protected ConcurrentMap<Object, Object> nativeCache;

    protected ConcurrentMapCache cache;

    protected ConcurrentMap<Object, Object> nativeCacheNoNull;

    protected ConcurrentMapCache cacheNoNull;

    @Test
    public void testIsStoreByReferenceByDefault() {
        Assert.assertFalse(this.cache.isStoreByValue());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSerializer() {
        ConcurrentMapCache serializeCache = createCacheWithStoreByValue();
        Assert.assertTrue(serializeCache.isStoreByValue());
        Object key = createRandomKey();
        List<String> content = new ArrayList<>();
        content.addAll(Arrays.asList("one", "two", "three"));
        serializeCache.put(key, content);
        content.remove(0);
        List<String> entry = ((List<String>) (serializeCache.get(key).get()));
        Assert.assertEquals(3, entry.size());
        Assert.assertEquals("one", entry.get(0));
    }

    @Test
    public void testNonSerializableContent() {
        ConcurrentMapCache serializeCache = createCacheWithStoreByValue();
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Failed to serialize");
        this.thrown.expectMessage(this.cache.getClass().getName());
        serializeCache.put(createRandomKey(), this.cache);
    }

    @Test
    public void testInvalidSerializedContent() {
        ConcurrentMapCache serializeCache = createCacheWithStoreByValue();
        String key = createRandomKey();
        this.nativeCache.put(key, "Some garbage");
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Failed to deserialize");
        this.thrown.expectMessage("Some garbage");
        serializeCache.get(key);
    }
}

