/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.cache.jcache.interceptor;


import java.util.Collection;
import javax.cache.annotation.CacheResult;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.cache.Cache;
import org.springframework.cache.jcache.AbstractJCacheTests;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class CacheResolverAdapterTests extends AbstractJCacheTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void resolveSimpleCache() throws Exception {
        DefaultCacheInvocationContext<?> dummyContext = createDummyContext();
        CacheResolverAdapter adapter = new CacheResolverAdapter(getCacheResolver(dummyContext, "testCache"));
        Collection<? extends Cache> caches = adapter.resolveCaches(dummyContext);
        Assert.assertNotNull(caches);
        Assert.assertEquals(1, caches.size());
        Assert.assertEquals("testCache", getName());
    }

    @Test
    public void resolveUnknownCache() throws Exception {
        DefaultCacheInvocationContext<?> dummyContext = createDummyContext();
        CacheResolverAdapter adapter = new CacheResolverAdapter(getCacheResolver(dummyContext, null));
        thrown.expect(IllegalStateException.class);
        adapter.resolveCaches(dummyContext);
    }

    static class Sample {
        @CacheResult
        public Object get(String id) {
            return null;
        }
    }
}

