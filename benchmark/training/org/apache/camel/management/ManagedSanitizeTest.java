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


import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.junit.Test;


public class ManagedSanitizeTest extends ManagementTestSupport {
    @Test
    public void testSanitize() throws Exception {
        MBeanServer mbeanServer = getMBeanServer();
        ObjectName name = ObjectName.getInstance("org.apache.camel:context=camel-1,type=endpoints,name=\"stub://foo\\?password=xxxxxx&username=foo\"");
        assertTrue("Should be registered", mbeanServer.isRegistered(name));
        String uri = ((String) (mbeanServer.getAttribute(name, "EndpointUri")));
        assertEquals("stub://foo?password=xxxxxx&username=foo", uri);
    }
}

