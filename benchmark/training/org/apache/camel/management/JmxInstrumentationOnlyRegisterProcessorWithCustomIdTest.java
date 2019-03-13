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


import java.util.Set;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

import static DefaultManagementAgent.DEFAULT_DOMAIN;


public class JmxInstrumentationOnlyRegisterProcessorWithCustomIdTest extends ContextTestSupport {
    protected String domainName = DEFAULT_DOMAIN;

    protected MBeanServer server;

    @Test
    public void testCustomId() throws Exception {
        // JMX tests dont work well on AIX CI servers (hangs them)
        if (isPlatform("aix")) {
            return;
        }
        Set<ObjectName> s = server.queryNames(new ObjectName(((domainName) + ":type=endpoints,*")), null);
        assertEquals(("Could not find 2 endpoints: " + s), 6, s.size());
        s = server.queryNames(new ObjectName(((domainName) + ":type=context,*")), null);
        assertEquals(("Could not find 1 context: " + s), 1, s.size());
        s = server.queryNames(new ObjectName(((domainName) + ":type=processors,*")), null);
        assertEquals(("Could not find 1 processor: " + s), 1, s.size());
        // should be mock foo
        ObjectName on = s.iterator().next();
        String id = ((String) (server.getAttribute(on, "ProcessorId")));
        assertEquals("myfoo", id);
        s = server.queryNames(new ObjectName(((domainName) + ":type=routes,*")), null);
        assertEquals(("Could not find 2 route: " + s), 2, s.size());
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        template.sendBody("direct:start", "Hello World");
        assertMockEndpointsSatisfied();
    }
}

