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


import Exchange.FILE_NAME;
import ServiceStatus.Started;
import ServiceStatus.Stopped;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class ManagedRouteRemoveTest extends ManagementTestSupport {
    @Test
    public void testRemove() throws Exception {
        // JMX tests dont work well on AIX CI servers (hangs them)
        if (isPlatform("aix")) {
            return;
        }
        MBeanServer mbeanServer = getMBeanServer();
        ObjectName on = ManagedRouteRemoveTest.getRouteObjectName(mbeanServer);
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World");
        template.sendBodyAndHeader("file://target/data/managed", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
        // should be started
        String state = ((String) (mbeanServer.getAttribute(on, "State")));
        assertEquals("Should be started", Started.name(), state);
        // stop
        mbeanServer.invoke(on, "stop", null, null);
        state = ((String) (mbeanServer.getAttribute(on, "State")));
        assertEquals("Should be stopped", Stopped.name(), state);
        // remove
        mbeanServer.invoke(on, "remove", null, null);
        // should not be registered anymore
        boolean registered = mbeanServer.isRegistered(on);
        assertFalse("Route mbean should have been unregistered", registered);
        // and no more routes
        Set<ObjectName> set = mbeanServer.queryNames(new ObjectName("*:type=routes,*"), null);
        assertEquals(0, set.size());
    }
}

