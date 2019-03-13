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
package org.apache.camel.component.micrometer.eventNotifier;


import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import org.apache.camel.Exchange;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.ExpressionAdapter;
import org.junit.Test;


public class MicrometerExchangeEventNotifierTest extends AbstractMicrometerEventNotifierTest {
    private static final String ROUTE_ID = "test";

    private static final String MOCK_OUT = "mock://out";

    private static final String DIRECT_IN = "direct://in";

    private static final Long SLEEP = 20L;

    @Test
    public void testCamelRouteEvents() throws Exception {
        int count = 10;
        MockEndpoint mock = getMockEndpoint(MicrometerExchangeEventNotifierTest.MOCK_OUT);
        mock.returnReplyBody(new ExpressionAdapter() {
            @Override
            public Object evaluate(Exchange exchange) {
                try {
                    Thread.sleep(MicrometerExchangeEventNotifierTest.SLEEP);
                    return exchange.getIn().getBody();
                } catch (InterruptedException e) {
                    throw new org.apache.camel.CamelExecutionException(e.getMessage(), exchange, e);
                }
            }
        });
        mock.expectedMessageCount(count);
        for (int i = 0; i < count; i++) {
            template.sendBody(MicrometerExchangeEventNotifierTest.DIRECT_IN, new Object());
        }
        Timer timer = meterRegistry.find(MicrometerExchangeEventNotifierTest.MOCK_OUT).timer();
        assertEquals(count, timer.count());
        assertTrue(((timer.mean(TimeUnit.MILLISECONDS)) > (MicrometerExchangeEventNotifierTest.SLEEP.doubleValue())));
    }
}

