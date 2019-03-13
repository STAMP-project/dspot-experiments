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
import java.util.List;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Service;
import org.apache.camel.TestSupport;
import org.apache.camel.spi.CamelEvent;
import org.junit.Assert;
import org.junit.Test;


public class EventNotifierServiceStoppingFailedEventTest extends ContextTestSupport {
    private static List<CamelEvent> events = new ArrayList<>();

    private static String stopOrder;

    @Test
    public void testStopWithFailure() throws Exception {
        EventNotifierServiceStoppingFailedEventTest.stopOrder = "";
        context.stop();
        Assert.assertEquals("CBA", EventNotifierServiceStoppingFailedEventTest.stopOrder);
        Assert.assertEquals(5, EventNotifierServiceStoppingFailedEventTest.events.size());
        TestSupport.assertIsInstanceOf(CamelContextStartingEvent.class, EventNotifierServiceStoppingFailedEventTest.events.get(0));
        TestSupport.assertIsInstanceOf(CamelContextStartedEvent.class, EventNotifierServiceStoppingFailedEventTest.events.get(1));
        TestSupport.assertIsInstanceOf(CamelContextStoppingEvent.class, EventNotifierServiceStoppingFailedEventTest.events.get(2));
        ServiceStopFailureEvent event = TestSupport.assertIsInstanceOf(ServiceStopFailureEvent.class, EventNotifierServiceStoppingFailedEventTest.events.get(3));
        TestSupport.assertIsInstanceOf(CamelContextStoppedEvent.class, EventNotifierServiceStoppingFailedEventTest.events.get(4));
        Assert.assertEquals("Fail B", event.getCause().getMessage());
        Assert.assertEquals("Failure to stop service: B due to Fail B", event.toString());
    }

    private static final class MyService implements Service {
        private String name;

        private boolean fail;

        private MyService(String name, boolean fail) {
            this.name = name;
            this.fail = fail;
        }

        public void start() throws Exception {
        }

        public void stop() throws Exception {
            EventNotifierServiceStoppingFailedEventTest.stopOrder = (EventNotifierServiceStoppingFailedEventTest.stopOrder) + (name);
            if (fail) {
                throw new IllegalArgumentException(("Fail " + (name)));
            }
        }

        @Override
        public String toString() {
            return name;
        }
    }
}

