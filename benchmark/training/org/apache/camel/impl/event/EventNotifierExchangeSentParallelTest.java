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
package org.apache.camel.impl.event;


import org.apache.camel.ContextTestSupport;
import org.apache.camel.spi.CamelEvent;
import org.junit.Assert;
import org.junit.Test;


public class EventNotifierExchangeSentParallelTest extends EventNotifierExchangeSentTest {
    @Test
    public void testExchangeSentRecipient() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBodyAndHeader("direct:foo", "Hello World", "foo", "direct:cool,direct:start");
        // wait for the message to be fully done using oneExchangeDone
        assertMockEndpointsSatisfied();
        Assert.assertTrue(oneExchangeDone.matchesMockWaitTime());
        // stop Camel to let all the events complete
        context.stop();
        Assert.assertTrue(("Should be 11 or more, was: " + (EventNotifierExchangeSentTest.events.size())), ((EventNotifierExchangeSentTest.events.size()) >= 11));
        // we run parallel so just assert we got 6 sending and 6 sent events
        int sent = 0;
        int sending = 0;
        for (CamelEvent event : EventNotifierExchangeSentTest.events) {
            if (event instanceof ExchangeSendingEvent) {
                sending++;
            } else {
                sent++;
            }
        }
        Assert.assertTrue(("There should be 5 or more, was " + sending), (sending >= 5));
        Assert.assertTrue(("There should be 5 or more, was " + sent), (sent >= 5));
    }
}

