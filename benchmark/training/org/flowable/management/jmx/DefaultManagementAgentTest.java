/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.management.jmx;


import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.modelmbean.RequiredModelMBean;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 *
 *
 * @author Saeid Mirzaei
 */
public class DefaultManagementAgentTest {
    @Mock
    MBeanServer mbeanServer;

    @Mock
    ObjectInstance instance;

    Object object = "object";

    ObjectName sourceObjectName;

    ObjectName registeredObjectName;

    ManagementAgent agent;

    @Test
    public void testRegisterandUnregister() throws JMException {
        Mockito.reset();
        // Register MBean and return different ObjectName
        Mockito.when(mbeanServer.isRegistered(sourceObjectName)).thenReturn(false);
        Mockito.when(mbeanServer.registerMBean(ArgumentMatchers.any(RequiredModelMBean.class), ArgumentMatchers.any(ObjectName.class))).thenReturn(instance);
        Mockito.when(instance.getObjectName()).thenReturn(registeredObjectName);
        Mockito.when(mbeanServer.isRegistered(registeredObjectName)).thenReturn(true);
        agent.register(object, sourceObjectName);
        Mockito.verify(mbeanServer).isRegistered(sourceObjectName);
        Mockito.verify(mbeanServer).registerMBean(ArgumentMatchers.any(RequiredModelMBean.class), ArgumentMatchers.any(ObjectName.class));
        Assert.assertTrue(agent.isRegistered(sourceObjectName));
        agent.unregister(sourceObjectName);
        Mockito.verify(mbeanServer).unregisterMBean(registeredObjectName);
        Assert.assertFalse(agent.isRegistered(sourceObjectName));
    }

    @Test
    public void testRegisterExisting() throws JMException {
        Mockito.reset(mbeanServer, instance);
        // do not try to reregister it, if it already exists
        Mockito.when(mbeanServer.isRegistered(sourceObjectName)).thenReturn(true);
        agent.register(object, sourceObjectName);
        Mockito.verify(mbeanServer, Mockito.never()).registerMBean(object, sourceObjectName);
    }

    @Test
    public void testUnRegisterNotExisting() throws JMException {
        Mockito.reset(mbeanServer, instance);
        // ... do not unregister if it does not exist
        Mockito.when(mbeanServer.isRegistered(sourceObjectName)).thenReturn(false);
        Mockito.when(instance.getObjectName()).thenReturn(registeredObjectName);
        agent.unregister(sourceObjectName);
        Mockito.verify(mbeanServer).isRegistered(sourceObjectName);
        Mockito.verify(mbeanServer, Mockito.never()).unregisterMBean(registeredObjectName);
        Assert.assertFalse(agent.isRegistered(sourceObjectName));
    }
}

