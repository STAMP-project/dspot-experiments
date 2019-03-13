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
package org.apache.camel.component.jms;


import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 * Test that all thread pools is removed when adding and removing a route dynamically
 */
public class JmsAddAndRemoveRouteManagementTest extends CamelTestSupport {
    @Test
    public void testAddAndRemoveRoute() throws Exception {
        MBeanServer mbeanServer = getMBeanServer();
        Set<ObjectName> before = mbeanServer.queryNames(new ObjectName("*:type=threadpools,*"), null);
        getMockEndpoint("mock:result").expectedMessageCount(1);
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("activemq:queue:in").routeId("myNewRoute").to("activemq:queue:foo");
            }
        });
        Set<ObjectName> during = mbeanServer.queryNames(new ObjectName("*:type=threadpools,*"), null);
        assertEquals("There should be one more thread pool in JMX", ((before.size()) + 1), during.size());
        template.sendBody("activemq:queue:in", "Hello World");
        assertMockEndpointsSatisfied();
        // now stop and remove that route
        context.getRouteController().stopRoute("myNewRoute");
        context.removeRoute("myNewRoute");
        Set<ObjectName> after = mbeanServer.queryNames(new ObjectName("*:type=threadpools,*"), null);
        assertEquals("Should have removed all thread pools from removed route", before.size(), after.size());
    }
}

