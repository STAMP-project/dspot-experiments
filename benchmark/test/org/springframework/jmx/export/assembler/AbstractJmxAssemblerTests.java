/**
 * Copyright 2002-2016 the original author or authors.
 *
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
package org.springframework.jmx.export.assembler;


import javax.management.Attribute;
import javax.management.Descriptor;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jmx.AbstractJmxTests;
import org.springframework.jmx.IJmxTestBean;
import org.springframework.jmx.support.ObjectNameManager;


/**
 *
 *
 * @author Rob Harrop
 * @author Chris Beams
 */
public abstract class AbstractJmxAssemblerTests extends AbstractJmxTests {
    protected static final String AGE_ATTRIBUTE = "Age";

    protected static final String NAME_ATTRIBUTE = "Name";

    @Test
    public void testMBeanRegistration() throws Exception {
        // beans are registered at this point - just grab them from the server
        ObjectInstance instance = getObjectInstance();
        Assert.assertNotNull("Bean should not be null", instance);
    }

    @Test
    public void testRegisterOperations() throws Exception {
        IJmxTestBean bean = getBean();
        Assert.assertNotNull(bean);
        MBeanInfo inf = getMBeanInfo();
        Assert.assertEquals("Incorrect number of operations registered", getExpectedOperationCount(), inf.getOperations().length);
    }

    @Test
    public void testRegisterAttributes() throws Exception {
        IJmxTestBean bean = getBean();
        Assert.assertNotNull(bean);
        MBeanInfo inf = getMBeanInfo();
        Assert.assertEquals("Incorrect number of attributes registered", getExpectedAttributeCount(), inf.getAttributes().length);
    }

    @Test
    public void testGetMBeanInfo() throws Exception {
        ModelMBeanInfo info = getMBeanInfoFromAssembler();
        Assert.assertNotNull("MBeanInfo should not be null", info);
    }

    @Test
    public void testGetMBeanAttributeInfo() throws Exception {
        ModelMBeanInfo info = getMBeanInfoFromAssembler();
        MBeanAttributeInfo[] inf = info.getAttributes();
        Assert.assertEquals("Invalid number of Attributes returned", getExpectedAttributeCount(), inf.length);
        for (int x = 0; x < (inf.length); x++) {
            Assert.assertNotNull("MBeanAttributeInfo should not be null", inf[x]);
            Assert.assertNotNull("Description for MBeanAttributeInfo should not be null", inf[x].getDescription());
        }
    }

    @Test
    public void testGetMBeanOperationInfo() throws Exception {
        ModelMBeanInfo info = getMBeanInfoFromAssembler();
        MBeanOperationInfo[] inf = info.getOperations();
        Assert.assertEquals("Invalid number of Operations returned", getExpectedOperationCount(), inf.length);
        for (int x = 0; x < (inf.length); x++) {
            Assert.assertNotNull("MBeanOperationInfo should not be null", inf[x]);
            Assert.assertNotNull("Description for MBeanOperationInfo should not be null", inf[x].getDescription());
        }
    }

    @Test
    public void testDescriptionNotNull() throws Exception {
        ModelMBeanInfo info = getMBeanInfoFromAssembler();
        Assert.assertNotNull("The MBean description should not be null", info.getDescription());
    }

    @Test
    public void testSetAttribute() throws Exception {
        ObjectName objectName = ObjectNameManager.getInstance(getObjectName());
        getServer().setAttribute(objectName, new Attribute(AbstractJmxAssemblerTests.NAME_ATTRIBUTE, "Rob Harrop"));
        IJmxTestBean bean = ((IJmxTestBean) (getContext().getBean("testBean")));
        Assert.assertEquals("Rob Harrop", bean.getName());
    }

    @Test
    public void testGetAttribute() throws Exception {
        ObjectName objectName = ObjectNameManager.getInstance(getObjectName());
        getBean().setName("John Smith");
        Object val = getServer().getAttribute(objectName, AbstractJmxAssemblerTests.NAME_ATTRIBUTE);
        Assert.assertEquals("Incorrect result", "John Smith", val);
    }

    @Test
    public void testOperationInvocation() throws Exception {
        ObjectName objectName = ObjectNameManager.getInstance(getObjectName());
        Object result = getServer().invoke(objectName, "add", new Object[]{ new Integer(20), new Integer(30) }, new String[]{ "int", "int" });
        Assert.assertEquals("Incorrect result", new Integer(50), result);
    }

    @Test
    public void testAttributeInfoHasDescriptors() throws Exception {
        ModelMBeanInfo info = getMBeanInfoFromAssembler();
        ModelMBeanAttributeInfo attr = info.getAttribute(AbstractJmxAssemblerTests.NAME_ATTRIBUTE);
        Descriptor desc = attr.getDescriptor();
        Assert.assertNotNull("getMethod field should not be null", desc.getFieldValue("getMethod"));
        Assert.assertNotNull("setMethod field should not be null", desc.getFieldValue("setMethod"));
        Assert.assertEquals("getMethod field has incorrect value", "getName", desc.getFieldValue("getMethod"));
        Assert.assertEquals("setMethod field has incorrect value", "setName", desc.getFieldValue("setMethod"));
    }

    @Test
    public void testAttributeHasCorrespondingOperations() throws Exception {
        ModelMBeanInfo info = getMBeanInfoFromAssembler();
        ModelMBeanOperationInfo get = info.getOperation("getName");
        Assert.assertNotNull("get operation should not be null", get);
        Assert.assertEquals("get operation should have visibility of four", get.getDescriptor().getFieldValue("visibility"), new Integer(4));
        Assert.assertEquals("get operation should have role \"getter\"", "getter", get.getDescriptor().getFieldValue("role"));
        ModelMBeanOperationInfo set = info.getOperation("setName");
        Assert.assertNotNull("set operation should not be null", set);
        Assert.assertEquals("set operation should have visibility of four", set.getDescriptor().getFieldValue("visibility"), new Integer(4));
        Assert.assertEquals("set operation should have role \"setter\"", "setter", set.getDescriptor().getFieldValue("role"));
    }

    @Test
    public void testNotificationMetadata() throws Exception {
        ModelMBeanInfo info = ((ModelMBeanInfo) (getMBeanInfo()));
        MBeanNotificationInfo[] notifications = info.getNotifications();
        Assert.assertEquals("Incorrect number of notifications", 1, notifications.length);
        Assert.assertEquals("Incorrect notification name", "My Notification", notifications[0].getName());
        String[] notifTypes = notifications[0].getNotifTypes();
        Assert.assertEquals("Incorrect number of notification types", 2, notifTypes.length);
        Assert.assertEquals("Notification type.foo not found", "type.foo", notifTypes[0]);
        Assert.assertEquals("Notification type.bar not found", "type.bar", notifTypes[1]);
    }
}

