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
package org.apache.camel.component.ehcache;


import EhcacheConstants.EVENT_TYPE;
import EhcacheConstants.KEY;
import EhcacheConstants.OLD_VALUE;
import org.apache.camel.component.mock.MockEndpoint;
import org.ehcache.Cache;
import org.ehcache.event.EventType;
import org.junit.Test;


public class EhcacheConsumerTest extends EhcacheTestSupport {
    @Test
    public void testEvents() throws Exception {
        String key = EhcacheTestSupport.generateRandomString();
        String[] values = EhcacheTestSupport.generateRandomArrayOfStrings(2);
        MockEndpoint created = getMockEndpoint("mock:created");
        created.expectedMinimumMessageCount(1);
        created.expectedHeaderReceived(KEY, key);
        created.expectedHeaderReceived(EVENT_TYPE, EventType.CREATED);
        MockEndpoint updated = getMockEndpoint("mock:updated");
        updated.expectedMinimumMessageCount(1);
        updated.expectedHeaderReceived(KEY, key);
        updated.expectedHeaderReceived(OLD_VALUE, values[0]);
        updated.expectedHeaderReceived(EVENT_TYPE, EventType.UPDATED);
        MockEndpoint all = getMockEndpoint("mock:all");
        all.expectedMinimumMessageCount(2);
        all.expectedHeaderValuesReceivedInAnyOrder(KEY, key, key);
        all.expectedHeaderValuesReceivedInAnyOrder(OLD_VALUE, null, values[0]);
        all.expectedHeaderValuesReceivedInAnyOrder(EVENT_TYPE, EventType.CREATED, EventType.UPDATED);
        all.expectedBodiesReceived(values);
        Cache<Object, Object> cache = getCache(EhcacheTestSupport.TEST_CACHE_NAME);
        cache.put(key, values[0]);
        cache.put(key, values[1]);
        assertMockEndpointsSatisfied();
    }
}

