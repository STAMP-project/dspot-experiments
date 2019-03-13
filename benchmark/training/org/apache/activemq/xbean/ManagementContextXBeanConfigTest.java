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
package org.apache.activemq.xbean;


import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import junit.framework.TestCase;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.util.JMXSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class ManagementContextXBeanConfigTest extends TestCase {
    protected BrokerService brokerService;

    private static final transient Logger LOG = LoggerFactory.getLogger(ManagementContextXBeanConfigTest.class);

    public void testManagmentContextConfiguredCorrectly() throws Exception {
        TestCase.assertEquals(2011, brokerService.getManagementContext().getConnectorPort());
        TestCase.assertEquals("test.domain", brokerService.getManagementContext().getJmxDomainName());
        // Make sure the broker is registered in the right jmx domain.
        Hashtable<String, String> map = new Hashtable<String, String>();
        map.put("type", "Broker");
        map.put("brokerName", JMXSupport.encodeObjectNamePart("localhost"));
        ObjectName on = new ObjectName("test.domain", map);
        Object value = brokerService.getManagementContext().getAttribute(on, "TotalEnqueueCount");
        TestCase.assertNotNull(value);
    }

    public void testSuccessAuthentication() throws Exception {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:2011/jmxrmi");
        Map<String, Object> env = new HashMap<String, Object>();
        env.put(JMXConnector.CREDENTIALS, new String[]{ "admin", "activemq" });
        JMXConnector connector = JMXConnectorFactory.connect(url, env);
        assertAuthentication(connector);
    }

    public void testFailAuthentication() throws Exception {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:2011/jmxrmi");
        try {
            JMXConnector connector = JMXConnectorFactory.connect(url, null);
            assertAuthentication(connector);
        } catch (SecurityException e) {
            return;
        }
        TestCase.fail("Should have thrown an exception");
    }
}

