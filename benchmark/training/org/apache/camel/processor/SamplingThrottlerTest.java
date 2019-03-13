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


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class SamplingThrottlerTest extends ContextTestSupport {
    @Test
    public void testSamplingFromExchangeStream() throws Exception {
        NotifyBuilder notify = whenDone(15).create();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(2);
        mock.setResultWaitTime(3000);
        List<Exchange> sentExchanges = new ArrayList<>();
        sendExchangesThroughDroppingThrottler(sentExchanges, 15);
        notify.matchesMockWaitTime();
        mock.assertIsSatisfied();
        validateDroppedExchanges(sentExchanges, mock.getReceivedCounter());
    }

    @Test
    public void testBurstySampling() throws Exception {
        NotifyBuilder notify = whenDone(5).create();
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(2);
        mock.setResultWaitTime(3000);
        List<Exchange> sentExchanges = new ArrayList<>();
        // send a burst of 5 exchanges, expecting only one to get through
        sendExchangesThroughDroppingThrottler(sentExchanges, 5);
        // sleep through a complete period
        Thread.sleep(1100);
        // send another 5 now
        sendExchangesThroughDroppingThrottler(sentExchanges, 5);
        notify.matchesMockWaitTime();
        mock.assertIsSatisfied();
        validateDroppedExchanges(sentExchanges, mock.getReceivedCounter());
    }

    @Test
    public void testSendLotsOfMessagesSimultaneouslyButOnly3GetThrough() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(3);
        mock.setResultWaitTime(4000);
        final List<Exchange> sentExchanges = Collections.synchronizedList(new ArrayList<Exchange>());
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        sendExchangesThroughDroppingThrottler(sentExchanges, 35);
                    } catch (Exception e) {
                        // ignore
                    }
                }
            });
        }
        mock.assertIsSatisfied();
        executor.shutdownNow();
    }

    @Test
    public void testSamplingUsingMessageFrequency() throws Exception {
        long totalMessages = 100;
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(10);
        mock.setResultWaitTime(100);
        for (int i = 0; i < totalMessages; i++) {
            template.sendBody("direct:sample-messageFrequency", (("<message>" + i) + "</message>"));
        }
        mock.assertIsSatisfied();
    }

    @Test
    public void testSamplingUsingMessageFrequencyViaDSL() throws Exception {
        long totalMessages = 50;
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(10);
        mock.setResultWaitTime(100);
        for (int i = 0; i < totalMessages; i++) {
            template.sendBody("direct:sample-messageFrequency-via-dsl", (("<message>" + i) + "</message>"));
        }
        mock.assertIsSatisfied();
    }
}

