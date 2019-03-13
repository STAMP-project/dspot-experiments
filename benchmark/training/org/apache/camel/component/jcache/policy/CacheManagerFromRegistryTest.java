/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jcache.policy;


import java.net.URI;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import org.junit.Test;


// This test requires a registered CacheManager, but the others do not.
public class CacheManagerFromRegistryTest extends JCachePolicyTestBase {
    // Register cacheManager in CamelContext. Set cacheName
    @Test
    public void testCacheManagerFromContext() throws Exception {
        final String key = JCachePolicyTestBase.randomString();
        // Send exchange
        Object responseBody = template().requestBody("direct:policy-context-manager", key);
        // Verify the cacheManager "hzsecond" registered in the CamelContext was used
        assertNull(JCachePolicyTestBase.lookupCache("contextCacheManager"));
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager(URI.create("hzsecond"), null);
        Cache cache = cacheManager.getCache("contextCacheManager");
        assertEquals(JCachePolicyTestBase.generateValue(key), cache.get(key));
        assertEquals(JCachePolicyTestBase.generateValue(key), responseBody);
        assertEquals(1, getMockEndpoint("mock:value").getExchanges().size());
    }
}

