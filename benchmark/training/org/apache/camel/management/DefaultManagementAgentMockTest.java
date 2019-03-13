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


import JmxSystemPropertyKeys.CREATE_CONNECTOR;
import JmxSystemPropertyKeys.USE_HOST_IP_ADDRESS;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.ManagementAgent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests proper behavior of DefaultManagementAgent when
 * {@link MBeanServer#registerMBean(Object, ObjectName)} returns an
 * {@link ObjectInstance} with a different ObjectName
 */
public class DefaultManagementAgentMockTest {
    @Test
    public void testObjectNameModification() throws JMException {
        MBeanServer mbeanServer = Mockito.mock(MBeanServer.class);
        ObjectInstance instance = Mockito.mock(ObjectInstance.class);
        ManagementAgent agent = new DefaultManagementAgent();
        agent.setMBeanServer(mbeanServer);
        Object object = "object";
        ObjectName sourceObjectName = new ObjectName("domain", "key", "value");
        ObjectName registeredObjectName = new ObjectName("domain", "key", "otherValue");
        // Register MBean and return different ObjectName
        Mockito.when(mbeanServer.isRegistered(sourceObjectName)).thenReturn(false);
        Mockito.when(mbeanServer.registerMBean(object, sourceObjectName)).thenReturn(instance);
        Mockito.when(instance.getObjectName()).thenReturn(registeredObjectName);
        Mockito.when(mbeanServer.isRegistered(registeredObjectName)).thenReturn(true);
        agent.register(object, sourceObjectName);
        Assert.assertTrue(agent.isRegistered(sourceObjectName));
        Mockito.reset(mbeanServer, instance);
        // ... and unregister it again
        Mockito.when(mbeanServer.isRegistered(registeredObjectName)).thenReturn(true);
        mbeanServer.unregisterMBean(registeredObjectName);
        Mockito.when(mbeanServer.isRegistered(sourceObjectName)).thenReturn(false);
        agent.unregister(sourceObjectName);
        Assert.assertFalse(agent.isRegistered(sourceObjectName));
    }

    @Test
    public void testShouldUseHostIPAddressWhenFlagisTrue() throws Exception {
        System.setProperty(USE_HOST_IP_ADDRESS, "true");
        System.setProperty(CREATE_CONNECTOR, "true");
        CamelContext ctx = new DefaultCamelContext();
        ManagementAgent agent = new DefaultManagementAgent(ctx);
        agent.start();
        Assert.assertTrue(agent.getUseHostIPAddress());
    }

    @Test
    public void shouldUseHostNameWhenFlagisFalse() throws Exception {
        System.setProperty(USE_HOST_IP_ADDRESS, "false");
        System.setProperty(CREATE_CONNECTOR, "true");
        CamelContext ctx = new DefaultCamelContext();
        ManagementAgent agent = new DefaultManagementAgent(ctx);
        agent.start();
        Assert.assertFalse(agent.getUseHostIPAddress());
    }
}

