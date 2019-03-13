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


import java.util.Map;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.TabularData;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreamsService;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import reactor.core.publisher.Flux;


/**
 * Test exposed services on JMX.
 */
public class ReactiveStreamsJMXTest extends CamelTestSupport {
    @Test
    public void testJmxExposedService() throws Exception {
        MBeanServer mbeanServer = getMBeanServer();
        ObjectName rxService = getReactiveStreamsServiceName(mbeanServer);
        Map<String, Object> subdata = getValues(mbeanServer, rxService, "camelSubscribers", 0);
        assertEquals("unbounded", subdata.get("name"));
        assertEquals(0L, subdata.get("inflight"));
        assertEquals(0L, subdata.get("requested"));
        Map<String, Object> pubdata = getValues(mbeanServer, rxService, "camelPublishers", 0);
        assertEquals("strings", pubdata.get("name"));
        assertEquals(1, pubdata.get("subscribers"));
        TabularData subTd0 = ((TabularData) (pubdata.get("subscriptions")));
        assertEquals(1, subTd0.values().size());
        // Create another subscriber
        CamelReactiveStreamsService rxCamel = CamelReactiveStreams.get(context());
        Flux.from(rxCamel.fromStream("strings")).subscribe();
        pubdata = getValues(mbeanServer, rxService, "camelPublishers", 0);
        TabularData subTd = ((TabularData) (pubdata.get("subscriptions")));
        assertEquals(2, subTd.values().size());
        Map<String, Object> subscriptions = getValues(subTd, 1);
        assertEquals("BUFFER", subscriptions.get("back pressure"));
    }
}

