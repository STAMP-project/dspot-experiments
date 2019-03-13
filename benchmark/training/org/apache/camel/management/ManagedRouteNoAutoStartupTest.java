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
package org.apache.camel.management;


import ServiceStatus.Stopped;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


/**
 * Extended test to see if mbeans is removed and stats are correct
 */
public class ManagedRouteNoAutoStartupTest extends ManagementTestSupport {
    @Test
    public void testRouteNoAutoStartup() throws Exception {
        // JMX tests dont work well on AIX CI servers (hangs them)
        if (isPlatform("aix")) {
            return;
        }
        MBeanServer mbeanServer = getMBeanServer();
        ObjectName on = ManagedRouteNoAutoStartupTest.getRouteObjectName(mbeanServer);
        // should be stopped
        String state = ((String) (mbeanServer.getAttribute(on, "State")));
        assertEquals("Should be stopped", Stopped.name(), state);
        // start
        mbeanServer.invoke(on, "start", null, null);
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World");
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
        // need a bit time to let JMX update
        await().atMost(1, TimeUnit.SECONDS).untilAsserted(() -> {
            // should have 1 completed exchange
            Long completed = ((Long) (mbeanServer.getAttribute(on, "ExchangesCompleted")));
            assertEquals(1, completed.longValue());
        });
        // should be 1 consumer and 1 processor
        Set<ObjectName> set = mbeanServer.queryNames(new ObjectName("*:type=consumers,*"), null);
        assertEquals("Should be 1 consumer", 1, set.size());
        set = mbeanServer.queryNames(new ObjectName("*:type=processors,*"), null);
        assertEquals("Should be 1 processor", 1, set.size());
        // stop
        mbeanServer.invoke(on, "stop", null, null);
        state = ((String) (mbeanServer.getAttribute(on, "State")));
        assertEquals("Should be stopped", Stopped.name(), state);
        // should be 0 consumer and 0 processor
        set = mbeanServer.queryNames(new ObjectName("*:type=consumers,*"), null);
        assertEquals("Should be 0 consumer", 0, set.size());
        set = mbeanServer.queryNames(new ObjectName("*:type=processors,*"), null);
        assertEquals("Should be 0 processor", 0, set.size());
    }
}

