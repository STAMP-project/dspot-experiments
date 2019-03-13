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
package org.apache.camel.component.reactive.streams;


import java.util.List;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Test the number of refill requests that are sent to a published from a Camel consumer.
 */
public class RequestRefillTest extends CamelTestSupport {
    @Test
    public void testUnboundedRequests() throws Exception {
        int numReqs = 100;
        List<Long> requests = executeTest("unbounded", numReqs);
        assertEquals(1, requests.size());
        assertEquals(Long.MAX_VALUE, requests.get(0).longValue());
    }

    @Test
    public void testUnboundedRequestsWatermarkNoEffect() throws Exception {
        int numReqs = 100;
        List<Long> requests = executeTest("unbounded-100", numReqs);
        assertEquals(1, requests.size());
        assertEquals(Long.MAX_VALUE, requests.get(0).longValue());
    }

    @Test
    public void testBoundedRequests() throws Exception {
        int numReqs = 100;
        List<Long> requests = executeTest("bounded", numReqs);
        assertTrue(((requests.size()) >= (numReqs / 10)));
    }

    @Test
    public void testBoundedRequestsPercentageRefill() throws Exception {
        int numReqs = 120;
        List<Long> requests0 = executeTest("bounded-0", numReqs);
        List<Long> requests10 = executeTest("bounded-10", numReqs);
        List<Long> requests25 = executeTest("bounded", numReqs);
        List<Long> requests80 = executeTest("bounded-80", numReqs);
        List<Long> requests100 = executeTest("bounded-100", numReqs);
        assertTrue(((requests0.size()) <= (requests10.size())));// too close

        assertTrue(((requests10.size()) < (requests25.size())));
        assertTrue(((requests25.size()) < (requests80.size())));
        assertTrue(((requests80.size()) < (requests100.size())));
    }
}

