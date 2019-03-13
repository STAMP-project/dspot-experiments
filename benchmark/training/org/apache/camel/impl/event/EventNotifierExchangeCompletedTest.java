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


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.TestSupport;
import org.apache.camel.spi.CamelEvent;
import org.junit.Assert;
import org.junit.Test;


public class EventNotifierExchangeCompletedTest extends ContextTestSupport {
    private static List<CamelEvent> events = new ArrayList<>();

    @Test
    public void testExchangeCompleted() throws Exception {
        getMockEndpoint("mock:result").expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        Assert.assertEquals(1, EventNotifierExchangeCompletedTest.events.size());
        // get the event
        ExchangeCompletedEvent event = ((ExchangeCompletedEvent) (EventNotifierExchangeCompletedTest.events.get(0)));
        Assert.assertNotNull(event.getExchange());
        Assert.assertNotNull(event.getExchange().getFromEndpoint());
        Assert.assertEquals("direct://start", event.getExchange().getFromEndpoint().getEndpointUri());
        // grab the created timestamp
        Date created = event.getExchange().getCreated();
        Assert.assertNotNull(created);
        // calculate elapsed time
        Date now = new Date();
        long elapsed = (now.getTime()) - (created.getTime());
        Assert.assertTrue(("Should be > 400, was: " + elapsed), (elapsed > 400));
        log.info(("Elapsed time in millis: " + elapsed));
    }
}

