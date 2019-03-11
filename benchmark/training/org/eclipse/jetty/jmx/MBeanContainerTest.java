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


import com.acme.Managed;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MBeanContainerTest {
    private MBeanContainer mbeanContainer;

    private MBeanServer mbeanServer;

    private String beanName;

    private Managed managed;

    private ObjectName objectName;

    @Test
    public void testMakeName() {
        beanName = "mngd:bean";
        beanName = mbeanContainer.makeName(beanName);
        Assertions.assertEquals("mngd_bean", beanName, "Bean name should be mngd_bean");
    }

    @Test
    public void testFindBean() {
        managed = getManaged();
        objectName = mbeanContainer.findMBean(managed);
        Assertions.assertNotNull(objectName);
        Assertions.assertEquals(managed, mbeanContainer.findBean(objectName), "Bean must be added");
        Assertions.assertNull(mbeanContainer.findBean(null), "It must return null as there is no bean with the name null");
    }

    @Test
    public void testMBeanContainer() {
        Assertions.assertNotNull(mbeanContainer, "Container shouldn't be null");
    }

    @Test
    public void testGetMBeanServer() {
        Assertions.assertEquals(mbeanServer, mbeanContainer.getMBeanServer(), "MBean server Instance must be equal");
    }

    @Test
    public void testDomain() {
        String domain = "Test";
        mbeanContainer.setDomain(domain);
        Assertions.assertEquals(domain, mbeanContainer.getDomain(), "Domain name must be Test");
    }

    @Test
    public void testBeanAdded() {
        setBeanAdded();
        objectName = mbeanContainer.findMBean(managed);
        Assertions.assertTrue(mbeanServer.isRegistered(objectName), "Bean must have been registered");
    }

    @Test
    public void testBeanAddedNullCheck() {
        setBeanAdded();
        Integer mbeanCount = mbeanServer.getMBeanCount();
        mbeanContainer.beanAdded(null, null);
        Assertions.assertEquals(mbeanCount, mbeanServer.getMBeanCount(), "MBean count must not change after beanAdded(null, null) call");
    }

    @Test
    public void testBeanRemoved() {
        setUpBeanRemoved();
        mbeanContainer.beanRemoved(null, managed);
        Assertions.assertNull(mbeanContainer.findMBean(managed), "Bean shouldn't be registered with container as we removed the bean");
    }

    @Test
    public void testBeanRemovedInstanceNotFoundException() throws Exception {
        // given
        setUpBeanRemoved();
        objectName = mbeanContainer.findMBean(managed);
        // when
        mbeanContainer.getMBeanServer().unregisterMBean(objectName);
        // then
        Assertions.assertFalse(mbeanServer.isRegistered(objectName), "Bean must not have been registered as we unregistered the bean");
        // this flow covers InstanceNotFoundException. Actual code just eating
        // the exception. i.e Actual code just printing the stacktrace, whenever
        // an exception of type InstanceNotFoundException occurs.
        mbeanContainer.beanRemoved(null, managed);
    }

    @Test
    public void testDump() {
        Assertions.assertNotNull(mbeanContainer.dump(), "Dump operation shouldn't return null if operation is success");
    }

    @Test
    public void testDestroy() {
        setUpDestroy();
        objectName = mbeanContainer.findMBean(managed);
        mbeanContainer.destroy();
        Assertions.assertFalse(mbeanContainer.getMBeanServer().isRegistered(objectName), "Unregistered bean - managed");
    }

    @Test
    public void testDestroyInstanceNotFoundException() throws Exception {
        setUpDestroy();
        objectName = mbeanContainer.findMBean(managed);
        mbeanContainer.getMBeanServer().unregisterMBean(objectName);
        Assertions.assertFalse(mbeanContainer.getMBeanServer().isRegistered(objectName), "Unregistered bean - managed");
        // this flow covers InstanceNotFoundException. Actual code just eating
        // the exception. i.e Actual code just printing the stacktrace, whenever
        // an exception of type InstanceNotFoundException occurs.
        mbeanContainer.destroy();
    }

    @Test
    public void testNonManagedLifecycleNotUnregistered() throws Exception {
        testNonManagedObjectNotUnregistered(new ContainerLifeCycle());
    }

    @Test
    public void testNonManagedPojoNotUnregistered() throws Exception {
        testNonManagedObjectNotUnregistered(new Object());
    }
}

