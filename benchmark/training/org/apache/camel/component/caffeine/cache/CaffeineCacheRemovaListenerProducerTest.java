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
package org.apache.camel.component.caffeine.cache;


import CaffeineConstants.ACTION;
import CaffeineConstants.ACTION_CLEANUP;
import CaffeineConstants.ACTION_GET;
import CaffeineConstants.ACTION_GET_ALL;
import CaffeineConstants.ACTION_HAS_RESULT;
import CaffeineConstants.ACTION_INVALIDATE;
import CaffeineConstants.ACTION_INVALIDATE_ALL;
import CaffeineConstants.ACTION_PUT;
import CaffeineConstants.ACTION_PUT_ALL;
import CaffeineConstants.ACTION_SUCCEEDED;
import CaffeineConstants.KEY;
import CaffeineConstants.KEYS;
import com.github.benmanes.caffeine.cache.Cache;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class CaffeineCacheRemovaListenerProducerTest extends CaffeineCacheTestSupport {
    // ****************************
    // Clear
    // ****************************
    @Test
    public void testCacheClear() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        mock.expectedBodiesReceived(((Object) (null)));
        mock.expectedHeaderReceived(ACTION_HAS_RESULT, false);
        mock.expectedHeaderReceived(ACTION_SUCCEEDED, true);
        fluentTemplate().withHeader(ACTION, ACTION_CLEANUP).to("direct://start").send();
        assertMockEndpointsSatisfied();
    }

    // ****************************
    // Put
    // ****************************
    @Test
    public void testCachePut() throws Exception {
        final String key = CaffeineCacheTestSupport.generateRandomString();
        final String val = CaffeineCacheTestSupport.generateRandomString();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        mock.expectedBodiesReceived(val);
        mock.expectedHeaderReceived(ACTION_HAS_RESULT, false);
        mock.expectedHeaderReceived(ACTION_SUCCEEDED, true);
        fluentTemplate().withHeader(ACTION, ACTION_PUT).withHeader(KEY, key).withBody(val).to("direct://start").send();
        assertTrue(((getTestRemovalListenerCache().getIfPresent(key)) != null));
        assertEquals(val, getTestRemovalListenerCache().getIfPresent(key));
    }

    @Test
    public void testCachePutAll() throws Exception {
        final Map<String, String> map = CaffeineCacheTestSupport.generateRandomMapOfString(3);
        final Set<String> keys = map.keySet().stream().limit(2).collect(Collectors.toSet());
        fluentTemplate().withHeader(ACTION, ACTION_PUT_ALL).withBody(map).to("direct://start").send();
        MockEndpoint mock1 = getMockEndpoint("mock:result");
        mock1.expectedMinimumMessageCount(1);
        mock1.expectedHeaderReceived(ACTION_HAS_RESULT, false);
        mock1.expectedHeaderReceived(ACTION_SUCCEEDED, true);
        final Map<String, String> elements = getTestRemovalListenerCache().getAllPresent(keys);
        keys.forEach(( k) -> {
            assertTrue(elements.containsKey(k));
            assertEquals(map.get(k), elements.get(k));
        });
        assertMockEndpointsSatisfied();
    }

    // ****************************
    // Get
    // ****************************
    @Test
    public void testCacheGet() throws Exception {
        final Cache<Object, Object> cache = getTestRemovalListenerCache();
        final String key = CaffeineCacheTestSupport.generateRandomString();
        final String val = CaffeineCacheTestSupport.generateRandomString();
        cache.put(key, val);
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        mock.expectedBodiesReceived(val);
        mock.expectedHeaderReceived(ACTION_HAS_RESULT, true);
        mock.expectedHeaderReceived(ACTION_SUCCEEDED, true);
        fluentTemplate().withHeader(ACTION, ACTION_GET).withHeader(KEY, key).withBody(val).to("direct://start").send();
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testCacheGetAll() throws Exception {
        final Cache<Object, Object> cache = getTestRemovalListenerCache();
        final Map<String, String> map = CaffeineCacheTestSupport.generateRandomMapOfString(3);
        final Set<String> keys = map.keySet().stream().limit(2).collect(Collectors.toSet());
        cache.putAll(map);
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        mock.expectedHeaderReceived(ACTION_HAS_RESULT, true);
        mock.expectedHeaderReceived(ACTION_SUCCEEDED, true);
        fluentTemplate().withHeader(ACTION, ACTION_GET_ALL).withHeader(KEYS, keys).to("direct://start").send();
        assertMockEndpointsSatisfied();
        final Map<String, String> elements = mock.getExchanges().get(0).getIn().getBody(Map.class);
        keys.forEach(( k) -> {
            assertTrue(elements.containsKey(k));
            assertEquals(map.get(k), elements.get(k));
        });
    }

    // 
    // ****************************
    // INVALIDATE
    // ****************************
    @Test
    public void testCacheInvalidate() throws Exception {
        final Cache<Object, Object> cache = getTestRemovalListenerCache();
        final String key = CaffeineCacheTestSupport.generateRandomString();
        final String val = CaffeineCacheTestSupport.generateRandomString();
        cache.put(key, val);
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        mock.expectedHeaderReceived(ACTION_HAS_RESULT, false);
        mock.expectedHeaderReceived(ACTION_SUCCEEDED, true);
        fluentTemplate().withHeader(ACTION, ACTION_INVALIDATE).withHeader(KEY, key).to("direct://start").send();
        assertMockEndpointsSatisfied();
        assertFalse(((cache.getIfPresent(key)) != null));
    }

    @Test
    public void testCacheInvalidateAll() throws Exception {
        final Cache<Object, Object> cache = getTestRemovalListenerCache();
        final Map<String, String> map = CaffeineCacheTestSupport.generateRandomMapOfString(3);
        final Set<String> keys = map.keySet().stream().limit(2).collect(Collectors.toSet());
        cache.putAll(map);
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(1);
        mock.expectedHeaderReceived(ACTION_HAS_RESULT, false);
        mock.expectedHeaderReceived(ACTION_SUCCEEDED, true);
        fluentTemplate().withHeader(ACTION, ACTION_INVALIDATE_ALL).withHeader(KEYS, keys).to("direct://start").send();
        assertMockEndpointsSatisfied();
        final Map<String, String> elements = getTestRemovalListenerCache().getAllPresent(keys);
        keys.forEach(( k) -> {
            assertFalse(elements.containsKey(k));
        });
    }

    @Test
    public void testStats() throws Exception {
        final Map<String, String> map = CaffeineCacheTestSupport.generateRandomMapOfString(3);
        final Set<String> keys = map.keySet().stream().limit(2).collect(Collectors.toSet());
        fluentTemplate().withHeader(ACTION, ACTION_PUT_ALL).withBody(map).to("direct://start").send();
        MockEndpoint mock1 = getMockEndpoint("mock:result");
        mock1.expectedMinimumMessageCount(1);
        mock1.expectedHeaderReceived(ACTION_HAS_RESULT, false);
        mock1.expectedHeaderReceived(ACTION_SUCCEEDED, true);
        final Map<String, String> elements = getTestRemovalListenerCache().getAllPresent(keys);
        keys.forEach(( k) -> {
            assertTrue(elements.containsKey(k));
            assertEquals(map.get(k), elements.get(k));
        });
    }
}

