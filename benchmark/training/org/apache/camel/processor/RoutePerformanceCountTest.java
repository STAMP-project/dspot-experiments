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
package org.apache.camel.processor;


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.TestSupport;
import org.junit.Assert;
import org.junit.Test;


public class RoutePerformanceCountTest extends ContextTestSupport {
    private RoutePerformanceCountTest.CountProcessor processor = new RoutePerformanceCountTest.CountProcessor();

    private int size = 500;

    private String url = "direct:start";

    @Test
    public void testSendMessages() throws Exception {
        long start = System.currentTimeMillis();
        for (int i = 0; i < (size); i++) {
            template.sendBody(url, ("Message " + i));
        }
        Assert.assertEquals(size, processor.getCounter());
        long delta = (System.currentTimeMillis()) - start;
        log.info("RoutePerformanceCountTest: Sent: {} Took: {} ms", size, delta);
    }

    private static class CountProcessor implements Processor {
        private AtomicInteger counter = new AtomicInteger(0);

        public void process(Exchange exchange) throws Exception {
            counter.incrementAndGet();
        }

        public int getCounter() {
            return counter.intValue();
        }
    }
}

