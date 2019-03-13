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


import java.util.Set;
import java.util.function.Consumer;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MBeanContainerLifeCycleTest {
    private ContainerLifeCycle container;

    private MBeanServer mbeanServer;

    @Test
    public void testAddBeanRegistersMBeanRemoveBeanUnregistersMBean() throws Exception {
        // Adding a bean to the container should register the MBean.
        QueuedThreadPool bean = new QueuedThreadPool();
        container.addBean(bean);
        String pkg = bean.getClass().getPackage().getName();
        Set<ObjectName> objectNames = mbeanServer.queryNames(ObjectName.getInstance((pkg + ":*")), null);
        Assertions.assertEquals(1, objectNames.size());
        // Removing the bean should unregister the MBean.
        container.removeBean(bean);
        objectNames = mbeanServer.queryNames(ObjectName.getInstance((pkg + ":*")), null);
        Assertions.assertEquals(0, objectNames.size());
    }

    @Test
    public void testStoppingContainerDoesNotUnregistersMBeans() throws Exception {
        QueuedThreadPool bean = new QueuedThreadPool();
        container.addBean(bean, true);
        String pkg = bean.getClass().getPackage().getName();
        Set<ObjectName> objectNames = mbeanServer.queryNames(ObjectName.getInstance((pkg + ":*")), null);
        Assertions.assertEquals(1, objectNames.size());
        container.stop();
        objectNames = mbeanServer.queryNames(ObjectName.getInstance((pkg + ":*")), null);
        Assertions.assertEquals(1, objectNames.size());
        // Remove the MBeans to start clean on the next test.
        objectNames.forEach(( objectName) -> {
            try {
                mbeanServer.unregisterMBean(objectName);
            } catch (Throwable ignored) {
            }
        });
    }

    @Test
    public void testDestroyingContainerUnregistersMBeans() throws Exception {
        QueuedThreadPool bean = new QueuedThreadPool();
        container.addBean(bean, true);
        String pkg = bean.getClass().getPackage().getName();
        Set<ObjectName> objectNames = mbeanServer.queryNames(ObjectName.getInstance((pkg + ":*")), null);
        Assertions.assertEquals(1, objectNames.size());
        container.stop();
        container.destroy();
        objectNames = mbeanServer.queryNames(ObjectName.getInstance((pkg + ":*")), null);
        Assertions.assertEquals(0, objectNames.size());
    }
}

