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
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBean;
import org.flowable.management.jmx.annotations.NotificationSenderAware;
import org.flowable.management.jmx.testMbeans.TestMbean;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


/**
 *
 *
 * @author Saeid Mirzaei
 */
public class DefaultManagementMBeanAssemblerTest {
    DefaultManagementMBeanAssembler defaultManagementMBeanAssembler = new DefaultManagementMBeanAssembler();

    @Test
    public void testHappyPath() throws JMException, MalformedObjectNameException {
        TestMbean testMbean = new TestMbean();
        ModelMBean mbean = defaultManagementMBeanAssembler.assemble(testMbean, new ObjectName("org.flowable.jmx.Mbeans:type=something"));
        Assert.assertNotNull(mbean);
        Assert.assertNotNull(mbean.getMBeanInfo());
        Assert.assertNotNull(mbean.getMBeanInfo().getAttributes());
        MBeanAttributeInfo[] attributes = mbean.getMBeanInfo().getAttributes();
        Assert.assertEquals(2, attributes.length);
        Assert.assertTrue((((attributes[0].getName().equals("TestAttributeString")) && (attributes[1].getName().equals("TestAttributeBoolean"))) || ((attributes[1].getName().equals("TestAttributeString")) && (attributes[0].getName().equals("TestAttributeBoolean")))));
        Assert.assertNotNull(mbean.getMBeanInfo().getOperations());
        MBeanOperationInfo[] operations = mbean.getMBeanInfo().getOperations();
        Assert.assertNotNull(operations);
        Assert.assertEquals(3, operations.length);
    }

    @Test
    public void testNotificationAware() throws JMException, MalformedObjectNameException {
        NotificationSenderAware mockedNotificationAwareMbean = Mockito.mock(NotificationSenderAware.class);
        ModelMBean modelBean = defaultManagementMBeanAssembler.assemble(mockedNotificationAwareMbean, new ObjectName("org.flowable.jmx.Mbeans:type=something"));
        Assert.assertNotNull(modelBean);
        ArgumentCaptor<NotificationSender> argument = ArgumentCaptor.forClass(NotificationSender.class);
        Mockito.verify(mockedNotificationAwareMbean).setNotificationSender(argument.capture());
        Assert.assertNotNull(argument);
        Assert.assertNotNull(argument.getValue());
    }
}

