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
import ServiceStatus.Suspended;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.camel.api.management.ManagedCamelContext;
import org.apache.camel.api.management.mbean.ManagedSuspendableRouteMBean;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;


public class ManagedRouteSuspendAndResumeTest extends ManagementTestSupport {
    @Test
    public void testSuspendAndResume() throws Exception {
        // JMX tests dont work well on AIX CI servers (hangs them)
        if (isPlatform("aix")) {
            return;
        }
        MBeanServer mbeanServer = getMBeanServer();
        ObjectName on = ManagedRouteSuspendAndResumeTest.getRouteObjectName(mbeanServer);
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World");
        template.sendBodyAndHeader("file://target/data/managed", "Hello World", FILE_NAME, "hello.txt");
        assertMockEndpointsSatisfied();
        // should be started
        String state = ((String) (mbeanServer.getAttribute(on, "State")));
        assertEquals("Should be started", Started.name(), state);
        // stop
        mbeanServer.invoke(on, "suspend", null, null);
        state = ((String) (mbeanServer.getAttribute(on, "State")));
        assertEquals("Should be suspended", Suspended.name(), state);
        mock.reset();
        mock.expectedBodiesReceived("Bye World");
        // wait a little bit while route is stopped to verify that file was not consumed
        mock.setResultWaitTime(250);
        template.sendBodyAndHeader("file://target/data/managed", "Bye World", FILE_NAME, "bye.txt");
        // route is stopped so we do not get the file
        mock.assertIsNotSatisfied();
        // prepare mock for starting route
        mock.reset();
        mock.expectedBodiesReceived("Bye World");
        // start
        mbeanServer.invoke(on, "resume", null, null);
        state = ((String) (mbeanServer.getAttribute(on, "State")));
        assertEquals("Should be started", Started.name(), state);
        // this time the file is consumed
        mock.assertIsSatisfied();
        ManagedSuspendableRouteMBean route = context.getExtension(ManagedCamelContext.class).getManagedRoute("foo", ManagedSuspendableRouteMBean.class);
        assertNotNull(route);
        assertEquals(2, route.getExchangesCompleted());
    }
}

