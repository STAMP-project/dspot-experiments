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
import CaffeineConstants.ACTION_HAS_RESULT;
import CaffeineConstants.ACTION_PUT_ALL;
import CaffeineConstants.ACTION_SUCCEEDED;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class CaffeineCacheStatsCounterProducerTest extends CaffeineCacheTestSupport {
    @Test
    public void testStats() throws Exception {
        final Map<String, String> map = CaffeineCacheTestSupport.generateRandomMapOfString(3);
        final Set<String> keys = map.keySet().stream().limit(2).collect(Collectors.toSet());
        fluentTemplate().withHeader(ACTION, ACTION_PUT_ALL).withBody(map).to("direct://start").send();
        MockEndpoint mock1 = getMockEndpoint("mock:result");
        mock1.expectedMinimumMessageCount(1);
        mock1.expectedHeaderReceived(ACTION_HAS_RESULT, false);
        mock1.expectedHeaderReceived(ACTION_SUCCEEDED, true);
        final Map<String, String> elements = getTestStatsCounterCache().getAllPresent(keys);
        keys.forEach(( k) -> {
            assertTrue(elements.containsKey(k));
            assertEquals(map.get(k), elements.get(k));
        });
        assertEquals(2, getMetricRegistry().counter("camelcache.hits").getCount());
    }
}

