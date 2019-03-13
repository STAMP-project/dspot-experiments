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


import javax.cache.Cache;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;


public class SpringJCachePolicyTest extends CamelSpringTestSupport {
    // Verify value gets cached and route is not executed for the second time
    @Test
    public void testXmlDslValueGetsCached() throws Exception {
        final String key = JCachePolicyTestBase.randomString();
        MockEndpoint mock = getMockEndpoint("mock:spring");
        // Send first, key is not in cache
        Object responseBody = template().requestBody("direct:spring", key);
        // We got back the value, mock was called once, value got cached.
        Cache cache = JCachePolicyTestBase.lookupCache("spring");
        assertEquals(JCachePolicyTestBase.generateValue(key), cache.get(key));
        assertEquals(JCachePolicyTestBase.generateValue(key), responseBody);
        assertEquals(1, mock.getExchanges().size());
        // Send again, key is already in cache
        responseBody = template().requestBody("direct:spring", key);
        // We got back the stored value, but the mock was not called again
        assertEquals(JCachePolicyTestBase.generateValue(key), cache.get(key));
        assertEquals(JCachePolicyTestBase.generateValue(key), responseBody);
        assertEquals(1, mock.getExchanges().size());
    }

    // Verify if we call the route with different keys, both gets cached
    @Test
    public void testXmlDslDifferent() throws Exception {
        final String key1 = JCachePolicyTestBase.randomString();
        MockEndpoint mock = getMockEndpoint("mock:spring");
        // Send first, key is not in cache
        Object responseBody = template().requestBody("direct:spring", key1);
        // We got back the value, mock was called once, value got cached.
        Cache cache = JCachePolicyTestBase.lookupCache("spring");
        assertEquals(JCachePolicyTestBase.generateValue(key1), cache.get(key1));
        assertEquals(JCachePolicyTestBase.generateValue(key1), responseBody);
        assertEquals(1, mock.getExchanges().size());
        // Send again, different key
        final String key2 = JCachePolicyTestBase.randomString();
        responseBody = template().requestBody("direct:spring", key2);
        // We got back the stored value, mock was called again, value got cached.
        assertEquals(JCachePolicyTestBase.generateValue(key2), cache.get(key2));
        assertEquals(JCachePolicyTestBase.generateValue(key2), responseBody);
        assertEquals(2, mock.getExchanges().size());
    }

    // Verify policy applies only on the section of the route wrapped
    @Test
    public void testXmlDslPartial() throws Exception {
        final String key = JCachePolicyTestBase.randomString();
        MockEndpoint mock = getMockEndpoint("mock:spring");
        MockEndpoint mockUnwrapped = getMockEndpoint("mock:unwrapped");
        // Send first, key is not in cache
        Object responseBody = template().requestBody("direct:spring-partial", key);
        // We got back the value, mock was called once, value got cached.
        Cache cache = JCachePolicyTestBase.lookupCache("spring");
        assertEquals(JCachePolicyTestBase.generateValue(key), cache.get(key));
        assertEquals(JCachePolicyTestBase.generateValue(key), responseBody);
        assertEquals(1, mock.getExchanges().size());
        assertEquals(1, mockUnwrapped.getExchanges().size());
        // Send again, key is already in cache
        responseBody = template().requestBody("direct:spring-partial", key);
        // We got back the stored value, but the mock was not called again
        assertEquals(JCachePolicyTestBase.generateValue(key), cache.get(key));
        assertEquals(JCachePolicyTestBase.generateValue(key), responseBody);
        assertEquals(1, mock.getExchanges().size());
        assertEquals(2, mockUnwrapped.getExchanges().size());
    }

    // Use a key expression ${header.mykey}
    @Test
    public void testXmlDslKeyExpression() throws Exception {
        final String key = JCachePolicyTestBase.randomString();
        final String body = JCachePolicyTestBase.randomString();
        MockEndpoint mock = getMockEndpoint("mock:spring");
        // Send first, key is not in cache
        Object responseBody = template().requestBodyAndHeader("direct:spring-byheader", body, "mykey", key);
        // We got back the value, mock was called once, value got cached.
        Cache cache = JCachePolicyTestBase.lookupCache("spring");
        assertEquals(JCachePolicyTestBase.generateValue(body), cache.get(key));
        assertEquals(JCachePolicyTestBase.generateValue(body), responseBody);
        assertEquals(1, mock.getExchanges().size());
        // Send again, use another body, but the same key
        responseBody = template().requestBodyAndHeader("direct:spring-byheader", JCachePolicyTestBase.randomString(), "mykey", key);
        // We got back the stored value, and the mock was not called again
        assertEquals(JCachePolicyTestBase.generateValue(body), cache.get(key));
        assertEquals(JCachePolicyTestBase.generateValue(body), responseBody);
        assertEquals(1, mock.getExchanges().size());
    }
}

