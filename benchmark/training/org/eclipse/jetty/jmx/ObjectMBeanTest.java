/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.jmx;


import com.acme.Derived;
import com.acme.Managed;
import javax.management.Attribute;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ObjectName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ObjectMBeanTest {
    private MBeanContainer container;

    @Test
    public void testMBeanForNull() {
        Object mBean = container.mbeanFor(null);
        Assertions.assertNull(mBean);
    }

    @Test
    public void testMBeanForString() {
        String obj = "foo";
        Object mbean = container.mbeanFor(obj);
        Assertions.assertNotNull(mbean);
        container.beanAdded(null, obj);
        ObjectName objectName = container.findMBean(obj);
        Assertions.assertNotNull(objectName);
    }

    @Test
    public void testMBeanForStringArray() {
        String[] obj = new String[]{ "a", "b" };
        Object mbean = container.mbeanFor(obj);
        Assertions.assertNotNull(mbean);
        container.beanAdded(null, obj);
        ObjectName objectName = container.findMBean(obj);
        Assertions.assertNotNull(objectName);
    }

    @Test
    public void testMBeanForIntArray() {
        int[] obj = new int[]{ 0, 1, 2 };
        Object mbean = container.mbeanFor(obj);
        Assertions.assertNotNull(mbean);
        container.beanAdded(null, obj);
        ObjectName objectName = container.findMBean(obj);
        Assertions.assertNotNull(objectName);
    }

    @Test
    public void testMetaDataCaching() {
        Derived derived = new Derived();
        ObjectMBean derivedMBean = ((ObjectMBean) (container.mbeanFor(derived)));
        ObjectMBean derivedMBean2 = ((ObjectMBean) (container.mbeanFor(derived)));
        Assertions.assertNotSame(derivedMBean, derivedMBean2);
        Assertions.assertSame(derivedMBean.metaData(), derivedMBean2.metaData());
    }

    @Test
    public void testDerivedAttributes() throws Exception {
        Derived derived = new Derived();
        Managed managed = derived.getManagedInstance();
        ObjectMBean derivedMBean = ((ObjectMBean) (container.mbeanFor(derived)));
        ObjectMBean managedMBean = ((ObjectMBean) (container.mbeanFor(managed)));
        container.beanAdded(null, derived);
        container.beanAdded(null, managed);
        MBeanInfo derivedInfo = derivedMBean.getMBeanInfo();
        Assertions.assertNotNull(derivedInfo);
        MBeanInfo managedInfo = managedMBean.getMBeanInfo();
        Assertions.assertNotNull(managedInfo);
        Assertions.assertEquals("com.acme.Derived", derivedInfo.getClassName(), "name does not match");
        Assertions.assertEquals("Test the mbean stuff", derivedInfo.getDescription(), "description does not match");
        Assertions.assertEquals(6, derivedInfo.getAttributes().length, "attribute count does not match");
        Assertions.assertEquals("Full Name", derivedMBean.getAttribute("fname"), "attribute values does not match");
        derivedMBean.setAttribute(new Attribute("fname", "Fuller Name"));
        Assertions.assertEquals("Fuller Name", derivedMBean.getAttribute("fname"), "set attribute value does not match");
        Assertions.assertEquals("goop", derivedMBean.getAttribute("goop"), "proxy attribute values do not match");
    }

    @Test
    public void testDerivedOperations() throws Exception {
        Derived derived = new Derived();
        ObjectMBean mbean = ((ObjectMBean) (container.mbeanFor(derived)));
        container.beanAdded(null, derived);
        MBeanInfo info = mbean.getMBeanInfo();
        Assertions.assertEquals(5, info.getOperations().length, "operation count does not match");
        MBeanOperationInfo[] operationInfos = info.getOperations();
        boolean publish = false;
        boolean doodle = false;
        boolean good = false;
        for (MBeanOperationInfo operationInfo : operationInfos) {
            if ("publish".equals(operationInfo.getName())) {
                publish = true;
                Assertions.assertEquals("publish something", operationInfo.getDescription(), "description doesn't match");
            }
            if ("doodle".equals(operationInfo.getName())) {
                doodle = true;
                Assertions.assertEquals("Doodle something", operationInfo.getDescription(), "description doesn't match");
                MBeanParameterInfo[] parameterInfos = operationInfo.getSignature();
                Assertions.assertEquals("A description of the argument", parameterInfos[0].getDescription(), "parameter description doesn't match");
                Assertions.assertEquals("doodle", parameterInfos[0].getName(), "parameter name doesn't match");
            }
            // This is a proxied operation on the MBean wrapper.
            if ("good".equals(operationInfo.getName())) {
                good = true;
                Assertions.assertEquals("test of proxy operations", operationInfo.getDescription(), "description does not match");
                Assertions.assertEquals("not bad", mbean.invoke("good", new Object[]{  }, new String[]{  }), "execution contexts wrong");
            }
        }
        Assertions.assertTrue(publish, "publish operation was not not found");
        Assertions.assertTrue(doodle, "doodle operation was not not found");
        Assertions.assertTrue(good, "good operation was not not found");
    }

    @Test
    public void testMethodNameMining() {
        Assertions.assertEquals("fullName", MetaData.toAttributeName("getFullName"));
        Assertions.assertEquals("fullName", MetaData.toAttributeName("getfullName"));
        Assertions.assertEquals("fullName", MetaData.toAttributeName("isFullName"));
        Assertions.assertEquals("fullName", MetaData.toAttributeName("isfullName"));
        Assertions.assertEquals("fullName", MetaData.toAttributeName("setFullName"));
        Assertions.assertEquals("fullName", MetaData.toAttributeName("setfullName"));
        Assertions.assertEquals("fullName", MetaData.toAttributeName("FullName"));
        Assertions.assertEquals("fullName", MetaData.toAttributeName("fullName"));
    }
}

