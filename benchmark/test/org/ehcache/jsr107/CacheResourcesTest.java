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
package org.ehcache.jsr107;


import java.io.Closeable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import org.ehcache.jsr107.internal.Jsr107CacheLoaderWriter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class CacheResourcesTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testRegisterDeregisterAfterClose() {
        Map<CacheEntryListenerConfiguration<Object, Object>, ListenerResources<Object, Object>> emptyMap = Collections.emptyMap();
        CacheResources<Object, Object> cacheResources = new CacheResources("cache", null, null, emptyMap);
        cacheResources.closeResources();
        try {
            cacheResources.registerCacheEntryListener(Mockito.mock(CacheEntryListenerConfiguration.class));
            Assert.fail();
        } catch (IllegalStateException ise) {
            // expected
        }
        try {
            cacheResources.deregisterCacheEntryListener(Mockito.mock(CacheEntryListenerConfiguration.class));
            Assert.fail();
        } catch (IllegalStateException ise) {
            // expected
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void closesAllResources() throws Exception {
        Jsr107CacheLoaderWriter<Object, Object> loaderWriter = Mockito.mock(Jsr107CacheLoaderWriter.class, Mockito.withSettings().extraInterfaces(Closeable.class));
        Eh107Expiry<Object, Object> expiry = Mockito.mock(Eh107Expiry.class, Mockito.withSettings().extraInterfaces(Closeable.class));
        CacheEntryListenerConfiguration<Object, Object> listenerConfiguration = Mockito.mock(CacheEntryListenerConfiguration.class);
        ListenerResources<Object, Object> listenerResources = Mockito.mock(ListenerResources.class);
        Map<CacheEntryListenerConfiguration<Object, Object>, ListenerResources<Object, Object>> map = new HashMap<>();
        map.put(listenerConfiguration, listenerResources);
        CacheResources<Object, Object> cacheResources = new CacheResources("cache", loaderWriter, expiry, map);
        cacheResources.closeResources();
        Mockito.verify(((Closeable) (loaderWriter))).close();
        Mockito.verify(((Closeable) (expiry))).close();
        Mockito.verify(listenerResources).close();
    }
}

