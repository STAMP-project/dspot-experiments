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
package org.springframework.jmx.support;


import JmxUtils.IDENTITY_OBJECT_NAME_KEY;
import java.beans.PropertyDescriptor;
import javax.management.DynamicMBean;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.jmx.IJmxTestBean;
import org.springframework.jmx.JmxTestBean;
import org.springframework.jmx.export.TestDynamicMBean;
import org.springframework.util.ObjectUtils;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 */
public class JmxUtilsTests {
    @Test
    public void testIsMBeanWithDynamicMBean() throws Exception {
        DynamicMBean mbean = new TestDynamicMBean();
        Assert.assertTrue("Dynamic MBean not detected correctly", JmxUtils.isMBean(mbean.getClass()));
    }

    @Test
    public void testIsMBeanWithStandardMBeanWrapper() throws Exception {
        StandardMBean mbean = new StandardMBean(new JmxTestBean(), IJmxTestBean.class);
        Assert.assertTrue("Standard MBean not detected correctly", JmxUtils.isMBean(mbean.getClass()));
    }

    @Test
    public void testIsMBeanWithStandardMBeanInherited() throws Exception {
        StandardMBean mbean = new JmxUtilsTests.StandardMBeanImpl();
        Assert.assertTrue("Standard MBean not detected correctly", JmxUtils.isMBean(mbean.getClass()));
    }

    @Test
    public void testNotAnMBean() throws Exception {
        Assert.assertFalse("Object incorrectly identified as an MBean", JmxUtils.isMBean(Object.class));
    }

    @Test
    public void testSimpleMBean() throws Exception {
        JmxUtilsTests.Foo foo = new JmxUtilsTests.Foo();
        Assert.assertTrue("Simple MBean not detected correctly", JmxUtils.isMBean(foo.getClass()));
    }

    @Test
    public void testSimpleMXBean() throws Exception {
        JmxUtilsTests.FooX foo = new JmxUtilsTests.FooX();
        Assert.assertTrue("Simple MXBean not detected correctly", JmxUtils.isMBean(foo.getClass()));
    }

    @Test
    public void testSimpleMBeanThroughInheritance() throws Exception {
        JmxUtilsTests.Bar bar = new JmxUtilsTests.Bar();
        JmxUtilsTests.Abc abc = new JmxUtilsTests.Abc();
        Assert.assertTrue("Simple MBean (through inheritance) not detected correctly", JmxUtils.isMBean(bar.getClass()));
        Assert.assertTrue("Simple MBean (through 2 levels of inheritance) not detected correctly", JmxUtils.isMBean(abc.getClass()));
    }

    @Test
    public void testGetAttributeNameWithStrictCasing() {
        PropertyDescriptor pd = new BeanWrapperImpl(JmxUtilsTests.AttributeTestBean.class).getPropertyDescriptor("name");
        String attributeName = JmxUtils.getAttributeName(pd, true);
        Assert.assertEquals("Incorrect casing on attribute name", "Name", attributeName);
    }

    @Test
    public void testGetAttributeNameWithoutStrictCasing() {
        PropertyDescriptor pd = new BeanWrapperImpl(JmxUtilsTests.AttributeTestBean.class).getPropertyDescriptor("name");
        String attributeName = JmxUtils.getAttributeName(pd, false);
        Assert.assertEquals("Incorrect casing on attribute name", "name", attributeName);
    }

    @Test
    public void testAppendIdentityToObjectName() throws MalformedObjectNameException {
        ObjectName objectName = ObjectNameManager.getInstance("spring:type=Test");
        Object managedResource = new Object();
        ObjectName uniqueName = JmxUtils.appendIdentityToObjectName(objectName, managedResource);
        String typeProperty = "type";
        Assert.assertEquals("Domain of transformed name is incorrect", objectName.getDomain(), uniqueName.getDomain());
        Assert.assertEquals("Type key is incorrect", objectName.getKeyProperty(typeProperty), uniqueName.getKeyProperty("type"));
        Assert.assertEquals("Identity key is incorrect", ObjectUtils.getIdentityHexString(managedResource), uniqueName.getKeyProperty(IDENTITY_OBJECT_NAME_KEY));
    }

    @Test
    public void testLocatePlatformMBeanServer() {
        MBeanServer server = null;
        try {
            server = JmxUtils.locateMBeanServer();
        } finally {
            if (server != null) {
                MBeanServerFactory.releaseMBeanServer(server);
            }
        }
    }

    @Test
    public void testIsMBean() {
        // Correctly returns true for a class
        Assert.assertTrue(JmxUtils.isMBean(JmxUtilsTests.JmxClass.class));
        // Correctly returns false since JmxUtils won't navigate to the extended interface
        Assert.assertFalse(JmxUtils.isMBean(JmxUtilsTests.SpecializedJmxInterface.class));
        // Incorrectly returns true since it doesn't detect that this is an interface
        Assert.assertFalse(JmxUtils.isMBean(JmxUtilsTests.JmxInterface.class));
    }

    public static class AttributeTestBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class StandardMBeanImpl extends StandardMBean implements IJmxTestBean {
        public StandardMBeanImpl() throws NotCompliantMBeanException {
            super(IJmxTestBean.class);
        }

        @Override
        public int add(int x, int y) {
            return 0;
        }

        @Override
        public long myOperation() {
            return 0;
        }

        @Override
        public int getAge() {
            return 0;
        }

        @Override
        public void setAge(int age) {
        }

        @Override
        public void setName(String name) {
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void dontExposeMe() {
        }
    }

    public interface FooMBean {
        String getName();
    }

    public static class Foo implements JmxUtilsTests.FooMBean {
        @Override
        public String getName() {
            return "Rob Harrop";
        }
    }

    public interface FooMXBean {
        String getName();
    }

    public static class FooX implements JmxUtilsTests.FooMXBean {
        @Override
        public String getName() {
            return "Rob Harrop";
        }
    }

    public static class Bar extends JmxUtilsTests.Foo {}

    public static class Abc extends JmxUtilsTests.Bar {}

    private interface JmxInterfaceMBean {}

    private interface JmxInterface extends JmxUtilsTests.JmxInterfaceMBean {}

    private interface SpecializedJmxInterface extends JmxUtilsTests.JmxInterface {}

    private interface JmxClassMBean {}

    private static class JmxClass implements JmxUtilsTests.JmxClassMBean {}
}

