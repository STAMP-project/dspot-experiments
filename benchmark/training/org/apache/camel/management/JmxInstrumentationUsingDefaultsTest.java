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
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

import static DefaultManagementAgent.DEFAULT_DOMAIN;


/**
 * This test verifies JMX is enabled by default and it uses local mbean
 * server to conduct the test as connector server is not enabled by default.
 */
public class JmxInstrumentationUsingDefaultsTest extends ContextTestSupport {
    protected String domainName = DEFAULT_DOMAIN;

    protected MBeanServerConnection mbsc;

    @Test
    public void testMBeansRegistered() throws Exception {
        // JMX tests dont work well on AIX CI servers (hangs them)
        if (isPlatform("aix")) {
            return;
        }
        assertDefaultDomain();
        template.sendBody("direct:start", "Hello World");
        resolveMandatoryEndpoint("mock:end", MockEndpoint.class);
        Set<ObjectName> s = mbsc.queryNames(new ObjectName(((domainName) + ":type=endpoints,*")), null);
        assertEquals(("Could not find 2 endpoints: " + s), 2, s.size());
        s = mbsc.queryNames(new ObjectName(((domainName) + ":type=context,*")), null);
        assertEquals(("Could not find 1 context: " + s), 1, s.size());
        s = mbsc.queryNames(new ObjectName(((domainName) + ":type=processors,*")), null);
        assertEquals(("Could not find 1 processors: " + s), 2, s.size());
        s = mbsc.queryNames(new ObjectName(((domainName) + ":type=consumers,*")), null);
        assertEquals(("Could not find 1 consumers: " + s), 1, s.size());
        s = mbsc.queryNames(new ObjectName(((domainName) + ":type=producers,*")), null);
        assertEquals(("Could not find 1 producers: " + s), 1, s.size());
        s = mbsc.queryNames(new ObjectName(((domainName) + ":type=routes,*")), null);
        assertEquals(("Could not find 1 route: " + s), 1, s.size());
    }

    @Test
    public void testCounters() throws Exception {
        // JMX tests dont work well on AIX CI servers (hangs them)
        if (isPlatform("aix")) {
            return;
        }
        MockEndpoint resultEndpoint = resolveMandatoryEndpoint("mock:end", MockEndpoint.class);
        resultEndpoint.expectedBodiesReceived("<hello>world!</hello>");
        sendBody("direct:start", "<hello>world!</hello>");
        resultEndpoint.assertIsSatisfied();
        verifyCounter(mbsc, new ObjectName(((domainName) + ":type=routes,*")));
    }
}

