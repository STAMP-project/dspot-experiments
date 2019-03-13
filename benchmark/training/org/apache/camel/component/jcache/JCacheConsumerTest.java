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
package org.apache.camel.component.jcache;


import JCacheConstants.KEY;
import javax.cache.Cache;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class JCacheConsumerTest extends JCacheComponentTestSupport {
    @Test
    public void testFilters() throws Exception {
        final Cache<Object, Object> cache = getCacheFromEndpoint("jcache://test-cache");
        final String key = randomString();
        final String val1 = "to-filter-" + (randomString());
        final String val2 = randomString();
        cache.put(key, val1);
        cache.put(key, val2);
        cache.remove(key);
        MockEndpoint mockCreated = getMockEndpoint("mock:created");
        mockCreated.expectedMinimumMessageCount(1);
        mockCreated.expectedHeaderReceived(KEY, key);
        mockCreated.expectedMessagesMatches(new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                return exchange.getIn().getBody(String.class).equals(val1);
            }
        });
        MockEndpoint mockUpdated = getMockEndpoint("mock:updated");
        mockUpdated.expectedMinimumMessageCount(1);
        mockUpdated.expectedHeaderReceived(KEY, key);
        mockUpdated.expectedMessagesMatches(new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                return exchange.getIn().getBody(String.class).equals(val2);
            }
        });
        MockEndpoint mockRemoved = getMockEndpoint("mock:removed");
        mockRemoved.expectedMinimumMessageCount(1);
        mockRemoved.expectedHeaderReceived(KEY, key);
        mockRemoved.expectedMessagesMatches(new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                return (exchange.getIn().getBody(String.class)) == null;
            }
        });
        MockEndpoint mockMyFilter = getMockEndpoint("mock:my-filter");
        mockMyFilter.expectedMinimumMessageCount(1);
        mockMyFilter.expectedHeaderReceived(KEY, key);
        mockMyFilter.expectedMessagesMatches(new Predicate() {
            @Override
            public boolean matches(Exchange exchange) {
                return exchange.getIn().getBody(String.class).equals(val2);
            }
        });
        assertMockEndpointsSatisfied();
    }
}

