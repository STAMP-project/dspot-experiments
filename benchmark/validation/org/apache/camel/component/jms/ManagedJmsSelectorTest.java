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
import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


/**
 *
 */
public class ManagedJmsSelectorTest extends CamelTestSupport {
    @Test
    public void testJmsSelectorChangeViaJmx() throws Exception {
        MBeanServer mbeanServer = getMBeanServer();
        Set<ObjectName> set = mbeanServer.queryNames(new ObjectName("*:type=consumers,*"), null);
        assertEquals(1, set.size());
        ObjectName on = set.iterator().next();
        assertTrue("Should be registered", mbeanServer.isRegistered(on));
        String selector = ((String) (mbeanServer.getAttribute(on, "MessageSelector")));
        assertEquals("brand='beer'", selector);
        getMockEndpoint("mock:result").expectedBodiesReceived("Carlsberg");
        template.sendBodyAndHeader("activemq:queue:start", "Pepsi", "brand", "softdrink");
        template.sendBodyAndHeader("activemq:queue:start", "Carlsberg", "brand", "beer");
        assertMockEndpointsSatisfied();
        // change the selector at runtime
        resetMocks();
        mbeanServer.setAttribute(on, new Attribute("MessageSelector", "brand='softdrink'"));
        // give it a little time to adjust
        Thread.sleep(100);
        getMockEndpoint("mock:result").expectedBodiesReceived("Pepsi");
        template.sendBodyAndHeader("activemq:queue:start", "Pepsi", "brand", "softdrink");
        template.sendBodyAndHeader("activemq:queue:start", "Carlsberg", "brand", "beer");
        assertMockEndpointsSatisfied();
        selector = ((String) (mbeanServer.getAttribute(on, "MessageSelector")));
        assertEquals("brand='softdrink'", selector);
    }
}

