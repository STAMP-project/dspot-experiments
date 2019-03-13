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
package org.apache.camel.component.guava.eventbus;


import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import java.util.Date;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class GuavaEventBusConsumingDeadEventsTest extends CamelTestSupport {
    EventBus eventBus = new EventBus();

    @Test
    public void shouldForwardMessageToCamel() throws InterruptedException {
        // Given
        Date message = new Date();
        // When
        eventBus.post(message);
        // Then
        getMockEndpoint("mock:customListenerEvents").setExpectedMessageCount(0);
        assertMockEndpointsSatisfied();
        getMockEndpoint("mock:deadEvents").setExpectedMessageCount(1);
        assertMockEndpointsSatisfied();
        assertEquals(message, getMockEndpoint("mock:deadEvents").getExchanges().get(0).getIn().getBody(DeadEvent.class).getEvent());
    }
}

