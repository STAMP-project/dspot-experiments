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


import ServiceStatus.Started;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.junit.Test;


public class ManagedThrottlingInflightRoutePolicyTest extends ManagementTestSupport {
    @Test
    public void testRoutes() throws Exception {
        // JMX tests dont work well on AIX CI servers (hangs them)
        if (isPlatform("aix")) {
            return;
        }
        MBeanServer mbeanServer = getMBeanServer();
        Set<ObjectName> set = mbeanServer.queryNames(new ObjectName("*:type=routes,*"), null);
        assertEquals(1, set.size());
        ObjectName on = set.iterator().next();
        boolean registered = mbeanServer.isRegistered(on);
        assertEquals("Should be registered", true, registered);
        String uri = ((String) (mbeanServer.getAttribute(on, "EndpointUri")));
        // the route has this starting endpoint uri
        assertEquals("direct://start", uri);
        Long val = ((Long) (mbeanServer.getAttribute(on, "ExchangesInflight")));
        // the route has no inflight exchanges
        assertEquals(0L, val.longValue());
        // should be started
        String state = ((String) (mbeanServer.getAttribute(on, "State")));
        assertEquals("Should be started", Started.name(), state);
        // should have route policy
        String policy = ((String) (mbeanServer.getAttribute(on, "RoutePolicyList")));
        assertNotNull(policy);
        assertTrue(("Should be a throttling, was: " + policy), policy.startsWith("ThrottlingInflightRoutePolicy"));
    }
}

