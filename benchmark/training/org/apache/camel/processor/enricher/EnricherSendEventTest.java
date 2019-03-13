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
package org.apache.camel.processor.enricher;


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.spi.CamelEvent.ExchangeSendingEvent;
import org.apache.camel.spi.CamelEvent.ExchangeSentEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.junit.Assert;
import org.junit.Test;


public class EnricherSendEventTest extends ContextTestSupport {
    private EnricherSendEventTest.MyEventNotifier en = new EnricherSendEventTest.MyEventNotifier();

    @Test
    public void testAsyncEnricher() throws Exception {
        template.sendBody("direct:start1", "test");
        Assert.assertEquals("Get a wrong sending event number", 3, en.exchangeSendingEvent.get());
        Assert.assertEquals("Get a wrong sent event number", 3, en.exchangeSentEvent.get());
    }

    @Test
    public void testSyncEnricher() throws Exception {
        template.sendBody("direct:start2", "test");
        Assert.assertEquals("Get a wrong sending event number", 3, en.exchangeSendingEvent.get());
        Assert.assertEquals("Get a wrong sent event number", 3, en.exchangeSentEvent.get());
    }

    static class MyEventNotifier extends EventNotifierSupport {
        AtomicInteger exchangeSendingEvent = new AtomicInteger();

        AtomicInteger exchangeSentEvent = new AtomicInteger();

        @Override
        public void notify(CamelEvent event) throws Exception {
            if (event instanceof ExchangeSendingEvent) {
                exchangeSendingEvent.incrementAndGet();
            } else
                if (event instanceof ExchangeSentEvent) {
                    exchangeSentEvent.incrementAndGet();
                }

        }
    }
}

